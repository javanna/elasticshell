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
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilder;
import org.elasticsearch.shell.json.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for delete API
 */
@SuppressWarnings("unused")
public class DeleteRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilder<DeleteRequest, DeleteResponse, JsonInput, JsonOutput> {

    public DeleteRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client,new DeleteRequest(), jsonSerializer);
    }

    public DeleteRequestBuilder timeout(String timeout) {
        request.timeout(timeout);
        return this;
    }

    public DeleteRequestBuilder index(String index) {
        request.index(index);
        return this;
    }

    public DeleteRequestBuilder type(String type) {
        request.type(type);
        return this;
    }

    public DeleteRequestBuilder id(String id) {
        request.id(id);
        return this;
    }

    public DeleteRequestBuilder parent(String parent) {
        request.parent(parent);
        return this;
    }

    public DeleteRequestBuilder routing(String routing) {
        request.routing(routing);
        return this;
    }

    public DeleteRequestBuilder refresh(boolean refresh) {
        request.refresh(refresh);
        return this;
    }

    public DeleteRequestBuilder version(long version) {
        request.version(version);
        return this;
    }

    public DeleteRequestBuilder versionType(String versionType) {
        request.versionType(VersionType.fromString(versionType));
        return this;
    }

    public DeleteRequestBuilder replicationType(String replicationType) {
        request.replicationType(replicationType);
        return this;
    }

    public DeleteRequestBuilder consistencyLevel(String consistencyLevel) {
        request.consistencyLevel(WriteConsistencyLevel.fromString(consistencyLevel));
        return this;
    }

    @Override
    protected ActionFuture<DeleteResponse> doExecute(DeleteRequest request) {
        return client.delete(request);
    }

    @Override
    protected XContentBuilder toXContent(DeleteRequest request, DeleteResponse response, XContentBuilder builder) throws IOException {
        return builder.startObject()
                .field(Fields.OK, true)
                .field(Fields.FOUND, !response.notFound())
                .field(Fields._INDEX, response.index())
                .field(Fields._TYPE, response.type())
                .field(Fields._ID, response.id())
                .field(Fields._VERSION, response.version())
                .endObject();
    }
}
