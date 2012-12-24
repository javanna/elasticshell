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
package org.elasticsearch.shell.jline;

import jline.console.completer.Completer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.name.Named;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RhinoCompleter implements Completer {

    private final ScriptableObject scope;

    @Inject
    RhinoCompleter(@Named("shellScope") ScriptableObject scope) {
        this.scope = scope;
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        try {
            return tryComplete(buffer, cursor, candidates);
        } catch(Exception e) {
            //TODO logger!!!!
            return buffer.length();
        }
    }

    public int tryComplete(String buffer, int cursor, List<CharSequence> candidates) {

        //Look backward and collect a list of identifiers separated by dots
        int m = cursor - 1;
        while (m >= 0) {
            char c = buffer.charAt(m);
            if (!Character.isJavaIdentifierPart(c) && c != '.') {
                break;
            }

            m--;
        }

        String namesAndDots = buffer.substring(m+1, cursor);
        //System.out.println("namesAndDots: " + namesAndDots);

        String[] names = namesAndDots.split("\\.", -1);
        //System.out.println("names: " + Arrays.asList(names));

        //looks for the last object whose name is complete
        Scriptable object = this.scope;
        for (int i=0; i < names.length - 1; i++) {
            String currentName = names[i];
            Object val = object.get(currentName, scope);
            if (!(val instanceof Scriptable)) {
                if (object.getPrototype() == null) {
                    return buffer.length(); // no matches
                }
                val = object.getPrototype().get(currentName, scope);
                if (!(val instanceof Scriptable)) {
                    return buffer.length(); // no matches
                }
            }
            object = (Scriptable) val;
        }

        String lastPart = names[names.length-1];
        //System.out.println("lastPart: " + lastPart);

        //Gets the candidates related to the current context (last object whose name is complete)
        Object[] ids = object instanceof ScriptableObject ? ((ScriptableObject)object).getAllIds() : object.getIds();
        //System.out.println("ids: " + Arrays.asList(ids));

        if (ids != null) {
            addCandidates(ids, object, lastPart, candidates);
            //System.out.println("1) candidates: " + candidates);
        }

        if (object.getPrototype() != null && object.getPrototype().getIds() != null) {
            //System.out.println("prototype ids: " + Arrays.asList(object.getPrototype().getIds()));
            addCandidates(object.getPrototype().getIds(), object.getPrototype(), lastPart, candidates);
            //System.out.println("2) candidates: " + candidates);
        }

        Collections.sort(candidates, new Comparator<CharSequence>() {
            @Override
            public int compare(CharSequence o1, CharSequence o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        return buffer.length() - lastPart.length();
    }

    private void addCandidates(Object[] ids, Scriptable object, String lastPart, List<CharSequence> candidates){
        //TODO Filter java methods? we might not want to see toString getClass and stuff all the time
        for (Object idObject : ids) {
            if (idObject instanceof String) {
                String id = (String) idObject;
                if (id.startsWith(lastPart)) {
                    if (object.get(id, object) instanceof Function) {
                        id += "(";
                    }
                    candidates.add(id);
                }
            }
        }
    }
}
