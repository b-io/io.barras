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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import jupiter.common.util.Integers;
import jupiter.graphics.charts.Charts;
import jupiter.math.analysis.struct.XY;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.ContourEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;

public class ChartPanels {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link ChartPanels}.
	 */
	protected ChartPanels() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the closest {@link ChartEntity} to the specified mouse event in the specified chart
	 * panel.
	 * <p>
	 * @param chartPanel a {@link ChartPanel}
	 * @param mouseEvent a {@link ChartMouseEvent}
	 * <p>
	 * @return the closest {@link ChartEntity} to the specified mouse event in the specified chart
	 *         panel
	 */
	@SuppressWarnings({"deprecation", "unchecked"})
	public static ChartEntity getEntity(final ChartPanel chartPanel,
			final ChartMouseEvent mouseEvent) {
		ChartEntity entity = mouseEvent.getEntity();

		// Test whether a chart entity is selected
		if (entity.getToolTipText() != null) {
			return entity;
		}

		// Find the closest chart entity
		// • Get all the chart entities
		final Collection<ChartEntity> entities = chartPanel.getChartRenderingInfo()
				.getEntityCollection()
				.getEntities();
		// • Get the mouse position
		final int xPosition = Integers.convert(
				(mouseEvent.getTrigger().getX() - chartPanel.getInsets().left) /
						chartPanel.getScaleX());
		final int yPosition = Integers.convert(
				(mouseEvent.getTrigger().getY() - chartPanel.getInsets().top) /
						chartPanel.getScaleY());
		final Point2D position = new Point2D.Double(xPosition, yPosition);
		// • Select the closest chart entity to the mouse position
		double minDistance = Integer.MAX_VALUE;
		for (final ChartEntity e : entities) {
			if (e instanceof CategoryItemEntity || e instanceof ContourEntity ||
					e instanceof PieSectionEntity || e instanceof XYItemEntity) {
				final Rectangle r = e.getArea().getBounds();
				final Point2D center = new Point2D.Double(r.getCenterX(), r.getCenterY());
				final double distance = position.distance(center);
				if (distance < minDistance) {
					minDistance = distance;
					entity = e;
				}
			}
		}
		return entity;
	}

	/**
	 * Returns the {@link XYPlot} at the specified position in the specified chart panel.
	 * <p>
	 * @param chartPanel a {@link ChartPanel}
	 * @param position   a {@link Point}
	 * <p>
	 * @return the {@link XYPlot} at the specified position in the specified chart panel
	 */
	public static XYPlot getPlot(final ChartPanel chartPanel, final Point position) {
		XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
		if (plot instanceof CombinedDomainXYPlot) {
			final CombinedDomainXYPlot combinedDomainPlot = (CombinedDomainXYPlot) plot;
			final Point2D point = chartPanel.translateScreenToJava2D(position);
			plot = combinedDomainPlot.findSubplot(chartPanel.getChartRenderingInfo().getPlotInfo(),
					point);
		}
		return plot;
	}

	/**
	 * Returns the screen area {@link Rectangle2D} at the specified position in the specified chart
	 * panel.
	 * <p>
	 * @param chartPanel a {@link ChartPanel}
	 * @param position   a {@link Point}
	 * <p>
	 * @return the screen area {@link Rectangle2D} at the specified position in the specified chart
	 *         panel
	 */
	public static Rectangle2D getScreenArea(final ChartPanel chartPanel, final Point position) {
		return chartPanel.getScreenDataArea(position.x, position.y);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void setDefaultParameters(final ChartPanel chartPanel) {
		Charts.setSizes(chartPanel);
		JPanels.addScrollZoom(chartPanel);
		chartPanel.setMouseZoomable(true, false);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double java2DToDomainValue(final ChartPanel chartPanel, final Point position) {
		final XYPlot plot = getPlot(chartPanel, position);
		final Rectangle2D screenArea = getScreenArea(chartPanel, position);
		final ValueAxis domainAxis = plot.getDomainAxis();
		final RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
		return domainAxis.java2DToValue(position.x, screenArea, domainAxisEdge);
	}

	public static double java2DToRangeValue(final ChartPanel chartPanel, final Point position) {
		final XYPlot plot = getPlot(chartPanel, position);
		final Rectangle2D screenArea = getScreenArea(chartPanel, position);
		final ValueAxis rangeAxis = plot.getRangeAxis();
		final RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
		return rangeAxis.java2DToValue(position.y, screenArea, rangeAxisEdge);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double domainValueToJava2D(final ChartPanel chartPanel, final Point position,
			final XY<Double> coordinates) {
		final XYPlot plot = getPlot(chartPanel, position);
		final Rectangle2D screenArea = getScreenArea(chartPanel, position);
		final ValueAxis domainAxis = plot.getDomainAxis();
		final RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
		return domainAxis.valueToJava2D(coordinates.getX(), screenArea, domainAxisEdge);
	}

	public static double rangeValueToJava2D(final ChartPanel chartPanel, final Point position,
			final XY<Double> coordinates) {
		final XYPlot plot = getPlot(chartPanel, position);
		final Rectangle2D screenArea = getScreenArea(chartPanel, position);
		final ValueAxis rangeAxis = plot.getRangeAxis();
		final RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
		return rangeAxis.valueToJava2D(coordinates.getY(), screenArea, rangeAxisEdge);
	}
}
