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
package eu.matejkormuth.pingchecker.tests.icmp;

import eu.matejkormuth.pingchecker.beans.Target;
import eu.matejkormuth.pingchecker.tests.AvailabilityTest;
import eu.matejkormuth.pingchecker.tests.TestResult;
import eu.matejkormuth.pingchecker.tests.socket.SocketConnectionAvailabilityTest;
import eu.matejkormuth.pingchecker.utils.Os;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SystemPingAvailabilityTest implements AvailabilityTest {

    private static final Logger log = LoggerFactory.getLogger(SocketConnectionAvailabilityTest.class);
    private PingProcess process;

    public SystemPingAvailabilityTest() {
        // Create inner implementation by operating system.
        switch (Os.getType()) {
            case WINDOWS:
                process = new WindowsPingProcess();
                break;
            case LINUX:
                process = new LinuxPingProcess();
                break;
            case UNKNOWN:
                log.error("This operating system is not yet supported by SystemPingAvailabilityTest!");
                break;
        }
    }

    @Override
    public TestResult check(Target target) {
        try {
            process.run(target.getAddress());
            String output = process.getOutput();
            // TODO: Parse output.

            return TestResult.success(0);
        } catch (IOException e) {
            log.error("Can't check target {}!", target.getAddress());
            log.error("Error occurred: ", e);
            return TestResult.destinationUnreachable();
        }
    }
}
