/*
 * Licensed to Luca Cavanna (the "Author") under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.shell.client;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.shell.ResourceRegistry;
import org.elasticsearch.shell.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Cavanna
 *
 * Base implementation of {@link ClientFactory}
 * Contains the generic elasticsearch node creation and delegates to a {@link ClientWrapper} object
 * the creation of the shell specific wrapper, which depends on the script engine in use
 *
 * @param <ShellNativeClient> the shell native class used to represent a client within the shell
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
public class DefaultClientFactory<ShellNativeClient, JsonInput, JsonOutput> implements ClientFactory<ShellNativeClient> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultClientFactory.class);

    private static final String DEFAULT_NODE_NAME = "elasticshell";
    private static final String DEFAULT_CLUSTER_NAME = "elasticsearch";

    private static final String DEFAULT_TRANSPORT_HOST = "localhost";
    private static final int DEFAULT_TRANSPORT_PORT = 9300;

    private final ClientWrapper<ShellNativeClient, JsonInput, JsonOutput> clientWrapper;

    private final ResourceRegistry resourceRegistry;

    private final ClientScopeSynchronizerFactory<ShellNativeClient> clientClientScopeSynchronizerFactory;
    private final Scheduler scheduler;

    /**
     * Creates the DefaultClientFactory given the shell scope and the scheduler
     *
     * @param clientWrapper the wrapper from elasticsearch client to shell native client
     */
    @Inject
    DefaultClientFactory(ClientWrapper<ShellNativeClient, JsonInput, JsonOutput> clientWrapper,
                         ResourceRegistry resourceRegistry,
                         ClientScopeSynchronizerFactory<ShellNativeClient> clientClientScopeSynchronizerFactory,
                         Scheduler scheduler) {
        this.clientWrapper = clientWrapper;
        this.resourceRegistry = resourceRegistry;
        this.clientClientScopeSynchronizerFactory = clientClientScopeSynchronizerFactory;
        this.scheduler = scheduler;
    }

    @Override
    public ShellNativeClient newNodeClient() {
        return newNodeClient(DEFAULT_CLUSTER_NAME);
    }

    /**
     * Creates a new {@link org.elasticsearch.client.node.NodeClient}, wraps it into a shell {@link NodeClient},
     * register it as a closeable resource to the shell scope, and optionally schedules the runnable action to
     * keep the scope up-to-date for what concerns the name of the indexes and their types
     *
     * @param clusterName the name of the elasticsearch cluster to connect to
     * @return the native client that will be used within the shell
     */
    @Override
    public ShellNativeClient newNodeClient(String clusterName) {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("node.name", DEFAULT_NODE_NAME)
                .put("http.enabled", false)
                .build();
        Node node  = NodeBuilder.nodeBuilder().clusterName(clusterName).client(true).settings(settings).build();
        node.start();
        //unfortunately the es clients are not type safe, need to cast itr
        Client client = node.client();
        if (client instanceof org.elasticsearch.client.node.NodeClient) {
            throw new RuntimeException("Unable to create node client the returned node isn't a NodeClient!");
        }
        org.elasticsearch.client.node.NodeClient nodeClient = (org.elasticsearch.client.node.NodeClient)client;
        //if clusterKo we immediately close both the client and the node that we just created
        if (clusterKo(nodeClient)) {
            nodeClient.close();
            node.close();
            return null;
        }

        AbstractClient<org.elasticsearch.client.node.NodeClient, JsonInput, JsonOutput> shellClient = clientWrapper.wrapEsNodeClient(node, nodeClient);
        resourceRegistry.registerResource(shellClient);
        ShellNativeClient shellNativeClient = clientWrapper.wrapShellClient(shellClient);
        runSynchronizer(shellNativeClient);
        return shellNativeClient;
    }

    /* Should be useful if a client node is started and it's the only node in the cluster.
     * It means that there is no master and the checkCluster fails */
    protected boolean clusterKo(Client client) {
        try {
            client.admin().cluster().prepareHealth().setTimeout(TimeValue.timeValueSeconds(1)).execute().actionGet();
            return false;
        } catch(Exception e) {
            return true;
        }
    }

    @Override
    public ShellNativeClient newTransportClient() {
        return newTransportClient(new InetSocketTransportAddress(DEFAULT_TRANSPORT_HOST, DEFAULT_TRANSPORT_PORT));
    }

    @Override
    public ShellNativeClient newTransportClient(String... addresses) {
        TransportAddress[] transportAddresses = new TransportAddress[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            String address = addresses[i];

            String[] splitAddress = address.split(":");
            String host = DEFAULT_TRANSPORT_HOST;

            if (splitAddress.length>=1) {
                host = splitAddress[0];
            }

            int port = DEFAULT_TRANSPORT_PORT;
            if (splitAddress.length>=2) {
                try {
                    port = Integer.valueOf(splitAddress[1]);
                } catch(NumberFormatException e) {
                    logger.warn("Unable to parse port [{}]", splitAddress[1], e);
                }
            }

            transportAddresses[i] = new InetSocketTransportAddress(host, port);
        }
        return newTransportClient(transportAddresses);
    }

    protected ShellNativeClient newTransportClient(TransportAddress... addresses) {

        Settings settings = ImmutableSettings.settingsBuilder().put("client.transport.ignore_cluster_name", true).build();
        org.elasticsearch.client.transport.TransportClient client = new TransportClient(settings).addTransportAddresses(addresses);

        //if no connected node we can already close the (useless) client
        if (client.connectedNodes().size() == 0) {
            client.close();
            return null;
        }

        AbstractClient<TransportClient, JsonInput, JsonOutput> shellClient = clientWrapper.wrapEsTransportClient(client);
        resourceRegistry.registerResource(shellClient);
        ShellNativeClient shellNativeClient = clientWrapper.wrapShellClient(shellClient);
        runSynchronizer(shellNativeClient);
        return shellNativeClient;
    }

    protected void runSynchronizer(ShellNativeClient shellNativeClient) {
        ClientScopeSynchronizer<ShellNativeClient> clientScopeSynchronizer = clientClientScopeSynchronizerFactory.createClientScopeSynchronizer(shellNativeClient);
        scheduler.schedule(clientScopeSynchronizer, 2);
    }
}
