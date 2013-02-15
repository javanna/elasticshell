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

import org.apache.lucene.search.Explanation;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.explain.ExplainRequest;
import org.elasticsearch.action.explain.ExplainResponse;
import org.elasticsearch.action.explain.ExplainSourceBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilder;
import org.elasticsearch.shell.json.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for explain API
 */
@SuppressWarnings("unused")
public class ExplainRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilder<ExplainRequest, ExplainResponse, JsonInput, JsonOutput> {

    public ExplainRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new ExplainRequest(null, null, null), jsonSerializer);
    }

    public ExplainRequestBuilder<JsonInput, JsonOutput> index(String index) {
        request.index(index);
        return this;
    }

    public ExplainRequestBuilder<JsonInput, JsonOutput> type(String type) {
        request.type(type);
        return this;
    }

    public ExplainRequestBuilder<JsonInput, JsonOutput> id(String id) {
        request.id(id);
        return this;
    }

    public ExplainRequestBuilder<JsonInput, JsonOutput> routing(String routing) {
        request.routing(routing);
        return this;
    }

    public ExplainRequestBuilder<JsonInput, JsonOutput> parent(String parent) {
        request.parent(parent);
        return this;
    }

    public ExplainRequestBuilder<JsonInput, JsonOutput> preference(String preference) {
        request.preference(preference);
        return this;
    }

    public ExplainRequestBuilder<JsonInput, JsonOutput> query(QueryBuilder query) {
        request.source(new ExplainSourceBuilder().query(query));
        return this;
    }

    public ExplainRequestBuilder<JsonInput, JsonOutput> query(JsonInput query) {
        request.source(new ExplainSourceBuilder().query(new BytesArray(jsonToString(query))));
        return this;
    }

    public ExplainRequestBuilder<JsonInput, JsonOutput> source(JsonInput source) {
        request.source(new BytesArray(jsonToString(source)), false);
        return this;
    }

    public ExplainRequestBuilder<JsonInput, JsonOutput> fields(String... fields) {
        request.fields(fields);
        return this;
    }

    @Override
    protected ActionFuture<ExplainResponse> doExecute(ExplainRequest request) {
        return client.explain(request);
    }

    @Override
    protected XContentBuilder toXContent(ExplainRequest request, ExplainResponse response, XContentBuilder builder) throws IOException {
        builder.startObject();
        builder.field(Fields.OK, response.exists())
                .field(Fields._INDEX, request.index())
                .field(Fields._TYPE, request.type())
                .field(Fields._ID, request.id())
                .field(Fields.MATCHED, response.match());

        if (response.hasExplanation()) {
            builder.startObject(Fields.EXPLANATION);
            buildExplanation(builder, response.explanation());
            builder.endObject();
        }
        GetResult getResult = response.getResult();
        if (getResult != null) {
            builder.startObject(Fields.GET);
            response.getResult().toXContentEmbedded(builder, ToXContent.EMPTY_PARAMS);
            builder.endObject();
        }
        builder.endObject();
        return builder;
    }

    private void buildExplanation(XContentBuilder builder, Explanation explanation) throws IOException {
        builder.field(Fields.VALUE, explanation.getValue());
        builder.field(Fields.DESCRIPTION, explanation.getDescription());
        Explanation[] innerExps = explanation.getDetails();
        if (innerExps != null) {
            builder.startArray(Fields.DETAILS);
            for (Explanation exp : innerExps) {
                builder.startObject();
                buildExplanation(builder, exp);
                builder.endObject();
            }
            builder.endArray();
        }
    }
}
