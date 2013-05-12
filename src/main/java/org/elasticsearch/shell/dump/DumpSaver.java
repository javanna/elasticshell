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

import java.io.*;
import java.nio.charset.Charset;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.shell.console.Console;
import org.elasticsearch.shell.json.JsonToString;

/**
 * @author Luca Cavanna
 *
 * Takes care of saving to file an index dump
 */
public class DumpSaver<JsonInput> {

    private final Console<PrintStream> console;
    private final JsonToString<JsonInput> jsonToString;

    @Inject
    DumpSaver(Console<PrintStream> console, JsonToString<JsonInput> jsonToString) {
        this.console = console;
        this.jsonToString = jsonToString;
    }

    void dumpSave(Client client, Builder builder) throws IOException {

        TimeValue scrollDuration = TimeValue.timeValueSeconds(30);
        SearchResponse searchResponse = client.prepareSearch(builder.indices())
                .setTypes(builder.types()).setSearchType(SearchType.SCAN)
                .setQuery(builder.query()).setSize(100).setScroll(scrollDuration).execute().actionGet();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(builder.path(), true), builder.charset());

        int i = 0;
        try {
            while (true) {
                searchResponse = client.prepareSearchScroll(searchResponse.getScrollId())
                        .setScroll(scrollDuration).execute().actionGet();

                if (searchResponse.getHits().hits().length == 0) {
                    break;
                }

                for (SearchHit hit : searchResponse.getHits()) {
                    Document document = Document.fromSource(hit.sourceAsString(), hit.id());
                    writer.write(document.getDump());
                    i++;
                    writer.write('\n');
                }
            }
        } finally {
            writer.close();
            console.println("Saved " + i + " documents to " + builder.path());
        }

    }

    public class Builder {
        private final Client client;
        private String[] indices = new String[0];
        private String[] types = new String[0];
        private Charset charset;
        private String path;
        private BytesReference query;

        public Builder(Client client) {
            this.client = client;
        }

        public Builder indices(String... indices) {
            this.indices = indices;
            return this;
        }

        public String[] indices() {
            return indices;
        }

        public Builder types(String... types) {
            this.types = types;
            return this;
        }

        public String[] types() {
            return types;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public String path() {
            return path;
        }

        public Builder charset(String charset) {
            this.charset = Charset.forName(charset);
            return this;
        }

        public Charset charset() {
            if (charset == null) {
                return Charset.defaultCharset();
            }
            return charset;
        }

        public Builder queryBuilder(QueryBuilder queryBuilder) {
            this.query = queryBuilder.buildAsBytes();
            return this;
        }

        public Builder query(JsonInput query) {
            this.query = new BytesArray(jsonToString.jsonToString(query, false));
            return this;
        }

        public BytesReference query() {
            return query;
        }

        public void execute() throws IOException {
            DumpSaver.this.dumpSave(client, this);
        }
    }
}
