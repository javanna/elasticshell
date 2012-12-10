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
package org.elasticsearch.shell.rhino;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.mozilla.javascript.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * Value Converter to marshal objects between Java and Javascript.
 *
 *
 */
public final class ScriptValueConverter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }


    /**
     * Private constructor - methods are static
     */
    private ScriptValueConverter() {
    }

    /**
     * Convert an object from a script wrapper value to a serializable value valid outside
     * of the Rhino script processor context.
     * <p/>
     * This includes converting JavaScript Array objects to Lists of valid objects.
     *
     * @param value Value to convert from script wrapper object to external object value.
     * @return unwrapped and converted value.
     */
    public static Object unwrapValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Wrapper) {
            // unwrap a Java object from a JavaScript wrapper
            // recursively call this method to convert the unwrapped value
            return unwrapValue(((Wrapper) value).unwrap());
        } else if (value instanceof Function) {
            return Context.toString(value);
        } else if (value instanceof IdScriptableObject) {
            // check for special case Native object wrappers
            String className = ((IdScriptableObject) value).getClassName();
            // check for special case of the String object
            if (String.class.getSimpleName().equals(className)) {
                return Context.jsToJava(value, String.class);
            }
            // check for special case of a Date object
            else if (Date.class.getSimpleName().equals(className)) {
                return Context.jsToJava(value, Date.class);
            } else {
                // a scriptable object will probably indicate a multi-value property set
                // set using a JavaScript associative Array object
                Scriptable values = (Scriptable) value;
                Object[] propIds = values.getIds();
                // is it a JavaScript associative Array object using Integer indexes?
                if (values instanceof NativeArray && isArray(propIds)) {
                    // convert JavaScript array of values to a List of Serializable objects
                    List<Object> propValues = new ArrayList<Object>(propIds.length);
                    for (int i = 0; i < propIds.length; i++) {
                        // work on each key in turn
                        Integer propId = (Integer) propIds[i];
                        // we are only interested in keys that indicate a list of values
                        if (propId instanceof Integer) {
                            // getContext the value out for the specified key
                            Object val = values.get(propId, values);
                            // recursively call this method to convert the value
                            propValues.add(unwrapValue(val));
                        }
                    }
                    return propValues;
                } else {
                    // any other JavaScript object that supports properties - convert to a Map of objects
                    Map<String, Object> propValues = new HashMap<String, Object>(propIds.length);
                    for (int i = 0; i < propIds.length; i++) {
                        // work on each key in turn
                        Object propId = propIds[i];

                        // we are only interested in keys that indicate a list of values
                        if (propId instanceof String) {
                            // getContext the value out for the specified key
                            Object val = values.get((String) propId, values);
                            // recursively call this method to convert the value
                            propValues.put((String) propId, unwrapValue(val));
                        }
                    }

                    //TODO do we really need to use jackson only to show a proper json given the java map
                    //that we build from the javascript object? seems like an overkill
                    StringWriter jsonWriter = new StringWriter();

                    try {
                        OBJECT_MAPPER.writeValue(jsonWriter, propValues);
                        return jsonWriter.toString();
                    } catch (IOException e) {
                        return propValues;
                    }
                }
            }
        } else if (value instanceof Object[]) {
            // convert back a list Object Java values
            Object[] array = (Object[]) value;
            ArrayList<Object> list = new ArrayList<Object>(array.length);
            for (int i = 0; i < array.length; i++) {
                list.add(unwrapValue(array[i]));
            }
            return list;
        } else if (value instanceof Map) {
            // ensure each value in the Map is unwrapped (which may have been an unwrapped NativeMap!)
            Map<Object, Object> map = (Map<Object, Object>) value;
            Map<Object, Object> copyMap = new HashMap<Object, Object>(map.size());
            for (Object key : map.keySet()) {
                copyMap.put(key, unwrapValue(map.get(key)));
            }
            return copyMap;
        }
        return value;
    }

    /**
     * Convert an object from any repository serialized value to a valid script object.
     * This includes converting Collection multi-value properties into JavaScript Array objects.
     *
     * @param scope Scripting scope
     * @param value Property value
     * @return Value safe for scripting usage
     */
    public static Object wrapValue(Scriptable scope, Object value) {
        // perform conversions from Java objects to JavaScript scriptable instances
        if (value == null) {
            return null;
        } else if (value instanceof Date) {
            // convert Date to JavaScript native Date object
            // call the "Date" constructor on the root scope object - passing in the millisecond
            // value from the Java date - this will construct a JavaScript Date with the same value
            Date date = (Date) value;
            value = ScriptRuntime.newObject(
                    Context.getCurrentContext(), scope, Date.class.getSimpleName(), new Object[]{date.getTime()});
        } else if (value instanceof Collection) {
            // recursively convert each value in the collection
            Collection<Object> collection = (Collection<Object>) value;
            Object[] array = new Object[collection.size()];
            int index = 0;
            for (Object obj : collection) {
                array[index++] = wrapValue(scope, obj);
            }
            // convert array to a native JavaScript Array
            value = Context.getCurrentContext().newArray(scope, array);
        } /*else if (value instanceof Map) {
            value = new NativeMap(scope, (Map) value);
        }*/

        // simple numbers, strings and booleans are wrapped automatically by Rhino

        return value;
    }

    /**
     * Look at the id's of a native array and try to determine whether it's actually an Array or a Hashmap
     *
     * @param ids id's of the native array
     * @return boolean  true if it's an array, false otherwise (ie it's a map)
     */
    private static boolean isArray(final Object[] ids) {
        boolean result = true;
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] instanceof Integer == false) {
                result = false;
                break;
            }
        }
        return result;
    }
}