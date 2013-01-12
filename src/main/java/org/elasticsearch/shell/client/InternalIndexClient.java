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
import org.elasticsearch.cluster.metadata.IndexMetaData;

/**
 * @author Luca Cavanna
 *
 * Internal shell client that exposes operations available on a single index
 */
public class InternalIndexClient {

    private final AbstractClient shellClient;
    private final String indexName;

    public InternalIndexClient(AbstractClient shellClient, String indexName) {
        this.shellClient = shellClient;
        this.indexName = indexName;
    }

    String indexName() {
        return indexName;
    }

    protected Index getIndex() {
        ClusterStateResponse response = shellClient.client().admin().cluster().prepareState().setFilterBlocks(true)
                .setFilterRoutingTable(true).setFilterNodes(true).setFilterIndices(indexName).execute().actionGet();

        IndexMetaData indexMetaData = response.state().metaData().indices().values().iterator().next();

        return new Index(indexMetaData.index(), indexMetaData.mappings().keySet(), indexMetaData.aliases().keySet());
    }

    public String[] showTypes() {
        return getIndex().types();
    }

    public String[] showAliases() {
        return getIndex().aliases();
    }

    @Override
    public String toString() {
        return shellClient.toString() + " - index [" + indexName + "]";
    }
}
