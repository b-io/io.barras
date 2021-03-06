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
package jupiter.common.struct.table;

import java.io.IOException;

import jupiter.common.map.parser.IParsers;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Floats;

/**
 * {@link FloatTable} is the {@link NumberTable} of {@link Float}.
 */
public class FloatTable
		extends NumberTable<Float> {

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
	 * Constructs a {@link FloatTable} with the specified numbers of rows and columns.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public FloatTable(final int rowCount, final int columnCount) {
		super(Float.class, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link FloatTable} with the specified header and numbers of rows and columns.
	 * <p>
	 * @param header      an array of {@link String} (may be {@code null})
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public FloatTable(final String[] header, final int rowCount, final int columnCount) {
		super(Float.class, header, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link FloatTable} with the specified index, header and numbers of rows and
	 * columns.
	 * <p>
	 * @param index       an array of {@link Object} (may be {@code null})
	 * @param header      an array of {@link String} (may be {@code null})
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public FloatTable(final Object[] index, final String[] header, final int rowCount,
			final int columnCount) {
		super(Float.class, index, header, rowCount, columnCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link FloatTable} with the specified {@code float} values.
	 * <p>
	 * @param values a 2D {@code float} array
	 */
	public FloatTable(final float[]... values) {
		this(Floats.toArray2D(values));
	}

	/**
	 * Constructs a {@link FloatTable} with the specified elements.
	 * <p>
	 * @param elements a 2D array of {@link Float}
	 */
	public FloatTable(final Float[]... elements) {
		super(Float.class, elements);
	}

	/**
	 * Constructs a {@link FloatTable} with the specified header and values.
	 * <p>
	 * @param header an array of {@link String}
	 * @param values a 2D {@code float} array
	 */
	public FloatTable(final String[] header, final float[]... values) {
		this(header, Floats.toArray2D(values));
	}

	/**
	 * Constructs a {@link FloatTable} with the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Float}
	 */
	public FloatTable(final String[] header, final Float[]... elements) {
		super(Float.class, header, elements);
	}

	/**
	 * Constructs a {@link FloatTable} with the specified index, header and values.
	 * <p>
	 * @param index  an array of {@link Object} (may be {@code null})
	 * @param header an array of {@link String}
	 * @param values a 2D {@code float} array
	 */
	public FloatTable(final Object[] index, final String[] header, final float[]... values) {
		this(index, header, Floats.toArray2D(values));
	}

	/**
	 * Constructs a {@link FloatTable} with specified index, header and elements.
	 * <p>
	 * @param index    an array of {@link Object} (may be {@code null})
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Float}
	 */
	public FloatTable(final Object[] index, final String[] header, final Float[]... elements) {
		super(Float.class, index, header, elements);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link FloatTable} loaded from the file denoted by the specified path.
	 * <p>
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public FloatTable(final String path, final boolean hasHeader)
			throws IOException {
		super(IParsers.FLOAT_PARSER, path, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code float} array containing all the elements of {@code this} in the same order,
	 * or {@code null} if {@code this} is empty.
	 * <p>
	 * @return a {@code float} array containing all the elements of {@code this} in the same order,
	 *         or {@code null} if {@code this} is empty
	 *
	 * @see Floats#toPrimitiveArray(Object[][])
	 */
	public float[] toPrimitiveArray() {
		return Floats.toPrimitiveArray(elements);
	}

	/**
	 * Returns a 2D {@code float} array containing all the elements of {@code this} in the same
	 * order, or {@code null} if {@code this} is empty.
	 * <p>
	 * @return a 2D {@code float} array containing all the elements of {@code this} in the same
	 *         order, or {@code null} if {@code this} is empty
	 *
	 * @see Floats#toPrimitiveArray2D(Object[][])
	 */
	public float[][] toPrimitiveArray2D() {
		return Floats.toPrimitiveArray2D(elements);
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
	public FloatTable clone() {
		return (FloatTable) super.clone();
	}
}
