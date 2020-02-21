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
package jupiter.common.util;

import java.util.Map;

import jupiter.common.struct.list.ExtendedList;

public class Maps
		extends Collections {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Maps}.
	 */
	protected Maps() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the {@code V} values of the specified {@link Map} associated to the specified
	 * keys in an {@link ExtendedList}.
	 * <p>
	 * @param <K>  the key type of the {@link Map}
	 * @param <V>  the value type of the {@link Map}
	 * @param map  a {@link Map}
	 * @param keys the array of key {@link Object} of the {@code V} values to get
	 * <p>
	 * @return all the {@code V} values of the specified {@link Map} associated to the specified
	 *         keys in an {@link ExtendedList}
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared with the {@code map} keys
	 * @throws NullPointerException if any {@code keys} are {@code null}
	 */
	public static <K, V> ExtendedList<V> getAll(final Map<K, V> map, final Object[] keys) {
		final ExtendedList<V> values = new ExtendedList<V>(keys.length);
		for (final Object key : keys) {
			values.add(map.get(key));
		}
		return values;
	}

	/**
	 * Returns all the {@code V} values of the specified {@link Map} associated to the specified
	 * keys or the specified default {@code V} value for those that are not present in an
	 * {@link ExtendedList}.
	 * <p>
	 * @param <K>          the key type of the {@link Map}
	 * @param <V>          the value type of the {@link Map}
	 * @param map          a {@link Map}
	 * @param keys         the array of key {@link Object} of the {@code V} values to get
	 * @param defaultValue the default {@code V} value (may be {@code null})
	 * <p>
	 * @return all the {@code V} values of the specified {@link Map} associated to the specified
	 *         keys or the specified default {@code V} value for those that are not present in an
	 *         {@link ExtendedList}
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared with the {@code map} keys
	 * @throws NullPointerException if any {@code keys} are {@code null}
	 */
	public static <K, V> ExtendedList<V> getAll(final Map<K, V> map, final Object[] keys,
			final V defaultValue) {
		final ExtendedList<V> values = new ExtendedList<V>(keys.length);
		for (final Object key : keys) {
			values.add(getOrDefault(map, key, defaultValue));
		}
		return values;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@code V} value of the specified {@link Map} associated to the specified key, or
	 * the specified default {@code V} value if it is not present.
	 * <p>
	 * @param <K>          the key type of the {@link Map}
	 * @param <V>          the value type of the {@link Map}
	 * @param map          a {@link Map}
	 * @param key          the key {@link Object} of the {@code V} value to get
	 * @param defaultValue the default {@code V} value (may be {@code null})
	 * <p>
	 * @return the {@code V} value of the specified {@link Map} associated to the specified key, or
	 *         the specified default {@code V} value if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the {@code map} keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public static <K, V> V getOrDefault(final Map<K, V> map, final Object key,
			final V defaultValue) {
		final V value;
		return (value = map.get(key)) != null || map.containsKey(key) ? value : defaultValue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link Map}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link Map},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return Map.class.isAssignableFrom(c);
	}
}
