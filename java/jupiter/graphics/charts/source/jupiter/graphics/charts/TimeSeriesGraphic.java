/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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

import java.text.ParseException;
import java.util.Date;

import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYDataset;

import jupiter.common.struct.table.StringTable;
import jupiter.common.time.Dates;
import jupiter.common.util.Doubles;
import jupiter.graphics.charts.structure.TimeSeriesList;

public class TimeSeriesGraphic
		extends ChartGraphic {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 5812015887596159277L;


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
	 * @param coordinates  the {@link StringTable} of the x and y coordinates
	 * @param xColumnIndex the index of the column containing the x coordinates in the table
	 * @param yColumnIndex the index of the column containing the y coordinates in the table
	 * @param hasTime      the flag specifying whether to parse the x coordinates with time
	 * <p>
	 * @throws ParseException if there is a problem with parsing the x coordinates to {@link Date}
	 */
	public void loadXY(final StringTable coordinates, final int xColumnIndex,
			final int yColumnIndex, final boolean hasTime)
			throws ParseException {
		if (coordinates != null) {
			final int seriesIndex = addSeries(coordinates.getColumnName(yColumnIndex));
			final int m = coordinates.getRowCount();
			if (m > 0) {
				for (int i = 0; i < m; ++i) {
					addValue(seriesIndex,
							hasTime ? Dates.parseWithTime(coordinates.get(i, xColumnIndex)) :
									Dates.parse(coordinates.get(i, xColumnIndex)),
							Doubles.convert(coordinates.get(i, yColumnIndex)));
				}
			} else {
				IO.warn("No coordinates found");
			}
		}
	}
}
