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

/**
 * @author Luca Cavanna
 *
 * Rhino implementation of the {@link ClientScopeSynchronizerFactory}
 * Allows to client the Rhino specific synchronizer that will keep the client up-to-date
 */
public class RhinoClientScopeSynchronizerFactory implements ClientScopeSynchronizerFactory<RhinoClientNativeJavaObject> {

    @Override
    public ClientScopeSynchronizer<RhinoClientNativeJavaObject> createClientScopeSynchronizer(RhinoClientNativeJavaObject rhinoClientNativeJavaObject) {
        return new RhinoClientScopeSynchronizer(rhinoClientNativeJavaObject);
    }
}
