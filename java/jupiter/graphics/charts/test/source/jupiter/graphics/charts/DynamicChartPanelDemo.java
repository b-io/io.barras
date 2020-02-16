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
import jupiter.gui.swing.Swings;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

public class DynamicChartPanelDemo {

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
	 * Constructs a {@link DynamicChartPanelDemo}.
	 */
	public DynamicChartPanelDemo() {
		graph = new TimeSeriesGraphic("DynamicChartPanel Demo", "Time", "Value");
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Demonstrates {@link DynamicChartPanel}.
	 * <p>
	 * @param args ignored
	 */
	public static void main(final String[] args) {
		final DynamicChartPanelDemo demo = new DynamicChartPanelDemo();
		demo.loadSeries("test/resources/coordinates.csv");
		demo.display();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void display() {
		// Create the chart
		final JFreeChart chart = graph.createChart();
		final XYPlot plot = chart.getXYPlot();
		plot.getRenderer().setSeriesPaint(0, Color.BLUE);
		plot.getRenderer().setSeriesPaint(1, Color.RED);

		// Create the dynamic chart panel
		final ChartPanel chartPanel = new DynamicChartPanel(chart, Charts.DEFAULT_DATE_FORMAT);
		graph.setContentPane(chartPanel);

		// Show the chart
		Swings.show(graph);
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
