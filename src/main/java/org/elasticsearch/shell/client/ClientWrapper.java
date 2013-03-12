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
import org.elasticsearch.node.Node;

/**
 * @author Luca Cavanna
 *
 * Wrapper that converts an elasticsearch client object to the related shell wrappers that will expose it
 * as javascript representation, that depends on the script engine in use
 * @param <ShellNativeClient> the type of the client wrapper, which depends on the script engine
 * @param <JsonInput> the type used to represent a json as input within the script engine in use
 * @param <JsonOutput> the type used to represent a json as output within the script engine in use
 */
public interface ClientWrapper<ShellNativeClient, JsonInput, JsonOutput> {

    /**
     * Wraps an elasticsearch transport client into its shell representation that will be
     * embedded within the shell as a javascript object
     * @param client the elasticsearch transport client to wrap
     * @return the shell client ready to be embedded within the shell
     */
    public AbstractClient<JsonInput, JsonOutput> wrapEsTransportClient(Client client);

    /**
     * Wraps an elasticsearch node client into its shell representation that will be
     * embedded within the shell as a javascript object
     * @param node the elasticsearch node that generated the node client to wrap
     * @param client the elasticsearch node client to wrap
     * @return the shell client ready to be embedded within the shell
     */
    public AbstractClient<JsonInput, JsonOutput> wrapEsNodeClient(Node node, Client client);

    /**
     * Wraps an shell client into its javascript representation that can be
     * embedded within the shell as a javascript object
     * @param shellClient the shell client to embed within the shell
     * @return the shell client ready to be embedded within the shell
     */
    public ShellNativeClient wrapShellClient(AbstractClient<JsonInput, JsonOutput> shellClient);
}
