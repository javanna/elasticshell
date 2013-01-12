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

import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.shell.ExecutorWithProgress;
import org.elasticsearch.shell.client.ClientFactory;
import org.elasticsearch.shell.console.Console;

import java.io.PrintStream;

/**
 * @author Luca Cavanna
 *
 * {@link org.elasticsearch.shell.command.Command} that creates a new {@link org.elasticsearch.shell.client.NodeClient}
 */
public class TransportClientCommand<ShellNativeClient> extends CommandWithProgress {

    private static final String INITIAL_MESSAGE = "Creating new transport client";

    private final ClientFactory<ShellNativeClient> clientFactory;

    TransportClientCommand(ClientFactory<ShellNativeClient> clientFactory, Console<PrintStream> console) {
        super(console);
        this.clientFactory = clientFactory;
    }

    @Override
    protected String initialMessage() {
        return INITIAL_MESSAGE;
    }

    @SuppressWarnings("unused")
    public ShellNativeClient execute() {
        return checkNull(executeWithProgress(new ExecutorWithProgress.ActionCallback<ShellNativeClient>() {
            @Override
            public ShellNativeClient execute() {
                return clientFactory.newTransportClient();
            }
        }));
    }

    @SuppressWarnings("unused")
    public ShellNativeClient execute(final String host, final int port) {
        return checkNull(executeWithProgress(new ExecutorWithProgress.ActionCallback<ShellNativeClient>() {
            @Override
            public ShellNativeClient execute() {
                return clientFactory.newTransportClient(host, port);
            }
        }));
    }

    @SuppressWarnings("unused")
    public ShellNativeClient execute(final TransportAddress... addresses) {
        return checkNull(executeWithProgress(new ExecutorWithProgress.ActionCallback<ShellNativeClient>() {
            @Override
            public ShellNativeClient execute() {
                return clientFactory.newTransportClient(addresses);
            }
        }));
    }

    protected ShellNativeClient checkNull(ShellNativeClient shellNativeClient) {
        if (shellNativeClient == null) {
            console.println("No client created, check the provided address!");
        }
        return shellNativeClient;
    }
}
