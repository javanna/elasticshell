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
import org.elasticsearch.action.admin.cluster.node.hotthreads.NodeHotThreads;
import org.elasticsearch.action.admin.cluster.node.hotthreads.NodesHotThreadsRequest;
import org.elasticsearch.action.admin.cluster.node.hotthreads.NodesHotThreadsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilder;
import org.elasticsearch.shell.json.JsonToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Cavanna
 *
 * Request builder for nodes hot threads API
 */
@SuppressWarnings("unused")
public class NodesHotThreadsRequestBuilder<JsonInput> extends AbstractRequestBuilder<NodesHotThreadsRequest, NodesHotThreadsResponse, JsonInput, String> {

    private static final Logger logger = LoggerFactory.getLogger(NodesHotThreadsRequestBuilder.class);

    public NodesHotThreadsRequestBuilder(Client client, JsonToString<JsonInput> jsonToString) {
        super(client, new NodesHotThreadsRequest(), jsonToString);
    }

    public NodesHotThreadsRequestBuilder<JsonInput> threads(int threads) {
        request.threads(threads);
        return this;
    }

    public NodesHotThreadsRequestBuilder<JsonInput> type(String type) {
        request.type(type);
        return this;
    }

    public NodesHotThreadsRequestBuilder<JsonInput> interval(TimeValue interval) {
        request.interval(interval);
        return this;
    }

    @Override
    protected ActionFuture<NodesHotThreadsResponse> doExecute(NodesHotThreadsRequest request) {
        return client.admin().cluster().nodesHotThreads(request);
    }

    @Override
    protected String responseToOutput(NodesHotThreadsRequest request, NodesHotThreadsResponse response) {
        StringBuilder sb = new StringBuilder();
        try {
            for (NodeHotThreads node : response) {
                sb.append("::: ").append(node.node().toString()).append("\n");
                Strings.spaceify(3, node.hotThreads(), sb);
                sb.append('\n');
            }
        } catch(Exception e) {
            logger.error("Error while generating the nodes hot thread output", e);
        }
        return sb.toString();
    }

}
