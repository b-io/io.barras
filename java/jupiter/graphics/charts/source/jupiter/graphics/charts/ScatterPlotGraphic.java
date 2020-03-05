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
package jupiter.graphics.charts;

import jupiter.common.model.ICloneable;
import jupiter.common.test.ArrayArguments;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;
import jupiter.graphics.charts.struct.SeriesStyle;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScatterPlotGraphic
		extends ChartGraphic {

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
	 * The {@link XYSeriesCollection}.
	 */
	protected XYSeriesCollection collection;
	/**
	 * The number of series.
	 */
	protected int seriesCount;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ScatterPlotGraphic} with the specified title, x-axis label and y-axis
	 * label.
	 * <p>
	 * @param title  the title
	 * @param xLabel the label of the x-axis
	 * @param yLabel the label of the y-axis
	 */
	public ScatterPlotGraphic(final String title, final String xLabel, final String yLabel) {
		this(title, xLabel, yLabel, new XYSeriesCollection());
	}

	/**
	 * Constructs a {@link ScatterPlotGraphic} with the specified title, x-axis label, y-axis label
	 * and {@link XYSeriesCollection}.
	 * <p>
	 * @param title      the title
	 * @param xLabel     the label of the x-axis
	 * @param yLabel     the label of the y-axis
	 * @param collection the {@link XYSeriesCollection}
	 */
	public ScatterPlotGraphic(final String title, final String xLabel, final String yLabel,
			final XYSeriesCollection collection) {
		super(title, xLabel, yLabel);
		this.collection = collection;
		seriesCount = collection.getSeriesCount();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the size of the specified series.
	 * <p>
	 * @param seriesIndex the index of the series to get the size from
	 * <p>
	 * @return the size of the specified series
	 */
	public int getSeriesSize(final int seriesIndex) {
		return collection.getSeries(seriesIndex).getItemCount();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public JFreeChart createChart() {
		final JFreeChart chart = Charts.createScatterPlot(title, labels.getX(), labels.getY(),
				collection);
		final XYPlot plot = chart.getXYPlot();
		// Set the dataset
		plot.setDataset(collection);
		// Set the styles
		plot.setRenderer(createItemRenderer());
		return chart;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public int addSeries(final String title) {
		final int seriesIndex = seriesCount;
		collection.addSeries(new XYSeries(title));
		++seriesCount;
		return seriesIndex;
	}

	public int addSeries(final String title, final SeriesStyle style) {
		final int seriesIndex = addSeries(title);
		styles.put(seriesIndex, style);
		return seriesIndex;
	}

	public int addSeries(final String title, final SeriesStyle style, final Number[] xValues,
			final Number[] yValues) {
		// Check the arguments
		ArrayArguments.<Number>requireSameLength(xValues, yValues);

		// Create the series with the title and style
		final int seriesIndex = addSeries(title, style);
		// Add the points
		final int n = xValues.length;
		for (int i = 0; i < n; ++i) {
			addPoint(seriesIndex, xValues[i], yValues[i]);
		}
		return seriesIndex;
	}

	public void addPoint(final int index, final Number x, final Number y) {
		collection.getSeries(index).add(x, y);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public ScatterPlotGraphic clone() {
		try {
			final ScatterPlotGraphic clone = (ScatterPlotGraphic) super.clone();
			clone.collection = Objects.clone(collection);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		}
	}
}
