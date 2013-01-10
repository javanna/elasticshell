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
 * Factory used to create new client objects, which can either be {@link org.elasticsearch.client.node.NodeClient}
 * or {@link org.elasticsearch.client.transport.TransportClient}.
 * The elasticsearch {@link org.elasticsearch.client.Client} will be wrapped into the related shell client object
 * {@link NodeClient} or {@link TransportClient}, which will be again wrapped into the shell native object used
 * within the shell.
 *
 * @param <ShellNativeClient> the shell native class used to represent a client within the shell
 */
public interface ClientFactory<ShellNativeClient> {

    /**
     * Creates a new {@link NodeClient} which will connect to the elasticsearch cluster with the default name.
     * @return the shell native object that represents the created {@link NodeClient}
     */
    public ShellNativeClient newNodeClient();

    /**
     * Creates a new {@link NodeClient} which will connect to the elasticsearch cluster with the given name.
     * @param clusterName the name of the elasticsearch cluster to connect to
     * @return the shell native object that represents the created {@link NodeClient}
     */
    public ShellNativeClient newNodeClient(String clusterName);

    public ShellNativeClient newTransportClient();
}
