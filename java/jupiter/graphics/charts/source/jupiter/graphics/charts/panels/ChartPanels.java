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

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.ContourEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.entity.XYItemEntity;

import jupiter.common.util.Integers;

public class ChartPanels {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The date format.
	 */
	public static volatile DateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm");


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected ChartPanels() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// JPANELS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the closest chart entity of the specified mouse position in the specified chart
	 * panel.
	 * <p>
	 * @param panel the chart panel
	 * @param event the mouse event
	 * <p>
	 * @return the closest chart entity of the specified mouse position in the specified chart panel
	 */
	public static ChartEntity getChartEntity(final ChartPanel panel, final ChartMouseEvent event) {
		ChartEntity entity = event.getEntity();

		// Test whether a chart entity is selected
		if (entity.getToolTipText() != null) {
			return entity;
		}

		// Find the closest chart entity
		// - Get all the chart entities
		final Collection<ChartEntity> entities = panel.getChartRenderingInfo().getEntityCollection()
				.getEntities();
		// - Get the mouse position
		final int xPosition = Integers
				.convert((event.getTrigger().getX() - panel.getInsets().left) / panel.getScaleX());
		final int yPosition = Integers
				.convert((event.getTrigger().getY() - panel.getInsets().top) / panel.getScaleY());
		final Point2D position = new Point2D.Double(xPosition, yPosition);
		// - Select the closest chart entity to the mouse position
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
}
