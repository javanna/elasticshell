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
package org.elasticsearch.shell;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

public class Client {

    private final Node node;
    private final org.elasticsearch.client.Client client;

    public Client() {
        this("elasticsearch");
    }

    public Client(String clusterName) {
        Settings settings = ImmutableSettings.settingsBuilder().put("node.name", "elasticsearch-shell").build();
        this.node = NodeBuilder.nodeBuilder().clusterName(clusterName).client(true).settings(settings).build();
        node.start();
        this.client = node.client();
    }

    public String get(String index, String type, String id) {
        return client.prepareGet(index, type, id).execute().actionGet().sourceAsString();
    }

    @Override
    public String toString() {
        //TODO write a nicer message, maybe with some more information
        return "client connected";
    }

    public void close() {
        this.client.close();
        this.node.close();
    }
}
