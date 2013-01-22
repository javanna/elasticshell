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
package org.elasticsearch.shell.client.executors;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.percolate.PercolateRequest;
import org.elasticsearch.action.percolate.PercolateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * {@link RequestExecutor} implementation for percolate API
 */
public class PercolateRequestExecutor<JsonInput, JsonOutput> extends AbstractRequestExecutor<PercolateRequest, PercolateResponse, JsonInput, JsonOutput> {

    public PercolateRequestExecutor(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, jsonSerializer);
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
