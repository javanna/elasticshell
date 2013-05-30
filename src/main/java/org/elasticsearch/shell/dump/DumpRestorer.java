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

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.shell.console.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Cavanna
 *
 * Takes care of restoring a dump
 *
 */
public class DumpRestorer {

    private static final Logger logger = LoggerFactory.getLogger(DumpRestorer.class);

    private final Console<PrintStream> console;

    @Inject
    public DumpRestorer(Console<PrintStream> console) {
        this.console = console;
    }

    protected void dumpRestore(Client client, Builder builder) throws IOException {

        BulkProcessor bulkProcessor = buildBulkProcessor(client);
        BufferedReader reader = null;
        try {
            FileInputStream fis = new FileInputStream(builder.path());
            reader = new BufferedReader(new InputStreamReader(fis, builder.charset()));
            String line;
            while( (line=reader.readLine()) != null) {
                indexLine(bulkProcessor, builder.index(), builder.type(), line);
            }

        } finally {
            bulkProcessor.close();
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void indexLine(BulkProcessor bulkProcessor, String index, String type, String line) {
        try {
            Document document = Document.fromDump(line);
            bulkProcessor.add(Requests.indexRequest(index == null ? document.getIndex() : index)
                    .type(type == null ? document.getType() : type)
                    .id(document.getId()).source(document.getDocument()));
        } catch(Exception e) {
            logger.error("Error while indexing document {}", line, e);
        }
    }

    private BulkProcessor buildBulkProcessor(Client client) {
        return BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {

            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                console.println("Executed bulk of " + response.getItems().length + " items");
                if (response.hasFailures()) {
                    console.println(response.buildFailureMessage());
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                console.println("Error executing bulk: " + failure.getMessage());
                logger.error("Error executing bulk", failure);
            }
        }).setConcurrentRequests(0).build();
    }

    public class Builder {
        private final Client client;
        private String index;
        private String type;
        private Charset charset;
        private String path;

        public Builder(Client client) {
            this.client = client;
        }

        public Builder index(String index) {
            this.index = index;
            return this;
        }

        public String index() {
            return index;
        }
        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public String type() {
            return type;
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

        public void execute() throws IOException {
            DumpRestorer.this.dumpRestore(client, this);
        }
    }
}
