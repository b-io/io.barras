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
package jupiter.common.map;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.set.ExtendedHashSet;
import jupiter.common.test.Arguments;
import jupiter.common.thread.Worker;
import jupiter.common.util.Arrays;

/**
 * {@link Mapper} is the {@link Worker} mapping an {@code I} input to an {@code O} output.
 * <p>
 * @param <I> the input type
 * @param <O> the output type
 */
public abstract class Mapper<I, O>
		extends Worker<I, O> {

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
	 * Constructs a {@link Mapper} of {@code I} and {@code O} types.
	 */
	protected Mapper() {
		super();
	}

	/**
	 * Constructs a {@link Mapper} of {@code I} and {@code O} types with the specified output
	 * {@link Class}.
	 * <p>
	 * @param c the output {@link Class} of {@code O} type
	 */
	protected Mapper(final Class<O> c) {
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

	/**
	 * Sets the output {@link Class}.
	 * <p>
	 * @param c a {@link Class} of {@code O} type
	 */
	public void setOutputClass(final Class<O> c) {
		this.c = c;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public O call(final I input) {
		return c.cast(input);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public O[] callToArray(final I[] input) {
		final O[] output = Arrays.<O>create(c, input.length);
		for (int i = 0; i < input.length; ++i) {
			output[i] = call(input[i]);
		}
		return output;
	}

	public O[] callToArray(final I[][] input2D) {
		final int n = input2D[0].length;
		final O[] output = Arrays.<O>create(c, input2D.length * n);
		for (int i = 0; i < input2D.length; ++i) {
			for (int j = 0; j < n; ++j) {
				output[i * n + j] = call(input2D[i][j]);
			}
		}
		return output;
	}

	public O[] callToArray(final I[][][] input3D) {
		final int n = input3D[0].length;
		final int o = input3D[0][0].length;
		final O[] output = Arrays.<O>create(c, input3D.length * n * o);
		for (int i = 0; i < input3D.length; ++i) {
			for (int j = 0; j < n; ++j) {
				for (int k = 0; k < o; ++k) {
					output[i * n + j * o + k] = call(input3D[i][j][k]);
				}
			}
		}
		return output;
	}

	//////////////////////////////////////////////

	public O[][] callToArray2D(final I[][] input2D) {
		final O[][] output2D = Arrays.<O>create(c, input2D.length, 0);
		for (int i = 0; i < input2D.length; ++i) {
			output2D[i] = callToArray(input2D[i]);
		}
		return output2D;
	}

	//////////////////////////////////////////////

	public O[][][] callToArray3D(final I[][][] input3D) {
		final O[][][] output3D = Arrays.<O>create(c, input3D.length, 0, 0);
		for (int i = 0; i < input3D.length; ++i) {
			output3D[i] = callToArray2D(input3D[i]);
		}
		return output3D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public ExtendedList<O> callToList(final I[] input) {
		final ExtendedList<O> output = new ExtendedList<O>(input.length);
		for (final I element : input) {
			output.add(call(element));
		}
		return output;
	}

	public ExtendedLinkedList<O> callToLinkedList(final I[] input) {
		final ExtendedLinkedList<O> output = new ExtendedLinkedList<O>();
		for (final I element : input) {
			output.add(call(element));
		}
		return output;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public ExtendedHashSet<O> callToSet(final I[] input) {
		final ExtendedHashSet<O> output = new ExtendedHashSet<O>(input.length);
		for (final I element : input) {
			output.add(call(element));
		}
		return output;
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
	public Mapper<I, O> clone() {
		return this;
	}
}
