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

import org.elasticsearch.common.inject.Inject;

/**
 * Shutdown hook that shutdowns the shell before the jvm is shutdown
 * Works either after a normal exit or after a forced exit via CTRL-C
 *
 * @author Luca Cavanna
 */
public class ShutdownHook {
    @Inject
    public ShutdownHook(final Shell shell) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shell.shutdown();
            }
        });
    }
}
