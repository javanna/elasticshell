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

import jline.console.completer.Completer;
import jline.console.completer.CompletionHandler;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.inject.TypeLiteral;
import org.elasticsearch.shell.console.Console;
import org.elasticsearch.shell.console.JLineConsole;
import org.elasticsearch.shell.console.completer.JLineCompletionHandler;
import org.elasticsearch.shell.console.completer.JLineRhinoCompleter;

import java.io.PrintStream;

/**
 * Module that binds all the objects that depend on JLine
 *
 * @author Luca Cavanna
 */
public class JLineModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(new TypeLiteral<Console<PrintStream>>(){}).to(JLineConsole.class).asEagerSingleton();
        bind(Completer.class).to(JLineRhinoCompleter.class).asEagerSingleton();
        bind(CompletionHandler.class).to(JLineCompletionHandler.class).asEagerSingleton();
    }
}
