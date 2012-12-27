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
package org.elasticsearch.shell.console;

import jline.console.completer.Completer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.shell.RhinoShellTopLevel;
import org.elasticsearch.shell.ShellScope;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * JLine completer based on the Rhino engine and its top-level object (scope)
 *
 * @author Luca Cavanna
 */
public class JLineRhinoCompleter implements Completer {

    private static final Logger logger = LoggerFactory.getLogger(JLineRhinoCompleter.class);

    private final ShellScope<RhinoShellTopLevel> shellScope;
    private final List<String> excludeList = new ArrayList<String>();

    @Inject
    JLineRhinoCompleter(ShellScope<RhinoShellTopLevel> shellScope) {
        this.shellScope = shellScope;
        //TODO decide whether we want to filter out all the Object methods or only some of them (notify etc.)
        for (Method method : Object.class.getMethods()) {
            this.excludeList.add(method.getName());
        }
        this.excludeList.add("class");
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        try {
            return tryComplete(buffer, cursor, candidates);
        } catch(Exception e) {
            logger.warn("Exception while trying to complete buffer {}, cursor {}", buffer, cursor,e);
            return buffer.length();
        }
    }

    public int tryComplete(String buffer, int cursor, List<CharSequence> candidates) {

        logger.debug("Trying to complete buffer {}, cursor {}", buffer, cursor);

        //Look backward and collect a list of identifiers separated by dots
        int m = cursor - 1;
        while (m >= 0) {
            char c = buffer.charAt(m);
            if (!Character.isJavaIdentifierPart(c) && c != '.') {
                break;
            }

            m--;
        }

        String namesAndDots = buffer.substring(m + 1, cursor);
        logger.debug("NamesAndDots: {}", namesAndDots);

        String[] names = namesAndDots.split("\\.", -1);
        if (logger.isDebugEnabled()) {
            logger.debug("Names: {}", Arrays.asList(names));
        }

        //looks for the last object whose name is complete
        Scriptable object = this.shellScope.get();
        for (int i = 0; i < names.length - 1; i++) {
            String currentName = names[i];
            Object val = object.get(currentName, this.shellScope.get());
            if (!(val instanceof Scriptable)) {
                if (object.getPrototype() == null) {
                    return buffer.length(); // no matches
                }
                val = object.getPrototype().get(currentName, this.shellScope.get());
                if (!(val instanceof Scriptable)) {
                    return buffer.length(); // no matches
                }
            }
            object = (Scriptable) val;
        }

        String lastPart = names[names.length - 1];
        logger.debug("LastPart: {}", lastPart);

        //Gets the candidates related to the current context (last object whose name is complete)
        Object[] ids = object instanceof ScriptableObject ? ((ScriptableObject) object).getAllIds() : object.getIds();


        if (ids != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Ids: {}", Arrays.asList(ids));
            }
            addCandidates(ids, object, lastPart, candidates);
            logger.debug("Candidates after 1st round: {}", candidates);
        }

        if (object.getPrototype() != null && object.getPrototype().getIds() != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Prototype ids: {}", Arrays.asList(object.getPrototype().getIds()));
            }
            addCandidates(object.getPrototype().getIds(), object.getPrototype(), lastPart, candidates);
            logger.debug("Candidates after 2nd round (object prototype): {}", candidates);
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
        for (Object idObject : ids) {
            if (idObject instanceof String) {
                String id = (String) idObject;
                if (id.startsWith(lastPart) && !excludeList.contains(id)) {
                    if (object.get(id, object) instanceof Function) {
                        id += "(";
                    }
                    candidates.add(id);
                }
            }
        }
    }
}
