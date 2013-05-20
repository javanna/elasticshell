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

import org.elasticsearch.shell.client.builders.cluster.*;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

/**
 * @author Luca Cavanna
 * 
 * Client that exposes all the cluster Apis
 */
public class ClusterApiClient<EsClient extends org.elasticsearch.client.support.AbstractClient, JsonInput, JsonOutput> {

    private final AbstractClient<EsClient, JsonInput, JsonOutput> client;
    private final JsonToString<JsonInput> jsonToString;
    private final StringToJson<JsonOutput> stringToJson;

    ClusterApiClient(AbstractClient<EsClient, JsonInput, JsonOutput> client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        this.client = client;
        this.jsonToString = jsonToString;
        this.stringToJson = stringToJson;
    }

    public ClusterHealthRequestBuilder<JsonInput, JsonOutput> healthBuilder() {
        return new ClusterHealthRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput health(String... indices) {
        return healthBuilder().indices(indices).execute();
    }

    public ClusterStateRequestBuilder<JsonInput, JsonOutput> stateBuilder() {
        return new ClusterStateRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput state() {
        return stateBuilder().execute();
    }

    protected GetClusterSettingsRequestBuilder<JsonInput, JsonOutput> settingsGetBuilder() {
        return new GetClusterSettingsRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput settingsGet() {
        return settingsGetBuilder().execute();
    }

    public UpdateClusterSettingsRequestBuilder<JsonInput, JsonOutput> settingsUpdateBuilder() {
        return new UpdateClusterSettingsRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput settingsTransientUpdate(JsonInput settings) {
        return settingsUpdateBuilder().transientSettings(settings).execute();
    }

    public JsonOutput settingsPersistentUpdate(JsonInput settings) {
        return settingsUpdateBuilder().persistentSettings(settings).execute();
    }

    public NodesInfoRequestBuilder<JsonInput, JsonOutput> nodesInfoBuilder() {
        return new NodesInfoRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput nodesInfo() {
        return nodesInfoBuilder().execute();
    }

    public NodesStatsRequestBuilder<JsonInput, JsonOutput> nodesStatsBuilder() {
        return new NodesStatsRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput nodesStats() {
        return nodesStatsBuilder().execute();
    }

    public NodesHotThreadsRequestBuilder<JsonInput> nodesHotThreadsBuilder() {
        return new NodesHotThreadsRequestBuilder<JsonInput>(client.client(), jsonToString);
    }

    public String nodesHotThreads() {
        return nodesHotThreadsBuilder().execute();
    }

    public ClusterRerouteRequestBuilder<JsonInput, JsonOutput> rerouteBuilder() {
        return new ClusterRerouteRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput reroute(JsonInput source) throws Exception {
        return rerouteBuilder().source(source).execute();
    }

    public NodesRestartRequestBuilder<JsonInput, JsonOutput> nodesRestartBuilder() {
        return new NodesRestartRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput nodesRestart() throws Exception {
        return nodesRestartBuilder().execute();
    }

    public NodesShutdownRequestBuilder<JsonInput, JsonOutput> nodesShutdownBuilder() {
        return new NodesShutdownRequestBuilder<JsonInput, JsonOutput>(client.client(), jsonToString, stringToJson);
    }

    public JsonOutput nodesShutdown(String... nodes) throws Exception {
        return nodesShutdownBuilder().nodesIds(nodes).execute();
    }
}
