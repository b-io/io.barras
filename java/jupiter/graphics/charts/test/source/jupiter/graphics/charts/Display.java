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

import static jupiter.common.io.IO.IO;

import java.awt.Color;
import java.io.IOException;
import java.text.ParseException;

import jupiter.common.struct.table.StringTable;
import jupiter.graphics.charts.panels.DynamicChartPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

public class Display {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link TimeSeriesGraphic}.
	 */
	protected final TimeSeriesGraphic graph;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Display}.
	 */
	public Display() {
		graph = new TimeSeriesGraphic("Test Series", "Time", "Value");
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts displaying the chart.
	 * <p>
	 * @param args the array of command line arguments
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		display.loadSeries("test/resources/coordinates.csv");
		display.plot();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void plot() {
		// Create the chart
		final JFreeChart chart = graph.createChart();
		final XYPlot plot = chart.getXYPlot();
		plot.getRenderer().setSeriesPaint(0, Color.RED);
		plot.getRenderer().setSeriesPaint(1, Color.GREEN);
		plot.getRenderer().setSeriesPaint(2, Color.BLUE);
		plot.getRenderer().setSeriesPaint(3, Color.YELLOW);

		// Create the chart panel
		final ChartPanel chartPanel = new DynamicChartPanel(chart, Charts.DATE_FORMAT);

		// Display
		graph.setContentPane(chartPanel);
		graph.setVisible(true);
	}

	protected void loadSeries(final String path) {
		try {
			final StringTable coordinates = new StringTable(path, true);
			graph.load(coordinates, 0, 1, true);
			graph.load(coordinates, 0, 2, true);
		} catch (final IOException ex) {
			IO.error(ex);
		} catch (final ParseException ex) {
			IO.error(ex);
		}
	}
}
