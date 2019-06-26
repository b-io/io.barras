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

public class ComparableQuintuple<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>>
		extends Quintuple<T1, T2, T3, T4, T5> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 980509932686150429L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public ComparableQuintuple() {
		super();
	}

	public ComparableQuintuple(final T1 first, final T2 second, final T3 third, final T4 fourth,
			final T5 fifth) {
		super(first, second, third, fourth, fifth);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code quintuple} for order. Returns a negative integer, zero or a
	 * positive integer as {@code this} is less than, equal to or greater than {@code quintuple}.
	 * <p>
	 * @param quintuple the {@link ComparablePair} of type {@code T1}, {@code T2}, {@code T3},
	 *                  {@code T4} and {@code T5} to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code this} is less than, equal to
	 *         or greater than {@code quintuple}
	 * <p>
	 * @throws NullPointerException if {@code quintuple} is {@code null}
	 */
	public int compareTo(final ComparableQuintuple<T1, T2, T3, T4, T5> quintuple) {
		int comparison = first.compareTo(quintuple.first);
		if (comparison != 0) {
			return comparison;
		}
		comparison = second.compareTo(quintuple.second);
		if (comparison != 0) {
			return comparison;
		}
		comparison = third.compareTo(quintuple.third);
		if (comparison != 0) {
			return comparison;
		}
		comparison = fourth.compareTo(quintuple.fourth);
		if (comparison != 0) {
			return comparison;
		}
		return fifth.compareTo(quintuple.fifth);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public ComparableQuintuple<T1, T2, T3, T4, T5> clone() {
		try {
			final ComparableQuintuple<T1, T2, T3, T4, T5> clone = (ComparableQuintuple<T1, T2, T3, T4, T5>) super.clone();
			clone.first = Objects.clone(first);
			clone.second = Objects.clone(second);
			clone.third = Objects.clone(third);
			clone.fourth = Objects.clone(fourth);
			clone.fifth = Objects.clone(fifth);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new RuntimeException(Strings.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof ComparableQuintuple)) {
			return false;
		}
		final ComparableQuintuple<?, ?, ?, ?, ?> otherQuintuple = (ComparableQuintuple) other;
		return Objects.equals(first, otherQuintuple.first) &&
				Objects.equals(second, otherQuintuple.second) &&
				Objects.equals(third, otherQuintuple.third) &&
				Objects.equals(fourth, otherQuintuple.fourth) &&
				Objects.equals(fifth, otherQuintuple.fifth);
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
		return Objects.hashCode(serialVersionUID, first, second, third, fourth, fifth);
	}
}
