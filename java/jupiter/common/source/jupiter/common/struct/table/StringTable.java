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
package jupiter.common.struct.table;

import java.io.IOException;

import jupiter.common.map.parser.IParsers;
import jupiter.common.test.ArrayArguments;

/**
 * {@link StringTable} is the {@link Table} of {@link String}.
 */
public class StringTable
		extends Table<String> {

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
	 * Constructs a {@link StringTable} with the specified numbers of rows and columns.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public StringTable(final int rowCount, final int columnCount) {
		super(String.class, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link StringTable} with the specified header and numbers of rows and columns.
	 * <p>
	 * @param header      an array of {@link String}
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public StringTable(final String[] header, final int rowCount, final int columnCount) {
		super(String.class, header, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link StringTable} with the specified elements.
	 * <p>
	 * @param elements a 2D array of {@link String}
	 */
	public StringTable(final String[]... elements) {
		super(String.class, elements);
	}

	/**
	 * Constructs a {@link StringTable} with the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link String}
	 */
	public StringTable(final String[] header, final String[]... elements) {
		super(String.class, header, elements);
	}

	/**
	 * Constructs a {@link StringTable} loaded from the specified file.
	 * <p>
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading {@code path}
	 */
	public StringTable(final String path, final boolean hasHeader)
			throws IOException {
		super(IParsers.STRING_PARSER, path, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the specified row.
	 * <p>
	 * @param i      the row index
	 * @param values an array of {@link String}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	@Override
	public void setRow(final int i, final String[] values) {
		// Check the arguments
		// • i
		ArrayArguments.requireIndex(i, m);
		// • values
		ArrayArguments.requireMinLength(values, n);

		// Set the corresponding row
		for (int j = 0; j < n; ++j) {
			elements[i][j] = values[j];
		}
	}

	/**
	 * Sets the specified column.
	 * <p>
	 * @param j      the column index
	 * @param values an array of {@link String}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	@Override
	public void setColumn(final int j, final String[] values) {
		// Check the arguments
		// • j
		ArrayArguments.requireIndex(j, n);
		// • values
		ArrayArguments.requireMinLength(values, m);

		// Set the corresponding column
		for (int i = 0; i < m; ++i) {
			elements[i][j] = values[i];
		}
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
	public StringTable clone() {
		return (StringTable) super.clone();
	}
}
