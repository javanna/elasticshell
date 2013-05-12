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

import org.elasticsearch.node.Node;
import org.elasticsearch.shell.dump.DumpRestorer;
import org.elasticsearch.shell.dump.DumpSaver;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

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
public abstract class AbstractClientWrapper<ShellNativeClient, JsonInput, JsonOutput>
        implements ClientWrapper<ShellNativeClient, JsonInput, JsonOutput> {

    protected final JsonToString<JsonInput> jsonToString;
    protected final StringToJson<JsonOutput> stringToJson;
    protected final DumpSaver<JsonInput> dumpSaver;
    protected final DumpRestorer dumpRestorer;

    AbstractClientWrapper(JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson,
                          DumpSaver<JsonInput> dumpSaver, DumpRestorer dumpRestorer) {
        this.jsonToString = jsonToString;
        this.stringToJson = stringToJson;
        this.dumpSaver = dumpSaver;
        this.dumpRestorer = dumpRestorer;
    }

    @Override
    public AbstractClient<org.elasticsearch.client.transport.TransportClient, JsonInput, JsonOutput> wrapEsTransportClient(org.elasticsearch.client.transport.TransportClient client) {
        return new TransportClient<JsonInput, JsonOutput>(client, jsonToString, stringToJson, dumpSaver, dumpRestorer);
    }

    @Override
    public AbstractClient<org.elasticsearch.client.node.NodeClient, JsonInput, JsonOutput> wrapEsNodeClient(Node node, org.elasticsearch.client.node.NodeClient client) {
        return new NodeClient<JsonInput, JsonOutput>(node, client, jsonToString, stringToJson, dumpSaver, dumpRestorer);
    }

    @Override
    public AbstractClient<org.elasticsearch.client.node.NodeClient, JsonInput, JsonOutput> wrapEsLocalNodeClient(org.elasticsearch.client.node.NodeClient client) {
        return new LocalNodeClient<JsonInput, JsonOutput>(client, jsonToString, stringToJson, dumpSaver, dumpRestorer);
    }

    @Override
    public abstract ShellNativeClient wrapShellClient(AbstractClient<? extends org.elasticsearch.client.support.AbstractClient, JsonInput, JsonOutput> shellClient);
}
