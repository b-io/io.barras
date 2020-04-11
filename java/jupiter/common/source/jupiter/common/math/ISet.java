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
 * {@link ISet} is the well-defined collection of distinct {@code T} objects.
 * <p>
 * @param <T> the self {@link Comparable} type of the {@link ISet}
 */
public interface ISet<T extends Comparable<? super T>> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code value} if {@code value} is inside {@code this}, {@code null} otherwise.
	 * <p>
	 * @param value the {@code T} value to constrain (may be {@code null})
	 * <p>
	 * @return {@code value} if {@code value} is inside {@code this}, {@code null} otherwise
	 */
	public T constrain(final T value);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is empty.
	 * <p>
	 * @return {@code true} if {@code this} is empty, {@code false} otherwise
	 */
	public boolean isEmpty();

	/**
	 * Tests whether {@code this} contains the specified {@code T} object.
	 * <p>
	 * @param object the {@code T} object to test for membership (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} contains the specified {@code T} object, {@code false}
	 *         otherwise
	 */
	public boolean isInside(final T object);

	/**
	 * Tests whether {@code this} is valid.
	 * <p>
	 * @return {@code true} if {@code this} is valid, {@code false} otherwise
	 */
	public boolean isValid();
}
