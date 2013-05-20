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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.elasticsearch.common.inject.*;
import org.elasticsearch.shell.client.*;
import org.elasticsearch.shell.command.Command;
import org.elasticsearch.shell.command.CommandModule;
import org.elasticsearch.shell.command.ExecutableCommand;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Luca Cavanna
 */
public class MessagesProviderTest {

    private static final List<String> HELP_METHODS_EXCLUDE = new ArrayList<String>();

    static {
        HELP_METHODS_EXCLUDE.add("toString");
        HELP_METHODS_EXCLUDE.add("close");
        HELP_METHODS_EXCLUDE.add("wait");
        HELP_METHODS_EXCLUDE.add("notify");
        HELP_METHODS_EXCLUDE.add("notifyAll");
        HELP_METHODS_EXCLUDE.add("equals");
        HELP_METHODS_EXCLUDE.add("hashCode");
        HELP_METHODS_EXCLUDE.add("getClass");
    }

    @Test
    public void testLoadHelpFile() throws Exception {
        MessagesProvider provider = new MessagesProvider();
        provider.load(MessagesProvider.MESSAGES_FILE);
    }

    @Test(expectedExceptions = IOException.class)
    public void testLoadFileNotFound() throws Exception {
        MessagesProvider provider = new MessagesProvider();
        provider.load("not found");
    }

    @Test
    public void testAllCommandsHaveHelp() {
        Injector injector = Guice.createInjector(new ShellModule(), new JLineModule(), new RhinoShellModule(), new CommandModule(), new Module() {
            @Override
            public void configure(Binder binder) {
                binder.bind(CommandRegistry.class).asEagerSingleton();
            }
        });
        CommandRegistry registry = injector.getInstance(CommandRegistry.class);

        for (Command command : registry.commands) {
            String help = MessagesProvider.getHelp(command);
            Assert.assertNotNull(help);
            Assert.assertTrue(help.trim().length() > 0);
        }
    }

    @Test
    public void testHelpMessageContainsAllCommands() {
        Injector injector = Guice.createInjector(new ShellModule(), new JLineModule(), new RhinoShellModule(), new CommandModule(), new Module() {
            @Override
            public void configure(Binder binder) {
                binder.bind(CommandRegistry.class).asEagerSingleton();
            }
        });

        String help = MessagesProvider.getHelp("help");

        CommandRegistry registry = injector.getInstance(CommandRegistry.class);

        List<String> missingCommands = new ArrayList<String>();
        for (Command command : registry.commands) {
            ExecutableCommand annotation = command.getClass().getAnnotation(ExecutableCommand.class);
            boolean found = false;
            for (String alias : annotation.aliases()) {
                //pretty basic but works for now, a regex might be better here
                if (help.contains(alias)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                missingCommands.add(command.getClass().getSimpleName());
            }
        }

        Assert.assertTrue(missingCommands.isEmpty(),
                "The help message doesn't mention commands " + missingCommands);

    }

    @Test
    public void testAllAbstractClientPublicMethodsHaveHelp() {
        testAllPublicMethodsHaveHelp(AbstractClient.class);
    }

    @Test
    public void testAllIndicesApiClientPublicMethodsHaveHelp() {
        testAllPublicMethodsHaveHelp(IndicesApiClient.class);
    }

    @Test
    public void testAllClusterApiClientPublicMethodsHaveHelp() {
        testAllPublicMethodsHaveHelp(ClusterApiClient.class);
    }

    @Test
    public void testAllInternalIndexClientPublicMethodsHaveHelp() {
        testAllPublicMethodsHaveHelp(InternalIndexClient.class);
    }

    @Test
    public void testAllInternalTypeClientPublicMethodsHaveHelp() {
        testAllPublicMethodsHaveHelp(InternalTypeClient.class);
    }

    private void testAllPublicMethodsHaveHelp(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (!HELP_METHODS_EXCLUDE.contains(method.getName())) {
                String help = MessagesProvider.getHelp(clazz.getSimpleName() + "." + method.getName());
                Assert.assertNotNull(help, "Method " + method.getName() + " doesn't have a help message");
                Assert.assertTrue(help.trim().length() > 0, "Method " + method.getName() + " has an empty help message");
            }
        }
    }

    private static class CommandRegistry {
        final Set<Command> commands;

        @Inject
        CommandRegistry(Set<Command> commands) {
            this.commands = commands;
        }
    }
}
