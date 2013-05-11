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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.shell.Unwrapper;
import org.elasticsearch.shell.console.Console;
import org.elasticsearch.shell.json.ToXContentAsString;

/**
 * Command that allows to print out to the console its arguments
 *
 * @author Luca Cavanna
 */
@ExecutableCommand(aliases = {"save"})
public class SaveCommand extends Command {

    private final Unwrapper unwrapper;
    private final ToXContentAsString toXContentAsString;

    @Inject
    protected SaveCommand(Console<PrintStream> console, Unwrapper unwrapper,
                          ToXContentAsString toXContentAsString) {
        super(console);
        this.unwrapper = unwrapper;
        this.toXContentAsString = toXContentAsString;
    }

    @SuppressWarnings("unused")
    public void execute(Object arg) throws IOException {
        //Writes by default in output directory
        File dir = new File("output");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        execute(arg, new File(dir, "output.txt"));
    }

    @SuppressWarnings("unused")
    public void execute(Object arg, String path) throws IOException {
        execute(arg, new File(path));
    }

    public void execute(Object arg, File file) throws IOException {
        //keeps appending to the same file if existing
        FileWriter writer = new FileWriter(file, true);
        writer.write(unwrap(arg));
        writer.write('\n');
        writer.close();
    }

    protected String unwrap(Object arg) throws IOException {
        Object unwrappedArg = unwrapper.unwrap(arg);

        if (unwrappedArg instanceof  ToXContent) {
            ToXContent toXContent = (ToXContent) unwrappedArg;
            return toXContentAsString.asString(toXContent);
        }

        if (unwrappedArg != null) {
            return unwrappedArg.toString();
        }
        return null;
    }
}
