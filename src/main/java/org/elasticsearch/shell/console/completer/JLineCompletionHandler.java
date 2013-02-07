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

import jline.console.ConsoleReader;
import jline.console.CursorBuffer;
import jline.console.completer.CandidateListCompletionHandler;
import jline.console.completer.CompletionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * JLine {@link CompletionHandler} that displays auto-suggestions
 * @see CandidateListCompletionHandler
 *
 * @author Luca Cavanna
 */
public class JLineCompletionHandler implements CompletionHandler {

    private static final Logger logger = LoggerFactory.getLogger(JLineCompletionHandler.class);

    public boolean complete(final ConsoleReader reader, final List<CharSequence> candidates, final int pos) throws IOException {
        CursorBuffer buffer = reader.getCursorBuffer();

        // if there is only one completion, then fill in the buffer
        if (candidates.size() == 1) {
            CharSequence candidate = candidates.get(0);
            // fail if the only candidate is the same as the current buffer
            if (candidate.equals(buffer.toString())) {
                return false;
            }

            updateBuffer(reader, candidate, pos);
            return true;
        }

        if (candidates.size() > 1) {
            String commonPrefix = getUnambiguousCompletions(candidates);
            updateBuffer(reader, commonPrefix, pos);
        }

        printCandidates(reader, candidates);
        //next line seems to cause small problems when cursor is  not at the end of the buffer
        //e.g. FilterBuilders.queryFilter(QueryBuilders.)
        reader.drawLine();
        return true;
    }

    /**
     * Prints out the candidates. If the size of the candidates is greater than the
     * {@link ConsoleReader#getAutoprintThreshold}, a warning is printed
     *
     * @param candidates the list of candidates to print
     */
    protected void printCandidates(final ConsoleReader reader, List<CharSequence> candidates) throws IOException {

        Set<CharSequence> distinctCandidates = new HashSet<CharSequence>(candidates);
        if (distinctCandidates.size() > reader.getAutoprintThreshold()) {
            reader.println();
            reader.print(Messages.DISPLAY_CANDIDATES.format(candidates.size()));
            reader.flush();

            char[] allowed = {'y', 'n'};
            int c;
            while ((c = reader.readCharacter(allowed)) != -1) {
                //String input = new StringBuilder(c).toString();
                if (c=='n') {
                    reader.println();
                    return;
                }
                if (c=='y') {
                    break;
                }
                reader.beep();
            }
        }

        reader.println();
        reader.printColumns(sortCandidates(distinctCandidates));
    }

    protected List<CharSequence> sortCandidates(Set<CharSequence> candidates) {
        List<CharSequence> orderedCandidates = new ArrayList<CharSequence>(candidates);
        Collections.sort(orderedCandidates, new Comparator<CharSequence>() {
            @Override
            public int compare(CharSequence o1, CharSequence o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });
        return orderedCandidates;
    }

    protected void updateBuffer(final ConsoleReader reader, final CharSequence output, final int offset) throws IOException {
        while ((reader.getCursorBuffer().cursor > offset) && reader.backspace()) {

        }

        if (output != null && output.length() > 0) {
            reader.putString(output);
            reader.setCursorPosition(offset + output.length());
        }
    }

    /**
     * Returns a root that matches all the {@link String} elements of the specified {@link List},
     * or null if there are no commonalities. For example, if the list contains
     * <i>foobar</i>, <i>foobaz</i>, <i>foobuz</i>, the method will return <i>foob</i>.
     */
    protected String getUnambiguousCompletions(final List<CharSequence> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        CharSequence firstCandidate = candidates.get(0);
        StringBuilder commonPrefix = new StringBuilder();
        for (int i = 0; i < firstCandidate.length(); i++) {
            char c = firstCandidate.charAt(i);
            StringBuilder tmpPrefix = new StringBuilder(commonPrefix).append(c);
            if (!allCandidatesStartsWith(candidates, tmpPrefix.toString())) {
                break;
            }
            commonPrefix = tmpPrefix;
        }
        return commonPrefix.toString();
    }

    /**
     * @return true is all the elements of <i>candidates</i> start with <i>starts</i>
     */
    protected boolean allCandidatesStartsWith(final List<CharSequence> candidates, final String commonPrefix) {
        for (CharSequence candidate : candidates) {
            if (!candidate.toString().startsWith(commonPrefix)) {
                return false;
            }
        }
        return true;
    }

    private static enum Messages {
        DISPLAY_CANDIDATES;

        private static final ResourceBundle bundle = ResourceBundle.getBundle(CandidateListCompletionHandler.class.getName(), Locale.getDefault());

        public String format(final Object... args) {
            if (bundle == null) {
                return "";
            }
            return String.format(bundle.getString(name()), args);
        }
    }
}
