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
import org.elasticsearch.shell.console.Console;
import org.elasticsearch.shell.scheduler.Scheduler;
import org.elasticsearch.shell.script.ScriptExecutor;
import org.elasticsearch.shell.source.CompilableSourceReader;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoCustomWrapFactory;

import java.io.PrintStream;

/**
 * Rhino implementation of the {@link Shell}
 * Uses the basic functionality provided by the {@link BasicShell}. Adds the Rhino {@link Context} initialization
 * and the Rhino specific operations needed.
 *
 * @author Luca Cavanna
 */
public class RhinoShell extends BasicShell<RhinoClientNativeJavaObject> {
    @Inject
    RhinoShell(Console<PrintStream> console, CompilableSourceReader compilableSourceReader,
               ScriptExecutor scriptExecutor, Unwrapper unwrapper, ShellScope<RhinoShellTopLevel> shellScope,
               ClientFactory<RhinoClientNativeJavaObject> clientFactory, SchedulerHolder schedulerHolder) {
        super(console, compilableSourceReader, scriptExecutor, unwrapper, shellScope, clientFactory, schedulerHolder.scheduler);
    }

    static class SchedulerHolder {
        @Inject(optional = true)
        Scheduler scheduler;
    }

    @Override
    protected void init() {
        Context context = Context.enter();
        context.setErrorReporter(new RhinoErrorReporter(false, console.out()));
        context.setWrapFactory(new RhinoCustomWrapFactory());
    }

    @Override
    protected void close() {
        Context.exit();
    }
}
