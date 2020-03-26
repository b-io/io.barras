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

public class ShortArguments
		extends Arguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link ShortArguments}.
	 */
	protected ShortArguments() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static short require(final short found, final short expected) {
		if (CHECK_ARGS && found != expected) {
			throw new IllegalArgumentException(
					"The specified short number is wrong " + expectedButFound(found, expected));
		}
		return found;
	}

	public static void requireEquals(final short a, final short b) {
		if (CHECK_ARGS && a != b) {
			throw new IllegalArgumentException(
					"The specified short numbers are not equal " + isNotEqualTo(a, b));
		}
	}

	public static short requireGreaterThan(final short found, final short expectedLowerBound) {
		if (CHECK_ARGS && found <= expectedLowerBound) {
			throw new IllegalArgumentException("The specified short number " + found +
					" is lower or equal to " + expectedLowerBound);
		}
		return found;
	}

	public static short requireGreaterOrEqualTo(final short found, final short expectedLowerBound) {
		if (CHECK_ARGS && found < expectedLowerBound) {
			throw new IllegalArgumentException(
					"The specified short number " + found + " is lower than " + expectedLowerBound);
		}
		return found;
	}

	public static short requireLessThan(final short found, final short expectedUpperBound) {
		if (CHECK_ARGS && found >= expectedUpperBound) {
			throw new IllegalArgumentException("The specified short number " + found +
					" is greater or equal to " + expectedUpperBound);
		}
		return found;
	}

	public static short requireLessOrEqualTo(final short found, final short expectedUpperBound) {
		if (CHECK_ARGS && found > expectedUpperBound) {
			throw new IllegalArgumentException("The specified short number " + found +
					" is greater than " + expectedUpperBound);
		}
		return found;
	}

	public static short requireNegative(final short found) {
		if (CHECK_ARGS && found >= 0L) {
			throw new IllegalArgumentException(
					"The specified short number " + found + " is zero or positive");
		}
		return found;
	}

	public static short requireNonNegative(final short found) {
		if (CHECK_ARGS && found < 0L) {
			throw new IllegalArgumentException(
					"The specified short number " + found + " is negative");
		}
		return found;
	}

	public static short requireNonZero(final short found) {
		if (CHECK_ARGS && found == 0L) {
			throw new IllegalArgumentException("The specified short number " + found + " is zero");
		}
		return found;
	}

	public static short requirePositive(final short found) {
		if (CHECK_ARGS && found <= 0L) {
			throw new IllegalArgumentException(
					"The specified short number " + found + " is zero or negative");
		}
		return found;
	}

	public static short requireNonPositive(final short found) {
		if (CHECK_ARGS && found > 0L) {
			throw new IllegalArgumentException(
					"The specified short number " + found + " is positive");
		}
		return found;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static short[] requireNonEmpty(final short... array) {
		if (CHECK_ARGS) {
			requireNonEmpty(requireNonNull(array).length);
		}
		return array;
	}

	public static void requireNonEmpty(final int length) {
		if (CHECK_ARGS && length == 0) {
			throw new IllegalArgumentException("The specified short array is empty");
		}
	}

	//////////////////////////////////////////////

	public static short[] requireLength(final short[] array, final int expectedLength) {
		if (CHECK_ARGS) {
			requireLength(requireNonNull(array).length, expectedLength);
		}
		return array;
	}

	public static void requireLength(final int foundLength, final int expectedLength) {
		if (CHECK_ARGS && foundLength != expectedLength) {
			throw new IllegalArgumentException("The specified short array has wrong length " +
					expectedButFound(foundLength, expectedLength));
		}
	}

	public static short[] requireMinLength(final short[] array, final int minExpectedLength) {
		if (CHECK_ARGS) {
			requireMinLength(requireNonNull(array).length, minExpectedLength);
		}
		return array;
	}

	public static void requireMinLength(final int foundLength, final int minExpectedLength) {
		if (CHECK_ARGS && foundLength < minExpectedLength) {
			throw new IllegalArgumentException("The specified short array has a length " +
					foundLength + " inferior to " + minExpectedLength);
		}
	}

	public static short[] requireMaxLength(final short[] array, final int maxExpectedLength) {
		if (CHECK_ARGS) {
			requireMaxLength(requireNonNull(array).length, maxExpectedLength);
		}
		return array;
	}

	public static void requireMaxLength(final int foundLength, final int maxExpectedLength) {
		if (CHECK_ARGS && foundLength > maxExpectedLength) {
			throw new IllegalArgumentException("The specified short array has a length " +
					foundLength + " superior to " + maxExpectedLength);
		}
	}

	//////////////////////////////////////////////

	public static void requireSameLength(final short[] a, final int bLength) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, bLength);
		}
	}

	public static void requireSameLength(final short[] a, final short[] b) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, requireNonNull(b).length);
		}
	}

	public static void requireSameLength(final int a, final int b) {
		if (CHECK_ARGS && a != b) {
			throw new IllegalArgumentException(
					"The specified short arrays do not have the same length " + isNotEqualTo(a, b));
		}
	}
}
