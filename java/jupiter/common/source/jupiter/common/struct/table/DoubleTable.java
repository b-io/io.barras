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
import jupiter.common.util.Doubles;

/**
 * {@link DoubleTable} is a {@link NumberTable} of {@link Double}.
 */
public class DoubleTable
		extends NumberTable<Double> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -1687484064115022716L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link DoubleTable} of the specified numbers of rows and columns.
	 * <p>
	 * @param m the number of rows
	 * @param n the number of columns
	 */
	public DoubleTable(final int m, final int n) {
		super(Double.class, m, n);
	}

	/**
	 * Constructs a {@link DoubleTable} from the specified values.
	 * <p>
	 * @param values a 2D array of {@code double} values
	 */
	public DoubleTable(final double[]... values) {
		this(Doubles.toArray2D(values));
	}

	/**
	 * Constructs a {@link DoubleTable} from the specified elements.
	 * <p>
	 * @param elements a 2D array of {@link Double}
	 */
	public DoubleTable(final Double[]... elements) {
		super(Double.class, elements);
	}

	/**
	 * Constructs a {@link DoubleTable} from the specified header and values.
	 * <p>
	 * @param header an array of {@link String}
	 * @param values a 2D array of {@code double} values
	 */
	public DoubleTable(final String[] header, final double[]... values) {
		this(header, Doubles.toArray2D(values));
	}

	/**
	 * Constructs a {@link DoubleTable} from the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Double}
	 */
	public DoubleTable(final String[] header, final Double[]... elements) {
		super(Double.class, header, elements);
	}

	/**
	 * Constructs a {@link DoubleTable} imported from the specified file.
	 * <p>
	 * @param pathname  the pathname of the file to import
	 * @param hasHeader the option specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file
	 */
	public DoubleTable(final String pathname, final boolean hasHeader)
			throws IOException {
		super(Parsers.DOUBLE_PARSER, pathname, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public double[][] toPrimitiveArray2D() {
		return Doubles.toPrimitiveArray2D(elements);
	}
}
