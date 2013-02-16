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
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.support.IgnoreIndices;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for types exists API
 */
@SuppressWarnings("unused")
public class TypesExistsRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<TypesExistsRequest, TypesExistsResponse, JsonInput, JsonOutput> {

    public TypesExistsRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new TypesExistsRequest(new String[0]), jsonToString, stringToJson);
    }

    public TypesExistsRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        request.indices(indices);
        return this;
    }

    public TypesExistsRequestBuilder<JsonInput, JsonOutput> types(String... types) {
        request.types(types);
        return this;
    }

    public TypesExistsRequestBuilder<JsonInput, JsonOutput> ignoreIndices(String ignoreIndices) {
        request.ignoreIndices(IgnoreIndices.fromString(ignoreIndices));
        return this;
    }

    @Override
    protected ActionFuture<TypesExistsResponse> doExecute(TypesExistsRequest request) {
        return client.admin().indices().typesExists(request);
    }

    @Override
    protected XContentBuilder toXContent(TypesExistsRequest request, TypesExistsResponse response, XContentBuilder builder) throws IOException {
        builder.startObject();
        builder.field(Fields.OK, response.exists());
        builder.endObject();
        return builder;
    }
}
