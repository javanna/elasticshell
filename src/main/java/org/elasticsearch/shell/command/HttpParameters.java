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
package org.elasticsearch.shell.command;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Luca Cavanna
 *
 * Http components compatible builder for http parameters needed for a POST or PUT request
 */
public class HttpParameters implements Iterable<NameValuePair> {

    private List<NameValuePair> parameters = new ArrayList<NameValuePair>();

    public static HttpParameters builder() {
        return new HttpParameters();
    }

    public HttpParameters add(String name, String value) {
        parameters.add(new BasicNameValuePair(name, value));
        return this;
    }

    @Override
    public Iterator<NameValuePair> iterator() {
        return parameters.iterator();
    }

    @Override
    public String toString() {
        return parameters.toString();
    }
}
