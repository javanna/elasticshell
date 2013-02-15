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
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderToXContent;
import org.elasticsearch.shell.json.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for nodes stats API
 */
@SuppressWarnings("unused")
public class NodesStatsRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderToXContent<NodesStatsRequest, NodesStatsResponse, JsonInput, JsonOutput> {

    public NodesStatsRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new NodesStatsRequest(), jsonSerializer);
    }

    public NodesStatsRequestBuilder<JsonInput, JsonOutput> clear() {
        request.clear();
        return this;
    }

    public NodesStatsRequestBuilder<JsonInput, JsonOutput> all() {
        request.all();
        return this;
    }

    public NodesStatsRequestBuilder indices(boolean indices) {
        request.indices(indices);
        return this;
    }

    public NodesStatsRequestBuilder<JsonInput, JsonOutput> os(boolean os) {
        request.os(os);
        return this;
    }

    public NodesStatsRequestBuilder<JsonInput, JsonOutput> process(boolean process) {
        request.process(process);
        return this;
    }

    public NodesStatsRequestBuilder<JsonInput, JsonOutput> jvm(boolean jvm) {
        request.jvm(jvm);
        return this;
    }

    public NodesStatsRequestBuilder<JsonInput, JsonOutput> threadPool(boolean threadPool) {
        request.threadPool(threadPool);
        return this;
    }

    public NodesStatsRequestBuilder<JsonInput, JsonOutput> network(boolean network) {
        request.network(network);
        return this;
    }

    public NodesStatsRequestBuilder fs(boolean fs) {
        request.fs(fs);
        return this;
    }

    public NodesStatsRequestBuilder<JsonInput, JsonOutput> transport(boolean transport) {
        request.transport(transport);
        return this;
    }

    public NodesStatsRequestBuilder<JsonInput, JsonOutput> http(boolean http) {
        request.http(http);
        return this;
    }

    @Override
    protected ActionFuture<NodesStatsResponse> doExecute(NodesStatsRequest request) {
        return client.admin().cluster().nodesStats(request);
    }

    @Override
    protected XContentBuilder toXContent(NodesStatsRequest request, NodesStatsResponse response, XContentBuilder builder) throws IOException {
        return super.toXContent(request, response, builder.startObject()).endObject();
    }
}
