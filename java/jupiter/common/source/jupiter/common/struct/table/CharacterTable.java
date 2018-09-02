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
import jupiter.common.util.Characters;

/**
 * {@link CharacterTable} is a {@link Table} of {@link Character}.
 */
public class CharacterTable
		extends Table<Character> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 6300218397547054958L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link CharacterTable} of the specified numbers of rows and columns.
	 * <p>
	 * @param m the number of rows
	 * @param n the number of columns
	 */
	public CharacterTable(final int m, final int n) {
		super(Character.class, m, n);
	}

	/**
	 * Constructs a {@link CharacterTable} of the specified header and numbers of rows and columns.
	 * <p>
	 * @param header an array of {@link String}
	 * @param m      the number of rows
	 * @param n      the number of columns
	 */
	public CharacterTable(final String[] header, final int m, final int n) {
		super(Character.class, header, m, n);
	}

	/**
	 * Constructs a {@link CharacterTable} from the specified values.
	 * <p>
	 * @param values a 2D array of {@code char} values
	 */
	public CharacterTable(final char[]... values) {
		this(Characters.toArray2D(values));
	}

	/**
	 * Constructs a {@link CharacterTable} from the specified elements.
	 * <p>
	 * @param elements a 2D array of {@link Character}
	 */
	public CharacterTable(final Character[]... elements) {
		super(Character.class, elements);
	}

	/**
	 * Constructs a {@link CharacterTable} from the specified header and values.
	 * <p>
	 * @param header an array of {@link String}
	 * @param values a 2D array of {@code char} values
	 */
	public CharacterTable(final String[] header, final char[]... values) {
		this(header, Characters.toArray2D(values));
	}

	/**
	 * Constructs a {@link CharacterTable} from the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of {@link Character}
	 */
	public CharacterTable(final String[] header, final Character[]... elements) {
		super(Character.class, header, elements);
	}

	/**
	 * Constructs a {@link CharacterTable} imported from the specified file.
	 * <p>
	 * @param pathname  the pathname of the file to import
	 * @param hasHeader the option specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file
	 */
	public CharacterTable(final String pathname, final boolean hasHeader)
			throws IOException {
		super(Parsers.CHARACTER_PARSER, pathname, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public char[][] toPrimitiveArray2D() {
		return Characters.toPrimitiveArray2D(elements);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public CharacterTable clone() {
		return new CharacterTable(header, elements);
	}
}
