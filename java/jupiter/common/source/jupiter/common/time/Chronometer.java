/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.util.Characters.COLON;
import static jupiter.common.util.Characters.POINT;
import static jupiter.common.util.Strings.EMPTY;

import java.io.Serializable;

import jupiter.common.math.Maths;
import jupiter.common.util.Longs;
import jupiter.common.util.Strings;

public class Chronometer
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The number of time units.
	 */
	public static final int TIME_UNIT_COUNT = TimeUnit.values().length;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final long[] time = new long[TIME_UNIT_COUNT];
	protected final double[] timeByUnit = new double[TIME_UNIT_COUNT];
	protected long begin = 0L, end = 0L;
	protected long difference = 0L;
	protected String representation = EMPTY;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Chronometer}.
	 */
	public Chronometer() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public double getNanoseconds() {
		return timeByUnit[TimeUnit.NANOSECOND.value];
	}

	public double getMicroseconds() {
		return timeByUnit[TimeUnit.MICROSECOND.value];
	}

	public double getMilliseconds() {
		return timeByUnit[TimeUnit.MILLISECOND.value];
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
	// CONTROLLERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public synchronized void start() {
		begin = System.nanoTime();
	}

	public synchronized long stop() {
		end = System.nanoTime();
		compute();
		return difference;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void compute() {
		difference = end - begin;
		for (int ti = 0; ti < 3; ++ti) {
			timeByUnit[ti] = difference / Maths.pow(1E3, ti);
			time[ti] = Maths.round(timeByUnit[ti]) % 1000L;
		}
		for (int ti = 3; ti < TIME_UNIT_COUNT; ++ti) {
			timeByUnit[ti] = difference / (1E9 * Maths.pow(60., ti - 3));
			time[ti] = Longs.convert(timeByUnit[ti]) % 60L;
		}
		representation = Strings.join(time[TimeUnit.HOUR.value], COLON, time[TimeUnit.MINUTE.value],
				COLON, time[TimeUnit.SECOND.value], POINT, time[TimeUnit.MILLISECOND.value]);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return representation;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum TimeUnit {
		NANOSECOND(0),
		MICROSECOND(1),
		MILLISECOND(2),
		SECOND(3),
		MINUTE(4),
		HOUR(5);

		public final int value;

		private TimeUnit(final int value) {
			this.value = value;
		}
	}
}
