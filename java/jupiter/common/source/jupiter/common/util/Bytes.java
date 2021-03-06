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

import static java.lang.Byte.MAX_VALUE;
import static java.lang.Byte.MIN_VALUE;
import static jupiter.common.util.Characters.LOWER_CASE_DIGITS;
import static jupiter.common.util.Characters.UPPER_CASE_DIGITS;

import java.util.Collection;
import java.util.Comparator;
import java.util.Random;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.ByteParser;
import jupiter.common.map.parser.IParsers;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.set.ExtendedHashSet;

public class Bytes {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final byte[] EMPTY_PRIMITIVE_ARRAY = new byte[] {};
	public static final byte[][] EMPTY_PRIMITIVE_ARRAY_2D = new byte[][] {};
	public static final byte[][][] EMPTY_PRIMITIVE_ARRAY_3D = new byte[][][] {};

	public static final Byte[] EMPTY_ARRAY = new Byte[] {};
	public static final Byte[][] EMPTY_ARRAY_2D = new Byte[][] {};
	public static final Byte[][][] EMPTY_ARRAY_3D = new Byte[][][] {};

	protected static final ByteParser PARSER = IParsers.BYTE_PARSER;

	//////////////////////////////////////////////

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
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified {@code byte} values for order. Returns a negative integer, {@code 0}
	 * or a positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code byte} value to compare for order
	 * @param b the other {@code byte} value to compare against for order
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final byte a, final byte b) {
		return a - b;
	}

	//////////////////////////////////////////////

	/**
	 * Compares the specified {@code byte} arrays for order. Returns a negative integer, {@code 0}
	 * or a positive integer as {@code a} is less than, equal to or greater than {@code b} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@code byte} array to compare for order (may be {@code null})
	 * @param b the other {@code byte} array to compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final byte[] a, final byte[] b) {
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
	 * Returns a {@code byte} value converted from the specified {@code short} value.
	 * <p>
	 * @param value the {@code short} value to convert
	 * <p>
	 * @return a {@code byte} value converted from the specified {@code short} value
	 */
	public static byte convert(final short value) {
		if (value < MIN_VALUE || value > MAX_VALUE) {
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
		if (value < MIN_VALUE || value > MAX_VALUE) {
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
		if (value < MIN_VALUE || value > MAX_VALUE) {
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
		if (value < MIN_VALUE || value > MAX_VALUE) {
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
		if (value < MIN_VALUE || value > MAX_VALUE) {
			throw new ArithmeticException("Byte under/overflow");
		}
		return (byte) value;
	}

	/**
	 * Returns a {@link Byte} converted from the specified {@link Object}.
	 * <p>
	 * @param object the {@link Object} to convert (may be {@code null})
	 * <p>
	 * @return a {@link Byte} converted from the specified {@link Object}
	 */
	public static Byte convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts the specified source {@code byte} value to a representative unsigned {@code int}
	 * value written to the specified target {@code char} buffer.
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
	 * value written to the specified target {@code char} buffer.
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
	 * value written to the specified target {@code char} buffer.
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
			final int offset, final int length, final char... digits) {
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
	public static String toBinaryString(final byte[] array, final char... digits) {
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
	public static String toOctalString(final byte[] array, final char... digits) {
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
	public static String toHexString(final byte[] array, final char... digits) {
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
	 * Returns a {@code byte} value converted from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object the {@code T} object to convert
	 * <p>
	 * @return a {@code byte} value converted from the specified {@code T} object
	 */
	public static <T> byte toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code byte} array converted from the specified {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to convert
	 * <p>
	 * @return a {@code byte} array converted from the specified {@code byte} array
	 */
	public static byte[] toPrimitiveArray(final byte... array) {
		// Check the arguments
		if (array == null) {
			return null;
		}
		if (array.length == 0) {
			return EMPTY_PRIMITIVE_ARRAY;
		}

		// Copy the array to an array
		final byte[] output = new byte[array.length];
		System.arraycopy(array, 0, output, 0, array.length);
		return output;
	}

	/**
	 * Returns a {@code byte} array converted from the specified 2D {@code byte} array.
	 * <p>
	 * @param array2D the 2D {@code byte} array to convert
	 * <p>
	 * @return a {@code byte} array converted from the specified 2D {@code byte} array
	 */
	public static byte[] toPrimitiveArray(final byte[]... array2D) {
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
		final byte[] output = new byte[rowCount * columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array2D[i], 0, output, i * columnCount, columnCount);
		}
		return output;
	}

	/**
	 * Returns a {@code byte} array converted from the specified 3D {@code byte} array.
	 * <p>
	 * @param array3D the 3D {@code byte} array to convert
	 * <p>
	 * @return a {@code byte} array converted from the specified 3D {@code byte} array
	 */
	public static byte[] toPrimitiveArray(final byte[][]... array3D) {
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
		final byte[] output = new byte[rowCount * columnCount * depthCount];
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
	 * Returns a {@code byte} array converted from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@code byte} array converted from the specified {@code T} array
	 */
	public static <T> byte[] toPrimitiveArray(final T[] array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a {@code byte} array converted from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@code byte} array converted from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> byte[] asPrimitiveArray(final T... array) {
		return toPrimitiveArray(array);
	}

	/**
	 * Returns a {@code byte} array converted from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a {@code byte} array converted from the specified 2D {@code T} array
	 */
	public static <T> byte[] toPrimitiveArray(final T[][] array2D) {
		return PARSER.callToPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code byte} array converted from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a {@code byte} array converted from the specified 2D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> byte[] asPrimitiveArray(final T[]... array2D) {
		return toPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code byte} array converted from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a {@code byte} array converted from the specified 3D {@code T} array
	 */
	public static <T> byte[] toPrimitiveArray(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray(array3D);
	}

	/**
	 * Returns a {@code byte} array converted from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a {@code byte} array converted from the specified 3D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> byte[] asPrimitiveArray(final T[][]... array3D) {
		return toPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	public static byte[][] toPrimitiveArray2D(final byte[] array, final int rowCount) {
		// Check the arguments
		if (array == null) {
			return null;
		}
		if (array.length == 0 || rowCount == 0) {
			return EMPTY_PRIMITIVE_ARRAY_2D;
		}

		// Copy the array to a 2D array
		final int columnCount = array.length / rowCount;
		final byte[][] output2D = new byte[rowCount][columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array, i * columnCount, output2D[i], 0, columnCount);
		}
		return output2D;
	}

	public static byte[][] toPrimitiveArray2D(final byte[][] array2D) {
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
		final byte[][] output2D = new byte[rowCount][columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array2D[i], 0, output2D[i], 0, columnCount);
		}
		return output2D;
	}

	public static byte[][] toPrimitiveArray2D(final byte[][][] array3D) {
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
		final byte[][] output2D = new byte[rowCount][columnCount * depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array3D[i][j], 0, output2D[i], j * depthCount, depthCount);
			}
		}
		return output2D;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D {@code byte} array converted from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a 2D {@code byte} array converted from the specified 2D {@code T} array
	 */
	public static <T> byte[][] toPrimitiveArray2D(final T[][] array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 2D {@code byte} array converted from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a 2D {@code byte} array converted from the specified 2D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> byte[][] asPrimitiveArray2D(final T[]... array2D) {
		return toPrimitiveArray2D(array2D);
	}

	//////////////////////////////////////////////

	public static byte[][][] toPrimitiveArray3D(final byte[] array, final int rowCount,
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
		final byte[][][] output3D = new byte[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array, (i * columnCount + j) * depthCount, output3D[i][j], 0,
						depthCount);
			}
		}
		return output3D;
	}

	public static byte[][][] toPrimitiveArray3D(final byte[][] array2D,
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
		final byte[][][] output3D = new byte[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array2D[i], j * depthCount, output3D[i][j], 0, depthCount);
			}
		}
		return output3D;
	}

	public static byte[][][] toPrimitiveArray3D(final byte[][][] array3D) {
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
		final byte[][][] output3D = new byte[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array3D[i][j], 0, output3D[i][j], 0, depthCount);
			}
		}
		return output3D;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D {@code byte} array converted from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a 3D {@code byte} array converted from the specified 3D {@code T} array
	 */
	public static <T> byte[][][] toPrimitiveArray3D(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a 3D {@code byte} array converted from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a 3D {@code byte} array converted from the specified 3D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> byte[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code byte} array converted from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return a {@code byte} array converted from the specified {@link Collection}
	 */
	public static byte[] collectionToPrimitiveArray(final Collection<?> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Byte} converted from the specified {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to convert
	 * <p>
	 * @return an array of {@link Byte} converted from the specified {@code byte} array
	 */
	public static Byte[] toArray(final byte[] array) {
		final Byte[] newArray = new Byte[array.length];
		for (int i = 0; i < array.length; ++i) {
			newArray[i] = array[i];
		}
		return newArray;
	}

	/**
	 * Returns an array of {@link Byte} converted from the specified {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to convert
	 * <p>
	 * @return an array of {@link Byte} converted from the specified {@code byte} array
	 */
	public static Byte[] asArray(final byte... array) {
		return toArray(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D array of {@link Byte} converted from the specified 2D {@code byte} array.
	 * <p>
	 * @param array2D the 2D {@code byte} array to convert
	 * <p>
	 * @return a 2D array of {@link Byte} converted from the specified 2D {@code byte} array
	 */
	public static Byte[][] toArray2D(final byte[][] array2D) {
		final Byte[][] newArray2D = new Byte[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			newArray2D[i] = toArray(array2D[i]);
		}
		return newArray2D;
	}

	/**
	 * Returns a 2D array of {@link Byte} converted from the specified 2D {@code byte} array.
	 * <p>
	 * @param array2D the 2D {@code byte} array to convert
	 * <p>
	 * @return a 2D array of {@link Byte} converted from the specified 2D {@code byte} array
	 */
	public static Byte[][] asArray2D(final byte[]... array2D) {
		return toArray2D(array2D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D array of {@link Byte} converted from the specified 3D {@code byte} array.
	 * <p>
	 * @param array3D the 3D {@code byte} array to convert
	 * <p>
	 * @return a 3D array of {@link Byte} converted from the specified 3D {@code byte} array
	 */
	public static Byte[][][] toArray3D(final byte[][][] array3D) {
		final Byte[][][] newArray3D = new Byte[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			newArray3D[i] = toArray2D(array3D[i]);
		}
		return newArray3D;
	}

	/**
	 * Returns a 3D array of {@link Byte} converted from the specified 3D {@code byte} array.
	 * <p>
	 * @param array3D the 3D {@code byte} array to convert
	 * <p>
	 * @return a 3D array of {@link Byte} converted from the specified 3D {@code byte} array
	 */
	public static Byte[][][] asArray3D(final byte[][]... array3D) {
		return toArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an array of {@link Byte} converted from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an array of {@link Byte} converted from the specified {@link Collection}
	 */
	public static Byte[] collectionToArray(final Collection<?> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} converted from the specified {@code byte}
	 * array.
	 * <p>
	 * @param array the {@code byte} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} converted from the specified {@code byte}
	 *         array
	 */
	public static ExtendedList<Byte> toList(final byte[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} converted from the specified {@code byte}
	 * array.
	 * <p>
	 * @param array the {@code byte} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} converted from the specified {@code byte}
	 *         array
	 */
	public static ExtendedList<Byte> asList(final byte... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Byte} converted from the specified
	 * {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Byte} converted from the specified
	 *         {@code byte} array
	 */
	public static ExtendedLinkedList<Byte> toLinkedList(final byte[] array) {
		return PARSER.callToLinkedList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Byte} converted from the specified
	 * {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Byte} converted from the specified
	 *         {@code byte} array
	 */
	public static ExtendedLinkedList<Byte> asLinkedList(final byte... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} converted from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} converted from the specified {@code T} array
	 */
	public static <T> ExtendedList<Byte> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} converted from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} converted from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedList<Byte> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Byte} converted from the specified {@code T}
	 * array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Byte} converted from the specified {@code T}
	 *         array
	 */
	public static <T> ExtendedLinkedList<Byte> toLinkedList(final T[] array) {
		return PARSER.callToLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Byte} converted from the specified {@code T}
	 * array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Byte} converted from the specified {@code T}
	 *         array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedLinkedList<Byte> asLinkedList(final T... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Byte} converted from the specified
	 * {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Byte} converted from the specified
	 *         {@link Collection}
	 */
	public static ExtendedList<Byte> collectionToList(final Collection<?> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Byte} converted from the specified
	 * {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Byte} converted from the specified
	 *         {@link Collection}
	 */
	public static ExtendedLinkedList<Byte> collectionToLinkedList(
			final Collection<?> collection) {
		return PARSER.callCollectionToLinkedList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Byte} converted from the specified {@code byte}
	 * array.
	 * <p>
	 * @param array the {@code byte} array to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Byte} converted from the specified {@code byte}
	 *         array
	 */
	public static ExtendedHashSet<Byte> toSet(final byte[] array) {
		return PARSER.callToSet(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Byte} converted from the specified {@code byte}
	 * array.
	 * <p>
	 * @param array the {@code byte} array to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Byte} converted from the specified {@code byte}
	 *         array
	 */
	public static ExtendedHashSet<Byte> asSet(final byte... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Byte} converted from the specified {@code T}
	 * array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Byte} converted from the specified {@code T}
	 *         array
	 */
	public static <T> ExtendedHashSet<Byte> toSet(final T[] array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Byte} converted from the specified {@code T}
	 * array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Byte} converted from the specified {@code T}
	 *         array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedHashSet<Byte> asSet(final T... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Byte} converted from the specified
	 * {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Byte} converted from the specified
	 *         {@link Collection}
	 */
	public static ExtendedHashSet<Byte> collectionToSet(final Collection<?> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code byte} array of the specified length containing the sequence of numbers
	 * starting with {@code 0} and spaced by {@code 1}.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return a {@code byte} array of the specified length containing the sequence of numbers
	 *         starting with {@code 0} and spaced by {@code 1}
	 */
	public static byte[] createSequence(final int length) {
		return createSequence(length, (byte) 0, (byte) 1);
	}

	/**
	 * Creates a {@code byte} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by {@code 1}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return a {@code byte} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by {@code 1}
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
	 * @param length the length of the random sequence to create
	 * @param from   the {@code byte} lower bound of the random sequence to create (inclusive)
	 * @param to     the {@code byte} upper bound of the random sequence to create (exclusive)
	 * <p>
	 * @return a {@code byte} array of the specified length containing pseudorandom, uniformly
	 *         distributed {@code byte} values between the specified bounds
	 */
	public static byte[] createRandomSequence(final int length, final byte from, final byte to) {
		final byte[] array = new byte[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(from, to);
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
		return random(MIN_VALUE, MAX_VALUE);
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code byte} value between the specified
	 * bounds.
	 * <p>
	 * @param from the {@code byte} lower bound of the value to generate (inclusive)
	 * @param to   the {@code byte} upper bound of the value to generate (exclusive)
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code byte} value between the specified bounds
	 */
	public static byte random(final byte from, final byte to) {
		return convert(from + RANDOM.nextInt(to - from));
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
	// PARSERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code byte} array converted from the specified binary representative
	 * {@link String}.
	 * <p>
	 * @param binaryString the binary representative {@link String} to parse
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

	/**
	 * Returns a {@code byte} array converted from the specified octal representative
	 * {@link String}.
	 * <p>
	 * @param octalString the octal representative {@link String} to parse
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

	/**
	 * Returns a {@code byte} array converted from the specified hexadecimal representative
	 * {@link String}.
	 * <p>
	 * @param hexString the hexadecimal representative {@link String} to parse
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
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code byte} array containing the specified {@code byte} value and all the elements
	 * of the specified {@code byte} array.
	 * <p>
	 * @param a a {@code byte} value
	 * @param b another {@code byte} array (may be {@code null})
	 * <p>
	 * @return a {@code byte} array containing the specified {@code byte} value and all the elements
	 *         of the specified {@code byte} array
	 */
	public static byte[] concat(final byte a, final byte... b) {
		return concat(asPrimitiveArray(a), b);
	}

	/**
	 * Returns a {@code byte} array containing all the elements of the specified {@code byte}
	 * arrays.
	 * <p>
	 * @param a a {@code byte} array (may be {@code null})
	 * @param b another {@code byte} array (may be {@code null})
	 * <p>
	 * @return a {@code byte} array containing all the elements of the specified {@code byte} arrays
	 */
	public static byte[] concat(final byte[] a, final byte... b) {
		// Check the arguments
		if (a == null) {
			return clone(b);
		} else if (b == null) {
			return clone(a);
		}

		// Concatenate the arrays
		final byte[] newArray = new byte[a.length + b.length];
		System.arraycopy(a, 0, newArray, 0, a.length);
		System.arraycopy(b, 0, newArray, a.length, b.length);
		return newArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of elements in the specified 2D {@code byte} array.
	 * <p>
	 * @param array2D the 2D {@code byte} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 2D {@code byte} array
	 */
	public static int count(final byte[][] array2D) {
		int count = 0;
		if (array2D != null) {
			for (final byte[] array : array2D) {
				count += array.length;
			}
		}
		return count;
	}

	/**
	 * Returns the number of elements in the specified 3D {@code byte} array.
	 * <p>
	 * @param array3D the 3D {@code byte} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 3D {@code byte} array
	 */
	public static int count(final byte[][][] array3D) {
		int count = 0;
		if (array3D != null) {
			for (final byte[][] array2D : array3D) {
				count += count(array2D);
			}
		}
		return count;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code byte} token in the specified
	 * {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to count from (may be {@code null})
	 * @param token the {@code byte} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code byte} token in the specified
	 *         {@code byte} array
	 */
	public static int count(final byte[] array, final byte token) {
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
	 * Returns the number of occurrences of the specified {@code byte} token in the specified 2D
	 * {@code byte} array.
	 * <p>
	 * @param array2D the 2D {@code byte} array to count from (may be {@code null})
	 * @param token   the {@code byte} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code byte} token in the specified 2D
	 *         {@code byte} array
	 */
	public static int count(final byte[][] array2D, final byte token) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final byte[] array : array2D) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code byte} token in the specified 3D
	 * {@code byte} array.
	 * <p>
	 * @param array3D the 3D {@code byte} array to count from (may be {@code null})
	 * @param token   the {@code byte} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code byte} token in the specified 3D
	 *         {@code byte} array
	 */
	public static int count(final byte[][][] array3D, final byte token) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final byte[][] array2D : array3D) {
				occurrenceCount += count(array2D, token);
			}
		}
		return occurrenceCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code byte} tokens in the specified
	 * {@code byte} array.
	 * <p>
	 * @param array  the {@code byte} array to count from (may be {@code null})
	 * @param tokens the {@code byte} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code byte} tokens in the specified
	 *         {@code byte} array
	 */
	public static int count(final byte[] array, final byte... tokens) {
		int occurrenceCount = 0;
		if (array != null && tokens != null) {
			for (final byte token : tokens) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code byte} tokens in the specified 2D
	 * {@code byte} array.
	 * <p>
	 * @param array2D the 2D {@code byte} array to count from (may be {@code null})
	 * @param tokens  the {@code byte} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code byte} tokens in the specified 2D
	 *         {@code byte} array
	 */
	public static int count(final byte[][] array2D, final byte... tokens) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final byte[] array : array2D) {
				occurrenceCount += count(array, tokens);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code byte} tokens in the specified 3D
	 * {@code byte} array.
	 * <p>
	 * @param array3D the 3D {@code byte} array to count from (may be {@code null})
	 * @param tokens  the {@code byte} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code byte} tokens in the specified 3D
	 *         {@code byte} array
	 */
	public static int count(final byte[][][] array3D, final byte... tokens) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final byte[][] array2D : array3D) {
				occurrenceCount += count(array2D, tokens);
			}
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
	 * Returns a {@code byte} array containing all the elements of the specified {@code byte} array
	 * at the specified indices.
	 * <p>
	 * @param array   the {@code byte} array to filter from
	 * @param indices the indices to filter
	 * <p>
	 * @return a {@code byte} array containing all the elements of the specified {@code byte} array
	 *         at the specified indices
	 */
	public static byte[] filter(final byte[] array, final int... indices) {
		final byte[] newArray = new byte[indices.length];
		for (int i = 0; i < indices.length; ++i) {
			newArray[i] = array[indices[i]];
		}
		return newArray;
	}

	/**
	 * Returns a 2D {@code byte} array containing all the elements of the specified {@code byte}
	 * array at all the specified indices.
	 * <p>
	 * @param array   the {@code byte} array to filter from
	 * @param indices the array of indices to filter
	 * <p>
	 * @return a 2D {@code byte} array containing all the elements of the specified {@code byte}
	 *         array at all the specified indices
	 */
	public static byte[][] filterAll(final byte[] array, final int[]... indices) {
		final byte[][] newArray2D = new byte[indices.length][];
		for (int i = 0; i < indices.length; ++i) {
			newArray2D[i] = filter(array, indices[i]);
		}
		return newArray2D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code byte} value rounded down.
	 * <p>
	 * @param value a {@code byte} value
	 * <p>
	 * @return the middle of the specified {@code byte} value rounded down
	 */
	public static byte middle(final byte value) {
		return convert(value / 2);
	}

	/**
	 * Returns the middle of the specified {@code byte} lower and upper bounds rounded down.
	 * <p>
	 * @param from a {@code byte} value
	 * @param to   another {@code byte} value
	 * <p>
	 * @return the middle of the specified {@code byte} lower and upper bounds rounded down
	 */
	public static byte middle(final byte from, final byte to) {
		return convert(from + (to - from) / 2);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code byte} value rounded up.
	 * <p>
	 * @param value a {@code byte} value
	 * <p>
	 * @return the middle of the specified {@code byte} value rounded up
	 */
	public static byte middleUp(final byte value) {
		return convert((value + 1) / 2);
	}

	/**
	 * Returns the middle of the specified {@code byte} lower and upper bounds rounded up.
	 * <p>
	 * @param from a {@code byte} value
	 * @param to   another {@code byte} value
	 * <p>
	 * @return the middle of the specified {@code byte} lower and upper bounds rounded up
	 */
	public static byte middleUp(final byte from, final byte to) {
		return convert(from + (to - from + 1) / 2);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the element at the specified index from the specified {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to remove from
	 * @param index the index of the element to remove
	 * <p>
	 * @return the specified {@code byte} array without the element at the specified index
	 */
	public static byte[] remove(final byte[] array, final int index) {
		final byte[] newArray = new byte[array.length - 1];
		System.arraycopy(array, 0, newArray, 0, index);
		System.arraycopy(array, index + 1, newArray, index, array.length - index - 1);
		return newArray;
	}

	/**
	 * Removes all the occurrences of the specified {@code byte} value from the specified
	 * {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to remove from
	 * @param value the {@code byte} value to remove (may be {@code null})
	 * <p>
	 * @return the specified {@code byte} array without the specified {@code byte} value
	 */
	public static byte[] removeAll(final byte[] array, final byte value) {
		final byte[] newArray = new byte[array.length - count(array, value)];
		int index = 0;
		for (int i = 0; i < array.length; ++i) {
			if (array[i] != value) {
				newArray[index++] = array[i];
			}
		}
		return newArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void reverse(final byte... array) {
		reverse(array, 0, array.length);
	}

	public static void reverse(final byte[] array, final int fromIndex, final int toIndex) {
		final int limit = Integers.middle(toIndex - fromIndex);
		for (int i = 0; i < limit; ++i) {
			swap(array, fromIndex + i, toIndex - 1 - i);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shuffles the specified {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to shuffle
	 */
	public static void shuffle(final byte... array) {
		shuffle(array, 0, array.length);
	}

	/**
	 * Shuffles the specified {@code byte} array between the specified indices.
	 * <p>
	 * @param array     the {@code byte} array to shuffle
	 * @param fromIndex the index to start shuffling from (inclusive)
	 * @param toIndex   the index to finish shuffling at (exclusive)
	 */
	public static void shuffle(final byte[] array, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			swap(array, i, Integers.random(fromIndex, toIndex));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void swap(final byte[] array, final int i, final int j) {
		final byte element = array[i];
		array[i] = array[j];
		array[j] = element;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static byte[] take(final byte[] array, final int fromIndex, final int length) {
		if (isNullOrEmpty(array)) {
			return array;
		}
		final int maxLength = Math.min(length, array.length - fromIndex);
		final byte[] subarray = new byte[maxLength];
		System.arraycopy(array, fromIndex, subarray, 0, maxLength);
		return subarray;
	}

	//////////////////////////////////////////////

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

	//////////////////////////////////////////////

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

	public static int tally(final byte value, final byte[] boundaries) {
		for (int i = 0; i < boundaries.length; ++i) {
			if (value < boundaries[i]) {
				return i;
			}
		}
		return boundaries.length;
	}

	public static int[] tally(final byte[] array, final byte[] boundaries) {
		final int[] intervals = new int[array.length];
		for (int i = 0; i < array.length; ++i) {
			intervals[i] = tally(array[i], boundaries);
		}
		return intervals;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified {@code byte} array.
	 * <p>
	 * @param rowCount the number of rows of the {@code byte} array
	 * @param array    a {@code byte} array
	 * <p>
	 * @return the transpose of the specified {@code byte} array
	 */
	public static byte[] transpose(final int rowCount, final byte... array) {
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
	 * @param array2D the 2D {@code byte} array to convert
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
		if (array != null) {
			return findFirstIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findFirstIndex(final byte[] array, final byte token, final int from) {
		if (array != null) {
			return findFirstIndex(array, token, from, array.length);
		}
		return -1;
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
		if (array != null) {
			return findLastIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findLastIndex(final byte[] array, final byte token, final int from) {
		if (array != null) {
			return findLastIndex(array, token, from, array.length);
		}
		return -1;
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
	 * Tests whether the specified {@link Object} is an instance of {@link Byte}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Byte},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Byte;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code byte} value or a
	 * {@link Byte}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code byte} value or
	 *         a {@link Byte}, {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
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
	public static boolean isPrimitiveFrom(final Class<?> c) {
		return byte.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Object} is an instance of {@code byte} array.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@code byte} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArray(final Object object) {
		return object instanceof byte[];
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code byte} array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code byte} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArrayFrom(final Class<?> c) {
		return byte[].class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code byte} array is {@code null} or empty.
	 * <p>
	 * @param array the {@code byte} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code byte} array is {@code null} or empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final byte[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Tests whether the specified {@code byte} array is non-{@code null} and empty.
	 * <p>
	 * @param array the {@code byte} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code byte} array is non-{@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final byte[] array) {
		return array != null && array.length == 0;
	}

	/**
	 * Tests whether the specified {@code byte} array is non-{@code null} and non-empty.
	 * <p>
	 * @param array the {@code byte} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code byte} array is non-{@code null} and non-empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNonEmpty(final byte[] array) {
		return array != null && array.length > 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

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
		return isBetween(value, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@code byte} value is between the specified {@code byte} lower
	 * and upper bounds.
	 * <p>
	 * @param value            the {@code byte} value to test
	 * @param from             the {@code byte} lower bound to test against (inclusive)
	 * @param to               the {@code byte} upper bound to test against
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code byte} value is between the specified
	 *         {@code byte} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final byte value, final byte from, final byte to,
			final boolean isUpperInclusive) {
		return isBetween(value, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code byte} value is between the specified {@code byte} lower
	 * and upper bounds.
	 * <p>
	 * @param value            the {@code byte} value to test
	 * @param from             the {@code byte} lower bound to test against
	 * @param to               the {@code byte} upper bound to test against
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code byte} value is between the specified
	 *         {@code byte} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final byte value, final byte from, final byte to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return (isLowerInclusive ? value >= from : value > from) &&
				(isUpperInclusive ? value <= to : value < to);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code byte} array is between the specified lower and upper bound
	 * {@code byte} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array the {@code byte} array to test (may be {@code null})
	 * @param from  the lower bound {@code byte} array to test against (inclusive) (may be
	 *              {@code null})
	 * @param to    the upper bound {@code byte} array to test against (exclusive) (may be
	 *              {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code byte} array is between the specified lower and
	 *         upper bound {@code byte} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final byte[] array, final byte[] from, final byte[] to) {
		return isBetween(array, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@code byte} array is between the specified lower and upper bound
	 * {@code byte} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array            the {@code byte} array to test (may be {@code null})
	 * @param from             the lower bound {@code byte} array to test against (inclusive) (may
	 *                         be {@code null})
	 * @param to               the upper bound {@code byte} array to test against (may be
	 *                         {@code null})
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code byte} array is between the specified lower and
	 *         upper bound {@code byte} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final byte[] array, final byte[] from, final byte[] to,
			final boolean isUpperInclusive) {
		return isBetween(array, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code byte} array is between the specified lower and upper bound
	 * {@code byte} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array            the {@code byte} array to test (may be {@code null})
	 * @param from             the lower bound {@code byte} array to test against (may be
	 *                         {@code null})
	 * @param to               the upper bound {@code byte} array to test against (may be
	 *                         {@code null})
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code byte} array is between the specified lower and
	 *         upper bound {@code byte} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final byte[] array, final byte[] from, final byte[] to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return (isLowerInclusive ? compare(array, from) >= 0 : compare(array, from) > 0) &&
				(isUpperInclusive ? compare(array, to) <= 0 : compare(array, to) < 0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code byte} array contains the specified {@code byte} token.
	 * <p>
	 * @param array the {@code byte} array to test (may be {@code null})
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
	 * @param array  the {@code byte} array to test (may be {@code null})
	 * @param tokens the {@code byte} tokens to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code byte} array contains any of the specified
	 *         {@code byte} tokens, {@code false} otherwise
	 */
	public static boolean containsAny(final byte[] array, final byte... tokens) {
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
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a clone of the specified {@code byte} array, or {@code null} if it is {@code null}.
	 * <p>
	 * @param array the {@code byte} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified {@code byte} array, or {@code null} if it is {@code null}
	 */
	public static byte[] clone(final byte... array) {
		// Check the arguments
		if (array == null) {
			return null;
		}

		// Clone the array
		return array.clone();
	}

	/**
	 * Clones the specified 2D {@code byte} array.
	 * <p>
	 * @param array2D the 2D {@code byte} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified 2D {@code byte} array, or {@code null} if it is {@code null}
	 */
	public static byte[][] clone(final byte[]... array2D) {
		// Check the arguments
		if (array2D == null) {
			return null;
		}

		// Clone the 2D array
		final byte[][] clone = new byte[array2D.length]
				[array2D.length > 0 ? array2D[0].length : 0];
		for (int i = 0; i < array2D.length; ++i) {
			clone[i] = clone(array2D[i]);
		}
		return clone;
	}

	/**
	 * Clones the specified 3D {@code byte} array.
	 * <p>
	 * @param array3D the 3D {@code byte} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified 3D {@code byte} array, or {@code null} if it is {@code null}
	 */
	public static byte[][][] clone(final byte[][][] array3D) {
		// Check the arguments
		if (array3D == null) {
			return null;
		}

		// Clone the 3D array
		final byte[][][] clone = new byte[array3D.length]
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
	 * @param a the {@code byte} array to compare for equality (may be {@code null})
	 * @param b the other {@code byte} array to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final byte[] a, final byte[] b) {
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
	 * @param a the 2D {@code byte} array to compare for equality (may be {@code null})
	 * @param b the other 2D {@code byte} array to compare against for equality (may be
	 *          {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final byte[][] a, final byte[][] b) {
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
	 * @param a the 3D {@code byte} array to compare for equality (may be {@code null})
	 * @param b the other 3D {@code byte} array to compare against for equality (may be
	 *          {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final byte[][][] a, final byte[][][] b) {
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
	 * Returns the hash code value for the specified {@code byte} array.
	 * <p>
	 * @param array the {@code byte} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code byte} array
	 */
	public static int hashCode(final byte... array) {
		return hashCodeWith(0, array);
	}

	/**
	 * Returns the hash code value for the specified {@code byte} array at the specified depth.
	 * <p>
	 * @param depth the depth to hash at
	 * @param array the {@code byte} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code byte} array at the specified depth
	 */
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
	 * @param array the {@code byte} array to convert
	 * <p>
	 * @return a representative {@link String} of the specified {@code byte} array
	 */
	public static String toString(final byte... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a representative {@link String} of the specified {@code byte} array joined with the
	 * specified {@code char} delimiter.
	 * <p>
	 * @param array     a {@code byte} array
	 * @param delimiter the {@code char} delimiter
	 * <p>
	 * @return a representative {@link String} of the specified {@code byte} array joined with the
	 *         specified {@code char} delimiter
	 */
	public static String toStringWith(final byte[] array, final char delimiter) {
		return Arrays.toStringWith(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code byte} array joined with the
	 * specified delimiting {@link String}.
	 * <p>
	 * @param array     a {@code byte} array
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@code byte} array joined with the
	 *         specified delimiting {@link String}
	 */
	public static String toStringWith(final byte[] array, final String delimiter) {
		return Arrays.toStringWith(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code byte} array wrapped by
	 * {@code wrapper}.
	 * <p>
	 * @param array   a {@code byte} array
	 * @param wrapper an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code byte} array wrapped by
	 *         {@code wrapper}
	 */
	public static String toStringWith(final byte[] array, final ObjectToStringMapper wrapper) {
		return Arrays.toStringWith(toArray(array), wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code byte} array joined with the
	 * specified delimiting {@link String} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     a {@code byte} array
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code byte} array joined with the
	 *         specified delimiting {@link String} and wrapped by {@code wrapper}
	 */
	public static String toStringWith(final byte[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toStringWith(toArray(array), delimiter, wrapper);
	}
}
