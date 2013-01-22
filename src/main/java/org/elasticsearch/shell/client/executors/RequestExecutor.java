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
package org.elasticsearch.shell.client.executors;

import org.elasticsearch.action.ActionRequest;

/**
 * @author Luca Cavanna
 *
 * Executes an elasticsearch {@link ActionRequest} and returns a shell native json
 * @param <Request> the type of the <code>ActionRequest</code>
 * @param <JsonOutput> the type of the native json, depending on the script engine
 */
public interface RequestExecutor<Request extends ActionRequest<Request>, JsonOutput> {
    /**
     * Executes the <code>ActionRequest</code> given as input
     * @param request the elasticsearch ActionRequest to execute
     * @return the result of the execution as json native
     */
    JsonOutput execute(Request request);
}
