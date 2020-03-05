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

import static java.lang.Float.MAX_VALUE;
import static java.lang.Float.MIN_VALUE;
import static jupiter.common.util.Formats.DECIMAL_FORMAT;

import java.util.Collection;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.FloatParser;
import jupiter.common.map.parser.IParsers;
import jupiter.common.math.Maths;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;

public class Floats {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final float[] EMPTY_PRIMITIVE_ARRAY = new float[] {};
	public static final Float[] EMPTY_ARRAY = new Float[] {};

	protected static final FloatParser PARSER = IParsers.FLOAT_PARSER;

	public static volatile Random RANDOM = new Random();

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Comparator<Float> COMPARATOR = new Comparator<Float>() {
		@Override
		public int compare(final Float a, final Float b) {
			return Floats.compare(a, b);
		}
	};

	public static final Comparator<float[]> ARRAY_COMPARATOR = new Comparator<float[]>() {
		@Override
		public int compare(final float[] a, final float[] b) {
			return Floats.compare(a, b);
		}
	};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Floats}.
	 */
	protected Floats() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static float getDecimalPart(final float value) {
		return value - Maths.floorToShort(value);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code float} value converted from the specified {@code double} value.
	 * <p>
	 * @param value the {@code double} value to convert
	 * <p>
	 * @return a {@code float} value converted from the specified {@code double} value
	 */
	public static float convert(final double value) {
		if (value < MIN_VALUE || value > MAX_VALUE) {
			throw new ArithmeticException("Float under/overflow");
		}
		return (float) value;
	}

	/**
	 * Returns a {@link Float} converted from the specified {@link Object}.
	 * <p>
	 * @param object the {@link Object} to convert
	 * <p>
	 * @return a {@link Float} converted from the specified {@link Object}
	 */
	public static Float convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code float} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object the {@code T} object to convert
	 * <p>
	 * @return a {@code float} value from the specified {@code T} object
	 */
	public static <T> float toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code float} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@code float} array from the specified {@code T} array
	 */
	public static <T> float[] toPrimitiveArray(final T[] array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a {@code float} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@code float} array from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> float[] asPrimitiveArray(final T... array) {
		return toPrimitiveArray(array);
	}

	/**
	 * Returns a {@code float} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a {@code float} array from the specified 2D {@code T} array
	 */
	public static <T> float[] toPrimitiveArray(final T[][] array2D) {
		return PARSER.callToPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code float} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a {@code float} array from the specified 2D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> float[] asPrimitiveArray(final T[]... array2D) {
		return toPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code float} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a {@code float} array from the specified 3D {@code T} array
	 */
	public static <T> float[] toPrimitiveArray(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray(array3D);
	}

	/**
	 * Returns a {@code float} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a {@code float} array from the specified 3D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> float[] asPrimitiveArray(final T[][]... array3D) {
		return toPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D {@code float} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a 2D {@code float} array from the specified 2D {@code T} array
	 */
	public static <T> float[][] toPrimitiveArray2D(final T[][] array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 2D {@code float} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a 2D {@code float} array from the specified 2D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> float[][] asPrimitiveArray2D(final T[]... array2D) {
		return toPrimitiveArray2D(array2D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D {@code float} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a 3D {@code float} array from the specified 3D {@code T} array
	 */
	public static <T> float[][][] toPrimitiveArray3D(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a 3D {@code float} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a 3D {@code float} array from the specified 3D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> float[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code float} array from the specified {@link Collection}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection the {@link Collection} of {@code E} element type to convert
	 * <p>
	 * @return a {@code float} array from the specified {@link Collection}
	 */
	public static <E> float[] collectionToPrimitiveArray(final Collection<E> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Float} from the specified {@code float} array.
	 * <p>
	 * @param array the {@code float} array to convert
	 * <p>
	 * @return an array of {@link Float} from the specified {@code float} array
	 */
	public static Float[] toArray(final float[] array) {
		final Float[] convertedArray = new Float[array.length];
		for (int i = 0; i < array.length; ++i) {
			convertedArray[i] = array[i];
		}
		return convertedArray;
	}

	/**
	 * Returns an array of {@link Float} from the specified {@code float} array.
	 * <p>
	 * @param array the {@code float} array to convert
	 * <p>
	 * @return an array of {@link Float} from the specified {@code float} array
	 */
	public static Float[] asArray(final float... array) {
		return toArray(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D array of {@link Float} from the specified 2D {@code float} array.
	 * <p>
	 * @param array2D the 2D {@code float} array to convert
	 * <p>
	 * @return a 2D array of {@link Float} from the specified 2D {@code float} array
	 */
	public static Float[][] toArray2D(final float[][] array2D) {
		final Float[][] convertedArray2D = new Float[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			convertedArray2D[i] = toArray(array2D[i]);
		}
		return convertedArray2D;
	}

	/**
	 * Returns a 2D array of {@link Float} from the specified 2D {@code float} array.
	 * <p>
	 * @param array2D the 2D {@code float} array to convert
	 * <p>
	 * @return a 2D array of {@link Float} from the specified 2D {@code float} array
	 */
	public static Float[][] asArray2D(final float[]... array2D) {
		return toArray2D(array2D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D array of {@link Float} from the specified 3D {@code float} array.
	 * <p>
	 * @param array3D the 3D {@code float} array to convert
	 * <p>
	 * @return a 3D array of {@link Float} from the specified 3D {@code float} array
	 */
	public static Float[][][] toArray3D(final float[][][] array3D) {
		final Float[][][] convertedArray3D = new Float[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			convertedArray3D[i] = toArray2D(array3D[i]);
		}
		return convertedArray3D;
	}

	/**
	 * Returns a 3D array of {@link Float} from the specified 3D {@code float} array.
	 * <p>
	 * @param array3D the 3D {@code float} array to convert
	 * <p>
	 * @return a 3D array of {@link Float} from the specified 3D {@code float} array
	 */
	public static Float[][][] asArray3D(final float[][]... array3D) {
		return toArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an array of {@link Float} from the specified {@link Collection}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection the {@link Collection} of {@code E} element type to convert
	 * <p>
	 * @return an array of {@link Float} from the specified {@link Collection}
	 */
	public static <E> Float[] collectionToArray(final Collection<E> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Float} from the specified {@code float} array.
	 * <p>
	 * @param array the {@code float} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Float} from the specified {@code float} array
	 */
	public static ExtendedList<Float> toList(final float[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Float} from the specified {@code float} array.
	 * <p>
	 * @param array the {@code float} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Float} from the specified {@code float} array
	 */
	public static ExtendedList<Float> asList(final float... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Float} from the specified {@code float}
	 * array.
	 * <p>
	 * @param array the {@code float} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Float} from the specified {@code float} array
	 */
	public static ExtendedLinkedList<Float> toLinkedList(final float[] array) {
		return PARSER.callToLinkedList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Float} from the specified {@code float}
	 * array.
	 * <p>
	 * @param array the {@code float} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Float} from the specified {@code float} array
	 */
	public static ExtendedLinkedList<Float> asLinkedList(final float... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Float} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Float} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Float> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Float} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Float} from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedList<Float> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Float} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Float} from the specified {@code T} array
	 */
	public static <T> ExtendedLinkedList<Float> toLinkedList(final T[] array) {
		return PARSER.callToLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Float} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Float} from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedLinkedList<Float> asLinkedList(final T... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Float} from the specified {@link Collection}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection the {@link Collection} of {@code E} element type to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Float} from the specified {@link Collection}
	 */
	public static <E> ExtendedList<Float> collectionToList(final Collection<E> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Float} from the specified {@link Collection}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection the {@link Collection} of {@code E} element type to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Float} from the specified {@link Collection}
	 */
	public static <E> ExtendedLinkedList<Float> collectionToLinkedList(
			final Collection<E> collection) {
		return PARSER.callCollectionToLinkedList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Float} from the specified {@code float} array.
	 * <p>
	 * @param array the {@code float} array to convert
	 * <p>
	 * @return a {@link Set} of {@link Float} from the specified {@code float} array
	 */
	public static Set<Float> toSet(final float[] array) {
		return PARSER.callToSet(toArray(array));
	}

	/**
	 * Returns a {@link Set} of {@link Float} from the specified {@code float} array.
	 * <p>
	 * @param array the {@code float} array to convert
	 * <p>
	 * @return a {@link Set} of {@link Float} from the specified {@code float} array
	 */
	public static Set<Float> asSet(final float... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Float} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@link Set} of {@link Float} from the specified {@code T} array
	 */
	public static <T> Set<Float> toSet(final T[] array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Float} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@link Set} of {@link Float} from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> Set<Float> asSet(final T... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Float} from the specified {@link Collection}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection the {@link Collection} of {@code E} element type to convert
	 * <p>
	 * @return a {@link Set} of {@link Float} from the specified {@link Collection}
	 */
	public static <E> Set<Float> collectionToSet(final Collection<E> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code float} array of the specified length containing the sequence of numbers
	 * starting with {@code 0} and spaced by {@code 1}.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return a {@code float} array of the specified length containing the sequence of numbers
	 *         starting with {@code 0} and spaced by {@code 1}
	 */
	public static float[] createSequence(final int length) {
		return createSequence(length, 0f, 1f);
	}

	/**
	 * Creates a {@code float} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by {@code 1}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return a {@code float} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by {@code 1}
	 */
	public static float[] createSequence(final int length, final float from) {
		return createSequence(length, from, 1f);
	}

	/**
	 * Creates a {@code float} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return a {@code float} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by {@code step}
	 */
	public static float[] createSequence(final int length, final float from, final float step) {
		final float[] array = new float[length];
		float value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	//////////////////////////////////////////////

	/**
	 * Creates a random {@code float} array of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return a random {@code float} array of the specified length
	 */
	public static float[] createRandomSequence(final int length) {
		final float[] array = new float[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates a {@code float} array of the specified length containing pseudorandom, uniformly
	 * distributed {@code float} values between the specified bounds.
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create (inclusive)
	 * @param upperBound the upper bound of the random sequence to create (exclusive)
	 * <p>
	 * @return a {@code float} array of the specified length containing pseudorandom, uniformly
	 *         distributed {@code float} values between the specified bounds
	 */
	public static float[] createRandomSequence(final int length, final float lowerBound,
			final float upperBound) {
		final float[] array = new float[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(lowerBound, upperBound);
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code float} value between {@code 0f} and
	 * {@code 1f}.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code float} value between {@code 0f} and
	 *         {@code 1f}
	 */
	public static float random() {
		return RANDOM.nextFloat();
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code float} value between the specified
	 * bounds.
	 * <p>
	 * @param lowerBound the lower bound of the {@code float} value to generate (inclusive)
	 * @param upperBound the upper bound of the {@code float} value to generate (exclusive)
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code float} value between the specified
	 *         bounds
	 */
	public static float random(final float lowerBound, final float upperBound) {
		return lowerBound + RANDOM.nextFloat() * (upperBound - lowerBound);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code float} array of the specified length with the specified {@code float}
	 * element.
	 * <p>
	 * @param element the {@code float} element of the {@code float} array to create
	 * @param length  the length of the {@code float} array to create
	 * <p>
	 * @return a {@code float} array of the specified length with the specified {@code float}
	 *         element
	 */
	public static float[] repeat(final float element, final int length) {
		return fill(new float[length], element);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FORMATTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the formatted representative {@link String} of the specified {@code float} value.
	 * <p>
	 * @param value a {@code float} value
	 * <p>
	 * @return the formatted representative {@link String} of the specified {@code float} value
	 */
	public static String format(final float value) {
		return DECIMAL_FORMAT.format(value);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the percentage representative {@link String} of the specified {@code float} value.
	 * <p>
	 * @param value a {@code float} value
	 * <p>
	 * @return the percentage representative {@link String} of the specified {@code float} value
	 */
	public static String formatPercent(final float value) {
		return format(value * 100f) + "%";
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of elements in the specified 2D {@code float} array.
	 * <p>
	 * @param array2D the 2D {@code float} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 2D {@code float} array
	 */
	public static int count(final float[][] array2D) {
		int count = 0;
		if (array2D != null) {
			for (final float[] array : array2D) {
				count += array.length;
			}
		}
		return count;
	}

	/**
	 * Returns the number of elements in the specified 3D {@code float} array.
	 * <p>
	 * @param array3D the 3D {@code float} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 3D {@code float} array
	 */
	public static int count(final float[][][] array3D) {
		int count = 0;
		if (array3D != null) {
			for (final float[][] array2D : array3D) {
				count += count(array2D);
			}
		}
		return count;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code float} token in the specified
	 * {@code float} array.
	 * <p>
	 * @param array the {@code float} array to count from (may be {@code null})
	 * @param token the {@code float} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code float} token in the specified
	 *         {@code float} array
	 */
	public static int count(final float[] array, final float token) {
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
	 * Returns the number of occurrences of the specified {@code float} token in the specified 2D
	 * {@code float} array.
	 * <p>
	 * @param array2D the 2D {@code float} array to count from (may be {@code null})
	 * @param token   the {@code float} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code float} token in the specified 2D
	 *         {@code float} array
	 */
	public static int count(final float[][] array2D, final float token) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final float[] array : array2D) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code float} token in the specified 3D
	 * {@code float} array.
	 * <p>
	 * @param array3D the 3D {@code float} array to count from (may be {@code null})
	 * @param token   the {@code float} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code float} token in the specified 3D
	 *         {@code float} array
	 */
	public static int count(final float[][][] array3D, final float token) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final float[][] array2D : array3D) {
				occurrenceCount += count(array2D, token);
			}
		}
		return occurrenceCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code float} tokens in the specified
	 * {@code float} array.
	 * <p>
	 * @param array  the {@code float} array to count from (may be {@code null})
	 * @param tokens the {@code float} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code float} tokens in the specified
	 *         {@code float} array
	 */
	public static int count(final float[] array, final float[] tokens) {
		int occurrenceCount = 0;
		if (array != null && tokens != null) {
			for (final float token : tokens) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code float} tokens in the specified 2D
	 * {@code float} array.
	 * <p>
	 * @param array2D the 2D {@code float} array to count from (may be {@code null})
	 * @param tokens  the {@code float} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code float} tokens in the specified 2D
	 *         {@code float} array
	 */
	public static int count(final float[][] array2D, final float[] tokens) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final float[] array : array2D) {
				occurrenceCount += count(array, tokens);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code float} tokens in the specified 3D
	 * {@code float} array.
	 * <p>
	 * @param array3D the 3D {@code float} array to count from (may be {@code null})
	 * @param tokens  the {@code float} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code float} tokens in the specified 3D
	 *         {@code float} array
	 */
	public static int count(final float[][][] array3D, final float[] tokens) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final float[][] array2D : array3D) {
				occurrenceCount += count(array2D, tokens);
			}
		}
		return occurrenceCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static float[] fill(final float[] array, final float value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
		return array;
	}

	public static float[][] fill(final float[][] array2D, final float value) {
		for (final float[] array : array2D) {
			fill(array, value);
		}
		return array2D;
	}

	public static float[][][] fill(final float[][][] array3D, final float value) {
		for (final float[][] array2D : array3D) {
			fill(array2D, value);
		}
		return array3D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code float} array containing all the elements of the specified {@code float}
	 * array at the specified indexes.
	 * <p>
	 * @param array   the {@code float} array to filter from
	 * @param indexes the indexes to filter
	 * <p>
	 * @return a {@code float} array containing all the elements of the specified {@code float}
	 *         array at the specified indexes
	 */
	public static float[] filter(final float[] array, final int... indexes) {
		final float[] filteredArray = new float[indexes.length];
		for (int i = 0; i < indexes.length; ++i) {
			filteredArray[i] = array[indexes[i]];
		}
		return filteredArray;
	}

	/**
	 * Returns a 2D {@code float} array containing all the elements of the specified {@code float}
	 * array at all the specified indexes.
	 * <p>
	 * @param array   the {@code float} array to filter from
	 * @param indexes the array of indexes to filter
	 * <p>
	 * @return a 2D {@code float} array containing all the elements of the specified {@code float}
	 *         array at all the specified indexes
	 */
	public static float[][] filterAll(final float[] array, final int[]... indexes) {
		final float[][] filteredArrays = new float[indexes.length][];
		for (int i = 0; i < indexes.length; ++i) {
			filteredArrays[i] = filter(array, indexes[i]);
		}
		return filteredArrays;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code float} array containing the specified {@code float} value and all the
	 * elements of the specified {@code float} array.
	 * <p>
	 * @param a a {@code float} value
	 * @param b a {@code float} array (may be {@code null})
	 * <p>
	 * @return a {@code float} array containing the specified {@code float} value and all the
	 *         elements of the specified {@code float} array
	 */
	public static float[] merge(final float a, final float... b) {
		return merge(asPrimitiveArray(a), b);
	}

	/**
	 * Returns a {@code float} array containing all the elements of the specified {@code float}
	 * arrays.
	 * <p>
	 * @param a a {@code float} array (may be {@code null})
	 * @param b a {@code float} array (may be {@code null})
	 * <p>
	 * @return a {@code float} array containing all the elements of the specified {@code float}
	 *         arrays
	 */
	public static float[] merge(final float[] a, final float... b) {
		if (a == null) {
			return clone(b);
		} else if (b == null) {
			return clone(a);
		}
		final float[] mergedArray = new float[a.length + b.length];
		System.arraycopy(a, 0, mergedArray, 0, a.length);
		System.arraycopy(b, 0, mergedArray, a.length, b.length);
		return mergedArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code float} value.
	 * <p>
	 * @param value a {@code float} value
	 * <p>
	 * @return the middle of the specified {@code float} value
	 */
	public static float middle(final float value) {
		return value / 2f;
	}

	/**
	 * Returns the middle of the specified {@code float} lower and upper bounds.
	 * <p>
	 * @param lowerBound a {@code float} value
	 * @param upperBound another {@code float} value
	 * <p>
	 * @return the middle of the specified {@code float} lower and upper bounds
	 */
	public static float middle(final float lowerBound, final float upperBound) {
		return lowerBound + (upperBound - lowerBound) / 2f;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void reverse(final float[] array) {
		reverse(array, 0, array.length - 1);
	}

	public static void reverse(final float[] array, final int fromIndex) {
		reverse(array, fromIndex, array.length - 1);
	}

	public static void reverse(final float[] array, final int fromIndex, final int toIndex) {
		final int limit = Integers.middleUp(toIndex - fromIndex);
		for (int i = 0; i < limit; ++i) {
			swap(array, fromIndex + i, toIndex - i);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shuffles the specified {@code float} array.
	 * <p>
	 * @param array the {@code float} array to shuffle
	 */
	public static void shuffle(final float[] array) {
		shuffle(array, 0, array.length);
	}

	/**
	 * Shuffles the specified {@code float} array from the specified index.
	 * <p>
	 * @param array     the {@code float} array to shuffle
	 * @param fromIndex the index to start shuffling from (inclusive)
	 */
	public static void shuffle(final float[] array, final int fromIndex) {
		shuffle(array, fromIndex, array.length);
	}

	/**
	 * Shuffles the specified {@code float} array between the specified indexes.
	 * <p>
	 * @param array     the {@code float} array to shuffle
	 * @param fromIndex the index to start shuffling from (inclusive)
	 * @param toIndex   the index to finish shuffling at (exclusive)
	 */
	public static void shuffle(final float[] array, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			swap(array, i, Integers.random(fromIndex, toIndex));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void swap(final float[] array, final int i, final int j) {
		final float element = array[i];
		array[i] = array[j];
		array[j] = element;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static float[] take(final float... array) {
		return take(array, 0, array.length);
	}

	public static float[] take(final float[] array, final int fromIndex, final int length) {
		final int maxLength = Math.min(length, array.length - fromIndex);
		final float[] subarray = new float[maxLength];
		System.arraycopy(array, fromIndex, subarray, 0, maxLength);
		return subarray;
	}

	//////////////////////////////////////////////

	public static float[] take(final float[]... array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static float[] take(final float[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static float[] take(final float[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length - fromRow);
		final float[] subarray = new float[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, subarray,
					i * columnCount, columnCount);
		}
		return subarray;
	}

	//////////////////////////////////////////////

	public static float[] take(final float[][]... array3D) {
		return take(array3D, 0, array3D.length, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static float[] take(final float[][][] array3D, final int fromRow, final int rowCount) {
		return take(array3D, fromRow, rowCount, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static float[] take(final float[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0, array3D[0][0].length);
	}

	public static float[] take(final float[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length - fromRow);
		final int length = columnCount * depthCount;
		final float[] subarray = new float[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					subarray, i * length, length);
		}
		return subarray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified {@code float} array.
	 * <p>
	 * @param rowCount the number of rows of the {@code float} array
	 * @param array    a {@code float} array
	 * <p>
	 * @return the transpose of the specified {@code float} array
	 */
	public static float[] transpose(final int rowCount, final float[] array) {
		final int n = rowCount;
		final int m = array.length / rowCount;
		final float[] transpose = new float[m * n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i * n + j] = array[j * m + i];
			}
		}
		return transpose;
	}

	/**
	 * Returns the transpose of the specified 2D {@code float} array.
	 * <p>
	 * @param array2D the 2D {@code float} array to convert
	 * <p>
	 * @return the transpose of the specified 2D {@code float} array
	 */
	public static float[][] transpose(final float[]... array2D) {
		final int n = array2D.length;
		final int m = array2D[0].length;
		final float[][] transpose = new float[m][n];
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

	public static int findFirstIndex(final float[] array, final float token) {
		if (array != null) {
			return findFirstIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findFirstIndex(final float[] array, final float token, final int from) {
		if (array != null) {
			return findFirstIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static int findFirstIndex(final float[] array, final float token, final int from,
			final int to) {
		if (array != null) {
			for (int i = from; i < to; ++i) {
				if (equals(array[i], token)) {
					return i;
				}
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int findLastIndex(final float[] array, final float token) {
		if (array != null) {
			return findLastIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findLastIndex(final float[] array, final float token, final int from) {
		if (array != null) {
			return findLastIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static int findLastIndex(final float[] array, final float token, final int from,
			final int to) {
		if (array != null) {
			for (int i = to - 1; i >= from; --i) {
				if (equals(array[i], token)) {
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
	 * Tests whether the specified {@link Object} is an instance of {@link Float}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Float},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Float;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code float} value or a
	 * {@link Float}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code float} value or
	 *         a {@link Float}, {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return float.class.isAssignableFrom(c) || Float.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code float} value.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code float} value,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveFrom(final Class<?> c) {
		return float.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Object} is an instance of {@code float} array.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@code float} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArray(final Object object) {
		return object instanceof float[];
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code float} array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code float} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArrayFrom(final Class<?> c) {
		return float[].class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code float} array is {@code null} or empty.
	 * <p>
	 * @param array the {@code float} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code float} array is {@code null} or empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final float[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Tests whether the specified {@code float} array is non-{@code null} and empty.
	 * <p>
	 * @param array the {@code float} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code float} array is non-{@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final float[] array) {
		return array != null && array.length == 0;
	}

	/**
	 * Tests whether the specified {@code float} array is non-{@code null} and non-empty.
	 * <p>
	 * @param array the {@code float} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code float} array is non-{@code null} and non-empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNonEmpty(final float[] array) {
		return array != null && array.length > 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code float} value is between the specified {@code float} lower
	 * and upper bounds.
	 * <p>
	 * @param value the {@code float} value to test
	 * @param from  the {@code float} lower bound to test against (inclusive)
	 * @param to    the {@code float} upper bound to test against (exclusive)
	 * <p>
	 * @return {@code true} if the specified {@code float} value is between the specified
	 *         {@code float} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final float value, final float from, final float to) {
		return isBetween(value, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@code float} value is between the specified {@code float} lower
	 * and upper bounds.
	 * <p>
	 * @param value            the {@code float} value to test
	 * @param from             the {@code float} lower bound to test against (inclusive)
	 * @param to               the {@code float} upper bound to test against
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code float} value is between the specified
	 *         {@code float} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final float value, final float from, final float to,
			final boolean isUpperInclusive) {
		return isBetween(value, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code float} value is between the specified {@code float} lower
	 * and upper bounds.
	 * <p>
	 * @param value            the {@code float} value to test
	 * @param from             the {@code float} lower bound to test against
	 * @param to               the {@code float} upper bound to test against
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code float} value is between the specified
	 *         {@code float} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final float value, final float from, final float to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return (isLowerInclusive ? value >= from : value > from) &&
				(isUpperInclusive ? value <= to : value < to);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code float} array is between the specified lower and upper
	 * bound {@code float} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array the {@code float} array to test (may be {@code null})
	 * @param from  the lower bound {@code float} array to test against (inclusive) (may be
	 *              {@code null})
	 * @param to    the upper bound {@code float} array to test against (exclusive) (may be
	 *              {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code float} array is between the specified lower and
	 *         upper bound {@code float} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final float[] array, final float[] from, final float[] to) {
		return isBetween(array, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@code float} array is between the specified lower and upper
	 * bound {@code float} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array            the {@code float} array to test (may be {@code null})
	 * @param from             the lower bound {@code float} array to test against (inclusive) (may
	 *                         be {@code null})
	 * @param to               the upper bound {@code float} array to test against (may be
	 *                         {@code null})
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code float} array is between the specified lower and
	 *         upper bound {@code float} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final float[] array, final float[] from, final float[] to,
			final boolean isUpperInclusive) {
		return isBetween(array, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code float} array is between the specified lower and upper
	 * bound {@code float} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array            the {@code float} array to test (may be {@code null})
	 * @param from             the lower bound {@code float} array to test against (may be
	 *                         {@code null})
	 * @param to               the upper bound {@code float} array to test against (may be
	 *                         {@code null})
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code float} array is between the specified lower and
	 *         upper bound {@code float} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final float[] array, final float[] from, final float[] to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return (isLowerInclusive ? compare(array, from) >= 0 : compare(array, from) > 0) &&
				(isUpperInclusive ? compare(array, to) <= 0 : compare(array, to) < 0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code float} array contains the specified {@code float} token.
	 * <p>
	 * @param array the {@code float} array to test (may be {@code null})
	 * @param token the {@code float} token to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code float} array contains the specified
	 *         {@code float} token, {@code false} otherwise
	 */
	public static boolean contains(final float[] array, final float token) {
		return findFirstIndex(array, token) >= 0;
	}

	/**
	 * Tests whether the specified {@code float} array contains any of the specified {@code float}
	 * tokens.
	 * <p>
	 * @param array  the {@code float} array to test (may be {@code null})
	 * @param tokens the {@code float} tokens to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code float} array contains any of the specified
	 *         {@code float} tokens, {@code false} otherwise
	 */
	public static boolean containsAny(final float[] array, final float[] tokens) {
		if (array != null) {
			for (final float token : tokens) {
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
	 * Compares the specified {@code float} values for order. Returns a negative integer, {@code 0}
	 * or a positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code float} value to compare for order
	 * @param b the other {@code float} value to compare against for order
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final float a, final float b) {
		if (a < b) {
			return -1; // neither value is NaN
		}
		if (a > b) {
			return 1; // neither value is NaN
		}
		// @note cannot use floatToRawIntBits because of NaNs
		final int aBits = Float.floatToIntBits(a);
		final int bBits = Float.floatToIntBits(b);
		return aBits == bBits ? 0 : // the values are equal
				aBits < bBits ? -1 : // (-0f, 0f) or (!NaN, NaN)
						1; // (0f, -0f) or (NaN, !NaN)
	}

	//////////////////////////////////////////////

	/**
	 * Compares the specified {@code float} arrays for order. Returns a negative integer, {@code 0}
	 * or a positive integer as {@code a} is less than, equal to or greater than {@code b} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@code float} array to compare for order (may be {@code null})
	 * @param b the other {@code float} array to compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final float[] a, final float[] b) {
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
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a clone of the specified {@code float} array, or {@code null} if {@code array} is
	 * {@code null}.
	 * <p>
	 * @param array the {@code float} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified {@code float} array, or {@code null} if {@code array} is
	 *         {@code null}
	 */
	public static float[] clone(final float... array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the {@code float} value to compare for equality
	 * @param b the other {@code float} value to compare against for equality
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final float a, final float b) {
		return equals(a, b, Maths.FLOAT_TOLERANCE);
	}

	/**
	 * Tests whether {@code a} is equal to {@code b} within {@code tolerance}.
	 * <p>
	 * @param a         the {@code float} value to compare for equality
	 * @param b         the other {@code float} value to compare against for equality
	 * @param tolerance the tolerance level
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b} within {@code tolerance},
	 *         {@code false} otherwise
	 */
	public static boolean equals(final float a, final float b, final float tolerance) {
		return Maths.delta(a, b) <= tolerance;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the hash code value for the specified {@code float} array.
	 * <p>
	 * @param array the {@code float} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code float} array
	 */
	public static int hashCode(final float... array) {
		return hashCodeWith(0, array);
	}

	/**
	 * Returns the hash code value for the specified {@code float} array at the specified depth.
	 * <p>
	 * @param depth the depth to hash at
	 * @param array the {@code float} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code float} array at the specified depth
	 */
	public static int hashCodeWith(final int depth, final float... array) {
		if (array == null) {
			return 0;
		}
		switch (array.length) {
			case 0:
				return Bits.SEEDS[depth % Bits.SEEDS.length];
			case 1:
				return Float.floatToIntBits(array[0]);
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
	 * Returns a representative {@link String} of the specified {@code float} array.
	 * <p>
	 * @param array the {@code float} array to convert
	 * <p>
	 * @return a representative {@link String} of the specified {@code float} array
	 */
	public static String toString(final float... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a representative {@link String} of the specified {@code float} array joined by the
	 * specified delimiting {@link String}.
	 * <p>
	 * @param array     a {@code float} array
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@code float} array joined by the
	 *         specified delimiting {@link String}
	 */
	public static String toStringWith(final float[] array, final String delimiter) {
		return Arrays.toStringWith(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code float} array wrapped by
	 * {@code wrapper}.
	 * <p>
	 * @param array   a {@code float} array
	 * @param wrapper an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code float} array wrapped by
	 *         {@code wrapper}
	 */
	public static String toStringWith(final float[] array, final ObjectToStringMapper wrapper) {
		return Arrays.toStringWith(toArray(array), wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code float} array joined by the
	 * specified delimiting {@link String} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     a {@code float} array
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code float} array joined by the
	 *         specified delimiting {@link String} and wrapped by {@code wrapper}
	 */
	public static String toStringWith(final float[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toStringWith(toArray(array), delimiter, wrapper);
	}
}
