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

import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.shell.JsonSerializer;

/**
 * @author Luca Cavanna
 *
 * Client that exposes the elasticsearch admin client
 * Wraps an {@link org.elasticsearch.client.AdminClient}
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
public class AdminClient<JsonInput,JsonOutput> {

    private final IndicesClient<JsonInput, JsonOutput> indicesClient;
    private final ClusterClient<JsonInput, JsonOutput> clusterClient;

    public AdminClient(IndicesAdminClient indicesAdminClient, ClusterAdminClient clusterAdminClient,
                       JsonSerializer<JsonInput, JsonOutput>jsonSerializer) {
        this.indicesClient = new IndicesClient<JsonInput, JsonOutput>(indicesAdminClient, jsonSerializer);
        this.clusterClient = new ClusterClient<JsonInput, JsonOutput>(clusterAdminClient, jsonSerializer);
    }

    /**
     * Returns the indices client that exposes the indices apis
     * @return the indices client
     */
    public IndicesClient<JsonInput, JsonOutput> indices() {
        return indicesClient;
    }

    /**
     * Returns the cluster client that exposes the cluster apis
     * @return the cluster client
     */
    public ClusterClient<JsonInput, JsonOutput> cluster() {
        return clusterClient;
    }
}
