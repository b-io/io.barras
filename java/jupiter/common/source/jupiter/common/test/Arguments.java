/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Strings.EMPTY;

import jupiter.common.util.Strings;

public class Arguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String NAME = "object";
	public static final String NAMES = NAME + "s";

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
		return Strings.parenthesize(Strings.paste(Strings.quote(expected), "expected but",
				Strings.quote(found), "found"));
	}

	public static String atLeastExpectedButFound(final Object found, final Object expected) {
		return Strings.parenthesize(Strings.paste("at least", Strings.quote(expected),
				"expected but", Strings.quote(found), "found"));
	}

	public static String atMostExpectedButFound(final Object found, final Object expected) {
		return Strings.parenthesize(Strings.paste("at most", Strings.quote(expected),
				"expected but", Strings.quote(found), "found"));
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
		return Strings.parenthesize(Strings.paste(
				"between", Strings.quote(expectedFrom),
				isLowerInclusive ? "(inclusive)" : "(exclusive)", "and",
				Strings.quote(expectedTo), isUpperInclusive ? "(inclusive)" : "(exclusive)",
				"expected but", Strings.quote(found), "found"));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String isNotEqualTo(final Object a, final Object b) {
		return Strings.parenthesize(Strings.paste(Strings.quote(a), "is not equal to",
				Strings.quote(b)));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> T requireNonNull(final T object) {
		if (CHECK_ARGS) {
			return requireNonNull(object, NAME, 1);
		}
		return object;
	}

	public static <T> T requireNonNull(final T object, final String name) {
		if (CHECK_ARGS) {
			return requireNonNull(object, name, 1);
		}
		return object;
	}

	public static <T> T requireNonNull(final T object, final String name, final int stackIndex) {
		if (CHECK_ARGS && object == null) {
			throw new NullPointerException(Strings.paste(
					"The specified argument", Strings.quote(name), "is null",
					IO.getSeverityLevel().isDebug() ?
							Tests.getStackTraceMessage(stackIndex + 1) : EMPTY));
		}
		return object;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> T require(final T found, final T expected) {
		if (CHECK_ARGS && !requireNonNull(found).equals(expected)) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, "is wrong",
					expectedButFound(found, expected)));
		}
		return found;
	}

	//////////////////////////////////////////////

	public static <T> void requireEquals(final T a, final T b) {
		if (CHECK_ARGS && !requireNonNull(a).equals(requireNonNull(b))) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAMES,
					"are not equal", isNotEqualTo(a, b)));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void requireFalse(final boolean found, final String message) {
		if (CHECK_ARGS && found) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void requireTrue(final boolean found, final String message) {
		if (CHECK_ARGS && !found) {
			throw new IllegalArgumentException(message);
		}
	}
}
