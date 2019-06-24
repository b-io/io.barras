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

import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;

public class IOPrinter
		extends IOHandler {

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
	 * The IO handlers.
	 */
	protected final List<IOHandler> handlers;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public IOPrinter(final IOHandler... handlers) {
		super();
		this.handlers = Arrays.<IOHandler>asList(handlers);
	}

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
	 * @param content the content {@link Object}
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
	 * @param content the content {@link Object}
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 */
	@Override
	public void println(final Object content, final boolean isError) {
		for (final IOHandler handler : handlers) {
			handler.println(content, isError);
		}
	}

	/**
	 * Prints the specified message (whether in the standard output or in the standard error) and
	 * then terminates the line.
	 * <p>
	 * @param message a {@link Message}
	 */
	@Override
	public void println(final Message message) {
		for (final IOHandler handler : handlers) {
			handler.println(message);
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


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see Cloneable
	 */
	@Override
	public IOPrinter clone() {
		return new IOPrinter(handlers);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the {@link Object} to compare against for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Content)) {
			return false;
		}
		final IOPrinter otherIOPrinter = (IOPrinter) other;
		return Objects.equals(handlers, otherIOPrinter.handlers);
	}

	/**
	 * Returns the hash code {@code int} value for {@code this}.
	 * <p>
	 * @return the hash code {@code int} value for {@code this}
	 *
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, handlers);
	}
}
