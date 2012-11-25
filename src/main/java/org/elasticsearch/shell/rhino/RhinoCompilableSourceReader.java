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
package org.elasticsearch.shell.rhino;

import org.elasticsearch.shell.CompilableSourceReader;
import org.elasticsearch.shell.Console;
import org.mozilla.javascript.Context;

public class RhinoCompilableSourceReader extends CompilableSourceReader {

    private final Context context;

    public RhinoCompilableSourceReader(Console console, Context context) {
        super(console);
        this.context = context;
    }

    @Override
    public boolean isCompilable(String source) {
        return context.stringIsCompilableUnit(source);
    }
}
