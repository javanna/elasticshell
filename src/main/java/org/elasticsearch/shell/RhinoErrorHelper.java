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

import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.tools.ToolErrorReporter;

/**
 * Helper to extract the message given a RhinoException
 * Extracted from {@link ToolErrorReporter}
 *
 * @author Luca Cavanna
 */
public final class RhinoErrorHelper {
    private RhinoErrorHelper() {

    }

    public static String getExceptionMessage(RhinoException ex) {
        String msg;
        if (ex instanceof JavaScriptException) {
            msg = ToolErrorReporter.getMessage("msg.uncaughtJSException", ex.details());
        } else if (ex instanceof EcmaError) {
            msg = ToolErrorReporter.getMessage("msg.uncaughtEcmaError", ex.details());
        } else if (ex instanceof EvaluatorException) {
            msg = ex.details();
        } else {
            msg = ex.toString();
        }
        return msg;
    }
}
