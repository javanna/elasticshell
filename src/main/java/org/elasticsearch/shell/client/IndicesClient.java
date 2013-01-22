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

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.executors.CreateIndexRequestExecutor;

/**
 * @author Luca Cavanna
 *
 * Client that exposes the elasticsearch indices APIs
 * Wraps a {@link org.elasticsearch.client.IndicesAdminClient}
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
public class IndicesClient<JsonInput, JsonOutput> {

    private final JsonSerializer<JsonInput, JsonOutput> jsonSerializer;
    private final Client client;

    public IndicesClient(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        this.client = client;
        this.jsonSerializer = jsonSerializer;
    }

    public JsonOutput createIndex(String index, JsonInput source) {
        return createIndex(Requests.createIndexRequest(index).source(jsonToString(source)));
    }

    public JsonOutput createIndex(String index, String source) {
        return createIndex(Requests.createIndexRequest(index).source(source));
    }

    public JsonOutput createIndex(CreateIndexRequest request) {
        return new CreateIndexRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    protected String jsonToString(JsonInput source) {
        return jsonSerializer.jsonToString(source, false);
    }
}
