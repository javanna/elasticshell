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
package org.elasticsearch.shell.client.executors;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * {@link RequestExecutor} implementation that makes it easy to write
 * the {@link ActionResponse} if it implements {@link ToXContent}
 * @param <Request> the type of the {@link ActionRequest}
 * @param <Response> the type of the {@link ActionResponse}
 * @param <JsonInput> the native json received as input, depending on the script engine in use
 * @param <JsonOutput> the native json returned as output, depending on the script engine in use
 */
public abstract class AbstractRequestExecutorToXContent<Request extends ActionRequest<Request>, Response extends ActionResponse & ToXContent, JsonInput, JsonOutput>
        extends AbstractRequestExecutor<Request, Response, JsonInput, JsonOutput> {

    protected AbstractRequestExecutorToXContent(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, jsonSerializer);
    }

    @Override
    protected XContentBuilder toXContent(Request request, Response response, XContentBuilder builder)  throws IOException {
        return response.toXContent(builder, ToXContent.EMPTY_PARAMS);
    }
}
