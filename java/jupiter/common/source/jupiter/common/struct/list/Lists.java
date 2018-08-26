/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.common.struct.list;

import java.util.List;

import jupiter.common.math.Numbers;
import jupiter.common.test.CollectionArguments;

public class Lists {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Lists() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T extends Number> ExtendedList<T> getMinElements(final List<T> a,
			final List<T> b) {
		// Check the arguments
		CollectionArguments.<List<T>>requireSameSize(a, b);

		// Get the size
		final int size = a.size();
		// For each index, get the minimum number
		final ExtendedList<T> minElements = new ExtendedList<T>(size);
		for (int i = 0; i < size; ++i) {
			minElements.add(Numbers.<T>getMin(a.get(i), b.get(i)));
		}
		return minElements;
	}

	public static <T extends Number> ExtendedList<T> getMaxElements(final List<T> a,
			final List<T> b) {
		// Check the arguments
		CollectionArguments.<List<T>>requireSameSize(a, b);

		// Get the size
		final int size = a.size();
		// For each index, get the maximum number
		final ExtendedList<T> maxElements = new ExtendedList<T>(size);
		for (int i = 0; i < size; ++i) {
			maxElements.add(Numbers.<T>getMax(a.get(i), b.get(i)));
		}
		return maxElements;
	}
}
