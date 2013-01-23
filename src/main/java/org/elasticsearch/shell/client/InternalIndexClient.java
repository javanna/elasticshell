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
import org.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheRequest;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.optimize.OptimizeRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequest;
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
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.shell.client.executors.core.MoreLikeThisHelper;

/**
 * @author Luca Cavanna
 *
 * Internal shell client that exposes operations available on a single index
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
@SuppressWarnings("unused")
public class InternalIndexClient<JsonInput, JsonOutput> {

    private final AbstractClient<JsonInput, JsonOutput> shellClient;
    private final String indexName;
    private final boolean alias;

    public InternalIndexClient(AbstractClient<JsonInput, JsonOutput> shellClient, String indexName, boolean alias) {
        this.shellClient = shellClient;
        this.indexName = indexName;
        this.alias = alias;
    }

    String indexName() {
        return indexName;
    }

    protected Index getIndex() {
        ClusterStateResponse response = shellClient.client().admin().cluster().prepareState().setLocal(true)
                .setFilterBlocks(true).setFilterRoutingTable(true).setFilterNodes(true).setFilterIndices(indexName)
                .setListenerThreaded(false).execute().actionGet();
        IndexMetaData indexMetaData = response.state().metaData().indices().values().iterator().next();
        return new Index(indexMetaData.index(), indexMetaData.mappings().keySet(), indexMetaData.aliases().keySet());
    }

    public String[] showTypes() {
        return getIndex().types();
    }

    /*
    Core Apis
     */

    public JsonOutput count() {
        return shellClient.count(Requests.countRequest(indexName));
    }

    public JsonOutput count(String source) {
        return shellClient.count(indexName, source);
    }

    public JsonOutput count(JsonInput source) {
        return shellClient.count(indexName, source);
    }

    public JsonOutput count(QueryBuilder queryBuilder) {
        return shellClient.count(indexName, queryBuilder);
    }

    public JsonOutput count(String type, String source) {
        return shellClient.count(indexName, type, source);
    }

    public JsonOutput count(String type, JsonInput source) {
        return shellClient.count(indexName, type, source);
    }

    public JsonOutput count(String type, QueryBuilder queryBuilder) {
        return shellClient.count(indexName, type, queryBuilder);
    }

    public JsonOutput count(CountRequest countRequest) {
        if (countRequest != null) {
            countRequest.indices(indexName);
        }
        return shellClient.count(countRequest);
    }

    public JsonOutput delete(String type, String id) {
        return shellClient.delete(indexName, type, id);
    }

    public JsonOutput delete(DeleteRequest deleteRequest) {
        if (deleteRequest != null) {
            deleteRequest.index(indexName);
        }
        return shellClient.delete(deleteRequest);
    }

    public JsonOutput deleteByQuery(JsonInput query) {
        return shellClient.deleteByQuery(indexName, query);
    }

    public JsonOutput deleteByQuery(String query) {
        return shellClient.deleteByQuery(indexName, query);
    }

    public JsonOutput deleteByQuery(QueryBuilder queryBuilder) {
        return shellClient.deleteByQuery(indexName, queryBuilder);
    }

    public JsonOutput deleteByQuery(String type, JsonInput query) {
        return shellClient.deleteByQuery(indexName, type, query);
    }

    public JsonOutput deleteByQuery(String type, String query) {
        return shellClient.deleteByQuery(indexName, type, query);
    }

    public JsonOutput deleteByQuery(String type, QueryBuilder queryBuilder) {
        return shellClient.deleteByQuery(indexName, type, queryBuilder);
    }

    public JsonOutput deleteByQuery(DeleteByQueryRequest deleteByQueryRequest) {
        if (deleteByQueryRequest != null) {
            deleteByQueryRequest.indices(new String[]{indexName});
        }
        return shellClient.deleteByQuery(deleteByQueryRequest);
    }

    public JsonOutput explain(String type, String id, JsonInput source) {
        return shellClient.explain(indexName, type, id, source);
    }

    public JsonOutput explain(String type, String id, String source) {
        return shellClient.explain(indexName, type, id, source);
    }

    public JsonOutput explain(String type, String id, ExplainSourceBuilder explainSourceBuilder) {
        return shellClient.explain(indexName, type, id, explainSourceBuilder);
    }

    public JsonOutput explain(ExplainRequest explainRequest) {
        if (explainRequest != null) {
            explainRequest.index(indexName);
        }
        return shellClient.explain(explainRequest);
    }

    public JsonOutput get(String type, String id) {
        return shellClient.get(indexName, type, id);
    }

    public JsonOutput get(GetRequest getRequest) {
        if (getRequest != null) {
            getRequest.index(indexName);
        }
        return shellClient.get(getRequest);
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

    public JsonOutput moreLikeThis(String type, String id) {
        return shellClient.moreLikeThis(indexName, type, id);
    }

    public JsonOutput moreLikeThis(MoreLikeThisRequest moreLikeThisRequest) {
        if (moreLikeThisRequest != null) {
            if (!indexName.equals(moreLikeThisRequest.index())) {
                //the needed method is not public, creating a brand new request
                //moreLikeThisRequest.index(indexName);
                moreLikeThisRequest = MoreLikeThisHelper.newMoreLikeThisRequest(moreLikeThisRequest, indexName);
            }
        }
        return shellClient.moreLikeThis(moreLikeThisRequest);
    }

    public JsonOutput percolate(String type, JsonInput source) {
        return shellClient.percolate(indexName, type, source);
    }

    public JsonOutput percolate(String type, String source) {
        return shellClient.percolate(indexName, type, source);
    }

    public JsonOutput percolate(PercolateRequest percolateRequest) {
        if (percolateRequest != null) {
            percolateRequest.index(indexName);
        }
        return shellClient.percolate(percolateRequest);
    }

    public JsonOutput search() {
        return shellClient.search(Requests.searchRequest(indexName));
    }

    public JsonOutput search(String source) {
        return shellClient.search(indexName, source);
    }

    public JsonOutput search(JsonInput source) {
        return shellClient.search(indexName, source);
    }

    public JsonOutput search(SearchSourceBuilder searchSourceBuilder) {
        return shellClient.search(Requests.searchRequest(indexName).source(searchSourceBuilder));
    }

    public JsonOutput search(String type, String source) {
        return shellClient.search(indexName, type, source);
    }

    public JsonOutput search(String type, JsonInput source) {
        return shellClient.search(indexName, type, source);
    }

    public JsonOutput search(String type, SearchSourceBuilder searchSourceBuilder) {
        return shellClient.search(indexName, type, searchSourceBuilder);
    }

    public JsonOutput search(SearchRequest searchRequest) {
        if (searchRequest != null) {
            searchRequest.indices(indexName);
        }
        return shellClient.search(searchRequest);
    }

    public JsonOutput update(String type, String id, String script) {
        return shellClient.update(new UpdateRequest(indexName, type, id).script(script));
    }

    public JsonOutput update(UpdateRequest updateRequest) {
        if (updateRequest != null) {
            updateRequest.index(indexName);
        }
        return shellClient.update(updateRequest);
    }

    public JsonOutput validate(JsonInput source) {
        return shellClient.validate(indexName, source);
    }

    public JsonOutput validate(String source) {
        return shellClient.validate(indexName, source);
    }

    public JsonOutput validate(QueryBuilder queryBuilder) {
        return shellClient.validate(indexName, queryBuilder);
    }

    public JsonOutput validate(String type, JsonInput source) {
        return shellClient.validate(indexName, type, source);
    }

    public JsonOutput validate(String type, String source) {
        return shellClient.validate(indexName, type, source);
    }

    public JsonOutput validate(String type, QueryBuilder queryBuilder) {
        return shellClient.validate(indexName, type, queryBuilder);
    }

    public JsonOutput validate(ValidateQueryRequest validateQueryRequest) {
        if (validateQueryRequest != null) {
            validateQueryRequest.indices(indexName);
        }
        return shellClient.validate(validateQueryRequest);
    }

    /*
    Indices APIs that make sense for a specific index
     */
    public JsonOutput getAliases() {
        return shellClient.getAliases(indexName);
    }

    public JsonOutput addAlias(String alias) {
        return shellClient.addAlias(indexName, alias);
    }

    public JsonOutput addAlias(String alias, String filter) {
        return shellClient.addAlias(indexName, alias, filter);
    }

    public JsonOutput addAlias(String alias, JsonInput filter) {
        return shellClient.addAlias(indexName, alias, filter);
    }

    public JsonOutput addAlias(String alias, FilterBuilder filterBuilder) {
        return shellClient.addAlias(indexName, alias, filterBuilder);
    }

    public JsonOutput analyzeByField(String text, String field) {
        return shellClient.analyzeByField(indexName, text, field);
    }

    public JsonOutput analyze(String text) {
        return shellClient.analyze(indexName, text);
    }

    public JsonOutput removeAlias(String alias) {
        return shellClient.removeAlias(indexName, alias);
    }

    public JsonOutput clearCache() {
        return shellClient.clearCache(indexName);
    }

    public JsonOutput clearCache(ClearIndicesCacheRequest request) {
        if (request != null) {
            request.indices(indexName);
        }
        return shellClient.clearCache(request);
    }

    public JsonOutput closeIndex() {
        return shellClient.closeIndex(indexName);
    }

    public JsonOutput closeIndex(CloseIndexRequest request) {
        if (request != null) {
            request.index(indexName);
        }
        return shellClient.closeIndex(request);
    }

    public JsonOutput deleteIndex() {
        return shellClient.deleteIndex(indexName);
    }

    public JsonOutput deleteIndex(DeleteIndexRequest request) {
        if (request != null) {
            request.indices(indexName);
        }
        return shellClient.deleteIndex(request);
    }

    public JsonOutput deleteMapping(String type) {
        return shellClient.deleteMapping(indexName, type);
    }

    public JsonOutput deleteMapping(DeleteMappingRequest request) {
        if (request != null) {
            request.indices(new String[]{indexName});
        }
        return shellClient.deleteMapping(request);
    }

    public JsonOutput typesExists(String... types) {
        return shellClient.typesExists(indexName, types);
    }

    public JsonOutput flush() {
        return shellClient.flush(indexName);
    }

    public JsonOutput flush(FlushRequest request) {
        if (request != null) {
            request.indices(indexName);
        }
        return shellClient.flush(request);
    }

    public JsonOutput getMapping() {
        return shellClient.getMapping(indexName);
    }

    public JsonOutput getMapping(String type) {
        //TODO filtering by type in shellClient
        return shellClient.getMapping(indexName);
    }

    public JsonOutput getSettings() {
        return shellClient.getSettings(indexName);
    }

    public JsonOutput openIndex() {
        return shellClient.openIndex(indexName);
    }

    public JsonOutput openIndex(OpenIndexRequest request) {
        if (request != null) {
            request.index(indexName);
        }
        return shellClient.openIndex(request);
    }

    public JsonOutput optimize() {
        return shellClient.optimize(indexName);
    }

    public JsonOutput optimize(OptimizeRequest request) {
        if (request != null) {
            request.indices(indexName);
        }
        return shellClient.optimize(request);
    }

    public JsonOutput putMapping(String type, JsonInput source) {
        return shellClient.putMapping(indexName, type, source);
    }

    public JsonOutput putMapping(String type, String source) {
        return shellClient.putMapping(indexName, type, source);
    }

    public JsonOutput putMapping(PutMappingRequest request) {
        if (request != null) {
            request.indices(new String[]{indexName});
        }
        return shellClient.putMapping(request);
    }

    public JsonOutput refresh() {
        return shellClient.refresh(indexName);
    }

    public JsonOutput refresh(RefreshRequest request) {
        if (request != null) {
            request.indices(indexName);
        }
        return shellClient.refresh(request);
    }

    public JsonOutput segments() {
        return shellClient.segments(indexName);
    }

    public JsonOutput segments(IndicesSegmentsRequest request) {
        if (request != null) {
            request.indices(indexName);
        }
        return shellClient.segments(request);
    }

    public JsonOutput stats() {
        return shellClient.stats(indexName);
    }

    public JsonOutput stats(IndicesStatsRequest request) {
        if (request != null) {
            request.indices(indexName);
        }
        return shellClient.stats(request);
    }

    public JsonOutput status() {
        return shellClient.status(indexName);
    }

    public JsonOutput status(IndicesStatusRequest request) {
        if (request != null) {
            request.indices(indexName);
        }
        return shellClient.status(request);
    }

    public JsonOutput updateSettings(String settings) {
        return shellClient.updateSettings(indexName, settings);
    }

    public JsonOutput updateSettings(JsonInput settings) {
        return shellClient.updateSettings(indexName, settings);
    }

    @Override
    public String toString() {
        return shellClient.toString() + " - " + (alias ? "alias" : "index") + " [" + indexName + "]";
    }
}
