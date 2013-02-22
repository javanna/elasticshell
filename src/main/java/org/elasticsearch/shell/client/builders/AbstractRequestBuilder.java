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
import org.elasticsearch.shell.json.JsonToString;

/**
 * @author Luca Cavanna
 *
 * Base request builder which contains the common methods to handle an execution
 * together with its {@link ActionResponse}, which needs to be converted to some kind of generic output format
 *
 * @param <Request> the type of the {@link ActionRequest}
 * @param <Response> the type of the {@link ActionResponse}
 * @param <JsonInput> the native json received as input, depending on the script engine in use
 * @param <Output> the output format use to show the result of the execution
 */
public abstract class AbstractRequestBuilder<Request extends ActionRequest, Response extends ActionResponse, JsonInput, Output> {

    protected final Client client;
    protected Request request;
    protected final JsonToString<JsonInput> jsonToString;

    protected AbstractRequestBuilder(Client client, Request request, JsonToString<JsonInput> jsonToString) {
        this.client = client;
        this.request = request;
        this.jsonToString = jsonToString;
    }

    /**
     * Executes the underlying request after the request validation
     * @return the result of the execution
     */
    public Output execute() {
        ActionRequestValidationException validationException = request().validate();
        if (validationException != null) {
            throw validationException;
        }
        return responseToOutput(request, doExecute(request()).actionGet());
    }

    /**
     * Executes an elasticsearch {@link ActionRequest}
     * @param request  the request to execute
     * @return the result of the async execution as a Future
     */
    protected abstract ActionFuture<Response> doExecute(Request request);

    /**
     * Converts an elasticsearch {@link ActionResponse} to the output format that is used to present it as output
     * @param request the request that generated the given response
     * @param response the response to be converted
     * @return the output representation of the response
     */
    protected abstract Output responseToOutput(Request request, Response response);

    protected Request request() {
        return request;
    }

    /**
     * Helper common method that converts a native json as input to its string representation
     * @param source the native json as input
     * @return the json as string
     */
    protected String jsonToString(JsonInput source) {
        return jsonToString.jsonToString(source, false);
    }

    @Override
    public String toString() {
        String message = "%s for the underlying %s that follows:\n%s" +
                "\nYou might want to use the execute() method to send the request.";

        return String.format(message, getClass().getSimpleName(), request.getClass().getSimpleName()
                , request.toString());
    }
}
