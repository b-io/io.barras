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
package jupiter.common.math;

/**
 * {@link IMultiSet} is an {@link ISet} of {@code T} type in which the objects may occur more than
 * once.
 * <p>
 * @param <T> the self {@link Comparable} type of the {@link IMultiSet}
 */
public interface IMultiSet<T extends Comparable<? super T>>
		extends ISet<T> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the underlying {@link ISet} of {@code T} type.
	 * <p>
	 * @return the underlying {@link ISet} of {@code T} type
	 */
	public ISet<T> getUnderlyingSet();

	/**
	 * Returns the multiplicity of the specified {@code T} object.
	 * <p>
	 * @param object a {@code T} object
	 * <p>
	 * @return the multiplicity of the specified {@code T} object
	 */
	public int getMultiplicity(final T object);
}
