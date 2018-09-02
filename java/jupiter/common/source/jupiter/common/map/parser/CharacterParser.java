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

import jupiter.common.map.ObjectToCharacterMapper;
import jupiter.common.util.Strings;

/**
 * {@link CharacterParser} is a map operator parsing an {@link Object} to a {@link Character}.
 */
public class CharacterParser
		extends ObjectToCharacterMapper
		implements Parser<Character> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public CharacterParser() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CALLABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Character call(final Object input) {
		if (input == null) {
			return null;
		}
		if (input instanceof Character) {
			return (Character) input;
		}
		final CharSequence inputCharSequence;
		if (input instanceof CharSequence) {
			inputCharSequence = (CharSequence) input;
		} else {
			inputCharSequence = Strings.toString(input);
		}
		if (inputCharSequence.length() > 0) {
			return inputCharSequence.charAt(0);
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PARSER
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Character parse(final Object input) {
		return call(input);
	}

	public Character[] parseToArray(final Object... input) {
		return callToArray(input);
	}

	public Character[][] parseToArray2D(final Object[]... input) {
		return callToArray2D(input);
	}

	public Character[][][] parseToArray3D(final Object[][]... input) {
		return callToArray3D(input);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public CharacterParser clone() {
		return new CharacterParser();
	}
}
