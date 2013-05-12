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

import java.io.IOException;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderToXContent;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

/**
 * @author Luca Cavanna
 *
 * Request builder for multi search API
 */
@SuppressWarnings("unused")
public class MultiSearchRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderToXContent<MultiSearchRequest, MultiSearchResponse, JsonInput, JsonOutput> {

    public MultiSearchRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new MultiSearchRequest(), jsonToString, stringToJson);
    }

    public MultiSearchRequestBuilder<JsonInput, JsonOutput> add(String index, String type, JsonInput source) {
        this.request.add(new SearchRequest().indices(index).types(type).source(jsonToString(source)));
        return this;
    }

    public MultiSearchRequestBuilder<JsonInput, JsonOutput> add(String index, JsonInput source) {
        this.request.add(new SearchRequest().indices(index).source(jsonToString(source)));
        return this;
    }

    public MultiSearchRequestBuilder<JsonInput, JsonOutput> add(JsonInput source) {
        this.request.add(new SearchRequest().indices(new String[0]).source(jsonToString(source)));
        return this;
    }

    public MultiSearchRequestBuilder<JsonInput, JsonOutput> add(SearchRequest request) {
        this.request.add(request);
        return this;
    }

    public MultiSearchRequestBuilder<JsonInput, JsonOutput> add(SearchSourceBuilder searchSourceBuilder) {
        this.request.add(Requests.searchRequest().source(searchSourceBuilder));
        return this;
    }

    @Override
    protected ActionFuture<MultiSearchResponse> doExecute(MultiSearchRequest request) {
        return client.multiSearch(request);
    }

    @Override
    protected XContentBuilder toXContent(MultiSearchRequest request, MultiSearchResponse response, XContentBuilder builder) throws IOException {
        return super.toXContent(request, response, builder.startObject()).endObject();
    }
}
