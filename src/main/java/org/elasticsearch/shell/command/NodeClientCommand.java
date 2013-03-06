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

import org.elasticsearch.shell.ExecutorWithProgress;
import org.elasticsearch.shell.client.ClientFactory;
import org.elasticsearch.shell.console.Console;

import java.io.PrintStream;

/**
 * @author Luca Cavanna
 *
 * {@link Command} that creates a new {@link org.elasticsearch.shell.client.NodeClient}
 */
public class NodeClientCommand<ShellNativeClient> extends CommandWithProgress {

    private static final String INITIAL_MESSAGE = "Creating new node client";

    private final ClientFactory<ShellNativeClient> clientFactory;

    NodeClientCommand(ClientFactory<ShellNativeClient> clientFactory, Console<PrintStream> console) {
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
                return clientFactory.newNodeClient();
            }
        }));
    }

    @SuppressWarnings("unused")
    public ShellNativeClient execute(final String clusterName) {
        return checkNull(executeWithProgress(new ExecutorWithProgress.ActionCallback<ShellNativeClient>() {
            @Override
            public ShellNativeClient execute() {
                return clientFactory.newNodeClient(clusterName);
            }
        }));
    }

    protected ShellNativeClient checkNull(ShellNativeClient shellNativeClient) {
        if (shellNativeClient == null) {
            console.println("No client created, check the provided cluster name!");
        }
        return shellNativeClient;
    }

    @Override
    public String help() {
        return HELP;
    }

    private static final String HELP = "Creates a new elasticsearch node client using the Java API\n" +
            "(http://www.elasticsearch.org/guide/reference/java-api/client.html)\n" +
            "\n" +
            "The following command with no arguments will create a new\n" +
            "node client connected to the cluster with name elasticsearch:\n" +
            "es = nodeClient(); \n\n" +
            "The following command with a string argument will create a new\n" +
            "node client connected to the cluster with name es-cluster:\n" +
            "es = nodeClient('es-cluster');\n";
}
