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
package jupiter.graphics.charts.overlays;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.List;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.test.Arguments;
import jupiter.common.util.Objects;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.event.OverlayChangeEvent;
import org.jfree.chart.panel.AbstractOverlay;
import org.jfree.chart.panel.Overlay;
import org.jfree.chart.util.PublicCloneable;

/**
 * {@link XYSelectionOverlay} is the overlay for {@link ChartPanel} painting an
 * {@link ExtendedLinkedList} of {@link XYSelection} on a plot.
 */
public class XYSelectionOverlay
		extends AbstractOverlay
		implements ICloneable<XYSelectionOverlay>, Overlay, PropertyChangeListener, PublicCloneable,
		Serializable {

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
	 * The {@link ExtendedLinkedList} of {@link XYSelection}.
	 */
	protected ExtendedLinkedList<XYSelection> selections = new ExtendedLinkedList<XYSelection>();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link XYSelectionOverlay}.
	 */
	public XYSelectionOverlay() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends the specified {@link XYSelection} and sends an {@link OverlayChangeEvent} to all the
	 * registered listeners.
	 * <p>
	 * @param selection the {@link XYSelection} to add
	 */
	public void addSelection(final XYSelection selection) {
		// Check the arguments
		Arguments.requireNonNull(selection, "selection");

		// Add the selection
		selections.add(selection);
		selection.addPropertyChangeListener(this);
		fireOverlayChanged();
	}

	//////////////////////////////////////////////

	/**
	 * Removes the specified {@link XYSelection} and sends an {@link OverlayChangeEvent} to all the
	 * registered listeners.
	 * <p>
	 * @param selection the {@link XYSelection} to remove
	 */
	public void removeSelection(final XYSelection selection) {
		// Check the arguments
		Arguments.requireNonNull(selection, "selection");

		// Remove the selection
		selections.remove(selection);
		selection.removePropertyChangeListener(this);
		fireOverlayChanged();
	}

	/**
	 * Removes all the {@link XYSelection} and sends an {@link OverlayChangeEvent} to all the
	 * registered listeners.
	 */
	public void removeAllSelection() {
		if (selections.isNonEmpty()) {
			for (final XYSelection selection : selections) {
				selections.remove(selection);
				selection.removePropertyChangeListener(this);
			}
			fireOverlayChanged();
		}
	}

	//////////////////////////////////////////////

	/**
	 * Receives a property change event (typically a change in a {@link XYSelection}).
	 * <p>
	 * @param event a {@link PropertyChangeEvent}
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		fireOverlayChanged();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Paints the {@link List} of {@link XYSelection} in the specified {@link Graphics2D}.
	 * <p>
	 * @param g          the {@link Graphics2D} to paint with
	 * @param chartPanel the {@link ChartPanel} containing the overlay to paint
	 */
	@Override
	public void paintOverlay(final Graphics2D g, final ChartPanel chartPanel) {
		// Get the screen area
		final Rectangle2D screenArea = chartPanel.getScreenDataArea();
		if (screenArea == null) {
			return;
		}

		// Draw the selections
		final Shape savedClip = g.getClip();
		g.clip(screenArea);
		for (final XYSelection selection : selections) {
			selection.paint(g, chartPanel);
		}
		g.setClip(savedClip);
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
	public XYSelectionOverlay clone() {
		try {
			final XYSelectionOverlay clone = (XYSelectionOverlay) super.clone();
			clone.selections = Objects.clone(selections);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (other == this) {
			return true;
		}
		if (other == null || !(other instanceof XYSelectionOverlay)) {
			return false;
		}
		final XYSelectionOverlay otherXYSelectionOverlay = (XYSelectionOverlay) other;
		return selections.equals(otherXYSelectionOverlay.selections);
	}

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see #equals(Object)
	 * @see System#identityHashCode(Object)
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, selections);
	}
}
