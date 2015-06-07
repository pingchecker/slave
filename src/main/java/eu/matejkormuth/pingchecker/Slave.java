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
package eu.matejkormuth.pingchecker;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import eu.matejkormuth.pingchecker.beans.Category;
import eu.matejkormuth.pingchecker.beans.Ping;
import eu.matejkormuth.pingchecker.beans.Target;
import eu.matejkormuth.pingchecker.beans.User;
import eu.matejkormuth.pingchecker.services.CheckingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

class Slave {

    private static final Logger log = LoggerFactory.getLogger(Slave.class);

    // Services
    private CheckingService checkingService;

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

        ServerConfig config = new ServerConfig();
        config.setName("default");

        config.setRegister(true);
        config.setDefaultServer(true);

        config.addClass(User.class);
        config.addClass(Ping.class);
        config.addClass(Category.class);
        config.addClass(Target.class);

        // config.setDdlGenerate(true);
        // config.setDdlRun(true);

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver("com.mysql.jdbc.Driver");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("");
        dataSourceConfig.setUrl("jdbc:mysql://127.0.0.1:3306/pingchecker");

        config.setDataSourceConfig(dataSourceConfig);

        System.out.println("Creating default ebean server...");
        EbeanServerFactory.create(config);
        System.out.println("Default ebean server created!");
    }

    private void setupServices() {
        log.info("Setting up services...");
        checkingService = new CheckingService();
    }

    private void registerChecks() {
        log.info("Registering periodic checks...");

        List<Target> targetList = Ebean.createQuery(Target.class)
                .findList();

        targetList.forEach(checkingService::register);
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

}
