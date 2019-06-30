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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Objects {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Object[] EMPTY_ARRAY = new Object[] {};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Objects}.
	 */
	protected Objects() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean isBasic(final Class<?> c) {
		return isVoid(c) ||
				Booleans.is(c) ||
				Characters.is(c) ||
				Bytes.is(c) ||
				Shorts.is(c) ||
				Integers.is(c) ||
				Longs.is(c) ||
				Floats.is(c) ||
				Doubles.is(c) ||
				Strings.is(c) ||
				Class.class.isAssignableFrom(c);
	}

	public static boolean isVoid(final Class<?> c) {
		return void.class.isAssignableFrom(c);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code object} is {@code null} or its representative {@link String} is empty.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if {@code object} is {@code null} or its representative {@link String}
	 *         is empty, {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final Object object) {
		return object == null || Strings.isNullOrEmpty(object.toString());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean hasToString(final Class<?> c) {
		final Method[] methods = c.getDeclaredMethods();
		for (final Method method : methods) {
			if (method.getName().equals("toString")) {
				return true;
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to clone
	 * @param object the {@code T} object to clone
	 * <p>
	 * @return a copy of the specified {@code T} object
	 * <p>
	 * @throws CloneNotSupportedException if the type {@code T} does not implement {@link Cloneable}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	public static <T> T clone(final T object)
			throws CloneNotSupportedException {
		if (object == null) {
			return null;
		}
		try {
			final Class<?> c = object.getClass();
			if (c.isArray()) {
				if (Bytes.isPrimitiveArray(c)) {
					return (T) Bytes.clone((byte[]) object);
				} else if (Shorts.isPrimitiveArray(c)) {
					return (T) Shorts.clone((short[]) object);
				} else if (Integers.isPrimitiveArray(c)) {
					return (T) Integers.clone((int[]) object);
				} else if (Longs.isPrimitiveArray(c)) {
					return (T) Longs.clone((long[]) object);
				} else if (Floats.isPrimitiveArray(c)) {
					return (T) Floats.clone((float[]) object);
				} else if (Doubles.isPrimitiveArray(c)) {
					return (T) Doubles.clone((double[]) object);
				}
				return (T) Arrays.clone((Object[]) object);
			} else if (isBasic(c)) {
				return object;
			}
			return (T) c.getMethod("clone").invoke(object);
		} catch (final IllegalAccessException ex) {
			throw new CloneNotSupportedException(Strings.toString(ex));
		} catch (final IllegalArgumentException ex) {
			throw new CloneNotSupportedException(Strings.toString(ex));
		} catch (final InvocationTargetException ex) {
			throw new CloneNotSupportedException(Strings.toString(ex));
		} catch (final NoSuchMethodException ex) {
			throw new CloneNotSupportedException(Strings.toString(ex));
		} catch (final SecurityException ex) {
			throw new CloneNotSupportedException(Strings.toString(ex));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the {@link Object} to compare for equality
	 * @param b the other {@link Object} to compare against for equality
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final Object a, final Object b) {
		if (a == null) {
			return b == null;
		}
		return b != null && a.equals(b);
	}

	/**
	 * Returns the hash code value for the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to hash
	 * @param array the {@code T} array to hash
	 * <p>
	 * @return the hash code value for the specified {@code T} array
	 */
	public static <T> int hashCode(final T... array) {
		return hashCodeWith(0, array);
	}

	/**
	 * Returns the hash code value for the specified {@code T} array at the specified depth.
	 * <p>
	 * @param <T>   the component type of the array to hash
	 * @param array the {@code T} array to hash
	 * @param depth the depth to hash at
	 * <p>
	 * @return the hash code value for the specified {@code T} array at the specified depth
	 */
	@SuppressWarnings("unchecked")
	public static <T> int hashCodeWith(final int depth, final T... array) {
		if (array == null) {
			return 0;
		}
		switch (array.length) {
			case 0:
				return Bits.SEEDS[depth % Bits.SEEDS.length];
			case 1:
				final T element = array[0];
				if (element instanceof Object[]) {
					return hashCodeWith(depth + 1, Bits.SEEDS[depth % Bits.SEEDS.length],
							hashCodeWith(depth + 1, (Object[]) element));
				} else if (element != null) {
					return element.hashCode();
				}
				return Bits.SEEDS[(depth + 1) % Bits.SEEDS.length];
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
}
