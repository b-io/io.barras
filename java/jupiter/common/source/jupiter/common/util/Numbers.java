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
package jupiter.common.util;

import static jupiter.common.Formats.DECIMAL_FORMAT;
import static jupiter.common.Formats.MAX_SCIENTIFIC_THRESHOLD;
import static jupiter.common.Formats.MIN_SCIENTIFIC_THRESHOLD;
import static jupiter.common.Formats.SCIENTIFIC_DECIMAL_FORMAT;
import static jupiter.common.util.Strings.EMPTY;
import static jupiter.common.util.Strings.NULL;

import java.math.BigDecimal;
import java.util.Comparator;

import jupiter.common.exception.IllegalClassException;
import jupiter.common.math.Comparables;
import jupiter.common.math.Maths;

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
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified {@link Number} for order. Returns a negative integer, {@code 0} or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@link Number} to compare for order (may be {@code null})
	 * @param b the other {@link Number} to compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final Number a, final Number b) {
		// Check the arguments
		if (a == b) {
			return 0;
		}
		if (a == null) {
			return -1;
		}
		if (b == null) {
			return 1;
		}

		// Compare the numbers for order
		if (a instanceof BigDecimal || b instanceof BigDecimal) {
			return Comparables.compare(toBigDecimal(a), toBigDecimal(b));
		}
		return Doubles.compare(a.doubleValue(), b.doubleValue());
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is less than {@code b} (with {@code null} considered as the minimum
	 * value).
	 * <p>
	 * @param a the {@link Number} to compare (may be {@code null})
	 * @param b the other {@link Number} to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is less than {@code b}, {@code false} otherwise
	 */
	public static boolean isLessThan(final Number a, final Number b) {
		return compare(a, b) < 0;
	}

	/**
	 * Tests whether {@code a} is less or equal to {@code b} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param a the {@link Number} to compare (may be {@code null})
	 * @param b the other {@link Number} to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is less or equal to {@code b}, {@code false} otherwise
	 */
	public static boolean isLessOrEqualTo(final Number a, final Number b) {
		return compare(a, b) <= 0;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is greater than {@code b} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param a the {@link Number} to compare (may be {@code null})
	 * @param b the other {@link Number} to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is greater than {@code b}, {@code false} otherwise
	 */
	public static boolean isGreaterThan(final Number a, final Number b) {
		return compare(a, b) > 0;
	}

	/**
	 * Tests whether {@code a} is greater or equal to {@code b} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param a the {@link Number} to compare (may be {@code null})
	 * @param b the other {@link Number} to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is greater or equal to {@code b}, {@code false} otherwise
	 */
	public static boolean isGreaterOrEqualTo(final Number a, final Number b) {
		return compare(a, b) >= 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the smaller of {@code a} and {@code b}, or {@code a} if they are equal (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param <T> the {@link Number} type
	 * @param a   the {@link Number} to compare (may be {@code null})
	 * @param b   the other {@link Number} to compare against (may be {@code null})
	 * <p>
	 * @return the smaller of {@code a} and {@code b}, or {@code a} if they are equal
	 */
	public static <T extends Number> T getMin(final T a, final T b) {
		return compare(a, b) <= 0 ? a : b;
	}

	/**
	 * Returns the larger of {@code a} and {@code b}, or {@code a} if they are equal (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param <T> the {@link Number} type
	 * @param a   the {@link Number} to compare (may be {@code null})
	 * @param b   the other {@link Number} to compare against (may be {@code null})
	 * <p>
	 * @return the larger of {@code a} and {@code b}, or {@code a} if they are equal
	 */
	public static <T extends Number> T getMax(final T a, final T b) {
		return compare(a, b) >= 0 ? a : b;
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
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the {@link Number} to compare for equality (may be {@code null})
	 * @param b the other {@link Number} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
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
	 * Returns a representative {@link String} of the specified {@link Number}, or {@code "null"} if
	 * it is {@code null}.
	 * <p>
	 * @param number a {@link Number} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Number}, or {@code "null"} if
	 *         it is {@code null}
	 */
	public static String toString(final Number number) {
		// Check the arguments
		if (number == null) {
			return NULL;
		}

		// Convert the number to a representative string
		final double value = Maths.abs(number.doubleValue());
		return value <= MIN_SCIENTIFIC_THRESHOLD || value >= MAX_SCIENTIFIC_THRESHOLD ?
				SCIENTIFIC_DECIMAL_FORMAT.format(number).replace("E0", EMPTY) :
				DECIMAL_FORMAT.format(number);
	}
}
