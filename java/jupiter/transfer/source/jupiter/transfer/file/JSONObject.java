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
package jupiter.transfer.file;

import static jupiter.common.io.string.Stringifiers.JSON;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.map.hash.ExtendedHashMap;

/**
 * {@link JSONObject} is the {@link ExtendedHashMap} containing the JSON key-value mappings.
 */
public class JSONObject
		extends ExtendedHashMap<String, Object>
		implements Serializable {

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
	 * Constructs an empty {@link JSONObject} by default.
	 */
	public JSONObject() {
		super();
	}

	/**
	 * Constructs an empty {@link JSONObject} with the specified initial capacity.
	 * <p>
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public JSONObject(final int initialCapacity) {
		super(initialCapacity);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link JSONObject} loaded from the specified key and value arrays containing the
	 * key-value mappings.
	 * <p>
	 * @param keys   the array of {@link String} containing the keys of the key-value mappings to
	 *               load
	 * @param values the array of {@link Object} containing the values of the key-value mappings to
	 *               load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public JSONObject(final String[] keys, final Object[] values) {
		super(keys, values);
	}

	/**
	 * Constructs a {@link JSONObject} loaded from the specified key array and value
	 * {@link Collection} containing the key-value mappings.
	 * <p>
	 * @param keys   the array of {@link String} containing the keys of the key-value mappings to
	 *               load
	 * @param values the {@link Collection} containing the values of the key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public JSONObject(final String[] keys, final Collection<?> values) {
		super(keys, values);
	}

	/**
	 * Constructs a {@link JSONObject} loaded from the specified {@link Map} containing the
	 * key-value mappings.
	 * <p>
	 * @param map the {@link Map} containing the key-value mappings to load
	 */
	public JSONObject(final Map<String, ?> map) {
		super(map);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a JSON {@link String} of {@code this}.
	 * <p>
	 * @return a JSON {@link String} of {@code this}
	 */
	public String stringify() {
		return JSON.stringifyNode(this);
	}

	/**
	 * Returns a JSON {@link String} of the specified JSON key-value mappings.
	 * <p>
	 * @param keys the array of key {@link String} of the key-value mappings to represent as a JSON
	 *             {@link String} (may be {@code null})
	 * <p>
	 * @return a JSON {@link String} of the specified key-value mapping
	 */
	public String stringify(final String... keys) {
		return JSON.stringifyNode(getAll(keys));
	}

	//////////////////////////////////////////////

	/**
	 * Returns a JSON entry {@link String} of the specified key-value mapping.
	 * <p>
	 * @param key the key {@link String} of the key-value mapping to represent as a JSON entry
	 *            {@link String} (may be {@code null})
	 * <p>
	 * @return a JSON entry {@link String} of the specified key-value mapping
	 */
	public String stringifyNode(final String key) {
		return JSON.stringifyNode(key, get(key));
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
	public JSONObject clone() {
		return (JSONObject) super.clone();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return stringify();
	}
}
