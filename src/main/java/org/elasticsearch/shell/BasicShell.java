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


import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.shell.client.ClientFactory;
import org.elasticsearch.shell.command.ScriptLoader;
import org.elasticsearch.shell.console.Console;
import org.elasticsearch.shell.scheduler.Scheduler;
import org.elasticsearch.shell.script.ScriptExecutor;
import org.elasticsearch.shell.source.CompilableSource;
import org.elasticsearch.shell.source.CompilableSourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Basic shell implementation: it reads a compilable source and executes it
 *
 * @author Luca Cavanna
 */
public class BasicShell<ShellNativeClient> implements Shell {

    private static final Logger logger = LoggerFactory.getLogger(BasicShell.class);

    protected final Console<PrintStream> console;
    protected final CompilableSourceReader compilableSourceReader;
    protected final ScriptExecutor scriptExecutor;
    protected final Unwrapper unwrapper;
    protected final ShellScope<?> shellScope;
    protected final ClientFactory<ShellNativeClient> clientFactory;
    protected final ScriptLoader scriptLoader;
    protected final Scheduler scheduler;
    protected final ShellSettings shellSettings;

    protected final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * Creates a new <code>BasicShell</code>
     *
     * @param console the console used to read commands and prints the results
     * @param compilableSourceReader reader that receives a potential script and determines whether
     *                               it really is a compilable script or eventually waits for more input
     * @param scriptExecutor executor used to execute a compilable source
     * @param unwrapper unwraps a script object and converts it to its Java representation
     * @param shellScope the generic shell scope
     * @param scheduler the scheduler that handles all the scheduled actions
     */
    public BasicShell(Console<PrintStream> console, CompilableSourceReader compilableSourceReader,
                      ScriptExecutor scriptExecutor, Unwrapper unwrapper, ShellScope<?> shellScope,
                      ClientFactory<ShellNativeClient> clientFactory,
                      ScriptLoader scriptLoader, Scheduler scheduler,
                      ShellSettings shellSettings) {
        this.console = console;
        this.compilableSourceReader = compilableSourceReader;
        this.scriptExecutor = scriptExecutor;
        this.unwrapper = unwrapper;
        this.shellScope = shellScope;
        this.clientFactory = clientFactory;
        this.scriptLoader = scriptLoader;
        this.scheduler = scheduler;
        this.shellSettings = shellSettings;
    }

    @Override
    public void run() {
        init();
        loadStartupScript();
        try {
            doRun();
        } finally {
            close();
        }
    }

    protected void doRun() {
        printLogoAndWelcomeMessage();

        tryRegisterDefaultClient();

        while (true) {
            CompilableSource source = null;
            try {
                source = compilableSourceReader.read();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                console.println("Error while checking the input: " + e.toString());
            }
            if (source != null) {
                Object jsResult = scriptExecutor.execute(source);
                Object javaResult = unwrap(jsResult);
                if (javaResult instanceof ExitSignal) {
                    shutdown();
                    return;
                }
                if (javaResult != null) {
                    console.println(javaToString(javaResult));
                }
            }
        }
    }

    /**
     * Initializes the shell: nothing to do here but can be overridden
     */
    void init() {

    }

    /**
     * Loads the optional startup script
     */
    void loadStartupScript() {
        String startupScript = shellSettings.settings().get(ShellSettings.STARTUP_SCRIPT);
        if (startupScript != null) {
            try {
                scriptLoader.loadScript(startupScript);
            } catch(Throwable t) {
                logger.error("Error loading the startup script [{}]", startupScript, t);
            }
        }
    }

    /**
     * Close the shell: nothing to do here but can be overridden
     */
    void close() {

    }

    protected void printLogoAndWelcomeMessage() {
        StringBuilder logoBuilder = new StringBuilder();
        logoBuilder.append("               ___       \n");
        logoBuilder.append("             //   \\\\   \n");
        logoBuilder.append("         {{ ((     ))    ").append(MessagesProvider.getMessage(ShellSettings.WELCOME_MESSAGE));
        logoBuilder.append("         {{  \\\\___//   \n");
        logoBuilder.append("        {{     //        \n");
        logoBuilder.append("         {{   //  }}     \n");
        logoBuilder.append("         {{  //    }}    \n");
        logoBuilder.append("         {{_______}}     \n");

        console.println(logoBuilder.toString());
    }

    protected void tryRegisterDefaultClient() {
        ShellNativeClient shellNativeClient = clientFactory.newTransportClient();
        if (shellNativeClient != null) {
            registerClient(shellNativeClient);
        }
    }

    protected void registerClient(ShellNativeClient shellNativeClient) {
        shellScope.registerJavaObject("es", shellNativeClient);
        console.println(shellNativeClient.toString() + " registered with name es");
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

        //We wanna show a string output whenever there's a ToXContent, which we can convert to json
        if (javaObject instanceof ToXContent) {
            ToXContent toXContent = (ToXContent) javaObject;
            try {
                XContentBuilder builder = XContentFactory.jsonBuilder().prettyPrint();
                try {
                    toXContent.toXContent(builder, ToXContent.EMPTY_PARAMS);
                } catch (IOException e) {
                    //hack: the first object in the builder might need to be opened, depending on the ToXContent implementation
                    //Let's just try again, hopefully it'll work
                    builder.startObject();
                    toXContent.toXContent(builder, ToXContent.EMPTY_PARAMS);
                    builder.endObject();
                }
                return builder.string();
            } catch (Exception e) {
                logger.warn("Error while trying to convert a {} to XContent", javaObject.getClass().getSimpleName(), e);
            }
        }

        return javaObject.toString();
    }

    @Override
    public void shutdown() {
        if (closed.compareAndSet(false, true)) {
            if (scheduler != null) {
                logger.debug("Shutting down the scheduler");
                scheduler.shutdown();
            }
            shellScope.closeAllResources();
            console.println();
            console.println(MessagesProvider.getMessage(ShellSettings.BYE_MESSAGE));
        }
    }
}
