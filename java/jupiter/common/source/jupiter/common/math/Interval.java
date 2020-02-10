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

import java.io.Serializable;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.test.Arguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link Interval} is the {@link ISet} of type {@code T}.
 * <p>
 * @param <T> the self {@link Comparable} type of the {@link Interval}
 */
public class Interval<T extends Comparable<T>>
		implements ICloneable<Interval<T>>, ISet<T>, Serializable {

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
	 * The {@code T} lower and upper bounds.
	 */
	protected T lowerBound, upperBound;
	/**
	 * The flags specifying whether the lower and upper bound are respectively inclusive.
	 */
	protected boolean isLowerInclusive, isUpperInclusive;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link Interval} of type {@code T}.
	 */
	public Interval() {
		lowerBound = null;
		upperBound = null;
		isLowerInclusive = true;
		isUpperInclusive = false;
	}

	/**
	 * Constructs an {@link Interval} of type {@code T} with the specified {@code T} lower and upper
	 * bounds.
	 * <p>
	 * @param lowerBound the {@code T} lower bound (inclusive)
	 * @param upperBound the {@code T} upper bound (exclusive)
	 */
	public Interval(final T lowerBound, final T upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		isLowerInclusive = true;
		isUpperInclusive = false;
	}

	/**
	 * Constructs an {@link Interval} of type {@code T} with the specified {@code T} lower and upper
	 * bounds.
	 * <p>
	 * @param lowerBound       the {@code T} lower bound
	 * @param upperBound       the {@code T} upper bound
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 */
	public Interval(final T lowerBound, final T upperBound, final boolean isLowerInclusive,
			final boolean isUpperInclusive) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.isLowerInclusive = isLowerInclusive;
		this.isUpperInclusive = isUpperInclusive;
	}

	/**
	 * Constructs an {@link Interval} of type {@code T} with the specified lower and upper bounds
	 * {@link Pair}.
	 * <p>
	 * @param pair the lower and upper bounds {@link Pair} of type {@code T}
	 */
	public Interval(final Pair<T, T> pair) {
		this(Arguments.requireNonNull(pair, "pair").getFirst(), pair.getSecond());
	}

	/**
	 * Constructs an {@link Interval} of type {@code T} with the specified lower and upper bounds
	 * {@link Pair}.
	 * <p>
	 * @param pair             the lower and upper bounds {@link Pair} of type {@code T}
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 */
	public Interval(final Pair<T, T> pair, final boolean isLowerInclusive,
			final boolean isUpperInclusive) {
		this(Arguments.requireNonNull(pair, "pair").getFirst(), pair.getSecond(),
				isLowerInclusive, isUpperInclusive);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code T} lower bound.
	 * <p>
	 * @return the {@code T} lower bound
	 */
	public T getLowerBound() {
		return lowerBound;
	}

	/**
	 * Returns the {@code T} upper bound.
	 * <p>
	 * @return the {@code T} upper bound
	 */
	public T getUpperBound() {
		return upperBound;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@code T} lower bound.
	 * <p>
	 * @param lowerBound the {@code T} lower bound
	 */
	public void setLowerBound(final T lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * Sets the {@code T} upper bound.
	 * <p>
	 * @param upperBound the {@code T} upper bound
	 */
	public void setUpperBound(final T upperBound) {
		this.upperBound = upperBound;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GROUP
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is empty.
	 * <p>
	 * @return {@code true} if {@code this} is empty, {@code false} otherwise
	 */
	public boolean isEmpty() {
		return lowerBound == null || upperBound == null;
	}

	/**
	 * Tests whether {@code this} contains the specified {@code T} object.
	 * <p>
	 * @param object the {@code T} object to test for presence
	 * <p>
	 * @return {@code true} if {@code this} contains the {@code T} object, {@code false} otherwise
	 */
	public boolean isInside(final T object) {
		return (isLowerInclusive ? object.compareTo(lowerBound) >= 0 :
				object.compareTo(lowerBound) > 0) &&
				(isUpperInclusive ? object.compareTo(upperBound) <= 0 :
						object.compareTo(upperBound) < 0);
	}

	/**
	 * Tests whether {@code this} is valid.
	 * <p>
	 * @return {@code true} if {@code this} is valid, {@code false} otherwise
	 */
	public boolean isValid() {
		return !isEmpty() && (isLowerInclusive && isUpperInclusive ?
				lowerBound.compareTo(upperBound) <= 0 :
				lowerBound.compareTo(upperBound) < 0);
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
	public Interval<T> clone() {
		try {
			return (Interval<T>) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
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
	 * @throws ClassCastException   if the {@code other} type prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Interval)) {
			return false;
		}
		final Interval<?> otherInterval = (Interval<?>) other;
		return Objects.equals(lowerBound, otherInterval.lowerBound) &&
				Objects.equals(upperBound, otherInterval.upperBound) &&
				Objects.equals(isLowerInclusive, otherInterval.isLowerInclusive) &&
				Objects.equals(isUpperInclusive, otherInterval.isUpperInclusive);
	}

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
	 */
	@Override
	@SuppressWarnings("unchecked")
	public int hashCode() {
		return Objects.hashCode(serialVersionUID,
				lowerBound, upperBound,
				isLowerInclusive, isUpperInclusive);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return (isLowerInclusive ? LEFT_BRACKET : RIGHT_BRACKET) +
				Arrays.join(lowerBound, upperBound) +
				(isUpperInclusive ? RIGHT_BRACKET : LEFT_BRACKET);
	}
}
