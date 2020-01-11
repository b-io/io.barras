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
package jupiter.common.util;

import static jupiter.common.util.Formats.DECIMAL_FORMAT;

import java.util.Collection;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.DoubleParser;
import jupiter.common.math.Maths;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;

public class Doubles {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final double[] EMPTY_PRIMITIVE_ARRAY = new double[] {};
	public static final Double[] EMPTY_ARRAY = new Double[] {};

	protected static final DoubleParser PARSER = new DoubleParser();

	public static volatile Random RANDOM = new Random();

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Comparator<Double> COMPARATOR = new Comparator<Double>() {
		@Override
		public int compare(final Double a, final Double b) {
			return Doubles.compare(a, b);
		}
	};

	public static final Comparator<double[]> ARRAY_COMPARATOR = new Comparator<double[]>() {
		@Override
		public int compare(final double[] a, final double[] b) {
			return Doubles.compare(a, b);
		}
	};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Doubles}.
	 */
	protected Doubles() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double getDecimalPart(final double value) {
		return value - Math.floor(value);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Double} converted from the specified {@link Object}.
	 * <p>
	 * @param object the {@link Object} to convert
	 * <p>
	 * @return a {@link Double} converted from the specified {@link Object}
	 */
	public static Double convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code double} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object the {@code T} object to convert
	 * <p>
	 * @return a {@code double} value from the specified {@code T} object
	 */
	public static <T> double toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code double} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@code double} array from the specified {@code T} array
	 */
	public static <T> double[] toPrimitiveArray(final T[] array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a {@code double} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@code double} array from the specified {@code T} array
	 */
	public static <T> double[] asPrimitiveArray(final T... array) {
		return toPrimitiveArray(array);
	}

	/**
	 * Returns a {@code double} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a {@code double} array from the specified 2D {@code T} array
	 */
	public static <T> double[] toPrimitiveArray(final T[][] array2D) {
		return PARSER.callToPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code double} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a {@code double} array from the specified 2D {@code T} array
	 */
	public static <T> double[] asPrimitiveArray(final T[]... array2D) {
		return toPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code double} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a {@code double} array from the specified 3D {@code T} array
	 */
	public static <T> double[] toPrimitiveArray(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray(array3D);
	}

	/**
	 * Returns a {@code double} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a {@code double} array from the specified 3D {@code T} array
	 */
	public static <T> double[] asPrimitiveArray(final T[][]... array3D) {
		return toPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D {@code double} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a 2D {@code double} array from the specified 2D {@code T} array
	 */
	public static <T> double[][] toPrimitiveArray2D(final T[][] array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 2D {@code double} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a 2D {@code double} array from the specified 2D {@code T} array
	 */
	public static <T> double[][] asPrimitiveArray2D(final T[]... array2D) {
		return toPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 3D {@code double} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code double} array from the specified 3D {@code T} array
	 */
	public static <T> double[][][] toPrimitiveArray3D(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a 3D {@code double} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code double} array from the specified 3D {@code T} array
	 */
	public static <T> double[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a {@code double} array from the specified {@link Collection} of element type
	 * {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return a {@code double} array from the specified {@link Collection} of element type
	 *         {@code E}
	 */
	public static <E> double[] collectionToPrimitiveArray(final Collection<E> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Double} from the specified {@code double} array.
	 * <p>
	 * @param array a {@code double} array
	 * <p>
	 * @return an array of {@link Double} from the specified {@code double} array
	 */
	public static Double[] toArray(final double[] array) {
		final Double[] convertedArray = new Double[array.length];
		for (int i = 0; i < array.length; ++i) {
			convertedArray[i] = array[i];
		}
		return convertedArray;
	}

	/**
	 * Returns an array of {@link Double} from the specified {@code double} array.
	 * <p>
	 * @param array a {@code double} array
	 * <p>
	 * @return an array of {@link Double} from the specified {@code double} array
	 */
	public static Double[] asArray(final double... array) {
		return toArray(array);
	}

	/**
	 * Returns a 2D array of {@link Double} from the specified 2D {@code double} array.
	 * <p>
	 * @param array2D a 2D {@code double} array
	 * <p>
	 * @return a 2D array of {@link Double} from the specified 2D {@code double} array
	 */
	public static Double[][] toArray2D(final double[][] array2D) {
		final Double[][] convertedArray2D = new Double[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			convertedArray2D[i] = toArray(array2D[i]);
		}
		return convertedArray2D;
	}

	/**
	 * Returns a 2D array of {@link Double} from the specified 2D {@code double} array.
	 * <p>
	 * @param array2D a 2D {@code double} array
	 * <p>
	 * @return a 2D array of {@link Double} from the specified 2D {@code double} array
	 */
	public static Double[][] asArray2D(final double[]... array2D) {
		return toArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@link Double} from the specified 3D {@code double} array.
	 * <p>
	 * @param array3D a 3D {@code double} array
	 * <p>
	 * @return a 3D array of {@link Double} from the specified 3D {@code double} array
	 */
	public static Double[][][] toArray3D(final double[][][] array3D) {
		final Double[][][] convertedArray3D = new Double[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			convertedArray3D[i] = toArray2D(array3D[i]);
		}
		return convertedArray3D;
	}

	/**
	 * Returns a 3D array of {@link Double} from the specified 3D {@code double} array.
	 * <p>
	 * @param array3D a 3D {@code double} array
	 * <p>
	 * @return a 3D array of {@link Double} from the specified 3D {@code double} array
	 */
	public static Double[][][] asArray3D(final double[][]... array3D) {
		return toArray3D(array3D);
	}

	/**
	 * Returns an array of {@link Double} from the specified {@link Collection} of element type
	 * {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an array of {@link Double} from the specified {@link Collection} of element type
	 *         {@code E}
	 */
	public static <E> Double[] collectionToArray(final Collection<E> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Double} from the specified {@code double} array.
	 * <p>
	 * @param array a {@code double} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Double} from the specified {@code double} array
	 */
	public static ExtendedList<Double> toList(final double[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Double} from the specified {@code double} array.
	 * <p>
	 * @param array a {@code double} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Double} from the specified {@code double} array
	 */
	public static ExtendedList<Double> asList(final double... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Double} from the specified {@code double}
	 * array.
	 * <p>
	 * @param array a {@code double} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Double} from the specified {@code double}
	 *         array
	 */
	public static ExtendedLinkedList<Double> toLinkedList(final double[] array) {
		return PARSER.callToLinkedList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Double} from the specified {@code double}
	 * array.
	 * <p>
	 * @param array a {@code double} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Double} from the specified {@code double}
	 *         array
	 */
	public static ExtendedLinkedList<Double> asLinkedList(final double... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Double} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Double} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Double> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Double} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Double} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Double> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Double} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Double} from the specified {@code T} array
	 */
	public static <T> ExtendedLinkedList<Double> toLinkedList(final T[] array) {
		return PARSER.callToLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Double} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Double} from the specified {@code T} array
	 */
	public static <T> ExtendedLinkedList<Double> asLinkedList(final T... array) {
		return toLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Double} from the specified {@link Collection} of
	 * type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Double} from the specified {@link Collection} of
	 *         type {@code E}
	 */
	public static <E> ExtendedList<Double> collectionToList(final Collection<E> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Double} from the specified {@link Collection}
	 * of element type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Double} from the specified {@link Collection}
	 *         of element type {@code E}
	 */
	public static <E> ExtendedLinkedList<Double> collectionToLinkedList(
			final Collection<E> collection) {
		return PARSER.callCollectionToLinkedList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Double} from the specified {@code double} array.
	 * <p>
	 * @param array a {@code double} array
	 * <p>
	 * @return a {@link Set} of {@link Double} from the specified {@code double} array
	 */
	public static Set<Double> toSet(final double[] array) {
		return PARSER.callToSet(toArray(array));
	}

	/**
	 * Returns a {@link Set} of {@link Double} from the specified {@code double} array.
	 * <p>
	 * @param array a {@code double} array
	 * <p>
	 * @return a {@link Set} of {@link Double} from the specified {@code double} array
	 */
	public static Set<Double> asSet(final double... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Double} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Double} from the specified {@code T} array
	 */
	public static <T> Set<Double> toSet(final T[] array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Double} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Double} from the specified {@code T} array
	 */
	public static <T> Set<Double> asSet(final T... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Double} from the specified {@link Collection} of element type
	 * {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return a {@link Set} of {@link Double} from the specified {@link Collection} of element type
	 *         {@code E}
	 */
	public static <E> Set<Double> collectionToSet(final Collection<E> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code double} value between {@code 0.} and
	 * {@code 1.}.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code double} value between {@code 0.} and
	 *         {@code 1.}
	 */
	public static double random() {
		return RANDOM.nextDouble();
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code double} value between the specified
	 * bounds.
	 * <p>
	 * @param lowerBound the lower bound of the {@code double} value to create (inclusive)
	 * @param upperBound the upper bound of the {@code double} value to create (exclusive)
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code double} value between the specified
	 *         bounds
	 */
	public static double random(final double lowerBound, final double upperBound) {
		return lowerBound + RANDOM.nextDouble() * (upperBound - lowerBound);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code double} array of the specified length containing the sequence of numbers
	 * starting with zero and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return a {@code double} array of the specified length containing the sequence of numbers
	 *         starting with zero and spaced by one
	 */
	public static double[] createSequence(final int length) {
		return createSequence(length, 0., 1.);
	}

	/**
	 * Creates a {@code double} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return a {@code double} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by one
	 */
	public static double[] createSequence(final int length, final double from) {
		return createSequence(length, from, 1.);
	}

	/**
	 * Creates a {@code double} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return a {@code double} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by {@code step}
	 */
	public static double[] createSequence(final int length, final double from, final double step) {
		final double[] array = new double[length];
		double value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a random {@code double} array of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return a random {@code double} array of the specified length
	 */
	public static double[] createRandomSequence(final int length) {
		final double[] array = new double[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates a {@code double} array of the specified length containing pseudorandom, uniformly
	 * distributed {@code double} values between the specified bounds.
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create (inclusive)
	 * @param upperBound the upper bound of the random sequence to create (exclusive)
	 * <p>
	 * @return a {@code double} array of the specified length containing pseudorandom, uniformly
	 *         distributed {@code double} values between the specified bounds
	 */
	public static double[] createRandomSequence(final int length, final double lowerBound,
			final double upperBound) {
		final double[] array = new double[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(lowerBound, upperBound);
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the formatted representative {@link String} of the specified {@code double} value.
	 * <p>
	 * @param value a {@code double} value
	 * <p>
	 * @return the formatted representative {@link String} of the specified {@code double} value
	 */
	public static String format(final double value) {
		return DECIMAL_FORMAT.format(value);
	}

	/**
	 * Returns the percentage representative {@link String} of the specified {@code double} value.
	 * <p>
	 * @param value a {@code double} value
	 * <p>
	 * @return the percentage representative {@link String} of the specified {@code double} value
	 */
	public static String toPercentage(final double value) {
		return format(value * 100.) + "%";
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void fill(final double[] array, final double value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
	}

	public static void fill(final double[][] array2D, final double value) {
		for (final double[] array : array2D) {
			fill(array, value);
		}
	}

	public static void fill(final double[][][] array3D, final double value) {
		for (final double[][] array2D : array3D) {
			fill(array2D, value);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the filtered {@code double} array from the specified {@code double} array and
	 * indexes.
	 * <p>
	 * @param array   a {@code double} array
	 * @param indexes the indexes to filter
	 * <p>
	 * @return the filtered {@code double} array from the specified {@code double} array and indexes
	 */
	public static double[] filter(final double[] array, final int... indexes) {
		final double[] filteredArray = new double[indexes.length];
		for (int i = 0; i < indexes.length; ++i) {
			filteredArray[i] = array[indexes[i]];
		}
		return filteredArray;
	}

	/**
	 * Returns all the filtered {@code double} arrays from the specified {@code double} array and
	 * indexes.
	 * <p>
	 * @param array   a {@code double} array
	 * @param indexes the array of indexes to filter
	 * <p>
	 * @return all the filtered {@code double} arrays from the specified {@code double} array and
	 *         indexes
	 */
	public static double[][] filterAll(final double[] array, final int[]... indexes) {
		final double[][] filteredArrays = new double[indexes.length][];
		for (int i = 0; i < indexes.length; ++i) {
			filteredArrays[i] = filter(array, indexes[i]);
		}
		return filteredArrays;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int indexOf(final double[] array, final double token) {
		if (array != null) {
			for (int i = 0; i < array.length; ++i) {
				if (equals(array[i], token)) {
					return i;
				}
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code double} array containing the specified {@code double} value and all the
	 * elements of the specified {@code double} array.
	 * <p>
	 * @param a a {@code double} value (may be {@code null})
	 * @param b a {@code double} array (may be {@code null})
	 * <p>
	 * @return a {@code double} array containing the specified {@code double} value and all the
	 *         elements of the specified {@code double} array
	 */
	public static double[] merge(final double a, final double... b) {
		return merge(asPrimitiveArray(a), b);
	}

	/**
	 * Returns a {@code double} array containing all the elements of the specified {@code double}
	 * arrays.
	 * <p>
	 * @param a a {@code double} array (may be {@code null})
	 * @param b a {@code double} array (may be {@code null})
	 * <p>
	 * @return a {@code double} array containing all the elements of the specified {@code double}
	 *         arrays
	 */
	public static double[] merge(final double[] a, final double... b) {
		if (a == null) {
			return clone(b);
		} else if (b == null) {
			return clone(a);
		}
		final double[] mergedArray = new double[a.length + b.length];
		System.arraycopy(a, 0, mergedArray, 0, a.length);
		System.arraycopy(b, 0, mergedArray, a.length, b.length);
		return mergedArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code double} value.
	 * <p>
	 * @param value a {@code double} value
	 * <p>
	 * @return the middle of the specified {@code double} value
	 */
	public static double middle(final double value) {
		return middle(0., value);
	}

	/**
	 * Returns the middle of the specified lower and upper bounds rounded to the lower
	 * {@code double} value.
	 * <p>
	 * @param lowerBound a {@code double} value
	 * @param upperBound another {@code double} value
	 * <p>
	 * @return the middle of the specified lower and upper bounds rounded to the lower
	 *         {@code double} value
	 */
	public static double middle(final double lowerBound, final double upperBound) {
		return lowerBound + (upperBound - lowerBound) / 2.;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void swap(final double[] array, final int i, final int j) {
		final double element = array[i];
		array[i] = array[j];
		array[j] = element;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double[] take(final double... array) {
		return take(array, 0, array.length);
	}

	public static double[] take(final double[] array, final int fromIndex, final int length) {
		final int maxLength = Math.min(length, array.length - fromIndex);
		final double[] subarray = new double[maxLength];
		System.arraycopy(array, fromIndex, subarray, 0, maxLength);
		return subarray;
	}

	public static double[] take(final double[]... array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static double[] take(final double[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static double[] take(final double[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length - fromRow);
		final double[] subarray = new double[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, subarray, i * columnCount,
					columnCount);
		}
		return subarray;
	}

	public static double[] take(final double[][]... array3D) {
		return take(array3D, 0, array3D.length, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static double[] take(final double[][][] array3D, final int fromRow, final int rowCount) {
		return take(array3D, fromRow, rowCount, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static double[] take(final double[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0, array3D[0][0].length);
	}

	public static double[] take(final double[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length - fromRow);
		final int length = columnCount * depthCount;
		final double[] subarray = new double[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					subarray, i * length, length);
		}
		return subarray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified {@code double} array.
	 * <p>
	 * @param rowCount the number of rows of the array
	 * @param array    a {@code double} array
	 * <p>
	 * @return the transpose of the specified {@code double} array
	 */
	public static double[] transpose(final int rowCount, final double[] array) {
		final int n = rowCount;
		final int m = array.length / rowCount;
		final double[] transpose = new double[m * n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i * n + j] = array[j * m + i];
			}
		}
		return transpose;
	}

	/**
	 * Returns the transpose of the specified 2D {@code double} array.
	 * <p>
	 * @param array2D a 2D {@code double} array
	 * <p>
	 * @return the transpose of the specified 2D {@code double} array
	 */
	public static double[][] transpose(final double[]... array2D) {
		final int n = array2D.length;
		final int m = array2D[0].length;
		final double[][] transpose = new double[m][n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i][j] = array2D[j][i];
			}
		}
		return transpose;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code double} value or a
	 * {@link Double}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code double} value
	 *         or a {@link Double}, {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return double.class.isAssignableFrom(c) || Double.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code double} value.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code double} value,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitive(final Class<?> c) {
		return double.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code double} array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code double} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArray(final Class<?> c) {
		return double[].class.isAssignableFrom(c);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code array} contains {@code token}.
	 * <p>
	 * @param array a {@code double} array
	 * @param token the {@code double} value to test for presence
	 * <p>
	 * @return {@code true} if {@code array} contains {@code token}, {@code false} otherwise
	 */
	public static boolean contains(final double[] array, final double token) {
		return indexOf(array, token) >= 0;
	}

	/**
	 * Tests whether {@code array} contains any {@code tokens}.
	 * <p>
	 * @param array  a {@code double} array
	 * @param tokens the {@code double} array to test for presence
	 * <p>
	 * @return {@code true} if {@code array} contains any {@code tokens}, {@code false} otherwise
	 */
	public static boolean containsAny(final double[] array, final double[] tokens) {
		if (array == null) {
			return false;
		}
		for (final double token : tokens) {
			if (contains(array, token)) {
				return true;
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified {@code double} values for order. Returns a negative integer, zero or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code double} value to compare for order
	 * @param b the other {@code double} value to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 */
	public static int compare(final double a, final double b) {
		if (a < b) {
			return -1; // neither value is NaN
		}
		if (a > b) {
			return 1; // neither value is NaN
		}
		// @note cannot use doubleToRawLongBits because of NaNs
		final long aBits = Double.doubleToLongBits(a);
		final long bBits = Double.doubleToLongBits(b);
		return aBits == bBits ? 0 : // the values are equal
				aBits < bBits ? -1 : // (-0., 0.) or (!NaN, NaN)
						1; // (0., -0.) or (NaN, !NaN)
	}

	//////////////////////////////////////////////

	/**
	 * Compares the specified {@code double} arrays for order. Returns a negative integer, zero or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code double} array to compare for order
	 * @param b the other {@code double} array to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static int compare(final double[] a, final double[] b) {
		if (a == b) {
			return 0;
		}
		final int limit = Math.min(a.length, b.length);
		for (int i = 0; i < limit; ++i) {
			final int comparison = compare(a[i], b[i]);
			if (comparison != 0) {
				return comparison;
			}
		}
		return Integers.compare(a.length, b.length);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a clone of the specified {@code double} array, or {@code null} if {@code array} is
	 * {@code null}.
	 * <p>
	 * @param array a {@code double} array
	 * <p>
	 * @return a clone of the specified {@code double} array, or {@code null} if {@code array} is
	 *         {@code null}
	 */
	public static double[] clone(final double... array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the {@code double} value to compare for equality
	 * @param b the other {@code double} value to compare against for equality
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final double a, final double b) {
		return equals(a, b, Maths.TOLERANCE);
	}

	/**
	 * Tests whether {@code a} is equal to {@code b} within {@code tolerance}.
	 * <p>
	 * @param a         the {@code double} value to compare for equality
	 * @param b         the other {@code double} value to compare against for equality
	 * @param tolerance the tolerance level
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b} within {@code tolerance},
	 *         {@code false} otherwise
	 */
	public static boolean equals(final double a, final double b, final double tolerance) {
		return Maths.delta(a, b) <= tolerance;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the hash code value for the specified {@code double} array.
	 * <p>
	 * @param array the {@code double} array to hash
	 * <p>
	 * @return the hash code value for the specified {@code double} array
	 */
	public static int hashCode(final double... array) {
		return hashCodeWith(0, array);
	}

	/**
	 * Returns the hash code value for the specified {@code double} array at the specified depth.
	 * <p>
	 * @param array the {@code double} array to hash
	 * @param depth the depth to hash at
	 * <p>
	 * @return the hash code value for the specified {@code double} array at the specified depth
	 */
	@SuppressWarnings("unchecked")
	public static int hashCodeWith(final int depth, final double... array) {
		if (array == null) {
			return 0;
		}
		switch (array.length) {
			case 0:
				return Bits.SEEDS[depth % Bits.SEEDS.length];
			case 1:
				final long bits = Double.doubleToLongBits(array[0]);
				return (int) (bits ^ (bits >>> 32));
			default:
				int hashCode = Bits.SEEDS[depth % Bits.SEEDS.length];
				for (int i = 0; i < array.length; ++i) {
					if (i % 2 == 0) {
						hashCode = Bits.rotateLeft(hashCode) ^ hashCodeWith(depth, array[i]);
					} else {
						hashCode = Bits.rotateRight(hashCode) ^ hashCodeWith(depth, array[i]);
					}
				}
				return hashCode;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@code double} array.
	 * <p>
	 * @param array a {@code double} array
	 * <p>
	 * @return a representative {@link String} of the specified {@code double} array
	 */
	public static String toString(final double... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a representative {@link String} of the specified {@code double} array joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     a {@code double} array
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@code double} array joined by
	 *         {@code delimiter}
	 */
	public static String toString(final double[] array, final String delimiter) {
		return Arrays.toString(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code double} array joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     a {@code double} array
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code double} array joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final double[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(toArray(array), delimiter, wrapper);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified array of {@link Double}.
	 * <p>
	 * @param array an array of {@link Double}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Double}
	 */
	public static String toString(final Double... array) {
		return Arrays.toString(array);
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Double} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an array of {@link Double}
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Double} joined by
	 *         {@code delimiter}
	 */
	public static String toString(final Double[] array, final String delimiter) {
		return Arrays.toString(array, delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Double} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@link Double}
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Double} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final Double[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(array, delimiter, wrapper);
	}
}
