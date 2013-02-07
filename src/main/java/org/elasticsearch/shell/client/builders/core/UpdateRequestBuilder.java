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
import org.elasticsearch.action.support.replication.ReplicationType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilder;

import java.io.IOException;
import java.util.Map;

/**
 * @author Luca Cavanna
 *
 * Request builder for update API
 */
@SuppressWarnings("unused")
public class UpdateRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilder<UpdateRequest, UpdateResponse, JsonInput, JsonOutput> {

    public UpdateRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new UpdateRequest(null, null, null), jsonSerializer);
    }

    public UpdateRequestBuilder index(String index) {
        request.index(index);
        return this;
    }

    public UpdateRequestBuilder timeout(String timeout) {
        request.timeout(timeout);
        return this;
    }

    public UpdateRequestBuilder type(String type) {
        request.type(type);
        return this;
    }

    public UpdateRequestBuilder id(String id) {
        request.id(id);
        return this;
    }

    public UpdateRequestBuilder routing(String routing) {
        request.routing(routing);
        return this;
    }

    public UpdateRequestBuilder parent(String parent) {
        request.parent(parent);
        return this;
    }

    public UpdateRequestBuilder script(String script) {
        request.script(script);
        return this;
    }

    public UpdateRequestBuilder scriptLang(String scriptLang) {
        request.scriptLang(scriptLang);
        return this;
    }

    public UpdateRequestBuilder scriptParams(Map<String, Object> scriptParams) {
        request.scriptParams(scriptParams);
        return this;
    }

    public UpdateRequestBuilder addScriptParam(String name, Object value) {
        request.addScriptParam(name, value);
        return this;
    }

    public UpdateRequestBuilder fields(String... fields) {
        request.fields(fields);
        return this;
    }

    public UpdateRequestBuilder retryOnConflict(int retryOnConflict) {
        request.retryOnConflict(retryOnConflict);
        return this;
    }

    public UpdateRequestBuilder refresh(boolean refresh) {
        request.refresh(refresh);
        return this;
    }

    public UpdateRequestBuilder replicationType(String replicationType) {
        request.replicationType(ReplicationType.fromString(replicationType));
        return this;
    }

    public UpdateRequestBuilder consistencyLevel(String consistencyLevel) {
        request.consistencyLevel(WriteConsistencyLevel.fromString(consistencyLevel));
        return this;
    }

    public UpdateRequestBuilder percolate(String percolate) {
        request.percolate(percolate);
        return this;
    }

    public UpdateRequestBuilder doc(IndexRequest indexRequest) {
        request.doc(indexRequest);
        return this;
    }

    public UpdateRequestBuilder doc(String source) {
        request.doc(source);
        return this;
    }

    public UpdateRequestBuilder doc(JsonInput source) {
        request.doc(jsonToString(source));
        return this;
    }

    public UpdateRequestBuilder upsert(IndexRequest indexRequest) {
        request.upsert(indexRequest);
        return this;
    }

    public UpdateRequestBuilder upsert(String source) {
        request.upsert(source);
        return this;
    }

    public UpdateRequestBuilder upsert(JsonInput source) {
        request.upsert(jsonToString(source));
        return this;
    }

    public UpdateRequestBuilder source(String source) throws Exception {
        request.source(new BytesArray(source));
        return this;
    }

    public UpdateRequestBuilder source(JsonInput source) throws Exception {
        request.source(new BytesArray(jsonToString(source)));
        return this;
    }

    @Override
    protected ActionFuture<UpdateResponse> doExecute(UpdateRequest request) {
        return client.update(request);
    }

    @Override
    protected XContentBuilder toXContent(UpdateRequest request, UpdateResponse response, XContentBuilder builder) throws IOException {
        builder.startObject()
                .field(Fields.OK, true)
                .field(Fields._INDEX, response.index())
                .field(Fields._TYPE, response.type())
                .field(Fields._ID, response.id())
                .field(Fields._VERSION, response.version());

        if (response.getResult() != null) {
            builder.startObject(Fields.GET);
            response.getResult().toXContentEmbedded(builder, ToXContent.EMPTY_PARAMS);
            builder.endObject();
        }

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
