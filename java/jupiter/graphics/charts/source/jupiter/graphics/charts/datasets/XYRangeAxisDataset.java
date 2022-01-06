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
package jupiter.graphics.charts.datasets;

import static jupiter.common.util.Strings.NULL;

import java.io.Serializable;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYDataset;

/**
 * {@link XYRangeAxisDataset} is the {@code D} dataset (subtype of {@link XYDataset}) associated to
 * a range {@link ValueAxis}.
 * <p>
 * @param <D> the type of the dataset (subtype of {@link XYDataset})
 */
public class XYRangeAxisDataset<D extends XYDataset>
		implements ICloneable<XYRangeAxisDataset<D>>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The range {@link ValueAxis}.
	 */
	public final ValueAxis yAxis;

	/**
	 * The {@code D} dataset.
	 */
	public final D dataset;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link XYRangeAxisDataset} of {@code D} type with the specified range label and
	 * {@code D} dataset.
	 * <p>
	 * @param yLabel  the range label
	 * @param dataset the {@code D} dataset
	 */
	public XYRangeAxisDataset(final String yLabel, final D dataset) {
		this(new NumberAxis(yLabel), dataset);
	}

	/**
	 * Constructs a {@link XYRangeAxisDataset} of {@code D} type with the specified range
	 * {@link ValueAxis} and {@code D} dataset.
	 * <p>
	 * @param yAxis   the range {@link ValueAxis}
	 * @param dataset the {@code D} dataset
	 */
	public XYRangeAxisDataset(final ValueAxis yAxis, final D dataset) {
		this.yAxis = yAxis;
		this.dataset = dataset;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the range {@link ValueAxis}.
	 * <p>
	 * @return the range {@link ValueAxis}
	 */
	public ValueAxis getRangeAxis() {
		return yAxis;
	}

	/**
	 * Returns the {@code D} dataset.
	 * <p>
	 * @return the {@code D} dataset
	 */
	public D getDataset() {
		return dataset;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the range label.
	 * <p>
	 * @return the range label
	 */
	public String getLabel() {
		return yAxis.getLabel();
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
	@SuppressWarnings({"cast", "unchecked"})
	public XYRangeAxisDataset<D> clone() {
		try {
			return (XYRangeAxisDataset<D>) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	@Override
	@SuppressWarnings({"cast", "unchecked"})
	public boolean equals(final Object other) {
		if (other == this) {
			return true;
		}
		if (other == null || !(other instanceof XYRangeAxisDataset)) {
			return false;
		}
		final XYRangeAxisDataset<D> otherXYRangeAxisDataset = (XYRangeAxisDataset<D>) other;
		return Objects.equals(yAxis, otherXYRangeAxisDataset.yAxis) &&
				Objects.equals(dataset, otherXYRangeAxisDataset.dataset);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see #equals(Object)
	 * @see System#identityHashCode(Object)
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, yAxis, dataset);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return yAxis != null ? Objects.toString(yAxis.getLabel()) : NULL;
	}
}
