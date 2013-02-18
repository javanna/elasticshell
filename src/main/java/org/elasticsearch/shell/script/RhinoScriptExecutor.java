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
package org.elasticsearch.shell.script;


import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.shell.RhinoShellTopLevel;
import org.elasticsearch.shell.ShellScope;
import org.elasticsearch.shell.source.CompilableSource;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rhino executor for a compilable source
 *
 * @author Luca Cavanna
 */
public class RhinoScriptExecutor implements ScriptExecutor {

    private static final Logger logger = LoggerFactory.getLogger(RhinoScriptExecutor.class);

    private final ShellScope<RhinoShellTopLevel> shellScope;

    @Inject
    RhinoScriptExecutor(ShellScope<RhinoShellTopLevel> shellScope) {
        this.shellScope = shellScope;
    }

    public Object execute(CompilableSource source) {
        try {
            logger.debug("Compiling source {}", source.getSource());
            Script script = compile(source);
            if (script != null) {
                logger.debug("Executing compiled script");
                Object result = script.exec(Context.getCurrentContext(), shellScope.get());
                logger.debug("Returned object [{}]", result);
                if(result instanceof Undefined) {
                    return null;
                }
                return result;
            }
        } catch(RhinoException rex) {
            logger.error(rex.getMessage(), rex);
            Context.reportError(rex.details(), null, rex.lineNumber(), rex.lineSource(), rex.columnNumber());
        } catch(VirtualMachineError ex) {
            logger.error(ex.getMessage(), ex);
            String msg = ToolErrorReporter.getMessage("msg.uncaughtJSException", ex.toString());
            Context.reportError(msg);
        }
        return null;
    }

    private Script compile(CompilableSource source) {
        return Context.getCurrentContext().compileString(source.getSource(), null, source.getLineNumbers(), null);
    }
}
