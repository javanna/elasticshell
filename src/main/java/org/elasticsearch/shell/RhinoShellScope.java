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
 * Rhino implementation of the shell scope that wraps the {@link RhinoShellTopLevel}
 * and contains all the objects needed in the context of the shell
 *
 * @author Luca Cavanna
 */
public class RhinoShellScope extends ShellScope<RhinoShellTopLevel> {
    /**
     * Creates a RhinoShellScope given the Rhino top-level object and an optional scheduler
     * @param scope
     */
    @Inject
    RhinoShellScope(RhinoShellTopLevel scope) {
        super(scope);
    }
}
