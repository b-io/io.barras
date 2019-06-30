/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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
import jupiter.common.util.Longs;

/**
 * {@link LongTable} is a {@link NumberTable} of {@link Long}.
 */
public class LongTable
		extends NumberTable<Long> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -4612095602803223389L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link LongTable} of the specified numbers of rows and columns.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public LongTable(final int rowCount, final int columnCount) {
		super(Long.class, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link LongTable} of the specified header and numbers of rows and columns.
	 * <p>
	 * @param header      an array of {@link String}
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public LongTable(final String[] header, final int rowCount, final int columnCount) {
		super(Long.class, header, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link LongTable} of the specified {@code long} values.
	 * <p>
	 * @param values a 2D {@code long} array
	 */
	public LongTable(final long[]... values) {
		this(Longs.toArray2D(values));
	}

	/**
	 * Constructs a {@link LongTable} of the specified elements.
	 * <p>
	 * @param elements a 2D array of {@link Long}
	 */
	public LongTable(final Long[]... elements) {
		super(Long.class, elements);
	}

	/**
	 * Constructs a {@link LongTable} of the specified header and values.
	 * <p>
	 * @param header an array of {@link String}
	 * @param values a 2D {@code long} array
	 */
	public LongTable(final String[] header, final long[]... values) {
		this(header, Longs.toArray2D(values));
	}

	/**
	 * Constructs a {@link LongTable} of the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Long}
	 */
	public LongTable(final String[] header, final Long[]... elements) {
		super(Long.class, header, elements);
	}

	/**
	 * Constructs a {@link LongTable} loaded from the specified file.
	 * <p>
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the specified file
	 */
	public LongTable(final String path, final boolean hasHeader)
			throws IOException {
		super(IParsers.LONG_PARSER, path, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public long[] toPrimitiveArray() {
		return Longs.toPrimitiveArray(elements);
	}

	public long[][] toPrimitiveArray2D() {
		return Longs.toPrimitiveArray2D(elements);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	public LongTable clone() {
		return (LongTable) super.clone();
	}
}
