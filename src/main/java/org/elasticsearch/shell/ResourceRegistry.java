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

import java.io.Closeable;

/**
 * @author Luca Cavanna
 *
 * Registry that takes care of registering {@link Closeable} resources and close them all when needed
 * Doesn't allow to unregister a resource for now, if a resource is closed manually stays in the register
 * When closing all resources this must not be a problem
 */
public interface ResourceRegistry {

    /**
     * Registers a closeable resource
     * @param resource the closeable resource to register
     */
    public void registerResource(Closeable resource);

    /**
     * Close all previously registered resources
     * Not supposed to throw any exception if a resource is already closed
     */
    public void closeAllResources();
}
