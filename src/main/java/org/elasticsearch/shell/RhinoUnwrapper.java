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
package org.elasticsearch.shell;


import org.mozilla.javascript.*;

import java.util.*;

/**
 * Rhino implementation of the {@link Unwrapper}
 *
 * @author Luca Cavanna
 */
public class RhinoUnwrapper implements Unwrapper {

    /**
     * Converts an object from a script wrapper value to a serializable value valid outside
     * of the Rhino script processor context.
     *
     * This includes converting JavaScript Array objects to Lists of valid objects.
     *
     * @param scriptObject Value to convert from script wrapper object to external object value
     * @return unwrapped and converted value
     */
    @Override
    public Object unwrap(Object scriptObject) {
        if (scriptObject == null) {
            return null;
        } else if (scriptObject instanceof Wrapper) {
            // unwrap a Java object from a JavaScript wrapper
            // recursively call this method to convert the unwrapped value
            return unwrap(((Wrapper) scriptObject).unwrap());
        } else if (scriptObject instanceof Function) {
            return Context.toString(scriptObject);
        } else if (scriptObject instanceof IdScriptableObject) {
            // check for special case Native object wrappers
            String className = ((IdScriptableObject) scriptObject).getClassName();
            // check for special case of the String object
            if (String.class.getSimpleName().equals(className)) {
                return Context.jsToJava(scriptObject, String.class);
            }
            // check for special case of a Date object
            else if (Date.class.getSimpleName().equals(className)) {
                return Context.jsToJava(scriptObject, Date.class);
            } else {
                // a scriptable object will probably indicate a multi-value property set
                // set using a JavaScript associative Array object
                Scriptable values = (Scriptable) scriptObject;
                Object[] propIds = values.getIds();
                // is it a JavaScript associative Array object using Integer indexes?
                if (values instanceof NativeArray && isArray(propIds)) {
                    // convert JavaScript array of values to a List of Serializable objects
                    List<Object> propValues = new ArrayList<Object>(propIds.length);
                    for (Object propIdObject : propIds) {
                        // work on each key in turn
                        // we are only interested in keys that indicate a list of values
                        if (propIdObject instanceof Integer) {
                            Integer propId = (Integer)propIdObject;
                            // getContext the value out for the specified key
                            Object val = values.get(propId, values);
                            // recursively call this method to convert the value
                            propValues.add(unwrap(val));
                        }
                    }
                    return propValues;
                } else {
                    // any other JavaScript object that supports properties (json)
                    Context context = Context.getCurrentContext();
                    return NativeJSON.stringify(context, ScriptRuntime.getGlobal(context), values, null, "  ");
                }
            }
        } else if (scriptObject instanceof Object[]) {
            // convert back a list Object Java values
            Object[] array = (Object[]) scriptObject;
            ArrayList<Object> list = new ArrayList<Object>(array.length);
            for (Object object : array) {
                list.add(unwrap(object));
            }
            return list;
        } else if (scriptObject instanceof Map) {
            // ensure each value in the Map is unwrapped (which may have been an unwrapped NativeMap!)
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) scriptObject;
            Map<Object, Object> copyMap = new HashMap<Object, Object>(map.size());
            for (Object key : map.keySet()) {
                copyMap.put(key, unwrap(map.get(key)));
            }
            return copyMap;
        }
        return scriptObject;
    }

    /**
     * Look at the id's of a native array and try to determine whether it's actually an Array or a Hashmap
     *
     * @param ids id's of the native array
     * @return boolean  true if it's an array, false otherwise (ie it's a map)
     */
    private boolean isArray(final Object[] ids) {
        for (Object id : ids) {
            if (!(id instanceof Integer)) {
                return false;
            }
        }
        return true;
    }
}
