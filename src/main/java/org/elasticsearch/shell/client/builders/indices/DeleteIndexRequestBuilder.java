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
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for delete index API
 */
@SuppressWarnings("unused")
public class DeleteIndexRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<DeleteIndexRequest, DeleteIndexResponse, JsonInput, JsonOutput> {

    public DeleteIndexRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new DeleteIndexRequest(), jsonSerializer);
    }

    public DeleteIndexRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        request.indices(indices);
        return this;
    }

    public DeleteIndexRequestBuilder<JsonInput, JsonOutput> timeout(String timeout) {
        request.timeout(timeout);
        return this;
    }

    @Override
    protected ActionFuture<DeleteIndexResponse> doExecute(DeleteIndexRequest request) {
        return client.admin().indices().delete(request);
    }

    @Override
    protected XContentBuilder toXContent(DeleteIndexRequest request, DeleteIndexResponse response, XContentBuilder builder) throws IOException {
        return builder.startObject()
                .field(Fields.OK, true)
                .field(Fields.ACKNOWLEDGED, response.acknowledged())
                .endObject();
    }
}
