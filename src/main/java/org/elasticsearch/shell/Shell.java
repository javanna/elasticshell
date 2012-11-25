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


public abstract class Shell {

    protected final Console console;

    protected Shell(Console console) {
        this.console = console;
    }

    protected void run(CompilableSourceReader compilableSourceReader, ScriptExecutor scriptExecutor) {
        //TODO end???
        boolean end = false;
        while (!end) {
            CompilableSource source = compilableSourceReader.read();
            if (source != null){
                String result = scriptExecutor.execute(source);
                if (result != null) {
                    console.println(result);
                }
            }
        }
        //TODO ???
        console.println("bye");
    }
}
