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

import java.util.List;

public class IOPrinter
		extends IOHandler {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The IO handlers.
	 */
	protected final List<IOHandler> handlers;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public IOPrinter(final List<IOHandler> handlers) {
		super();
		this.handlers = handlers;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the specified content with the IO handlers.
	 * <p>
	 * @param content the {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 */
	@Override
	public void print(final Object content, final boolean isError) {
		for (final IOHandler handler : handlers) {
			handler.print(content, isError);
		}
	}

	/**
	 * Prints the specified content and terminates the line with the IO handlers.
	 * <p>
	 * @param content the {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 */
	@Override
	public void println(final Object content, final boolean isError) {
		for (final IOHandler handler : handlers) {
			handler.println(content, isError);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEANERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clears the IO handlers.
	 */
	@Override
	public void clear() {
		for (final IOHandler handler : handlers) {
			handler.clear();
		}
	}
}
