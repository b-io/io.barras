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
import jupiter.common.util.Doubles;

/**
 * {@link DoubleTable} is the {@link NumberTable} of {@link Double}.
 */
public class DoubleTable
		extends NumberTable<Double> {

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
	 * Constructs a {@link DoubleTable} with the specified numbers of rows and columns.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public DoubleTable(final int rowCount, final int columnCount) {
		super(Double.class, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link DoubleTable} with the specified header and numbers of rows and columns.
	 * <p>
	 * @param header      an array of {@link String} (may be {@code null})
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public DoubleTable(final String[] header, final int rowCount, final int columnCount) {
		super(Double.class, header, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link DoubleTable} with the specified index, header and numbers of rows and
	 * columns.
	 * <p>
	 * @param index       an array of {@link Object} (may be {@code null})
	 * @param header      an array of {@link String} (may be {@code null})
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public DoubleTable(final Object[] index, final String[] header, final int rowCount,
			final int columnCount) {
		super(Double.class, index, header, rowCount, columnCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link DoubleTable} with the specified {@code double} values.
	 * <p>
	 * @param values a 2D {@code double} array
	 */
	public DoubleTable(final double[]... values) {
		this(Doubles.toArray2D(values));
	}

	/**
	 * Constructs a {@link DoubleTable} with the specified elements.
	 * <p>
	 * @param elements a 2D array of {@link Double}
	 */
	public DoubleTable(final Double[]... elements) {
		super(Double.class, elements);
	}

	/**
	 * Constructs a {@link DoubleTable} with the specified header and values.
	 * <p>
	 * @param header an array of {@link String}
	 * @param values a 2D {@code double} array
	 */
	public DoubleTable(final String[] header, final double[]... values) {
		this(header, Doubles.toArray2D(values));
	}

	/**
	 * Constructs a {@link DoubleTable} with the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Double}
	 */
	public DoubleTable(final String[] header, final Double[]... elements) {
		super(Double.class, header, elements);
	}

	/**
	 * Constructs a {@link DoubleTable} with the specified index, header and values.
	 * <p>
	 * @param index  an array of {@link Object} (may be {@code null})
	 * @param header an array of {@link String}
	 * @param values a 2D {@code double} array
	 */
	public DoubleTable(final Object[] index, final String[] header, final double[]... values) {
		this(index, header, Doubles.toArray2D(values));
	}

	/**
	 * Constructs a {@link DoubleTable} with specified index, header and elements.
	 * <p>
	 * @param index    an array of {@link Object} (may be {@code null})
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Double}
	 */
	public DoubleTable(final Object[] index, final String[] header, final Double[]... elements) {
		super(Double.class, index, header, elements);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link DoubleTable} loaded from the file denoted by the specified path.
	 * <p>
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public DoubleTable(final String path, final boolean hasHeader)
			throws IOException {
		super(IParsers.DOUBLE_PARSER, path, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code double} array containing all the elements of {@code this} in the same order,
	 * or {@code null} if {@code this} is empty.
	 * <p>
	 * @return a {@code double} array containing all the elements of {@code this} in the same order,
	 *         or {@code null} if {@code this} is empty
	 *
	 * @see Doubles#toPrimitiveArray(Object[][])
	 */
	public double[] toPrimitiveArray() {
		return Doubles.toPrimitiveArray(elements);
	}

	/**
	 * Returns a 2D {@code double} array containing all the elements of {@code this} in the same
	 * order, or {@code null} if {@code this} is empty.
	 * <p>
	 * @return a 2D {@code double} array containing all the elements of {@code this} in the same
	 *         order, or {@code null} if {@code this} is empty
	 *
	 * @see Doubles#toPrimitiveArray2D(Object[][])
	 */
	public double[][] toPrimitiveArray2D() {
		return Doubles.toPrimitiveArray2D(elements);
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
	public DoubleTable clone() {
		return (DoubleTable) super.clone();
	}
}
