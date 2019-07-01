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
package jupiter.common.math;

import java.io.Serializable;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Strings;

/**
 * {@link ComparableObject} is an {@link IComparable} of type {@code T}.
 * <p>
 * @param <T> the self {@link Comparable} type of the {@link ComparableObject}
 */
public abstract class ComparableObject<T extends Comparable<T>>
		implements ICloneable<ComparableObject<T>>, IComparable<T>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ComparableObject}.
	 */
	protected ComparableObject() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code other} for order. Returns a negative integer, zero or a
	 * positive integer as {@code this} is less than, equal to or greater than {@code other}.
	 * <p>
	 * @param other the other {@code T} object to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code this} is less than, equal to
	 *         or greater than {@code other}
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	public abstract int compareTo(final T other);

	/**
	 * Tests whether {@code this} is less than {@code other}.
	 * <p>
	 * @param other the other {@code T} object to compare against
	 * <p>
	 * @return {@code true} if {@code this} is less than {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	public boolean isLessThan(final T other) {
		return compareTo(other) < 0;
	}

	/**
	 * Tests whether {@code this} is less or equal to {@code other}.
	 * <p>
	 * @param other the other {@code T} object to compare against
	 * <p>
	 * @return {@code true} if {@code this} is less or equal to {@code other}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	public boolean isLessOrEqualTo(final T other) {
		return compareTo(other) <= 0;
	}

	/**
	 * Tests whether {@code this} is greater than {@code other}.
	 * <p>
	 * @param other the other {@code T} object to compare against
	 * <p>
	 * @return {@code true} if {@code this} is greater than {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	public boolean isGreaterThan(final T other) {
		return compareTo(other) > 0;
	}

	/**
	 * Tests whether {@code this} is greater or equal to {@code other}.
	 * <p>
	 * @param other the other {@code T} object to compare against
	 * <p>
	 * @return {@code true} if {@code this} is greater or equal to {@code other}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	public boolean isGreaterOrEqualTo(final T other) {
		return compareTo(other) >= 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the smaller of {@code this} and {@code other}, or {@code this} if they are equal.
	 * <p>
	 * @param other the other {@code T} object to compare against
	 * <p>
	 * @return the smaller of {@code this} and {@code other}, or {@code this} if they are equal
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	public Comparable<T> getMin(final T other) {
		return isLessOrEqualTo(other) ? this : other;
	}

	/**
	 * Returns the larger of {@code this} and {@code other}, or {@code this} if they are equal.
	 * <p>
	 * @param other the other {@code T} object to compare against
	 * <p>
	 * @return the larger of {@code this} and {@code other}, or {@code this} if they are equal
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	public Comparable<T> getMax(final T other) {
		return isGreaterOrEqualTo(other) ? this : other;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ComparableObject<T> clone() {
		try {
			return (ComparableObject<T>) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new RuntimeException(Strings.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 *
	 * @see #hashCode()
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
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@code T} object to compare against for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 *
	 * @see #hashCode()
	 */
	public boolean equals(final T other) {
		return compareTo(other) == 0;
	}

	/**
	 * Returns the hash code for {@code this}.
	 * <p>
	 * @return the hash code for {@code this}
	 *
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
	 */
	@Override
	public abstract int hashCode();
}
