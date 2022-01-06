/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jupiter.log;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import java.util.Enumeration;

import jupiter.common.io.InputOutput.SeverityLevel;
import jupiter.common.test.Test;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

public class IOLog4jTest
		extends Test {

	protected static final Logger LOG = Logger.getLogger(IOLog4jTest.class);

	public IOLog4jTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link IOLog4j#append}.
	 */
	public void testTrace() {
		IO.test(BULLET, "trace");
		IO.setSeverityLevel(SeverityLevel.TRACE);

		final String content = "This is a trace message";
		LOG.trace(content);
	}

	/**
	 * Tests {@link IOLog4j#append}.
	 */
	public void testDebug() {
		IO.test(BULLET, "debug");
		IO.setSeverityLevel(SeverityLevel.DEBUG);

		final String content = "This is a debug message";
		LOG.debug(content);
	}

	/**
	 * Tests {@link IOLog4j#append}.
	 */
	public void testInfo() {
		IO.test(BULLET, "info");

		final String content = "This is an info message";
		LOG.info(content);
	}

	//////////////////////////////////////////////

	/**
	 * Tests {@link IOLog4j#append}.
	 */
	public void testWarn() {
		IO.test(BULLET, "warn");

		final String content = "This is a warning message";
		LOG.warn(content);
	}

	/**
	 * Tests {@link IOLog4j#append}.
	 */
	public void testError() {
		IO.test(BULLET, "error");

		final String content = "This is an error message";
		LOG.error(content);
	}

	/**
	 * Tests {@link IOLog4j#append}.
	 */
	public void testFatal() {
		IO.test(BULLET, "fatal");

		final String content = "This is a fatal message";
		LOG.fatal(content);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link IOLog4j#close}.
	 */
	public void testClose() {
		IO.test(BULLET, "close");

		final Enumeration<?> appenders = LOG.getAllAppenders();
		while (appenders.hasMoreElements()) {
			final Appender appender = (Appender) appenders.nextElement();
			appender.close();
		}
	}
}
