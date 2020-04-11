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
package jupiter.math.analysis.function.parametric;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Bytes;
import jupiter.common.util.Doubles;
import jupiter.common.util.Floats;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Shorts;
import jupiter.math.analysis.function.univariate.UnivariateFunction;

/**
 * {@link ParametricFunction} is the parametric {@link UnivariateFunction}.
 */
public abstract class ParametricFunction
		extends UnivariateFunction
		implements IParametricFunction {

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
	 * The {@code double} parameters.
	 */
	protected final double[] parameters;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ParametricFunction} by default.
	 */
	protected ParametricFunction() {
		this(Doubles.EMPTY_PRIMITIVE_ARRAY);
	}

	/**
	 * Constructs a {@link ParametricFunction} with the specified parameters.
	 * <p>
	 * @param parameters the {@code double} parameters
	 */
	protected ParametricFunction(final double... parameters) {
		super();
		this.parameters = parameters;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the parametric function to the specified value with the parameters.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code f(x, parameters)}
	 *
	 * @see #a(double, double...)
	 */
	@Override
	protected double a(final double x) {
		return a(x, parameters);
	}

	/**
	 * Applies the parametric function to the specified value with the parameters.
	 * <p>
	 * @param x          a {@code double} value (on the abscissa)
	 * @param parameters the {@code double} parameters
	 * <p>
	 * @return {@code f(x, parameters)}
	 */
	protected abstract double a(final double x, final double... parameters);

	//////////////////////////////////////////////

	/**
	 * Applies the parametric function to the specified value with the parameters.
	 * <p>
	 * @param x          a {@code double} value (on the abscissa)
	 * @param parameters the {@code double} parameters
	 * <p>
	 * @return {@code f(x, parameters)}
	 *
	 * @see #a(double, double...)
	 */
	public double apply(final double x, final double... parameters) {
		return a(bound(x), parameters);
	}

	/**
	 * Applies the parametric function to the specified {@link Number} with the parameters.
	 * <p>
	 * @param x          a {@link Number} (on the abscissa)
	 * @param parameters the {@code double} parameters
	 * <p>
	 * @return {@code f(x, parameters)}
	 *
	 * @see #apply(double, double...)
	 */
	public double apply(final Number x, final double... parameters) {
		return apply(x.doubleValue(), parameters);
	}

	//////////////////////////////////////////////

	public byte applyToByte(final double x, final double... parameters) {
		return Bytes.convert(apply(x, parameters));
	}

	public short applyToShort(final double x, final double... parameters) {
		return Shorts.convert(apply(x, parameters));
	}

	public int applyToInt(final double x, final double... parameters) {
		return Integers.convert(apply(x, parameters));
	}

	public long applyToLong(final double x, final double... parameters) {
		return Longs.convert(apply(x, parameters));
	}

	public float applyToFloat(final double x, final double... parameters) {
		return Floats.convert(apply(x, parameters));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double[] applyToPrimitiveArray(final double[] array, final double... parameters) {
		final double[] result = new double[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = apply(array[i], parameters);
		}
		return result;
	}

	public double[] applyToPrimitiveArray(final Number[] array, final double... parameters) {
		final double[] result = new double[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = apply(array[i], parameters);
		}
		return result;
	}

	//////////////////////////////////////////////

	public byte[] applyToPrimitiveByteArray(final double[] array, final double... parameters) {
		final byte[] result = new byte[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = applyToByte(array[i], parameters);
		}
		return result;
	}

	public short[] applyToPrimitiveShortArray(final double[] array, final double... parameters) {
		final short[] result = new short[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = applyToShort(array[i], parameters);
		}
		return result;
	}

	public int[] applyToPrimitiveIntArray(final double[] array, final double... parameters) {
		final int[] result = new int[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = applyToInt(array[i], parameters);
		}
		return result;
	}

	public long[] applyToPrimitiveLongArray(final double[] array, final double... parameters) {
		final long[] result = new long[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = applyToLong(array[i], parameters);
		}
		return result;
	}

	public float[] applyToPrimitiveFloatArray(final double[] array, final double... parameters) {
		final float[] result = new float[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = applyToFloat(array[i], parameters);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double[][] applyToPrimitiveArray2D(final double[][] array2D,
			final double... parameters) {
		final double[][] result = new double[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveArray(array2D[i], parameters);
		}
		return result;
	}

	public double[][] applyToPrimitiveArray2D(final Number[][] array2D,
			final double... parameters) {
		final double[][] result = new double[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveArray(array2D[i], parameters);
		}
		return result;
	}

	//////////////////////////////////////////////

	public byte[][] applyToPrimitiveByteArray2D(final double[][] array2D,
			final double... parameters) {
		final byte[][] result = new byte[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveByteArray(array2D[i], parameters);
		}
		return result;
	}

	public short[][] applyToPrimitiveShortArray2D(final double[][] array2D,
			final double... parameters) {
		final short[][] result = new short[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveShortArray(array2D[i], parameters);
		}
		return result;
	}

	public int[][] applyToPrimitiveIntArray2D(final double[][] array2D,
			final double... parameters) {
		final int[][] result = new int[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveIntArray(array2D[i], parameters);
		}
		return result;
	}

	public long[][] applyToPrimitiveLongArray2D(final double[][] array2D,
			final double... parameters) {
		final long[][] result = new long[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveLongArray(array2D[i], parameters);
		}
		return result;
	}

	public float[][] applyToPrimitiveFloatArray2D(final double[][] array2D,
			final double... parameters) {
		final float[][] result = new float[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = applyToPrimitiveFloatArray(array2D[i], parameters);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double[][][] applyToPrimitiveArray3D(final double[][][] array3D,
			final double... parameters) {
		final double[][][] result = new double[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveArray2D(array3D[i], parameters);
		}
		return result;
	}

	public double[][][] applyToPrimitiveArray3D(final Number[][][] array3D,
			final double... parameters) {
		final double[][][] result = new double[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveArray2D(array3D[i], parameters);
		}
		return result;
	}

	//////////////////////////////////////////////

	public byte[][][] applyToPrimitiveByteArray3D(final double[][][] array3D,
			final double... parameters) {
		final byte[][][] result = new byte[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveByteArray2D(array3D[i], parameters);
		}
		return result;
	}

	public short[][][] applyToPrimitiveShortArray3D(final double[][][] array3D,
			final double... parameters) {
		final short[][][] result = new short[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveShortArray2D(array3D[i], parameters);
		}
		return result;
	}

	public int[][][] applyToPrimitiveIntArray3D(final double[][][] array3D,
			final double... parameters) {
		final int[][][] result = new int[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveIntArray2D(array3D[i], parameters);
		}
		return result;
	}

	public long[][][] applyToPrimitiveLongArray3D(final double[][][] array3D,
			final double... parameters) {
		final long[][][] result = new long[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveLongArray2D(array3D[i], parameters);
		}
		return result;
	}

	public float[][][] applyToPrimitiveFloatArray3D(final double[][][] array3D,
			final double... parameters) {
		final float[][][] result = new float[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = applyToPrimitiveFloatArray2D(array3D[i], parameters);
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
	public ParametricFunction clone() {
		return (ParametricFunction) super.clone();
	}
}
