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
package org.elasticsearch.shell.console.completer;

import jline.console.completer.Completer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.shell.RhinoShellTopLevel;
import org.elasticsearch.shell.ShellScope;
import org.mozilla.javascript.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
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

    private final NamesExtractor namesExtractor = new NamesExtractor();

    @Inject
    JLineRhinoCompleter(ShellScope<RhinoShellTopLevel> shellScope) {
        this.shellScope = shellScope;
        for (Method method : Object.class.getMethods()) {
            if (!"toString".equals(method.getName())
                && !"equals".equals(method.getName())
                && !"getClass".equals(method.getName())) {
                this.excludeList.add(method.getName());
            }
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

        logger.debug("Trying to complete buffer [{}], cursor {}", buffer, cursor);

        List<String> names = namesExtractor.extractNames(buffer, cursor);

        logger.debug("Names: {}", names);

        //looks for the last object whose name is complete
        Scriptable object = this.shellScope.get();
        Set<Class<?>> returnTypes = null;
        boolean newFound = false;
        for (int i = 0; i < names.size() - 1; i++) {
            String currentName = names.get(i);

            //when we get to the new keyword we just go ahead with the next name (we'll take into account later)
            if ("new".equals(currentName)) {
                newFound = true;
                continue;
            }

            Object val;
            try {
                val = object.get(currentName, this.shellScope.get());
                logger.debug("Found {} while looking for [{}] in {}", val.getClass(), currentName, object.getClass());

                if (newFound && !(val instanceof NativeJavaPackage) && !(val instanceof NativeJavaClass)) {
                    newFound = false;
                }

            } catch(EvaluatorException e) {
                logger.debug("Error while looking for [{}] in {}", currentName, object.getClass());
                return buffer.length(); //no matches
            }

            if (val instanceof RhinoCustomNativeJavaClass && newFound) {
                Class<?> clazz = ((RhinoCustomNativeJavaClass) val).getClazz();
                returnTypes = new HashSet<Class<?>>();
                returnTypes.add(clazz);

                for (int j = i + 1; j < names.size() - 1; j++) {
                    if (returnTypes.isEmpty()) {
                        return buffer.length(); // no matches
                    }

                    //gets all the possible return types given the input types
                    returnTypes = findReturnTypes(returnTypes, names.get(j));
                }

                break;

            }

            //If we have a java method we won't find it within the parent object
            //we need to search through reflections for the remaining elements
            if (val instanceof RhinoCustomNativeJavaMethod) {
                RhinoCustomNativeJavaMethod nativeJavaMethod = (RhinoCustomNativeJavaMethod) val;
                //gets the return types given the native java method
                returnTypes = nativeJavaMethod.getReturnTypes();

                for (int j = i + 1; j < names.size() - 1; j++) {
                    if (returnTypes.isEmpty()) {
                        return buffer.length(); // no matches
                    }
                    //gets all the possible return types given the input types
                    returnTypes = findReturnTypes(returnTypes, names.get(j));
                }

                break;
            }

            if (!(val instanceof Scriptable)) {
                if (object.getPrototype() == null) {
                    return buffer.length(); // no matches
                }
                val = object.getPrototype().get(currentName, this.shellScope.get());
                if (!(val instanceof Scriptable)) {
                    return buffer.length(); // no matches
                }
            }
            object = (Scriptable)val;
        }

        String lastPart = names.get(names.size() - 1);
        logger.debug("LastPart: {}", lastPart);

        if (returnTypes == null) {
            findCandidatesInScriptable(object, lastPart, candidates);
        } else {
            findCandidatesInReturnTypes(returnTypes, lastPart, candidates);
        }


        Collections.sort(candidates, new Comparator<CharSequence>() {
            @Override
            public int compare(CharSequence o1, CharSequence o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        return buffer.length() - lastPart.length();
    }

    private void findCandidatesInScriptable(Scriptable object, String prefix, List<CharSequence> candidates) {
        //Gets the candidates related to the current context (last object whose name is complete)
        Object[] ids = object instanceof ScriptableObject ? ((ScriptableObject) object).getAllIds() : object.getIds();

        if (ids != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Ids: {}", Arrays.asList(ids));
            }
            addCandidatesFromScriptable(ids, object, prefix, candidates);
            logger.debug("Candidates after 1st round: {}", candidates);
        }

        if (object.getPrototype() != null && object.getPrototype().getIds() != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Prototype ids: {}", Arrays.asList(object.getPrototype().getIds()));
            }
            addCandidatesFromScriptable(object.getPrototype().getIds(), object.getPrototype(), prefix, candidates);
            logger.debug("Candidates after 2nd round (object prototype): {}", candidates);
        }
    }

    private void addCandidatesFromScriptable(Object[] ids, Scriptable parent, String lastPart, List<CharSequence> candidates){
        for (Object idObject : ids) {
            if (idObject instanceof String) {
                String id = (String) idObject;
                if (id.startsWith(lastPart) && !excludeList.contains(id)) {
                    Object object = parent.get(id, parent);
                    if (!(object instanceof UniqueTag)) {
                        if (object instanceof Function && !(object instanceof NativeJavaClass)) {
                            id += "(";
                        }
                        candidates.add(id);
                    }
                }
            }
        }
    }

    private Set<Class<?>> findReturnTypes(Set<Class<?>>  classes, String methodName) {
        Set<Class<?>> returnTypes = new HashSet<Class<?>>();
        for (Class<?> clazz : classes) {
            returnTypes.addAll(findReturnTypes(clazz, methodName));
        }
        return returnTypes;
    }

    //TODO performance might suck! Caching?
    private Set<Class<?>> findReturnTypes(Class<?> clazz, String methodName) {
        Set<Class<?>> returnTypes = new HashSet<Class<?>>();
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                returnTypes.add(method.getReturnType());
            }
        }
        return returnTypes;
    }

    private void findCandidatesInReturnTypes(Set<Class<?>> returnTypes, String prefix, List<CharSequence> candidates) {
        for (Class<?> clazz : returnTypes) {
            candidates.addAll(findCandidates(clazz, prefix));
        }
    }

    private Set<String> findCandidates(Class<?> clazz, String methodPrefix) {
        Set<String> returnTypes = new HashSet<String>();
        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith(methodPrefix) && !excludeList.contains(method.getName())) {
                returnTypes.add(method.getName() + "(");
            }
        }

        for (Field field : clazz.getFields()) {
            if (field.getName().startsWith(methodPrefix) && !excludeList.contains(field.getName())) {
                returnTypes.add(field.getName());
            }
        }
        return returnTypes;
    }

    ShellScope<RhinoShellTopLevel> getScope() {
        return shellScope;
    }
}
