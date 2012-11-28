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

import org.elasticsearch.shell.Console;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.ToolErrorReporter;

import java.io.PrintStream;

public class ShellTopLevel extends ImporterTopLevel {

    private final PrintStream out;

    public ShellTopLevel(PrintStream out, Context context){

        this.out = out;

        //TODO sealed? what?
        //initStandardObjects(context, true);

        //default imports (check mvel)

        defineFunctionProperties(new String[]{"help"}, getClass(), ScriptableObject.DONTENUM);

        defineProperty("test", new Object() {
            @Override
            public String toString() {
                return "ciao";
            }
        }, ScriptableObject.DONTENUM);

    }

    public static void help(Context cx, Scriptable thisObj,
                            Object[] args, Function funObj) {

        //TODO add our help message instead of the rhino console message
        getInstance(funObj).out.println(ToolErrorReporter.getMessage("msg.help"));
    }

    private static ShellTopLevel getInstance(Function function)
    {
        Scriptable scope = function.getParentScope();
        if (!(scope instanceof ShellTopLevel))
            throw reportRuntimeError("msg.bad.shell.function.scope",
                    String.valueOf(scope));
        return (ShellTopLevel)scope;
    }

    static RuntimeException reportRuntimeError(String msgId) {
        String message = ToolErrorReporter.getMessage(msgId);
        return Context.reportRuntimeError(message);
    }

    static RuntimeException reportRuntimeError(String msgId, String msgArg)
    {
        String message = ToolErrorReporter.getMessage(msgId, msgArg);
        return Context.reportRuntimeError(message);
    }
}
