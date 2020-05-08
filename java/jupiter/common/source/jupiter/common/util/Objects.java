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

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.COLON;
import static jupiter.common.util.Formats.NEW_LINE;
import static jupiter.common.util.Strings.NULL;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import jupiter.common.time.Dates;

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
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the name of the {@link Class} of the specified {@link Object}, {@code null}
	 * otherwise.
	 * <p>
	 * @param object an {@link Object} (may be {@code null})
	 * <p>
	 * @return the name of the {@link Class} of the specified {@link Object}, {@code null} otherwise
	 */
	public static String getName(final Object object) {
		return getName(Classes.get(object));
	}

	/**
	 * Returns the name of the specified {@link Class}, {@code null} otherwise.
	 * <p>
	 * @param c a {@link Class} (may be {@code null})
	 * <p>
	 * @return the name of the specified {@link Class}, {@code null} otherwise
	 */
	public static String getName(final Class<?> c) {
		return c != null ? c.getName() : null;
	}

	//////////////////////////////////////////////

	public static String getSimpleName(final Object object) {
		return getSimpleName(Classes.get(object));
	}

	public static String getSimpleName(final Class<?> c) {
		return getSimpleName(getName(c));
	}

	public static String getSimpleName(final String className) {
		return className != null ? className.substring(className.lastIndexOf('.') + 1) : null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean isEnum(final Object object) {
		return isEnumFrom(Classes.get(object));
	}

	public static boolean isEnumFrom(final Class<?> c) {
		return c.isEnum();
	}

	public static boolean isImmutable(final Object object) {
		return isEnum(object) ||
				Booleans.is(object) ||
				Characters.is(object) ||
				Classes.is(object) ||
				Numbers.is(object) ||
				Strings.is(object);
	}

	public static boolean isImmutableFrom(final Class<?> c) {
		return isEnumFrom(c) ||
				isPrimitiveFrom(c) ||
				isVoidFrom(c) ||
				Booleans.isFrom(c) ||
				Characters.isFrom(c) ||
				Classes.isFrom(c) ||
				Numbers.isFrom(c) ||
				Strings.isFrom(c);
	}

	public static boolean isPrimitiveFrom(final Class<?> c) {
		return c.isPrimitive();
	}

	public static boolean isVoidFrom(final Class<?> c) {
		return void.class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is {@code null} or its representative
	 * {@link String} is {@code null} or empty.
	 * <p>
	 * @param object the {@link Object} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link Object} is {@code null} or its representative
	 *         {@link String} is {@code null} or empty, {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final Object object) {
		return object == null || Strings.isNullOrEmpty(object.toString());
	}

	/**
	 * Tests whether the specified {@link Object} is non-{@code null} and its representative
	 * {@link String} is non-{@code null} and empty.
	 * <p>
	 * @param object the {@link Object} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link Object} is non-{@code null} and its
	 *         representative {@link String} is non-{@code null} and empty, {@code false} otherwise
	 */
	public static boolean isEmpty(final Object object) {
		return object != null && Strings.isEmpty(object.toString());
	}

	/**
	 * Tests whether the specified {@link Object} is non-{@code null} and its representative
	 * {@link String} is non-{@code null} and non-empty.
	 * <p>
	 * @param object the {@link Object} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link Object} is non-{@code null} and its
	 *         representative {@link String} is non-{@code null} and non-empty, {@code false}
	 *         otherwise
	 */
	public static boolean isNonEmpty(final Object object) {
		return object != null && Strings.isNonEmpty(object.toString());
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
	 * Clones the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to clone
	 * @param object the {@code T} object to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified {@code T} object, or {@code null} if it is {@code null} or
	 *         not cloneable
	 */
	@SuppressWarnings({"cast", "unchecked"})
	public static <T> T clone(final T object) {
		// Check the arguments
		if (object == null) {
			return null;
		}

		// Clone the object
		try {
			if (Arrays.is(object)) {
				if (Booleans.isPrimitiveArray(object)) {
					return (T) Booleans.clone((boolean[]) object);
				} else if (Characters.isPrimitiveArray(object)) {
					return (T) Characters.clone((char[]) object);
				} else if (Bytes.isPrimitiveArray(object)) {
					return (T) Bytes.clone((byte[]) object);
				} else if (Shorts.isPrimitiveArray(object)) {
					return (T) Shorts.clone((short[]) object);
				} else if (Integers.isPrimitiveArray(object)) {
					return (T) Integers.clone((int[]) object);
				} else if (Longs.isPrimitiveArray(object)) {
					return (T) Longs.clone((long[]) object);
				} else if (Floats.isPrimitiveArray(object)) {
					return (T) Floats.clone((float[]) object);
				} else if (Doubles.isPrimitiveArray(object)) {
					return (T) Doubles.clone((double[]) object);
				}
				return (T) Arrays.clone((Object[]) object);
			} else if (isImmutable(object)) {
				return object;
			}
			return (T) Classes.get(object).getMethod("clone").invoke(object);
		} catch (final Exception ex) {
			IO.warn(ex);
		}
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the {@link Object} to compare for equality (may be {@code null})
	 * @param b the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final Object a, final Object b) {
		return a == b || a != null && b != null && a.equals(b);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the hash code value for the specified array.
	 * <p>
	 * @param array the array of {@link Object} to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified array
	 */
	public static int hashCode(final Object... array) {
		return hashCodeWith(0, array);
	}

	/**
	 * Returns the hash code value for the specified array at the specified depth.
	 * <p>
	 * @param depth the depth to hash at
	 * @param array the array of {@link Object} to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified array at the specified depth
	 */
	public static int hashCodeWith(final int depth, final Object... array) {
		if (array == null) {
			return 0;
		}
		switch (array.length) {
			case 0:
				return Bits.SEEDS[depth % Bits.SEEDS.length];
			case 1:
				final Object object = array[0];
				if (object == null) {
					return Bits.SEEDS[(depth + 1) % Bits.SEEDS.length];
				}
				if (Arrays.is(object)) {
					final int hashCode;
					if (Booleans.isPrimitiveArray(object)) {
						hashCode = Booleans.hashCodeWith(depth + 1, (boolean[]) object);
					} else if (Characters.isPrimitiveArray(object)) {
						hashCode = Characters.hashCodeWith(depth + 1, (char[]) object);
					} else if (Bytes.isPrimitiveArray(object)) {
						hashCode = Bytes.hashCodeWith(depth + 1, (byte[]) object);
					} else if (Shorts.isPrimitiveArray(object)) {
						hashCode = Shorts.hashCodeWith(depth + 1, (short[]) object);
					} else if (Integers.isPrimitiveArray(object)) {
						hashCode = Integers.hashCodeWith(depth + 1, (int[]) object);
					} else if (Longs.isPrimitiveArray(object)) {
						hashCode = Longs.hashCodeWith(depth + 1, (long[]) object);
					} else if (Floats.isPrimitiveArray(object)) {
						hashCode = Floats.hashCodeWith(depth + 1, (float[]) object);
					} else if (Doubles.isPrimitiveArray(object)) {
						hashCode = Doubles.hashCodeWith(depth + 1, (double[]) object);
					} else {
						hashCode = hashCodeWith(depth + 1, (Object[]) object);
					}
					return hashCodeWith(depth + 1, Bits.SEEDS[depth % Bits.SEEDS.length], hashCode);
				}
				return object.hashCode();
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
	 * Returns a representative {@link String} of the specified {@link Object}, or {@code "null"} if
	 * it is {@code null}.
	 * <p>
	 * @param object an {@link Object} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or {@code "null"} if
	 *         it is {@code null}
	 */
	public static String toString(final Object object) {
		// Check the arguments
		if (object == null) {
			return NULL;
		}

		// Convert the object to a representative string
		if (Arrays.is(object)) {
			if (Booleans.isPrimitiveArray(object)) {
				return Booleans.toString((boolean[]) object);
			} else if (Characters.isPrimitiveArray(object)) {
				return Characters.toString((char[]) object);
			} else if (Bytes.isPrimitiveArray(object)) {
				return Bytes.toString((byte[]) object);
			} else if (Shorts.isPrimitiveArray(object)) {
				return Shorts.toString((short[]) object);
			} else if (Integers.isPrimitiveArray(object)) {
				return Integers.toString((int[]) object);
			} else if (Longs.isPrimitiveArray(object)) {
				return Longs.toString((long[]) object);
			} else if (Floats.isPrimitiveArray(object)) {
				return Floats.toString((float[]) object);
			} else if (Doubles.isPrimitiveArray(object)) {
				return Doubles.toString((double[]) object);
			}
			return Arrays.toString((Object[]) object);
		} else if (Collections.is(object)) {
			return Collections.toString((Collection<?>) object);
		} else if (Dates.is(object)) {
			return Dates.toString((Date) object);
		} else if (Maps.is(object)) {
			return Maps.toString((Map<?, ?>) object);
		} else if (Numbers.is(object)) {
			return Numbers.toString((Number) object);
		}
		return String.valueOf(object);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or {@code "null"} if
	 * it is {@code null}, truncated to the specified length.
	 * <p>
	 * @param object an {@link Object} (may be {@code null})
	 * @param length the length of the representative {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or {@code "null"} if
	 *         it is {@code null}, truncated to the specified length
	 */
	public static String toString(final Object object, final int length) {
		return Strings.truncate(toString(object), length);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or {@code null} if
	 * it is {@code null} or {@code "null"}.
	 * <p>
	 * @param object an {@link Object} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or {@code null} if
	 *         it is {@code null} or {@code "null"}
	 */
	public static String toStringWithNull(final Object object) {
		final String string = toString(object);
		return !NULL.equals(string) ? string : null;
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or {@code null} if
	 * it is {@code null} or {@code "null"}, truncated to the specified length.
	 * <p>
	 * @param object an {@link Object} (may be {@code null})
	 * @param length the length of the representative {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or {@code null} if
	 *         it is {@code null} or {@code "null"}, truncated to the specified length
	 */
	public static String toStringWithNull(final Object object, final int length) {
		return Strings.truncate(toStringWithNull(object), length);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or
	 * {@code defaultString} if it is {@code null}.
	 * <p>
	 * @param object        the {@link Object} (may be {@code null})
	 * @param defaultString the default {@link String} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or
	 *         {@code defaultString} if it is {@code null}
	 */
	public static String toString(final Object object, final String defaultString) {
		return object != null ? toString(object) : defaultString;
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or
	 * {@code defaultString} if it is {@code null}, truncated to the specified length.
	 * <p>
	 * @param object        an {@link Object} (may be {@code null})
	 * @param defaultString the default {@link String} (may be {@code null})
	 * @param length        the length of the representative {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or
	 *         {@code defaultString} if it is {@code null}, truncated to the specified length
	 */
	public static String toString(final Object object, final String defaultString,
			final int length) {
		return Strings.truncate(toString(object, defaultString), length);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or
	 * {@code defaultString} if it is {@code null} or {@code "null"}.
	 * <p>
	 * @param object        the {@link Object} (may be {@code null})
	 * @param defaultString the default {@link String} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or
	 *         {@code defaultString} if it is {@code null} or {@code "null"}
	 */
	public static String toStringWithNull(final Object object, final String defaultString) {
		final String string = toString(object);
		return !NULL.equals(string) ? string : defaultString;
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or
	 * {@code defaultString} if it is {@code null} or {@code "null"}, truncated to the specified
	 * length.
	 * <p>
	 * @param object        the {@link Object} (may be {@code null})
	 * @param defaultString the default {@link String} (may be {@code null})
	 * @param length        the length of the representative {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or
	 *         {@code defaultString} if it is {@code null} or {@code "null"}, truncated to the
	 *         specified length
	 */
	public static String toStringWithNull(final Object object, final String defaultString,
			final int length) {
		return Strings.truncate(toStringWithNull(object, defaultString), length);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Exception}, or {@code "null"}
	 * if it is {@code null}.
	 * <p>
	 * @param exception an {@link Exception} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Exception}, or {@code "null"}
	 *         if it is {@code null}
	 */
	public static String toString(final Exception exception) {
		return toString(exception, 0);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Exception} with the specified
	 * number of {@link StackTraceElement}, or {@code "null"} if it is {@code null}.
	 * <p>
	 * @param exception              an {@link Exception} (may be {@code null})
	 * @param stackTraceElementCount the number of {@link StackTraceElement} to add
	 * <p>
	 * @return a representative {@link String} of the specified {@link Exception} with the specified
	 *         number of {@link StackTraceElement}, or {@code "null"} if it is {@code null}
	 */
	public static String toString(final Exception exception, final int stackTraceElementCount) {
		if (exception == null) {
			return NULL;
		}
		if (stackTraceElementCount > 0) {
			final StackTraceElement[] stackTraces = Arrays.<StackTraceElement>take(
					exception.getStackTrace(), 0, stackTraceElementCount);
			return Strings.join(exception.getLocalizedMessage(), COLON, NEW_LINE,
					Strings.joinWith(stackTraces, NEW_LINE));
		}
		return exception.getLocalizedMessage();
	}
}
