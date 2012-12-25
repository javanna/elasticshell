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


import org.elasticsearch.shell.console.Console;
import org.elasticsearch.shell.script.ScriptExecutor;
import org.elasticsearch.shell.source.CompilableSource;
import org.elasticsearch.shell.source.CompilableSourceReader;

/**
 * Basic shell implementation: it reads a compilable source and executes it
 *
 * @author Luca Cavanna
 */
public class BasicShell implements Shell {

    protected final Console console;
    protected final CompilableSourceReader compilableSourceReader;
    protected final ScriptExecutor scriptExecutor;
    protected final Unwrapper unwrapper;

    /**
     * Creates a new <code>BasicShell</code>
     *
     * @param console the console used to read commands and prints the results
     * @param compilableSourceReader reader that receives a potential script and determines whether
     *                               it really is a compilable script or eventually waits for more input
     * @param scriptExecutor executor used to execute a compilable source
     * @param unwrapper unwraps a script object and converts it to its Java representation
     */
    public BasicShell(Console console, CompilableSourceReader compilableSourceReader,
                      ScriptExecutor scriptExecutor, Unwrapper unwrapper) {
        this.console = console;
        this.compilableSourceReader = compilableSourceReader;
        this.scriptExecutor = scriptExecutor;
        this.unwrapper = unwrapper;
    }

    @Override
    public void run() {
        while (true) {
            CompilableSource source = null;
            try {
                source = compilableSourceReader.read();
            } catch (Exception e) {
                e.printStackTrace(console.out());
            }
            if (source != null){
                Object jsResult = scriptExecutor.execute(source);
                Object javaResult = unwrap(jsResult);
                if (javaResult instanceof ExitSignal) {
                    return;
                }
                if (javaResult != null) {
                    console.println(javaToString(javaResult));
                }
            }
        }
    }

    /**
     * Converts a javascript object returned by the shell to its Java representation
     * @param scriptObject the javascript object to convert
     * @return the Java representation of the input javascript object
     */
    protected Object unwrap(Object scriptObject) {
        return unwrapper.unwrap(scriptObject);
    }

    /**
     * Converts a Java object to its textual representation that can be shown as a result of a script execution
     * @param javaObject the Java object to convert to its textual representation
     * @return the textual representation of the input Java object
     */
    protected String javaToString(Object javaObject) {
        return javaObject.toString();
    }

    @Override
    public void shutdown() {
        console.println();
        console.println("bye");
        //TODO close any opened clients/nodes/threads
    }
}
