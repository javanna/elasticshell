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
import org.elasticsearch.shell.Unwrapper;
import org.elasticsearch.shell.console.Console;

import java.io.PrintStream;

/**
 * Command that allows to print out to the console its arguments
 *
 * @author Luca Cavanna
 */
@ExecutableCommand(aliases = {"print"})
public class PrintCommand extends Command {

    private final Unwrapper unwrapper;

    @Inject
    protected PrintCommand(Console<PrintStream> console, Unwrapper unwrapper) {
        super(console);
        this.unwrapper = unwrapper;
    }

    @SuppressWarnings("unused")
    public void execute(Object arg) {
        console.print(unwrap(arg));
        console.println();
    }

    @SuppressWarnings("unused")
    public void execute(Object[] args) {
        for (int i=0; i < args.length; i++) {
            if (i > 0) {
                console.print(" ");
            }
            console.print(unwrap(args[i]));
        }
        console.println();
    }

    protected String unwrap(Object arg) {
        Object unwrappedArg = unwrapper.unwrap(arg);
        if (unwrappedArg != null) {
            return unwrappedArg.toString();
        }
        return null;
    }
}
