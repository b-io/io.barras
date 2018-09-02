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

import java.awt.Color;
import java.awt.Dimension;
import java.text.DateFormat;
import java.util.Calendar;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;

import jupiter.common.util.Integers;
import jupiter.graphics.charts.panels.ChartPanels;
import jupiter.graphics.charts.panels.DynamicChartPanel;
import jupiter.graphics.charts.panels.JPanels;

public class Display {

	protected final DateFormat xFormat = ChartPanels.DATE_FORMAT;
	protected final TimeSeriesGraphic graph;
	protected JFreeChart chart = null;
	protected XYPlot plot = null;

	public Display() {
		graph = new TimeSeriesGraphic("Test Series", "Time", "Value");
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		display.createSeries(2, 100);
		display.plot();
	}

	public void plot() {
		// Set the dimension
		final Dimension minDimension = new Dimension(640, 400);
		final Dimension dimension = new Dimension(1920, 1200);
		graph.setMinimumSize(minDimension);
		graph.setPreferredSize(dimension);

		// Create the chart
		chart = graph.createChart();
		plot = chart.getXYPlot();
		final DateAxis axis = (DateAxis) plot.getDomainAxis();
		plot.getRenderer().setSeriesPaint(0, Color.RED);
		plot.getRenderer().setSeriesPaint(1, Color.GREEN);
		plot.getRenderer().setSeriesPaint(2, Color.BLUE);
		plot.getRenderer().setSeriesPaint(3, Color.YELLOW);
		axis.setDateFormatOverride(xFormat);

		// Create the chart panel
		final ChartPanel chartPanel = new DynamicChartPanel(chart, xFormat);
		chartPanel.setMinimumSize(minDimension);
		chartPanel.setPreferredSize(dimension);
		chartPanel.setMouseZoomable(true, false);

		// Display
		JPanels.addScrollZoom(chartPanel);
		graph.setContentPane(chartPanel);
		graph.setVisible(true);
	}

	protected void createSeries(final int seriesCount, final int valueCount) {
		for (int s = 0; s < seriesCount; ++s) {
			final int series = graph.addSeries("Test " + s);
			final Calendar calendar = Calendar.getInstance();

			for (int v = 0; v < valueCount; ++v) {
				graph.addValue(series, calendar.getTime(), Integers.random(0, 10));
				calendar.add(Calendar.SECOND, 60);
			}
		}
	}
}
