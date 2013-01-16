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

import org.elasticsearch.client.Requests;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaClass;

/**
 * Rhino shell scriptable object that can be used as top-level (global) object
 *
 * @author Luca Cavanna
 */
public class RhinoShellTopLevel extends ImporterTopLevel {

    RhinoShellTopLevel(){
        //the top-level automatically initializes itself
        Context context = Context.enter();
        try {
            initStandardObjects(context, true);

            put(Requests.class.getSimpleName(), this, new NativeJavaClass(this, Requests.class));

        } finally {
            Context.exit();
        }
    }
}
