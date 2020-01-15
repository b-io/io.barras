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
import java.util.Comparator;
import java.util.Random;
import java.util.Set;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.LongParser;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;

public class Longs {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final long[] EMPTY_PRIMITIVE_ARRAY = new long[] {};
	public static final Long[] EMPTY_ARRAY = new Long[] {};

	protected static final LongParser PARSER = new LongParser();

	public static volatile Random RANDOM = new Random();

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Comparator<Long> COMPARATOR = new Comparator<Long>() {
		@Override
		public int compare(final Long a, final Long b) {
			return Longs.compare(a, b);
		}
	};

	public static final Comparator<long[]> ARRAY_COMPARATOR = new Comparator<long[]>() {
		@Override
		public int compare(final long[] a, final long[] b) {
			return Longs.compare(a, b);
		}
	};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Longs}.
	 */
	protected Longs() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code long} value converted from the specified {@code float} value.
	 * <p>
	 * @param value the {@code float} value to convert
	 * <p>
	 * @return a {@code long} value converted from the specified {@code float} value
	 */
	public static long convert(final float value) {
		if (value < Long.MIN_VALUE || value > Long.MAX_VALUE) {
			throw new ArithmeticException("Long under/overflow");
		}
		return (long) value;
	}

	/**
	 * Returns a {@code long} value converted from the specified {@code double} value.
	 * <p>
	 * @param value the {@code double} value to convert
	 * <p>
	 * @return a {@code long} value converted from the specified {@code double} value
	 */
	public static long convert(final double value) {
		if (value < Long.MIN_VALUE || value > Long.MAX_VALUE) {
			throw new ArithmeticException("Long under/overflow");
		}
		return (long) value;
	}

	/**
	 * Returns a {@link Long} converted from the specified {@link Object}.
	 * <p>
	 * @param object the {@link Object} to convert
	 * <p>
	 * @return a {@link Long} converted from the specified {@link Object}
	 */
	public static Long convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code long} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object a {@code T} object
	 * <p>
	 * @return a {@code long} value from the specified {@code T} object
	 */
	public static <T> long toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code long} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@code long} array from the specified {@code T} array
	 */
	public static <T> long[] toPrimitiveArray(final T[] array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a {@code long} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@code long} array from the specified {@code T} array
	 */
	public static <T> long[] asPrimitiveArray(final T... array) {
		return toPrimitiveArray(array);
	}

	/**
	 * Returns a {@code long} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a {@code long} array from the specified 2D {@code T} array
	 */
	public static <T> long[] toPrimitiveArray(final T[][] array2D) {
		return PARSER.callToPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code long} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a {@code long} array from the specified 2D {@code T} array
	 */
	public static <T> long[] asPrimitiveArray(final T[]... array2D) {
		return toPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code long} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a {@code long} array from the specified 3D {@code T} array
	 */
	public static <T> long[] toPrimitiveArray(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray(array3D);
	}

	/**
	 * Returns a {@code long} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a {@code long} array from the specified 3D {@code T} array
	 */
	public static <T> long[] asPrimitiveArray(final T[][]... array3D) {
		return toPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D {@code long} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a 2D {@code long} array from the specified 2D {@code T} array
	 */
	public static <T> long[][] toPrimitiveArray2D(final T[][] array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 2D {@code long} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a 2D {@code long} array from the specified 2D {@code T} array
	 */
	public static <T> long[][] asPrimitiveArray2D(final T[]... array2D) {
		return toPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 3D {@code long} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code long} array from the specified 3D {@code T} array
	 */
	public static <T> long[][][] toPrimitiveArray3D(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a 3D {@code long} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code long} array from the specified 3D {@code T} array
	 */
	public static <T> long[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a {@code long} array from the specified {@link Collection} of element type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return a {@code long} array from the specified {@link Collection} of element type {@code E}
	 */
	public static <E> long[] collectionToPrimitiveArray(final Collection<E> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Long} from the specified {@code long} array.
	 * <p>
	 * @param array a {@code long} array
	 * <p>
	 * @return an array of {@link Long} from the specified {@code long} array
	 */
	public static Long[] toArray(final long[] array) {
		final Long[] convertedArray = new Long[array.length];
		for (int i = 0; i < array.length; ++i) {
			convertedArray[i] = array[i];
		}
		return convertedArray;
	}

	/**
	 * Returns an array of {@link Long} from the specified {@code long} array.
	 * <p>
	 * @param array a {@code long} array
	 * <p>
	 * @return an array of {@link Long} from the specified {@code long} array
	 */
	public static Long[] asArray(final long... array) {
		return toArray(array);
	}

	/**
	 * Returns a 2D array of {@link Long} from the specified 2D {@code long} array.
	 * <p>
	 * @param array2D a 2D {@code long} array
	 * <p>
	 * @return a 2D array of {@link Long} from the specified 2D {@code long} array
	 */
	public static Long[][] toArray2D(final long[][] array2D) {
		final Long[][] convertedArray2D = new Long[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			convertedArray2D[i] = toArray(array2D[i]);
		}
		return convertedArray2D;
	}

	/**
	 * Returns a 2D array of {@link Long} from the specified 2D {@code long} array.
	 * <p>
	 * @param array2D a 2D {@code long} array
	 * <p>
	 * @return a 2D array of {@link Long} from the specified 2D {@code long} array
	 */
	public static Long[][] asArray2D(final long[]... array2D) {
		return toArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@link Long} from the specified 3D {@code long} array.
	 * <p>
	 * @param array3D a 3D {@code long} array
	 * <p>
	 * @return a 3D array of {@link Long} from the specified 3D {@code long} array
	 */
	public static Long[][][] toArray3D(final long[][][] array3D) {
		final Long[][][] convertedArray3D = new Long[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			convertedArray3D[i] = toArray2D(array3D[i]);
		}
		return convertedArray3D;
	}

	/**
	 * Returns a 3D array of {@link Long} from the specified 3D {@code long} array.
	 * <p>
	 * @param array3D a 3D {@code long} array
	 * <p>
	 * @return a 3D array of {@link Long} from the specified 3D {@code long} array
	 */
	public static Long[][][] asArray3D(final long[][]... array3D) {
		return toArray3D(array3D);
	}

	/**
	 * Returns an array of {@link Long} from the specified {@link Collection} of element type
	 * {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an array of {@link Long} from the specified {@link Collection} of element type
	 *         {@code E}
	 */
	public static <E> Long[] collectionToArray(final Collection<E> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Long} from the specified {@code long} array.
	 * <p>
	 * @param array a {@code long} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Long} from the specified {@code long} array
	 */
	public static ExtendedList<Long> toList(final long[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Long} from the specified {@code long} array.
	 * <p>
	 * @param array a {@code long} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Long} from the specified {@code long} array
	 */
	public static ExtendedList<Long> asList(final long... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Long} from the specified {@code long} array.
	 * <p>
	 * @param array a {@code long} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Long} from the specified {@code long} array
	 */
	public static ExtendedLinkedList<Long> toLinkedList(final long[] array) {
		return PARSER.callToLinkedList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Long} from the specified {@code long} array.
	 * <p>
	 * @param array a {@code long} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Long} from the specified {@code long} array
	 */
	public static ExtendedLinkedList<Long> asLinkedList(final long... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Long} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Long} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Long> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Long} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Long} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Long> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Long} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Long} from the specified {@code T} array
	 */
	public static <T> ExtendedLinkedList<Long> toLinkedList(final T[] array) {
		return PARSER.callToLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Long} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Long} from the specified {@code T} array
	 */
	public static <T> ExtendedLinkedList<Long> asLinkedList(final T... array) {
		return toLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Long} from the specified {@link Collection} of
	 * element type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Long} from the specified {@link Collection} of
	 *         element type {@code E}
	 */
	public static <E> ExtendedList<Long> collectionToList(final Collection<E> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Long} from the specified {@link Collection}
	 * of element type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Long} from the specified {@link Collection}
	 *         of element type {@code E}
	 */
	public static <E> ExtendedLinkedList<Long> collectionToLinkedList(
			final Collection<E> collection) {
		return PARSER.callCollectionToLinkedList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Long} from the specified {@code long} array.
	 * <p>
	 * @param array a {@code long} array
	 * <p>
	 * @return a {@link Set} of {@link Long} from the specified {@code long} array
	 */
	public static Set<Long> toSet(final long[] array) {
		return PARSER.callToSet(toArray(array));
	}

	/**
	 * Returns a {@link Set} of {@link Long} from the specified {@code long} array.
	 * <p>
	 * @param array a {@code long} array
	 * <p>
	 * @return a {@link Set} of {@link Long} from the specified {@code long} array
	 */
	public static Set<Long> asSet(final long... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Long} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Long} from the specified {@code T} array
	 */
	public static <T> Set<Long> toSet(final T[] array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Long} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Long} from the specified {@code T} array
	 */
	public static <T> Set<Long> asSet(final T... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Long} from the specified {@link Collection} of element type
	 * {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return a {@link Set} of {@link Long} from the specified {@link Collection} of element type
	 *         {@code E}
	 */
	public static <E> Set<Long> collectionToSet(final Collection<E> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code long} value.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code long} value
	 */
	public static long random() {
		return random(Long.MIN_VALUE, Long.MAX_VALUE);
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code long} value between the specified
	 * bounds.
	 * <p>
	 * @param lowerBound the lower bound of the {@code long} value to generate (inclusive)
	 * @param upperBound the upper bound of the {@code long} value to generate (exclusive)
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code long} value between the specified bounds
	 */
	public static long random(final long lowerBound, final long upperBound) {
		return lowerBound + convert(RANDOM.nextDouble() * (upperBound - lowerBound));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code long} array of the specified length containing the sequence of numbers
	 * starting with zero and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return a {@code long} array of the specified length containing the sequence of numbers
	 *         starting with zero and spaced by one
	 */
	public static long[] createSequence(final int length) {
		return createSequence(length, 0L, 1L);
	}

	/**
	 * Creates a {@code long} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return a {@code long} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by one
	 */
	public static long[] createSequence(final int length, final long from) {
		return createSequence(length, from, 1L);
	}

	/**
	 * Creates a {@code long} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return a {@code long} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by {@code step}
	 */
	public static long[] createSequence(final int length, final long from, final long step) {
		final long[] array = new long[length];
		long value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a random {@code long} array of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return a random {@code long} array of the specified length
	 */
	public static long[] createRandomSequence(final int length) {
		final long[] array = new long[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates a {@code long} array of the specified length containing pseudorandom, uniformly
	 * distributed {@code long} values between the specified bounds.
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create (inclusive)
	 * @param upperBound the upper bound of the random sequence to create (exclusive)
	 * <p>
	 * @return a {@code long} array of the specified length containing pseudorandom, uniformly
	 *         distributed {@code long} values between the specified bounds
	 */
	public static long[] createRandomSequence(final int length, final long lowerBound,
			final long upperBound) {
		final long[] array = new long[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(lowerBound, upperBound);
		}
		return array;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code long} token in the specified
	 * {@code long} array.
	 * <p>
	 * @param array a {@code long} array
	 * @param token the {@code long} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code long} token in the specified
	 *         {@code long} array
	 */
	public static int count(final long[] array, final long token) {
		int occurrenceCount = 0, index = 0;
		while ((index = findFirstIndex(array, token, index)) >= 0) {
			++occurrenceCount;
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code long} tokens in the specified
	 * {@code long} array.
	 * <p>
	 * @param array  a {@code long} array
	 * @param tokens the {@code long} tokens to count
	 * <p>
	 * @return the number of occurrences of the specified {@code long} tokens in the specified
	 *         {@code long} array
	 */
	public static int count(final long[] array, final long[] tokens) {
		int occurrenceCount = 0;
		for (final long token : tokens) {
			occurrenceCount += count(array, token);
		}
		return occurrenceCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void fill(final long[] array, final long value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
	}

	public static void fill(final long[][] array2D, final long value) {
		for (final long[] array : array2D) {
			fill(array, value);
		}
	}

	public static void fill(final long[][][] array3D, final long value) {
		for (final long[][] array2D : array3D) {
			fill(array2D, value);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the filtered {@code long} array from the specified {@code long} array and indexes.
	 * <p>
	 * @param array   a {@code long} array
	 * @param indexes the indexes to filter
	 * <p>
	 * @return the filtered {@code long} array from the specified {@code long} array and indexes
	 */
	public static long[] filter(final long[] array, final int... indexes) {
		final long[] filteredArray = new long[indexes.length];
		for (int i = 0; i < indexes.length; ++i) {
			filteredArray[i] = array[indexes[i]];
		}
		return filteredArray;
	}

	/**
	 * Returns all the filtered {@code long} arrays from the specified {@code long} array and
	 * indexes.
	 * <p>
	 * @param array   a {@code long} array
	 * @param indexes the array of indexes to filter
	 * <p>
	 * @return all the filtered {@code long} arrays from the specified {@code long} array and
	 *         indexes
	 */
	public static long[][] filterAll(final long[] array, final int[]... indexes) {
		final long[][] filteredArrays = new long[indexes.length][];
		for (int i = 0; i < indexes.length; ++i) {
			filteredArrays[i] = filter(array, indexes[i]);
		}
		return filteredArrays;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code long} array containing the specified {@code long} value and all the elements
	 * of the specified {@code long} array.
	 * <p>
	 * @param a a {@code long} value (may be {@code null})
	 * @param b a {@code long} array (may be {@code null})
	 * <p>
	 * @return a {@code long} array containing the specified {@code long} value and all the elements
	 *         of the specified {@code long} array
	 */
	public static long[] merge(final long a, final long... b) {
		return merge(asPrimitiveArray(a), b);
	}

	/**
	 * Returns a {@code long} array containing all the elements of the specified {@code long}
	 * arrays.
	 * <p>
	 * @param a a {@code long} array (may be {@code null})
	 * @param b a {@code long} array (may be {@code null})
	 * <p>
	 * @return a {@code long} array containing all the elements of the specified {@code long} arrays
	 */
	public static long[] merge(final long[] a, final long... b) {
		if (a == null) {
			return clone(b);
		} else if (b == null) {
			return clone(a);
		}
		final long[] mergedArray = new long[a.length + b.length];
		System.arraycopy(a, 0, mergedArray, 0, a.length);
		System.arraycopy(b, 0, mergedArray, a.length, b.length);
		return mergedArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code long} value.
	 * <p>
	 * @param value a {@code long} value
	 * <p>
	 * @return the middle of the specified {@code long} value
	 */
	public static long middle(final long value) {
		return middle(0L, value);
	}

	/**
	 * Returns the middle of the specified lower and upper bounds rounded to the lower {@code long}
	 * value.
	 * <p>
	 * @param lowerBound a {@code long} value
	 * @param upperBound another {@code long} value
	 * <p>
	 * @return the middle of the specified lower and upper bounds rounded to the lower {@code long}
	 *         value
	 */
	public static long middle(final long lowerBound, final long upperBound) {
		return lowerBound + (upperBound - lowerBound) / 2L;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void swap(final long[] array, final int i, final int j) {
		final long element = array[i];
		array[i] = array[j];
		array[j] = element;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static long[] take(final long... array) {
		return take(array, 0, array.length);
	}

	public static long[] take(final long[] array, final int fromIndex, final int length) {
		final int maxLength = Math.min(length, array.length - fromIndex);
		final long[] subarray = new long[maxLength];
		System.arraycopy(array, fromIndex, subarray, 0, maxLength);
		return subarray;
	}

	public static long[] take(final long[]... array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static long[] take(final long[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static long[] take(final long[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length - fromRow);
		final long[] subarray = new long[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, subarray, i * columnCount,
					columnCount);
		}
		return subarray;
	}

	public static long[] take(final long[][]... array3D) {
		return take(array3D, 0, array3D.length, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static long[] take(final long[][][] array3D, final int fromRow, final int rowCount) {
		return take(array3D, fromRow, rowCount, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static long[] take(final long[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0, array3D[0][0].length);
	}

	public static long[] take(final long[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length - fromRow);
		final int length = columnCount * depthCount;
		final long[] subarray = new long[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					subarray, i * length, length);
		}
		return subarray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified {@code long} array.
	 * <p>
	 * @param rowCount the number of rows of the array
	 * @param array    a {@code long} array
	 * <p>
	 * @return the transpose of the specified {@code long} array
	 */
	public static long[] transpose(final int rowCount, final long[] array) {
		final int n = rowCount;
		final int m = array.length / rowCount;
		final long[] transpose = new long[m * n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i * n + j] = array[j * m + i];
			}
		}
		return transpose;
	}

	/**
	 * Returns the transpose of the specified 2D {@code long} array.
	 * <p>
	 * @param array2D a 2D {@code long} array
	 * <p>
	 * @return the transpose of the specified 2D {@code long} array
	 */
	public static long[][] transpose(final long[]... array2D) {
		final int n = array2D.length;
		final int m = array2D[0].length;
		final long[][] transpose = new long[m][n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i][j] = array2D[j][i];
			}
		}
		return transpose;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SEEKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int findFirstIndex(final long[] array, final long token) {
		return findFirstIndex(array, token, 0, array.length);
	}

	public static int findFirstIndex(final long[] array, final long token, final int from) {
		return findFirstIndex(array, token, from, array.length);
	}

	public static int findFirstIndex(final long[] array, final long token, final int from,
			final int to) {
		if (array != null) {
			for (int i = from; i < to; ++i) {
				if (array[i] == token) {
					return i;
				}
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int findLastIndex(final long[] array, final long token) {
		return findLastIndex(array, token, 0, array.length);
	}

	public static int findLastIndex(final long[] array, final long token, final int from) {
		return findLastIndex(array, token, from, array.length);
	}

	public static int findLastIndex(final long[] array, final long token, final int from,
			final int to) {
		if (array != null) {
			for (int i = to - 1; i >= from; --i) {
				if (array[i] == token) {
					return i;
				}
			}
		}
		return -1;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code long} value or a
	 * {@link Long}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code long} value or
	 *         a {@link Long}, {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return long.class.isAssignableFrom(c) || Long.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code long} value.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code long} value,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitive(final Class<?> c) {
		return long.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code long} array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code long} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArray(final Class<?> c) {
		return long[].class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code long} array is {@code null} or empty.
	 * <p>
	 * @param array the {@code long} array to test
	 * <p>
	 * @return {@code true} if the specified {@code long} array is {@code null} or empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final long[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Tests whether the specified {@code long} array is not {@code null} and empty.
	 * <p>
	 * @param array the {@code long} array to test
	 * <p>
	 * @return {@code true} if the specified {@code long} array is not {@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final long[] array) {
		return array != null && array.length == 0;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code long} value is between the specified {@code long} lower
	 * and upper bounds.
	 * <p>
	 * @param value the {@code long} value to test
	 * @param from  the {@code long} lower bound to test against (inclusive)
	 * @param to    the {@code long} upper bound to test against (exclusive)
	 * <p>
	 * @return {@code true} if the specified {@code long} value is between the specified
	 *         {@code long} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final long value, final long from, final long to) {
		return value >= from && value < to;
	}

	/**
	 * Tests whether the specified {@code long} array is between the specified lower and upper bound
	 * {@code long} arrays.
	 * <p>
	 * @param array the {@code long} array to test
	 * @param from  the lower bound {@code long} array to test against (inclusive)
	 * @param to    the upper bound {@code long} array to test against (exclusive)
	 * <p>
	 * @return {@code true} if the specified {@code long} array is between the specified lower and
	 *         upper bound {@code long} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final long[] array, final long[] from, final long[] to) {
		return compare(array, from) >= 0 && compare(array, to) < 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code long} array contains the specified {@code long} token.
	 * <p>
	 * @param array the {@code long} array to test
	 * @param token the {@code long} token to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code long} array contains the specified {@code long}
	 *         token, {@code false} otherwise
	 */
	public static boolean contains(final long[] array, final long token) {
		return findFirstIndex(array, token) >= 0;
	}

	/**
	 * Tests whether the specified {@code long} array contains any of the specified {@code long}
	 * tokens.
	 * <p>
	 * @param array  the {@code long} array to test
	 * @param tokens the {@code long} tokens to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code long} array contains any of the specified
	 *         {@code long} tokens, {@code false} otherwise
	 */
	public static boolean containsAny(final long[] array, final long[] tokens) {
		if (array != null) {
			for (final long token : tokens) {
				if (contains(array, token)) {
					return true;
				}
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified {@code long} values for order. Returns a negative integer, zero or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code long} value to compare for order
	 * @param b the other {@code long} value to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 */
	public static int compare(final long a, final long b) {
		return a < b ? -1 : a == b ? 0 : 1;
	}

	//////////////////////////////////////////////

	/**
	 * Compares the specified {@code long} arrays for order. Returns a negative integer, zero or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code long} array to compare for order
	 * @param b the other {@code long} array to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static int compare(final long[] a, final long[] b) {
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
	 * Returns a clone of the specified {@code long} array, or {@code null} if {@code array} is
	 * {@code null}.
	 * <p>
	 * @param array the {@code long} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified {@code long} array, or {@code null} if {@code array} is
	 *         {@code null}
	 */
	public static long[] clone(final long... array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the hash code value for the specified {@code long} array.
	 * <p>
	 * @param array the {@code long} array to hash
	 * <p>
	 * @return the hash code value for the specified {@code long} array
	 */
	public static int hashCode(final long... array) {
		return hashCodeWith(0, array);
	}

	/**
	 * Returns the hash code value for the specified {@code long} array at the specified depth.
	 * <p>
	 * @param array the {@code long} array to hash
	 * @param depth the depth to hash at
	 * <p>
	 * @return the hash code value for the specified {@code long} array at the specified depth
	 */
	@SuppressWarnings("unchecked")
	public static int hashCodeWith(final int depth, final long... array) {
		if (array == null) {
			return 0;
		}
		switch (array.length) {
			case 0:
				return Bits.SEEDS[depth % Bits.SEEDS.length];
			case 1:
				return (int) (array[0] ^ (array[0] >>> 32));
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
	 * Returns a representative {@link String} of the specified {@code long} array.
	 * <p>
	 * @param array a {@code long} array
	 * <p>
	 * @return a representative {@link String} of the specified {@code long} array
	 */
	public static String toString(final long... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a representative {@link String} of the specified {@code long} array joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     a {@code long} array
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@code long} array joined by
	 *         {@code delimiter}
	 */
	public static String toString(final long[] array, final String delimiter) {
		return Arrays.toString(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code long} array joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     a {@code long} array
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code long} array joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final long[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(toArray(array), delimiter, wrapper);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified array of {@link Long}.
	 * <p>
	 * @param array an array of {@link Long}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Long}
	 */
	public static String toString(final Long... array) {
		return Arrays.toString(array);
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Long} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an array of {@link Long}
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Long} joined by
	 *         {@code delimiter}
	 */
	public static String toString(final Long[] array, final String delimiter) {
		return Arrays.toString(array, delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Long} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@link Long}
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Long} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final Long[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(array, delimiter, wrapper);
	}
}
