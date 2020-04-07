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
package jupiter.common.io.string;

import java.util.Collection;

import jupiter.common.util.Classes;
import jupiter.common.util.Objects;

public class Stringifiers {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final JSON JSON = new JSON();

	//////////////////////////////////////////////

	public static volatile Stringifier STRINGIFIER = JSON;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Stringifiers}.
	 */
	protected Stringifiers() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Class} is a leaf.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is a leaf, {@code false} otherwise
	 */
	public static boolean isLeaf(final Class<?> c) {
		return c == null || Objects.isImmutableFrom(c);
	}

	/**
	 * Tests whether the specified array is a leaf.
	 * <p>
	 * @param objects the array of {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified array is a leaf, {@code false} otherwise
	 */
	public static boolean isLeaf(final Object[] objects) {
		for (final Object object : objects) {
			if (!isLeaf(Classes.get(object))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests whether the specified {@link Collection} is a leaf.
	 * <p>
	 * @param collection the {@link Collection} to test
	 * <p>
	 * @return {@code true} if the specified {@link Collection} is a leaf, {@code false} otherwise
	 */
	public static boolean isLeaf(final Collection<?> collection) {
		for (final Object element : collection) {
			if (!isLeaf(Classes.get(element))) {
				return false;
			}
		}
		return true;
	}
}
