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
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.AliasAction;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilder;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for get indices aliases API
 */
@SuppressWarnings("unused")
public class UpdateIndicesAliasesRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilder<IndicesAliasesRequest, IndicesAliasesResponse, JsonInput, JsonOutput> {

    public UpdateIndicesAliasesRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new IndicesAliasesRequest(), jsonSerializer);
    }

    public UpdateIndicesAliasesRequestBuilder addAlias(String index, String alias) {
        request.addAlias(index, alias);
        return this;
    }

    public UpdateIndicesAliasesRequestBuilder addAlias(String index, String alias, JsonInput filter) {
        request.addAlias(index, alias, jsonToString(filter));
        return this;
    }

    public UpdateIndicesAliasesRequestBuilder addAlias(String index, String alias, FilterBuilder filterBuilder) {
        request.addAlias(index, alias, filterBuilder);
        return this;
    }

    public UpdateIndicesAliasesRequestBuilder addAliasAction(AliasAction aliasAction) {
        request.addAliasAction(aliasAction);
        return this;
    }

    public UpdateIndicesAliasesRequestBuilder removeAlias(String index, String alias) {
        request.removeAlias(index, alias);
        return this;
    }

    public UpdateIndicesAliasesRequestBuilder timeout(String timeout) {
        request.timeout(timeout);
        return this;
    }

    @Override
    protected ActionFuture<IndicesAliasesResponse> doExecute(IndicesAliasesRequest request) {
        return client.admin().indices().aliases(request);
    }

    @Override
    protected XContentBuilder toXContent(IndicesAliasesRequest request, IndicesAliasesResponse response, XContentBuilder builder) throws IOException {
        builder.startObject()
                .field(Fields.OK, true)
                .field(Fields.ACKNOWLEDGED, response.acknowledged())
                .endObject();
        return builder;
    }
}
