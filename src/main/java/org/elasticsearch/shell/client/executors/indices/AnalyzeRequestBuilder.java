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
package org.elasticsearch.shell.client.executors.indices;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.executors.AbstractRequestBuilderToXContent;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for analyze API
 */
@SuppressWarnings("unused")
public class AnalyzeRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderToXContent<AnalyzeRequest, AnalyzeResponse, JsonInput, JsonOutput> {

    public AnalyzeRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new AnalyzeRequest(null), jsonSerializer);
    }

    public AnalyzeRequestBuilder text(String text) {
        request = new AnalyzeRequest(text);
        return this;
    }

    public AnalyzeRequestBuilder index(String index) {
        request.index(index);
        return this;
    }

    public AnalyzeRequestBuilder analyzer(String analyzer) {
        request.analyzer(analyzer);
        return this;
    }

    public AnalyzeRequestBuilder field(String field) {
        request.field(field);
        return this;
    }

    public AnalyzeRequestBuilder tokenizer(String tokenizer) {
        request.tokenizer(tokenizer);
        return this;
    }

    public AnalyzeRequestBuilder tokenFilters(String... tokenFilters) {
        request.tokenFilters(tokenFilters);
        return this;
    }

    @Override
    protected ActionFuture<AnalyzeResponse> doExecute(AnalyzeRequest request) {
        return client.admin().indices().analyze(request);
    }

    @Override
    protected XContentBuilder initContentBuilder() throws IOException {
        return super.initContentBuilder().startObject();
    }

    @Override
    protected XContentBuilder toXContent(AnalyzeRequest request, AnalyzeResponse response, XContentBuilder builder) throws IOException {
        return super.toXContent(request, response, builder).endObject();
    }
}
