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
import java.util.Collection;
import java.util.Map;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.util.Objects;

/**
 * {@link JSONList} is the {@link ExtendedList} of {@link JSONObject}.
 */
public class JSONList
		extends ExtendedList<JSONObject>
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty {@link JSONList} by default.
	 */
	public JSONList() {
		super();
	}

	/**
	 * Constructs an empty {@link JSONList} with the specified initial capacity.
	 * <p>
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public JSONList(final int initialCapacity) {
		super(initialCapacity);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link JSONList} with the specified elements.
	 * <p>
	 * @param elements an array of {@link JSONObject}
	 */
	public JSONList(final JSONObject... elements) {
		super(elements);
	}

	/**
	 * Constructs a {@link JSONList} with the elements of the specified {@link Collection}.
	 * <p>
	 * @param elements a {@link Collection} of {@link JSONObject}
	 */
	public JSONList(final Collection<? extends JSONObject> elements) {
		super(elements);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends the {@link JSONObject} loaded from the specified key and value arrays containing the
	 * key-value mappings to the end of {@code this}.
	 * <p>
	 * @param keys   the array of {@link String} containing the keys of the key-value mappings of
	 *               the {@link JSONObject} to append
	 * @param values the array of {@link Object} containing the values of the key-value mappings of
	 *               the {@link JSONObject} to append
	 * <p>
	 * @return {@code true} (as specified by {@link Collection#add}
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public synchronized boolean add(final String[] keys, final Object[] values) {
		return add(new JSONObject(keys, values));
	}

	/**
	 * Appends the {@link JSONObject} loaded from the specified {@link Map} containing the key-value
	 * mappings to the end of {@code this}.
	 * <p>
	 * @param map the {@link Map} containing the key-value mappings of the {@link JSONObject} to
	 *            append
	 * <p>
	 * @return {@code true} (as specified by {@link Collection#add})
	 */
	public synchronized boolean add(final Map<String, ? extends Object> map) {
		return add(new JSONObject(map));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a JSON {@link String} of {@code this}.
	 * <p>
	 * @return a JSON {@link String} of {@code this}
	 */
	public String stringify() {
		return JSON.stringify(this);
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
	public JSONList clone() {
		final JSONList clone = new JSONList(size());
		for (final JSONObject element : this) {
			clone.add(Objects.clone(element));
		}
		return clone;
	}
}
