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

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.shell.ShellScope;
import org.elasticsearch.shell.scheduler.Scheduler;

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
public abstract class AbstractClientFactory<ShellNativeClient, Scope> implements ClientFactory<ShellNativeClient> {

    private static final String DEFAULT_NODE_NAME = "elasticsearch-shell";
    private static final String DEFAULT_CLUSTER_NAME = "elasticsearch";
    private static final String DEFAULT_TRANSPORT_HOST = "localhost";
    private static final int DEFAULT_TRANSPORT_PORT = 9300;

    protected final ShellScope<Scope> shellScope;
    protected final Scheduler scheduler;

    /**
     * Creates the AbstractClientFactory given the shell scope and the scheduler (optional)
     *
     * @param shellScope the shell scope
     * @param scheduler the scheduler used to schedule runnable actions
     */
    protected AbstractClientFactory(ShellScope<Scope> shellScope, Scheduler scheduler) {
        this.shellScope = shellScope;
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
        Settings settings = ImmutableSettings.settingsBuilder().put("node.name", DEFAULT_NODE_NAME).build();
        Node node  = NodeBuilder.nodeBuilder().clusterName(clusterName).client(true).settings(settings).build();
        node.start();
        NodeClient nodeClient = new NodeClient(node);
        registerClientResource(nodeClient);
        ShellNativeClient shellNativeClient = wrapClient(nodeClient);
        if (scheduler != null) {
            scheduler.schedule(createScopeSyncRunnable(shellNativeClient), 2);
        }
        return shellNativeClient;
    }

    @Override
    public ShellNativeClient newNodeClient() {
        return newNodeClient(DEFAULT_CLUSTER_NAME);
    }

    @Override
    public ShellNativeClient newTransportClient() {
        return newTransportClient(DEFAULT_TRANSPORT_HOST, DEFAULT_TRANSPORT_PORT);
    }

    @Override
    public ShellNativeClient newTransportClient(String host, int port) {
        return newTransportClient(new InetSocketTransportAddress(host, port));
    }

    @Override
    public ShellNativeClient newTransportClient(TransportAddress... addresses) {

        Settings settings = ImmutableSettings.settingsBuilder().put("client.transport.ignore_cluster_name", true).build();
        org.elasticsearch.client.transport.TransportClient client = new TransportClient(settings).addTransportAddresses(addresses);

        org.elasticsearch.shell.client.TransportClient shellClient = new org.elasticsearch.shell.client.TransportClient(client);
        registerClientResource(shellClient);
        ShellNativeClient shellNativeClient = wrapClient(shellClient);
        if (scheduler != null) {
            scheduler.schedule(createScopeSyncRunnable(shellNativeClient), 2);
        }
        return shellNativeClient;
    }

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
