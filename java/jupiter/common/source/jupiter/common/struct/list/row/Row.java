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
package jupiter.common.struct.list.row;

import static jupiter.common.util.Strings.INITIAL_CAPACITY;

import java.io.Serializable;

import jupiter.common.model.ICloneable;
import jupiter.common.test.ArrayArguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Booleans;
import jupiter.common.util.Bytes;
import jupiter.common.util.Characters;
import jupiter.common.util.Doubles;
import jupiter.common.util.Floats;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Maps;
import jupiter.common.util.Objects;
import jupiter.common.util.Shorts;
import jupiter.common.util.Strings;

public class Row
		implements ICloneable<Row>, Serializable {

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
	 * The index (row name).
	 */
	public Object index;
	/**
	 * The header (column names).
	 */
	public String[] header;
	/**
	 * The elements.
	 */
	public Object[] elements;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Row} with the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements an array of {@link Object}
	 */
	public Row(final String[] header, final Object... elements) {
		this(null, header, elements);
	}

	/**
	 * Constructs a {@link Row} with the specified header and elements.
	 * <p>
	 * @param index    an {@link Object} (may be {@code null})
	 * @param header   an array of {@link String}
	 * @param elements an array of {@link Object}
	 */
	public Row(final Object index, final String[] header, final Object... elements) {
		// Check the arguments
		ArrayArguments.requireSameLength(header, "header", elements, "elements");

		// Set the attributes
		this.index = index;
		this.header = header;
		this.elements = elements;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element of the specified column.
	 * <p>
	 * @param name the column name (may be {@code null})
	 * <p>
	 * @return the element of the specified column
	 * <p>
	 * @throws IllegalArgumentException if {@code name} is not present
	 */
	public Object get(final String name) {
		final int index = Strings.findFirstIndexIgnoreCase(header, name);
		if (index < 0) {
			throw new IllegalArgumentException("There is no column " + Strings.quote(name));
		}
		return elements[index];
	}

	/**
	 * Returns all the elements of the specified columns.
	 * <p>
	 * @param names the column names
	 * <p>
	 * @return all the elements of the specified columns
	 * <p>
	 * @throws IllegalArgumentException if any {@code names} is not present
	 */
	public Object[] getAll(final String... names) {
		final Object[] values = new Object[names.length];
		for (int i = 0; i < names.length; ++i) {
			values[i] = get(names[i]);
		}
		return values;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@code boolean} element of the specified column.
	 * <p>
	 * @param name the column name (may be {@code null})
	 * <p>
	 * @return the {@code boolean} element of the specified column
	 * <p>
	 * @throws IllegalArgumentException if {@code name} is not present
	 */
	public boolean getBoolean(final String name) {
		return Booleans.convert(get(name));
	}

	/**
	 * Returns the {@code char} element of the specified column.
	 * <p>
	 * @param name the column name (may be {@code null})
	 * <p>
	 * @return the {@code char} element of the specified column
	 * <p>
	 * @throws IllegalArgumentException if {@code name} is not present
	 */
	public char getChar(final String name) {
		return Characters.convert(get(name));
	}

	/**
	 * Returns the {@code byte} element of the specified column.
	 * <p>
	 * @param name the column name (may be {@code null})
	 * <p>
	 * @return the {@code byte} element of the specified column
	 * <p>
	 * @throws IllegalArgumentException if {@code name} is not present
	 */
	public byte getByte(final String name) {
		return Bytes.convert(get(name));
	}

	/**
	 * Returns the {@code short} element of the specified column.
	 * <p>
	 * @param name the column name (may be {@code null})
	 * <p>
	 * @return the {@code short} element of the specified column
	 * <p>
	 * @throws IllegalArgumentException if {@code name} is not present
	 */
	public short getShort(final String name) {
		return Shorts.convert(get(name));
	}

	/**
	 * Returns the {@code int} element of the specified column.
	 * <p>
	 * @param name the column name (may be {@code null})
	 * <p>
	 * @return the {@code int} element of the specified column
	 * <p>
	 * @throws IllegalArgumentException if {@code name} is not present
	 */
	public int getInt(final String name) {
		return Integers.convert(get(name));
	}

	/**
	 * Returns the {@code long} element of the specified column.
	 * <p>
	 * @param name the column name (may be {@code null})
	 * <p>
	 * @return the {@code long} element of the specified column
	 * <p>
	 * @throws IllegalArgumentException if {@code name} is not present
	 */
	public long getLong(final String name) {
		return Longs.convert(get(name));
	}

	/**
	 * Returns the {@code float} element of the specified column.
	 * <p>
	 * @param name the column name (may be {@code null})
	 * <p>
	 * @return the {@code float} element of the specified column
	 * <p>
	 * @throws IllegalArgumentException if {@code name} is not present
	 */
	public float getFloat(final String name) {
		return Floats.convert(get(name));
	}

	/**
	 * Returns the {@code double} element of the specified column.
	 * <p>
	 * @param name the column name (may be {@code null})
	 * <p>
	 * @return the {@code double} element of the specified column
	 * <p>
	 * @throws IllegalArgumentException if {@code name} is not present
	 */
	public double getDouble(final String name) {
		return Doubles.convert(get(name));
	}

	/**
	 * Returns the element {@link String} of the specified column.
	 * <p>
	 * @param name the column name (may be {@code null})
	 * <p>
	 * @return the element {@link String} of the specified column
	 * <p>
	 * @throws IllegalArgumentException if {@code name} is not present
	 */
	public String getString(final String name) {
		return Strings.convert(get(name));
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
	public Row clone() {
		try {
			final Row clone = (Row) super.clone();
			clone.index = Objects.clone(index);
			clone.header = Arrays.clone(header);
			clone.elements = Arrays.clone(elements);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Row)) {
			return false;
		}
		final Row otherRow = (Row) other;
		return Objects.equals(index, otherRow.index) &&
				Arrays.equals(header, otherRow.header) &&
				Arrays.equals(elements, otherRow.elements);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see #equals(Object)
	 * @see System#identityHashCode(Object)
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, index, header, elements);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = Strings.createBuilder(header.length *
				(2 * INITIAL_CAPACITY + 4));
		if (header.length == elements.length) {
			for (int i = 0; i < header.length; ++i) {
				if (i > 0) {
					builder.append(Arrays.DELIMITER);
				}
				builder.append(Maps.toString(header[i], elements[i]));
			}
		}
		return Strings.brace(builder.toString());
	}
}
