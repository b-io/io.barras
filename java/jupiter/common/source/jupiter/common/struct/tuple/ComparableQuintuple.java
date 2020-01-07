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

import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class ComparableQuintuple<E1 extends Comparable<E1>, E2 extends Comparable<E2>, E3 extends Comparable<E3>, E4 extends Comparable<E4>, E5 extends Comparable<E5>>
		extends Quintuple<E1, E2, E3, E4, E5>
		implements Comparable<ComparableQuintuple<E1, E2, E3, E4, E5>> {

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
	 * Constructs a {@link ComparableQuintuple} with the specified {@code E1}, {@code E2},
	 * {@code E3}, {@code E4} and {@code E5} components.
	 * <p>
	 * @param first  the {@code E1} component
	 * @param second the {@code E2} component
	 * @param third  the {@code E3} component
	 * @param fourth the {@code E4} component
	 * @param fifth  the {@code E5} component
	 */
	public ComparableQuintuple(final E1 first, final E2 second, final E3 third, final E4 fourth,
			final E5 fifth) {
		super(first, second, third, fourth, fifth);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code other} for order. Returns a negative integer, zero or a
	 * positive integer as {@code this} is less than, equal to or greater than {@code other}.
	 * <p>
	 * @param other the other {@link ComparablePair} of type {@code E1}, {@code E2}, {@code E3},
	 *              {@code E4} and {@code E5} to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code this} is less than, equal to
	 *         or greater than {@code other}
	 * <p>
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	public int compareTo(final ComparableQuintuple<E1, E2, E3, E4, E5> other) {
		int comparison = first.compareTo(other.first);
		if (comparison != 0) {
			return comparison;
		}
		comparison = second.compareTo(other.second);
		if (comparison != 0) {
			return comparison;
		}
		comparison = third.compareTo(other.third);
		if (comparison != 0) {
			return comparison;
		}
		comparison = fourth.compareTo(other.fourth);
		if (comparison != 0) {
			return comparison;
		}
		return fifth.compareTo(other.fifth);
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
	public ComparableQuintuple<E1, E2, E3, E4, E5> clone() {
		try {
			final ComparableQuintuple<E1, E2, E3, E4, E5> clone = (ComparableQuintuple<E1, E2, E3, E4, E5>) super.clone();
			clone.first = Objects.clone(first);
			clone.second = Objects.clone(second);
			clone.third = Objects.clone(third);
			clone.fourth = Objects.clone(fourth);
			clone.fifth = Objects.clone(fifth);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
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
		if (other == null || !(other instanceof ComparableQuintuple)) {
			return false;
		}
		final ComparableQuintuple<?, ?, ?, ?, ?> otherQuintuple = (ComparableQuintuple<?, ?, ?, ?, ?>) other;
		return Objects.equals(first, otherQuintuple.first) &&
				Objects.equals(second, otherQuintuple.second) &&
				Objects.equals(third, otherQuintuple.third) &&
				Objects.equals(fourth, otherQuintuple.fourth) &&
				Objects.equals(fifth, otherQuintuple.fifth);
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
		return Objects.hashCode(serialVersionUID, first, second, third, fourth, fifth);
	}
}
