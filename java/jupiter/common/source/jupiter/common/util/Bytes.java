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

import static jupiter.common.util.Characters.LOWER_CASE_DIGITS;
import static jupiter.common.util.Characters.UPPER_CASE_DIGITS;

import java.util.Collection;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.ByteParser;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;

public class Bytes {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final byte[] EMPTY_PRIMITIVE_ARRAY = new byte[] {};
	public static final Byte[] EMPTY_ARRAY = new Byte[] {};

	protected static final ByteParser PARSER = new ByteParser();

	public static volatile Random RANDOM = new Random();

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Comparator<Byte> COMPARATOR = new Comparator<Byte>() {
		@Override
		public int compare(final Byte a, final Byte b) {
			return Bytes.compare(a, b);
		}
	};

	public static final Comparator<byte[]> ARRAY_COMPARATOR = new Comparator<byte[]>() {
		@Override
		public int compare(final byte[] a, final byte[] b) {
			return Bytes.compare(a, b);
		}
	};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Bytes}.
	 */
	protected Bytes() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code byte} value converted from the specified {@code short} value.
	 * <p>
	 * @param value the {@code short} value to convert
	 * <p>
	 * @return a {@code byte} value converted from the specified {@code short} value
	 */
	public static byte convert(final short value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			throw new ArithmeticException("Byte under/overflow");
		}
		return (byte) value;
	}

	/**
	 * Returns a {@code byte} value converted from the specified {@code int} value.
	 * <p>
	 * @param value the {@code int} value to convert
	 * <p>
	 * @return a {@code byte} value converted from the specified {@code int} value
	 */
	public static byte convert(final int value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			throw new ArithmeticException("Byte under/overflow");
		}
		return (byte) value;
	}

	/**
	 * Returns a {@code byte} value converted from the specified {@code long} value.
	 * <p>
	 * @param value the {@code long} value to convert
	 * <p>
	 * @return a {@code byte} value converted from the specified {@code long} value
	 */
	public static byte convert(final long value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			throw new ArithmeticException("Byte under/overflow");
		}
		return (byte) value;
	}

	/**
	 * Returns a {@code byte} value converted from the specified {@code float} value.
	 * <p>
	 * @param value the {@code float} value to convert
	 * <p>
	 * @return a {@code byte} value converted from the specified {@code float} value
	 */
	public static byte convert(final float value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			throw new ArithmeticException("Byte under/overflow");
		}
		return (byte) value;
	}

	/**
	 * Returns a {@code byte} value converted from the specified {@code double} value.
	 * <p>
	 * @param value the {@code double} value to convert
	 * <p>
	 * @return a {@code byte} value converted from the specified {@code double} value
	 */
	public static byte convert(final double value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			throw new ArithmeticException("Byte under/overflow");
		}
		return (byte) value;
	}

	/**
	 * Returns a {@link Byte} converted from the specified {@link Object}.
	 * <p>
	 * @param object the {@link Object} to convert
	 * <p>
	 * @return a {@link Byte} converted from the specified {@link Object}
	 */
	public static Byte convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts the specified source {@code byte} value to a representative unsigned {@code int}
	 * value written to the specified target {@code char} buffer and returns the lowest index of the
	 * specified target {@code char} buffer used.
	 * <p>
	 * @param source the source {@code byte} value
	 * @param shift  the log2 of the base to format in (4 for hex, 3 for octal, 1 for binary)
	 * @param target the target {@code char} buffer
	 * @param offset the offset in the target {@code char} buffer to start at
	 * @param length the number of digits to write
	 * <p>
	 * @return the lowest index of the specified target {@code char} buffer used
	 */
	public static int toUnsignedInt(final byte source, final int shift, final char[] target,
			final int offset, final int length) {
		return toUnsignedInt(source, shift, target, offset, length, false);
	}

	/**
	 * Converts the specified source {@code byte} value to a representative unsigned {@code int}
	 * value written to the specified target {@code char} buffer and returns the lowest index of the
	 * specified target {@code char} buffer used.
	 * <p>
	 * @param source       the source {@code byte} value
	 * @param shift        the log2 of the base to format in (4 for hex, 3 for octal, 1 for binary)
	 * @param target       the target {@code char} buffer
	 * @param offset       the offset in the target {@code char} buffer to start at
	 * @param length       the number of digits to write
	 * @param useLowerCase the flag specifying whether to use lower or upper case digits
	 * <p>
	 * @return the lowest index of the specified target {@code char} buffer used
	 */
	public static int toUnsignedInt(final byte source, final int shift, final char[] target,
			final int offset, final int length, final boolean useLowerCase) {
		return toUnsignedInt(source, shift, target, offset, length,
				useLowerCase ? LOWER_CASE_DIGITS : UPPER_CASE_DIGITS);
	}

	/**
	 * Converts the specified source {@code byte} value to a representative unsigned {@code int}
	 * value written to the specified target {@code char} buffer and returns the lowest index of the
	 * specified target {@code char} buffer used.
	 * <p>
	 * @param source the source {@code byte} value
	 * @param shift  the log2 of the base to format in (4 for hex, 3 for octal, 1 for binary)
	 * @param target the target {@code char} buffer
	 * @param offset the offset in the target {@code char} buffer to start at
	 * @param length the number of digits to write
	 * @param digits the digits to use
	 * <p>
	 * @return the lowest index of the specified target {@code char} buffer used
	 */
	public static int toUnsignedInt(final byte source, final int shift, final char[] target,
			final int offset, final int length, final char[] digits) {
		return Integers.toUnsignedInt(source & 0xFF, shift, target, offset, length, digits);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a binary representative {@link String} of the specified {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to convert
	 * <p>
	 * @return a binary representative {@link String} of the specified {@code byte} array
	 */
	public static String toBinaryString(final byte... array) {
		return toBinaryString(array, UPPER_CASE_DIGITS);
	}

	/**
	 * Returns a binary representative {@link String} of the specified {@code byte} array using the
	 * specified digits.
	 * <p>
	 * @param array  the {@code byte} array to convert
	 * @param digits the digits to use
	 * <p>
	 * @return a binary representative {@link String} of the specified {@code byte} array using the
	 *         specified digits
	 */
	public static String toBinaryString(final byte[] array, final char[] digits) {
		final char[] buffer = new char[array.length << 3];
		for (int i = 0; i < array.length; ++i) {
			final int offset = i << 3;
			final int index = toUnsignedInt(array[i], 1, buffer, offset, 8, digits);
			for (int j = offset; j < offset + index; ++j) {
				buffer[j] = '0';
			}
		}
		return new String(buffer);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an octal representative {@link String} of the specified {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to convert
	 * <p>
	 * @return an octal representative {@link String} of the specified {@code byte} array
	 */
	public static String toOctalString(final byte... array) {
		return toOctalString(array, UPPER_CASE_DIGITS);
	}

	/**
	 * Returns an octal representative {@link String} of the specified {@code byte} array using the
	 * specified digits.
	 * <p>
	 * @param array  the {@code byte} array to convert
	 * @param digits the digits to use
	 * <p>
	 * @return an octal representative {@link String} of the specified {@code byte} array using the
	 *         specified digits
	 */
	public static String toOctalString(final byte[] array, final char[] digits) {
		final char[] buffer = new char[array.length << 2];
		for (int i = 0; i < array.length; ++i) {
			final int offset = i << 2;
			final int index = toUnsignedInt(array[i], 3, buffer, offset, 4, digits);
			for (int j = offset; j < offset + index; ++j) {
				buffer[j] = '0';
			}
		}
		return new String(buffer);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a hexadecimal representative {@link String} of the specified {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to convert
	 * <p>
	 * @return a hexadecimal representative {@link String} of the specified {@code byte} array
	 */
	public static String toHexString(final byte... array) {
		return toHexString(array, false);
	}

	/**
	 * Returns a hexadecimal representative {@link String} of the specified {@code byte} array.
	 * <p>
	 * @param array        the {@code byte} array to convert
	 * @param useLowerCase the flag specifying whether to use lower or upper case digits
	 * <p>
	 * @return a hexadecimal representative {@link String} of the specified {@code byte} array
	 */
	public static String toHexString(final byte[] array, final boolean useLowerCase) {
		return toHexString(array, useLowerCase ? LOWER_CASE_DIGITS : UPPER_CASE_DIGITS);
	}

	/**
	 * Returns a hexadecimal representative {@link String} of the specified {@code byte} array using
	 * the specified digits.
	 * <p>
	 * @param array  the {@code byte} array to convert
	 * @param digits the digits to use
	 * <p>
	 * @return a hexadecimal representative {@link String} of the specified {@code byte} array using
	 *         the specified digits
	 */
	public static String toHexString(final byte[] array, final char[] digits) {
		final char[] buffer = new char[array.length << 1];
		for (int i = 0; i < array.length; ++i) {
			final int offset = i << 1;
			final int index = toUnsignedInt(array[i], 4, buffer, offset, 2, digits);
			for (int j = offset; j < offset + index; ++j) {
				buffer[j] = '0';
			}
		}
		return new String(buffer);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code byte} array converted from the specified binary representative
	 * {@link String}.
	 * <p>
	 * @param binaryString the binary representative {@link String} to convert
	 * <p>
	 * @return a {@code byte} array converted from the specified binary representative
	 *         {@link String}
	 */
	public static byte[] parseBinaryString(final String binaryString) {
		final byte[] array = new byte[binaryString.length() >> 3];
		for (int i = 0; i < binaryString.length(); i += 8) {
			array[i >> 3] = (byte) Integers.parseUnsignedInt(binaryString.substring(i, i + 8), 2);
		}
		return array;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code byte} array converted from the specified octal representative
	 * {@link String}.
	 * <p>
	 * @param octalString the octal representative {@link String} to convert
	 * <p>
	 * @return a {@code byte} array converted from the specified octal representative {@link String}
	 */
	public static byte[] parseOctalString(final String octalString) {
		final byte[] array = new byte[octalString.length() >> 2];
		for (int i = 0; i < octalString.length(); i += 4) {
			array[i >> 2] = (byte) Integers.parseUnsignedInt(octalString.substring(i, i + 4), 8);
		}
		return array;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code byte} array converted from the specified hexadecimal representative
	 * {@link String}.
	 * <p>
	 * @param hexString the hexadecimal representative {@link String} to convert
	 * <p>
	 * @return a {@code byte} array converted from the specified hexadecimal representative
	 *         {@link String}
	 */
	public static byte[] parseHexString(final String hexString) {
		final byte[] array = new byte[hexString.length() >> 1];
		for (int i = 0; i < hexString.length(); i += 2) {
			array[i >> 1] = (byte) Integers.parseUnsignedInt(hexString.substring(i, i + 2), 16);
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code byte} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object the {@code T} object to convert
	 * <p>
	 * @return a {@code byte} value from the specified {@code T} object
	 */
	public static <T> byte toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code byte} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@code byte} array from the specified {@code T} array
	 */
	public static <T> byte[] toPrimitiveArray(final T[] array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a {@code byte} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@code byte} array from the specified {@code T} array
	 */
	public static <T> byte[] asPrimitiveArray(final T... array) {
		return toPrimitiveArray(array);
	}

	/**
	 * Returns a {@code byte} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a {@code byte} array from the specified 2D {@code T} array
	 */
	public static <T> byte[] toPrimitiveArray(final T[][] array2D) {
		return PARSER.callToPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code byte} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a {@code byte} array from the specified 2D {@code T} array
	 */
	public static <T> byte[] asPrimitiveArray(final T[]... array2D) {
		return toPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code byte} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a {@code byte} array from the specified 3D {@code T} array
	 */
	public static <T> byte[] toPrimitiveArray(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray(array3D);
	}

	/**
	 * Returns a {@code byte} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a {@code byte} array from the specified 3D {@code T} array
	 */
	public static <T> byte[] asPrimitiveArray(final T[][]... array3D) {
		return toPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D {@code byte} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a 2D {@code byte} array from the specified 2D {@code T} array
	 */
	public static <T> byte[][] toPrimitiveArray2D(final T[][] array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 2D {@code byte} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a 2D {@code byte} array from the specified 2D {@code T} array
	 */
	public static <T> byte[][] asPrimitiveArray2D(final T[]... array2D) {
		return toPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 3D {@code byte} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code byte} array from the specified 3D {@code T} array
	 */
	public static <T> byte[][][] toPrimitiveArray3D(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a 3D {@code byte} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code byte} array from the specified 3D {@code T} array
	 */
	public static <T> byte[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a {@code byte} array from the specified {@link Collection} of element type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return a {@code byte} array from the specified {@link Collection} of element type {@code E}
	 */
	public static <E> byte[] collectionToPrimitiveArray(final Collection<E> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Byte} from the specified {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * <p>
	 * @return an array of {@link Byte} from the specified {@code byte} array
	 */
	public static Byte[] toArray(final byte[] array) {
		final Byte[] convertedArray = new Byte[array.length];
		for (int i = 0; i < array.length; ++i) {
			convertedArray[i] = array[i];
		}
		return convertedArray;
	}

	/**
	 * Returns an array of {@link Byte} from the specified {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * <p>
	 * @return an array of {@link Byte} from the specified {@code byte} array
	 */
	public static Byte[] asArray(final byte... array) {
		return toArray(array);
	}

	/**
	 * Returns a 2D array of {@link Byte} from the specified 2D {@code byte} array.
	 * <p>
	 * @param array2D a 2D {@code byte} array
	 * <p>
	 * @return a 2D array of {@link Byte} from the specified 2D {@code byte} array
	 */
	public static Byte[][] toArray2D(final byte[][] array2D) {
		final Byte[][] convertedArray2D = new Byte[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			convertedArray2D[i] = toArray(array2D[i]);
		}
		return convertedArray2D;
	}

	/**
	 * Returns a 2D array of {@link Byte} from the specified 2D {@code byte} array.
	 * <p>
	 * @param array2D a 2D {@code byte} array
	 * <p>
	 * @return a 2D array of {@link Byte} from the specified 2D {@code byte} array
	 */
	public static Byte[][] asArray2D(final byte[]... array2D) {
		return toArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@link Byte} from the specified 3D {@code byte} array.
	 * <p>
	 * @param array3D a 3D {@code byte} array
	 * <p>
	 * @return a 3D array of {@link Byte} from the specified 3D {@code byte} array
	 */
	public static Byte[][][] toArray3D(final byte[][][] array3D) {
		final Byte[][][] convertedArray3D = new Byte[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			convertedArray3D[i] = toArray2D(array3D[i]);
		}
		return convertedArray3D;
	}

	/**
	 * Returns a 3D array of {@link Byte} from the specified 3D {@code byte} array.
	 * <p>
	 * @param array3D a 3D {@code byte} array
	 * <p>
	 * @return a 3D array of {@link Byte} from the specified 3D {@code byte} array
	 */
	public static Byte[][][] asArray3D(final byte[][]... array3D) {
		return toArray3D(array3D);
	}

	/**
	 * Returns an array of {@link Byte} from the specified {@link Collection} of element type
	 * {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an array of {@link Byte} from the specified {@link Collection} of element type
	 *         {@code E}
	 */
	public static <E> Byte[] collectionToArray(final Collection<E> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} from the specified {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} from the specified {@code byte} array
	 */
	public static ExtendedList<Byte> toList(final byte[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} from the specified {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} from the specified {@code byte} array
	 */
	public static ExtendedList<Byte> asList(final byte... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Byte} from the specified {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Byte} from the specified {@code byte} array
	 */
	public static ExtendedLinkedList<Byte> toLinkedList(final byte[] array) {
		return PARSER.callToLinkedList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Byte} from the specified {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Byte} from the specified {@code byte} array
	 */
	public static ExtendedLinkedList<Byte> asLinkedList(final byte... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Byte> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Byte> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Byte} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Byte} from the specified {@code T} array
	 */
	public static <T> ExtendedLinkedList<Byte> toLinkedList(final T[] array) {
		return PARSER.callToLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Byte} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Byte} from the specified {@code T} array
	 */
	public static <T> ExtendedLinkedList<Byte> asLinkedList(final T... array) {
		return toLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} from the specified {@link Collection} of
	 * element type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} from the specified {@link Collection} of
	 *         element type {@code E}
	 */
	public static <E> ExtendedList<Byte> collectionToList(final Collection<E> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Byte} from the specified {@link Collection}
	 * of element type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Byte} from the specified {@link Collection}
	 *         of element type {@code E}
	 */
	public static <E> ExtendedLinkedList<Byte> collectionToLinkedList(
			final Collection<E> collection) {
		return PARSER.callCollectionToLinkedList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Byte} from the specified {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * <p>
	 * @return a {@link Set} of {@link Byte} from the specified {@code byte} array
	 */
	public static Set<Byte> toSet(final byte[] array) {
		return PARSER.callToSet(toArray(array));
	}

	/**
	 * Returns a {@link Set} of {@link Byte} from the specified {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * <p>
	 * @return a {@link Set} of {@link Byte} from the specified {@code byte} array
	 */
	public static Set<Byte> asSet(final byte... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Byte} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Byte} from the specified {@code T} array
	 */
	public static <T> Set<Byte> toSet(final T[] array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Byte} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Byte} from the specified {@code T} array
	 */
	public static <T> Set<Byte> asSet(final T... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Byte} from the specified {@link Collection} of element type
	 * {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return a {@link Set} of {@link Byte} from the specified {@link Collection} of element type
	 *         {@code E}
	 */
	public static <E> Set<Byte> collectionToSet(final Collection<E> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code byte} array of the specified length containing the sequence of numbers
	 * starting with zero and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return a {@code byte} array of the specified length containing the sequence of numbers
	 *         starting with zero and spaced by one
	 */
	public static byte[] createSequence(final int length) {
		return createSequence(length, (byte) 0, (byte) 1);
	}

	/**
	 * Creates a {@code byte} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return a {@code byte} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by one
	 */
	public static byte[] createSequence(final int length, final byte from) {
		return createSequence(length, from, (byte) 1);
	}

	/**
	 * Creates a {@code byte} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return a {@code byte} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by {@code step}
	 */
	public static byte[] createSequence(final int length, final byte from, final byte step) {
		final byte[] array = new byte[length];
		byte value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	//////////////////////////////////////////////

	/**
	 * Creates a random {@code byte} array of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return a random {@code byte} array of the specified length
	 */
	public static byte[] createRandomSequence(final int length) {
		final byte[] array = new byte[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates a {@code byte} array of the specified length containing pseudorandom, uniformly
	 * distributed {@code byte} values between the specified bounds.
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create (inclusive)
	 * @param upperBound the upper bound of the random sequence to create (exclusive)
	 * <p>
	 * @return a {@code byte} array of the specified length containing pseudorandom, uniformly
	 *         distributed {@code byte} values between the specified bounds
	 */
	public static byte[] createRandomSequence(final int length, final byte lowerBound,
			final byte upperBound) {
		final byte[] array = new byte[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(lowerBound, upperBound);
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code byte} value.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code byte} value
	 */
	public static byte random() {
		return random(Byte.MIN_VALUE, Byte.MAX_VALUE);
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code byte} value between the specified
	 * bounds.
	 * <p>
	 * @param lowerBound the lower bound of the {@code byte} value to generate (inclusive)
	 * @param upperBound the upper bound of the {@code byte} value to generate (exclusive)
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code byte} value between the specified bounds
	 */
	public static byte random(final byte lowerBound, final byte upperBound) {
		return convert(lowerBound + RANDOM.nextDouble() * (upperBound - lowerBound));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code byte} array of the specified length with the specified {@code byte} element.
	 * <p>
	 * @param element the {@code byte} element of the {@code byte} array to create
	 * @param length  the length of the {@code byte} array to create
	 * <p>
	 * @return a {@code byte} array of the specified length with the specified {@code byte} element
	 */
	public static byte[] repeat(final byte element, final int length) {
		return fill(new byte[length], element);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code byte} token in the specified
	 * {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * @param token the {@code byte} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code byte} token in the specified
	 *         {@code byte} array
	 */
	public static int count(final byte[] array, final byte token) {
		int occurrenceCount = 0, index = -1;
		while ((index = findFirstIndex(array, token, index + 1)) >= 0) {
			++occurrenceCount;
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code byte} tokens in the specified
	 * {@code byte} array.
	 * <p>
	 * @param array  a {@code byte} array
	 * @param tokens the {@code byte} tokens to count
	 * <p>
	 * @return the number of occurrences of the specified {@code byte} tokens in the specified
	 *         {@code byte} array
	 */
	public static int count(final byte[] array, final byte[] tokens) {
		int occurrenceCount = 0;
		for (final byte token : tokens) {
			occurrenceCount += count(array, token);
		}
		return occurrenceCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static byte[] fill(final byte[] array, final byte value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
		return array;
	}

	public static byte[][] fill(final byte[][] array2D, final byte value) {
		for (final byte[] array : array2D) {
			fill(array, value);
		}
		return array2D;
	}

	public static byte[][][] fill(final byte[][][] array3D, final byte value) {
		for (final byte[][] array2D : array3D) {
			fill(array2D, value);
		}
		return array3D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the filtered {@code byte} array from the specified {@code byte} array and indexes.
	 * <p>
	 * @param array   a {@code byte} array
	 * @param indexes the indexes to filter
	 * <p>
	 * @return the filtered {@code byte} array from the specified {@code byte} array and indexes
	 */
	public static byte[] filter(final byte[] array, final int... indexes) {
		final byte[] filteredArray = new byte[indexes.length];
		for (int i = 0; i < indexes.length; ++i) {
			filteredArray[i] = array[indexes[i]];
		}
		return filteredArray;
	}

	/**
	 * Returns all the filtered {@code byte} arrays from the specified {@code byte} array and
	 * indexes.
	 * <p>
	 * @param array   a {@code byte} array
	 * @param indexes the array of indexes to filter
	 * <p>
	 * @return all the filtered {@code byte} arrays from the specified {@code byte} array and
	 *         indexes
	 */
	public static byte[][] filterAll(final byte[] array, final int[]... indexes) {
		final byte[][] filteredArrays = new byte[indexes.length][];
		for (int i = 0; i < indexes.length; ++i) {
			filteredArrays[i] = filter(array, indexes[i]);
		}
		return filteredArrays;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code byte} array containing the specified {@code byte} value and all the elements
	 * of the specified {@code byte} array.
	 * <p>
	 * @param a a {@code byte} value (may be {@code null})
	 * @param b a {@code byte} array (may be {@code null})
	 * <p>
	 * @return a {@code byte} array containing the specified {@code byte} value and all the elements
	 *         of the specified {@code byte} array
	 */
	public static byte[] merge(final byte a, final byte... b) {
		return merge(asPrimitiveArray(a), b);
	}

	/**
	 * Returns a {@code byte} array containing all the elements of the specified {@code byte}
	 * arrays.
	 * <p>
	 * @param a a {@code byte} array (may be {@code null})
	 * @param b a {@code byte} array (may be {@code null})
	 * <p>
	 * @return a {@code byte} array containing all the elements of the specified {@code byte} arrays
	 */
	public static byte[] merge(final byte[] a, final byte... b) {
		if (a == null) {
			return clone(b);
		} else if (b == null) {
			return clone(a);
		}
		final byte[] mergedArray = new byte[a.length + b.length];
		System.arraycopy(a, 0, mergedArray, 0, a.length);
		System.arraycopy(b, 0, mergedArray, a.length, b.length);
		return mergedArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code byte} value.
	 * <p>
	 * @param value a {@code byte} value
	 * <p>
	 * @return the middle of the specified {@code byte} value
	 */
	public static byte middle(final byte value) {
		return middle((byte) 0, value);
	}

	/**
	 * Returns the middle of the specified lower and upper bounds rounded to the lower {@code byte}
	 * value.
	 * <p>
	 * @param lowerBound a {@code byte} value
	 * @param upperBound another {@code byte} value
	 * <p>
	 * @return the middle of the specified lower and upper bounds rounded to the lower {@code byte}
	 *         value
	 */
	public static byte middle(final byte lowerBound, final byte upperBound) {
		return convert(lowerBound + (upperBound - lowerBound) / 2);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void swap(final byte[] array, final int i, final int j) {
		final byte element = array[i];
		array[i] = array[j];
		array[j] = element;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static byte[] take(final byte... array) {
		return take(array, 0, array.length);
	}

	public static byte[] take(final byte[] array, final int fromIndex, final int length) {
		final int maxLength = Math.min(length, array.length - fromIndex);
		final byte[] subarray = new byte[maxLength];
		System.arraycopy(array, fromIndex, subarray, 0, maxLength);
		return subarray;
	}

	public static byte[] take(final byte[]... array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static byte[] take(final byte[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static byte[] take(final byte[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length - fromRow);
		final byte[] subarray = new byte[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, subarray,
					i * columnCount, columnCount);
		}
		return subarray;
	}

	public static byte[] take(final byte[][]... array3D) {
		return take(array3D, 0, array3D.length, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static byte[] take(final byte[][][] array3D, final int fromRow, final int rowCount) {
		return take(array3D, fromRow, rowCount, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static byte[] take(final byte[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0, array3D[0][0].length);
	}

	public static byte[] take(final byte[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length - fromRow);
		final int length = columnCount * depthCount;
		final byte[] subarray = new byte[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					subarray, i * length, length);
		}
		return subarray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified {@code byte} array.
	 * <p>
	 * @param rowCount the number of rows of the array
	 * @param array    a {@code byte} array
	 * <p>
	 * @return the transpose of the specified {@code byte} array
	 */
	public static byte[] transpose(final int rowCount, final byte[] array) {
		final int n = rowCount;
		final int m = array.length / rowCount;
		final byte[] transpose = new byte[m * n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i * n + j] = array[j * m + i];
			}
		}
		return transpose;
	}

	/**
	 * Returns the transpose of the specified 2D {@code byte} array.
	 * <p>
	 * @param array2D a 2D {@code byte} array
	 * <p>
	 * @return the transpose of the specified 2D {@code byte} array
	 */
	public static byte[][] transpose(final byte[]... array2D) {
		final int n = array2D.length;
		final int m = array2D[0].length;
		final byte[][] transpose = new byte[m][n];
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

	public static int findFirstIndex(final byte[] array, final byte token) {
		return findFirstIndex(array, token, 0, array.length);
	}

	public static int findFirstIndex(final byte[] array, final byte token, final int from) {
		return findFirstIndex(array, token, from, array.length);
	}

	public static int findFirstIndex(final byte[] array, final byte token, final int from,
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

	public static int findLastIndex(final byte[] array, final byte token) {
		return findLastIndex(array, token, 0, array.length);
	}

	public static int findLastIndex(final byte[] array, final byte token, final int from) {
		return findLastIndex(array, token, from, array.length);
	}

	public static int findLastIndex(final byte[] array, final byte token, final int from,
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
	 * Tests whether the specified {@link Class} is assignable to a {@code byte} value or a
	 * {@link Byte}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code byte} value or
	 *         a {@link Byte}, {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return byte.class.isAssignableFrom(c) || Byte.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code byte} value.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code byte} value,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitive(final Class<?> c) {
		return byte.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code byte} array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code byte} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArray(final Class<?> c) {
		return byte[].class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code byte} array is {@code null} or empty.
	 * <p>
	 * @param array the {@code byte} array to test
	 * <p>
	 * @return {@code true} if the specified {@code byte} array is {@code null} or empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final byte[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Tests whether the specified {@code byte} array is not {@code null} and empty.
	 * <p>
	 * @param array the {@code byte} array to test
	 * <p>
	 * @return {@code true} if the specified {@code byte} array is not {@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final byte[] array) {
		return array != null && array.length == 0;
	}

	/**
	 * Tests whether the specified {@code byte} array is not {@code null} and not empty.
	 * <p>
	 * @param array the {@code byte} array to test
	 * <p>
	 * @return {@code true} if the specified {@code byte} array is not {@code null} and not empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNotEmpty(final byte[] array) {
		return array != null && array.length > 0;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code byte} value is between the specified {@code byte} lower
	 * and upper bounds.
	 * <p>
	 * @param value the {@code byte} value to test
	 * @param from  the {@code byte} lower bound to test against (inclusive)
	 * @param to    the {@code byte} upper bound to test against (exclusive)
	 * <p>
	 * @return {@code true} if the specified {@code byte} value is between the specified
	 *         {@code byte} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final byte value, final byte from, final byte to) {
		return value >= from && value < to;
	}

	/**
	 * Tests whether the specified {@code byte} array is between the specified lower and upper bound
	 * {@code byte} arrays.
	 * <p>
	 * @param array the {@code byte} array to test
	 * @param from  the lower bound {@code byte} array to test against (inclusive)
	 * @param to    the upper bound {@code byte} array to test against (exclusive)
	 * <p>
	 * @return {@code true} if the specified {@code byte} array is between the specified lower and
	 *         upper bound {@code byte} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final byte[] array, final byte[] from, final byte[] to) {
		return compare(array, from) >= 0 && compare(array, to) < 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code byte} array contains the specified {@code byte} token.
	 * <p>
	 * @param array the {@code byte} array to test
	 * @param token the {@code byte} token to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code byte} array contains the specified {@code byte}
	 *         token, {@code false} otherwise
	 */
	public static boolean contains(final byte[] array, final byte token) {
		return findFirstIndex(array, token) >= 0;
	}

	/**
	 * Tests whether the specified {@code byte} array contains any of the specified {@code byte}
	 * tokens.
	 * <p>
	 * @param array  the {@code byte} array to test
	 * @param tokens the {@code byte} tokens to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code byte} array contains any of the specified
	 *         {@code byte} tokens, {@code false} otherwise
	 */
	public static boolean containsAny(final byte[] array, final byte[] tokens) {
		if (array != null) {
			for (final byte token : tokens) {
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
	 * Compares the specified {@code byte} values for order. Returns a negative integer, zero or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code byte} value to compare for order
	 * @param b the {@code byte} value to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 */
	public static int compare(final byte a, final byte b) {
		return a - b;
	}

	//////////////////////////////////////////////

	/**
	 * Compares the specified {@code byte} arrays for order. Returns a negative integer, zero or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code byte} array to compare for order
	 * @param b the other {@code byte} array to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static int compare(final byte[] a, final byte[] b) {
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
	 * Returns a clone of the specified {@code byte} array, or {@code null} if {@code array} is
	 * {@code null}.
	 * <p>
	 * @param array the {@code byte} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified {@code byte} array, or {@code null} if {@code array} is
	 *         {@code null}
	 */
	public static byte[] clone(final byte... array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the hash code value for the specified {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to hash
	 * <p>
	 * @return the hash code value for the specified {@code byte} array
	 */
	public static int hashCode(final byte... array) {
		return hashCodeWith(0, array);
	}

	/**
	 * Returns the hash code value for the specified {@code byte} array at the specified depth.
	 * <p>
	 * @param array the {@code byte} array to hash
	 * @param depth the depth to hash at
	 * <p>
	 * @return the hash code value for the specified {@code byte} array at the specified depth
	 */
	@SuppressWarnings("unchecked")
	public static int hashCodeWith(final int depth, final byte... array) {
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
	 * Returns a representative {@link String} of the specified {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * <p>
	 * @return a representative {@link String} of the specified {@code byte} array
	 */
	public static String toString(final byte... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a representative {@link String} of the specified {@code byte} array joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     a {@code byte} array
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@code byte} array joined by
	 *         {@code delimiter}
	 */
	public static String toString(final byte[] array, final String delimiter) {
		return Arrays.toString(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code byte} array joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     a {@code byte} array
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code byte} array joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final byte[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(toArray(array), delimiter, wrapper);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified array of {@link Byte}.
	 * <p>
	 * @param array an array of {@link Byte}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Byte}
	 */
	public static String toString(final Byte... array) {
		return Arrays.toString(array);
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Byte} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an array of {@link Byte}
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Byte} joined by
	 *         {@code delimiter}
	 */
	public static String toString(final Byte[] array, final String delimiter) {
		return Arrays.toString(array, delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Byte} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@link Byte}
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Byte} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final Byte[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(array, delimiter, wrapper);
	}
}
