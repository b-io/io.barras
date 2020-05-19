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

import static jupiter.common.Formats.CHARSET;
import static jupiter.common.io.InputOutput.IO;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.PhantomReference;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;

import jupiter.common.model.ICloneable;
import jupiter.common.thread.Worker;

public abstract class IOHandler
		extends Worker<Message, Integer>
		implements Closeable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link IOHandler}.
	 */
	protected IOHandler() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEARERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clears {@code this}.
	 */
	public void clear() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the specified content {@link Object} in the standard output (or in the standard error
	 * if {@code isError}).
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public abstract boolean print(final Object content, final boolean isError);

	//////////////////////////////////////////////

	/**
	 * Prints the specified {@link Message} in the standard output (or in the standard error if
	 * {@code isError}) and terminates the line.
	 * <p>
	 * @param message the {@link Message} to print
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean println(final Message message) {
		return println(message, message.getSeverityLevel().isError());
	}

	/**
	 * Prints the specified content {@link Object} in the standard output (or in the standard error
	 * if {@code isError}) and terminates the line.
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public abstract boolean println(final Object content, final boolean isError);

	/**
	 * Prints the specified {@link InputStream} in the standard output (or in the standard error if
	 * {@code isError}) and terminates the line.
	 * <p>
	 * @param input   the {@link InputStream} of the data to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean println(final InputStream input, final boolean isError) {
		return println(input, CHARSET, isError);
	}

	/**
	 * Prints the specified {@link InputStream} in the standard output (or in the standard error if
	 * {@code isError}) and terminates the line.
	 * <p>
	 * @param input   the {@link InputStream} of the data to print
	 * @param charset the {@link Charset} of the data to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean println(final InputStream input, final Charset charset, final boolean isError) {
		BufferedReader reader = null;
		try {
			reader = InputOutput.createReader(input, charset);
			boolean status = true;
			String line;
			while ((line = reader.readLine()) != null) {
				status &= println(line, isError);
			}
			return status;
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			Resources.close(reader);
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Integer call(final Message input) {
		println(input);
		return InputOutput.EXIT_SUCCESS;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLOSEABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Closes {@code this}.
	 */
	public void close() {
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
	public abstract IOHandler clone();

	/**
	 * Disposes of system resources and performs a cleanup.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>This method is called by the garbage collector on an {@link Object} when the garbage
	 * collection determines that there are no more references to the {@link Object}.</dd>
	 * </dl>
	 *
	 * @see PhantomReference
	 * @see WeakReference
	 */
	@Override
	@SuppressWarnings("deprecation")
	protected void finalize() {
		IO.debug(this, " is finalized");
		try {
			close();
		} finally {
			try {
				super.finalize();
			} catch (final Throwable ignored) {
			}
		}
	}
}
