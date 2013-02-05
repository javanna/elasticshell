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

/**
 * Represents an identifier extracted from the user input in order to provide auto-suggestions
 * Allows to keep track of the identifier itself and its position within the whole user input
 *
 * @author Luca Cavanna
 */
public class Identifier {

    private final int lastPosition;
    private StringBuilder nameBuilder;
    private int offset;

    public Identifier(String name, int lastPosition) {
        this.nameBuilder = new StringBuilder(name);
        this.lastPosition = lastPosition;
        this.offset = name.length();
    }

    public Identifier(int lastPosition) {
        this.nameBuilder = new StringBuilder();
        this.lastPosition = lastPosition;
        this.offset = 0;
    }

    /**
     * Increments the offset of the identifier
     * @return the identifier itself
     */
    public Identifier incrementOffset() {
        this.offset++;
        return this;
    }

    /**
     * Increments the offset of the identifier by the value of the input parameter
     * @param n the input parameter
     * @return the identifier itself
     */
    public Identifier incrementOffset(int n) {
        this.offset += n;
        return this;
    }

    /**
     * Appends a character to the identifier name
     * @param c the character that needs to be added to the identifier name
     * @return the identifier itself
     */
    public Identifier append(char c) {
        this.nameBuilder.append(c);
        incrementOffset();
        return this;
    }

    /**
     * Allows to revert the identifier name
     * Needs to be called when the identifier is complete
     * @return the identifier itself
     */
    public Identifier reverseName() {
        this.nameBuilder.reverse();
        return this;
    }

    /**
     * Getter for the identifier name
     * @return the name of the identifier
     */
    public String getName() {
        return nameBuilder.toString();
    }

    /**
     * Getter for the last position of the identifier within the user input
     * @return the last position of the identifier
     */
    public int getLastPosition() {
        return lastPosition;
    }

    /**
     * Getter for the offset (or length) of the identifier within the user input
     * @return the offset of the identifier
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Returns the position where the identifier starts within the current user input
     * @return the first position of the identifier
     */
    public int getFirstPosition() {
        return this.lastPosition - (this.offset > 0 ? this.offset - 1 : 0);
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "startPosition=" + getFirstPosition() +
                ", lastPosition=" + lastPosition +
                ", name=" + getName() +
                ", offset=" + offset +
                '}';
    }
}
