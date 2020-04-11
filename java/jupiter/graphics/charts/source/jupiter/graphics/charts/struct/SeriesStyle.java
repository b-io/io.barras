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
package jupiter.graphics.charts.struct;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

public class SeriesStyle
		implements ICloneable<SeriesStyle> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link Color}.
	 */
	protected Color color;
	/**
	 * The {@link Shape}.
	 */
	protected Shape shape;
	/**
	 * The {@link Stroke}.
	 */
	protected Stroke stroke;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SeriesStyle} with the specified {@link Color}, {@link Shape} and
	 * {@link Stroke}.
	 * <p>
	 * @param color  the {@link Color}
	 * @param shape  the {@link Shape}
	 * @param stroke the {@link Stroke}
	 */
	public SeriesStyle(final Color color, final Shape shape, final Stroke stroke) {
		this.color = color;
		this.shape = shape;
		this.stroke = stroke;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Color}.
	 * <p>
	 * @return the {@link Color}
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Returns the {@link Shape}.
	 * <p>
	 * @return the {@link Shape}
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Returns the {@link Stroke}.
	 * <p>
	 * @return the {@link Stroke}
	 */
	public Stroke getStroke() {
		return stroke;
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
	public SeriesStyle clone() {
		try {
			final SeriesStyle clone = (SeriesStyle) super.clone();
			clone.color = Objects.clone(color);
			clone.shape = Objects.clone(shape);
			clone.stroke = Objects.clone(stroke);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}
}
