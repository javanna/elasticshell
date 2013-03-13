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
package org.elasticsearch.shell.node;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.shell.ResourceRegistry;
import org.elasticsearch.shell.client.AbstractClient;
import org.elasticsearch.shell.client.ClientScopeSynchronizerRunner;
import org.elasticsearch.shell.client.ClientWrapper;

import java.io.Closeable;

/**
 * @author Luca Cavanna
 */
public class Node<ShellNativeClient, JsonInput, JsonOutput> implements Closeable {

    private final org.elasticsearch.node.Node node;
    private final ClientWrapper<ShellNativeClient, JsonInput, JsonOutput> clientWrapper;
    private final ResourceRegistry resourceRegistry;
    private final ClientScopeSynchronizerRunner<ShellNativeClient> clientScopeSynchronizerRunner;

    Node(org.elasticsearch.node.Node node,
         ClientWrapper<ShellNativeClient, JsonInput, JsonOutput> clientWrapper,
         ResourceRegistry resourceRegistry,
         ClientScopeSynchronizerRunner<ShellNativeClient> clientScopeSynchronizerRunner) {
        this.node = node;
        this.clientWrapper = clientWrapper;
        this.resourceRegistry = resourceRegistry;
        this.clientScopeSynchronizerRunner = clientScopeSynchronizerRunner;

        resourceRegistry.registerResource(this);
    }

    public ShellNativeClient client() {
        Client client = node.client();
        if (client instanceof NodeClient) {
            throw new RuntimeException("Unable to create node client: the returned node isn't a NodeClient!");
        }

        NodeClient nodeClient = (NodeClient) client;

        AbstractClient<NodeClient, JsonInput, JsonOutput> shellClient = this.clientWrapper.wrapEsLocalNodeClient(nodeClient);
        resourceRegistry.registerResource(shellClient);

        ShellNativeClient shellNativeClient = clientWrapper.wrapShellClient(shellClient);
        clientScopeSynchronizerRunner.startSynchronizer(shellNativeClient);

        return shellNativeClient;
    }

    @Override
    public void close() {
        node.close();
    }
}