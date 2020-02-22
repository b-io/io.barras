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

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DateFormat;
import java.text.Format;
import java.util.Iterator;
import java.util.List;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.time.SafeDateFormat;
import jupiter.common.util.Arrays;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CrosshairLabelGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

public class Charts {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default {@link DateFormat} of the x-axis.
	 */
	public static final DateFormat DEFAULT_DATE_FORMAT = new SafeDateFormat("dd/MM/YY HH:mm");


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Charts}.
	 */
	protected Charts() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the colors in an {@link ExtendedList}.
	 * <p>
	 * @return the colors in an {@link ExtendedList}
	 */
	public static ExtendedList<Color> getColors() {
		return new ExtendedList<Color>(
				Arrays.<Color>asList(Color.BLUE, Color.GREEN, Color.RED, Color.ORANGE));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a line chart with the specified title, xy-axes labels and {@link XYDataset}.
	 * <p>
	 * @param title   the title of the chart to create
	 * @param xLabel  the label of the x-axis of the chart to create
	 * @param yLabel  the label of the y-axis of the chart to create
	 * @param dataset the {@link XYDataset} of the chart to create
	 * <p>
	 * @return a line chart with the specified title, xy-axes labels and {@link XYDataset}
	 */
	public static JFreeChart createLineChart(final String title, final String xLabel,
			final String yLabel, final XYDataset dataset) {
		// Create the line chart with the title, xy-axes labels and dataset
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
	 * Creates a scatter chart with the specified title, xy-axes labels and {@link XYDataset}.
	 * <p>
	 * @param title   the title of the chart to create
	 * @param xLabel  the label of the x-axis of the chart to create
	 * @param yLabel  the label of the y-axis of the chart to create
	 * @param dataset the {@link XYDataset} of the chart to create
	 * <p>
	 * @return a scatter chart with the specified title, xy-axes labels and {@link XYDataset}
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
	 * Creates a time series chart with the specified title, xy-axes labels and {@link XYDataset}.
	 * <p>
	 * @param title   the title of the chart to create
	 * @param xLabel  the label of the x-axis of the chart to create
	 * @param yLabel  the label of the y-axis of the chart to create
	 * @param dataset the {@link XYDataset} of the chart to create
	 * <p>
	 * @return a time series chart with the specified title, xy-axes labels and {@link XYDataset}
	 */
	public static JFreeChart createTimeSeriesChart(final String title, final String xLabel,
			final String yLabel, final XYDataset dataset) {
		return createTimeSeriesChart(title, xLabel, yLabel, dataset, DEFAULT_DATE_FORMAT);
	}

	/**
	 * Creates a time series chart with the specified title, xy-axes labels, {@link XYDataset} and
	 * {@link DateFormat}.
	 * <p>
	 * @param title      the title of the chart to create
	 * @param xLabel     the label of the x-axis of the chart to create
	 * @param yLabel     the label of the y-axis of the chart to create
	 * @param dataset    the {@link XYDataset} of the chart to create
	 * @param dateFormat the {@link DateFormat} of the x-axis of the chart to create
	 * <p>
	 * @return a time series chart with the specified title, xy-axes labels, {@link XYDataset} and
	 *         {@link DateFormat}
	 */
	public static JFreeChart createTimeSeriesChart(final String title, final String xLabel,
			final String yLabel, final XYDataset dataset, final DateFormat dateFormat) {
		// Create the time series chart with the title, xy-axes labels and dataset
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
					BasicStroke.JOIN_ROUND, 1f, new float[] {6f, 6f}, 0f); // new BasicStroke(2f)
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
	 * Creates a combined chart with the specified title, x-axis label, y-axis labels and
	 * {@link List} of {@link XYDataset}.
	 * <p>
	 * @param title    the title of the chart to create
	 * @param xLabel   the label of the x-axis of the chart to create
	 * @param yLabels  the labels of the y-axis of the chart to create
	 * @param datasets the {@link List} of {@link XYDataset} of the chart to create
	 * <p>
	 * @return a combined chart with the specified title, x-axis label, y-axis labels and
	 *         {@link List} of {@link XYDataset}
	 */
	public static JFreeChart createCombinedChart(final String title, final String xLabel,
			final List<String> yLabels, final List<XYDataset> datasets) {
		return createCombinedChart(title, new NumberAxis(xLabel), yLabels, datasets);
	}

	/**
	 * Creates a combined chart with the specified title, x-axis, y-axis labels and {@link List} of
	 * {@link XYDataset}.
	 * <p>
	 * @param title    the title of the chart to create
	 * @param xAxis    the x-axis of the chart to create
	 * @param yLabels  the labels of the y-axis of the chart to create
	 * @param datasets the {@link List} of {@link XYDataset} of the chart to create
	 * <p>
	 * @return a combined chart with the specified title, x-axis, y-axis labels and {@link List} of
	 *         {@link XYDataset}
	 */
	public static JFreeChart createCombinedChart(final String title, final ValueAxis xAxis,
			final List<String> yLabels, final List<XYDataset> datasets) {
		final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(xAxis);
		setUpPlot(plot);
		final Iterator<XYDataset> datasetIterator = datasets.iterator();
		final Iterator<String> yLabelIterator = yLabels.iterator();
		while (datasetIterator.hasNext() && yLabelIterator.hasNext()) {
			plot.add(new XYPlot(datasetIterator.next(), null, new NumberAxis(yLabelIterator.next()),
					new StandardXYItemRenderer()));
		}
		return new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Crosshair createCrosshair(final boolean showLabel, final Format labelFormat) {
		// Create the crosshair
		final Crosshair crosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(1f,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] {10f, 5f}, 0f));
		// Set the label
		crosshair.setLabelBackgroundPaint(Color.BLACK);
		crosshair.setLabelGenerator(new CrosshairLabelGenerator() {
			@Override
			public String generateLabel(final Crosshair crosshair) {
				return labelFormat.format(crosshair.getValue());
			}
		});
		crosshair.setLabelOutlinePaint(Color.WHITE);
		crosshair.setLabelOutlineStroke(new BasicStroke(1f));
		crosshair.setLabelPaint(Color.WHITE);
		crosshair.setLabelVisible(showLabel);
		return crosshair;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets up the specified {@link XYPlot}.
	 * <p>
	 * @param plot the {@link XYPlot} to set up
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
