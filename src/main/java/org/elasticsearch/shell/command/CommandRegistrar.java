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

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.Singleton;
import org.elasticsearch.common.inject.name.Named;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.lang.reflect.Method;
import java.util.Set;

@Singleton
public class CommandRegistrar {

    private final Scriptable scope;

    @Inject
    CommandRegistrar(@Named("shellScope") ScriptableObject scope, Set<Command> commands) {
        this.scope = scope;

        for (Command command : commands) {
            ExecutableCommand annotation = command.getClass().getAnnotation(ExecutableCommand.class);
            if (annotation != null) {
                //TODO throw some exception instead of silently fail registering the commands ?
                for (Method method : command.getClass().getMethods()) {
                    if (method.getName().equals(annotation.executeMethod())) {
                        for (String alias : annotation.aliases()) {
                            CommandFunctionObject commandFunctionObject = new CommandFunctionObject(alias, command, CommandExecutor.EXECUTE_COMMAND_METHOD, scope);
                            scope.defineProperty(alias, commandFunctionObject, ScriptableObject.DONTENUM);
                            command.setScope(scope);
                        }
                        break;
                    }
                }
            }
        }

    }
}
