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
 * @author Luca Cavanna
 */
public class Identifier {

    private final int endPosition;
    private StringBuilder nameBuilder;
    private int length;

    public Identifier(String name, int endPosition) {
        this.nameBuilder = new StringBuilder(name);
        this.endPosition = endPosition;
        this.length = name.length();
    }

    public Identifier(int endPosition) {
        this.nameBuilder = new StringBuilder();
        this.endPosition = endPosition;
        this.length = 0;
    }

    public Identifier incrementLength() {
        this.length++;
        return this;
    }

    public Identifier incrementLength(int n) {
        this.length += n;
        return this;
    }

    public Identifier append(char c) {
        this.nameBuilder.append(c);
        incrementLength();
        return this;
    }

    public Identifier reverse() {
        this.nameBuilder.reverse();
        return this;
    }

    public String getName() {
        return nameBuilder.toString();
    }

    public int getEndPosition() {
        return endPosition;
    }

    public int getLength() {
        return length;
    }

    public int getStartPosition() {
        return this.endPosition - (this.length > 0 ? this.length - 1 : 0);
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "startPosition=" + getStartPosition() +
                ", endPosition=" + endPosition +
                ", name=" + getName() +
                ", length=" + length +
                '}';
    }
}
