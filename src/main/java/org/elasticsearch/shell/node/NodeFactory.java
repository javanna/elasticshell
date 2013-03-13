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
package org.elasticsearch.shell.node;

/**
 * @author Luca Cavanna
 *
 * Factory used to create new elasticsearch nodes
 * @param <ShellNativeClient> the shell native class used to represent a client within the shell
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
public interface NodeFactory<ShellNativeClient, JsonInput, JsonOutput> {

    /**
     * Creates a new local node which will join the cluster with the default name
     * @return the new local node created
     */
    public Node<ShellNativeClient, JsonInput, JsonOutput> newLocalNode();

    /**
     * Creates a new local node whih will join the cluster with the given name
     * @param clusterName the name of the cluster to join (or create)
     * @return the new local node created
     */
    public Node<ShellNativeClient, JsonInput, JsonOutput> newLocalNode(String clusterName);
}