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
import jupiter.common.util.Bytes;

/**
 * {@link ByteTable} is the {@link NumberTable} of {@link Byte}.
 */
public class ByteTable
		extends NumberTable<Byte> {

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
	 * Constructs a {@link ByteTable} with the specified numbers of rows and columns.
	 *
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public ByteTable(final int rowCount, final int columnCount) {
		super(Byte.class, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link ByteTable} with the specified header and numbers of rows and columns.
	 *
	 * @param header      an array of {@link String} (may be {@code null})
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public ByteTable(final String[] header, final int rowCount, final int columnCount) {
		super(Byte.class, header, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link ByteTable} with the specified index, header and numbers of rows and
	 * columns.
	 *
	 * @param index       an array of {@link Object} (may be {@code null})
	 * @param header      an array of {@link String} (may be {@code null})
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public ByteTable(final Object[] index, final String[] header, final int rowCount,
			final int columnCount) {
		super(Byte.class, index, header, rowCount, columnCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ByteTable} with the specified {@code byte} values.
	 *
	 * @param values a 2D {@code byte} array
	 */
	public ByteTable(final byte[]... values) {
		this(Bytes.toArray2D(values));
	}

	/**
	 * Constructs a {@link ByteTable} with the specified elements.
	 *
	 * @param elements a 2D array of {@link Byte}
	 */
	public ByteTable(final Byte[]... elements) {
		super(Byte.class, elements);
	}

	/**
	 * Constructs a {@link ByteTable} with the specified header and values.
	 *
	 * @param header an array of {@link String}
	 * @param values a 2D {@code byte} array
	 */
	public ByteTable(final String[] header, final byte[]... values) {
		this(header, Bytes.toArray2D(values));
	}

	/**
	 * Constructs a {@link ByteTable} with the specified header and elements.
	 *
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Byte}
	 */
	public ByteTable(final String[] header, final Byte[]... elements) {
		super(Byte.class, header, elements);
	}

	/**
	 * Constructs a {@link ByteTable} with the specified index, header and values.
	 *
	 * @param index  an array of {@link Object} (may be {@code null})
	 * @param header an array of {@link String}
	 * @param values a 2D {@code byte} array
	 */
	public ByteTable(final Object[] index, final String[] header, final byte[]... values) {
		this(index, header, Bytes.toArray2D(values));
	}

	/**
	 * Constructs a {@link ByteTable} with specified index, header and elements.
	 *
	 * @param index    an array of {@link Object} (may be {@code null})
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Byte}
	 */
	public ByteTable(final Object[] index, final String[] header, final Byte[]... elements) {
		super(Byte.class, index, header, elements);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ByteTable} loaded from the file denoted by the specified path.
	 *
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public ByteTable(final String path, final boolean hasHeader)
			throws IOException {
		super(IConverters.BYTE_CONVERTER, path, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code byte} array containing all the elements of {@code this} in the same order,
	 * or {@code null} if {@code this} is empty.
	 *
	 * @return a {@code byte} array containing all the elements of {@code this} in the same order,
	 *         or {@code null} if {@code this} is empty
	 *
	 * @see Bytes#toPrimitiveArray(Object[][])
	 */
	public byte[] toPrimitiveArray() {
		return Bytes.toPrimitiveArray(elements);
	}

	/**
	 * Returns a 2D {@code byte} array containing all the elements of {@code this} in the same
	 * order, or {@code null} if {@code this} is empty.
	 *
	 * @return a 2D {@code byte} array containing all the elements of {@code this} in the same
	 *         order, or {@code null} if {@code this} is empty
	 *
	 * @see Bytes#toPrimitiveArray2D(Object[][])
	 */
	public byte[][] toPrimitiveArray2D() {
		return Bytes.toPrimitiveArray2D(elements);
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
	public ByteTable clone() {
		return (ByteTable) super.clone();
	}
}
