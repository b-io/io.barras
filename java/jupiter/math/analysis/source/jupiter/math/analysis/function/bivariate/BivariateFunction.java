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
package jupiter.math.analysis.function.bivariate;

import jupiter.common.math.Domain;
import jupiter.common.model.ICloneable;
import jupiter.common.test.IntervalArguments;
import jupiter.common.util.Bytes;
import jupiter.common.util.Floats;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Shorts;
import jupiter.math.analysis.function.univariate.UnivariateFunction;

/**
 * {@link BivariateFunction} is a bivariate function defined in the {@link Domain} of each variable.
 */
public abstract class BivariateFunction
		extends UnivariateFunction
		implements IBivariateFunction {

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
	 * The {@link Domain} of the second variable.
	 */
	protected final Domain secondDomain;

	/**
	 * The initial {@code double} value.
	 */
	protected final double initialValue;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link BivariateFunction} by default.
	 */
	protected BivariateFunction() {
		this(0.);
	}

	/**
	 * Constructs a {@link BivariateFunction} with the specified initial value.
	 * <p>
	 * @param initialValue the initial {@code double} value
	 */
	public BivariateFunction(final double initialValue) {
		super();
		secondDomain = domain;
		this.initialValue = bound(initialValue);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link BivariateFunction} with the specified {@link Domain} for both variables.
	 * <p>
	 * @param domain the {@link Domain} for both variables
	 */
	protected BivariateFunction(final Domain domain) {
		this(domain, 0.);
	}

	/**
	 * Constructs a {@link BivariateFunction} with the specified {@link Domain} for both variables
	 * and initial value.
	 * <p>
	 * @param domain       the {@link Domain} for both variables
	 * @param initialValue the initial {@code double} value
	 */
	public BivariateFunction(final Domain domain, final double initialValue) {
		super(domain);
		secondDomain = domain;
		this.initialValue = bound(initialValue);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link BivariateFunction} with the specified {@link Domain} of the first
	 * variable and {@link Domain} of the second variable.
	 * <p>
	 * @param firstDomain  the {@link Domain} of the first variable
	 * @param secondDomain the {@link Domain} of the second variable
	 */
	protected BivariateFunction(final Domain firstDomain, final Domain secondDomain) {
		this(firstDomain, secondDomain, 0.);
	}

	/**
	 * Constructs a {@link BivariateFunction} with the specified {@link Domain} of the first
	 * variable, {@link Domain} of the second variable and initial value.
	 * <p>
	 * @param firstDomain  the {@link Domain} of the first variable
	 * @param secondDomain the {@link Domain} of the second variable
	 * @param initialValue the initial {@code double} value
	 */
	public BivariateFunction(final Domain firstDomain, final Domain secondDomain,
			final double initialValue) {
		super(firstDomain);

		// Check the arguments
		IntervalArguments.requireValid(secondDomain, "second domain");

		// Set the attributes
		this.secondDomain = secondDomain;
		this.initialValue = bound(initialValue);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Domain} of the first variable.
	 * <p>
	 * @return the {@link Domain} of the first variable
	 */
	public Domain getFirstDomain() {
		return domain;
	}

	/**
	 * Returns the {@link Domain} of the second variable.
	 * <p>
	 * @return the {@link Domain} of the second variable
	 */
	public Domain getSecondDomain() {
		return secondDomain;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the initial {@code double} value.
	 * <p>
	 * @return the initial {@code double} value
	 */
	public double getInitialValue() {
		return initialValue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code x2} if {@code x2} is inside {@code secondDomain}, {@code secondLowerBound} if
	 * {@code x2 < secondLowerBound}, {@code secondUpperBound} if {@code x2 > secondUpperBound},
	 * {@code NaN} otherwise.
	 * <p>
	 * @param x2 a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code x2} if {@code x2} is inside {@code secondDomain}, {@code secondLowerBound} if
	 *         {@code x2 < secondLowerBound}, {@code secondUpperBound} if
	 *         {@code x2 > secondUpperBound}, {@code NaN} otherwise
	 */
	public double boundSecond(final double x2) {
		return secondDomain.bound(x2);
	}

	/**
	 * Returns {@code x2} if {@code x2} is inside {@code secondDomain}, {@code NaN} otherwise.
	 * <p>
	 * @param x2 a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code x2} if {@code x2} is inside {@code secondDomain}, {@code NaN} otherwise
	 */
	public double constrainSecond(final double x2) {
		return secondDomain.constrain(x2);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the bivariate function to the initial value and the specified value.
	 * <p>
	 * @param x2 a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code f(initialValue, x2)}
	 *
	 * @see #a(double, double)
	 */
	@Override
	protected double a(final double x2) {
		return a(initialValue, boundSecond(x2));
	}

	/**
	 * Applies the bivariate function to the specified values.
	 * <p>
	 * @param x1 a {@code double} value (on the abscissa)
	 * @param x2 another {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code f(x1, x2)}
	 */
	protected abstract double a(final double x1, final double x2);

	//////////////////////////////////////////////

	/**
	 * Applies the bivariate function to the specified values.
	 * <p>
	 * @param x1 a {@code double} value (on the abscissa)
	 * @param x2 another {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code f(x1, x2)}
	 *
	 * @see #a(double, double)
	 */
	public double apply(final double x1, final double x2) {
		return a(bound(x1), boundSecond(x2));
	}

	/**
	 * Applies the bivariate function to the specified {@link Number}.
	 * <p>
	 * @param x1 a {@link Number} (on the abscissa)
	 * @param x2 another {@link Number} (on the abscissa)
	 * <p>
	 * @return {@code f(x1, x2)}
	 *
	 * @see #apply(double, double)
	 */
	public double apply(final Number x1, final Number x2) {
		return apply(x1.doubleValue(), x2.doubleValue());
	}

	//////////////////////////////////////////////

	public byte applyToByte(final double x1, final double x2) {
		return Bytes.convert(apply(x1, x2));
	}

	public short applyToShort(final double x1, final double x2) {
		return Shorts.convert(apply(x1, x2));
	}

	public int applyToInt(final double x1, final double x2) {
		return Integers.convert(apply(x1, x2));
	}

	public long applyToLong(final double x1, final double x2) {
		return Longs.convert(apply(x1, x2));
	}

	public float applyToFloat(final double x1, final double x2) {
		return Floats.convert(apply(x1, x2));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double[] slideToPrimitiveArray(final double... array) {
		final double[] result = new double[array.length];
		int i = 0;
		if (i < array.length) {
			result[i] = apply(array[i]);
			++i;
		}
		while (i < array.length) {
			result[i] = apply(array[i - 1], array[i]);
			++i;
		}
		return result;
	}

	public double[] slideToPrimitiveArray(final Number[] array) {
		final double[] result = new double[array.length];
		int i = 0;
		if (i < array.length) {
			result[i] = apply(array[i]);
			++i;
		}
		while (i < array.length) {
			result[i] = apply(array[i - 1], array[i]);
			++i;
		}
		return result;
	}

	//////////////////////////////////////////////

	public byte[] slideToPrimitiveByteArray(final double... array) {
		final byte[] result = new byte[array.length];
		int i = 0;
		if (i < array.length) {
			result[i] = applyToByte(array[i]);
			++i;
		}
		while (i < array.length) {
			result[i] = applyToByte(array[i - 1], array[i]);
			++i;
		}
		return result;
	}

	public short[] slideToPrimitiveShortArray(final double... array) {
		final short[] result = new short[array.length];
		int i = 0;
		if (i < array.length) {
			result[i] = applyToShort(array[i]);
			++i;
		}
		while (i < array.length) {
			result[i] = applyToShort(array[i - 1], array[i]);
			++i;
		}
		return result;
	}

	public int[] slideToPrimitiveIntArray(final double... array) {
		final int[] result = new int[array.length];
		int i = 0;
		if (i < array.length) {
			result[i] = applyToInt(array[i]);
			++i;
		}
		while (i < array.length) {
			result[i] = applyToInt(array[i - 1], array[i]);
			++i;
		}
		return result;
	}

	public long[] slideToPrimitiveLongArray(final double... array) {
		final long[] result = new long[array.length];
		int i = 0;
		if (i < array.length) {
			result[i] = applyToLong(array[i]);
			++i;
		}
		while (i < array.length) {
			result[i] = applyToLong(array[i - 1], array[i]);
			++i;
		}
		return result;
	}

	public float[] slideToPrimitiveFloatArray(final double... array) {
		final float[] result = new float[array.length];
		int i = 0;
		if (i < array.length) {
			result[i] = applyToFloat(array[i]);
			++i;
		}
		while (i < array.length) {
			result[i] = applyToFloat(array[i - 1], array[i]);
			++i;
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double[][] slideToPrimitiveArray2D(final double[]... array2D) {
		final double[][] result = new double[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = slideToPrimitiveArray(array2D[i]);
		}
		return result;
	}

	public double[][] slideToPrimitiveArray2D(final Number[]... array2D) {
		final double[][] result = new double[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = slideToPrimitiveArray(array2D[i]);
		}
		return result;
	}

	//////////////////////////////////////////////

	public byte[][] slideToPrimitiveByteArray2D(final double[]... array2D) {
		final byte[][] result = new byte[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = slideToPrimitiveByteArray(array2D[i]);
		}
		return result;
	}

	public short[][] slideToPrimitiveShortArray2D(final double[]... array2D) {
		final short[][] result = new short[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = slideToPrimitiveShortArray(array2D[i]);
		}
		return result;
	}

	public int[][] slideToPrimitiveIntArray2D(final double[]... array2D) {
		final int[][] result = new int[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = slideToPrimitiveIntArray(array2D[i]);
		}
		return result;
	}

	public long[][] slideToPrimitiveLongArray2D(final double[]... array2D) {
		final long[][] result = new long[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = slideToPrimitiveLongArray(array2D[i]);
		}
		return result;
	}

	public float[][] slideToPrimitiveFloatArray2D(final double[]... array2D) {
		final float[][] result = new float[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = slideToPrimitiveFloatArray(array2D[i]);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double[][][] slideToPrimitiveArray3D(final double[][]... array3D) {
		final double[][][] result = new double[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = slideToPrimitiveArray2D(array3D[i]);
		}
		return result;
	}

	public double[][][] slideToPrimitiveArray3D(final Number[][]... array3D) {
		final double[][][] result = new double[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = slideToPrimitiveArray2D(array3D[i]);
		}
		return result;
	}

	//////////////////////////////////////////////

	public byte[][][] slideToPrimitiveByteArray3D(final double[][]... array3D) {
		final byte[][][] result = new byte[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = slideToPrimitiveByteArray2D(array3D[i]);
		}
		return result;
	}

	public short[][][] slideToPrimitiveShortArray3D(final double[][]... array3D) {
		final short[][][] result = new short[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = slideToPrimitiveShortArray2D(array3D[i]);
		}
		return result;
	}

	public int[][][] slideToPrimitiveIntArray3D(final double[][]... array3D) {
		final int[][][] result = new int[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = slideToPrimitiveIntArray2D(array3D[i]);
		}
		return result;
	}

	public long[][][] slideToPrimitiveLongArray3D(final double[][]... array3D) {
		final long[][][] result = new long[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = slideToPrimitiveLongArray2D(array3D[i]);
		}
		return result;
	}

	public float[][][] slideToPrimitiveFloatArray3D(final double[][]... array3D) {
		final float[][][] result = new float[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = slideToPrimitiveFloatArray2D(array3D[i]);
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
	public BivariateFunction clone() {
		return (BivariateFunction) super.clone();
	}
}
