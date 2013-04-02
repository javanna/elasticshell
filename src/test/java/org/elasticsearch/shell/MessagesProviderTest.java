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

import org.elasticsearch.common.inject.*;
import org.elasticsearch.shell.command.Command;
import org.elasticsearch.shell.command.CommandModule;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Set;

/**
 * @author Luca Cavanna
 */
public class MessagesProviderTest {

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

    private static class CommandRegistry {
        final Set<Command> commands;

        @Inject
        CommandRegistry(Set<Command> commands) {
            this.commands = commands;
        }
    }
}
