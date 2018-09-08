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

public interface IComparable<T extends Comparable<T>>
		extends Comparable<T> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code comparable} for order. Returns a negative integer, zero or
	 * a positive integer as {@code this} is less than, equal to or greater than {@code comparable}.
	 * <p>
	 * @param comparable the {@code T} object to compare with
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code this} is less than, equal to
	 *         or greater than {@code comparable}
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparable} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparable} is {@code null}
	 */
	public int compareTo(final T comparable);

	/**
	 * Tests whether {@code this} is less than {@code comparable}.
	 * <p>
	 * @param comparable the {@code T} object to compare with
	 * <p>
	 * @return {@code true} if {@code this} is less than {@code comparable}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparable} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparable} is {@code null}
	 */
	public boolean isLessThan(final T comparable);

	/**
	 * Tests whether {@code this} is less or equal to {@code comparable}.
	 * <p>
	 * @param comparable the {@code T} object to compare with
	 * <p>
	 * @return {@code true} if {@code this} is less or equal to {@code comparable}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparable} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparable} is {@code null}
	 */
	public boolean isLessOrEqualTo(final T comparable);

	/**
	 * Tests whether {@code this} is greater than {@code comparable}.
	 * <p>
	 * @param comparable the {@code T} object to compare with
	 * <p>
	 * @return {@code true} if {@code this} is greater than {@code comparable}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparable} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparable} is {@code null}
	 */
	public boolean isGreaterThan(final T comparable);

	/**
	 * Returns @code true} if {@code this} is greater or equal to {@code comparable}.
	 * <p>
	 * @param comparable the {@code T} object to compare with
	 * <p>
	 * @return {@code true} if {@code this} is greater or equal to {@code comparable}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparable} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparable} is {@code null}
	 */
	public boolean isGreaterOrEqualTo(final T comparable);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the smaller of {@code this} and {@code comparable}, or {@code this} if they are
	 * equal.
	 * <p>
	 * @param comparable the {@code T} object to compare with
	 * <p>
	 * @return the smaller of {@code this} and {@code comparable}, or {@code this} if they are equal
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparable} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparable} is {@code null}
	 */
	public Comparable<T> getMin(final T comparable);

	/**
	 * Returns the larger of {@code this} and {@code comparable}, or {@code this} if they are equal.
	 * <p>
	 * @param comparable the {@code T} object to compare with
	 * <p>
	 * @return the larger of {@code this} and {@code comparable}, or {@code this} if they are equal
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparable} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparable} is {@code null}
	 */
	public Comparable<T> getMax(final T comparable);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code comparable}.
	 * <p>
	 * @param comparable the {@code T} object to compare with for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code comparable}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparable} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparable} is {@code null}
	 */
	public boolean equals(final T comparable);
}
