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

public abstract class ComparableObject<T extends Comparable<T>>
		implements IComparable<T> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected ComparableObject() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code comparableObject} for order. Returns a negative integer,
	 * zero or a positive integer as {@code this} is less than, equal to or greater than
	 * {@code comparableObject}.
	 * <p>
	 * @param comparableObject the {@code T} object to compare with
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code this} is less than, equal to
	 *         or greater than {@code comparableObject}
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparableObject} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparableObject} is {@code null}
	 */
	public abstract int compareTo(final T comparableObject);

	/**
	 * Tests whether {@code this} is less than {@code comparableObject}.
	 * <p>
	 * @param comparableObject the {@code T} object to compare with
	 * <p>
	 * @return {@code true} if {@code this} is less than {@code comparableObject}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparableObject} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparableObject} is {@code null}
	 */
	public boolean isLessThan(final T comparableObject) {
		return compareTo(comparableObject) < 0;
	}

	/**
	 * Tests whether {@code this} is less or equal to {@code comparableObject}.
	 * <p>
	 * @param comparableObject the {@code T} object to compare with
	 * <p>
	 * @return {@code true} if {@code this} is less or equal to {@code comparableObject},
	 *         {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparableObject} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparableObject} is {@code null}
	 */
	public boolean isLessOrEqualTo(final T comparableObject) {
		return compareTo(comparableObject) <= 0;
	}

	/**
	 * Tests whether {@code this} is greater than {@code comparableObject}.
	 * <p>
	 * @param comparableObject the {@code T} object to compare with
	 * <p>
	 * @return {@code true} if {@code this} is greater than {@code comparableObject}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparableObject} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparableObject} is {@code null}
	 */
	public boolean isGreaterThan(final T comparableObject) {
		return compareTo(comparableObject) > 0;
	}

	/**
	 * Tests whether {@code this} is greater or equal to {@code comparableObject}.
	 * <p>
	 * @param comparableObject the {@code T} object to compare with
	 * <p>
	 * @return {@code true} if {@code this} is greater or equal to {@code comparableObject},
	 *         {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparableObject} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparableObject} is {@code null}
	 */
	public boolean isGreaterOrEqualTo(final T comparableObject) {
		return compareTo(comparableObject) >= 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the smaller of {@code this} and {@code comparableObject}, or {@code this} if they are
	 * equal.
	 * <p>
	 * @param comparableObject the {@code T} object to compare with
	 * <p>
	 * @return the smaller of {@code this} and {@code comparableObject}, or {@code this} if they are
	 *         equal
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparableObject} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparableObject} is {@code null}
	 */
	public Comparable<T> getMin(final T comparableObject) {
		return isLessOrEqualTo(comparableObject) ? this : comparableObject;
	}

	/**
	 * Returns the larger of {@code this} and {@code comparableObject}, or {@code this} if they are
	 * equal.
	 * <p>
	 * @param comparableObject the {@code T} object to compare with
	 * <p>
	 * @return the larger of {@code this} and {@code comparableObject}, or {@code this} if they are
	 *         equal
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparableObject} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparableObject} is {@code null}
	 */
	public Comparable<T> getMax(final T comparableObject) {
		return isGreaterOrEqualTo(comparableObject) ? this : comparableObject;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the {@link Object} to compare with for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the class of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		return equals((T) other);
	}

	/**
	 * Compares {@code this} with {@code comparableObject} for equivalence.
	 * <p>
	 * @param comparableObject the {@code T} object to compare with for equality
	 * <p>
	 * @return {@code true} if {@code this} is less than {@code comparableObject}, {@code false}
	 * <p>
	 * @throws ClassCastException   if the class of {@code comparableObject} prevents it from being
	 *                              compared to {@code this}
	 * @throws NullPointerException if {@code comparableObject} is {@code null}
	 */
	public boolean equals(final T comparableObject) {
		return compareTo(comparableObject) == 0;
	}

	@Override
	public abstract int hashCode();
}
