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

import jupiter.common.transform.ObjectToBooleanMapper;
import jupiter.common.model.ICloneable;

/**
 * {@link BooleanConverter} is the {@link ObjectToBooleanMapper} converting an input {@link Object} to an
 * output {@link Boolean}.
 */
public class BooleanConverter
		extends ObjectToBooleanMapper
		implements IConverter<Boolean> {

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
	 * Constructs a {@link BooleanConverter}.
	 */
	public BooleanConverter() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Boolean convert(final Object input) {
		return call(input);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public Boolean[] toArray(final Object[] input) {
		return callToArray(input);
	}

	public Boolean[] asArray(final Object... input) {
		return callToArray(input);
	}

	//////////////////////////////////////////////

	public Boolean[][] toArray2D(final Object[][] input2D) {
		return callToArray2D(input2D);
	}

	public Boolean[][] asArray2D(final Object[]... input2D) {
		return callToArray2D(input2D);
	}

	//////////////////////////////////////////////

	public Boolean[][][] toArray3D(final Object[][][] input3D) {
		return callToArray3D(input3D);
	}

	public Boolean[][][] asArray3D(final Object[][]... input3D) {
		return callToArray3D(input3D);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Boolean call(final Object input) {
		if (input == null) {
			return null;
		}
		if (input instanceof Boolean) {
			return (Boolean) input;
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
	public BooleanConverter clone() {
		return new BooleanConverter();
	}
}
