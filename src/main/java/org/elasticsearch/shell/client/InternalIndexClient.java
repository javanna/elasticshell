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

import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.IndexMetaData;

/**
 * @author Luca Cavanna
 *
 * Internal shell client that exposes operations available on a single index
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
public class InternalIndexClient<JsonInput, JsonOutput> {

    private final AbstractClient<JsonInput, JsonOutput> shellClient;
    private final String indexName;

    public InternalIndexClient(AbstractClient<JsonInput, JsonOutput> shellClient, String indexName) {
        this.shellClient = shellClient;
        this.indexName = indexName;
    }

    String indexName() {
        return indexName;
    }

    protected Index getIndex() {
        ClusterStateResponse response = shellClient.client().admin().cluster().prepareState().setFilterBlocks(true)
                .setFilterRoutingTable(true).setFilterNodes(true).setFilterIndices(indexName).execute().actionGet();
        IndexMetaData indexMetaData = response.state().metaData().indices().values().iterator().next();
        return new Index(indexMetaData.index(), indexMetaData.mappings().keySet(), indexMetaData.aliases().keySet());
    }

    @SuppressWarnings("unused")
    public String[] showTypes() {
        return getIndex().types();
    }

    @SuppressWarnings("unused")
    public String[] showAliases() {
        return getIndex().aliases();
    }

    public JsonOutput index(String type, String id, String source) {
        return shellClient.index(indexName, type, id, source);
    }

    public JsonOutput index(String type, String source) {
        return shellClient.index(indexName, type, null, source);
    }

    public JsonOutput index(String type, String id, JsonInput source) {
        return shellClient.index(indexName, type, id, source);
    }

    public JsonOutput index(String type, JsonInput source) {
        return shellClient.index(indexName, type, null, source);
    }

    public JsonOutput index(IndexRequest indexRequest) {
        if (indexRequest != null) {
            indexRequest.index(indexName);
        }
        return shellClient.index(indexRequest);
    }

    public JsonOutput get(String type, String id) {
        return shellClient.get(Requests.getRequest(indexName).type(type).id(id));
    }

    public JsonOutput get(GetRequest getRequest) {
        if (getRequest != null) {
            getRequest.index(indexName);
        }
        return shellClient.get(getRequest);
    }

    @Override
    public String toString() {
        return shellClient.toString() + " - index [" + indexName + "]";
    }
}
