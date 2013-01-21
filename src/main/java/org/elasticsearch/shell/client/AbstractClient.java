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
package org.elasticsearch.shell.client;

import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequest;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.deletebyquery.IndexDeleteByQueryResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.percolate.PercolateRequest;
import org.elasticsearch.action.percolate.PercolateResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentBuilderString;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.rest.action.support.RestActions.buildBroadcastShardsHeader;

/**
 * @author Luca Cavanna
 *
 * Generic elasticsearch client wrapper which exposes common client operations that don't depend
 * on the specific type of client in use (transport or node)
 *
 * @param <JsonInput> the shell native object that represents a json object received as input from the shell
 * @param <JsonOutput> the shell native object that represents a json object that we give as output to the shell
 */
public abstract class AbstractClient<JsonInput, JsonOutput> implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);

    private final Client client;
    private final JsonSerializer<JsonInput, JsonOutput> jsonSerializer;

    protected AbstractClient(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        this.client= client;
        this.jsonSerializer = jsonSerializer;
    }

    @SuppressWarnings("unused")
    public Index[] showIndexes() {
        ClusterStateResponse response = client.admin().cluster().prepareState().setFilterBlocks(true)
                .setFilterRoutingTable(true).setFilterNodes(true).execute().actionGet();
        List<Index> indexes = new ArrayList<Index>();
        for (IndexMetaData indexMetaData : response.state().metaData().indices().values()) {
            indexes.add(new Index(indexMetaData.index(), indexMetaData.mappings().keySet(), indexMetaData.aliases().keySet()));
        }
        return indexes.toArray(new Index[indexes.size()]);
    }

    public JsonOutput index(String index, String type, JsonInput source) {
        return index(index, type, null, source);
    }

    public JsonOutput index(String index, String type, String id, JsonInput source) {
        return index(index, type, id, jsonSerializer.jsonToString(source, true));
    }

    public JsonOutput index(String index, String type, String source) {
        return index(index, type, null, source);
    }

    public JsonOutput index(String index, String type, String id, String source) {
        return index(Requests.indexRequest(index).type(type).id(id).source(source));
    }

    public JsonOutput index(IndexRequest indexRequest) {
        IndexResponse response = client.index(indexRequest).actionGet();
        try {
            XContentBuilder builder = JsonXContent.contentBuilder();
            builder.startObject()
                    .field(Fields.OK, true)
                    .field(Fields._INDEX, response.index())
                    .field(Fields._TYPE, response.type())
                    .field(Fields._ID, response.id())
                    .field(Fields._VERSION, response.version());
            if (response.matches() != null) {
                builder.startArray(Fields.MATCHES);
                for (String match : response.matches()) {
                    builder.value(match);
                }
                builder.endArray();
            }
            builder.endObject();
            return jsonSerializer.stringToJson(builder.string());
        } catch (IOException e) {
            logger.error("Error while generating the XContent response", e);
            return null;
        }
    }

    public JsonOutput get(String index, String type, String id) {
        return get(Requests.getRequest(index).type(type).id(id));
    }

    public JsonOutput get(GetRequest getRequest) {
        GetResponse response = client.get(getRequest).actionGet();
        return xContentToJson(response, false);
    }

    public JsonOutput delete(String index, String type, String id) {
        return delete(Requests.deleteRequest(index).type(type).id(id));
    }

    public JsonOutput delete(DeleteRequest deleteRequest) {
        DeleteResponse response = client.delete(deleteRequest).actionGet();
        try {
            XContentBuilder builder = JsonXContent.contentBuilder();
            builder.startObject()
                    .field(Fields.OK, true)
                    .field(Fields.FOUND, !response.notFound())
                    .field(Fields._INDEX, response.index())
                    .field(Fields._TYPE, response.type())
                    .field(Fields._ID, response.id())
                    .field(Fields._VERSION, response.version())
                    .endObject();
            return jsonSerializer.stringToJson(builder.string());
        } catch (IOException e) {
            logger.error("Error while generating the XContent response", e);
            return null;
        }
    }

    public JsonOutput deleteByQuery(String index, JsonInput query) {
        return deleteByQuery(Requests.deleteByQueryRequest(index).query(jsonToString(query)));
    }

    public JsonOutput deleteByQuery(String index, String query) {
        return deleteByQuery(Requests.deleteByQueryRequest(index).query(query));
    }

    public JsonOutput deleteByQuery(String index, QueryBuilder queryBuilder) {
        return deleteByQuery(Requests.deleteByQueryRequest(index).query(queryBuilder));
    }

    public JsonOutput deleteByQuery(String index, String type, JsonInput query) {
        return deleteByQuery(Requests.deleteByQueryRequest(index).types(type).query(jsonToString(query)));
    }

    public JsonOutput deleteByQuery(String index, String type, String query) {
        return deleteByQuery(Requests.deleteByQueryRequest(index).types(type).query(query));
    }

    public JsonOutput deleteByQuery(String index, String type, QueryBuilder queryBuilder) {
        return deleteByQuery(Requests.deleteByQueryRequest(index).types(type).query(queryBuilder));
    }

    public JsonOutput deleteByQuery(DeleteByQueryRequest deleteByQueryRequest) {
        DeleteByQueryResponse response = client.deleteByQuery(deleteByQueryRequest).actionGet();
        try {
            XContentBuilder builder = JsonXContent.contentBuilder();
            builder.startObject().field("ok", true);
            builder.startObject("_indices");
            for (IndexDeleteByQueryResponse indexDeleteByQueryResponse : response.indices().values()) {
                builder.startObject(indexDeleteByQueryResponse.index(), XContentBuilder.FieldCaseConversion.NONE);
                builder.startObject("_shards");
                builder.field("total", indexDeleteByQueryResponse.totalShards());
                builder.field("successful", indexDeleteByQueryResponse.successfulShards());
                builder.field("failed", indexDeleteByQueryResponse.failedShards());
                builder.endObject();
                builder.endObject();
            }
            builder.endObject();
            builder.endObject();
            return jsonSerializer.stringToJson(builder.string());
        } catch (IOException e) {
            logger.error("Error while generating the XContent response", e);
            return null;
        }
    }

    public JsonOutput search() {
        return search(Requests.searchRequest());
    }

    public JsonOutput search(String source) {
        return search(Requests.searchRequest().source(source));
    }

    public JsonOutput search(JsonInput source) {
        return search(Requests.searchRequest().source(jsonToString(source)));
    }

    public JsonOutput search(SearchSourceBuilder searchSourceBuilder) {
        return search(Requests.searchRequest().source(searchSourceBuilder));
    }

    public JsonOutput search(String index, String source) {
        return search(Requests.searchRequest(index).source(source));
    }

    public JsonOutput search(String index, JsonInput source) {
        return search(Requests.searchRequest(index).source(jsonToString(source)));
    }

    public JsonOutput search(String index, SearchSourceBuilder searchSourceBuilder) {
        return search(Requests.searchRequest(index).source(searchSourceBuilder));
    }

    public JsonOutput search(String index, String type, String source) {
        return search(Requests.searchRequest(index).types(type).source(source));
    }

    public JsonOutput search(String index, String type, JsonInput source) {
        return search(Requests.searchRequest(index).types(type).source(jsonToString(source)));
    }

    public JsonOutput search(String index, String type, SearchSourceBuilder searchSourceBuilder) {
        return search(Requests.searchRequest(index).types(type).source(searchSourceBuilder));
    }

    public JsonOutput search(SearchRequest searchRequest) {
        SearchResponse response = client.search(searchRequest).actionGet();
        return xContentToJson(response, true);
    }

    public JsonOutput count() {
        return count(Requests.countRequest());
    }

    public JsonOutput count(String source) {
        return count(Requests.countRequest().query(source));
    }

    public JsonOutput count(JsonInput source) {
        return count(Requests.countRequest().query(jsonToString(source)));
    }

    public JsonOutput count(QueryBuilder queryBuilder) {
        return count(Requests.countRequest().query(queryBuilder));
    }

    public JsonOutput count(String index, String source) {
        return count(Requests.countRequest(index).query(source));
    }

    public JsonOutput count(String index, JsonInput source) {
        return count(Requests.countRequest(index).query(jsonToString(source)));
    }

    public JsonOutput count(String index, QueryBuilder queryBuilder) {
        return count(Requests.countRequest(index).query(queryBuilder));
    }

    public JsonOutput count(String index, String type, String source) {
        return count(Requests.countRequest(index).types(type).query(source));
    }

    public JsonOutput count(String index, String type, JsonInput source) {
        return count(Requests.countRequest(index).types(type).query(jsonToString(source)));
    }

    public JsonOutput count(String index, String type, QueryBuilder queryBuilder) {
        return count(Requests.countRequest(index).types(type).query(queryBuilder));
    }

    public JsonOutput count(CountRequest countRequest) {
        CountResponse response = client.count(countRequest).actionGet();
        try {
            XContentBuilder builder = JsonXContent.contentBuilder();
            builder.startObject();
            builder.field(Fields.COUNT, response.count());
            buildBroadcastShardsHeader(builder, response);
            builder.endObject();
            return jsonSerializer.stringToJson(builder.string());
        } catch (IOException e) {
            logger.error("Error while generating the XContent response", e);
            return null;
        }
    }

    public JsonOutput update(String index, String type, String id, String script) {
        return update(new UpdateRequest(index, type, id).script(script));
    }

    public JsonOutput update(UpdateRequest updateRequest) {
        UpdateResponse response = client.update(updateRequest).actionGet();
        try {
            XContentBuilder builder = JsonXContent.contentBuilder();
            builder.startObject()
                    .field(Fields.OK, true)
                    .field(Fields._INDEX, response.index())
                    .field(Fields._TYPE, response.type())
                    .field(Fields._ID, response.id())
                    .field(Fields._VERSION, response.version());

            if (response.getResult() != null) {
                builder.startObject(Fields.GET);
                response.getResult().toXContentEmbedded(builder, ToXContent.EMPTY_PARAMS);
                builder.endObject();
            }

            if (response.matches() != null) {
                builder.startArray(Fields.MATCHES);
                for (String match : response.matches()) {
                    builder.value(match);
                }
                builder.endArray();
            }
            builder.endObject();
            return jsonSerializer.stringToJson(builder.string());
        } catch (IOException e) {
            logger.error("Error while generating the XContent response", e);
            return null;
        }
    }

    public JsonOutput percolate(String index, String type, JsonInput source) {
        PercolateRequest percolateRequest = new PercolateRequest(index, type).source(jsonToString(source));
        return percolate(percolateRequest);
    }

    public JsonOutput percolate(String index, String type, String source) {
        PercolateRequest percolateRequest = new PercolateRequest(index, type).source(source);
        return percolate(percolateRequest);
    }

    public JsonOutput percolate(PercolateRequest percolateRequest) {
        PercolateResponse response = client.percolate(percolateRequest).actionGet();
        try {
            XContentBuilder builder = JsonXContent.contentBuilder();
            builder.startObject();
            builder.field(Fields.OK, true);
            builder.startArray(Fields.MATCHES);
            for (String match : response) {
                builder.value(match);
            }
            builder.endArray();
            builder.endObject();
            return jsonSerializer.stringToJson(builder.string());
        } catch (IOException e) {
            logger.error("Error while generating the XContent response", e);
            return null;
        }
    }

    protected String jsonToString(JsonInput source) {
        return jsonSerializer.jsonToString(source, false);
    }

    protected JsonOutput xContentToJson(ToXContent xContent, boolean needsInit) {
        try {
            XContentBuilder xContentBuilder = JsonXContent.contentBuilder();
            if (needsInit) {
                xContentBuilder.startObject();
            }
            xContent.toXContent(xContentBuilder, ToXContent.EMPTY_PARAMS);
            if (needsInit) {
                xContentBuilder.endObject();
            }
            return jsonSerializer.stringToJson(xContentBuilder.string());
        } catch (IOException e) {
            logger.error("Error while generating the XContent response", e);
            return null;
        }
    }

    Client client() {
        return client;
    }

    @Override
    public String toString() {
        return asString();
    }

    protected abstract String asString();

    static final class Fields {
        static final XContentBuilderString OK = new XContentBuilderString("ok");
        static final XContentBuilderString _INDEX = new XContentBuilderString("_index");
        static final XContentBuilderString _TYPE = new XContentBuilderString("_type");
        static final XContentBuilderString _ID = new XContentBuilderString("_id");
        static final XContentBuilderString _VERSION = new XContentBuilderString("_version");
        static final XContentBuilderString MATCHES = new XContentBuilderString("matches");
        static final XContentBuilderString FOUND = new XContentBuilderString("found");
        static final XContentBuilderString COUNT = new XContentBuilderString("count");
        static final XContentBuilderString GET = new XContentBuilderString("get");
    }
}
