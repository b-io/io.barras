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
package jupiter.common.math;

import static jupiter.common.io.IO.IO;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jupiter.common.test.ArrayArguments;
import jupiter.common.test.ByteArguments;
import jupiter.common.test.CollectionArguments;
import jupiter.common.test.DoubleArguments;
import jupiter.common.test.FloatArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.test.LongArguments;
import jupiter.common.test.ShortArguments;
import jupiter.common.util.Bytes;
import jupiter.common.util.Floats;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Shorts;
import jupiter.common.util.Strings;

public class Maths {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The tolerance level (or termination criterion) {@code ε} for {@code float} values.
	 */
	public static volatile float FLOAT_TOLERANCE = 1E-5f;
	/**
	 * The tiny tolerance level for {@code float} values.
	 */
	public static volatile float FLOAT_TINY_TOLERANCE = 1E-25f;

	/**
	 * The tolerance level (or termination criterion) {@code ε} for {@code double} values.
	 */
	public static volatile double TOLERANCE = 1E-10;
	/**
	 * The tiny tolerance level for {@code double} values.
	 */
	public static volatile double TINY_TOLERANCE = 1E-50;

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final double DEFAULT_CONFIDENCE = 0.975; // 97.5%
	public static final double DEFAULT_Z = 1.9599639845400536; // 97.5%

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final double LOG_2 = log(2.);
	public static final double SQUARE_ROOT_OF_2 = sqrt(2.);

	//////////////////////////////////////////////

	/**
	 * The Napier constant {@code e}, the base of the natural logarithm.
	 */
	public static final double E = 2850325. / 1048576. + 8.254840070411028747E-8;
	public static final double SQUARE_ROOT_OF_E = sqrt(E);

	//////////////////////////////////////////////

	/**
	 * The Archimede constant {@code π}, the ratio of the circumference of a circle to its diameter.
	 */
	public static final double PI = 105414357. / 33554432. + 1.984187159361080883E-9;
	public static final double SQUARE_ROOT_OF_PI = sqrt(PI);
	public static final double SQUARE_ROOT_OF_2_PI = sqrt(2. * PI);

	public static final double DEGREE_TO_RADIAN = PI / 180.;
	public static final double RADIAN_TO_DEGREE = 180. / PI;

	//////////////////////////////////////////////

	public static final double F_1_2 = 1. / 2.;
	public static final double F_1_3 = 1. / 3.;
	public static final double F_1_4 = 1. / 4.;
	public static final double F_1_5 = 1. / 5.;
	public static final double F_1_6 = 1. / 6.;
	public static final double F_1_7 = 1. / 7.;
	public static final double F_1_8 = 1. / 8.;
	public static final double F_1_9 = 1. / 9.;
	public static final double F_1_10 = 1. / 10.;
	public static final double F_1_11 = 1. / 11.;
	public static final double F_1_12 = 1. / 12.;
	public static final double F_1_13 = 1. / 13.;
	public static final double F_1_14 = 1. / 14.;
	public static final double F_1_15 = 1. / 15.;
	public static final double F_1_16 = 1. / 16.;
	public static final double F_1_17 = 1. / 17.;
	public static final double F_1_18 = 1. / 18.;
	public static final double F_1_19 = 1. / 19.;
	public static final double F_1_20 = 1. / 20.;
	public static final double F_1_30 = 1. / 30.;
	public static final double F_1_40 = 1. / 40.;
	public static final double F_1_50 = 1. / 50.;
	public static final double F_2_3 = 2. / 3.;
	public static final double F_3_2 = 3. / 2.;
	public static final double F_3_4 = 3. / 4.;
	public static final double F_4_5 = 4. / 5.;
	public static final double F_5_6 = 5. / 6.;

	//////////////////////////////////////////////

	protected static final int[] INT_FACTORIALS = new int[] {
		1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600
	};
	protected static final long[] LONG_FACTORIALS = new long[] {
		1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L,
		6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L,
		6402373705728000L, 121645100408832000L, 2432902008176640000L
	};

	//////////////////////////////////////////////

	/**
	 * The mask to clear the non-sign part of an {@code int} value.
	 */
	private static final int MASK_NON_SIGN_INT = 0x7fffffff;
	/**
	 * The mask to clear the non-sign part of a {@code long} value.
	 */
	private static final long MASK_NON_SIGN_LONG = 0x7fffffffffffffffl;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Maths}.
	 */
	protected Maths() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static short floorToShort(final double value) {
		return Shorts.convert(floor(value));
	}

	public static int floorToInt(final double value) {
		return Integers.convert(floor(value));
	}

	public static long floorToLong(final double value) {
		return Longs.convert(floor(value));
	}

	public static double floor(final double value) {
		return StrictMath.floor(value);
	}

	//////////////////////////////////////////////

	public static short ceilToShort(final double value) {
		return Shorts.convert(ceil(value));
	}

	public static int ceilToInt(final double value) {
		return Integers.convert(ceil(value));
	}

	public static long ceilToLong(final double value) {
		return Longs.convert(ceil(value));
	}

	public static double ceil(final double value) {
		return StrictMath.ceil(value);
	}

	//////////////////////////////////////////////

	public static short roundToShort(final double value) {
		return Shorts.convert(round(value));
	}

	public static int roundToInt(final double value) {
		return Integers.convert(round(value));
	}

	public static long round(final double value) {
		return Math.round(value);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static byte sum(final byte... values) {
		byte sum = 0;
		for (final byte value : values) {
			sum += value;
		}
		return sum;
	}

	public static short sum(final short... values) {
		short sum = 0;
		for (final short value : values) {
			sum += value;
		}
		return sum;
	}

	public static int sum(final int... values) {
		int sum = 0;
		for (final int value : values) {
			sum += value;
		}
		return sum;
	}

	public static long sum(final long... values) {
		long sum = 0L;
		for (final long value : values) {
			sum += value;
		}
		return sum;
	}

	public static float sum(final float... values) {
		float sum = 0f;
		for (final float value : values) {
			sum += value;
		}
		return sum;
	}

	public static double sum(final double... values) {
		double sum = 0.;
		for (final double value : values) {
			sum += value;
		}
		return sum;
	}

	public static double sum(final Number[] numbers) {
		double sum = 0.;
		for (final Number number : numbers) {
			if (number != null) {
				sum += number.doubleValue();
			}
		}
		return sum;
	}

	public static double sum(final Collection<? extends Number> numbers) {
		double sum = 0.;
		for (final Number number : numbers) {
			if (number != null) {
				sum += number.doubleValue();
			}
		}
		return sum;
	}

	//////////////////////////////////////////////

	public static byte sumInterval(final int fromIndex, final int toIndex, final byte... values) {
		byte sum = 0;
		for (int i = fromIndex; i < toIndex; ++i) {
			sum += values[i];
		}
		return sum;
	}

	public static short sumInterval(final int fromIndex, final int toIndex, final short... values) {
		short sum = 0;
		for (int i = fromIndex; i < toIndex; ++i) {
			sum += values[i];
		}
		return sum;
	}

	public static int sumInterval(final int fromIndex, final int toIndex, final int... values) {
		int sum = 0;
		for (int i = fromIndex; i < toIndex; ++i) {
			sum += values[i];
		}
		return sum;
	}

	public static long sumInterval(final int fromIndex, final int toIndex, final long... values) {
		long sum = 0L;
		for (int i = fromIndex; i < toIndex; ++i) {
			sum += values[i];
		}
		return sum;
	}

	public static float sumInterval(final int fromIndex, final int toIndex, final float... values) {
		float sum = 0f;
		for (int i = fromIndex; i < toIndex; ++i) {
			sum += values[i];
		}
		return sum;
	}

	public static double sumInterval(final int fromIndex, final int toIndex,
			final double... values) {
		double sum = 0.;
		for (int i = fromIndex; i < toIndex; ++i) {
			sum += values[i];
		}
		return sum;
	}

	public static double sumInterval(final int fromIndex, final int toIndex,
			final Number[] numbers) {
		double sum = 0.;
		for (int i = fromIndex; i < toIndex; ++i) {
			if (numbers[i] != null) {
				sum += numbers[i].doubleValue();
			}
		}
		return sum;
	}

	public static double sumInterval(final int fromIndex, final int toIndex,
			final Collection<? extends Number> numbers) {
		double sum = 0.;
		int i = 0;
		for (final Number number : numbers) {
			if (number != null && i >= fromIndex && i < toIndex) {
				sum += number.doubleValue();
			}
			++i;
		}
		return sum;
	}

	//////////////////////////////////////////////

	public static int sumSeries(final int n) {
		return n * (n + 1) / 2;
	}

	public static int sumSeries(final int from, final int to) {
		return sumSeries(to) - sumSeries(from);
	}

	public static long sumSeries(final long n) {
		return n * (n + 1) / 2L;
	}

	public static long sumSeries(final long from, final long to) {
		return sumSeries(to) - sumSeries(from);
	}

	//////////////////////////////////////////////

	public static float sumWithoutNaN(final float... values) {
		float sum = 0f;
		for (final float value : values) {
			if (!Float.isNaN(value)) {
				sum += value;
			}
		}
		return sum;
	}

	public static double sumWithoutNaN(final double... values) {
		double sum = 0.;
		for (final double value : values) {
			if (!Double.isNaN(value)) {
				sum += value;
			}
		}
		return sum;
	}

	public static double sumWithoutNaN(final Number[] numbers) {
		double sum = 0.;
		for (final Number number : numbers) {
			if (number != null) {
				final double elementValue = number.doubleValue();
				if (!Double.isNaN(elementValue)) {
					sum += elementValue;
				}
			}
		}
		return sum;
	}

	public static double sumWithoutNaN(final Collection<? extends Number> numbers) {
		double sum = 0.;
		for (final Number number : numbers) {
			if (number != null) {
				final double elementValue = number.doubleValue();
				if (!Double.isNaN(elementValue)) {
					sum += elementValue;
				}
			}
		}
		return sum;
	}

	//////////////////////////////////////////////

	public static float sumOfSquares(final float[] values, final float mean) {
		float sum = 0f;
		for (final float value : values) {
			sum += square(value - mean);
		}
		return sum;
	}

	public static double sumOfSquares(final double[] values, final double mean) {
		double sum = 0.;
		for (final double value : values) {
			sum += square(value - mean);
		}
		return sum;
	}

	public static double sumOfSquares(final Number[] numbers, final double mean) {
		double sum = 0.;
		for (final Number number : numbers) {
			if (number != null) {
				sum += square(number.doubleValue() - mean);
			}
		}
		return sum;
	}

	public static double sumOfSquares(final Collection<? extends Number> numbers,
			final double mean) {
		double sum = 0.;
		for (final Number number : numbers) {
			if (number != null) {
				sum += square(number.doubleValue() - mean);
			}
		}
		return sum;
	}

	//////////////////////////////////////////////

	public static float sumOfSquaresWithoutNaN(final float[] values, final float mean) {
		float sum = 0f;
		for (final float value : values) {
			if (!Float.isNaN(value)) {
				sum += square(value - mean);
			}
		}
		return sum;
	}

	public static double sumOfSquaresWithoutNaN(final double[] values, final double mean) {
		double sum = 0.;
		for (final double value : values) {
			if (!Double.isNaN(value)) {
				sum += square(value - mean);
			}
		}
		return sum;
	}

	public static double sumOfSquaresWithoutNaN(final Number[] numbers, final double mean) {
		double sum = 0.;
		for (final Number number : numbers) {
			if (number != null) {
				final double elementValue = number.doubleValue();
				if (!Double.isNaN(elementValue)) {
					sum += square(elementValue - mean);
				}
			}
		}
		return sum;
	}

	public static double sumOfSquaresWithoutNaN(final Collection<? extends Number> numbers,
			final double mean) {
		double sum = 0.;
		for (final Number number : numbers) {
			if (number != null) {
				final double elementValue = number.doubleValue();
				if (!Double.isNaN(elementValue)) {
					sum += square(elementValue - mean);
				}
			}
		}
		return sum;
	}

	//////////////////////////////////////////////

	/**
	 * Adds the specified {@code byte} value to the specified {@code byte} array.
	 * <p>
	 * @param values a {@code byte} array
	 * @param value  the {@code byte} value to add
	 */
	public static void sum(final byte[] values, final byte value) {
		sum(values, value, 0, values.length);
	}

	/**
	 * Adds the specified {@code byte} value to the specified {@code byte} array between the
	 * specified indices.
	 * <p>
	 * @param values    a {@code byte} array
	 * @param value     the {@code byte} value to add
	 * @param fromIndex the index to start incrementing from (inclusive)
	 * @param toIndex   the index to finish incrementing at (exclusive)
	 */
	public static void sum(final byte[] values, final byte value, final int fromIndex,
			final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			values[i] += value;
		}
	}

	/**
	 * Adds the specified {@code short} value to the specified {@code short} array.
	 * <p>
	 * @param values a {@code short} array
	 * @param value  the {@code short} value to add
	 */
	public static void sum(final short[] values, final short value) {
		sum(values, value, 0, values.length);
	}

	/**
	 * Adds the specified {@code short} value to the specified {@code short} array between the
	 * specified indices.
	 * <p>
	 * @param values    a {@code short} array
	 * @param value     the {@code short} value to add
	 * @param fromIndex the index to start incrementing from (inclusive)
	 * @param toIndex   the index to finish incrementing at (exclusive)
	 */
	public static void sum(final short[] values, final short value, final int fromIndex,
			final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			values[i] += value;
		}
	}

	/**
	 * Adds the specified {@code int} value to the specified {@code int} array.
	 * <p>
	 * @param values an {@code int} array
	 * @param value  the {@code int} value to add
	 */
	public static void sum(final int[] values, final int value) {
		sum(values, value, 0, values.length);
	}

	/**
	 * Adds the specified {@code int} value to the specified {@code int} array between the specified
	 * indices.
	 * <p>
	 * @param values    an {@code int} array
	 * @param value     the {@code int} value to add
	 * @param fromIndex the index to start incrementing from (inclusive)
	 * @param toIndex   the index to finish incrementing at (exclusive)
	 */
	public static void sum(final int[] values, final int value, final int fromIndex,
			final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			values[i] += value;
		}
	}

	/**
	 * Adds the specified {@code long} value to the specified {@code long} array.
	 * <p>
	 * @param values a {@code long} array
	 * @param value  the {@code long} value to add
	 */
	public static void sum(final long[] values, final long value) {
		sum(values, value, 0, values.length);
	}

	/**
	 * Adds the specified {@code long} value to the specified {@code long} array between the
	 * specified indices.
	 * <p>
	 * @param values    a {@code long} array
	 * @param value     the {@code long} value to add
	 * @param fromIndex the index to start incrementing from (inclusive)
	 * @param toIndex   the index to finish incrementing at (exclusive)
	 */
	public static void sum(final long[] values, final long value, final int fromIndex,
			final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			values[i] += value;
		}
	}

	/**
	 * Adds the specified {@code float} value to the specified {@code float} array.
	 * <p>
	 * @param values a {@code float} array
	 * @param value  the {@code float} value to add
	 */
	public static void sum(final float[] values, final float value) {
		sum(values, value, 0, values.length);
	}

	/**
	 * Adds the specified {@code float} value to the specified {@code float} array between the
	 * specified indices.
	 * <p>
	 * @param values    a {@code float} array
	 * @param value     the {@code float} value to add
	 * @param fromIndex the index to start incrementing from (inclusive)
	 * @param toIndex   the index to finish incrementing at (exclusive)
	 */
	public static void sum(final float[] values, final float value, final int fromIndex,
			final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			values[i] += value;
		}
	}

	/**
	 * Adds the specified {@code double} value to the specified {@code double} array.
	 * <p>
	 * @param values a {@code double} array
	 * @param value  the {@code double} value to add
	 */
	public static void sum(final double[] values, final double value) {
		sum(values, value, 0, values.length);
	}

	/**
	 * Adds the specified {@code double} value to the specified {@code double} array between the
	 * specified indices.
	 * <p>
	 * @param values    a {@code double} array
	 * @param value     the {@code double} value to add
	 * @param fromIndex the index to start incrementing from (inclusive)
	 * @param toIndex   the index to finish incrementing at (exclusive)
	 */
	public static void sum(final double[] values, final double value, final int fromIndex,
			final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			values[i] += value;
		}
	}

	//////////////////////////////////////////////

	public static byte weightedSum(final byte[] values, final byte[] weights) {
		// Check the arguments
		ByteArguments.requireSameLength(values, weights);

		// Sum
		byte sum = 0;
		for (int i = 0; i < values.length; ++i) {
			sum += weights[i] * values[i];
		}
		return sum;
	}

	public static short weightedSum(final short[] values, final short[] weights) {
		// Check the arguments
		ShortArguments.requireSameLength(values, weights);

		// Sum
		short sum = 0;
		for (int i = 0; i < values.length; ++i) {
			sum += weights[i] * values[i];
		}
		return sum;
	}

	public static int weightedSum(final int[] values, final int[] weights) {
		// Check the arguments
		IntegerArguments.requireSameLength(values, weights);

		// Sum
		int sum = 0;
		for (int i = 0; i < values.length; ++i) {
			sum += weights[i] * values[i];
		}
		return sum;
	}

	public static long weightedSum(final long[] values, final long[] weights) {
		// Check the arguments
		LongArguments.requireSameLength(values, weights);

		// Sum
		long sum = 0L;
		for (int i = 0; i < values.length; ++i) {
			sum += weights[i] * values[i];
		}
		return sum;
	}

	public static float weightedSum(final float[] values, final float[] weights) {
		// Check the arguments
		FloatArguments.requireSameLength(values, weights);

		// Sum
		float sum = 0f;
		for (int i = 0; i < values.length; ++i) {
			sum += weights[i] * values[i];
		}
		return sum;
	}

	public static double weightedSum(final double[] values, final double[] weights) {
		// Check the arguments
		DoubleArguments.requireSameLength(values, weights);

		// Sum
		double sum = 0.;
		for (int i = 0; i < values.length; ++i) {
			sum += weights[i] * values[i];
		}
		return sum;
	}

	public static double weightedSum(final Number[] numbers, final Number[] weights) {
		// Check the arguments
		ArrayArguments.requireSameLength(numbers, weights);

		// Sum
		double sum = 0.;
		for (int i = 0; i < numbers.length; ++i) {
			if (numbers[i] != null && weights[i] != null) {
				sum += weights[i].doubleValue() * numbers[i].doubleValue();
			}
		}
		return sum;
	}

	public static double weightedSum(final List<? extends Number> numbers,
			final List<? extends Number> weights) {
		// Check the arguments
		CollectionArguments.requireSameSize(numbers, weights);

		// Sum
		double sum = 0.;
		final Iterator<? extends Number> numberIterator = numbers.iterator();
		final Iterator<? extends Number> weightIterator = weights.iterator();
		while (numberIterator.hasNext() && weightIterator.hasNext()) {
			final Number number = numberIterator.next();
			final Number weight = weightIterator.next();
			if (number != null && weight != null) {
				sum += weight.doubleValue() * number.doubleValue();
			}
		}
		return sum;
	}

	//////////////////////////////////////////////

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A}.
	 * <p>
	 * @param A the {@code byte} array to add
	 * @param B the {@code byte} array to multiply
	 * @param c the constant {@code c} to multiply
	 */
	public static void arraySum(final byte[] A, final byte[] B, final byte c) {
		arraySum(A, B, c, 0, 0, 0, Math.min(A.length - 1, B.length - 1));
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A} from the specified offsets.
	 * <p>
	 * @param A       the {@code byte} array to add
	 * @param B       the {@code byte} array to multiply
	 * @param c       the constant {@code c} to multiply
	 * @param aOffset the offset of {@code A}
	 * @param bOffset the offset of {@code B}
	 */
	public static void arraySum(final byte[] A, final byte[] B, final byte c, final int aOffset,
			final int bOffset) {
		arraySum(A, B, c, aOffset, bOffset, 0,
				Math.min(A.length - 1 - aOffset, B.length - 1 - bOffset));
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A} from the specified offsets
	 * between the specified indices.
	 * <p>
	 * @param A         the {@code byte} array to add
	 * @param B         the {@code byte} array to multiply
	 * @param c         the constant {@code c} to multiply
	 * @param aOffset   the offset of {@code A}
	 * @param bOffset   the offset of {@code B}
	 * @param fromIndex the index to start summing from (inclusive)
	 * @param toIndex   the index to finish summing at (exclusive)
	 */
	public static void arraySum(final byte[] A, final byte[] B, final byte c, final int aOffset,
			final int bOffset, final int fromIndex, final int toIndex) {
		if (c != 0) {
			for (int i = fromIndex; i < toIndex; ++i) {
				A[aOffset + i] += c * B[bOffset + i];
			}
		}
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A}.
	 * <p>
	 * @param A the {@code short} array to add
	 * @param B the {@code short} array to multiply
	 * @param c the constant {@code c} to multiply
	 */
	public static void arraySum(final short[] A, final short[] B, final short c) {
		arraySum(A, B, c, 0, 0, 0, Math.min(A.length - 1, B.length - 1));
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A} from the specified offsets.
	 * <p>
	 * @param A       the {@code short} array to add
	 * @param B       the {@code short} array to multiply
	 * @param c       the constant {@code c} to multiply
	 * @param aOffset the offset of {@code A}
	 * @param bOffset the offset of {@code B}
	 */
	public static void arraySum(final short[] A, final short[] B, final short c, final int aOffset,
			final int bOffset) {
		arraySum(A, B, c, aOffset, bOffset, 0,
				Math.min(A.length - 1 - aOffset, B.length - 1 - bOffset));
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A} from the specified offsets
	 * between the specified indices.
	 * <p>
	 * @param A         the {@code short} array to add
	 * @param B         the {@code short} array to multiply
	 * @param c         the constant {@code c} to multiply
	 * @param aOffset   the offset of {@code A}
	 * @param bOffset   the offset of {@code B}
	 * @param fromIndex the index to start summing from (inclusive)
	 * @param toIndex   the index to finish summing at (exclusive)
	 */
	public static void arraySum(final short[] A, final short[] B, final short c, final int aOffset,
			final int bOffset, final int fromIndex, final int toIndex) {
		if (c != 0) {
			for (int i = fromIndex; i < toIndex; ++i) {
				A[aOffset + i] += c * B[bOffset + i];
			}
		}
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A}.
	 * <p>
	 * @param A the {@code int} array to add
	 * @param B the {@code int} array to multiply
	 * @param c the constant {@code c} to multiply
	 */
	public static void arraySum(final int[] A, final int[] B, final int c) {
		arraySum(A, B, c, 0, 0, 0, Math.min(A.length - 1, B.length - 1));
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A} from the specified offsets.
	 * <p>
	 * @param A       the {@code int} array to add
	 * @param B       the {@code int} array to multiply
	 * @param c       the constant {@code c} to multiply
	 * @param aOffset the offset of {@code A}
	 * @param bOffset the offset of {@code B}
	 */
	public static void arraySum(final int[] A, final int[] B, final int c, final int aOffset,
			final int bOffset) {
		arraySum(A, B, c, aOffset, bOffset, 0,
				Math.min(A.length - 1 - aOffset, B.length - 1 - bOffset));
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A} from the specified offsets
	 * between the specified indices.
	 * <p>
	 * @param A         the {@code int} array to add
	 * @param B         the {@code int} array to multiply
	 * @param c         the constant {@code c} to multiply
	 * @param aOffset   the offset of {@code A}
	 * @param bOffset   the offset of {@code B}
	 * @param fromIndex the index to start summing from (inclusive)
	 * @param toIndex   the index to finish summing at (exclusive)
	 */
	public static void arraySum(final int[] A, final int[] B, final int c, final int aOffset,
			final int bOffset, final int fromIndex, final int toIndex) {
		if (c != 0) {
			for (int i = fromIndex; i < toIndex; ++i) {
				A[aOffset + i] += c * B[bOffset + i];
			}
		}
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A}.
	 * <p>
	 * @param A the {@code long} array to add
	 * @param B the {@code long} array to multiply
	 * @param c the constant {@code c} to multiply
	 */
	public static void arraySum(final long[] A, final long[] B, final long c) {
		arraySum(A, B, c, 0, 0, 0, Math.min(A.length - 1, B.length - 1));
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A} from the specified offsets.
	 * <p>
	 * @param A       the {@code long} array to add
	 * @param B       the {@code long} array to multiply
	 * @param c       the constant {@code c} to multiply
	 * @param aOffset the offset of {@code A}
	 * @param bOffset the offset of {@code B}
	 */
	public static void arraySum(final long[] A, final long[] B, final long c, final int aOffset,
			final int bOffset) {
		arraySum(A, B, c, aOffset, bOffset, 0,
				Math.min(A.length - 1 - aOffset, B.length - 1 - bOffset));
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A} from the specified offsets
	 * between the specified indices.
	 * <p>
	 * @param A         the {@code long} array to add
	 * @param B         the {@code long} array to multiply
	 * @param c         the constant {@code c} to multiply
	 * @param aOffset   the offset of {@code A}
	 * @param bOffset   the offset of {@code B}
	 * @param fromIndex the index to start summing from (inclusive)
	 * @param toIndex   the index to finish summing at (exclusive)
	 */
	public static void arraySum(final long[] A, final long[] B, final long c, final int aOffset,
			final int bOffset, final int fromIndex, final int toIndex) {
		if (c != 0L) {
			for (int i = fromIndex; i < toIndex; ++i) {
				A[aOffset + i] += c * B[bOffset + i];
			}
		}
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A}.
	 * <p>
	 * @param A the {@code float} array to add
	 * @param B the {@code float} array to multiply
	 * @param c the constant {@code c} to multiply
	 */
	public static void arraySum(final float[] A, final float[] B, final float c) {
		arraySum(A, B, c, 0, 0, 0, Math.min(A.length - 1, B.length - 1));
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A} from the specified offsets.
	 * <p>
	 * @param A       the {@code float} array to add
	 * @param B       the {@code float} array to multiply
	 * @param c       the constant {@code c} to multiply
	 * @param aOffset the offset of {@code A}
	 * @param bOffset the offset of {@code B}
	 */
	public static void arraySum(final float[] A, final float[] B, final float c, final int aOffset,
			final int bOffset) {
		arraySum(A, B, c, aOffset, bOffset, 0,
				Math.min(A.length - 1 - aOffset, B.length - 1 - bOffset));
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A} from the specified offsets
	 * between the specified indices.
	 * <p>
	 * @param A         the {@code float} array to add
	 * @param B         the {@code float} array to multiply
	 * @param c         the constant {@code c} to multiply
	 * @param aOffset   the offset of {@code A}
	 * @param bOffset   the offset of {@code B}
	 * @param fromIndex the index to start summing from (inclusive)
	 * @param toIndex   the index to finish summing at (exclusive)
	 */
	public static void arraySum(final float[] A, final float[] B, final float c, final int aOffset,
			final int bOffset, final int fromIndex, final int toIndex) {
		if (c != 0f) {
			for (int i = fromIndex; i < toIndex; ++i) {
				A[aOffset + i] += c * B[bOffset + i];
			}
		}
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A}.
	 * <p>
	 * @param A the {@code double} array to add
	 * @param B the {@code double} array to multiply
	 * @param c the constant {@code c} to multiply
	 */
	public static void arraySum(final double[] A, final double[] B, final double c) {
		arraySum(A, B, c, 0, 0, 0, Math.min(A.length - 1, B.length - 1));
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A} from the specified offsets.
	 * <p>
	 * @param A       the {@code double} array to add
	 * @param B       the {@code double} array to multiply
	 * @param c       the constant {@code c} to multiply
	 * @param aOffset the offset of {@code A}
	 * @param bOffset the offset of {@code B}
	 */
	public static void arraySum(final double[] A, final double[] B, final double c,
			final int aOffset, final int bOffset) {
		arraySum(A, B, c, aOffset, bOffset, 0,
				Math.min(A.length - 1 - aOffset, B.length - 1 - bOffset));
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A} from the specified offsets
	 * between the specified indices.
	 * <p>
	 * @param A         the {@code double} array to add
	 * @param B         the {@code double} array to multiply
	 * @param c         the constant {@code c} to multiply
	 * @param aOffset   the offset of {@code A}
	 * @param bOffset   the offset of {@code B}
	 * @param fromIndex the index to start summing from (inclusive)
	 * @param toIndex   the index to finish summing at (exclusive)
	 */
	public static void arraySum(final double[] A, final double[] B, final double c,
			final int aOffset, final int bOffset, final int fromIndex, final int toIndex) {
		if (c != 0.) {
			for (int i = fromIndex; i < toIndex; ++i) {
				A[aOffset + i] += c * B[bOffset + i];
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static byte delta(final byte a, final byte b) {
		return Bytes.convert(abs(a - b));
	}

	public static short delta(final short a, final short b) {
		return Shorts.convert(abs(a - b));
	}

	public static int delta(final int a, final int b) {
		return abs(a - b);
	}

	public static long delta(final long a, final long b) {
		return abs(a - b);
	}

	public static float delta(final float a, final float b) {
		return abs(a - b);
	}

	public static double delta(final double a, final double b) {
		return abs(a - b);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static byte product(final byte... values) {
		byte product = 1;
		for (final byte value : values) {
			product *= value;
		}
		return product;
	}

	public static short product(final short... values) {
		short product = 1;
		for (final short value : values) {
			product *= value;
		}
		return product;
	}

	public static int product(final int... values) {
		int product = 1;
		for (final int value : values) {
			product *= value;
		}
		return product;
	}

	public static long product(final long... values) {
		long product = 1L;
		for (final long value : values) {
			product *= value;
		}
		return product;
	}

	public static float product(final float... values) {
		float product = 1f;
		for (final float value : values) {
			product *= value;
		}
		return product;
	}

	public static double product(final double... values) {
		double product = 1.;
		for (final double value : values) {
			product *= value;
		}
		return product;
	}

	public static double product(final Number... numbers) {
		double product = 1.;
		for (final Number number : numbers) {
			if (number != null) {
				product *= number.doubleValue();
			}
		}
		return product;
	}

	public static double product(final Collection<? extends Number> numbers) {
		double product = 1.;
		for (final Number number : numbers) {
			if (number != null) {
				product *= number.doubleValue();
			}
		}
		return product;
	}

	//////////////////////////////////////////////

	public static int productSeries(final int n) {
		return productSeries(1, n);
	}

	public static int productSeries(final int from, final int to) {
		if (from == 0 || to == 0) {
			return 0;
		}
		int product = 1;
		for (int value = from; value <= to; ++value) {
			product *= value;
		}
		return product;
	}

	public static long productSeries(final long n) {
		return productSeries(1L, n);
	}

	public static long productSeries(final long from, final long to) {
		if (from == 0L || to == 0L) {
			return 0L;
		}
		long product = 1L;
		for (long value = from; value <= to; ++value) {
			product *= value;
		}
		return product;
	}

	public static float productSeries(final float n) {
		return productSeries(1f, n);
	}

	public static float productSeries(final float from, final float to) {
		if (from == 0f || to == 0f) {
			return 0f;
		}
		float product = 1f;
		for (float value = from; value <= to; ++value) {
			product *= value;
		}
		return product;
	}

	public static double productSeries(final double n) {
		return productSeries(1., n);
	}

	public static double productSeries(final double from, final double to) {
		if (from == 0. || to == 0.) {
			return 0.;
		}
		double product = 1.;
		for (double value = from; value <= to; ++value) {
			product *= value;
		}
		return product;
	}

	//////////////////////////////////////////////

	public static boolean isFactorialInt(final double n) {
		return n < INT_FACTORIALS.length;
	}

	public static int factorial(final int n) {
		if (!isFactorialInt(n)) {
			throw new ArithmeticException(
					Strings.join("The factorial of ", n, " cannot be represented as an int value"));
		}
		return INT_FACTORIALS[n];
	}

	public static boolean isFactorialLong(final double n) {
		return n < LONG_FACTORIALS.length;
	}

	public static long factorial(final long n) {
		if (!isFactorialLong(n)) {
			throw new ArithmeticException(
					Strings.join("The factorial of ", n, " cannot be represented as a long value"));
		}
		return LONG_FACTORIALS[Integers.convert(n)];
	}

	public static double factorial(final double n) {
		if (isFactorialLong(n)) {
			return LONG_FACTORIALS[Integers.convert(n)];
		}
		return floor(exp(factorialLog(Integers.convert(n))) + 0.5);
	}

	/**
	 * Returns the natural logarithm of the factorial of {@code n}.
	 * <p>
	 * @param n an {@code int} value
	 * <p>
	 * @return the natural logarithm of the factorial of {@code n}
	 */
	public static double factorialLog(final int n) {
		if (n < LONG_FACTORIALS.length) {
			return log(LONG_FACTORIALS[Integers.convert(n)]);
		}
		double factorialLog = 0.;
		for (int i = 2; i <= n; ++i) {
			factorialLog += log(i);
		}
		return factorialLog;
	}

	/**
	 * Returns the Ramanujan approximation of the factorial of the specified {@code int} value.
	 * <p>
	 * @param n an {@code int} value
	 * <p>
	 * @return the Ramanujan approximation of the factorial of the specified {@code int} value
	 */
	public static double factorialLimit(final int n) {
		if (n < LONG_FACTORIALS.length) {
			return log(LONG_FACTORIALS[Integers.convert(n)]);
		}
		return SQUARE_ROOT_OF_PI * pow(n / E, n) * pow(((8 * n + 4) * n + 1) * n + F_1_30, F_1_6);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static float safeDivision(final float dividend, final float divisor) {
		return dividend / (divisor + FLOAT_TINY_TOLERANCE);
	}

	public static double safeDivision(final double dividend, final double divisor) {
		return dividend / (divisor + TINY_TOLERANCE);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ANALYTIC FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the absolute value of {@code x}.
	 * <p>
	 * @param x a {@code byte} value
	 * <p>
	 * @return {@code abs(x)}
	 */
	public static byte abs(final byte x) {
		return Bytes.convert(abs(x));
	}

	/**
	 * Returns the absolute value of {@code x}.
	 * <p>
	 * @param x a {@code short} value
	 * <p>
	 * @return {@code abs(x)}
	 */
	public static short abs(final short x) {
		return Shorts.convert(abs(x));
	}

	/**
	 * Returns the absolute value of {@code x}.
	 * <p>
	 * @param x an {@code int} value
	 * <p>
	 * @return {@code abs(x)}
	 */
	public static int abs(final int x) {
		final int i = x >>> 31;
		return (x ^ ~i + 1) + i;
	}

	/**
	 * Returns the absolute value of {@code x}.
	 * <p>
	 * @param x a {@code long} value
	 * <p>
	 * @return {@code abs(x)}
	 */
	public static long abs(final long x) {
		final long l = x >>> 63;
		return (x ^ ~l + 1) + l;
	}

	/**
	 * Returns the absolute value of {@code x}.
	 * <p>
	 * @param x a {@code float} value
	 * <p>
	 * @return {@code abs(x)}
	 */
	public static float abs(final float x) {
		return Float.intBitsToFloat(MASK_NON_SIGN_INT & Float.floatToRawIntBits(x));
	}

	/**
	 * Returns the absolute value of {@code x}.
	 * <p>
	 * @param x a {@code double} value
	 * <p>
	 * @return {@code abs(x)}
	 */
	public static double abs(final double x) {
		return Double.longBitsToDouble(MASK_NON_SIGN_LONG & Double.doubleToRawLongBits(x));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double exp(final double x) {
		return StrictMath.exp(x);
	}

	//////////////////////////////////////////////

	public static double log(final double x) {
		return StrictMath.log(x);
	}

	public static double safeLog(final double x) {
		return log(x + TINY_TOLERANCE);
	}

	public static double log10(final double x) {
		return StrictMath.log10(x);
	}

	public static double safeLog10(final double x) {
		return log10(x + TINY_TOLERANCE);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static float safeInverse(final float x) {
		return safeDivision(1f, x);
	}

	public static double safeInverse(final double x) {
		return safeDivision(1., x);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the value of the specified base raised to the power of the specified exponent.
	 * <p>
	 * @param base     an {@code int} value
	 * @param exponent an {@code int} value
	 * <p>
	 * @return the value of the specified base raised to the power of the specified exponent
	 */
	public static int pow(final int base, final int exponent) {
		return Integers.convert(pow(base, exponent));
	}

	/**
	 * Returns the value of the specified base raised to the power of the specified exponent.
	 * <p>
	 * @param base     a {@code long} value
	 * @param exponent a {@code long} value
	 * <p>
	 * @return the value of the specified base raised to the power of the specified exponent
	 */
	public static long pow(final long base, final long exponent) {
		return Longs.convert(pow(base, exponent));
	}

	/**
	 * Returns the value of the specified base raised to the power of the specified exponent.
	 * <p>
	 * @param base     a {@code float} value
	 * @param exponent a {@code float} value
	 * <p>
	 * @return the value of the specified base raised to the power of the specified exponent
	 */
	public static float pow(final float base, final float exponent) {
		return Floats.convert(StrictMath.pow(base, exponent));
	}

	/**
	 * Returns the value of the specified base raised to the power of the specified exponent.
	 * <p>
	 * @param base     a {@code double} value
	 * @param exponent a {@code double} value
	 * <p>
	 * @return the value of the specified base raised to the power of the specified exponent
	 */
	public static double pow(final double base, final double exponent) {
		return StrictMath.pow(base, exponent);
	}

	//////////////////////////////////////////////

	/**
	 * Returns {@code 2} raised to the power of the specified exponent.
	 * <p>
	 * @param exponent an {@code int} value
	 * <p>
	 * @return {@code 2} raised to the power of the specified exponent
	 */
	public static int pow2(final int exponent) {
		return 1 << exponent;
	}

	/**
	 * Returns {@code 2L} raised to the power of the specified exponent.
	 * <p>
	 * @param exponent a {@code long} value
	 * <p>
	 * @return {@code 2L} raised to the power of the specified exponent
	 */
	public static long pow2(final long exponent) {
		return 1L << exponent;
	}

	//////////////////////////////////////////////

	public static double square(final double x) {
		return x * x;
	}

	public static double cube(final double x) {
		return x * x * x;
	}

	//////////////////////////////////////////////

	public static double sqrt(final double x) {
		return StrictMath.sqrt(x);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double sigmoid(final double x) {
		return sigmoid(x, 1.);
	}

	public static double sigmoid(final double x, final double coefficient) {
		return 1. / (1. + exp(-coefficient * x));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the sign of {@code x}; {@code 0f} if {@code x} is equal to {@code 0f}, {@code 1f} if
	 * {@code x} is greater than {@code 0f}, or {@code -1f} if {@code x} is less than {@code 0f}.
	 * <p>
	 * @param x a {@code float} value
	 * <p>
	 * @return the sign of {@code x}; {@code 0f} if {@code x} is equal to {@code 0f}, {@code 1f} if
	 *         {@code x} is greater than {@code 0f}, or {@code -1f} if {@code x} is less than
	 *         {@code 0f}
	 */
	public static float sign(final float x) {
		return Math.signum(x);
	}

	/**
	 * Returns the sign of {@code x}; {@code 0.} if {@code x} is equal to {@code 0.}, {@code 1.} if
	 * {@code x} is greater than {@code 0.}, or {@code -1.} if {@code x} is less than {@code 0.}.
	 * <p>
	 * @param x a {@code double} value
	 * <p>
	 * @return the sign of {@code x}; {@code 0.} if {@code x} is equal to {@code 0.}, {@code 1.} if
	 *         {@code x} is greater than {@code 0.}, or {@code -1.} if {@code x} is less than
	 *         {@code 0.}
	 */
	public static double sign(final double x) {
		return Math.signum(x);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// DISCRETE FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int countMinSteps(final double from, final double to, final double step) {
		return from < to ? floorToInt((to - from) / step) : 0;
	}

	public static int countMaxSteps(final double from, final double to, final double step) {
		return from < to ? ceilToInt((to - from) / step) : 0;
	}

	//////////////////////////////////////////////

	public static double getStepsValue(final double from, final double step, final int stepCount) {
		return from + stepCount * step;
	}

	//////////////////////////////////////////////

	public static double remainderMinSteps(final double from, final double to, final double step,
			final int stepCount) {
		return from < to ? to - getStepsValue(from, step, stepCount) : 0;
	}

	public static double remainderMaxSteps(final double from, final double to, final double step,
			final int stepCount) {
		return from < to ? getStepsValue(from, step, stepCount) - to : 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the greatest common divisor (GCD) of {@code a} and {@code b}.
	 * <p>
	 * @param a an {@code int} value
	 * @param b another {@code int} value
	 * <p>
	 * @return the greatest common divisor (GCD) of {@code a} and {@code b}
	 */
	public static int gcd(final int a, final int b) {
		int i = a, j = b, t;
		while (j > 0) {
			t = j;
			j = i % t;
			i = t;
		}
		return i;
	}

	/**
	 * Returns the greatest common divisor (GCD) of {@code a} and {@code b}.
	 * <p>
	 * @param a a {@code long} value
	 * @param b another {@code long} value
	 * <p>
	 * @return the greatest common divisor (GCD) of {@code a} and {@code b}
	 */
	public static long gcd(final long a, final long b) {
		long i = a, j = b, t;
		while (j > 0L) {
			t = j;
			j = i % t;
			i = t;
		}
		return i;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the least common multiple (LCM) of {@code a} and {@code b}.
	 * <p>
	 * @param a an {@code int} value
	 * @param b another {@code int} value
	 * <p>
	 * @return the least common multiple (LCM) of {@code a} and {@code b}
	 */
	public static int lcm(final int a, final int b) {
		return a * b / gcd(a, b);
	}

	/**
	 * Returns the least common multiple (LCM) of {@code a} and {@code b}.
	 * <p>
	 * @param a a {@code long} value
	 * @param b another {@code long} value
	 * <p>
	 * @return the least common multiple (LCM) of {@code a} and {@code b}
	 */
	public static long lcm(final long a, final long b) {
		return a * b / gcd(a, b);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 * {@code upperBound}, {@code lowerBound} if {@code x < lowerBound}, {@code upperBound} if
	 * {@code x > upperBound}.
	 * <p>
	 * @param x          a {@code byte} value
	 * @param lowerBound the {@code byte} lower bound of the domain (inclusive)
	 * @param upperBound the {@code byte} upper bound of the domain (inclusive)
	 * <p>
	 * @return {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 *         {@code upperBound}, {@code lowerBound} if {@code x < lowerBound}, {@code upperBound}
	 *         if {@code x > upperBound}
	 */
	public static byte bound(final byte x, final byte lowerBound, final byte upperBound) {
		if (x < lowerBound) {
			return lowerBound;
		}
		if (x > upperBound) {
			return upperBound;
		}
		return x;
	}

	/**
	 * Returns {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 * {@code upperBound}, {@code lowerBound} if {@code x < lowerBound}, {@code upperBound} if
	 * {@code x > upperBound}.
	 * <p>
	 * @param x          a {@code short} value
	 * @param lowerBound the {@code short} lower bound of the domain (inclusive)
	 * @param upperBound the {@code short} upper bound of the domain (inclusive)
	 * <p>
	 * @return {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 *         {@code upperBound}, {@code lowerBound} if {@code x < lowerBound}, {@code upperBound}
	 *         if {@code x > upperBound}
	 */
	public static short bound(final short x, final short lowerBound, final short upperBound) {
		if (x < lowerBound) {
			return lowerBound;
		}
		if (x > upperBound) {
			return upperBound;
		}
		return x;
	}

	/**
	 * Returns {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 * {@code upperBound}, {@code lowerBound} if {@code x < lowerBound}, {@code upperBound} if
	 * {@code x > upperBound}.
	 * <p>
	 * @param x          a {@code int} value
	 * @param lowerBound the {@code int} lower bound of the domain (inclusive)
	 * @param upperBound the {@code int} upper bound of the domain (inclusive)
	 * <p>
	 * @return {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 *         {@code upperBound}, {@code lowerBound} if {@code x < lowerBound}, {@code upperBound}
	 *         if {@code x > upperBound}
	 */
	public static int bound(final int x, final int lowerBound, final int upperBound) {
		if (x < lowerBound) {
			return lowerBound;
		}
		if (x > upperBound) {
			return upperBound;
		}
		return x;
	}

	/**
	 * Returns {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 * {@code upperBound}, {@code lowerBound} if {@code x < lowerBound}, {@code upperBound} if
	 * {@code x > upperBound}.
	 * <p>
	 * @param x          a {@code long} value
	 * @param lowerBound the {@code long} lower bound of the domain (inclusive)
	 * @param upperBound the {@code long} upper bound of the domain (inclusive)
	 * <p>
	 * @return {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 *         {@code upperBound}, {@code lowerBound} if {@code x < lowerBound}, {@code upperBound}
	 *         if {@code x > upperBound}
	 */
	public static long bound(final long x, final long lowerBound, final long upperBound) {
		if (x < lowerBound) {
			return lowerBound;
		}
		if (x > upperBound) {
			return upperBound;
		}
		return x;
	}

	/**
	 * Returns {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 * {@code upperBound}, {@code lowerBound} if {@code x < lowerBound}, {@code upperBound} if
	 * {@code x > upperBound}, {@code NaN} otherwise.
	 * <p>
	 * @param x          a {@code float} value
	 * @param lowerBound the {@code float} lower bound of the domain (inclusive) (may be
	 *                   {@code Float.NEGATIVE_INFINITY} if there is no lower bound)
	 * @param upperBound the {@code float} upper bound of the domain (inclusive) (may be
	 *                   {@code Float.POSITIVE_INFINITY} if there is no upper bound)
	 * <p>
	 * @return {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 *         {@code upperBound}, {@code lowerBound} if {@code x < lowerBound}, {@code upperBound}
	 *         if {@code x > upperBound}, {@code NaN} otherwise
	 */
	public static float bound(final float x, final float lowerBound, final float upperBound) {
		if (Float.isNaN(x)) {
			IO.warn("The specified float number is NaN");
			return Float.NaN;
		}
		if (x < lowerBound) {
			return lowerBound;
		}
		if (x > upperBound) {
			return upperBound;
		}
		return x;
	}

	/**
	 * Returns {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 * {@code upperBound}, {@code lowerBound} if {@code x < lowerBound}, {@code upperBound} if
	 * {@code x > upperBound}, {@code NaN} otherwise.
	 * <p>
	 * @param x          a {@code double} value
	 * @param lowerBound the {@code double} lower bound of the domain (inclusive) (may be
	 *                   {@code Double.NEGATIVE_INFINITY} if there is no lower bound)
	 * @param upperBound the {@code double} upper bound of the domain (inclusive) (may be
	 *                   {@code Double.POSITIVE_INFINITY} if there is no upper bound)
	 * <p>
	 * @return {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 *         {@code upperBound}, {@code lowerBound} if {@code x < lowerBound}, {@code upperBound}
	 *         if {@code x > upperBound}, {@code NaN} otherwise
	 */
	public static double bound(final double x, final double lowerBound, final double upperBound) {
		if (Double.isNaN(x)) {
			IO.warn("The specified double number is NaN");
			return Double.NaN;
		}
		if (x < lowerBound) {
			return lowerBound;
		}
		if (x > upperBound) {
			return upperBound;
		}
		return x;
	}

	//////////////////////////////////////////////

	/**
	 * Returns {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 * {@code upperBound}, {@code NaN} otherwise.
	 * <p>
	 * @param x          a {@code float} value
	 * @param lowerBound the {@code float} lower bound of the domain (inclusive) (may be
	 *                   {@code Float.NEGATIVE_INFINITY} if there is no lower bound)
	 * @param upperBound the {@code float} upper bound of the domain (inclusive) (may be
	 *                   {@code Float.POSITIVE_INFINITY} if there is no upper bound)
	 * <p>
	 * @return {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 *         {@code upperBound}, {@code NaN} otherwise
	 */
	public static float constrain(final float x, final float lowerBound, final float upperBound) {
		// Check the arguments
		if (Float.isNaN(x)) {
			IO.warn("The specified float number is NaN");
			return Float.NaN;
		}
		if (x < lowerBound) {
			IO.warn("The specified float number ", x,
					" is less than the lower bound of the domain ", lowerBound);
			return Float.NaN;
		}
		if (x > upperBound) {
			IO.warn("The specified float number ", x,
					" is greater than the upper bound of the domain ", upperBound);
			return Float.NaN;
		}
		return x;
	}

	/**
	 * Returns {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 * {@code upperBound}, {@code NaN} otherwise.
	 * <p>
	 * @param x          a {@code double} value
	 * @param lowerBound the {@code double} lower bound of the domain (inclusive) (may be
	 *                   {@code Double.NEGATIVE_INFINITY} if there is no lower bound)
	 * @param upperBound the {@code double} upper bound of the domain (inclusive) (may be
	 *                   {@code Double.POSITIVE_INFINITY} if there is no upper bound)
	 * <p>
	 * @return {@code x} if {@code x} is inside the domain determined by {@code lowerBound} and
	 *         {@code upperBound}, {@code NaN} otherwise
	 */
	public static double constrain(final double x, final double lowerBound,
			final double upperBound) {
		// Check the arguments
		if (Double.isNaN(x)) {
			IO.warn("The specified double number is NaN");
			return Double.NaN;
		}
		if (x < lowerBound) {
			IO.warn("The specified double number ", x,
					" is less than the lower bound of the domain ", lowerBound);
			return Double.NaN;
		}
		if (x > upperBound) {
			IO.warn("The specified double number ", x,
					" is greater than the upper bound of the domain ", upperBound);
			return Double.NaN;
		}
		return x;
	}

	//////////////////////////////////////////////

	public static byte minToShort(final byte... values) {
		// Check the arguments
		ByteArguments.requireNonEmpty(values);

		// Get the minimum value
		byte min = Byte.MAX_VALUE;
		for (final byte value : values) {
			min = value < min ? value : min;
		}
		return min;
	}

	public static short minToShort(final short... values) {
		// Check the arguments
		ShortArguments.requireNonEmpty(values);

		// Get the minimum value
		short min = Short.MAX_VALUE;
		for (final short value : values) {
			min = value < min ? value : min;
		}
		return min;
	}

	public static int minToInt(final int... values) {
		// Check the arguments
		IntegerArguments.requireNonEmpty(values);

		// Get the minimum value
		int min = Integer.MAX_VALUE;
		for (final int value : values) {
			min = value < min ? value : min;
		}
		return min;
	}

	public static long minToLong(final long... values) {
		// Check the arguments
		LongArguments.requireNonEmpty(values);

		// Get the minimum value
		long min = Long.MAX_VALUE;
		for (final long value : values) {
			min = value < min ? value : min;
		}
		return min;
	}

	public static float minToFloat(final float... values) {
		// Check the arguments
		FloatArguments.requireNonEmpty(values);

		// Get the minimum value
		float min = Float.MAX_VALUE;
		for (final float value : values) {
			min = Math.min(value, min);
		}
		return min;
	}

	public static double min(final double... values) {
		// Check the arguments
		DoubleArguments.requireNonEmpty(values);

		// Get the minimum value
		double min = Double.MAX_VALUE;
		for (final double value : values) {
			min = Math.min(value, min);
		}
		return min;
	}

	public static double min(final Number[] numbers) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(numbers, "numbers");

		// Get the minimum value
		double min = Double.MAX_VALUE;
		for (final Number number : numbers) {
			if (number != null) {
				min = Math.min(number.doubleValue(), min);
			}
		}
		return min;
	}

	public static double min(final Collection<? extends Number> numbers) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(numbers, "numbers");

		// Get the minimum value
		double min = Double.MAX_VALUE;
		for (final Number number : numbers) {
			if (number != null) {
				min = Math.min(number.doubleValue(), min);
			}
		}
		return min;
	}

	//////////////////////////////////////////////

	public static byte maxToByte(final byte... values) {
		// Check the arguments
		ByteArguments.requireNonEmpty(values);

		// Get the maximum value
		byte max = Byte.MIN_VALUE;
		for (final byte value : values) {
			max = value > max ? value : max;
		}
		return max;
	}

	public static short maxToShort(final short... values) {
		// Check the arguments
		ShortArguments.requireNonEmpty(values);

		// Get the maximum value
		short max = Short.MIN_VALUE;
		for (final short value : values) {
			max = value > max ? value : max;
		}
		return max;
	}

	public static int maxToInt(final int... values) {
		// Check the arguments
		IntegerArguments.requireNonEmpty(values);

		// Get the maximum value
		int max = Integer.MIN_VALUE;
		for (final int value : values) {
			max = value > max ? value : max;
		}
		return max;
	}

	public static long maxToLong(final long... values) {
		// Check the arguments
		LongArguments.requireNonEmpty(values);

		// Get the maximum value
		long max = Long.MIN_VALUE;
		for (final long value : values) {
			max = value > max ? value : max;
		}
		return max;
	}

	public static float maxToFloat(final float... values) {
		// Check the arguments
		FloatArguments.requireNonEmpty(values);

		// Get the maximum value
		float max = Float.MIN_VALUE;
		for (final float value : values) {
			max = Math.max(max, value);
		}
		return max;
	}

	public static double max(final double... values) {
		// Check the arguments
		DoubleArguments.requireNonEmpty(values);

		// Get the maximum value
		double max = Double.MIN_VALUE;
		for (final double value : values) {
			max = Math.max(max, value);
		}
		return max;
	}

	public static double max(final Number[] numbers) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(numbers, "numbers");

		// Get the maximum value
		double max = Double.MIN_VALUE;
		for (final Number number : numbers) {
			if (number != null) {
				max = Math.max(max, number.doubleValue());
			}
		}
		return max;
	}

	public static double max(final Collection<? extends Number> numbers) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(numbers, "numbers");

		// Get the maximum value
		double max = Double.MIN_VALUE;
		for (final Number number : numbers) {
			if (number != null) {
				max = Math.max(max, number.doubleValue());
			}
		}
		return max;
	}

	//////////////////////////////////////////////

	public static int closest(final byte[] values, final byte value) {
		int index = -1;
		byte delta = Byte.MAX_VALUE;
		for (int i = 0; i < values.length; ++i) {
			final byte d = delta(values[i], value);
			if (d < delta) {
				index = i;
				delta = d;
			}
		}
		return index;
	}

	public static int closest(final short[] values, final short value) {
		int index = -1;
		short delta = Short.MAX_VALUE;
		for (int i = 0; i < values.length; ++i) {
			final short d = delta(values[i], value);
			if (d < delta) {
				index = i;
				delta = d;
			}
		}
		return index;
	}

	public static int closest(final int[] values, final int value) {
		int index = -1;
		int delta = Integer.MAX_VALUE;
		for (int i = 0; i < values.length; ++i) {
			final int d = delta(values[i], value);
			if (d < delta) {
				index = i;
				delta = d;
			}
		}
		return index;
	}

	public static int closest(final long[] values, final long value) {
		int index = -1;
		long delta = Long.MAX_VALUE;
		for (int i = 0; i < values.length; ++i) {
			final long d = delta(values[i], value);
			if (d < delta) {
				index = i;
				delta = d;
			}
		}
		return index;
	}

	public static int closest(final float[] values, final float value) {
		int index = -1;
		float delta = Float.MAX_VALUE;
		for (int i = 0; i < values.length; ++i) {
			final float d = delta(values[i], value);
			if (d < delta) {
				index = i;
				delta = d;
			}
		}
		return index;
	}

	public static int closest(final double[] values, final double value) {
		int index = -1;
		double delta = Double.MAX_VALUE;
		for (int i = 0; i < values.length; ++i) {
			final double d = delta(values[i], value);
			if (d < delta) {
				index = i;
				delta = d;
			}
		}
		return index;
	}

	public static int closest(final Number[] numbers, final double value) {
		int index = -1;
		double delta = Double.MAX_VALUE;
		for (int i = 0; i < numbers.length; ++i) {
			if (numbers[i] != null) {
				final double d = delta(numbers[i].doubleValue(), value);
				if (d < delta) {
					index = i;
					delta = d;
				}
			}
		}
		return index;
	}

	public static int closest(final Collection<? extends Number> numbers, final double value) {
		int index = -1;
		double delta = Double.MAX_VALUE;
		int i = 0;
		for (final Number number : numbers) {
			if (number != null) {
				final double d = delta(number.doubleValue(), value);
				if (d < delta) {
					index = i;
					delta = d;
				}
			}
			++i;
		}
		return index;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code x} rounded up to the nearest multiple of {@code unit}.
	 * <p>
	 * @param value the {@code int} value to round up
	 * @param unit  an {@code int} value
	 * <p>
	 * @return {@code x} rounded up to the nearest multiple of {@code unit}
	 */
	public static int roundUp(final int value, final int unit) {
		final int remainder = value % unit;
		if (remainder == 0) {
			return value;
		}
		return value + unit - remainder;
	}

	/**
	 * Returns {@code x} rounded up to the nearest multiple of {@code unit}.
	 * <p>
	 * @param value the {@code long} value to round up
	 * @param unit  a {@code long} value
	 * <p>
	 * @return {@code x} rounded up to the nearest multiple of {@code unit}
	 */
	public static long roundUp(final long value, final long unit) {
		final long remainder = value % unit;
		if (remainder == 0) {
			return value;
		}
		return value + unit - remainder;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the least significant bit of the specified {@code float} value.
	 * <p>
	 * @param value a {@code float} value
	 * <p>
	 * @return {@code ulp(x)}
	 */
	public static float ulp(final float value) {
		if (Float.isInfinite(value)) {
			return Float.POSITIVE_INFINITY;
		}
		return abs(value - Float.intBitsToFloat(Float.floatToIntBits(value) ^ 1));
	}

	/**
	 * Returns the least significant bit of the specified value.
	 * <p>
	 * @param value a {@code double} value
	 * <p>
	 * @return {@code ulp(x)}
	 */
	public static double ulp(final double value) {
		if (Double.isInfinite(value)) {
			return Double.POSITIVE_INFINITY;
		}
		return abs(value - Double.longBitsToDouble(Double.doubleToRawLongBits(value) ^ 1));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GEOMETRIC FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the approximate edge length of the circumscribed square of the circle of the
	 * specified radius.
	 * <p>
	 * @param radius the radius of the circle
	 * <p>
	 * @return the approximate edge length of the circumscribed square of the circle of the
	 *         specified radius
	 */
	public static double getCircumscribedSquare(final double radius) {
		return 2. * radius;
	}

	/**
	 * Returns the approximate edge length of the inscribed square of the circle of the specified
	 * radius.
	 * <p>
	 * @param radius the radius of the circle
	 * <p>
	 * @return the approximate edge length of the inscribed square of the circle of the specified
	 *         radius
	 */
	public static double getInscribedSquare(final double radius) {
		return SQUARE_ROOT_OF_2 * radius;
	}

	/**
	 * Returns the approximate edge length of the square that has the same area of the circle of the
	 * specified radius.
	 * <p>
	 * @param radius the radius of the circle
	 * <p>
	 * @return the approximate edge length of the square that has the same area of the circle of the
	 *         specified radius
	 */
	public static double getSquareWithSameAreaAsCircle(final double radius) {
		return SQUARE_ROOT_OF_PI * radius;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// TRIGONOMETRIC FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the sine of the specified angle (in radians).
	 * <p>
	 * @param angle an angle (in radians)
	 * <p>
	 * @return the sine of the specified angle (in radians)
	 */
	public static double sin(final double angle) {
		return StrictMath.sin(angle);
	}

	/**
	 * Returns the secant of the specified angle (in radians).
	 * <p>
	 * @param angle an angle (in radians)
	 * <p>
	 * @return the secant of the specified angle (in radians)
	 */
	public static double sec(final double angle) {
		return 1. / sin(angle);
	}

	/**
	 * Returns the cosine of the specified angle (in radians).
	 * <p>
	 * @param angle an angle (in radians)
	 * <p>
	 * @return the cosine of the specified angle (in radians)
	 */
	public static double cos(final double angle) {
		return StrictMath.cos(angle);
	}

	/**
	 * Returns the cosecant of the specified angle (in radians).
	 * <p>
	 * @param angle an angle (in radians)
	 * <p>
	 * @return the cosecant of the specified angle (in radians)
	 */
	public static double cosec(final double angle) {
		return 1. / cos(angle);
	}

	/**
	 * Returns the tangent of the specified angle (in radians).
	 * <p>
	 * @param angle an angle (in radians)
	 * <p>
	 * @return the tangent of the specified angle (in radians)
	 */
	public static double tan(final double angle) {
		return StrictMath.tan(angle);
	}

	/**
	 * Returns the cotangent of the specified angle (in radians).
	 * <p>
	 * @param angle an angle (in radians)
	 * <p>
	 * @return the cotangent of the specified angle (in radians)
	 */
	public static double cot(final double angle) {
		return 1. / tan(angle);
	}

	/**
	 * Returns the hypotenuse of the specified values.
	 * <p>
	 * @param x a {@code double} value
	 * @param y another {@code double} value
	 * <p>
	 * @return the hypotenuse of the specified values
	 */
	public static double hypot(final double x, final double y) {
		return StrictMath.hypot(x, y);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the inverse (in radians) of the specified sine value.
	 * <p>
	 * @param value a sine value
	 * <p>
	 * @return the inverse (in radians) of the specified sine value
	 */
	public static double asin(final double value) {
		return StrictMath.asin(value);
	}

	/**
	 * Returns the inverse (in radians) of the specified secant value.
	 * <p>
	 * @param value a secant value
	 * <p>
	 * @return the inverse (in radians) of the specified secant value
	 */
	public static double asec(final double value) {
		return asin(1. / value);
	}

	/**
	 * Returns the inverse (in radians) of the specified cosine value.
	 * <p>
	 * @param value a cosine value
	 * <p>
	 * @return the inverse (in radians) of the specified cosine value
	 */
	public static double acos(final double value) {
		return StrictMath.acos(value);
	}

	/**
	 * Returns the inverse (in radians) of the specified cosecant value.
	 * <p>
	 * @param value a cosecant value
	 * <p>
	 * @return the inverse (in radians) of the specified cosecant value
	 */
	public static double acosec(final double value) {
		return acos(1. / value);
	}

	/**
	 * Returns the inverse (in radians) of the specified tangent value.
	 * <p>
	 * @param value a tangent value
	 * <p>
	 * @return the inverse (in radians) of the specified tangent value
	 */
	public static double atan(final double value) {
		return StrictMath.atan(value);
	}

	/**
	 * Returns the inverse (in radians) of the specified cotangent value.
	 * <p>
	 * @param value a cotangent value
	 * <p>
	 * @return the inverse (in radians) of the specified cotangent value
	 */
	public static double acot(final double value) {
		return atan(1. / value);
	}

	/**
	 * Returns the angle {@code theta} from the conversion of the specified rectangular coordinates
	 * ({@code x}, {@code y}) to the polar coordinates ({@code r}, {@code theta}) by computing an
	 * arc tangent of {@code y / x} in the range of {@code -π} to {@code π}.
	 * <p>
	 * @param x a {@code double} x-coordinate
	 * @param y a {@code double} y-coordinate
	 * <p>
	 * @return the angle {@code theta} from the conversion of the specified rectangular coordinates
	 *         ({@code x}, {@code y}) to the polar coordinates ({@code r}, {@code theta}) by
	 *         computing an arc tangent of {@code y / x} in the range of {@code -π} to {@code π}
	 */
	public static double atan2(final double x, final double y) {
		return StrictMath.atan2(y, x);
	}

	/**
	 * Returns the angle {@code theta} from the conversion of the specified rectangular coordinates
	 * ({@code x}, {@code y}) to the polar coordinates ({@code r}, {@code theta}) by computing an
	 * arc tangent of {@code y / x} in the range of {@code 0.} to {@code 2. * π}.
	 * <p>
	 * @param x a {@code double} x-coordinate
	 * @param y a {@code double} y-coordinate
	 * <p>
	 * @return the angle {@code theta} from the conversion of the specified rectangular coordinates
	 *         ({@code x}, {@code y}) to the polar coordinates ({@code r}, {@code theta}) by
	 *         computing an arc tangent of {@code y / x} in the range of {@code 0.} to
	 *         {@code 2. * π}
	 */
	public static double atan3(final double x, final double y) {
		final double theta = atan2(x, y);
		if (theta < 0.) {
			return 2. * PI + theta;
		}
		return theta;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the hyperbolic sine of the specified angle (in radians).
	 * <p>
	 * @param angle an angle (in radians)
	 * <p>
	 * @return the hyperbolic sine of the specified angle (in radians)
	 */
	public static double sinh(final double angle) {
		return StrictMath.sinh(angle);
	}

	/**
	 * Returns the hyperbolic secant of the specified angle (in radians).
	 * <p>
	 * @param angle an angle (in radians)
	 * <p>
	 * @return the hyperbolic secant of the specified angle (in radians)
	 */
	public static double sech(final double angle) {
		return 1. / sinh(angle);
	}

	/**
	 * Returns the hyperbolic cosine of the specified angle (in radians).
	 * <p>
	 * @param angle an angle (in radians)
	 * <p>
	 * @return the hyperbolic cosine of the specified angle (in radians)
	 */
	public static double cosh(final double angle) {
		return StrictMath.cosh(angle);
	}

	/**
	 * Returns the hyperbolic cosecant of the specified angle (in radians).
	 * <p>
	 * @param angle an angle (in radians)
	 * <p>
	 * @return the hyperbolic cosecant of the specified angle (in radians)
	 */
	public static double cosech(final double angle) {
		return 1. / cosh(angle);
	}

	/**
	 * Returns the hyperbolic tangent of the specified angle (in radians).
	 * <p>
	 * @param angle an angle (in radians)
	 * <p>
	 * @return the hyperbolic tangent of the specified angle (in radians)
	 */
	public static double tanh(final double angle) {
		return StrictMath.tanh(angle);
	}

	/**
	 * Returns the hyperbolic cotangent of the specified angle (in radians).
	 * <p>
	 * @param angle an angle (in radians)
	 * <p>
	 * @return the hyperbolic cotangent of the specified angle (in radians)
	 */
	public static double coth(final double angle) {
		return 1. / tanh(angle);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the inverse (in radians) of the specified hyperbolic sine value.
	 * <p>
	 * @param value a hyperbolic sine value
	 * <p>
	 * @return the inverse (in radians) of the specified hyperbolic sine value
	 */
	public static double asinh(final double value) {
		return log(value + sqrt(square(value) + 1.));
	}

	/**
	 * Returns the inverse (in radians) of the specified hyperbolic secant value.
	 * <p>
	 * @param value a hyperbolic secant value
	 * <p>
	 * @return the inverse (in radians) of the specified hyperbolic secant value
	 */
	public static double asech(final double value) {
		return asinh(1. / value);
	}

	/**
	 * Returns the inverse (in radians) of the specified hyperbolic cosine value.
	 * <p>
	 * @param value a hyperbolic cosine value
	 * <p>
	 * @return the inverse (in radians) of the specified hyperbolic cosine value
	 */
	public static double acosh(final double value) {
		return log(value + sqrt(square(value) - 1.));
	}

	/**
	 * Returns the inverse (in radians) of the specified hyperbolic cosecant value.
	 * <p>
	 * @param value a hyperbolic cosecant value
	 * <p>
	 * @return the inverse (in radians) of the specified hyperbolic cosecant value
	 */
	public static double acosech(final double value) {
		return acosh(1. / value);
	}

	/**
	 * Returns the inverse (in radians) of the specified hyperbolic tangent value.
	 * <p>
	 * @param value a hyperbolic tangent value
	 * <p>
	 * @return the inverse (in radians) of the specified hyperbolic tangent value
	 */
	public static double atanh(final double value) {
		return log((1. + value) / (1. - value)) / 2.;
	}

	/**
	 * Returns the inverse (in radians) of the specified hyperbolic cotangent value.
	 * <p>
	 * @param value a hyperbolic cotangent value
	 * <p>
	 * @return the inverse (in radians) of the specified hyperbolic cotangent value
	 */
	public static double acoth(final double value) {
		return atanh(1. / value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the haversine of the specified angle (in radians).
	 * <p>
	 * @param angle an angle (in radians)
	 * <p>
	 * @return the haversine of the specified angle (in radians)
	 */
	public static double hav(final double angle) {
		return (1. - cos(angle)) / 2.;
	}

	/**
	 * Returns the inverse (in radians) of the specified haversine value.
	 * <p>
	 * @param value a haversine value
	 * <p>
	 * @return the inverse (in radians) of the specified haversine value
	 */
	public static double ahav(final double value) {
		return acos(1. - 2. * value);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the great-circle distance or orthodromic distance (shortest distance) between the
	 * points defined by their latitudes and longitudes (in radians) around the sphere of the
	 * specified radius.
	 * <p>
	 * @param radius     the radius of the sphere
	 * @param latitude1  the latitude of the first point (in radians)
	 * @param longitude1 the longitude of the first point (in radians)
	 * @param latitude2  the latitude of the second point (in radians)
	 * @param longitude2 the longitude of the second point (in radians)
	 * <p>
	 * @return the great-circle distance or orthodromic distance (shortest distance) between the
	 *         points defined by their latitudes and longitudes (in radians) around the sphere of
	 *         the specified radius
	 */
	public static double getGreatCircleDistance(final double radius, final double latitude1,
			final double longitude1, final double latitude2, final double longitude2) {
		final double h = hav(latitude2 - latitude1) +
				cos(latitude1) * cos(latitude2) * hav(longitude2 - longitude1);
		return 2. * radius * atan2(sqrt(1. - h), sqrt(h));
	}
}
