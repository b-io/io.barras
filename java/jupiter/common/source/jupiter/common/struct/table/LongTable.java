/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2026 Florian Barras <https://barras.io> (florian@barras.io)
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

import jupiter.common.transform.converter.IConverters;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Longs;

/**
 * {@link LongTable} is the {@link NumberTable} of {@link Long}.
 */
public class LongTable
		extends NumberTable<Long> {

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
	 * Constructs a {@link LongTable} with the specified numbers of rows and columns.
	 *
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public LongTable(final int rowCount, final int columnCount) {
		super(Long.class, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link LongTable} with the specified header and numbers of rows and columns.
	 *
	 * @param header      an array of {@link String} (may be {@code null})
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public LongTable(final String[] header, final int rowCount, final int columnCount) {
		super(Long.class, header, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link LongTable} with the specified index, header and numbers of rows and
	 * columns.
	 *
	 * @param index       an array of {@link Object} (may be {@code null})
	 * @param header      an array of {@link String} (may be {@code null})
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public LongTable(final Object[] index, final String[] header, final int rowCount,
			final int columnCount) {
		super(Long.class, index, header, rowCount, columnCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link LongTable} with the specified {@code long} values.
	 *
	 * @param values a 2D {@code long} array
	 */
	public LongTable(final long[]... values) {
		this(Longs.toArray2D(values));
	}

	/**
	 * Constructs a {@link LongTable} with the specified elements.
	 *
	 * @param elements a 2D array of {@link Long}
	 */
	public LongTable(final Long[]... elements) {
		super(Long.class, elements);
	}

	/**
	 * Constructs a {@link LongTable} with the specified header and values.
	 *
	 * @param header an array of {@link String}
	 * @param values a 2D {@code long} array
	 */
	public LongTable(final String[] header, final long[]... values) {
		this(header, Longs.toArray2D(values));
	}

	/**
	 * Constructs a {@link LongTable} with the specified header and elements.
	 *
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Long}
	 */
	public LongTable(final String[] header, final Long[]... elements) {
		super(Long.class, header, elements);
	}

	/**
	 * Constructs a {@link LongTable} with the specified index, header and values.
	 *
	 * @param index  an array of {@link Object} (may be {@code null})
	 * @param header an array of {@link String}
	 * @param values a 2D {@code long} array
	 */
	public LongTable(final Object[] index, final String[] header, final long[]... values) {
		this(index, header, Longs.toArray2D(values));
	}

	/**
	 * Constructs a {@link LongTable} with specified index, header and elements.
	 *
	 * @param index    an array of {@link Object} (may be {@code null})
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Long}
	 */
	public LongTable(final Object[] index, final String[] header, final Long[]... elements) {
		super(Long.class, index, header, elements);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link LongTable} loaded from the file denoted by the specified path.
	 *
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public LongTable(final String path, final boolean hasHeader)
			throws IOException {
		super(IConverters.LONG_CONVERTER, path, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code long} array containing all the elements of {@code this} in the same order,
	 * or {@code null} if {@code this} is empty.
	 *
	 * @return a {@code long} array containing all the elements of {@code this} in the same order,
	 *         or {@code null} if {@code this} is empty
	 *
	 * @see Longs#toPrimitiveArray(Object[][])
	 */
	public long[] toPrimitiveArray() {
		return Longs.toPrimitiveArray(elements);
	}

	/**
	 * Returns a 2D {@code long} array containing all the elements of {@code this} in the same
	 * order, or {@code null} if {@code this} is empty.
	 *
	 * @return a 2D {@code long} array containing all the elements of {@code this} in the same
	 *         order, or {@code null} if {@code this} is empty
	 *
	 * @see Longs#toPrimitiveArray2D(Object[][])
	 */
	public long[][] toPrimitiveArray2D() {
		return Longs.toPrimitiveArray2D(elements);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 *
	 * @return a clone of {@code this}
	 * @see ICloneable
	 */
	@Override
	public LongTable clone() {
		return (LongTable) super.clone();
	}
}
