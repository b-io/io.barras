/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2025 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.common.transform.converter;

import static jupiter.common.io.InputOutput.IO;

import jupiter.common.transform.ObjectToDoubleMapper;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link DoubleConverter} is the {@link ObjectToDoubleMapper} converting an input {@link Object} to an
 * output {@link Double}.
 */
public class DoubleConverter
		extends ObjectToDoubleMapper
		implements IConverter<Double> {

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
	 * Constructs a {@link DoubleConverter}.
	 */
	public DoubleConverter() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Double convert(final Object input) {
		return call(input);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public Double[] toArray(final Object[] input) {
		return callToArray(input);
	}

	public Double[] asArray(final Object... input) {
		return callToArray(input);
	}

	//////////////////////////////////////////////

	public Double[][] toArray2D(final Object[][] input2D) {
		return callToArray2D(input2D);
	}

	public Double[][] asArray2D(final Object[]... input2D) {
		return callToArray2D(input2D);
	}

	//////////////////////////////////////////////

	public Double[][][] toArray3D(final Object[][][] input3D) {
		return callToArray3D(input3D);
	}

	public Double[][][] asArray3D(final Object[][]... input3D) {
		return callToArray3D(input3D);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
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
		final String value = Objects.toStringWithNull(input);
		if (value == null) {
			return null;
		}
		try {
			return Double.valueOf(value);
		} catch (final NumberFormatException ignored) {
			IO.error("Cannot convert", Strings.quote(input), "to a", Objects.getName(c));
		}
		return null;
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
	public DoubleConverter clone() {
		return new DoubleConverter();
	}
}
