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

import org.elasticsearch.shell.MessageHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Luca Cavanna
 *
 * Custom {@link NativeJavaMethod} that wraps a normal <code>NativeJavaMethod</code> and allows to return
 * some more information about the java methods behind it in order to make better auto-completion and provide better help.
 */
public class RhinoCustomNativeJavaMethod extends BaseFunction {

    private final NativeJavaMethod nativeJavaMethod;

    public RhinoCustomNativeJavaMethod(NativeJavaMethod nativeJavaMethod) {
        this.nativeJavaMethod = nativeJavaMethod;
    }

    public Set<Class<?>> getReturnTypes() {
        Set<Class<?>> returnTypes = new HashSet<Class<?>>();
        for (MemberBox method : nativeJavaMethod.methods) {
            returnTypes.add(method.method().getReturnType());
        }
        return returnTypes;
    }

    Class<?> getDeclaringClass() {
        if (nativeJavaMethod.methods != null && nativeJavaMethod.methods.length > 0) {
            //hopefully it's always the same class
            return nativeJavaMethod.methods[0].getDeclaringClass();
        }
        return null;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return nativeJavaMethod.call(cx, scope, thisObj, args);
    }

    @Override
    String decompile(int indent, int flags) {
        String helpMessage = MessageHelper.getMessage(getDeclaringClass().getSimpleName() + "." + nativeJavaMethod.getFunctionName());
        if (helpMessage != null && helpMessage.trim().length() > 0) {
            return helpMessage;
        }
        return nativeJavaMethod.decompile(indent, flags);
    }

    @Override
    public String toString() {
        return nativeJavaMethod.toString();
    }
}