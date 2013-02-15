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
import org.elasticsearch.action.mlt.MoreLikeThisRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderToXContent;
import org.elasticsearch.shell.json.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for more like this API
 */
@SuppressWarnings("unused")
public class MoreLikeThisRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderToXContent<MoreLikeThisRequest, SearchResponse, JsonInput, JsonOutput> {

    public MoreLikeThisRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer, String index) {
        super(client, new MoreLikeThisRequest(index), jsonSerializer);
    }

    //The index is received as input in the constructor and is not modifiable after the creation of the builder
    //since there's no index setter exposed on MoreLikeThisRequest

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> id(String id) {
        request.id(id);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> type(String type) {
        request.type(type);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> fields(String... fields) {
        request.fields(fields);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> routing(String routing) {
        request.routing(routing);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> percentTermsToMatch(float percentTermsToMatch) {
        request.percentTermsToMatch(percentTermsToMatch);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> minTermFreq(int minTermFreq) {
        request.minTermFreq(minTermFreq);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> maxQueryTerms(int maxQueryTerms) {
        request.maxQueryTerms(maxQueryTerms);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> stopWords(String... stopWords) {
        request.stopWords(stopWords);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> minDocFreq(int minDocFreq) {
        request.minDocFreq(minDocFreq);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> maxDocFreq(int maxDocFreq) {
        request.maxDocFreq(maxDocFreq);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> minWordLen(int minWordLen) {
        request.minWordLen(minWordLen);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> maxWordLen(int maxWordLen) {
        request.maxWordLen(maxWordLen);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> boostTerms(float boostTerms) {
        request.boostTerms(boostTerms);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> searchSource(SearchSourceBuilder sourceBuilder) {
        request.searchSource(sourceBuilder);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> searchSource(JsonInput searchSource) {
        request.searchSource(jsonToString(searchSource));
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> searchType(String searchType) {
        request.searchType(searchType);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> searchIndices(String... searchIndices) {
        request.searchIndices(searchIndices);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> searchTypes(String... searchTypes) {
        request.searchTypes(searchTypes);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> searchScroll(Scroll searchScroll) {
        request.searchScroll(searchScroll);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> searchSize(int size) {
        request.searchSize(size);
        return this;
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> searchFrom(int from) {
        request.searchFrom(from);
        return this;
    }

    @Override
    protected ActionFuture<SearchResponse> doExecute(MoreLikeThisRequest request) {
        return client.moreLikeThis(request);
    }

    @Override
    protected XContentBuilder initContentBuilder() throws IOException {
        return super.initContentBuilder().startObject();
    }

    @Override
    protected XContentBuilder toXContent(MoreLikeThisRequest request, SearchResponse response, XContentBuilder builder) throws IOException {
        return super.toXContent(request, response, builder).endObject();
    }
}
