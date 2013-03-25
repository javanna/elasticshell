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
package org.elasticsearch.shell.client.builders.core;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IgnoreIndices;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderToXContent;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import java.io.IOException;
import java.util.Map;

/**
 * @author Luca Cavanna
 *
 * Request builder for search API
 */
@SuppressWarnings("unused")
public class SearchRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderToXContent<SearchRequest, SearchResponse, JsonInput, JsonOutput> {

    private SearchSourceBuilder sourceBuilder;

    public SearchRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new SearchRequest(new String[0]), jsonToString, stringToJson);
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        request.indices(indices);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> types(String... types) {
        request.types(types);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> searchType(String searchType) {
        request.searchType(searchType);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> scroll(String keepAlive) {
        request.scroll(keepAlive);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> timeout(String timeout) {
        sourceBuilder().timeout(timeout);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> routing(String... routing) {
        request.routing(routing);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> preference(String preference) {
        request.preference(preference);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> operationThreading(String operationThreading) {
        request.operationThreading(operationThreading);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> ignoreIndices(String ignoreIndices) {
        request().ignoreIndices(IgnoreIndices.fromString(ignoreIndices));
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> queryBuilder(QueryBuilder queryBuilder) {
        sourceBuilder().query(queryBuilder);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> query(JsonInput query) {
        sourceBuilder().query(jsonToString(query));
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> filterBuilder(FilterBuilder filter) {
        sourceBuilder().filter(filter);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> filter(JsonInput filter) {
        sourceBuilder().filter(jsonToString(filter));
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> minScore(float minScore) {
        sourceBuilder().minScore(minScore);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> from(int from) {
        sourceBuilder().from(from);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> size(int size) {
        sourceBuilder().size(size);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> explain(boolean explain) {
        sourceBuilder().explain(explain);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> version(boolean version) {
        sourceBuilder().version(version);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> indexBoost(String index, float indexBoost) {
        sourceBuilder().indexBoost(index, indexBoost);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> stats(String... statsGroups) {
        sourceBuilder().stats(statsGroups);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> noFields() {
        sourceBuilder().noFields();
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> scriptField(String name, String script) {
        sourceBuilder().scriptField(name, script);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> scriptField(String name, String script, Map<String, Object> params) {
        sourceBuilder().scriptField(name, script, params);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> partialField(String name, @Nullable String include, @Nullable String exclude) {
        sourceBuilder().partialField(name, include, exclude);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> partialField(String name, @Nullable String[] includes, @Nullable String[] excludes) {
        sourceBuilder().partialField(name, includes, excludes);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> scriptField(String name, String lang, String script, Map<String, Object> params) {
        sourceBuilder().scriptField(name, lang, script, params);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> sort(String field, SortOrder order) {
        sourceBuilder().sort(field, order);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> sort(SortBuilder sort) {
        sourceBuilder().sort(sort);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> trackScores(boolean trackScores) {
        sourceBuilder().trackScores(trackScores);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> fields(String... fields) {
        sourceBuilder().fields(fields);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> facet(FacetBuilder facet) {
        sourceBuilder().facet(facet);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> facets(JsonInput facets) {
        sourceBuilder().facets(new BytesArray(jsonToString(facets)));
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> source(JsonInput source) {
        request.source(jsonToString(source));
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> extraSource(JsonInput source) {
        request.extraSource(jsonToString(source));
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> highlightedField(String name) {
        highlightBuilder().field(name);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> highlightedField(String name, int fragmentSize) {
        highlightBuilder().field(name, fragmentSize);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> highlightedField(String name, int fragmentSize, int numberOfFragments) {
        highlightBuilder().field(name, fragmentSize, numberOfFragments);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> highlightedField(String name, int fragmentSize, int numberOfFragments,
                                                    int fragmentOffset) {
        highlightBuilder().field(name, fragmentSize, numberOfFragments, fragmentOffset);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> highlightedField(HighlightBuilder.Field field) {
        highlightBuilder().field(field);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> highlighterTagsSchema(String schemaName) {
        highlightBuilder().tagsSchema(schemaName);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> highlighterPreTags(String... preTags) {
        highlightBuilder().preTags(preTags);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> highlighterPostTags(String... postTags) {
        highlightBuilder().postTags(postTags);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> highlighterOrder(String order) {
        highlightBuilder().order(order);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> highlighterEncoder(String encoder) {
        highlightBuilder().encoder(encoder);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> highlighterRequireFieldMatch(boolean requireFieldMatch) {
        highlightBuilder().requireFieldMatch(requireFieldMatch);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> highlighterType(String type) {
        highlightBuilder().highlighterType(type);
        return this;
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> highlight(HighlightBuilder highlightBuilder) {
        sourceBuilder().highlight(highlightBuilder);
        return this;
    }

    private HighlightBuilder highlightBuilder() {
        return sourceBuilder().highlighter();
    }

    private SearchSourceBuilder sourceBuilder() {
        if (sourceBuilder == null) {
            sourceBuilder = new SearchSourceBuilder();
        }
        return sourceBuilder;
    }

    @Override
    public SearchRequest request() {
        if (request.source() != null) {
            return request;
        }
        if (sourceBuilder != null) {
            request.source(sourceBuilder);
        } else {
            request.source(sourceBuilder().query(QueryBuilders.matchAllQuery()));
        }
        return request;
    }

    @Override
    protected ActionFuture<SearchResponse> doExecute(SearchRequest request) {
        return client.search(request);
    }

    @Override
    protected XContentBuilder initContentBuilder() throws IOException {
        return super.initContentBuilder().startObject();
    }

    @Override
    protected XContentBuilder toXContent(SearchRequest request, SearchResponse response, XContentBuilder builder) throws IOException {
        return super.toXContent(request, response, builder).endObject();
    }
}
