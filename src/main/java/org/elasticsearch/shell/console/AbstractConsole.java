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

import java.io.PrintStream;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract representation of the {@link Console} that wraps a {@link PrintStream} for the output messages
 *
 * @author Luca Cavanna
 */
public abstract class AbstractConsole implements Console<PrintStream> {

    private final Logger logger = LoggerFactory.getLogger(AbstractConsole.class);

    private final PrintStream out;

    protected AbstractConsole(PrintStream out) {
        this.out = out;
        AnsiConsole.systemInstall();
    }

    @Override
    public void print(String message) {
        logger.debug("print: {}", message);
        out.print(Ansi.ansi().render(message));
    }

    @Override
    public void println() {
        logger.debug("println");
        out.println();
    }

    @Override
    public void println(String message) {
        logger.debug("println: {}", message);
        out.println(Ansi.ansi().render(message));
    }

    @Override
    public PrintStream out() {
        return out;
    }

    @Override
    public void shutdown() {
        AnsiConsole.systemUninstall();
    }
}
