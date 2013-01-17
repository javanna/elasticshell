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
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Luca Cavanna
 *
 * Generic elasticsearch client wrapper which exposes common client operations that don't depend
 * on the specific type of client in use (transport or node)
 *
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
public abstract class AbstractClient<JsonInput, JsonOutput> implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);

    private final Client client;
    private final JsonSerializer<JsonInput, JsonOutput> jsonSerializer;

    protected AbstractClient(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        this.client= client;
        this.jsonSerializer = jsonSerializer;
    }

    @SuppressWarnings("unused")
    public Index[] showIndexes() {
        ClusterStateResponse response = client.admin().cluster().prepareState().setFilterBlocks(true)
                .setFilterRoutingTable(true).setFilterNodes(true).execute().actionGet();
        List<Index> indexes = new ArrayList<Index>();
        for (IndexMetaData indexMetaData : response.state().metaData().indices().values()) {
            indexes.add(new Index(indexMetaData.index(), indexMetaData.mappings().keySet(), indexMetaData.aliases().keySet()));
        }
        return indexes.toArray(new Index[indexes.size()]);
    }

    public JsonOutput index(String index, String type, JsonInput source) {
        return index(index, type, null, source);
    }

    public JsonOutput index(String index, String type, String id, JsonInput source) {
        return index(index, type, id, jsonSerializer.jsonToString(source, true));
    }

    public JsonOutput index(String index, String type, String source) {
        return index(index, type, null, source);
    }

    public JsonOutput index(String index, String type, String id, String source) {
        return index(Requests.indexRequest(index).type(type).id(id).source(source));
    }

    public JsonOutput index(IndexRequest indexRequest) {
        IndexResponse response = client.index(indexRequest).actionGet();
        return jsonSerializer.stringToJson(String.format("{\"ok\":true, \"_index\":\"%s\", \"_type\":\"%s\", \"_id\":\"%s\", \"version\":%s}",
                response.index(), response.type(), response.id(), response.version()));
    }

    public JsonOutput get(String index, String type, String id) {
        return get(Requests.getRequest(index).type(type).id(id));
    }

    public JsonOutput get(GetRequest getRequest) {
        GetResponse response = client.get(getRequest).actionGet();
        return xContentToJson(response, false);
    }

    public JsonOutput search() {
        return search(Requests.searchRequest());
    }

    public JsonOutput search(String source) {
        return search(Requests.searchRequest().source(source));
    }

    public JsonOutput search(JsonInput source) {
        return search(Requests.searchRequest().source(jsonToString(source)));
    }

    public JsonOutput search(String index, String source) {
        return search(Requests.searchRequest(index).source(source));
    }

    public JsonOutput search(String index, JsonInput source) {
        return search(Requests.searchRequest(index).source(jsonToString(source)));
    }

    public JsonOutput search(String index, String type, String source) {
        return search(Requests.searchRequest(index).types(type).source(source));
    }

    public JsonOutput search(String index, String type, JsonInput source) {
        return search(Requests.searchRequest(index).types(type).source(jsonToString(source)));
    }

    public JsonOutput search(SearchSourceBuilder searchSourceBuilder) {
        return search(Requests.searchRequest().source(searchSourceBuilder));
    }

    public JsonOutput search(SearchRequest searchRequest) {
        SearchResponse response = client.search(searchRequest).actionGet();
        return xContentToJson(response, true);
    }

    protected String jsonToString(JsonInput source) {
        return jsonSerializer.jsonToString(source, false);
    }

    protected JsonOutput xContentToJson(ToXContent xContent, boolean needsInit) {
        try {
            XContentBuilder xContentBuilder = JsonXContent.contentBuilder();
            if (needsInit) {
                xContentBuilder.startObject();
            }
            xContent.toXContent(xContentBuilder, ToXContent.EMPTY_PARAMS);
            if (needsInit) {
                xContentBuilder.endObject();
            }
            return jsonSerializer.stringToJson(xContentBuilder.string());
        } catch (IOException e) {
            logger.error("Error while generating the XContent response", e);
            return null;
        }
    }

    Client client() {
        return client;
    }

    @Override
    public String toString() {
        return asString();
    }

    protected abstract String asString();
}
