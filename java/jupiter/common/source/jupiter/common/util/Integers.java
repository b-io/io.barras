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
package jupiter.common.util;

import static jupiter.common.util.Characters.LOWER_CASE_DIGITS;
import static jupiter.common.util.Characters.UPPER_CASE_DIGITS;

import java.util.Collection;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.IntegerParser;
import jupiter.common.math.Maths;
import jupiter.common.struct.list.ExtendedLinkedList;
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

	public static final Comparator<Integer> COMPARATOR = new Comparator<Integer>() {
		@Override
		public int compare(final Integer a, final Integer b) {
			return Integers.compare(a, b);
		}
	};

	public static final Comparator<int[]> ARRAY_COMPARATOR = new Comparator<int[]>() {
		@Override
		public int compare(final int[] a, final int[] b) {
			return Integers.compare(a, b);
		}
	};


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
	 * Converts the specified source {@code int} value to a representative unsigned {@code int}
	 * value written to the specified target {@code char} buffer and returns the lowest index of the
	 * specified target {@code char} buffer used.
	 * <p>
	 * @param source the source {@code int} value
	 * @param shift  the log2 of the base to format in (4 for hex, 3 for octal, 1 for binary)
	 * @param target the target {@code char} buffer
	 * @param offset the offset in the target {@code char} buffer to start at
	 * @param length the number of digits to write
	 * <p>
	 * @return the lowest index of the specified target {@code char} buffer used
	 */
	public static int toUnsignedInt(final int source, final int shift, final char[] target,
			final int offset, final int length) {
		return toUnsignedInt(source, shift, target, offset, length, false);
	}

	/**
	 * Converts the specified source {@code int} value to a representative unsigned {@code int}
	 * value written to the specified target {@code char} buffer and returns the lowest index of the
	 * specified target {@code char} buffer used.
	 * <p>
	 * @param source       the source {@code int} value
	 * @param shift        the log2 of the base to format in (4 for hex, 3 for octal, 1 for binary)
	 * @param target       the target {@code char} buffer
	 * @param offset       the offset in the target {@code char} buffer to start at
	 * @param length       the number of digits to write
	 * @param useLowerCase the flag specifying whether to use lower or upper case digits
	 * <p>
	 * @return the lowest index of the specified target {@code char} buffer used
	 */
	public static int toUnsignedInt(final int source, final int shift, final char[] target,
			final int offset, final int length, final boolean useLowerCase) {
		return toUnsignedInt(source, shift, target, offset, length,
				useLowerCase ? LOWER_CASE_DIGITS : UPPER_CASE_DIGITS);
	}

	/**
	 * Converts the specified source {@code int} value to a representative unsigned {@code int}
	 * value written to the specified target {@code char} buffer and returns the lowest index of the
	 * specified target {@code char} buffer used.
	 * <p>
	 * @param source the source {@code int} value
	 * @param shift  the log2 of the base to format in (4 for hex, 3 for octal, 1 for binary)
	 * @param target the target {@code char} buffer
	 * @param offset the offset in the target {@code char} buffer to start at
	 * @param length the number of digits to write
	 * @param digits the digits to use
	 * <p>
	 * @return the lowest index of the specified target {@code char} buffer used
	 */
	public static int toUnsignedInt(final int source, final int shift, final char[] target,
			final int offset, final int length, final char[] digits) {
		// Initialize
		final int radix = Maths.pow2(shift);
		final int mask = radix - 1;

		// Convert the source int to a representative unsigned int written to the target char buffer
		int index = length, value = source;
		do {
			target[offset + --index] = digits[value & mask];
			value >>>= shift;
		} while (value != 0 && index > 0);
		return index;
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@code int} value converted from the specified representative {@link String} of
	 * the specified radix.
	 * <p>
	 * @param text  the representative {@link String} to convert
	 * @param radix the radix of the representative {@link String} to convert
	 * <p>
	 * @return an {@code int} value converted from the specified representative {@link String} of
	 *         the specified radix
	 */
	public static int parseUnsignedInt(final String text, final int radix)
			throws NumberFormatException {
		// Check the arguments
		if (Strings.isNullOrEmpty(text) || text.charAt(0) == '-') {
			throw new NumberFormatException(Strings.join("Cannot parse ", Strings.quote(text),
					" to an unsigned int value"));
		}

		// Parse the text
		final int length = text.length();
		if (length <= 5 || // Integer.MAX_VALUE in Character.MAX_RADIX is 6 digits
				radix == 10 && length <= 9) { // Integer.MAX_VALUE in base 10 is 10 digits
			return Integer.parseInt(text, radix);
		} else {
			final long value = Long.parseLong(text, radix);
			if ((value & 0xffffffff00000000L) == 0) {
				return (int) value;
			} else {
				throw new NumberFormatException(Strings.join("Cannot parse ", Strings.quote(text),
						" to an unsigned int value (range exceeded)"));
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@code int} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object the {@code T} object to convert
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
	 * @param <T>   the component type of the array to convert
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
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@code int} array from the specified {@code T} array
	 */
	@SuppressWarnings("unchecked")
	public static <T> int[] asPrimitiveArray(final T... array) {
		return toPrimitiveArray(array);
	}

	/**
	 * Returns an {@code int} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
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
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return an {@code int} array from the specified 2D {@code T} array
	 */
	@SuppressWarnings("unchecked")
	public static <T> int[] asPrimitiveArray(final T[]... array2D) {
		return toPrimitiveArray(array2D);
	}

	/**
	 * Returns an {@code int} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
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
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return an {@code int} array from the specified 3D {@code T} array
	 */
	@SuppressWarnings("unchecked")
	public static <T> int[] asPrimitiveArray(final T[][]... array3D) {
		return toPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D {@code int} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
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
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a 2D {@code int} array from the specified 2D {@code T} array
	 */
	@SuppressWarnings("unchecked")
	public static <T> int[][] asPrimitiveArray2D(final T[]... array2D) {
		return toPrimitiveArray2D(array2D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D {@code int} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
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
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code int} array from the specified 3D {@code T} array
	 */
	@SuppressWarnings("unchecked")
	public static <T> int[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@code int} array from the specified {@link Collection} of element type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an {@code int} array from the specified {@link Collection} of element type {@code E}
	 */
	public static <E> int[] collectionToPrimitiveArray(final Collection<E> collection) {
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
		final Integer[] convertedArray = new Integer[array.length];
		for (int i = 0; i < array.length; ++i) {
			convertedArray[i] = array[i];
		}
		return convertedArray;
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

	//////////////////////////////////////////////

	/**
	 * Returns a 2D array of {@link Integer} from the specified 2D {@code int} array.
	 * <p>
	 * @param array2D a 2D {@code int} array
	 * <p>
	 * @return a 2D array of {@link Integer} from the specified 2D {@code int} array
	 */
	public static Integer[][] toArray2D(final int[][] array2D) {
		final Integer[][] convertedArray2D = new Integer[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			convertedArray2D[i] = toArray(array2D[i]);
		}
		return convertedArray2D;
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

	//////////////////////////////////////////////

	/**
	 * Returns a 3D array of {@link Integer} from the specified 3D {@code int} array.
	 * <p>
	 * @param array3D a 3D {@code int} array
	 * <p>
	 * @return a 3D array of {@link Integer} from the specified 3D {@code int} array
	 */
	public static Integer[][][] toArray3D(final int[][][] array3D) {
		final Integer[][][] convertedArray3D = new Integer[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			convertedArray3D[i] = toArray2D(array3D[i]);
		}
		return convertedArray3D;
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

	//////////////////////////////////////////////

	/**
	 * Returns an array of {@link Integer} from the specified {@link Collection} of element type
	 * {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an array of {@link Integer} from the specified {@link Collection} of element type
	 *         {@code E}
	 */
	public static <E> Integer[] collectionToArray(final Collection<E> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Integer} from the specified {@code int} array.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Integer} from the specified {@code int} array
	 */
	public static ExtendedList<Integer> toList(final int[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Integer} from the specified {@code int} array.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Integer} from the specified {@code int} array
	 */
	public static ExtendedList<Integer> asList(final int... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Integer} from the specified {@code int}
	 * array.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Integer} from the specified {@code int} array
	 */
	public static ExtendedLinkedList<Integer> toLinkedList(final int[] array) {
		return PARSER.callToLinkedList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Integer} from the specified {@code int}
	 * array.
	 * <p>
	 * @param array an {@code int} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Integer} from the specified {@code int} array
	 */
	public static ExtendedLinkedList<Integer> asLinkedList(final int... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Integer} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Integer} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Integer> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Integer} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Integer} from the specified {@code T} array
	 */
	@SuppressWarnings("unchecked")
	public static <T> ExtendedList<Integer> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Integer} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Integer} from the specified {@code T} array
	 */
	public static <T> ExtendedLinkedList<Integer> toLinkedList(final T[] array) {
		return PARSER.callToLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Integer} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Integer} from the specified {@code T} array
	 */
	@SuppressWarnings("unchecked")
	public static <T> ExtendedLinkedList<Integer> asLinkedList(final T... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Integer} from the specified {@link Collection} of
	 * type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Integer} from the specified {@link Collection} of
	 *         type {@code E}
	 */
	public static <E> ExtendedList<Integer> collectionToList(final Collection<E> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Integer} from the specified
	 * {@link Collection} of element type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Integer} from the specified
	 *         {@link Collection} of element type {@code E}
	 */
	public static <E> ExtendedLinkedList<Integer> collectionToLinkedList(
			final Collection<E> collection) {
		return PARSER.callCollectionToLinkedList(collection);
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

	//////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Integer} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
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
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Integer} from the specified {@code T} array
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<Integer> asSet(final T... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Integer} from the specified {@link Collection} of element
	 * type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return a {@link Set} of {@link Integer} from the specified {@link Collection} of element
	 *         type {@code E}
	 */
	public static <E> Set<Integer> collectionToSet(final Collection<E> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
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

	//////////////////////////////////////////////

	/**
	 * Creates a random {@code int} array of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return a random {@code int} array of the specified length
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
	 * distributed {@code int} values between the specified bounds.
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create (inclusive)
	 * @param upperBound the upper bound of the random sequence to create (exclusive)
	 * <p>
	 * @return an {@code int} array of the specified length containing pseudorandom, uniformly
	 *         distributed {@code int} values between the specified bounds
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

	/**
	 * Returns a pseudorandom, uniformly distributed {@code int} value.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code int} value
	 */
	public static int random() {
		return RANDOM.nextInt();
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code int} value between the specified bounds.
	 * <p>
	 * @param lowerBound the lower bound of the {@code int} value to generate (inclusive)
	 * @param upperBound the upper bound of the {@code int} value to generate (exclusive)
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code int} value between the specified bounds
	 */
	public static int random(final int lowerBound, final int upperBound) {
		return lowerBound + RANDOM.nextInt(upperBound - lowerBound);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code int} array of the specified length with the specified {@code int} element.
	 * <p>
	 * @param element the {@code int} element of the {@code int} array to create
	 * @param length  the length of the {@code int} array to create
	 * <p>
	 * @return a {@code int} array of the specified length with the specified {@code int} element
	 */
	public static int[] repeat(final int element, final int length) {
		return fill(new int[length], element);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code int} token in the specified
	 * {@code int} array.
	 * <p>
	 * @param array an {@code int} array
	 * @param token the {@code int} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code int} token in the specified
	 *         {@code int} array
	 */
	public static int count(final int[] array, final int token) {
		int occurrenceCount = 0, index = -1;
		while ((index = findFirstIndex(array, token, index + 1)) >= 0) {
			++occurrenceCount;
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code int} tokens in the specified
	 * {@code int} array.
	 * <p>
	 * @param array  an {@code int} array
	 * @param tokens the {@code int} tokens to count
	 * <p>
	 * @return the number of occurrences of the specified {@code int} tokens in the specified
	 *         {@code int} array
	 */
	public static int count(final int[] array, final int[] tokens) {
		int occurrenceCount = 0;
		for (final int token : tokens) {
			occurrenceCount += count(array, token);
		}
		return occurrenceCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int[] fill(final int[] array, final int value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
		return array;
	}

	public static int[][] fill(final int[][] array2D, final int value) {
		for (final int[] array : array2D) {
			fill(array, value);
		}
		return array2D;
	}

	public static int[][][] fill(final int[][][] array3D, final int value) {
		for (final int[][] array2D : array3D) {
			fill(array2D, value);
		}
		return array3D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the filtered {@code int} array from the specified {@code int} array and indexes.
	 * <p>
	 * @param array   an {@code int} array
	 * @param indexes the indexes to filter
	 * <p>
	 * @return the filtered {@code int} array from the specified {@code int} array and indexes
	 */
	public static int[] filter(final int[] array, final int... indexes) {
		final int[] filteredArray = new int[indexes.length];
		for (int i = 0; i < indexes.length; ++i) {
			filteredArray[i] = array[indexes[i]];
		}
		return filteredArray;
	}

	/**
	 * Returns all the filtered {@code int} arrays from the specified {@code int} array and indexes.
	 * <p>
	 * @param array   an {@code int} array
	 * @param indexes the array of indexes to filter
	 * <p>
	 * @return all the filtered {@code int} arrays from the specified {@code int} array and indexes
	 */
	public static int[][] filterAll(final int[] array, final int[]... indexes) {
		final int[][] filteredArrays = new int[indexes.length][];
		for (int i = 0; i < indexes.length; ++i) {
			filteredArrays[i] = filter(array, indexes[i]);
		}
		return filteredArrays;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@code int} array containing the specified {@code int} value and all the elements
	 * of the specified {@code int} array.
	 * <p>
	 * @param a an {@code int} value
	 * @param b an {@code int} array (may be {@code null})
	 * <p>
	 * @return an {@code int} array containing the specified {@code int} value and all the elements
	 *         of the specified {@code int} array
	 */
	public static int[] merge(final int a, final int... b) {
		return merge(asPrimitiveArray(a), b);
	}

	/**
	 * Returns an {@code int} array containing all the elements of the specified {@code int} arrays.
	 * <p>
	 * @param a an {@code int} array (may be {@code null})
	 * @param b an {@code int} array (may be {@code null})
	 * <p>
	 * @return an {@code int} array containing all the elements of the specified {@code int} arrays
	 */
	public static int[] merge(final int[] a, final int... b) {
		if (a == null) {
			return clone(b);
		} else if (b == null) {
			return clone(a);
		}
		final int[] mergedArray = new int[a.length + b.length];
		System.arraycopy(a, 0, mergedArray, 0, a.length);
		System.arraycopy(b, 0, mergedArray, a.length, b.length);
		return mergedArray;
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

	public static void reverse(final int[] array) {
		reverse(array, 0, array.length - 1);
	}

	public static void reverse(final int[] array, final int fromIndex) {
		reverse(array, fromIndex, array.length - 1);
	}

	public static void reverse(final int[] array, final int fromIndex, final int toIndex) {
		final int limit = Maths.ceilToInt((toIndex - fromIndex) / 2.);
		for (int i = 0; i < limit; ++i) {
			swap(array, fromIndex + i, toIndex - i);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void swap(final int[] array, final int i, final int j) {
		final int element = array[i];
		array[i] = array[j];
		array[j] = element;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int[] take(final int... array) {
		return take(array, 0, array.length);
	}

	public static int[] take(final int[] array, final int fromIndex, final int length) {
		final int maxLength = Math.min(length, array.length - fromIndex);
		final int[] subarray = new int[maxLength];
		System.arraycopy(array, fromIndex, subarray, 0, maxLength);
		return subarray;
	}

	//////////////////////////////////////////////

	public static int[] take(final int[]... array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static int[] take(final int[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static int[] take(final int[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length - fromRow);
		final int[] subarray = new int[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, subarray,
					i * columnCount, columnCount);
		}
		return subarray;
	}

	//////////////////////////////////////////////

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
		final int[] subarray = new int[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					subarray, i * length, length);
		}
		return subarray;
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
	// SEEKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int findFirstIndex(final int[] array, final int token) {
		if (array != null) {
			return findFirstIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findFirstIndex(final int[] array, final int token, final int from) {
		if (array != null) {
			return findFirstIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static int findFirstIndex(final int[] array, final int token, final int from,
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

	public static int findLastIndex(final int[] array, final int token) {
		if (array != null) {
			return findLastIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findLastIndex(final int[] array, final int token, final int from) {
		if (array != null) {
			return findLastIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static int findLastIndex(final int[] array, final int token, final int from,
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
	 * @return {@code true} if the specified {@link Class} is assignable to an {@code int} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArray(final Class<?> c) {
		return int[].class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code int} array is {@code null} or empty.
	 * <p>
	 * @param array the {@code int} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code int} array is {@code null} or empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final int[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Tests whether the specified {@code int} array is not {@code null} and empty.
	 * <p>
	 * @param array the {@code int} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code int} array is not {@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final int[] array) {
		return array != null && array.length == 0;
	}

	/**
	 * Tests whether the specified {@code int} array is not {@code null} and not empty.
	 * <p>
	 * @param array the {@code int} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code int} array is not {@code null} and not empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNotEmpty(final int[] array) {
		return array != null && array.length > 0;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code int} value is between the specified {@code int} lower and
	 * upper bounds.
	 * <p>
	 * @param value the {@code int} value to test
	 * @param from  the {@code int} lower bound to test against (inclusive)
	 * @param to    the {@code int} upper bound to test against (exclusive)
	 * <p>
	 * @return {@code true} if the specified {@code int} value is between the specified {@code int}
	 *         lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final int value, final int from, final int to) {
		return isBetween(value, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@code int} value is between the specified {@code int} lower and
	 * upper bounds.
	 * <p>
	 * @param value            the {@code int} value to test
	 * @param from             the {@code int} lower bound to test against (inclusive)
	 * @param to               the {@code int} upper bound to test against
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code int} value is between the specified {@code int}
	 *         lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final int value, final int from, final int to,
			final boolean isUpperInclusive) {
		return isBetween(value, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code int} value is between the specified {@code int} lower and
	 * upper bounds.
	 * <p>
	 * @param value            the {@code int} value to test
	 * @param from             the {@code int} lower bound to test against
	 * @param to               the {@code int} upper bound to test against
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code int} value is between the specified {@code int}
	 *         lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final int value, final int from, final int to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return (isLowerInclusive ? value >= from : value > from) &&
				(isUpperInclusive ? value <= to : value < to);
	}

	/**
	 * Tests whether the specified {@code int} array is between the specified lower and upper bound
	 * {@code int} arrays.
	 * <p>
	 * @param array the {@code int} array to test
	 * @param from  the lower bound {@code int} array to test against (inclusive)
	 * @param to    the upper bound {@code int} array to test against (exclusive)
	 * <p>
	 * @return {@code true} if the specified {@code int} array is between the specified lower and
	 *         upper bound {@code int} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final int[] array, final int[] from, final int[] to) {
		return compare(array, from) >= 0 && compare(array, to) < 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code int} array contains the specified {@code int} token.
	 * <p>
	 * @param array the {@code int} array to test (may be {@code null})
	 * @param token the {@code int} token to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code int} array contains the specified {@code int}
	 *         token, {@code false} otherwise
	 */
	public static boolean contains(final int[] array, final int token) {
		return findFirstIndex(array, token) >= 0;
	}

	/**
	 * Tests whether the specified {@code int} array contains any of the specified {@code int}
	 * tokens.
	 * <p>
	 * @param array  the {@code int} array to test (may be {@code null})
	 * @param tokens the {@code int} tokens to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code int} array contains any of the specified
	 *         {@code int} tokens, {@code false} otherwise
	 */
	public static boolean containsAny(final int[] array, final int[] tokens) {
		if (array != null) {
			for (final int token : tokens) {
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

	//////////////////////////////////////////////

	/**
	 * Compares the specified {@code int} arrays for order. Returns a negative integer, zero or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code int} array to compare for order
	 * @param b the other {@code int} array to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static int compare(final int[] a, final int[] b) {
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
	 * Returns a clone of the specified {@code int} array, or {@code null} if {@code array} is
	 * {@code null}.
	 * <p>
	 * @param array the {@code int} array to clone (may be {@code null})
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
	 * Returns the hash code value for the specified {@code int} array.
	 * <p>
	 * @param array the {@code int} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code int} array
	 */
	public static int hashCode(final int... array) {
		return hashCodeWith(0, array);
	}

	/**
	 * Returns the hash code value for the specified {@code int} array at the specified depth.
	 * <p>
	 * @param depth the depth to hash at
	 * @param array the {@code int} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code int} array at the specified depth
	 */
	@SuppressWarnings("unchecked")
	public static int hashCodeWith(final int depth, final int... array) {
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
