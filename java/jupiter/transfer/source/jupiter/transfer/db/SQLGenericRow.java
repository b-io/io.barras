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
package jupiter.transfer.db;

import static jupiter.common.util.Characters.LEFT_BRACE;
import static jupiter.common.util.Characters.RIGHT_BRACE;

import java.io.Serializable;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class SQLGenericRow
		implements ICloneable<SQLGenericRow>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The header.
	 */
	public String[] header;
	/**
	 * The elements.
	 */
	public Object[] elements;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SQLGenericRow} with the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements an array
	 */
	public SQLGenericRow(final String[] header, final Object[] elements) {
		this.header = header;
		this.elements = elements;
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
	public SQLGenericRow clone() {
		try {
			final SQLGenericRow clone = (SQLGenericRow) super.clone();
			clone.header = Objects.clone(header);
			clone.elements = Objects.clone(elements);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = Strings.createBuilder(10 * header.length);
		builder.append(LEFT_BRACE);
		final int n = header.length;
		if (n == elements.length) {
			for (int i = 0; i < n; ++i) {
				builder.append(Strings.doubleQuote(header[i]));
				builder.append(": ");
				builder.append(Strings.doubleQuote(elements[i]));
				if (i != n - 1) {
					builder.append(Arrays.DELIMITER);
				}
			}
		}
		builder.append(RIGHT_BRACE);
		return builder.toString();
	}
}
