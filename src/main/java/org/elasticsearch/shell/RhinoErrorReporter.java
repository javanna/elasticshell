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

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.tools.ToolErrorReporter;

import java.io.PrintStream;

/**
 * Custom Rhino {@link ErrorReporter} used to avoid printing out stacktraces
 * and providing better and shorter error messages as output
 *
 *
 * @author Luca Cavanna
 */
public class RhinoErrorReporter implements ErrorReporter {

    private final boolean reportWarnings;
    private final PrintStream err;

    public RhinoErrorReporter(boolean reportWarnings, PrintStream err) {
        this.reportWarnings = reportWarnings;
        this.err = err;
    }

    public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
        if (!reportWarnings) {
            return;
        }
        reportErrorMessage(message, sourceName, line, lineSource, lineOffset, true);
    }

    public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
        reportErrorMessage(message, sourceName, line, lineSource, lineOffset, false);
    }

    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
        return new EvaluatorException(message, sourceName, line, lineSource, lineOffset);
    }

    private void reportErrorMessage(String message, String sourceName, int line, String lineSource, int lineOffset, boolean justWarning) {
        if (line > 0) {
            String lineStr = String.valueOf(line);
            if (sourceName != null) {
                Object[] args = {sourceName, lineStr, message};
                message = ToolErrorReporter.getMessage("msg.format3", args);
            } else {
                Object[] args = {lineStr, message};
                message = ToolErrorReporter.getMessage("msg.format2", args);
            }
        } else {
            Object[] args = {message};
            message = ToolErrorReporter.getMessage("msg.format1", args);
        }
        if (justWarning) {
            message = ToolErrorReporter.getMessage("msg.warning", message);
        }
        err.println(message);
        if (null != lineSource) {
            err.println(lineSource);
            err.println(buildIndicator(lineOffset));
        }
    }

    private String buildIndicator(int offset) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < offset - 1; i++)
            sb.append(".");
        sb.append("^");
        return sb.toString();
    }
}