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

import java.util.Comparator;

public class ComparableObjects {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected ComparableObjects() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code 0} if {@code a} and {@code b} are identical, {@code c.compare(a, b)}
	 * otherwise.
	 * <p>
	 * @param <T> the type of the objects to compare
	 * @param a   a {@code T} object
	 * @param b   another {@code T} object to compare with {@code a} for order
	 * @param c   the {@link Comparator} to use
	 * <p>
	 * @return {@code 0} if {@code a} and {@code b} are identical, {@code c.compare(a, b)} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T> int compare(final T a, final T b, final Comparator<? super T> c) {
		return a == b ? 0 : c.compare(a, b);
	}

	/**
	 * Returns {@code true} if {@code a} is less than {@code b}, {@code false} otherwise.
	 * <p>
	 * @param <T> the type of the comparable objects to compare
	 * @param a   a comparable {@code T} object
	 * @param b   another comparable {@code T} object to compare with {@code a}
	 * <p>
	 * @return {@code true} if {@code a} is less than {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Comparable<T>> boolean isLessThan(final T a, final T b) {
		return a.compareTo(b) < 0;
	}

	/**
	 * Returns {@code true} if {@code a} is less or equal to {@code b}, {@code false} otherwise.
	 * <p>
	 * @param <T> the type of the comparable objects to compare
	 * @param a   a comparable {@code T} object
	 * @param b   another comparable {@code T} object to compare with {@code a}
	 * <p>
	 * @return {@code true} if {@code a} is less or equal to {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Comparable<T>> boolean isLessOrEqualTo(final T a, final T b) {
		return a.compareTo(b) <= 0;
	}

	/**
	 * Returns {@code true} if {@code a} is greater than {@code b}, {@code false} otherwise.
	 * <p>
	 * @param <T> the type of the comparable objects to compare
	 * @param a   a comparable {@code T} object
	 * @param b   another comparable {@code T} object to compare with {@code a}
	 * <p>
	 * @return {@code true} if {@code a} is greater than {@code b}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Comparable<T>> boolean isGreaterThan(final T a, final T b) {
		return a.compareTo(b) > 0;
	}

	/**
	 * Returns {@code true} if {@code a} is greater or equal to {@code b}, {@code false} otherwise.
	 * <p>
	 * @param <T> the type of the comparable objects to compare
	 * @param a   a comparable {@code T} object
	 * @param b   another comparable {@code T} object to compare with {@code a}
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
	 * @param <T> the type of the comparable objects to compare
	 * @param a   a comparable {@code T} object
	 * @param b   another comparable {@code T} object to compare with {@code a}
	 * <p>
	 * @return the smaller of {@code a} and {@code b}, or {@code a} if they are equal
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Comparable<T>> T getMin(final T a, final T b) {
		return ComparableObjects.<T>isLessOrEqualTo(a, b) ? a : b;
	}

	/**
	 * Returns the larger of {@code a} and {@code b}, or {@code a} if they are equal.
	 * <p>
	 * @param <T> the type of the comparable objects to compare
	 * @param a   a comparable {@code T} object
	 * @param b   another comparable {@code T} object to compare with {@code a}
	 * <p>
	 * @return the larger of {@code a} and {@code b}, or {@code a} if they are equal
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Comparable<T>> T getMax(final T a, final T b) {
		return ComparableObjects.<T>isGreaterOrEqualTo(a, b) ? a : b;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if {@code a} and {@code b} are equal to each other, {@code false}
	 * otherwise.
	 * <p>
	 * @param <T> the type of the comparable objects to compare
	 * @param a   a comparable {@code T} object
	 * @param b   another comparable {@code T} object to compare with {@code a} for equality
	 * <p>
	 * @return {@code true} if {@code a} and {@code b} are equal to each other, {@code false}
	 *         otherwise
	 */
	public static <T extends Comparable<T>> boolean equals(final T a, final T b) {
		return a == b || a != null && a.compareTo(b) == 0;
	}
}
