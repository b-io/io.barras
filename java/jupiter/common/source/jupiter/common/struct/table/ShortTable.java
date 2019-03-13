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
import jupiter.common.util.Shorts;

/**
 * {@link ShortTable} is a {@link NumberTable} of {@link Short}.
 */
public class ShortTable
		extends NumberTable<Short> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 2242151657118690374L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ShortTable} of the specified numbers of rows and columns.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public ShortTable(final int rowCount, final int columnCount) {
		super(Short.class, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link ShortTable} of the specified header and numbers of rows and columns.
	 * <p>
	 * @param header      an array of {@link String}
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public ShortTable(final String[] header, final int rowCount, final int columnCount) {
		super(Short.class, header, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link ShortTable} from the specified values.
	 * <p>
	 * @param values a 2D array of {@code short} values
	 */
	public ShortTable(final short[]... values) {
		this(Shorts.toArray2D(values));
	}

	/**
	 * Constructs a {@link ShortTable} from the specified elements.
	 * <p>
	 * @param elements a 2D array of {@link Short}
	 */
	public ShortTable(final Short[]... elements) {
		super(Short.class, elements);
	}

	/**
	 * Constructs a {@link ShortTable} from the specified header and values.
	 * <p>
	 * @param header an array of {@link String}
	 * @param values a 2D array of {@code short} values
	 */
	public ShortTable(final String[] header, final short[]... values) {
		this(header, Shorts.toArray2D(values));
	}

	/**
	 * Constructs a {@link ShortTable} from the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Short}
	 */
	public ShortTable(final String[] header, final Short[]... elements) {
		super(Short.class, header, elements);
	}

	/**
	 * Constructs a {@link ShortTable} imported from the specified file.
	 * <p>
	 * @param pathName  the path name of the file to import
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file
	 */
	public ShortTable(final String pathName, final boolean hasHeader)
			throws IOException {
		super(IParsers.SHORT_PARSER, pathName, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public short[] toPrimitiveArray() {
		return Shorts.toPrimitiveArray(elements);
	}

	public short[][] toPrimitiveArray2D() {
		return Shorts.toPrimitiveArray2D(elements);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public ShortTable clone() {
		return new ShortTable(header, elements);
	}
}