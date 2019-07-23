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
import jupiter.common.map.parser.ByteParser;
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
	 * Formats the specified source {@code byte} value into the specified target {@code char}
	 * buffer.
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
	 * Formats the specified source {@code byte} value into the specified target {@code char}
	 * buffer.
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
				useLowerCase ? Characters.LOWER_CASE_DIGITS : Characters.UPPER_CASE_DIGITS);
	}

	/**
	 * Formats the specified source {@code byte} value into the specified target {@code char} buffer
	 * using the specified digits.
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
		return toBinaryString(array, Characters.UPPER_CASE_DIGITS);
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
		return toOctalString(array, Characters.UPPER_CASE_DIGITS);
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
		return toHexString(array, useLowerCase ? Characters.LOWER_CASE_DIGITS :
				Characters.UPPER_CASE_DIGITS);
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
	 * @param object a {@code T} object
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
	 * @param <T>   the component type of the array
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
	 * @param <T>   the component type of the array
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
	 * @param <T>     the component type of the array
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
	 * @param <T>     the component type of the array
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
	 * @param <T>     the component type of the array
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
	 * @param <T>     the component type of the array
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
	 * @param <T>     the component type of the array
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
	 * @param <T>     the component type of the array
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
	 * @param <T>     the component type of the array
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
	 * @param <T>     the component type of the array
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code byte} array from the specified 3D {@code T} array
	 */
	public static <T> byte[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a {@code byte} array from the specified {@link Collection} of type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@code byte} array from the specified {@link Collection} of type {@code T}
	 */
	public static <T> byte[] collectionToPrimitiveArray(final Collection<T> collection) {
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
		final Byte[] result = new Byte[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = array[i];
		}
		return result;
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
		final Byte[][] result = new Byte[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = toArray(array2D[i]);
		}
		return result;
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
		final Byte[][][] result = new Byte[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = toArray2D(array3D[i]);
		}
		return result;
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
	 * Returns an array of {@link Byte} from the specified {@link Collection} of type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@link Byte} from the specified {@link Collection} of type {@code T}
	 */
	public static <T> Byte[] collectionToArray(final Collection<T> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link List} of {@link Byte} from the specified {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * <p>
	 * @return a {@link List} of {@link Byte} from the specified {@code byte} array
	 */
	public static List<Byte> toList(final byte[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns a {@link List} of {@link Byte} from the specified {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * <p>
	 * @return a {@link List} of {@link Byte} from the specified {@code byte} array
	 */
	public static List<Byte> asList(final byte... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} from the specified {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} from the specified {@code byte} array
	 */
	public static ExtendedList<Byte> toExtendedList(final byte[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} from the specified {@code byte} array.
	 * <p>
	 * @param array a {@code byte} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} from the specified {@code byte} array
	 */
	public static ExtendedList<Byte> asExtendedList(final byte... array) {
		return toExtendedList(array);
	}

	/**
	 * Returns a {@link List} of {@link Byte} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link List} of {@link Byte} from the specified {@code T} array
	 */
	public static <T> List<Byte> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Byte} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link List} of {@link Byte} from the specified {@code T} array
	 */
	public static <T> List<Byte> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Byte> toExtendedList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Byte> asExtendedList(final T... array) {
		return toExtendedList(array);
	}

	/**
	 * Returns a {@link List} of {@link Byte} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Byte} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> List<Byte> collectionToList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> ExtendedList<Byte> collectionToExtendedList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
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
	 * @param <T>   the component type of the array
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
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Byte} from the specified {@code T} array
	 */
	public static <T> Set<Byte> asSet(final T... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Byte} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Byte} from the specified {@link Collection} of type {@code T}
	 */
	public static <T> Set<Byte> collectionToSet(final Collection<T> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
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
	 * Returns a pseudorandom, uniformly distributed {@code byte} value between {@code lowerBound}
	 * (inclusive) and {@code upperBound} (exclusive).
	 * <p>
	 * @param lowerBound the lower bound of the {@code byte} value to create
	 * @param upperBound the upper bound of the {@code byte} value to create
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code byte} value between {@code lowerBound}
	 *         (inclusive) and {@code upperBound} (exclusive)
	 */
	public static byte random(final byte lowerBound, final byte upperBound) {
		return convert(lowerBound + RANDOM.nextDouble() * (upperBound - lowerBound));
	}

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

	////////////////////////////////////////////////////////////////////////////////////////////////

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
	 * distributed {@code byte} values between {@code lowerBound} (inclusive) and {@code upperBound}
	 * (exclusive).
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create
	 * @param upperBound the upper bound of the random sequence to create
	 * <p>
	 * @return a {@code byte} array of the specified length containing pseudorandom, uniformly
	 *         distributed {@code byte} values between {@code lowerBound} (inclusive) and
	 *         {@code upperBound} (exclusive)
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
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void fill(final byte[] array, final byte value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
	}

	public static void fill(final byte[][] array2D, final byte value) {
		for (final byte[] array : array2D) {
			fill(array, value);
		}
	}

	public static void fill(final byte[][][] array3D, final byte value) {
		for (final byte[][] array2D : array3D) {
			fill(array2D, value);
		}
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
		final byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
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

	public static byte[] take(final byte... array) {
		return take(array, 0, array.length);
	}

	public static byte[] take(final byte[] array, final int from, final int length) {
		final int maxLength = Math.min(length, array.length - from);
		final byte[] result = new byte[maxLength];
		System.arraycopy(array, from, result, 0, maxLength);
		return result;
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
		final byte[] result = new byte[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, result, i * columnCount,
					columnCount);
		}
		return result;
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
		final byte[] result = new byte[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					result, i * length, length);
		}
		return result;
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

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code array} contains {@code token}.
	 * <p>
	 * @param array a {@code byte} array
	 * @param token the {@code byte} value to test for presence
	 * <p>
	 * @return {@code true} if {@code array} contains {@code token}, {@code false} otherwise
	 */
	public static boolean contains(final byte[] array, final byte token) {
		if (array == null) {
			return false;
		}
		for (final byte element : array) {
			if (element == token) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether {@code array} contains any {@code tokens}.
	 * <p>
	 * @param array  a {@code byte} array
	 * @param tokens the {@code byte} array to test for presence
	 * <p>
	 * @return {@code true} if {@code array} contains any {@code tokens}, {@code false} otherwise
	 */
	public static boolean containsAny(final byte[] array, final byte[] tokens) {
		if (array == null) {
			return false;
		}
		for (final byte token : tokens) {
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


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a clone of the specified {@code byte} array, or {@code null} if {@code array} is
	 * {@code null}.
	 * <p>
	 * @param array a {@code byte} array
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
	 * Returns the hash code value for the specified {@code byte} value.
	 * <p>
	 * @param value a {@code byte} value
	 * <p>
	 * @return the hash code value for the specified {@code byte} value
	 */
	public static int hashCode(final byte value) {
		return Objects.hashCode((int) value, value >>> Bits.HALF_LONG_BITS_COUNT);
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
