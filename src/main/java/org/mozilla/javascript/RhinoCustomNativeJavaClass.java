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
package org.mozilla.javascript;

/**
 * @author Luca Cavanna
 *
 * Custom {@link NativeJavaClass}. The goal is to get access to some {@link NativeJavaMethod} properties that have
 * package visibility for better help and auto-completion.
 * We could redefine entirely how a java object gets wrapped, in order to have our own method representation,
 * but that is not easily customizable (too many static methods involved).
 * What we can do is customize what our {@link NativeJavaClass} returns when a method is requested through the get method
 */
public class RhinoCustomNativeJavaClass extends NativeJavaClass {

    private final Class<?> clazz;

    public RhinoCustomNativeJavaClass(Scriptable scope, Class<?> cl) {
        super(scope, cl);
        this.clazz = cl;
    }

    @Override
    public Object get(String name, Scriptable start) {
        Object res = super.get(name, start);
        if (res instanceof NativeJavaMethod) {
            RhinoCustomNativeJavaMethod javaMethod = new RhinoCustomNativeJavaMethod((NativeJavaMethod) res);
            ScriptRuntime.setFunctionProtoAndParent(javaMethod, getParentScope());
            return javaMethod;
        }
        return res;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
