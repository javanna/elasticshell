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

import org.elasticsearch.client.Client;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;

/**
 * @author Luca Cavanna
 *
 * Rhino specific transport client which uses {@link NativeObject} as json representation
 * and is able to convert it to a string needed by elasticsearch
 */
public class RhinoTransportClient extends TransportClient<NativeObject> {

    protected RhinoTransportClient(Client client) {
        super(client);
    }

    @Override
    protected String jsonToString(NativeObject nativeObject) {
        Context context = Context.getCurrentContext();
        Object jsonString = NativeJSON.stringify(context, ScriptRuntime.getGlobal(context), nativeObject, null, null);
        return jsonString.toString();
    }
}
