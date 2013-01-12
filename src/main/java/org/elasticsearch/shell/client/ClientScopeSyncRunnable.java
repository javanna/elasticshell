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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Luca Cavanna
 *
 * Runnable that keeps up-to-date the shell scope while the indexes and types available in elasticsearch change.
 * Needed in order to provide the ability to run commands that are client or type specific (e.g. client.index.type.search() )
 */
public abstract class ClientScopeSyncRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ClientScopeSyncRunnable.class);

    protected final AbstractClient shellClient;
    protected Set<Index> indexes = new HashSet<Index>();

    protected ClientScopeSyncRunnable(AbstractClient shellClient) {
        this.shellClient = shellClient;
    }

    /**
     * Keeps in sync the shell scope
     */
    @Override
    public void run() {
        try {
            syncIndexes(getIndexes());
        } catch(Throwable t) {
            logger.error("Error while synchronizing the scope", t);
        }
    }

    /**
     * Retrieves the current indexes and types from elasticsearch
     * @return a set containing the indexes available in the elasticsearch cluster and their types
     */
    protected Set<Index> getIndexes() {
        ClusterStateResponse response = shellClient.client().admin().cluster().prepareState().setFilterBlocks(true)
                .setFilterRoutingTable(true).setFilterNodes(true).execute().actionGet();

        Set<Index> newIndexes = new HashSet<Index>();
        for (IndexMetaData indexMetaData : response.state().metaData().indices().values()) {
            logger.debug("Processing index {}", indexMetaData.index());

            Set<String> indexNames = indexMetaData.mappings().keySet();
            newIndexes.add(new Index(indexMetaData.index(), indexNames.toArray(new String[indexNames.size()])));
            for (String alias : indexMetaData.aliases().keySet()) {
                newIndexes.add(new Index(alias, indexMetaData.mappings().keySet().toArray(new String[indexNames.size()])));
            }
        }
        return newIndexes;
    }

    /**
     * Synchronizes the registered indexes given the new indexes retrieved from the elasticsearch cluster
     * @param newIndexes the indexes currently available in the cluster
     */
    protected synchronized void syncIndexes(Set<Index> newIndexes) {
        //every index that is currently available gets registered (insert/update)
        for (Index index : newIndexes) {
            registerIndex(index);
            indexes.remove(index);
        }

        //The indexes that are left in the set need to be removed because they don't exist anymore
        for (Index index : indexes) {
            unregisterIndex(index);
        }

        this.indexes = newIndexes;
    }

    /**
     * Registers an index to the shell scope
     * @param index the index that needs to be registered to the shell scope
     */
    protected void registerIndex(Index index) {
        InternalIndexClient indexClient = new InternalIndexClient(shellClient, index.name());
        InternalTypeClient[] typeClients = new InternalTypeClient[index.types().length];
        if (index.types() != null) {
            for (int i = 0; i < index.types().length; i++) {
                typeClients[i] = new InternalTypeClient(shellClient, index.name(), index.types()[i]);
            }
        }
        registerIndexAndTypes(indexClient, typeClients);
    }

    /**
     * Registers the {@link InternalIndexClient} and related {@link InternalTypeClient} for each available type
     * @param indexClient clients that exposes only the index-specific operations
     * @param typeClients list of clients that expose only the type-specific operations
     */
    protected abstract void registerIndexAndTypes(InternalIndexClient indexClient, InternalTypeClient... typeClients);

    /**
     * Unregisters an index from the shell scope
     * @param index the index that needs to be unregistered
     */
    protected abstract void unregisterIndex(Index index);

    /**
     * Inner class that represents an index with its optional types
     */
    static class Index {

        private final String name;
        private final String[] types;

        Index(String name, String... types) {
            this.name = name;
            this.types = types;
        }

        public String name() {
            return name;
        }

        public String[] types() {
            return types;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Index)) return false;

            Index index = (Index) o;

            if (name != null ? !name.equals(index.name) : index.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }
}