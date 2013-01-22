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
package org.elasticsearch.shell.client.executors;

import org.elasticsearch.action.mlt.MoreLikeThisRequest;

/**
 * @author Luca Cavanna
 */
public class MoreLikeThisHelper {

    public static MoreLikeThisRequest newMoreLikeThisRequest(MoreLikeThisRequest moreLikeThisRequest, String index) {
        MoreLikeThisRequest newMoreLikeThisRequest = new MoreLikeThisRequest(index);
        newMoreLikeThisRequest.type(moreLikeThisRequest.type())
                .searchQueryHint(moreLikeThisRequest.searchQueryHint())
                .searchScroll(moreLikeThisRequest.searchScroll())
                .searchSize(moreLikeThisRequest.searchSize())
                .searchFrom(moreLikeThisRequest.searchFrom())
                .searchSource(moreLikeThisRequest.searchSource(), false)
                .searchTypes(moreLikeThisRequest.searchTypes())
                .stopWords(moreLikeThisRequest.stopWords())
                .searchIndices(moreLikeThisRequest.searchIndices())
                .boostTerms(moreLikeThisRequest.boostTerms())
                .fields(moreLikeThisRequest.fields())
                .id(moreLikeThisRequest.id())
                .maxDocFreq(moreLikeThisRequest.maxDocFreq())
                .maxQueryTerms(moreLikeThisRequest.maxQueryTerms())
                .maxWordLen(moreLikeThisRequest.maxWordLen())
                .minDocFreq(moreLikeThisRequest.minDocFreq())
                .minTermFreq(moreLikeThisRequest.minTermFreq())
                .minWordLen(moreLikeThisRequest.minWordLen())
                .percentTermsToMatch(moreLikeThisRequest.percentTermsToMatch())
                .routing(moreLikeThisRequest.routing());
        return newMoreLikeThisRequest;
    }
}
