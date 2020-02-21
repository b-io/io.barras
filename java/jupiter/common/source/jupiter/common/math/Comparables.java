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

	@SuppressWarnings("unchecked")
	public static <T> Comparator<? super T> getComparator(final Class<T> c) {
		if (Comparables.is(c)) {
			return Comparables.<T>createCastComparator();
		} else if (Characters.isPrimitiveArray(c)) {
			return (Comparator<? super T>) Characters.ARRAY_COMPARATOR;
		} else if (Bytes.isPrimitiveArray(c)) {
			return (Comparator<? super T>) Bytes.ARRAY_COMPARATOR;
		} else if (Shorts.isPrimitiveArray(c)) {
			return (Comparator<? super T>) Shorts.ARRAY_COMPARATOR;
		} else if (Integers.isPrimitiveArray(c)) {
			return (Comparator<? super T>) Integers.ARRAY_COMPARATOR;
		} else if (Longs.isPrimitiveArray(c)) {
			return (Comparator<? super T>) Longs.ARRAY_COMPARATOR;
		} else if (Floats.isPrimitiveArray(c)) {
			return (Comparator<? super T>) Floats.ARRAY_COMPARATOR;
		} else if (Doubles.isPrimitiveArray(c)) {
			return (Comparator<? super T>) Doubles.ARRAY_COMPARATOR;
		}
		throw new IllegalArgumentException(
				Strings.join("The specified type ", c, " cannot be compared"));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T extends Comparable<T>> Comparator<T> createComparator() {
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
			@SuppressWarnings("unchecked")
			public int compare(final T a, final T b) {
				return Comparables.compareCast(a, b);
			}
		};
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link Comparable}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link Comparable},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return Comparable.class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Comparable} is between the specified lower and upper bound
	 * {@link Comparable}.
	 * <p>
	 * @param <T>    the component type of the object to test
	 * @param object the {@link Comparable} of type {@code T} to test
	 * @param from   the lower bound {@link Comparable} of type {@code T} to test against
	 *               (inclusive)
	 * @param to     the upper bound {@link Comparable} of type {@code T} to test against
	 *               (exclusive)
	 * <p>
	 * @return {@code true} if the specified {@link Comparable} is between the specified lower and
	 *         upper bound {@link Comparable}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code object}, {@code from} or {@code to} is {@code null}
	 */
	public static <T> boolean isBetween(final T object, final T from, final T to) {
		return isBetween(object, from, to, createCastComparator());
	}

	/**
	 * Tests whether the specified {@link Comparable} is between the specified lower and upper bound
	 * {@link Comparable}.
	 * <p>
	 * @param <T>        the component type of the object to test
	 * @param object     the {@link Comparable} of type {@code T} to test
	 * @param from       the lower bound {@link Comparable} of type {@code T} to test against
	 *                   (inclusive)
	 * @param to         the upper bound {@link Comparable} of type {@code T} to test against
	 *                   (exclusive)
	 * @param comparator the {@link Comparator} of supertype {@code T} to determine the order
	 * <p>
	 * @return {@code true} if the specified {@link Comparable} is between the specified lower and
	 *         upper bound {@link Comparable}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code object}, {@code from} or {@code to} is {@code null}
	 */
	public static <T> boolean isBetween(final T object, final T from, final T to,
			final Comparator<? super T> comparator) {
		return compare(object, from, comparator) >= 0 && compare(object, to, comparator) < 0;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified {@link Comparable} for order. Returns a negative integer, zero or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare
	 * @param a   the {@link Comparable} of type {@code T} to compare
	 * @param b   the other {@link Comparable} of type {@code T} to compare against
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Comparable<T>> int compare(final T a, final T b) {
		return a == b ? 0 : a.compareTo(b);
	}

	/**
	 * Compares the specified {@code T} objects for order. Returns a negative integer, zero or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param <T> the type of the objects to compare
	 * @param a   the {@code T} object to compare
	 * @param b   the other {@code T} object to compare against
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static <T> int compareCast(final T a, final T b) {
		return a == b ? 0 : ((Comparable<T>) a).compareTo(b);
	}

	/**
	 * Returns zero if {@code a} and {@code b} are identical, {@code comparator.compare(a, b)}
	 * otherwise.
	 * <p>
	 * @param <T>        the type of the objects to compare for order
	 * @param a          the {@link Comparable} of type {@code T} to compare
	 * @param b          the other {@link Comparable} of type {@code T} to compare against
	 * @param comparator the {@link Comparator} of supertype {@code T} to determine the order
	 * <p>
	 * @return zero if {@code a} and {@code b} are identical, {@code comparator.compare(a, b)}
	 *         otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T> int compare(final T a, final T b, final Comparator<? super T> comparator) {
		return a == b ? 0 : comparator.compare(a, b);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is less than {@code b}.
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare
	 * @param a   the {@link Comparable} of type {@code T} to compare
	 * @param b   the other {@link Comparable} of type {@code T} to compare against
	 * <p>
	 * @return {@code true} if {@code a} is less than {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Comparable<T>> boolean isLessThan(final T a, final T b) {
		return a.compareTo(b) < 0;
	}

	/**
	 * Tests whether {@code a} is less or equal to {@code b}.
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare
	 * @param a   the {@link Comparable} of type {@code T} to compare
	 * @param b   the other {@link Comparable} of type {@code T} to compare against
	 * <p>
	 * @return {@code true} if {@code a} is less or equal to {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Comparable<T>> boolean isLessOrEqualTo(final T a, final T b) {
		return a.compareTo(b) <= 0;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is greater than {@code b}.
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare
	 * @param a   the {@link Comparable} of type {@code T} to compare
	 * @param b   the other {@link Comparable} of type {@code T} to compare against
	 * <p>
	 * @return {@code true} if {@code a} is greater than {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Comparable<T>> boolean isGreaterThan(final T a, final T b) {
		return a.compareTo(b) > 0;
	}

	/**
	 * Tests whether {@code a} is greater or equal to {@code b}.
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare
	 * @param a   the {@link Comparable} of type {@code T} to compare
	 * @param b   the other {@link Comparable} of type {@code T} to compare against
	 * <p>
	 * @return {@code true} if {@code a} is greater or equal to {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Comparable<T>> boolean isGreaterOrEqualTo(final T a, final T b) {
		return a.compareTo(b) >= 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the smaller of {@code a} and {@code b}, or {@code a} if they are equal.
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare
	 * @param a   the {@link Comparable} of type {@code T} to compare
	 * @param b   the other {@link Comparable} of type {@code T} to compare against
	 * <p>
	 * @return the smaller of {@code a} and {@code b}, or {@code a} if they are equal
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Comparable<T>> T getMin(final T a, final T b) {
		return Comparables.<T>isLessOrEqualTo(a, b) ? a : b;
	}

	/**
	 * Returns the larger of {@code a} and {@code b}, or {@code a} if they are equal.
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare
	 * @param a   the {@link Comparable} of type {@code T} to compare
	 * @param b   the other {@link Comparable} of type {@code T} to compare against
	 * <p>
	 * @return the larger of {@code a} and {@code b}, or {@code a} if they are equal
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Comparable<T>> T getMax(final T a, final T b) {
		return Comparables.<T>isGreaterOrEqualTo(a, b) ? a : b;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code a} and {@code b} are equal to each other.
	 * <p>
	 * @param <T> the self {@link Comparable} type of the objects to compare for equality
	 * @param a   the {@link Comparable} of type {@code T} to compare for equality (may be
	 *            {@code null})
	 * @param b   the other {@link Comparable} of type {@code T} to compare against for equality
	 *            (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} and {@code b} are equal to each other, {@code false}
	 *         otherwise
	 */
	public static <T extends Comparable<T>> boolean equals(final T a, final T b) {
		return a == b || a != null && b != null && a.compareTo(b) == 0;
	}
}
