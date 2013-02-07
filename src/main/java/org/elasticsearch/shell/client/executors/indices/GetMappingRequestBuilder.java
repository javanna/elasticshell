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
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.collect.ImmutableSet;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.executors.AbstractRequestBuilder;

import java.io.IOException;
import java.util.Set;

/**
 * @author Luca Cavanna
 *
 * Request builder for get mapping API
 */
@SuppressWarnings("unused")
public class GetMappingRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilder<ClusterStateRequest, ClusterStateResponse, JsonInput, JsonOutput> {

    private String[] types;

    public GetMappingRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new ClusterStateRequest(), jsonSerializer);
        this.request.filterRoutingTable(true).filterNodes(true).listenerThreaded(false);
    }

    public GetMappingRequestBuilder indices(String... indices) {
        this.request.filteredIndices(indices);
        return this;
    }

    public GetMappingRequestBuilder types(String... types) {
        this.types = types;
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

        if (metaData.indices().isEmpty()) {
            return builder.endObject();
        }

        Set<String> typesSet;
        if (types != null) {
            typesSet = ImmutableSet.copyOf(types);
        } else {
            typesSet = ImmutableSet.of();
        }

        if (request.filteredIndices() != null && request.filteredIndices().length == 1 && typesSet.size() == 1) {
            boolean foundType = false;
            IndexMetaData indexMetaData = metaData.iterator().next();
            for (MappingMetaData mappingMd : indexMetaData.mappings().values()) {
                if (!typesSet.isEmpty() && !typesSet.contains(mappingMd.type())) {
                    // filter this type out...
                    continue;
                }
                foundType = true;
                builder.field(mappingMd.type());
                builder.map(mappingMd.sourceAsMap());
            }
            if (!foundType) {
                return builder.endObject();
            }
        } else {
            for (IndexMetaData indexMetaData : metaData) {
                builder.startObject(indexMetaData.index(), XContentBuilder.FieldCaseConversion.NONE);

                for (MappingMetaData mappingMd : indexMetaData.mappings().values()) {
                    if (!typesSet.isEmpty() && !typesSet.contains(mappingMd.type())) {
                        // filter this type out...
                        continue;
                    }
                    builder.field(mappingMd.type());
                    builder.map(mappingMd.sourceAsMap());
                }

                builder.endObject();
            }
        }

        return builder.endObject();
    }
}
