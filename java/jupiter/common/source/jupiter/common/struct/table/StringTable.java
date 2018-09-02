/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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

import jupiter.common.map.parser.Parsers;
import jupiter.common.test.ArrayArguments;
import jupiter.common.test.IntegerArguments;

/**
 * {@link StringTable} is a {@link Table} of {@link String}.
 */
public class StringTable
		extends Table<String> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -2081035613974725698L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link StringTable} of the specified numbers of rows and columns.
	 * <p>
	 * @param m the number of rows
	 * @param n the number of columns
	 */
	public StringTable(final int m, final int n) {
		super(String.class, m, n);
	}

	/**
	 * Constructs a {@link StringTable} of the specified header and numbers of rows and columns.
	 * <p>
	 * @param header an array of {@link String}
	 * @param m      the number of rows
	 * @param n      the number of columns
	 */
	public StringTable(final String[] header, final int m, final int n) {
		super(String.class, header, m, n);
	}

	/**
	 * Constructs a {@link StringTable} from the specified elements.
	 * <p>
	 * @param elements a 2D array of {@link String}
	 */
	public StringTable(final String[]... elements) {
		super(String.class, elements);
	}

	/**
	 * Constructs a {@link StringTable} from the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link String}
	 */
	public StringTable(final String[] header, final String[]... elements) {
		super(String.class, header, elements);
	}

	/**
	 * Constructs a {@link StringTable} imported from the specified file.
	 * <p>
	 * @param pathname  the pathname of the file to import
	 * @param hasHeader the option specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file
	 */
	public StringTable(final String pathname, final boolean hasHeader)
			throws IOException {
		super(Parsers.STRING_PARSER, pathname, hasHeader);
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
	 * @throws ArrayIndexOutOfBoundsException if the specified index is out of bounds
	 */
	@Override
	public void setRow(final int i, final String[] values) {
		// Check the arguments
		// - i
		IntegerArguments.requireNonNegative(i);
		IntegerArguments.requireLessThan(i, m);
		// - values
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
	 * @throws ArrayIndexOutOfBoundsException if the specified index is out of bounds
	 */
	@Override
	public void setColumn(final int j, final String[] values) {
		// Check the arguments
		// - j
		IntegerArguments.requireNonNegative(j);
		IntegerArguments.requireLessThan(j, n);
		// - values
		ArrayArguments.requireMinLength(values, m);

		// Set the corresponding column
		for (int i = 0; i < m; ++i) {
			elements[i][j] = values[i];
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public StringTable clone() {
		return new StringTable(header, elements);
	}
}
