/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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
import static jupiter.common.time.Dates.SWISS_PUBLIC_HOLIDAYS;
import static jupiter.common.util.Characters.BULLET;

import java.util.Date;

import jupiter.common.test.Test;

public class DatesTest
		extends Test {

	public DatesTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Dates#getDayStart}.
	 */
	public void testGetDayStart() {
		IO.test(BULLET, "getDayStart");

		assertEquals(Dates.createDateTime(2020, 2, 20, 0, 0, 0),
				Dates.getDayStart(Dates.createDateTime(2020, 2, 20, 1, 2, 3)));
	}

	/**
	 * Tests {@link Dates#getDayEnd}.
	 */
	public void testGetDayEnd() {
		IO.test(BULLET, "getDayEnd");

		assertEquals(Dates.createDateTime(2020, 2, 20, 23, 59, 59, 999),
				Dates.getDayEnd(Dates.createDateTime(2020, 2, 20, 1, 2, 3)));
	}

	//////////////////////////////////////////////

	/**
	 * Tests {@link Dates#getMonthStart}.
	 */
	public void testGetMonthStart() {
		IO.test(BULLET, "getMonthStart");

		assertEquals(Dates.createDateTime(2020, 2, 1, 1, 2, 3),
				Dates.getMonthStart(Dates.createDateTime(2020, 2, 20, 1, 2, 3)));
	}

	/**
	 * Tests {@link Dates#getMonthEnd}.
	 */
	public void testGetMonthEnd() {
		IO.test(BULLET, "getMonthEnd");

		assertEquals(Dates.createDateTime(2020, 2, 29, 1, 2, 3),
				Dates.getMonthEnd(Dates.createDateTime(2020, 2, 20, 1, 2, 3)));
	}

	//////////////////////////////////////////////

	/**
	 * Tests {@link Dates#getYearStart}.
	 */
	public void testGetYearStart() {
		IO.test(BULLET, "getYearStart");

		assertEquals(Dates.createDateTime(2020, 01, 01, 1, 2, 3),
				Dates.getYearStart(Dates.createDateTime(2020, 2, 20, 1, 2, 3)));
	}

	/**
	 * Tests {@link Dates#getYearEnd}.
	 */
	public void testGetYearEnd() {
		IO.test(BULLET, "getYearEnd");

		assertEquals(Dates.createDateTime(2020, 12, 31, 1, 2, 3),
				Dates.getYearEnd(Dates.createDateTime(2020, 2, 20, 1, 2, 3)));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Dates#getPreviousBusinessDay}.
	 */
	public void testGetPreviousBusinessDay() {
		IO.test(BULLET, "getPreviousBusinessDay");

		assertEquals(Dates.createDateTime(2020, 12, 24, 1, 2, 3),
				Dates.getPreviousBusinessDay(Dates.createDateTime(2020, 12, 27, 1, 2, 3),
						SWISS_PUBLIC_HOLIDAYS));
	}

	/**
	 * Tests {@link Dates#getNextBusinessDay}.
	 */
	public void testGetNextBusinessDay() {
		IO.test(BULLET, "getNextBusinessDay");

		assertEquals(Dates.createDateTime(2020, 12, 28, 1, 2, 3),
				Dates.getNextBusinessDay(Dates.createDateTime(2020, 12, 24, 1, 2, 3),
						SWISS_PUBLIC_HOLIDAYS));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Dates#createBusinessDaySequence}.
	 */
	public void testCreateBusinessDaySequence() {
		IO.test(BULLET, "createBusinessDaySequence");

		final Date from = Dates.createDateTime(2010, 1, 1, 1, 2, 3);
		final Date to = Dates.createDateTime(2019, 12, 31, 1, 2, 3);
		assertEquals(2608, Dates.createBusinessDaySequence(from, to).length);
		assertEquals(2531, Dates.createBusinessDaySequence(from, to, SWISS_PUBLIC_HOLIDAYS).length);
	}

	/**
	 * Tests {@link Dates#createBusinessMonthEndSequence}.
	 */
	public void testCreateBusinessMonthEndSequence() {
		IO.test(BULLET, "createBusinessMonthEndSequence");

		final Date from = Dates.createDateTime(2010, 1, 1, 1, 2, 3);
		final Date to = Dates.createDateTime(2019, 12, 31, 1, 2, 3);
		final Date[] dates = Dates.createBusinessMonthEndSequence(from, to);
		assertEquals(120, dates.length);
		assertEquals(Dates.createDateTime(2015, 5, 29, 1, 2, 3), dates[64]);
		assertEquals(120,
				Dates.createBusinessMonthEndSequence(from, to, SWISS_PUBLIC_HOLIDAYS).length);
	}

	/**
	 * Tests {@link Dates#createBusinessYearEndSequence}.
	 */
	public void testCreateBusinessYearEndSequence() {
		IO.test(BULLET, "createBusinessYearEndSequence");

		final Date from = Dates.createDateTime(2010, 1, 1, 1, 2, 3);
		final Date to = Dates.createDateTime(2019, 12, 31, 1, 2, 3);
		final Date[] dates = Dates.createBusinessYearEndSequence(from, to);
		assertEquals(10, dates.length);
		assertEquals(Dates.createDateTime(2015, 12, 31, 1, 2, 3), dates[5]);
		assertEquals(10,
				Dates.createBusinessYearEndSequence(from, to, SWISS_PUBLIC_HOLIDAYS).length);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Dates#toString}.
	 */
	public void testToString() {
		IO.test(BULLET, "toString");

		assertEquals("1234-05-06", Dates.toString(Dates.createDate(1234, 5, 6)));
		assertEquals("1234-05-06 01:02:03",
				Dates.toString(Dates.createDateTime(1234, 5, 6, 1, 2, 3)));
	}
}
