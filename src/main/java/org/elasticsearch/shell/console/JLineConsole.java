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
package org.elasticsearch.shell.console;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.CompletionHandler;
import jline.console.history.FileHistory;
import jline.console.history.History;
import org.elasticsearch.common.base.Function;
import org.elasticsearch.common.collect.Iterators;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.name.Named;
import org.elasticsearch.shell.ShellSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>Console</code> implementation that depends on JLine for the input.
 *
 * @author Luca Cavanna
 */
public class JLineConsole extends AbstractConsole {

    private static final Logger logger = LoggerFactory.getLogger(JLineConsole.class);

    private final ConsoleReader reader;
    private final FileHistory fileHistory;

    /**
     * Constructor for the JLineConsole
     * @param appName the application name
     * @param in the <code>InputStream</code> to read input from
     * @param out the <code>PrintStream</code> to print output to
     * @param completerHolder holds an optional JLine completer to auto-complete commands
     */
    @Inject
    JLineConsole(@Named("appName") String appName,
                 @Named("shellInput") InputStream in, @Named("shellOutput") PrintStream out,
                 ShellSettings shellSettings, CompleterHolder completerHolder) {
        super(out);
        try {
            this.reader = new ConsoleReader(appName, in, out, null);

            String userHome = System.getProperty("user.home");
            if (userHome != null && userHome.length()>0) {
                this.fileHistory = new FileHistory(new File(userHome, ShellSettings.HISTORY_FILE));
                this.reader.setHistory(fileHistory);
            } else {
                this.fileHistory = null;
            }

            Integer maxSuggestions = shellSettings.settings().getAsInt(ShellSettings.SUGGESTIONS_MAX, 100);
            this.reader.setAutoprintThreshold(maxSuggestions);

            reader.setBellEnabled(false);
            if (completerHolder.completer != null) {
                reader.addCompleter(completerHolder.completer);
            }
            if (completerHolder.completionHandler != null) {
                reader.setCompletionHandler(completerHolder.completionHandler);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class CompleterHolder {
        @Inject(optional=true) Completer completer;
        @Inject(optional=true) CompletionHandler completionHandler;
    }

    /**
     * Reads a line using the JLine {@link ConsoleReader}
     * @param prompt the prompt to be printed
     * @return the line read
     * @throws IOException in case of problems while reading the line
     */
    @Override
    public String readLine(String prompt) throws IOException {
        String line = reader.readLine(prompt);
        logger.debug("Read line {}", line);
        return line;
    }

    @Override
    public Iterator<CharSequence> getHistoryEntries() {
        if (fileHistory == null) {
            return new ArrayList<CharSequence>().iterator();
        }
        return Iterators.transform(fileHistory.iterator(), new Function<History.Entry, CharSequence>() {
            @Override
            public CharSequence apply(jline.console.history.History.Entry entry) {
                return entry.value();
            }
        });
    }

    @Override
    public void shutdown() {

        super.shutdown();

        if (fileHistory != null) {
            try {
                fileHistory.flush();
            } catch (IOException e) {
                logger.error("Error while flushing the history to file {}", fileHistory.getFile().getAbsolutePath(), e);
            }
        }

        //Calling shutdown breaks the shutdown hook, which waits for it forever
        //reader.shutdown();
    }
}
