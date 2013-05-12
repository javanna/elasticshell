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
package org.elasticsearch.shell.http;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author Luca Cavanna
 *
 * Http client that wraps the http components http client and provides it to all the commands that need it
 */
public class ShellHttpClient {

    private final HttpClient httpClient;

    ShellHttpClient() {
        this.httpClient = new DecompressingHttpClient(new DefaultHttpClient());
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void shutdown() {
        httpClient.getConnectionManager().shutdown();
    }
}
