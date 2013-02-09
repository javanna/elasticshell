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
    public CountRequestBuilder count() {
        return shellClient.count().indices(indexName);
    }

    public DeleteRequestBuilder delete() {
        return shellClient.delete().index(indexName);
    }

    public DeleteByQueryRequestBuilder deleteByQuery() {
        return shellClient.deleteByQuery().indices(indexName);
    }

    public ExplainRequestBuilder explain() {
        return shellClient.explain().index(indexName);
    }

    public GetRequestBuilder get() {
        return shellClient.get().index(indexName);
    }

    public IndexRequestBuilder index() {
        return shellClient.index().index(indexName);
    }

    public MoreLikeThisRequestBuilder moreLikeThis() {
        return shellClient.moreLikeThis().index(indexName);
    }

    public PercolateRequestBuilder percolate() {
        return shellClient.percolate().index(indexName);
    }

    public SearchRequestBuilder search() {
        return shellClient.search().indices(indexName);
    }

    public UpdateRequestBuilder update() {
        return shellClient.update().index(indexName);
    }

    public ValidateQueryRequestBuilder validate() {
        return shellClient.validate().indices(indexName);
    }

    /*
    Indices APIs that make sense for a specific index
     */
    public GetAliasesIndicesRequestBuilder getAliases() {
        return shellClient.getAliases().indices(indexName);
    }

    public AnalyzeRequestBuilder analyze() {
        return shellClient.analyze().index(indexName);
    }

    public ClearCacheRequestBuilder clearCache() {
        return shellClient.clearCache().indices(indexName);
    }

    public CloseIndexRequestBuilder closeIndex() {
        return shellClient.closeIndex().index(indexName);
    }

    public DeleteIndexRequestBuilder deleteIndex() {
        return shellClient.deleteIndex().indices(indexName);
    }

    public DeleteMappingRequestBuilder deleteMapping() {
        return shellClient.deleteMapping().indices(indexName);
    }

    public FlushRequestBuilder flush() {
        return shellClient.flush().indices(indexName);
    }

    public GetMappingRequestBuilder getMapping() {
        return shellClient.getMapping().indices(indexName);
    }

    public GetSettingsRequestBuilder getSettings() {
        return shellClient.getSettings().indices(indexName);
    }

    public OpenIndexRequestBuilder openIndex() {
        return shellClient.openIndex().index(indexName);
    }

    public OptimizeRequestBuilder optimize() {
        return shellClient.optimize().indices(indexName);
    }

    public PutMappingRequestBuilder putMapping() {
        return shellClient.putMapping().indices(indexName);
    }

    public RefreshRequestBuilder refresh() {
        return shellClient.refresh().indices(indexName);
    }

    public SegmentsRequestBuilder segments() {
        return shellClient.segments().indices(indexName);
    }

    public StatsRequestBuilder stats() {
        return shellClient.stats().indices(indexName);
    }

    public StatusRequestBuilder status() {
        return shellClient.status().indices(indexName);
    }

    public TypesExistsRequestBuilder typesExists() {
        return shellClient.typesExists().indices(indexName);
    }

    public UpdateSettingsRequestBuilder updateSettings() {
        return shellClient.updateSettings().indices(indexName);
    }

    public GetWarmerRequestBuilder getWarmer() {
        return shellClient.getWarmer().indices(indexName);
    }

    public DeleteWarmerRequestBuilder deleteWarmer() {
        return shellClient.deleteWarmer().indices(indexName);
    }

    @Override
    public String toString() {
        return shellClient.toString() + " - " + (alias ? "alias" : "index") + " [" + indexName + "]";
    }
}
