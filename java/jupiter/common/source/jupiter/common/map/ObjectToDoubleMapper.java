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

import java.util.Collection;

import jupiter.common.model.ICloneable;

/**
 * {@link ObjectToDoubleMapper} is the {@link ObjectMapper} mapping an input {@link Object} to an
 * output {@link Double}.
 */
public abstract class ObjectToDoubleMapper
		extends ObjectMapper<Double> {

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
	 * Constructs an {@link ObjectToDoubleMapper}.
	 */
	protected ObjectToDoubleMapper() {
		super(Double.class);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public <I> double callToPrimitive(final I input) {
		return call(input);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public <I> double[] callToPrimitiveArray(final I[] input) {
		final double[] output = new double[input.length];
		for (int i = 0; i < input.length; ++i) {
			output[i] = call(input[i]);
		}
		return output;
	}

	public <I> double[] callToPrimitiveArray(final I[][] input2D) {
		final int n = input2D[0].length;
		final double[] output = new double[input2D.length * n];
		for (int i = 0; i < input2D.length; ++i) {
			for (int j = 0; j < n; ++j) {
				output[i * n + j] = call(input2D[i][j]);
			}
		}
		return output;
	}

	public <I> double[] callToPrimitiveArray(final I[][][] input3D) {
		final int n = input3D[0].length;
		final int o = input3D[0][0].length;
		final double[] output = new double[input3D.length * n * o];
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

	public <I> double[][] callToPrimitiveArray2D(final I[][] input2D) {
		final double[][] output2D = new double[input2D.length][];
		for (int i = 0; i < input2D.length; ++i) {
			output2D[i] = callToPrimitiveArray(input2D[i]);
		}
		return output2D;
	}

	//////////////////////////////////////////////

	public <I> double[][][] callToPrimitiveArray3D(final I[][][] input3D) {
		final double[][][] output3D = new double[input3D.length][][];
		for (int i = 0; i < input3D.length; ++i) {
			output3D[i] = callToPrimitiveArray2D(input3D[i]);
		}
		return output3D;
	}

	//////////////////////////////////////////////

	public <I> double[] callCollectionToPrimitiveArray(final Collection<I> input) {
		final double[] output = new double[input.size()];
		int i = 0;
		for (final I element : input) {
			output[i++] = call(element);
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
	public ObjectToDoubleMapper clone() {
		return this;
	}
}
