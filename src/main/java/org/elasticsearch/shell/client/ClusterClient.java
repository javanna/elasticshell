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
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.shell.JsonSerializer;

/**
 * @author Luca Cavanna
 *
 * Client that exposes the elasticsearch cluster APIs
 * Wraps a {@link ClusterAdminClient}
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
public class ClusterClient<JsonInput, JsonOutput> {

    private final AbstractClient<JsonInput, JsonOutput> shellClient;
    private final JsonSerializer<JsonInput, JsonOutput> jsonSerializer;
    private final Client client;

    public ClusterClient(AbstractClient<JsonInput, JsonOutput> shellClient) {
        this.client = shellClient.client();
        this.jsonSerializer = shellClient.jsonSerializer();
        this.shellClient = shellClient;
    }

    protected String jsonToString(JsonInput source) {
        return jsonSerializer.jsonToString(source, false);
    }

    @Override
    public String toString() {
        return shellClient.toString();
    }
}
