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

import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.executors.core.*;
import org.elasticsearch.shell.client.executors.indices.*;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Luca Cavanna
 *
 * Generic elasticsearch client wrapper which exposes common client operations that don't depend
 * on the specific type of client in use (transport or node)
 *
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
@SuppressWarnings("unused")
public abstract class AbstractClient<JsonInput, JsonOutput> implements Closeable {

    private final Client client;
    private final JsonSerializer<JsonInput, JsonOutput> jsonSerializer;

    protected AbstractClient(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        this.client = client;
        this.jsonSerializer = jsonSerializer;
    }

    public Index[] showIndexes() {
        ClusterStateResponse response = client.admin().cluster().prepareState().setLocal(true).setFilterBlocks(true)
                .setFilterRoutingTable(true).setFilterNodes(true).setListenerThreaded(false).execute().actionGet();
        List<Index> indexes = new ArrayList<Index>();
        for (IndexMetaData indexMetaData : response.state().metaData().indices().values()) {
            indexes.add(new Index(indexMetaData.index(), indexMetaData.mappings().keySet(), indexMetaData.aliases().keySet()));
        }
        return indexes.toArray(new Index[indexes.size()]);
    }

    public CountRequestBuilder count() {
        return new CountRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public DeleteRequestBuilder delete() {
        return new DeleteRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public DeleteByQueryRequestBuilder deleteByQuery() {
        return new DeleteByQueryRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public ExplainRequestBuilder explain() {
        return new ExplainRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public GetRequestBuilder get() {
        return new GetRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public IndexRequestBuilder index() {
        return new IndexRequestBuilder<JsonInput,JsonOutput>(client, jsonSerializer);
    }

    public MoreLikeThisRequestBuilder moreLikeThis() {
        return new MoreLikeThisRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public PercolateRequestBuilder percolate() {
        return new PercolateRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public SearchRequestBuilder search() {
        return new SearchRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public UpdateRequestBuilder update() {
        return new UpdateRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public ValidateQueryRequestBuilder validate() {
        return new ValidateQueryRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    /*
    Indices APIs
     */
    public GetAliasesIndicesRequestBuilder getAliases() {
        return new GetAliasesIndicesRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public UpdateIndicesAliasesRequestBuilder updateAliases() {
        return new UpdateIndicesAliasesRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public AnalyzeRequestBuilder analyze() {
        return new AnalyzeRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public ClearCacheRequestBuilder clearCache() {
        return new ClearCacheRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public CloseIndexRequestBuilder closeIndex() {
        return new CloseIndexRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public CreateIndexRequestBuilder createIndex() {
        return new CreateIndexRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public DeleteIndexRequestBuilder deleteIndex() {
        return new DeleteIndexRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public DeleteMappingRequestBuilder deleteMapping() {
        return new DeleteMappingRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public FlushRequestBuilder flush() {
        return new FlushRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public GetMappingRequestBuilder getMapping() {
        return new GetMappingRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public GetSettingsRequestBuilder getSettings() {
        return new GetSettingsRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public IndicesExistsRequestBuilder indicesExists() {
        return new IndicesExistsRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public OpenIndexRequestBuilder openIndex() {
        return new OpenIndexRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public OptimizeRequestBuilder optimize() {
        return new OptimizeRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public PutMappingRequestBuilder putMapping() {
        return new PutMappingRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public RefreshRequestBuilder refresh() {
        return new RefreshRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public SegmentsRequestBuilder segments() {
        return new SegmentsRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public StatsRequestBuilder stats() {
        return new StatsRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public StatusRequestBuilder status() {
        return new StatusRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public GetIndexTemplateRequestBuilder getTemplate() {
        return new GetIndexTemplateRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public DeleteIndexTemplateRequestBuilder deleteTemplate() {
        return new DeleteIndexTemplateRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public PutIndexTemplateRequestBuilder putTemplate() {
        return new PutIndexTemplateRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public TypesExistsRequestBuilder typesExists() {
        return new TypesExistsRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public UpdateSettingsRequestBuilder updateSettings() {
        return new UpdateSettingsRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public GetWarmerRequestBuilder getWarmer() {
        return new GetWarmerRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public PutWarmerRequestBuilder putWarmer() {
        return new PutWarmerRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    public DeleteWarmerRequestBuilder deleteWarmer() {
        return new DeleteWarmerRequestBuilder<JsonInput, JsonOutput>(client, jsonSerializer);
    }

    /*
    Cluster APIs
     */



    JsonSerializer<JsonInput, JsonOutput> jsonSerializer() {
        return jsonSerializer;
    }

    Client client() {
        return client;
    }

    protected String jsonToString(JsonInput source) {
        return jsonSerializer.jsonToString(source, false);
    }

    @Override
    public String toString() {
        return asString();
    }

    /**
     * Returns the string representation of the object
     * Handy abstract method to avoid forgetting to implement toString
     * The string representation is important here since shown on the console
     * @return the string representation of the client
     */
    protected abstract String asString();
}