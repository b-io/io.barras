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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public abstract class IOHandler {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected IOHandler() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the specified content.
	 * <p>
	 * @param content the {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 */
	public abstract void print(final Object content, final boolean isError);

	/**
	 * Prints the specified content and then terminates the line.
	 * <p>
	 * @param content the {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 */
	public abstract void println(final Object content, final boolean isError);

	/**
	 * Prints the specified message (whether in the standard output or in the standard error) and
	 * then terminates the line.
	 * <p>
	 * @param message the {@link Message} to print
	 */
	public void println(final Message message) {
		println(message, message.getLevel().isError());
	}

	/**
	 * Prints the specified {@link InputStream} (whether in the standard output or in the standard
	 * error) and then terminates the line.
	 * <p>
	 * @param input   the {@link InputStream} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 */
	public void println(final InputStream input, final boolean isError) {
		BufferedReader reader = null;
		try {
			reader = IO.createReader(input);
			String line;
			while ((line = reader.readLine()) != null) {
				println(line, isError);
			}
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			Resources.close(reader);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEANERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clears the IO handler.
	 */
	public abstract void clear();
}
