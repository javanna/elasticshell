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

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.shell.client.builders.indices.*;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

/**
 * @author Luca Cavanna
 *
 * Client that exposes all the indices Apis
 */
@SuppressWarnings("unused")
public class IndicesApiClient<EsClient extends org.elasticsearch.client.support.AbstractClient, JsonInput, JsonOutput> {

    private final AbstractClient<EsClient, JsonInput, JsonOutput> client;
    private final JsonToString<JsonInput> jsonToString;
    private final StringToJson<JsonOutput> stringToJson;

    IndicesApiClient(AbstractClient<EsClient, JsonInput, JsonOutput> client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        this.client = client;
        this.jsonToString = jsonToString;
        this.stringToJson = stringToJson;
    }
    
    public GetAliasesIndicesRequestBuilder<JsonInput, JsonOutput> aliasesGetBuilder() {
        return new GetAliasesIndicesRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput aliasesGet() {
        return aliasesGetBuilder().execute();
    }

    public UpdateIndicesAliasesRequestBuilder<JsonInput, JsonOutput> aliasesUpdateBuilder() {
        return new UpdateIndicesAliasesRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public AnalyzeRequestBuilder<JsonInput, JsonOutput> analyzeBuilder() {
        return new AnalyzeRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput analyze(String text, String index, String field) {
        return analyzeBuilder().index(index).field(field).text(text).execute();
    }

    public JsonOutput analyze(String text, String analyzer) {
        return analyzeBuilder().analyzer(analyzer).text(text).execute();
    }

    public ClearCacheRequestBuilder<JsonInput, JsonOutput> clearCacheBuilder() {
        return new ClearCacheRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput clearCache() {
        return clearCacheBuilder().execute();
    }

    public CloseIndexRequestBuilder<JsonInput, JsonOutput> closeIndexBuilder() {
        return new CloseIndexRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput closeIndex(String index) {
        return closeIndexBuilder().index(index).execute();
    }

    public CreateIndexRequestBuilder<JsonInput, JsonOutput> createIndexBuilder() {
        return new CreateIndexRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput createIndex(String index) {
        return createIndexBuilder().index(index).execute();
    }

    public DeleteIndexRequestBuilder<JsonInput, JsonOutput> deleteIndexBuilder() {
        return new DeleteIndexRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput deleteIndex() {
        return deleteIndexBuilder().execute();
    }

    public FlushRequestBuilder<JsonInput, JsonOutput> flushBuilder() {
        return new FlushRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput flush() {
        return flushBuilder().execute();
    }

    protected IndicesExistsRequestBuilder<JsonInput, JsonOutput> indicesExistsBuilder() {
        return new IndicesExistsRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput indicesExists(String... indices) {
        return indicesExistsBuilder().indices(indices).execute();
    }

    public GetMappingRequestBuilder<JsonInput, JsonOutput> mappingGetBuilder() {
        return new GetMappingRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput mappingGet(String... indices) {
        return mappingGetBuilder().indices(indices).execute();
    }

    public DeleteMappingRequestBuilder<JsonInput, JsonOutput> mappingDeleteBuilder() {
        return new DeleteMappingRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput mappingDelete(String index, String type) {
        return mappingDeleteBuilder().indices(index).type(type).execute();
    }

    public PutMappingRequestBuilder<JsonInput, JsonOutput> mappingPutBuilder() {
        return new PutMappingRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput mappingPut(String index, String type, JsonInput source) {
        return mappingPutBuilder().indices(index).type(type).source(source).execute();
    }

    public OpenIndexRequestBuilder<JsonInput, JsonOutput> openIndexBuilder() {
        return new OpenIndexRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput openIndex(String index) {
        return openIndexBuilder().index(index).execute();
    }

    public OptimizeRequestBuilder<JsonInput, JsonOutput> optimizeBuilder() {
        return new OptimizeRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput optimize(String... indices) {
        return optimizeBuilder().indices(indices).execute();
    }

    public RefreshRequestBuilder<JsonInput, JsonOutput> refreshBuilder() {
        return new RefreshRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput refresh(String... indices) {
        return refreshBuilder().indices(indices).execute();
    }

    protected SegmentsRequestBuilder<JsonInput, JsonOutput> segmentsBuilder() {
        return new SegmentsRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput segments(String... indices) {
        return segmentsBuilder().indices(indices).execute();
    }

    public GetSettingsRequestBuilder<JsonInput, JsonOutput> settingsGetBuilder() {
        return new GetSettingsRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput settingsGet(String... indices) {
        return settingsGetBuilder().indices(indices).execute();
    }

    public UpdateSettingsRequestBuilder<JsonInput, JsonOutput> settingsUpdateBuilder() {
        return new UpdateSettingsRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput settingsUpdate(String index, JsonInput source) {
        return settingsUpdateBuilder().indices(index).settings(source).execute();
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> statsBuilder() {
        return new StatsRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput stats(String... indices) {
        return statsBuilder().indices(indices).execute();
    }

    public StatusRequestBuilder<JsonInput, JsonOutput> statusBuilder() {
        return new StatusRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput status(String... indices) {
        return statusBuilder().indices(indices).execute();
    }

    public GetIndexTemplateRequestBuilder<JsonInput, JsonOutput> templateGetBuilder() {
        return new GetIndexTemplateRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput templateGet(String... names) {
        return templateGetBuilder().names(names).execute();
    }

    public DeleteIndexTemplateRequestBuilder<JsonInput, JsonOutput> templateDeleteBuilder(String name) {
        return new DeleteIndexTemplateRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson, name);
    }

    public JsonOutput templateDelete(String name) {
        return templateDeleteBuilder(name).execute();
    }

    public PutIndexTemplateRequestBuilder<JsonInput, JsonOutput> templatePutBuilder() {
        return new PutIndexTemplateRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput templatePut(String name, JsonInput source) {
        return templatePutBuilder().template(name).source(source).execute();
    }

    public TypesExistsRequestBuilder<JsonInput, JsonOutput> typesExistsBuilder() {
        return new TypesExistsRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput typesExists(String index, String type) {
        return typesExistsBuilder().indices(index).types(type).execute();
    }

    public GetWarmerRequestBuilder<JsonInput, JsonOutput> warmerGetBuilder() {
        return new GetWarmerRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput warmerGet(String... indices) {
        return warmerGetBuilder().indices(indices).execute();
    }

    public PutWarmerRequestBuilder<JsonInput, JsonOutput> warmerPutBuilder() {
        return new PutWarmerRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput warmerPut(String name, SearchRequest searchRequest) {
        return warmerPutBuilder().name(name).searchRequest(searchRequest).execute();
    }

    public JsonOutput warmerPut(String name, JsonInput source) {
        return warmerPutBuilder().name(name).source(source).execute();
    }

    public DeleteWarmerRequestBuilder<JsonInput, JsonOutput> warmerDeleteBuilder() {
        return new DeleteWarmerRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput warmerDelete(String index, String name) {
        return warmerDeleteBuilder().indices(index).name(name).execute();
    }

    public JsonOutput warmerDelete(String index) {
        return warmerDeleteBuilder().indices(index).execute();
    }

    @Override
    public String toString() {
        return client.toString();
    }
}
