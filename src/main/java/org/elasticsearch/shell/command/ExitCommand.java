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
import org.elasticsearch.shell.ExitSignal;
import org.elasticsearch.shell.console.Console;

import java.io.PrintStream;

/**
 * Command that allows to quit the shell
 *
 * @author Luca Cavanna
 */
@ExecutableCommand(aliases = {"exit", "quit"})
public class ExitCommand extends Command {

    @Inject
    protected ExitCommand(Console<PrintStream> console) {
        super(console);
    }

    @SuppressWarnings("unused")
    public ExitSignal execute() {
        return new ExitSignal();
    }

    private static final String HELP = "Quits the elasticshell\n";

    @Override
    public String help() {
        return HELP;
    }
}
