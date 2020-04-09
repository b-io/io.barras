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

import java.io.Closeable;
import java.io.IOException;

public class Resources {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Resources}.
	 */
	protected Resources() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Closes the specified {@link Closeable}.
	 * <p>
	 * @param closeable the {@link Closeable} to close (may be {@code null})
	 * <p>
	 * @return {@code true} if the resource is closed, {@code false} otherwise
	 */
	public static boolean close(final Closeable closeable) {
		return close(closeable, null);
	}

	/**
	 * Closes the specified {@link Closeable}, or prints a warning message {@link String} if it is
	 * {@code null}.
	 * <p>
	 * @param closeable the {@link Closeable} to close (may be {@code null})
	 * @param message   the warning message {@link String} (may be {@code null})
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


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of {@link Closeable}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Closeable},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Closeable;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link Closeable}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link Closeable},
	 *         {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return Closeable.class.isAssignableFrom(c);
	}
}
