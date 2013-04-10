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
package org.elasticsearch.shell.console;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Console abstraction used to read and write from the shell
 *
 * @author Luca Cavanna
 */
public interface Console<Output> {

    /**
     * Prints a String
     * @see PrintStream#print(String)
     * @param message The <code>String</code> to be printed
     */
    void print(String message);

    /**
     * Terminates the current line
     * @see PrintStream#println()
     */
    void println();

    /**
     * Prints a String and then terminate the line
     * @see PrintStream#println(String)
     * @param message The <code>String</code> to be printed
     */
    void println(String message);

    /**
     * Returns the underlying output channel
     * @return the underlying output channel
     */
    Output out();

    /**
     * Reads a line from the input channel after printing out the desired prompt
     * @param prompt the prompt to be printed
     * @return the line read
     * @throws IOException in case of problems while reading the input
     */
    String readLine(String prompt) throws IOException;

    /**
     * Flushes the history if necessary
     */
    void flushHistory();
}
