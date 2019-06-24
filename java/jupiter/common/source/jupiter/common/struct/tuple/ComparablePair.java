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
package jupiter.common.struct.tuple;

import java.io.Serializable;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;

public class ComparablePair<T1 extends Comparable<T1>, T2 extends Comparable<T2>>
		implements ICloneable<ComparablePair<T1, T2>>, Comparable<ComparablePair<T1, T2>>,
		Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -3899643258386029273L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The first component.
	 */
	protected T1 first;
	/**
	 * The second component.
	 */
	protected T2 second;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public ComparablePair() {
	}

	public ComparablePair(final T1 first, final T2 second) {
		this.first = first;
		this.second = second;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PAIR
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the first component.
	 * <p>
	 * @return the first component
	 */
	public T1 getFirst() {
		return first;
	}

	/**
	 * Returns the second component.
	 * <p>
	 * @return the second component
	 */
	public T2 getSecond() {
		return second;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the first component.
	 * <p>
	 * @param first a {@code T1} object
	 */
	public void setFirst(final T1 first) {
		this.first = first;
	}

	/**
	 * Sets the second component.
	 * <p>
	 * @param second a {@code T2} object
	 */
	public void setSecond(final T2 second) {
		this.second = second;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code pair} for order. Returns a negative integer, zero or a
	 * positive integer as {@code this} is less than, equal to or greater than {@code pair}.
	 * <p>
	 * @param pair the {@link ComparablePair} of type {@code T1} and {@code T2} to compare against
	 *             for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code this} is less than, equal to
	 *         or greater than {@code pair}
	 * <p>
	 * @throws NullPointerException if {@code pair} is {@code null}
	 */
	public int compareTo(final ComparablePair<T1, T2> pair) {
		final int comparison = first.compareTo(pair.first);
		if (comparison != 0) {
			return comparison;
		}
		return second.compareTo(pair.second);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@SuppressWarnings("unchecked")
	public ComparablePair<T1, T2> clone() {
		try {
			final ComparablePair<T1, T2> clone = (ComparablePair<T1, T2>) super.clone();
			clone.first = Objects.clone(first);
			clone.second = Objects.clone(second);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new AssertionError(ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof ComparablePair)) {
			return false;
		}
		final ComparablePair<?, ?> otherComparablePair = (ComparablePair) other;
		return Objects.equals(first, otherComparablePair.first) &&
				Objects.equals(second, otherComparablePair.second);
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
		return Objects.hashCode(serialVersionUID, first, second);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Arrays.toString(first, second);
	}
}
