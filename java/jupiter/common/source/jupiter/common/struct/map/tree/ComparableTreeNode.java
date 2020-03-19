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
package jupiter.common.struct.map.tree;

import java.io.Serializable;
import java.util.Map.Entry;

import jupiter.common.math.Comparables;
import jupiter.common.test.Arguments;
import jupiter.common.util.Maps;
import jupiter.common.util.Objects;

public class ComparableTreeNode<K extends Comparable<? super K>, V>
		implements Comparable<Entry<K, V>>, Entry<K, V>, Serializable {

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
	 * The {@code K} key.
	 */
	protected K key;
	/**
	 * The {@code V} value.
	 */
	protected V value;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ComparableTreeNode} with the specified {@code K} key and {@code V} value.
	 * <p>
	 * @param key   the {@code K} key
	 * @param value the {@code V} value (may be {@code null})
	 */
	public ComparableTreeNode(final K key, final V value) {
		// Check the arguments
		Arguments.requireNonNull(key, "key");

		// Set the attributes
		this.key = key;
		this.value = value;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code K} key.
	 * <p>
	 * @return the {@code K} key
	 */
	public K getKey() {
		return key;
	}

	/**
	 * Returns the {@code V} value.
	 * <p>
	 * @return the {@code V} value
	 */
	public V getValue() {
		return value;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@code V} value.
	 * <p>
	 * @param value a {@code V} value (may be {@code null})
	 * <p>
	 * @return the previous associated {@code V} value
	 */
	public V setValue(final V value) {
		final V previousValue = this.value;
		this.value = value;
		return previousValue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code other} for order. Returns a negative integer, {@code 0} or
	 * a positive integer as {@code this} is less than, equal to or greater than {@code other} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@link Entry} of {@code K} and {@code V} types to compare against for
	 *              order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code this} is less than,
	 *         equal to or greater than {@code other}
	 */
	public int compareTo(final Entry<K, V> other) {
		return Comparables.compare(key, other.getKey());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
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
		if (other == null || !(other instanceof Entry)) {
			return false;
		}
		final Entry<?, ?> otherEntry = (Entry<?, ?>) other;
		return Objects.equals(key, otherEntry.getKey()) &&
				Objects.equals(value, otherEntry.getValue());
	}

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
		return Objects.hashCode(serialVersionUID, key, value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Maps.toString(this);
	}
}
