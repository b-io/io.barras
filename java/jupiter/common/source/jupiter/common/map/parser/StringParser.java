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
package jupiter.common.map.parser;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.util.Strings;

/**
 * {@link StringParser} is a map operator parsing an {@link Object} to a {@link String}.
 */
public class StringParser
		extends ObjectToStringMapper
		implements Parser<String> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public StringParser() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CALLABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String call(final Object input) {
		if (input == null) {
			return null;
		}
		if (input instanceof String) {
			return (String) input;
		}
		return Strings.toString(input);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PARSER
	////////////////////////////////////////////////////////////////////////////////////////////////

	public String parse(final Object input) {
		return call(input);
	}

	public String[] parseToArray(final Object... input) {
		return callToArray(input);
	}

	public String[][] parseToArray2D(final Object[]... input) {
		return callToArray2D(input);
	}

	public String[][][] parseToArray3D(final Object[][]... input) {
		return callToArray3D(input);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public StringParser clone() {
		return new StringParser();
	}
}
