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

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.shell.ExecutorWithProgress;
import org.elasticsearch.shell.client.ClientFactory;
import org.elasticsearch.shell.console.Console;

import java.io.PrintStream;

/**
 * @author Luca Cavanna
 *
 * {@link org.elasticsearch.shell.command.Command} that creates a new {@link org.elasticsearch.shell.client.NodeClient}
 */
@ExecutableCommand(aliases = {"transportClient"})
public class TransportClientCommand<ShellNativeClient> extends CommandWithProgress {

    private static final String INITIAL_MESSAGE = "Creating new transport client";

    private final ClientFactory<ShellNativeClient> clientFactory;

    @Inject
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

    public ShellNativeClient execute(final String... addresses) {
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

    @Override
    public String help() {
        return HELP;
    }

    private static final String HELP = "Creates a new elasticsearch transport client using the Java API.\n" +
            "(http://www.elasticsearch.org/guide/reference/java-api/client.html)\n" +
            "\n" +
            "The following command with no arguments will create a " +
            "new transport client connected to localhost, port 9300:\n" +
            "es = transportClient();\n\n" +
            "The following command with a string argument will create " +
            "a new transport client connected to es-host, port 9302:\n" +
            "es = transportClient('es-host:9302');\n\n" +
            "You can connect to multiple nodes just providing a list of addresses:\n" +
            "es = transportClient('es-host1:9300','es-host2:9300','es-host3:9300');\n";
}
