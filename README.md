elasticshell - a shell for elasticsearch
==============================

The elasticshell is a javascript shell written in Java.
It allows to interact with a running [elasticsearch](http://www.elasticsearch.org) cluster using the [Java API](http://www.elasticsearch.org/guide/reference/java-api/).

Getting Started
==============================

Installation
------------------------------

* [Download](http://www.javanna.net/downloads/elasticshell-0.20.5.zip) and unzip the elasticshell distribution
* Run `bin/elasticshell` on unix, or `bin/elasticshell.bat` on windows

Help
------------------------------

Use the help() command to have a look at the elasticshell help.
Every command is exposed as a javascript function. If you want to get help for a specific command, just type its name without the curly brackets.

Auto-suggestions
------------------------------

Have a look at the auto-suggestions through the tab key to see the available commands and variables. JSON is native within the elasticshell, thus auto-suggestions are available within JSON objects too.

Connecting to a cluster
------------------------------

The elasticshell will automatically try to create a new transport client connected to a node running on localhost:9300. That default transport client will be registered with the es variable name, same result as the following command:

`var es = transportClient('localhost:9300');`


You can manually connect to a running elasticsearch cluster using the following commands:
`transportClient('hostname:port')` creates a new [transport client](http://www.elasticsearch.org/guide/reference/java-api/client.html). You can provide a list of addresses too.

`nodeClient('clusterName')` creates a new [node client](http://www.elasticsearch.org/guide/reference/java-api/client.html).

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
`es.searchBuilder().query(termQuery);`

We can also make use of the elasticsearch [query builders](http://www.elasticsearch.org/guide/reference/java-api/query-dsl-queries.html) like this:

`es.searchBuilder().query(QueryBuilders.termQuery('user','kimchy')).execute();`

Let's add a facet to the previous query
------------------------------

```javascript
var userFacet = {
   "user" : { "terms" : {"field" : "user"} }
}
es.searchBuilder().query(termQuery).facets(userFacet).execute();
```

```javascript
es.searchBuilder()
    .query(QueryBuilders.termQuery('user','kimchy'))
    .facet(FacetBuilders.termsFacet('user').field('user')).execute();
```

All the elasticsearch API are exposed through the elasticshell.
Remember that the elasticshell is a javascript shell, thus you can have fun executing javascript code.
On the other hand, the elasticshell has been built on top of the Rhino engine, which means that you can execute Java code too.