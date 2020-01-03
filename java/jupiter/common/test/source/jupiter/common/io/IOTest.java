/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.common.io;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Strings.EMPTY;

import jupiter.common.io.IO.SeverityLevel;
import jupiter.common.io.IO.Type;
import jupiter.common.io.console.IConsole;
import jupiter.common.io.console.SystemConsole;
import jupiter.common.test.Test;

public class IOTest
		extends Test {

	public IOTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public void testPrint() {
		IO.test("• print");

		final String content = "This is a test message";
		IO.print(content, false);
		IO.print(content, true);
		IO.print(EMPTY, 0, false);
		IO.println();
	}

	/**
	 * Test of println method, of class IO.
	 */
	public void testPrintln_Object() {
		IO.test("• println_Object");

		final String content = "This is a test message";
		IO.println(content, false);
		IO.println(content, true);
		IO.println();
	}

	/**
	 * Test of println method, of class IO.
	 */
	public void testPrintln_Message() {
		IO.test("• println_Message");

		IO.println(IO.test("This is a test message"));
		IO.println(IO.warn("This is a warning message"));
		IO.println();
	}

	/**
	 * Test of printInput method, of class IO.
	 */
	public void testPrintInput() {
		IO.test("• printInput");

		IO.printInput();
		IO.println();
	}

	/**
	 * Test of bar method, of class IO.
	 */
	public void testBar() {
		IO.test("• bar");

		IO.bar();
	}

	/**
	 * Test of trace method, of class IO.
	 */
	public void testTrace() {
		IO.test("• trace");
		IO.setSeverityLevel(SeverityLevel.TRACE);

		final String content = "This is a test message";
		final Message result = IO.trace(content);
		assertEquals(content, result.getContent());
		assertEquals(new Message(Type.OUTPUT, SeverityLevel.TRACE, content), IO.trace(content));

		IO.setSeverityLevel(SeverityLevel.TEST);
	}

	/**
	 * Test of debug method, of class IO.
	 */
	public void testDebug() {
		IO.test("• debug");
		IO.setSeverityLevel(SeverityLevel.DEBUG);

		final String content = "This is a test message";
		final Message result = IO.debug(content);
		assertEquals(content, result.getContent());

		IO.setSeverityLevel(SeverityLevel.TEST);
	}

	/**
	 * Test of test method, of class IO.
	 */
	public void testTest() {
		IO.test("• test");

		final String content = "This is a test message";
		final Message result = IO.test(content);
		assertEquals(content, result.getContent());
	}

	/**
	 * Test of info method, of class IO.
	 */
	public void testInfo() {
		IO.test("• info");

		final String content = "This is a test message";
		final Message result = IO.info(content);
		assertEquals(content, result.getContent());
	}

	/**
	 * Test of result method, of class IO.
	 */
	public void testResult() {
		IO.test("• result");

		final String content = "This is a test message";
		final Message result = IO.result(content);
		assertEquals(content, result.getContent());
	}

	/**
	 * Test of warn method, of class IO.
	 */
	public void testWarn_Object() {
		IO.test("• warn_Object");

		final String content = "This is a test message";
		final Message result = IO.warn(content);
		assertEquals(content, result.getContent());
	}

	/**
	 * Test of warn method, of class IO.
	 */
	public void testWarn_Object_Exception() {
		IO.test("• warn_Object_Exception");

		final String content = "This is a test message";
		IO.warn(content, new Exception("This is a test warning"));
	}

	/**
	 * Test of error method, of class IO.
	 */
	public void testError_Object() {
		IO.test("• error_Object");

		final String content = "This is a test message";
		final Message result = IO.error(content);
		assertEquals(content, result.getContent());
	}

	/**
	 * Test of error method, of class IO.
	 */
	public void testError_Exception() {
		IO.test("• error_Exception");

		IO.error(new Exception("This is a test error"));
	}

	/**
	 * Test of error method, of class IO.
	 */
	public void testError_Object_Exception() {
		IO.test("• error_Object_Exception");

		final String content = "This is a test message";
		IO.error(content, new Exception("This is a test error"));
	}

	/**
	 * Test of fail method, of class IO.
	 */
	public void testFail_Object() {
		IO.test("• fail_Object");

		final String content = "This is a test message";
		final Message result = IO.fail(content);
		assertEquals(content, result.getContent());
	}

	/**
	 * Test of fail method, of class IO.
	 */
	public void testFail_Exception() {
		IO.test("• fail_Exception");

		IO.fail(new Exception("This is a test failure"));
	}

	/**
	 * Test of setConsole method, of class IO.
	 */
	public void testSetConsole() {
		IO.test("• setConsole");

		final IConsole console = new SystemConsole();
		IO.setConsole(console);
	}

	/**
	 * Test of clear method, of class IO.
	 */
	public void testClear() {
		IO.test("• clear");

		IO.clear();
	}
}
