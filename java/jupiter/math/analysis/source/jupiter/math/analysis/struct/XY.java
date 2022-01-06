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
package jupiter.math.analysis.struct;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.tuple.Pair;

/**
 * A {@link Pair} containing the {@code T} xy-coordinates.
 * <p>
 * @param <T> the type of the xy-coordinates
 */
public class XY<T>
		extends Pair<T, T> {

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
	 * Constructs a {@link XY}.
	 */
	public XY() {
		super();
	}

	/**
	 * Constructs a {@link XY} with the specified {@code T} xy-coordinates.
	 * <p>
	 * @param x the x {@code T} x-coordinate
	 * @param y the y {@code T} y-coordinate
	 */
	public XY(final T x, final T y) {
		super(x, y);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code T} x-coordinate.
	 * <p>
	 * @return the {@code T} x-coordinate
	 */
	public T getX() {
		return first;
	}

	/**
	 * Returns the {@code T} y-coordinate.
	 * <p>
	 * @return the {@code T} y-coordinate
	 */
	public T getY() {
		return second;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@code T} x-coordinate.
	 * <p>
	 * @param x a {@code T} object (on the abscissa)
	 */
	public void setX(final T x) {
		super.setFirst(x);
	}

	/**
	 * Sets the {@code T} y-coordinate.
	 * <p>
	 * @param y a {@code T} object (on the ordinate)
	 */
	public void setY(final T y) {
		super.setSecond(y);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public XY<T> clone() {
		return (XY<T>) super.clone();
	}
}
