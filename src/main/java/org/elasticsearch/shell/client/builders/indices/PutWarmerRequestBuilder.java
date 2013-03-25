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
package org.elasticsearch.shell.client.builders.indices;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.warmer.put.PutWarmerRequest;
import org.elasticsearch.action.admin.indices.warmer.put.PutWarmerResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for (put) warmer API
 */
@SuppressWarnings("unused")
public class PutWarmerRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<PutWarmerRequest, PutWarmerResponse, JsonInput, JsonOutput> {

    public PutWarmerRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new PutWarmerRequest(null), jsonToString, stringToJson);
    }

    public PutWarmerRequestBuilder<JsonInput, JsonOutput> name(String name) {
        this.request.name(name);
        return this;
    }

    public PutWarmerRequestBuilder<JsonInput, JsonOutput> searchRequest(SearchRequest searchRequest) {
        request.searchRequest(searchRequest);
        return this;
    }

    public PutWarmerRequestBuilder<JsonInput, JsonOutput> source(String index, String type, JsonInput source) {
        request.searchRequest(new SearchRequest(index).types(type).source(jsonToString(source)));
        return this;
    }

    public PutWarmerRequestBuilder<JsonInput, JsonOutput> source(String index, JsonInput source) {
        request.searchRequest(new SearchRequest(index).source(jsonToString(source)));
        return this;
    }

    public PutWarmerRequestBuilder<JsonInput, JsonOutput> source(JsonInput source) {
        request.searchRequest(new SearchRequest(new String[0]).source(jsonToString(source)));
        return this;
    }

    @Override
    protected ActionFuture<PutWarmerResponse> doExecute(PutWarmerRequest request) {
        return client.admin().indices().putWarmer(request);
    }

    @Override
    protected XContentBuilder toXContent(PutWarmerRequest request, PutWarmerResponse response, XContentBuilder builder) throws IOException {
        builder.startObject()
                .field(Fields.OK, true)
                .field(Fields.ACKNOWLEDGED, response.isAcknowledged());
        builder.endObject();
        return builder;
    }
}
