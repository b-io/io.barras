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

import static jupiter.common.io.InputOutput.IO;

import java.awt.EventQueue;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.map.hash.ExtendedHashMap;
import jupiter.common.struct.table.StringTable;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.test.Arguments;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;
import jupiter.graphics.charts.datasets.XYRangeAxisDataset;
import jupiter.graphics.charts.panels.DynamicChartPanel;
import jupiter.graphics.charts.struct.SeriesStyle;
import jupiter.gui.swing.Swings;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Series;
import org.jfree.data.xy.XYDataset;

/**
 * {@link ChartGraphic} is a 2D chart graphic.
 * <p>
 * @param <D> the dataset type of the {@link ChartGraphic} (subtype of {@link XYDataset})
 * @param <S> the series type of the {@link ChartGraphic} (subtype of {@link Series})
 */
public abstract class ChartGraphic<D extends XYDataset, S extends Series>
		extends Graphic {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The domain label.
	 */
	protected String xLabel;

	/**
	 * The {@link ExtendedLinkedList} of {@link XYRangeAxisDataset} of {@code D} type.
	 */
	protected ExtendedLinkedList<XYRangeAxisDataset<D>> axisDatasets = new ExtendedLinkedList<XYRangeAxisDataset<D>>();

	/**
	 * The {@link SeriesStyle} associated to their {@link XYRangeAxisDataset} and {@code S} series.
	 */
	protected ExtendedHashMap<Pair<Integer, Integer>, SeriesStyle> styles = new ExtendedHashMap<Pair<Integer, Integer>, SeriesStyle>();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ChartGraphic} of {@code D} and {@code S} types with the specified title
	 * and domain label.
	 * <p>
	 * @param title  the title
	 * @param xLabel the domain label
	 */
	protected ChartGraphic(final String title, final String xLabel) {
		this(title, xLabel, Strings.EMPTY_ARRAY);
	}

	/**
	 * Constructs a {@link ChartGraphic} of {@code D} and {@code S} types with the specified title,
	 * domain label and range labels.
	 * <p>
	 * @param title   the title
	 * @param xLabel  the domain label
	 * @param yLabels the range labels
	 */
	protected ChartGraphic(final String title, final String xLabel, final String... yLabels) {
		super(title);
		this.xLabel = xLabel;
		for (final String yLabel : yLabels) {
			addAxisDataset(yLabel);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code D} dataset of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the {@code D}
	 *                         dataset to get
	 * <p>
	 * @return the {@code D} dataset of the specified {@link XYRangeAxisDataset}
	 */
	public D getDataset(final int axisDatasetIndex) {
		return axisDatasets.get(axisDatasetIndex).getDataset();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@code S} series at the specified index in the {@code D} dataset of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the {@code D}
	 *                         dataset of the {@code S} series to get
	 * @param seriesIndex      the index of the {@code S} series to get
	 * <p>
	 * @return the {@code S} series at the specified index in the {@code D} dataset of the specified
	 *         {@link XYRangeAxisDataset}
	 */
	public abstract S getSeries(final int axisDatasetIndex, final int seriesIndex);

	/**
	 * Returns the number of {@code S} series in the {@code D} dataset of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the {@code D}
	 *                         dataset of the {@code S} series to count
	 * <p>
	 * @return the number of {@code S} series in the {@code D} dataset of the specified
	 *         {@link XYRangeAxisDataset}
	 */
	public abstract int countSeries(final int axisDatasetIndex);

	/**
	 * Returns the size of the {@code S} series at the specified index in the {@code D} dataset of
	 * the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the {@code D}
	 *                         dataset of the {@code S} series to get the size from
	 * @param seriesIndex      the index of the {@code S} series to get the size from
	 * <p>
	 * @return the size of the {@code S} series at the specified index in the {@code D} dataset of
	 *         the specified {@link XYRangeAxisDataset}
	 */
	public int getSeriesSize(final int axisDatasetIndex, final int seriesIndex) {
		return getSeries(axisDatasetIndex, seriesIndex).getItemCount();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a chart.
	 * <p>
	 * @return a chart
	 */
	public abstract JFreeChart createChart();

	//////////////////////////////////////////////

	/**
	 * Creates a {@link ChartPanel} for the specified chart.
	 * <p>
	 * @param chart the {@link JFreeChart} to create for
	 * <p>
	 * @return a {@link ChartPanel} for the specified chart
	 */
	public ChartPanel createChartPanel(final JFreeChart chart) {
		return new DynamicChartPanel(chart);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link XYItemRenderer} for each {@link XYRangeAxisDataset}.
	 * <p>
	 * @return a {@link XYItemRenderer} for each {@link XYRangeAxisDataset}
	 */
	public XYItemRenderer[] createAllRenderers() {
		final XYItemRenderer[] renderers = new XYItemRenderer[axisDatasets.size()];
		for (int adi = 0; adi < axisDatasets.size(); ++adi) {
			renderers[adi] = createRenderer(adi);
		}
		return renderers;
	}

	/**
	 * Creates a {@link XYItemRenderer} for the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} to create for
	 * <p>
	 * @return a {@link XYItemRenderer} for the specified {@link XYRangeAxisDataset}
	 */
	public XYItemRenderer createRenderer(final int axisDatasetIndex) {
		final XYItemRenderer renderer = new XYLineAndShapeRenderer();
		final XYDataset dataset = getDataset(axisDatasetIndex);
		final int seriesCount = dataset.getSeriesCount();
		for (int si = 0; si < seriesCount; ++si) {
			final SeriesStyle style = styles.get(new Pair<Integer, Integer>(axisDatasetIndex, si));
			if (style != null) {
				renderer.setSeriesPaint(si, style.getColor());
				renderer.setSeriesShape(si, style.getShape());
				renderer.setSeriesStroke(si, style.getStroke());
			} else {
				renderer.setSeriesPaint(si, Charts.COLORS.get(si % Charts.COLORS.size()));
				renderer.setSeriesStroke(si, Charts.STROKE);
			}
		}
		return renderer;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Loads the {@code S} series from the specified {@link StringTable} to the {@code D} dataset of
	 * the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the {@code D}
	 *                         dataset to load to
	 * @param coordinates      the {@link StringTable} containing the coordinates to load
	 * @param xColumnIndex     the index of the column containing the domain coordinates to load
	 * @param yColumnIndex     the index of the column containing the range coordinates to load
	 */
	public void load(final int axisDatasetIndex, final StringTable coordinates,
			final int xColumnIndex, final int yColumnIndex) {
		// Check the arguments
		Arguments.requireNonNull(coordinates, "coordinates");

		// Add the series
		final int n = coordinates.getColumnCount();
		final int seriesIndex;
		if (yColumnIndex < n) {
			seriesIndex = addSeries(axisDatasetIndex, coordinates.getColumnName(yColumnIndex));
		} else {
			seriesIndex = addSeries(axisDatasetIndex);
		}
		// Add the points to the series
		final int m = coordinates.getRowCount();
		if (m > 0) {
			if (xColumnIndex < n && yColumnIndex < n) {
				for (int i = 0; i < m; ++i) {
					addPoint(axisDatasetIndex, seriesIndex, coordinates.get(i, xColumnIndex),
							coordinates.get(i, yColumnIndex));
				}
			} else if (xColumnIndex >= n) {
				IO.warn("The column of the domain coordinates is missing");
			} else {
				IO.warn("The column of the range coordinates is missing");
			}
		} else {
			IO.warn("No coordinates found");
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends a {@link XYRangeAxisDataset} constructed with the specified range label.
	 * <p>
	 * @param yLabel the range label of the {@link XYRangeAxisDataset} to append
	 */
	public abstract void addAxisDataset(final String yLabel);

	/**
	 * Appends a {@link XYRangeAxisDataset} constructed with the specified range label and {@code D}
	 * dataset.
	 * <p>
	 * @param yLabel  the range label of the {@link XYRangeAxisDataset} to append
	 * @param dataset the {@code D} dataset of the {@link XYRangeAxisDataset} to append
	 */
	public void addAxisDataset(final String yLabel, final D dataset) {
		addAxisDataset(new XYRangeAxisDataset<D>(yLabel, dataset));
	}

	/**
	 * Appends the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDataset the {@link XYRangeAxisDataset} to append
	 */
	public void addAxisDataset(final XYRangeAxisDataset<D> axisDataset) {
		axisDatasets.add(axisDataset);
	}

	//////////////////////////////////////////////

	/**
	 * Removes the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} to remove
	 */
	public void removeAxisDataset(final int axisDatasetIndex) {
		axisDatasets.remove(axisDatasetIndex);
	}

	/**
	 * Removes the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDataset the {@link XYRangeAxisDataset} to remove
	 */
	public void removeAxisDataset(final XYRangeAxisDataset<D> axisDataset) {
		axisDatasets.remove(axisDataset);
	}

	/**
	 * Removes all the {@link XYRangeAxisDataset}.
	 */
	public void removeAllAxisDataset() {
		axisDatasets.clear();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends a {@code S} series to the {@code D} dataset of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the {@code D}
	 *                         dataset to append to
	 * <p>
	 * @return the index of the {@code S} series appended to the {@code D} dataset of the specified
	 *         {@link XYRangeAxisDataset}
	 */
	public int addSeries(final int axisDatasetIndex) {
		return addSeries(axisDatasetIndex, "Y" + countSeries(axisDatasetIndex));
	}

	/**
	 * Appends a {@code S} series constructed with the specified name to the {@code D} dataset of
	 * the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the {@code D}
	 *                         dataset to append to
	 * @param name             the name of the {@code S} series to append
	 * <p>
	 * @return the index of the {@code S} series appended to the {@code D} dataset of the specified
	 *         {@link XYRangeAxisDataset}
	 */
	public abstract int addSeries(final int axisDatasetIndex, final String name);

	/**
	 * Appends the specified {@code S} series to the {@code D} dataset of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the {@code D}
	 *                         dataset to append to
	 * @param series           the {@code S} series to append
	 * <p>
	 * @return the index of the specified {@code S} series appended to the {@code D} dataset of the
	 *         specified {@link XYRangeAxisDataset}
	 */
	public abstract int addSeries(final int axisDatasetIndex, final S series);

	/**
	 * Appends a {@code S} series constructed with the specified name and {@link SeriesStyle} to the
	 * {@code D} dataset of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the {@code D}
	 *                         dataset to append to
	 * @param name             the name of the {@code S} series to append
	 * @param style            the {@link SeriesStyle} of the {@code S} series to append
	 * <p>
	 * @return the index of the {@code S} series appended to the {@code D} dataset of the specified
	 *         {@link XYRangeAxisDataset}
	 */
	public int addSeries(final int axisDatasetIndex, final String name, final SeriesStyle style) {
		final int seriesIndex = addSeries(axisDatasetIndex, name);
		styles.put(new Pair<Integer, Integer>(axisDatasetIndex, seriesIndex), style);
		return seriesIndex;
	}

	/**
	 * Appends the specified {@code S} series with the specified {@link SeriesStyle} to the
	 * {@code D} dataset of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the {@code D}
	 *                         dataset to append to
	 * @param series           the {@code S} series to append
	 * @param style            the {@link SeriesStyle} of the {@code S} series to append
	 * <p>
	 * @return the index of the specified {@code S} series appended to the {@code D} dataset of the
	 *         specified {@link XYRangeAxisDataset}
	 */
	public int addSeries(final int axisDatasetIndex, final S series, final SeriesStyle style) {
		final int seriesIndex = addSeries(axisDatasetIndex, series);
		styles.put(new Pair<Integer, Integer>(axisDatasetIndex, seriesIndex), style);
		return seriesIndex;
	}

	//////////////////////////////////////////////

	/**
	 * Removes the {@code S} series at the specified index from the {@code D} dataset of the
	 * specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the {@code D}
	 *                         dataset to remove from
	 * @param seriesIndex      the index of the {@code S} series to remove
	 */
	public abstract void removeSeries(final int axisDatasetIndex, final int seriesIndex);

	/**
	 * Removes the specified {@code S} series from the {@code D} dataset of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the {@code D}
	 *                         dataset to remove from
	 * @param series           the {@code S} series to remove
	 */
	public abstract void removeSeries(final int axisDatasetIndex, final S series);

	/**
	 * Removes all the {@code S} series from the {@code D} dataset of the specified
	 * {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} containing the {@code D}
	 *                         dataset to remove from
	 */
	public abstract void removeAllSeries(final int axisDatasetIndex);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends a point with the specified domain and range coordinates to the specified {@code S}
	 * series of the {@code D} dataset of the specified {@link XYRangeAxisDataset}.
	 * <p>
	 * @param axisDatasetIndex the index of the {@link XYRangeAxisDataset} of the {@code D} dataset
	 *                         containing the {@code S} series to append to
	 * @param seriesIndex      the index of the {@code S} series to append to
	 * @param x                the domain coordinate {@link String} of the point to append
	 * @param y                the range coordinate {@link String} of the point to append
	 */
	public abstract void addPoint(final int axisDatasetIndex, final int seriesIndex,
			final String x, final String y);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Displays {@code this}.
	 */
	public void display() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Create the chart panel
				final JFreeChart chart = createChart();
				final XYPlot plot = (XYPlot) chart.getPlot();
				for (int adi = 0; adi < axisDatasets.size(); ++adi) {
					plot.setRenderer(adi, createRenderer(adi));
				}
				final ChartPanel chartPanel = createChartPanel(chart);
				setContentPane(chartPanel);
				// Show the chart graphic
				Swings.show(ChartGraphic.this);
			}
		});
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
	@SuppressWarnings({"cast", "unchecked"})
	public ChartGraphic<D, S> clone() {
		final ChartGraphic<D, S> clone = (ChartGraphic<D, S>) super.clone();
		clone.xLabel = Objects.clone(xLabel);
		clone.axisDatasets = Objects.clone(axisDatasets);
		clone.styles = Objects.clone(styles);
		return clone;
	}
}
