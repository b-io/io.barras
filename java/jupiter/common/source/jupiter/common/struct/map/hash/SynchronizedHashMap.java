/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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
import java.util.Map;
import java.util.Map.Entry;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.util.Objects;

/**
 * {@link SynchronizedHashMap} is the synchronized {@link ExtendedHashMap} of {@code K} and
 * {@code V} types.
 * <p>
 * @param <K> the key type of the {@link SynchronizedHashMap}
 * @param <V> the value type of the {@link SynchronizedHashMap}
 */
public class SynchronizedHashMap<K, V>
		extends ExtendedHashMap<K, V> {

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
	 * Constructs an empty {@link SynchronizedHashMap} of {@code K} and {@code V} types by default.
	 */
	public SynchronizedHashMap() {
		super();
	}

	/**
	 * Constructs an empty {@link SynchronizedHashMap} of {@code K} and {@code V} types with the
	 * specified initial capacity.
	 * <p>
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public SynchronizedHashMap(final int initialCapacity) {
		super(initialCapacity);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link SynchronizedHashMap} of {@code K} and {@code V} types loaded from the
	 * specified key and value arrays containing the key-value mappings.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@code V} array containing the values of the key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public SynchronizedHashMap(final K[] keys, final V[] values) {
		super(keys, values);
	}

	/**
	 * Constructs a {@link SynchronizedHashMap} of {@code K} and {@code V} types loaded from the
	 * specified key array and value {@link Collection} containing the key-value mappings.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@link Collection} of {@code V} element subtype containing the values of
	 *               the key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public SynchronizedHashMap(final K[] keys, final Collection<? extends V> values) {
		super(keys, values);
	}

	/**
	 * Constructs a {@link SynchronizedHashMap} of {@code K} and {@code V} types loaded from the
	 * specified key and value {@link Collection} containing the key-value mappings.
	 * <p>
	 * @param keys   the {@link Collection} of {@code K} element subtype containing the keys of the
	 *               key-value mappings to load
	 * @param values the {@link Collection} of {@code V} element subtype containing the values of
	 *               the key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public SynchronizedHashMap(final Collection<? extends K> keys,
			final Collection<? extends V> values) {
		super(keys, values);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link SynchronizedHashMap} of {@code K} and {@code V} types loaded from the
	 * specified {@link Map} containing the key-value mappings.
	 * <p>
	 * @param map the {@link Map} containing the key-value mappings of {@code K} and {@code V}
	 *            subtypes to load
	 */
	public SynchronizedHashMap(final Map<? extends K, ? extends V> map) {
		super(map);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEARERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes all the key-value mappings from {@code this}.
	 */
	@Override
	public synchronized void clear() {
		super.clear();
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
	 * @throws ClassCastException   if any {@code keys} cannot be compared to {@code this} keys
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	@Override
	public synchronized void putAll(final K[] keys, final V[] values) {
		super.putAll(keys, values);
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
	@Override
	public synchronized void putAll(final K[] keys, final Collection<? extends V> values) {
		super.putAll(keys, values);
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
	@Override
	public synchronized void putAll(final Collection<? extends K> keys,
			final Collection<? extends V> values) {
		super.putAll(keys, values);
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
	public synchronized void putAll(final Map<? extends K, ? extends V> map) {
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
	public synchronized V remove(final Object key) {
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
	@Override
	public synchronized ExtendedList<V> removeAll(final Object... keys) {
		return super.removeAll(keys);
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
	public SynchronizedHashMap<K, V> clone() {
		final SynchronizedHashMap<K, V> clone = new SynchronizedHashMap<K, V>(size());
		for (final Entry<K, V> entry : entrySet()) {
			clone.put(Objects.clone(entry.getKey()), Objects.clone(entry.getValue()));
		}
		return clone;
	}
}
