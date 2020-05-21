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
import jupiter.common.model.ICloneable;
import jupiter.common.util.Characters;

/**
 * {@link CharacterTable} is the {@link Table} of {@link Character}.
 */
public class CharacterTable
		extends Table<Character> {

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
	 * Constructs a {@link CharacterTable} with the specified numbers of rows and columns.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public CharacterTable(final int rowCount, final int columnCount) {
		super(Character.class, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link CharacterTable} with the specified header and numbers of rows and
	 * columns.
	 * <p>
	 * @param header      an array of {@link String}
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public CharacterTable(final String[] header, final int rowCount, final int columnCount) {
		super(Character.class, header, rowCount, columnCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link CharacterTable} with the specified {@code char} values.
	 * <p>
	 * @param values a 2D {@code char} array
	 */
	public CharacterTable(final char[]... values) {
		this(Characters.toArray2D(values));
	}

	/**
	 * Constructs a {@link CharacterTable} with the specified elements.
	 * <p>
	 * @param elements a 2D array of {@link Character}
	 */
	public CharacterTable(final Character[]... elements) {
		super(Character.class, elements);
	}

	/**
	 * Constructs a {@link CharacterTable} with the specified header and values.
	 * <p>
	 * @param header an array of {@link String}
	 * @param values a 2D {@code char} array
	 */
	public CharacterTable(final String[] header, final char[]... values) {
		this(header, Characters.toArray2D(values));
	}

	/**
	 * Constructs a {@link CharacterTable} with specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Character}
	 */
	public CharacterTable(final String[] header, final Character[]... elements) {
		super(Character.class, header, elements);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link CharacterTable} loaded from the file denoted by the specified path.
	 * <p>
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public CharacterTable(final String path, final boolean hasHeader)
			throws IOException {
		super(IParsers.CHARACTER_PARSER, path, hasHeader);
	}

	/**
	 * Constructs a {@link CharacterTable} with the specified header loaded from the file denoted by
	 * the specified path.
	 * <p>
	 * @param header    an array of {@link String}
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public CharacterTable(final String[] header, final String path, final boolean hasHeader)
			throws IOException {
		super(header, IParsers.CHARACTER_PARSER, path, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code char} array containing all the elements of {@code this} in the same order,
	 * or {@code null} if {@code this} is empty.
	 * <p>
	 * @return a {@code char} array containing all the elements of {@code this} in the same order,
	 *         or {@code null} if {@code this} is empty
	 *
	 * @see Characters#toPrimitiveArray(Object[][])
	 */
	public char[] toPrimitiveArray() {
		return Characters.toPrimitiveArray(elements);
	}

	/**
	 * Returns a 2D {@code char} array containing all the elements of {@code this} in the same
	 * order, or {@code null} if {@code this} is empty.
	 * <p>
	 * @return a 2D {@code char} array containing all the elements of {@code this} in the same
	 *         order, or {@code null} if {@code this} is empty
	 *
	 * @see Characters#toPrimitiveArray2D(Object[][])
	 */
	public char[][] toPrimitiveArray2D() {
		return Characters.toPrimitiveArray2D(elements);
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
	public CharacterTable clone() {
		return (CharacterTable) super.clone();
	}
}
