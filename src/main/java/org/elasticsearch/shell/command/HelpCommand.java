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
import org.elasticsearch.shell.console.Console;

import java.io.PrintStream;

/**
 * Command that allows to display a help text
 *
 * @author Luca Cavanna
 */
@ExecutableCommand(aliases = "help")
public class HelpCommand extends Command {

    @Inject
    protected HelpCommand(Console<PrintStream> console) {
        super(console);
    }

    @SuppressWarnings("unused")
    public String execute() {
        return help();
    }

    @Override
    public String help() {
        return HELP;
    }

    private static final String HELP = "This is the elasticshell, a  shell for elasticsearch\n" +
            "For information about elasticsearch, visit: http://elasticsearch.org\n" +
            "\n" +
            "You can either write Java or JavaScript code in here.\n" +
            "Every command is exposed as a JavaScript function. In order to get help\n" +
            "for a specific function, just type its name (without the brackets)!\n" +
            "\n" +
            "The elasticshell has nice auto-suggestions, available pressing the tab key\n" +
            "which will show as a result a list of all the commands and objects that " +
            "are available in the current context.\n" +
            "\n" +
            "The following are the available commands (with example arguments):\n" +
            "   exit() or quit()                   Quits the elasticshell\n" +
            "   help()                             Display this help message\n" +
            "   importClass(java.util.Date)        Imports a Java class\n" +
            "   importPackage(java.util)           Imports a Java package\n" +
            "   load('./scripts/elastic.js')       Loads external Javascript source files\n" +
            "   nodeClient('elasticsearch')        Creates a new node client given\n" +
            "                                      the cluster name to connect to\n" +
            "   print(es)                          Prints out the string representation\n" +
            "                                      of the provided arguments\n" +
            "   transportClient('localhost:9300')  Creates a new transport client given\n" +
            "                                      the address of the node to connect to\n" +
            "   version()                          Prints out the current elasticsearch version";
}
