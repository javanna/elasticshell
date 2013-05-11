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

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.shell.client.builders.core.*;
import org.elasticsearch.shell.client.builders.indices.DeleteMappingRequestBuilder;
import org.elasticsearch.shell.client.builders.indices.GetMappingRequestBuilder;
import org.elasticsearch.shell.client.builders.indices.PutMappingRequestBuilder;

/**
 * @author Luca Cavanna
 *
 * Internal shell client that exposes operations available on a single type
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
@SuppressWarnings("unused")
public class InternalTypeClient<JsonInput, JsonOutput> {

    private final AbstractClient<? extends org.elasticsearch.client.support.AbstractClient, JsonInput, JsonOutput> shellClient;
    private final String indexName;
    private final String typeName;

    public InternalTypeClient(AbstractClient<? extends org.elasticsearch.client.support.AbstractClient, JsonInput, JsonOutput> shellClient, String indexName, String typeName) {
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

    /*
    Core APIs
     */
    public CountRequestBuilder<JsonInput, JsonOutput> countBuilder() {
        return shellClient.countBuilder().indices(indexName).types(typeName);
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
        return shellClient.deleteBuilder().index(indexName).type(typeName);
    }

    public JsonOutput delete(String id) {
        return deleteBuilder().id(id).execute();
    }

    public DeleteByQueryRequestBuilder<JsonInput, JsonOutput> deleteByQueryBuilder() {
        return shellClient.deleteByQueryBuilder().indices(indexName).types(typeName);
    }

    public JsonOutput deleteByQuery(String queryString) {
        return deleteByQueryBuilder().queryBuilder(QueryBuilders.queryString(queryString)).execute();
    }

    public JsonOutput deleteByQuery(JsonInput query) {
        return deleteByQueryBuilder().query(query).execute();
    }

    public ExplainRequestBuilder<JsonInput, JsonOutput> explainBuilder() {
        return shellClient.explainBuilder().index(indexName).type(typeName);
    }

    public JsonOutput explain(String id, String queryString) {
        return explainBuilder().id(id).
                queryBuilder(QueryBuilders.queryString(queryString)).execute();
    }

    public JsonOutput explain(String id, JsonInput source) {
        return explainBuilder().id(id).source(source).execute();
    }

    public GetRequestBuilder<JsonInput, JsonOutput> getBuilder() {
        return shellClient.getBuilder().index(indexName).type(typeName);
    }

    public JsonOutput get(String id) {
        return getBuilder().id(id).execute();
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> indexBuilder() {
        return shellClient.indexBuilder().index(indexName).type(typeName);
    }

    public JsonOutput index(String id, JsonInput source) {
        return indexBuilder().id(id).source(source).execute();
    }

    public JsonOutput multiGet(String... ids) {
        return shellClient.multiGetBuilder().add(indexName, typeName, ids).execute();
    }

    public JsonOutput multiSearch(JsonInput... sources) {
        MultiSearchRequestBuilder<JsonInput, JsonOutput> multiSearchRequestBuilder = shellClient.multiSearchBuilder();
        if (sources != null) {
            for (JsonInput source : sources) {
                multiSearchRequestBuilder.add(indexName, typeName, source);
            }
        }
        return multiSearchRequestBuilder.execute();
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> moreLikeThisBuilder() {
        return shellClient.moreLikeThisBuilder(indexName).type(typeName);
    }

    public JsonOutput moreLikeThis(String id) {
        return moreLikeThisBuilder().id(id).execute();
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> searchBuilder() {
        return shellClient.searchBuilder().indices(indexName).types(typeName);
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
        return shellClient.updateBuilder().index(indexName).type(typeName);
    }

    public JsonOutput update(String id, String script) {
        return updateBuilder().id(id).script(script).execute();
    }

    public ValidateQueryRequestBuilder<JsonInput, JsonOutput> validateBuilder() {
        return shellClient.validateBuilder().indices(indexName).types(typeName);
    }

    public JsonOutput validate(String queryString) {
        return validateBuilder().queryBuilder(QueryBuilders.queryString(queryString)).execute();
    }

    public JsonOutput validate(JsonInput query) {
        return validateBuilder().query(query).execute();
    }

    /*
    Indices APIs that make sense for a specific type
     */
    protected GetMappingRequestBuilder<JsonInput, JsonOutput> mappingGetBuilder() {
        return shellClient.indicesApi().mappingGetBuilder().indices(indexName).types(typeName);
    }

    public JsonOutput mappingGet() {
        return mappingGetBuilder().execute();
    }

    public DeleteMappingRequestBuilder<JsonInput, JsonOutput> mappingDeleteBuilder() {
        return shellClient.indicesApi().mappingDeleteBuilder().indices(indexName).type(typeName);
    }

    public JsonOutput mappingDelete() {
        return mappingDeleteBuilder().execute();
    }

    public PutMappingRequestBuilder<JsonInput, JsonOutput> mappingPutBuilder() {
        return shellClient.indicesApi().mappingPutBuilder().indices(indexName).type(typeName);
    }

    public JsonOutput mappingPut(JsonInput source) {
        return mappingPutBuilder().source(source).execute();
    }

    @Override
    public String toString() {
        return shellClient.toString() + " - index [" + indexName + "] type [" + typeName + "]";
    }
}
