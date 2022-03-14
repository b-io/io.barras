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

import jupiter.common.util.Bytes;
import jupiter.common.util.Strings;

public class ByteArguments
		extends Arguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String NAME = "byte";
	public static final String NAMES = NAME + "s";

	public static final String ARRAY_NAME = "byte array";
	public static final String ARRAY_NAMES = ARRAY_NAME + "s";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link ByteArguments}.
	 */
	protected ByteArguments() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Byte requireNonNull(final Byte value) {
		return Arguments.requireNonNull(value, NAME);
	}

	public static byte[] requireNonNull(final byte[] array) {
		return Arguments.requireNonNull(array, ARRAY_NAME);
	}

	public static <T> T[] requireNonNull(final T[] array) {
		return Arguments.requireNonNull(array, ARRAY_NAME);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void requireByte(final Object object) {
		if (CHECK_ARGS) {
			requireByte(object, "object");
		}
	}

	public static void requireByte(final Object object, final String name) {
		if (CHECK_ARGS && !Bytes.is(requireNonNull(object, name))) {
			throw new IllegalArgumentException(Strings.paste("The specified", Strings.quote(name),
					"is not a", NAME));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static byte require(final byte found, final byte expected) {
		if (CHECK_ARGS && found != expected) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, "is wrong",
					expectedButFound(found, expected)));
		}
		return found;
	}

	//////////////////////////////////////////////

	public static void requireEquals(final byte a, final byte b) {
		if (CHECK_ARGS && a != b) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAMES,
					"are not equal", isNotEqualTo(a, b)));
		}
	}

	//////////////////////////////////////////////

	public static byte requireGreaterThan(final byte found, final byte expectedLowerBound) {
		if (CHECK_ARGS && found <= expectedLowerBound) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, found,
					"is lower or equal to", expectedLowerBound));
		}
		return found;
	}

	public static byte requireGreaterOrEqualTo(final byte found, final byte expectedLowerBound) {
		if (CHECK_ARGS && found < expectedLowerBound) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, found,
					"is lower than", expectedLowerBound));
		}
		return found;
	}

	public static byte requireLessThan(final byte found, final byte expectedUpperBound) {
		if (CHECK_ARGS && found >= expectedUpperBound) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, found,
					"is greater or equal to", expectedUpperBound));
		}
		return found;
	}

	public static byte requireLessOrEqualTo(final byte found, final byte expectedUpperBound) {
		if (CHECK_ARGS && found > expectedUpperBound) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, found,
					"is greater than", expectedUpperBound));
		}
		return found;
	}

	//////////////////////////////////////////////

	public static byte requireNegative(final byte found) {
		if (CHECK_ARGS && found >= 0) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, found,
					"is zero or positive"));
		}
		return found;
	}

	public static byte requireNonNegative(final byte found) {
		if (CHECK_ARGS && found < 0) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, found,
					"is negative"));
		}
		return found;
	}

	public static byte requireNonZero(final byte found) {
		if (CHECK_ARGS && found == 0) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, found,
					"is zero"));
		}
		return found;
	}

	public static byte requirePositive(final byte found) {
		if (CHECK_ARGS && found <= 0) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, found,
					"is zero or negative"));
		}
		return found;
	}

	public static byte requireNonPositive(final byte found) {
		if (CHECK_ARGS && found > 0) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, found,
					"is positive"));
		}
		return found;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static byte[] requireNonEmpty(final byte[] array) {
		if (CHECK_ARGS) {
			requireNonEmpty(requireNonNull(array).length);
		}
		return array;
	}

	public static void requireNonEmpty(final int length) {
		if (CHECK_ARGS && length == 0) {
			throw new IllegalArgumentException(Strings.paste("The specified", ARRAY_NAME,
					"is empty"));
		}
	}

	//////////////////////////////////////////////

	public static byte[] requireLength(final byte[] array, final int expectedLength) {
		if (CHECK_ARGS) {
			requireLength(requireNonNull(array).length, expectedLength);
		}
		return array;
	}

	public static void requireLength(final int foundLength, final int expectedLength) {
		if (CHECK_ARGS && foundLength != expectedLength) {
			throw new IllegalArgumentException(Strings.paste("The specified", ARRAY_NAME,
					"has wrong length", expectedButFound(foundLength, expectedLength)));
		}
	}

	//////////////////////////////////////////////

	public static byte[] requireMinLength(final byte[] array, final int minExpectedLength) {
		if (CHECK_ARGS) {
			requireMinLength(requireNonNull(array).length, minExpectedLength);
		}
		return array;
	}

	public static void requireMinLength(final int foundLength, final int minExpectedLength) {
		if (CHECK_ARGS && foundLength < minExpectedLength) {
			throw new IllegalArgumentException(Strings.paste("The specified", ARRAY_NAME,
					"has a length", foundLength,
					"inferior to", minExpectedLength));
		}
	}

	//////////////////////////////////////////////

	public static byte[] requireMaxLength(final byte[] array, final int maxExpectedLength) {
		if (CHECK_ARGS) {
			requireMaxLength(requireNonNull(array).length, maxExpectedLength);
		}
		return array;
	}

	public static void requireMaxLength(final int foundLength, final int maxExpectedLength) {
		if (CHECK_ARGS && foundLength > maxExpectedLength) {
			throw new IllegalArgumentException(Strings.paste("The specified", ARRAY_NAME,
					"has a length", foundLength,
					"superior to", maxExpectedLength));
		}
	}

	//////////////////////////////////////////////

	public static void requireSameLength(final byte[] a, final byte[] b) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, requireNonNull(b).length);
		}
	}

	public static void requireSameLength(final byte[] a, final int bLength) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, bLength);
		}
	}

	public static void requireSameLength(final int aLength, final int bLength) {
		if (CHECK_ARGS && aLength != bLength) {
			throw new IllegalArgumentException(Strings.paste("The specified", ARRAY_NAMES,
					"do not have the same length", isNotEqualTo(aLength, bLength)));
		}
	}
}
