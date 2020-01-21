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
package jupiter.common.util;

import java.math.BigDecimal;
import java.util.Comparator;

import jupiter.common.exception.IllegalOperationException;

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
	 * @param number a {@link Number}
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
		return new BigDecimal(Strings.toString(number));
	}

	/**
	 * Returns a {@link Number} of the specified {@link Class} converted from the specified
	 * {@link String}.
	 * <p>
	 * @param c    a {@link Class}
	 * @param text a {@link String}
	 * <p>
	 * @return a {@link Number} of the specified {@link Class} converted from the specified
	 *         {@link String}
	 */
	public static Number toNumber(final Class<?> c, final String text) {
		if (text == null) {
			return null;
		}
		if (Bytes.is(c)) {
			return Byte.valueOf(text);
		} else if (Shorts.is(c)) {
			return Short.valueOf(text);
		} else if (Integers.is(c)) {
			return Integer.valueOf(text);
		} else if (Longs.is(c)) {
			return Long.valueOf(text);
		} else if (Floats.is(c)) {
			return Float.valueOf(text);
		} else if (Doubles.is(c)) {
			return Double.valueOf(text);
		} else if (BigDecimal.class.isAssignableFrom(c)) {
			return new BigDecimal(text);
		}
		throw new IllegalOperationException(Strings.join(
				"Cannot convert ", Strings.quote(text), " to a ", c.getSimpleName()));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Class} is assignable to a primitive number or a
	 * {@link Number}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a primitive number or a
	 *         {@link Number}, {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return Number.class.isAssignableFrom(c) || isPrimitive(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a primitive number.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a primitive number,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitive(final Class<?> c) {
		return Bytes.isPrimitive(c) ||
				Shorts.isPrimitive(c) ||
				Integers.isPrimitive(c) ||
				Longs.isPrimitive(c) ||
				Floats.isPrimitive(c) ||
				Doubles.isPrimitive(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link String} is a parsable {@link Number}.
	 * <p>
	 * @param text the {@link String} to test
	 * <p>
	 * @return {@code true} if the specified {@link String} is a parsable
	 *         {@link Number}, {@code false} otherwise
	 */
	public static boolean is(final String text) {
		try {
			Double.parseDouble(text);
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
	 * @param a the {@link Number} to compare for equality
	 * @param b the other {@link Number} to compare against for equality
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
	 * @param a         the {@link Number} to compare for equality
	 * @param b         the other {@link Number} to compare against for equality
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
}
