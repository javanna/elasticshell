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

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.shell.ScriptExecutor;
import org.elasticsearch.shell.Shell;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.ToolErrorReporter;

public class RhinoShellModule extends AbstractModule {

    @Override
    protected void configure() {

        ContextFactory contextFactory = new ShellContextFactory(new ToolErrorReporter(false, System.out));
        bind(ContextFactory.class).toInstance(contextFactory);

        contextFactory.call(new ContextAction() {
            @Override
            public Object run(Context context) {
                RhinoShellModule.this.bind(Context.class).toInstance(context);
                Scriptable scope = context.initStandardObjects(new ShellTopLevel(System.out, context));
                RhinoShellModule.this.bind(Scriptable.class).toInstance(scope);
                return null;
            }
        });

        bind(ScriptExecutor.class).to(RhinoScriptExecutor.class);

        bind(Shell.class).to(RhinoShell.class);
    }
}
