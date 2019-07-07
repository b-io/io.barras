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
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.text.Format;

import org.jfree.chart.ChartPanel;
import org.jfree.util.PublicCloneable;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;
import jupiter.graphics.charts.panels.ChartPanels;
import jupiter.math.analysis.struct.XY;

/**
 * A selection to display on a plot.
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
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The mouse position.
	 */
	protected Point mousePosition;

	/**
	 * The x and y coordinates.
	 */
	protected XY<Double> coordinates = new XY<Double>();
	/**
	 * The formats of the x and y coordinates.
	 */
	protected XY<Format> formats;

	/**
	 * The flag specifying whether {@code this} is visible.
	 */
	protected boolean isVisible;

	/**
	 * The property change support.
	 */
	protected transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link XYSelection} with the specified formats of the x and y coordinates.
	 * <p>
	 * @param formats a {@link XY} of {@link Format}
	 */
	public XYSelection(final XY<Format> formats) {
		this(formats, true);
	}

	/**
	 * Constructs a {@link XYSelection} with the specified formats of the x and y coordinates and
	 * flag specifying whether {@code this} is visible.
	 * <p>
	 * @param formats   a {@link XY} of {@link Format}
	 * @param isVisible the flag specifying whether {@code this} is visible
	 */
	public XYSelection(final XY<Format> formats, final boolean isVisible) {
		this.formats = formats;
		this.isVisible = isVisible;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the mouse position.
	 * <p>
	 * @return the mouse position
	 */
	public Point getMousePosition() {
		return mousePosition;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the x and y coordinates.
	 * <p>
	 * @return the x and y coordinates
	 */
	public XY<Double> getCoordinates() {
		return coordinates;
	}

	/**
	 * Returns the x coordinate.
	 * <p>
	 * @return the x coordinate
	 */
	public double getX() {
		return coordinates.getX();
	}

	/**
	 * Returns the y coordinate.
	 * <p>
	 * @return the y coordinate
	 */
	public double getY() {
		return coordinates.getY();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the flag specifying whether {@code this} is visible.
	 * <p>
	 * @return the flag specifying whether {@code this} is visible
	 */
	public boolean isVisible() {
		return isVisible;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the mouse position.
	 * <p>
	 * @param mousePosition a {@link Point}
	 */
	public void setMousePosition(final Point mousePosition) {
		this.mousePosition = mousePosition;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the x and y coordinates and sends a property change event to all registered listeners.
	 * <p>
	 * @param x a {@code double} value
	 * @param y a {@code double} value
	 */
	public void setCoordinates(final double x, final double y) {
		final XY<Double> oldValue = coordinates.clone();
		coordinates.setX(x);
		coordinates.setY(y);
		propertyChangeSupport.firePropertyChange("coordinates", oldValue, coordinates);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the flag specifying whether {@code this} is visible and sends a property change event to
	 * all registered listeners.
	 * <p>
	 * @param isVisible the flag specifying whether {@code this} is visible
	 */
	public void setVisible(final boolean isVisible) {
		final boolean oldValue = this.isVisible;
		this.isVisible = isVisible;
		propertyChangeSupport.firePropertyChange("isVisible", oldValue, isVisible);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds the specified property change listener.
	 * <p>
	 * @param propertyChangeListener a {@link PropertyChangeListener}
	 *
	 * @see #removePropertyChangeListener(PropertyChangeListener)
	 */
	public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
		propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
	}

	/**
	 * Removes the specified property change listener.
	 * <p>
	 * @param propertyChangeListener a {@link PropertyChangeListener}
	 *
	 * @see #addPropertyChangeListener(PropertyChangeListener)
	 */
	public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
		propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public void draw(final Graphics2D g, final ChartPanel chartPanel) {
		if (mousePosition == null ||
				Double.isNaN(coordinates.getX()) || Double.isNaN(coordinates.getY())) {
			return;
		}

		if (isVisible) {
			// Draw the selection
			final double xSelection = ChartPanels.domainValueToJava2D(chartPanel, mousePosition,
					coordinates);
			final double ySelection = ChartPanels.rangeValueToJava2D(chartPanel, mousePosition,
					coordinates);
			final Ellipse2D selection = new Ellipse2D.Double(
					xSelection - 5, ySelection - 5,
					10, 10);
			g.draw(selection);
			g.fill(selection);
		}
	}

	/**
	 * Formats the specified x and y coordinates.
	 * <p>
	 * @param coordinates a {@link XY} of {@link Double}
	 * <p>
	 * @return the formatted x and y coordinates in an array of {@link String}
	 */
	protected String[] formatCoordinates(final XY<Double> coordinates) {
		final String[] formattedCoordinates = new String[2];
		formattedCoordinates[0] = "X:" + formats.getX().format(coordinates.getX());
		formattedCoordinates[1] = "Y:" + formats.getY().format(coordinates.getY());
		return formattedCoordinates;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
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
			throw new RuntimeException(Strings.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (other == this) {
			return true;
		}
		if (!(other instanceof XYSelection)) {
			return false;
		}
		final XYSelection otherXYSelection = (XYSelection) other;
		return Objects.equals(mousePosition, otherXYSelection.mousePosition) &&
				Objects.equals(coordinates, otherXYSelection.coordinates) &&
				Objects.equals(formats, otherXYSelection.formats) &&
				Objects.equals(isVisible, otherXYSelection.isVisible) &&
				Objects.equals(propertyChangeSupport, otherXYSelection.propertyChangeSupport);
	}

	/**
	 * Returns the hash code for {@code this}.
	 * <p>
	 * @return the hash code for {@code this}
	 *
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, mousePosition, coordinates, formats, isVisible,
				propertyChangeSupport);
	}
}
