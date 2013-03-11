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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.RhinoCustomNativeJavaObject;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Cavanna
 *
 * Keeps up-to-date the Rhino shell scope while the indexes and types available in the elasticsearch cluster change
 */
public class RhinoClientScopeSynchronizer extends ClientScopeSynchronizer {

    private static final Logger logger = LoggerFactory.getLogger(RhinoClientScopeSynchronizer.class);

    private final RhinoClientNativeJavaObject shellNativeClient;

    RhinoClientScopeSynchronizer(RhinoClientNativeJavaObject shellNativeClient) {
        super(shellNativeClient.shellClient());
        this.shellNativeClient = shellNativeClient;
    }

    @Override
    protected void registerIndexAndTypes(InternalIndexClient indexClient, InternalTypeClient... typeClients) {
        //We are in a different thread, therefore we cannot rely on the current shell context but we need to create a new one
        Context context = Context.enter();
        try {
            //register index
            NativeJavaObject indexNativeJavaObject = new RhinoCustomNativeJavaObject(shellNativeClient.getParentScope(), indexClient, InternalIndexClient.class);
            indexNativeJavaObject.setPrototype(context.newObject(shellNativeClient.getParentScope()));

            if (typeClients != null) {
                //register types
                for (InternalTypeClient typeClient : typeClients) {
                    NativeJavaObject typeNativeJavaObject = new RhinoCustomNativeJavaObject(shellNativeClient.getParentScope(), typeClient, InternalTypeClient.class);
                    ScriptableObject.putProperty(indexNativeJavaObject, typeClient.typeName(), typeNativeJavaObject);
                }
            }

            logger.trace("Adding index {} to shell native client", indexClient.indexName());
            ScriptableObject.putProperty(shellNativeClient, indexClient.indexName(), indexNativeJavaObject);
        } finally {
            Context.exit();
        }
    }

    @Override
    protected void unregisterIndex(Index index) {
        shellNativeClient.getPrototype().delete(index.name());
    }
}
