/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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

import static java.lang.Character.MAX_VALUE;
import static java.lang.Character.MIN_VALUE;
import static jupiter.common.util.Strings.UNICODE;

import java.util.Collection;
import java.util.Comparator;
import java.util.Random;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.CharacterParser;
import jupiter.common.map.parser.IParsers;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.set.ExtendedHashSet;

public class Characters {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final char[] EMPTY_PRIMITIVE_ARRAY = new char[] {};
	public static final char[][] EMPTY_PRIMITIVE_ARRAY_2D = new char[][] {};
	public static final char[][][] EMPTY_PRIMITIVE_ARRAY_3D = new char[][][] {};

	public static final Character[] EMPTY_ARRAY = new Character[] {};
	public static final Character[][] EMPTY_ARRAY_2D = new Character[][] {};
	public static final Character[][][] EMPTY_ARRAY_3D = new Character[][][] {};

	protected static final CharacterParser PARSER = IParsers.CHARACTER_PARSER;

	//////////////////////////////////////////////

	public static volatile Random RANDOM = new Random();

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final char BAR = '|';
	public static final char BULLET = '\u2022'; // '•'
	public static final char COLON = ':';
	public static final char ESCAPE = '\u001B';
	public static final char HYPHEN = '-';
	public static final char POINT = '.';
	public static final char SEMICOLON = ';';
	public static final char SPACE = ' ';

	//////////////////////////////////////////////

	public static final char SINGLE_QUOTE = '\'';
	public static final char DOUBLE_QUOTE = '"';
	public static final char LEFT_QUOTE = '\u2018'; // '‘'
	public static final char RIGHT_QUOTE = '\u2019'; // '’'

	public static final char LEFT_PARENTHESIS = '(';
	public static final char RIGHT_PARENTHESIS = ')';
	public static final char LEFT_BRACKET = '[';
	public static final char RIGHT_BRACKET = ']';
	public static final char LEFT_BRACE = '{';
	public static final char RIGHT_BRACE = '}';

	//////////////////////////////////////////////

	public static final char ZERO = '0';
	public static final char ONE = '1';

	//////////////////////////////////////////////

	/**
	 * The numerical digits.
	 */
	public static final char[] NUMERICAL_DIGITS = createSequence(10, '0');

	/**
	 * The lower case alphabetical digits.
	 */
	public static final char[] LOWER_CASE_ALPHABETICAL_DIGITS = createSequence(26, 'a');
	/**
	 * The upper case alphabetical digits.
	 */
	public static final char[] UPPER_CASE_ALPHABETICAL_DIGITS = createSequence(26, 'A');

	/**
	 * The lower case digits.
	 */
	public static final char[] LOWER_CASE_DIGITS = concat(NUMERICAL_DIGITS,
			LOWER_CASE_ALPHABETICAL_DIGITS);
	/**
	 * The upper case digits.
	 */
	public static final char[] UPPER_CASE_DIGITS = concat(NUMERICAL_DIGITS,
			UPPER_CASE_ALPHABETICAL_DIGITS);

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Comparator<Character> COMPARATOR = new Comparator<Character>() {
		@Override
		public int compare(final Character a, final Character b) {
			return Characters.compare(a, b);
		}
	};

	public static final Comparator<char[]> ARRAY_COMPARATOR = new Comparator<char[]>() {
		@Override
		public int compare(final char[] a, final char[] b) {
			return Characters.compare(a, b);
		}
	};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Characters}.
	 */
	protected Characters() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified {@code char} values for order. Returns a negative integer, {@code 0}
	 * or a positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code char} value to compare for order
	 * @param b the other {@code char} value to compare against for order
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final char a, final char b) {
		return a < b ? -1 : a == b ? 0 : 1;
	}

	//////////////////////////////////////////////

	/**
	 * Compares the specified {@code char} arrays for order. Returns a negative integer, {@code 0}
	 * or a positive integer as {@code a} is less than, equal to or greater than {@code b} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@code char} array to compare for order (may be {@code null})
	 * @param b the other {@code char} array to compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final char[] a, final char[] b) {
		// Check the arguments
		if (a == b) {
			return 0;
		}
		if (a == null) {
			return -1;
		}
		if (b == null) {
			return 1;
		}

		// Compare the arrays for order
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
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Character} converted from the specified {@link Object}.
	 * <p>
	 * @param object the {@link Object} to convert (may be {@code null})
	 * <p>
	 * @return a {@link Character} converted from the specified {@link Object}
	 */
	public static Character convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a Unicode {@link String} converted from the specified {@code char} token.
	 * <p>
	 * @param token the {@code char} token to convert
	 * <p>
	 * @return a Unicode {@link String} converted from the specified {@code char} token
	 */
	public static String toUnicode(final char token) {
		return UNICODE.concat(Strings.leftPad(Integer.toString(token, 16), 4, ZERO));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code char} value converted from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object the {@code T} object to convert
	 * <p>
	 * @return a {@code char} value converted from the specified {@code T} object
	 */
	public static <T> char toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code char} array converted from the specified {@code char} array.
	 * <p>
	 * @param array the {@code char} array to convert
	 * <p>
	 * @return a {@code char} array converted from the specified {@code char} array
	 */
	public static char[] toPrimitiveArray(final char... array) {
		// Check the arguments
		if (array == null) {
			return null;
		}
		if (array.length == 0) {
			return EMPTY_PRIMITIVE_ARRAY;
		}

		// Copy the array to an array
		final char[] output = new char[array.length];
		System.arraycopy(array, 0, output, 0, array.length);
		return output;
	}

	/**
	 * Returns a {@code char} array converted from the specified 2D {@code char} array.
	 * <p>
	 * @param array2D the 2D {@code char} array to convert
	 * <p>
	 * @return a {@code char} array converted from the specified 2D {@code char} array
	 */
	public static char[] toPrimitiveArray(final char[]... array2D) {
		// Check the arguments
		if (array2D == null) {
			return null;
		}
		if (array2D.length == 0 || array2D[0].length == 0) {
			return EMPTY_PRIMITIVE_ARRAY;
		}

		// Copy the 2D array to an array
		final int rowCount = array2D.length;
		final int columnCount = array2D[0].length;
		final char[] output = new char[rowCount * columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array2D[i], 0, output, i * columnCount, columnCount);
		}
		return output;
	}

	/**
	 * Returns a {@code char} array converted from the specified 3D {@code char} array.
	 * <p>
	 * @param array3D the 3D {@code char} array to convert
	 * <p>
	 * @return a {@code char} array converted from the specified 3D {@code char} array
	 */
	public static char[] toPrimitiveArray(final char[][]... array3D) {
		// Check the arguments
		if (array3D == null) {
			return null;
		}
		if (array3D.length == 0 || array3D[0].length == 0 || array3D[0][0].length == 0) {
			return EMPTY_PRIMITIVE_ARRAY;
		}

		// Copy the 3D array to an array
		final int rowCount = array3D.length;
		final int columnCount = array3D[0].length;
		final int depthCount = array3D[0][0].length;
		final char[] output = new char[rowCount * columnCount * depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array3D[i][j], 0, output, (i * columnCount + j) * depthCount,
						depthCount);
			}
		}
		return output;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code char} array converted from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@code char} array converted from the specified {@code T} array
	 */
	public static <T> char[] toPrimitiveArray(final T[] array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a {@code char} array converted from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@code char} array converted from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> char[] asPrimitiveArray(final T... array) {
		return toPrimitiveArray(array);
	}

	/**
	 * Returns a {@code char} array converted from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a {@code char} array converted from the specified 2D {@code T} array
	 */
	public static <T> char[] toPrimitiveArray(final T[][] array2D) {
		return PARSER.callToPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code char} array converted from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a {@code char} array converted from the specified 2D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> char[] asPrimitiveArray(final T[]... array2D) {
		return toPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code char} array converted from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a {@code char} array converted from the specified 3D {@code T} array
	 */
	public static <T> char[] toPrimitiveArray(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray(array3D);
	}

	/**
	 * Returns a {@code char} array converted from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a {@code char} array converted from the specified 3D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> char[] asPrimitiveArray(final T[][]... array3D) {
		return toPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	public static char[][] toPrimitiveArray2D(final char[] array, final int rowCount) {
		// Check the arguments
		if (array == null) {
			return null;
		}
		if (array.length == 0 || rowCount == 0) {
			return EMPTY_PRIMITIVE_ARRAY_2D;
		}

		// Copy the array to a 2D array
		final int columnCount = array.length / rowCount;
		final char[][] output2D = new char[rowCount][columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array, i * columnCount, output2D[i], 0, columnCount);
		}
		return output2D;
	}

	public static char[][] toPrimitiveArray2D(final char[][] array2D) {
		// Check the arguments
		if (array2D == null) {
			return null;
		}
		if (array2D.length == 0 || array2D[0].length == 0) {
			return EMPTY_PRIMITIVE_ARRAY_2D;
		}

		// Copy the 2D array to a 2D array
		final int rowCount = array2D.length;
		final int columnCount = array2D[0].length;
		final char[][] output2D = new char[rowCount][columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array2D[i], 0, output2D[i], 0, columnCount);
		}
		return output2D;
	}

	public static char[][] toPrimitiveArray2D(final char[][][] array3D) {
		// Check the arguments
		if (array3D == null) {
			return null;
		}
		if (array3D.length == 0 || array3D[0].length == 0 || array3D[0][0].length == 0) {
			return EMPTY_PRIMITIVE_ARRAY_2D;
		}

		// Copy the 3D array to a 2D array
		final int rowCount = array3D.length;
		final int columnCount = array3D[0].length;
		final int depthCount = array3D[0][0].length;
		final char[][] output2D = new char[rowCount][columnCount * depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array3D[i][j], 0, output2D[i], j * depthCount, depthCount);
			}
		}
		return output2D;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D {@code char} array converted from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a 2D {@code char} array converted from the specified 2D {@code T} array
	 */
	public static <T> char[][] toPrimitiveArray2D(final T[][] array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 2D {@code char} array converted from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a 2D {@code char} array converted from the specified 2D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> char[][] asPrimitiveArray2D(final T[]... array2D) {
		return toPrimitiveArray2D(array2D);
	}

	//////////////////////////////////////////////

	public static char[][][] toPrimitiveArray3D(final char[] array, final int rowCount,
			final int columnCount) {
		// Check the arguments
		if (array == null) {
			return null;
		}
		if (array.length == 0 || rowCount == 0 || columnCount == 0) {
			return EMPTY_PRIMITIVE_ARRAY_3D;
		}

		// Copy the array to a 3D array
		final int depthCount = array.length / (rowCount * columnCount);
		final char[][][] output3D = new char[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array, (i * columnCount + j) * depthCount, output3D[i][j], 0,
						depthCount);
			}
		}
		return output3D;
	}

	public static char[][][] toPrimitiveArray3D(final char[][] array2D,
			final int columnCount) {
		// Check the arguments
		if (array2D == null) {
			return null;
		}
		if (array2D.length == 0 || array2D[0].length == 0 || columnCount == 0) {
			return EMPTY_PRIMITIVE_ARRAY_3D;
		}

		// Copy the 2D array to a 3D array
		final int rowCount = array2D.length;
		final int depthCount = array2D[0].length / columnCount;
		final char[][][] output3D = new char[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array2D[i], j * depthCount, output3D[i][j], 0, depthCount);
			}
		}
		return output3D;
	}

	public static char[][][] toPrimitiveArray3D(final char[][][] array3D) {
		// Check the arguments
		if (array3D == null) {
			return null;
		}
		if (array3D.length == 0 || array3D[0].length == 0 || array3D[0][0].length == 0) {
			return EMPTY_PRIMITIVE_ARRAY_3D;
		}

		// Copy the 3D array to a 3D array
		final int rowCount = array3D.length;
		final int columnCount = array3D[0].length;
		final int depthCount = array3D[0][0].length;
		final char[][][] output3D = new char[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array3D[i][j], 0, output3D[i][j], 0, depthCount);
			}
		}
		return output3D;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D {@code char} array converted from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a 3D {@code char} array converted from the specified 3D {@code T} array
	 */
	public static <T> char[][][] toPrimitiveArray3D(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a 3D {@code char} array converted from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a 3D {@code char} array converted from the specified 3D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> char[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code char} array converted from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return a {@code char} array converted from the specified {@link Collection}
	 */
	public static char[] collectionToPrimitiveArray(final Collection<?> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Character} converted from the specified {@code char} array.
	 * <p>
	 * @param array the {@code char} array to convert
	 * <p>
	 * @return an array of {@link Character} converted from the specified {@code char} array
	 */
	public static Character[] toArray(final char[] array) {
		final Character[] newArray = new Character[array.length];
		for (int i = 0; i < array.length; ++i) {
			newArray[i] = array[i];
		}
		return newArray;
	}

	/**
	 * Returns an array of {@link Character} converted from the specified {@code char} array.
	 * <p>
	 * @param array the {@code char} array to convert
	 * <p>
	 * @return an array of {@link Character} converted from the specified {@code char} array
	 */
	public static Character[] asArray(final char... array) {
		return toArray(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D array of {@link Character} converted from the specified 2D {@code char} array.
	 * <p>
	 * @param array2D the 2D {@code char} array to convert
	 * <p>
	 * @return a 2D array of {@link Character} converted from the specified 2D {@code char} array
	 */
	public static Character[][] toArray2D(final char[][] array2D) {
		final Character[][] newArray2D = new Character[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			newArray2D[i] = toArray(array2D[i]);
		}
		return newArray2D;
	}

	/**
	 * Returns a 2D array of {@link Character} converted from the specified 2D {@code char} array.
	 * <p>
	 * @param array2D the 2D {@code char} array to convert
	 * <p>
	 * @return a 2D array of {@link Character} converted from the specified 2D {@code char} array
	 */
	public static Character[][] asArray2D(final char[]... array2D) {
		return toArray2D(array2D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D array of {@link Character} converted from the specified 3D {@code char} array.
	 * <p>
	 * @param array3D the 3D {@code char} array to convert
	 * <p>
	 * @return a 3D array of {@link Character} converted from the specified 3D {@code char} array
	 */
	public static Character[][][] toArray3D(final char[][][] array3D) {
		final Character[][][] newArray3D = new Character[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			newArray3D[i] = toArray2D(array3D[i]);
		}
		return newArray3D;
	}

	/**
	 * Returns a 3D array of {@link Character} converted from the specified 3D {@code char} array.
	 * <p>
	 * @param array3D the 3D {@code char} array to convert
	 * <p>
	 * @return a 3D array of {@link Character} converted from the specified 3D {@code char} array
	 */
	public static Character[][][] asArray3D(final char[][]... array3D) {
		return toArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an array of {@link Character} converted from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an array of {@link Character} converted from the specified {@link Collection}
	 */
	public static Character[] collectionToArray(final Collection<?> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Character} converted from the specified
	 * {@code char} array.
	 * <p>
	 * @param array the {@code char} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Character} converted from the specified
	 *         {@code char} array
	 */
	public static ExtendedList<Character> toList(final char[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Character} converted from the specified
	 * {@code char} array.
	 * <p>
	 * @param array the {@code char} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Character} converted from the specified
	 *         {@code char} array
	 */
	public static ExtendedList<Character> asList(final char... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Character} converted from the specified
	 * {@code char} array.
	 * <p>
	 * @param array the {@code char} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Character} converted from the specified
	 *         {@code char} array
	 */
	public static ExtendedLinkedList<Character> toLinkedList(final char[] array) {
		return PARSER.callToLinkedList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Character} converted from the specified
	 * {@code char} array.
	 * <p>
	 * @param array the {@code char} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Character} converted from the specified
	 *         {@code char} array
	 */
	public static ExtendedLinkedList<Character> asLinkedList(final char... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Character} converted from the specified {@code T}
	 * array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Character} converted from the specified {@code T}
	 *         array
	 */
	public static <T> ExtendedList<Character> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Character} converted from the specified {@code T}
	 * array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Character} converted from the specified {@code T}
	 *         array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedList<Character> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Character} converted from the specified
	 * {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Character} converted from the specified
	 *         {@code T} array
	 */
	public static <T> ExtendedLinkedList<Character> toLinkedList(final T[] array) {
		return PARSER.callToLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Character} converted from the specified
	 * {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Character} converted from the specified
	 *         {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedLinkedList<Character> asLinkedList(final T... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Character} converted from the specified
	 * {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Character} converted from the specified
	 *         {@link Collection}
	 */
	public static ExtendedList<Character> collectionToList(final Collection<?> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Character} converted from the specified
	 * {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Character} converted from the specified
	 *         {@link Collection}
	 */
	public static ExtendedLinkedList<Character> collectionToLinkedList(
			final Collection<?> collection) {
		return PARSER.callCollectionToLinkedList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Character} converted from the specified
	 * {@code char} array.
	 * <p>
	 * @param array the {@code char} array to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Character} converted from the specified
	 *         {@code char} array
	 */
	public static ExtendedHashSet<Character> toSet(final char[] array) {
		return PARSER.callToSet(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Character} converted from the specified
	 * {@code char} array.
	 * <p>
	 * @param array the {@code char} array to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Character} converted from the specified
	 *         {@code char} array
	 */
	public static ExtendedHashSet<Character> asSet(final char... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Character} converted from the specified
	 * {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Character} converted from the specified
	 *         {@code T} array
	 */
	public static <T> ExtendedHashSet<Character> toSet(final T[] array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Character} converted from the specified
	 * {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Character} converted from the specified
	 *         {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedHashSet<Character> asSet(final T... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Character} converted from the specified
	 * {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Character} converted from the specified
	 *         {@link Collection}
	 */
	public static ExtendedHashSet<Character> collectionToSet(final Collection<?> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code char} array of the specified length containing the sequence of {@code char}
	 * values starting with {@code 0} and spaced by {@code 1}.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return a {@code char} array of the specified length containing the sequence of {@code char}
	 *         values starting with {@code 0} and spaced by {@code 1}
	 */
	public static char[] createSequence(final int length) {
		return createSequence(length, MIN_VALUE, '\u0001');
	}

	/**
	 * Creates a {@code char} array of the specified length containing the sequence of {@code char}
	 * values starting with {@code from} and spaced by {@code 1}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return a {@code char} array of the specified length containing the sequence of {@code char}
	 *         values starting with {@code from} and spaced by {@code 1}
	 */
	public static char[] createSequence(final int length, final char from) {
		return createSequence(length, from, '\u0001');
	}

	/**
	 * Creates a {@code char} array of the specified length containing the sequence of {@code char}
	 * values starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return a {@code char} array of the specified length containing the sequence of {@code char}
	 *         values starting with {@code from} and spaced by {@code step}
	 */
	public static char[] createSequence(final int length, final char from, final char step) {
		final char[] array = new char[length];
		char value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	//////////////////////////////////////////////

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
	 * distributed {@code char} values between the specified bounds.
	 * <p>
	 * @param length the length of the random sequence to create
	 * @param from   the {@code char} lower bound of the random sequence to create (inclusive)
	 * @param to     the {@code char} upper bound of the random sequence to create (exclusive)
	 * <p>
	 * @return a {@code char} array of the specified length containing pseudorandom, uniformly
	 *         distributed {@code char} values between the specified bounds
	 */
	public static char[] createRandomSequence(final int length, final char from, final char to) {
		final char[] array = new char[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(from, to);
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code char} value.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code char} value
	 */
	public static char random() {
		return (char) (MIN_VALUE + RANDOM.nextInt(MAX_VALUE + 1));
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code char} value between the specified
	 * bounds.
	 * <p>
	 * @param from the {@code char} lower bound of the value to generate (inclusive)
	 * @param to   the {@code char} upper bound of the value to generate (exclusive)
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code char} value between the specified bounds
	 */
	public static char random(final char from, final char to) {
		return (char) (from + RANDOM.nextInt(to - from));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code char} array of the specified length with the specified {@code char} element.
	 * <p>
	 * @param element the {@code char} element of the {@code char} array to create
	 * @param length  the length of the {@code char} array to create
	 * <p>
	 * @return a {@code char} array of the specified length with the specified {@code char} element
	 */
	public static char[] repeat(final char element, final int length) {
		return fill(new char[length], element);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code char} array containing the specified {@code char} value and all the elements
	 * of the specified {@code char} array.
	 * <p>
	 * @param a a {@code char} value
	 * @param b another {@code char} array (may be {@code null})
	 * <p>
	 * @return a {@code char} array containing the specified {@code char} value and all the elements
	 *         of the specified {@code char} array
	 */
	public static char[] concat(final char a, final char... b) {
		return concat(asPrimitiveArray(a), b);
	}

	/**
	 * Returns a {@code char} array containing all the elements of the specified {@code char}
	 * arrays.
	 * <p>
	 * @param a a {@code char} array (may be {@code null})
	 * @param b another {@code char} array (may be {@code null})
	 * <p>
	 * @return a {@code char} array containing all the elements of the specified {@code char} arrays
	 */
	public static char[] concat(final char[] a, final char... b) {
		// Check the arguments
		if (a == null) {
			return clone(b);
		} else if (b == null) {
			return clone(a);
		}

		// Concatenate the arrays
		final char[] newArray = new char[a.length + b.length];
		System.arraycopy(a, 0, newArray, 0, a.length);
		System.arraycopy(b, 0, newArray, a.length, b.length);
		return newArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of elements in the specified 2D {@code char} array.
	 * <p>
	 * @param array2D the 2D {@code char} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 2D {@code char} array
	 */
	public static int count(final char[][] array2D) {
		int count = 0;
		if (array2D != null) {
			for (final char[] array : array2D) {
				count += array.length;
			}
		}
		return count;
	}

	/**
	 * Returns the number of elements in the specified 3D {@code char} array.
	 * <p>
	 * @param array3D the 3D {@code char} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 3D {@code char} array
	 */
	public static int count(final char[][][] array3D) {
		int count = 0;
		if (array3D != null) {
			for (final char[][] array2D : array3D) {
				count += count(array2D);
			}
		}
		return count;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code char} token in the specified
	 * {@code char} array.
	 * <p>
	 * @param array the {@code char} array to count from (may be {@code null})
	 * @param token the {@code char} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code char} token in the specified
	 *         {@code char} array
	 */
	public static int count(final char[] array, final char token) {
		int occurrenceCount = 0;
		if (array != null) {
			int index = -1;
			while ((index = findFirstIndex(array, token, index + 1)) >= 0) {
				++occurrenceCount;
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code char} token in the specified 2D
	 * {@code char} array.
	 * <p>
	 * @param array2D the 2D {@code char} array to count from (may be {@code null})
	 * @param token   the {@code char} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code char} token in the specified 2D
	 *         {@code char} array
	 */
	public static int count(final char[][] array2D, final char token) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final char[] array : array2D) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code char} token in the specified 3D
	 * {@code char} array.
	 * <p>
	 * @param array3D the 3D {@code char} array to count from (may be {@code null})
	 * @param token   the {@code char} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code char} token in the specified 3D
	 *         {@code char} array
	 */
	public static int count(final char[][][] array3D, final char token) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final char[][] array2D : array3D) {
				occurrenceCount += count(array2D, token);
			}
		}
		return occurrenceCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code char} tokens in the specified
	 * {@code char} array.
	 * <p>
	 * @param array  the {@code char} array to count from (may be {@code null})
	 * @param tokens the {@code char} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code char} tokens in the specified
	 *         {@code char} array
	 */
	public static int count(final char[] array, final char... tokens) {
		int occurrenceCount = 0;
		if (array != null && tokens != null) {
			for (final char token : tokens) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code char} tokens in the specified 2D
	 * {@code char} array.
	 * <p>
	 * @param array2D the 2D {@code char} array to count from (may be {@code null})
	 * @param tokens  the {@code char} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code char} tokens in the specified 2D
	 *         {@code char} array
	 */
	public static int count(final char[][] array2D, final char... tokens) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final char[] array : array2D) {
				occurrenceCount += count(array, tokens);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code char} tokens in the specified 3D
	 * {@code char} array.
	 * <p>
	 * @param array3D the 3D {@code char} array to count from (may be {@code null})
	 * @param tokens  the {@code char} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code char} tokens in the specified 3D
	 *         {@code char} array
	 */
	public static int count(final char[][][] array3D, final char... tokens) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final char[][] array2D : array3D) {
				occurrenceCount += count(array2D, tokens);
			}
		}
		return occurrenceCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of lower case {@code char} tokens in the specified {@code char} array.
	 * <p>
	 * @param array a {@code char} array
	 * <p>
	 * @return the number of lower case {@code char} tokens in the specified {@code char} array
	 */
	public static int countLowerCase(final char... array) {
		int occurrenceCount = 0;
		for (final char token : array) {
			if (Character.isLowerCase(token)) {
				++occurrenceCount;
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of upper case {@code char} tokens in the specified {@code char} array.
	 * <p>
	 * @param array a {@code char} array
	 * <p>
	 * @return the number of upper case {@code char} tokens in the specified {@code char} array
	 */
	public static int countUpperCase(final char... array) {
		int occurrenceCount = 0;
		for (final char token : array) {
			if (Character.isUpperCase(token)) {
				++occurrenceCount;
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of title case {@code char} tokens in the specified {@code char} array.
	 * <p>
	 * @param array a {@code char} array
	 * <p>
	 * @return the number of title case {@code char} tokens in the specified {@code char} array
	 */
	public static int countTitleCase(final char... array) {
		int occurrenceCount = 0;
		for (final char token : array) {
			if (Character.isTitleCase(token)) {
				++occurrenceCount;
			}
		}
		return occurrenceCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static char[] fill(final char[] array, final char value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
		return array;
	}

	public static char[][] fill(final char[][] array2D, final char value) {
		for (final char[] array : array2D) {
			fill(array, value);
		}
		return array2D;
	}

	public static char[][][] fill(final char[][][] array3D, final char value) {
		for (final char[][] array2D : array3D) {
			fill(array2D, value);
		}
		return array3D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code char} array containing all the elements of the specified {@code char} array
	 * at the specified indices.
	 * <p>
	 * @param array   the {@code char} array to filter from
	 * @param indices the indices to filter
	 * <p>
	 * @return a {@code char} array containing all the elements of the specified {@code char} array
	 *         at the specified indices
	 */
	public static char[] filter(final char[] array, final int... indices) {
		final char[] newArray = new char[indices.length];
		for (int i = 0; i < indices.length; ++i) {
			newArray[i] = array[indices[i]];
		}
		return newArray;
	}

	/**
	 * Returns a 2D {@code char} array containing all the elements of the specified {@code char}
	 * array at all the specified indices.
	 * <p>
	 * @param array   the {@code char} array to filter from
	 * @param indices the array of indices to filter
	 * <p>
	 * @return a 2D {@code char} array containing all the elements of the specified {@code char}
	 *         array at all the specified indices
	 */
	public static char[][] filterAll(final char[] array, final int[]... indices) {
		final char[][] newArray2D = new char[indices.length][];
		for (int i = 0; i < indices.length; ++i) {
			newArray2D[i] = filter(array, indices[i]);
		}
		return newArray2D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the element at the specified index from the specified {@code char} array.
	 * <p>
	 * @param array the {@code char} array to remove from
	 * @param index the index of the element to remove
	 * <p>
	 * @return the specified {@code char} array without the element at the specified index
	 */
	public static char[] remove(final char[] array, final int index) {
		final char[] newArray = new char[array.length - 1];
		System.arraycopy(array, 0, newArray, 0, index);
		System.arraycopy(array, index + 1, newArray, index, array.length - index - 1);
		return newArray;
	}

	/**
	 * Removes all the occurrences of the specified {@code char} value from the specified
	 * {@code char} array.
	 * <p>
	 * @param array the {@code char} array to remove from
	 * @param value the {@code char} value to remove (may be {@code null})
	 * <p>
	 * @return the specified {@code char} array without the specified {@code char} value
	 */
	public static char[] removeAll(final char[] array, final char value) {
		final char[] newArray = new char[array.length - count(array, value)];
		int index = 0;
		for (int i = 0; i < array.length; ++i) {
			if (array[i] != value) {
				newArray[index++] = array[i];
			}
		}
		return newArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void reverse(final char... array) {
		reverse(array, 0, array.length);
	}

	public static void reverse(final char[] array, final int fromIndex, final int toIndex) {
		final int limit = Integers.middle(toIndex - fromIndex);
		for (int i = 0; i < limit; ++i) {
			swap(array, fromIndex + i, toIndex - 1 - i);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shuffles the specified {@code char} array.
	 * <p>
	 * @param array the {@code char} array to shuffle
	 */
	public static void shuffle(final char... array) {
		shuffle(array, 0, array.length);
	}

	/**
	 * Shuffles the specified {@code char} array between the specified indices.
	 * <p>
	 * @param array     the {@code char} array to shuffle
	 * @param fromIndex the index to start shuffling from (inclusive)
	 * @param toIndex   the index to finish shuffling at (exclusive)
	 */
	public static void shuffle(final char[] array, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			swap(array, i, Integers.random(fromIndex, toIndex));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void swap(final char[] array, final int i, final int j) {
		final char element = array[i];
		array[i] = array[j];
		array[j] = element;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static char[] take(final char[] array, final int fromIndex, final int length) {
		if (isNullOrEmpty(array)) {
			return array;
		}
		final int maxLength = Math.min(length, array.length - fromIndex);
		final char[] subarray = new char[maxLength];
		System.arraycopy(array, fromIndex, subarray, 0, maxLength);
		return subarray;
	}

	//////////////////////////////////////////////

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
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, subarray,
					i * columnCount, columnCount);
		}
		return subarray;
	}

	//////////////////////////////////////////////

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

	public static int tally(final char value, final char[] boundaries) {
		for (int i = 0; i < boundaries.length; ++i) {
			if (value < boundaries[i]) {
				return i;
			}
		}
		return boundaries.length;
	}

	public static int[] tally(final char[] array, final char[] boundaries) {
		final int[] intervals = new int[array.length];
		for (int i = 0; i < array.length; ++i) {
			intervals[i] = tally(array[i], boundaries);
		}
		return intervals;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified {@code char} array.
	 * <p>
	 * @param rowCount the number of rows of the {@code char} array
	 * @param array    a {@code char} array
	 * <p>
	 * @return the transpose of the specified {@code char} array
	 */
	public static char[] transpose(final int rowCount, final char... array) {
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
	 * @param array2D the 2D {@code char} array to convert
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
	// SEEKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int findFirstIndex(final char[] array, final char token) {
		if (array != null) {
			return findFirstIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findFirstIndex(final char[] array, final char token, final int from) {
		if (array != null) {
			return findFirstIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static int findFirstIndex(final char[] array, final char token, final int from,
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

	public static int findLastIndex(final char[] array, final char token) {
		if (array != null) {
			return findLastIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findLastIndex(final char[] array, final char token, final int from) {
		if (array != null) {
			return findLastIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static int findLastIndex(final char[] array, final char token, final int from,
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
	 * Tests whether the specified {@link Object} is an instance of {@link Character}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Character},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Character;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code char} value or a
	 * {@link Character}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code char} value or
	 *         a {@link Character}, {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
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
	public static boolean isPrimitiveFrom(final Class<?> c) {
		return char.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Object} is an instance of {@code char} array.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@code char} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArray(final Object object) {
		return object instanceof char[];
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code char} array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code char} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArrayFrom(final Class<?> c) {
		return char[].class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code char} array is {@code null} or empty.
	 * <p>
	 * @param array the {@code char} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code char} array is {@code null} or empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final char[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Tests whether the specified {@code char} array is non-{@code null} and empty.
	 * <p>
	 * @param array the {@code char} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code char} array is non-{@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final char[] array) {
		return array != null && array.length == 0;
	}

	/**
	 * Tests whether the specified {@code char} array is non-{@code null} and non-empty.
	 * <p>
	 * @param array the {@code char} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code char} array is non-{@code null} and non-empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNonEmpty(final char[] array) {
		return array != null && array.length > 0;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code char} token is a parenthesis.
	 * <p>
	 * @param token the {@code char} token to test
	 * <p>
	 * @return {@code true} if the specified {@code char} token is a parenthesis, {@code false}
	 *         otherwise
	 */
	public static boolean isParenthesis(final char token) {
		return token == LEFT_PARENTHESIS || token == RIGHT_PARENTHESIS;
	}

	/**
	 * Tests whether the specified {@code char} token is a bracket.
	 * <p>
	 * @param token the {@code char} token to test
	 * <p>
	 * @return {@code true} if the specified {@code char} token is a bracket, {@code false}
	 *         otherwise
	 */
	public static boolean isBracket(final char token) {
		return token == LEFT_BRACKET || token == RIGHT_BRACKET;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code char} value is between the specified {@code char} lower
	 * and upper bounds.
	 * <p>
	 * @param value the {@code char} value to test
	 * @param from  the {@code char} lower bound to test against (inclusive)
	 * @param to    the {@code char} upper bound to test against (exclusive)
	 * <p>
	 * @return {@code true} if the specified {@code char} value is between the specified
	 *         {@code char} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final char value, final char from, final char to) {
		return isBetween(value, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@code char} value is between the specified {@code char} lower
	 * and upper bounds.
	 * <p>
	 * @param value            the {@code char} value to test
	 * @param from             the {@code char} lower bound to test against (inclusive)
	 * @param to               the {@code char} upper bound to test against
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code char} value is between the specified
	 *         {@code char} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final char value, final char from, final char to,
			final boolean isUpperInclusive) {
		return isBetween(value, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code char} value is between the specified {@code char} lower
	 * and upper bounds.
	 * <p>
	 * @param value            the {@code char} value to test
	 * @param from             the {@code char} lower bound to test against
	 * @param to               the {@code char} upper bound to test against
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code char} value is between the specified
	 *         {@code char} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final char value, final char from, final char to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return (isLowerInclusive ? value >= from : value > from) &&
				(isUpperInclusive ? value <= to : value < to);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code char} array is between the specified lower and upper bound
	 * {@code char} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array the {@code char} array to test (may be {@code null})
	 * @param from  the lower bound {@code char} array to test against (inclusive) (may be
	 *              {@code null})
	 * @param to    the upper bound {@code char} array to test against (exclusive) (may be
	 *              {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code char} array is between the specified lower and
	 *         upper bound {@code char} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final char[] array, final char[] from, final char[] to) {
		return isBetween(array, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@code char} array is between the specified lower and upper bound
	 * {@code char} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array            the {@code char} array to test (may be {@code null})
	 * @param from             the lower bound {@code char} array to test against (inclusive) (may
	 *                         be {@code null})
	 * @param to               the upper bound {@code char} array to test against (may be
	 *                         {@code null})
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code char} array is between the specified lower and
	 *         upper bound {@code char} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final char[] array, final char[] from, final char[] to,
			final boolean isUpperInclusive) {
		return isBetween(array, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code char} array is between the specified lower and upper bound
	 * {@code char} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array            the {@code char} array to test (may be {@code null})
	 * @param from             the lower bound {@code char} array to test against (may be
	 *                         {@code null})
	 * @param to               the upper bound {@code char} array to test against (may be
	 *                         {@code null})
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code char} array is between the specified lower and
	 *         upper bound {@code char} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final char[] array, final char[] from, final char[] to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return (isLowerInclusive ? compare(array, from) >= 0 : compare(array, from) > 0) &&
				(isUpperInclusive ? compare(array, to) <= 0 : compare(array, to) < 0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code char} array contains the specified {@code char} token.
	 * <p>
	 * @param array the {@code char} array to test (may be {@code null})
	 * @param token the {@code char} token to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code char} array contains the specified {@code char}
	 *         token, {@code false} otherwise
	 */
	public static boolean contains(final char[] array, final char token) {
		return findFirstIndex(array, token) >= 0;
	}

	/**
	 * Tests whether the specified {@code char} array contains any of the specified {@code char}
	 * tokens.
	 * <p>
	 * @param array  the {@code char} array to test (may be {@code null})
	 * @param tokens the {@code char} tokens to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code char} array contains any of the specified
	 *         {@code char} tokens, {@code false} otherwise
	 */
	public static boolean containsAny(final char[] array, final char... tokens) {
		if (array != null) {
			for (final char token : tokens) {
				if (contains(array, token)) {
					return true;
				}
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a clone of the specified {@code char} array, or {@code null} if it is {@code null}.
	 * <p>
	 * @param array the {@code char} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified {@code char} array, or {@code null} if it is {@code null}
	 */
	public static char[] clone(final char... array) {
		// Check the arguments
		if (array == null) {
			return null;
		}

		// Clone the array
		return array.clone();
	}

	/**
	 * Clones the specified 2D {@code char} array.
	 * <p>
	 * @param array2D the 2D {@code char} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified 2D {@code char} array, or {@code null} if it is {@code null}
	 */
	public static char[][] clone(final char[]... array2D) {
		// Check the arguments
		if (array2D == null) {
			return null;
		}

		// Clone the 2D array
		final char[][] clone = new char[array2D.length]
				[array2D.length > 0 ? array2D[0].length : 0];
		for (int i = 0; i < array2D.length; ++i) {
			clone[i] = clone(array2D[i]);
		}
		return clone;
	}

	/**
	 * Clones the specified 3D {@code char} array.
	 * <p>
	 * @param array3D the 3D {@code char} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified 3D {@code char} array, or {@code null} if it is {@code null}
	 */
	public static char[][][] clone(final char[][][] array3D) {
		// Check the arguments
		if (array3D == null) {
			return null;
		}

		// Clone the 3D array
		final char[][][] clone = new char[array3D.length]
				[array3D.length > 0 ? array3D[0].length : 0]
				[array3D[0].length > 0 ? array3D[0][0].length : 0];
		for (int i = 0; i < array3D.length; ++i) {
			clone[i] = clone(array3D[i]);
		}
		return clone;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the {@code char} array to compare for equality (may be {@code null})
	 * @param b the other {@code char} array to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final char[] a, final char[] b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null || a.length != b.length) {
			return false;
		}
		final int length = a.length; // or b.length
		for (int i = 0; i < length; ++i) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the 2D {@code char} array to compare for equality (may be {@code null})
	 * @param b the other 2D {@code char} array to compare against for equality (may be
	 *          {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final char[][] a, final char[][] b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null || a.length != b.length) {
			return false;
		}
		final int length = a.length; // or b.length
		for (int i = 0; i < length; ++i) {
			if (!equals(a[i], b[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the 3D {@code char} array to compare for equality (may be {@code null})
	 * @param b the other 3D {@code char} array to compare against for equality (may be
	 *          {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final char[][][] a, final char[][][] b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null || a.length != b.length) {
			return false;
		}
		final int length = a.length; // or b.length
		for (int i = 0; i < length; ++i) {
			if (!equals(a[i], b[i])) {
				return false;
			}
		}
		return true;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the hash code value for the specified {@code char} array.
	 * <p>
	 * @param array the {@code char} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code char} array
	 */
	public static int hashCode(final char... array) {
		return hashCodeWith(0, array);
	}

	/**
	 * Returns the hash code value for the specified {@code char} array at the specified depth.
	 * <p>
	 * @param depth the depth to hash at
	 * @param array the {@code char} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code char} array at the specified depth
	 */
	public static int hashCodeWith(final int depth, final char... array) {
		if (array == null) {
			return 0;
		}
		switch (array.length) {
			case 0:
				return Bits.SEEDS[depth % Bits.SEEDS.length];
			case 1:
				return array[0];
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
	 * Returns a representative {@link String} of the specified {@code char} array.
	 * <p>
	 * @param array the {@code char} array to convert
	 * <p>
	 * @return a representative {@link String} of the specified {@code char} array
	 */
	public static String toString(final char... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a representative {@link String} of the specified {@code char} array joined with the
	 * specified {@code char} delimiter.
	 * <p>
	 * @param array     a {@code char} array
	 * @param delimiter the {@code char} delimiter
	 * <p>
	 * @return a representative {@link String} of the specified {@code char} array joined with the
	 *         specified {@code char} delimiter
	 */
	public static String toStringWith(final char[] array, final char delimiter) {
		return Arrays.toStringWith(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code char} array joined with the
	 * specified delimiting {@link String}.
	 * <p>
	 * @param array     a {@code char} array
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@code char} array joined with the
	 *         specified delimiting {@link String}
	 */
	public static String toStringWith(final char[] array, final String delimiter) {
		return Arrays.toStringWith(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code char} array wrapped by
	 * {@code wrapper}.
	 * <p>
	 * @param array   a {@code char} array
	 * @param wrapper an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code char} array wrapped by
	 *         {@code wrapper}
	 */
	public static String toStringWith(final char[] array, final ObjectToStringMapper wrapper) {
		return Arrays.toStringWith(toArray(array), wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code char} array joined with the
	 * specified delimiting {@link String} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     a {@code char} array
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code char} array joined with the
	 *         specified delimiting {@link String} and wrapped by {@code wrapper}
	 */
	public static String toStringWith(final char[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toStringWith(toArray(array), delimiter, wrapper);
	}
}
