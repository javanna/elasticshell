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
import org.mozilla.javascript.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;

/**
 * Loads an external javascript source file through Rhino
 *
 * @author Luca Cavanna
 */
public class RhinoScriptLoader implements ScriptLoader {

    private static final Logger logger = LoggerFactory.getLogger(RhinoScriptLoader.class);

    private ShellScope<RhinoShellTopLevel> shellScope;

    @Inject
    RhinoScriptLoader(ShellScope<RhinoShellTopLevel> shellScope) {
        this.shellScope = shellScope;
    }

    @Override
    public void loadScript(String scriptPath) throws IOException {

        /*
            Interpretive mode is always used. The compilation time is minimized at the expense of runtime performance.
            No class files are generated, which may improve memory usage depending on your system.
            Another benefit of the interpreted mode is that the interpreter performs tail-call elimination
            of recursive functions. Also, you must use this optimization level if your code uses Continuation objects.
             */
        //needed to be able to import external libraries like env.js, hopefully won't hurt that much even when not needed
        Context.getCurrentContext().setOptimizationLevel(-1);

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(scriptPath);
            Context.getCurrentContext().evaluateReader(shellScope.get(), fileReader, scriptPath, 1, null);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch(IOException e) {
                    logger.error("Error while closing the script FileReader" , e);
                }
            }
        }

    }
}
