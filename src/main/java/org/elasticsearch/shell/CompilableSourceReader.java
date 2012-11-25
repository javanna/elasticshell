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

public abstract class CompilableSourceReader {

    private final Console console;

    public CompilableSourceReader(Console console) {
        this.console = console;
    }

    public CompilableSource read() {

        boolean previousLineWasEmpty = false;
        int lineNumber = 0;
        String source = "";

        while(true) {
            String line;
            try {
                line = console.readLine(lineNumber == 0 ? "> " : "... ");
            } catch (Exception e) {
                console.println(e.toString());
                return null;
            }

            source = source + line + "\n";
            lineNumber++;

            if (isCompilable(source)) {
                break;
            }

            if (line.length() == 0) {
                if (previousLineWasEmpty) {
                    return null;
                }
                previousLineWasEmpty = true;
            }
        }

        return new CompilableSource(source, lineNumber);
    }

    public abstract boolean isCompilable(String source);

}
