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
 * Command that sends http PUT requests to the url provided as input
 */
@ExecutableCommand(aliases = {"httpPut", "put"})
public class HttpPutCommand extends Command {

    @Inject
    HttpPutCommand(Console<PrintStream> console) {
        super(console);
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url) throws IOException {
        return new HttpCommandResponse(Request.Put(url)
                .execute().returnResponse());
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, String body) throws IOException {
        return new HttpCommandResponse(Request.Put(url).bodyString(body, ContentType.DEFAULT_TEXT)
                .execute().returnResponse());
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, String body, String mimeType) throws IOException {
        return new HttpCommandResponse(Request.Put(url).bodyString(body, ContentType.create(mimeType))
                .execute().returnResponse());
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, String body, String mimeType, String charsetName) throws IOException {
        return new HttpCommandResponse(Request.Put(url).bodyString(body, ContentType.create(mimeType, Charset.forName(charsetName)))
                .execute().returnResponse());
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, HttpParameters parameters) throws IOException {
        return new HttpCommandResponse(Request.Put(url).bodyForm(parameters)
                .execute().returnResponse());
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, HttpParameters parameters, String charsetName) throws IOException {
        return new HttpCommandResponse(Request.Put(url).bodyForm(parameters, Charset.forName(charsetName))
                .execute().returnResponse());
    }
    
    @Override
    public String help() {
        return HELP;
    }

    private static final String HELP = "Sends an http PUT request to the specified url" +
            " and returns the response.\n\n" +
            "The following command will index a document within elasticsearch using the INDEX api:\n" +
            "httpPut('http://localhost:9200/twitter/tweet/1', '{\"content\":\"#elasticsearch rocks\"}');\n\n" +
            "You can either provide the request body as a string like in the example above" +
            " or specify more parameters as key value pairs using the HttpParameters helper class like this:\n" +
            "var params = HttpParameters.builder().add('foo', 'bla').add('bar', 'bla');\n" +
            "httpPut('http://host:8080', params);\n";
}
