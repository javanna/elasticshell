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
import org.elasticsearch.action.admin.cluster.reroute.ClusterRerouteRequest;
import org.elasticsearch.action.admin.cluster.reroute.ClusterRerouteResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.routing.allocation.command.AllocationCommand;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for cluster reroute API
 */
@SuppressWarnings("unused")
public class ClusterRerouteRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<ClusterRerouteRequest, ClusterRerouteResponse, JsonInput, JsonOutput> {

    public ClusterRerouteRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new ClusterRerouteRequest(), jsonToString, stringToJson);
    }

    public ClusterRerouteRequestBuilder<JsonInput, JsonOutput> add(AllocationCommand... commands) {
        request.add(commands);
        return this;
    }

    public ClusterRerouteRequestBuilder<JsonInput, JsonOutput> dryRun(boolean dryRun) {
        request.dryRun(dryRun);
        return this;
    }

    public ClusterRerouteRequestBuilder<JsonInput, JsonOutput> source(JsonInput source) throws Exception {
        request.source(new BytesArray(jsonToString(source)));
        return this;
    }

    @Override
    protected ActionFuture<ClusterRerouteResponse> doExecute(ClusterRerouteRequest request) {
        return client.admin().cluster().reroute(request);
    }

    @Override
    protected XContentBuilder toXContent(ClusterRerouteRequest request, ClusterRerouteResponse response, XContentBuilder builder) throws IOException {
        builder.startObject();
        builder.field(Fields.OK, true);
        builder.startObject("state");
        response.getState().settingsFilter(new SettingsFilter(ImmutableSettings.settingsBuilder().build())).toXContent(builder, ToXContent.EMPTY_PARAMS);
        builder.endObject();

        builder.endObject();

        return builder;
    }
}
