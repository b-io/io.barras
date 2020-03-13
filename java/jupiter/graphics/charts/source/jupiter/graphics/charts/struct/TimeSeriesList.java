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

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

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
	 * The {@link List} of {@link TimeSeries}.
	 */
	protected List<TimeSeries> list;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty {@link TimeSeriesList} by default.
	 */
	public TimeSeriesList() {
		this(new ExtendedList<TimeSeries>());
	}

	/**
	 * Constructs an empty {@link TimeSeriesList} with the specified initial capacity.
	 * <p>
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public TimeSeriesList(final int initialCapacity) {
		this(new ExtendedList<TimeSeries>(initialCapacity));
	}

	/**
	 * Constructs a {@link TimeSeriesList} with the specified {@link List} of {@link TimeSeries}.
	 * <p>
	 * @param list the {@link List} of {@link TimeSeries}
	 */
	public TimeSeriesList(final List<TimeSeries> list) {
		super();
		this.list = list;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// TIME SERIES COLLECTION
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void addSeries(final TimeSeries timeSeries) {
		super.addSeries(timeSeries);
		list.add(timeSeries);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// TIME SERIES LIST
	////////////////////////////////////////////////////////////////////////////////////////////////

	public int addSeries(final String title) {
		addSeries(new TimeSeries(Arguments.requireNonNull(title, "title")));
		return list.size() - 1;
	}

	public void addValue(final int index, final Number value) {
		list.get(index)
				.addOrUpdate(new Millisecond(), Arguments.requireNonNull(value, "value"));
	}

	public void addValue(final int index, final Date time, final Number value) {
		list.get(index)
				.addOrUpdate(new Millisecond(time), Arguments.requireNonNull(value, "value"));
	}

	public void addValues(final Number[] values) {
		// Check the arguments
		ArrayArguments.requireMinLength(values, list.size());

		// Add or update the values
		final Millisecond time = new Millisecond();
		final Iterator<TimeSeries> timeSeriesIterator = list.iterator();
		int i = 0;
		while (timeSeriesIterator.hasNext()) {
			timeSeriesIterator.next()
					.addOrUpdate(time, Arguments.requireNonNull(values[i++], "value"));
		}
	}

	public int size() {
		return list.size();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public TimeSeriesList clone() {
		try {
			final TimeSeriesList clone = (TimeSeriesList) super.clone();
			clone.list = Objects.clone(list);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		}
	}
}
