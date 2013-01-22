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
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryRequest;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequest;
import org.elasticsearch.action.explain.ExplainRequest;
import org.elasticsearch.action.explain.ExplainSourceBuilder;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.mlt.MoreLikeThisRequest;
import org.elasticsearch.action.percolate.PercolateRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.executors.core.*;

import java.io.Closeable;
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

    private final Client client;
    private final JsonSerializer<JsonInput, JsonOutput> jsonSerializer;
    private final AdminClient<JsonInput, JsonOutput> adminClient;

    protected AbstractClient(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        this.client = client;
        this.jsonSerializer = jsonSerializer;
        this.adminClient = new AdminClient<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public AdminClient<JsonInput, JsonOutput> admin() {
        return adminClient;
    }

    @SuppressWarnings("unused")
    public Index[] showIndexes() {
        ClusterStateResponse response = client.admin().cluster().prepareState().setLocal(true).setFilterBlocks(true)
                .setFilterRoutingTable(true).setFilterNodes(true).setListenerThreaded(false).execute().actionGet();
        List<Index> indexes = new ArrayList<Index>();
        for (IndexMetaData indexMetaData : response.state().metaData().indices().values()) {
            indexes.add(new Index(indexMetaData.index(), indexMetaData.mappings().keySet(), indexMetaData.aliases().keySet()));
        }
        return indexes.toArray(new Index[indexes.size()]);
    }

    public JsonOutput count() {
        return count(Requests.countRequest());
    }

    public JsonOutput count(String source) {
        return count(Requests.countRequest().query(source));
    }

    public JsonOutput count(JsonInput source) {
        return count(Requests.countRequest().query(jsonToString(source)));
    }

    public JsonOutput count(QueryBuilder queryBuilder) {
        return count(Requests.countRequest().query(queryBuilder));
    }

    public JsonOutput count(String index, String source) {
        return count(Requests.countRequest(index).query(source));
    }

    public JsonOutput count(String index, JsonInput source) {
        return count(Requests.countRequest(index).query(jsonToString(source)));
    }

    public JsonOutput count(String index, QueryBuilder queryBuilder) {
        return count(Requests.countRequest(index).query(queryBuilder));
    }

    public JsonOutput count(String index, String type, String source) {
        return count(Requests.countRequest(index).types(type).query(source));
    }

    public JsonOutput count(String index, String type, JsonInput source) {
        return count(Requests.countRequest(index).types(type).query(jsonToString(source)));
    }

    public JsonOutput count(String index, String type, QueryBuilder queryBuilder) {
        return count(Requests.countRequest(index).types(type).query(queryBuilder));
    }

    public JsonOutput count(CountRequest countRequest) {
        return new CountRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(countRequest);
    }

    public JsonOutput delete(String index, String type, String id) {
        return delete(Requests.deleteRequest(index).type(type).id(id));
    }

    public JsonOutput delete(DeleteRequest deleteRequest) {
        return new DeleteRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(deleteRequest);
    }

    public JsonOutput deleteByQuery(String index, JsonInput query) {
        return deleteByQuery(Requests.deleteByQueryRequest(index).query(jsonToString(query)));
    }

    public JsonOutput deleteByQuery(String index, String query) {
        return deleteByQuery(Requests.deleteByQueryRequest(index).query(query));
    }

    public JsonOutput deleteByQuery(String index, QueryBuilder queryBuilder) {
        return deleteByQuery(Requests.deleteByQueryRequest(index).query(queryBuilder));
    }

    public JsonOutput deleteByQuery(String index, String type, JsonInput query) {
        return deleteByQuery(Requests.deleteByQueryRequest(index).types(type).query(jsonToString(query)));
    }

    public JsonOutput deleteByQuery(String index, String type, String query) {
        return deleteByQuery(Requests.deleteByQueryRequest(index).types(type).query(query));
    }

    public JsonOutput deleteByQuery(String index, String type, QueryBuilder queryBuilder) {
        return deleteByQuery(Requests.deleteByQueryRequest(index).types(type).query(queryBuilder));
    }

    public JsonOutput deleteByQuery(DeleteByQueryRequest deleteByQueryRequest) {
        return new DeleteByQueryRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(deleteByQueryRequest);
    }

    public JsonOutput explain(String index, String type, String id, JsonInput source) {
        return explain(index, type, id, jsonToString(source));
    }

    public JsonOutput explain(String index, String type, String id, String source) {
        return explain(new ExplainRequest(index, type, id).source(new BytesArray(source), false));
    }

    public JsonOutput explain(String index, String type, String id, ExplainSourceBuilder explainSourceBuilder) {
        return explain(new ExplainRequest(index, type, id).source(explainSourceBuilder));
    }

    public JsonOutput explain(ExplainRequest explainRequest) {
        return new ExplainRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(explainRequest);
    }

    public JsonOutput get(String index, String type, String id) {
        return get(Requests.getRequest(index).type(type).id(id));
    }

    public JsonOutput get(GetRequest getRequest) {
        return new GetRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(getRequest);
    }

    public JsonOutput index(String index, String type, JsonInput source) {
        return index(Requests.indexRequest(index).type(type).source(jsonToString(source)));
    }

    public JsonOutput index(String index, String type, String id, JsonInput source) {
        return index(Requests.indexRequest(index).type(type).id(id).source(jsonToString(source)));
    }

    public JsonOutput index(String index, String type, String source) {
        return index(Requests.indexRequest(index).type(type).source(source));
    }

    public JsonOutput index(String index, String type, String id, String source) {
        return index(Requests.indexRequest(index).type(type).id(id).source(source));
    }

    public JsonOutput index(IndexRequest indexRequest) {
        return new IndexRequestExecutor<JsonInput,JsonOutput>(client, jsonSerializer).execute(indexRequest);
    }

    public JsonOutput moreLikeThis(String index, String type, String id) {
        return moreLikeThis(Requests.moreLikeThisRequest(index).type(type).id(id));
    }

    public JsonOutput moreLikeThis(MoreLikeThisRequest moreLikeThisRequest) {
        return new MoreLikeThisRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(moreLikeThisRequest);
    }

    public JsonOutput percolate(String index, String type, JsonInput source) {
        return percolate(new PercolateRequest(index, type).source(jsonToString(source)));
    }

    public JsonOutput percolate(String index, String type, String source) {
        return percolate(new PercolateRequest(index, type).source(source));
    }

    public JsonOutput percolate(PercolateRequest percolateRequest) {
        return new PercolateRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(percolateRequest);
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

    public JsonOutput search(SearchSourceBuilder searchSourceBuilder) {
        return search(Requests.searchRequest().source(searchSourceBuilder));
    }

    public JsonOutput search(String index, String source) {
        return search(Requests.searchRequest(index).source(source));
    }

    public JsonOutput search(String index, JsonInput source) {
        return search(Requests.searchRequest(index).source(jsonToString(source)));
    }

    public JsonOutput search(String index, SearchSourceBuilder searchSourceBuilder) {
        return search(Requests.searchRequest(index).source(searchSourceBuilder));
    }

    public JsonOutput search(String index, String type, String source) {
        return search(Requests.searchRequest(index).types(type).source(source));
    }

    public JsonOutput search(String index, String type, JsonInput source) {
        return search(Requests.searchRequest(index).types(type).source(jsonToString(source)));
    }

    public JsonOutput search(String index, String type, SearchSourceBuilder searchSourceBuilder) {
        return search(Requests.searchRequest(index).types(type).source(searchSourceBuilder));
    }

    public JsonOutput search(SearchRequest searchRequest) {
        return new SearchRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(searchRequest);
    }

    public JsonOutput update(String index, String type, String id, String script) {
        return update(new UpdateRequest(index, type, id).script(script));
    }

    public JsonOutput update(UpdateRequest updateRequest) {
        return new UpdateRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(updateRequest);
    }

    public JsonOutput validate(JsonInput source) {
        return validate(new ValidateQueryRequest().query(jsonToString(source)));
    }

    public JsonOutput validate(String source) {
        return validate(new ValidateQueryRequest().query(source));
    }

    public JsonOutput validate(QueryBuilder queryBuilder) {
        return validate(new ValidateQueryRequest().query(queryBuilder));
    }

    public JsonOutput validate(String index, JsonInput source) {
        return validate(new ValidateQueryRequest(index).query(jsonToString(source)));
    }

    public JsonOutput validate(String index, String source) {
        return validate(new ValidateQueryRequest(index).query(source));
    }

    public JsonOutput validate(String index, QueryBuilder queryBuilder) {
        return validate(new ValidateQueryRequest(index).query(queryBuilder));
    }

    public JsonOutput validate(String index, String type, JsonInput source) {
        return validate(new ValidateQueryRequest(index).types(type).query(jsonToString(source)));
    }

    public JsonOutput validate(String index, String type, String source) {
        return validate(new ValidateQueryRequest(index).types(type).query(source));
    }

    public JsonOutput validate(String index, String type, QueryBuilder queryBuilder) {
        return validate(new ValidateQueryRequest(index).types(type).query(queryBuilder));
    }

    public JsonOutput validate(ValidateQueryRequest validateQueryRequest) {
        return new ValidateQueryRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(validateQueryRequest);
    }

    Client client() {
        return client;
    }

    protected String jsonToString(JsonInput source) {
        return jsonSerializer.jsonToString(source, false);
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