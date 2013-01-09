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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Default {@link Scheduler} implementation based on {@link ScheduledExecutorService}
 * @author Luca Cavanna
 */
public class DefaultScheduler implements Scheduler {

    private final ScheduledExecutorService scheduler;

    public DefaultScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void schedule(Runnable runnable, long intervalSeconds) {
        this.scheduler.scheduleAtFixedRate(runnable, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    public void shutdown() {
        this.scheduler.shutdownNow();
    }
}
