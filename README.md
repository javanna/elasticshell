elasticshell - a shell for elasticsearch [![Build Status](https://buildhive.cloudbees.com/job/javanna/job/elasticshell/badge/icon)](https://buildhive.cloudbees.com/job/javanna/job/elasticshell/) [![Build Status](https://travis-ci.org/javanna/elasticshell.png)](https://travis-ci.org/javanna/elasticshell) [![Build Status](https://drone.io/github.com/javanna/elasticshell/status.png)](https://drone.io/github.com/javanna/elasticshell/latest)
==============================

The elasticshell is a javascript shell written in Java.
It allows to interact with a running [elasticsearch](http://www.elasticsearch.org) cluster using the [Java API](http://www.elasticsearch.org/guide/reference/java-api/).

Getting Started
==============================

Versions
------------------------------

The elasticshell version is tightly coupled with the elasticsearch version since it uses its Java API to connect to it.
There currently are two active development branches: one for 0.19.x and one for 0.20.x.

<table>
	<thead>
		<tr>
			<td>elasticshell</td>
			<td>elasticsearch</td>
		</tr>
	</thead>
	<tbody>
		<tr>
            <td><a href="http://bit.ly/13593TO">0.20.5-BETA</a></td>
            <td>0.20.x</td>
        </tr>
		<tr>
            <td><a href="http://bit.ly/13f6BJZ">0.19.12-BETA</a></td>
        	<td>0.19.x</td>
        </tr>
	</tbody>
</table>

Installation
------------------------------

* [Download](http://bit.ly/13593TO) and unzip the elasticshell distribution
* Run `bin/elasticshell` on unix, or `bin/elasticshell.bat` on windows

Help
------------------------------

Use the help() command to have a look at the elasticshell help.
Every command is exposed as a javascript function. If you want to get help for a specific command, just type its name without the curly brackets. The help output is currently available only for a few available commands, some more documentation will be added soon.

Auto-suggestions
------------------------------

Have a look at the auto-suggestions through the tab key to see the available commands and variables. JSON is native within the elasticshell, thus auto-suggestions are available within JSON objects too.

Connecting to a cluster
------------------------------

The elasticshell will automatically try to create a new transport client connected to a node running on localhost:9300. That default transport client will be registered with the es variable name, same result as the following command:

`var es = transportClient('localhost:9300');`


You can manually connect to a running elasticsearch cluster using the following commands:
`var es = transportClient('hostname:port')` creates a new [transport client](http://www.elasticsearch.org/guide/reference/java-api/client.html). You can provide a list of addresses too.

`var es = nodeClient('clusterName')` creates a new [node client](http://www.elasticsearch.org/guide/reference/java-api/client.html).

Let's index a document
------------------------------

```javascript
var jsonDoc = {
   "user": "kimchy",
   "postDate": "2009-11-15T13:12:00",
   "message": "Trying out Elastic Search, so far so good?"
}
```

`es.index('twitter','tweet','1', jsonDoc);`

We can also use the available index builder, which allows to use all the options available when [indexing a document](http://www.elasticsearch.org/guide/reference/api/index_.html):

`es.indexBuilder().index('twitter').type('tweet').id('1').source(jsonDoc).execute();`

Interact with a specific index or type
------------------------------

You can easily execute operations on a specific index or type like this:

`es.<index>.<type>.search();`

If the elasticshell does not accept the name of the index or type, for instance if the name contains a space or starts with a number, you can use the following alternate syntax:

`es['index name'].search();`


Let's retrieve a document
------------------------------

`es.twitter.tweet.get('1');`

The above command retrieves the previously indexed document using the [get API](http://www.elasticsearch.org/guide/reference/api/get.html).

Let's [search](http://www.elasticsearch.org/guide/reference/api/search/)
------------------------------

```javascript
var termQuery = {
    "query" : {
        "term" : { "user": "kimchy" }
    }
}
es.search(termQuery);
```

We can also use the search builder:
`es.searchBuilder().query(termQuery.query).execute();`

We can also make use of the elasticsearch [query builders](http://www.elasticsearch.org/guide/reference/java-api/query-dsl-queries.html) like this:

`es.searchBuilder().queryBuilder(QueryBuilders.termQuery('user','kimchy')).execute();`

Let's add a facet to the previous query
------------------------------

```javascript
es.searchBuilder()
    .query(QueryBuilders.termQuery('user','kimchy'))
    .facet(FacetBuilders.termsFacet('user').field('user')).execute();
```

All the elasticsearch API are exposed through the elasticshell.
Remember that the elasticshell is a javascript shell, thus you can have fun with javascript code.
On the other hand, the elasticshell has been built on top of the Rhino engine, which means that you can execute Java code too.

Contribute
=======

You can easily fork the project in order to contribute to it and send your pull requests.
Due to limitations on all IDEs console, it's recommended to test your changes from a real command line. The project uses in fact the great [JLine](https://github.com/jline/jline2) which needs to execute a bit of native code to provide nice auto-suggestions and so on.
You can easily run the elasticshell from the command line through maven using the following command which compiles the project and run its main class:

```mvn compile exec:java```

The above command has the same result as executing the elasticshell from the normal distribution using the executable provided within the bin folder.

License
=======

```
This software is licensed under the Apache 2 license, quoted below.

Copyright 2013 Luca Cavanna

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
