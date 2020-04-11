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
package jupiter.common.io.console;

import static jupiter.common.io.InputOutput.IO;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Scanner;

public class SystemConsole
		implements IConsole, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link Charset} of the input lines to read.
	 */
	protected final Charset charset;
	/**
	 * The {@link Scanner} of the input lines to read.
	 */
	protected final Scanner scanner;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SystemConsole} by default.
	 */
	public SystemConsole() {
		this(Charset.defaultCharset());
	}

	/**
	 * Constructs a {@link SystemConsole} with the specified {@link Charset}.
	 * <p>
	 * @param charset the {@link Charset} of the input lines to read
	 */
	public SystemConsole(final Charset charset) {
		this.charset = charset;
		scanner = new Scanner(getIn(), charset.name());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS / WRITERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the standard input {@link InputStream}.
	 * <p>
	 * @return the standard input {@link InputStream}
	 */
	public InputStream getIn() {
		return System.in;
	}

	/**
	 * Returns the input line.
	 * <p>
	 * @return the input line
	 */
	public String getInputLine() {
		IO.input();
		return scanner.nextLine();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the standard output {@link PrintStream}.
	 * <p>
	 * @return the standard output {@link PrintStream}
	 */
	public PrintStream getOut() {
		return System.out;
	}

	/**
	 * Returns the standard error {@link PrintStream}.
	 * <p>
	 * @return the standard error {@link PrintStream}
	 */
	public PrintStream getErr() {
		return System.err;
	}
}
