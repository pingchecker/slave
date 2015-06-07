/**
 * ====
 *     pingchecker - Tool to periodically check services availability
 *     Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
 *     All rights reserved.
 *
 *     Redistribution and use in source and binary forms, with or without modification,
 *     are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 *
 *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *     ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *     DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *     ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *     (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *     LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *     ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *     (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====
 *
 * slave - Tool to periodically check services availability
 * Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.matejkormuth.pingchecker.services;

import com.avaje.ebean.Ebean;
import eu.matejkormuth.pingchecker.beans.Ping;
import eu.matejkormuth.pingchecker.beans.Target;
import eu.matejkormuth.pingchecker.tests.AvailabilityTest;
import eu.matejkormuth.pingchecker.tests.AvailabilityTestFactory;
import eu.matejkormuth.pingchecker.tests.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.concurrent.*;

public class CheckingService {

    private static final Logger log = LoggerFactory.getLogger(CheckingService.class);

    private final ExecutorService threadPool;
    private final ScheduledExecutorService scheduler;
    private final AvailabilityTestFactory factory;

    public CheckingService() {
        log.info("Creating cached thread pool for tasks...");
        threadPool = Executors.newCachedThreadPool();
        log.info("Create scheduled thread pool with 8 threads...");
        scheduler = Executors.newScheduledThreadPool(8);
        factory = new AvailabilityTestFactory();
    }

    public void register(final Target target) {
        log.info("Registering target {}...", target.getAddress());
        Runnable invokeCheckTask = () -> check(target);
        new Thread(() -> {
            try {
                scheduler.scheduleAtFixedRate(invokeCheckTask, 10, target.getCheckInterval(), TimeUnit.SECONDS).get();
            } catch (InterruptedException e) {
                log.error("Error", e);
            } catch (ExecutionException e) {
                log.error("Error", e);
            }
        }).start();
    }

    private void check(Target target) {
        // Create task.
        Runnable checkTask = () -> {
            // Invoke availability test.
            AvailabilityTest test = factory.create(target);
            log.info("Running availability test of type {} on target {}", test.getClass().getName(), target.getAddress());
            TestResult testResult = test.check(target);
            log.info("Result of this test is: {}", testResult.getLatencyOrErrorCode());
            // Create Ping bean and save it.
            Ping pingResult = new Ping();
            pingResult.setTarget(target);
            pingResult.setTimestamp(new Timestamp(System.currentTimeMillis() * 1000));
            pingResult.setPing(testResult.getLatencyOrErrorCode());
            log.info("Saving new Ping record...");
            // Submit Ping entity to DB.
            Ebean.save(pingResult);
            log.info("Record saved!");
        };
        // Submit task to thread pool.
        try {
            threadPool.submit(checkTask).get();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    public void shutdown() {
        log.info("Shutting down all thread pools.");
        threadPool.shutdown();
        scheduler.shutdown();
    }
}
