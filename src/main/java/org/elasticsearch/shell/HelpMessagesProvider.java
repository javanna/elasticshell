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

import org.elasticsearch.common.io.Streams;
import org.elasticsearch.common.settings.loader.SettingsLoader;
import org.elasticsearch.common.settings.loader.SettingsLoaderFactory;
import org.elasticsearch.shell.command.Command;
import org.elasticsearch.shell.command.ExecutableCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author Luca Cavanna
 *
 * Provides the help messages based on a file within the classpath
 */
public class HelpMessagesProvider {

    private static final Logger logger = LoggerFactory.getLogger(HelpMessagesProvider.class);

    static final String HELP_FILE = "/help.yml";

    private static final HelpMessagesProvider helpMessagesProvider = new HelpMessagesProvider();

    static {
        try {
            helpMessagesProvider.load(HELP_FILE);
        } catch (Exception e) {
            logger.error("Unable to load help file {} from classpath", HELP_FILE, e);
        }
    }

    private Map<String, String> helpMessages;

    void load(String resourceName) throws Exception {
        this.helpMessages = loadFromClasspath(resourceName);
    }

    Map<String, String> loadFromClasspath(String resourceName) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(resourceName);
        if (is == null) {
            throw new FileNotFoundException("Unable to find file " + HELP_FILE);
        }
        SettingsLoader settingsLoader = SettingsLoaderFactory.loaderFromResource(resourceName);
        return settingsLoader.load(Streams.copyToString(new InputStreamReader(is, "UTF-8")));
    }

    static String getHelp(String key) {
        if (helpMessagesProvider.helpMessages != null) {
            String message = helpMessagesProvider.helpMessages.get(key);
            if (message != null) {
                return message;
            }
        }
        return "";
    }

    public static String getHelp(Command command) {
        String[] commandAliases = command.getClass().getAnnotation(ExecutableCommand.class).aliases();
        for (String commandAlias : commandAliases) {
            String helpMessage = getHelp(commandAlias + ".help");
            if (helpMessage != null && helpMessage.trim().length()> 0) {
                return helpMessage;
            }
        }
        return null;
    }

    public static String getHelp(Class<?> clazz, String functionName) {
        return getHelp(clazz.getSimpleName() + "." + functionName);
    }
}
