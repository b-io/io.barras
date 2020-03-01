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
package jupiter.common.util;

import static jupiter.common.util.Formats.DECIMAL_FORMAT;
import static jupiter.common.util.Formats.DOUBLE_DECIMAL_FORMAT;
import static jupiter.common.util.Formats.MAX_FRACTION_DIGITS;
import static jupiter.common.util.Formats.MAX_INTEGER_DIGITS;
import static jupiter.common.util.Formats.SCIENTIFIC_DECIMAL_FORMAT;
import static jupiter.common.util.Strings.EMPTY;
import static jupiter.common.util.Strings.NULL;

import java.math.BigDecimal;
import java.util.Comparator;

import jupiter.common.exception.IllegalClassException;

public class Numbers {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Comparator<Number> COMPARATOR = new Comparator<Number>() {
		@Override
		public int compare(final Number a, final Number b) {
			return Numbers.compare(a, b);
		}
	};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Numbers}.
	 */
	protected Numbers() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link BigDecimal} converted from the specified {@link Number}.
	 * <p>
	 * @param number a {@link Number} (may be {@code null})
	 * <p>
	 * @return a {@link BigDecimal} converted from the specified {@link Number}
	 */
	public static BigDecimal toBigDecimal(final Number number) {
		if (number == null) {
			return null;
		}
		if (number instanceof BigDecimal) {
			return (BigDecimal) number;
		}
		return new BigDecimal(toString(number));
	}

	/**
	 * Returns a {@link Number} of the specified {@link Class} converted from the specified
	 * {@link String}.
	 * <p>
	 * @param c    a {@link Class}
	 * @param text a {@link String} (may be {@code null})
	 * <p>
	 * @return a {@link Number} of the specified {@link Class} converted from the specified
	 *         {@link String}
	 */
	public static Number toNumber(final Class<?> c, final String text) {
		// Check the arguments
		if (text == null) {
			return null;
		}

		// Convert the text to a number of the class
		if (Bytes.isFrom(c)) {
			return Byte.valueOf(text);
		} else if (Shorts.isFrom(c)) {
			return Short.valueOf(text);
		} else if (Integers.isFrom(c)) {
			return Integer.valueOf(text);
		} else if (Longs.isFrom(c)) {
			return Long.valueOf(text);
		} else if (Floats.isFrom(c)) {
			return Float.valueOf(text);
		} else if (Doubles.isFrom(c)) {
			return Double.valueOf(text);
		} else if (BigDecimal.class.isAssignableFrom(c)) {
			return new BigDecimal(text);
		}
		throw new IllegalClassException(c);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of {@link Number}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Number},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Number;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a primitive number or a
	 * {@link Number}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a primitive number or a
	 *         {@link Number}, {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return Number.class.isAssignableFrom(c) || isPrimitiveFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a primitive number.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a primitive number,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveFrom(final Class<?> c) {
		return Bytes.isPrimitiveFrom(c) ||
				Shorts.isPrimitiveFrom(c) ||
				Integers.isPrimitiveFrom(c) ||
				Longs.isPrimitiveFrom(c) ||
				Floats.isPrimitiveFrom(c) ||
				Doubles.isPrimitiveFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified value {@link String} is parsable to a {@link Number}.
	 * <p>
	 * @param value the value {@link String} to test
	 * <p>
	 * @return {@code true} if the specified value {@link String} is parsable to a {@link Number},
	 *         {@code false} otherwise
	 */
	public static boolean isParsableFrom(final String value) {
		try {
			Double.parseDouble(value);
		} catch (final NumberFormatException ignored) {
			return false;
		}
		return true;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified {@link Number} for order. Returns a negative integer, zero or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@link Number} to compare for order
	 * @param b the other {@link Number} to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static int compare(final Number a, final Number b) {
		if (a == b) {
			return 0;
		}
		if (a instanceof BigDecimal || b instanceof BigDecimal) {
			return toBigDecimal(a).compareTo(toBigDecimal(b));
		}
		return Doubles.compare(a.doubleValue(), b.doubleValue());
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is less than {@code b}.
	 * <p>
	 * @param a the {@link Number} to compare
	 * @param b the other {@link Number} to compare against
	 * <p>
	 * @return {@code true} if {@code a} is less than {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static boolean isLessThan(final Number a, final Number b) {
		return compare(a, b) < 0;
	}

	/**
	 * Tests whether {@code a} is less or equal to {@code b}.
	 * <p>
	 * @param a the {@link Number} to compare
	 * @param b the other {@link Number} to compare against
	 * <p>
	 * @return {@code true} if {@code a} is less or equal to {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static boolean isLessOrEqualTo(final Number a, final Number b) {
		return compare(a, b) <= 0;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is greater than {@code b}.
	 * <p>
	 * @param a the {@link Number} to compare
	 * @param b the other {@link Number} to compare against
	 * <p>
	 * @return {@code true} if {@code a} is greater than {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static boolean isGreaterThan(final Number a, final Number b) {
		return compare(a, b) > 0;
	}

	/**
	 * Tests whether {@code a} is greater or equal to {@code b}.
	 * <p>
	 * @param a the {@link Number} to compare
	 * @param b the other {@link Number} to compare against
	 * <p>
	 * @return {@code true} if {@code a} is greater or equal to {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static boolean isGreaterOrEqualTo(final Number a, final Number b) {
		return compare(a, b) >= 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the smaller of {@code a} and {@code b}. If they have the same value, the result is
	 * {@code a}.
	 * <p>
	 * @param <T> the {@link Number} type
	 * @param a   the {@link Number} to compare
	 * @param b   the other {@link Number} to compare against
	 * <p>
	 * @return the smaller of {@code a} and {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Number> T getMin(final T a, final T b) {
		return compare(a, b) <= 0 ? a : b;
	}

	/**
	 * Returns the larger of {@code a} and {@code b}. If they have the same value, the result is
	 * {@code a}.
	 * <p>
	 * @param <T> the {@link Number} type
	 * @param a   the {@link Number} to compare
	 * @param b   the other {@link Number} to compare against
	 * <p>
	 * @return the larger of {@code a} and {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Number> T getMax(final T a, final T b) {
		return compare(a, b) >= 0 ? a : b;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the {@link Number} to compare for equality (may be {@code null})
	 * @param b the other {@link Number} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static boolean equals(final Number a, final Number b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		if (a instanceof BigDecimal || b instanceof BigDecimal) {
			return toBigDecimal(a).equals(toBigDecimal(b));
		}
		return Doubles.equals(a.doubleValue(), b.doubleValue());
	}

	/**
	 * Tests whether {@code a} is equal to {@code b} within {@code tolerance}.
	 * <p>
	 * @param a         the {@link Number} to compare for equality (may be {@code null})
	 * @param b         the other {@link Number} to compare against for equality (may be
	 *                  {@code null})
	 * @param tolerance the tolerance level
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b} within {@code tolerance},
	 *         {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static boolean equals(final Number a, final Number b, final double tolerance) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		if (a instanceof BigDecimal || b instanceof BigDecimal) {
			return toBigDecimal(a).subtract(toBigDecimal(b)).abs().doubleValue() <= tolerance;
		}
		return Doubles.equals(a.doubleValue(), b.doubleValue(), tolerance);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Number} if it is not
	 * {@code null}, {@code "null"} otherwise.
	 * <p>
	 * @param number a {@link Number} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Number} if it is not
	 *         {@code null}, {@code "null"} otherwise
	 */
	public static String toString(final Number number) {
		// Check the arguments
		if (number == null) {
			return NULL;
		}

		// Convert the number to a representative string
		final String formattedNumber;
		final String numberString = DOUBLE_DECIMAL_FORMAT.format(number);
		int integerDigitCount = numberString.length(), fractionDigitCount = numberString.length();
		if (numberString.contains("-")) {
			--integerDigitCount;
			--fractionDigitCount;
		}
		final int decimalPointIndex = numberString.indexOf('.');
		if (decimalPointIndex >= 0) {
			integerDigitCount -= numberString.length() - decimalPointIndex;
			fractionDigitCount -= decimalPointIndex + 1;
		} else {
			fractionDigitCount = 0;
		}
		if (integerDigitCount > MAX_INTEGER_DIGITS || fractionDigitCount > MAX_FRACTION_DIGITS) {
			formattedNumber = SCIENTIFIC_DECIMAL_FORMAT.format(number).replace("E0", EMPTY);
		} else {
			formattedNumber = DECIMAL_FORMAT.format(number);
		}
		return formattedNumber;
	}
}
