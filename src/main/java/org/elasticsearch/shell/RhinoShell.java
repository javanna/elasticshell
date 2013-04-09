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


import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.shell.client.ClientFactory;
import org.elasticsearch.shell.client.RhinoClientNativeJavaObject;
import org.elasticsearch.shell.command.ScriptLoader;
import org.elasticsearch.shell.console.Console;
import org.elasticsearch.shell.node.NodeFactory;
import org.elasticsearch.shell.scheduler.Scheduler;
import org.elasticsearch.shell.script.ScriptExecutor;
import org.elasticsearch.shell.source.CompilableSource;
import org.elasticsearch.shell.source.CompilableSourceReader;
import org.mozilla.javascript.*;

import java.io.PrintStream;

/**
 * Rhino implementation of the {@link Shell}
 * Uses the basic functionality provided by the {@link BasicShell}. Adds the Rhino {@link Context} initialization
 * and the Rhino specific operations needed.
 *
 * @author Luca Cavanna
 */
public class RhinoShell extends BasicShell<RhinoClientNativeJavaObject, NativeObject, Object> {

    private final ShellScope<RhinoShellTopLevel> rhinoShellScope;

    @Inject
    RhinoShell(Console<PrintStream> console, CompilableSourceReader compilableSourceReader,
               ScriptExecutor scriptExecutor, Unwrapper unwrapper, ShellScope<RhinoShellTopLevel> shellScope,
               ClientFactory<RhinoClientNativeJavaObject> clientFactory,
               NodeFactory<RhinoClientNativeJavaObject, NativeObject, Object> nodeFactory,
               ScriptLoader scriptLoader, Scheduler scheduler, ShellSettings shellSettings) {

        super(console, compilableSourceReader, scriptExecutor, unwrapper, shellScope,
                clientFactory, nodeFactory, scriptLoader, scheduler, shellSettings);

        this.rhinoShellScope = shellScope;
    }

    @Override
    protected void init() {
        super.init();
        Context context = Context.enter();
        context.setErrorReporter(new RhinoErrorReporter(false, console.out()));
        context.setWrapFactory(new RhinoCustomWrapFactory());
    }

    @Override
    protected String getPrompt() {
        Object promptObject = rhinoShellScope.get().get(ShellSettings.PROMPT_MESSAGE, rhinoShellScope.get());

        if (promptObject != null
                && !promptObject.equals(UniqueTag.NOT_FOUND)
                && !promptObject.equals(UniqueTag.NULL_VALUE)) {

            Object result;

            if (promptObject instanceof Function) {
                result = scriptExecutor.execute(new CompilableSource(ShellSettings.PROMPT_MESSAGE + "()", 1));
            } else {
                result = promptObject;
            }

            return javaToString(unwrap(result));
        }

        return super.getPrompt();
    }

    @Override
    protected void close() {
        super.close();
        Context.exit();
    }
}
