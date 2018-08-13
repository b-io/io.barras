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

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.util.Floats;

public class Charts {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Charts() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the colors.
	 * <p>
	 * @return the colors
	 */
	public static List<Color> getColors() {
		return new ExtendedList<Color>(
				Arrays.<Color>asList(Color.BLUE, Color.GREEN, Color.RED, Color.ORANGE));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a line chart with the specified title, xy-axes labels and dataset.
	 * <p>
	 * @param title   the title of the chart to create
	 * @param xLabel  the label of the x-axis of the chart to create
	 * @param yLabel  the label of the y-axis of the chart to create
	 * @param dataset the dataset of the chart to create
	 * <p>
	 * @return a line chart with the specified title, xy-axes labels and dataset
	 */
	public static JFreeChart createLineChart(final String title, final String xLabel,
			final String yLabel, final XYDataset dataset) {
		// Create a line chart
		final JFreeChart chart = ChartFactory.createXYLineChart(title, // the title
				xLabel, // the x-axis label
				yLabel, // the y-axis label
				dataset, // the dataset
				PlotOrientation.VERTICAL, // the orientation
				true, // generate the legends
				true, // generate the tooltips
				false // generate the URLs
		);
		chart.setBackgroundPaint(Color.WHITE);

		// Set up the plot
		final XYPlot plot = (XYPlot) chart.getPlot();
		setUpPlot(plot);
		return chart;
	}

	/**
	 * Creates a scatter chart with the specified title, xy-axes labels and dataset.
	 * <p>
	 * @param title   the title of the chart to create
	 * @param xLabel  the label of the x-axis of the chart to create
	 * @param yLabel  the label of the y-axis of the chart to create
	 * @param dataset the dataset of the chart to create
	 * <p>
	 * @return a scatter chart with the specified title, xy-axes labels and dataset
	 */
	public static JFreeChart createScatterPlot(final String title, final String xLabel,
			final String yLabel, final XYDataset dataset) {
		final JFreeChart chart = ChartFactory.createScatterPlot(title, // the title
				xLabel, // the x-axis label
				yLabel, // the y-axis label
				dataset, // the dataset
				PlotOrientation.VERTICAL, // the orientation
				true, // generate the legends
				true, // generate the tooltips
				false // generate the URLs
		);
		chart.setBackgroundPaint(Color.WHITE);

		// Set up the plot
		final XYPlot plot = (XYPlot) chart.getPlot();
		setUpPlot(plot);
		return chart;
	}

	/**
	 * Creates a time series chart with the specified title, xy-axes labels and dataset.
	 * <p>
	 * @param title   the title of the chart to create
	 * @param xLabel  the label of the x-axis of the chart to create
	 * @param yLabel  the label of the y-axis of the chart to create
	 * @param dataset the dataset of the chart to create
	 * <p>
	 * @return a time series chart with the specified title, xy-axes labels and dataset
	 */
	public static JFreeChart createTimeSeriesChart(final String title, final String xLabel,
			final String yLabel, final XYDataset dataset) {
		return createTimeSeriesChart(title, xLabel, yLabel, dataset, null);
	}

	/**
	 * Creates a time series chart with the specified title, xy-axes labels, dataset and date
	 * format.
	 * <p>
	 * @param title      the title of the chart to create
	 * @param xLabel     the label of the x-axis of the chart to create
	 * @param yLabel     the label of the y-axis of the chart to create
	 * @param dataset    the dataset of the chart to create
	 * @param dateFormat the date format of the x-axis of the chart to create
	 * <p>
	 * @return a time series chart with the specified title, xy-axes labels, dataset and date format
	 */
	public static JFreeChart createTimeSeriesChart(final String title, final String xLabel,
			final String yLabel, final XYDataset dataset, final DateFormat dateFormat) {
		// Create a time series chart
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(title, // the title
				xLabel, // the x-axis label
				yLabel, // the y-axis label
				dataset, // the dataset
				true, // generate the legends
				true, // generate the tooltips
				false // generate the URLs
		);
		chart.setBackgroundPaint(Color.WHITE);

		// Set up the plot
		final XYPlot plot = (XYPlot) chart.getPlot();
		setUpPlot(plot);
		final XYItemRenderer renderer = plot.getRenderer();
		if (renderer instanceof StandardXYItemRenderer) {
			final BasicStroke dashedStroke = new BasicStroke(2f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND, 1f, Floats.toPrimitiveArray(6f, 6f), 0f); // new BasicStroke(2f)
			renderer.setSeriesStroke(0, dashedStroke);
			renderer.setSeriesStroke(1, dashedStroke);
		}
		if (dateFormat != null) {
			final DateAxis axis = (DateAxis) plot.getDomainAxis();
			axis.setDateFormatOverride(dateFormat);
		}
		return chart;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a combined chart with the specified title, x-axis label, y-axis labels and datasets.
	 * <p>
	 * @param title    the title of the chart to create
	 * @param xLabel   the label of the x-axis of the chart to create
	 * @param yLabels  the labels of the y-axis of the chart to create
	 * @param datasets the datasets of the chart to create
	 * <p>
	 * @return a combined chart
	 */
	public static JFreeChart createCombinedChart(final String title, final String xLabel,
			final List<String> yLabels, final List<XYDataset> datasets) {
		return createCombinedChart(title, new NumberAxis(xLabel), yLabels, datasets);
	}

	/**
	 * Creates a combined chart with the specified title, x-axis, y-axis labels and datasets.
	 * <p>
	 * @param title    the title of the chart to create
	 * @param xAxis    the x-axis of the chart to create
	 * @param yLabels  the labels of the y-axis of the chart to create
	 * @param datasets the datasets of the chart to create
	 * <p>
	 * @return a combined chart
	 */
	public static JFreeChart createCombinedChart(final String title, final ValueAxis xAxis,
			final List<String> yLabels, final List<XYDataset> datasets) {
		final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(xAxis);
		setUpPlot(plot);
		final int nDatasets = datasets.size();
		for (int i = 0; i < nDatasets; ++i) {
			plot.add(new XYPlot(datasets.get(i), null, new NumberAxis(yLabels.get(i)),
					new StandardXYItemRenderer()));
		}
		return new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets up the specified plot.
	 * <p>
	 * @param plot a {@link XYPlot}
	 */
	public static void setUpPlot(final XYPlot plot) {
		// Set the orientation
		plot.setOrientation(PlotOrientation.VERTICAL);
		// Set the xy-axes
		plot.getDomainAxis().setLowerMargin(0.);
		plot.getDomainAxis().setUpperMargin(0.);
		plot.setAxisOffset(new RectangleInsets(0., 0., 0., 0.));
		// Set the crosshair
		plot.setDomainCrosshairVisible(false);
		plot.setDomainCrosshairLockedOnData(true);
		plot.setRangeCrosshairVisible(false);
		plot.setRangeCrosshairLockedOnData(true);
		// Set the colors
		plot.setOutlinePaint(Color.BLACK);
		plot.setBackgroundPaint(Color.WHITE);
		// Set the grid lines
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
	}
}
