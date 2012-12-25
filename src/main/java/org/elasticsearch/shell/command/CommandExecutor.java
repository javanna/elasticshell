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
package org.elasticsearch.shell.command;

import org.mozilla.javascript.*;

import java.lang.reflect.Method;

public final class CommandExecutor {

    private static final String EXECUTE_COMMAND_METHOD_NAME = "executeCommand";
    static final Method EXECUTE_COMMAND_METHOD;

    static {
        Method method;
        try {
            try {
                method = CommandExecutor.class.getDeclaredMethod(EXECUTE_COMMAND_METHOD_NAME, Context.class, Scriptable.class, Object[].class, Function.class);
            } catch(SecurityException e) {
                method = CommandExecutor.class.getMethod(EXECUTE_COMMAND_METHOD_NAME, Context.class, Scriptable.class, Object[].class, Function.class);
            }
            EXECUTE_COMMAND_METHOD = method;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method " + EXECUTE_COMMAND_METHOD_NAME + " not found", e);
        }
    }

    private CommandExecutor() {

    }

    @SuppressWarnings("unused")
    public static Object executeCommand(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        if (funObj instanceof RhinoCommandFunctionObject) {
            RhinoCommandFunctionObject rhinoCommandFunctionObject = (RhinoCommandFunctionObject) funObj;
            Scriptable parentScope = rhinoCommandFunctionObject.getParentScope();
            Command command = rhinoCommandFunctionObject.getCommand();
            ExecutableCommand annotation = command.getClass().getAnnotation(ExecutableCommand.class);
            Callable callable = ScriptRuntime.getPropFunctionAndThis(command, annotation.executeMethod(), cx, parentScope);
            return callable.call(cx, parentScope, ScriptRuntime.lastStoredScriptable(cx), args);
        }
        throw new RuntimeException("Unable to determine the command to run");
    }
}
