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
 * {@link Bound} is the inclusive or exclusive {@code T} value (inclusive by default).
 * <p>
 * @param <T> the self {@link Comparable} type of the {@link Bound}
 */
public abstract class Bound<T extends Comparable<? super T>>
		implements Comparable<Bound<T>>, ICloneable<Bound<T>>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@code T} value.
	 */
	protected T value;
	/**
	 * The flag specifying whether {@code this} is inclusive.
	 */
	protected boolean isInclusive;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Bound} of {@code T} type with the specified {@code T} value.
	 * <p>
	 * @param value       the {@code T} value
	 * @param isInclusive the flag specifying whether {@code this} is inclusive
	 */
	protected Bound(final T value, final boolean isInclusive) {
		this.value = value;
		this.isInclusive = isInclusive;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code T} value.
	 * <p>
	 * @return the {@code T} value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Returns the flag specifying whether {@code this} is inclusive.
	 * <p>
	 * @return the flag specifying whether {@code this} is inclusive
	 */
	public boolean isInclusive() {
		return isInclusive;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@code T} value.
	 * <p>
	 * @param value a {@code T} value
	 */
	public void setValue(final T value) {
		this.value = value;
	}

	/**
	 * Sets the flag specifying whether {@code this} is inclusive.
	 * <p>
	 * @param isInclusive a {@code boolean} value
	 */
	public void setInclusive(final boolean isInclusive) {
		this.isInclusive = isInclusive;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code other} for order. Returns a negative integer, {@code 0} or
	 * a positive integer as {@code this} is less than, equal to or greater than {@code other} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@link Bound} of {@code T} type to compare against for order (may be
	 *              {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code this} is less than,
	 *         equal to or greater than {@code other}
	 */
	@Override
	public abstract int compareTo(final Bound<T> other);


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
	public Bound<T> clone() {
		try {
			final Bound<T> clone = (Bound<T>) super.clone();
			clone.value = Objects.clone(value);
			return clone;
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
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Bound)) {
			return false;
		}
		final Bound<?> otherBound = (Bound<?>) other;
		return Objects.equals(value, otherBound.value) &&
				Objects.equals(isInclusive, otherBound.isInclusive);
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
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, value, isInclusive);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Objects.toString(value);
	}
}
