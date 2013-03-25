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
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import java.io.IOException;

import static org.elasticsearch.rest.action.support.RestActions.buildBroadcastShardsHeader;

/**
 * @author Luca Cavanna
 *
 * Request builder for refresh API
 */
@SuppressWarnings("unused")
public class RefreshRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<RefreshRequest, RefreshResponse, JsonInput, JsonOutput> {

    public RefreshRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new RefreshRequest(), jsonToString, stringToJson);
    }

    public RefreshRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        this.request.indices(indices);
        return this;
    }

    public RefreshRequestBuilder<JsonInput, JsonOutput> waitForOperations(boolean waitForOperations) {
        this.request.waitForOperations(waitForOperations);
        return this;
    }

    @Override
    protected ActionFuture<RefreshResponse> doExecute(RefreshRequest request) {
        return client.admin().indices().refresh(request);
    }

    @Override
    protected XContentBuilder toXContent(RefreshRequest request, RefreshResponse response, XContentBuilder builder) throws IOException {
        builder.startObject();
        builder.field(Fields.OK, true);
        buildBroadcastShardsHeader(builder, response);
        builder.endObject();
        return builder;
    }
}
