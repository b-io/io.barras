/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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

import java.io.Closeable;
import java.io.IOException;

public class Resources {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Resources() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Closes the specified {@link Closeable}.
	 * <p>
	 * @param closeable a {@link Closeable}
	 * <p>
	 * @return {@code true} if the resource is closed, {@code false} otherwise
	 */
	public static boolean close(final Closeable closeable) {
		return close(closeable, null);
	}

	/**
	 * Closes the specified {@link Closeable}, or prints a warning message if it is {@code null}.
	 * <p>
	 * @param closeable a {@link Closeable}
	 * @param message   a warning message
	 * <p>
	 * @return {@code true} if the resource is closed, {@code false} otherwise
	 */
	public static boolean close(final Closeable closeable, final String message) {
		if (closeable != null) {
			try {
				closeable.close();
				return true;
			} catch (final IOException ex) {
				IO.error(ex);
			}
		} else if (message != null) {
			IO.warn(message);
		}
		return false;
	}

	/**
	 * Closes the specified {@link AutoCloseable}.
	 * <p>
	 * @param closeable an {@link AutoCloseable}
	 * <p>
	 * @return {@code true} if the resource is closed, {@code false} otherwise
	 * <p>
	 * @since 1.7
	 */
	public static boolean close(final AutoCloseable closeable) {
		return close(closeable, null);
	}

	/**
	 * Closes the specified {@link AutoCloseable}, or prints a warning message if it is
	 * {@code null}.
	 * <p>
	 * @param closeable an {@link AutoCloseable}
	 * @param message   a warning message
	 * <p>
	 * @return {@code true} if the resource is closed, {@code false} otherwise
	 * <p>
	 * @since 1.7
	 */
	public static boolean close(final AutoCloseable closeable, final String message) {
		if (closeable != null) {
			try {
				closeable.close();
				return true;
			} catch (final Exception ex) {
				IO.error(ex);
			}
		} else if (message != null) {
			IO.warn(message);
		}
		return false;
	}
}
