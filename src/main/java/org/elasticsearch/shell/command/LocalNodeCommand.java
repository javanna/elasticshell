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
import org.elasticsearch.shell.console.Console;
import org.elasticsearch.shell.node.Node;
import org.elasticsearch.shell.node.NodeFactory;

import java.io.PrintStream;

/**
 * @author Luca Cavanna
 *
 * Commands that allows to create a new node within the shell
 * @param <ShellNativeClient> the shell native class used to represent a client within the shell
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
@ExecutableCommand(aliases = {"localNode"})
public class LocalNodeCommand<ShellNativeClient, JsonInput, JsonOutput> extends CommandWithProgress {

    private static final String INITIAL_MESSAGE = "Creating new local node";

    private final NodeFactory<ShellNativeClient, JsonInput, JsonOutput> nodeFactory;

    @Inject
    LocalNodeCommand(Console<PrintStream> console, NodeFactory<ShellNativeClient, JsonInput, JsonOutput> nodeFactory) {
        super(console);
        this.nodeFactory = nodeFactory;
    }

    @SuppressWarnings("unused")
    public Node<ShellNativeClient, JsonInput, JsonOutput> execute() {
        return checkNull(executeWithProgress(new ExecutorWithProgress.ActionCallback<Node<ShellNativeClient, JsonInput, JsonOutput>>() {
            @Override
            public Node<ShellNativeClient, JsonInput, JsonOutput> execute() {
                return nodeFactory.newLocalNode();
            }
        }));
    }

    @SuppressWarnings("unused")
    public Node<ShellNativeClient, JsonInput, JsonOutput> execute(final String clusterName) {
        return checkNull(executeWithProgress(new ExecutorWithProgress.ActionCallback<Node<ShellNativeClient, JsonInput, JsonOutput>>() {
            @Override
            public Node<ShellNativeClient, JsonInput, JsonOutput> execute() {
                return nodeFactory.newLocalNode(clusterName);
            }
        }));
    }

    protected Node<ShellNativeClient, JsonInput, JsonOutput> checkNull(Node<ShellNativeClient, JsonInput, JsonOutput> shellNode) {
        if (shellNode == null) {
            console.println("No node created!");
        }
        return shellNode;
    }

    @Override
    protected String initialMessage() {
        return INITIAL_MESSAGE;
    }
}
