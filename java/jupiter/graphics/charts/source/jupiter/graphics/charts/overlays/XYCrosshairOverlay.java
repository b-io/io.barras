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
package jupiter.graphics.charts.overlays;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.map.hash.ExtendedHashMap;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;

/**
 * {@link XYCrosshairOverlay} is the extended {@link CrosshairOverlay} supporting multiple range
 * axes.
 */
public class XYCrosshairOverlay
		extends CrosshairOverlay {

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

	/*
	 * The domain {@link ExtendedList} of {@link Crosshair}.
	 */
	protected final ExtendedList<Crosshair> xCrosshairs = new ExtendedList<Crosshair>();
	/*
	 * The range {@link ExtendedList} of {@link Crosshair}.
	 */
	protected final ExtendedList<Crosshair> yCrosshairs = new ExtendedList<Crosshair>();
	/*
	 * The index of the range axis of each {@link Crosshair}.
	 */
	protected final ExtendedHashMap<Integer, Integer> rangeAxisIndices = new ExtendedHashMap<Integer, Integer>();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link XYCrosshairOverlay}.
	 */
	public XYCrosshairOverlay() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void addDomainCrosshair(Crosshair crosshair) {
		super.addDomainCrosshair(crosshair);
		xCrosshairs.add(crosshair);
	}

	@Override
	public void addRangeCrosshair(Crosshair crosshair) {
		super.addRangeCrosshair(crosshair);
		yCrosshairs.add(crosshair);

	}

	//////////////////////////////////////////////

	/**
	 * Maps the specified range {@link Crosshair} to the specified range axis.
	 * <p>
	 * @param crosshairIndex the index of the {@link Crosshair} to map
	 * @param rangeAxisIndex the range axis of the {@link Crosshair} to map
	 */
	public void mapCrosshairToRangeAxis(final int crosshairIndex, final int rangeAxisIndex) {
		rangeAxisIndices.put(crosshairIndex, rangeAxisIndex);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Paints {@code this} with the specified {@link Graphics2D} in the specified
	 * {@link ChartPanel}.
	 * <p>
	 * @param g2         the {@link Graphics2D} to paint with
	 * @param chartPanel the {@link ChartPanel} containing the overlay to paint
	 */
	@Override
	public void paintOverlay(final Graphics2D g2, final ChartPanel chartPanel) {
		// Initialize
		final Shape savedClip = g2.getClip();
		final Rectangle2D screenArea = chartPanel.getScreenDataArea();
		g2.clip(screenArea);
		final JFreeChart chart = chartPanel.getChart();
		final XYPlot plot = (XYPlot) chart.getPlot();

		// Paint
		// • The domain crosshair
		final ValueAxis xAxis = plot.getDomainAxis();
		final RectangleEdge xAxisEdge = plot.getDomainAxisEdge();
		for (final Crosshair xCrosshair : xCrosshairs) {
			if (!xCrosshair.isVisible()) {
				continue;
			}
			double x = xCrosshair.getValue();
			double xx = xAxis.valueToJava2D(x, screenArea, xAxisEdge);
			if (plot.getOrientation() == PlotOrientation.VERTICAL) {
				drawVerticalCrosshair(g2, screenArea, xx, xCrosshair);
			} else {
				drawHorizontalCrosshair(g2, screenArea, xx, xCrosshair);
			}
		}
		// • The range crosshairs
		for (int i = 0; i < yCrosshairs.size(); ++i) {
			Crosshair yCrosshair = yCrosshairs.get(i);
			if (!yCrosshair.isVisible()) {
				continue;
			}
			int rangeAxisIndex = rangeAxisIndices.getOrDefault(i, 0);
			if (rangeAxisIndex >= plot.getRangeAxisCount()) {
				continue;
			}
			final ValueAxis yAxis = plot.getRangeAxis(rangeAxisIndex);
			final RectangleEdge yAxisEdge = plot.getRangeAxisEdge(rangeAxisIndex);
			double y = yCrosshair.getValue();
			double yy = yAxis.valueToJava2D(y, screenArea, yAxisEdge);
			if (plot.getOrientation() == PlotOrientation.VERTICAL) {
				drawHorizontalCrosshair(g2, screenArea, yy, yCrosshair);
			} else {
				drawVerticalCrosshair(g2, screenArea, yy, yCrosshair);
			}
		}
		g2.setClip(savedClip);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Object clone()
			throws CloneNotSupportedException {
		return super.clone();
	}
}
