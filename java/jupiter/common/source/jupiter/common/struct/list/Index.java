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
package jupiter.common.struct.list;

import java.io.Serializable;
import java.util.Comparator;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Integers;
import jupiter.common.util.Objects;

/**
 * {@link Index} is the index of the {@code T} token.
 * <p>
 * @param <T> the type of the token to index
 */
public class Index<T>
		implements Comparable<Index<T>>, ICloneable<Index<T>>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Comparator<Index<?>> COMPARATOR = new Comparator<Index<?>>() {
		@Override
		public int compare(final Index<?> a, final Index<?> b) {
			return Index.compare(a, b);
		}
	};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The index of the {@code T} token.
	 */
	protected int index;
	/**
	 * The {@code T} token.
	 */
	protected T token;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link Index} with the specified index and {@code T} token.
	 * <p>
	 * @param index the index of the {@code T} token
	 * @param token the {@code T} token
	 */
	public Index(final int index, final T token) {
		this.index = index;
		this.token = token;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the {@code T} token.
	 * <p>
	 * @return the index of the {@code T} token
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the {@code T} token.
	 * <p>
	 * @return the {@code T} token
	 */
	public T getToken() {
		return token;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code other} for order. Returns a negative integer, {@code 0} or
	 * a positive integer as {@code this} is less than, equal to or greater than {@code other} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@link Index} of {@code T} type to compare against for order (may be
	 *              {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code this} is less than,
	 *         equal to or greater than {@code other}
	 */
	public int compareTo(final Index<T> other) {
		return compare(this, other);
	}

	/**
	 * Compares the specified {@link Index} for order. Returns a negative integer, {@code 0} or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@link Index} to compare for order (may be {@code null})
	 * @param b the other {@link Index} to compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final Index<?> a, final Index<?> b) {
		return a == b ? 0 : a == null ? -1 : b == null ? 1 :
				Integers.compare(a.getIndex(), b.getIndex());
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
	@SuppressWarnings({"cast", "unchecked"})
	public Index<T> clone() {
		try {
			final Index<T> clone = (Index<T>) super.clone();
			clone.token = Objects.clone(token);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
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
		if (other == null || !(other instanceof Index)) {
			return false;
		}
		final Index<?> otherIndex = (Index<?>) other;
		return Objects.equals(index, otherIndex.index) && Objects.equals(token, otherIndex.token);
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
		return Objects.hashCode(serialVersionUID, index, token);
	}
}
