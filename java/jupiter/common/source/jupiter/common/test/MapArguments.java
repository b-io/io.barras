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
package jupiter.common.test;

import java.util.Map;

import jupiter.common.util.Strings;

public class MapArguments
		extends Arguments {

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

	public static <M extends Map<?, ?>> M requireNonEmpty(final M map) {
		if (CHECK_ARGS) {
			return requireNonEmpty(map, "map");
		}
		return map;
	}

	public static <M extends Map<?, ?>> M requireNonEmpty(final M map, final String name) {
		if (CHECK_ARGS) {
			requireNonEmpty(requireNonNull(map, name).size(), name);
		}
		return map;
	}

	public static void requireNonEmpty(final int length, final String name) {
		if (CHECK_ARGS && length == 0) {
			throw new IllegalArgumentException("The specified " + Strings.quote(name) +
					" is empty");
		}
	}

	//////////////////////////////////////////////

	public static <M extends Map<?, ?>> M requireMinSize(final M map, final int minExpectedSize) {
		if (CHECK_ARGS && requireNonNull(map).size() < minExpectedSize) {
			throw new IllegalArgumentException("The specified map has a size " + map.size() +
					" inferior to " + minExpectedSize);
		}
		return map;
	}

	public static <M extends Map<?, ?>> M requireMaxSize(final M map, final int maxExpectedSize) {
		if (CHECK_ARGS && requireNonNull(map).size() > maxExpectedSize) {
			throw new IllegalArgumentException("The specified map has a size " + map.size() +
					" superior to " + maxExpectedSize);
		}
		return map;
	}

	public static void requireSameSize(final Map<?, ?> a, final Map<?, ?> b) {
		if (CHECK_ARGS && requireNonNull(a).size() != requireNonNull(b).size()) {
			throw new IllegalArgumentException("The specified maps do not have the same size " +
					isNotEqualTo(a.size(), b.size()));
		}
	}
}
