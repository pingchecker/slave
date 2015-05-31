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
package eu.matejkormuth.pingchecker.tests;

import eu.matejkormuth.pingchecker.beans.Ping;

public class TestResult {
    private short latencyOrErrorCode;
    private boolean successful;

    public static TestResult success(int latency) {
        TestResult testResult = new TestResult();
        testResult.latencyOrErrorCode = (short) latency;
        testResult.successful = true;
        return testResult;
    }

    public static TestResult destinationUnreachable() {
        TestResult testResult = new TestResult();
        testResult.latencyOrErrorCode = Ping.ErrorCodes.NOT_REACHABLE;
        testResult.successful = false;
        return testResult;
    }

    public static TestResult invalidAddress() {
        TestResult testResult = new TestResult();
        testResult.latencyOrErrorCode = Ping.ErrorCodes.INVALID_ADDRESS;
        testResult.successful = false;
        return testResult;
    }

    public short getLatencyOrErrorCode() {
        return latencyOrErrorCode;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
