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

import org.elasticsearch.client.Requests;
import org.elasticsearch.common.inject.Guice;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.shell.ShellModule;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoCustomWrapFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luca Cavanna
 */
public class JLineRhinoCompleterTest {

    JLineRhinoCompleter completer;

    @BeforeClass
    public void init() {
        Injector injector = Guice.createInjector(new ShellModule());
        Context context = Context.enter();
        context.setWrapFactory(new RhinoCustomWrapFactory());
        completer = injector.getInstance(JLineRhinoCompleter.class);
    }

    @AfterClass
    public void destroy() {
        Context.exit();
    }

    @Test
    public void testComplete_EmptyInput() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), completer.getScope().get().getAllIds().length);
    }

    @Test
    public void testCompleteNativeJavaClass_Name() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requ";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "Requests");
    }

    @Test
    public void testCompleteNativeJavaClass_WholeName() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "Requests");
    }

    @Test
    public void testComplete_Nothing() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests1";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
    }

    @Test
    public void testCompleteNativeJavaClass_AllMethods() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 35);
    }

    @Test
    public void testCompleteNativeJavaClass_FilteredMethods() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.index";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertEquals(candidates.get(0), "indexAliasesRequest(");
        Assert.assertEquals(candidates.get(1), "indexRequest(");
    }

    @Test
    public void testCompleteImportCommand() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "impo";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertEquals(candidates.get(0), "importClass(");
        Assert.assertEquals(candidates.get(1), "importPackage(");
    }

    @Test
    public void testCompleteNativeJavaClass_MethodNotFound() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.doesntExist().";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
    }

    @Test
    public void testCompleteNativeJavaClass_MethodNotFound2() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.doesntExist('abcd').";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
    }

    @Test
    public void testCompleteNativeJavaClass_MethodNotFound3() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.doesntExist().test";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
    }

    @Test
    public void testCompleteNativeJavaClass_MethodNotFound4() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.doesntExist('abcd').test";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
    }

    @Test
    public void testCompleteNativeJavaMethod_1MethodReflection() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest().";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 34);
    }

    @Test
    public void testCompleteNativeJavaMethod_1MethodReflectionWithArguments() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest(QueryBuilders.try(ddd)).";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 34);
    }

    @Test
    public void testCompleteNativeJavaMethod_1MethodReflection2() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest('index_name').ty";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "type(");
    }

    @Test
    public void testCompleteNativeJavaMethod_2MethodsReflection() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest('index_name').type('index_name').id";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "id(");
    }

    @Test
    public void testCompleteNativeJavaMethod_3MethodsReflection() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest('index_name').type(\"type_name\").id('id').so";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertEquals(candidates.get(0), "source(");
        Assert.assertEquals(candidates.get(1), "sourceAsMap(");
    }

    @Test
    public void testCompleteNativeJavaMethod_MethodNotFound() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest('index_name').notFound(\"type_name\").id('id').so";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
    }

    @Test
    public void testCompleteNativeJavaClassNotImported() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "java.util.Collections.empty";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 3);
        Assert.assertEquals(candidates.get(0), "emptyList(");
        Assert.assertEquals(candidates.get(1), "emptyMap(");
        Assert.assertEquals(candidates.get(2), "emptySet(");
    }

    @Test
    public void testCompleteNativeJavaClassNotImported2() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "java.util.Collections.emptyList(blablabla).add";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertEquals(candidates.get(0), "add(");
        Assert.assertEquals(candidates.get(1), "addAll(");
    }

    @Test
    public void testCompleteNativeJavaClassNotImported3() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "java.util.Collections.emptyList(blablabla).get(0).";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 3);
        Assert.assertEquals(candidates.get(0), "equals(");
        Assert.assertEquals(candidates.get(1), "getClass(");
        Assert.assertEquals(candidates.get(2), "toString(");
    }

    @Test
    public void testCompleteNativeJavaClassNotImported_Void() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "java.util.Collections.sort(sdfsdf).";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
    }

    @Test
    public void testCompleteNativeJavaClass_MultipleReturnTypes() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest().index().";
        completer.complete(input, input.length(), candidates);
        //merge between String return type and ShardReplicationOperationRequest
        Assert.assertEquals(candidates.size(), 56);
        Assert.assertTrue(candidates.contains("substring("));
        Assert.assertTrue(candidates.contains("replicationType("));
    }

    @Test
    public void testCompleteNativeJavaObject() {
        completer.getScope().registerJavaObject("ir", Context.javaToJS(Requests.indexRequest("index_name"), completer.getScope().get()));
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "ir.ty";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "type(");

        candidates.clear();
        input = "ir.type('type_name').id";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "id(");

        candidates.clear();
        input = "ir.type('type_name').id(\"id\").so";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertEquals(candidates.get(0), "source(");
        Assert.assertEquals(candidates.get(1), "sourceAsMap(");
    }

    @Test
    public void testCompletePackages() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "ja";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertEquals(candidates.get(0), "java");
        Assert.assertEquals(candidates.get(1), "javax");

        candidates.clear();
        input = "java.";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 7);
        Assert.assertEquals(candidates.get(0), "applet");
        Assert.assertEquals(candidates.get(1), "io");
        Assert.assertEquals(candidates.get(2), "lang");
        Assert.assertEquals(candidates.get(3), "math");
        Assert.assertEquals(candidates.get(4), "net");
        Assert.assertEquals(candidates.get(5), "text");
        Assert.assertEquals(candidates.get(6), "util");

        /*
        TODO Would be nice to give back the classes that belong to that package instead of only the packages (e.g. zip)
        candidates.clear();
        input = "java.util.";
        completer.complete(input, input.length(), candidates);
        */
    }

    @Test
    public void testCompleteConstructor_StartBuffer() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        //String input = "Requests.indexRequest().index(new AliasAction().";
        String input = "new AliasAction().";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 15);
        //contains both static and instance methods
        Assert.assertTrue(candidates.contains("filter("));
        Assert.assertTrue(candidates.contains("routing("));
        Assert.assertTrue(candidates.contains("newAddAliasAction("));
    }

    @Test
    public void testCompleteConstructor_StartBufferWithLastPart() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        //String input = "Requests.indexRequest().index(new AliasAction().";
        String input = "new AliasAction().fil";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "filter(");
    }

    @Test
    public void testCompleteConstructor_StartBufferMultipleMethods() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        //String input = "Requests.indexRequest().index(new AliasAction().";
        String input = "new AliasAction().filter('args').rou";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "routing(");
    }

    @Test
    public void testCompleteConstructor_StartBufferQualifiedPackage() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "new java.util.Date().";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 27);
        Assert.assertTrue(candidates.contains("after("));
        Assert.assertTrue(candidates.contains("before("));
        Assert.assertTrue(candidates.contains("getDay("));
    }

    @Test
    public void testCompleteConstructor_StartBufferQualifiedPackageWithLastPart() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "new java.util.Date().af";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "after(");
    }

    @Test
    public void testCompleteConstructor_StartBufferQualifiedPackageMultipleMethods() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "new org.elasticsearch.action.index.IndexRequest().id('blablabla').ty";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "type(");
    }

    @Test
    public void testCompleteConstructor_FunctionArg() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        //String input = "Requests.indexRequest().index(new AliasAction().";
        String input = "function(new AliasAction().";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 15);
        //contains both static and instance methods
        Assert.assertTrue(candidates.contains("filter("));
        Assert.assertTrue(candidates.contains("routing("));
        Assert.assertTrue(candidates.contains("newAddAliasAction("));
    }

    @Test
    public void testCompleteConstructor_FunctionArgWithLastPart() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        //String input = "Requests.indexRequest().index(new AliasAction().";
        String input = "function(new AliasAction().fil";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "filter(");
    }

    @Test
    public void testCompleteConstructor_FunctionArgMultipleMethods() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        //String input = "Requests.indexRequest().index(new AliasAction().";
        String input = "function(new AliasAction().filter('args').rou";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "routing(");
    }

    @Test
    public void testCompleteConstructor_FunctionArgQualifiedPackageWithLastPart() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "function(new java.util.Date().af";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "after(");
    }

    @Test
    public void testCompleteConstructor_FunctionArgQualifiedPackageMultipleMethods() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "function(new org.elasticsearch.action.index.IndexRequest().id('blablabla').ty";
        completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "type(");
    }


    //TODO non java identifier example  e.g. es.index1['type-name']

    //TODO cursor in the middle

    //TODO array[0]

    //TODO new java.util.Date().
}
