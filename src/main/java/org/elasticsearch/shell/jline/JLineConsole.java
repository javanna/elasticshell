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
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.Singleton;
import org.elasticsearch.common.inject.name.Named;
import org.elasticsearch.shell.Console;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

@Singleton
public class JLineConsole implements Console {

    private final ConsoleReader reader;
    private final PrintStream out;

    @Inject
    JLineConsole(@Named("appName") String appName, @Named("shellInput") InputStream in, @Named("shellOutput") PrintStream out) {
        this.out = out;
        try {
            this.reader = new ConsoleReader(appName, in, out, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return reader.readLine(prompt);
    }

    public PrintStream getOut() {
        return out;
    }
}
