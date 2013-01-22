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
package org.elasticsearch.shell.client.executors.core;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequest;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.deletebyquery.IndexDeleteByQueryResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.executors.AbstractRequestExecutor;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * {@link org.elasticsearch.shell.client.executors.RequestExecutor} implementation for delete by query API
 */
public class DeleteByQueryRequestExecutor<JsonInput, JsonOutput>  extends AbstractRequestExecutor<DeleteByQueryRequest, DeleteByQueryResponse, JsonInput, JsonOutput> {

    public DeleteByQueryRequestExecutor(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, jsonSerializer);
    }

    @Override
    protected ActionFuture<DeleteByQueryResponse> doExecute(DeleteByQueryRequest request) {
        return client.deleteByQuery(request);
    }

    @Override
    protected XContentBuilder toXContent(DeleteByQueryRequest request, DeleteByQueryResponse response, XContentBuilder builder) throws IOException {
        builder.startObject().field(Fields.OK, true);
        builder.startObject("_indices");
        for (IndexDeleteByQueryResponse indexDeleteByQueryResponse : response.indices().values()) {
            builder.startObject(indexDeleteByQueryResponse.index(), XContentBuilder.FieldCaseConversion.NONE);
            builder.startObject("_shards");
            builder.field("total", indexDeleteByQueryResponse.totalShards());
            builder.field("successful", indexDeleteByQueryResponse.successfulShards());
            builder.field("failed", indexDeleteByQueryResponse.failedShards());
            builder.endObject();
            builder.endObject();
        }
        builder.endObject();
        builder.endObject();
        return builder;
    }
}