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
import java.io.PrintStream;
import java.nio.charset.Charset;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.shell.console.Console;
import org.elasticsearch.shell.http.ShellHttpClient;

/**
 * @author Luca Cavanna
 *
 * Command that sends http POST requests to the url provided as input
 */
@ExecutableCommand(aliases = {"httpPost", "post"})
public class HttpPostCommand extends Command {

    private final ShellHttpClient shellHttpClient;

    @Inject
    HttpPostCommand(Console<PrintStream> console, ShellHttpClient shellHttpClient) {
        super(console);
        this.shellHttpClient = shellHttpClient;
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url) throws IOException {
        return new HttpCommandResponse(shellHttpClient.getHttpClient().execute(new HttpPost(url)));
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, String body) throws IOException {
        HttpPost httpPut = new HttpPost(url);
        httpPut.setEntity(new StringEntity(body, ContentType.DEFAULT_TEXT));
        return new HttpCommandResponse(shellHttpClient.getHttpClient().execute(httpPut));
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, String body, String mimeType) throws IOException {
        HttpPost httpPut = new HttpPost(url);
        httpPut.setEntity(new StringEntity(body, ContentType.create(mimeType)));
        return new HttpCommandResponse(shellHttpClient.getHttpClient().execute(httpPut));
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, String body, String mimeType, String charsetName) throws IOException {
        HttpPost httpPut = new HttpPost(url);
        httpPut.setEntity(new StringEntity(body, ContentType.create(mimeType, Charset.forName(charsetName))));
        return new HttpCommandResponse(shellHttpClient.getHttpClient().execute(httpPut));
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, HttpParameters parameters) throws IOException {
        HttpPost httpPut = new HttpPost(url);
        httpPut.setEntity(new UrlEncodedFormEntity(parameters));
        return new HttpCommandResponse(shellHttpClient.getHttpClient().execute(httpPut));
    }

    @SuppressWarnings("unused")
    public HttpCommandResponse execute(String url, HttpParameters parameters, String charsetName) throws IOException {
        HttpPost httpPut = new HttpPost(url);
        httpPut.setEntity(new UrlEncodedFormEntity(parameters, Charset.forName(charsetName)));
        return new HttpCommandResponse(shellHttpClient.getHttpClient().execute(httpPut));
    }
}
