/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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

import jupiter.common.math.Comparables;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

public class ComparableQuintuple<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>>
		extends Quintuple<T1, T2, T3, T4, T5>
		implements Comparable<ComparableQuintuple<T1, T2, T3, T4, T5>> {

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
	 * Constructs a {@link ComparableQuintuple}.
	 */
	public ComparableQuintuple() {
		super();
	}

	/**
	 * Constructs a {@link ComparableQuintuple} with the specified {@code T1}, {@code T2},
	 * {@code T3}, {@code T4} and {@code T5} components.
	 * <p>
	 * @param first  the {@code T1} component
	 * @param second the {@code T2} component
	 * @param third  the {@code T3} component
	 * @param fourth the {@code T4} component
	 * @param fifth  the {@code T5} component
	 */
	public ComparableQuintuple(final T1 first, final T2 second, final T3 third, final T4 fourth,
			final T5 fifth) {
		super(first, second, third, fourth, fifth);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code other} for order. Returns a negative integer, {@code 0} or
	 * a positive integer as {@code this} is less than, equal to or greater than {@code other} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@link ComparablePair} of {@code T1}, {@code T2}, {@code T3},
	 *              {@code T4} and {@code T5} component types to compare against for order (may be
	 *              {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code this} is less than,
	 *         equal to or greater than {@code other}
	 */
	public int compareTo(final ComparableQuintuple<T1, T2, T3, T4, T5> other) {
		// Check the arguments
		if (this == other) {
			return 0;
		}
		if (other == null) {
			return 1;
		}

		// Compare the quintuples for order
		int comparison = Comparables.compare(first, other.first);
		if (comparison != 0) {
			return comparison;
		}
		comparison = Comparables.compare(second, other.second);
		if (comparison != 0) {
			return comparison;
		}
		comparison = Comparables.compare(third, other.third);
		if (comparison != 0) {
			return comparison;
		}
		comparison = Comparables.compare(fourth, other.fourth);
		if (comparison != 0) {
			return comparison;
		}
		return Comparables.compare(fifth, other.fifth);
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
	public ComparableQuintuple<T1, T2, T3, T4, T5> clone() {
		final ComparableQuintuple<T1, T2, T3, T4, T5> clone = (ComparableQuintuple<T1, T2, T3, T4, T5>) super.clone();
		clone.first = Objects.clone(first);
		clone.second = Objects.clone(second);
		clone.third = Objects.clone(third);
		clone.fourth = Objects.clone(fourth);
		clone.fifth = Objects.clone(fifth);
		return clone;
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
		if (other == null || !(other instanceof ComparableQuintuple)) {
			return false;
		}
		final ComparableQuintuple<?, ?, ?, ?, ?> otherComparableQuintuple = (ComparableQuintuple<?, ?, ?, ?, ?>) other;
		return Objects.equals(first, otherComparableQuintuple.first) &&
				Objects.equals(second, otherComparableQuintuple.second) &&
				Objects.equals(third, otherComparableQuintuple.third) &&
				Objects.equals(fourth, otherComparableQuintuple.fourth) &&
				Objects.equals(fifth, otherComparableQuintuple.fifth);
	}

	//////////////////////////////////////////////

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
		return Objects.hashCode(serialVersionUID, first, second, third, fourth, fifth);
	}
}
