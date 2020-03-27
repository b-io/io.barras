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
import java.awt.Stroke;
import java.text.DateFormat;
import java.text.Format;
import java.util.List;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.time.SafeDateFormat;
import jupiter.common.util.Arrays;
import jupiter.common.util.Formats;
import jupiter.graphics.charts.datasets.XYRangeAxisDataset;
import jupiter.graphics.charts.struct.TimeSeriesList;
import jupiter.math.analysis.function.univariate.UnivariateFunction;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CrosshairLabelGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.function.Function2D;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

public class Charts {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link SafeDateFormat}.
	 */
	public static volatile SafeDateFormat DATE_FORMAT = Formats.DATE_FORMAT;

	/**
	 * The {@link ExtendedList} of {@link Color}.
	 */
	public static volatile ExtendedList<Color> COLORS = new ExtendedList<Color>(
			Arrays.<Color>asList(Color.BLUE, Color.GREEN, Color.RED,
					Color.CYAN, Color.YELLOW, Color.MAGENTA,
					new Color(127, 0, 255), new Color(191, 255, 0), new Color(255, 127, 0),
					new Color(0, 127, 255), new Color(0, 255, 191), new Color(255, 0, 127)));
	/**
	 * The {@link Stroke}.
	 */
	public static volatile Stroke STROKE = new BasicStroke(2f);
	/**
	 * The dashed {@link Stroke}.
	 */
	public static volatile Stroke DASHED_STROKE = new BasicStroke(2f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND, 1f, new float[] {6f, 6f}, 0f);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Charts}.
	 */
	protected Charts() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a line chart with the specified title, domain label and {@link List} of
	 * {@link XYRangeAxisDataset} of {@link XYDataset}.
	 * <p>
	 * @param title        the title of the line chart to create
	 * @param xLabel       the domain label of the line chart to create
	 * @param axisDatasets the {@link List} of {@link XYRangeAxisDataset} of {@link XYDataset} of
	 *                     the line chart to create
	 * <p>
	 * @return a line chart with the specified title, domain label and {@link List} of
	 *         {@link XYRangeAxisDataset} of {@link XYDataset}
	 */
	public static JFreeChart createLineChart(final String title, final String xLabel,
			final List<? extends XYRangeAxisDataset<? extends XYDataset>> axisDatasets) {
		// Create the line chart with the title and domain label
		final JFreeChart chart = ChartFactory.createXYLineChart(
				title, // the title
				xLabel, // the domain label
				null, // the range label
				null, // the dataset
				PlotOrientation.VERTICAL, // the orientation
				true, // generate the legends
				true, // generate the tooltips
				false // generate the URLs
		);
		chart.setBackgroundPaint(Color.WHITE);

		// Set the parameters, axis datasets and renderers of the plot
		final XYPlot plot = (XYPlot) chart.getPlot();
		setDefaultParameters(plot);
		setAllAxisDatasets(plot, axisDatasets);
		setAllRenderers(plot);
		return chart;
	}

	//////////////////////////////////////////////

	/**
	 * Creates a scatter chart with the specified title, domain label and {@link List} of
	 * {@link XYRangeAxisDataset} of {@link XYDataset}.
	 * <p>
	 * @param title        the title of the scatter chart to create
	 * @param xLabel       the domain label of the scatter chart to create
	 * @param axisDatasets the {@link List} of {@link XYRangeAxisDataset} of {@link XYDataset} of
	 *                     the scatter chart to create
	 * <p>
	 * @return a scatter chart with the specified title, domain label and {@link List} of
	 *         {@link XYRangeAxisDataset} of {@link XYDataset}
	 */
	public static JFreeChart createScatterPlot(final String title, final String xLabel,
			final List<? extends XYRangeAxisDataset<? extends XYDataset>> axisDatasets) {
		// Create the scatter chart with the title and domain label
		final JFreeChart chart = ChartFactory.createScatterPlot(
				title, // the title
				xLabel, // the domain label
				null, // the range label
				null, // the dataset
				PlotOrientation.VERTICAL, // the orientation
				true, // generate the legends
				true, // generate the tooltips
				false // generate the URLs
		);
		chart.setBackgroundPaint(Color.WHITE);

		// Set the parameters, axis datasets and renderers of the plot
		final XYPlot plot = (XYPlot) chart.getPlot();
		setDefaultParameters(plot);
		setAllAxisDatasets(plot, axisDatasets);
		setAllRenderers(plot);
		return chart;
	}

	//////////////////////////////////////////////

	/**
	 * Creates a time series chart with the specified title, domain label and {@link List} of
	 * {@link XYRangeAxisDataset} of {@link TimeSeriesList}.
	 * <p>
	 * @param title        the title of the time series chart to create
	 * @param xLabel       the domain label of the time series chart to create
	 * @param axisDatasets the {@link List} of {@link XYRangeAxisDataset} of {@link TimeSeriesList}
	 *                     of the time series chart to create
	 * <p>
	 * @return a time series chart with the specified title, domain label and {@link List} of
	 *         {@link XYRangeAxisDataset} of {@link TimeSeriesList}
	 */
	public static JFreeChart createTimeSeriesChart(final String title, final String xLabel,
			final List<? extends XYRangeAxisDataset<TimeSeriesList>> axisDatasets) {
		return createTimeSeriesChart(title, xLabel, DATE_FORMAT, axisDatasets);
	}

	/**
	 * Creates a time series chart with the specified title, domain label, {@link DateFormat} and
	 * {@link List} of {@link XYRangeAxisDataset} of {@link TimeSeriesList}.
	 * <p>
	 * @param title        the title of the time series chart to create
	 * @param xLabel       the domain label of the time series chart to create
	 * @param xDateFormat  the domain {@link DateFormat} of the time series chart to create
	 * @param axisDatasets the {@link List} of {@link XYRangeAxisDataset} of {@link TimeSeriesList}
	 *                     of the time series chart to create
	 * <p>
	 * @return a time series chart with the specified title, domain label, {@link DateFormat} and
	 *         {@link List} of {@link XYRangeAxisDataset} of {@link TimeSeriesList}
	 */
	public static JFreeChart createTimeSeriesChart(final String title, final String xLabel,
			final DateFormat xDateFormat,
			final List<? extends XYRangeAxisDataset<? extends TimeSeriesList>> axisDatasets) {
		// Create the time series chart with the title and domain label
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(
				title, // the title
				xLabel, // the domain label
				null, // the range label
				null, // the dataset
				true, // generate the legends
				true, // generate the tooltips
				false // generate the URLs
		);
		chart.setBackgroundPaint(Color.WHITE);

		// Set the parameters, axis datasets and renderers of the plot
		final XYPlot plot = (XYPlot) chart.getPlot();
		setDefaultParameters(plot);
		setAllAxisDatasets(plot, axisDatasets);
		setAllRenderers(plot);
		if (xDateFormat != null) {
			final DateAxis xAxis = (DateAxis) plot.getDomainAxis();
			xAxis.setDateFormatOverride(xDateFormat);
		}
		return chart;
	}

	//////////////////////////////////////////////

	/**
	 * Creates a combined chart with the specified title, domain {@link ValueAxis} and {@link List}
	 * of {@link XYRangeAxisDataset} of {@link XYDataset}.
	 * <p>
	 * @param title        the title of the combined chart to create
	 * @param xAxis        the domain {@link ValueAxis} of the combined chart to create
	 * @param axisDatasets the {@link List} of {@link XYRangeAxisDataset} of {@link XYDataset} of
	 *                     the combined chart to create
	 * <p>
	 * @return a combined chart with the specified title, domain {@link ValueAxis} and {@link List}
	 *         of {@link XYRangeAxisDataset} of {@link XYDataset}
	 */
	public static JFreeChart createCombinedChart(final String title, final ValueAxis xAxis,
			final List<? extends XYRangeAxisDataset<? extends XYDataset>> axisDatasets) {
		// Create a combined domain plot for each axis dataset
		final CombinedDomainXYPlot plots = new CombinedDomainXYPlot(xAxis);
		setDefaultParameters(plots);
		for (final XYRangeAxisDataset<? extends XYDataset> axisDataset : axisDatasets) {
			plots.add(new XYPlot(axisDataset.getDataset(), null, axisDataset.getRangeAxis(),
					new StandardXYItemRenderer()));
		}
		return new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plots, true);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Crosshair createCrosshair(final boolean showLabel, final Format labelFormat) {
		// Create the crosshair
		final Crosshair crosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(1f,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] {10f, 5f}, 0f));
		// Set the label of the crosshair
		crosshair.setLabelBackgroundPaint(Color.BLACK);
		crosshair.setLabelGenerator(new CrosshairLabelGenerator() {
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

	//////////////////////////////////////////////

	/**
	 * Creates a {@link XYDataset} constructed by sampling the specified {@link UnivariateFunction}.
	 * <p>
	 * @param seriesKey  the identifier of the {@link XYDataset} to create
	 * @param f          a {@link UnivariateFunction} {@code f: R -{@literal >} R}
	 * @param lowerBound the lower bound of the domain
	 * @param upperBound the upper bound of the domain
	 * @param sampleSize the sample size
	 * <p>
	 * @return a {@link XYDataset} constructed by sampling the specified {@link UnivariateFunction}
	 */
	public static XYDataset createDataset(final Comparable<?> seriesKey,
			final UnivariateFunction f, final double lowerBound, final double upperBound,
			final int sampleSize) {
		final Function2D function = new Function2D() {
			@Override
			public double getValue(final double x) {
				return f.apply(x);
			}
		};
		return createDataset(seriesKey, function, lowerBound, upperBound, sampleSize);
	}

	/**
	 * Creates a {@link XYDataset} constructed by sampling the specified {@link Function2D}.
	 * <p>
	 * @param seriesKey  the identifier of the {@link XYDataset} to create
	 * @param f          a {@link Function2D} {@code f: R -{@literal >} R}
	 * @param lowerBound the lower bound of the domain
	 * @param upperBound the upper bound of the domain
	 * @param sampleSize the sample size
	 * <p>
	 * @return a {@link XYDataset} constructed by sampling the specified {@link Function2D}
	 */
	public static XYDataset createDataset(final Comparable<?> seriesKey, final Function2D f,
			final double lowerBound, final double upperBound, final int sampleSize) {
		return DatasetUtils.sampleFunction2D(f, lowerBound, upperBound, sampleSize, seriesKey);
	}

	//////////////////////////////////////////////

	/**
	 * Creates a {@link XYSeries} constructed by sampling the specified {@link UnivariateFunction}.
	 * <p>
	 * @param seriesKey  the identifier of the {@link XYSeries} to create
	 * @param f          a {@link UnivariateFunction} {@code f: R -{@literal >} R}
	 * @param lowerBound the lower bound of the domain
	 * @param upperBound the upper bound of the domain
	 * @param sampleSize the sample size
	 * <p>
	 * @return a {@link XYSeries} constructed by sampling the specified {@link UnivariateFunction}
	 */
	public static XYSeries createSeries(final Comparable<?> seriesKey, final UnivariateFunction f,
			final double lowerBound, final double upperBound, final int sampleSize) {
		final Function2D function = new Function2D() {
			@Override
			public double getValue(final double x) {
				return f.apply(x);
			}
		};
		return createSeries(seriesKey, function, lowerBound, upperBound, sampleSize);
	}

	/**
	 * Creates a {@link XYSeries} constructed by sampling the specified {@link Function2D}.
	 * <p>
	 * @param seriesKey  the identifier of the {@link XYSeries} to create
	 * @param f          a {@link Function2D} {@code f: R -{@literal >} R}
	 * @param lowerBound the lower bound of the domain
	 * @param upperBound the upper bound of the domain
	 * @param sampleSize the sample size
	 * <p>
	 * @return a {@link XYSeries} constructed by sampling the specified {@link Function2D}
	 */
	public static XYSeries createSeries(final Comparable<?> seriesKey, final Function2D f,
			final double lowerBound, final double upperBound, final int sampleSize) {
		return DatasetUtils.sampleFunction2DToSeries(f, lowerBound, upperBound, sampleSize,
				seriesKey);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the parameters of the specified {@link XYPlot} by default.
	 * <p>
	 * @param plot the {@link XYPlot} to set
	 */
	public static void setDefaultParameters(final XYPlot plot) {
		// Set the orientation
		plot.setOrientation(PlotOrientation.VERTICAL);
		// Set the domain and range axes
		plot.getDomainAxis().setLowerMargin(0.);
		plot.getDomainAxis().setUpperMargin(0.);
		plot.setAxisOffset(new RectangleInsets(0., 0., 0., 0.));
		// Set the domain and range crosshairs
		plot.setDomainCrosshairVisible(false);
		plot.setDomainCrosshairLockedOnData(true);
		plot.setRangeCrosshairVisible(false);
		plot.setRangeCrosshairLockedOnData(true);
		// Set the colors
		plot.setOutlinePaint(Color.BLACK);
		plot.setBackgroundPaint(Color.WHITE);
		// Set the domain and range grid lines
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
	}

	/**
	 * Sets all the {@link XYRangeAxisDataset} of {@link XYDataset} of the specified {@link XYPlot}
	 * to the specified {@link List} of {@link XYRangeAxisDataset} of {@link XYDataset}.
	 * <p>
	 * @param plot         the {@link XYPlot} to set
	 * @param axisDatasets a {@link List} of {@link XYRangeAxisDataset} of {@link XYDataset}
	 */
	public static void setAllAxisDatasets(final XYPlot plot,
			final List<? extends XYRangeAxisDataset<? extends XYDataset>> axisDatasets) {
		int i = 0;
		for (final XYRangeAxisDataset<? extends XYDataset> axisDataset : axisDatasets) {
			// Set the range axis
			plot.setRangeAxis(i, axisDataset.getRangeAxis());
			// Set the dataset
			final XYDataset dataset = axisDataset.getDataset();
			plot.setDataset(i, dataset);
			++i;
		}
	}

	/**
	 * Sets all the {@link XYLineAndShapeRenderer} of the specified {@link XYPlot} by default.
	 * <p>
	 * @param plot the {@link XYPlot} to set
	 */
	public static void setAllRenderers(final XYPlot plot) {
		setAllRenderers(plot, COLORS.toArray(), new Stroke[] {STROKE});
	}

	/**
	 * Sets all the {@link XYLineAndShapeRenderer} of the specified {@link XYPlot} to the respective
	 * specified {@link Color} and {@link Stroke}.
	 * <p>
	 * @param plot    the {@link XYPlot} to set
	 * @param colors  an array of {@link Color}
	 * @param strokes an array of {@link Stroke}
	 */
	public static void setAllRenderers(final XYPlot plot, final Color[] colors,
			final Stroke[] strokes) {
		final XYItemRenderer renderer = plot.getRenderer();
		if (renderer instanceof StandardXYItemRenderer) {
			final int datasetCount = plot.getDatasetCount();
			for (int i = 0; i < datasetCount; ++i) {
				renderer.setSeriesPaint(i, colors[i % colors.length]);
				renderer.setSeriesStroke(i, strokes[i % strokes.length]);
			}
		}
	}
}
