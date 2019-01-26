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
package jupiter.common.struct.map.tree;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.map.tree.node.TreeNode;
import jupiter.common.test.Arguments;

public abstract class TreeMap<K extends Comparable<K>, V, N extends TreeNode<K, V>>
		extends AbstractMap<K, V>
		implements ICloneable<TreeMap<K, V, N>>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -7806719664662968650L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The root.
	 */
	protected N root = null;
	/**
	 * The number of nodes (key-value mappings).
	 */
	protected int size = 0;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected TreeMap() {
		super();
	}

	protected TreeMap(final Map<? extends K, ? extends V> map) {
		super();
		putAll(map);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the value associated with the specified key, or {@code null} if it is not present.
	 * <p>
	 * @param key the key of the value to get
	 * <p>
	 * @return the value associated with the specified key, or {@code null} if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the current keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	public V get(final Object key) {
		// Check the arguments
		Arguments.requireNonNull(key, "The specified key is null");

		// Get the value
		final N node = getNode(key);
		return node == null ? null : node.value;
	}

	/**
	 * Returns the root {@link Map.Entry}.
	 * <p>
	 * @return the root {@link Map.Entry}
	 */
	public Entry<K, V> getRootEntry() {
		return root;
	}

	/**
	 * Returns the node of the specified key, or {@code null} if it is not present.
	 * <p>
	 * @param key the key of the node to get
	 * <p>
	 * @return the node of the specified key, or {@code null} if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the current keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@SuppressWarnings("unchecked")
	protected N getNode(final Object key) {
		// Check the arguments
		Arguments.requireNonNull(key, "The specified key is null");

		// Get the node
		return getNode((Comparable<? super K>) key);
	}

	/**
	 * Returns the node of the specified key, or {@code null} if it is not present.
	 * <p>
	 * @param key the key of the node to get
	 * <p>
	 * @return the node of the specified key, or {@code null} if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the current keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	protected abstract N getNode(final Comparable<? super K> key);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Puts all the key-value mappings of the specified map to {@code this} replacing any entries
	 * with identical keys.
	 * <p>
	 * @param map a {@link Map}
	 * <p>
	 * @throws ClassCastException   if the class of a key or value in {@code map} prevents it from
	 *                              being stored in {@code this}
	 * @throws NullPointerException if {@code map} is {@code null} or {@code map} contains a null
	 *                              key
	 */
	@Override
	public void putAll(final Map<? extends K, ? extends V> map) {
		super.putAll(map);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints {@code this}.
	 */
	public abstract void print();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public abstract TreeMap<K, V, N> clone();
}
