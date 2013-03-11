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
 * Factory used to create new client objects ready to be embedded within the shell.
 * The returned client object is the javascript representation of the java client object used,
 * which depends on the script engine in use.
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

    /**
     * Creates a new {@link TransportClient} which will connect to the elasticsearch node with the default address.
     * @return the shell native object that represents the created {@link TransportClient}
     */
    public ShellNativeClient newTransportClient();

    /**
     * Creates a new {@link TransportClient} which will connect to the elasticsearch nodes with the given addresses.
     * Multiple addresses can be provided in the following form ["host1:9300","host2:9300"]
     * @param addresses The addresses of the nodes to connect to
     * @return the shell native object that represents the created {@link TransportClient}
     */
    public ShellNativeClient newTransportClient(String... addresses);
}
