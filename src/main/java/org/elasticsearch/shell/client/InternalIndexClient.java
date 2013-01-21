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
import org.elasticsearch.action.percolate.PercolateRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

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
        return shellClient.get(indexName, type, id);
    }

    public JsonOutput get(GetRequest getRequest) {
        if (getRequest != null) {
            getRequest.index(indexName);
        }
        return shellClient.get(getRequest);
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

    public JsonOutput update(String type, String id, String script) {
        return shellClient.update(new UpdateRequest(indexName, type, id).script(script));
    }

    public JsonOutput update(UpdateRequest updateRequest) {
        if (updateRequest != null) {
            updateRequest.index(indexName);
        }
        return shellClient.update(updateRequest);
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

    @Override
    public String toString() {
        return shellClient.toString() + " - index [" + indexName + "]";
    }
}
