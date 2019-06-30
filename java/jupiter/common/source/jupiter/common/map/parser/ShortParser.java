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
package jupiter.common.map.parser;

import static jupiter.common.io.IO.IO;

import jupiter.common.map.ObjectToShortMapper;
import jupiter.common.util.Strings;

/**
 * {@link ShortParser} is an {@link ObjectToShortMapper} parsing an input {@link Object} to an
 * output {@link Short}.
 */
public class ShortParser
		extends ObjectToShortMapper
		implements IParser<Short> {

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
	 * Constructs a {@link ShortParser}.
	 */
	public ShortParser() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CALLABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Short call(final Object input) {
		if (input == null) {
			return null;
		}
		if (input instanceof Short) {
			return (Short) input;
		}
		if (input instanceof Number) {
			return ((Number) input).shortValue();
		}
		final String value = Strings.toStringWithNull(input);
		if (value == null) {
			return null;
		}
		try {
			return Short.valueOf(value);
		} catch (final NumberFormatException ignored) {
			IO.error("Cannot convert ", Strings.quote(input), " to a ", c.getSimpleName());
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PARSER
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Short parse(final Object input) {
		return call(input);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public Short[] parseToArray(final Object[] input) {
		return callToArray(input);
	}

	public Short[] parseAsArray(final Object... input) {
		return callToArray(input);
	}

	//////////////////////////////////////////////

	public Short[][] parseToArray2D(final Object[][] input2D) {
		return callToArray2D(input2D);
	}

	public Short[][] parseAsArray2D(final Object[]... input2D) {
		return callToArray2D(input2D);
	}

	//////////////////////////////////////////////

	public Short[][][] parseToArray3D(final Object[][][] input3D) {
		return callToArray3D(input3D);
	}

	public Short[][][] parseAsArray3D(final Object[][]... input3D) {
		return callToArray3D(input3D);
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
	public ShortParser clone() {
		return new ShortParser();
	}
}
