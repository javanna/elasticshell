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


import org.elasticsearch.shell.CompilableSource;
import org.elasticsearch.shell.ScriptExecutor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.tools.ToolErrorReporter;

public class RhinoScriptExecutor implements ScriptExecutor<RhinoExecutionContext> {

    public String execute(CompilableSource source, RhinoExecutionContext executionContext) {
        try {
            Script script = compile(source, executionContext);
            if (script != null) {
                Object result = script.exec(executionContext.getContext(), executionContext.getScope());
                //Avoids printing out undefined
                if (result != Context.getUndefinedValue()) {
                    return convertScriptResult(result);
                }
            }
        } catch(RhinoException rex) {
            ToolErrorReporter.reportException(executionContext.getContext().getErrorReporter(), rex);

        } catch(VirtualMachineError ex) {
            String msg = ToolErrorReporter.getMessage("msg.uncaughtJSException", ex.toString());
            Context.reportError(msg);
        }
        return null;
    }

    private Script compile(CompilableSource source, RhinoExecutionContext executionContext) {
        return executionContext.getContext().compileString(source.getSource(), null, source.getLineNumbers(), null);
    }

    private String convertScriptResult(Object result) {
        //TODO convert objects properly
        return Context.toString(result);
    }

}
