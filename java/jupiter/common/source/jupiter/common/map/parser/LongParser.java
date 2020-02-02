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
package jupiter.common.map.parser;

import static jupiter.common.io.IO.IO;

import jupiter.common.map.ObjectToLongMapper;
import jupiter.common.util.Strings;

/**
 * {@link LongParser} is the {@link ObjectToLongMapper} parsing an input {@link Object} to an output
 * {@link Long}.
 */
public class LongParser
		extends ObjectToLongMapper
		implements IParser<Long> {

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
	 * Constructs a {@link LongParser}.
	 */
	public LongParser() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CALLABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Long call(final Object input) {
		if (input == null) {
			return null;
		}
		if (input instanceof Long) {
			return (Long) input;
		}
		if (input instanceof Number) {
			return ((Number) input).longValue();
		}
		final String value = Strings.toStringWithNull(input);
		if (value == null) {
			return null;
		}
		try {
			return Long.valueOf(value);
		} catch (final NumberFormatException ignored) {
			IO.error("Cannot convert ", Strings.quote(input), " to a ", c.getSimpleName());
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PARSERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Long parse(final Object input) {
		return call(input);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public Long[] parseToArray(final Object[] input) {
		return callToArray(input);
	}

	public Long[] parseAsArray(final Object... input) {
		return callToArray(input);
	}

	//////////////////////////////////////////////

	public Long[][] parseToArray2D(final Object[][] input2D) {
		return callToArray2D(input2D);
	}

	public Long[][] parseAsArray2D(final Object[]... input2D) {
		return callToArray2D(input2D);
	}

	//////////////////////////////////////////////

	public Long[][][] parseToArray3D(final Object[][][] input3D) {
		return callToArray3D(input3D);
	}

	public Long[][][] parseAsArray3D(final Object[][]... input3D) {
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
	public LongParser clone() {
		return new LongParser();
	}
}
