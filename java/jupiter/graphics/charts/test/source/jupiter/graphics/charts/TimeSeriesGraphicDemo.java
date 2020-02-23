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

import jupiter.common.time.Chronometer;
import jupiter.common.util.Doubles;
import jupiter.graphics.charts.struct.TimeSeriesList;

import org.jfree.ui.RefineryUtilities;

public class TimeSeriesGraphicDemo {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Demonstrates {@link TimeSeriesGraphic}.
	 * <p>
	 * @param args ignored
	 */
	public static void main(final String[] args) {
		final TimeSeriesGraphic graphic = new TimeSeriesGraphic("TimeSeriesGraphic Demo", "Time",
				"Value", createTimeSeriesList(100));
		graphic.pack();
		RefineryUtilities.centerFrameOnScreen(graphic);
		graphic.display();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link TimeSeriesList}.
	 * <p>
	 * @param count the length of the time series to create
	 * <p>
	 * @return a {@link TimeSeriesList}
	 */
	protected static TimeSeriesList createTimeSeriesList(final int count) {
		// Initialize
		final TimeSeriesList dataset = new TimeSeriesList();
		final Double[] values = new Double[2];
		final Chronometer chrono = new Chronometer();

		// Create the time series list
		dataset.addSeries("Time intervals [ms]");
		dataset.addSeries("Random values");
		chrono.start();
		for (int i = 0; i < count; ++i) {
			chrono.stop();
			chrono.start();
			values[0] = chrono.getMilliseconds();
			values[1] = Doubles.random();
			dataset.addValues(values);
		}
		return dataset;
	}
}
