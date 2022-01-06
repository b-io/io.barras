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

import jupiter.common.util.Arrays;
import jupiter.common.util.Integers;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class ArrayArguments
		extends Arguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link ArrayArguments}.
	 */
	protected ArrayArguments() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void requireArray(final Object object) {
		if (CHECK_ARGS) {
			requireArray(object, "object");
		}
	}

	public static void requireArray(final Object object, final String name) {
		if (CHECK_ARGS && !Arrays.is(requireNonNull(object, name))) {
			throw new IllegalArgumentException(Strings.paste("The specified", Strings.quote(name),
					"is not an array"));
		}
	}

	//////////////////////////////////////////////

	/**
	 * Checks if {@code a} is either the same as, or is a superclass or superinterface of, the class
	 * or interface represented by {@code b}.
	 * <p>
	 * @param a a {@link Class}
	 * @param b another {@link Class}
	 */
	public static void requireAssignableFrom(final Class<?> a, final Class<?> b) {
		if (CHECK_ARGS && !a.isAssignableFrom(b)) {
			throw new IllegalArgumentException(Strings.paste(
					"Cannot store", Objects.getName(b),
					"in an array of", Objects.getName(a)));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> T[] requireNonEmpty(final T[] array) {
		if (CHECK_ARGS) {
			return requireNonEmpty(array, "array");
		}
		return array;
	}

	public static <T> T[] requireNonEmpty(final T[] array, final String name) {
		if (CHECK_ARGS) {
			requireNonEmpty(requireNonNull(array, name).length, name);
		}
		return array;
	}

	public static void requireNonEmpty(final int length, final String name) {
		if (CHECK_ARGS && length == 0) {
			throw new IllegalArgumentException(Strings.paste("The specified", Strings.quote(name),
					"is empty"));
		}
	}

	//////////////////////////////////////////////

	public static <T> T[] requireLength(final T[] array, final int expectedLength) {
		if (CHECK_ARGS) {
			requireLength(requireNonNull(array).length, expectedLength);
		}
		return array;
	}

	public static void requireLength(final int foundLength, final int expectedLength) {
		if (CHECK_ARGS && foundLength != expectedLength) {
			throw new IllegalArgumentException("The specified array has wrong length " +
					expectedButFound(foundLength, expectedLength));
		}
	}

	public static <T> T[] requireMinLength(final T[] array, final int minExpectedLength) {
		if (CHECK_ARGS) {
			requireMinLength(requireNonNull(array).length, minExpectedLength);
		}
		return array;
	}

	public static void requireMinLength(final int foundLength, final int minExpectedLength) {
		if (CHECK_ARGS && foundLength < minExpectedLength) {
			throw new IllegalArgumentException(Strings.paste(
					"The specified array has a length", foundLength,
					"inferior to", minExpectedLength));
		}
	}

	public static <T> T[] requireMaxLength(final T[] array, final int maxExpectedLength) {
		if (CHECK_ARGS) {
			requireMaxLength(requireNonNull(array).length, maxExpectedLength);
		}
		return array;
	}

	public static void requireMaxLength(final int foundLength, final int maxExpectedLength) {
		if (CHECK_ARGS && foundLength > maxExpectedLength) {
			throw new IllegalArgumentException(Strings.paste(
					"The specified array has a length", foundLength,
					"superior to", maxExpectedLength));
		}
	}

	//////////////////////////////////////////////

	public static <T> void requireSameLength(final T[] a, final int bLength) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, bLength);
		}
	}

	public static <T> void requireSameLength(final T[] a, final T[] b) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, requireNonNull(b).length);
		}
	}

	public static void requireSameLength(final int a, final int b) {
		if (CHECK_ARGS && a != b) {
			throw new IllegalArgumentException("The specified arrays do not have the same length " +
					isNotEqualTo(a, b));
		}
	}

	//////////////////////////////////////////////

	public static void requireIndex(final int foundIndex, final int maxExpectedLength) {
		if (CHECK_ARGS) {
			requireIndex(foundIndex, maxExpectedLength, true, false);
		}
	}

	public static void requireIndex(final int foundIndex, final int maxExpectedLength,
			final boolean isUpperInclusive) {
		if (CHECK_ARGS) {
			requireIndex(foundIndex, maxExpectedLength, true, isUpperInclusive);
		}
	}

	public static void requireIndex(final int foundIndex, final int maxExpectedLength,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		if (CHECK_ARGS && !Integers.isBetween(foundIndex, 0, maxExpectedLength, isLowerInclusive,
				isUpperInclusive)) {
			throw new IllegalArgumentException("The specified index is out of bounds " +
					betweenExpectedButFound(foundIndex, 0, maxExpectedLength, isLowerInclusive,
							isUpperInclusive));
		}
	}
}
