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
package eu.matejkormuth.pingchecker.tests.socket;

import eu.matejkormuth.pingchecker.beans.Target;
import eu.matejkormuth.pingchecker.tests.AvailabilityTest;
import eu.matejkormuth.pingchecker.tests.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketConnectionAvailabilityTest implements AvailabilityTest {

    private static final Logger log = LoggerFactory.getLogger(SocketConnectionAvailabilityTest.class);
    private static final int NS_TO_MS = 1000000;

    private static InetSocketAddress createAddress(String address) throws IllegalArgumentException {
        if (address.contains(":")) {
            return new InetSocketAddress(address.split(":")[0], Integer.valueOf(address.split(":")[1]));
        } else {
            throw new IllegalArgumentException("Address must be in format 'ip:port'.");
        }
    }

    @Override
    public TestResult check(Target target) {
        try {
            InetSocketAddress address = createAddress(target.getAddress());

            Socket socket = new Socket();
            long connectionStartTime = System.nanoTime();
            socket.connect(address);
            long latencyNs = System.nanoTime() - connectionStartTime;
            socket.close();

            return TestResult.success((int) (latencyNs / NS_TO_MS));
        } catch (IllegalArgumentException e) {
            log.info("Target {} has invalid address!", target.getAddress());
            return TestResult.invalidAddress();
        } catch (IOException e) {
            log.info("Target {} is unreachable!", target.getAddress());
            return TestResult.destinationUnreachable();
        }
    }
}
