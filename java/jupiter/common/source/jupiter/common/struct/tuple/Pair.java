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

import jupiter.common.math.ITuple;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class Pair<T1, T2>
		implements ICloneable<Pair<T1, T2>>, ITuple, Serializable {

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
	 * The {@code T1} component.
	 */
	protected T1 first;
	/**
	 * The {@code T2} component.
	 */
	protected T2 second;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Pair}.
	 */
	public Pair() {
	}

	/**
	 * Constructs a {@link Pair} with the specified {@code T1} and {@code T2} components.
	 * <p>
	 * @param first  the {@code T1} component
	 * @param second the {@code T2} component
	 */
	public Pair(final T1 first, final T2 second) {
		this.first = first;
		this.second = second;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code T1} component.
	 * <p>
	 * @return the {@code T1} component
	 */
	public T1 getFirst() {
		return first;
	}

	/**
	 * Returns the {@code T2} component.
	 * <p>
	 * @return the {@code T2} component
	 */
	public T2 getSecond() {
		return second;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@code T1} component.
	 * <p>
	 * @param first a {@code T1} object
	 */
	public void setFirst(final T1 first) {
		this.first = first;
	}

	/**
	 * Sets the {@code T2} component.
	 * <p>
	 * @param second a {@code T2} object
	 */
	public void setSecond(final T2 second) {
		this.second = second;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the size of {@code this}.
	 * <p>
	 * @return the size of {@code this}
	 */
	@Override
	public int size() {
		return 2;
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
	public Pair<T1, T2> clone() {
		try {
			final Pair<T1, T2> clone = (Pair<T1, T2>) super.clone();
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
	 * @throws ClassCastException   if the {@code other} type prevents it from being compared to
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
		if (other == null || !(other instanceof Pair)) {
			return false;
		}
		final Pair<?, ?> otherPair = (Pair<?, ?>) other;
		return Objects.equals(first, otherPair.first) && Objects.equals(second, otherPair.second);
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
