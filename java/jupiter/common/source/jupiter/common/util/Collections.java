/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io>
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
package jupiter.common.util;

import java.util.Collection;

public class Collections {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String DEFAULT_DELIMITER = ", ";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Collections() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to join
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link String} representation of the specified {@link Collection} of type {@code T}
	 */
	public static <T> String join(final Collection<T> collection) {
		return Strings.joinWith(collection, DEFAULT_DELIMITER);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link String} representation of the specified {@link Collection} of type {@code T}
	 */
	public static <T> String toString(final Collection<T> collection) {
		return Strings.parenthesize(Strings.joinWith(collection, DEFAULT_DELIMITER));
	}

	/**
	 * Returns a {@link String} representation of the specified {@link Collection} of type {@code T}
	 * joined by {@code delimiter} (and wrapped with parentheses if {@code useParentheses}).
	 * <p>
	 * @param <T>            the type of the {@link Collection} to convert
	 * @param collection     a {@link Collection} of type {@code T}
	 * @param delimiter      a {@link String}
	 * @param useParentheses the option specifying whether to use parentheses
	 * <p>
	 * @return a {@link String} representation of the specified {@link Collection} of type {@code T}
	 *         joined by {@code delimiter} (and wrapped with parentheses if {@code useParentheses})
	 */
	public static <T> String toString(final Collection<T> collection, final String delimiter,
			final boolean useParentheses) {
		if (useParentheses) {
			return Strings.parenthesize(Strings.joinWith(collection, delimiter));
		}
		return Strings.joinWith(collection, delimiter);
	}
}
