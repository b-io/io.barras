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
package jupiter.common.test;

import java.util.Collection;

import jupiter.common.util.Strings;

public class CollectionArguments
		extends Arguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link CollectionArguments}.
	 */
	protected CollectionArguments() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <C extends Collection<?>> C requireNotEmpty(final C collection) {
		return requireNotEmpty(collection, "collection");
	}

	public static <C extends Collection<?>> C requireNotEmpty(final C collection,
			final String name) {
		if (CHECK_ARGS) {
			requireNotEmpty(requireNotNull(collection, name).size(), name);
		}
		return collection;
	}

	public static void requireNotEmpty(final int length, final String name) {
		if (CHECK_ARGS && length == 0) {
			throw new IllegalArgumentException(Strings.join("The specified ", Strings.quote(name),
					" is empty"));
		}
	}

	//////////////////////////////////////////////

	public static <C extends Collection<?>> C requireMinSize(final C collection,
			final int minExpectedSize) {
		if (CHECK_ARGS && requireNotNull(collection).size() < minExpectedSize) {
			throw new IllegalArgumentException("The specified collection has a size " +
					collection.size() + " inferior to " + minExpectedSize);
		}
		return collection;
	}

	public static <C extends Collection<?>> C requireMaxSize(final C collection,
			final int maxExpectedSize) {
		if (CHECK_ARGS && requireNotNull(collection).size() > maxExpectedSize) {
			throw new IllegalArgumentException("The specified collection has a size " +
					collection.size() + " superior to " + maxExpectedSize);
		}
		return collection;
	}

	public static <C extends Collection<?>> void requireSameSize(final C a, final C b) {
		if (CHECK_ARGS && requireNotNull(a).size() != requireNotNull(b).size()) {
			throw new IllegalArgumentException(
					"The specified collections do not have the same size " +
							isNotEqualTo(a.size(), b.size()));
		}
	}
}
