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

import org.apache.lucene.search.Explanation;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.explain.ExplainRequest;
import org.elasticsearch.action.explain.ExplainResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.shell.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * {@link RequestExecutor} implementation for explain API
 */
public class ExplainRequestExecutor<JsonInput, JsonOutput> extends AbstractRequestExecutor<ExplainRequest, ExplainResponse, JsonInput, JsonOutput> {

    public ExplainRequestExecutor(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, jsonSerializer);
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
