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
import org.jfree.data.function.Function2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;


public class LineChartGraphic
		extends ChartGraphic {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 7528275147125444712L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected XYDataset dataset;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public LineChartGraphic(final String title, final String xLabel, final String yLabel) {
		super(title, xLabel, yLabel);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CHART GRAPHIC
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public JFreeChart createChart() {
		return Charts.createLineChart(title, xLabel, yLabel, dataset);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// LINE CHART GRAPHIC
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a dataset from the specified {@link Function2D}.
	 * <p>
	 * @param f          a {@link Function2D} {@code f: R -{@literal >} R}
	 * @param lowerBound the lowerbound of the domain
	 * @param upperBound the upperbound of the domain
	 * @param samples    the number of samples
	 * @param seriesKey  the identifier of the dataset to create
	 */
	public void createDataset(final Function2D f, final double lowerBound, final double upperBound,
			final int samples, final Comparable<?> seriesKey) {
		dataset = DatasetUtilities.sampleFunction2D(f, lowerBound, upperBound, samples, seriesKey);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the dataset.
	 * <p>
	 * @return the dataset
	 */
	public XYDataset getDataset() {
		return dataset;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the dataset.
	 * <p>
	 * @param dataset a {@link XYDataset}
	 */
	public void setDataset(final XYDataset dataset) {
		this.dataset = dataset;
	}
}
