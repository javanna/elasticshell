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

import org.elasticsearch.shell.ShellScope;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Generic abstract class that registers a given set of commands to the shell scope object
 *
 * @author Luca Cavanna
 */
public abstract class AbstractCommandRegistrar<Scope> {

    AbstractCommandRegistrar(ShellScope<Scope> shellScope, Set<Command> commands) {
        for (Command command : commands) {
            ExecutableCommand annotation = command.getClass().getAnnotation(ExecutableCommand.class);
            if (annotation != null) {
                for (Method method : command.getClass().getMethods()) {
                    if (method.getName().equals(annotation.executeMethod())) {
                        for (String alias : annotation.aliases()) {
                            registerCommand(alias, command, shellScope);
                            //command.setShellScope(shellScope);
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Register a given command with the given name in the given shell scope, that depends on the script engine
     * @param name the name used to register the command
     * @param command the command that needs to be registered
     * @param shellScope the current shell scope that wraps the real scope that depends on the script engine in use
     */
    protected abstract void registerCommand(String name, Command command, ShellScope<Scope> shellScope);
}
