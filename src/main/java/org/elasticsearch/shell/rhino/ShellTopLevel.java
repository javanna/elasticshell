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
import org.elasticsearch.common.inject.Singleton;
import org.elasticsearch.common.inject.name.Named;
import org.elasticsearch.shell.command.Command;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.ToolErrorReporter;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

@Singleton
public class ShellTopLevel extends ImporterTopLevel {

    private final PrintStream out;
    private final Map<String, Object> commands;

    @Inject
    ShellTopLevel(@Named("shellOutput") PrintStream out, @Named("commands") Map<String, Object> commands){

        this.out = out;
        this.commands = commands;

        //TODO default imports (check mvel)

        Context context = Context.enter();
        try {
            initStandardObjects(context, true);
        } finally {
            Context.exit();
        }

        //TODO Context.javaToJS ???
        //defineProperty("h", new Test(), ScriptableObject.DONTENUM);

        defineFunctionProperties("executeCommand", commands.keySet(), ScriptableObject.DONTENUM);

    }

    private void defineFunctionProperties(String staticMethodName, Set<String> commandNames, int attributes) {
        Method method;
        try {
            try {
                method = getClass().getDeclaredMethod(staticMethodName, Context.class, Scriptable.class, Object[].class, Function.class);
            } catch(SecurityException e) {
                method = getClass().getMethod(staticMethodName, Context.class, Scriptable.class, Object[].class, Function.class);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method " + staticMethodName + " not found", e);
        }

        for (String commandName : commandNames) {
            FunctionObject f = new FunctionObject(commandName, method, this);
            defineProperty(commandName, f, attributes);
        }
    }

    @SuppressWarnings("unused")
    public static Object executeCommand(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        if (funObj instanceof FunctionObject) {
            ShellTopLevel shellTopLevel = getInstance(funObj);
            String functionName = ((FunctionObject)funObj).getFunctionName();
            Object command = shellTopLevel.commands.get(functionName);
            Command annotation = command.getClass().getAnnotation(Command.class);
            Callable callable = ScriptRuntime.getPropFunctionAndThis(command, annotation.method(), cx, shellTopLevel);
            return callable.call(cx, shellTopLevel, ScriptRuntime.lastStoredScriptable(cx), args);
        }
        throw new RuntimeException("Unable to determine the command to run");
    }

    private static ShellTopLevel getInstance(Function function)
    {
        Scriptable scope = function.getParentScope();
        if (!(scope instanceof ShellTopLevel))
            throw reportRuntimeError("msg.bad.shell.function.scope",
                    String.valueOf(scope));
        return (ShellTopLevel)scope;
    }

    static RuntimeException reportRuntimeError(String msgId, String msgArg)
    {
        String message = ToolErrorReporter.getMessage(msgId, msgArg);
        return Context.reportRuntimeError(message);
    }
}
