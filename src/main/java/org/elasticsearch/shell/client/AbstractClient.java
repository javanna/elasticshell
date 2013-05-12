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

import java.io.Closeable;

import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.shell.client.builders.core.*;
import org.elasticsearch.shell.dump.DumpRestorer;
import org.elasticsearch.shell.dump.DumpSaver;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

/**
 * @author Luca Cavanna
 *
 * Generic elasticsearch client wrapper which exposes common client operations that don't depend
 * on the specific type of client in use (transport or node)
 *
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
@SuppressWarnings("unused")
public abstract class AbstractClient<EsClient extends org.elasticsearch.client.support.AbstractClient, JsonInput, JsonOutput>
        implements Closeable {

    private final EsClient client;
    private final JsonToString<JsonInput> jsonToString;
    private final StringToJson<JsonOutput> stringToJson;
    private final IndicesApiClient<EsClient, JsonInput, JsonOutput> indicesApiClient;
    private final ClusterApiClient<EsClient, JsonInput, JsonOutput> clusterApiClient;
    private final DumpSaver<JsonInput> dumpSaver;
    private final DumpRestorer dumpRestorer;

    protected AbstractClient(EsClient client, JsonToString<JsonInput> jsonToString,
                             StringToJson<JsonOutput> stringToJson,
                             DumpSaver<JsonInput> dumpSaver, DumpRestorer dumpRestorer) {
        this.client = client;
        this.jsonToString = jsonToString;
        this.stringToJson = stringToJson;
        this.indicesApiClient = new IndicesApiClient<EsClient, JsonInput, JsonOutput>(this, jsonToString, stringToJson);
        this.clusterApiClient = new ClusterApiClient<EsClient, JsonInput, JsonOutput>(this, jsonToString, stringToJson);
        this.dumpSaver = dumpSaver;
        this.dumpRestorer = dumpRestorer;
    }

    //Just a shortcut to get all the available indexes with their types and aliases
    public JsonOutput availableIndices(String... indices) throws Exception {
        ClusterStateResponse response = this.client.admin().cluster().state(new ClusterStateRequest()
                .filterBlocks(true).filterNodes(true).filteredIndices(indices)
                .filterRoutingTable(true)).actionGet();

        XContentBuilder builder = JsonXContent.contentBuilder();
        builder.startObject();
        for (IndexMetaData indexMetaData : response.state().metaData()) {
            builder.startObject(indexMetaData.index());
            if (indexMetaData.aliases() != null && indexMetaData.aliases().size() > 0) {
                builder.startArray("aliases");
                for (String alias : indexMetaData.aliases().keySet()) {
                    builder.value(alias);
                }
                builder.endArray();
            }
            if (indexMetaData.mappings() != null && indexMetaData.mappings().size() > 0) {
                builder.startArray("types");
                for (String alias : indexMetaData.mappings().keySet()) {
                    builder.value(alias);
                }
                builder.endArray();
            }
            builder.endObject();
        }
        builder.endObject();

        return stringToJson.stringToJson(builder.string());
    }

    //Just a shortcut to get all the available nodes within the cluster
    public JsonOutput availableNodes() throws Exception {
        ClusterStateResponse response = this.client.admin().cluster().state(new ClusterStateRequest()
                .filterBlocks(true).filterNodes(false).filterMetaData(true)
                .filterRoutingTable(true)).actionGet();

        XContentBuilder builder = JsonXContent.contentBuilder();
        builder.startObject();
        for (DiscoveryNode discoveryNode : response.state().nodes()) {
            builder.startObject(discoveryNode.id());
            builder.field("name", discoveryNode.name());
            builder.endObject();
        }
        builder.endObject();

        return stringToJson.stringToJson(builder.string());
    }

    public BulkProcessor.Builder bulkBuilder() {
        return BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
            }
        });
    }

    public BulkProcessor bulk() {
        return bulkBuilder().build();
    }

    public CountRequestBuilder<JsonInput, JsonOutput> countBuilder() {
        return new CountRequestBuilder<JsonInput, JsonOutput>(client, jsonToString, stringToJson);
    }

    public JsonOutput count() {
        return countBuilder().execute();
    }

    public JsonOutput count(String queryString) {
        return countBuilder().queryBuilder(QueryBuilders.queryString(queryString)).execute();
    }

    public JsonOutput count(JsonInput query) {
        return countBuilder().query(query).execute();
    }

    public DeleteRequestBuilder<JsonInput, JsonOutput> deleteBuilder() {
        return new DeleteRequestBuilder<JsonInput, JsonOutput>(client, jsonToString, stringToJson);
    }

    public JsonOutput delete(String index, String type, String id) {
        return deleteBuilder().index(index).type(type).id(id).execute();
    }

    public DeleteByQueryRequestBuilder<JsonInput, JsonOutput> deleteByQueryBuilder() {
        return new DeleteByQueryRequestBuilder<JsonInput, JsonOutput>(client, jsonToString, stringToJson);
    }

    public JsonOutput deleteByQuery(String queryString) {
        return deleteByQueryBuilder().queryBuilder(QueryBuilders.queryString(queryString)).execute();
    }

    public JsonOutput deleteByQuery(JsonInput query) {
        return deleteByQueryBuilder().query(query).execute();
    }

    public ExplainRequestBuilder<JsonInput, JsonOutput> explainBuilder() {
        return new ExplainRequestBuilder<JsonInput, JsonOutput>(client, jsonToString, stringToJson);
    }

    public JsonOutput explain(String index, String type, String id, String queryString) {
        return explainBuilder().index(index).type(type).id(id).
                queryBuilder(QueryBuilders.queryString(queryString)).execute();
    }

    public JsonOutput explain(String index, String type, String id, JsonInput source) {
        return explainBuilder().index(index).type(type).id(id).source(source).execute();
    }

    public GetRequestBuilder<JsonInput, JsonOutput> getBuilder() {
        return new GetRequestBuilder<JsonInput, JsonOutput>(client, jsonToString, stringToJson);
    }

    public JsonOutput get(String index, String type, String id) {
        return getBuilder().index(index).type(type).id(id).execute();
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> indexBuilder() {
        return new IndexRequestBuilder<JsonInput,JsonOutput>(client, jsonToString, stringToJson);
    }

    public JsonOutput index(String index, String type, String id, JsonInput source) {
        return indexBuilder().index(index).type(type).id(id).source(source).execute();
    }

    public MultiGetRequestBuilder<JsonInput, JsonOutput> multiGetBuilder() {
        return new MultiGetRequestBuilder<JsonInput, JsonOutput>(client, jsonToString, stringToJson);
    }

    public JsonOutput multiGet(String index, String type, String... ids) {
        return multiGetBuilder().add(index, type, ids).execute();
    }

    public MultiSearchRequestBuilder<JsonInput, JsonOutput> multiSearchBuilder() {
        return new MultiSearchRequestBuilder<JsonInput, JsonOutput>(client, jsonToString, stringToJson);
    }

    public JsonOutput multiSearch(JsonInput... sources) {
        MultiSearchRequestBuilder<JsonInput, JsonOutput> multiSearchRequestBuilder = multiSearchBuilder();
        if (sources != null) {
            for (JsonInput source : sources) {
                multiSearchRequestBuilder.add(source);
            }
        }
        return multiSearchRequestBuilder.execute();
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> moreLikeThisBuilder(String index) {
        return new MoreLikeThisRequestBuilder<JsonInput, JsonOutput>(client, jsonToString, stringToJson, index);
    }

    public JsonOutput moreLikeThis(String index, String type, String id) {
        return moreLikeThisBuilder(index).type(type).id(id).execute();
    }

    public PercolateRequestBuilder<JsonInput, JsonOutput> percolateBuilder() {
        return new PercolateRequestBuilder<JsonInput, JsonOutput>(client, jsonToString, stringToJson);
    }

    public JsonOutput percolate(String index, String type, JsonInput source) {
        return percolateBuilder().index(index).type(type).source(source).execute();
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> searchBuilder() {
        return new SearchRequestBuilder<JsonInput, JsonOutput>(client, jsonToString, stringToJson);
    }

    public JsonOutput search() {
        return searchBuilder().execute();
    }

    public JsonOutput search(String queryString) {
        return searchBuilder().queryBuilder(QueryBuilders.queryString(queryString)).execute();
    }

    public JsonOutput search(JsonInput source) {
        return searchBuilder().source(source).execute();
    }

    public UpdateRequestBuilder<JsonInput, JsonOutput> updateBuilder() {
        return new UpdateRequestBuilder<JsonInput, JsonOutput>(client, jsonToString, stringToJson);
    }

    public JsonOutput update(String index, String type, String id, JsonInput doc) {
        return updateBuilder().index(index).type(type).id(id).doc(doc).execute();
    }

    public JsonOutput update(String index, String type, String id, String script) {
        return updateBuilder().index(index).type(type).id(id).script(script).execute();
    }

    public ValidateQueryRequestBuilder<JsonInput, JsonOutput> validateBuilder() {
        return new ValidateQueryRequestBuilder<JsonInput, JsonOutput>(client, jsonToString, stringToJson);
    }

    public JsonOutput validate(String queryString) {
        return validateBuilder().queryBuilder(QueryBuilders.queryString(queryString)).execute();
    }

    public JsonOutput validate(JsonInput query) {
        return validateBuilder().query(query).execute();
    }

    public IndicesApiClient<EsClient, JsonInput, JsonOutput> indicesApi() {
        return indicesApiClient;
    }

    public ClusterApiClient<EsClient, JsonInput, JsonOutput> clusterApi() {
        return clusterApiClient;
    }

    public DumpSaver<JsonInput>.Builder dumpSaveBuilder() {
        return dumpSaver.new Builder(client);
    }

    public DumpRestorer.Builder dumpRestoreBuilder() {
        return dumpRestorer.new Builder(client);
    }

    EsClient client() {
        return client;
    }

    protected String jsonToString(JsonInput source) {
        return jsonToString.jsonToString(source, false);
    }

    @Override
    public String toString() {
        return asString();
    }

    /**
     * Returns the string representation of the object
     * Handy abstract method to avoid forgetting to implement toString
     * The string representation is important here since shown on the console
     * @return the string representation of the client
     */
    protected abstract String asString();
}