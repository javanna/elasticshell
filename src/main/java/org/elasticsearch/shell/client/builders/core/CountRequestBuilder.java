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
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilder;
import org.elasticsearch.shell.json.JsonSerializer;

import java.io.IOException;

import static org.elasticsearch.rest.action.support.RestActions.buildBroadcastShardsHeader;

/**
 * @author Luca Cavanna
 *
 * Request builder for count API
 */
@SuppressWarnings("unused")
public class CountRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilder<CountRequest, CountResponse, JsonInput, JsonOutput> {

    public CountRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new CountRequest(), jsonSerializer);
    }

    public CountRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        request.indices(indices);
        return this;
    }

    public CountRequestBuilder<JsonInput, JsonOutput> types(String... types) {
        request.types(types);
        return this;
    }

    public CountRequestBuilder<JsonInput, JsonOutput> minScore(float minScore) {
        request.minScore(minScore);
        return this;
    }

    public CountRequestBuilder<JsonInput, JsonOutput> queryHint(String queryHint) {
        request.queryHint(queryHint);
        return this;
    }

    public CountRequestBuilder<JsonInput, JsonOutput> routing(String... routing) {
        request.routing(routing);
        return this;
    }

    public CountRequestBuilder<JsonInput, JsonOutput> query(QueryBuilder queryBuilder) {
        request.query(queryBuilder);
        return this;
    }

    public CountRequestBuilder<JsonInput, JsonOutput> query(JsonInput query) {
        request.query(jsonToString(query));
        return this;
    }

    @Override
    protected ActionFuture<CountResponse> doExecute(CountRequest request) {
        return client.count(request);
    }

    @Override
    protected XContentBuilder toXContent(CountRequest request, CountResponse response, XContentBuilder builder) throws IOException {
        builder.startObject();
        builder.field(Fields.COUNT, response.count());
        buildBroadcastShardsHeader(builder, response);
        builder.endObject();
        return builder;
    }
}
