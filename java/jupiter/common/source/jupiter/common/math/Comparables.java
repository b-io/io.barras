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
package jupiter.common.math;

import java.util.Comparator;

import jupiter.common.util.Bytes;
import jupiter.common.util.Characters;
import jupiter.common.util.Doubles;
import jupiter.common.util.Floats;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Shorts;
import jupiter.common.util.Strings;

public class Comparables {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Comparables}.
	 */
	protected Comparables() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings({"cast", "unchecked"})
	public static <T> Comparator<? super T> getComparator(final Class<T> c) {
		if (Comparables.isFrom(c)) {
			return Comparables.<T>createCastComparator();
		} else if (Characters.isPrimitiveArrayFrom(c)) {
			return (Comparator<? super T>) Characters.ARRAY_COMPARATOR;
		} else if (Bytes.isPrimitiveArrayFrom(c)) {
			return (Comparator<? super T>) Bytes.ARRAY_COMPARATOR;
		} else if (Shorts.isPrimitiveArrayFrom(c)) {
			return (Comparator<? super T>) Shorts.ARRAY_COMPARATOR;
		} else if (Integers.isPrimitiveArrayFrom(c)) {
			return (Comparator<? super T>) Integers.ARRAY_COMPARATOR;
		} else if (Longs.isPrimitiveArrayFrom(c)) {
			return (Comparator<? super T>) Longs.ARRAY_COMPARATOR;
		} else if (Floats.isPrimitiveArrayFrom(c)) {
			return (Comparator<? super T>) Floats.ARRAY_COMPARATOR;
		} else if (Doubles.isPrimitiveArrayFrom(c)) {
			return (Comparator<? super T>) Doubles.ARRAY_COMPARATOR;
		}
		throw new IllegalArgumentException(
				Strings.join("The specified ", c, " cannot be compared"));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T extends Comparable<? super T>> Comparator<T> createComparator() {
		return new Comparator<T>() {
			@Override
			public int compare(final T a, final T b) {
				return Comparables.compare(a, b);
			}
		};
	}

	public static <T> Comparator<T> createCastComparator() {
		return new Comparator<T>() {
			@Override
			public int compare(final T a, final T b) {
				return Comparables.compareCast(a, b);
			}
		};
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of {@link Comparable}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Comparable},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Comparable;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link Comparable}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link Comparable},
	 *         {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return Comparable.class.isAssignableFrom(c);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code T} object is between the specified {@code T} lower and
	 * upper bounds (with {@code null} considered as the minimum value).
	 * <p>
	 * @param <T>    the type of the object to test
	 * @param object the {@code T} object to test (may be {@code null})
	 * @param from   the {@code T} lower bound to test against (inclusive) (may be {@code null})
	 * @param to     the {@code T} upper bound to test against (exclusive) (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code T} object is between the specified {@code T}
	 *         lower and upper bounds, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException if {@code object} cannot be compared to {@code from} or {@code to}
	 */
	public static <T> boolean isBetween(final T object, final T from, final T to) {
		return isBetween(object, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@code T} object is between the specified {@code T} lower and
	 * upper bounds (with {@code null} considered as the minimum value).
	 * <p>
	 * @param <T>              the type of the object to test
	 * @param object           the {@code T} object to test (may be {@code null})
	 * @param from             the {@code T} lower bound to test against (inclusive) (may be
	 *                         {@code null})
	 * @param to               the {@code T} upper bound to test against (may be {@code null})
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code T} object is between the specified {@code T}
	 *         lower and upper bounds, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException if {@code object} cannot be compared to {@code from} or {@code to}
	 */
	public static <T> boolean isBetween(final T object, final T from, final T to,
			final boolean isUpperInclusive) {
		return isBetween(object, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code T} object is between the specified {@code T} lower and
	 * upper bounds (with {@code null} considered as the minimum value).
	 * <p>
	 * @param <T>              the type of the object to test
	 * @param object           the {@code T} object to test (may be {@code null})
	 * @param from             the {@code T} lower bound to test against (may be {@code null})
	 * @param to               the {@code T} upper bound to test against (may be {@code null})
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code T} object is between the specified {@code T}
	 *         lower and upper bounds, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException if {@code object} cannot be compared to {@code from} or {@code to}
	 */
	public static <T> boolean isBetween(final T object, final T from, final T to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return isBetween(object, from, to, createCastComparator(), isLowerInclusive,
				isUpperInclusive);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code T} object is between the specified {@code T} lower and
	 * upper bounds using the specified {@link Comparator} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param <T>        the type of the object to test
	 * @param object     the {@code T} object to test (may be {@code null})
	 * @param from       the {@code T} lower bound to test against (inclusive) (may be {@code null})
	 * @param to         the {@code T} upper bound to test against (exclusive) (may be {@code null})
	 * @param comparator the {@link Comparator} of {@code T} supertype to determine the order
	 * <p>
	 * @return {@code true} if the specified {@code T} object is between the specified {@code T}
	 *         lower and upper bounds using the specified {@link Comparator}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException if {@code object} cannot be compared to {@code from} or {@code to}
	 *                            using {@code comparator}
	 */
	public static <T> boolean isBetween(final T object, final T from, final T to,
			final Comparator<? super T> comparator) {
		return isBetween(object, from, to, comparator, true, false);
	}

	/**
	 * Tests whether the specified {@code T} object is between the specified {@code T} lower and
	 * upper bounds using the specified {@link Comparator} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param <T>              the type of the object to test
	 * @param object           the {@code T} object to test (may be {@code null})
	 * @param from             the {@code T} lower bound to test against (inclusive) (may be
	 *                         {@code null})
	 * @param to               the {@code T} upper bound to test against (may be {@code null})
	 * @param comparator       the {@link Comparator} of {@code T} supertype to determine the order
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code T} object is between the specified {@code T}
	 *         lower and upper bounds using the specified {@link Comparator}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException if {@code object} cannot be compared to {@code from} or {@code to}
	 *                            using {@code comparator}
	 */
	public static <T> boolean isBetween(final T object, final T from, final T to,
			final Comparator<? super T> comparator, final boolean isUpperInclusive) {
		return isBetween(object, from, to, comparator, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code T} object is between the specified {@code T} lower and
	 * upper bounds using the specified {@link Comparator} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param <T>              the type of the object to test
	 * @param object           the {@code T} object to test (may be {@code null})
	 * @param from             the {@code T} lower bound to test against (may be {@code null})
	 * @param to               the {@code T} upper bound to test against (may be {@code null})
	 * @param comparator       the {@link Comparator} of {@code T} supertype to determine the order
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code T} object is between the specified {@code T}
	 *         lower and upper bounds using the specified {@link Comparator}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException if {@code object} cannot be compared to {@code from} or {@code to}
	 *                            using {@code comparator}
	 */
	public static <T> boolean isBetween(final T object, final T from, final T to,
			final Comparator<? super T> comparator, final boolean isLowerInclusive,
			final boolean isUpperInclusive) {
		return (isLowerInclusive ?
				compare(object, from, comparator) >= 0 :
				compare(object, from, comparator) > 0) &&
				(isUpperInclusive ?
						compare(object, to, comparator) <= 0 :
						compare(object, to, comparator) < 0);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified {@link Comparable} with the specified {@link Object} for order.
	 * Returns a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 * to or greater than {@code b} (with {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@link Comparable} to compare (may be {@code null})
	 * @param b the other {@link Object} to compare against (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 * <p>
	 * @throws ClassCastException if {@code a} cannot be compared to {@code b}
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static int compare(final Comparable a, final Object b) {
		return a == b ? 0 : a == null ? -1 : b == null ? 1 : a.compareTo(b);
	}

	/**
	 * Compares the specified {@link Object} for order. Returns a negative integer, {@code 0} or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@link Object} to compare (may be {@code null})
	 * @param b the other {@link Object} to compare against (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 * <p>
	 * @throws ClassCastException if {@code a} cannot be compared to {@code b}
	 */
	public static int compareCast(final Object a, final Object b) {
		return compare((Comparable) a, b);
	}

	/**
	 * Returns {@code 0} if {@code a} and {@code b} are identical, {@code comparator.compare(a, b)}
	 * otherwise (with {@code null} considered as the minimum value).
	 * <p>
	 * @param <T>        the type of the objects to compare for order
	 * @param a          the {@code T} object to compare (may be {@code null})
	 * @param b          the other {@code T} object to compare against (may be {@code null})
	 * @param comparator the {@link Comparator} of {@code T} supertype to determine the order
	 * <p>
	 * @return {@code 0} if {@code a} and {@code b} are identical, {@code comparator.compare(a, b)}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException if {@code a} cannot be compared to {@code b} using
	 *                            {@code comparator}
	 */
	public static <T> int compare(final T a, final T b, final Comparator<? super T> comparator) {
		return a == b ? 0 : a == null ? -1 : b == null ? 1 : comparator.compare(a, b);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is less than {@code b} (with {@code null} considered as the minimum
	 * value).
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare
	 * @param a   the {@link Comparable} of {@code T} type to compare (may be {@code null})
	 * @param b   the other {@link Comparable} of {@code T} type to compare against (may be
	 *            {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is less than {@code b}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException if {@code a} cannot be compared to {@code b}
	 */
	public static <T extends Comparable<? super T>> boolean isLessThan(final T a, final T b) {
		return b != null && (a == null || a.compareTo(b) < 0);
	}

	/**
	 * Tests whether {@code a} is less or equal to {@code b} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare
	 * @param a   the {@link Comparable} of {@code T} type to compare (may be {@code null})
	 * @param b   the other {@link Comparable} of {@code T} type to compare against (may be
	 *            {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is less or equal to {@code b}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException if {@code a} cannot be compared to {@code b}
	 */
	public static <T extends Comparable<? super T>> boolean isLessOrEqualTo(final T a, final T b) {
		return a == null || b != null && a.compareTo(b) <= 0;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is greater than {@code b} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare
	 * @param a   the {@link Comparable} of {@code T} type to compare (may be {@code null})
	 * @param b   the other {@link Comparable} of {@code T} type to compare against (may be
	 *            {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is greater than {@code b}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException if {@code a} cannot be compared to {@code b}
	 */
	public static <T extends Comparable<? super T>> boolean isGreaterThan(final T a, final T b) {
		return a != null && (b == null || a.compareTo(b) > 0);
	}

	/**
	 * Tests whether {@code a} is greater or equal to {@code b} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare
	 * @param a   the {@link Comparable} of {@code T} type to compare (may be {@code null})
	 * @param b   the other {@link Comparable} of {@code T} type to compare against (may be
	 *            {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is greater or equal to {@code b}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException if {@code a} cannot be compared to {@code b}
	 */
	public static <T extends Comparable<? super T>> boolean isGreaterOrEqualTo(final T a,
			final T b) {
		return b == null || a != null && a.compareTo(b) >= 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the smaller of {@code a} and {@code b}, or {@code a} if they are equal (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare
	 * @param a   the {@link Comparable} of {@code T} type to compare (may be {@code null})
	 * @param b   the other {@link Comparable} of {@code T} type to compare against (may be
	 *            {@code null})
	 * <p>
	 * @return the smaller of {@code a} and {@code b}, or {@code a} if they are equal
	 * <p>
	 * @throws ClassCastException if {@code a} cannot be compared to {@code b}
	 */
	public static <T extends Comparable<? super T>> T getMin(final T a, final T b) {
		return Comparables.<T>isLessOrEqualTo(a, b) ? a : b;
	}

	/**
	 * Returns the larger of {@code a} and {@code b}, or {@code a} if they are equal (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare
	 * @param a   the {@link Comparable} of {@code T} type to compare (may be {@code null})
	 * @param b   the other {@link Comparable} of {@code T} type to compare against (may be
	 *            {@code null})
	 * <p>
	 * @return the larger of {@code a} and {@code b}, or {@code a} if they are equal
	 * <p>
	 * @throws ClassCastException if {@code a} cannot be compared to {@code b}
	 */
	public static <T extends Comparable<? super T>> T getMax(final T a, final T b) {
		return Comparables.<T>isGreaterOrEqualTo(a, b) ? a : b;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code a} and {@code b} are equal to each other.
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare for equality
	 * @param a   the {@link Comparable} of {@code T} type to compare for equality (may be
	 *            {@code null})
	 * @param b   the other {@link Comparable} of {@code T} type to compare against for equality
	 *            (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} and {@code b} are equal to each other, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException if {@code a} cannot be compared to {@code b}
	 */
	public static <T extends Comparable<? super T>> boolean equals(final T a, final T b) {
		return a == b || a != null && b != null && a.compareTo(b) == 0;
	}
}
