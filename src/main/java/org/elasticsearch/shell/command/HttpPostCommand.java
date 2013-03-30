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

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.shell.console.Console;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * @author Luca Cavanna
 *
 * Command that sends http POST requests to the url provided as input
 */
@ExecutableCommand(aliases = {"httpPost", "post"})
public class HttpPostCommand extends Command {

    @Inject
    HttpPostCommand(Console<PrintStream> console) {
        super(console);
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url) throws IOException {
        return new HttpCommandResponse(Request.Post(url)
                .execute().returnResponse());
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, String body) throws IOException {
        return new HttpCommandResponse(Request.Post(url).bodyString(body, ContentType.DEFAULT_TEXT)
                .execute().returnResponse());
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, String body, String mimeType) throws IOException {
        return new HttpCommandResponse(Request.Post(url).bodyString(body, ContentType.create(mimeType))
                .execute().returnResponse());
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, String body, String mimeType, String charsetName) throws IOException {
        return new HttpCommandResponse(Request.Post(url).bodyString(body, ContentType.create(mimeType, Charset.forName(charsetName)))
                .execute().returnResponse());
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, HttpParameters parameters) throws IOException {
        return new HttpCommandResponse(Request.Post(url).bodyForm(parameters)
                .execute().returnResponse());
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, HttpParameters parameters, String charsetName) throws IOException {
        return new HttpCommandResponse(Request.Post(url).bodyForm(parameters, Charset.forName(charsetName))
                .execute().returnResponse());
    }
}
