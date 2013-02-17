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

import org.elasticsearch.shell.client.builders.cluster.ClusterHealthRequestBuilder;
import org.elasticsearch.shell.client.builders.cluster.ClusterStateRequestBuilder;
import org.elasticsearch.shell.client.builders.core.*;
import org.elasticsearch.shell.client.builders.indices.*;

/**
 * @author Luca Cavanna
 *
 * Internal shell client that exposes operations available on a single index
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
@SuppressWarnings("unused")
public class InternalIndexClient<JsonInput, JsonOutput> {

    private final AbstractClient<JsonInput, JsonOutput> shellClient;
    private final String indexName;
    private final boolean alias;

    public InternalIndexClient(AbstractClient<JsonInput, JsonOutput> shellClient, String indexName, boolean alias) {
        this.shellClient = shellClient;
        this.indexName = indexName;
        this.alias = alias;
    }

    String indexName() {
        return indexName;
    }

    /*
    Core Apis
     */
    public CountRequestBuilder<JsonInput, JsonOutput> countBuilder() {
        return shellClient.countBuilder().indices(indexName);
    }

    public JsonOutput count() {
        return countBuilder().execute();
    }

    public JsonOutput count(JsonInput query) {
        return countBuilder().query(query).execute();
    }

    public DeleteRequestBuilder<JsonInput, JsonOutput> deleteBuilder() {
        return shellClient.deleteBuilder().index(indexName);
    }

    public JsonOutput delete(String type, String id) {
        return deleteBuilder().type(type).id(id).execute();
    }

    public DeleteByQueryRequestBuilder<JsonInput, JsonOutput> deleteByQueryBuilder() {
        return shellClient.deleteByQueryBuilder().indices(indexName);
    }

    public JsonOutput deleteByQuery(JsonInput query) {
        return deleteByQueryBuilder().query(query).execute();
    }

    public ExplainRequestBuilder<JsonInput, JsonOutput> explainBuilder() {
        return shellClient.explainBuilder().index(indexName);
    }

    public JsonOutput explain(String type, String id, JsonInput source) {
        return explainBuilder().type(type).id(id).source(source).execute();
    }

    public GetRequestBuilder<JsonInput, JsonOutput> getBuilder() {
        return shellClient.getBuilder().index(indexName);
    }

    public JsonOutput get(String type, String id) {
        return getBuilder().type(type).id(id).execute();
    }

    public IndexRequestBuilder<JsonInput, JsonOutput> indexBuilder() {
        return shellClient.indexBuilder().index(indexName);
    }

    public JsonOutput index(String type, String id, JsonInput source) {
        return indexBuilder().type(type).id(id).source(source).execute();
    }

    public JsonOutput multiGet(String type, String... ids) {
        return shellClient.multiGetBuilder().add(indexName, type, ids).execute();
    }

    public JsonOutput multiSearch(JsonInput... sources) {
        return shellClient.multiSearchBuilder().add(indexName, sources).execute();
    }

    public MoreLikeThisRequestBuilder<JsonInput, JsonOutput> moreLikeThisBuilder() {
        return shellClient.moreLikeThisBuilder(indexName);
    }

    public JsonOutput moreLikeThis(String type, String id) {
        return moreLikeThisBuilder().type(type).id(id).execute();
    }

    public PercolateRequestBuilder<JsonInput, JsonOutput> percolateBuilder() {
        return shellClient.percolateBuilder().index(indexName);
    }

    public JsonOutput percolate(String type, JsonInput source) {
        return percolateBuilder().type(type).source(source).execute();
    }

    public SearchRequestBuilder<JsonInput, JsonOutput> searchBuilder() {
        return shellClient.searchBuilder().indices(indexName);
    }

    public JsonOutput search() {
        return searchBuilder().execute();
    }

    public JsonOutput search(JsonInput source) {
        return searchBuilder().source(source).execute();
    }

    public UpdateRequestBuilder<JsonInput, JsonOutput> updateBuilder() {
        return shellClient.updateBuilder().index(indexName);
    }

    public JsonOutput update(String type, String id, JsonInput doc) {
        return updateBuilder().type(type).id(id).doc(doc).execute();
    }

    public JsonOutput update(String type, String id, String script) {
        return updateBuilder().type(type).id(id).script(script).execute();
    }

    public ValidateQueryRequestBuilder<JsonInput, JsonOutput> validateBuilder() {
        return shellClient.validateBuilder().indices(indexName);
    }

    public JsonOutput validate(JsonInput query) {
        return validateBuilder().query(query).execute();
    }

    /*
    Indices APIs that make sense for a specific index
     */
    public GetAliasesIndicesRequestBuilder<JsonInput, JsonOutput> aliasesGetBuilder() {
        return shellClient.aliasesGetBuilder().indices(indexName);
    }

    public JsonOutput aliasesGet() {
        return aliasesGetBuilder().execute();
    }

    public AnalyzeRequestBuilder<JsonInput, JsonOutput> analyzeBuilder() {
        return shellClient.analyzeBuilder().index(indexName);
    }

    public ClearCacheRequestBuilder<JsonInput, JsonOutput> clearCacheBuilder() {
        return shellClient.clearCacheBuilder().indices(indexName);
    }

    public JsonOutput clearCache() {
        return clearCacheBuilder().execute();
    }

    public CloseIndexRequestBuilder<JsonInput, JsonOutput> closeIndexBuilder() {
        return shellClient.closeIndexBuilder().index(indexName);
    }

    public JsonOutput closeIndex() {
        return closeIndexBuilder().execute();
    }

    public DeleteIndexRequestBuilder<JsonInput, JsonOutput> deleteIndexBuilder() {
        return shellClient.deleteIndexBuilder().indices(indexName);
    }

    public JsonOutput deleteIndex() {
        return deleteIndexBuilder().execute();
    }

    public FlushRequestBuilder<JsonInput, JsonOutput> flushBuilder() {
        return shellClient.flushBuilder().indices(indexName);
    }

    public JsonOutput flush() {
        return flushBuilder().execute();
    }

    public GetMappingRequestBuilder<JsonInput, JsonOutput> mappingGetBuilder() {
        return shellClient.mappingGetBuilder().indices(indexName);
    }

    public JsonOutput mappingGet() {
        return mappingGetBuilder().execute();
    }

    public DeleteMappingRequestBuilder<JsonInput, JsonOutput> mappingDeleteBuilder() {
        return shellClient.mappingDeleteBuilder().indices(indexName);
    }

    public JsonOutput mappingDelete(String type) {
        return mappingDeleteBuilder().type(type).execute();
    }

    public PutMappingRequestBuilder<JsonInput, JsonOutput> mappingPutBuilder() {
        return shellClient.mappingPutBuilder().indices(indexName);
    }

    public JsonOutput mappingPut(String type, JsonInput source) {
        return mappingPutBuilder().type(type).source(source).execute();
    }

    public OpenIndexRequestBuilder<JsonInput, JsonOutput> openIndexBuilder() {
        return shellClient.openIndexBuilder().index(indexName);
    }

    public JsonOutput openIndex() {
        return openIndexBuilder().execute();
    }

    public OptimizeRequestBuilder<JsonInput, JsonOutput> optimizeBuilder() {
        return shellClient.optimizeBuilder().indices(indexName);
    }

    public JsonOutput optimize() {
        return optimizeBuilder().execute();
    }

    public RefreshRequestBuilder<JsonInput, JsonOutput> refreshBuilder() {
        return shellClient.refreshBuilder().indices(indexName);
    }

    public JsonOutput refresh() {
        return refreshBuilder().execute();
    }

    public SegmentsRequestBuilder<JsonInput, JsonOutput> segmentsBuilder() {
        return shellClient.segmentsBuilder().indices(indexName);
    }

    public JsonOutput segments() {
        return segmentsBuilder().execute();
    }

    public GetSettingsRequestBuilder<JsonInput, JsonOutput> settingsGetBuilder() {
        return shellClient.settingsGetBuilder().indices(indexName);
    }

    public JsonOutput settingsGet() {
        return settingsGetBuilder().execute();
    }

    public UpdateSettingsRequestBuilder<JsonInput, JsonOutput> settingsUpdateBuilder() {
        return shellClient.settingsUpdateBuilder().indices(indexName);
    }

    public JsonOutput settingsUpdate(JsonInput source) {
        return settingsUpdateBuilder().settings(source).execute();
    }


    public StatsRequestBuilder<JsonInput, JsonOutput> statsBuilder() {
        return shellClient.statsBuilder().indices(indexName);
    }

    public JsonOutput stats() {
        return statsBuilder().execute();
    }

    public StatusRequestBuilder<JsonInput, JsonOutput> statusBuilder() {
        return shellClient.statusBuilder().indices(indexName);
    }

    public JsonOutput status() {
        return statusBuilder().execute();
    }

    public TypesExistsRequestBuilder<JsonInput, JsonOutput> typesExistsBuilder() {
        return shellClient.typesExistsBuilder().indices(indexName);
    }

    public JsonOutput typesExists(String type) {
        return typesExistsBuilder().types(type).execute();
    }

    public GetWarmerRequestBuilder<JsonInput, JsonOutput> warmerGetBuilder() {
        return shellClient.warmerGetBuilder().indices(indexName);
    }

    public JsonOutput warmerGet() {
        return warmerGetBuilder().execute();
    }

    public DeleteWarmerRequestBuilder<JsonInput, JsonOutput> warmerDeleteBuilder() {
        return shellClient.warmerDeleteBuilder().indices(indexName);
    }

    public JsonOutput warmerDelete(String name) {
        return warmerDeleteBuilder().name(name).execute();
    }

    public JsonOutput warmerDelete() {
        return warmerDeleteBuilder().execute();
    }

    /*
    Cluster APIs that make sense for a specific index
     */
    public ClusterHealthRequestBuilder<JsonInput, JsonOutput> clusterHealthBuilder() {
        return shellClient.clusterHealthBuilder().indices(indexName);
    }

    public JsonOutput clusterHealth() {
        return clusterHealthBuilder().execute();
    }

    public ClusterStateRequestBuilder<JsonInput, JsonOutput> clusterStateBuilder() {
        return shellClient.clusterStateBuilder().filterIndices(indexName);
    }

    public JsonOutput clusterState() {
        return clusterStateBuilder().execute();
    }


    @Override
    public String toString() {
        return shellClient.toString() + " - " + (alias ? "alias" : "index") + " [" + indexName + "]";
    }
}
