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
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import jupiter.common.math.Comparables;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.util.Maps;

/**
 * {@link TreeMap} is a light sorted synchronized {@link Map} implementation of {@code K} and
 * {@code V} types based on a tree with a {@link Comparator} to determine the order of the entries.
 * <p>
 * @param <K> the key type of the {@link TreeMap}
 * @param <V> the value type of the {@link TreeMap}
 * @param <N> the {@link TreeNode} type of the {@link TreeMap}
 */
public abstract class TreeMap<K, V, N extends TreeNode<K, V>>
		extends AbstractMap<K, V>
		implements ICloneable<TreeMap<K, V, N>>, Serializable {

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
	/**
	 * The key {@link Comparator} of {@code K} supertype to use.
	 */
	protected final Comparator<? super K> keyComparator;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty {@link TreeMap} of {@code K}, {@code V} and {@code N} types by default.
	 * <p>
	 * @param c the key {@link Class} of {@code K} type
	 */
	protected TreeMap(final Class<K> c) {
		this(Comparables.getComparator(c));
	}

	/**
	 * Constructs a {@link TreeMap} of {@code K}, {@code V} and {@code N} types loaded from the
	 * specified key and value arrays containing the key-value mappings.
	 * <p>
	 * @param c      the key {@link Class} of {@code K} type
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@code V} array containing the values of the key-value mappings to load
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be mutually compared
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	protected TreeMap(final Class<K> c, final K[] keys, final V[] values) {
		this(c);

		// Check the arguments
		ArrayArguments.requireSameLength(keys, values);

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs a {@link TreeMap} of {@code K}, {@code V} and {@code N} types loaded from the
	 * specified {@link Map} containing the key-value mappings.
	 * <p>
	 * @param c   the key {@link Class} of {@code K} type
	 * @param map the {@link Map} of {@code K} and {@code V} subtypes containing the key-value
	 *            mappings to load
	 * <p>
	 * @throws ClassCastException   if any {@code map} keys cannot be mutually compared
	 * @throws NullPointerException if {@code map} is {@code null}
	 */
	protected TreeMap(final Class<K> c, final Map<? extends K, ? extends V> map) {
		this(c);
		putAll(map);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty {@link TreeMap} of {@code K}, {@code V} and {@code N} types with the
	 * specified key {@link Comparator}.
	 * <p>
	 * @param keyComparator the key {@link Comparator} of {@code K} supertype to determine the order
	 */
	protected TreeMap(final Comparator<? super K> keyComparator) {
		super();
		this.keyComparator = keyComparator;
	}

	/**
	 * Constructs a {@link TreeMap} of {@code K}, {@code V} and {@code N} types with the specified
	 * key {@link Comparator} loaded from the specified key and value arrays containing the
	 * key-value mappings.
	 * <p>
	 * @param keyComparator the key {@link Comparator} of {@code K} supertype to determine the order
	 * @param keys          the {@code K} array containing the keys of the key-value mappings to
	 *                      load
	 * @param values        the {@code V} array containing the values of the key-value mappings to
	 *                      load
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be mutually compared using
	 *                              {@code keyComparator}
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	protected TreeMap(final Comparator<? super K> keyComparator, final K[] keys, final V[] values) {
		this(keyComparator);

		// Check the arguments
		ArrayArguments.requireSameLength(keys, values);

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs a {@link TreeMap} of {@code K}, {@code V} and {@code N} types with the specified
	 * key {@link Comparator} loaded from the specified {@link Map} containing the key-value
	 * mappings.
	 * <p>
	 * @param keyComparator the key {@link Comparator} of {@code K} supertype to determine the order
	 * @param map           the {@link Map} of {@code K} and {@code V} subtypes containing the
	 *                      key-value mappings to load
	 * <p>
	 * @throws ClassCastException   if any {@code map} keys cannot be mutually compared using
	 *                              {@code keyComparator}
	 * @throws NullPointerException if {@code map} is {@code null}
	 */
	protected TreeMap(final Comparator<? super K> keyComparator,
			final Map<? extends K, ? extends V> map) {
		this(keyComparator);
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
	 * @throws ClassCastException   if {@code key} cannot be compared to {@code this} keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	public V get(final Object key) {
		// Check the arguments
		Arguments.requireNonNull(key, "key");

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
	 * @throws ClassCastException   if {@code key} cannot be compared to {@code this} keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public V getOrDefault(final Object key, final V defaultValue) {
		// Check the arguments
		Arguments.requireNonNull(key, "key");

		// Get the value associated to the key or the default value if it is not present
		return Maps.<V>getOrDefault(this, key, defaultValue);
	}

	/**
	 * Returns all the {@code V} values associated to the specified keys or {@code null} for those
	 * that are not present in an {@link ExtendedList}.
	 * <p>
	 * @param keys the array of key {@link Object} of the {@code V} values to get
	 * <p>
	 * @return all the {@code V} values associated to the specified keys or {@code null} for those
	 *         that are not present in an {@link ExtendedList}
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared to {@code this} keys
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public ExtendedList<V> getAll(final Object[] keys) {
		// Check the arguments
		Arguments.requireNonNull(keys, "keys");

		// Get the values associated to the keys
		return Maps.<V>getAll(this, keys);
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
	 * @throws ClassCastException   if any {@code keys} cannot be compared to {@code this} keys
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public ExtendedList<V> getAll(final Object[] keys, final V defaultValue) {
		// Check the arguments
		Arguments.requireNonNull(keys, "keys");

		// Get the values associated to the keys or the default value for those that are not present
		return Maps.<V>getAll(this, keys, defaultValue);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the key {@link Comparator} of {@code K} supertype.
	 * <p>
	 * @return the key {@link Comparator} of {@code K} supertype
	 */
	public Comparator<? super K> getKeyComparator() {
		return keyComparator;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the root {@link Entry} of {@code K} and {@code V} types.
	 * <p>
	 * @return the root {@link Entry} of {@code K} and {@code V} types
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
	 * @throws ClassCastException   if {@code key} cannot be compared to {@code this} keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@SuppressWarnings({"cast", "unchecked"})
	protected N getNode(final Object key) {
		// Check the arguments
		Arguments.requireNonNull(key, "key");

		// Get the node
		return findNode((Comparable<? super K>) key);
	}

	/**
	 * Returns the {@code N} node associated to the specified key {@link Comparable}, or
	 * {@code null} if it is not present.
	 * <p>
	 * @param keyComparable the key {@link Comparable} of {@code K} supertype to find
	 * <p>
	 * @return the {@code N} node associated to the specified key {@link Comparable}, or
	 *         {@code null} if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code keyComparable} cannot be compared to {@code this} keys
	 * @throws NullPointerException if {@code keyComparable} is {@code null}
	 */
	protected abstract N findNode(final Comparable<? super K> keyComparable);

	//////////////////////////////////////////////

	/**
	 * Returns the size.
	 * <p>
	 * @return the size
	 */
	@Override
	public int size() {
		return size;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Puts all the key-value mappings of the specified key and value arrays to {@code this}
	 * replacing any entries with identical keys.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to put
	 * @param values the {@code V} array containing the values of the key-value mappings to put
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared to {@code this} keys
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public synchronized void putAll(final K[] keys, final V[] values) {
		Maps.putAll(this, keys, values);
	}

	/**
	 * Puts all the key-value mappings of the specified map to {@code this} replacing any entries
	 * with identical keys.
	 * <p>
	 * @param map the {@link Map} of {@code K} and {@code V} subtypes containing the key-value
	 *            mappings to put
	 * <p>
	 * @throws ClassCastException   if any {@code map} keys cannot be compared to {@code this} keys
	 * @throws NullPointerException if {@code map} is {@code null}
	 */
	@Override
	public synchronized void putAll(final Map<? extends K, ? extends V> map) {
		super.putAll(map);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the {@code V} values associated to the specified keys or {@code null} for those
	 * that are not present in an {@link ExtendedList}.
	 * <p>
	 * @param keys the array of key {@link Object} of the {@code V} values to remove
	 * <p>
	 * @return all the {@code V} values associated to the specified keys or {@code null} for those
	 *         that are not present in an {@link ExtendedList}
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared to {@code this} keys
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public synchronized ExtendedList<V> removeAll(final Object... keys) {
		return Maps.<V>removeAll(this, keys);
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
	 * @see ICloneable
	 */
	@Override
	public abstract TreeMap<K, V, N> clone();
}
