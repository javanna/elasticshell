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
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for indices exists API
 */
@SuppressWarnings("unused")
public class IndicesExistsRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<IndicesExistsRequest, IndicesExistsResponse, JsonInput, JsonOutput> {

    public IndicesExistsRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new IndicesExistsRequest(), jsonToString, stringToJson);
    }

    public IndicesExistsRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        this.request.indices(indices);
        return this;
    }

    @Override
    protected ActionFuture<IndicesExistsResponse> doExecute(IndicesExistsRequest request) {
        return client.admin().indices().exists(request);
    }

    @Override
    protected XContentBuilder toXContent(IndicesExistsRequest request, IndicesExistsResponse response, XContentBuilder builder) throws IOException {
        builder.startObject();
        builder.field(Fields.OK, response.isExists());
        builder.endObject();
        return builder;
    }
}
