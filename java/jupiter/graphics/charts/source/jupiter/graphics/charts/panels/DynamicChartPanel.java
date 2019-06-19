/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Strings.SPACE;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.Format;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import jupiter.common.math.Maths;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.util.Integers;
import jupiter.common.util.Strings;

public class DynamicChartPanel
		extends ChartPanel {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 6804628237598646797L;

	protected static volatile boolean DRAW_X_CROSSHAIR = true;
	protected static volatile boolean DRAW_Y_CROSSHAIR = false;

	protected static volatile boolean DRAW_SELECTION = true;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The format of the x coordinate.
	 */
	protected final Format xFormat;
	/**
	 * The format of the y coordinate.
	 */
	protected final Format yFormat;

	/**
	 * The mouse position.
	 */
	protected int xMousePosition, yMousePosition;
	/**
	 * The x coordinate of the mouse position.
	 */
	protected double xMouseCoordinate;
	/**
	 * The y coordinate of the mouse position.
	 */
	protected double yMouseCoordinate;

	/**
	 * The vertical line of the crosshair.
	 */
	protected Line2D xCrosshair;
	/**
	 * The horizontal line of the crosshair.
	 */
	protected Line2D yCrosshair;

	/**
	 * The selection.
	 */
	protected Ellipse2D selection;
	/**
	 * The x coordinate of the selection.
	 */
	protected double xSelectionCoordinate = Double.NaN;
	/**
	 * The y coordinate of the selection.
	 */
	protected double ySelectionCoordinate = Double.NaN;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public DynamicChartPanel(final JFreeChart chart) {
		super(chart);
		xFormat = null;
		yFormat = null;
		setDefaultParameters();
	}

	public DynamicChartPanel(final JFreeChart chart, final Format xFormat) {
		super(chart);
		this.xFormat = xFormat;
		yFormat = null;
		setDefaultParameters();
	}

	public DynamicChartPanel(final JFreeChart chart, final Format xFormat, final Format yFormat) {
		super(chart);
		this.xFormat = xFormat;
		this.yFormat = yFormat;
		setDefaultParameters();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public XYPlot getXYPlot() {
		XYPlot plot = (XYPlot) getChart().getPlot();
		if (plot instanceof CombinedDomainXYPlot) {
			final CombinedDomainXYPlot combinedDomainPlot = (CombinedDomainXYPlot) plot;
			final Point2D point = translateScreenToJava2D(
					new Point(xMousePosition, yMousePosition));
			plot = combinedDomainPlot.findSubplot(getChartRenderingInfo().getPlotInfo(), point);
		}
		return plot;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void setDefaultParameters() {
		ChartPanels.setDefaultParameters(this);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Formats the specified x and y coordinates.
	 * <p>
	 * @param xCoordinate a {@code double} value
	 * @param yCoordinate a {@code double} value
	 * <p>
	 * @return the formatted x and y coordinates in an {@link ExtendedList}
	 */
	public ExtendedList<Pair<String, String>> formatXY(final double xCoordinate,
			final double yCoordinate) {
		final ExtendedList<Pair<String, String>> info = new ExtendedList<Pair<String, String>>(2);
		info.add(new Pair<String, String>("X:", formatX(xCoordinate)));
		info.add(new Pair<String, String>("Y:", formatY(yCoordinate)));
		return info;
	}

	/**
	 * Formats the specified x coordinate.
	 * <p>
	 * @param xCoordinate a {@code double} value
	 * <p>
	 * @return the formatted x coordinate
	 */
	public String formatX(final double xCoordinate) {
		if (xFormat != null) {
			if (xFormat instanceof DateFormat) {
				return xFormat.format(new Date((long) xCoordinate));
			}
			return xFormat.format(xCoordinate);
		}
		return Strings.toString(xCoordinate);
	}

	/**
	 * Formats the specified y coordinate.
	 * <p>
	 * @param yCoordinate a {@code double} value
	 * <p>
	 * @return the formatted y coordinate
	 */
	public String formatY(final double yCoordinate) {
		if (yFormat != null) {
			if (yFormat instanceof DateFormat) {
				return yFormat.format(new Date((long) yCoordinate));
			}
			return yFormat.format(yCoordinate);
		}
		return Strings.toString(yCoordinate);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double java2DToDomainValue(final double position, final Rectangle2D screenDataArea) {
		final XYPlot plot = getXYPlot();
		final ValueAxis domainAxis = plot.getDomainAxis();
		final RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
		return domainAxis.java2DToValue(position, screenDataArea, domainAxisEdge);
	}

	public double java2DToRangeValue(final double position, final Rectangle2D screenDataArea) {
		final XYPlot plot = getXYPlot();
		final ValueAxis rangeAxis = plot.getRangeAxis();
		final RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
		return rangeAxis.java2DToValue(position, screenDataArea, rangeAxisEdge);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double domainValueToJava2D(final double position, final Rectangle2D screenDataArea) {
		final XYPlot plot = getXYPlot();
		final ValueAxis domainAxis = plot.getDomainAxis();
		final RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
		return domainAxis.valueToJava2D(position, screenDataArea, domainAxisEdge);
	}

	public double rangeValueToJava2D(final double position, final Rectangle2D screenDataArea) {
		final XYPlot plot = getXYPlot();
		final ValueAxis rangeAxis = plot.getRangeAxis();
		final RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
		return rangeAxis.valueToJava2D(position, screenDataArea, rangeAxisEdge);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Draws the crosshairs.
	 */
	protected void drawCrosshair() {
		if (!DRAW_X_CROSSHAIR && !DRAW_Y_CROSSHAIR) {
			return;
		}

		// Get the boundaries
		final Rectangle2D screenDataArea = getScreenDataArea(xMousePosition, yMousePosition);
		if (screenDataArea == null) {
			return;
		}
		final int minX = Integers.convert(screenDataArea.getMinX());
		final int maxX = Integers.convert(screenDataArea.getMaxX());
		final int minY = Integers.convert(screenDataArea.getMinY());
		final int maxY = Integers.convert(screenDataArea.getMaxY());

		// Get the graphics
		final Graphics2D g = (Graphics2D) getGraphics();

		// Clear the previous crosshairs
		g.setXORMode(new Color(0xFFFF00));
		if (DRAW_X_CROSSHAIR && xCrosshair != null) {
			g.draw(xCrosshair);
		}
		if (DRAW_Y_CROSSHAIR && yCrosshair != null) {
			g.draw(yCrosshair);
		}

		// Draw the crosshairs
		if (DRAW_X_CROSSHAIR) {
			xCrosshair = new Line2D.Double(xMousePosition, minY, xMousePosition, maxY);
			g.draw(xCrosshair);
		}
		if (DRAW_Y_CROSSHAIR) {
			yCrosshair = new Line2D.Double(minX, yMousePosition, maxX, yMousePosition);
			g.draw(yCrosshair);
		}
		g.dispose();
	}

	/**
	 * Draws the selection.
	 */
	protected void drawSelection() {
		if (!DRAW_SELECTION) {
			return;
		}

		// Get the boundaries
		final Rectangle2D screenDataArea = getScreenDataArea(xMousePosition, yMousePosition);
		if (screenDataArea == null) {
			return;
		}

		// Get the graphics
		final Graphics2D g = (Graphics2D) getGraphics();

		// Clear the previous selection
		g.setXORMode(new Color(0xFFFF00));
		if (selection != null) {
			g.draw(selection);
			g.fill(selection);
		}

		// Select the closest item
		final XYPlot plot = getXYPlot();
		final XYDataset dataset = plot.getDataset();
		final int seriesCount = dataset.getSeriesCount();
		double minDistance = Double.POSITIVE_INFINITY;
		for (int s = 0; s < seriesCount; ++s) {
			final int itemCount = dataset.getItemCount(s);
			if (itemCount > 0) {
				int i = 0;
				while (i < itemCount - 1 && dataset.getXValue(s, i) < xMouseCoordinate) {
					++i;
				}
				final double distance = Maths.delta(dataset.getYValue(s, i), yMouseCoordinate);
				if (distance < minDistance) {
					minDistance = distance;
					xSelectionCoordinate = dataset.getXValue(s, i);
					ySelectionCoordinate = dataset.getYValue(s, i);
				}
			}
		}
		if (xSelectionCoordinate == Double.NaN || ySelectionCoordinate == Double.NaN) {
			return;
		}

		// Draw the selection
		IO.test(xSelectionCoordinate, SPACE, ySelectionCoordinate);
		final double xSelection = domainValueToJava2D(xSelectionCoordinate, screenDataArea);
		final double ySelection = rangeValueToJava2D(ySelectionCoordinate, screenDataArea);
		selection = new Ellipse2D.Double(xSelection - 5, ySelection - 5, 10, 10);
		g.draw(selection);
		g.fill(selection);
		g.dispose();
	}

	/**
	 * Draws the formatted coordinates of the selection.
	 */
	protected void drawSelectionCoordinates() {
		if (!DRAW_SELECTION) {
			return;
		}

		// Get the boundaries
		final Rectangle2D screenDataArea = getScreenDataArea(xMousePosition, yMousePosition);
		if (screenDataArea == null) {
			return;
		}
		final int maxX = Integers.convert(screenDataArea.getMaxX());

		// Format the selection coordinates
		final List<Pair<String, String>> coordinates = formatXY(xSelectionCoordinate,
				ySelectionCoordinate);
		final int coordinateCount = coordinates.size();

		// Draw the selection coordinates
		final Graphics2D g = (Graphics2D) getGraphics();
		final FontMetrics fontMetrics = g.getFontMetrics();
		final int fontHeight = fontMetrics.getHeight();
		final double stepCount = 8.;
		final double step = Math.floor(maxX / stepCount);
		final double fromStepNumber = 0.75 * stepCount;
		final double fromStep = fromStepNumber * step;
		g.setColor(Color.WHITE);
		g.fillRect(Integers.convert(fromStep), 0, Integers.convert(maxX - fromStep),
				Integers.convert(1.5 * fontHeight));
		g.setColor(Color.BLACK);
		for (int i = 0; i < coordinateCount; ++i) {
			final Pair<String, String> coordinate = coordinates.get(i);
			final int position = Integers.convert((fromStepNumber + i) * step);
			g.drawString(coordinate.getFirst() + SPACE + coordinate.getSecond(), position,
					fontHeight);
		}
		g.dispose();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CHART PANEL
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseMoved(final MouseEvent event) {
		// Get the boundaries
		final Rectangle2D screenDataArea = getScreenDataArea(xMousePosition, yMousePosition);
		if (screenDataArea == null) {
			return;
		}
		final int minX = Integers.convert(screenDataArea.getMinX());
		final int maxX = Integers.convert(screenDataArea.getMaxX());
		final int minY = Integers.convert(screenDataArea.getMinY());
		final int maxY = Integers.convert(screenDataArea.getMaxY());

		// Set the (bounded) mouse position
		xMousePosition = Math.max(minX, Math.min(event.getX(), maxX));
		yMousePosition = Math.max(minY, Math.min(event.getY(), maxY));

		// Set the (bounded) mouse coordinates
		xMouseCoordinate = java2DToDomainValue(xMousePosition, screenDataArea);
		yMouseCoordinate = java2DToRangeValue(yMousePosition, screenDataArea);

		// Redraw
		drawCrosshair();
		drawSelection();
		drawSelectionCoordinates();
	}
}
