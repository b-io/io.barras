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
package jupiter.common.struct.map.hash;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.test.CollectionArguments;
import jupiter.common.util.Maps;
import jupiter.common.util.Objects;

/**
 * {@link ExtendedHashMap} is the extended {@link HashMap} of {@code K} and {@code V} types.
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
	 * @throws ClassCastException   if any {@code keys} cannot be compared to {@code this} keys
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public ExtendedHashMap(final K[] keys, final V[] values) {
		super(Arguments.requireNonNull(keys, "keys").length);

		// Check the arguments
		ArrayArguments.requireSameLength(keys, values);

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs an {@link ExtendedHashMap} of {@code K} and {@code V} types loaded from the
	 * specified key array and value {@link Collection} containing the key-value mappings.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@link Collection} of {@code V} element subtype containing the values of
	 *               the key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public ExtendedHashMap(final K[] keys, final Collection<? extends V> values) {
		super(Arguments.requireNonNull(keys, "keys").length);

		// Check the arguments
		ArrayArguments.requireSameLength(keys, Arguments.requireNonNull(values, "values").size());

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs an {@link ExtendedHashMap} of {@code K} and {@code V} types loaded from the
	 * specified key and value {@link Collection} containing the key-value mappings.
	 * <p>
	 * @param keys   the {@link Collection} of {@code K} element subtype containing the keys of the
	 *               key-value mappings to load
	 * @param values the {@link Collection} of {@code V} element subtype containing the values of
	 *               the key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public ExtendedHashMap(final Collection<? extends K> keys,
			final Collection<? extends V> values) {
		super(Arguments.requireNonNull(keys, "keys").size());

		// Check the arguments
		CollectionArguments.requireSameSize(keys, values);

		// Put all the key-value mappings
		putAll(keys, values);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs an {@link ExtendedHashMap} of {@code K} and {@code V} types loaded from the
	 * specified {@link Map} containing the key-value mappings.
	 * <p>
	 * @param map the {@link Map} containing the key-value mappings of {@code K} and {@code V}
	 *            subtypes to load
	 */
	public ExtendedHashMap(final Map<? extends K, ? extends V> map) {
		super(map);
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
		return Maps.getElementClass(keySet());
	}

	//////////////////////////////////////////////

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
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public V getOrDefault(final Object key, final V defaultValue) {
		return Maps.<V>getOrDefault(this, key, defaultValue);
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


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEARERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes all the key-value mappings from {@code this}.
	 */
	@Override
	public void clear() {
		super.clear();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a reverse {@link ExtendedHashMap} of {@code V} and {@code K} types.
	 * <p>
	 * @return a reverse {@link ExtendedHashMap} of {@code V} and {@code K} types
	 */
	public ExtendedHashMap<V, K> createReverse() {
		return Maps.<K, V>createReverse(this);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Puts the key-value mapping of the specified key and value into {@code this} replacing any
	 * entry with an identical key.
	 * <p>
	 * @param key   the {@code K} key of the key-value mapping to put
	 * @param value the {@code V} value of the key-value mapping to put (may be {@code null})
	 * <p>
	 * @return the previous associated {@code V} value, or {@code null} if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared to {@code this} keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	public V put(final K key, final V value) {
		return super.put(key, value);
	}

	/**
	 * Puts all the key-value mappings of the specified key and value arrays into {@code this}
	 * replacing any entries with identical keys.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to put
	 * @param values the {@code V} array containing the values of the key-value mappings to put
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared to {@code this} keys
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
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public void putAll(final Collection<? extends K> keys, final Collection<? extends V> values) {
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
	 */
	@Override
	public void putAll(final Map<? extends K, ? extends V> map) {
		super.putAll(map);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the key-value mapping of the specified key {@link Object} from {@code this}.
	 * <p>
	 * @param key the key {@link Object} of the key-value mapping to remove
	 * <p>
	 * @return the previous associated {@code V} value, or {@code null} if it is not present
	 * <p>
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	public V remove(final Object key) {
		return super.remove(key);
	}

	/**
	 * Removes all the key-value mappings associated to the specified keys from {@code this}.
	 * <p>
	 * @param keys the array of key {@link Object} of the key-value mappings to remove
	 * <p>
	 * @return the previous associated {@code V} values and {@code null} for the specified keys that
	 *         are not present in an {@link ExtendedList}
	 * <p>
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
	public ExtendedHashMap<K, V> clone() {
		final ExtendedHashMap<K, V> clone = new ExtendedHashMap<K, V>(size());
		for (final Entry<K, V> entry : entrySet()) {
			clone.put(Objects.clone(entry.getKey()), Objects.clone(entry.getValue()));
		}
		return clone;
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
