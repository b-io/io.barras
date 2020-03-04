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
package jupiter.common.struct.tuple;

import jupiter.common.math.Comparables;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class ComparablePair<T1 extends Comparable<T1>, T2 extends Comparable<T2>>
		extends Pair<T1, T2>
		implements Comparable<ComparablePair<T1, T2>> {

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
	 * Constructs a {@link ComparablePair}.
	 */
	public ComparablePair() {
		super();
	}

	/**
	 * Constructs a {@link ComparablePair} with the specified {@code T1} and {@code T2} components.
	 * <p>
	 * @param first  the {@code T1} component
	 * @param second the {@code T2} component
	 */
	public ComparablePair(final T1 first, final T2 second) {
		super(first, second);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code other} for order. Returns a negative integer, {@code 0} or
	 * a positive integer as {@code this} is less than, equal to or greater than {@code other} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@link ComparablePair} of {@code T1} and {@code T2} component types to
	 *              compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code this} is less than,
	 *         equal to or greater than {@code other}
	 */
	public int compareTo(final ComparablePair<T1, T2> other) {
		// Check the arguments
		if (this == other) {
			return 0;
		}
		if (other == null) {
			return 1;
		}

		// Compare the pairs for order
		final int comparison = Comparables.compare(first, other.first);
		if (comparison != 0) {
			return comparison;
		}
		return Comparables.compare(second, other.second);
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
	public ComparablePair<T1, T2> clone() {
		try {
			final ComparablePair<T1, T2> clone = (ComparablePair<T1, T2>) super.clone();
			clone.first = Objects.clone(first);
			clone.second = Objects.clone(second);
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
	 * <p>
	 * @throws ClassCastException if the {@code other} type prevents it from being compared to
	 *                            {@code this}
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof ComparablePair)) {
			return false;
		}
		final ComparablePair<?, ?> otherComparablePair = (ComparablePair<?, ?>) other;
		return Objects.equals(first, otherComparablePair.first) &&
				Objects.equals(second, otherComparablePair.second);
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
		return Objects.hashCode(serialVersionUID, first, second);
	}
}
