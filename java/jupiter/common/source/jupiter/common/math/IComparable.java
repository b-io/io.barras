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

/**
 * {@link IComparable} extends {@link Comparable} of {@code T} type.
 * <p>
 * @param <T> the self {@link Comparable} type of the {@link IComparable}
 */
public interface IComparable<T extends Comparable<? super T>>
		extends Comparable<T> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code other} for order. Returns a negative integer, {@code 0} or
	 * a positive integer as {@code this} is less than, equal to or greater than {@code other} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code this} is less than,
	 *         equal to or greater than {@code other} (with {@code null} considered as the minimum
	 *         value)
	 */
	public int compareTo(final T other);

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is less than {@code other} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is less than {@code other}, {@code false} otherwise
	 *         (with {@code null} considered as the minimum value)
	 * <p>
	 * @throws ClassCastException if the {@code other} type prevents it from being compared to
	 *                            {@code this}
	 */
	public boolean isLessThan(final T other);

	/**
	 * Tests whether {@code this} is less or equal to {@code other} (with {@code null} considered as
	 * the minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is less or equal to {@code other}, {@code false}
	 *         otherwise (with {@code null} considered as the minimum value)
	 * <p>
	 * @throws ClassCastException if the {@code other} type prevents it from being compared to
	 *                            {@code this}
	 */
	public boolean isLessOrEqualTo(final T other);

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is greater than {@code other} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is greater than {@code other}, {@code false} otherwise
	 *         (with {@code null} considered as the minimum value)
	 * <p>
	 * @throws ClassCastException if the {@code other} type prevents it from being compared to
	 *                            {@code this}
	 */
	public boolean isGreaterThan(final T other);

	/**
	 * Tests whether {@code this} is greater or equal to {@code other} (with {@code null} considered
	 * as the minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is greater or equal to {@code other}, {@code false}
	 *         otherwise (with {@code null} considered as the minimum value)
	 * <p>
	 * @throws ClassCastException if the {@code other} type prevents it from being compared to
	 *                            {@code this}
	 */
	public boolean isGreaterOrEqualTo(final T other);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the smaller of {@code this} and {@code other}, or {@code this} if they are equal
	 * (with {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against (may be {@code null})
	 * <p>
	 * @return the smaller of {@code this} and {@code other}, or {@code this} if they are equal
	 *         (with {@code null} considered as the minimum value)
	 * <p>
	 * @throws ClassCastException if the {@code other} type prevents it from being compared to
	 *                            {@code this}
	 */
	public Comparable<? super T> getMin(final T other);

	/**
	 * Returns the larger of {@code this} and {@code other}, or {@code this} if they are equal (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against (may be {@code null})
	 * <p>
	 * @return the larger of {@code this} and {@code other}, or {@code this} if they are equal (with
	 *         {@code null} considered as the minimum value)
	 * <p>
	 * @throws ClassCastException if the {@code other} type prevents it from being compared to
	 *                            {@code this}
	 */
	public Comparable<? super T> getMax(final T other);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@code T} object to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the {@code other} type prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 *
	 * @see #hashCode()
	 */
	public boolean equals(final T other);

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
	 */
	public int hashCode();
}
