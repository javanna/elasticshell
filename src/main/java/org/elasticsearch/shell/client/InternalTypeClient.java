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
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.shell.client.executors.core.MoreLikeThisHelper;

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

    public JsonOutput count() {
        return shellClient.count(Requests.countRequest(indexName).types(typeName));
    }

    public JsonOutput count(String source) {
        return shellClient.count(indexName, typeName, source);
    }

    public JsonOutput count(JsonInput source) {
        return shellClient.count(indexName, typeName, source);
    }

    public JsonOutput count(QueryBuilder queryBuilder) {
        return shellClient.count(indexName, typeName, queryBuilder);
    }

    public JsonOutput count(CountRequest countRequest) {
        if (countRequest != null) {
            countRequest.indices(indexName).types(typeName);
        }
        return shellClient.count(countRequest);
    }

    public JsonOutput delete(String id) {
        return shellClient.delete(indexName, typeName, id);
    }

    public JsonOutput delete(DeleteRequest deleteRequest) {
        if (deleteRequest != null) {
            deleteRequest.index(indexName).type(typeName);
        }
        return shellClient.delete(deleteRequest);
    }

    public JsonOutput deleteByQuery(JsonInput query) {
        return shellClient.deleteByQuery(indexName, typeName, query);
    }

    public JsonOutput deleteByQuery(String query) {
        return shellClient.deleteByQuery(indexName, typeName, query);
    }

    public JsonOutput deleteByQuery(QueryBuilder queryBuilder) {
        return shellClient.deleteByQuery(indexName, typeName, queryBuilder);
    }

    public JsonOutput deleteByQuery(DeleteByQueryRequest deleteByQueryRequest) {
        if (deleteByQueryRequest != null) {
            deleteByQueryRequest.indices(new String[]{indexName}).types(typeName);
        }
        return shellClient.deleteByQuery(deleteByQueryRequest);
    }

    public JsonOutput explain(String id, JsonInput source) {
        return shellClient.explain(indexName, typeName, id, source);
    }

    public JsonOutput explain(String id, String source) {
        return shellClient.explain(indexName, typeName, id, source);
    }

    public JsonOutput explain(String id, ExplainSourceBuilder explainSourceBuilder) {
        return shellClient.explain(indexName, typeName, id, explainSourceBuilder);
    }

    public JsonOutput explain(ExplainRequest explainRequest) {
        if (explainRequest != null) {
            explainRequest.index(indexName).type(typeName);
        }
        return shellClient.explain(explainRequest);
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

    public JsonOutput moreLikeThis(String id) {
        return shellClient.moreLikeThis(indexName, typeName, id);
    }

    public JsonOutput moreLikeThis(MoreLikeThisRequest moreLikeThisRequest) {
        if (moreLikeThisRequest != null) {
            if (!indexName.equals(moreLikeThisRequest.index())) {
                //the needed method is not public, creating a brand new request
                //moreLikeThisRequest.index(indexName);
                moreLikeThisRequest = MoreLikeThisHelper.newMoreLikeThisRequest(moreLikeThisRequest, indexName);
            }
            moreLikeThisRequest.type(typeName);
        }
        return shellClient.moreLikeThis(moreLikeThisRequest);
    }

    public JsonOutput percolate(JsonInput source) {
        return shellClient.percolate(indexName, typeName, source);
    }

    public JsonOutput percolate(String source) {
        return shellClient.percolate(indexName, typeName, source);
    }

    public JsonOutput percolate(PercolateRequest percolateRequest) {
        if (percolateRequest != null) {
            percolateRequest.index(indexName).type(typeName);
        }
        return shellClient.percolate(percolateRequest);
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

    public JsonOutput search(SearchSourceBuilder searchSourceBuilder) {
        return shellClient.search(Requests.searchRequest(indexName).types(typeName).source(searchSourceBuilder));
    }

    public JsonOutput search(SearchRequest searchRequest) {
        if (searchRequest != null) {
            searchRequest.indices(indexName).types(typeName);
        }
        return shellClient.search(searchRequest);
    }

    public JsonOutput update(String id, String script) {
        return shellClient.update(new UpdateRequest(indexName, typeName, id).script(script));
    }

    public JsonOutput update(UpdateRequest updateRequest) {
        if (updateRequest != null) {
            updateRequest.index(indexName).type(typeName);
        }
        return shellClient.update(updateRequest);
    }

    public JsonOutput validate(JsonInput source) {
        return shellClient.validate(indexName, typeName, source);
    }

    public JsonOutput validate(String source) {
        return shellClient.validate(indexName, typeName, source);
    }

    public JsonOutput validate(QueryBuilder queryBuilder) {
        return shellClient.validate(indexName, typeName, queryBuilder);
    }

    public JsonOutput validate(ValidateQueryRequest validateQueryRequest) {
        if (validateQueryRequest != null) {
            validateQueryRequest.indices(indexName).types(typeName);
        }
        return shellClient.validate(validateQueryRequest);
    }

    @Override
    public String toString() {
        return shellClient.toString() + " - index [" + indexName + "] type [" + typeName + "]";
    }
}
