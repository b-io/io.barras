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
package jupiter.common.map;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.util.Arrays;

/**
 * {@link ObjectMapper} is an operator mapping an {@link Object} to an {@code O} object.
 * <p>
 * @param <O> the output type
 */
public abstract class ObjectMapper<O>
		extends Mapper<Object, O> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public ObjectMapper(final Class<O> c) {
		super(c);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public <I> O[] callCollectionToArray(final Collection<I> collection) {
		final O[] result = Arrays.<O>create(c, collection.size());
		int i = 0;
		for (final I element : collection) {
			result[i] = call(element);
			++i;
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public <I> List<O> callCollectionToList(final Collection<I> collection) {
		final List<O> result = new ExtendedList<O>(collection.size());
		for (final I element : collection) {
			result.add(call(element));
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public <I> Set<O> callCollectionToSet(final Collection<I> collection) {
		final Set<O> result = new HashSet<O>(collection.size());
		for (final I element : collection) {
			result.add(call(element));
		}
		return result;
	}
}
