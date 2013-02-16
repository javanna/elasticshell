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
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterIndexHealth;
import org.elasticsearch.action.admin.cluster.health.ClusterShardHealth;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for cluster health API
 */
@SuppressWarnings("unused")
public class ClusterHealthRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<ClusterHealthRequest, ClusterHealthResponse, JsonInput, JsonOutput> {

    private String level;

    public ClusterHealthRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new ClusterHealthRequest(), jsonToString, stringToJson);
    }

    public ClusterHealthRequestBuilder<JsonInput, JsonOutput> level(String level) {
        this.level = level;
        return this;
    }

    public ClusterHealthRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        request.indices(indices);
        return this;
    }

    public ClusterHealthRequestBuilder<JsonInput, JsonOutput> timeout(String timeout) {
        request.timeout(timeout);
        return this;
    }

    public ClusterHealthRequestBuilder<JsonInput, JsonOutput> waitForGreenStatus() {
        request.waitForGreenStatus();
        return this;
    }

    public ClusterHealthRequestBuilder<JsonInput, JsonOutput> waitForYellowStatus() {
        request.waitForYellowStatus();
        return this;
    }

    public ClusterHealthRequestBuilder<JsonInput, JsonOutput> waitForRelocatingShards(int waitForRelocatingShards) {
        request.waitForRelocatingShards(waitForRelocatingShards);
        return this;
    }

    public ClusterHealthRequestBuilder<JsonInput, JsonOutput> waitForActiveShards(int waitForActiveShards) {
        request.waitForActiveShards(waitForActiveShards);
        return this;
    }

    public ClusterHealthRequestBuilder<JsonInput, JsonOutput> waitForNodes(String waitForNodes) {
        request.waitForNodes(waitForNodes);
        return this;
    }

    @Override
    protected ActionFuture<ClusterHealthResponse> doExecute(ClusterHealthRequest request) {
        return client.admin().cluster().health(request);
    }

    @Override
    protected XContentBuilder toXContent(ClusterHealthRequest request, ClusterHealthResponse response, XContentBuilder builder) throws IOException {

        builder.startObject();

        builder.field(Fields.CLUSTER_NAME, response.clusterName());
        builder.field(Fields.STATUS, response.status().name().toLowerCase());
        builder.field(Fields.TIMED_OUT, response.timedOut());
        builder.field(Fields.NUMBER_OF_NODES, response.numberOfNodes());
        builder.field(Fields.NUMBER_OF_DATA_NODES, response.numberOfDataNodes());
        builder.field(Fields.ACTIVE_PRIMARY_SHARDS, response.activePrimaryShards());
        builder.field(Fields.ACTIVE_SHARDS, response.activeShards());
        builder.field(Fields.RELOCATING_SHARDS, response.relocatingShards());
        builder.field(Fields.INITIALIZING_SHARDS, response.initializingShards());
        builder.field(Fields.UNASSIGNED_SHARDS, response.unassignedShards());

        int levelAsInt = 0;
        if (level != null) {
            if ("cluster".equals(level)) {
                levelAsInt = 0;
            } else if ("indices".equals(level)) {
                levelAsInt = 1;
            } else if ("shards".equals(level)) {
                levelAsInt = 2;
            }
        }

        if (!response.validationFailures().isEmpty()) {
            builder.startArray(Fields.VALIDATION_FAILURES);
            for (String validationFailure : response.validationFailures()) {
                builder.value(validationFailure);
            }
            // if we don't print index level information, still print the index validation failures
            // so we know why the status is red
            if (levelAsInt == 0) {
                for (ClusterIndexHealth indexHealth : response) {
                    builder.startObject(indexHealth.index());

                    if (!indexHealth.validationFailures().isEmpty()) {
                        builder.startArray(Fields.VALIDATION_FAILURES);
                        for (String validationFailure : indexHealth.validationFailures()) {
                            builder.value(validationFailure);
                        }
                        builder.endArray();
                    }

                    builder.endObject();
                }
            }
            builder.endArray();
        }

        if (levelAsInt > 0) {
            builder.startObject(Fields.INDICES);
            for (ClusterIndexHealth indexHealth : response) {
                builder.startObject(indexHealth.index(), XContentBuilder.FieldCaseConversion.NONE);

                builder.field(Fields.STATUS, indexHealth.status().name().toLowerCase());
                builder.field(Fields.NUMBER_OF_SHARDS, indexHealth.numberOfShards());
                builder.field(Fields.NUMBER_OF_REPLICAS, indexHealth.numberOfReplicas());
                builder.field(Fields.ACTIVE_PRIMARY_SHARDS, indexHealth.activePrimaryShards());
                builder.field(Fields.ACTIVE_SHARDS, indexHealth.activeShards());
                builder.field(Fields.RELOCATING_SHARDS, indexHealth.relocatingShards());
                builder.field(Fields.INITIALIZING_SHARDS, indexHealth.initializingShards());
                builder.field(Fields.UNASSIGNED_SHARDS, indexHealth.unassignedShards());

                if (!indexHealth.validationFailures().isEmpty()) {
                    builder.startArray(Fields.VALIDATION_FAILURES);
                    for (String validationFailure : indexHealth.validationFailures()) {
                        builder.value(validationFailure);
                    }
                    builder.endArray();
                }

                if (levelAsInt > 1) {
                    builder.startObject(Fields.SHARDS);

                    for (ClusterShardHealth shardHealth : indexHealth) {
                        builder.startObject(Integer.toString(shardHealth.id()));

                        builder.field(Fields.STATUS, shardHealth.status().name().toLowerCase());
                        builder.field(Fields.PRIMARY_ACTIVE, shardHealth.primaryActive());
                        builder.field(Fields.ACTIVE_SHARDS, shardHealth.activeShards());
                        builder.field(Fields.RELOCATING_SHARDS, shardHealth.relocatingShards());
                        builder.field(Fields.INITIALIZING_SHARDS, shardHealth.initializingShards());
                        builder.field(Fields.UNASSIGNED_SHARDS, shardHealth.unassignedShards());

                        builder.endObject();
                    }

                    builder.endObject();
                }

                builder.endObject();
            }
            builder.endObject();
        }

        builder.endObject();

        return builder;
    }
}
