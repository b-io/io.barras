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

import jupiter.common.map.ObjectToDoubleMapper;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link DoubleParser} is the {@link ObjectToDoubleMapper} parsing an input {@link Object} to an
 * output {@link Double}.
 */
public class DoubleParser
		extends ObjectToDoubleMapper
		implements IParser<Double> {

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
	 * Constructs a {@link DoubleParser}.
	 */
	public DoubleParser() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CALLABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Double call(final Object input) {
		if (input == null) {
			return null;
		}
		if (input instanceof Double) {
			return (Double) input;
		}
		if (input instanceof Number) {
			return ((Number) input).doubleValue();
		}
		final String value = Strings.toStringWithNull(input);
		if (value == null) {
			return null;
		}
		try {
			return Double.valueOf(value);
		} catch (final NumberFormatException ignored) {
			IO.error("Cannot convert ", Strings.quote(input), " to a ", Objects.getName(c));
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PARSERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Double parse(final Object input) {
		return call(input);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public Double[] parseToArray(final Object[] input) {
		return callToArray(input);
	}

	public Double[] parseAsArray(final Object... input) {
		return callToArray(input);
	}

	//////////////////////////////////////////////

	public Double[][] parseToArray2D(final Object[][] input2D) {
		return callToArray2D(input2D);
	}

	public Double[][] parseAsArray2D(final Object[]... input2D) {
		return callToArray2D(input2D);
	}

	//////////////////////////////////////////////

	public Double[][][] parseToArray3D(final Object[][][] input3D) {
		return callToArray3D(input3D);
	}

	public Double[][][] parseAsArray3D(final Object[][]... input3D) {
		return callToArray3D(input3D);
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
	public DoubleParser clone() {
		return new DoubleParser();
	}
}
