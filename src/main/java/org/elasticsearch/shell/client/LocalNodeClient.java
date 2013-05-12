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

import org.elasticsearch.shell.dump.DumpRestorer;
import org.elasticsearch.shell.dump.DumpSaver;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

/**
 * @author Luca Cavanna
 *
 * Node client generated from a local node/cluster, running on the same jvm where the shell process is
 * The close doesn't close the node, only the client
 */
public class LocalNodeClient<JsonInput, JsonOutput>
        extends AbstractClient<org.elasticsearch.client.node.NodeClient, JsonInput, JsonOutput> {

    public LocalNodeClient(org.elasticsearch.client.node.NodeClient client, JsonToString<JsonInput> jsonToString,
                           StringToJson<JsonOutput> stringToJson, DumpSaver<JsonInput> dumpSaver, DumpRestorer dumpRestorer) {
        super(client, jsonToString, stringToJson,  dumpSaver, dumpRestorer);
    }

    @Override
    public void close() throws IOException {
        client().close();
    }

    @Override
    protected String asString() {
        return "Local node client connected to local cluster [" + client().settings().get("cluster.name") + "]";
    }
}
