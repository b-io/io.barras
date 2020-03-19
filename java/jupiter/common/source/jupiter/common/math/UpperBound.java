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

import static jupiter.common.util.Characters.LEFT_BRACKET;
import static jupiter.common.util.Characters.RIGHT_BRACKET;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link UpperBound} is the inclusive or exclusive upper {@link Bound}.
 * <p>
 * @param <T> the self {@link Comparable} type of the {@link UpperBound}
 */
public class UpperBound<T extends Comparable<? super T>>
		extends Bound<T> {

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
	 * Constructs an {@link UpperBound} of {@code T} type.
	 */
	public UpperBound() {
		this(null);
	}

	/**
	 * Constructs an {@link UpperBound} of {@code T} type with the specified {@code T} value.
	 * <p>
	 * @param value the {@code T} value (exclusive)
	 */
	public UpperBound(final T value) {
		this(value, false);
	}

	/**
	 * Constructs an {@link UpperBound} of {@code T} type with the specified {@code T} value.
	 * <p>
	 * @param value       the {@code T} value
	 * @param isInclusive the flag specifying whether {@code this} is inclusive
	 */
	public UpperBound(final T value, final boolean isInclusive) {
		super(value, isInclusive);
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
	public int compareTo(final Bound<T> other) {
		// Check the arguments
		if (this == other) {
			return 0;
		}
		if (other == null) {
			return 1;
		}

		// Compare the bounds for order
		return Comparables.isLessThan(value, other.value) ? -1 :
				Comparables.isGreaterThan(value, other.value) ? 1 :
				isInclusive == other.isInclusive ? 0 :
						other instanceof UpperBound ? (isInclusive ? 1 : -1) :
								(isInclusive ? -1 : 1);
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
	public UpperBound<T> clone() {
		return (UpperBound<T>) super.clone();
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
		if (other == null || !(other instanceof UpperBound)) {
			return false;
		}
		final UpperBound<?> otherBound = (UpperBound<?>) other;
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
		return Strings.join(value, isInclusive ? RIGHT_BRACKET : LEFT_BRACKET);
	}
}
