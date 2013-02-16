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
import org.elasticsearch.action.admin.cluster.node.restart.NodesRestartRequest;
import org.elasticsearch.action.admin.cluster.node.restart.NodesRestartResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for nodes restart API
 */
@SuppressWarnings("unused")
public class NodesRestartRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<NodesRestartRequest, NodesRestartResponse, JsonInput, JsonOutput> {

    public NodesRestartRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new NodesRestartRequest(), jsonToString, stringToJson);
    }

    public NodesRestartRequestBuilder<JsonInput, JsonOutput> delay(String delay) {
        request.delay(delay);
        return this;
    }

    @Override
    protected ActionFuture<NodesRestartResponse> doExecute(NodesRestartRequest request) {
        return client.admin().cluster().nodesRestart(request);
    }

    @Override
    protected XContentBuilder toXContent(NodesRestartRequest request, NodesRestartResponse response, XContentBuilder builder) throws IOException {
        builder.startObject();
        builder.field("cluster_name", response.clusterName().value());
        builder.startObject("nodes");
        for (NodesRestartResponse.NodeRestartResponse nodeInfo : response) {
            builder.startObject(nodeInfo.node().id());
            builder.field("name", nodeInfo.node().name());
            builder.endObject();
        }
        builder.endObject();
        builder.endObject();
        return builder;
    }
}
