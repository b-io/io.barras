/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2026 Florian Barras <https://barras.io> (florian@barras.io)
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

import jupiter.common.transform.ObjectToCharacterMapper;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

/**
 * {@link CharacterConverter} is the {@link ObjectToCharacterMapper} converting an input {@link Object} to
 * an output {@link Character}.
 */
public class CharacterConverter
		extends ObjectToCharacterMapper
		implements IConverter<Character> {

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
	 * Constructs a {@link CharacterConverter}.
	 */
	public CharacterConverter() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Character convert(final Object input) {
		return call(input);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public Character[] toArray(final Object[] input) {
		return callToArray(input);
	}

	public Character[] asArray(final Object... input) {
		return callToArray(input);
	}

	//////////////////////////////////////////////

	public Character[][] toArray2D(final Object[][] input2D) {
		return callToArray2D(input2D);
	}

	public Character[][] asArray2D(final Object[]... input2D) {
		return callToArray2D(input2D);
	}

	//////////////////////////////////////////////

	public Character[][][] toArray3D(final Object[][][] input3D) {
		return callToArray3D(input3D);
	}

	public Character[][][] asArray3D(final Object[][]... input3D) {
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
	 *
	 * @return a clone of {@code this}
	 * @see ICloneable
	 */
	@Override
	public CharacterConverter clone() {
		return new CharacterConverter();
	}
}
