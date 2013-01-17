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

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * Shell scope which wraps the real scope object that depends on the underlying engine
 *
 * @author Luca Cavanna
 */
public abstract class ShellScope<Scope> {

    private static final Logger logger = LoggerFactory.getLogger(ShellScope.class);

    private final Scope scope;
    private final List<Closeable> resources = new ArrayList<Closeable>();

    /**
     * Creates a new <code>ShellScope</code> given the actual scope object
     * @param scope the actual scope object that depends on the engine in use
     */
    ShellScope(Scope scope) {
        this.scope = scope;
        registerJavaClass(Requests.class);
        registerJavaClass(SearchSourceBuilder.class);
        registerJavaClass(QueryBuilders.class);
        registerJavaClass(FilterBuilders.class);
        registerJavaClass(SortBuilders.class);
        registerJavaClass(FacetBuilders.class);
        registerJavaClass(UpdateRequest.class);
    }

    /**
     * Allows to retrieve the actual scope object
     * @return the actual scope object depending on the engine in use
     */
    public Scope get() {
        return scope;
    }

    /**
     * Registers a java object to the scope, depending on the script engine
     * @param name name to use to register the object
     * @param javaObject the java object to register
     * @param <T> the type of the object to register
     */
    public abstract <T> void registerJavaObject(String name, T javaObject);

    /**
     * Registers a java class to the scope, so that it is available
     * without the need to import it manually
     * @param clazz the class to register to the scope
     */
    public abstract void registerJavaClass(Class<?> clazz);

    /**
     * Registers a {@link Closeable} resource that will be closed when the close method will be invoked
     * @param resource the closeable resource to register
     */
    public void registerResource(Closeable resource) {
        resources.add(resource);
    }

    /**
     * Closes all the underlying {@link Closeable} resources previously registered
     */
    public void close() {
        logger.debug("Closing the shell scope");
        for (Closeable resource : resources) {
            try {
                resource.close();
                logger.debug("Resource {} closed", resource);
            } catch (Throwable t) {
                logger.error("Error while closing resource", t);
            }
        }
    }
}
