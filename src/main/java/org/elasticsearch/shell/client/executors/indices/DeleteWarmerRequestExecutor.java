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
package org.elasticsearch.shell.client.executors.indices;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.warmer.delete.DeleteWarmerRequest;
import org.elasticsearch.action.admin.indices.warmer.delete.DeleteWarmerResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.executors.AbstractRequestExecutor;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * {@link org.elasticsearch.shell.client.executors.RequestExecutor} implementation for (delete) warmer API
 */
public class DeleteWarmerRequestExecutor<JsonInput, JsonOutput> extends AbstractRequestExecutor<DeleteWarmerRequest, DeleteWarmerResponse, JsonInput, JsonOutput> {

    public DeleteWarmerRequestExecutor(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, jsonSerializer);
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
