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
import jupiter.common.map.parser.CharacterParser;
import jupiter.common.map.parser.IParsers;
import jupiter.common.struct.list.ExtendedList;

public class Characters {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final char[] EMPTY_PRIMITIVE_ARRAY = new char[] {};
	public static final Character[] EMPTY_ARRAY = new Character[] {};

	protected static final CharacterParser PARSER = IParsers.CHARACTER_PARSER;

	public static volatile Random RANDOM = new Random();

	public static final char ESCAPE = '\u001B';

	public static final char SINGLE_QUOTE = '\'';
	public static final char DOUBLE_QUOTE = '"';
	public static final char LEFT_QUOTE = '‘'; // "\u2018"
	public static final char RIGHT_QUOTE = '’'; // "\u2019"

	public static final char LEFT_PARENTHESIS = '(';
	public static final char RIGHT_PARENTHESIS = ')';
	public static final char LEFT_BRACKET = '[';
	public static final char RIGHT_BRACKET = ']';

	/**
	 * The lower case digits.
	 */
	public static final char[] LOWER_CASE_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
		'9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
		'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	/**
	 * The upper case digits.
	 */
	public static final char[] UPPER_CASE_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
		'9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
		'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Characters}.
	 */
	protected Characters() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Character} converted from the specified {@link Object}.
	 * <p>
	 * @param object the {@link Object} to convert
	 * <p>
	 * @return a {@link Character} converted from the specified {@link Object}
	 */
	public static Character convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code char} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object a {@code T} object
	 * <p>
	 * @return a {@code char} value from the specified {@code T} object
	 */
	public static <T> char toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code char} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@code char} array from the specified {@code T} array
	 */
	public static <T> char[] toPrimitiveArray(final T[] array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a {@code char} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@code char} array from the specified {@code T} array
	 */
	public static <T> char[] asPrimitiveArray(final T... array) {
		return toPrimitiveArray(array);
	}

	/**
	 * Returns a {@code char} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a {@code char} array from the specified 2D {@code T} array
	 */
	public static <T> char[] toPrimitiveArray(final T[][] array2D) {
		return PARSER.callToPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code char} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a {@code char} array from the specified 2D {@code T} array
	 */
	public static <T> char[] asPrimitiveArray(final T[]... array2D) {
		return toPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code char} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a {@code char} array from the specified 3D {@code T} array
	 */
	public static <T> char[] toPrimitiveArray(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray(array3D);
	}

	/**
	 * Returns a {@code char} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a {@code char} array from the specified 3D {@code T} array
	 */
	public static <T> char[] asPrimitiveArray(final T[][]... array3D) {
		return toPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D {@code char} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a 2D {@code char} array from the specified 2D {@code T} array
	 */
	public static <T> char[][] toPrimitiveArray2D(final T[][] array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 2D {@code char} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a 2D {@code char} array from the specified 2D {@code T} array
	 */
	public static <T> char[][] asPrimitiveArray2D(final T[]... array2D) {
		return toPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 3D {@code char} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code char} array from the specified 3D {@code T} array
	 */
	public static <T> char[][][] toPrimitiveArray3D(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a 3D {@code char} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code char} array from the specified 3D {@code T} array
	 */
	public static <T> char[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a {@code char} array from the specified {@link Collection} of type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@code char} array from the specified {@link Collection} of type {@code T}
	 */
	public static <T> char[] collectionToPrimitiveArray(final Collection<T> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Character} from the specified {@code char} array.
	 * <p>
	 * @param array a {@code char} array
	 * <p>
	 * @return an array of {@link Character} from the specified {@code char} array
	 */
	public static Character[] toArray(final char[] array) {
		final Character[] convertedArray = new Character[array.length];
		for (int i = 0; i < array.length; ++i) {
			convertedArray[i] = array[i];
		}
		return convertedArray;
	}

	/**
	 * Returns an array of {@link Character} from the specified {@code char} array.
	 * <p>
	 * @param array a {@code char} array
	 * <p>
	 * @return an array of {@link Character} from the specified {@code char} array
	 */
	public static Character[] asArray(final char... array) {
		return toArray(array);
	}

	/**
	 * Returns a 2D array of {@link Character} from the specified 2D {@code char} array.
	 * <p>
	 * @param array2D a 2D {@code char} array
	 * <p>
	 * @return a 2D array of {@link Character} from the specified 2D {@code char} array
	 */
	public static Character[][] toArray2D(final char[][] array2D) {
		final Character[][] convertedArray2D = new Character[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			convertedArray2D[i] = toArray(array2D[i]);
		}
		return convertedArray2D;
	}

	/**
	 * Returns a 2D array of {@link Character} from the specified 2D {@code char} array.
	 * <p>
	 * @param array2D a 2D {@code char} array
	 * <p>
	 * @return a 2D array of {@link Character} from the specified 2D {@code char} array
	 */
	public static Character[][] asArray2D(final char[]... array2D) {
		return toArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@link Character} from the specified 3D {@code char} array.
	 * <p>
	 * @param array3D a 3D {@code char} array
	 * <p>
	 * @return a 3D array of {@link Character} from the specified 3D {@code char} array
	 */
	public static Character[][][] toArray3D(final char[][][] array3D) {
		final Character[][][] convertedArray3D = new Character[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			convertedArray3D[i] = toArray2D(array3D[i]);
		}
		return convertedArray3D;
	}

	/**
	 * Returns a 3D array of {@link Character} from the specified 3D {@code char} array.
	 * <p>
	 * @param array3D a 3D {@code char} array
	 * <p>
	 * @return a 3D array of {@link Character} from the specified 3D {@code char} array
	 */
	public static Character[][][] asArray3D(final char[][]... array3D) {
		return toArray3D(array3D);
	}

	/**
	 * Returns an array of {@link Character} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@link Character} from the specified {@link Collection} of type {@code T}
	 */
	public static <T> Character[] collectionToArray(final Collection<T> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link List} of {@link Character} from the specified {@code char} array.
	 * <p>
	 * @param array a {@code char} array
	 * <p>
	 * @return a {@link List} of {@link Character} from the specified {@code char} array
	 */
	public static List<Character> toList(final char[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns a {@link List} of {@link Character} from the specified {@code char} array.
	 * <p>
	 * @param array a {@code char} array
	 * <p>
	 * @return a {@link List} of {@link Character} from the specified {@code char} array
	 */
	public static List<Character> asList(final char... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Character} from the specified {@code char} array.
	 * <p>
	 * @param array a {@code char} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Character} from the specified {@code char} array
	 */
	public static ExtendedList<Character> toExtendedList(final char[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Character} from the specified {@code char} array.
	 * <p>
	 * @param array a {@code char} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Character} from the specified {@code char} array
	 */
	public static ExtendedList<Character> asExtendedList(final char... array) {
		return toExtendedList(array);
	}

	/**
	 * Returns a {@link List} of {@link Character} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link List} of {@link Character} from the specified {@code T} array
	 */
	public static <T> List<Character> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Character} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link List} of {@link Character} from the specified {@code T} array
	 */
	public static <T> List<Character> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Character} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Character} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Character> toExtendedList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Character} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Character} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Character> asExtendedList(final T... array) {
		return toExtendedList(array);
	}

	/**
	 * Returns a {@link List} of {@link Character} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Character} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> List<Character> collectionToList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Character} from the specified {@link Collection} of
	 * type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Character} from the specified {@link Collection} of
	 *         type {@code T}
	 */
	public static <T> ExtendedList<Character> collectionToExtendedList(
			final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Character} from the specified {@code char} array.
	 * <p>
	 * @param array a {@code char} array
	 * <p>
	 * @return a {@link Set} of {@link Character} from the specified {@code char} array
	 */
	public static Set<Character> toSet(final char[] array) {
		return PARSER.callToSet(toArray(array));
	}

	/**
	 * Returns a {@link Set} of {@link Character} from the specified {@code char} array.
	 * <p>
	 * @param array a {@code char} array
	 * <p>
	 * @return a {@link Set} of {@link Character} from the specified {@code char} array
	 */
	public static Set<Character> asSet(final char... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Character} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Character} from the specified {@code T} array
	 */
	public static <T> Set<Character> toSet(final T[] array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Character} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Character} from the specified {@code T} array
	 */
	public static <T> Set<Character> asSet(final T... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Character} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Character} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> Set<Character> collectionToSet(final Collection<T> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code char} value.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code char} value
	 */
	public static char random() {
		return (char) (Character.MIN_VALUE + RANDOM.nextInt(Character.MAX_VALUE + 1));
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code char} value between {@code lowerBound}
	 * (inclusive) and {@code upperBound} (exclusive).
	 * <p>
	 * @param lowerBound the lower bound of the {@code char} value to create
	 * @param upperBound the upper bound of the {@code char} value to create
	 * <p>
	 * @return the next pseudorandom, uniformly distributed {@code char} value between
	 *         {@code lowerBound} (inclusive) and {@code upperBound} (exclusive)
	 */
	public static char random(final char lowerBound, final char upperBound) {
		return (char) (lowerBound + RANDOM.nextInt(upperBound - lowerBound));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code char} array of the specified length containing the sequence of characters
	 * starting with zero and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return a {@code char} array of the specified length containing the sequence of characters
	 *         starting with zero and spaced by one
	 */
	public static char[] createSequence(final char length) {
		return createSequence(length, Character.MIN_VALUE, '\u0001');
	}

	/**
	 * Creates a {@code char} array of the specified length containing the sequence of characters
	 * starting with {@code from} and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return a {@code char} array of the specified length containing the sequence of characters
	 *         starting with {@code from} and spaced by one
	 */
	public static char[] createSequence(final char length, final char from) {
		return createSequence(length, from, '\u0001');
	}

	/**
	 * Creates a {@code char} array of the specified length containing the sequence of characters
	 * starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return a {@code char} array of the specified length containing the sequence of characters
	 *         starting with {@code from} and spaced by {@code step}
	 */
	public static char[] createSequence(final char length, final char from, final char step) {
		final char[] array = new char[length];
		char value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a random {@code char} array of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return a random {@code char} array of the specified length
	 */
	public static char[] createRandomSequence(final int length) {
		final char[] array = new char[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates a {@code char} array of the specified length containing pseudorandom, uniformly
	 * distributed {@code char} values between {@code lowerBound} (inclusive) and {@code upperBound}
	 * (exclusive).
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create
	 * @param upperBound the upper bound of the random sequence to create
	 * <p>
	 * @return a {@code char} array of the specified length containing pseudorandom, uniformly
	 *         distributed {@code char} values between {@code lowerBound} (inclusive) and
	 *         {@code upperBound} (exclusive)
	 */
	public static char[] createRandomSequence(final int length, final char lowerBound,
			final char upperBound) {
		final char[] array = new char[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(lowerBound, upperBound);
		}
		return array;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void fill(final char[] array, final char value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
	}

	public static void fill(final char[][] array2D, final char value) {
		for (final char[] array : array2D) {
			fill(array, value);
		}
	}

	public static void fill(final char[][][] array3D, final char value) {
		for (final char[][] array2D : array3D) {
			fill(array2D, value);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the filtered {@code char} array from the specified {@code char} array and indexes.
	 * <p>
	 * @param array   a {@code char} array
	 * @param indexes the indexes to filter
	 * <p>
	 * @return the filtered {@code char} array from the specified {@code char} array and indexes
	 */
	public static char[] filter(final char[] array, final int... indexes) {
		final char[] filteredArray = new char[indexes.length];
		for (int i = 0; i < indexes.length; ++i) {
			filteredArray[i] = array[indexes[i]];
		}
		return filteredArray;
	}

	/**
	 * Returns all the filtered {@code char} arrays from the specified {@code char} array and
	 * indexes.
	 * <p>
	 * @param array   a {@code char} array
	 * @param indexes the array of indexes to filter
	 * <p>
	 * @return all the filtered {@code char} arrays from the specified {@code char} array and
	 *         indexes
	 */
	public static char[][] filterAll(final char[] array, final int[]... indexes) {
		final char[][] filteredArrays = new char[indexes.length][];
		for (int i = 0; i < indexes.length; ++i) {
			filteredArrays[i] = filter(array, indexes[i]);
		}
		return filteredArrays;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int indexOf(final char[] array, final char token) {
		if (array != null) {
			for (int i = 0; i < array.length; ++i) {
				if (array[i] == token) {
					return i;
				}
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code char} array containing the specified {@code char} value and all the elements
	 * of the specified {@code char} array.
	 * <p>
	 * @param a a {@code char} value (may be {@code null})
	 * @param b a {@code char} array (may be {@code null})
	 * <p>
	 * @return a {@code char} array containing the specified {@code char} value and all the elements
	 *         of the specified {@code char} array
	 */
	public static char[] merge(final char a, final char... b) {
		return merge(asPrimitiveArray(a), b);
	}

	/**
	 * Returns a {@code char} array containing all the elements of the specified {@code char}
	 * arrays.
	 * <p>
	 * @param a a {@code char} array (may be {@code null})
	 * @param b a {@code char} array (may be {@code null})
	 * <p>
	 * @return a {@code char} array containing all the elements of the specified {@code char} arrays
	 */
	public static char[] merge(final char[] a, final char... b) {
		if (a == null) {
			return clone(b);
		} else if (b == null) {
			return clone(a);
		}
		final char[] mergedArray = new char[a.length + b.length];
		System.arraycopy(a, 0, mergedArray, 0, a.length);
		System.arraycopy(b, 0, mergedArray, a.length, b.length);
		return mergedArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void swap(final char[] array, final int i, final int j) {
		final char element = array[i];
		array[i] = array[j];
		array[j] = element;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static char[] take(final char... array) {
		return take(array, 0, array.length);
	}

	public static char[] take(final char[] array, final int from, final int length) {
		final int maxLength = Math.min(length, array.length - from);
		final char[] subarray = new char[maxLength];
		System.arraycopy(array, from, subarray, 0, maxLength);
		return subarray;
	}

	public static char[] take(final char[]... array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static char[] take(final char[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static char[] take(final char[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length - fromRow);
		final char[] subarray = new char[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, subarray, i * columnCount,
					columnCount);
		}
		return subarray;
	}

	public static char[] take(final char[][]... array3D) {
		return take(array3D, 0, array3D.length, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static char[] take(final char[][][] array3D, final int fromRow, final int rowCount) {
		return take(array3D, fromRow, rowCount, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static char[] take(final char[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0, array3D[0][0].length);
	}

	public static char[] take(final char[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length - fromRow);
		final int length = columnCount * depthCount;
		final char[] subarray = new char[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					subarray, i * length, length);
		}
		return subarray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified {@code char} array.
	 * <p>
	 * @param rowCount the number of rows of the array
	 * @param array    a {@code char} array
	 * <p>
	 * @return the transpose of the specified {@code char} array
	 */
	public static char[] transpose(final int rowCount, final char[] array) {
		final int n = rowCount;
		final int m = array.length / rowCount;
		final char[] transpose = new char[m * n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i * n + j] = array[j * m + i];
			}
		}
		return transpose;
	}

	/**
	 * Returns the transpose of the specified 2D {@code char} array.
	 * <p>
	 * @param array2D a 2D {@code char} array
	 * <p>
	 * @return the transpose of the specified 2D {@code char} array
	 */
	public static char[][] transpose(final char[]... array2D) {
		final int n = array2D.length;
		final int m = array2D[0].length;
		final char[][] transpose = new char[m][n];
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
	 * Tests whether the specified {@link Class} is assignable to a {@code char} value or a
	 * {@link Character}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code char} value or
	 *         a {@link Character}, {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return char.class.isAssignableFrom(c) || Character.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code char} value.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code char} value,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitive(final Class<?> c) {
		return char.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code char} array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code char} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArray(final Class<?> c) {
		return char[].class.isAssignableFrom(c);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code char} value is a parenthesis.
	 * <p>
	 * @param character the {@code char} value to test
	 * <p>
	 * @return {@code true} if the specified {@code char} value is a parenthesis, {@code false}
	 *         otherwise
	 */
	public static boolean isParenthesis(final char character) {
		return character == LEFT_PARENTHESIS || character == RIGHT_PARENTHESIS;
	}

	/**
	 * Tests whether the specified {@code char} value is a bracket.
	 * <p>
	 * @param character the {@code char} value to test
	 * <p>
	 * @return {@code true} if the specified {@code char} value is a bracket, {@code false}
	 *         otherwise
	 */
	public static boolean isBracket(final char character) {
		return character == LEFT_BRACKET || character == RIGHT_BRACKET;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code array} contains {@code token}.
	 * <p>
	 * @param array a {@code char} array
	 * @param token the {@code char} value to test for presence
	 * <p>
	 * @return {@code true} if {@code array} contains {@code token}, {@code false} otherwise
	 */
	public static boolean contains(final char[] array, final char token) {
		return indexOf(array, token) >= 0;
	}

	/**
	 * Tests whether {@code array} contains any {@code tokens}.
	 * <p>
	 * @param array  a {@code char} array
	 * @param tokens the {@code char} array to test for presence
	 * <p>
	 * @return {@code true} if {@code array} contains any {@code tokens}, {@code false} otherwise
	 */
	public static boolean containsAny(final char[] array, final char[] tokens) {
		if (array == null) {
			return false;
		}
		for (final char token : tokens) {
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
	 * Compares the specified {@code char} values for order. Returns a negative integer, zero or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code char} value to compare for order
	 * @param b the other {@code char} value to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 */
	public static int compare(final char a, final char b) {
		return a < b ? -1 : a == b ? 0 : 1;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a clone of the specified {@code char} array, or {@code null} if {@code array} is
	 * {@code null}.
	 * <p>
	 * @param array a {@code char} array
	 * <p>
	 * @return a clone of the specified {@code char} array, or {@code null} if {@code array} is
	 *         {@code null}
	 */
	public static char[] clone(final char... array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@code char} array.
	 * <p>
	 * @param array a {@code char} array
	 * <p>
	 * @return a representative {@link String} of the specified {@code char} array
	 */
	public static String toString(final char... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a representative {@link String} of the specified {@code char} array joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     a {@code char} array
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@code char} array joined by
	 *         {@code delimiter}
	 */
	public static String toString(final char[] array, final String delimiter) {
		return Arrays.toString(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code char} array joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     a {@code char} array
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code char} array joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final char[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(toArray(array), delimiter, wrapper);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified array of {@link Character}.
	 * <p>
	 * @param array an array of {@link Character}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Character}
	 */
	public static String toString(final Character... array) {
		return Arrays.toString(array);
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Character} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an array of {@link Character}
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Character} joined by
	 *         {@code delimiter}
	 */
	public static String toString(final Character[] array, final String delimiter) {
		return Arrays.toString(array, delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Character} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@link Character}
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Character} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final Character[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(array, delimiter, wrapper);
	}
}
