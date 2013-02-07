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
import org.elasticsearch.action.admin.indices.warmer.delete.DeleteWarmerRequest;
import org.elasticsearch.action.admin.indices.warmer.delete.DeleteWarmerResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilder;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for (delete) warmer API
 */
@SuppressWarnings("unused")
public class DeleteWarmerRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilder<DeleteWarmerRequest, DeleteWarmerResponse, JsonInput, JsonOutput> {

    public DeleteWarmerRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new DeleteWarmerRequest(""), jsonSerializer);
    }

    public DeleteWarmerRequestBuilder indices(String... indices) {
        request.indices(indices);
        return this;
    }

    public DeleteWarmerRequestBuilder name(String name) {
        request.name(name);
        return this;
    }

    @Override
    protected ActionFuture<DeleteWarmerResponse> doExecute(DeleteWarmerRequest request) {
        return client.admin().indices().deleteWarmer(request);
    }

    @Override
    protected XContentBuilder toXContent(DeleteWarmerRequest request, DeleteWarmerResponse response, XContentBuilder builder) throws IOException {
        builder.startObject()
                .field(Fields.OK, true)
                .field(Fields.ACKNOWLEDGED, response.acknowledged());
        builder.endObject();
        return builder;
    }
}
