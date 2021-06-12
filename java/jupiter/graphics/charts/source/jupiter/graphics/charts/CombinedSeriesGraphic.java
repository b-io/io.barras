/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;

/**
 * {@link CombinedSeriesGraphic} is the combined {@link SeriesGraphic}.
 */
public class CombinedSeriesGraphic
		extends SeriesGraphic {

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
	 * Constructs a {@link CombinedSeriesGraphic} with the specified title and domain label.
	 * <p>
	 * @param title  the title
	 * @param xLabel the domain label
	 */
	public CombinedSeriesGraphic(final String title, final String xLabel) {
		super(title, xLabel);
	}

	/**
	 * Constructs a {@link CombinedSeriesGraphic} with the specified title, domain label and range
	 * labels.
	 * <p>
	 * @param title   the title
	 * @param xLabel  the domain label
	 * @param yLabels the range labels
	 */
	public CombinedSeriesGraphic(final String title, final String xLabel, final String... yLabels) {
		super(title, xLabel, yLabels);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a combined chart.
	 * <p>
	 * @return a combined chart
	 */
	@Override
	public JFreeChart createChart() {
		return Charts.createCombinedChart(title, new NumberAxis(), axisDatasets);
	}

	//////////////////////////////////////////////

	/**
	 * Creates a {@link ChartPanel} for the specified chart.
	 * <p>
	 * @param chart the {@link JFreeChart} to create for
	 * <p>
	 * @return a {@link ChartPanel} for the specified chart
	 */
	@Override
	public ChartPanel createChartPanel(final JFreeChart chart) {
		return new ChartPanel(chart);
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
	public CombinedSeriesGraphic clone() {
		return (CombinedSeriesGraphic) super.clone();
	}
}
