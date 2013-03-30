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

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.suggest.SuggestRequest;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import java.io.IOException;

import static org.elasticsearch.rest.action.support.RestActions.buildBroadcastShardsHeader;

/**
 * @author Luca Cavanna
 *
 * Request builder for suggest API
 */
public class SuggestRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<SuggestRequest, SuggestResponse, JsonInput, JsonOutput> {

    final SuggestBuilder suggest = new SuggestBuilder();

    public SuggestRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new SuggestRequest(), jsonToString, stringToJson);
    }

    public SuggestRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        this.request.indices(indices);
        return this;
    }

    public <T> SuggestRequestBuilder<JsonInput, JsonOutput> addSuggestion(SuggestBuilder.SuggestionBuilder<T> suggestion) {
        suggest.addSuggestion(suggestion);
        return this;
    }

    public SuggestRequestBuilder<JsonInput, JsonOutput> suggestText(String globalText) {
        this.suggest.setText(globalText);
        return this;
    }

    public SuggestRequestBuilder<JsonInput, JsonOutput> preference(String preference) {
        request.preference(preference);
        return this;
    }

    public SuggestRequestBuilder<JsonInput, JsonOutput> routing(String... routing) {
        request.routing(routing);
        return this;
    }

    @Override
    protected SuggestRequest request() {
        SuggestRequest suggestRequest = super.request();
        try {
            XContentBuilder builder = XContentFactory.contentBuilder(Requests.CONTENT_TYPE);
            suggest.toXContent(builder, ToXContent.EMPTY_PARAMS);
            suggestRequest.suggest(builder.bytes());
        } catch (IOException e) {
            throw new ElasticSearchException("Unable to build suggestion request", e);
        }
        return suggestRequest;
    }

    @Override
    protected ActionFuture<SuggestResponse> doExecute(SuggestRequest request) {
        return client.suggest(request);
    }

    @Override
    protected XContentBuilder toXContent(SuggestRequest request, SuggestResponse response, XContentBuilder builder) throws IOException {
        builder.startObject();
        buildBroadcastShardsHeader(builder, response);
        Suggest suggest = response.getSuggest();
        if (suggest != null) {
            suggest.toXContent(builder, ToXContent.EMPTY_PARAMS);
        }
        builder.endObject();
        return builder;
    }
}
