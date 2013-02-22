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
import org.elasticsearch.action.admin.indices.stats.IndicesStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import java.io.IOException;

import static org.elasticsearch.rest.action.support.RestActions.buildBroadcastShardsHeader;

/**
 * @author Luca Cavanna
 *
 * Request builder for stats API
 */
@SuppressWarnings("unused")
public class StatsRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<IndicesStatsRequest, IndicesStats, JsonInput, JsonOutput> {

    public StatsRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new IndicesStatsRequest(), jsonToString, stringToJson);
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        this.request.indices(indices);
        return this;
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> all() {
        request.all();
        return this;
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> clear() {
        request.clear();
        return this;
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> types(String... types) {
        request.types(types);
        return this;
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> groups(String... groups) {
        request.groups(groups);
        return this;
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> docs(boolean docs) {
        request.docs(docs);
        return this;
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> store(boolean store) {
        request.store(store);
        return this;
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> indexing(boolean indexing) {
        request.indexing(indexing);
        return this;
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> get(boolean get) {
        request.get(get);
        return this;
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> search(boolean search) {
        request.search(search);
        return this;
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> merge(boolean merge) {
        request.merge(merge);
        return this;
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> refresh(boolean refresh) {
        request.refresh(refresh);
        return this;
    }

    public StatsRequestBuilder<JsonInput, JsonOutput> flush(boolean flush) {
        request.flush(flush);
        return this;
    }

    @Override
    protected ActionFuture<IndicesStats> doExecute(IndicesStatsRequest request) {
        return client.admin().indices().stats(request);
    }

    @Override
    protected XContentBuilder toXContent(IndicesStatsRequest request, IndicesStats response, XContentBuilder builder) throws IOException {
        builder.startObject();
        builder.field(Fields.OK, true);
        buildBroadcastShardsHeader(builder, response);
        response.toXContent(builder, ToXContent.EMPTY_PARAMS);
        builder.endObject();
        return builder;
    }
}
