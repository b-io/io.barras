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
package jupiter.common.reduce;

import jupiter.common.util.Arrays;

/**
 * {@link Reducer} is an operator reducing an array of {@code I} objects to an {@code O} object.
 * <p>
 * @param <I> the input type
 * @param <O> the output type
 */
public class Reducer<I, O> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final Class<O> c;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Reducer(final Class<O> c) {
		this.c = c;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CALLABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	public O call(final I... input) {
		return c.cast(input);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public O[] callToArray(final I[]... input2D) {
		final O[] result = Arrays.<O>create(c, input2D.length);
		for (int i = 0; i < input2D.length; ++i) {
			result[i] = call(input2D[i]);
		}
		return result;
	}

	public O[][] callToArray2D(final I[][]... input3D) {
		final O[][] result = Arrays.<O>create(c, input3D.length, 0);
		for (int i = 0; i < input3D.length; ++i) {
			result[i] = callToArray(input3D[i]);
		}
		return result;
	}
}
