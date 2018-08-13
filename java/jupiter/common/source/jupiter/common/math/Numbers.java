/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.common.math;

import java.math.BigDecimal;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.util.Doubles;
import jupiter.common.util.Strings;

public class Numbers {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Numbers() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link BigDecimal} converted from the specified {@link Number}.
	 * <p>
	 * @param number a {@link Number}
	 * <p>
	 * @return a {@link BigDecimal} converted from the specified {@link Number}
	 */
	public static BigDecimal toBigDecimal(final Number number) {
		if (number instanceof BigDecimal) {
			return (BigDecimal) number;
		}
		return new BigDecimal(Strings.toString(number));
	}

	/**
	 * Returns a {@link Number} of the specified {@code Class} converted from the specified
	 * {@link String}.
	 * <p>
	 * @param c      a {@link Class}
	 * @param string a {@link String}
	 * <p>
	 * @return a {@link Number} of the specified {@code Class} converted from the specified
	 *         {@link String}
	 */
	public static Number toNumber(final Class<?> c, final String string) {
		if (string == null) {
			return null;
		}
		if (c.isAssignableFrom(Byte.class)) {
			return Byte.valueOf(string);
		} else if (c.isAssignableFrom(Short.class)) {
			return Short.valueOf(string);
		} else if (c.isAssignableFrom(Integer.class)) {
			return Integer.valueOf(string);
		} else if (c.isAssignableFrom(Long.class)) {
			return Long.valueOf(string);
		} else if (c.isAssignableFrom(Float.class)) {
			return Float.valueOf(string);
		} else if (c.isAssignableFrom(Double.class)) {
			return Double.valueOf(string);
		} else if (c.isAssignableFrom(BigDecimal.class)) {
			return new BigDecimal(string);
		}
		throw new IllegalOperationException(
				"Cannot convert " + Strings.quote(c) + " from " + Strings.quote(string));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified numbers for order. Returns a negative integer, zero or a positive
	 * integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a a {@link Number}
	 * @param b another {@link Number} to compare with for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static int compare(final Number a, final Number b) {
		if (a instanceof BigDecimal || b instanceof BigDecimal) {
			return toBigDecimal(a).compareTo(toBigDecimal(b));
		}
		return Doubles.compare(a.doubleValue(), b.doubleValue());
	}

	/**
	 * Returns {@code true} if {@code a} is less than {@code b}, {@code false} otherwise.
	 * <p>
	 * @param a a {@link Number}
	 * @param b another {@link Number} to compare with
	 * <p>
	 * @return {@code true} if {@code a} is less than {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static boolean isLessThan(final Number a, final Number b) {
		return compare(a, b) < 0;
	}

	/**
	 * Returns {@code true} if {@code a} is less or equal to {@code b}, {@code false} otherwise.
	 * <p>
	 * @param a a {@link Number}
	 * @param b another {@link Number} to compare with
	 * <p>
	 * @return {@code true} if {@code a} is less or equal to {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static boolean isLessOrEqualTo(final Number a, final Number b) {
		return compare(a, b) <= 0;
	}

	/**
	 * Returns {@code true} if {@code a} is greater than {@code b}, {@code false} otherwise.
	 * <p>
	 * @param a a {@link Number}
	 * @param b another {@link Number} to compare with
	 * <p>
	 * @return {@code true} if {@code a} is greater than {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static boolean isGreaterThan(final Number a, final Number b) {
		return compare(a, b) > 0;
	}

	/**
	 * Returns {@code true} if {@code a} is greater or equal to {@code b}, {@code false} otherwise.
	 * <p>
	 * @param a a {@link Number}
	 * @param b another {@link Number} to compare with
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
	 * @param <T> the type of the numbers to compare
	 * @param a   a {@link Number}
	 * @param b   another {@link Number} to compare with
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
	 * @param <T> the type of the numbers to compare
	 * @param a   a {@link Number}
	 * @param b   another {@link Number} to compare with
	 * <p>
	 * @return the larger of {@code a} and {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Number> T getMax(final T a, final T b) {
		return compare(a, b) >= 0 ? a : b;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if {@code type} is assignable to a number, {@code false} otherwise.
	 * <p>
	 * @param type a {@link Class}
	 * <p>
	 * @return {@code true} if {@code type} is assignable to a number, {@code false} otherwise
	 */
	public static boolean isNumber(final Class<?> type) {
		return Number.class.isAssignableFrom(type);
	}

	/**
	 * Returns {@code true} if {@code string} is a parsable number, {@code false} otherwise.
	 * <p>
	 * @param string a {@link String}
	 * <p>
	 * @return {@code true} if {@code string} is a parsable number, {@code false} otherwise
	 */
	public static boolean isNumber(final String string) {
		try {
			Double.parseDouble(string);
		} catch (final NumberFormatException ignored) {
			return false;
		}
		return true;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise.
	 * <p>
	 * @param a a {@link Number}
	 * @param b another {@link Number} to compare with for equality
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static boolean equals(final Number a, final Number b) {
		if (a == b) {
			return true;
		}
		if (a instanceof BigDecimal || b instanceof BigDecimal) {
			return toBigDecimal(a).equals(toBigDecimal(b));
		}
		return Doubles.equals(a.doubleValue(), b.doubleValue());
	}

	/**
	 * Returns {@code true} if {@code a} is equal to {@code b} within {@code tolerance},
	 * {@code false} otherwise.
	 * <p>
	 * @param a         a {@link Number}
	 * @param b         another {@link Number} to compare with for equality
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
		if (a instanceof BigDecimal || b instanceof BigDecimal) {
			return toBigDecimal(a).subtract(toBigDecimal(b)).abs().doubleValue() <= tolerance;
		}
		return Doubles.equals(a.doubleValue(), b.doubleValue(), tolerance);
	}
}
