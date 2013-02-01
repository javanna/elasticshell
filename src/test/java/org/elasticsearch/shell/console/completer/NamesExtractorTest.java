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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Luca Cavanna
 */
public class NamesExtractorTest {

    private NamesExtractor namesExtractor =  new NamesExtractor();

    @Test
    public void testNameEmptyInput() throws Exception {
        String buffer = "";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 1);
        Assert.assertEquals(names.get(0), "");
    }

    @Test
    public void testNameLastPartEmpty() throws Exception {
        String buffer = "Requests.";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 2);
        Assert.assertEquals(names.get(0), "Requests");
        Assert.assertEquals(names.get(1), "");
    }

    @Test
    public void testNameLastPartNotEmpty() throws Exception {
        String buffer = "Requests.ind";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 2);
        Assert.assertEquals(names.get(0), "Requests");
        Assert.assertEquals(names.get(1), "ind");
    }
    
    @Test
    public void testNamesLastPartEmpty() throws Exception {
        String buffer = "es.index.type.";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 4);
        Assert.assertEquals(names.get(0), "es");
        Assert.assertEquals(names.get(1), "index");
        Assert.assertEquals(names.get(2), "type");
        Assert.assertEquals(names.get(3), "");
    }

    @Test
    public void testNamesLastPartNotEmpty() throws Exception {
        String buffer = "es.index.type.ind";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 4);
        Assert.assertEquals(names.get(0), "es");
        Assert.assertEquals(names.get(1), "index");
        Assert.assertEquals(names.get(2), "type");
        Assert.assertEquals(names.get(3), "ind");
    }

    @Test
    public void testNamesFunctionNoArgsLastPartEmpty() throws Exception {
        String buffer = "Requests.indexRequest().";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 3);
        Assert.assertEquals(names.get(0), "Requests");
        Assert.assertEquals(names.get(1), "indexRequest");
        Assert.assertEquals(names.get(2), "");
    }

    @Test
    public void testNamesFunctionNoArgsLastPartNotEmpty() throws Exception {
        String buffer = "Requests.indexRequest().ind";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 3);
        Assert.assertEquals(names.get(0), "Requests");
        Assert.assertEquals(names.get(1), "indexRequest");
        Assert.assertEquals(names.get(2), "ind");
    }

    @Test
    public void testNamesFunctionsNoArgsLastPartEmpty() throws Exception {
        String buffer = "Requests.indexRequest().index().";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 4);
        Assert.assertEquals(names.get(0), "Requests");
        Assert.assertEquals(names.get(1), "indexRequest");
        Assert.assertEquals(names.get(2), "index");
        Assert.assertEquals(names.get(3), "");
    }

    @Test
    public void testNamesFunctionsNoArgsLastPartNotEmpty() throws Exception {
        String buffer = "Requests.indexRequest().index().ind";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 4);
        Assert.assertEquals(names.get(0), "Requests");
        Assert.assertEquals(names.get(1), "indexRequest");
        Assert.assertEquals(names.get(2), "index");
        Assert.assertEquals(names.get(3), "ind");
    }

    @Test
    public void testNamesFunctionsArgsLastPartEmpty() throws Exception {
        String buffer = "Requests.indexRequest().index('arg2','arg2').";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 4);
        Assert.assertEquals(names.get(0), "Requests");
        Assert.assertEquals(names.get(1), "indexRequest");
        Assert.assertEquals(names.get(2), "index");
        Assert.assertEquals(names.get(3), "");
    }

    @Test
    public void testNamesFunctionsArgsLastPartEmpty2() throws Exception {
        String buffer = "Requests.indexRequest('arg1', 123).index('arg2','arg2').";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 4);
        Assert.assertEquals(names.get(0), "Requests");
        Assert.assertEquals(names.get(1), "indexRequest");
        Assert.assertEquals(names.get(2), "index");
        Assert.assertEquals(names.get(3), "");
    }

    @Test
    public void testNamesFunctionsArgsLastPartNotEmpty() throws Exception {
        String buffer = "Requests.indexRequest('arg1', 123456).index('index_name').ind";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 4);
        Assert.assertEquals(names.get(0), "Requests");
        Assert.assertEquals(names.get(1), "indexRequest");
        Assert.assertEquals(names.get(2), "index");
        Assert.assertEquals(names.get(3), "ind");
    }

    @Test
    public void testNamesNestedFunctionsLastPartEmpty() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(QueryBuilders.matchAllQuery()).";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 4);
        Assert.assertEquals(names.get(0), "SearchSourceBuilder");
        Assert.assertEquals(names.get(1), "searchSource");
        Assert.assertEquals(names.get(2), "query");
        Assert.assertEquals(names.get(3), "");
    }

    @Test
    public void testNamesNestedFunctionsLastPartNotEmpty() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(QueryBuilders.matchAllQuery()).facets().sor";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 5);
        Assert.assertEquals(names.get(0), "SearchSourceBuilder");
        Assert.assertEquals(names.get(1), "searchSource");
        Assert.assertEquals(names.get(2), "query");
        Assert.assertEquals(names.get(3), "facets");
        Assert.assertEquals(names.get(4), "sor");
    }

    @Test
    public void testNamesNestedFunctionsLastPartNotEmpty2() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(Nested1.function1('arg1', Nested2.function2('test'))).facets().sor";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 5);
        Assert.assertEquals(names.get(0), "SearchSourceBuilder");
        Assert.assertEquals(names.get(1), "searchSource");
        Assert.assertEquals(names.get(2), "query");
        Assert.assertEquals(names.get(3), "facets");
        Assert.assertEquals(names.get(4), "sor");
    }

    @Test
    public void testNamesNestedFunctions() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(Nested1.fun";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 2);
        Assert.assertEquals(names.get(0), "Nested1");
        Assert.assertEquals(names.get(1), "fun");
    }

    @Test
    public void testNamesNestedFunctions2() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(Nested1.function1('args').fun";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 3);
        Assert.assertEquals(names.get(0), "Nested1");
        Assert.assertEquals(names.get(1), "function1");
        Assert.assertEquals(names.get(2), "fun");
    }

    @Test
    public void testNamesNestedFunctions3() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query('args', Nested2.function(1,2,3), Nested1.function1('args').fun";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 3);
        Assert.assertEquals(names.get(0), "Nested1");
        Assert.assertEquals(names.get(1), "function1");
        Assert.assertEquals(names.get(2), "fun");
    }

    @Test
    public void testNamesNestedFunctionsCursorInBetween() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query('arg1', QueryBuilders.match, 'arg3')";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length() - 9);
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 2);
        Assert.assertEquals(names.get(0), "QueryBuilders");
        Assert.assertEquals(names.get(1), "match");
    }

    @Test
    public void testNamesNestedFunctionsCursorInBetween2() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().tes_whatever.query('arg1', QueryBuilders.match, 'arg3')";
        List<String> names = namesExtractor.extractNames(buffer, "SearchSourceBuilder.searchSource().tes".length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 3);
        Assert.assertEquals(names.get(0), "SearchSourceBuilder");
        Assert.assertEquals(names.get(1), "searchSource");
        Assert.assertEquals(names.get(2), "tes");
    }

    @Test
    public void testNamesDoubleDot() throws Exception {
        String buffer = "name1.name2..name3";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 4);
        Assert.assertEquals(names.get(0), "name1");
        Assert.assertEquals(names.get(1), "name2");
        Assert.assertEquals(names.get(2), "");
        Assert.assertEquals(names.get(3), "name3");
    }

    @Test
    public void testNamesWrongParentheses() throws Exception {
        String buffer = "name1.name2()).";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 1);
        Assert.assertEquals(names.get(0), "");
    }

    @Test
    public void testNamesConstructor() throws Exception {
        String buffer = "x= new Name2().";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 3);
        Assert.assertEquals(names.get(0), "new");
        Assert.assertEquals(names.get(1), "Name2");
        Assert.assertEquals(names.get(2), "");
    }

    @Test
    public void testNamesConstructor2() throws Exception {
        String buffer = "x= new    Name2().";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);
        Assert.assertEquals(names.size(), 3);
        Assert.assertEquals(names.get(0), "new");
        Assert.assertEquals(names.get(1), "Name2");
        Assert.assertEquals(names.get(2), "");
    }

    @Test
    public void testNamesConstructor3() throws Exception {
        String buffer = "x= new2    Name2().";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);

        //TODO Requests(). returns static methods
        // this is not quite what I would want to see but for now it's ok
        Assert.assertEquals(names.size(), 2);
        Assert.assertEquals(names.get(0), "Name2");
        Assert.assertEquals(names.get(1), "");
    }

    @Test
    public void testNamesConstructor4() throws Exception {
        String buffer = "x= n2w    Name2().";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);

        //TODO Requests(). returns static methods
        // this is not quite what I would want to see but for now it's ok
        Assert.assertEquals(names.size(), 2);
        Assert.assertEquals(names.get(0), "Name2");
        Assert.assertEquals(names.get(1), "");
    }

    @Test
    public void testNamesConstructor5() throws Exception {
        String buffer = "new Name().";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);

        Assert.assertEquals(names.size(), 3);
        Assert.assertEquals(names.get(0), "new");
        Assert.assertEquals(names.get(1), "Name");
        Assert.assertEquals(names.get(2), "");
    }

    @Test
    public void testNamesConstructor6() throws Exception {
        String buffer = "Requests.indexRequest(new Name().";
        List<String> names = namesExtractor.extractNames(buffer, buffer.length());
        Assert.assertNotNull(names);

        Assert.assertEquals(names.size(), 3);
        Assert.assertEquals(names.get(0), "new");
        Assert.assertEquals(names.get(1), "Name");
        Assert.assertEquals(names.get(2), "");
    }

/*
    cursor + 1 with inner parentheses   FilterBuilders.queryFilter(QueryBuilders.)

    FilterBuilders.queryFilter(QueryBuilders.  no suggestions
*/
}
