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
import java.util.List;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.validate.query.QueryExplanation;
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryRequest;
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import static org.elasticsearch.rest.action.support.RestActions.buildBroadcastShardsHeader;

/**
 * @author Luca Cavanna
 *
 * Request builder for validate query API
 */
@SuppressWarnings("unused")
public class ValidateQueryRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<ValidateQueryRequest, ValidateQueryResponse, JsonInput, JsonOutput> {

    public ValidateQueryRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new ValidateQueryRequest(), jsonToString, stringToJson);
    }

    public ValidateQueryRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        request.indices(indices);
        return this;
    }

    public ValidateQueryRequestBuilder<JsonInput, JsonOutput> types(String... types) {
        request.types(types);
        return this;
    }

    public ValidateQueryRequestBuilder<JsonInput, JsonOutput> queryBuilder(QueryBuilder queryBuilder) {
        request.query(queryBuilder);
        return this;
    }

    public ValidateQueryRequestBuilder<JsonInput, JsonOutput> query(JsonInput query) {
        request.query(jsonToString(query));
        return this;
    }

    public ValidateQueryRequestBuilder<JsonInput, JsonOutput> explain(boolean explain) {
        request.explain(explain);
        return this;
    }

    @Override
    protected ActionFuture<ValidateQueryResponse> doExecute(ValidateQueryRequest request) {
        return client.admin().indices().validateQuery(request);
    }

    @Override
    protected XContentBuilder toXContent(ValidateQueryRequest request, ValidateQueryResponse response, XContentBuilder builder) throws IOException {
        builder.startObject();
        builder.field("valid", response.valid());
        buildBroadcastShardsHeader(builder, response);
        if (response.queryExplanations() != null && !response.queryExplanations().isEmpty()) {
            builder.startArray("explanations");
            for (QueryExplanation explanation : response.queryExplanations()) {
                builder.startObject();
                if (explanation.index() != null) {
                    builder.field("index", explanation.index(), XContentBuilder.FieldCaseConversion.NONE);
                }
                builder.field("valid", explanation.valid());
                if (explanation.error() != null) {
                    builder.field("error", explanation.error());
                }
                if (explanation.explanation() != null) {
                    builder.field("explanation", explanation.explanation());
                }
                builder.endObject();
            }
            builder.endArray();
        }
        builder.endObject();
        return builder;
    }
}
