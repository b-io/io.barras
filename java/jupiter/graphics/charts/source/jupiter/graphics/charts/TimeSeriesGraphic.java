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

import static jupiter.common.io.IO.IO;

import java.util.Date;

import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYDataset;

import jupiter.common.struct.table.Table;
import jupiter.graphics.charts.structure.TimeSeriesList;

public class TimeSeriesGraphic
		extends ChartGraphic {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 897811620954639678L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final TimeSeriesList dataset;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public TimeSeriesGraphic(final String title, final String xLabel, final String yLabel) {
		super(title, xLabel, yLabel);
		dataset = new TimeSeriesList();
	}

	public TimeSeriesGraphic(final String title, final String xLabel, final String yLabel,
			final TimeSeriesList list) {
		super(title, xLabel, yLabel);
		dataset = list;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CHART GRAPHIC
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public JFreeChart createChart() {
		return Charts.createTimeSeriesChart(title, xLabel, yLabel, dataset);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// TIME SERIES GRAPHIC
	////////////////////////////////////////////////////////////////////////////////////////////////

	public int addSeries(final String title) {
		return dataset.addSeries(title);
	}

	public void addSeries(final TimeSeries timeSeries) {
		dataset.addSeries(timeSeries);
	}

	public void addValue(final int index, final Number value) {
		dataset.addValue(index, value);
	}

	public void addValue(final int index, final Date time, final Number value) {
		dataset.addValue(index, time, value);
	}

	public void addValues(final Number[] values) {
		dataset.addValues(values);
	}

	public XYDataset getDataset() {
		return dataset;
	}

	/**
	 * Loads the x and y coordinates from the specified table.
	 * <p>
	 * @param coordinates  the {@link Table} of the x and y coordinates
	 * @param xColumnIndex the index of the column containing the x coordinates in the table
	 * @param yColumnIndex the index of the column containing the y coordinates in the table
	 */
	public void loadXY(final Table<String> coordinates, final int xColumnIndex,
			final int yColumnIndex) {
		if (coordinates != null) {
			final int m = coordinates.getRowCount();
			if (m > 0) {
				final int n = coordinates.getColumnCount();
				for (int i = 0; i < n; ++i) {
					coordinates.get(i, xColumnIndex);
					coordinates.get(i, yColumnIndex);
					// @todo
				}
			} else {
				IO.warn("No coordinates found");
			}
		}
	}
}
