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
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.node.Node;
import org.elasticsearch.shell.RhinoShellTopLevel;
import org.elasticsearch.shell.ShellScope;
import org.elasticsearch.shell.json.JsonSerializer;
import org.elasticsearch.shell.scheduler.Scheduler;
import org.mozilla.javascript.NativeObject;

/**
 * @author Luca Cavanna
 *
 * Rhino specific {@link ClientFactory} implementation
 */
public class RhinoClientFactory extends AbstractClientFactory<RhinoClientNativeJavaObject, RhinoShellTopLevel, NativeObject, Object> {

    @Inject
    public RhinoClientFactory(ShellScope<RhinoShellTopLevel> shellScope, SchedulerHolder schedulerHolder, JsonSerializer<NativeObject, Object> jsonSerializer) {
        super(shellScope, jsonSerializer, schedulerHolder.scheduler);
    }

    static class SchedulerHolder {
        @Inject(optional = true)
        Scheduler scheduler;
    }

    @Override
    protected NodeClient newNodeClient(Node node, Client client) {
        return new NodeClient<NativeObject, Object>(node, client, jsonSerializer);
    }

    @Override
    protected TransportClient newTransportClient(Client client) {
        return new TransportClient<NativeObject, Object>(client, jsonSerializer);
    }

    @Override
    public RhinoClientNativeJavaObject wrapClient(AbstractClient shellClient) {
        return new RhinoClientNativeJavaObject(shellScope.get(), shellClient);
    }

    @Override
    protected ClientScopeSyncRunnable createScopeSyncRunnable(RhinoClientNativeJavaObject rhinoClientNativeJavaObject) {
        return new RhinoClientScopeSyncRunnable(rhinoClientNativeJavaObject);
    }
}
