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
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for nodes info API
 */
@SuppressWarnings("unused")
public class NodesInfoRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<NodesInfoRequest, NodesInfoResponse, JsonInput, JsonOutput> {

    public NodesInfoRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new NodesInfoRequest(), jsonSerializer);
    }

    public NodesInfoRequestBuilder<JsonInput, JsonOutput> clear() {
        request.clear();
        return this;
    }

    public NodesInfoRequestBuilder<JsonInput, JsonOutput> all() {
        request.all();
        return this;
    }

    public NodesInfoRequestBuilder<JsonInput, JsonOutput> settings(boolean settings) {
        request.settings(settings);
        return this;
    }

    public NodesInfoRequestBuilder<JsonInput, JsonOutput> os(boolean os) {
        request.os(os);
        return this;
    }

    public NodesInfoRequestBuilder<JsonInput, JsonOutput> process(boolean process) {
        request.process(process);
        return this;
    }

    public NodesInfoRequestBuilder<JsonInput, JsonOutput> jvm(boolean jvm) {
        request.jvm(jvm);
        return this;
    }

    public NodesInfoRequestBuilder<JsonInput, JsonOutput> threadPool(boolean threadPool) {
        request.threadPool(threadPool);
        return this;
    }

    public NodesInfoRequestBuilder<JsonInput, JsonOutput> network(boolean network) {
        request.network(network);
        return this;
    }

    public NodesInfoRequestBuilder<JsonInput, JsonOutput> transport(boolean transport) {
        request.transport(transport);
        return this;
    }

    public NodesInfoRequestBuilder<JsonInput, JsonOutput> http(boolean http) {
        request.http(http);
        return this;
    }

    @Override
    protected ActionFuture<NodesInfoResponse> doExecute(NodesInfoRequest request) {
        return client.admin().cluster().nodesInfo(request);
    }

    @Override
    protected XContentBuilder toXContent(NodesInfoRequest request, NodesInfoResponse response, XContentBuilder builder) throws IOException {
        response.settingsFilter(new SettingsFilter(ImmutableSettings.settingsBuilder().build()));
        builder.startObject();
        builder.field(Fields.OK, true);
        response.toXContent(builder, ToXContent.EMPTY_PARAMS);
        builder.endObject();
        return builder;
    }
}
