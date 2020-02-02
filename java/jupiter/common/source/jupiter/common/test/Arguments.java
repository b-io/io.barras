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
package jupiter.common.test;

import jupiter.common.util.Strings;

public class Arguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile boolean CHECK_ARGS = true;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Arguments}.
	 */
	protected Arguments() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String expectedButFound(final Object found, final Object expected) {
		return Strings.parenthesize(Strings.join(Strings.quote(expected), " expected but ",
				Strings.quote(found), " found"));
	}

	public static String atLeastExpectedButFound(final Object found, final Object expected) {
		return Strings.parenthesize(Strings.join("at least ", Strings.quote(expected),
				" expected but ", Strings.quote(found), " found"));
	}

	public static String atMostExpectedButFound(final Object found, final Object expected) {
		return Strings.parenthesize(Strings.join("at most ", Strings.quote(expected),
				" expected but ", Strings.quote(found), " found"));
	}

	//////////////////////////////////////////////

	public static String betweenExpectedButFound(final Object found, final Object expectedFrom,
			final Object expectedTo) {
		return betweenExpectedButFound(found, expectedFrom, expectedTo, true, false);
	}

	public static String betweenExpectedButFound(final Object found, final Object expectedFrom,
			final Object expectedTo, final boolean isUpperInclusive) {
		return betweenExpectedButFound(found, expectedFrom, expectedTo, true, isUpperInclusive);
	}

	public static String betweenExpectedButFound(final Object found, final Object expectedFrom,
			final Object expectedTo, final boolean isLowerInclusive,
			final boolean isUpperInclusive) {
		return Strings.parenthesize(Strings.join("between ", Strings.quote(expectedFrom),
				isLowerInclusive ? " (inclusive)" : " (exclusive)", " and ",
				Strings.quote(expectedTo), isUpperInclusive ? " (inclusive)" : " (exclusive)",
				" expected but ", Strings.quote(found), " found"));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String isNotEqualTo(final Object a, final Object b) {
		return Strings.parenthesize(
				Strings.join(Strings.quote(a), " is not equal to ", Strings.quote(b)));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> T requireNonNull(final T object) {
		if (CHECK_ARGS) {
			return requireNonNull(object, "The specified object is null");
		}
		return object;
	}

	public static <T> T requireNonNull(final T object, final String name) {
		if (CHECK_ARGS && object == null) {
			throw new NullPointerException("The specified ".concat(Strings.quote(name))
					.concat(" is null"));
		}
		return object;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> void require(final T found, final T expected) {
		if (CHECK_ARGS && !requireNonNull(found).equals(expected)) {
			throw new IllegalArgumentException(Strings.join("The specified object is wrong ",
					expectedButFound(found, expected)));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> void requireEquals(final T a, final T b) {
		if (CHECK_ARGS && !requireNonNull(a).equals(requireNonNull(b))) {
			throw new IllegalArgumentException(
					Strings.join("The specified objects are not equal ", isNotEqualTo(a, b)));
		}
	}
}
