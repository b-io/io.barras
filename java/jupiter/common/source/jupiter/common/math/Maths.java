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
	 * The tolerance level (or termination criterion) {@code ε} for {@code double} values.
	 */
	public static volatile double TOLERANCE = 1E-10;
	/**
	 * The tiny tolerance level for {@code double} values.
	 */
	public static volatile double TINY_TOLERANCE = 1E-100;

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final double DEFAULT_CONFIDENCE = 0.975; // 97.5%
	public static final double DEFAULT_Z = 1.9599639845400536; // 97.5%

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final double LOG_2 = Math.log(2.);
	public static final double SQUARE_ROOT_OF_2 = Math.sqrt(2.);

	//////////////////////////////////////////////

	/**
	 * The Napier constant {@code e}, the base of the natural logarithm.
	 */
	public static final double E = 2850325. / 1048576. + 8.254840070411028747E-8;
	public static final double SQUARE_ROOT_OF_E = Math.sqrt(E);

	//////////////////////////////////////////////

	/**
	 * The Archimede constant {@code π}, the ratio of the circumference of a circle to its diameter.
	 */
	public static final double PI = 105414357. / 33554432. + 1.984187159361080883E-9;
	public static final double SQUARE_ROOT_OF_PI = Math.sqrt(PI);
	public static final double SQUARE_ROOT_OF_2_PI = Math.sqrt(2. * PI);
	public static final double DEGREE_TO_RADIAN = PI / 180.;

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

	public static short floorToShort(final double number) {
		return Shorts.convert(Math.floor(number));
	}

	public static int floorToInt(final double number) {
		return Integers.convert(Math.floor(number));
	}

	public static long floorToLong(final double number) {
		return Longs.convert(Math.floor(number));
	}

	//////////////////////////////////////////////

	public static short ceilToShort(final double number) {
		return Shorts.convert(Math.ceil(number));
	}

	public static int ceilToInt(final double number) {
		return Integers.convert(Math.ceil(number));
	}

	public static long ceilToLong(final double number) {
		return Longs.convert(Math.ceil(number));
	}

	//////////////////////////////////////////////

	public static short roundToShort(final double number) {
		return Shorts.convert(Math.round(number));
	}

	public static int roundToInt(final double number) {
		return Integers.convert(Math.round(number));
	}

	public static long roundToLong(final double number) {
		return Math.round(number);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// BASIC OPERATORS
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

	public static <T extends Number> double sum(final T... numbers) {
		double sum = 0.;
		for (final T number : numbers) {
			if (number != null) {
				sum += number.doubleValue();
			}
		}
		return sum;
	}

	public static <E extends Number> double sum(final Collection<E> numbers) {
		double sum = 0.;
		for (final E number : numbers) {
			if (number != null) {
				sum += number.doubleValue();
			}
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

	public static double sumWithoutNaN(final double... values) {
		double sum = 0.;
		for (final double value : values) {
			if (!Double.isNaN(value)) {
				sum += value;
			}
		}
		return sum;
	}

	public static <T extends Number> double sumWithoutNaN(final T... numbers) {
		double sum = 0.;
		for (final T number : numbers) {
			if (number != null) {
				final double elementValue = number.doubleValue();
				if (!Double.isNaN(elementValue)) {
					sum += elementValue;
				}
			}
		}
		return sum;
	}

	public static <E extends Number> double sumWithoutNaN(final Collection<E> numbers) {
		double sum = 0.;
		for (final E number : numbers) {
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

	public static double sumOfSquares(final double[] values, final double mean) {
		double sum = 0.;
		for (final double value : values) {
			sum += square(value - mean);
		}
		return sum;
	}

	public static <T extends Number> double sumOfSquares(final T[] numbers, final double mean) {
		double sum = 0.;
		for (final T number : numbers) {
			if (number != null) {
				sum += square(number.doubleValue() - mean);
			}
		}
		return sum;
	}

	public static <E extends Number> double sumOfSquares(final Collection<E> numbers,
			final double mean) {
		double sum = 0.;
		for (final E number : numbers) {
			if (number != null) {
				sum += square(number.doubleValue() - mean);
			}
		}
		return sum;
	}

	//////////////////////////////////////////////

	public static double sumOfSquaresWithoutNaN(final double[] values, final double mean) {
		double sum = 0.;
		for (final double value : values) {
			if (!Double.isNaN(value)) {
				sum += square(value - mean);
			}
		}
		return sum;
	}

	public static <T extends Number> double sumOfSquaresWithoutNaN(final T[] numbers,
			final double mean) {
		double sum = 0.;
		for (final T number : numbers) {
			if (number != null) {
				final double elementValue = number.doubleValue();
				if (!Double.isNaN(elementValue)) {
					sum += square(elementValue - mean);
				}
			}
		}
		return sum;
	}

	public static <E extends Number> double sumOfSquaresWithoutNaN(final Collection<E> numbers,
			final double mean) {
		double sum = 0.;
		for (final E number : numbers) {
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

	public static <T extends Number> double weightedSum(final T[] numbers, final T[] weights) {
		// Check the arguments
		ArrayArguments.requireSameLength(numbers, weights);

		// Sum
		double sum = 0.;
		for (int i = 0; i < numbers.length; ++i) {
			sum += weights[i].doubleValue() * numbers[i].doubleValue();
		}
		return sum;
	}

	public static <E extends Number> double weightedSum(final List<E> numbers,
			final List<E> weights) {
		// Check the arguments
		CollectionArguments.requireSameSize(numbers, weights);

		// Sum
		double sum = 0.;
		final Iterator<E> weightIterator = weights.iterator();
		final Iterator<E> numberIterator = numbers.iterator();
		while (weightIterator.hasNext() && numberIterator.hasNext()) {
			sum += weightIterator.next().doubleValue() * numberIterator.next().doubleValue();
		}
		return sum;
	}

	//////////////////////////////////////////////

	/**
	 * Increments the specified {@code byte} array between the specified indexes.
	 * <p>
	 * @param values    the {@code byte} array to increment
	 * @param fromIndex the index to start incrementing from (inclusive)
	 * @param toIndex   the index to finish incrementing at (exclusive)
	 */
	public static void increment(final byte[] values, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			++values[i];
		}
	}

	/**
	 * Increments the specified {@code short} array between the specified indexes.
	 * <p>
	 * @param values    the {@code short} array to increment
	 * @param fromIndex the index to start incrementing from (inclusive)
	 * @param toIndex   the index to finish incrementing at (exclusive)
	 */
	public static void increment(final short[] values, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			++values[i];
		}
	}

	/**
	 * Increments the specified {@code int} array between the specified indexes.
	 * <p>
	 * @param values    the {@code int} array to increment
	 * @param fromIndex the index to start incrementing from (inclusive)
	 * @param toIndex   the index to finish incrementing at (exclusive)
	 */
	public static void increment(final int[] values, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			++values[i];
		}
	}

	/**
	 * Increments the specified {@code long} array between the specified indexes.
	 * <p>
	 * @param values    the {@code long} array to increment
	 * @param fromIndex the index to start incrementing from (inclusive)
	 * @param toIndex   the index to finish incrementing at (exclusive)
	 */
	public static void increment(final long[] values, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			++values[i];
		}
	}

	/**
	 * Increments the specified {@code float} array between the specified indexes.
	 * <p>
	 * @param values    the {@code float} array to increment
	 * @param fromIndex the index to start incrementing from (inclusive)
	 * @param toIndex   the index to finish incrementing at (exclusive)
	 */
	public static void increment(final float[] values, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			++values[i];
		}
	}

	/**
	 * Increments the specified {@code double} array between the specified indexes.
	 * <p>
	 * @param values    the {@code double} array to increment
	 * @param fromIndex the index to start incrementing from (inclusive)
	 * @param toIndex   the index to finish incrementing at (exclusive)
	 */
	public static void increment(final double[] values, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			++values[i];
		}
	}

	//////////////////////////////////////////////

	/**
	 * Decrements the specified {@code byte} array between the specified indexes.
	 * <p>
	 * @param values    the {@code byte} array to decrement
	 * @param fromIndex the index to start decrementing from (inclusive)
	 * @param toIndex   the index to finish decrementing at (exclusive)
	 */
	public static void decrement(final byte[] values, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			--values[i];
		}
	}

	/**
	 * Decrements the specified {@code short} array between the specified indexes.
	 * <p>
	 * @param values    the {@code short} array to decrement
	 * @param fromIndex the index to start decrementing from (inclusive)
	 * @param toIndex   the index to finish decrementing at (exclusive)
	 */
	public static void decrement(final short[] values, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			--values[i];
		}
	}

	/**
	 * Decrements the specified {@code int} array between the specified indexes.
	 * <p>
	 * @param values    the {@code int} array to decrement
	 * @param fromIndex the index to start decrementing from (inclusive)
	 * @param toIndex   the index to finish decrementing at (exclusive)
	 */
	public static void decrement(final int[] values, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			--values[i];
		}
	}

	/**
	 * Decrements the specified {@code long} array between the specified indexes.
	 * <p>
	 * @param values    the {@code long} array to decrement
	 * @param fromIndex the index to start decrementing from (inclusive)
	 * @param toIndex   the index to finish decrementing at (exclusive)
	 */
	public static void decrement(final long[] values, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			--values[i];
		}
	}

	/**
	 * Decrements the specified {@code float} array between the specified indexes.
	 * <p>
	 * @param values    the {@code float} array to decrement
	 * @param fromIndex the index to start decrementing from (inclusive)
	 * @param toIndex   the index to finish decrementing at (exclusive)
	 */
	public static void decrement(final float[] values, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			--values[i];
		}
	}

	/**
	 * Decrements the specified {@code double} array between the specified indexes.
	 * <p>
	 * @param values    the {@code double} array to decrement
	 * @param fromIndex the index to start decrementing from (inclusive)
	 * @param toIndex   the index to finish decrementing at (exclusive)
	 */
	public static void decrement(final double[] values, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			--values[i];
		}
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
	 * between the specified indexes.
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
	 * between the specified indexes.
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
	 * between the specified indexes.
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
	 * between the specified indexes.
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
	 * between the specified indexes.
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
	 * between the specified indexes.
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

	public static double delta(final double a, final double b) {
		return Math.abs(a - b);
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

	/**
	 * Returns the value of the specified base raised to the power of the specified exponent.
	 * <p>
	 * @param base     an {@code int} value
	 * @param exponent an {@code int} value
	 * <p>
	 * @return the value of the specified base raised to the power of the specified exponent
	 */
	public static int pow(final int base, final int exponent) {
		return Integers.convert(Math.pow(base, exponent));
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
		return Longs.convert(Math.pow(base, exponent));
	}

	/**
	 * Returns the value of two raised to the power of the specified exponent.
	 * <p>
	 * @param exponent an {@code int} value
	 * <p>
	 * @return the value of two raised to the power of the specified exponent
	 */
	public static int pow2(final int exponent) {
		return 1 << exponent;
	}

	/**
	 * Returns the value of two raised to the power of the specified exponent.
	 * <p>
	 * @param exponent a {@code long} value
	 * <p>
	 * @return the value of two raised to the power of the specified exponent
	 */
	public static long pow2(final long exponent) {
		return 1L << exponent;
	}

	public static double square(final double value) {
		return value * value;
	}

	public static double cube(final double value) {
		return value * value * value;
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
		return Math.floor(Math.exp(factorialLog(Integers.convert(n))) + 0.5);
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
			return Math.log(LONG_FACTORIALS[Integers.convert(n)]);
		}
		double factorialLog = 0.;
		for (int i = 2; i <= n; ++i) {
			factorialLog += Math.log(i);
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
			return Math.log(LONG_FACTORIALS[Integers.convert(n)]);
		}
		return SQUARE_ROOT_OF_PI * Math.pow(n / E, n) *
				Math.pow(((8 * n + 4) * n + 1) * n + 1 / 30., F_1_6);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double safeDivision(final double dividend, final double divisor) {
		return dividend / (divisor + TOLERANCE);
	}

	//////////////////////////////////////////////

	public static double safeInverse(final double value) {
		return safeDivision(1., value);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ANALYTIC FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double safeLog(final double value) {
		return Math.log(value + TOLERANCE);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// DISCRETE FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int countIncrements(final double min, final double max, final double increment) {
		return min <= max ? 1 + floorToInt((max - min) / increment) : 0;
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
	 * Returns {@code number} rounded up to the nearest multiple of {@code unit}.
	 * <p>
	 * @param number the {@code int} value to round up
	 * @param unit   an {@code int} value
	 * <p>
	 * @return {@code number} rounded up to the nearest multiple of {@code unit}
	 */
	public static int roundUp(final int number, final int unit) {
		final int remainder = number % unit;
		if (remainder == 0) {
			return number;
		}
		return number + unit - remainder;
	}

	/**
	 * Returns {@code number} rounded up to the nearest multiple of {@code unit}.
	 * <p>
	 * @param number the {@code long} value to round up
	 * @param unit   a {@code long} value
	 * <p>
	 * @return {@code number} rounded up to the nearest multiple of {@code unit}
	 */
	public static long roundUp(final long number, final long unit) {
		final long remainder = number % unit;
		if (remainder == 0) {
			return number;
		}
		return number + unit - remainder;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

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
			min = Math.min(value, min);
		}
		return min;
	}

	public static long minToLong(final long... values) {
		// Check the arguments
		LongArguments.requireNonEmpty(values);

		// Get the minimum value
		long min = Long.MAX_VALUE;
		for (final long value : values) {
			min = Math.min(value, min);
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

	public static double minToDouble(final double... values) {
		// Check the arguments
		DoubleArguments.requireNonEmpty(values);

		// Get the minimum value
		double min = Double.MAX_VALUE;
		for (final double value : values) {
			min = Math.min(value, min);
		}
		return min;
	}

	public static <T extends Number> double minToDouble(final T... numbers) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(numbers);

		// Get the minimum value
		double min = Double.MAX_VALUE;
		for (final T number : numbers) {
			min = Math.min(number.doubleValue(), min);
		}
		return min;
	}

	public static <E extends Number> double minToDouble(final Collection<E> numbers) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(numbers);

		// Get the minimum value
		double min = Double.MAX_VALUE;
		for (final E number : numbers) {
			min = Math.min(number.doubleValue(), min);
		}
		return min;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

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
			max = Math.max(max, value);
		}
		return max;
	}

	public static long maxToLong(final long... values) {
		// Check the arguments
		LongArguments.requireNonEmpty(values);

		// Get the maximum value
		long max = Long.MIN_VALUE;
		for (final long value : values) {
			max = Math.max(max, value);
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

	public static double maxToDouble(final double... values) {
		// Check the arguments
		DoubleArguments.requireNonEmpty(values);

		// Get the maximum value
		double max = Double.MIN_VALUE;
		for (final double value : values) {
			max = Math.max(max, value);
		}
		return max;
	}

	public static <T extends Number> double maxToDouble(final T... numbers) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(numbers);

		// Get the maximum value
		double max = Double.MIN_VALUE;
		for (final T number : numbers) {
			max = Math.max(max, number.doubleValue());
		}
		return max;
	}

	public static <E extends Number> double maxToDouble(final Collection<E> numbers) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(numbers);

		// Get the maximum value
		double max = Double.MIN_VALUE;
		for (final E number : numbers) {
			max = Math.max(max, number.doubleValue());
		}
		return max;
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
	 * Returns the cosinus of the specified angle in degrees.
	 * <p>
	 * @param angle an angle in degrees
	 * <p>
	 * @return the cosinus of the specified angle in degrees
	 */
	public static double cos(final double angle) {
		return Math.cos(angle * DEGREE_TO_RADIAN);
	}

	/**
	 * Returns the sinus of the specified angle in degrees.
	 * <p>
	 * @param angle an angle in degrees
	 * <p>
	 * @return the sinus of the specified angle in degrees
	 */
	public static double sin(final double angle) {
		return Math.sin(angle * DEGREE_TO_RADIAN);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the sinus of the specified angle in degrees.
	 * <p>
	 * @param angle an angle in degrees
	 * <p>
	 * @return the sinus of the specified angle in degrees
	 */
	public static double tan(final double angle) {
		return Math.tan(angle * DEGREE_TO_RADIAN);
	}

	/**
	 * Returns the cotangent of the specified angle in degrees.
	 * <p>
	 * @param angle a {@code double} angle in degrees
	 * <p>
	 * @return the cotangent of the specified angle in degrees
	 */
	public static double cot(final double angle) {
		return 1. / tan(angle);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the haversine of the specified angle in degrees.
	 * <p>
	 * @param angle a {@code double} angle in degrees
	 * <p>
	 * @return the haversine of the specified angle in degrees
	 */
	public static double hav(final double angle) {
		return (1. - cos(angle)) / 2.;
	}
}
