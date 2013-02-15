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
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilder;
import org.elasticsearch.shell.json.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for cluster state API
 */
@SuppressWarnings("unused")
public class ClusterStateRequestBuilder<JsonInput,JsonOutput> extends AbstractRequestBuilder<ClusterStateRequest, ClusterStateResponse, JsonInput, JsonOutput> {

    public ClusterStateRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new ClusterStateRequest(), jsonSerializer);
    }

    public ClusterStateRequestBuilder<JsonInput,JsonOutput> filterAll() {
        request.filterAll();
        return this;
    }

    public ClusterStateRequestBuilder<JsonInput,JsonOutput> filterBlocks(boolean filter) {
        request.filterBlocks(filter);
        return this;
    }

    public ClusterStateRequestBuilder<JsonInput,JsonOutput> filterMetaData(boolean filter) {
        request.filterMetaData(filter);
        return this;
    }

    public ClusterStateRequestBuilder<JsonInput,JsonOutput> filterNodes(boolean filter) {
        request.filterNodes(filter);
        return this;
    }

    public ClusterStateRequestBuilder<JsonInput,JsonOutput> filterRoutingTable(boolean filter) {
        request.filterRoutingTable(filter);
        return this;
    }

    public ClusterStateRequestBuilder<JsonInput,JsonOutput> filterIndices(String... indices) {
        request.filteredIndices(indices);
        return this;
    }

    public ClusterStateRequestBuilder<JsonInput,JsonOutput> filterIndexTemplates(String... templates) {
        request.filteredIndexTemplates(templates);
        return this;
    }

    public ClusterStateRequestBuilder<JsonInput,JsonOutput> local(boolean local) {
        request.local(local);
        return this;
    }

    @Override
    protected ActionFuture<ClusterStateResponse> doExecute(ClusterStateRequest request) {
        return client.admin().cluster().state(request);
    }

    @Override
    protected XContentBuilder toXContent(ClusterStateRequest request, ClusterStateResponse response, XContentBuilder builder) throws IOException {
        builder.startObject();
        builder.field(Fields.CLUSTER_NAME, response.clusterName().value());
        response.state().settingsFilter(new SettingsFilter(ImmutableSettings.settingsBuilder().build())).toXContent(builder, ToXContent.EMPTY_PARAMS);
        builder.endObject();
        return builder;
    }
}
