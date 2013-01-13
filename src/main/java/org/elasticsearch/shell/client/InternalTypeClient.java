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

/**
 * @author Luca Cavanna
 *
 * Internal shell client that exposes operations available on a single type
 * @param <JSON> the shell native object that represents a json object (depending on the engine)
 */
public class InternalTypeClient<JSON> {

    private final AbstractClient<JSON> shellClient;
    private final String indexName;
    private final String typeName;

    public InternalTypeClient(AbstractClient shellClient, String indexName, String typeName) {
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

    public void index(String id, String source) {
        shellClient.index(indexName, typeName, id, source);
    }

    public void index(String source) {
        shellClient.index(indexName, typeName, null, source);
    }

    public void index(String id, JSON source) {
        shellClient.index(indexName, typeName, id, source);
    }

    public void index(JSON source) {
        shellClient.index(indexName, typeName, null, source);
    }

    @Override
    public String toString() {
        return shellClient.toString() + " - index [" + indexName + "] type [" + typeName + "]";
    }
}
