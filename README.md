How to create transportClient / nodeClient

explain registration of default client

showIndexes / showTypes / showAliases

import class and package


mention auto-complete (with limitations on fluent interface)

index type and name shortcuts (non-allowed chars replaced with _)

index parameters index type id source / IndexRequest

get parameters index type id/ GetRequest


builders usually require client, not exposed to the shell

can use json directly (much better than string), not doable when using es objects (need to stringify)


conversion from ToXContent to native json so that builders and native json are interchangeable
JSON.parse(queryBuilder.toString())

write about difference from what you see (string representation of objects) and what you assign

es['index'] instead of es.index if index contain special characters
