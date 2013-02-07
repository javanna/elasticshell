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
import org.elasticsearch.cluster.metadata.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.compress.CompressedString;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.executors.AbstractRequestBuilder;

import java.io.IOException;
import java.util.Map;

/**
 * @author Luca Cavanna
 *
 * Request builder for (get) index template API
 */
@SuppressWarnings("unused")
public class GetIndexTemplateRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilder<ClusterStateRequest, ClusterStateResponse, JsonInput, JsonOutput> {

    public GetIndexTemplateRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new ClusterStateRequest(), jsonSerializer);
        this.request.filterRoutingTable(true).filterNodes(true)
                .filteredIndices("_na").listenerThreaded(false);
    }

    public GetIndexTemplateRequestBuilder names(String... names) {
        this.request.filteredIndexTemplates(names);
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

        for (IndexTemplateMetaData indexMetaData : metaData.templates().values()) {
            builder.startObject(indexMetaData.name(), XContentBuilder.FieldCaseConversion.NONE);

            builder.field("template", indexMetaData.template());
            builder.field("order", indexMetaData.order());

            builder.startObject("settings");
            for (Map.Entry<String, String> entry : indexMetaData.settings().getAsMap().entrySet()) {
                builder.field(entry.getKey(), entry.getValue());
            }
            builder.endObject();

            builder.startObject("mappings");
            for (Map.Entry<String, CompressedString> entry : indexMetaData.mappings().entrySet()) {
                byte[] mappingSource = entry.getValue().uncompressed();
                XContentParser parser = XContentFactory.xContent(mappingSource).createParser(mappingSource);
                Map<String, Object> mapping = parser.map();
                if (mapping.size() == 1 && mapping.containsKey(entry.getKey())) {
                    // the type name is the root value, reduce it
                    mapping = (Map<String, Object>) mapping.get(entry.getKey());
                }
                builder.field(entry.getKey());
                builder.map(mapping);
            }
            builder.endObject();

            builder.endObject();
        }

        builder.endObject();
        return builder;
    }
}
