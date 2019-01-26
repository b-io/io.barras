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
package jupiter.graphics.charts.structure;

import java.util.Date;
import java.util.List;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;

public class TimeSeriesList
		extends TimeSeriesCollection {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 9081191510467664812L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final List<TimeSeries> list;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public TimeSeriesList() {
		super();
		list = new ExtendedList<TimeSeries>();
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
		addSeries(new TimeSeries(Arguments.requireNonNull(title)));
		return list.size() - 1;
	}

	public void addValue(final int index, final Number value) {
		list.get(index).addOrUpdate(new Millisecond(), Arguments.requireNonNull(value));
	}

	public void addValue(final int index, final Date time, final Number value) {
		list.get(index).addOrUpdate(new Millisecond(time), Arguments.requireNonNull(value));
	}

	public void addValues(final Number[] values) {
		// Check the arguments
		ArrayArguments.requireMinLength(values, list.size());

		// Add or update the values
		final int size = list.size();
		final Millisecond time = new Millisecond();
		for (int i = 0; i < size; ++i) {
			list.get(i).addOrUpdate(time, Arguments.requireNonNull(values[i]));
		}
	}

	public int size() {
		return list.size();
	}
}
