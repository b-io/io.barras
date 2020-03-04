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

public class IntegerArguments
		extends Arguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link IntegerArguments}.
	 */
	protected IntegerArguments() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int require(final int found, final int expected) {
		if (CHECK_ARGS && found != expected) {
			throw new IllegalArgumentException(
					"The specified int number is wrong " + expectedButFound(found, expected));
		}
		return found;
	}

	public static void requireEquals(final int a, final int b) {
		if (CHECK_ARGS && a != b) {
			throw new IllegalArgumentException(
					"The specified int numbers are not equal " + isNotEqualTo(a, b));
		}
	}

	public static int requireGreaterThan(final int found, final int expectedLowerBound) {
		if (CHECK_ARGS && found <= expectedLowerBound) {
			throw new IllegalArgumentException("The specified int number " + found +
					" is lower or equal to " + expectedLowerBound);
		}
		return found;
	}

	public static int requireGreaterOrEqualTo(final int found, final int expectedLowerBound) {
		if (CHECK_ARGS && found < expectedLowerBound) {
			throw new IllegalArgumentException(
					"The specified int number " + found + " is lower than " + expectedLowerBound);
		}
		return found;
	}

	public static int requireLessThan(final int found, final int expectedUpperBound) {
		if (CHECK_ARGS && found >= expectedUpperBound) {
			throw new IllegalArgumentException("The specified int number " + found +
					" is greater or equal to " + expectedUpperBound);
		}
		return found;
	}

	public static int requireLessOrEqualTo(final int found, final int expectedUpperBound) {
		if (CHECK_ARGS && found > expectedUpperBound) {
			throw new IllegalArgumentException(
					"The specified int number " + found + " is greater than " + expectedUpperBound);
		}
		return found;
	}

	public static int requireNegative(final int found) {
		if (CHECK_ARGS && found >= 0) {
			throw new IllegalArgumentException(
					"The specified int number " + found + " is zero or positive");
		}
		return found;
	}

	public static int requireNonNegative(final int found) {
		if (CHECK_ARGS && found < 0) {
			throw new IllegalArgumentException(
					"The specified int number " + found + " is negative");
		}
		return found;
	}

	public static int requireNonZero(final int found) {
		if (CHECK_ARGS && found == 0) {
			throw new IllegalArgumentException("The specified int number " + found + " is zero");
		}
		return found;
	}

	public static int requirePositive(final int found) {
		if (CHECK_ARGS && found <= 0) {
			throw new IllegalArgumentException(
					"The specified int number " + found + " is zero or negative");
		}
		return found;
	}

	public static int requireNonPositive(final int found) {
		if (CHECK_ARGS && found > 0) {
			throw new IllegalArgumentException(
					"The specified int number " + found + " is positive");
		}
		return found;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int[] requireNonEmpty(final int... array) {
		if (CHECK_ARGS) {
			requireNonEmpty(requireNonNull(array).length);
		}
		return array;
	}

	public static void requireNonEmpty(final int length) {
		if (CHECK_ARGS && length == 0) {
			throw new IllegalArgumentException("The specified int array is empty");
		}
	}

	public static int[] requireLength(final int[] array, final int expectedLength) {
		if (CHECK_ARGS) {
			requireLength(requireNonNull(array).length, expectedLength);
		}
		return array;
	}

	public static void requireLength(final int foundLength, final int expectedLength) {
		if (CHECK_ARGS && foundLength != expectedLength) {
			throw new IllegalArgumentException("The specified int array has wrong length " +
					expectedButFound(foundLength, expectedLength));
		}
	}

	public static int[] requireMinLength(final int[] array, final int minExpectedLength) {
		if (CHECK_ARGS) {
			requireMinLength(requireNonNull(array).length, minExpectedLength);
		}
		return array;
	}

	public static void requireMinLength(final int foundLength, final int minExpectedLength) {
		if (CHECK_ARGS && foundLength < minExpectedLength) {
			throw new IllegalArgumentException("The specified int array has a length " +
					foundLength + " inferior to " + minExpectedLength);
		}
	}

	public static int[] requireMaxLength(final int[] array, final int maxExpectedLength) {
		if (CHECK_ARGS) {
			requireMaxLength(requireNonNull(array).length, maxExpectedLength);
		}
		return array;
	}

	public static void requireMaxLength(final int foundLength, final int maxExpectedLength) {
		if (CHECK_ARGS && foundLength > maxExpectedLength) {
			throw new IllegalArgumentException("The specified int array has a length " +
					foundLength + " superior to " + maxExpectedLength);
		}
	}

	public static void requireSameLength(final int[] a, final int[] b) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, requireNonNull(b).length);
		}
	}

	public static void requireSameLength(final int a, final int b) {
		if (CHECK_ARGS && a != b) {
			throw new IllegalArgumentException(
					"The specified int arrays do not have the same length " + isNotEqualTo(a, b));
		}
	}
}
