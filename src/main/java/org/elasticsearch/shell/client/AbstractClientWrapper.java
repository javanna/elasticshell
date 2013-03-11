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
import org.elasticsearch.shell.ResourceRegistry;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;
import org.elasticsearch.shell.scheduler.Scheduler;

/**
 * @author Luca Cavanna
 *
 * Abstract implementation of the {@link ClientWrapper} that provides the generic features which don't depend
 * on the script engine in use
 *
 * It receives as input either a {@link org.elasticsearch.client.node.NodeClient}
 * or a {@link org.elasticsearch.client.transport.TransportClient}, which will be wrapped into a proper
 * Java object that will be exposed to the shell. In order to expose it to the shell it needs
 * to be wrapped again into its javascript representation, which depends on the script engine in use.
 *
 * @param <ShellNativeClient> the type of the client wrapper, which depends on the script engine
 * @param <JsonInput> the json input format, native within the script engine in use
 * @param <JsonOutput> the json output format, native within the script engine in use
 */
public abstract class AbstractClientWrapper<ShellNativeClient, JsonInput, JsonOutput> implements ClientWrapper<ShellNativeClient> {

    protected final JsonToString<JsonInput> jsonToString;
    protected final StringToJson<JsonOutput> stringToJson;
    protected final ResourceRegistry resourceRegistry;
    protected final Scheduler scheduler;

    AbstractClientWrapper(JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson,
                          ResourceRegistry resourceRegistry, Scheduler scheduler) {
        this.jsonToString = jsonToString;
        this.stringToJson = stringToJson;
        this.resourceRegistry = resourceRegistry;
        this.scheduler = scheduler;
    }

    @Override
    public ShellNativeClient wrapTransportClient(Client client) {
        TransportClient<JsonInput, JsonOutput> transportClient = wrapEsTransportClient(client);
        return wrapAndRegisterShellClient(transportClient);
    }

    @Override
    public ShellNativeClient wrapNodeClient(Node node, Client client) {
        NodeClient<JsonInput, JsonOutput> transportClient = wrapEsNodeClient(node, client);
        return wrapAndRegisterShellClient(transportClient);
    }

    //TODO Take our from here, it doesn't have that much to do with wrapping clients
    protected ShellNativeClient wrapAndRegisterShellClient(AbstractClient<JsonInput, JsonOutput> client) {
        resourceRegistry.registerResource(client);
        return wrapShellClient(client);
    }

    /**
     * Creates a new {@link NodeClient} instance that depends on the script engine in use
     * for what concerns handling json
     * @param node the elasticsearch node, source of the client to wrap
     * @param client the elasticsearch client to wrap
     * @return the new <code>NodeClient</code> created
     */
    protected NodeClient<JsonInput, JsonOutput> wrapEsNodeClient(Node node, Client client) {
        return new NodeClient<JsonInput, JsonOutput>(node, client, jsonToString, stringToJson);
    }

    /**
     * Creates a new {@link org.elasticsearch.client.transport.TransportClient} instance that depends on the script engine in use
     * for what concerns handling json
     * @param client the elasticsearch client to wrap
     * @return the new <code>TransportClient</code> created
     */
    protected TransportClient<JsonInput, JsonOutput> wrapEsTransportClient(Client client) {
        return new TransportClient<JsonInput, JsonOutput>(client, jsonToString, stringToJson);
    }

    /**
     * Wraps a shell client into a shell native client object that depends on the used script engine
     * @param shellClient the shell client
     * @return the shell native client that wraps the given shell client
     */
    protected abstract ShellNativeClient wrapShellClient(AbstractClient<JsonInput, JsonOutput> shellClient);

}
