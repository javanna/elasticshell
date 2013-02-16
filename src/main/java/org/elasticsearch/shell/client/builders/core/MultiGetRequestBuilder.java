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
package org.elasticsearch.shell.client.builders.core;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderToXContent;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

/**
 * @author Luca Cavanna
 *
 * Request builder for multi get API
 */
@SuppressWarnings("unused")
public class MultiGetRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderToXContent<MultiGetRequest, MultiGetResponse, JsonInput, JsonOutput> {

    public MultiGetRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new MultiGetRequest(), jsonToString, stringToJson);
    }

    public MultiGetRequestBuilder<JsonInput, JsonOutput> add(String index, String type, String... ids) {
        for (String id : ids) {
            request.add(index, type, id);
        }
        return this;
    }

    public MultiGetRequestBuilder<JsonInput, JsonOutput> preference(String preference) {
        request.preference(preference);
        return this;
    }

    public MultiGetRequestBuilder<JsonInput, JsonOutput> refresh(boolean refresh) {
        request.refresh(refresh);
        return this;
    }

    public MultiGetRequestBuilder<JsonInput, JsonOutput> realtime(boolean realtime) {
        request.realtime(realtime);
        return this;
    }

    @Override
    protected ActionFuture<MultiGetResponse> doExecute(MultiGetRequest request) {
        return client.multiGet(request);
    }
}
