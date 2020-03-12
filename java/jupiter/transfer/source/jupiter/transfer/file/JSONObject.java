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

import java.io.Serializable;
import java.util.Map;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.map.hash.ExtendedHashMap;

/**
 * {@link JSONObject} is an {@link ExtendedHashMap} containing the JSON key-value mappings.
 */
public class JSONObject
		extends ExtendedHashMap<String, Object>
		implements Serializable {


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

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
	 * Constructs a {@link JSONObject} loaded from the specified {@link Map} containing the JSON
	 * key-value mappings.
	 * <p>
	 * @param map the {@link Map} containing the JSON key-value mappings to load
	 */
	public JSONObject(final Map<String, Object> map) {
		super(map);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a JSON {@link String} of {@code this}.
	 * <p>
	 * @return a JSON {@link String} of {@code this}
	 */
	public String stringify() {
		return JSON.stringify(this);
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
		return JSON.stringify(getAll(keys));
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
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public JSONObject clone() {
		return new JSONObject(this);
	}
}
