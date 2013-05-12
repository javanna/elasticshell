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

import java.io.IOException;

import org.elasticsearch.node.Node;
import org.elasticsearch.shell.dump.DumpRestorer;
import org.elasticsearch.shell.dump.DumpSaver;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

/**
 * @author Luca Cavanna
 *
 * Represents an elasticsearch node client within the shell
 * The close method closes the node too
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
public class NodeClient<JsonInput, JsonOutput>
        extends AbstractClient<org.elasticsearch.client.node.NodeClient, JsonInput, JsonOutput> {

    private final Node node;

    public NodeClient(Node node, org.elasticsearch.client.node.NodeClient client, JsonToString<JsonInput> jsonToString,
                      StringToJson<JsonOutput> stringToJson, DumpSaver<JsonInput> dumpSaver, DumpRestorer dumpRestorer) {
        super(client, jsonToString, stringToJson, dumpSaver, dumpRestorer);
        this.node = node;
    }

    @Override
    public void close() throws IOException {
        client().close();
        node.close();
    }

    @Override
    protected String asString() {
        return "Node client connected to cluster [" + client().settings().get("cluster.name") + "]";
    }
}
