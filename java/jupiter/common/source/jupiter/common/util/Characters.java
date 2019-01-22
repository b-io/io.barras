/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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
import jupiter.common.map.parser.Parsers;
import jupiter.common.struct.list.ExtendedList;

public class Characters {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final char[] EMPTY_PRIMITIVE_ARRAY = new char[] {};
	public static final Character[] EMPTY_ARRAY = new Character[] {};

	protected static final CharacterParser PARSER = Parsers.CHARACTER_PARSER;

	public static volatile Random RANDOM = new Random();

	public static final char SINGLE_QUOTE = '\'';
	public static final char DOUBLE_QUOTE = '"';
	public static final char LEFT_QUOTE = '`';
	public static final char RIGHT_QUOTE = '´';

	public static final char LEFT_PARENTHESIS = '(';
	public static final char RIGHT_PARENTHESIS = ')';
	public static final char LEFT_BRACKET = '[';
	public static final char RIGHT_BRACKET = ']';


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Characters() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Character} from the specified {@link Object}.
	 * <p>
	 * @param object an {@link Object}
	 * <p>
	 * @return a {@link Character} from the specified {@link Object}
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

	/**
	 * Returns an array of {@code char} values from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an array of {@code char} values from the specified array of type {@code T}
	 */
	public static <T> char[] toPrimitiveArray(final T... array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a 2D array of {@code char} values from the specified 2D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array to convert
	 * @param array2D a 2D array of type {@code T}
	 * <p>
	 * @return a 2D array of {@code char} values from the specified 2D array of type {@code T}
	 */
	public static <T> char[][] toPrimitiveArray2D(final T[]... array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@code char} values from the specified 3D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array to convert
	 * @param array3D a 3D array of type {@code T}
	 * <p>
	 * @return a 3D array of {@code char} values from the specified 3D array of type {@code T}
	 */
	public static <T> char[][][] toPrimitiveArray3D(final T[][]... array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns an array of {@code char} values from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@code char} values from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> char[] collectionToPrimitiveArray(final Collection<T> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Character} from the specified array of {@code char} values.
	 * <p>
	 * @param array an array of {@code char} values
	 * <p>
	 * @return an array of {@link Character} from the specified array of {@code char} values
	 */
	public static Character[] toArray(final char... array) {
		final Character[] result = new Character[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = array[i];
		}
		return result;
	}

	/**
	 * Returns a 2D array of {@link Character} from the specified 2D array of {@code char} values.
	 * <p>
	 * @param array2D a 2D array of {@code char} values
	 * <p>
	 * @return a 2D array of {@link Character} from the specified 2D array of {@code char} values
	 */
	public static Character[][] toArray2D(final char[]... array2D) {
		final Character[][] result = new Character[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = toArray(array2D[i]);
		}
		return result;
	}

	/**
	 * Returns a 3D array of {@link Character} from the specified 3D array of {@code char} values.
	 * <p>
	 * @param array3D a 3D array of {@code char} values
	 * <p>
	 * @return a 3D array of {@link Character} from the specified 3D array of {@code char} values
	 */
	public static Character[][][] toArray3D(final char[][]... array3D) {
		final Character[][][] result = new Character[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = toArray2D(array3D[i]);
		}
		return result;
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
	 * Returns a {@link List} of {@link Character} from the specified array of {@code char} values.
	 * <p>
	 * @param array an array of {@code char} values
	 * <p>
	 * @return a {@link List} of {@link Character} from the specified array of {@code char} values
	 */
	public static List<Character> toList(final char... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Character} from the specified array of {@code char}
	 * values.
	 * <p>
	 * @param array an array of {@code char} values
	 * <p>
	 * @return an {@link ExtendedList} of {@link Character} from the specified array of {@code char}
	 *         values
	 */
	public static ExtendedList<Character> toExtendedList(final char... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Character} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Character} from the specified array of type {@code T}
	 */
	public static <T> List<Character> toList(final T... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Character} from the specified array of type
	 * {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Character} from the specified array of type
	 *         {@code T}
	 */
	public static <T> ExtendedList<Character> toExtendedList(final T... array) {
		return PARSER.callToList(array);
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
	 * Returns a {@link Set} of {@link Character} from the specified array of {@code char} values.
	 * <p>
	 * @param array an array of {@code char} values
	 * <p>
	 * @return a {@link Set} of {@link Character} from the specified array of {@code char} values
	 */
	public static Set<Character> toSet(final char... array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Character} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Character} from the specified array of type {@code T}
	 */
	public static <T> Set<Character> toSet(final T... array) {
		return PARSER.callToSet(array);
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
	 * Creates an array of {@code char} values of the specified length containing the sequence of
	 * characters starting with zero and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return an array of {@code char} values of the specified length containing the sequence of
	 *         characters starting with zero and spaced by one
	 */
	public static char[] createSequence(final char length) {
		return createSequence(length, Character.MIN_VALUE, '\u0001');
	}

	/**
	 * Creates an array of {@code char} values of the specified length containing the sequence of
	 * characters starting with {@code from} and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return an array of {@code char} values of the specified length containing the sequence of
	 *         characters starting with {@code from} and spaced by one
	 */
	public static char[] createSequence(final char length, final char from) {
		return createSequence(length, from, '\u0001');
	}

	/**
	 * Creates an array of {@code char} values of the specified length containing the sequence of
	 * characters starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return an array of {@code char} values of the specified length containing the sequence of
	 *         characters starting with {@code from} and spaced by {@code step}
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
	 * Creates an array of random {@code char} values of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return an array of random {@code char} values of the specified length
	 */
	public static char[] createRandomSequence(final int length) {
		final char[] array = new char[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates an array of {@code char} values of the specified length containing pseudorandom,
	 * uniformly distributed {@code char} values between {@code lowerBound} (inclusive) and
	 * {@code upperBound} (exclusive).
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create
	 * @param upperBound the upper bound of the random sequence to create
	 * <p>
	 * @return an array of {@code char} values of the specified length containing pseudorandom,
	 *         uniformly distributed {@code char} values between {@code lowerBound} (inclusive) and
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
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void fill(final char[] array, final char value) {
		for (char i = 0; i < array.length; ++i) {
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

	public static char[] take(final char[] array) {
		return take(array, 0, array.length);
	}

	public static char[] take(final char[] array, final int from, final int length) {
		final int maxLength = Math.min(length, array.length);
		final char[] result = new char[maxLength];
		System.arraycopy(array, from, result, 0, maxLength);
		return result;
	}

	public static char[] take(final char[][] array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static char[] take(final char[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static char[] take(final char[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length);
		final char[] result = new char[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, result, i * columnCount,
					columnCount);
		}
		return result;
	}

	public static char[] take(final char[][][] array3D) {
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
		final int maxRowCount = Math.min(rowCount, array3D.length);
		final int length = columnCount * depthCount;
		final char[] result = new char[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					result, i * length, length);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified 2D array of {@code char} values.
	 * <p>
	 * @param array2D a 2D array of {@code char} values
	 * <p>
	 * @return the transpose of the specified 2D array of {@code char} values
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
	 * Tests whether the specified {@code char} value is a parenthesis.
	 * <p>
	 * @param character the {@code char} value to test
	 * <p>
	 * @return {@code true} if the specified {@code char} value is a parenthesis, {@code false}
	 *         otherwise
	 */
	public static boolean isParenthesis(final char character) {
		return character == Characters.LEFT_PARENTHESIS ||
				character == Characters.RIGHT_PARENTHESIS;
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
		return character == Characters.LEFT_BRACKET || character == Characters.RIGHT_BRACKET;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code characters} contains {@code character}.
	 * <p>
	 * @param characters an array of {@code char} values
	 * @param character  the {@code char} value to test for presence
	 * <p>
	 * @return {@code true} if {@code characters} contains {@code character}, {@code false}
	 *         otherwise
	 */
	public static boolean contains(final char[] characters, final char character) {
		if (characters == null) {
			return false;
		}
		for (final char c : characters) {
			if (character == c) {
				return true;
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified values for order. Returns a negative integer, zero or a positive
	 * integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a a {@code char} value
	 * @param b another {@code char} value to compare with for order
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
	 * Returns a {@link String} representation of the specified array of {@code char} values.
	 * <p>
	 * @param array an array of {@code char} values
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code char} values
	 */
	public static String toString(final char... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@code char} values joined
	 * by {@code delimiter}.
	 * <p>
	 * @param array     an array of {@code char} values
	 * @param delimiter a {@link String}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code char} values joined
	 *         by {@code delimiter}
	 */
	public static String toString(final char[] array, final String delimiter) {
		return Arrays.toString(toArray(array), delimiter);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@code char} values joined
	 * by {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@code char} values
	 * @param delimiter a {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code char} values joined
	 *         by {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final char[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(toArray(array), delimiter, wrapper);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified array of {@link Character}.
	 * <p>
	 * @param array an array of {@link Character}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Character}
	 */
	public static String toString(final Character... array) {
		return Arrays.toString(array);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Character} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an array of {@link Character}
	 * @param delimiter a {@link String}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Character} joined by
	 *         {@code delimiter}
	 */
	public static String toString(final Character[] array, final String delimiter) {
		return Arrays.toString(array, delimiter);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Character} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@link Character}
	 * @param delimiter a {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Character} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final Character[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(array, delimiter, wrapper);
	}
}
