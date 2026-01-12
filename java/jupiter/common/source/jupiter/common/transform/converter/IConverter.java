/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2026 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.common.transform.converter;

/**
 * {@link IConverter} converts an input {@link Object} to an {@code O} output.
 *
 * @param <O> the output type
 */
public interface IConverter<O> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the output {@link Class} of {@code O} type.
	 *
	 * @return the output {@link Class} of {@code O} type
	 */
	public Class<O> getOutputClass();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public O convert(final Object input);

	////////////////////////////////////////////////////////////////////////////////////////////////

	public O[] toArray(final Object[] input);

	public O[] asArray(final Object... input);

	//////////////////////////////////////////////

	public O[][] toArray2D(final Object[][] input2D);

	public O[][] asArray2D(final Object[]... input2D);

	//////////////////////////////////////////////

	public O[][][] toArray3D(final Object[][][] input3D);

	public O[][][] asArray3D(final Object[][]... input3D);
}
