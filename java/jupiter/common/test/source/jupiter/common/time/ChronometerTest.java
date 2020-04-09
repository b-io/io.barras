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
package jupiter.common.time;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.math.Maths;
import jupiter.common.test.Test;
import jupiter.common.thread.Threads;
import jupiter.common.util.Longs;

public class ChronometerTest
		extends Test {

	public ChronometerTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests getMilliseconds method, of class Chronometer.
	 */
	public void testGetMilliseconds() {
		IO.test(BULLET, " getMilliseconds");

		// Initialize
		final long sleepingTime = 600L; // [ms]
		final long unit = 1L;
		final long tolerance = 100L * unit;
		final Chronometer chrono = new Chronometer();

		// Measure the sleeping time
		chrono.start();
		Threads.sleep(sleepingTime);
		chrono.stop();

		// Verify the measured time
		final long time = Longs.convert(chrono.getMilliseconds());
		IO.test(time, " [ms]");
		assertEquals(sleepingTime * unit, Longs.convert(chrono.getMilliseconds()), tolerance);
	}

	/**
	 * Tests getMicroseconds method, of class Chronometer.
	 */
	public void testGetMicroseconds() {
		IO.test(BULLET, " getMicroseconds");

		// Initialize
		final long sleepingTime = 600L; // [ms]
		final long unit = Longs.convert(1E3);
		final long tolerance = 100L * unit;
		final Chronometer chrono = new Chronometer();

		// Measure the sleeping time
		chrono.start();
		Threads.sleep(sleepingTime);
		chrono.stop();

		// Verify the measured time
		final long time = Longs.convert(chrono.getMicroseconds());
		IO.test(time, " [µs] = ", Maths.round((double) time / unit), " [ms]");
		assertEquals(sleepingTime * unit, Longs.convert(chrono.getMicroseconds()), tolerance);
	}

	/**
	 * Tests getNanoseconds method, of class Chronometer.
	 */
	public void testGetNanoseconds() {
		IO.test(BULLET, " getNanoseconds");

		// Initialize
		final long sleepingTime = 600L; // [ms]
		final long unit = Longs.convert(1E6);
		final long tolerance = 100L * unit;
		final Chronometer chrono = new Chronometer();

		// Measure the sleeping time
		chrono.start();
		Threads.sleep(sleepingTime);
		chrono.stop();

		// Verify the measured time
		final long time = Longs.convert(chrono.getNanoseconds());
		IO.test(time, " [ns] = ", Maths.round((double) time / unit), " [ms]");
		assertEquals(sleepingTime * unit, Longs.convert(chrono.getNanoseconds()), tolerance);
	}
}
