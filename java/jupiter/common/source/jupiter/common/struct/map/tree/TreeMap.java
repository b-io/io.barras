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
package jupiter.common.struct.map.tree;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import jupiter.common.math.Comparables;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.test.CollectionArguments;
import jupiter.common.util.Maps;

/**
 * {@link TreeMap} is the light sorted {@link AbstractMap} of {@code K} and {@code V} types based on
 * a tree with a {@link Comparator} to determine the order of the entries.
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
	 * The key {@link Class} of {@code K} type.
	 */
	protected final Class<K> c;
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
		super();
		this.c = c;
		this.keyComparator = Comparables.getComparator(c);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link TreeMap} of {@code K}, {@code V} and {@code N} types loaded from the
	 * specified key and value arrays containing the key-value mappings.
	 * <p>
	 * @param c      the key {@link Class} of {@code K} type
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@code V} array containing the values of the key-value mappings to load
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be mutually compared using the
	 *                              default {@code keyComparator}
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	protected TreeMap(final Class<K> c, final K[] keys, final V[] values) {
		this(c);

		// Check the arguments
		ArrayArguments.requireSameLength(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs a {@link TreeMap} of {@code K}, {@code V} and {@code N} types loaded from the
	 * specified key array and value {@link Collection} containing the key-value mappings.
	 * <p>
	 * @param c      the key {@link Class} of {@code K} type
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@link Collection} of {@code V} element subtype containing the values of
	 *               the key-value mappings to load
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be mutually compared using the
	 *                              default {@code keyComparator}
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	protected TreeMap(final Class<K> c, final K[] keys, final Collection<? extends V> values) {
		this(c);

		// Check the arguments
		ArrayArguments.requireSameLength(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs a {@link TreeMap} of {@code K}, {@code V} and {@code N} types loaded from the
	 * specified key and value {@link Collection} containing the key-value mappings.
	 * <p>
	 * @param c      the key {@link Class} of {@code K} type
	 * @param keys   the {@link Collection} of {@code K} element subtype containing the keys of the
	 *               key-value mappings to load
	 * @param values the {@link Collection} of {@code V} element subtype containing the values of
	 *               the key-value mappings to load
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be mutually compared using the
	 *                              default {@code keyComparator}
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	protected TreeMap(final Class<K> c, final Collection<? extends K> keys,
			final Collection<? extends V> values) {
		this(c);

		// Check the arguments
		CollectionArguments.requireSameSize(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link TreeMap} of {@code K}, {@code V} and {@code N} types loaded from the
	 * specified {@link Map} containing the key-value mappings.
	 * <p>
	 * @param c   the key {@link Class} of {@code K} type
	 * @param map the {@link Map} containing the key-value mappings of {@code K} and {@code V}
	 *            subtypes to load
	 * <p>
	 * @throws ClassCastException if any {@code map} keys cannot be mutually compared using the
	 *                            default {@code keyComparator}
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
		this.c = null;
		this.keyComparator = keyComparator;
	}

	//////////////////////////////////////////////

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
		ArrayArguments.requireSameLength(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs a {@link TreeMap} of {@code K}, {@code V} and {@code N} types with the specified
	 * key {@link Comparator} loaded from the specified key array and value {@link Collection}
	 * containing the key-value mappings.
	 * <p>
	 * @param keyComparator the key {@link Comparator} of {@code K} supertype to determine the order
	 * @param keys          the {@code K} array containing the keys of the key-value mappings to
	 *                      load
	 * @param values        the {@link Collection} of {@code V} element subtype containing the
	 *                      values of the key-value mappings to load
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be mutually compared using
	 *                              {@code keyComparator}
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	protected TreeMap(final Comparator<? super K> keyComparator, final K[] keys,
			final Collection<? extends V> values) {
		this(keyComparator);

		// Check the arguments
		ArrayArguments.requireSameLength(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs a {@link TreeMap} of {@code K}, {@code V} and {@code N} types with the specified
	 * key {@link Comparator} loaded from the specified key and value {@link Collection} containing
	 * the key-value mappings.
	 * <p>
	 * @param keyComparator the key {@link Comparator} of {@code K} supertype to determine the order
	 * @param keys          the {@link Collection} of {@code K} element subtype containing the keys
	 *                      of the key-value mappings to load
	 * @param values        the {@link Collection} of {@code V} element subtype containing the
	 *                      values of the key-value mappings to load
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be mutually compared using
	 *                              {@code keyComparator}
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	protected TreeMap(final Comparator<? super K> keyComparator, final Collection<? extends K> keys,
			final Collection<? extends V> values) {
		this(keyComparator);

		// Check the arguments
		CollectionArguments.requireSameSize(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link TreeMap} of {@code K}, {@code V} and {@code N} types with the specified
	 * key {@link Comparator} loaded from the specified {@link Map} containing the key-value
	 * mappings.
	 * <p>
	 * @param keyComparator the key {@link Comparator} of {@code K} supertype to determine the order
	 * @param map           the {@link Map} containing the key-value mappings of {@code K} and
	 *                      {@code V} subtypes to load
	 * <p>
	 * @throws ClassCastException if any {@code map} keys cannot be mutually compared using
	 *                            {@code keyComparator}
	 */
	protected TreeMap(final Comparator<? super K> keyComparator,
			final Map<? extends K, ? extends V> map) {
		this(keyComparator);
		putAll(map);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the key {@link Class}.
	 * <p>
	 * @return the key {@link Class}
	 */
	public Class<?> getKeyClass() {
		return c != null ? c : Maps.getElementClass(keySet());
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@code V} value associated to the specified key {@link Object}, or {@code null}
	 * if it is not present.
	 * <p>
	 * @param key the key {@link Object} of the {@code V} value to get
	 * <p>
	 * @return the value associated to the specified key {@link Object}, or {@code null} if it is
	 *         not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared to {@code this} keys using
	 *                              {@code keyComparator}
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	public V get(final Object key) {
		// Check the arguments
		Arguments.requireNonNull(key, "key");

		// Return the value
		final N node = getNode(key);
		return node != null ? node.value : null;
	}

	/**
	 * Returns all the {@code V} values associated to the specified keys and {@code null} for those
	 * that are not present in an {@link ExtendedList}.
	 * <p>
	 * @param keys the array of key {@link Object} of the {@code V} values to get
	 * <p>
	 * @return all the {@code V} values associated to the specified keys and {@code null} for those
	 *         that are not present in an {@link ExtendedList}
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared to {@code this} keys
	 *                              using {@code keyComparator}
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public ExtendedList<V> getAll(final Object[] keys) {
		return Maps.<V>getAll(this, keys);
	}

	/**
	 * Returns all the {@code V} values associated to the specified keys and the specified default
	 * {@code V} value for those that are not present in an {@link ExtendedList}.
	 * <p>
	 * @param keys         the array of key {@link Object} of the {@code V} values to get
	 * @param defaultValue the default {@code V} value (may be {@code null})
	 * <p>
	 * @return all the {@code V} values associated to the specified keys and the specified default
	 *         {@code V} value for those that are not present in an {@link ExtendedList}
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared to {@code this} keys
	 *                              using {@code keyComparator}
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public ExtendedList<V> getAll(final Object[] keys, final V defaultValue) {
		return Maps.<V>getAll(this, keys, defaultValue);
	}

	/**
	 * Returns all the {@code V} values associated to the specified keys and the specified
	 * corresponding default {@code V} values for those that are not present in an
	 * {@link ExtendedList}.
	 * <p>
	 * @param keys          the array of key {@link Object} of the {@code V} values to get
	 * @param defaultValues the {@code V} array containing the corresponding default values
	 * <p>
	 * @return all the {@code V} values associated to the specified keys and the specified
	 *         corresponding default {@code V} values for those that are not present in an
	 *         {@link ExtendedList}
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public ExtendedList<V> getAll(final Object[] keys, final V[] defaultValues) {
		return Maps.<V>getAll(this, keys, defaultValues);
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
	 * @throws ClassCastException   if {@code key} cannot be compared to {@code this} keys using
	 *                              {@code keyComparator}
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@SuppressWarnings({"cast", "unchecked"})
	protected N getNode(final Object key) {
		// Check the arguments
		Arguments.requireNonNull(key, "key");

		// Return the node
		return findNode((Comparable<? super K>) key);
	}

	/**
	 * Returns the {@code N} node associated to the specified {@link Comparable} key, or
	 * {@code null} if it is not present.
	 * <p>
	 * @param keyComparable the {@link Comparable} key of {@code K} supertype to find
	 * <p>
	 * @return the {@code N} node associated to the specified {@link Comparable} key, or
	 *         {@code null} if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code keyComparable} cannot be compared to {@code this} keys
	 *                              using {@code keyComparator}
	 * @throws NullPointerException if {@code keyComparable} is {@code null}
	 */
	protected abstract N findNode(final Comparable<? super K> keyComparable);

	//////////////////////////////////////////////

	/**
	 * Returns the number of key-value mappings.
	 * <p>
	 * @return the number of key-value mappings
	 */
	@Override
	public int size() {
		return size;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints {@code this}.
	 */
	public abstract void print();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Puts all the key-value mappings of the specified key and value arrays into {@code this}
	 * replacing any entries with identical keys.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to put
	 * @param values the {@code V} array containing the values of the key-value mappings to put
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared to {@code this} keys
	 *                              using {@code keyComparator}
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public void putAll(final K[] keys, final V[] values) {
		Maps.<K, V>putAll(this, keys, values);
	}

	/**
	 * Puts all the key-value mappings of the specified key array and value {@link Collection} into
	 * {@code this} replacing any entries with identical keys.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to put
	 * @param values the {@link Collection} of {@code V} element subtype containing the values of
	 *               the key-value mappings to put
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared to {@code this} keys
	 *                              using {@code keyComparator}
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public void putAll(final K[] keys, final Collection<? extends V> values) {
		Maps.<K, V>putAll(this, keys, values);
	}

	/**
	 * Puts all the key-value mappings of the specified key and value {@link Collection} into
	 * {@code this} replacing any entries with identical keys.
	 * <p>
	 * @param keys   the {@link Collection} of {@code K} element subtype containing the keys of the
	 *               key-value mappings to put
	 * @param values the {@link Collection} of {@code V} element subtype containing the values of
	 *               the key-value mappings to put
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared to {@code this} keys
	 *                              using {@code keyComparator}
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public void putAll(final Collection<? extends K> keys,
			final Collection<? extends V> values) {
		Maps.<K, V>putAll(this, keys, values);
	}

	//////////////////////////////////////////////

	/**
	 * Puts all the key-value mappings of the specified {@link Map} into {@code this} replacing any
	 * entries with identical keys.
	 * <p>
	 * @param map the {@link Map} containing the key-value mappings of {@code K} and {@code V}
	 *            subtypes to put
	 * <p>
	 * @throws ClassCastException if any {@code map} keys cannot be compared to {@code this} keys
	 *                            using {@code keyComparator}
	 */
	@Override
	public void putAll(final Map<? extends K, ? extends V> map) {
		super.putAll(map);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes all the key-value mappings associated to the specified keys from {@code this}.
	 * <p>
	 * @param keys the array of key {@link Object} of the key-value mappings to remove
	 * <p>
	 * @return the previous associated {@code V} values and {@code null} for the specified keys that
	 *         are not present in an {@link ExtendedList}
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared to {@code this} keys
	 *                              using {@code keyComparator}
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public ExtendedList<V> removeAll(final Object... keys) {
		return Maps.<V>removeAll(this, keys);
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
	public abstract TreeMap<K, V, N> clone();
}
