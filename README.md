How to create transportClient / nodeClient

explain registration of default client

showIndexes / showTypes / showAliases

import class and package


mention auto-complete

index type and name shortcuts (non-allowed chars replaced with _)

index parameters index type id source / IndexRequest

get parameters index type id/ GetRequest


can use json directly (much better than string), not doable when using es objects (need to parse json from toString)
conversion from ToXContent to native json so that builders and native json are interchangeable
JSON.parse(queryBuilder.toString())
there are no strings anymore as input, only native json
ToXContent => JSON parse command??
FilterBuilders don't override toString!!! would be nice to be able to get json out of them anyway

explain difference from what you see (string representation of objects) and what you assign

es['index'] instead of es.index if index contain special characters

percolate example

multi index and multi type command only using builders
