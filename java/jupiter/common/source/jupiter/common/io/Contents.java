/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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

public class Contents {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Contents}.
	 */
	protected Contents() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of {@link Content}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Content},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Content;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link Content}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link Content},
	 *         {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return Content.class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Content} is {@code null}, {@code "null"} or empty.
	 * <p>
	 * @param content the {@link Content} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link Content} is {@code null}, {@code "null"} or
	 *         empty, {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final Content content) {
		return content == null || content.isNullOrEmpty();
	}

	/**
	 * Tests whether the specified {@link Content} is non-{@code null} and empty.
	 * <p>
	 * @param content the {@link Content} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link Content} is non-{@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final Content content) {
		return content != null && content.isEmpty();
	}

	/**
	 * Tests whether the specified {@link Content} is non-{@code null}, not {@code "null"} and
	 * non-empty.
	 * <p>
	 * @param content the {@link Content} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link Content} is non-{@code null}, not {@code "null"}
	 *         and non-empty, {@code false} otherwise
	 */
	public static boolean isNonEmpty(final Content content) {
		return content != null && content.isNonEmpty();
	}
}
