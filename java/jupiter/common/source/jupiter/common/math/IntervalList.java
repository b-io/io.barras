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

import java.util.LinkedList;
import java.util.List;

import jupiter.common.util.Strings;

public class IntervalList<T extends Comparable<T>>
		implements IGroup<T> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected List<Interval<T>> intervals;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link IntervalList}.
	 */
	public IntervalList() {
		intervals = new LinkedList<Interval<T>>();
	}

	/**
	 * Constructs an {@link IntervalList} with the specified {@link List} of {@link Interval}.
	 * <p>
	 * @param intervals a {@link List} of {@link Interval}
	 */
	public IntervalList(final List<Interval<T>> intervals) {
		this.intervals = intervals;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link List} of {@link Interval}.
	 * <p>
	 * @return the {@link List} of {@link Interval}
	 */
	public List<Interval<T>> getIntervals() {
		return intervals;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the intervals.
	 * <p>
	 * @param intervals a {@link List} of {@link Interval}
	 */
	public void setIntervals(final List<Interval<T>> intervals) {
		this.intervals = intervals;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GROUP
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if {@code this} is empty, {@code false} otherwise.
	 * <p>
	 * @return {@code true} if {@code this} is empty, {@code false} otherwise
	 */
	public boolean isEmpty() {
		return intervals.isEmpty();
	}

	/**
	 * Returns {@code true} if {@code this} contains the value, {@code false} otherwise.
	 * <p>
	 * @param value the value to test for presence
	 * <p>
	 * @return {@code true} if {@code this} contains the value, {@code false} otherwise
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
	 * Returns {@code true} if {@code this} is valid, {@code false} otherwise.
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

	@Override
	public String toString() {
		return Strings.toString(intervals);
	}
}
