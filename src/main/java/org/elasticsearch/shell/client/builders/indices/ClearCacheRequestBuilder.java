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
package org.elasticsearch.shell.client.builders.indices;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheRequest;
import org.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheResponse;
import org.elasticsearch.action.support.IgnoreIndices;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilderJsonOutput;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;

import java.io.IOException;

import static org.elasticsearch.rest.action.support.RestActions.buildBroadcastShardsHeader;

/**
 * @author Luca Cavanna
 *
 * Request builder for clear cache API
 */
@SuppressWarnings("unused")
public class ClearCacheRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilderJsonOutput<ClearIndicesCacheRequest, ClearIndicesCacheResponse, JsonInput, JsonOutput> {

    public ClearCacheRequestBuilder(Client client, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, new ClearIndicesCacheRequest(), jsonToString, stringToJson);
    }

    public ClearCacheRequestBuilder<JsonInput, JsonOutput> indices(String... indices) {
        request.indices(indices);
        return this;
    }

    public ClearCacheRequestBuilder<JsonInput, JsonOutput> ignoreIndices(String ignoreIndices) {
        request.ignoreIndices(IgnoreIndices.fromString(ignoreIndices));
        return this;
    }

    public ClearCacheRequestBuilder<JsonInput, JsonOutput> filterCache(boolean filterCache) {
        request.filterCache(filterCache);
        return this;
    }

    public ClearCacheRequestBuilder<JsonInput, JsonOutput> fieldDataCache(boolean fieldDataCache) {
        request.fieldDataCache(fieldDataCache);
        return this;
    }

    public ClearCacheRequestBuilder<JsonInput, JsonOutput> fields(String... fields) {
        request.fields(fields);
        return this;
    }

    public ClearCacheRequestBuilder<JsonInput, JsonOutput> filterKeys(String... filterKeys) {
        request.filterKeys(filterKeys);
        return this;
    }

    public ClearCacheRequestBuilder<JsonInput, JsonOutput> idCache(boolean idCache) {
        request.idCache(idCache);
        return this;
    }

    @Override
    protected ActionFuture<ClearIndicesCacheResponse> doExecute(ClearIndicesCacheRequest request) {
        return client.admin().indices().clearCache(request);
    }

    @Override
    protected XContentBuilder toXContent(ClearIndicesCacheRequest request, ClearIndicesCacheResponse response, XContentBuilder builder) throws IOException {
        builder.startObject();
        builder.field(Fields.OK, true);
        buildBroadcastShardsHeader(builder, response);
        builder.endObject();
        return builder;
    }
}
