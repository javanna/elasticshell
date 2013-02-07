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
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequest;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilder;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for (delete) index template API
 */
@SuppressWarnings("unused")
public class DeleteIndexTemplateRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilder<DeleteIndexTemplateRequest, DeleteIndexTemplateResponse, JsonInput, JsonOutput> {

    public DeleteIndexTemplateRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new DeleteIndexTemplateRequest(""), jsonSerializer);
    }

    public DeleteIndexTemplateRequestBuilder name(String name) {
        //need to create a new because it's the only way to set the name (private field without setter)
        this.request = new DeleteIndexTemplateRequest(name);
        return this;
    }

    public DeleteIndexTemplateRequestBuilder timeout(String timeout) {
        request.timeout(timeout);
        return this;
    }

    @Override
    protected ActionFuture<DeleteIndexTemplateResponse> doExecute(DeleteIndexTemplateRequest request) {
        return client.admin().indices().deleteTemplate(request);
    }

    @Override
    protected XContentBuilder toXContent(DeleteIndexTemplateRequest request, DeleteIndexTemplateResponse response, XContentBuilder builder) throws IOException {
        builder.startObject()
                .field(Fields.OK, true)
                .field(Fields.ACKNOWLEDGED, response.acknowledged())
                .endObject();
        return builder;
    }
}
