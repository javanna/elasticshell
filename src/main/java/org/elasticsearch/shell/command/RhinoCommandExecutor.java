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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Executes commands through the Rhino engine
 *
 * @author Luca Cavanna
 */
public final class RhinoCommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(RhinoCommandExecutor.class);

    private static final String EXECUTE_COMMAND_METHOD_NAME = "executeCommand";
    static final Method EXECUTE_COMMAND_METHOD;

    static {
        try {
            EXECUTE_COMMAND_METHOD = RhinoCommandExecutor.class.getMethod(
                    EXECUTE_COMMAND_METHOD_NAME, Context.class, Scriptable.class, Object[].class, Function.class);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Method " + EXECUTE_COMMAND_METHOD_NAME + " not found", e);
        }
    }

    private RhinoCommandExecutor() {

    }

    /**
     * Method used to run a command through the Rhino engine. Allows to have a single generic executeCommand
     * public static method (signature needed by Rhino) for all the commands.
     * The command function registered to the top-level contains a reference to the actual command to be run,
     * which is a {@link Command} object annotated with the {@link ExecutableCommand} annotation.
     * @param cx the Rhino context
     * @param thisObj the current scope
     * @param args The arguments provided when running the command as a javascript function
     * @param funObj The function invoked through the shell
     * @return the result of the command execution
     */
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
