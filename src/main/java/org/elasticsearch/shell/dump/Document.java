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
    private static final String INDEX = "_index";
    private static final String TYPE = "_type";

    private final String id;
    private final String dump;
    private String document;
    private String index;
    private String type;

    private Document(String dump, String id) throws IOException {
        this.dump = dump;
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

    private void setDocument(String document) {
        this.document = document;
    }

    public String getIndex() {
        return index;
    }

    private void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    static Document fromDump(String dump) throws IOException {
        Map<String,Object> map = asMap(dump);
        Object idObject = map.get(ID);
        if (idObject == null) {
            throw new RuntimeException("Unable to retrieve the id from the document: \n" + dump);
        }
        map.remove(ID);


        Document document = new Document(dump, idObject.toString());

        Object typeObject = map.get(TYPE);
        if (typeObject != null) {
            map.remove(TYPE);
            document.setType(typeObject.toString());
        }

        Object indexObject = map.get(INDEX);
        if (indexObject != null) {
            map.remove(INDEX);
            document.setIndex(indexObject.toString());
        }

        document.setDocument(asString(map));

        return document;
    }

    static Document fromSource(String document, String index, String type, String id) throws IOException {
        Map<String, Object> map = asMap(document);
        map.put(ID, id);
        map.put(INDEX, index);
        map.put(TYPE, type);

        Document doc = new Document(asString(map), id);
        doc.setDocument(document);
        doc.setIndex(index);
        doc.setType(type);
        return doc;
    }
}
