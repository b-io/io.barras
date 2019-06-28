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

	/**
	 * The {@code T} lower and upper bounds.
	 */
	protected T lowerBound, upperBound;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link Interval} of type {@code T}.
	 */
	public Interval() {
		lowerBound = null;
		upperBound = null;
	}

	/**
	 * Constructs an {@link Interval} with the specified {@code T} lower and upper bounds.
	 * <p>
	 * @param lowerBound the {@code T} lower bound
	 * @param upperBound the {@code T} upper bound
	 */
	public Interval(final T lowerBound, final T upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	/**
	 * Constructs an {@link Interval} with the specified lower and upper bounds {@link Pair}.
	 * <p>
	 * @param pair the lower and upper bounds {@link Pair} of type {@code T}
	 */
	public Interval(final Pair<T, T> pair) {
		this.lowerBound = pair.getFirst();
		this.upperBound = pair.getSecond();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
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
	 * Tests whether {@code this} contains the value {@code T}.
	 * <p>
	 * @param value the value {@code T} to test for presence
	 * <p>
	 * @return {@code true} if {@code this} contains the value {@code T}, {@code false} otherwise
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
