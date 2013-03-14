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
package org.elasticsearch.shell;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.inject.TypeLiteral;
import org.elasticsearch.shell.client.*;
import org.elasticsearch.shell.command.RhinoScriptLoader;
import org.elasticsearch.shell.command.ScriptLoader;
import org.elasticsearch.shell.json.JsonToString;
import org.elasticsearch.shell.json.RhinoJsonToString;
import org.elasticsearch.shell.json.RhinoStringToJson;
import org.elasticsearch.shell.json.StringToJson;
import org.elasticsearch.shell.node.DefaultNodeFactory;
import org.elasticsearch.shell.node.NodeFactory;
import org.elasticsearch.shell.script.RhinoScriptExecutor;
import org.elasticsearch.shell.script.ScriptExecutor;
import org.elasticsearch.shell.source.InputAnalyzer;
import org.elasticsearch.shell.source.RhinoInputAnalyzer;
import org.mozilla.javascript.NativeObject;

/**
 * Module that binds all the objects that depend on Rhino
 *
 * @author Luca Cavanna
 */
public class RhinoShellModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ScriptLoader.class).to(RhinoScriptLoader.class).asEagerSingleton();
        bind(new TypeLiteral<JsonToString<NativeObject>>() {}).to(RhinoJsonToString.class).asEagerSingleton();
        bind(new TypeLiteral<StringToJson<Object>>(){}).to(RhinoStringToJson.class).asEagerSingleton();
        bind(Unwrapper.class).to(RhinoUnwrapper.class).asEagerSingleton();
        bind(new TypeLiteral<ShellScope<RhinoShellTopLevel>>(){}).to(RhinoShellScope.class).asEagerSingleton();
        bind(ScriptExecutor.class).to(RhinoScriptExecutor.class).asEagerSingleton();
        bind(InputAnalyzer.class).to(RhinoInputAnalyzer.class).asEagerSingleton();
        bind(Shell.class).to(RhinoShell.class).asEagerSingleton();

        bind(new TypeLiteral<ClientScopeSynchronizerFactory<RhinoClientNativeJavaObject>>(){})
                .to(RhinoClientScopeSynchronizerFactory.class).asEagerSingleton();

        bind(new TypeLiteral<ClientWrapper<RhinoClientNativeJavaObject, NativeObject, Object>>(){})
                .to(RhinoClientWrapper.class).asEagerSingleton();

        bind(new TypeLiteral<ClientFactory<RhinoClientNativeJavaObject>>() {})
                .to(new TypeLiteral<DefaultClientFactory<RhinoClientNativeJavaObject, NativeObject, Object>>() {})
                .asEagerSingleton();

        bind(new TypeLiteral<NodeFactory<RhinoClientNativeJavaObject, NativeObject, Object>>(){})
                .to(new TypeLiteral<DefaultNodeFactory<RhinoClientNativeJavaObject, NativeObject, Object>>() {})
                .asEagerSingleton();
    }
}
