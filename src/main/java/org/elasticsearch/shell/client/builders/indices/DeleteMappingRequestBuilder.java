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
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilder;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for delete mapping API
 */
@SuppressWarnings("unused")
public class DeleteMappingRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilder<DeleteMappingRequest, DeleteMappingResponse, JsonInput, JsonOutput> {

    public DeleteMappingRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new DeleteMappingRequest(), jsonSerializer);
    }

    public DeleteMappingRequestBuilder indices(String... indices) {
        request.indices(indices);
        return this;
    }

    public DeleteMappingRequestBuilder type(String type) {
        request.type(type);
        return this;
    }

    @Override
    protected ActionFuture<DeleteMappingResponse> doExecute(DeleteMappingRequest request) {
        return client.admin().indices().deleteMapping(request);
    }

    @Override
    protected XContentBuilder toXContent(DeleteMappingRequest request, DeleteMappingResponse response, XContentBuilder builder) throws IOException {
        builder.startObject()
                .field(Fields.OK, true);
        builder.endObject();
        return builder;
    }
}
