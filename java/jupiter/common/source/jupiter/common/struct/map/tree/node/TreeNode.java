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
package jupiter.common.struct.map.tree.node;

import java.io.Serializable;
import java.util.Map.Entry;

import jupiter.common.test.Arguments;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class TreeNode<K extends Comparable<K>, V>
		implements Comparable<Entry<K, V>>, Entry<K, V>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1216540400306903197L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The key of type {@code K}.
	 */
	public K key;
	/**
	 * The value of type {@code V}.
	 */
	public V value;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link TreeNode} with the specified key and value.
	 * <p>
	 * @param key   a key of type {@code K}
	 * @param value a value of type {@code V}
	 */
	public TreeNode(final K key, final V value) {
		// Check the arguments
		Arguments.requireNonNull(key, "The specified key is null");

		// Set the attributes
		this.key = key;
		this.value = value;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the key of type {@code K}.
	 * <p>
	 * @return the key of type {@code K}
	 */
	public K getKey() {
		return key;
	}

	/**
	 * Returns the value of type {@code V}.
	 * <p>
	 * @return the value of type {@code V}
	 */
	public V getValue() {
		return value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the value and returns the previous associated value of type {@code V}.
	 * <p>
	 * @param value a {@code V} object
	 * <p>
	 * @return the previous associated value of type {@code V}
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
	 * Compares {@code this} with {@code entry} for order. Returns a negative integer, zero or a
	 * positive integer as {@code this} is less than, equal to or greater than {@code entry}.
	 * <p>
	 * @param entry the {@link java.util.Map.Entry} of type {@code K} and {@code V} to compare
	 *              against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code this} is less than, equal to
	 *         or greater than {@code entry}
	 * <p>
	 * @throws NullPointerException if {@code entry} is {@code null}
	 */
	public int compareTo(final Entry<K, V> entry) {
		return key.compareTo(entry.getKey());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the {@link Object} to compare against for equality
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
		if (other == null || !(other instanceof Entry)) {
			return false;
		}
		final Entry<?, ?> entry = (Entry<?, ?>) other;
		return Objects.equals(key, entry.getKey()) && Objects.equals(value, entry.getValue());
	}

	/**
	 * Returns the hash code {@code int} value for {@code this}.
	 * <p>
	 * @return the hash code {@code int} value for {@code this}
	 *
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, key, value);
	}

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Strings.bracketize(key + " => " + value);
	}
}
