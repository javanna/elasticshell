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
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for put mapping API
 */
@SuppressWarnings("unused")
public class PutMappingRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<PutMappingRequest, PutMappingResponse, JsonInput, JsonOutput> {

    public PutMappingRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new PutMappingRequest(), jsonToString, stringToJson);
    }

    public PutMappingRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        request.indices(indices);
        return this;
    }

    public PutMappingRequestBuilder<JsonInput, JsonOutput> type(String type) {
        request.type(type);
        return this;
    }

    public PutMappingRequestBuilder<JsonInput, JsonOutput> source(JsonInput mappingSource) {
        request.source(jsonToString(mappingSource));
        return this;
    }

    public PutMappingRequestBuilder<JsonInput, JsonOutput> timeout(String timeout) {
        request.timeout(timeout);
        return this;
    }

    public PutMappingRequestBuilder<JsonInput, JsonOutput> ignoreConflicts(boolean ignoreConflicts) {
        request.ignoreConflicts(ignoreConflicts);
        return this;
    }

    @Override
    protected ActionFuture<PutMappingResponse> doExecute(PutMappingRequest request) {
        return client.admin().indices().putMapping(request);
    }

    @Override
    protected XContentBuilder toXContent(PutMappingRequest request, PutMappingResponse response, XContentBuilder builder) throws IOException {
        builder.startObject()
                .field(Fields.OK, true)
                .field(Fields.ACKNOWLEDGED, response.acknowledged());
        builder.endObject();
        return builder;
    }
}
