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

import jupiter.common.struct.tuple.Pair;
import jupiter.common.util.Arrays;
import jupiter.common.util.Strings;

public class Interval<T extends Comparable<T>>
		implements IGroup<T> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected T lowerBound, upperBound;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link Interval}.
	 */
	public Interval() {
		lowerBound = null;
		upperBound = null;
	}

	/**
	 * Constructs an {@link Interval} with the specified lower and upper bounds.
	 * <p>
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 */
	public Interval(final T lowerBound, final T upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	/**
	 * Constructs an {@link Interval} with the specified {@link Pair} of lower and upper bounds.
	 * <p>
	 * @param pair the {@link Pair} of lower and upper bounds
	 */
	public Interval(final Pair<T, T> pair) {
		this.lowerBound = pair.getFirst();
		this.upperBound = pair.getSecond();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the lower bound.
	 * <p>
	 * @return the lower bound
	 */
	public T getLowerBound() {
		return lowerBound;
	}

	/**
	 * Returns the upper bound.
	 * <p>
	 * @return the upper bound
	 */
	public T getUpperBound() {
		return upperBound;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the lower bound.
	 * <p>
	 * @param lowerBound a {@code T} object
	 */
	public void setLowerBound(final T lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * Sets the upper bound.
	 * <p>
	 * @param upperBound a {@code T} object
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
	 * Tests whether {@code this} contains the value.
	 * <p>
	 * @param value the value of type {@code T} to test for presence
	 * <p>
	 * @return {@code true} if {@code this} contains the value, {@code false} otherwise
	 */
	public boolean isInside(final T value) {
		return value.compareTo(lowerBound) >= 0 && value.compareTo(upperBound) <= 0;
	}

	/**
	 * Tests whether {@code this} is valid.
	 * <p>
	 * @return {@code true} if {@code this} is valid, {@code false} otherwise
	 */
	public boolean isValid() {
		return !isEmpty() && lowerBound.compareTo(upperBound) < 0;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Strings.bracketize(Arrays.join(lowerBound, upperBound));
	}
}
