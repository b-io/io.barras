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
import jupiter.common.test.DoubleArguments;
import jupiter.graphics.charts.datasets.XYRangeAxisDataset;
import jupiter.graphics.charts.struct.SeriesStyle;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * {@link SeriesGraphic} is the {@link ChartGraphic} of {@link XYSeriesCollection} of
 * {@link XYSeries}.
 */
public class SeriesGraphic
		extends ChartGraphic<XYSeriesCollection, XYSeries> {

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
	 * Constructs a {@link SeriesGraphic} with the specified title and domain label.
	 * <p>
	 * @param title  the title
	 * @param xLabel the domain label
	 */
	public SeriesGraphic(final String title, final String xLabel) {
		super(title, xLabel);
	}

	/**
	 * Constructs a {@link SeriesGraphic} with the specified title, domain label and range labels.
	 * <p>
	 * @param title   the title
	 * @param xLabel  the domain label
	 * @param yLabels the range labels
	 */
	public SeriesGraphic(final String title, final String xLabel, final String... yLabels) {
		super(title, xLabel, yLabels);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link XYSeries} at the specified index in the {@link XYSeriesCollection} of the
	 * specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link XYSeriesCollection} of the {@link XYSeries} to get
	 * @param seriesIndex      the index of the {@link XYSeries} to get
	 * <p>
	 * @return the {@link XYSeries} at the specified index in the {@link XYSeriesCollection} of the
	 *         specified {@link XYRangeAxisDataset}
	 */
	@Override
	public XYSeries getSeries(final int axisDatasetIndex, final int seriesIndex) {
		return getDataset(axisDatasetIndex).getSeries(seriesIndex);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a series chart.
	 * <p>
	 * @return a series chart
	 */
	@Override
	public JFreeChart createChart() {
		return Charts.createScatterPlot(title, xLabel, axisDatasets);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends a {@link XYRangeAxisDataset} constructed with the specified range label.
	 * <p>
	 * @param yLabel the range label of the {@link XYRangeAxisDataset} to append
	 */
	@Override
	public void addAxisDataset(final String yLabel) {
		axisDatasets.add(new XYRangeAxisDataset<XYSeriesCollection>(yLabel,
				new XYSeriesCollection()));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends the specified {@link XYSeries} to the {@link XYSeriesCollection} of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link XYSeriesCollection} to append to
	 * @param series           the {@link XYSeries} to append
	 * <p>
	 * @return the index of the {@link XYSeries} appended to the {@link XYSeriesCollection} of the
	 *         specified {@link XYRangeAxisDataset}
	 */
	@Override
	public int addSeries(final int axisDatasetIndex, final XYSeries series) {
		final XYSeriesCollection collection = getDataset(axisDatasetIndex);
		collection.addSeries(series);
		return collection.getSeriesCount() - 1;
	}

	/**
	 * Appends a {@link XYSeries} constructed with the specified name to the
	 * {@link XYSeriesCollection} of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link XYSeriesCollection} to append to
	 * @param name             the name of the {@link XYSeries} to append
	 * <p>
	 * @return the index of the {@link XYSeries} appended to the {@link XYSeriesCollection} of the
	 *         specified {@link XYRangeAxisDataset}
	 */
	@Override
	public int addSeries(final int axisDatasetIndex, final String name) {
		return addSeries(axisDatasetIndex, new XYSeries(name));
	}

	/**
	 * Appends a {@link XYSeries} constructed with the specified name, {@link SeriesStyle} and
	 * points to the {@link XYSeriesCollection} of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link XYSeriesCollection} to append to
	 * @param name             the name of the {@link XYSeries} to append
	 * @param style            the {@link SeriesStyle} of the {@link XYSeries} to append
	 * @param X                the {@code double} domain coordinates of the points of the
	 *                         {@link XYSeries} to append
	 * @param Y                the {@code double} range coordinates of the points of the
	 *                         {@link XYSeries} to append
	 * <p>
	 * @return the index of the {@link XYSeries} appended to the {@link XYSeriesCollection} of the
	 *         specified {@link XYRangeAxisDataset}
	 */
	public int addSeries(final int axisDatasetIndex, final String name, final SeriesStyle style,
			final double[] X, final double[] Y) {
		// Check the arguments
		DoubleArguments.requireSameLength(X, Y);

		// Create the series with the name and style
		final int seriesIndex = addSeries(axisDatasetIndex, name, style);
		// Add the points
		for (int i = 0; i < X.length; ++i) {
			addPoint(axisDatasetIndex, seriesIndex, X[i], Y[i]);
		}
		return seriesIndex;
	}

	//////////////////////////////////////////////

	/**
	 * Removes the {@link XYSeries} at the specified index from the {@link XYSeriesCollection} of
	 * the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link XYSeriesCollection} to remove from
	 * @param seriesIndex      the index of the {@link XYSeries} to remove
	 */
	@Override
	public void removeSeries(final int axisDatasetIndex, final int seriesIndex) {
		getDataset(axisDatasetIndex).removeSeries(seriesIndex);
	}

	/**
	 * Removes the specified {@link XYSeries} from the {@link XYSeriesCollection} of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link XYSeriesCollection} to remove from
	 * @param series           the {@link XYSeries} to remove
	 */
	@Override
	public void removeSeries(final int axisDatasetIndex, final XYSeries series) {
		getDataset(axisDatasetIndex).removeSeries(series);
	}

	/**
	 * Removes all the {@link XYSeries} from the {@link XYSeriesCollection} of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link XYSeriesCollection} to remove from
	 */
	@Override
	public void removeAllSeries(final int axisDatasetIndex) {
		getDataset(axisDatasetIndex).removeAllSeries();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends a point with the specified domain and range coordinates to the specified
	 * {@link XYSeries} of the {@link XYSeriesCollection} of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} of the
	 *                         {@link XYSeriesCollection} containing the {@link XYSeries} to append
	 *                         to
	 * @param seriesIndex      the index of the {@link XYSeries} to append to
	 * @param x                the {@code double} domain coordinate of the point to append
	 * @param y                the {@code double} range coordinate of the point to append
	 */
	public void addPoint(final int axisDatasetIndex, final int seriesIndex, final double x,
			final double y) {
		getDataset(axisDatasetIndex).getSeries(seriesIndex).add(x, y);
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
	public SeriesGraphic clone() {
		return (SeriesGraphic) super.clone();
	}
}
