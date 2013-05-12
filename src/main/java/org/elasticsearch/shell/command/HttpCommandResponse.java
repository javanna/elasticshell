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
package org.elasticsearch.shell.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * @author Luca Cavanna
 *
 * Object that wraps the http response providing a proper toString()
 * together with the ability of extracting the content as a separate string
 */
public class HttpCommandResponse {

    private final String content;
    private final HttpResponse response;
    private final List<Header> headers;

    public HttpCommandResponse(HttpResponse response) {
        this.response = response;
        this.content = extractContent(response.getEntity());
        this.headers = Arrays.asList(response.getAllHeaders());
    }

    private String extractContent(HttpEntity entity) {
        if (entity == null) {
            return "";
        }

        try {
            return EntityUtils.toString(entity);
        } catch(IOException e) {
            throw new RuntimeException("Error while extracting http response content", e);
        }
    }

    @SuppressWarnings("unused")
    public String statusLine() {
        return response.getStatusLine().toString();
    }

    @SuppressWarnings("unused")
    public String headers() {
        return headers.toString();
    }

    @SuppressWarnings("unused")
    public String content() {
        return this.content;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(response.getStatusLine().toString())
                .append(" ")
                .append(headers)
                .append("\n")
                .append(content);

        return output.toString();
    }
}
