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
package jupiter.common.io.console;

import static jupiter.common.io.IO.IO;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Scanner;

public class SystemConsole
		implements IConsole {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final Charset charset;
	protected final Scanner scanner;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public SystemConsole() {
		charset = Charset.defaultCharset();
		scanner = new Scanner(getIn(), charset.name());
	}

	public SystemConsole(final Charset charset) {
		this.charset = charset;
		scanner = new Scanner(getIn(), charset.name());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// INPUT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the input line.
	 * <p>
	 * @return the input line
	 */
	public String input() {
		IO.printInput();
		return scanner.nextLine();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OUTPUT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the "standard" input stream.
	 * <p>
	 * @return the "standard" input stream
	 */
	public InputStream getIn() {
		return System.in;
	}

	/**
	 * Returns the "standard" output stream.
	 * <p>
	 * @return the "standard" output stream
	 */
	public PrintStream getOut() {
		return System.out;
	}

	/**
	 * Returns the "standard" error output stream.
	 * <p>
	 * @return the "standard" error output stream
	 */
	public PrintStream getErr() {
		return System.err;
	}
}
