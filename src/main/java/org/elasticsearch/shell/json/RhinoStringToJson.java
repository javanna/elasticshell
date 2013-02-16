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
package org.elasticsearch.shell.json;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.json.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Cavanna
 *
 * Handles the conversion from string to native json depending on the rhino engine
 * A generic <code>Object</code> is produced from a String
 */
public class RhinoStringToJson implements StringToJson<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RhinoStringToJson.class);

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
