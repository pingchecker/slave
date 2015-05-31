/**
 * slave - Tool to periodically check services availability
 * Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * <p>
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

import eu.matejkormuth.pingchecker.beans.Ping;
import eu.matejkormuth.pingchecker.beans.Target;
import eu.matejkormuth.pingchecker.tests.AvailabilityTest;
import eu.matejkormuth.pingchecker.tests.AvailabilityTestFactory;
import eu.matejkormuth.pingchecker.tests.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CheckingService {

    private static final Logger log = LoggerFactory.getLogger(CheckingService.class);

    private final ExecutorService threadPool;
    private final ScheduledExecutorService scheduler;
    private final AvailabilityTestFactory factory;

    public CheckingService() {
        log.info("Creating cached thread pool for tasks...");
        threadPool = Executors.newCachedThreadPool();
        log.info("Create scheduled thread pool with 2 threads...");
        scheduler = Executors.newScheduledThreadPool(2);
        factory = new AvailabilityTestFactory();
    }

    public void register(Target target) {
        log.debug("Registering target {}...", target.getAddress());
        Runnable invokeCheckTask = () -> check(target);
        scheduler.scheduleAtFixedRate(invokeCheckTask, 10, target.getCheckInterval(), TimeUnit.SECONDS);
    }

    private void check(Target target) {
        // Create task.
        Runnable checkTask = () -> {
            // Invoke availability test.
            AvailabilityTest test = factory.create(target);
            log.debug("Running availability test of type {} on target {}", test.getClass().getName(), target.getAddress());
            TestResult testResult = test.check(target);
            log.debug("Result of this test is: {}", testResult.getLatencyOrErrorCode());
            // Create Ping bean and save it.
            Ping pingResult = new Ping();
            pingResult.setTarget(target);
            pingResult.setPing(testResult.getLatencyOrErrorCode());
            log.debug("Saving new Ping record...");
            // TODO: Submit Ping entity to DB.
        };
        // Submit task to thread pool.
        threadPool.submit(checkTask);
    }

    public void shutdown() {
        log.info("Shutting down all thread pools.");
        threadPool.shutdown();
        scheduler.shutdown();
    }
}
