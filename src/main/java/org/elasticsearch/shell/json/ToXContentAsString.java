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

import java.io.IOException;

import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

/**
 * @author Luca Cavanna
 *
 * Converts a {@link ToXContent} object to string
 */
public class ToXContentAsString {

    public String asString(ToXContent toXContent) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder().prettyPrint();
        try {
            toXContent.toXContent(builder, ToXContent.EMPTY_PARAMS);
        } catch (IOException e) {
            //hack: the first object in the builder might need to be opened, depending on the ToXContent implementation
            //Let's just try again, hopefully it'll work
            builder.startObject();
            toXContent.toXContent(builder, ToXContent.EMPTY_PARAMS);
            builder.endObject();
        }
        return builder.string();
    }
}
