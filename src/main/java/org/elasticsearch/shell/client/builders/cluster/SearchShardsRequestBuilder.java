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
package org.elasticsearch.shell.client.builders.cluster;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.shards.ClusterSearchShardsRequest;
import org.elasticsearch.action.admin.cluster.shards.ClusterSearchShardsResponse;
import org.elasticsearch.action.support.IgnoreIndices;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import java.io.IOException;

/**
 * @author Luca Cavanna
 * 
 * Request builder for cluster search shards API
 */
public class SearchShardsRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<ClusterSearchShardsRequest, ClusterSearchShardsResponse, JsonInput, JsonOutput> {

    public SearchShardsRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new ClusterSearchShardsRequest(), jsonToString, stringToJson);
    }

    public SearchShardsRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        request.indices(indices);
        return this;
    }

    public SearchShardsRequestBuilder<JsonInput, JsonOutput> types(String... types) {
        request.types(types);
        return this;
    }

    public SearchShardsRequestBuilder<JsonInput, JsonOutput> routing(String... routing) {
        request.routing(routing);
        return this;
    }

    public SearchShardsRequestBuilder<JsonInput, JsonOutput> preference(String preference) {
        request.preference(preference);
        return this;
    }

    public SearchShardsRequestBuilder<JsonInput, JsonOutput> ignoreIndices(String ignoreIndices) {
        request.ignoreIndices(IgnoreIndices.fromString(ignoreIndices));
        return this;
    }

    public SearchShardsRequestBuilder<JsonInput, JsonOutput> local(boolean local) {
        request.local(local);
        return this;
    }
    
    @Override
    protected XContentBuilder toXContent(ClusterSearchShardsRequest request, ClusterSearchShardsResponse response, XContentBuilder builder) throws IOException {
        builder.startObject();
        builder.field(Fields.OK, true);
        response.toXContent(builder, ToXContent.EMPTY_PARAMS);
        builder.endObject();
        return builder;
    }

    @Override
    protected ActionFuture<ClusterSearchShardsResponse> doExecute(ClusterSearchShardsRequest request) {
        return client.admin().cluster().searchShards(request);
    }
}
