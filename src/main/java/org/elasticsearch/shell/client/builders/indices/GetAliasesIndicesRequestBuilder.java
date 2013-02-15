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
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilder;
import org.elasticsearch.shell.json.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for (get) aliases indices API
 */
@SuppressWarnings("unused")
public class GetAliasesIndicesRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilder<ClusterStateRequest, ClusterStateResponse, JsonInput, JsonOutput> {

    public GetAliasesIndicesRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new ClusterStateRequest(), jsonSerializer);
        this.request.filterRoutingTable(true).filterNodes(true).listenerThreaded(false);
    }

    public GetAliasesIndicesRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        this.request.filteredIndices(indices);
        return this;
    }

    @Override
    protected ActionFuture<ClusterStateResponse> doExecute(ClusterStateRequest request) {
        return client.admin().cluster().state(request);
    }

    @Override
    protected XContentBuilder toXContent(ClusterStateRequest request, ClusterStateResponse response, XContentBuilder builder) throws IOException {
        MetaData metaData = response.state().metaData();
        builder.startObject();
        for (IndexMetaData indexMetaData : metaData) {
            builder.startObject(indexMetaData.index(), XContentBuilder.FieldCaseConversion.NONE);
            builder.startObject("aliases");
            for (AliasMetaData alias : indexMetaData.aliases().values()) {
                AliasMetaData.Builder.toXContent(alias, builder, ToXContent.EMPTY_PARAMS);
            }
            builder.endObject();
            builder.endObject();
        }
        builder.endObject();
        return builder;
    }
}
