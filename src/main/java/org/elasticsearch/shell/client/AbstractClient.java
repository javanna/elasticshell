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

import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheRequest;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.optimize.OptimizeRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentsRequest;
import org.elasticsearch.action.admin.indices.settings.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequest;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryRequest;
import org.elasticsearch.action.admin.indices.warmer.delete.DeleteWarmerRequest;
import org.elasticsearch.action.admin.indices.warmer.put.PutWarmerRequest;
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
import org.elasticsearch.cluster.metadata.AliasAction;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.executors.core.*;
import org.elasticsearch.shell.client.executors.indices.*;

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
@SuppressWarnings("unused")
public abstract class AbstractClient<JsonInput, JsonOutput> implements Closeable {

    private final Client client;
    private final JsonSerializer<JsonInput, JsonOutput> jsonSerializer;

    protected AbstractClient(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        this.client = client;
        this.jsonSerializer = jsonSerializer;
    }

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

    /*
    Indices APIs
     */

    public JsonOutput getAliases() {
        ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest()
                .filterRoutingTable(true)
                .filterNodes(true)
                .listenerThreaded(false);
        return getAliases(clusterStateRequest);
    }

    public JsonOutput getAliases(String... indices) {
        ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest()
                .filterRoutingTable(true)
                .filterNodes(true)
                .filteredIndices(indices)
                .listenerThreaded(false);
        return getAliases(clusterStateRequest);
    }

    protected JsonOutput getAliases(ClusterStateRequest request) {
        return new GetAliasesIndicesRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput addAlias(String index, String alias) {
        return updateAliases(Requests.indexAliasesRequest().addAlias(index, alias));
    }

    public JsonOutput addAlias(String index, String alias, String filter) {
        return updateAliases(Requests.indexAliasesRequest().addAlias(index, alias, filter));
    }

    public JsonOutput addAlias(String index, String alias, JsonInput filter) {
        return updateAliases(Requests.indexAliasesRequest().addAlias(index, alias, jsonToString(filter)));
    }

    public JsonOutput addAlias(String index, String alias, FilterBuilder filterBuilder) {
        return updateAliases(Requests.indexAliasesRequest().addAlias(index, alias, filterBuilder));
    }

    public JsonOutput removeAlias(String index, String alias) {
        return updateAliases(Requests.indexAliasesRequest().removeAlias(index, alias));
    }

    public JsonOutput updateAliases(AliasAction... aliasActions) {
        IndicesAliasesRequest request = Requests.indexAliasesRequest();
        if (aliasActions != null){
            for (AliasAction action : aliasActions) {
                request.addAliasAction(action);
            }
        }
        return updateAliases(request);
    }

    public JsonOutput updateAliases(IndicesAliasesRequest request) {
        return new UpdateIndicesAliasesRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput analyzeByField(String index, String text, String field) {
        return analyze(new AnalyzeRequest(index, text).field(field));
    }

    public JsonOutput analyzeByTokenizer(String text, String tokenizer, String... tokenFilters) {
        return analyze(new AnalyzeRequest(text).tokenizer(tokenizer).tokenFilters(tokenFilters));
    }

    public JsonOutput analyzeByAnalyzer(String text, String analyzer) {
        return analyze(new AnalyzeRequest(text).analyzer(analyzer));
    }

    public JsonOutput analyze(String index, String text) {
        return analyze(new AnalyzeRequest(index, text));
    }

    public JsonOutput analyze(String text) {
        return analyze(new AnalyzeRequest(text));
    }

    public JsonOutput analyze(AnalyzeRequest request) {
        return new AnalyzeRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput clearCache() {
        return clearCache(new String[0]);
    }

    public JsonOutput clearCache(String... indices) {
        return clearCache(Requests.clearIndicesCacheRequest(indices));
    }

    public JsonOutput clearCache(ClearIndicesCacheRequest request) {
        return new ClearCacheRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput closeIndex(String index) {
        return closeIndex(Requests.closeIndexRequest(index));
    }

    public JsonOutput closeIndex(CloseIndexRequest request) {
        return new CloseIndexRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput createIndex(String index) {
        return createIndex(Requests.createIndexRequest(index));
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

    public JsonOutput deleteMapping(String index, String type) {
        return deleteMapping(Requests.deleteMappingRequest(index).type(type));
    }

    public JsonOutput deleteMapping(DeleteMappingRequest request) {
        return new DeleteMappingRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput deleteIndex(String index) {
        return deleteIndex(Requests.deleteIndexRequest(index));
    }

    public JsonOutput deleteIndex(DeleteIndexRequest request) {
        return new DeleteIndexRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput flush(String... indices) {
        return flush(Requests.flushRequest(indices));
    }

    public JsonOutput flush(FlushRequest request) {
        return new FlushRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput getMapping() {
        return getMapping(new String[0]);
    }

    public JsonOutput getMapping(String... indices) {
        ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest()
                .filterRoutingTable(true)
                .filterNodes(true)
                .filteredIndices(indices);
        clusterStateRequest.listenerThreaded(false);
        return new GetMappingRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(clusterStateRequest);
    }

    public JsonOutput getSettings() {
        return getSettings(new String[0]);
    }

    public JsonOutput getSettings(String... indices) {
        ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest()
                .filterRoutingTable(true)
                .filterNodes(true)
                .filteredIndices(indices);
        clusterStateRequest.listenerThreaded(false);
        return new GetSettingsRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(clusterStateRequest);
    }

    public JsonOutput typesExists(String index, String... types) {
        return typesExists(new TypesExistsRequest(new String[]{index}, types));
    }

    public JsonOutput typesExists(TypesExistsRequest request) {
        return new TypesExistsRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput indicesExists(String... index) {
        return indicesExists(Requests.indicesExistsRequest(index));
    }

    public JsonOutput indicesExists(IndicesExistsRequest request) {
        return new IndicesExistsRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput openIndex(String index) {
        return openIndex(Requests.openIndexRequest(index));
    }

    public JsonOutput openIndex(OpenIndexRequest request) {
        return new OpenIndexRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput optimize() {
        return optimize(new String[0]);
    }

    public JsonOutput optimize(String... indices) {
        return optimize(Requests.optimizeRequest(indices));
    }

    public JsonOutput optimize(OptimizeRequest request) {
        return new OptimizeRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput putMapping(String index, String type, JsonInput source) {
        return putMapping(Requests.putMappingRequest(index).type(type).source(jsonToString(source)));
    }

    public JsonOutput putMapping(String index, String type, String source) {
        return putMapping(Requests.putMappingRequest(index).type(type).source(source));
    }

    public JsonOutput putMapping(String[] index, String type, JsonInput source) {
        return putMapping(Requests.putMappingRequest(index).type(type).source(jsonToString(source)));
    }

    public JsonOutput putMapping(String[] index, String type, String source) {
        return putMapping(Requests.putMappingRequest(index).type(type).source(source));
    }

    public JsonOutput putMapping(PutMappingRequest request) {
        return new PutMappingRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput refresh() {
        return refresh(new String[0]);
    }

    public JsonOutput refresh(String... indices) {
        return refresh(Requests.refreshRequest(indices));
    }

    public JsonOutput refresh(RefreshRequest request) {
        return new RefreshRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput segments() {
        return segments(new String[0]);
    }

    public JsonOutput segments(String... indices) {
        return segments(Requests.indicesSegmentsRequest(indices));
    }

    public JsonOutput segments(IndicesSegmentsRequest request) {
        return new SegmentsRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput stats() {
        return stats(new IndicesStatsRequest());
    }

    public JsonOutput stats(String... indices) {
        return stats(new IndicesStatsRequest().indices(indices));
    }

    public JsonOutput stats(IndicesStatsRequest request) {
        return new StatsRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput status() {
        return status(new String[0]);
    }

    public JsonOutput status(String... indices) {
        return status(Requests.indicesStatusRequest(indices));
    }

    public JsonOutput status(IndicesStatusRequest request) {
        return new StatusRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput getTemplate(String name) {
        ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest()
                .filterRoutingTable(true)
                .filterNodes(true)
                .filteredIndexTemplates(name)
                .filteredIndices("_na")
                .listenerThreaded(false);
        return getTemplate(clusterStateRequest);
    }

    protected JsonOutput getTemplate(ClusterStateRequest request) {
        return new GetIndexTemplateRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput deleteTemplate(String name) {
        return deleteTemplate(new DeleteIndexTemplateRequest(name));
    }

    public JsonOutput deleteTemplate(DeleteIndexTemplateRequest request) {
        return new DeleteIndexTemplateRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput putTemplate(String name, String template, JsonInput source) {
        return putTemplate(new PutIndexTemplateRequest(name).template(template).source(jsonToString(source)));
    }

    public JsonOutput putTemplate(String name, String template, String source) {
        return putTemplate(new PutIndexTemplateRequest(name).template(template).source(source));
    }

    public JsonOutput putTemplate(PutIndexTemplateRequest request) {
        return new PutIndexTemplateRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput updateSettings(String settings) {
        return updateSettings(Requests.updateSettingsRequest().settings(settings));
    }

    public JsonOutput updateSettings(JsonInput settings) {
        return updateSettings(Requests.updateSettingsRequest().settings(jsonToString(settings)));
    }

    public JsonOutput updateSettings(String index, String settings) {
        return updateSettings(Requests.updateSettingsRequest(index).settings(settings));
    }

    public JsonOutput updateSettings(String index, JsonInput settings) {
        return updateSettings(Requests.updateSettingsRequest(index).settings(jsonToString(settings)));
    }

    public JsonOutput updateSettings(UpdateSettingsRequest request) {
        return new UpdateSettingsRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput getWarmer(){
        ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest()
                .filterAll()
                .filterMetaData(false)
                .listenerThreaded(false);
        return getWarmer(clusterStateRequest, null);
    }

    public JsonOutput getWarmer(String name){
        ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest()
                .filterAll()
                .filterMetaData(false)
                .listenerThreaded(false);
        return getWarmer(clusterStateRequest, name);
    }

    public JsonOutput getWarmer(String index, String name){
        ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest()
                .filterAll()
                .filterMetaData(false)
                .filteredIndices(index)
                .listenerThreaded(false);
        return getWarmer(clusterStateRequest, name);
    }

    protected JsonOutput getWarmer(ClusterStateRequest request, String name) {
        return new GetWarmerRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer, name).execute(request);
    }

    public JsonOutput putWarmer(String name, SearchRequest searchRequest) {
        return putWarmer(new PutWarmerRequest(name).searchRequest(searchRequest));
    }

    public JsonOutput putWarmer(String index, String type, String name, SearchSourceBuilder searchSourceBuilder) {
        return putWarmer(new PutWarmerRequest(name).searchRequest(Requests.searchRequest(index).types(type).source(searchSourceBuilder)));
    }

    public JsonOutput putWarmer(String index, String type, String name, JsonInput source) {
        return putWarmer(new PutWarmerRequest(name).searchRequest(Requests.searchRequest(index).types(type).source(jsonToString(source))));
    }

    public JsonOutput putWarmer(String index, String type, String name, String source) {
        return putWarmer(new PutWarmerRequest(name).searchRequest(Requests.searchRequest(index).types(type).source(source)));
    }

    public JsonOutput putWarmer(String index, String name, SearchSourceBuilder searchSourceBuilder) {
        return putWarmer(new PutWarmerRequest(name).searchRequest(Requests.searchRequest(index).source(searchSourceBuilder)));
    }

    public JsonOutput putWarmer(String index, String name, JsonInput source) {
        return putWarmer(new PutWarmerRequest(name).searchRequest(Requests.searchRequest(index).source(jsonToString(source))));
    }

    public JsonOutput putWarmer(String index, String name, String source) {
        return putWarmer(new PutWarmerRequest(name).searchRequest(Requests.searchRequest(index).source(source)));
    }

    public JsonOutput putWarmer(PutWarmerRequest request) {
        return new PutWarmerRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput deleteWarmer(String... indices) {
        return deleteWarmer(new DeleteWarmerRequest(null).indices(indices));
    }

    public JsonOutput deleteWarmer(String index, String name) {
        return deleteWarmer(new DeleteWarmerRequest(name).indices(new String[]{index}));
    }

    public JsonOutput deleteWarmer(DeleteWarmerRequest request) {
        return new DeleteWarmerRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    /*
    Cluster APIs
     */



    JsonSerializer<JsonInput, JsonOutput> jsonSerializer() {
        return jsonSerializer;
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