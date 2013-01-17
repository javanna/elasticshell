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

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Requests;

/**
 * @author Luca Cavanna
 *
 * Internal shell client that exposes operations available on a single type
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
public class InternalTypeClient<JsonInput, JsonOutput> {

    private final AbstractClient<JsonInput, JsonOutput> shellClient;
    private final String indexName;
    private final String typeName;

    public InternalTypeClient(AbstractClient<JsonInput, JsonOutput> shellClient, String indexName, String typeName) {
        this.shellClient = shellClient;
        this.indexName = indexName;
        this.typeName = typeName;
    }

    String indexName() {
        return indexName;
    }

    String typeName() {
        return typeName;
    }

    public JsonOutput index(String source) {
        return shellClient.index(indexName, typeName, null, source);
    }

    public JsonOutput index(JsonInput source) {
        return shellClient.index(indexName, typeName, null, source);
    }

    public JsonOutput index(String id, String source) {
        return shellClient.index(indexName, typeName, id, source);
    }

    public JsonOutput index(String id, JsonInput source) {
        return shellClient.index(indexName, typeName, id, source);
    }

    public JsonOutput index(IndexRequest indexRequest) {
        if (indexRequest != null) {
            indexRequest.index(indexName).type(typeName);
        }
        return shellClient.index(indexRequest);
    }

    public JsonOutput get(String id) {
        return shellClient.get(indexName, typeName, id);
    }

    public JsonOutput get(GetRequest getRequest) {
        if (getRequest != null) {
            getRequest.index(indexName).type(typeName);
        }
        return shellClient.get(getRequest);
    }

    public JsonOutput search() {
        return shellClient.search(Requests.searchRequest(indexName).types(typeName));
    }

    public JsonOutput search(String source) {
        return shellClient.search(indexName, typeName, source);
    }

    public JsonOutput search(JsonInput source) {
        return shellClient.search(indexName, typeName, source);
    }

    public JsonOutput search(SearchRequest searchRequest) {
        if (searchRequest != null) {
            searchRequest.indices(indexName).types(typeName);
        }
        return shellClient.search(searchRequest);
    }

    @Override
    public String toString() {
        return shellClient.toString() + " - index [" + indexName + "] type [" + typeName + "]";
    }
}
