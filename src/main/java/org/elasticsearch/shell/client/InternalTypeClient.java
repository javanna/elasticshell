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

import org.elasticsearch.shell.client.executors.core.*;
import org.elasticsearch.shell.client.executors.indices.DeleteMappingRequestBuilder;
import org.elasticsearch.shell.client.executors.indices.GetMappingRequestBuilder;
import org.elasticsearch.shell.client.executors.indices.PutMappingRequestBuilder;

/**
 * @author Luca Cavanna
 *
 * Internal shell client that exposes operations available on a single type
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
@SuppressWarnings("unused")
public class InternalTypeClient<JsonInput, JsonOutput> {

    private final AbstractClient<JsonInput, JsonOutput> shellClient;
    private final String indexName;
    private final String typeName;

    public InternalTypeClient(AbstractClient<JsonInput, JsonOutput> shellClient, String indexName, String typeName) {
        this.shellClient = shellClient;
        this.indexName = indexName;
        this.typeName = typeName;
    }

    String indexName() {
        return indexName;
    }

    String typeName() {
        return typeName;
    }

    /*
    Core APIs
     */
    public CountRequestBuilder count() {
        return shellClient.count().indices(indexName).types(typeName);
    }

    public DeleteRequestBuilder delete() {
        return shellClient.delete().index(indexName).type(typeName);
    }

    public DeleteByQueryRequestBuilder deleteByQuery() {
        return shellClient.deleteByQuery().indices(indexName).types(typeName);
    }

    public ExplainRequestBuilder explain() {
        return shellClient.explain().index(indexName).type(typeName);
    }

    public GetRequestBuilder get() {
        return shellClient.get().index(indexName).type(typeName);
    }

    public IndexRequestBuilder index() {
        return shellClient.index().index(indexName).type(typeName);
    }

    public MoreLikeThisRequestBuilder moreLikeThis() {
        return shellClient.moreLikeThis().index(indexName).type(typeName);
    }

    public PercolateRequestBuilder percolate() {
        return shellClient.percolate().index(indexName).type(typeName);
    }

    public SearchRequestBuilder search() {
        return shellClient.search().indices(indexName).types(typeName);
    }

    public UpdateRequestBuilder update() {
        return shellClient.update().index(indexName).type(typeName);
    }

    public ValidateQueryRequestBuilder validate() {
        return shellClient.validate().indices(indexName).types(typeName);
    }


    /*
    Indices APIs that make sense for a specific type
     */
    public DeleteMappingRequestBuilder deleteMapping() {
        return shellClient.deleteMapping().indices(indexName).type(typeName);
    }

    public GetMappingRequestBuilder getMapping() {
        return shellClient.getMapping().indices(indexName).types(typeName);
    }

    public PutMappingRequestBuilder putMapping() {
        return shellClient.putMapping().indices(indexName).type(typeName);
    }

    @Override
    public String toString() {
        return shellClient.toString() + " - index [" + indexName + "] type [" + typeName + "]";
    }
}
