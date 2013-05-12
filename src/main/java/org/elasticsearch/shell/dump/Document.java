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
package org.elasticsearch.shell.dump;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * @author Luca Cavanna
 *
 * Represents a document to be dumped or already dumped
 *
 */
public class Document {

    private static final String ID = "_id";

    private final String document;
    private final String id;
    private final String dump;

    private Document(String dump, String document, String id) throws IOException {
        this.dump = dump;
        this.document = document;
        this.id = id;
    }

    private static Map<String, Object> asMap(String jsonSource) throws IOException {
        XContentParser parser = XContentFactory.xContent(XContentType.JSON).createParser(jsonSource);
        return parser.map();
    }

    private static String asString(Map<String, Object> map) throws IOException {
        XContentBuilder builder = XContentFactory.contentBuilder(XContentType.JSON);
        return builder.map(map).string();
    }

    public String getDocument() {
        return document;
    }

    public String getId() {
        return id;
    }

    public String getDump() {
        return dump;
    }

    static Document fromDump(String dump) throws IOException {
        Map<String,Object> map = asMap(dump);
        Object idObject = map.get(ID);
        if (idObject == null) {
            throw new RuntimeException("Unable to retrieve the id from the document: \n" + dump);
        }
        map.remove(ID);
        return new Document(dump, asString(map), idObject.toString());
    }

    static Document fromSource(String document, String id) throws IOException {
        Map<String, Object> map = asMap(document);
        map.put(ID, id);
        return new Document(asString(map), document, id);
    }
}
