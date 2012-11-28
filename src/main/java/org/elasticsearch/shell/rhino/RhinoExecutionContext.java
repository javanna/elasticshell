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

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.shell.ExecutionContext;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

public class RhinoExecutionContext implements ExecutionContext {

    private final Context context;
    private final Scriptable scope;
    private final ContextFactory contextFactory;

    @Inject
    public RhinoExecutionContext(ContextFactory contextFactory, Context context, Scriptable scope) {
        this.context = context;
        this.scope = scope;
        this.contextFactory = contextFactory;
    }

    public Context getContext() {
        return context;
    }

    public Scriptable getScope() {
        return scope;
    }

    @Override
    public boolean isCompilable(String source) {
        return context.stringIsCompilableUnit(source);
    }

    public void run(ContextAction contextAction) {
        contextFactory.call(contextAction);
    }
}
