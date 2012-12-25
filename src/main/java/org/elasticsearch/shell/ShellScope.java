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

/**
 * Shell scope which wraps the real scope object that depends on the underlying engine
 *
 * @author Luca Cavanna
 */
public class ShellScope<Scope> {

    private final Scope scope;

    /**
     * Creates a new <code>ShellScope</code> given the actual scope object
     * @param scope the actual scope object that depends on the engine in use
     */
    ShellScope(Scope scope) {
        this.scope = scope;
    }

    /**
     * Allows to retrieve the actual scope object
     * @return the actual scope object depending on the engine in use
     */
    public Scope get() {
        return scope;
    }
}
