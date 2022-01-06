/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.graphics.charts.panels;

import static jupiter.common.Formats.FORMAT;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.text.Format;

import jupiter.common.math.Maths;
import jupiter.common.util.Arrays;
import jupiter.graphics.charts.Charts;
import jupiter.graphics.charts.overlays.XYCrosshairOverlay;
import jupiter.graphics.charts.overlays.XYSelection;
import jupiter.graphics.charts.overlays.XYSelectionOverlay;
import jupiter.math.analysis.interpolation.LinearInterpolator;
import jupiter.math.analysis.struct.XY;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

public class DynamicChartPanel
		extends ChartPanel {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile boolean SHOW_CROSSHAIR_OUTSIDE = true;

	public static volatile boolean DRAW_X_CROSSHAIR = true;
	public static volatile boolean DRAW_Y_CROSSHAIR = true;

	public static volatile boolean DRAW_SELECTION = !DRAW_X_CROSSHAIR && !DRAW_Y_CROSSHAIR;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The number of range axes.
	 */
	protected final int rangeAxisCount;

	/**
	 * The {@link Format} of the domain label.
	 */
	protected final Format xFormat;
	/**
	 * The {@link Format} of each range label.
	 */
	protected final Format[] yFormats;

	/**
	 * The mouse position {@link Point}.
	 */
	protected Point mousePosition = new Point();

	/**
	 * The array of {@link XYSelection}.
	 */
	protected XYSelection[] selections;
	/**
	 * The domain {@link Crosshair}.
	 */
	protected Crosshair xCrosshair;
	/**
	 * The array of range {@link Crosshair}.
	 */
	protected Crosshair[] yCrosshairs;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link DynamicChartPanel} with the specified chart.
	 * <p>
	 * @param chart the chart
	 */
	public DynamicChartPanel(final JFreeChart chart) {
		this(chart, FORMAT);
	}

	/**
	 * Constructs a {@link DynamicChartPanel} with the specified chart and {@link Format} of the
	 * domain label.
	 * <p>
	 * @param chart   the chart
	 * @param xFormat the {@link Format} of the domain label
	 */
	public DynamicChartPanel(final JFreeChart chart, final Format xFormat) {
		this(chart, xFormat, FORMAT);
	}

	/**
	 * Constructs a {@link DynamicChartPanel} with the specified chart, {@link Format} of the domain
	 * label and {@link Format} of each range label.
	 * <p>
	 * @param chart    the chart
	 * @param xFormat  the {@link Format} of the domain label
	 * @param yFormats the {@link Format} of each range label
	 */
	public DynamicChartPanel(final JFreeChart chart, final Format xFormat,
			final Format... yFormats) {
		super(chart);
		rangeAxisCount = chart.getXYPlot().getDatasetCount();
		this.xFormat = xFormat;
		if (yFormats.length == 1) {
			this.yFormats = Arrays.<Format>repeat(yFormats[0], rangeAxisCount);
		} else {
			this.yFormats = yFormats;
		}
		setDefaultParameters();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the closest {@link ChartEntity} to the specified mouse event.
	 * <p>
	 * @param mouseEvent a {@link ChartMouseEvent}
	 * <p>
	 * @return the closest {@link ChartEntity} to the specified mouse event
	 */
	public ChartEntity getEntity(final ChartMouseEvent mouseEvent) {
		return ChartPanels.getEntity(this, mouseEvent);
	}

	/**
	 * Returns the {@link XYPlot} at the specified position.
	 * <p>
	 * @param position a {@link Point}
	 * <p>
	 * @return the {@link XYPlot} at the specified position
	 */
	public XYPlot getPlot(final Point position) {
		return ChartPanels.getPlot(this, position);
	}

	/**
	 * Returns the screen area {@link Rectangle2D} at the specified position.
	 * <p>
	 * @param position a {@link Point}
	 * <p>
	 * @return the screen area {@link Rectangle2D} at the specified position
	 */
	public Rectangle2D getScreenArea(final Point position) {
		return ChartPanels.getScreenArea(this, position);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the parameters by default.
	 */
	public void setDefaultParameters() {
		ChartPanels.setDefaultParameters(this);

		// Add an overlay for the domain and range crosshairs
		final XYCrosshairOverlay crosshairOverlay = new XYCrosshairOverlay();
		xCrosshair = Charts.createCrosshair(true, xFormat);
		crosshairOverlay.addDomainCrosshair(xCrosshair);
		yCrosshairs = new Crosshair[rangeAxisCount];
		for (int rai = 0; rai < rangeAxisCount; ++rai) {
			yCrosshairs[rai] = Charts.createCrosshair(true, yFormats[rai], rai);
			crosshairOverlay.addRangeCrosshair(yCrosshairs[rai]);
			crosshairOverlay.mapCrosshairToRangeAxis(rai, rai);
		}
		addOverlay(crosshairOverlay);

		// Add an overlay for the selections
		final XYSelectionOverlay selectionOverlay = new XYSelectionOverlay();
		selections = new XYSelection[rangeAxisCount];
		for (int rai = 0; rai < rangeAxisCount; ++rai) {
			selections[rai] = new XYSelection(rai, new XY<Format>(xFormat, yFormats[rai]));
			selectionOverlay.addSelection(selections[rai]);
		}
		addOverlay(selectionOverlay);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public double java2DToDomainValue(final Point position) {
		return ChartPanels.java2DToDomainValue(this, position);
	}

	public double java2DToRangeValue(final int rangeAxisIndex, final Point position) {
		return ChartPanels.java2DToRangeValue(this, rangeAxisIndex, position);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double domainValueToJava2D(final Point position, final XY<Double> coordinates) {
		return ChartPanels.domainValueToJava2D(this, position, coordinates);
	}

	public double rangeValueToJava2D(final int rangeAxisIndex, final Point position,
			final XY<Double> coordinates) {
		return ChartPanels.rangeValueToJava2D(this, rangeAxisIndex, position, coordinates);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MOUSE LISTENER
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseMoved(final MouseEvent event) {
		// Get the screen area
		final Rectangle2D screenArea = getScreenArea(mousePosition);
		if (screenArea == null) {
			return;
		}

		// Set the mouse position
		mousePosition.move(event.getX(), event.getY());
		for (final XYSelection selection : selections) {
			selection.setMousePosition(mousePosition);
		}

		// Update the coordinates of all the selections
		for (int rai = 0; rai < rangeAxisCount; ++rai) {
			// Set the mouse coordinates
			final double x = java2DToDomainValue(mousePosition);
			final double y = java2DToRangeValue(rai, mousePosition);

			// Find the closest interpolated point to the mouse position
			final XYPlot plot = getPlot(mousePosition);
			final XYDataset dataset = plot.getDataset(rai);
			final int seriesCount = dataset.getSeriesCount();
			double minDistance = Double.POSITIVE_INFINITY;
			double xPoint = Double.NaN, yPoint = Double.NaN;
			for (int si = 0; si < seriesCount; ++si) {
				// • Find the closest points of the series to the mouse position
				final int pointCount = dataset.getItemCount(si);
				if (pointCount == 0) {
					continue;
				}
				int toPointIndex = 0;
				while (toPointIndex < pointCount - 1 && dataset.getXValue(si, toPointIndex) < x) {
					++toPointIndex;
				}
				final int fromPointIndex = toPointIndex > 0 ? toPointIndex - 1 : toPointIndex;
				if (!SHOW_CROSSHAIR_OUTSIDE && (dataset.getXValue(si, fromPointIndex) > x ||
						dataset.getXValue(si, toPointIndex) < x)) {
					continue;
				}
				// • Interpolate between them
				final double xp = Maths.bound(x,
						dataset.getX(si, fromPointIndex).doubleValue(),
						dataset.getX(si, toPointIndex).doubleValue());
				final XY<Double> fromPoint = new XY<Double>(dataset.getXValue(si, fromPointIndex),
						dataset.getYValue(si, fromPointIndex));
				final XY<Double> toPoint = new XY<Double>(dataset.getXValue(si, toPointIndex),
						dataset.getYValue(si, toPointIndex));
				final double yp = new LinearInterpolator(fromPoint, toPoint).apply(xp);
				// • Compute the distance between the series and the mouse position
				final double distance = Maths.distance(yp, y);
				if (distance < minDistance) {
					minDistance = distance;
					xPoint = xp;
					yPoint = yp;
				}
			}

			// Update the coordinates of the selection
			selections[rai].setCoordinates(xPoint, yPoint);
		}

		// Update the coordinates of all the crosshairs
		updateAllCrosshairs();
	}

	//////////////////////////////////////////////

	/**
	 * Updates the coordinates of all the domain and range {@link Crosshair}.
	 */
	protected void updateAllCrosshairs() {
		if (Arrays.isNonEmpty(selections)) {
			// • The domain crosshair
			if (DRAW_X_CROSSHAIR) {
				xCrosshair.setValue(selections[0].getX());
			}
			// • The range crosshairs
			if (DRAW_Y_CROSSHAIR) {
				for (int rai = 0; rai < rangeAxisCount; ++rai) {
					yCrosshairs[rai].setValue(selections[rai].getY());
				}
			}
		}
	}
}
