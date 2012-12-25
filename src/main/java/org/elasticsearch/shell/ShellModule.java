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
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.inject.TypeLiteral;
import org.elasticsearch.common.inject.name.Names;
import org.elasticsearch.shell.console.Console;
import org.elasticsearch.shell.console.JLineConsole;
import org.elasticsearch.shell.console.JLineRhinoCompleter;
import org.elasticsearch.shell.script.RhinoScriptExecutor;
import org.elasticsearch.shell.script.ScriptExecutor;
import org.elasticsearch.shell.source.InputAnalyzer;
import org.elasticsearch.shell.source.RhinoInputAnalyzer;

import java.io.InputStream;
import java.io.PrintStream;

public class ShellModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("appName")).toInstance("elasticsearch-shell");
        bind(InputStream.class).annotatedWith(Names.named("shellInput")).toInstance(System.in);
        bind(PrintStream.class).annotatedWith(Names.named("shellOutput")).toInstance(new PrintStream(System.out, true));
        bind(ShutdownHook.class).asEagerSingleton();

        //JLine bindings
        bind(Console.class).to(JLineConsole.class).asEagerSingleton();
        bind(Completer.class).to(JLineRhinoCompleter.class).asEagerSingleton();

        //Rhino bindings
        bind(new TypeLiteral<ShellScope<RhinoShellTopLevel>>(){}).to(RhinoShellScope.class).asEagerSingleton();
        //bind(ScriptableObject.class).annotatedWith(Names.named("shellScope")).to(RhinoShellTopLevel.class).asEagerSingleton();
        bind(ScriptExecutor.class).to(RhinoScriptExecutor.class).asEagerSingleton();
        bind(InputAnalyzer.class).to(RhinoInputAnalyzer.class).asEagerSingleton();
        bind(Shell.class).to(RhinoShell.class).asEagerSingleton();
    }
}
