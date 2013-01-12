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
package org.elasticsearch.shell;

import org.elasticsearch.shell.console.Console;

import java.io.PrintStream;

/**
 * @author Luca Cavanna
 *
 * Allows to execute an action giving a progressive feedback through the console
 * The action to execute is given through the inner ActionCallback class
 * @param <Result> the object returned as a result of the execution of the action
 */
public class ExecutorWithProgress<Result> {

    final Console<PrintStream> console;
    final ActionCallback<Result> actionCallback;

    /**
     * Creates a new instance of the <code>ExecutorWithProgress</code> class given the console where
     * it needs to print feedback and the action to execute
     * @param console the console
     * @param actionCallback the action to execute
     */
    public ExecutorWithProgress(Console<PrintStream> console, ActionCallback<Result> actionCallback) {
        this.console = console;
        this.actionCallback = actionCallback;
    }

    /**
     * Executes the action and displays a progressive feedback on the console in the meantime,
     * till the execution has been completed
     * @return the result coming from the execution of the action
     */
    public Result execute() {
        ProgressThread progressThread = new ProgressThread();
        progressThread.start();
        try {
            return actionCallback.execute();
        } finally {
            progressThread.end();
            console.println();
        }
    }

    /**
     * Thread that runs concurrently to the action to give progressive feedback
     */
    private class ProgressThread extends Thread {
        private volatile boolean stopped = false;

        public void end() {
            this.stopped = true;
        }

        @Override
        public void run() {
            while(!stopped) {
                console.print(".");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                }
            }
        }
    }

    /**
     * Represents an action to execute
     * @param <Result> the object that the execution returns
     */
    public interface ActionCallback<Result> {
        Result execute();
    }
}
