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
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.executors.AbstractRequestExecutor;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * {@link org.elasticsearch.shell.client.executors.RequestExecutor} implementation for update API
 */
public class UpdateRequestExecutor<JsonInput, JsonOutput> extends AbstractRequestExecutor<UpdateRequest, UpdateResponse, JsonInput, JsonOutput> {

    public UpdateRequestExecutor(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, jsonSerializer);
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
