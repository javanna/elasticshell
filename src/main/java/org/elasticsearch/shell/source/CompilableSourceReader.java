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
package org.elasticsearch.shell.source;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.shell.console.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

/**
 * Reader that is able to detect whether a source is compilable or wait for eventual more input
 * in order to complete the source snippet
 *
 * @author Luca Cavanna
 */
public class CompilableSourceReader {

    private static final Logger logger = LoggerFactory.getLogger(CompilableSourceReader.class);

    private final Console<PrintStream> console;
    private final InputAnalyzer inputAnalyzer;

    /**
     * Creates the source reader given the console to read from and the input analyzer
     * @param console the console to read from
     * @param inputAnalyzer analyzer that determines whether a source is compilable or not
     */
    @Inject
    public CompilableSourceReader(Console<PrintStream> console, InputAnalyzer inputAnalyzer) {
        this.console = console;
        this.inputAnalyzer = inputAnalyzer;
    }

    /**
     * Reads the input from the console and returns the input compilable source
     * @return the compilable source taken from the console as soon as it is compilable
     * @throws Exception
     */
    public CompilableSource read() throws Exception {

        boolean previousLineWasEmpty = false;
        int lineNumber = 0;
        String source = "";

        while(true) {
            String line = console.readLine(lineNumber == 0 ? "> " : "... ");
            source = source + line + "\n";
            lineNumber++;

            if (inputAnalyzer.isCompilable(source)) {
                logger.debug("Source {} is compilable", source);
                break;
            }

            if (line.length() == 0) {
                if (previousLineWasEmpty) {
                    return null;
                }
                previousLineWasEmpty = true;
            }
            logger.debug("Source {} isn't compilable, waiting for more input", source);
        }

        return new CompilableSource(source, lineNumber);
    }
}
