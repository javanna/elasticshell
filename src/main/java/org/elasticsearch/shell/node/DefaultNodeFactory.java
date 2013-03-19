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

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.shell.ResourceRegistry;
import org.elasticsearch.shell.ShellSettings;
import org.elasticsearch.shell.client.ClientScopeSynchronizerRunner;
import org.elasticsearch.shell.client.ClientWrapper;

/**
 * @author Luca Cavanna
 *
 * Default implementation of {@link NodeFactory}
 * @param <ShellNativeClient> the shell native class used to represent a client within the shell
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
public class DefaultNodeFactory<ShellNativeClient, JsonInput, JsonOutput> implements NodeFactory<ShellNativeClient, JsonInput, JsonOutput> {

    private final ClientWrapper<ShellNativeClient, JsonInput, JsonOutput> clientWrapper;
    private final ResourceRegistry resourceRegistry;
    private final ClientScopeSynchronizerRunner<ShellNativeClient> clientScopeSynchronizerRunner;
    private final ShellSettings shellSettings;

    @Inject
    DefaultNodeFactory(ClientWrapper<ShellNativeClient, JsonInput, JsonOutput> clientWrapper,
                              ResourceRegistry resourceRegistry,
                              ClientScopeSynchronizerRunner<ShellNativeClient> clientScopeSynchronizerRunner,
                              ShellSettings shellSettings) {

        this.clientWrapper = clientWrapper;
        this.resourceRegistry = resourceRegistry;
        this.clientScopeSynchronizerRunner = clientScopeSynchronizerRunner;
        this.shellSettings = shellSettings;
    }

    @Override
    public Node<ShellNativeClient, JsonInput, JsonOutput> newLocalNode() {
        return new Node<ShellNativeClient, JsonInput, JsonOutput>(
                createLocalNode(shellSettings.settings().get(ShellSettings.CLUSTER_NAME)),
                clientWrapper, resourceRegistry, clientScopeSynchronizerRunner);
    }

    @Override
    public Node<ShellNativeClient, JsonInput, JsonOutput> newLocalNode(String clusterName) {
        return new Node<ShellNativeClient, JsonInput, JsonOutput>(
                createLocalNode(clusterName),
                clientWrapper, resourceRegistry, clientScopeSynchronizerRunner);
    }

    protected org.elasticsearch.node.Node createLocalNode(String clusterName) {
        return org.elasticsearch.node.NodeBuilder.nodeBuilder()
                .clusterName(clusterName).local(true).build().start();
    }
}
