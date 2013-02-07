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
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderToXContent;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for more like this API
 */
@SuppressWarnings("unused")
public class MoreLikeThisRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderToXContent<MoreLikeThisRequest, SearchResponse, JsonInput, JsonOutput> {

    public MoreLikeThisRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new MoreLikeThisRequest(null), jsonSerializer);
    }

    public MoreLikeThisRequestBuilder index(String index) {
        this.request = new MoreLikeThisRequest(index);
        return this;
    }

    public MoreLikeThisRequestBuilder type(String type) {
        request.type(type);
        return this;
    }

    public MoreLikeThisRequestBuilder fields(String... fields) {
        request.fields(fields);
        return this;
    }

    public MoreLikeThisRequestBuilder routing(String routing) {
        request.routing(routing);
        return this;
    }

    public MoreLikeThisRequestBuilder percentTermsToMatch(float percentTermsToMatch) {
        request.percentTermsToMatch(percentTermsToMatch);
        return this;
    }

    public MoreLikeThisRequestBuilder minTermFreq(int minTermFreq) {
        request.minTermFreq(minTermFreq);
        return this;
    }

    public MoreLikeThisRequestBuilder maxQueryTerms(int maxQueryTerms) {
        request.maxQueryTerms(maxQueryTerms);
        return this;
    }

    public MoreLikeThisRequestBuilder stopWords(String... stopWords) {
        request.stopWords(stopWords);
        return this;
    }

    public MoreLikeThisRequestBuilder minDocFreq(int minDocFreq) {
        request.minDocFreq(minDocFreq);
        return this;
    }

    public MoreLikeThisRequestBuilder maxDocFreq(int maxDocFreq) {
        request.maxDocFreq(maxDocFreq);
        return this;
    }

    public MoreLikeThisRequestBuilder minWordLen(int minWordLen) {
        request.minWordLen(minWordLen);
        return this;
    }

    public MoreLikeThisRequestBuilder maxWordLen(int maxWordLen) {
        request.maxWordLen(maxWordLen);
        return this;
    }

    public MoreLikeThisRequestBuilder boostTerms(float boostTerms) {
        request.boostTerms(boostTerms);
        return this;
    }

    public MoreLikeThisRequestBuilder searchSource(SearchSourceBuilder sourceBuilder) {
        request.searchSource(sourceBuilder);
        return this;
    }

    public MoreLikeThisRequestBuilder searchSource(String searchSource) {
        request.searchSource(searchSource);
        return this;
    }

    public MoreLikeThisRequestBuilder searchSource(JsonInput searchSource) {
        request.searchSource(jsonToString(searchSource));
        return this;
    }

    public MoreLikeThisRequestBuilder searchType(String searchType) {
        request.searchType(searchType);
        return this;
    }

    public MoreLikeThisRequestBuilder searchIndices(String... searchIndices) {
        request.searchIndices(searchIndices);
        return this;
    }

    public MoreLikeThisRequestBuilder searchTypes(String... searchTypes) {
        request.searchTypes(searchTypes);
        return this;
    }

    public MoreLikeThisRequestBuilder searchScroll(Scroll searchScroll) {
        request.searchScroll(searchScroll);
        return this;
    }

    public MoreLikeThisRequestBuilder searchSize(int size) {
        request.searchSize(size);
        return this;
    }

    public MoreLikeThisRequestBuilder searchFrom(int from) {
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
