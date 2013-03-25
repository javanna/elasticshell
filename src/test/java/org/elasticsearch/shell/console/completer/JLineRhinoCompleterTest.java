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

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.inject.Guice;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.shell.JLineModule;
import org.elasticsearch.shell.RhinoShellModule;
import org.elasticsearch.shell.ShellModule;
import org.mozilla.javascript.*;
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
        Injector injector = Guice.createInjector(new ShellModule(), new JLineModule(), new RhinoShellModule());
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
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), completer.getScope().get().getAllIds().length);
        Assert.assertEquals(output, 0);
    }

    @Test
    public void testCompleteNativeJavaClass_Name() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requ";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "Requests");
        Assert.assertEquals(output, 0);
    }

    @Test
    public void testCompleteNativeJavaClass_WholeName() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "Requests");
        Assert.assertEquals(output, 0);
    }

    @Test
    public void testComplete_Nothing() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests1";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
        Assert.assertEquals(output, 0);
    }

    @Test
    public void testCompleteNativeJavaClass_AllMethods() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 36);
        Assert.assertEquals(output, 9);
    }

    @Test
    public void testCompleteNativeJavaClass_FilteredMethods() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.index";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertTrue(candidates.contains("indexAliasesRequest()"));
        Assert.assertTrue(candidates.contains("indexRequest()"));
        Assert.assertEquals(output, 9);
    }

    @Test
    public void testCompleteImportCommand() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "impo";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertTrue(candidates.contains("importClass()"));
        Assert.assertTrue(candidates.contains("importPackage()"));
        Assert.assertEquals(output, 0);
    }

    @Test
    public void testCompleteNativeJavaClass_MethodNotFound() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.doesntExist().";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
        Assert.assertEquals(output, 23);
    }

    @Test
    public void testCompleteNativeJavaClass_MethodNotFound2() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.doesntExist('abcd').";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
        Assert.assertEquals(output, 29);
    }

    @Test
    public void testCompleteNativeJavaClass_MethodNotFound3() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.doesntExist().test";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
        Assert.assertEquals(output, 23);
    }

    @Test
    public void testCompleteNativeJavaClass_MethodNotFound4() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.doesntExist('abcd').test";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
        Assert.assertEquals(output, 29);
    }

    @Test
    public void testCompleteNativeJavaMethod_1MethodReflection() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest().";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 34);
        Assert.assertEquals(output, 24);
    }

    @Test
    public void testCompleteNativeJavaMethod_1MethodReflectionWithArguments() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest(QueryBuilders.try(ddd)).";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 34);
        Assert.assertEquals(output, 46);
    }

    @Test
    public void testCompleteNativeJavaMethod_1MethodReflection2() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest('index_name').ty";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "type()");
        Assert.assertEquals(output, 36);
    }

    @Test
    public void testCompleteNativeJavaMethod_2MethodsReflection() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest('index_name').type('index_name').id";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "id()");
        Assert.assertEquals(output, 55);
    }

    @Test
    public void testCompleteNativeJavaMethod_3MethodsReflection() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest('index_name').type(\"type_name\").id('id').so";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertTrue(candidates.contains("source()"));
        Assert.assertTrue(candidates.contains("sourceAsMap()"));
        Assert.assertEquals(output, 63);
    }

    @Test
    public void testCompleteNativeJavaMethod_MethodNotFound() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest('index_name').notFound(\"type_name\").id('id').so";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
        Assert.assertEquals(output, 67);
    }

    @Test
    public void testCompleteNativeJavaClassNotImported() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "java.util.Collections.so";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertTrue(candidates.contains("sort()"));
        Assert.assertEquals(output, 22);
    }

    @Test
    public void testCompleteNativeJavaClassNotImported2() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "java.util.Collections.emptyList(blablabla).add";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertTrue(candidates.contains("add()"));
        Assert.assertTrue(candidates.contains("addAll()"));
        Assert.assertEquals(output, 43);
    }

    @Test
    public void testCompleteNativeJavaClassNotImported3() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "java.util.Collections.emptyList(blablabla).get(0).";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 3);
        Assert.assertTrue(candidates.contains("equals()"));
        Assert.assertTrue(candidates.contains("getClass()"));
        Assert.assertTrue(candidates.contains("toString()"));
        Assert.assertEquals(output, 50);
    }

    @Test
    public void testCompleteNativeJavaClassNotImported_Void() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "java.util.Collections.sort(sdfsdf).";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 0);
        Assert.assertEquals(output, 35);
    }

    @Test
    public void testCompleteNativeJavaClass_MultipleReturnTypes() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "Requests.indexRequest().index().";
        int output = completer.complete(input, input.length(), candidates);
        //merge between String return type and ShardReplicationOperationRequest
        Assert.assertEquals(candidates.size(), 56);
        Assert.assertTrue(candidates.contains("substring()"));
        Assert.assertTrue(candidates.contains("replicationType()"));
        Assert.assertEquals(output, 32);
    }

    @Test
    public void testCompleteNativeJavaObject() {
        completer.getScope().registerJavaObject("ir", Context.javaToJS(Requests.indexRequest("index_name"), completer.getScope().get()));
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "ir.ty";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "type()");
        Assert.assertEquals(output, 3);

        candidates.clear();
        input = "ir.type('type_name').id";
        output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "id()");
        Assert.assertEquals(output, 21);

        candidates.clear();
        input = "ir.type('type_name').id(\"id\").so";
        output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertTrue(candidates.contains("source()"));
        Assert.assertTrue(candidates.contains("sourceAsMap()"));
        Assert.assertEquals(output, 30);
    }

    @Test
    public void testCompleteNestedNativeJavaObject() {
        NativeJavaObject nativeJavaObject = new RhinoCustomNativeJavaObject(completer.getScope().get(), Requests.indexRequest("index_name"), IndexRequest.class);
        nativeJavaObject.setPrototype(Context.getCurrentContext().newObject(completer.getScope().get()));

        NativeJavaObject nestedDativeJavaObject = new RhinoCustomNativeJavaObject(completer.getScope().get(), Requests.indexRequest("index_name"), IndexRequest.class);
        nestedDativeJavaObject.setPrototype(Context.getCurrentContext().newObject(completer.getScope().get()));

        ScriptableObject.putProperty(nativeJavaObject, "typeNested", nestedDativeJavaObject);
        completer.getScope().registerJavaObject("ir", nativeJavaObject);

        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "ir.ty";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertTrue(candidates.contains("type()"));
        Assert.assertTrue(candidates.contains("typeNested"));
        Assert.assertEquals(output, 3);

        candidates.clear();
        input = "ir.type('type_name').id";
        output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "id()");
        Assert.assertEquals(output, 21);

        candidates.clear();
        input = "ir.type('type_name').id(\"id\").so";
        output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertTrue(candidates.contains("source()"));
        Assert.assertTrue(candidates.contains("sourceAsMap()"));
        Assert.assertEquals(output, 30);

        candidates.clear();
        input = "ir.typeNested.ty";
        output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "type()");
        Assert.assertEquals(output, 14);

        candidates.clear();
        input = "ir.typeNested.type('type_name').id";
        output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "id()");
        Assert.assertEquals(output, 32);

        candidates.clear();
        input = "ir.typeNested.type('type_name').id(\"id\").so";
        output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertTrue(candidates.contains("source()"));
        Assert.assertTrue(candidates.contains("sourceAsMap()"));
        Assert.assertEquals(output, 41);
    }

    @Test
    public void testCompleteNestedNativeJavaObjectNonJavaIdentifier() {
        NativeJavaObject nativeJavaObject = new RhinoCustomNativeJavaObject(completer.getScope().get(), Requests.indexRequest("index_name"), IndexRequest.class);
        nativeJavaObject.setPrototype(Context.getCurrentContext().newObject(completer.getScope().get()));

        NativeJavaObject nestedDativeJavaObject = new RhinoCustomNativeJavaObject(completer.getScope().get(), Requests.indexRequest("index_name"), IndexRequest.class);
        nestedDativeJavaObject.setPrototype(Context.getCurrentContext().newObject(completer.getScope().get()));

        ScriptableObject.putProperty(nativeJavaObject, "type-name", nestedDativeJavaObject);
        completer.getScope().registerJavaObject("ir", nativeJavaObject);

        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "ir.ty";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertTrue(candidates.contains("type()"));
        Assert.assertTrue(candidates.contains("type-name"));
        Assert.assertEquals(output, 3);

        candidates.clear();
        input = "ir.type('type_name').id";
        output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "id()");
        Assert.assertEquals(output, 21);

        candidates.clear();
        input = "ir.type('type_name').id(\"id\").so";
        output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertTrue(candidates.contains("source()"));
        Assert.assertTrue(candidates.contains("sourceAsMap()"));
        Assert.assertEquals(output, 30);

        candidates.clear();
        input = "ir['type-name'].ty";
        output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "type()");
        Assert.assertEquals(output, 16);

        candidates.clear();
        input = "ir['type-name'].type('type_name').id(\"id\").so";
        output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertTrue(candidates.contains("source()"));
        Assert.assertTrue(candidates.contains("sourceAsMap()"));
        Assert.assertEquals(output, 43);
    }

    @Test
    public void testCompletePackages() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "ja";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 2);
        Assert.assertEquals(candidates.get(0), "java");
        Assert.assertEquals(candidates.get(1), "javax");
        Assert.assertEquals(output, 0);

        candidates.clear();
        input = "java.";
        output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 7);
        Assert.assertTrue(candidates.contains("applet"));
        Assert.assertTrue(candidates.contains("lang"));
        Assert.assertTrue(candidates.contains("math"));
        Assert.assertTrue(candidates.contains("io"));
        Assert.assertTrue(candidates.contains("net"));
        Assert.assertTrue(candidates.contains("text"));
        Assert.assertTrue(candidates.contains("util"));
        Assert.assertEquals(output, 5);

        //TODO Would be nice to give back the classes that belong to that package instead of only the packages (e.g. zip)
    }

    @Test
    public void testCompleteConstructor_StartBuffer() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "new AliasAction().";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 15);
        //contains both static and instance methods
        Assert.assertTrue(candidates.contains("filter()"));
        Assert.assertTrue(candidates.contains("routing()"));
        Assert.assertTrue(candidates.contains("newAddAliasAction()"));
        Assert.assertEquals(output, 18);
    }

    @Test
    public void testCompleteConstructor_StartBufferWithLastPart() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "new AliasAction().fil";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "filter()");
        Assert.assertEquals(output, 18);
    }

    @Test
    public void testCompleteConstructor_StartBufferMultipleMethods() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "new AliasAction().filter('args').rou";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "routing()");
        Assert.assertEquals(output, 33);
    }

    @Test
    public void testCompleteConstructor_StartBufferQualifiedPackage() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "new java.util.Date().";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 27);
        Assert.assertTrue(candidates.contains("after()"));
        Assert.assertTrue(candidates.contains("before()"));
        Assert.assertTrue(candidates.contains("getDay()"));
        Assert.assertEquals(output, 21);
    }

    @Test
    public void testCompleteConstructor_StartBufferQualifiedPackageWithLastPart() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "new java.util.Date().af";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "after()");
        Assert.assertEquals(output, 21);
    }

    @Test
    public void testCompleteConstructor_StartBufferQualifiedPackageMultipleMethods() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "new org.elasticsearch.action.index.IndexRequest().id('blablabla').ty";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "type()");
        Assert.assertEquals(output, 66);
    }

    @Test
    public void testCompleteConstructor_FunctionArg() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "function(new AliasAction().";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 15);
        //contains both static and instance methods
        Assert.assertTrue(candidates.contains("filter()"));
        Assert.assertTrue(candidates.contains("routing()"));
        Assert.assertTrue(candidates.contains("newAddAliasAction()"));
        Assert.assertEquals(output, 27);
    }

    @Test
    public void testCompleteConstructor_FunctionArgWithLastPart() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "function(new AliasAction().fil";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "filter()");
        Assert.assertEquals(output, 27);
    }

    @Test
    public void testCompleteConstructor_FunctionArgMultipleMethods() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "function(new AliasAction().filter('args').rou";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "routing()");
        Assert.assertEquals(output, 42);
    }

    @Test
    public void testCompleteConstructor_FunctionArgQualifiedPackageWithLastPart() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "function(new java.util.Date().af";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "after()");
        Assert.assertEquals(output, 30);
    }

    @Test
    public void testCompleteConstructor_FunctionArgQualifiedPackageMultipleMethods() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "function(new org.elasticsearch.action.index.IndexRequest().id('blablabla').ty";
        int output = completer.complete(input, input.length(), candidates);
        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0), "type()");
        Assert.assertEquals(output, 75);
    }

    @Test
    public void test() {
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        String input = "FilterBuilders.queryFilter(QueryBuilders.)";
        String inputLength = "FilterBuilders.queryFilter(QueryBuilders.";
        int output = completer.complete(input, inputLength.length(), candidates);
        Assert.assertEquals(candidates.size(), 49);
        Assert.assertTrue(candidates.contains("matchAllQuery()"));
        Assert.assertTrue(candidates.contains("termQuery()"));
        Assert.assertEquals(output, inputLength.length());
    }
}
