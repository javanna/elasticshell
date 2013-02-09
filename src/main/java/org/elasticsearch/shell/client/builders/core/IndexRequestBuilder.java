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
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilder;
import org.elasticsearch.shell.json.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for index API
 */
@SuppressWarnings("unused")
public class IndexRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilder<IndexRequest, IndexResponse, JsonInput, JsonOutput> {

    public IndexRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new IndexRequest(), jsonSerializer);
    }

    public IndexRequestBuilder type(String type) {
        request.type(type);
        return this;
    }

    public IndexRequestBuilder id(String id) {
        request.id(id);
        return this;
    }

    public IndexRequestBuilder routing(String routing) {
        request.routing(routing);
        return this;
    }

    public IndexRequestBuilder parent(String parent) {
        request.parent(parent);
        return this;
    }

    public IndexRequestBuilder source(JsonInput source) {
        request.source(jsonToString(source));
        return this;
    }

    public IndexRequestBuilder opType(String opType) {
        request.opType(opType);
        return this;
    }

    public IndexRequestBuilder create(boolean create) {
        request.create(create);
        return this;
    }

    public IndexRequestBuilder refresh(boolean refresh) {
        request.refresh(refresh);
        return this;
    }

    public IndexRequestBuilder replicationType(String replicationType) {
        request.replicationType(replicationType);
        return this;
    }

    public IndexRequestBuilder consistencyLevel(String consistencyLevel) {
        request.consistencyLevel(WriteConsistencyLevel.fromString(consistencyLevel));
        return this;
    }

    public IndexRequestBuilder version(long version) {
        request.version(version);
        return this;
    }

    public IndexRequestBuilder versionType(String versionType) {
        request.versionType(VersionType.fromString(versionType));
        return this;
    }

    public IndexRequestBuilder percolate(String percolate) {
        request.percolate(percolate);
        return this;
    }

    public IndexRequestBuilder timestamp(String timestamp) {
        request.timestamp(timestamp);
        return this;
    }

    public IndexRequestBuilder ttl(long ttl) {
        request.ttl(ttl);
        return this;
    }

    public IndexRequestBuilder timeout(String timeout) {
        request.timeout(timeout);
        return this;
    }

    public IndexRequestBuilder index(String index) {
        request.index(index);
        return this;
    }

    @Override
    protected ActionFuture<IndexResponse> doExecute(IndexRequest request) {
        return client.index(request);
    }

    @Override
    protected XContentBuilder toXContent(IndexRequest request, IndexResponse response, XContentBuilder builder) throws IOException {
        builder.startObject()
                .field(Fields.OK, true)
                .field(Fields._INDEX, response.index())
                .field(Fields._TYPE, response.type())
                .field(Fields._ID, response.id())
                .field(Fields._VERSION, response.version());
        if (response.matches() != null) {
            builder.startArray(Fields.MATCHES);
            for (String match : response.matches()) {
                builder.value(match);
            }
            builder.endArray();
        }
        builder.endObject();
        return builder;
    }
}
