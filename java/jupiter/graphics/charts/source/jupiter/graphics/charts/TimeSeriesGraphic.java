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

import static jupiter.common.io.InputOutput.IO;

import java.text.ParseException;
import java.util.Date;

import jupiter.common.model.ICloneable;
import jupiter.common.test.ArrayArguments;
import jupiter.common.time.Dates;
import jupiter.graphics.charts.datasets.XYRangeAxisDataset;
import jupiter.graphics.charts.panels.DynamicChartPanel;
import jupiter.graphics.charts.struct.SeriesStyle;
import jupiter.graphics.charts.struct.TimeSeriesList;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;

/**
 * {@link ChartGraphic} is the {@link ChartGraphic} of {@link TimeSeriesList} of {@link TimeSeries}.
 */
public class TimeSeriesGraphic
		extends ChartGraphic<TimeSeriesList, TimeSeries> {

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
	 * Constructs a {@link TimeSeriesGraphic} with the specified title and domain label.
	 * <p>
	 * @param title  the title
	 * @param xLabel the domain label
	 */
	public TimeSeriesGraphic(final String title, final String xLabel) {
		super(title, xLabel);
	}

	/**
	 * Constructs a {@link TimeSeriesGraphic} with the specified title, domain label and range
	 * labels.
	 * <p>
	 * @param title   the title
	 * @param xLabel  the domain label
	 * @param yLabels the range labels
	 */
	public TimeSeriesGraphic(final String title, final String xLabel, final String... yLabels) {
		super(title, xLabel, yLabels);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link TimeSeries} at the specified index in the {@link TimeSeriesList} of the
	 * specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link TimeSeriesList} of the {@link TimeSeries} to get
	 * @param timeSeriesIndex  the index of the {@link TimeSeries} to get
	 * <p>
	 * @return the {@link TimeSeries} at the specified index in the {@link TimeSeriesList} of the
	 *         specified {@link XYRangeAxisDataset}
	 */
	@Override
	public TimeSeries getSeries(final int axisDatasetIndex, final int timeSeriesIndex) {
		return getDataset(axisDatasetIndex).getSeries(timeSeriesIndex);
	}

	/**
	 * Returns the number of {@link TimeSeries} in the {@link TimeSeriesList} of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link TimeSeriesList} of the {@link TimeSeries} to count
	 * <p>
	 * @return the number of {@link TimeSeries} in the {@link TimeSeriesList} of the specified
	 *         {@link XYRangeAxisDataset}
	 */
	@Override
	public int countSeries(final int axisDatasetIndex) {
		return getDataset(axisDatasetIndex).getSeriesCount();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a time series chart.
	 * <p>
	 * @return a time series chart
	 */
	@Override
	public JFreeChart createChart() {
		return Charts.createTimeSeriesChart(title, xLabel, axisDatasets);
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
		return new DynamicChartPanel(chart, Charts.DATE_FORMAT);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends a {@link XYRangeAxisDataset} constructed with the specified range label.
	 * <p>
	 * @param yLabel the range label of the {@link XYRangeAxisDataset} to append
	 */
	@Override
	public void addAxisDataset(final String yLabel) {
		axisDatasets.add(new XYRangeAxisDataset<TimeSeriesList>(yLabel, new TimeSeriesList()));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends a {@link TimeSeries} constructed with the specified name to the
	 * {@link TimeSeriesList} of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link TimeSeriesList} to append to
	 * @param name             the name of the {@link TimeSeries} to append
	 * <p>
	 * @return the index of the {@link TimeSeries} appended to the {@link TimeSeriesList} of the
	 *         specified {@link XYRangeAxisDataset}
	 */
	@Override
	public int addSeries(final int axisDatasetIndex, final String name) {
		return addSeries(axisDatasetIndex, new TimeSeries(name));
	}

	/**
	 * Appends the specified {@link TimeSeries} to the {@link TimeSeriesList} of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link TimeSeriesList} to append to
	 * @param timeSeries       the {@link TimeSeries} to append
	 * <p>
	 * @return the index of the specified {@link TimeSeries} appended to the {@link TimeSeriesList}
	 *         of the specified {@link XYRangeAxisDataset}
	 */
	@Override
	public int addSeries(final int axisDatasetIndex, final TimeSeries timeSeries) {
		final TimeSeriesList timeSeriesList = getDataset(axisDatasetIndex);
		timeSeriesList.addSeries(timeSeries);
		return timeSeriesList.getSeriesCount() - 1;
	}

	/**
	 * Appends a {@link TimeSeries} constructed with the specified name, {@link SeriesStyle} and
	 * points to the {@link TimeSeriesList} of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link TimeSeriesList} to append to
	 * @param name             the name of the {@link TimeSeries} to append
	 * @param style            the {@link SeriesStyle} of the {@link TimeSeries} to append
	 * @param X                the {@code double} domain coordinates of the points of the
	 *                         {@link TimeSeries} to append
	 * @param Y                the {@code double} range coordinates of the points of the
	 *                         {@link TimeSeries} to append
	 * <p>
	 * @return the index of the {@link TimeSeries} appended to the {@link TimeSeriesList} of the
	 *         specified {@link XYRangeAxisDataset}
	 */
	public int addSeries(final int axisDatasetIndex, final String name, final SeriesStyle style,
			final Date[] X, final double[] Y) {
		return addSeries(axisDatasetIndex, new TimeSeries(name), style, X, Y);
	}

	/**
	 * Appends the specified {@link TimeSeries} with the specified {@link SeriesStyle} and points to
	 * the {@link TimeSeriesList} of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link TimeSeriesList} to append to
	 * @param timeSeries       the {@link TimeSeries} to append
	 * @param style            the {@link SeriesStyle} of the {@link TimeSeries} to append
	 * @param X                the {@code double} domain coordinates of the points of the
	 *                         {@link TimeSeries} to append
	 * @param Y                the {@code double} range coordinates of the points of the
	 *                         {@link TimeSeries} to append
	 * <p>
	 * @return the index of the specified {@link TimeSeries} appended to the {@link TimeSeriesList}
	 *         of the specified {@link XYRangeAxisDataset}
	 */
	public int addSeries(final int axisDatasetIndex, final TimeSeries timeSeries,
			final SeriesStyle style, final Date[] X, final double[] Y) {
		// Check the arguments
		ArrayArguments.requireSameLength(X, Y.length);

		// Add the time series with the style
		final int timeSeriesIndex = addSeries(axisDatasetIndex, timeSeries, style);
		// Add the points
		for (int i = 0; i < X.length; ++i) {
			addPoint(axisDatasetIndex, timeSeriesIndex, X[i], Y[i]);
		}
		return timeSeriesIndex;
	}

	//////////////////////////////////////////////

	/**
	 * Removes the {@link TimeSeries} at the specified index from the {@link TimeSeriesList} of the
	 * specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link TimeSeriesList} to remove from
	 * @param timeSeriesIndex  the index of the {@link TimeSeries} to remove
	 */
	@Override
	public void removeSeries(final int axisDatasetIndex, final int timeSeriesIndex) {
		getDataset(axisDatasetIndex).removeSeries(timeSeriesIndex);
	}

	/**
	 * Removes the specified {@link TimeSeries} from the {@link TimeSeriesList} of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link TimeSeriesList} to remove from
	 * @param timeSeries       the {@link TimeSeries} to remove
	 */
	@Override
	public void removeSeries(final int axisDatasetIndex, final TimeSeries timeSeries) {
		getDataset(axisDatasetIndex).removeSeries(timeSeries);
	}

	/**
	 * Removes all the {@link TimeSeries} from the {@link TimeSeriesList} of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the
	 *                         {@link TimeSeriesList} to remove from
	 */
	@Override
	public void removeAllSeries(final int axisDatasetIndex) {
		getDataset(axisDatasetIndex).removeAllSeries();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends a point with the specified domain and range coordinates to the specified
	 * {@link TimeSeries} of the {@link TimeSeriesList} of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} of the
	 *                         {@link TimeSeriesList} containing the {@link TimeSeries} to append to
	 * @param timeSeriesIndex  the index of the {@link TimeSeries} to append to
	 * @param x                the domain coordinate {@link String} of the point to append
	 * @param y                the range coordinate {@link String} of the point to append
	 */
	@Override
	public void addPoint(final int axisDatasetIndex, final int timeSeriesIndex, final String x,
			final String y) {
		try {
			addPoint(axisDatasetIndex, timeSeriesIndex, Dates.parseWithTime(x), Double.valueOf(y));
		} catch (final ParseException ex) {
			IO.error(ex);
		}
	}

	/**
	 * Appends a point with the specified range coordinate to the specified {@link TimeSeries} of
	 * the {@link TimeSeriesList} of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} of the
	 *                         {@link TimeSeriesList} containing the {@link TimeSeries} to append to
	 * @param timeSeriesIndex  the index of the {@link TimeSeries} to append to
	 * @param y                the {@code double} range coordinate of the point to append
	 */
	public void addPoint(final int axisDatasetIndex, final int timeSeriesIndex, final double y) {
		getDataset(axisDatasetIndex).addPoint(timeSeriesIndex, y);
	}

	/**
	 * Appends a point with the specified domain and range coordinates to the specified
	 * {@link TimeSeries} of the {@link TimeSeriesList} of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} of the
	 *                         {@link TimeSeriesList} containing the {@link TimeSeries} to append to
	 * @param timeSeriesIndex  the index of the {@link TimeSeries} to append to
	 * @param x                the {@code double} domain coordinate of the point to append
	 * @param y                the {@code double} range coordinate of the point to append
	 */
	public void addPoint(final int axisDatasetIndex, final int timeSeriesIndex, final Date x,
			final double y) {
		getDataset(axisDatasetIndex).addPoint(timeSeriesIndex, x, y);
	}

	/**
	 * Appends a point for each specified range coordinate to the respective {@link TimeSeries} of
	 * the {@link TimeSeriesList} of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} of the
	 *                         {@link TimeSeriesList} containing the {@link TimeSeries} to append to
	 * @param Y                the {@code double} range coordinate of each point to append
	 */
	public void addPointToAll(final int axisDatasetIndex, final double[] Y) {
		getDataset(axisDatasetIndex).addPointToAll(Y);
	}

	//////////////////////////////////////////////

	/**
	 * Appends a point with the specified range coordinate to the specified {@link TimeSeries} of
	 * the {@link TimeSeriesList} of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} of the
	 *                         {@link TimeSeriesList} containing the {@link TimeSeries} to append to
	 * @param timeSeriesIndex  the index of the {@link TimeSeries} to append to
	 * @param y                the range coordinate {@link Number} of the point to append
	 */
	public void addPoint(final int axisDatasetIndex, final int timeSeriesIndex, final Number y) {
		getDataset(axisDatasetIndex).addPoint(timeSeriesIndex, y);
	}

	/**
	 * Appends a point with the specified domain and range coordinates to the specified
	 * {@link TimeSeries} of the {@link TimeSeriesList} of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} of the
	 *                         {@link TimeSeriesList} containing the {@link TimeSeries} to append to
	 * @param timeSeriesIndex  the index of the {@link TimeSeries} to append to
	 * @param x                the domain coordinate {@link Number} of the point to append
	 * @param y                the range coordinate {@link Number} of the point to append
	 */
	public void addPoint(final int axisDatasetIndex, final int timeSeriesIndex, final Date x,
			final Number y) {
		getDataset(axisDatasetIndex).addPoint(timeSeriesIndex, x, y);
	}

	/**
	 * Appends a point for each specified range coordinate to the respective {@link TimeSeries} of
	 * the {@link TimeSeriesList} of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} of the
	 *                         {@link TimeSeriesList} containing the {@link TimeSeries} to append to
	 * @param Y                the range coordinate {@link Number} of each point to append
	 */
	public void addPointToAll(final int axisDatasetIndex, final Number[] Y) {
		getDataset(axisDatasetIndex).addPointToAll(Y);
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
	public TimeSeriesGraphic clone() {
		return (TimeSeriesGraphic) super.clone();
	}
}
