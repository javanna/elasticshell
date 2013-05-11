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
package org.elasticsearch.shell.command;

import java.io.IOException;
import java.io.PrintStream;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.shell.console.Console;
import org.elasticsearch.shell.json.StringToJson;
import org.elasticsearch.shell.json.ToXContentAsString;

/**
 * Command that's able to generate native json objects given different type of content
 *
 * @author Luca Cavanna
 */
@ExecutableCommand(aliases = "toJson")
public class ToJsonCommand<JsonOutput> extends Command {

    private final StringToJson<JsonOutput> stringToJson;
    private final ToXContentAsString toXContentAsString;

    @Inject
    protected ToJsonCommand(Console<PrintStream> console, StringToJson<JsonOutput> stringToJson,
                            ToXContentAsString toXContentAsString) {
        super(console);
        this.stringToJson = stringToJson;
        this.toXContentAsString = toXContentAsString;
    }

    @SuppressWarnings("unused")
    public JsonOutput execute(String jsonAsString) {
        return stringToJson.stringToJson(jsonAsString);
    }

    @SuppressWarnings("unused")
    public JsonOutput execute(ToXContent toXContent) throws IOException {
        return stringToJson.stringToJson(toXContentAsString.asString(toXContent));
    }
}
