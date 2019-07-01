/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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
import java.util.Set;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.util.Arrays;

/**
 * {@link ObjectMapper} is the {@link Mapper} mapping an input {@link Object} to an {@code O}
 * output.
 * <p>
 * @param <O> the output type
 */
public abstract class ObjectMapper<O>
		extends Mapper<Object, O> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link ObjectMapper} of type {@code O} with the specified output {@link Class}.
	 * <p>
	 * @param c the output {@link Class} of type {@code O}
	 */
	protected ObjectMapper(final Class<O> c) {
		super(c);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public <I> O[] callCollectionToArray(final Collection<I> input) {
		final O[] result = Arrays.<O>create(c, input.size());
		int i = 0;
		for (final I element : input) {
			result[i] = call(element);
			++i;
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public <I> ExtendedList<O> callCollectionToList(final Collection<I> input) {
		final ExtendedList<O> result = new ExtendedList<O>(input.size());
		for (final I element : input) {
			result.add(call(element));
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public <I> Set<O> callCollectionToSet(final Collection<I> input) {
		final Set<O> result = new HashSet<O>(input.size());
		for (final I element : input) {
			result.add(call(element));
		}
		return result;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	public abstract ObjectMapper<O> clone();
}
