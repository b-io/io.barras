/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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

import java.util.Map;

import jupiter.common.util.Maps;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class MapArguments
		extends Arguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String NAME = "map";
	public static String NAMES = NAME + "s";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link MapArguments}.
	 */
	protected MapArguments() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <M extends Map<?, ?>> M requireNonNull(final M map) {
		return Arguments.requireNonNull(map, NAME);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void requireMap(final Object object) {
		if (CHECK_ARGS) {
			requireMap(object, "object");
		}
	}

	public static void requireMap(final Object object, final String name) {
		if (CHECK_ARGS && !Maps.is(requireNonNull(object, name))) {
			throw new IllegalArgumentException(Strings.paste("The specified", Strings.quote(name),
					"is not a", NAME));
		}
	}

	//////////////////////////////////////////////

	/**
	 * Checks if {@code a} is either the same as, or is a superclass or superinterface of, the class
	 * or interface represented by {@code b}.
	 * <p>
	 * @param a a {@link Class}
	 * @param b another {@link Class}
	 */
	public static void requireAssignableFrom(final Class<?> a, final Class<?> b) {
		if (CHECK_ARGS && !a.isAssignableFrom(b)) {
			throw new IllegalArgumentException(Strings.paste("Cannot store", Objects.getName(b),
					"in a", NAME, "of", Objects.getName(a)));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <M extends Map<?, ?>> M require(final M found, final M expected) {
		if (CHECK_ARGS && !Maps.equals(found, expected)) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, "is wrong",
					expectedButFound(found, expected)));
		}
		return found;
	}

	//////////////////////////////////////////////

	public static void requireEquals(final Map<?, ?> a, final Map<?, ?> b) {
		if (CHECK_ARGS && !Maps.equals(a, b)) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAMES,
					"are not equal", isNotEqualTo(a, b)));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <M extends Map<?, ?>> M requireNonEmpty(final M map) {
		if (CHECK_ARGS) {
			return requireNonEmpty(map, NAME);
		}
		return map;
	}

	public static <M extends Map<?, ?>> M requireNonEmpty(final M map, final String name) {
		if (CHECK_ARGS) {
			requireNonEmpty(requireNonNull(map, name).size(), name);
		}
		return map;
	}

	public static void requireNonEmpty(final int size, final String name) {
		if (CHECK_ARGS && size == 0) {
			throw new IllegalArgumentException(Strings.paste("The specified", Strings.quote(name),
					"is empty"));
		}
	}

	//////////////////////////////////////////////

	public static <M extends Map<?, ?>> M requireSize(final M map, final int expectedSize) {
		if (CHECK_ARGS) {
			requireSize(requireNonNull(map).size(), expectedSize);
		}
		return map;
	}

	public static void requireSize(final int foundSize, final int expectedSize) {
		if (CHECK_ARGS && foundSize != expectedSize) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME,
					"has wrong size", expectedButFound(foundSize, expectedSize)));
		}
	}

	//////////////////////////////////////////////

	public static <M extends Map<?, ?>> M requireMinSize(final M map, final int minExpectedSize) {
		if (CHECK_ARGS) {
			requireMinSize(requireNonNull(map).size(), minExpectedSize);
		}
		return map;
	}

	public static void requireMinSize(final int foundSize, final int minExpectedSize) {
		if (CHECK_ARGS && foundSize < minExpectedSize) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME,
					"has a size", foundSize,
					"inferior to", minExpectedSize));
		}
	}

	//////////////////////////////////////////////

	public static <M extends Map<?, ?>> M requireMaxSize(final M map, final int maxExpectedSize) {
		if (CHECK_ARGS) {
			requireMaxSize(requireNonNull(map).size(), maxExpectedSize);
		}
		return map;
	}

	public static void requireMaxSize(final int foundSize, final int maxExpectedSize) {
		if (CHECK_ARGS && foundSize > maxExpectedSize) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME,
					"has a size", foundSize,
					"superior to", maxExpectedSize));
		}
	}

	//////////////////////////////////////////////

	public static void requireSameSize(final Map<?, ?> a, final Map<?, ?> b) {
		if (CHECK_ARGS) {
			requireSameSize(requireNonNull(a).size(), requireNonNull(b).size());
		}
	}

	public static void requireSameSize(final Map<?, ?> a, final int bSize) {
		if (CHECK_ARGS) {
			requireSameSize(requireNonNull(a).size(), bSize);
		}
	}

	public static void requireSameSize(final int aSize, final int bSize) {
		if (CHECK_ARGS && aSize != bSize) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAMES,
					"do not have the same size", isNotEqualTo(aSize, bSize)));
		}
	}
}
