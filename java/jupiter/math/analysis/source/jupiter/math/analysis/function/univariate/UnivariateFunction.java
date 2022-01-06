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
package jupiter.math.analysis.function.univariate;

import java.io.Serializable;

import jupiter.common.math.Domain;
import jupiter.common.model.ICloneable;
import jupiter.common.test.IntervalArguments;
import jupiter.common.util.Bytes;
import jupiter.common.util.Floats;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Objects;
import jupiter.common.util.Shorts;

/**
 * {@link UnivariateFunction} is a univariate function defined in the {@link Domain}.
 */
public abstract class UnivariateFunction
		implements ICloneable<UnivariateFunction>, Serializable {

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
	 * The {@link Domain}.
	 */
	protected final Domain domain;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link UnivariateFunction} by default.
	 */
	protected UnivariateFunction() {
		this(Domain.ALL);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link UnivariateFunction} with the specified {@link Domain}.
	 * <p>
	 * @param domain the {@link Domain}
	 */
	protected UnivariateFunction(final Domain domain) {
		// Check the arguments
		IntervalArguments.requireValid(domain, "domain");

		// Set the attributes
		this.domain = domain;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Domain}.
	 * <p>
	 * @return the {@link Domain}
	 */
	public Domain getDomain() {
		return domain;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code x} if {@code x} is inside {@code domain}, the closest bound if {@code x} is
	 * not {@code NaN}, {@code NaN} otherwise.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code x} if {@code x} is inside {@code domain}, the closest bound if {@code x} is
	 *         not {@code NaN}, {@code NaN} otherwise
	 */
	public double bound(final double x) {
		return domain.bound(x);
	}

	/**
	 * Returns {@code x} if {@code x} is inside {@code domain}, {@code NaN} otherwise.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code x} if {@code x} is inside {@code domain}, {@code NaN} otherwise
	 */
	public double constrain(final double x) {
		return domain.constrain(x);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the function to the specified value.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code f(x)}
	 */
	protected abstract double a(final double x);

	//////////////////////////////////////////////

	/**
	 * Applies the function to the specified value.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code f(x)}
	 *
	 * @see #a(double)
	 */
	public double apply(final double x) {
		return a(bound(x));
	}

	/**
	 * Applies the function to the specified {@link Number}.
	 * <p>
	 * @param x a {@link Number} (on the abscissa)
	 * <p>
	 * @return {@code f(x)}
	 *
	 * @see #apply(double)
	 */
	public double apply(final Number x) {
		return apply(x.doubleValue());
	}

	//////////////////////////////////////////////

	public byte applyToByte(final double x) {
		return Bytes.convert(apply(x));
	}

	public short applyToShort(final double x) {
		return Shorts.convert(apply(x));
	}

	public int applyToInt(final double x) {
		return Integers.convert(apply(x));
	}

	public long applyToLong(final double x) {
		return Longs.convert(apply(x));
	}

	public float applyToFloat(final double x) {
		return Floats.convert(apply(x));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double[] applyToPrimitiveArray(final double... array) {
		final double[] result = new double[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = apply(array[i]);
		}
		return result;
	}

	public double[] applyToPrimitiveArray(final Number[] array) {
		final double[] result = new double[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = apply(array[i]);
		}
		return result;
	}

	//////////////////////////////////////////////

	public byte[] applyToPrimitiveByteArray(final double... array) {
		final byte[] result = new byte[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = applyToByte(array[i]);
		}
		return result;
	}

	public short[] applyToPrimitiveShortArray(final double... array) {
		final short[] result = new short[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = applyToShort(array[i]);
		}
		return result;
	}

	public int[] applyToPrimitiveIntArray(final double... array) {
		final int[] result = new int[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = applyToInt(array[i]);
		}
		return result;
	}

	public long[] applyToPrimitiveLongArray(final double... array) {
		final long[] result = new long[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = applyToLong(array[i]);
		}
		return result;
	}

	public float[] applyToPrimitiveFloatArray(final double... array) {
		final float[] result = new float[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = applyToFloat(array[i]);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double[][] applyToPrimitiveArray2D(final double[]... array2D) {
		final double[][] result = new double[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveArray(array2D[i]);
		}
		return result;
	}

	public double[][] applyToPrimitiveArray2D(final Number[]... array2D) {
		final double[][] result = new double[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveArray(array2D[i]);
		}
		return result;
	}

	//////////////////////////////////////////////

	public byte[][] applyToPrimitiveByteArray2D(final double[]... array2D) {
		final byte[][] result = new byte[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveByteArray(array2D[i]);
		}
		return result;
	}

	public short[][] applyToPrimitiveShortArray2D(final double[]... array2D) {
		final short[][] result = new short[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveShortArray(array2D[i]);
		}
		return result;
	}

	public int[][] applyToPrimitiveIntArray2D(final double[]... array2D) {
		final int[][] result = new int[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveIntArray(array2D[i]);
		}
		return result;
	}

	public long[][] applyToPrimitiveLongArray2D(final double[]... array2D) {
		final long[][] result = new long[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveLongArray(array2D[i]);
		}
		return result;
	}

	public float[][] applyToPrimitiveFloatArray2D(final double[]... array2D) {
		final float[][] result = new float[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveFloatArray(array2D[i]);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double[][][] applyToPrimitiveArray3D(final double[][]... array3D) {
		final double[][][] result = new double[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveArray2D(array3D[i]);
		}
		return result;
	}

	public double[][][] applyToPrimitiveArray3D(final Number[][]... array3D) {
		final double[][][] result = new double[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveArray2D(array3D[i]);
		}
		return result;
	}

	//////////////////////////////////////////////

	public byte[][][] applyToPrimitiveByteArray3D(final double[][]... array3D) {
		final byte[][][] result = new byte[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveByteArray2D(array3D[i]);
		}
		return result;
	}

	public short[][][] applyToPrimitiveShortArray3D(final double[][]... array3D) {
		final short[][][] result = new short[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveShortArray2D(array3D[i]);
		}
		return result;
	}

	public int[][][] applyToPrimitiveIntArray3D(final double[][]... array3D) {
		final int[][][] result = new int[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveIntArray2D(array3D[i]);
		}
		return result;
	}

	public long[][][] applyToPrimitiveLongArray3D(final double[][]... array3D) {
		final long[][][] result = new long[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveLongArray2D(array3D[i]);
		}
		return result;
	}

	public float[][][] applyToPrimitiveFloatArray3D(final double[][]... array3D) {
		final float[][][] result = new float[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveFloatArray2D(array3D[i]);
		}
		return result;
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
	public UnivariateFunction clone() {
		try {
			return (UnivariateFunction) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}
}
