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
package org.elasticsearch.shell.rhino;

import org.elasticsearch.shell.CompilableSourceReader;
import org.elasticsearch.shell.Console;
import org.elasticsearch.shell.ScriptExecutor;
import org.elasticsearch.shell.Shell;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.ToolErrorReporter;

public class RhinoShell extends Shell {

    private final ContextFactory contextFactory;

    public RhinoShell(Console console) {
        super(console);
        this.contextFactory = new ShellContextFactory(new ToolErrorReporter(false, console.getOut()));
    }

    public void run() {
        contextFactory.call(new ContextAction() {
            @Override
            public Object run(Context context) {
                //TODO add top level scope
                ScriptableObject scope = context.initStandardObjects();
                CompilableSourceReader compilableSourceReader = new RhinoCompilableSourceReader(console, context);
                ScriptExecutor scriptExecutor = new RhinoScriptExecutor(context, scope);
                RhinoShell.this.run(compilableSourceReader, scriptExecutor);
                return null;
            }
        });
    }
}
