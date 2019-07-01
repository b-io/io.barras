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
import java.util.LinkedList;
import java.util.List;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Collections;
import jupiter.common.util.Objects;

import jupiter.common.util.Strings;

/**
 * {@link IntervalList} is an {@link IGroup} of type {@code T} containing a {@link List} of
 * {@link Interval}.
 * <p>
 * @param <T> the self {@link Comparable} type of the {@link IntervalList}
 */
public class IntervalList<T extends Comparable<T>>
		implements ICloneable<IntervalList<T>>, IGroup<T>, Serializable {

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
	 * The {@link List} of {@link Interval} of type {@code T}.
	 */
	protected List<Interval<T>> intervals;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link IntervalList} of type {@code T}.
	 */
	public IntervalList() {
		intervals = new LinkedList<Interval<T>>();
	}

	/**
	 * Constructs an {@link IntervalList} of type {@code T} with the specified {@link List} of
	 * {@link Interval}.
	 * <p>
	 * @param intervals a {@link List} of {@link Interval} of type {@code T}
	 */
	public IntervalList(final List<Interval<T>> intervals) {
		this.intervals = intervals;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link List} of {@link Interval} of type {@code T}.
	 * <p>
	 * @return the {@link List} of {@link Interval} of type {@code T}
	 */
	public List<Interval<T>> getIntervals() {
		return intervals;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@link List} of {@link Interval} of type {@code T}.
	 * <p>
	 * @param intervals a {@link List} of {@link Interval} of type {@code T}
	 */
	public void setIntervals(final List<Interval<T>> intervals) {
		this.intervals = intervals;
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
		return intervals.isEmpty();
	}

	/**
	 * Tests whether {@code this} contains the value {@code T}.
	 * <p>
	 * @param value the value {@code T} to test for presence
	 * <p>
	 * @return {@code true} if {@code this} contains the value {@code T}, {@code false} otherwise
	 */
	public boolean isInside(final T value) {
		for (final Interval<T> interval : intervals) {
			if (interval.isInside(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether {@code this} is valid.
	 * <p>
	 * @return {@code true} if {@code this} is valid, {@code false} otherwise
	 */
	public boolean isValid() {
		for (final Interval<T> interval : intervals) {
			if (!interval.isValid()) {
				return false;
			}
		}
		return !isEmpty();
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
	public IntervalList<T> clone() {
		try {
			return (IntervalList<T>) super.clone();
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
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof IntervalList)) {
			return false;
		}
		final IntervalList<?> otherIntervalList = (IntervalList<?>) other;
		return Objects.equals(intervals, otherIntervalList.intervals);
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
	@SuppressWarnings("unchecked")
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, intervals);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Collections.toString(intervals);
	}
}
