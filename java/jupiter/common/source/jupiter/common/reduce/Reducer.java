/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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

import jupiter.common.model.ICloneable;
import jupiter.common.test.Arguments;
import jupiter.common.thread.Worker;
import jupiter.common.util.Arrays;

/**
 * {@link Reducer} is the {@link Worker} reducing an {@code I} input array to an {@code O} output.
 * <p>
 * @param <I> the input type
 * @param <O> the output type
 */
public abstract class Reducer<I, O>
		extends Worker<I[], O> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The output {@link Class} of {@code O} type.
	 */
	protected Class<O> c;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Reducer} of {@code I} and {@code O} types.
	 */
	protected Reducer() {
		super();
	}

	/**
	 * Constructs a {@link Reducer} of {@code I} and {@code O} types with the specified output
	 * {@link Class}.
	 * <p>
	 * @param c the output {@link Class} of {@code O} type
	 */
	protected Reducer(final Class<O> c) {
		super();

		// Check the arguments
		Arguments.requireNonNull(c, "class");

		// Set the attributes
		this.c = c;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the output {@link Class} of {@code O} type.
	 * <p>
	 * @return the output {@link Class} of {@code O} type
	 */
	public Class<O> getOutputClass() {
		return c;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public O call(final I[] input) {
		return c.cast(input);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings({"unchecked", "varargs"})
	public O[] callToArray(final I[]... input2D) {
		final O[] output = Arrays.<O>create(c, input2D.length);
		for (int i = 0; i < input2D.length; ++i) {
			output[i] = call(input2D[i]);
		}
		return output;
	}

	//////////////////////////////////////////////

	@SuppressWarnings({"unchecked", "varargs"})
	public O[][] callToArray2D(final I[][]... input3D) {
		final O[][] output2D = Arrays.<O>create(c, input3D.length, 0);
		for (int i = 0; i < input3D.length; ++i) {
			output2D[i] = callToArray(input3D[i]);
		}
		return output2D;
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
	public Reducer<I, O> clone() {
		return this;
	}
}
