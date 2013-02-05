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
public class IdentifierTokenizerTest {

    private IdentifierTokenizer identifiersExtractor =  new IdentifierTokenizer();

    @Test
    public void testNameEmptyInput() throws Exception {
        String buffer = "";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 1);
        Assert.assertEquals(identifiers.get(0).getName(), "");
        Assert.assertEquals(identifiers.get(0).getOffset(), 0);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 0);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
    }

    @Test
    public void testNameLastPartEmpty() throws Exception {
        String buffer = "Requests.";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 2);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getOffset(), 8);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "");
        Assert.assertEquals(identifiers.get(1).getOffset(), 0);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 9);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 9);
    }

    @Test
    public void testNameLastPartNotEmpty() throws Exception {
        String buffer = "Requests.ind";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 2);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getOffset(), 8);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "ind");
        Assert.assertEquals(identifiers.get(1).getOffset(), 3);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 11);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 9);
    }
    
    @Test
    public void testNamesLastPartEmpty() throws Exception {
        String buffer = "es.index.type.";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "es");
        Assert.assertEquals(identifiers.get(0).getOffset(), 2);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 1);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "index");
        Assert.assertEquals(identifiers.get(1).getOffset(), 5);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 3);
        Assert.assertEquals(identifiers.get(2).getName(), "type");
        Assert.assertEquals(identifiers.get(2).getOffset(), 4);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 12);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 9);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getOffset(), 0);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 14);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 14);
    }

    @Test
    public void testNamesLastPartNotEmpty() throws Exception {
        String buffer = "es.index.type.ind";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "es");
        Assert.assertEquals(identifiers.get(0).getOffset(), 2);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 1);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "index");
        Assert.assertEquals(identifiers.get(1).getOffset(), 5);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 3);
        Assert.assertEquals(identifiers.get(2).getName(), "type");
        Assert.assertEquals(identifiers.get(2).getOffset(), 4);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 12);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 9);
        Assert.assertEquals(identifiers.get(3).getName(), "ind");
        Assert.assertEquals(identifiers.get(3).getOffset(), 3);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 16);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 14);
    }

    @Test
    public void testNamesFunctionNoArgsLastPartEmpty() throws Exception {
        String buffer = "Requests.indexRequest().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getOffset(), 8);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getOffset(), 14);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 22);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "");
        Assert.assertEquals(identifiers.get(2).getOffset(), 0);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 24);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 24);
    }

    @Test
    public void testNamesFunctionNoArgsLastPartNotEmpty() throws Exception {
        String buffer = "Requests.indexRequest().ind";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getOffset(), 8);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getOffset(), 14);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 22);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "ind");
        Assert.assertEquals(identifiers.get(2).getOffset(), 3);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 26);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 24);
    }

    @Test
    public void testNamesFunctionsNoArgsLastPartEmpty() throws Exception {
        String buffer = "Requests.indexRequest().index().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getOffset(), 8);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getOffset(), 14);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 22);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "index");
        Assert.assertEquals(identifiers.get(2).getOffset(), 7);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 30);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 24);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getOffset(), 0);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 32);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 32);
    }

    @Test
    public void testNamesFunctionsNoArgsLastPartNotEmpty() throws Exception {
        String buffer = "Requests.indexRequest().index().ind";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getOffset(), 8);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getOffset(), 14);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 22);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "index");
        Assert.assertEquals(identifiers.get(2).getOffset(), 7);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 30);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 24);
        Assert.assertEquals(identifiers.get(3).getName(), "ind");
        Assert.assertEquals(identifiers.get(3).getOffset(), 3);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 34);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 32);
    }

    @Test
    public void testNamesFunctionsArgsLastPartEmpty() throws Exception {
        String buffer = "Requests.indexRequest().index('arg2','arg2').";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getOffset(), 8);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getOffset(), 14);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 22);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "index");
        Assert.assertEquals(identifiers.get(2).getOffset(), 20);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 43);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 24);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getOffset(), 0);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 45);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 45);
    }

    @Test
    public void testNamesFunctionsArgsLastPartEmpty2() throws Exception {
        String buffer = "Requests.indexRequest('arg1', 123).index('arg2','arg2').";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getOffset(), 8);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getOffset(), 25);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 33);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "index");
        Assert.assertEquals(identifiers.get(2).getOffset(), 20);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 54);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 35);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getOffset(), 0);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 56);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 56);
    }

    @Test
    public void testNamesFunctionsArgsLastPartNotEmpty() throws Exception {
        String buffer = "Requests.indexRequest('arg1', 123456).index('index_name').ind";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getOffset(), 8);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getOffset(), 28);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 36);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "index");
        Assert.assertEquals(identifiers.get(2).getOffset(), 19);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 56);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 38);
        Assert.assertEquals(identifiers.get(3).getName(), "ind");
        Assert.assertEquals(identifiers.get(3).getOffset(), 3);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 60);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 58);
    }

    @Test
    public void testNamesNestedFunctionsLastPartEmpty() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(QueryBuilders.matchAllQuery()).";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "SearchSourceBuilder");
        Assert.assertEquals(identifiers.get(0).getOffset(), 19);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 18);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "searchSource");
        Assert.assertEquals(identifiers.get(1).getOffset(), 14);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 33);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getName(), "query");
        Assert.assertEquals(identifiers.get(2).getOffset(), 36);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 70);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 35);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getOffset(), 0);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 72);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 72);
    }

    @Test
    public void testNamesNestedFunctionsLastPartNotEmpty() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(QueryBuilders.matchAllQuery()).facets().sor";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 5);
        Assert.assertEquals(identifiers.get(0).getName(), "SearchSourceBuilder");
        Assert.assertEquals(identifiers.get(0).getOffset(), 19);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 18);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "searchSource");
        Assert.assertEquals(identifiers.get(1).getOffset(), 14);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 33);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getName(), "query");
        Assert.assertEquals(identifiers.get(2).getOffset(), 36);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 70);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 35);
        Assert.assertEquals(identifiers.get(3).getName(), "facets");
        Assert.assertEquals(identifiers.get(3).getOffset(), 8);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 79);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 72);
        Assert.assertEquals(identifiers.get(4).getName(), "sor");
        Assert.assertEquals(identifiers.get(4).getOffset(), 3);
        Assert.assertEquals(identifiers.get(4).getLastPosition(), 83);
        Assert.assertEquals(identifiers.get(4).getFirstPosition(), 81);
    }

    @Test
    public void testNamesNestedFunctionsLastPartNotEmpty2() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(Nested1.function1('arg1', Nested2.function2('test'))).facets().sor";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 5);
        Assert.assertEquals(identifiers.get(0).getName(), "SearchSourceBuilder");
        Assert.assertEquals(identifiers.get(0).getOffset(), 19);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 18);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "searchSource");
        Assert.assertEquals(identifiers.get(1).getOffset(), 14);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 33);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getName(), "query");
        Assert.assertEquals(identifiers.get(2).getOffset(), 59);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 93);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 35);
        Assert.assertEquals(identifiers.get(3).getName(), "facets");
        Assert.assertEquals(identifiers.get(3).getOffset(), 8);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 102);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 95);
        Assert.assertEquals(identifiers.get(4).getName(), "sor");
        Assert.assertEquals(identifiers.get(4).getOffset(), 3);
        Assert.assertEquals(identifiers.get(4).getLastPosition(), 106);
        Assert.assertEquals(identifiers.get(4).getFirstPosition(), 104);
    }

    @Test
    public void testNamesNestedFunctions() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(Nested1.fun";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 2);
        Assert.assertEquals(identifiers.get(0).getName(), "Nested1");
        Assert.assertEquals(identifiers.get(0).getOffset(), 7);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 47);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 41);
        Assert.assertEquals(identifiers.get(1).getName(), "fun");
        Assert.assertEquals(identifiers.get(1).getOffset(), 3);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 51);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 49);
    }

    @Test
    public void testNamesNestedFunctions2() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(Nested1.function1('args').fun";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "Nested1");
        Assert.assertEquals(identifiers.get(0).getOffset(), 7);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 47);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 41);
        Assert.assertEquals(identifiers.get(1).getName(), "function1");
        Assert.assertEquals(identifiers.get(1).getOffset(), 17);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 65);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 49);
        Assert.assertEquals(identifiers.get(2).getName(), "fun");
        Assert.assertEquals(identifiers.get(2).getOffset(), 3);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 69);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 67);
    }

    @Test
    public void testNamesNestedFunctions3() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query('args', Nested2.function(1,2,3), Nested1.function1('args').fun";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "Nested1");
        Assert.assertEquals(identifiers.get(0).getOffset(), 7);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 80);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 74);
        Assert.assertEquals(identifiers.get(1).getName(), "function1");
        Assert.assertEquals(identifiers.get(1).getOffset(), 17);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 98);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 82);
        Assert.assertEquals(identifiers.get(2).getName(), "fun");
        Assert.assertEquals(identifiers.get(2).getOffset(), 3);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 102);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 100);
    }

    @Test
    public void testNamesNestedFunctionsCursorInBetween() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query('arg1', QueryBuilders.match, 'arg3')";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length() - 9);
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 2);
        Assert.assertEquals(identifiers.get(0).getName(), "QueryBuilders");
        Assert.assertEquals(identifiers.get(0).getOffset(), 13);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 61);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 49);
        Assert.assertEquals(identifiers.get(1).getName(), "match");
        Assert.assertEquals(identifiers.get(1).getOffset(), 5);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 67);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 63);
    }

    @Test
    public void testNamesNestedFunctionsCursorInBetween2() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().tes_whatever.query('arg1', QueryBuilders.match, 'arg3')";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, "SearchSourceBuilder.searchSource().tes".length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "SearchSourceBuilder");
        Assert.assertEquals(identifiers.get(0).getOffset(), 19);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 18);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "searchSource");
        Assert.assertEquals(identifiers.get(1).getOffset(), 14);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 33);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getName(), "tes");
        Assert.assertEquals(identifiers.get(2).getOffset(), 3);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 37);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 35);
    }

    @Test
    public void testNamesDoubleDot() throws Exception {
        String buffer = "name1.name2..name3";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "name1");
        Assert.assertEquals(identifiers.get(0).getOffset(), 5);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 4);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "name2");
        Assert.assertEquals(identifiers.get(1).getOffset(), 5);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 10);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 6);
        Assert.assertEquals(identifiers.get(2).getName(), "");
        Assert.assertEquals(identifiers.get(2).getOffset(), 0);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 11); //11 and not 12???
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 11); //11 and not 12???
        Assert.assertEquals(identifiers.get(3).getName(), "name3");
        Assert.assertEquals(identifiers.get(3).getOffset(), 5);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 17);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 13);
    }

    @Test
    public void testNamesWrongParentheses() throws Exception {
        String buffer = "name1.name2()).";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 1);
        Assert.assertEquals(identifiers.get(0).getName(), "");
        Assert.assertEquals(identifiers.get(0).getOffset(), 0);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 15);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 15);
    }

    @Test
    public void testNamesConstructor() throws Exception {
        String buffer = "x= new Name2().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "new");
        Assert.assertEquals(identifiers.get(0).getOffset(), 3);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 5);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 3);
        Assert.assertEquals(identifiers.get(1).getName(), "Name2");
        Assert.assertEquals(identifiers.get(1).getOffset(), 7);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 13);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 7);
        Assert.assertEquals(identifiers.get(2).getName(), "");
        Assert.assertEquals(identifiers.get(2).getOffset(), 0);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 15);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 15);
    }

    @Test
    public void testNamesConstructor2() throws Exception {
        String buffer = "x= new    Name2().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "new");
        Assert.assertEquals(identifiers.get(0).getOffset(), 6); //counts additional whitespaces but not the normal one
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 8);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 3);
        Assert.assertEquals(identifiers.get(1).getName(), "Name2");
        Assert.assertEquals(identifiers.get(1).getOffset(), 7);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 16);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 10);
        Assert.assertEquals(identifiers.get(2).getName(), "");
        Assert.assertEquals(identifiers.get(2).getOffset(), 0);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 18);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 18);
    }

    @Test
    public void testNamesConstructor3() throws Exception {
        String buffer = "x= new2    Name2().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);

        //TODO Requests(). returns static methods
        // this is not quite what I would want to see but for now it's ok
        Assert.assertEquals(identifiers.size(), 2);
        Assert.assertEquals(identifiers.get(0).getName(), "Name2");
        Assert.assertEquals(identifiers.get(0).getOffset(), 7);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 17);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 11);
        Assert.assertEquals(identifiers.get(1).getName(), "");
        Assert.assertEquals(identifiers.get(1).getOffset(), 0);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 19);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 19);
    }

    @Test
    public void testNamesConstructor4() throws Exception {
        String buffer = "x= n2w    Name2().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);

        //Again, this is not quite what I would want to see but for now it's ok
        Assert.assertEquals(identifiers.size(), 2);
        Assert.assertEquals(identifiers.get(0).getName(), "Name2");
        Assert.assertEquals(identifiers.get(0).getOffset(), 7);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 16);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 10);
        Assert.assertEquals(identifiers.get(1).getName(), "");
        Assert.assertEquals(identifiers.get(1).getOffset(), 0);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 18);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 18);
    }

    @Test
    public void testNamesConstructor5() throws Exception {
        String buffer = "new Name().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);

        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "new");
        Assert.assertEquals(identifiers.get(0).getOffset(), 3);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 2);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "Name");
        Assert.assertEquals(identifiers.get(1).getOffset(), 6);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 9);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 4);
        Assert.assertEquals(identifiers.get(2).getName(), "");
        Assert.assertEquals(identifiers.get(2).getOffset(), 0);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 11);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 11);
    }

    @Test
    public void testNamesConstructor6() throws Exception {
        String buffer = "Requests.indexRequest(new Name().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);

        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "new");
        Assert.assertEquals(identifiers.get(0).getOffset(), 3);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 24);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 22);
        Assert.assertEquals(identifiers.get(1).getName(), "Name");
        Assert.assertEquals(identifiers.get(1).getOffset(),6);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 31);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 26);
        Assert.assertEquals(identifiers.get(2).getName(), "");
        Assert.assertEquals(identifiers.get(2).getOffset(), 0);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 33);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 33);
    }

    @Test
    public void testNamesSquareBrackets() throws Exception {
        String buffer = "es.index['type-name'].";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "es");
        Assert.assertEquals(identifiers.get(0).getOffset(), 2);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 1);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "index");
        Assert.assertEquals(identifiers.get(1).getOffset(), 5);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 3);
        Assert.assertEquals(identifiers.get(2).getName(), "type-name");
        Assert.assertEquals(identifiers.get(2).getOffset(), 13);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 8);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getOffset(), 0);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 22);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 22);
    }

    @Test
    public void testNamesSquareBrackets2() throws Exception {
        String buffer = "es.index['type-name'].typ";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "es");
        Assert.assertEquals(identifiers.get(0).getOffset(), 2);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 1);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "index");
        Assert.assertEquals(identifiers.get(1).getOffset(), 5);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 3);
        Assert.assertEquals(identifiers.get(2).getName(), "type-name");
        Assert.assertEquals(identifiers.get(2).getOffset(), 13);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 8);
        Assert.assertEquals(identifiers.get(3).getName(), "typ");
        Assert.assertEquals(identifiers.get(3).getOffset(), 3);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 24);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 22);
    }

    @Test
    public void testNamesSquareBrackets3() throws Exception {
        String buffer = "es.index[\"type-name\"].";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "es");
        Assert.assertEquals(identifiers.get(0).getOffset(), 2);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 1);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "index");
        Assert.assertEquals(identifiers.get(1).getOffset(), 5);
        Assert.assertEquals(identifiers.get(1).getLastPosition(), 7);
        Assert.assertEquals(identifiers.get(1).getFirstPosition(), 3);
        Assert.assertEquals(identifiers.get(2).getName(), "type-name");
        Assert.assertEquals(identifiers.get(2).getOffset(), 13);
        Assert.assertEquals(identifiers.get(2).getLastPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getFirstPosition(), 8);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getOffset(), 0);
        Assert.assertEquals(identifiers.get(3).getLastPosition(), 22);
        Assert.assertEquals(identifiers.get(3).getFirstPosition(), 22);
    }

    @Test
    public void testNamesSquareBrackets4() throws Exception {
        //no auto-complete for non java identifier within square brackets for now
        //identifiers get cut when there is a non java identifier part
        String buffer = "es.index['type-na";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 1);
        Assert.assertEquals(identifiers.get(0).getName(), "na");
        Assert.assertEquals(identifiers.get(0).getOffset(), 2);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 16);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 15);
    }

    @Test
    public void testNamesSquareBracketsWrongInput() throws Exception {
        String buffer = "es.index[type-name'].typ";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 1);
        Assert.assertEquals(identifiers.get(0).getName(), "typ");
        Assert.assertEquals(identifiers.get(0).getOffset(), 3);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 23);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 21);
    }

    @Test
    public void testNamesSquareBracketsWrongInput2() throws Exception {
        String buffer = "es.index.type-name'].typ";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 1);
        Assert.assertEquals(identifiers.get(0).getName(), "typ");
        Assert.assertEquals(identifiers.get(0).getOffset(), 3);
        Assert.assertEquals(identifiers.get(0).getLastPosition(), 23);
        Assert.assertEquals(identifiers.get(0).getFirstPosition(), 21);
    }
}