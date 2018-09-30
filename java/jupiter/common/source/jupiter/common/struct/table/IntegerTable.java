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
import jupiter.common.util.Integers;

/**
 * {@link IntegerTable} is a {@link NumberTable} of {@link Integer}.
 */
public class IntegerTable
		extends NumberTable<Integer> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -5265465158988560987L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link IntegerTable} of the specified numbers of rows and columns.
	 * <p>
	 * @param m the number of rows
	 * @param n the number of columns
	 */
	public IntegerTable(final int m, final int n) {
		super(Integer.class, m, n);
	}

	/**
	 * Constructs a {@link IntegerTable} of the specified header and numbers of rows and columns.
	 * <p>
	 * @param header an array of {@link String}
	 * @param m      the number of rows
	 * @param n      the number of columns
	 */
	public IntegerTable(final String[] header, final int m, final int n) {
		super(Integer.class, header, m, n);
	}

	/**
	 * Constructs an {@link IntegerTable} from the specified values.
	 * <p>
	 * @param values a 2D array of {@code int} values
	 */
	public IntegerTable(final int[]... values) {
		this(Integers.toArray2D(values));
	}

	/**
	 * Constructs an {@link IntegerTable} from the specified elements.
	 * <p>
	 * @param elements a 2D array of {@link Integer}
	 */
	public IntegerTable(final Integer[]... elements) {
		super(Integer.class, elements);
	}

	/**
	 * Constructs an {@link IntegerTable} from the specified header and values.
	 * <p>
	 * @param header an array of {@link String}
	 * @param values a 2D array of {@code int} values
	 */
	public IntegerTable(final String[] header, final int[]... values) {
		this(header, Integers.toArray2D(values));
	}

	/**
	 * Constructs an {@link IntegerTable} from the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Integer}
	 */
	public IntegerTable(final String[] header, final Integer[]... elements) {
		super(Integer.class, header, elements);
	}

	/**
	 * Constructs an {@link IntegerTable} imported from the specified file.
	 * <p>
	 * @param pathName  the path name of the file to import
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file
	 */
	public IntegerTable(final String pathName, final boolean hasHeader)
			throws IOException {
		super(Parsers.INTEGER_PARSER, pathName, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public int[][] toPrimitiveArray2D() {
		return Integers.toPrimitiveArray2D(elements);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public IntegerTable clone() {
		return new IntegerTable(header, elements);
	}
}
