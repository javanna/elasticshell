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


import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.name.Named;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * @author Luca Cavanna
 *
 * <code>Console</code> implementation that depends on JLine for the input.
 */
public class JLineConsole extends AbstractConsole {

    private final ConsoleReader reader;

    /**
     * Constructor for the JLineConsole
     * @param appName the application name
     * @param in the <code>InputStream</code> to read input from
     * @param out the <code>PrintStream</code> to print output to
     * @param completerHolder holds an optional JLine completer to auto-complete commands
     */
    @Inject
    JLineConsole(@Named("appName") String appName,
                 @Named("shellInput") InputStream in, @Named("shellOutput") PrintStream out,
                 CompleterHolder completerHolder) {
        super(out);
        try {
            this.reader = new ConsoleReader(appName, in, out, null);
            reader.setBellEnabled(false);
            if (completerHolder.completer != null) {
                reader.addCompleter(completerHolder.completer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class CompleterHolder {
        @Inject(optional=true) Completer completer;
    }

    /**
     * Reads a line using the JLine {@link ConsoleReader}
     * @param prompt the prompt to be printed
     * @return the line read
     * @throws IOException in case of problems while reading the line
     */
    public String readLine(String prompt) throws IOException {
        return reader.readLine(prompt);
    }
}
