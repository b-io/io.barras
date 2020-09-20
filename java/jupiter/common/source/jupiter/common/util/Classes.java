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

import static jupiter.common.util.Arrays.isNullOrEmpty;

public class Classes {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link Class} of {@link Object}.
	 */
	public static Class<Object> OBJECT_CLASS = Object.class;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Classes}.
	 */
	protected Classes() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Class} of the specified {@link Object}, or {@code null} if it is
	 * {@code null}.
	 * <p>
	 * @param object an {@link Object} (may be {@code null})
	 * <p>
	 * @return the {@link Class} of the specified {@link Object}, or {@code null} if it is
	 *         {@code null}
	 */
	public static Class<?> get(final Object object) {
		return object != null ? object.getClass() : null;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the common ancestor {@link Class} of the specified classes, or {@code null} if both
	 * of them are {@code null}.
	 * <p>
	 * @param c1 a {@link Class} (may be {@code null})
	 * @param c2 a {@link Class} (may be {@code null})
	 * <p>
	 * @return the common ancestor {@link Class} of the specified classes, or {@code null} if both
	 *         of them are {@code null}
	 */
	public static Class<?> getCommonAncestor(final Class<?> c1, final Class<?> c2) {
		// Check the arguments
		if (c1 == null) {
			return c2;
		}
		if (c2 == null) {
			return c1;
		}
		if (c1.isAssignableFrom(c2)) {
			return c1;
		}
		if (c2.isAssignableFrom(c1)) {
			return c2;
		}

		// Return the common ancestor of the classes
		Class<?> ancestor = c1;
		do {
			ancestor = ancestor.getSuperclass();
			if (ancestor == null || ancestor == OBJECT_CLASS) {
				return OBJECT_CLASS;
			}
		} while (!ancestor.isAssignableFrom(c2));
		return ancestor;
	}

	/**
	 * Returns the common ancestor {@link Class} of the specified classes, or {@code null} if all of
	 * them are {@code null}.
	 * <p>
	 * @param classes an array of {@link Class} (may be {@code null})
	 * <p>
	 * @return the common ancestor {@link Class} of the specified classes, or {@code null} if all of
	 *         them are {@code null}
	 */
	public static Class<?> getCommonAncestor(final Class<?>... classes) {
		// Check the arguments
		if (isNullOrEmpty(classes)) {
			return null;
		}

		// Return the common ancestor of the classes
		Class<?> ancestor = classes[0];
		for (int ci = 1; ci < classes.length; ++ci) {
			ancestor = getCommonAncestor(ancestor, classes[ci]);
		}
		return ancestor;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the value of the specified static field in the specified {@link Class}.
	 * <p>
	 * @param c    the {@link Class} containing the static field to get
	 * @param name the name of the static field to get
	 * <p>
	 * @return the value of the specified static field in the specified {@link Class}
	 * <p>
	 * @throws IllegalAccessException   if the field is inaccessible
	 * @throws IllegalArgumentException if {@code c} is not declaring the field
	 * @throws NoSuchFieldException     if there is no field with {@code name}
	 */
	public static Object getFieldValue(final Class<?> c, final String name)
			throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
		return c.getField(name).get(c);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean is(final Object object) {
		return object instanceof Class;
	}

	public static boolean isFrom(final Class<?> c) {
		return Class.class.isAssignableFrom(c);
	}
}
