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
package org.elasticsearch.shell.scheduler;

/**
 * Handles all the scheduled actions, operations that are repeated multiple times at a fixed time rate
 *
 * @author Luca Cavanna
 */
public interface Scheduler {
    /**
     * Schedules a new action
     * @param runnable the new {@link Runnable} action to be scheduled
     * @param intervalSeconds the number of seconds between an execution and the next one
     */
    public void schedule(Runnable runnable, long intervalSeconds);

    /**
     * Shutdowns the scheduler
     */
    public void shutdown();
}
