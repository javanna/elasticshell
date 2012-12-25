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
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.shell.RhinoShellTopLevel;
import org.elasticsearch.shell.ShellScope;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ScriptableObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClientFactory {

    private final ShellScope<RhinoShellTopLevel> shellScope;

    @Inject
    public ClientFactory(ShellScope<RhinoShellTopLevel> shellScope) {
        this.shellScope = shellScope;
    }

    public NativeJavaObject newNodeClient() {
        return newNodeClient("elasticsearch");
    }

    public NativeJavaObject newNodeClient(String clusterName) {

        Settings settings = ImmutableSettings.settingsBuilder().put("node.name", "elasticsearch-shell").build();
        Node node  = NodeBuilder.nodeBuilder().clusterName(clusterName).client(true).settings(settings).build();
        node.start();
        Client client = node.client();
        NodeClient nodeClient = new NodeClient(node, client);

        NativeJavaObject nativeJavaObject = new NativeJavaObject(shellScope.get(), nodeClient, NodeClient.class);
        nativeJavaObject.setPrototype(Context.getCurrentContext().newObject(shellScope.get()));


        Thread thread = new Thread(new IndexRunnable(shellScope.get(), nativeJavaObject, client));
        thread.start();

        //shellScope.defineProperty("test", nativeJavaObject, ScriptableObject.DONTENUM);

        return nativeJavaObject;
    }

    class IndexRunnable implements Runnable {

        private final ScriptableObject shellScope;
        private final Client client;
        private final NativeJavaObject clientNativeJavaObject;

        Map<String, Set<String>> indexes = new HashMap<String, Set<String>>();

        public IndexRunnable(ScriptableObject shellScope, NativeJavaObject clientNativeJavaObject, Client client) {
            this.client = client;
            this.clientNativeJavaObject = clientNativeJavaObject;
            this.shellScope = shellScope;
        }

        //TODO shutdown thread!!!
        //TODO use ScheduledExecutorService


        public void run() {

            while(true) {

                ClusterStateResponse response = client.admin().cluster().prepareState().setFilterBlocks(true)
                        .setFilterRoutingTable(true).setFilterNodes(true).execute().actionGet();

                Map<String, Set<String>> newIndexes = new HashMap<String, Set<String>>();
                for (IndexMetaData indexMetaData : response.state().metaData().indices().values()) {
                    newIndexes.put(indexMetaData.index(), indexMetaData.mappings().keySet());
                    for (String alias : indexMetaData.aliases().keySet()) {
                        newIndexes.put(alias, indexMetaData.mappings().keySet());
                    }
                }

                for (String index : indexes.keySet()) {
                    clientNativeJavaObject.delete(index);
                }

                Context context = Context.enter();
                try {

                    //TODO test with index changing and aliases

                    for (Map.Entry<String, Set<String>> entry : newIndexes.entrySet()) {
                        String index = entry.getKey();
                        IndexInternalClient indexClient = new IndexInternalClient(client, index);
                        NativeJavaObject indexNativeJavaObject = new NativeJavaObject(shellScope, indexClient, IndexInternalClient.class);
                        indexNativeJavaObject.setPrototype(context.newObject(shellScope));
                        clientNativeJavaObject.getPrototype().put(entry.getKey(), clientNativeJavaObject.getPrototype(), indexNativeJavaObject);

                        for (String type : entry.getValue()) {
                            NativeJavaObject typeNativeJavaObject = new NativeJavaObject(shellScope, new TypeInternalClient(client, type), TypeInternalClient.class);
                            typeNativeJavaObject.setPrototype(context.newObject(shellScope));
                            indexNativeJavaObject.getPrototype().put(type, indexNativeJavaObject.getPrototype(), typeNativeJavaObject);
                        }
                    }

                } finally {
                    Context.exit();
                }

                this.indexes = newIndexes;

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

    }
}
