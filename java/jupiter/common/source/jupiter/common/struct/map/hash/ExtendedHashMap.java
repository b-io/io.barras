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
package jupiter.common.struct.map.hash;

import java.util.HashMap;
import java.util.Map;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.util.Collections;
import jupiter.common.util.Maps;

/**
 * {@link ExtendedHashMap} extends {@link HashMap} of {@code K} and {@code V} types and is
 * synchronized.
 * <p>
 * @param <K> the key type of the {@link ExtendedHashMap}
 * @param <V> the value type of the {@link ExtendedHashMap}
 */
public class ExtendedHashMap<K, V>
		extends HashMap<K, V>
		implements ICloneable<ExtendedHashMap<K, V>> {

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
	 * Constructs an empty {@link ExtendedHashMap} of {@code K} and {@code V} types by default.
	 */
	public ExtendedHashMap() {
		super(Maps.DEFAULT_CAPACITY);
	}

	/**
	 * Constructs an empty {@link ExtendedHashMap} of {@code K} and {@code V} types with the
	 * specified initial capacity.
	 * <p>
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public ExtendedHashMap(final int initialCapacity) {
		super(initialCapacity);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs an {@link ExtendedHashMap} of {@code K} and {@code V} types loaded from the
	 * specified key and value arrays containing the key-value mappings.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@code V} array containing the values of the key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public ExtendedHashMap(final K[] keys, final V[] values) {
		super(keys.length);

		// Check the arguments
		ArrayArguments.requireSameLength(keys, values);

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs an {@link ExtendedHashMap} of {@code K} and {@code V} types loaded from the
	 * specified {@link Map} containing the key-value mappings.
	 * <p>
	 * @param map the {@link Map} containing the key-value mappings of {@code K} and {@code V}
	 *            subtypes to load
	 * <p>
	 * @throws NullPointerException if {@code map} is {@code null}
	 */
	public ExtendedHashMap(final Map<? extends K, ? extends V> map) {
		super(map);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the key {@link Class}.
	 * <p>
	 * @return the key {@link Class}
	 */
	public Class<?> getKeyClass() {
		return Collections.getElementClass(keySet());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the {@code V} values associated to the specified keys or {@code null} for those
	 * that are not present in an {@link ExtendedList}.
	 * <p>
	 * @param keys the array of key {@link Object} of the {@code V} values to get
	 * <p>
	 * @return all the {@code V} values associated to the specified keys or {@code null} for those
	 *         that are not present in an {@link ExtendedList}
	 * <p>
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
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public ExtendedList<V> getAll(final Object[] keys, final V defaultValue) {
		// Check the arguments
		Arguments.requireNonNull(keys, "keys");

		// Get the values associated to the keys or the default value for those that are not present
		return Maps.<V>getAll(this, keys, defaultValue);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Associates the specified {@code V} value to the specified {@code K} key and returns the
	 * previous associated {@code V} value, or {@code null} if it is not present.
	 * <p>
	 * @param key   the {@code K} key of the key-value mapping to put
	 * @param value the {@code V} value of the key-value mapping to put (may be {@code null})
	 * <p>
	 * @return the previous associated {@code V} value, or {@code null} if it is not present
	 * <p>
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	public synchronized V put(final K key, final V value) {
		return super.put(key, value);
	}

	/**
	 * Puts all the key-value mappings of the specified key and value arrays into {@code this}
	 * replacing any entries with identical keys.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to put
	 * @param values the {@code V} array containing the values of the key-value mappings to put
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public synchronized void putAll(final K[] keys, final V[] values) {
		Maps.putAll(this, keys, values);
	}

	/**
	 * Puts all the key-value mappings of the specified map into {@code this} replacing any entries
	 * with identical keys.
	 * <p>
	 * @param map the {@link Map} containing the key-value mappings of {@code K} and {@code V}
	 *            subtypes to put
	 * <p>
	 * @throws NullPointerException if {@code map} is {@code null}
	 */
	@Override
	public synchronized void putAll(final Map<? extends K, ? extends V> map) {
		super.putAll(map);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the key-value mapping of the specified key {@link Object} and returns the previous
	 * associated {@code V} value, or {@code null} if it is not present.
	 * <p>
	 * @param key the key {@link Object} of the key-value mapping to remove
	 * <p>
	 * @return the previous associated {@code V} value, or {@code null} if it is not present
	 * <p>
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	public synchronized V remove(Object key) {
		return super.remove(key);
	}

	/**
	 * Returns all the {@code V} values associated to the specified keys or {@code null} for those
	 * that are not present in an {@link ExtendedList}.
	 * <p>
	 * @param keys the array of key {@link Object} of the {@code V} values to remove
	 * <p>
	 * @return all the {@code V} values associated to the specified keys or {@code null} for those
	 *         that are not present in an {@link ExtendedList}
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public synchronized ExtendedList<V> removeAll(final Object... keys) {
		return Maps.<V>removeAll(this, keys);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes all the key-value mappings.
	 */
	@Override
	public synchronized void clear() {
		super.clear();
	}


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
	@SuppressWarnings({"cast", "unchecked"})
	public ExtendedHashMap<K, V> clone() {
		return (ExtendedHashMap<K, V>) super.clone();
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
