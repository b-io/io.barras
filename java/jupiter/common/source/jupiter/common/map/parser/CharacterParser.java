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

import jupiter.common.map.ObjectToCharacterMapper;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

/**
 * {@link CharacterParser} is the {@link ObjectToCharacterMapper} parsing an input {@link Object} to
 * an output {@link Character}.
 */
public class CharacterParser
		extends ObjectToCharacterMapper
		implements IParser<Character> {

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
	 * Constructs a {@link CharacterParser}.
	 */
	public CharacterParser() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PARSERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Character parse(final Object input) {
		return call(input);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public Character[] parseToArray(final Object[] input) {
		return callToArray(input);
	}

	public Character[] parseAsArray(final Object... input) {
		return callToArray(input);
	}

	//////////////////////////////////////////////

	public Character[][] parseToArray2D(final Object[][] input2D) {
		return callToArray2D(input2D);
	}

	public Character[][] parseAsArray2D(final Object[]... input2D) {
		return callToArray2D(input2D);
	}

	//////////////////////////////////////////////

	public Character[][][] parseToArray3D(final Object[][][] input3D) {
		return callToArray3D(input3D);
	}

	public Character[][][] parseAsArray3D(final Object[][]... input3D) {
		return callToArray3D(input3D);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
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
			inputCharSequence = Objects.toStringWithNull(input);
			if (inputCharSequence == null) {
				return null;
			}
		}
		if (inputCharSequence.length() > 0) {
			return inputCharSequence.charAt(0);
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
	public CharacterParser clone() {
		return new CharacterParser();
	}
}
