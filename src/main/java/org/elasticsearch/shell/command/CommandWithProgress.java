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
import org.elasticsearch.shell.console.Console;

import java.io.PrintStream;

/**
 * @author Luca Cavanna
 *
 * <code>Command</code> subclass that supports execution of long actions by providing a console feedback meanwhile
 */
public abstract class CommandWithProgress extends Command {

    protected CommandWithProgress(Console<PrintStream> console) {
        super(console);
    }

    /**
     * Helper method to execute an action providing a progressive feedback on the console
     * @param actionCallback the action to execute
     * @param <Result> the result of the execution
     * @return the result of the execution
     */
    protected <Result> Result executeWithProgress(ExecutorWithProgress.ActionCallback<Result> actionCallback) {
        console.print(initialMessage());
        return new ExecutorWithProgress<Result>(console, actionCallback).execute();
    }

    /**
     * Provides the initial message to print before starting an execution with progress
     * @return the initial message to print on the console
     */
    protected abstract String initialMessage();
}
