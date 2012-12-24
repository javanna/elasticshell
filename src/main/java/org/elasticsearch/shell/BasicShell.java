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


import org.elasticsearch.common.inject.name.Named;

import java.io.PrintStream;

public class BasicShell implements Shell {

    protected final PrintStream out;
    protected final CompilableSourceReader compilableSourceReader;
    protected final ScriptExecutor scriptExecutor;

    public BasicShell(@Named("shellOutput") PrintStream out, CompilableSourceReader compilableSourceReader,
                      ScriptExecutor scriptExecutor) {
        this.out = out;
        this.compilableSourceReader = compilableSourceReader;
        this.scriptExecutor = scriptExecutor;
    }

    public void run() {
        while (true) {
            CompilableSource source = null;
            try {
                source = compilableSourceReader.read();
            } catch (Exception e) {
                e.printStackTrace(out);
            }
            if (source != null){
                Object jsResult = scriptExecutor.execute(source);
                Object javaResult = jsToJava(jsResult);
                if (javaResult instanceof ExitSignal) {
                    return;
                }
                if (javaResult != null) {
                    out.println(javaToString(javaResult));
                }
            }
        }
    }

    protected Object jsToJava(Object jsResult) {
        return jsResult;
    }

    protected String javaToString(Object javaResult) {
        return javaResult.toString();
    }

    @Override
    public void shutdown() {
        out.println();
        out.println("bye");
        //TODO close any opened clients/nodes/threads
    }
}
