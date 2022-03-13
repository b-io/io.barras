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

import jupiter.common.util.Strings;

public class StringArguments
		extends Arguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String NAME = "text";
	public static String NAMES = NAME + "s";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link StringArguments}.
	 */
	protected StringArguments() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String requireNonNull(final String text) {
		return Arguments.requireNonNull(text, NAME);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void requireString(final Object object) {
		if (CHECK_ARGS) {
			requireString(object, "object");
		}
	}

	public static void requireString(final Object object, final String name) {
		if (CHECK_ARGS && !Strings.is(requireNonNull(object, name))) {
			throw new IllegalArgumentException(Strings.paste("The specified", Strings.quote(name),
					"is not a", NAME));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String require(final String found, final String expected) {
		if (CHECK_ARGS && !Strings.equals(found, expected)) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, "is wrong",
					expectedButFound(found, expected)));
		}
		return found;
	}

	//////////////////////////////////////////////

	public static void requireEquals(final String a, final String b) {
		if (CHECK_ARGS && !Strings.equals(a, b)) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAMES,
					"are not equal", isNotEqualTo(a, b)));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String requireNonEmpty(final String text) {
		if (CHECK_ARGS) {
			requireNonEmpty(requireNonNull(text).length());
		}
		return text;
	}

	public static void requireNonEmpty(final int length) {
		if (CHECK_ARGS && length == 0) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, "is empty"));
		}
	}

	//////////////////////////////////////////////

	public static String requireLength(final String text, final int expectedLength) {
		if (CHECK_ARGS) {
			requireLength(requireNonNull(text).length(), expectedLength);
		}
		return text;
	}

	public static void requireLength(final int foundLength, final int expectedLength) {
		if (CHECK_ARGS && foundLength != expectedLength) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME,
					"has wrong length", expectedButFound(foundLength, expectedLength)));
		}
	}

	//////////////////////////////////////////////

	public static String requireMinLength(final String text, final int minExpectedLength) {
		if (CHECK_ARGS) {
			requireMinLength(requireNonNull(text).length(), minExpectedLength);
		}
		return text;
	}

	public static void requireMinLength(final int foundLength, final int minExpectedLength) {
		if (CHECK_ARGS && foundLength < minExpectedLength) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME,
					"has a length", foundLength,
					"inferior to", minExpectedLength));
		}
	}

	//////////////////////////////////////////////

	public static String requireMaxLength(final String text, final int maxExpectedLength) {
		if (CHECK_ARGS) {
			requireMaxLength(requireNonNull(text).length(), maxExpectedLength);
		}
		return text;
	}

	public static void requireMaxLength(final int foundLength, final int maxExpectedLength) {
		if (CHECK_ARGS && foundLength > maxExpectedLength) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME,
					"has a length", foundLength,
					"superior to", maxExpectedLength));
		}
	}

	//////////////////////////////////////////////

	public static void requireSameLength(final String a, final String b) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length(), requireNonNull(b).length());
		}
	}

	public static void requireSameLength(final String a, final int bLength) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length(), bLength);
		}
	}

	public static void requireSameLength(final int aLength, final int bLength) {
		if (CHECK_ARGS && aLength != bLength) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAMES,
					"do not have the same length", isNotEqualTo(aLength, bLength)));
		}
	}
}
