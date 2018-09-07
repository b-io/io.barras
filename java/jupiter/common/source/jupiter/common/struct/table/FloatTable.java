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
import jupiter.common.util.Floats;

/**
 * {@link FloatTable} is a {@link NumberTable} of {@link Float}.
 */
public class FloatTable
		extends NumberTable<Float> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -3588360028341343406L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link FloatTable} of the specified numbers of rows and columns.
	 * <p>
	 * @param m the number of rows
	 * @param n the number of columns
	 */
	public FloatTable(final int m, final int n) {
		super(Float.class, m, n);
	}

	/**
	 * Constructs a {@link FloatTable} of the specified header and numbers of rows and columns.
	 * <p>
	 * @param header an array of {@link String}
	 * @param m      the number of rows
	 * @param n      the number of columns
	 */
	public FloatTable(final String[] header, final int m, final int n) {
		super(Float.class, header, m, n);
	}

	/**
	 * Constructs a {@link FloatTable} from the specified values.
	 * <p>
	 * @param values a 2D array of {@code float} values
	 */
	public FloatTable(final float[]... values) {
		this(Floats.toArray2D(values));
	}

	/**
	 * Constructs a {@link FloatTable} from the specified elements.
	 * <p>
	 * @param elements a 2D array of {@link Float}
	 */
	public FloatTable(final Float[]... elements) {
		super(Float.class, elements);
	}

	/**
	 * Constructs a {@link FloatTable} from the specified header and values.
	 * <p>
	 * @param header an array of {@link String}
	 * @param values a 2D array of {@code float} values
	 */
	public FloatTable(final String[] header, final float[]... values) {
		this(header, Floats.toArray2D(values));
	}

	/**
	 * Constructs a {@link FloatTable} from the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Float}
	 */
	public FloatTable(final String[] header, final Float[]... elements) {
		super(Float.class, header, elements);
	}

	/**
	 * Constructs a {@link FloatTable} imported from the specified file.
	 * <p>
	 * @param pathname  the pathname of the file to import
	 * @param hasHeader the option specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file
	 */
	public FloatTable(final String pathname, final boolean hasHeader)
			throws IOException {
		super(Parsers.FLOAT_PARSER, pathname, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public float[][] toPrimitiveArray2D() {
		return Floats.toPrimitiveArray2D(elements);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public FloatTable clone() {
		return new FloatTable(header, elements);
	}
}
