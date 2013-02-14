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
 * Looks within the Rhino scope to find available objects.
 * Uses reflection to find suggestions given the return type of a method (fluent interface)
 * In case of methods with different signatures (same name but different return types) all the possible matches
 * will be merged and provided as suggestions
 *
 * It doesn't support auto-suggestions for array elements
 * It doesn't complete packages with contained classes
 *
 * @author Luca Cavanna
 */
public class JLineRhinoCompleter implements Completer {

    private static final Logger logger = LoggerFactory.getLogger(JLineRhinoCompleter.class);

    private final ShellScope<RhinoShellTopLevel> shellScope;
    private final List<String> excludeList = new ArrayList<String>();

    private final IdentifierTokenizer idTokenizer = new IdentifierTokenizer();

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

        List<Identifier> identifiers = idTokenizer.tokenize(buffer, cursor);
        logger.debug("Identifiers: {}", identifiers);

        //looks for the last object whose name is complete
        Scriptable object = this.shellScope.get();
        Set<Class<?>> returnTypes = null;
        boolean newFound = false;
        for (int i = 0; i < identifiers.size() - 1; i++) {
            String currentName = identifiers.get(i).getName();

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
                return getLastPartStartPosition(identifiers); // no matches
            }

            if (val instanceof RhinoCustomNativeJavaClass && newFound) {
                Class<?> clazz = ((RhinoCustomNativeJavaClass) val).getClazz();
                returnTypes = new HashSet<Class<?>>();
                returnTypes.add(clazz);

                for (int j = i + 1; j < identifiers.size() - 1; j++) {
                    if (returnTypes.isEmpty()) {
                        return getLastPartStartPosition(identifiers); // no matches
                    }

                    //gets all the possible return types given the input types
                    returnTypes = findReturnTypes(returnTypes, identifiers.get(j).getName());
                }

                break;

            }

            //If we have a java method we won't find it within the parent object
            //we need to lookup the remaining elements through reflections
            if (val instanceof RhinoCustomNativeJavaMethod) {
                RhinoCustomNativeJavaMethod nativeJavaMethod = (RhinoCustomNativeJavaMethod) val;
                //gets the return types given the native java method
                returnTypes = nativeJavaMethod.getReturnTypes();

                for (int j = i + 1; j < identifiers.size() - 1; j++) {
                    if (returnTypes.isEmpty()) {
                        return getLastPartStartPosition(identifiers); // no matches
                    }
                    //gets all the possible return types given the input types
                    returnTypes = findReturnTypes(returnTypes, identifiers.get(j).getName());
                }

                break;
            }

            if (!(val instanceof Scriptable)) {
                if (object.getPrototype() == null) {
                    return getLastPartStartPosition(identifiers); // no matches
                }
                val = object.getPrototype().get(currentName, this.shellScope.get());
                if (!(val instanceof Scriptable)) {
                    return getLastPartStartPosition(identifiers); // no matches
                }
            }
            object = (Scriptable)val;
        }

        Identifier lastPart = identifiers.get(identifiers.size() - 1);
        logger.debug("LastPart: {}", lastPart);

        if (returnTypes == null) {
            findCandidatesInScriptable(object, lastPart.getName(), candidates);
        } else {
            findCandidatesInReturnTypes(returnTypes, lastPart.getName(), candidates);
        }

        return lastPart.getFirstPosition();
    }

    private int getLastPartStartPosition(List<Identifier> identifiers) {
        return identifiers.get(identifiers.size() - 1).getFirstPosition();
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
                            id += "()";
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
                returnTypes.add(method.getName() + "()");
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