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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.json.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Cavanna
 *
 * Handles the conversion from string to native json and the other way around depending on the rhino engine
 * A {@link NativeObject} is received as input and converted to a <code>String</code> while a
 * generic <code>Object</code> is produced from a String
 */
public class RhinoJsonSerializer implements JsonSerializer<NativeObject, Object> {

    private static final Logger logger = LoggerFactory.getLogger(RhinoJsonSerializer.class);

    @Override
    public String jsonToString(NativeObject json) {
        Context context = Context.getCurrentContext();
        Object jsonString = NativeJSON.stringify(context, ScriptRuntime.getGlobal(context), json, null, null);
        return jsonString.toString();
    }

    @Override
    public Object stringToJson(String json) {
        Context context = Context.getCurrentContext();
        try {
            return new JsonParser(context, ScriptRuntime.getGlobal(context)).parseValue(json);
        } catch (JsonParser.ParseException e) {
            logger.error("Unable to create a json object from string {}", json, e);
            return null;
        }
    }
}
