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
package jupiter.graphics.charts.struct;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.test.DoubleArguments;
import jupiter.common.util.Objects;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class TimeSeriesList
		extends TimeSeriesCollection {

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
	 * The {@link ExtendedList} of {@link TimeSeries}.
	 */
	protected ExtendedList<TimeSeries> timeSeriesList;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty {@link TimeSeriesList} by default.
	 */
	public TimeSeriesList() {
		super();
		timeSeriesList = new ExtendedList<TimeSeries>();
	}

	/**
	 * Constructs an empty {@link TimeSeriesList} with the specified initial capacity.
	 * <p>
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public TimeSeriesList(final int initialCapacity) {
		super();
		timeSeriesList = new ExtendedList<TimeSeries>(initialCapacity);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link TimeSeriesList} with the specified elements.
	 * <p>
	 * @param elements an array of {@link TimeSeries}
	 */
	public TimeSeriesList(final TimeSeries... elements) {
		super();
		timeSeriesList = new ExtendedList<TimeSeries>(elements);
	}

	/**
	 * Constructs a {@link TimeSeriesList} with the elements of the specified {@link Collection}.
	 * <p>
	 * @param elements a {@link Collection} of {@link TimeSeries}
	 */
	public TimeSeriesList(final Collection<? extends TimeSeries> elements) {
		super();
		timeSeriesList = new ExtendedList<TimeSeries>(elements);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of {@link TimeSeries}.
	 * <p>
	 * @return the number of {@link TimeSeries}
	 */
	public int size() {
		return timeSeriesList.size();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends the specified {@link TimeSeries} to the end of {@code this}.
	 * <p>
	 * @param timeSeries the {@link TimeSeries} to append
	 */
	@Override
	public void addSeries(final TimeSeries timeSeries) {
		super.addSeries(timeSeries);
		timeSeriesList.add(timeSeries);
	}

	/**
	 * Appends a {@link TimeSeries} constructed with the specified name to the end of {@code this}.
	 * <p>
	 * @param name the name of the {@link TimeSeries} to append
	 * <p>
	 * @return the index of the {@link TimeSeries} appended to the end of {@code this}
	 */
	public int addSeries(final String name) {
		addSeries(new TimeSeries(Arguments.requireNonNull(name, "name")));
		return timeSeriesList.size() - 1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends a point with the specified range coordinate to the specified {@link TimeSeries}.
	 * <p>
	 * @param index the index of the {@link TimeSeries} to append to
	 * @param y     the {@code double} range coordinate of the point to append
	 */
	public void addPoint(final int index, final double y) {
		addPoint(index, new Date(), y);
	}

	/**
	 * Appends a point with the specified domain and range coordinates to the specified
	 * {@link TimeSeries}.
	 * <p>
	 * @param index the index of the {@link TimeSeries} to append to
	 * @param x     the {@code double} domain coordinate of the point to append
	 * @param y     the {@code double} range coordinate of the point to append
	 */
	public void addPoint(final int index, final Date x, final double y) {
		timeSeriesList.get(index)
				.addOrUpdate(new Millisecond(Arguments.requireNonNull(x, "x-coordinate")), y);
	}

	/**
	 * Appends a point for each specified range coordinate to the respective {@link TimeSeries}.
	 * <p>
	 * @param Y the {@code double} range coordinate of each point to append
	 */
	public void addPointToAll(final double[] Y) {
		// Check the arguments
		DoubleArguments.requireMinLength(Y, timeSeriesList.size());

		// Add or update the points
		final Iterator<TimeSeries> timeSeriesIterator = timeSeriesList.iterator();
		int i = 0;
		while (timeSeriesIterator.hasNext()) {
			timeSeriesIterator.next().addOrUpdate(new Millisecond(), Y[i++]);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Appends a point with the specified range coordinate to the specified {@link TimeSeries}.
	 * <p>
	 * @param index the index of the {@link TimeSeries} to append to
	 * @param y     the range coordinate {@link Number} of the point to append
	 */
	public void addPoint(final int index, final Number y) {
		addPoint(index, new Date(), y);
	}

	/**
	 * Appends a point with the specified domain and range coordinates to the specified
	 * {@link TimeSeries}.
	 * <p>
	 * @param index the index of the {@link TimeSeries} to append to
	 * @param x     the domain coordinate {@link Number} of the point to append
	 * @param y     the range coordinate {@link Number} of the point to append
	 */
	public void addPoint(final int index, final Date x, final Number y) {
		timeSeriesList.get(index)
				.addOrUpdate(new Millisecond(Arguments.requireNonNull(x, "x-coordinate")),
						Arguments.requireNonNull(y, "y-coordinate"));
	}

	/**
	 * Appends a point for each specified range coordinate to the respective {@link TimeSeries}.
	 * <p>
	 * @param Y the range coordinate {@link Number} of each point to append
	 */
	public void addPointToAll(final Number[] Y) {
		// Check the arguments
		ArrayArguments.requireMinLength(Y, timeSeriesList.size());

		// Add or update the points
		final Iterator<TimeSeries> timeSeriesIterator = timeSeriesList.iterator();
		int i = 0;
		while (timeSeriesIterator.hasNext()) {
			timeSeriesIterator.next()
					.addOrUpdate(new Millisecond(),
							Arguments.requireNonNull(Y[i++], "y-coordinate"));
		}
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
	public TimeSeriesList clone() {
		try {
			final TimeSeriesList clone = (TimeSeriesList) super.clone();
			clone.timeSeriesList = Objects.clone(timeSeriesList);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}
}
