/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.common.time;

import jupiter.common.util.Longs;
import jupiter.common.util.Strings;

public class Chronometer {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The number of time units.
	 */
	public static final int N_TIME_UNITS = TimeUnit.values().length;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final long[] time = new long[N_TIME_UNITS];
	protected final double[] timeByUnit = new double[N_TIME_UNITS];
	protected long begin = 0L, end = 0L;
	protected long difference = 0L;
	protected String representation = Strings.EMPTY;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Chronometer() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public long getMilliseconds() {
		return difference;
	}

	public double getSeconds() {
		return timeByUnit[TimeUnit.SECOND.value];
	}

	public double getMinutes() {
		return timeByUnit[TimeUnit.MINUTE.value];
	}

	public double getHours() {
		return timeByUnit[TimeUnit.HOUR.value];
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the value.
	 * <p>
	 * @param value a {@code long} value
	 */
	public void setValue(final long value) {
		begin = 0;
		end = value;
		compute();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void start() {
		begin = System.currentTimeMillis();
	}

	public long stop() {
		end = System.currentTimeMillis();
		compute();
		return difference;
	}

	protected void compute() {
		difference = end - begin;
		timeByUnit[0] = difference;
		time[0] = Longs.convert(timeByUnit[0] % 1000);
		for (int i = 1; i < N_TIME_UNITS; ++i) {
			timeByUnit[i] = difference / (1000. * Math.pow(60., i - 1));
			time[i] = Longs.convert(timeByUnit[i] % 60);
		}
		representation = time[TimeUnit.HOUR.value] + ":" + time[TimeUnit.MINUTE.value] + ":" +
				time[TimeUnit.SECOND.value] + "." + time[TimeUnit.MILLISECOND.value];
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		return representation;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum TimeUnit {
		MILLISECOND(0),
		SECOND(1),
		MINUTE(2),
		HOUR(3);

		public final int value;

		private TimeUnit(final int index) {
			value = index;
		}
	}
}
