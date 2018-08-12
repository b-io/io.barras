/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io>
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
package jupiter.common.map;

import java.util.Collection;

/**
 * {@link ObjectToLongMapper} is an operator mapping an {@link Object} to a {@link Long}.
 */
public abstract class ObjectToLongMapper
		extends ObjectMapper<Long> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public ObjectToLongMapper() {
		super(Long.class);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public <I> long callToPrimitive(final I input) {
		return call(input);
	}

	public <I> long[] callToPrimitiveArray(final I... input) {
		final long[] result = new long[input.length];
		for (int i = 0; i < input.length; ++i) {
			result[i] = call(input[i]);
		}
		return result;
	}

	public <I> long[][] callToPrimitiveArray2D(final I[]... input2D) {
		final long[][] result = new long[input2D.length][];
		for (int i = 0; i < input2D.length; ++i) {
			result[i] = callToPrimitiveArray(input2D[i]);
		}
		return result;
	}

	public <I> long[][][] callToPrimitiveArray3D(final I[][]... array3D) {
		final long[][][] result = new long[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = callToPrimitiveArray2D(array3D[i]);
		}
		return result;
	}

	public <I> long[] callCollectionToPrimitiveArray(final Collection<I> collection) {
		final long[] result = new long[collection.size()];
		int i = 0;
		for (final I element : collection) {
			result[i] = call(element);
			++i;
		}
		return result;
	}
}
