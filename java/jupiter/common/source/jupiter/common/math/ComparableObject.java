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

import java.io.Serializable;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

/**
 * {@link ComparableObject} is an {@link IComparable} of {@code T} type.
 * <p>
 * @param <T> the self {@link Comparable} type of the {@link ComparableObject}
 */
public abstract class ComparableObject<T extends Comparable<? super T>>
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
	 * Compares {@code this} with {@code other} for order. Returns a negative integer, {@code 0} or
	 * a positive integer as {@code this} is less than, equal to or greater than {@code other} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code this} is less than,
	 *         equal to or greater than {@code other}
	 * <p>
	 * @throws ClassCastException if {@code other} cannot be compared to {@code this}
	 */
	public abstract int compareTo(final T other);

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is less than {@code other} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is less than {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException if {@code other} cannot be compared to {@code this}
	 */
	public boolean isLessThan(final T other) {
		return compareTo(other) < 0;
	}

	/**
	 * Tests whether {@code this} is less or equal to {@code other} (with {@code null} considered as
	 * the minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is less or equal to {@code other}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException if {@code other} cannot be compared to {@code this}
	 */
	public boolean isLessOrEqualTo(final T other) {
		return compareTo(other) <= 0;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is greater than {@code other} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is greater than {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException if {@code other} cannot be compared to {@code this}
	 */
	public boolean isGreaterThan(final T other) {
		return compareTo(other) > 0;
	}

	/**
	 * Tests whether {@code this} is greater or equal to {@code other} (with {@code null} considered
	 * as the minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is greater or equal to {@code other}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException if {@code other} cannot be compared to {@code this}
	 */
	public boolean isGreaterOrEqualTo(final T other) {
		return compareTo(other) >= 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the smaller of {@code this} and {@code other}, or {@code this} if they are equal
	 * (with {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against (may be {@code null})
	 * <p>
	 * @return the smaller of {@code this} and {@code other}, or {@code this} if they are equal
	 * <p>
	 * @throws ClassCastException if {@code other} cannot be compared to {@code this}
	 */
	public Comparable<? super T> getMin(final T other) {
		return isLessOrEqualTo(other) ? this : other;
	}

	/**
	 * Returns the larger of {@code this} and {@code other}, or {@code this} if they are equal (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@code T} object to compare against (may be {@code null})
	 * <p>
	 * @return the larger of {@code this} and {@code other}, or {@code this} if they are equal
	 * <p>
	 * @throws ClassCastException if {@code other} cannot be compared to {@code this}
	 */
	public Comparable<? super T> getMax(final T other) {
		return isGreaterOrEqualTo(other) ? this : other;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	@SuppressWarnings({"cast", "unchecked"})
	public ComparableObject<T> clone() {
		try {
			return (ComparableObject<T>) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException if {@code other} cannot be compared to {@code this}
	 *
	 * @see #hashCode()
	 */
	@Override
	@SuppressWarnings({"cast", "unchecked"})
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Comparable)) {
			return false;
		}
		return equals((T) other);
	}

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@code T} object to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException if {@code other} cannot be compared to {@code this}
	 *
	 * @see #hashCode()
	 */
	public boolean equals(final T other) {
		return compareTo(other) == 0;
	}

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see #equals(Object)
	 * @see System#identityHashCode(Object)
	 */
	@Override
	public abstract int hashCode();
}
