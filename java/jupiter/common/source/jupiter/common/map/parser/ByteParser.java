/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.io.InputOutput.IO;

import jupiter.common.map.ObjectToByteMapper;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link ByteParser} is the {@link ObjectToByteMapper} parsing an input {@link Object} to an output
 * {@link Byte}.
 */
public class ByteParser
		extends ObjectToByteMapper
		implements IParser<Byte> {

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
	 * Constructs a {@link ByteParser}.
	 */
	public ByteParser() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PARSERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Byte parse(final Object input) {
		return call(input);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public Byte[] parseToArray(final Object[] input) {
		return callToArray(input);
	}

	public Byte[] parseAsArray(final Object... input) {
		return callToArray(input);
	}

	//////////////////////////////////////////////

	public Byte[][] parseToArray2D(final Object[][] input2D) {
		return callToArray2D(input2D);
	}

	public Byte[][] parseAsArray2D(final Object[]... input2D) {
		return callToArray2D(input2D);
	}

	//////////////////////////////////////////////

	public Byte[][][] parseToArray3D(final Object[][][] input3D) {
		return callToArray3D(input3D);
	}

	public Byte[][][] parseAsArray3D(final Object[][]... input3D) {
		return callToArray3D(input3D);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Byte call(final Object input) {
		if (input == null) {
			return null;
		}
		if (input instanceof Byte) {
			return (Byte) input;
		}
		if (input instanceof Number) {
			return ((Number) input).byteValue();
		}
		final String value = Objects.toStringWithNull(input);
		if (value == null) {
			return null;
		}
		try {
			return Byte.valueOf(value);
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
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public ByteParser clone() {
		return new ByteParser();
	}
}
