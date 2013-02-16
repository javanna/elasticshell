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
import org.elasticsearch.action.percolate.PercolateRequest;
import org.elasticsearch.action.percolate.PercolateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for percolate API
 */
@SuppressWarnings("unused")
public class PercolateRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<PercolateRequest, PercolateResponse, JsonInput, JsonOutput> {

    public PercolateRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new PercolateRequest(), jsonSerializer);
    }

    public PercolateRequestBuilder<JsonInput, JsonOutput> index(String index) {
        request.index(index);
        return this;
    }

    public PercolateRequestBuilder<JsonInput, JsonOutput> type(String type) {
        request.type(type);
        return this;
    }

    public PercolateRequestBuilder<JsonInput, JsonOutput> source(JsonInput source) {
        request.source(jsonToString(source));
        return this;
    }

    public PercolateRequestBuilder<JsonInput, JsonOutput> preferLocal(boolean preferLocal) {
        request.preferLocal(preferLocal);
        return this;
    }

    @Override
    protected ActionFuture<PercolateResponse> doExecute(PercolateRequest request) {
        return client.percolate(request);
    }

    @Override
    protected XContentBuilder toXContent(PercolateRequest request, PercolateResponse response, XContentBuilder builder) throws IOException {
        builder.startObject();
        builder.field(Fields.OK, true);
        builder.startArray(Fields.MATCHES);
        for (String match : response) {
            builder.value(match);
        }
        builder.endArray();
        builder.endObject();
        return builder;
    }
}
