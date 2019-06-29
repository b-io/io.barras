/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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

import jupiter.common.util.Floats;

public class FloatArguments
		extends Arguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link FloatArguments}.
	 */
	protected FloatArguments() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static float require(final float found, final float expected) {
		if (CHECK_ARGS && !Floats.equals(found, expected)) {
			throw new IllegalArgumentException(
					"The specified float number is wrong " + expectedButFound(found, expected));
		}
		return found;
	}

	public static void requireEquals(final float a, final float b) {
		if (CHECK_ARGS && !Floats.equals(a, b)) {
			throw new IllegalArgumentException(
					"The specified float numbers are not equal " + isNotEqualTo(a, b));
		}
	}

	public static float requireGreaterThan(final float found, final float expectedLowerBound) {
		if (CHECK_ARGS && found <= expectedLowerBound) {
			throw new IllegalArgumentException("The specified float number " + found +
					" is lower or equal to " + expectedLowerBound);
		}
		return found;
	}

	public static float requireGreaterOrEqualTo(final float found, final float expectedLowerBound) {
		if (CHECK_ARGS && found < expectedLowerBound) {
			throw new IllegalArgumentException(
					"The specified float number " + found + " is lower than " + expectedLowerBound);
		}
		return found;
	}

	public static float requireLessThan(final float found, final float expectedUpperBound) {
		if (CHECK_ARGS && found >= expectedUpperBound) {
			throw new IllegalArgumentException("The specified float number " + found +
					" is greater or equal to " + expectedUpperBound);
		}
		return found;
	}

	public static float requireLessOrEqualTo(final float found, final float expectedUpperBound) {
		if (CHECK_ARGS && found > expectedUpperBound) {
			throw new IllegalArgumentException("The specified float number " + found +
					" is greater than " + expectedUpperBound);
		}
		return found;
	}

	public static float requireNegative(final float found) {
		if (CHECK_ARGS && found >= 0f) {
			throw new IllegalArgumentException(
					"The specified float number " + found + " is zero or positive");
		}
		return found;
	}

	public static float requireNonNegative(final float found) {
		if (CHECK_ARGS && found < 0f) {
			throw new IllegalArgumentException(
					"The specified float number " + found + " is negative");
		}
		return found;
	}

	public static float requireNonZero(final float found) {
		if (CHECK_ARGS && found == 0f) {
			throw new IllegalArgumentException("The specified float number " + found + " is zero");
		}
		return found;
	}

	public static float requirePositive(final float found) {
		if (CHECK_ARGS && found <= 0f) {
			throw new IllegalArgumentException(
					"The specified float number " + found + " is zero or negative");
		}
		return found;
	}

	public static float requireNonPositive(final float found) {
		if (CHECK_ARGS && found > 0f) {
			throw new IllegalArgumentException(
					"The specified float number " + found + " is positive");
		}
		return found;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static float[] requireNonEmpty(final float... array) {
		if (CHECK_ARGS) {
			requireNonEmpty(requireNonNull(array).length);
		}
		return array;
	}

	public static void requireNonEmpty(final int length) {
		if (CHECK_ARGS && length == 0) {
			throw new IllegalArgumentException("The specified float array is empty");
		}
	}

	public static float[] requireLength(final float[] array, final int expectedLength) {
		if (CHECK_ARGS) {
			requireLength(requireNonNull(array).length, expectedLength);
		}
		return array;
	}

	public static void requireLength(final int foundLength, final int expectedLength) {
		if (CHECK_ARGS && foundLength != expectedLength) {
			throw new IllegalArgumentException("The specified float array has wrong length " +
					expectedButFound(foundLength, expectedLength));
		}
	}

	public static float[] requireMinLength(final float[] array, final int minExpectedLength) {
		if (CHECK_ARGS) {
			requireMinLength(requireNonNull(array).length, minExpectedLength);
		}
		return array;
	}

	public static void requireMinLength(final int foundLength, final int minExpectedLength) {
		if (CHECK_ARGS && foundLength < minExpectedLength) {
			throw new IllegalArgumentException("The specified float array has a length " +
					foundLength + " inferior to " + minExpectedLength);
		}
	}

	public static float[] requireMaxLength(final float[] array, final int maxExpectedLength) {
		if (CHECK_ARGS) {
			requireMaxLength(requireNonNull(array).length, maxExpectedLength);
		}
		return array;
	}

	public static void requireMaxLength(final int foundLength, final int maxExpectedLength) {
		if (CHECK_ARGS && foundLength > maxExpectedLength) {
			throw new IllegalArgumentException("The specified float array has a length " +
					foundLength + " superior to " + maxExpectedLength);
		}
	}

	public static void requireSameLength(final float[] a, final float[] b) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, requireNonNull(b).length);
		}
	}

	public static void requireSameLength(final int a, final int b) {
		if (CHECK_ARGS && a != b) {
			throw new IllegalArgumentException(
					"The specified float arrays do not have the same length " + isNotEqualTo(a, b));
		}
	}
}
