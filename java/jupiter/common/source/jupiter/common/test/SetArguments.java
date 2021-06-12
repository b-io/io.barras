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

import jupiter.common.math.ISet;
import jupiter.common.util.Strings;

public class SetArguments
		extends Arguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link SetArguments}.
	 */
	protected SetArguments() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <S extends ISet<?>> S requireNonEmpty(final S set) {
		if (CHECK_ARGS) {
			return requireNonEmpty(set, "set");
		}
		return set;
	}

	public static <S extends ISet<?>> S requireNonEmpty(final S set, final String name) {
		if (CHECK_ARGS && requireNonNull(set, name).isEmpty()) {
			throw new IllegalArgumentException("The specified " + Strings.quote(name) +
					" is empty");
		}
		return set;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T extends Comparable<? super T>> T requireInside(final T object,
			final ISet<? super T> set) {
		if (CHECK_ARGS) {
			return requireInside(object, "object", set, "set");
		}
		return object;
	}

	public static <T extends Comparable<? super T>> T requireInside(final T object,
			final String objectName, final ISet<? super T> set, final String setName) {
		if (CHECK_ARGS &&
				!requireNonNull(set, setName).isInside(requireNonNull(object, objectName))) {
			throw new IllegalArgumentException("The specified " + Strings.quote(objectName) +
					" is not inside the " + Strings.quote(setName));
		}
		return object;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <S extends ISet<?>> S requireValid(final S set) {
		if (CHECK_ARGS) {
			return requireValid(set, "set");
		}
		return set;
	}

	public static <S extends ISet<?>> S requireValid(final S set, final String name) {
		if (CHECK_ARGS && !requireNonNull(set, name).isValid()) {
			throw new IllegalArgumentException("The specified " + Strings.quote(name) +
					" is invalid");
		}
		return set;
	}
}
