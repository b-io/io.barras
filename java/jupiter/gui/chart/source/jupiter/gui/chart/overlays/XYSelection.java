/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2026 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.gui.chart.overlays;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.text.Format;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;
import jupiter.gui.chart.panels.ChartPanels;
import jupiter.gui.chart.panels.DynamicChartPanel;
import jupiter.math.analysis.struct.XY;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.util.PublicCloneable;

/**
 * {@link XYSelection} is the closest point to the mouse position in a {@link DynamicChartPanel}.
 */
public class XYSelection
		implements ICloneable<XYSelection>, PublicCloneable, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default radius.
	 */
	public static final int DEFAULT_RADIUS = 5;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The index of the range axis.
	 */
	protected final int rangeAxisIndex;

	/**
	 * The mouse position.
	 */
	protected Point mousePosition;

	/**
	 * The {@link XY}-coordinates.
	 */
	protected XY<Double> coordinates = new XY<Double>();
	/**
	 * The {@link Format} of the domain and range labels.
	 */
	protected XY<Format> formats;

	/**
	 * The radius.
	 */
	protected final int radius;

	/**
	 * The flag specifying whether {@code this} is visible.
	 */
	protected boolean isVisible;

	/**
	 * The {@link PropertyChangeSupport}.
	 */
	protected transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link XYSelection} with the specified {@link Format} of the domain and range
	 * labels.
	 *
	 * @param formats the {@link Format} of the domain and range labels
	 */
	public XYSelection(final XY<Format> formats) {
		this(formats, DEFAULT_RADIUS);
	}

	/**
	 * Constructs a {@link XYSelection} with the specified {@link Format} of the domain and range
	 * labels and radius.
	 *
	 * @param formats the {@link Format} of the domain and range labels
	 * @param radius  the radius
	 */
	public XYSelection(final XY<Format> formats, final int radius) {
		this(formats, radius, true);
	}

	/**
	 * Constructs a {@link XYSelection} with the specified {@link Format} of the domain and range
	 * labels, radius and flag specifying whether {@code this} is visible.
	 *
	 * @param formats   the {@link Format} of the domain and range labels
	 * @param radius    the radius
	 * @param isVisible the flag specifying whether {@code this} is visible
	 */
	public XYSelection(final XY<Format> formats, final int radius,
			final boolean isVisible) {
		this(0, formats, radius, isVisible);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link XYSelection} with the specified index of the range axis and
	 * {@link Format} of the domain and range labels.
	 *
	 * @param rangeAxisIndex the index of the range axis
	 * @param formats        the {@link Format} of the domain and range labels
	 */
	public XYSelection(final int rangeAxisIndex, final XY<Format> formats) {
		this(rangeAxisIndex, formats, DEFAULT_RADIUS);
	}

	/**
	 * Constructs a {@link XYSelection} with the specified index of the range axis, {@link Format}
	 * of the domain and range labels and radius.
	 *
	 * @param rangeAxisIndex the index of the range axis
	 * @param formats        the {@link Format} of the domain and range labels
	 * @param radius         the radius
	 */
	public XYSelection(final int rangeAxisIndex, final XY<Format> formats, final int radius) {
		this(rangeAxisIndex, formats, radius, true);
	}

	/**
	 * Constructs a {@link XYSelection} with the specified index of the range axis, {@link Format}
	 * of the domain and range labels, radius and flag specifying whether {@code this} is visible.
	 *
	 * @param rangeAxisIndex the index of the range axis
	 * @param formats        the {@link Format} of the domain and range labels
	 * @param radius         the radius
	 * @param isVisible      the flag specifying whether {@code this} is visible
	 */
	public XYSelection(final int rangeAxisIndex, final XY<Format> formats, final int radius,
			final boolean isVisible) {
		this.rangeAxisIndex = rangeAxisIndex;
		this.formats = formats;
		this.radius = radius;
		this.isVisible = isVisible;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the mouse position.
	 *
	 * @return the mouse position
	 */
	public Point getMousePosition() {
		return mousePosition;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link XY}-coordinates.
	 *
	 * @return the {@link XY}-coordinates
	 */
	public XY<Double> getCoordinates() {
		return coordinates;
	}

	/**
	 * Returns the domain coordinate.
	 *
	 * @return the domain coordinate
	 */
	public double getX() {
		return coordinates.getX();
	}

	/**
	 * Returns the range coordinate.
	 *
	 * @return the range coordinate
	 */
	public double getY() {
		return coordinates.getY();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the flag specifying whether {@code this} is visible.
	 *
	 * @return the flag specifying whether {@code this} is visible
	 */
	public boolean isVisible() {
		return isVisible;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the mouse position.
	 *
	 * @param mousePosition a {@link Point}
	 */
	public void setMousePosition(final Point mousePosition) {
		this.mousePosition = mousePosition;
	}

	//////////////////////////////////////////////

	/**
	 * Sets the {@link XY}-coordinates and sends a property change event to all the registered
	 * listeners.
	 *
	 * @param x a {@code double} value
	 * @param y a {@code double} value
	 */
	public void setCoordinates(final double x, final double y) {
		final XY<Double> previousValue = coordinates.clone();
		coordinates.setX(x);
		coordinates.setY(y);
		propertyChangeSupport.firePropertyChange("coordinates", previousValue, coordinates);
	}

	//////////////////////////////////////////////

	/**
	 * Sets the flag specifying whether {@code this} is visible and sends a property change event to
	 * all the registered listeners.
	 *
	 * @param isVisible a {@code boolean} value
	 */
	public void setVisible(final boolean isVisible) {
		final boolean previousValue = this.isVisible;
		this.isVisible = isVisible;
		propertyChangeSupport.firePropertyChange("isVisible", previousValue, isVisible);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds the specified {@link PropertyChangeListener}.
	 *
	 * @param propertyChangeListener a {@link PropertyChangeListener}
	 * @see #removePropertyChangeListener(PropertyChangeListener)
	 */
	public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
		propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
	}

	//////////////////////////////////////////////

	/**
	 * Removes the specified {@link PropertyChangeListener}.
	 *
	 * @param propertyChangeListener a {@link PropertyChangeListener}
	 * @see #addPropertyChangeListener(PropertyChangeListener)
	 */
	public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
		propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Paints {@code this} with the specified {@link Graphics2D} in the specified
	 * {@link ChartPanel}.
	 *
	 * @param g          the {@link Graphics2D} to paint with
	 * @param chartPanel the {@link ChartPanel} containing the overlay to paint
	 */
	public void paint(final Graphics2D g, final ChartPanel chartPanel) {
		if (mousePosition != null && isVisible) {
			// Draw the selection
			final double xSelection = ChartPanels.domainValueToJava2D(chartPanel, mousePosition,
					coordinates);
			final double ySelection = ChartPanels.rangeValueToJava2D(chartPanel, rangeAxisIndex,
					mousePosition, coordinates);
			final Ellipse2D selection = new Ellipse2D.Double(
					xSelection - radius, ySelection - radius,
					2 * radius, 2 * radius);
			g.draw(selection);
			g.fill(selection);
		}
	}

	/**
	 * Formats the specified {@link XY}-coordinates.
	 *
	 * @param coordinates the {@link XY}-coordinates of {@link Double} to format
	 * @return the formatted {@link XY}-coordinates in an array of {@link String}
	 */
	protected String[] formatCoordinates(final XY<Double> coordinates) {
		final String[] formattedCoordinates = new String[2];
		formattedCoordinates[0] = "X: " + formats.getX().format(coordinates.getX());
		formattedCoordinates[1] = "Y: " + formats.getY().format(coordinates.getY());
		return formattedCoordinates;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 *
	 * @return a clone of {@code this}
	 * @see ICloneable
	 */
	@Override
	public XYSelection clone() {
		try {
			final XYSelection clone = (XYSelection) super.clone();
			clone.mousePosition = Objects.clone(mousePosition);
			clone.coordinates = Objects.clone(coordinates);
			clone.formats = Objects.clone(formats);
			clone.propertyChangeSupport = Objects.clone(propertyChangeSupport);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 *
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (other == this) {
			return true;
		}
		if (other == null || !(other instanceof XYSelection)) {
			return false;
		}
		final XYSelection otherXYSelection = (XYSelection) other;
		return Objects.equals(rangeAxisIndex, otherXYSelection.rangeAxisIndex) &&
				Objects.equals(mousePosition, otherXYSelection.mousePosition) &&
				Objects.equals(coordinates, otherXYSelection.coordinates) &&
				Objects.equals(formats, otherXYSelection.formats) &&
				Objects.equals(radius, otherXYSelection.radius) &&
				Objects.equals(isVisible, otherXYSelection.isVisible) &&
				Objects.equals(propertyChangeSupport, otherXYSelection.propertyChangeSupport);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the hash code of {@code this}.
	 *
	 * @return the hash code of {@code this}
	 * @see #equals(Object)
	 * @see System#identityHashCode(Object)
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, rangeAxisIndex, mousePosition, coordinates,
				formats, radius, isVisible, propertyChangeSupport);
	}
}
