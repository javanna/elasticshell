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
        Assert.assertEquals(identifiers.get(0).getLength(), 0);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 0);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
    }

    @Test
    public void testNameLastPartEmpty() throws Exception {
        String buffer = "Requests.";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 2);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getLength(), 8);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "");
        Assert.assertEquals(identifiers.get(1).getLength(), 0);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 9);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 9);
    }

    @Test
    public void testNameLastPartNotEmpty() throws Exception {
        String buffer = "Requests.ind";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 2);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getLength(), 8);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "ind");
        Assert.assertEquals(identifiers.get(1).getLength(), 3);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 11);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 9);
    }
    
    @Test
    public void testNamesLastPartEmpty() throws Exception {
        String buffer = "es.index.type.";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "es");
        Assert.assertEquals(identifiers.get(0).getLength(), 2);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 1);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "index");
        Assert.assertEquals(identifiers.get(1).getLength(), 5);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 3);
        Assert.assertEquals(identifiers.get(2).getName(), "type");
        Assert.assertEquals(identifiers.get(2).getLength(), 4);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 12);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 9);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getLength(), 0);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 14);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 14);
    }

    @Test
    public void testNamesLastPartNotEmpty() throws Exception {
        String buffer = "es.index.type.ind";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "es");
        Assert.assertEquals(identifiers.get(0).getLength(), 2);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 1);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "index");
        Assert.assertEquals(identifiers.get(1).getLength(), 5);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 3);
        Assert.assertEquals(identifiers.get(2).getName(), "type");
        Assert.assertEquals(identifiers.get(2).getLength(), 4);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 12);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 9);
        Assert.assertEquals(identifiers.get(3).getName(), "ind");
        Assert.assertEquals(identifiers.get(3).getLength(), 3);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 16);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 14);
    }

    @Test
    public void testNamesFunctionNoArgsLastPartEmpty() throws Exception {
        String buffer = "Requests.indexRequest().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getLength(), 8);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getLength(), 14);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 22);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "");
        Assert.assertEquals(identifiers.get(2).getLength(), 0);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 24);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 24);
    }

    @Test
    public void testNamesFunctionNoArgsLastPartNotEmpty() throws Exception {
        String buffer = "Requests.indexRequest().ind";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getLength(), 8);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getLength(), 14);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 22);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "ind");
        Assert.assertEquals(identifiers.get(2).getLength(), 3);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 26);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 24);
    }

    @Test
    public void testNamesFunctionsNoArgsLastPartEmpty() throws Exception {
        String buffer = "Requests.indexRequest().index().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getLength(), 8);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getLength(), 14);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 22);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "index");
        Assert.assertEquals(identifiers.get(2).getLength(), 7);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 30);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 24);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getLength(), 0);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 32);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 32);
    }

    @Test
    public void testNamesFunctionsNoArgsLastPartNotEmpty() throws Exception {
        String buffer = "Requests.indexRequest().index().ind";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getLength(), 8);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getLength(), 14);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 22);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "index");
        Assert.assertEquals(identifiers.get(2).getLength(), 7);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 30);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 24);
        Assert.assertEquals(identifiers.get(3).getName(), "ind");
        Assert.assertEquals(identifiers.get(3).getLength(), 3);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 34);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 32);
    }

    @Test
    public void testNamesFunctionsArgsLastPartEmpty() throws Exception {
        String buffer = "Requests.indexRequest().index('arg2','arg2').";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getLength(), 8);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getLength(), 14);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 22);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "index");
        Assert.assertEquals(identifiers.get(2).getLength(), 20);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 43);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 24);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getLength(), 0);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 45);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 45);
    }

    @Test
    public void testNamesFunctionsArgsLastPartEmpty2() throws Exception {
        String buffer = "Requests.indexRequest('arg1', 123).index('arg2','arg2').";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getLength(), 8);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getLength(), 25);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 33);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "index");
        Assert.assertEquals(identifiers.get(2).getLength(), 20);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 54);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 35);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getLength(), 0);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 56);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 56);
    }

    @Test
    public void testNamesFunctionsArgsLastPartNotEmpty() throws Exception {
        String buffer = "Requests.indexRequest('arg1', 123456).index('index_name').ind";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "Requests");
        Assert.assertEquals(identifiers.get(0).getLength(), 8);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "indexRequest");
        Assert.assertEquals(identifiers.get(1).getLength(), 28);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 36);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 9);
        Assert.assertEquals(identifiers.get(2).getName(), "index");
        Assert.assertEquals(identifiers.get(2).getLength(), 19);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 56);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 38);
        Assert.assertEquals(identifiers.get(3).getName(), "ind");
        Assert.assertEquals(identifiers.get(3).getLength(), 3);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 60);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 58);
    }

    @Test
    public void testNamesNestedFunctionsLastPartEmpty() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(QueryBuilders.matchAllQuery()).";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "SearchSourceBuilder");
        Assert.assertEquals(identifiers.get(0).getLength(), 19);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 18);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "searchSource");
        Assert.assertEquals(identifiers.get(1).getLength(), 14);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 33);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getName(), "query");
        Assert.assertEquals(identifiers.get(2).getLength(), 36);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 70);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 35);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getLength(), 0);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 72);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 72);
    }

    @Test
    public void testNamesNestedFunctionsLastPartNotEmpty() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(QueryBuilders.matchAllQuery()).facets().sor";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 5);
        Assert.assertEquals(identifiers.get(0).getName(), "SearchSourceBuilder");
        Assert.assertEquals(identifiers.get(0).getLength(), 19);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 18);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "searchSource");
        Assert.assertEquals(identifiers.get(1).getLength(), 14);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 33);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getName(), "query");
        Assert.assertEquals(identifiers.get(2).getLength(), 36);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 70);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 35);
        Assert.assertEquals(identifiers.get(3).getName(), "facets");
        Assert.assertEquals(identifiers.get(3).getLength(), 8);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 79);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 72);
        Assert.assertEquals(identifiers.get(4).getName(), "sor");
        Assert.assertEquals(identifiers.get(4).getLength(), 3);
        Assert.assertEquals(identifiers.get(4).getEndPosition(), 83);
        Assert.assertEquals(identifiers.get(4).getStartPosition(), 81);
    }

    @Test
    public void testNamesNestedFunctionsLastPartNotEmpty2() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(Nested1.function1('arg1', Nested2.function2('test'))).facets().sor";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 5);
        Assert.assertEquals(identifiers.get(0).getName(), "SearchSourceBuilder");
        Assert.assertEquals(identifiers.get(0).getLength(), 19);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 18);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "searchSource");
        Assert.assertEquals(identifiers.get(1).getLength(), 14);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 33);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getName(), "query");
        Assert.assertEquals(identifiers.get(2).getLength(), 59);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 93);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 35);
        Assert.assertEquals(identifiers.get(3).getName(), "facets");
        Assert.assertEquals(identifiers.get(3).getLength(), 8);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 102);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 95);
        Assert.assertEquals(identifiers.get(4).getName(), "sor");
        Assert.assertEquals(identifiers.get(4).getLength(), 3);
        Assert.assertEquals(identifiers.get(4).getEndPosition(), 106);
        Assert.assertEquals(identifiers.get(4).getStartPosition(), 104);
    }

    @Test
    public void testNamesNestedFunctions() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(Nested1.fun";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 2);
        Assert.assertEquals(identifiers.get(0).getName(), "Nested1");
        Assert.assertEquals(identifiers.get(0).getLength(), 7);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 47);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 41);
        Assert.assertEquals(identifiers.get(1).getName(), "fun");
        Assert.assertEquals(identifiers.get(1).getLength(), 3);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 51);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 49);
    }

    @Test
    public void testNamesNestedFunctions2() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query(Nested1.function1('args').fun";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "Nested1");
        Assert.assertEquals(identifiers.get(0).getLength(), 7);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 47);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 41);
        Assert.assertEquals(identifiers.get(1).getName(), "function1");
        Assert.assertEquals(identifiers.get(1).getLength(), 17);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 65);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 49);
        Assert.assertEquals(identifiers.get(2).getName(), "fun");
        Assert.assertEquals(identifiers.get(2).getLength(), 3);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 69);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 67);
    }

    @Test
    public void testNamesNestedFunctions3() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query('args', Nested2.function(1,2,3), Nested1.function1('args').fun";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "Nested1");
        Assert.assertEquals(identifiers.get(0).getLength(), 7);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 80);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 74);
        Assert.assertEquals(identifiers.get(1).getName(), "function1");
        Assert.assertEquals(identifiers.get(1).getLength(), 17);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 98);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 82);
        Assert.assertEquals(identifiers.get(2).getName(), "fun");
        Assert.assertEquals(identifiers.get(2).getLength(), 3);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 102);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 100);
    }

    @Test
    public void testNamesNestedFunctionsCursorInBetween() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().query('arg1', QueryBuilders.match, 'arg3')";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length() - 9);
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 2);
        Assert.assertEquals(identifiers.get(0).getName(), "QueryBuilders");
        Assert.assertEquals(identifiers.get(0).getLength(), 13);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 61);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 49);
        Assert.assertEquals(identifiers.get(1).getName(), "match");
        Assert.assertEquals(identifiers.get(1).getLength(), 5);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 67);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 63);
    }

    @Test
    public void testNamesNestedFunctionsCursorInBetween2() throws Exception {
        String buffer = "SearchSourceBuilder.searchSource().tes_whatever.query('arg1', QueryBuilders.match, 'arg3')";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, "SearchSourceBuilder.searchSource().tes".length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "SearchSourceBuilder");
        Assert.assertEquals(identifiers.get(0).getLength(), 19);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 18);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "searchSource");
        Assert.assertEquals(identifiers.get(1).getLength(), 14);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 33);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getName(), "tes");
        Assert.assertEquals(identifiers.get(2).getLength(), 3);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 37);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 35);
    }

    @Test
    public void testNamesDoubleDot() throws Exception {
        String buffer = "name1.name2..name3";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "name1");
        Assert.assertEquals(identifiers.get(0).getLength(), 5);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 4);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "name2");
        Assert.assertEquals(identifiers.get(1).getLength(), 5);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 10);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 6);
        Assert.assertEquals(identifiers.get(2).getName(), "");
        Assert.assertEquals(identifiers.get(2).getLength(), 0);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 11); //11 and not 12???
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 11); //11 and not 12???
        Assert.assertEquals(identifiers.get(3).getName(), "name3");
        Assert.assertEquals(identifiers.get(3).getLength(), 5);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 17);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 13);
    }

    @Test
    public void testNamesWrongParentheses() throws Exception {
        String buffer = "name1.name2()).";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 1);
        Assert.assertEquals(identifiers.get(0).getName(), "");
        Assert.assertEquals(identifiers.get(0).getLength(), 0);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 15);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 15);
    }

    @Test
    public void testNamesConstructor() throws Exception {
        String buffer = "x= new Name2().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "new");
        Assert.assertEquals(identifiers.get(0).getLength(), 3);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 5);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 3);
        Assert.assertEquals(identifiers.get(1).getName(), "Name2");
        Assert.assertEquals(identifiers.get(1).getLength(), 7);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 13);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 7);
        Assert.assertEquals(identifiers.get(2).getName(), "");
        Assert.assertEquals(identifiers.get(2).getLength(), 0);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 15);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 15);
    }

    @Test
    public void testNamesConstructor2() throws Exception {
        String buffer = "x= new    Name2().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "new");
        Assert.assertEquals(identifiers.get(0).getLength(), 6); //counts additional whitespaces but not the normal one
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 8);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 3);
        Assert.assertEquals(identifiers.get(1).getName(), "Name2");
        Assert.assertEquals(identifiers.get(1).getLength(), 7);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 16);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 10);
        Assert.assertEquals(identifiers.get(2).getName(), "");
        Assert.assertEquals(identifiers.get(2).getLength(), 0);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 18);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 18);
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
        Assert.assertEquals(identifiers.get(0).getLength(), 7);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 17);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 11);
        Assert.assertEquals(identifiers.get(1).getName(), "");
        Assert.assertEquals(identifiers.get(1).getLength(), 0);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 19);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 19);
    }

    @Test
    public void testNamesConstructor4() throws Exception {
        String buffer = "x= n2w    Name2().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);

        //Again, this is not quite what I would want to see but for now it's ok
        Assert.assertEquals(identifiers.size(), 2);
        Assert.assertEquals(identifiers.get(0).getName(), "Name2");
        Assert.assertEquals(identifiers.get(0).getLength(), 7);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 16);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 10);
        Assert.assertEquals(identifiers.get(1).getName(), "");
        Assert.assertEquals(identifiers.get(1).getLength(), 0);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 18);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 18);
    }

    @Test
    public void testNamesConstructor5() throws Exception {
        String buffer = "new Name().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);

        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "new");
        Assert.assertEquals(identifiers.get(0).getLength(), 3);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 2);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "Name");
        Assert.assertEquals(identifiers.get(1).getLength(), 6);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 9);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 4);
        Assert.assertEquals(identifiers.get(2).getName(), "");
        Assert.assertEquals(identifiers.get(2).getLength(), 0);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 11);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 11);
    }

    @Test
    public void testNamesConstructor6() throws Exception {
        String buffer = "Requests.indexRequest(new Name().";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);

        Assert.assertEquals(identifiers.size(), 3);
        Assert.assertEquals(identifiers.get(0).getName(), "new");
        Assert.assertEquals(identifiers.get(0).getLength(), 3);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 24);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 22);
        Assert.assertEquals(identifiers.get(1).getName(), "Name");
        Assert.assertEquals(identifiers.get(1).getLength(),6);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 31);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 26);
        Assert.assertEquals(identifiers.get(2).getName(), "");
        Assert.assertEquals(identifiers.get(2).getLength(), 0);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 33);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 33);
    }

    @Test
    public void testNamesSquareBrackets() throws Exception {
        String buffer = "es.index['type-name'].";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "es");
        Assert.assertEquals(identifiers.get(0).getLength(), 2);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 1);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "index");
        Assert.assertEquals(identifiers.get(1).getLength(), 5);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 3);
        Assert.assertEquals(identifiers.get(2).getName(), "type-name");
        Assert.assertEquals(identifiers.get(2).getLength(), 13);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 8);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getLength(), 0);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 22);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 22);
    }

    @Test
    public void testNamesSquareBrackets2() throws Exception {
        String buffer = "es.index['type-name'].typ";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "es");
        Assert.assertEquals(identifiers.get(0).getLength(), 2);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 1);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "index");
        Assert.assertEquals(identifiers.get(1).getLength(), 5);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 3);
        Assert.assertEquals(identifiers.get(2).getName(), "type-name");
        Assert.assertEquals(identifiers.get(2).getLength(), 13);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 8);
        Assert.assertEquals(identifiers.get(3).getName(), "typ");
        Assert.assertEquals(identifiers.get(3).getLength(), 3);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 24);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 22);
    }

    @Test
    public void testNamesSquareBrackets3() throws Exception {
        String buffer = "es.index[\"type-name\"].";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 4);
        Assert.assertEquals(identifiers.get(0).getName(), "es");
        Assert.assertEquals(identifiers.get(0).getLength(), 2);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 1);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 0);
        Assert.assertEquals(identifiers.get(1).getName(), "index");
        Assert.assertEquals(identifiers.get(1).getLength(), 5);
        Assert.assertEquals(identifiers.get(1).getEndPosition(), 7);
        Assert.assertEquals(identifiers.get(1).getStartPosition(), 3);
        Assert.assertEquals(identifiers.get(2).getName(), "type-name");
        Assert.assertEquals(identifiers.get(2).getLength(), 13);
        Assert.assertEquals(identifiers.get(2).getEndPosition(), 20);
        Assert.assertEquals(identifiers.get(2).getStartPosition(), 8);
        Assert.assertEquals(identifiers.get(3).getName(), "");
        Assert.assertEquals(identifiers.get(3).getLength(), 0);
        Assert.assertEquals(identifiers.get(3).getEndPosition(), 22);
        Assert.assertEquals(identifiers.get(3).getStartPosition(), 22);
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
        Assert.assertEquals(identifiers.get(0).getLength(), 2);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 16);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 15);
    }

    @Test
    public void testNamesSquareBracketsWrongInput() throws Exception {
        String buffer = "es.index[type-name'].typ";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 1);
        Assert.assertEquals(identifiers.get(0).getName(), "typ");
        Assert.assertEquals(identifiers.get(0).getLength(), 3);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 23);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 21);
    }

    @Test
    public void testNamesSquareBracketsWrongInput2() throws Exception {
        String buffer = "es.index.type-name'].typ";
        List<Identifier> identifiers = identifiersExtractor.tokenize(buffer, buffer.length());
        Assert.assertNotNull(identifiers);
        Assert.assertEquals(identifiers.size(), 1);
        Assert.assertEquals(identifiers.get(0).getName(), "typ");
        Assert.assertEquals(identifiers.get(0).getLength(), 3);
        Assert.assertEquals(identifiers.get(0).getEndPosition(), 23);
        Assert.assertEquals(identifiers.get(0).getStartPosition(), 21);
    }
}