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
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.shell.ShellScope;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;
import org.elasticsearch.shell.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Cavanna
 *
 * Base implementation of {@link ClientFactory}
 * Contains the generic elasticsearch node creation and delegates to subclasses the code fragments that depend
 * on the used script engine
 *
 * @param <ShellNativeClient> the shell native class used to represent a client within the shell
 * @param <Scope> the shell scope used to register resources that need to be closed before shutdown
 */
public abstract class AbstractClientFactory<ShellNativeClient, Scope, JsonInput, JsonOutput> implements ClientFactory<ShellNativeClient> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClientFactory.class);

    private static final String DEFAULT_NODE_NAME = "elasticshell";
    private static final String DEFAULT_CLUSTER_NAME = "elasticsearch";

    private static final String DEFAULT_TRANSPORT_HOST = "localhost";
    private static final int DEFAULT_TRANSPORT_PORT = 9300;

    protected final ShellScope<Scope> shellScope;
    protected final Scheduler scheduler;
    protected final JsonToString<JsonInput> jsonToString;
    protected final StringToJson<JsonOutput> stringToJson;

    /**
     * Creates the AbstractClientFactory given the shell scope and the scheduler (optional)
     *
     * @param shellScope the shell scope
     * @param scheduler the scheduler used to schedule runnable actions
     */
    protected AbstractClientFactory(ShellScope<Scope> shellScope, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson, Scheduler scheduler) {
        this.shellScope = shellScope;
        this.jsonToString = jsonToString;
        this.stringToJson = stringToJson;
        this.scheduler = scheduler;
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
        Client client = node.client();
        //if clusterKo we immediately close both the client and the node that we just created
        if (clusterKo(client)) {
            client.close();
            node.close();
            return null;
        }

        NodeClient nodeClient = newNodeClient(node, client);
        registerClientResource(nodeClient);
        ShellNativeClient shellNativeClient = wrapClient(nodeClient);
        if (scheduler != null) {
            scheduler.schedule(createScopeSyncRunnable(shellNativeClient), 2);
        }
        return shellNativeClient;
    }

    /**
     * Creates a new {@link NodeClient} instance that depends on the script engine in use
     * for what concerns handling json
     * @param node the elasticsearch node
     * @param client the client node
     * @return the new <code>NodeClient</code> created
     */
    protected abstract NodeClient newNodeClient(Node node, Client client);

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
    public ShellNativeClient newNodeClient() {
        return newNodeClient(DEFAULT_CLUSTER_NAME);
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

        org.elasticsearch.shell.client.TransportClient shellClient = newTransportClient(client);
        registerClientResource(shellClient);
        ShellNativeClient shellNativeClient = wrapClient(shellClient);
        if (scheduler != null) {
            scheduler.schedule(createScopeSyncRunnable(shellNativeClient), 2);
        }
        return shellNativeClient;
    }

    /**
     * Creates a new {@link TransportClient} instance that depends on the script engine in use
     * for what concerns handling json
     * @param client the elasticsearch client to wrap
     * @return the new <code>TransportClient</code> created
     */
    protected abstract org.elasticsearch.shell.client.TransportClient newTransportClient(Client client);

    /**
     * Registers the client as {@link java.io.Closeable} resource to the shell so that it can be closed when needed (e.g. on shutdown)
     * @param shellClient the shell client to register to the shell scope
     */
    protected void registerClientResource(AbstractClient shellClient) {
        shellScope.registerResource(shellClient);
    }

    /**
     * Wraps a shell client into a shell native client object that depends on the used script engine
     * @param shellClient the shell client
     * @return the shell native client that wraps the given shell client
     */
    protected abstract ShellNativeClient wrapClient(AbstractClient shellClient);

    /**
     * Creates the {@link Runnable} that keeps the scope up-to-date for what concerns the name of the indexes
     * and their types. It depends on the script engine in use.
     * @param shellNativeClient the shell native client to keep up-to-date
     * @return the {@link Runnable} that will keep up-to-date the shell native client
     */
    protected abstract ClientScopeSyncRunnable createScopeSyncRunnable(ShellNativeClient shellNativeClient);
}
