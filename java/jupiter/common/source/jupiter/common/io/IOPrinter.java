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

import java.io.IOException;
import java.util.List;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
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
	 * The {@link List} of {@link IOHandler}.
	 */
	protected final List<IOHandler> handlers;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link IOPrinter} with the specified array of {@link IOHandler}.
	 * <p>
	 * @param handlers the array of {@link IOHandler}
	 */
	public IOPrinter(final IOHandler... handlers) {
		super();
		this.handlers = new ExtendedList<IOHandler>(handlers);
	}

	/**
	 * Constructs an {@link IOPrinter} with the specified {@link List} of {@link IOHandler}.
	 * <p>
	 * @param handlers the {@link List} of {@link IOHandler}
	 */
	public IOPrinter(final List<IOHandler> handlers) {
		super();
		this.handlers = handlers;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the specified content {@link Object} in the standard output (or standard error if
	 * {@code isError}) with the {@link List} of {@link IOHandler}.
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	@Override
	public boolean print(final Object content, final boolean isError) {
		boolean status = true;
		for (final IOHandler handler : handlers) {
			status &= handler.print(content, isError);
		}
		return status;
	}

	//////////////////////////////////////////////

	/**
	 * Prints the specified {@link Message} and terminates the line.
	 * <p>
	 * @param message the {@link Message} to print
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	@Override
	public boolean println(final Message message) {
		boolean status = true;
		for (final IOHandler handler : handlers) {
			status &= handler.println(message);
		}
		return status;
	}

	/**
	 * Prints the specified content {@link Object} in the standard output (or standard error if
	 * {@code isError}) and terminates the line with the {@link List} of {@link IOHandler}.
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	@Override
	public boolean println(final Object content, final boolean isError) {
		boolean status = true;
		for (final IOHandler handler : handlers) {
			status &= handler.println(content, isError);
		}
		return status;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLOSEABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Closes {@code this}.
	 */
	@Override
	public void close() {
		for (final IOHandler handler : handlers) {
			Resources.close(handler);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public IOPrinter clone() {
		return new IOPrinter(handlers);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof IOPrinter)) {
			return false;
		}
		final IOPrinter otherIOPrinter = (IOPrinter) other;
		return Objects.equals(handlers, otherIOPrinter.handlers);
	}

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see #equals(Object)
	 * @see System#identityHashCode(Object)
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, handlers);
	}
}
