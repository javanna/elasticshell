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
import org.elasticsearch.shell.RhinoShellTopLevel;
import org.elasticsearch.shell.ShellScope;
import org.mozilla.javascript.ScriptableObject;

import java.util.Set;

/**
 * Rhino specific command registrar
 *
 * @author Luca Cavanna
 */
public class RhinoCommandRegistrar extends AbstractCommandRegistrar<RhinoShellTopLevel> {

    @Inject
    RhinoCommandRegistrar(ShellScope<RhinoShellTopLevel> shellScope, Set<Command> commands) {
        super(shellScope, commands);
    }

    @Override
    protected void registerCommand(String name, Command command, ShellScope<RhinoShellTopLevel> shellScope) {
        RhinoCommandFunctionObject rhinoCommandFunctionObject = new RhinoCommandFunctionObject(name, command, RhinoCommandExecutor.EXECUTE_COMMAND_METHOD, shellScope.get());
        shellScope.get().defineProperty(name, rhinoCommandFunctionObject, ScriptableObject.DONTENUM);
    }
}
