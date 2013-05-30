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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderToXContent;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

/**
 * @author Luca Cavanna
 *
 * Request builder for scroll API
 */
public class SearchScrollRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderToXContent<SearchScrollRequest, SearchResponse, JsonInput, JsonOutput> {

    public SearchScrollRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new SearchScrollRequest(), jsonToString, stringToJson);
    }

    public SearchScrollRequestBuilder<JsonInput, JsonOutput> scrollId(String scrollId) {
        request.scrollId(scrollId);
        return this;
    }

    public SearchScrollRequestBuilder<JsonInput, JsonOutput> scroll(String keepAlive) {
        request.scroll(keepAlive);
        return this;
    }

    @Override
    protected ActionFuture<SearchResponse> doExecute(SearchScrollRequest request) {
        return client.searchScroll(request);
    }

    @Override
    protected XContentBuilder initContentBuilder() throws IOException {
        return super.initContentBuilder().startObject();
    }

    @Override
    protected XContentBuilder toXContent(SearchScrollRequest request, SearchResponse response, XContentBuilder builder) throws IOException {
        return super.toXContent(request, response, builder).endObject();
    }
}
