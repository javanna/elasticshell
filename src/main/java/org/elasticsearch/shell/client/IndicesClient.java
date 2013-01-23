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
package org.elasticsearch.shell.client;

import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheRequest;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.optimize.OptimizeRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.executors.indices.*;

/**
 * @author Luca Cavanna
 *
 * Client that exposes the elasticsearch indices APIs
 * Wraps a {@link org.elasticsearch.client.IndicesAdminClient}
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
public class IndicesClient<JsonInput, JsonOutput> {

    private final JsonSerializer<JsonInput, JsonOutput> jsonSerializer;
    private final Client client;

    public IndicesClient(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        this.client = client;
        this.jsonSerializer = jsonSerializer;
    }

    public JsonOutput clearCache() {
        return clearCache(new String[0]);
    }

    public JsonOutput clearCache(String... indices) {
        return clearCache(Requests.clearIndicesCacheRequest(indices));
    }

    public JsonOutput clearCache(ClearIndicesCacheRequest request) {
        return new ClearCacheRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput closeIndex(String index) {
        return closeIndex(Requests.closeIndexRequest(index));
    }

    public JsonOutput closeIndex(CloseIndexRequest request) {
        return new CloseIndexRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput createIndex(String index, JsonInput source) {
        return createIndex(Requests.createIndexRequest(index).source(jsonToString(source)));
    }

    public JsonOutput createIndex(String index, String source) {
        return createIndex(Requests.createIndexRequest(index).source(source));
    }

    public JsonOutput createIndex(CreateIndexRequest request) {
        return new CreateIndexRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput deleteMapping(String index, String type) {
        return deleteMapping(Requests.deleteMappingRequest(index).type(type));
    }

    public JsonOutput deleteMapping(DeleteMappingRequest request) {
        return new DeleteMappingRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput deleteIndex(String index) {
        return deleteIndex(Requests.deleteIndexRequest(index));
    }

    public JsonOutput deleteIndex(DeleteIndexRequest request) {
        return new DeleteIndexRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput flush(String... indices) {
        return flush(Requests.flushRequest(indices));
    }

    public JsonOutput flush(FlushRequest request) {
        return new FlushRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput getMapping() {
        return getMapping(new String[0]);
    }

    public JsonOutput getMapping(String... indices) {
        ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest()
                .filterRoutingTable(true)
                .filterNodes(true)
                .filteredIndices(indices);
        clusterStateRequest.listenerThreaded(false);
        return new GetMappingRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(clusterStateRequest);
    }

    public JsonOutput getSettings() {
        return getSettings(new String[0]);
    }

    public JsonOutput getSettings(String... indices) {
        ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest()
                .filterRoutingTable(true)
                .filterNodes(true)
                .filteredIndices(indices);
        clusterStateRequest.listenerThreaded(false);
        return new GetSettingsRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(clusterStateRequest);
    }

    public JsonOutput exists(String index, String type) {
        return exists(new TypesExistsRequest(new String[]{index}, type));
    }

    public JsonOutput exists(TypesExistsRequest request) {
        return new TypesExistsRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput exists(String index) {
        return exists(Requests.indicesExistsRequest(index));
    }

    public JsonOutput exists(IndicesExistsRequest request) {
        return new IndicesExistsRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput openIndex(String index) {
        return openIndex(Requests.openIndexRequest(index));
    }

    public JsonOutput openIndex(OpenIndexRequest request) {
        return new OpenIndexRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput optimize() {
        return optimize(new String[0]);
    }

    public JsonOutput optimize(String... indices) {
        return optimize(Requests.optimizeRequest(indices));
    }

    public JsonOutput optimize(OptimizeRequest request) {
        return new OptimizeRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput putMapping(String index, String type, JsonInput source) {
        return putMapping(Requests.putMappingRequest(index).type(type).source(jsonToString(source)));
    }

    public JsonOutput putMapping(String index, String type, String source) {
        return putMapping(Requests.putMappingRequest(index).type(type).source(source));
    }

    public JsonOutput putMapping(String[] index, String type, JsonInput source) {
        return putMapping(Requests.putMappingRequest(index).type(type).source(jsonToString(source)));
    }

    public JsonOutput putMapping(String[] index, String type, String source) {
        return putMapping(Requests.putMappingRequest(index).type(type).source(source));
    }

    public JsonOutput putMapping(PutMappingRequest request) {
        return new PutMappingRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput refresh() {
        return refresh(new String[0]);
    }

    public JsonOutput refresh(String... indices) {
        return refresh(Requests.refreshRequest(indices));
    }

    public JsonOutput refresh(RefreshRequest request) {
        return new RefreshRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput segments() {
        return segments(new String[0]);
    }

    public JsonOutput segments(String... indices) {
        return segments(Requests.indicesSegmentsRequest(indices));
    }

    public JsonOutput segments(IndicesSegmentsRequest request) {
        return new SegmentsRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput stats() {
        return stats(new IndicesStatsRequest());
    }

    public JsonOutput stats(String... indices) {
        return stats(new IndicesStatsRequest().indices(indices));
    }

    public JsonOutput stats(IndicesStatsRequest request) {
        return new StatsRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    public JsonOutput status() {
        return status(new String[0]);
    }

    public JsonOutput status(String... indices) {
        return status(Requests.indicesStatusRequest(indices));
    }

    public JsonOutput status(IndicesStatusRequest request) {
        return new StatusRequestExecutor<JsonInput, JsonOutput>(client, jsonSerializer).execute(request);
    }

    protected String jsonToString(JsonInput source) {
        return jsonSerializer.jsonToString(source, false);
    }
}