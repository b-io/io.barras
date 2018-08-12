/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io>
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

	public K key;
	public V value;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link TreeNode} with the specified key and value.
	 * <p>
	 * @param key   the key of the node
	 * @param value the value of the node
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
	 * Returns the key.
	 * <p>
	 * @return the key
	 */
	public K getKey() {
		return key;
	}

	/**
	 * Returns the value associated with the key.
	 * <p>
	 * @return the value associated with the key
	 */
	public V getValue() {
		return value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the value associated with the key and returns the previous associated value.
	 * <p>
	 * @param value a {@code V} object
	 * <p>
	 * @return the previous associated value
	 */
	public V setValue(final V value) {
		final V previousValue = this.value;
		this.value = value;
		return previousValue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int compareTo(final Entry<K, V> entry) {
		return key.compareTo(entry.getKey());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

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

	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, key, value);
	}

	@Override
	public String toString() {
		return Strings.bracketize(key + " => " + value);
	}
}
