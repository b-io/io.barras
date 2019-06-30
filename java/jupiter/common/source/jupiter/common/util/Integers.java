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

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.IntegerParser;
import jupiter.common.struct.list.ExtendedList;

public class Integers {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final int[] EMPTY_PRIMITIVE_ARRAY = new int[] {};
	public static final Integer[] EMPTY_ARRAY = new Integer[] {};

	protected static final IntegerParser PARSER = new IntegerParser();

	public static volatile Random RANDOM = new Random();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Integers}.
	 */
	protected Integers() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@code int} value converted from the specified {@code long} value.
	 * <p>
	 * @param value the {@code long} value to convert
	 * <p>
	 * @return an {@code int} value converted from the specified {@code long} value
	 */
	public static int convert(final long value) {
		if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
			throw new ArithmeticException("Integer under/overflow");
		}
		return (int) value;
	}

	/**
	 * Returns an {@code int} value converted from the specified {@code float} value.
	 * <p>
	 * @param value the {@code float} value to convert
	 * <p>
	 * @return an {@code int} value converted from the specified {@code float} value
	 */
	public static int convert(final float value) {
		if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
			throw new ArithmeticException("Integer under/overflow");
		}
		return (int) value;
	}

	/**
	 * Returns an {@code int} value converted from the specified {@code double} value.
	 * <p>
	 * @param value the {@code double} value to convert
	 * <p>
	 * @return an {@code int} value converted from the specified {@code double} value
	 */
	public static int convert(final double value) {
		if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
			throw new ArithmeticException("Integer under/overflow");
		}
		return (int) value;
	}

	/**
	 * Returns an {@link Integer} converted from the specified {@link Object}.
	 * <p>
	 * @param object the {@link Object} to convert
	 * <p>
	 * @return an {@link Integer} converted from the specified {@link Object}
	 */
	public static Integer convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@code int} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object a {@code T} object
	 * <p>
	 * @return an {@code int} value from the specified {@code T} object
	 */
	public static <T> int toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@code int} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@code int} array from the specified {@code T} array
	 */
	public static <T> int[] toPrimitiveArray(final T[] array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns an {@code int} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@code int} array from the specified {@code T} array
	 */
	public static <T> int[] asPrimitiveArray(final T... array) {
		return toPrimitiveArray(array);
	}

	/**
	 * Returns an {@code int} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return an {@code int} array from the specified 2D {@code T} array
	 */
	public static <T> int[] toPrimitiveArray(final T[][] array2D) {
		return PARSER.callToPrimitiveArray(array2D);
	}

	/**
	 * Returns an {@code int} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return an {@code int} array from the specified 2D {@code T} array
	 */
	public static <T> int[] asPrimitiveArray(final T[]... array2D) {
		return toPrimitiveArray(array2D);
	}

	/**
	 * Returns an {@code int} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return an {@code int} array from the specified 3D {@code T} array
	 */
	public static <T> int[] toPrimitiveArray(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray(array3D);
	}

	/**
	 * Returns an {@code int} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return an {@code int} array from the specified 3D {@code T} array
	 */
	public static <T> int[] asPrimitiveArray(final T[][]... array3D) {
		return toPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D {@code int} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a 2D {@code int} array from the specified 2D {@code T} array
	 */
	public static <T> int[][] toPrimitiveArray2D(final T[][] array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 2D {@code int} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a 2D {@code int} array from the specified 2D {@code T} array
	 */
	public static <T> int[][] asPrimitiveArray2D(final T[]... array2D) {
		return toPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 3D {@code int} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code int} array from the specified 3D {@code T} array
	 */
	public static <T> int[][][] toPrimitiveArray3D(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a 3D {@code int} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code int} array from the specified 3D {@code T} array
	 */
	public static <T> int[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	/**
	 * Returns an {@code int} array from the specified {@link Collection} of type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an {@code int} array from the specified {@link Collection} of type {@code T}
	 */
	public static <T> int[] collectionToPrimitiveArray(final Collection<T> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Integer} from the specified {@code int} array.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return an array of {@link Integer} from the specified {@code int} array
	 */
	public static Integer[] toArray(final int[] array) {
		final Integer[] result = new Integer[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = array[i];
		}
		return result;
	}

	/**
	 * Returns an array of {@link Integer} from the specified {@code int} array.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return an array of {@link Integer} from the specified {@code int} array
	 */
	public static Integer[] asArray(final int... array) {
		return toArray(array);
	}

	/**
	 * Returns a 2D array of {@link Integer} from the specified 2D {@code int} array.
	 * <p>
	 * @param array2D a 2D {@code int} array
	 * <p>
	 * @return a 2D array of {@link Integer} from the specified 2D {@code int} array
	 */
	public static Integer[][] toArray2D(final int[][] array2D) {
		final Integer[][] result = new Integer[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = toArray(array2D[i]);
		}
		return result;
	}

	/**
	 * Returns a 2D array of {@link Integer} from the specified 2D {@code int} array.
	 * <p>
	 * @param array2D a 2D {@code int} array
	 * <p>
	 * @return a 2D array of {@link Integer} from the specified 2D {@code int} array
	 */
	public static Integer[][] asArray2D(final int[]... array2D) {
		return toArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@link Integer} from the specified 3D {@code int} array.
	 * <p>
	 * @param array3D a 3D {@code int} array
	 * <p>
	 * @return a 3D array of {@link Integer} from the specified 3D {@code int} array
	 */
	public static Integer[][][] toArray3D(final int[][][] array3D) {
		final Integer[][][] result = new Integer[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = toArray2D(array3D[i]);
		}
		return result;
	}

	/**
	 * Returns a 3D array of {@link Integer} from the specified 3D {@code int} array.
	 * <p>
	 * @param array3D a 3D {@code int} array
	 * <p>
	 * @return a 3D array of {@link Integer} from the specified 3D {@code int} array
	 */
	public static Integer[][][] asArray3D(final int[][]... array3D) {
		return toArray3D(array3D);
	}

	/**
	 * Returns an array of {@link Integer} from the specified {@link Collection} of type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@link Integer} from the specified {@link Collection} of type {@code T}
	 */
	public static <T> Integer[] collectionToArray(final Collection<T> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link List} of {@link Integer} from the specified {@code int} array.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return a {@link List} of {@link Integer} from the specified {@code int} array
	 */
	public static List<Integer> toList(final int[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns a {@link List} of {@link Integer} from the specified {@code int} array.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return a {@link List} of {@link Integer} from the specified {@code int} array
	 */
	public static List<Integer> asList(final int... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Integer} from the specified array of {@code int}
	 * values.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Integer} from the specified array of {@code int}
	 *         values
	 */
	public static ExtendedList<Integer> toExtendedList(final int[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Integer} from the specified array of {@code int}
	 * values.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Integer} from the specified array of {@code int}
	 *         values
	 */
	public static ExtendedList<Integer> asExtendedList(final int... array) {
		return toExtendedList(array);
	}

	/**
	 * Returns a {@link List} of {@link Integer} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link List} of {@link Integer} from the specified {@code T} array
	 */
	public static <T> List<Integer> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Integer} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link List} of {@link Integer} from the specified {@code T} array
	 */
	public static <T> List<Integer> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Integer} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Integer} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Integer> toExtendedList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Integer} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Integer} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Integer> asExtendedList(final T... array) {
		return toExtendedList(array);
	}

	/**
	 * Returns a {@link List} of {@link Integer} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Integer} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> List<Integer> collectionToList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Integer} from the specified {@link Collection} of
	 * type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Integer} from the specified {@link Collection} of
	 *         type {@code T}
	 */
	public static <T> ExtendedList<Integer> collectionToExtendedList(
			final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Integer} from the specified {@code int} array.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return a {@link Set} of {@link Integer} from the specified {@code int} array
	 */
	public static Set<Integer> toSet(final int[] array) {
		return PARSER.callToSet(toArray(array));
	}

	/**
	 * Returns a {@link Set} of {@link Integer} from the specified {@code int} array.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return a {@link Set} of {@link Integer} from the specified {@code int} array
	 */
	public static Set<Integer> asSet(final int... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Integer} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Integer} from the specified {@code T} array
	 */
	public static <T> Set<Integer> toSet(final T[] array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Integer} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Integer} from the specified {@code T} array
	 */
	public static <T> Set<Integer> asSet(final T... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Integer} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Integer} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> Set<Integer> collectionToSet(final Collection<T> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code int} value.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code int} value
	 */
	public static int random() {
		return RANDOM.nextInt();
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code int} value between {@code lowerBound}
	 * (inclusive) and {@code upperBound} (exclusive).
	 * <p>
	 * @param lowerBound the lower bound of the {@code int} value to create
	 * @param upperBound the upper bound of the {@code int} value to create
	 * <p>
	 * @return the next pseudorandom, uniformly distributed {@code int} value between
	 *         {@code lowerBound} (inclusive) and {@code upperBound} (exclusive)
	 */
	public static int random(final int lowerBound, final int upperBound) {
		return lowerBound + RANDOM.nextInt(upperBound - lowerBound);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an {@code int} array of the specified length containing the sequence of numbers
	 * starting with zero and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return an {@code int} array of the specified length containing the sequence of numbers
	 *         starting with zero and spaced by one
	 */
	public static int[] createSequence(final int length) {
		return createSequence(length, 0, 1);
	}

	/**
	 * Creates an {@code int} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return an {@code int} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by one
	 */
	public static int[] createSequence(final int length, final int from) {
		return createSequence(length, from, 1);
	}

	/**
	 * Creates an {@code int} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return an {@code int} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by {@code step}
	 */
	public static int[] createSequence(final int length, final int from, final int step) {
		final int[] array = new int[length];
		int value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of random {@code int} values of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return an array of random {@code int} values of the specified length
	 */
	public static int[] createRandomSequence(final int length) {
		final int[] array = new int[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates an {@code int} array of the specified length containing pseudorandom, uniformly
	 * distributed {@code int} values between {@code lowerBound} (inclusive) and {@code upperBound}
	 * (exclusive).
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create
	 * @param upperBound the upper bound of the random sequence to create
	 * <p>
	 * @return an {@code int} array of the specified length containing pseudorandom, uniformly
	 *         distributed {@code int} values between {@code lowerBound} (inclusive) and
	 *         {@code upperBound} (exclusive)
	 */
	public static int[] createRandomSequence(final int length, final int lowerBound,
			final int upperBound) {
		final int[] array = new int[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(lowerBound, upperBound);
		}
		return array;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void fill(final int[] array, final int value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
	}

	public static void fill(final int[][] array2D, final int value) {
		for (final int[] array : array2D) {
			fill(array, value);
		}
	}

	public static void fill(final int[][][] array3D, final int value) {
		for (final int[][] array2D : array3D) {
			fill(array2D, value);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@code int} array containing the specified {@code int} value and all the elements
	 * of the specified {@code int} array.
	 * <p>
	 * @param a an {@code int} value (may be {@code null})
	 * @param b an {@code int} array (may be {@code null})
	 * <p>
	 * @return an {@code int} array containing the specified {@code int} value and all the elements
	 *         of the specified {@code int} array
	 */
	public static int[] merge(final int a, final int... b) {
		return merge(asPrimitiveArray(a), b);
	}

	/**
	 * Returns an {@code int} array containing all the elements of the specified arrays of
	 * {@code int} values.
	 * <p>
	 * @param a an {@code int} array (may be {@code null})
	 * @param b an {@code int} array (may be {@code null})
	 * <p>
	 * @return an {@code int} array containing all the elements of the specified arrays of
	 *         {@code int} values
	 */
	public static int[] merge(final int[] a, final int... b) {
		if (a == null) {
			return clone(b);
		} else if (b == null) {
			return clone(a);
		}
		final int[] result = new int[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code int} value.
	 * <p>
	 * @param value an {@code int} value
	 * <p>
	 * @return the middle of the specified {@code int} value
	 */
	public static int middle(final int value) {
		return middle(0, value);
	}

	/**
	 * Returns the middle of the specified lower and upper bounds rounded to the lower {@code int}
	 * value.
	 * <p>
	 * @param lowerBound an {@code int} value
	 * @param upperBound another {@code int} value
	 * <p>
	 * @return the middle of the specified lower and upper bounds rounded to the lower {@code int}
	 *         value
	 */
	public static int middle(final int lowerBound, final int upperBound) {
		return lowerBound + Integers.convert((upperBound - lowerBound) / 2.);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int[] take(final int... array) {
		return take(array, 0, array.length);
	}

	public static int[] take(final int[] array, final int from, final int length) {
		final int maxLength = Math.min(length, array.length - from);
		final int[] result = new int[maxLength];
		System.arraycopy(array, from, result, 0, maxLength);
		return result;
	}

	public static int[] take(final int[]... array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static int[] take(final int[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static int[] take(final int[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length - fromRow);
		final int[] result = new int[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, result, i * columnCount,
					columnCount);
		}
		return result;
	}

	public static int[] take(final int[][]... array3D) {
		return take(array3D, 0, array3D.length, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static int[] take(final int[][][] array3D, final int fromRow, final int rowCount) {
		return take(array3D, fromRow, rowCount, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static int[] take(final int[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0, array3D[0][0].length);
	}

	public static int[] take(final int[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length - fromRow);
		final int length = columnCount * depthCount;
		final int[] result = new int[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					result, i * length, length);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified {@code int} array.
	 * <p>
	 * @param rowCount the number of rows of the array
	 * @param array    an {@code int} array
	 * <p>
	 * @return the transpose of the specified {@code int} array
	 */
	public static int[] transpose(final int rowCount, final int[] array) {
		final int n = rowCount;
		final int m = array.length / rowCount;
		final int[] transpose = new int[m * n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i * n + j] = array[j * m + i];
			}
		}
		return transpose;
	}

	/**
	 * Returns the transpose of the specified 2D {@code int} array.
	 * <p>
	 * @param array2D a 2D {@code int} array
	 * <p>
	 * @return the transpose of the specified 2D {@code int} array
	 */
	public static int[][] transpose(final int[]... array2D) {
		final int n = array2D.length;
		final int m = array2D[0].length;
		final int[][] transpose = new int[m][n];
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
	 * Tests whether the specified {@link Class} is assignable to an {@code int} value or an
	 * {@link Integer}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to an {@code int} value or
	 *         an {@link Integer}, {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return int.class.isAssignableFrom(c) || Integer.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to an {@code int} value.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to an {@code int} value,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitive(final Class<?> c) {
		return int.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to an {@code int} array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to an array of {@code int}
	 *         values, {@code false} otherwise
	 */
	public static boolean isPrimitiveArray(final Class<?> c) {
		return int[].class.isAssignableFrom(c);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code array} contains {@code token}.
	 * <p>
	 * @param array an {@code int} array
	 * @param token the {@code int} value to test for presence
	 * <p>
	 * @return {@code true} if {@code array} contains {@code token}, {@code false} otherwise
	 */
	public static boolean contains(final int[] array, final int token) {
		if (array == null) {
			return false;
		}
		for (final int element : array) {
			if (element == token) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether {@code array} contains any {@code tokens}.
	 * <p>
	 * @param array  an {@code int} array
	 * @param tokens the {@code int} array to test for presence
	 * <p>
	 * @return {@code true} if {@code array} contains any {@code tokens}, {@code false} otherwise
	 */
	public static boolean containsAny(final int[] array, final int[] tokens) {
		if (array == null) {
			return false;
		}
		for (final int token : tokens) {
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
	 * Compares the specified {@code int} values for order. Returns a negative integer, zero or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code int} value to compare for order
	 * @param b the other {@code int} value to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 */
	public static int compare(final int a, final int b) {
		return a < b ? -1 : a == b ? 0 : 1;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a clone of the specified {@code int} array, or {@code null} if {@code array} is
	 * {@code null}.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return a clone of the specified {@code int} array, or {@code null} if {@code array} is
	 *         {@code null}
	 */
	public static int[] clone(final int... array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@code int} array.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return a representative {@link String} of the specified {@code int} array
	 */
	public static String toString(final int... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a representative {@link String} of the specified {@code int} array joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an {@code int} array
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@code int} array joined by
	 *         {@code delimiter}
	 */
	public static String toString(final int[] array, final String delimiter) {
		return Arrays.toString(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code int} array joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an {@code int} array
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code int} array joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final int[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(toArray(array), delimiter, wrapper);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified array of {@link Integer}.
	 * <p>
	 * @param array an array of {@link Integer}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Integer}
	 */
	public static String toString(final Integer... array) {
		return Arrays.toString(array);
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Integer} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an array of {@link Integer}
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Integer} joined by
	 *         {@code delimiter}
	 */
	public static String toString(final Integer[] array, final String delimiter) {
		return Arrays.toString(array, delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Integer} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@link Integer}
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Integer} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final Integer[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(array, delimiter, wrapper);
	}
}
