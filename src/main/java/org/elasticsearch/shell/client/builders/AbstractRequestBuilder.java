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

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentBuilderString;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.shell.json.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Base reuqest builder which contains the common methods to handle an execution
 * together with its {@link ActionResponse}, which needs to be converted to native json
 *
 *
 * @param <Request> the type of the {@link ActionRequest}
 * @param <Response> the type of the {@link ActionResponse}
 * @param <JsonInput> the native json received as input, depending on the script engine in use
 * @param <JsonOutput> the native json returned as output, depending on the script engine in use
 */
public abstract class AbstractRequestBuilder<Request extends ActionRequest<Request>, Response extends ActionResponse, JsonInput, JsonOutput> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRequestBuilder.class);

    protected final Client client;
    protected final JsonSerializer<JsonInput, JsonOutput> jsonSerializer;
    protected Request request;

    protected AbstractRequestBuilder(Client client, Request request, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        this.client = client;
        this.request = request;
        this.jsonSerializer = jsonSerializer;
    }

    public JsonOutput execute() {
        ActionRequestValidationException validationException = request().validate();
        if (validationException != null) {
            throw validationException;
        }
        return responseToJson(request, doExecute(request()).actionGet());
    }

    protected Request request() {
        return request;
    }

    /**
     * Executes an elasticsearch {@link ActionRequest}
     * @param request  the request to execute
     * @return the result of the async execution as a Future
     */
    protected abstract ActionFuture<Response> doExecute(Request request);

    /**
     * Converts an elasticsearch {@link ActionResponse} to native json
     * @param request the request that generated the given response
     * @param response the response to be converted
     * @return the native json representation of the response
     */
    protected JsonOutput responseToJson(Request request, Response response) {
        try {
            return jsonSerializer.stringToJson(toXContent(request, response, initContentBuilder()).string());
        } catch (IOException e) {
            logger.error("Error while generating the XContent response", e);
            return null;
        }
    }

    protected XContentBuilder initContentBuilder() throws IOException {
        return JsonXContent.contentBuilder();
    }

    protected String jsonToString(JsonInput source) {
        return jsonSerializer.jsonToString(source, false);
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
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<" + request.getClass().getSimpleName() + ">\n" + request.toString();
    }
}