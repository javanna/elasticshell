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
package org.elasticsearch.shell.jline;


import jline.console.ConsoleReader;
import org.elasticsearch.shell.*;

import java.io.*;

public class JLineConsole implements org.elasticsearch.shell.Console {

    private final ConsoleReader reader;
    private final PrintStream out;

    public JLineConsole(String appName, InputStream in, PrintStream out) throws IOException {
        this.out = out;
        this.reader = new ConsoleReader(appName, in, out, null);
        reader.setBellEnabled(false);
    }

    public void print(String message) {
        out.print(message);
    }

    public void println() {
        out.println();
    }

    public void println(String message) {
        out.println(message);
    }

    public String readLine(String prompt) throws Exception {
        String line = reader.readLine(prompt);
        return line;
    }

    public PrintStream getOut() {
        return out;
    }
}
