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

import java.io.IOException;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

/**
 * @author Luca Cavanna
 *
 * Request builder for index API
 */
@SuppressWarnings("unused")
public class IndexRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<IndexRequest, IndexResponse, JsonInput, JsonOutput> {

    public IndexRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new IndexRequest(), jsonToString, stringToJson);
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> type(String type) {
        request.type(type);
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> id(String id) {
        request.id(id);
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> routing(String routing) {
        request.routing(routing);
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> parent(String parent) {
        request.parent(parent);
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> source(JsonInput source) {
        request.source(jsonToString(source));
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> opType(String opType) {
        request.opType(opType);
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> create(boolean create) {
        request.create(create);
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> refresh(boolean refresh) {
        request.refresh(refresh);
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> replicationType(String replicationType) {
        request.replicationType(replicationType);
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> consistencyLevel(String consistencyLevel) {
        request.consistencyLevel(WriteConsistencyLevel.fromString(consistencyLevel));
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> version(long version) {
        request.version(version);
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> versionType(String versionType) {
        request.versionType(VersionType.fromString(versionType));
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> percolate(String percolate) {
        request.percolate(percolate);
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> timestamp(String timestamp) {
        request.timestamp(timestamp);
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> ttl(long ttl) {
        request.ttl(ttl);
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> timeout(String timeout) {
        request.timeout(timeout);
        return this;
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> index(String index) {
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
                .field(Fields._INDEX, response.getIndex())
                .field(Fields._TYPE, response.getType())
                .field(Fields._ID, response.getId())
                .field(Fields._VERSION, response.getVersion());
        if (response.getMatches() != null) {
            builder.startArray(Fields.MATCHES);
            for (String match : response.getMatches()) {
                builder.value(match);
            }
            builder.endArray();
        }
        builder.endObject();
        return builder;
    }
}
