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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Luca Cavanna
 *
 * Helper that extracts the name of the objects that are useful to provide suggestions
 * from the current user input, given also the cursor position
 *
 */
public class IdentifierTokenizer {

    /**
     * Extracts the name of the objects that are useful in order to provide suggestions
     * given the current user input and the cursor position.
     * Strips out the dots and the arguments from any function call
     * e.g. Given Requests.indexRequests.index('index_name').ty will return [Requests,indexRequest,index,ty]
     * @param buffer the user input
     * @param cursor the cursor position
     * @return the list of the identifiers useful to provide suggestions
     */
    public List<Identifier> tokenize(String buffer, int cursor) {
        //Looks backward and collects a list of identifiers
        //We stop as soon as we find something that is not a java identifier part or . or ).
        //We also strip out the arguments from any function call
        List<Identifier> identifiers = new ArrayList<Identifier>();
        Identifier identifier = new Identifier(cursor);
        int m = cursor - 1;
        while (m >= 0) {
            //identifier = new Identifier(m);
            char c = buffer.charAt(m--);
            //we keep adding chars to the same name till the identifier is finished
            //using isJavaIdentifierPart even though it isn't always correct. Javascript identifiers have different rules,
            //and the first character is quite different too.
            if (Character.isJavaIdentifierPart(c)) {
                if (identifier.getEndPosition() == cursor) {
                    identifier = new Identifier(cursor - 1);
                }
                identifier.append(c);
                continue;
            }

            if (c == ']' && mightBeIdentifier(buffer, m)) {
                //TODO only support for identifiers among square brackets and quotation marks, no support for arrays
                int m2 = m - 1;
                char c2 = buffer.charAt(m2);
                boolean foundIdentifier = false;
                Identifier id = new Identifier(m+1).incrementLength(2);
                while (m2 > 0) {
                    if (c2=='"' || c2 == '\'') {
                        if (buffer.charAt(--m2)=='[') {
                            id.incrementLength(2);
                            foundIdentifier = true;
                        }
                        break;
                    }
                    id.append(c2);
                    c2 = buffer.charAt(--m2);
                }

                if (foundIdentifier) {
                    identifiers.add(id.reverse());
                    m = m2 - 1;
                    identifier = new Identifier(m);
                    continue;
                } else {
                    break;
                }
            }

            //when the identifier is finished we add it to the list of identifiers found and we start with a new identifier
            identifiers.add(identifier.reverse());
            identifier = new Identifier(m);

            if (c == ' ') {
                Identifier newKeyword = extractNewKeyword(buffer, m);
                if (newKeyword != null) {
                    identifiers.add(newKeyword.reverse());
                    break;
                }
            }

            if (c != '.') {
                break;
            }

            //if we've found ). we ignore all the arguments from the function call, till the previous open bracket
            if (c == '.' && m > 1 && buffer.charAt(m) == ')') {
                int m2 = stripArguments(buffer, m - 1);
                if (m2 < 0) {
                    break;
                }
                identifier.incrementLength(m - m2);
                m = m2;
            }

        }

        if (identifier.getLength() > 0 || identifiers.isEmpty()) {
            //adding the last identifier
            identifiers.add(identifier.reverse());
        }

        //no need to reverse if empty or only one element
        if (identifiers.size() > 1) {
            Collections.reverse(identifiers);
        }

        return identifiers;
    }

    private int stripArguments(String buffer, int cursor) {
        int innerBrackets = 0;
        while (cursor >= 0) {
            char c2 = buffer.charAt(cursor);
            if (c2 == ')') {
                innerBrackets++;
            }
            if (c2 == '(' && --innerBrackets == -1) {
                return --cursor;
            }
            cursor--;
        }
        return cursor;
    }

    private boolean mightBeIdentifier(String buffer, int cursor) {
        //not enough characters for an identifier among square brackets  e.g. a['b']
        if (cursor < 4) {
            return false;
        }
        char c = buffer.charAt(cursor);
        return c == '"' | c == '\'';
    }

    private Identifier extractNewKeyword(String buffer, int cursor) {
        int m = cursor;
        char c = buffer.charAt(m);
        //ignores any additional whitespace e.g. new    Test().
        while (c == ' ' && m > 0) {
            m--;
            c = buffer.charAt(m);
        }

        char[] newOp = new char[]{'n', 'e', 'w'};
        int pos = newOp.length - 1;

        while (m > 0 && pos >= 0) {
            if (c != newOp[pos--]) {
                return null;
            }
            c = buffer.charAt(--m);
        }
        //new can be the first word, otherwise it must not appear after a java identifier part (e.g. abcnew )
        if ( (m == 0 && pos == 0)
                || !Character.isJavaIdentifierPart(buffer.charAt(m)) ) {
            Identifier id = new Identifier("wen", cursor);
            int diff = cursor - m;
            if (diff>3) {
                id.incrementLength(diff - 3);
            }
            return id;
        }
        return null;
    }
}
