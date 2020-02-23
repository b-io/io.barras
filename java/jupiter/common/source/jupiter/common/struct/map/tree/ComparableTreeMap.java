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
import java.util.AbstractMap;
import java.util.Map;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.test.Arguments;
import jupiter.common.util.Maps;

/**
 * {@link ComparableTreeMap} is a light sorted {@link Map} implementation based on a tree.
 * <p>
 * @param <K> the self {@link Comparable} key type of the {@link ComparableTreeMap}
 * @param <V> the value type of the {@link ComparableTreeMap}
 * @param <N> the {@link ComparableTreeNode} type of the {@link ComparableTreeMap}
 */
public abstract class ComparableTreeMap<K extends Comparable<K>, V, N extends ComparableTreeNode<K, V>>
		extends AbstractMap<K, V>
		implements ICloneable<ComparableTreeMap<K, V, N>>, Serializable {

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

	/**
	 * Constructs a {@link ComparableTreeMap} of types {@code K}, {@code V} and {@code N}.
	 */
	protected ComparableTreeMap() {
		super();
	}

	/**
	 * Constructs a {@link ComparableTreeMap} of types {@code K}, {@code V} and {@code N} loaded
	 * from the specified {@link Map} containing the key-value mappings.
	 * <p>
	 * @param map the {@link Map} containing the {@code K} and {@code V} key-value mappings to load
	 */
	protected ComparableTreeMap(final Map<? extends K, ? extends V> map) {
		super();
		putAll(map);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code V} value associated to the specified key {@link Object}, or {@code null}
	 * if it is not present.
	 * <p>
	 * @param key the key {@link Object} of the {@code V} value to get
	 * <p>
	 * @return the value associated to the specified key {@link Object}, or {@code null} if it is
	 *         not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the current keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	public V get(final Object key) {
		// Check the arguments
		Arguments.requireNotNull(key, "key");

		// Get the value
		final N node = getNode(key);
		return node != null ? node.value : null;
	}

	/**
	 * Returns the {@code V} value associated to the specified key, or the specified default
	 * {@code V} value if it is not present.
	 * <p>
	 * @param key          the key {@link Object} of the {@code V} value to get
	 * @param defaultValue the default {@code V} value (may be {@code null})
	 * <p>
	 * @return the {@code V} value associated to the specified key, or the specified default
	 *         {@code V} value if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the current keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public V getOrDefault(final Object key, final V defaultValue) {
		// Check the arguments
		Arguments.requireNotNull(key, "key");

		// Get the value associated to the key or the default value if it is not present
		return Maps.getOrDefault(this, key, defaultValue);
	}

	/**
	 * Returns all the {@code V} values associated to the specified keys in an {@link ExtendedList}.
	 * <p>
	 * @param keys the array of key {@link Object} of the {@code V} values to get
	 * <p>
	 * @return all the {@code V} values associated to the specified keys in an {@link ExtendedList}
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared with the current keys
	 * @throws NullPointerException if any {@code keys} are {@code null}
	 */
	public ExtendedList<V> getAll(final Object[] keys) {
		// Check the arguments
		Arguments.requireNotNull(keys, "keys");

		// Get the values associated to the keys
		return Maps.getAll(this, keys);
	}

	/**
	 * Returns all the {@code V} values associated to the specified keys or the specified default
	 * {@code V} value for those that are not present in an {@link ExtendedList}.
	 * <p>
	 * @param keys         the array of key {@link Object} of the {@code V} values to get
	 * @param defaultValue the default {@code V} value (may be {@code null})
	 * <p>
	 * @return all the {@code V} values associated to the specified keys or the specified default
	 *         {@code V} value for those that are not present in an {@link ExtendedList}
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared with the current keys
	 * @throws NullPointerException if any {@code keys} are {@code null}
	 */
	public ExtendedList<V> getAll(final Object[] keys, final V defaultValue) {
		// Check the arguments
		Arguments.requireNotNull(keys, "keys");

		// Get the values associated to the keys or the default value for those that are not present
		return Maps.getAll(this, keys, defaultValue);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the root {@link Entry} of types {@code K} and {@code V}.
	 * <p>
	 * @return the root {@link Entry} of types {@code K} and {@code V}
	 */
	public Entry<K, V> getRootEntry() {
		return root;
	}

	/**
	 * Returns the {@code N} node of the specified key {@link Object}, or {@code null} if it is not
	 * present.
	 * <p>
	 * @param key the key {@link Object} of the {@code N} node to get
	 * <p>
	 * @return the {@code N} node of the specified key {@link Object}, or {@code null} if it is not
	 *         present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the current keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@SuppressWarnings("unchecked")
	protected N getNode(final Object key) {
		// Check the arguments
		Arguments.requireNotNull(key, "key");

		// Get the node
		return findNode((Comparable<? super K>) key);
	}

	/**
	 * Returns the {@code N} node associated to the specified key {@link Comparable}, or
	 * {@code null} if it is not present.
	 * <p>
	 * @param keyComparable the key {@link Comparable} of supertype {@code K} to find
	 * <p>
	 * @return the {@code N} node associated to the specified key {@link Comparable}, or
	 *         {@code null} if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the current keys
	 * @throws NullPointerException if {@code keyComparable} is {@code null}
	 */
	protected abstract N findNode(final Comparable<? super K> keyComparable);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Puts all the key-value mappings of the specified map to {@code this} replacing any entries
	 * with identical keys.
	 * <p>
	 * @param map the {@link Map} containing the {@code K} and {@code V} key-value mappings to put
	 * <p>
	 * @throws ClassCastException   if the {@code map} type prevents it from being stored in
	 *                              {@code this}
	 * @throws NullPointerException if {@code map} is {@code null} or {@code map} contains a
	 *                              {@code null} key
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

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	public abstract ComparableTreeMap<K, V, N> clone();
}
