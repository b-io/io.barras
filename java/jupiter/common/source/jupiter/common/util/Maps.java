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

import static jupiter.common.util.Characters.COLON;
import static jupiter.common.util.Strings.INITIAL_CAPACITY;
import static jupiter.common.util.Strings.NULL;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the {@code V} values of the specified {@link Map} associated to the specified
	 * keys or {@code null} for those that are not present in an {@link ExtendedList}.
	 * <p>
	 * @param <V>  the type of the {@link ExtendedList} to return
	 * @param map  a {@link Map} of {@code V} value subtype
	 * @param keys the array of key {@link Object} of the {@code V} values to get
	 * <p>
	 * @return all the {@code V} values of the specified {@link Map} associated to the specified
	 *         keys or {@code null} for those that are not present in an {@link ExtendedList}
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared to the {@code map} keys
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public static <V> ExtendedList<V> getAll(final Map<?, V> map, final Object[] keys) {
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
	 * @param <V>          the type of the {@link ExtendedList} to return
	 * @param map          a {@link Map} of {@code V} value subtype
	 * @param keys         the array of key {@link Object} of the {@code V} values to get
	 * @param defaultValue the default {@code V} value (may be {@code null})
	 * <p>
	 * @return all the {@code V} values of the specified {@link Map} associated to the specified
	 *         keys or the specified default {@code V} value for those that are not present in an
	 *         {@link ExtendedList}
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared to the {@code map} keys
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public static <V> ExtendedList<V> getAll(final Map<?, V> map, final Object[] keys,
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
	 * @param <V>          the type of the value to return
	 * @param map          a {@link Map} of {@code V} value subtype
	 * @param key          the key {@link Object} of the {@code V} value to get
	 * @param defaultValue the default {@code V} value (may be {@code null})
	 * <p>
	 * @return the {@code V} value of the specified {@link Map} associated to the specified key, or
	 *         the specified default {@code V} value if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared to the {@code map} keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public static <V> V getOrDefault(final Map<?, ? extends V> map, final Object key,
			final V defaultValue) {
		final V value;
		return (value = map.get(key)) != null || map.containsKey(key) ? value : defaultValue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the {@code V} values of the specified {@link Map} associated to the specified
	 * keys or {@code null} for those that are not present in an {@link ExtendedList}.
	 * <p>
	 * @param <V>  the type of the {@link ExtendedList} to return
	 * @param map  a {@link Map} of {@code V} value subtype
	 * @param keys the array of key {@link Object} of the {@code V} values to remove
	 * <p>
	 * @return all the {@code V} values of the specified {@link Map} associated to the specified
	 *         keys or {@code null} for those that are not present in an {@link ExtendedList}
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be compared to the {@code map} keys
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public static <V> ExtendedList<V> removeAll(final Map<?, ? extends V> map,
			final Object... keys) {
		final ExtendedList<V> values = new ExtendedList<V>(keys.length);
		for (final Object key : keys) {
			values.add(map.remove(key));
		}
		return values;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of {@link Map}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Map},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Map;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link Map}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link Map},
	 *         {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return Map.class.isAssignableFrom(c);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Map}, or {@code "null"} if it
	 * is {@code null}.
	 * <p>
	 * @param map a {@link Map} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Map}, or {@code "null"} if it
	 *         is {@code null}
	 */
	public static String toString(final Map<?, ?> map) {
		// Check the arguments
		if (map == null) {
			return NULL;
		}

		// Convert the map to a representative string
		final Set<? extends Entry<?, ?>> entries = map.entrySet();
		final StringBuilder builder = Strings.createBuilder(entries.size() *
				(2 * INITIAL_CAPACITY + 6));
		int i = 0;
		for (final Entry<?, ?> entry : entries) {
			if (i++ > 0) {
				builder.append(Arrays.DELIMITER);
			}
			builder.append(toString(entry));
		}
		return Strings.bracketize(builder.toString());
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Entry}, or {@code "null"} if
	 * it is {@code null}.
	 * <p>
	 * @param entry an {@link Entry} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Entry}, or {@code "null"} if
	 *         it is {@code null}
	 */
	public static String toString(final Entry<?, ?> entry) {
		if (entry == null) {
			return NULL;
		}
		return Strings.brace(toString(entry.getKey(), entry.getValue()));
	}

	/**
	 * Returns a representative {@link String} of the specified key-value mapping.
	 * <p>
	 * @param key   a key {@link Object} (may be {@code null})
	 * @param value a value {@link Object} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified key-value mapping
	 */
	public static String toString(final Object key, final Object value) {
		final StringBuilder builder = Strings.createBuilder(2 * INITIAL_CAPACITY + 3);
		if (key != null) {
			builder.append(Strings.doubleQuote(key));
			builder.append(COLON);
		}
		builder.append(Strings.valueToString(value));
		return builder.toString();
	}
}
