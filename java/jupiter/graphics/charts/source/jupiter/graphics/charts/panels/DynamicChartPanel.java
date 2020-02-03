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
package jupiter.graphics.charts.panels;

import static jupiter.common.util.Formats.DEFAULT_FORMAT;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.text.Format;

import jupiter.common.math.Maths;
import jupiter.common.util.Integers;
import jupiter.graphics.charts.Charts;
import jupiter.graphics.charts.overlays.XYSelection;
import jupiter.graphics.charts.overlays.XYSelectionOverlay;
import jupiter.math.analysis.interpolation.LinearInterpolator;
import jupiter.math.analysis.struct.XY;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.panel.CrosshairOverlay;
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

	protected static volatile boolean DRAW_X_CROSSHAIR = true;
	protected static volatile boolean DRAW_Y_CROSSHAIR = true;

	protected static volatile boolean DRAW_SELECTION = !DRAW_X_CROSSHAIR && !DRAW_Y_CROSSHAIR;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link Format} of the x and y coordinates.
	 */
	protected final XY<Format> formats;

	/**
	 * The mouse position {@link Point}.
	 */
	protected Point mousePosition = new Point();
	/**
	 * The x and y coordinates of the mouse position.
	 */
	protected XY<Double> mouseCoordinates = new XY<Double>();

	/**
	 * The {@link XYSelection}.
	 */
	protected XYSelection selection;
	/**
	 * The vertical and horizontal {@link Crosshair}.
	 */
	protected XY<Crosshair> crosshairs;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link DynamicChartPanel} with the specified {@link JFreeChart}.
	 * <p>
	 * @param chart the {@link JFreeChart}
	 */
	public DynamicChartPanel(final JFreeChart chart) {
		super(chart);
		formats = new XY<Format>(DEFAULT_FORMAT, DEFAULT_FORMAT);
		setDefaultParameters();
	}

	/**
	 * Constructs a {@link DynamicChartPanel} with the specified {@link JFreeChart} and
	 * {@link Format} of the x coordinate.
	 * <p>
	 * @param chart   the {@link JFreeChart}
	 * @param xFormat the {@link Format} of the x coordinate
	 */
	public DynamicChartPanel(final JFreeChart chart, final Format xFormat) {
		super(chart);
		formats = new XY<Format>(xFormat, DEFAULT_FORMAT);
		setDefaultParameters();
	}

	/**
	 * Constructs a {@link DynamicChartPanel} with the specified {@link JFreeChart}, {@link Format}
	 * of the x coordinate and {@link Format} of the y coordinate.
	 * <p>
	 * @param chart   the {@link JFreeChart}
	 * @param xFormat the {@link Format} of the x coordinate
	 * @param yFormat the {@link Format} of the y coordinate
	 */
	public DynamicChartPanel(final JFreeChart chart, final Format xFormat, final Format yFormat) {
		super(chart);
		formats = new XY<Format>(xFormat, yFormat);
		setDefaultParameters();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
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
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void setDefaultParameters() {
		ChartPanels.setDefaultParameters(this);

		// Add an overlay for the selection
		selection = new XYSelection(formats);
		final XYSelectionOverlay selectionOverlay = new XYSelectionOverlay();
		selectionOverlay.addSelection(selection);
		addOverlay(selectionOverlay);

		// Add an overlay for the vertical and horizontal crosshairs
		crosshairs = new XY<Crosshair>(Charts.createCrosshair(true, formats.getX()),
				Charts.createCrosshair(true, formats.getY()));
		final CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
		crosshairOverlay.addDomainCrosshair(crosshairs.getX());
		crosshairOverlay.addRangeCrosshair(crosshairs.getY());
		addOverlay(crosshairOverlay);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public double java2DToDomainValue(final Point position) {
		return ChartPanels.java2DToDomainValue(this, position);
	}

	public double java2DToRangeValue(final Point position) {
		return ChartPanels.java2DToRangeValue(this, position);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double domainValueToJava2D(final Point position, final XY<Double> coordinates) {
		return ChartPanels.domainValueToJava2D(this, position, coordinates);
	}

	public double rangeValueToJava2D(final Point position, final XY<Double> coordinates) {
		return ChartPanels.rangeValueToJava2D(this, position, coordinates);
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
		final int minX = Integers.convert(screenArea.getMinX());
		final int maxX = Integers.convert(screenArea.getMaxX());
		final int minY = Integers.convert(screenArea.getMinY());
		final int maxY = Integers.convert(screenArea.getMaxY());

		// Set the (bounded) mouse position
		mousePosition.move(Math.max(minX, Math.min(event.getX(), maxX)),
				Math.max(minY, Math.min(event.getY(), maxY)));
		selection.setMousePosition(mousePosition);

		// Set the (bounded) mouse coordinates
		mouseCoordinates.setX(java2DToDomainValue(mousePosition));
		mouseCoordinates.setY(java2DToRangeValue(mousePosition));

		// Interpolate between the closest items
		final double x = mouseCoordinates.getX();
		double y = mouseCoordinates.getY();
		final XYPlot plot = getPlot(mousePosition);
		final XYDataset dataset = plot.getDataset();
		final int seriesCount = dataset.getSeriesCount();
		if (seriesCount > 0) {
			// Get the closest items
			double minDistance = Double.POSITIVE_INFINITY;
			int seriesIndex = 0, from = 0, to = 0;
			for (int s = 0; s < seriesCount; ++s) {
				final int itemCount = dataset.getItemCount(s);
				if (itemCount > 0) {
					int index = 0;
					while (index < itemCount - 1 && dataset.getXValue(s, index) < x) {
						++index;
					}
					final double distance = Maths.delta(dataset.getYValue(s, index), y);
					if (distance < minDistance) {
						minDistance = distance;
						seriesIndex = s;
						from = index > 0 ? index - 1 : index;
						to = index;
					}
				}
			}

			// Interpolate between the items
			final XY<Double> fromItem = new XY<Double>(dataset.getXValue(seriesIndex, from),
					dataset.getYValue(seriesIndex, from));
			final XY<Double> toItem = new XY<Double>(dataset.getXValue(seriesIndex, to),
					dataset.getYValue(seriesIndex, to));
			y = LinearInterpolator.interpolate(fromItem, toItem, x);
		}
		selection.setCoordinates(x, y);

		// Draw the crosshairs
		drawCrosshairs();
	}

	//////////////////////////////////////////////

	/**
	 * Draws the vertical and horizontal crosshairs.
	 */
	protected void drawCrosshairs() {
		if (!Double.isNaN(selection.getX()) && !Double.isNaN(selection.getY())) {
			// Update the vertical and horizontal crosshairs
			// • Vertical crosshair
			if (DRAW_X_CROSSHAIR) {
				crosshairs.getX().setValue(selection.getX());
			}
			// • Horizontal crosshair
			if (DRAW_Y_CROSSHAIR) {
				crosshairs.getY().setValue(selection.getY());
			}
		}
	}
}
