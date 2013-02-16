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
package org.elasticsearch.shell.client.builders;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentBuilderString;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.StringToJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for most of the requests, that have native json as output
 *
 * @param <Request> the type of the {@link ActionRequest}
 * @param <Response> the type of the {@link ActionResponse}
 * @param <JsonInput> the native json received as input, depending on the script engine in use
 * @param <JsonOutput> the native json returned as output, depending on the script engine in use
 */
public abstract class AbstractRequestBuilderJsonOutput<Request extends ActionRequest<Request>, Response extends ActionResponse, JsonInput, JsonOutput>
                extends AbstractRequestBuilder<Request, Response, JsonInput, JsonOutput> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRequestBuilderJsonOutput.class);

    protected final StringToJson<JsonOutput> stringToJson;

    protected AbstractRequestBuilderJsonOutput(Client client, Request request, JsonToString<JsonInput> jsonToString, StringToJson<JsonOutput> stringToJson) {
        super(client, request, jsonToString);
        this.stringToJson = stringToJson;
    }

    protected XContentBuilder initContentBuilder() throws IOException {
        return JsonXContent.contentBuilder();
    }

    /**
     * Converts an elasticsearch {@link ActionResponse} to native json
     * @param request the request that generated the given response
     * @param response the response to be converted
     * @return the native json representation of the response
     */
    @Override
    protected JsonOutput responseToOutput(Request request, Response response) {
        try {
            return stringToJson.stringToJson(toXContent(request, response, initContentBuilder()).string());
        } catch (IOException e) {
            logger.error("Error while generating the XContent response", e);
            return null;
        }
    }

    /**
     * Writes an elasticsearch {@link ActionResponse} to the given {@link XContentBuilder}
     * @param request the request that generated the given response
     * @param response the response that needs to be written out
     * @param builder the builder where to write the response
     * @return the builder
     * @throws IOException if there are problems while writing the response
     */
    protected abstract XContentBuilder toXContent(Request request, Response response, XContentBuilder builder) throws IOException;

    protected static final class Fields {
        public static final XContentBuilderString OK = new XContentBuilderString("ok");
        public static final XContentBuilderString _INDEX = new XContentBuilderString("_index");
        public static final XContentBuilderString _TYPE = new XContentBuilderString("_type");
        public static final XContentBuilderString _ID = new XContentBuilderString("_id");
        public static final XContentBuilderString _VERSION = new XContentBuilderString("_version");
        public static final XContentBuilderString MATCHES = new XContentBuilderString("matches");
        public static final XContentBuilderString FOUND = new XContentBuilderString("found");
        public static final XContentBuilderString COUNT = new XContentBuilderString("count");
        public static final XContentBuilderString GET = new XContentBuilderString("get");
        public static final XContentBuilderString MATCHED = new XContentBuilderString("matched");
        public static final XContentBuilderString EXPLANATION = new XContentBuilderString("explanation");
        public static final XContentBuilderString VALUE = new XContentBuilderString("value");
        public static final XContentBuilderString DESCRIPTION = new XContentBuilderString("description");
        public static final XContentBuilderString DETAILS = new XContentBuilderString("details");
        public static final XContentBuilderString ACKNOWLEDGED = new XContentBuilderString("acknowledged");
        public static final XContentBuilderString CLUSTER_NAME = new XContentBuilderString("cluster_name");
        public static final XContentBuilderString STATUS = new XContentBuilderString("status");
        public static final XContentBuilderString TIMED_OUT = new XContentBuilderString("timed_out");
        public static final XContentBuilderString NUMBER_OF_SHARDS = new XContentBuilderString("number_of_shards");
        public static final XContentBuilderString NUMBER_OF_REPLICAS = new XContentBuilderString("number_of_replicas");
        public static final XContentBuilderString NUMBER_OF_NODES = new XContentBuilderString("number_of_nodes");
        public static final XContentBuilderString NUMBER_OF_DATA_NODES = new XContentBuilderString("number_of_data_nodes");
        public static final XContentBuilderString ACTIVE_PRIMARY_SHARDS = new XContentBuilderString("active_primary_shards");
        public static final XContentBuilderString ACTIVE_SHARDS = new XContentBuilderString("active_shards");
        public static final XContentBuilderString RELOCATING_SHARDS = new XContentBuilderString("relocating_shards");
        public static final XContentBuilderString INITIALIZING_SHARDS = new XContentBuilderString("initializing_shards");
        public static final XContentBuilderString UNASSIGNED_SHARDS = new XContentBuilderString("unassigned_shards");
        public static final XContentBuilderString VALIDATION_FAILURES = new XContentBuilderString("validation_failures");
        public static final XContentBuilderString INDICES = new XContentBuilderString("indices");
        public static final XContentBuilderString SHARDS = new XContentBuilderString("shards");
        public static final XContentBuilderString PRIMARY_ACTIVE = new XContentBuilderString("primary_active");
    }
}