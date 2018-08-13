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
package jupiter.graphics.charts;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import jupiter.common.test.ArrayArguments;
import jupiter.graphics.charts.structure.SeriesStyle;

public class ScatterPlotGraphic
		extends ChartGraphic {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -62726070454440278L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final XYSeriesCollection collection;
	protected int size;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public ScatterPlotGraphic(final String title, final String xLabel, final String yLabel) {
		super(title, xLabel, yLabel);
		collection = new XYSeriesCollection();
		size = 0;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public int getSize(final int index) {
		return collection.getSeries(index).getItemCount();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public JFreeChart createChart() {
		final JFreeChart chart = Charts.createScatterPlot(title, xLabel, yLabel, collection);
		final XYPlot plot = chart.getXYPlot();
		// Set the dataset
		plot.setDataset(collection);
		// Set the styles
		plot.setRenderer(getItemRenderer());
		return chart;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public int addSeries(final String title) {
		final int seriesIndex = size;
		collection.addSeries(new XYSeries(title));
		++size;
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

		// Create the series
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
}
