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
import jupiter.common.struct.tuple.Pair;
import jupiter.common.test.Arguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link Interval} is an {@link ISet} of {@code T} type.
 * <p>
 * @param <T> the self {@link Comparable} type of the {@link Interval}
 */
public class Interval<T extends Comparable<? super T>>
		implements Comparable<Interval<T>>, ICloneable<Interval<T>>, ISet<T>, Serializable {

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
	 * The {@link LowerBound} of {@code T} type.
	 */
	protected LowerBound<T> lowerBound;
	/**
	 * The {@link UpperBound} of {@code T} type.
	 */
	protected UpperBound<T> upperBound;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty {@link Interval} of {@code T} type by default.
	 */
	public Interval() {
		this(new LowerBound<T>(), new UpperBound<T>());
	}

	//////////////////////////////////////////////

	/**
	 * Constructs an {@link Interval} of {@code T} type with the specified values of the
	 * {@link LowerBound} and {@link UpperBound}.
	 * <p>
	 * @param lowerBoundValue the {@code T} value of the {@link LowerBound} (inclusive) (may be
	 *                        {@code null})
	 * @param upperBoundValue the {@code T} value of the {@link UpperBound} (exclusive) (may be
	 *                        {@code null})
	 */
	public Interval(final T lowerBoundValue, final T upperBoundValue) {
		this(new LowerBound<T>(lowerBoundValue), new UpperBound<T>(upperBoundValue));
	}

	/**
	 * Constructs an {@link Interval} of {@code T} type with the specified {@link LowerBound} and
	 * {@link UpperBound}.
	 * <p>
	 * @param lowerBound the {@link LowerBound} of {@code T} type
	 * @param upperBound the {@link UpperBound} of {@code T} type
	 */
	public Interval(final LowerBound<T> lowerBound, final UpperBound<T> upperBound) {
		// Check the arguments
		Arguments.requireNonNull(lowerBound, "lower bound");
		Arguments.requireNonNull(upperBound, "upper bound");

		// Set the attributes
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		updateMemberships();
	}

	/**
	 * Constructs an {@link Interval} of {@code T} type with the specified {@link Pair} of
	 * {@link LowerBound} and {@link UpperBound}.
	 * <p>
	 * @param pair the {@link Pair} of {@link LowerBound} and {@link UpperBound} of {@code T} type
	 */
	public Interval(final Pair<LowerBound<T>, UpperBound<T>> pair) {
		this(Arguments.requireNonNull(pair, "pair of lower and upper bounds").getFirst(),
				pair.getSecond());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link LowerBound} of {@code T} type.
	 * <p>
	 * @return the {@link LowerBound} of {@code T} type
	 */
	public LowerBound<T> getLowerBound() {
		return lowerBound;
	}

	/**
	 * Returns the {@link UpperBound} of {@code T} type.
	 * <p>
	 * @return the {@link UpperBound} of {@code T} type
	 */
	public UpperBound<T> getUpperBound() {
		return upperBound;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@link LowerBound}.
	 * <p>
	 * @param lowerBound a {@link LowerBound} of {@code T} type
	 */
	public void setLowerBound(final LowerBound<T> lowerBound) {
		// Check the arguments
		Arguments.requireNonNull(lowerBound, "lower bound");

		// Set the lower bound
		this.lowerBound = lowerBound;
		updateMemberships();
	}

	/**
	 * Sets the {@link UpperBound}.
	 * <p>
	 * @param upperBound an {@link UpperBound} of {@code T} type
	 */
	public void setUpperBound(final UpperBound<T> upperBound) {
		// Check the arguments
		Arguments.requireNonNull(upperBound, "upper bound");

		// Set the upper bound
		this.upperBound = upperBound;
		updateMemberships();
	}

	/**
	 * Updates the memberships of the {@link LowerBound} and {@link UpperBound}.
	 */
	protected void updateMemberships() {
		if ((lowerBound.isInclusive || upperBound.isInclusive) &&
				Objects.equals(lowerBound.value, upperBound.value)) {
			lowerBound.isInclusive = true;
			upperBound.isInclusive = true;
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Merges the specified {@link Interval} with {@code this}.
	 * <p>
	 * @param other the other {@link Interval} of {@code T} type to merge with
	 * <p>
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	public boolean merge(final Interval<T> other) {
		// Check the arguments
		if (other == null) {
			return false;
		}

		// Merge the intervals
		boolean hasChanged = false;
		// • The lower bound
		if (other.lowerBound.value != null &&
				(lowerBound.value == null &&
						(upperBound.value == null || isValid(other.lowerBound, upperBound)) ||
				lowerBound.value != null &&
						Comparables.isGreaterThan(lowerBound, other.lowerBound) &&
								(other.isInside(lowerBound.value) ||
										lowerBound.isInclusive &&
												lowerBound.value.equals(other.upperBound.value)))) {
			lowerBound = other.lowerBound.clone();
			hasChanged = true;
		}
		// • The upper bound
		if (other.upperBound.value != null &&
				(upperBound.value == null &&
						(lowerBound.value == null || isValid(lowerBound, other.upperBound)) ||
				upperBound.value != null &&
						Comparables.isLessThan(upperBound, other.upperBound) &&
								(other.isInside(upperBound.value) ||
										upperBound.isInclusive &&
												upperBound.value.equals(other.lowerBound.value)))) {
			upperBound = other.upperBound.clone();
			hasChanged = true;
		}
		updateMemberships();
		return hasChanged;
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
		return isEmpty(lowerBound, upperBound);
	}

	/**
	 * Tests whether the {@link Interval} with the specified {@link LowerBound} and
	 * {@link UpperBound} is empty.
	 * <p>
	 * @param <T>        the self {@link Comparable} type of the {@link Interval} to test
	 * @param lowerBound the {@link LowerBound} of {@code T} type of the {@link Interval} to test
	 * @param upperBound the {@link UpperBound} of {@code T} type of the {@link Interval} to test
	 * <p>
	 * @return {@code true} if the {@link Interval} with the specified {@link LowerBound} and
	 *         {@link UpperBound} is empty, {@code false} otherwise
	 */
	public static <T extends Comparable<? super T>> boolean isEmpty(
			final LowerBound<T> lowerBound, final UpperBound<T> upperBound) {
		return lowerBound.value == null || upperBound.value == null ||
				Comparables.isGreaterThan(lowerBound, upperBound) ||
				!(lowerBound.isInclusive && upperBound.isInclusive) &&
						lowerBound.value.equals(upperBound.value);
	}

	/**
	 * Tests whether {@code this} is non-empty.
	 * <p>
	 * @return {@code true} if {@code this} is non-empty, {@code false} otherwise
	 */
	public boolean isNonEmpty() {
		return isNonEmpty(lowerBound, upperBound);
	}

	/**
	 * Tests whether the {@link Interval} with the specified {@link LowerBound} and
	 * {@link UpperBound} is non-empty.
	 * <p>
	 * @param <T>        the self {@link Comparable} type of the {@link Interval} to test
	 * @param lowerBound the {@link LowerBound} of {@code T} type of the {@link Interval} to test
	 * @param upperBound the {@link UpperBound} of {@code T} type of the {@link Interval} to test
	 * <p>
	 * @return {@code true} if the {@link Interval} with the specified {@link LowerBound} and
	 *         {@link UpperBound} is non-empty, {@code false} otherwise
	 */
	public static <T extends Comparable<? super T>> boolean isNonEmpty(
			final LowerBound<T> lowerBound, final UpperBound<T> upperBound) {
		return !isEmpty(lowerBound, upperBound);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code this} contains the specified {@code T} object.
	 * <p>
	 * @param object the {@code T} object to test for membership (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} contains the specified {@code T} object, {@code false}
	 *         otherwise
	 */
	public boolean isInside(final T object) {
		return isInside(lowerBound, upperBound, object);
	}

	/**
	 * Tests whether the specified {@code T} object is inside the {@link Interval} with the
	 * specified {@link LowerBound} and {@link UpperBound}.
	 * <p>
	 * @param <T>        the self {@link Comparable} type of the {@link Interval} to test
	 * @param lowerBound the {@link LowerBound} of {@code T} type of the {@link Interval} to test
	 * @param upperBound the {@link UpperBound} of {@code T} type of the {@link Interval} to test
	 * @param object     the {@code T} object to test for membership (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code T} object is inside the {@link Interval} with
	 *         the specified {@link LowerBound} and {@link UpperBound}, {@code false} otherwise
	 */
	public static <T extends Comparable<? super T>> boolean isInside(final LowerBound<T> lowerBound,
			final UpperBound<T> upperBound, final T object) {
		return isNonEmpty(lowerBound, upperBound) && object != null &&
				(lowerBound.isInclusive ? Comparables.isGreaterOrEqualTo(object, lowerBound.value) :
						Comparables.isGreaterThan(object, lowerBound.value)) &&
				(upperBound.isInclusive ? Comparables.isLessOrEqualTo(object, upperBound.value) :
						Comparables.isLessThan(object, upperBound.value));
	}

	/**
	 * Tests whether {@code this} contains the specified {@link Interval}.
	 * <p>
	 * @param interval the {@link Interval} of {@code T} type to test for membership (may be
	 *                 {@code null})
	 * <p>
	 * @return {@code true} if {@code this} contains the specified {@link Interval}, {@code false}
	 *         otherwise
	 */
	public boolean isInside(final Interval<T> interval) {
		return isNonEmpty() &&
				Comparables.isLessOrEqualTo(lowerBound, interval.lowerBound) &&
				Comparables.isGreaterOrEqualTo(upperBound, interval.upperBound);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is valid.
	 * <p>
	 * @return {@code true} if {@code this} is valid, {@code false} otherwise
	 */
	public boolean isValid() {
		return isValid(lowerBound, upperBound);
	}

	/**
	 * Tests whether the {@link Interval} with the specified {@link LowerBound} and
	 * {@link UpperBound} is valid.
	 * <p>
	 * @param <T>        the self {@link Comparable} type of the {@link Interval} to test
	 * @param lowerBound the {@link LowerBound} of {@code T} type of the {@link Interval} to test
	 * @param upperBound the {@link UpperBound} of {@code T} type of the {@link Interval} to test
	 * <p>
	 * @return {@code true} if the {@link Interval} with the specified {@link LowerBound} and
	 *         {@link UpperBound} is valid, {@code false} otherwise
	 */
	public static <T extends Comparable<? super T>> boolean isValid(final LowerBound<T> lowerBound,
			final UpperBound<T> upperBound) {
		return lowerBound.value != null && upperBound.value != null &&
				Comparables.isLessOrEqualTo(lowerBound.value, upperBound.value);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code other} for order. Returns a negative integer, {@code 0} or
	 * a positive integer as {@code this} is less than, equal to or greater than {@code other} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@link Interval} of {@code T} type to compare against for order (may
	 *              be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code this} is less than,
	 *         equal to or greater than {@code other}
	 */
	@Override
	public int compareTo(final Interval<T> other) {
		// Check the arguments
		if (this == other) {
			return 0;
		}
		if (other == null) {
			return 1;
		}

		// Compare the intervals for order
		final int comparison = Comparables.compare(lowerBound, other.lowerBound);
		if (comparison != 0) {
			return comparison;
		}
		return Comparables.compare(upperBound, other.upperBound);
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
	public Interval<T> clone() {
		try {
			final Interval<T> clone = (Interval<T>) super.clone();
			clone.lowerBound = Objects.clone(lowerBound);
			clone.upperBound = Objects.clone(upperBound);
			return clone;
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
				Objects.equals(upperBound, otherInterval.upperBound);
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
		return Objects.hashCode(serialVersionUID, lowerBound, upperBound);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Strings.join(lowerBound, Arrays.DELIMITER, upperBound);
	}
}
