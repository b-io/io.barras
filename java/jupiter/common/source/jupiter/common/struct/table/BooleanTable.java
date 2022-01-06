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
package jupiter.common.struct.table;

import java.io.IOException;

import jupiter.common.map.parser.IParsers;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Booleans;

/**
 * {@link BooleanTable} is the {@link Table} of {@link Boolean}.
 */
public class BooleanTable
		extends Table<Boolean> {

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
	 * Constructs a {@link BooleanTable} with the specified numbers of rows and columns.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public BooleanTable(final int rowCount, final int columnCount) {
		super(Boolean.class, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link BooleanTable} with the specified header and numbers of rows and columns.
	 * <p>
	 * @param header      an array of {@link String} (may be {@code null})
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public BooleanTable(final String[] header, final int rowCount, final int columnCount) {
		super(Boolean.class, header, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link BooleanTable} with the specified index, header and numbers of rows and
	 * columns.
	 * <p>
	 * @param index       an array of {@link Object} (may be {@code null})
	 * @param header      an array of {@link String} (may be {@code null})
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public BooleanTable(final Object[] index, final String[] header, final int rowCount,
			final int columnCount) {
		super(Boolean.class, index, header, rowCount, columnCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link BooleanTable} with the specified {@code boolean} values.
	 * <p>
	 * @param values a 2D {@code boolean} array
	 */
	public BooleanTable(final boolean[]... values) {
		this(Booleans.toArray2D(values));
	}

	/**
	 * Constructs a {@link BooleanTable} with the specified elements.
	 * <p>
	 * @param elements a 2D array of {@link Boolean}
	 */
	public BooleanTable(final Boolean[]... elements) {
		super(Boolean.class, elements);
	}

	/**
	 * Constructs a {@link BooleanTable} with the specified header and values.
	 * <p>
	 * @param header an array of {@link String}
	 * @param values a 2D {@code boolean} array
	 */
	public BooleanTable(final String[] header, final boolean[]... values) {
		this(header, Booleans.toArray2D(values));
	}

	/**
	 * Constructs a {@link BooleanTable} with specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Boolean}
	 */
	public BooleanTable(final String[] header, final Boolean[]... elements) {
		super(Boolean.class, header, elements);
	}

	/**
	 * Constructs a {@link BooleanTable} with the specified index, header and values.
	 * <p>
	 * @param index  an array of {@link Object} (may be {@code null})
	 * @param header an array of {@link String}
	 * @param values a 2D {@code boolean} array
	 */
	public BooleanTable(final Object[] index, final String[] header, final boolean[]... values) {
		this(index, header, Booleans.toArray2D(values));
	}

	/**
	 * Constructs a {@link BooleanTable} with specified index, header and elements.
	 * <p>
	 * @param index    an array of {@link Object} (may be {@code null})
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Boolean}
	 */
	public BooleanTable(final Object[] index, final String[] header, final Boolean[]... elements) {
		super(Boolean.class, index, header, elements);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link BooleanTable} loaded from the file denoted by the specified path.
	 * <p>
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public BooleanTable(final String path, final boolean hasHeader)
			throws IOException {
		super(IParsers.BOOLEAN_PARSER, path, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code boolean} array containing all the elements of {@code this} in the same
	 * order, or {@code null} if {@code this} is empty.
	 * <p>
	 * @return a {@code boolean} array containing all the elements of {@code this} in the same
	 *         order, or {@code null} if {@code this} is empty
	 *
	 * @see Booleans#toPrimitiveArray(Object[][])
	 */
	public boolean[] toPrimitiveArray() {
		return Booleans.toPrimitiveArray(elements);
	}

	/**
	 * Returns a 2D {@code boolean} array containing all the elements of {@code this} in the same
	 * order, or {@code null} if {@code this} is empty.
	 * <p>
	 * @return a 2D {@code boolean} array containing all the elements of {@code this} in the same
	 *         order, or {@code null} if {@code this} is empty
	 *
	 * @see Booleans#toPrimitiveArray2D(Object[][])
	 */
	public boolean[][] toPrimitiveArray2D() {
		return Booleans.toPrimitiveArray2D(elements);
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
	public BooleanTable clone() {
		return (BooleanTable) super.clone();
	}
}
