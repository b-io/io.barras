/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2020 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;
import static jupiter.common.util.Strings.EMPTY;

import jupiter.common.io.InputOutput.SeverityLevel;
import jupiter.common.io.InputOutput.Type;
import jupiter.common.io.console.IConsole;
import jupiter.common.io.console.SystemConsole;
import jupiter.common.test.Test;

public class IOTest
		extends Test {

	public IOTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests print method, of class IO.
	 */
	public void testPrint() {
		IO.test(BULLET, " print");

		final String content = "This is a message";
		IO.print(content, false);
		IO.print(content, true);
		IO.print(EMPTY, 0, false);
		IO.println();
	}

	/**
	 * Tests printInput method, of class IO.
	 */
	public void testPrintInput() {
		IO.test(BULLET, " printInput");

		IO.printInput();
		IO.println();
	}

	//////////////////////////////////////////////

	/**
	 * Tests println method, of class IO.
	 */
	public void testPrintln_Object() {
		IO.test(BULLET, " println_Object");

		final String content = "This is a message";
		IO.println(content, false);
		IO.println(content, true);
		IO.println();
	}

	/**
	 * Tests println method, of class IO.
	 */
	public void testPrintln_Message() {
		IO.test(BULLET, " println_Message");

		IO.println(IO.test("This is a test message"));
		IO.println(IO.warn("This is a warning message"));
		IO.println();
	}

	//////////////////////////////////////////////

	/**
	 * Tests bar method, of class IO.
	 */
	public void testBar() {
		IO.test(BULLET, " bar");

		IO.bar();
	}

	//////////////////////////////////////////////

	/**
	 * Tests trace method, of class IO.
	 */
	public void testTrace() {
		IO.test(BULLET, " trace");
		IO.setSeverityLevel(SeverityLevel.TRACE);

		final String content = "This is a trace message";
		assertEquals(content, IO.trace(content).getContent());
		assertEquals(new Message(Type.OUTPUT, SeverityLevel.TRACE, content), IO.trace(content));

		IO.setSeverityLevel(SeverityLevel.TEST);
	}

	/**
	 * Tests debug method, of class IO.
	 */
	public void testDebug() {
		IO.test(BULLET, " debug");
		IO.setSeverityLevel(SeverityLevel.DEBUG);

		final String content = "This is a debug message";
		assertEquals(content, IO.debug(content).getContent());

		IO.setSeverityLevel(SeverityLevel.TEST);
	}

	/**
	 * Tests test method, of class IO.
	 */
	public void testTest() {
		IO.test(BULLET, " test");

		final String content = "This is a test message";
		assertEquals(content, IO.test(content).getContent());
	}

	/**
	 * Tests info method, of class IO.
	 */
	public void testInfo() {
		IO.test(BULLET, " info");

		final String content = "This is an info message";
		assertEquals(content, IO.info(content).getContent());
	}

	/**
	 * Tests result method, of class IO.
	 */
	public void testResult() {
		IO.test(BULLET, " result");

		final String content = "This is a result message";
		assertEquals(content, IO.result(content).getContent());
	}

	//////////////////////////////////////////////

	/**
	 * Tests warn method, of class IO.
	 */
	public void testWarn_Object() {
		IO.test(BULLET, " warn_Object");

		final String content = "This is a warning message";
		assertEquals(content, IO.warn(content).getContent());
	}

	/**
	 * Tests warn method, of class IO.
	 */
	public void testWarn_Exception() {
		IO.test(BULLET, " warn_Exception");

		IO.warn(new Exception("This is a warning exception"));
	}

	/**
	 * Tests warn method, of class IO.
	 */
	public void testWarn_Object_Exception() {
		IO.test(BULLET, " warn_Object_Exception");

		final String content = "This is a warning message";
		IO.warn(content, new Exception("This is a warning exception"));
	}

	//////////////////////////////////////////////

	/**
	 * Tests error method, of class IO.
	 */
	public void testError_Object() {
		IO.test(BULLET, " error_Object");

		final String content = "This is an error message";
		assertEquals(content, IO.error(content).getContent());
	}

	/**
	 * Tests error method, of class IO.
	 */
	public void testError_Exception() {
		IO.test(BULLET, " error_Exception");

		IO.error(new Exception("This is an error exception"));
	}

	/**
	 * Tests error method, of class IO.
	 */
	public void testError_Object_Exception() {
		IO.test(BULLET, " error_Object_Exception");

		final String content = "This is an error message";
		IO.error(content, new Exception("This is an error exception"));
	}

	//////////////////////////////////////////////

	/**
	 * Tests fail method, of class IO.
	 */
	public void testFail_Object() {
		IO.test(BULLET, " fail_Object");

		final String content = "This is a failure message";
		assertEquals(content, IO.fail(content).getContent());
	}

	/**
	 * Tests fail method, of class IO.
	 */
	public void testFail_Exception() {
		IO.test(BULLET, " fail_Exception");

		IO.fail(new Exception("This is a failure exception"));
	}

	/**
	 * Tests fail method, of class IO.
	 */
	public void testFail_Object_Exception() {
		IO.test(BULLET, " fail_Exception");

		final String content = "This is a failure message";
		IO.fail(content, new Exception("This is a failure exception"));
	}

	//////////////////////////////////////////////

	/**
	 * Tests setConsole method, of class IO.
	 */
	public void testSetConsole() {
		IO.test(BULLET, " setConsole");

		final IConsole console = new SystemConsole();
		IO.setConsole(console);
	}

	/**
	 * Tests clear method, of class IO.
	 */
	public void testClear() {
		IO.test(BULLET, " clear");

		IO.clear();
	}
}
