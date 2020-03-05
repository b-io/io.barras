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
	 * Prevents the construction of {@link Classes}.
	 */
	protected Classes() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
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

		// Get the common ancestor of the classes
		Class<?> ancestor = c1;
		do {
			ancestor = ancestor.getSuperclass();
			if (ancestor == null || ancestor == Object.class) {
				return Object.class;
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

		// Get the common ancestor of the classes
		Class<?> ancestor = classes[0];
		for (int i = 1; i < classes.length; ++i) {
			ancestor = Classes.getCommonAncestor(ancestor, classes[i]);
		}
		return ancestor;
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
