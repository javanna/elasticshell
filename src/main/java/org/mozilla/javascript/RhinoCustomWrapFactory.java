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
 * Rhino wrap factory that allows to customize how java objects and java classes get wrapped into rhino objects
 */
public class RhinoCustomWrapFactory extends WrapFactory {
    @Override
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
        return new RhinoCustomNativeJavaObject(scope, javaObject, staticType);
    }

    @Override
    public Scriptable wrapJavaClass(Context cx, Scriptable scope, Class javaClass) {
        return new RhinoCustomNativeJavaClass(scope, javaClass);
    }
}
