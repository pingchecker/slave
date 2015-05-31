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
package eu.matejkormuth.pingchecker;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import eu.matejkormuth.pingchecker.services.CheckingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

class Slave {

    private static final Logger log = LoggerFactory.getLogger(Slave.class);

    // Services
    private CheckingService checkingService;
    private EbeanServer ebeanServer;

    private boolean isRunning = true;

    public void boot() {
        loadSettings();
        setupEbeans();
        setupServices();
        registerChecks();
        readConsole();
    }

    private void loadSettings() {
        log.info("Loading settings...");

        // TODO: Settings, currently we use defaults.
    }

    private void setupEbeans() {
        log.info("Setting up database connection...");
        ebeanServer = EbeanServerFactory.create("default");
    }

    private void setupServices() {
        log.info("Setting up services...");
        checkingService = new CheckingService();
    }

    private void registerChecks() {
        log.info("Registering periodic checks...");
    }

    private void readConsole() {
        log.info("Load complete!");

        Scanner scanner = new Scanner(System.in);
        while (this.isRunning) {
            String line = scanner.nextLine();
            if (line.contains("exit")) {
                this.isRunning = false;
            }
        }

        this.shutdown();
    }

    private void shutdown() {
        log.info("Shutting down...");
        checkingService.shutdown();
        log.info("Shutting down...");
    }

    public CheckingService getCheckingService() {
        return checkingService;
    }

    public EbeanServer getEbeanServer() {
        return ebeanServer;
    }
}
