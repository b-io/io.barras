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

import static jupiter.common.Formats.DATE_FORMAT;
import static jupiter.common.Formats.DATE_TIME_FORMAT;
import static jupiter.common.util.Strings.NULL;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import jupiter.common.Formats;
import jupiter.common.map.Mapper;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class Dates {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final long HOURS_PER_DAY = 24L; // [h]
	public static final long MINUTES_PER_HOUR = 60L; // [min]
	public static final long SECONDS_PER_MINUTE = 60L; // [s]
	public static final long MILLISECONDS_PER_SECOND = 1000L; // [ms]

	public static final long MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY; // [min]
	public static final long SECONDS_PER_DAY = SECONDS_PER_MINUTE * MINUTES_PER_DAY; // [s]
	public static final long MILLISECONDS_PER_DAY = MILLISECONDS_PER_SECOND * SECONDS_PER_DAY; // [ms]

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Mapper<Integer, List<? extends Date>> NO_PUBLIC_HOLIDAYS = new Mapper<Integer, List<? extends Date>>() {
		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public List<? extends Date> call(final Integer year) {
			return new ExtendedList<Date>(0);
		}
	};

	public static final Mapper<Integer, List<? extends Date>> SWISS_PUBLIC_HOLIDAYS = new Mapper<Integer, List<? extends Date>>() {
		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public List<? extends Date> call(final Integer year) {
			return getSwissPublicHolidays(year);
		}
	};

	//////////////////////////////////////////////

	public static volatile Mapper<Integer, List<? extends Date>> PUBLIC_HOLIDAYS = NO_PUBLIC_HOLIDAYS;

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Comparator<Date> COMPARATOR = new Comparator<Date>() {
		@Override
		public int compare(final Date a, final Date b) {
			return Dates.compare(a, b);
		}
	};

	public static final Comparator<Date> COMPARATOR_WITH_TIME = new Comparator<Date>() {
		@Override
		public int compare(final Date a, final Date b) {
			return Dates.compareWithTime(a, b);
		}
	};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Dates}.
	 */
	protected Dates() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the current {@link Date}.
	 * <p>
	 * @return the current {@link Date}
	 */
	public static Date getDate() {
		return new Date() {
			/**
			 * The generated serial version ID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String toString() {
				return format(this);
			}
		};
	}

	/**
	 * Returns the current {@link Date} with time.
	 * <p>
	 * @return the current {@link Date} with time
	 */
	public static Date getDateTime() {
		return new Date() {
			/**
			 * The generated serial version ID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String toString() {
				return formatWithTime(this);
			}
		};
	}

	/**
	 * Returns the current length of time (in milliseconds) since the epoch (January 1, 1970,
	 * 00:00:00 GMT).
	 * <p>
	 * @return the current length of time (in milliseconds) since the epoch (January 1, 1970,
	 *         00:00:00 GMT)
	 */
	public static long getTimestamp() {
		return new Date().getTime();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int getDay() {
		return getDay(createCalendar());
	}

	public static int getDay(final Date date) {
		return getDay(createCalendar(date));
	}

	public static int getDay(final Calendar calendar) {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Returns the {@link Date} of the current day start.
	 * <p>
	 * @return the {@link Date} of the current day start
	 */
	public static Date getDayStart() {
		return getDayStart(createCalendar());
	}

	/**
	 * Returns the {@link Date} of the day start of the specified {@link Date}.
	 * <p>
	 * @param date the {@link Date} to consider
	 * <p>
	 * @return the {@link Date} of the day start of the specified {@link Date}
	 */
	public static Date getDayStart(final Date date) {
		return getDayStart(createCalendar(date));
	}

	/**
	 * Returns the {@link Date} of the day start of the specified {@link Calendar}.
	 * <p>
	 * @param calendar the {@link Calendar} to consider
	 * <p>
	 * @return the {@link Date} of the day start of the specified {@link Calendar}
	 */
	public static Date getDayStart(final Calendar calendar) {
		setTime(calendar, 0, 0, 0, 0);
		return calendar.getTime();
	}

	/**
	 * Returns the {@link Date} of the current day end.
	 * <p>
	 * @return the {@link Date} of the current day end
	 */
	public static Date getDayEnd() {
		return getDayEnd(createCalendar());
	}

	/**
	 * Returns the {@link Date} of the day end of the specified {@link Date}.
	 * <p>
	 * @param date the {@link Date} to consider
	 * <p>
	 * @return the {@link Date} of the day end of the specified {@link Date}
	 */
	public static Date getDayEnd(final Date date) {
		return getDayEnd(createCalendar(date));
	}

	/**
	 * Returns the {@link Date} of the day end of the specified {@link Calendar}.
	 * <p>
	 * @param calendar the {@link Calendar} to consider
	 * <p>
	 * @return the {@link Date} of the day end of the specified {@link Calendar}
	 */
	public static Date getDayEnd(final Calendar calendar) {
		setTime(calendar,
				calendar.getMaximum(Calendar.HOUR_OF_DAY),
				calendar.getMaximum(Calendar.MINUTE),
				calendar.getMaximum(Calendar.SECOND),
				calendar.getMaximum(Calendar.MILLISECOND));
		return calendar.getTime();
	}

	/**
	 * Returns the {@link Date} if it is a business day, the previous business day otherwise.
	 * <p>
	 * @return the {@link Date} if it is a business day, the previous business day otherwise
	 */
	public static Date getBusinessDay() {
		return getBusinessDay(createCalendar(), PUBLIC_HOLIDAYS, false);
	}

	/**
	 * Returns the {@link Date} if it is a business day, the previous (or next if {@code getNext})
	 * business day otherwise.
	 * <p>
	 * @param getNext the flag specifying whether to get the next or previous business day
	 * <p>
	 * @return the {@link Date} if it is a business day, the previous (or next if {@code getNext})
	 *         business day otherwise
	 */
	public static Date getBusinessDay(final boolean getNext) {
		return getBusinessDay(createCalendar(), PUBLIC_HOLIDAYS, getNext);
	}

	/**
	 * Returns the specified {@link Date} if it is a business day, the previous business day
	 * otherwise.
	 * <p>
	 * @param date the {@link Date} to consider
	 * <p>
	 * @return the specified {@link Date} if it is a business day, the previous business day
	 *         otherwise
	 */
	public static Date getBusinessDay(final Date date) {
		return getBusinessDay(createCalendar(date), PUBLIC_HOLIDAYS, false);
	}

	/**
	 * Returns the specified {@link Date} if it is a business day, the previous (or next if
	 * {@code getNext}) business day otherwise.
	 * <p>
	 * @param date    the {@link Date} to consider
	 * @param getNext the flag specifying whether to get the next or previous business day
	 * <p>
	 * @return the specified {@link Date} if it is a business day, the previous (or next if
	 *         {@code getNext}) business day otherwise
	 */
	public static Date getBusinessDay(final Date date, final boolean getNext) {
		return getBusinessDay(createCalendar(date), PUBLIC_HOLIDAYS, getNext);
	}

	/**
	 * Returns the {@link Date} of the specified {@link Calendar} if it is a business day, the
	 * previous business day otherwise.
	 * <p>
	 * @param calendar the {@link Calendar} to consider
	 * <p>
	 * @return the {@link Date} of the specified {@link Calendar} if it is a business day, the
	 *         previous business day otherwise
	 */
	public static Date getBusinessDay(final Calendar calendar) {
		return getBusinessDay(calendar, PUBLIC_HOLIDAYS, false);
	}

	/**
	 * Returns the {@link Date} of the specified {@link Calendar} if it is a business day, the
	 * previous (or next if {@code getNext}) business day otherwise.
	 * <p>
	 * @param calendar the {@link Calendar} to consider
	 * @param getNext  the flag specifying whether to get the next or previous business day
	 * <p>
	 * @return the {@link Date} of the specified {@link Calendar} if it is a business day, the
	 *         previous (or next if {@code getNext}) business day otherwise
	 */
	public static Date getBusinessDay(final Calendar calendar, final boolean getNext) {
		return getBusinessDay(calendar, PUBLIC_HOLIDAYS, getNext);
	}

	/**
	 * Returns the specified {@link Date} if it is a business day with the specified public holidays
	 * {@link Mapper}, the previous business day otherwise.
	 * <p>
	 * @param date           the {@link Date} to consider
	 * @param publicHolidays the {@link Mapper} mapping a year to a {@link List} of public holidays
	 * <p>
	 * @return the specified {@link Date} if it is a business day with the specified public holidays
	 *         {@link Mapper}, the previous business day otherwise
	 */
	public static Date getBusinessDay(final Date date,
			final Mapper<Integer, List<? extends Date>> publicHolidays) {
		return getBusinessDay(createCalendar(date), publicHolidays, false);
	}

	/**
	 * Returns the specified {@link Date} if it is a business day with the specified public holidays
	 * {@link Mapper}, the previous (or next if {@code getNext}) business day otherwise.
	 * <p>
	 * @param date           the {@link Date} to consider
	 * @param publicHolidays the {@link Mapper} mapping a year to a {@link List} of public holidays
	 * @param getNext        the flag specifying whether to get the next or previous business day
	 * <p>
	 * @return the specified {@link Date} if it is a business day with the specified public holidays
	 *         {@link Mapper}, the previous (or next if {@code getNext}) business day otherwise
	 */
	public static Date getBusinessDay(final Date date,
			final Mapper<Integer, List<? extends Date>> publicHolidays, final boolean getNext) {
		return getBusinessDay(createCalendar(date), publicHolidays, getNext);
	}

	/**
	 * Returns the {@link Date} of the specified {@link Calendar} if it is a business day with the
	 * specified public holidays {@link Mapper}, the previous business day otherwise.
	 * <p>
	 * @param calendar       the {@link Calendar} to consider
	 * @param publicHolidays the {@link Mapper} mapping a year to a {@link List} of public holidays
	 * <p>
	 * @return the {@link Date} of the specified {@link Calendar} if it is a business day with the
	 *         specified public holidays {@link Mapper}, the previous business day otherwise
	 */
	public static Date getBusinessDay(final Calendar calendar,
			final Mapper<Integer, List<? extends Date>> publicHolidays) {
		return getBusinessDay(calendar, publicHolidays, false);
	}

	/**
	 * Returns the {@link Date} of the specified {@link Calendar} if it is a business day with the
	 * specified public holidays {@link Mapper}, the previous (or next if {@code getNext}) business
	 * day otherwise.
	 * <p>
	 * @param calendar       the {@link Calendar} to consider
	 * @param publicHolidays the {@link Mapper} mapping a year to a {@link List} of public holidays
	 * @param getNext        the flag specifying whether to get the next or previous business day
	 * <p>
	 * @return the {@link Date} of the specified {@link Calendar} if it is a business day with the
	 *         specified public holidays {@link Mapper}, the previous (or next if {@code getNext})
	 *         business day otherwise
	 */
	public static Date getBusinessDay(final Calendar calendar,
			final Mapper<Integer, List<? extends Date>> publicHolidays, final boolean getNext) {
		if (isBusinessDay(calendar, publicHolidays.call(getYear(calendar)))) {
			return calendar.getTime();
		}
		if (getNext) {
			return getNextBusinessDay(calendar, publicHolidays);
		}
		return getPreviousBusinessDay(calendar, publicHolidays);
	}

	/**
	 * Returns the {@link Date} of the previous business day.
	 * <p>
	 * @return the {@link Date} of the previous business day
	 */
	public static Date getPreviousBusinessDay() {
		return getPreviousBusinessDay(createCalendar(), PUBLIC_HOLIDAYS);
	}

	/**
	 * Returns the {@link Date} of the previous business day of the specified {@link Date}.
	 * <p>
	 * @param date the {@link Date} to consider
	 * <p>
	 * @return the {@link Date} of the previous business day of the specified {@link Date}
	 */
	public static Date getPreviousBusinessDay(final Date date) {
		return getPreviousBusinessDay(createCalendar(date), PUBLIC_HOLIDAYS);
	}

	/**
	 * Returns the {@link Date} of the previous business day of the specified {@link Calendar}.
	 * <p>
	 * @param calendar the {@link Calendar} to consider
	 * <p>
	 * @return the {@link Date} of the previous business day of the specified {@link Calendar}
	 */
	public static Date getPreviousBusinessDay(final Calendar calendar) {
		return getPreviousBusinessDay(calendar, PUBLIC_HOLIDAYS);
	}

	/**
	 * Returns the {@link Date} of the previous business day of the specified {@link Date} with the
	 * specified public holidays {@link Mapper}.
	 * <p>
	 * @param date           the {@link Date} to consider
	 * @param publicHolidays the {@link Mapper} mapping a year to a {@link List} of public holidays
	 * <p>
	 * @return the {@link Date} of the previous business day of the specified {@link Date} with the
	 *         specified public holidays {@link Mapper}
	 */
	public static Date getPreviousBusinessDay(final Date date,
			final Mapper<Integer, List<? extends Date>> publicHolidays) {
		return getPreviousBusinessDay(createCalendar(date), publicHolidays);
	}

	/**
	 * Returns the {@link Date} of the previous business day of the specified {@link Calendar} with
	 * the specified public holidays {@link Mapper}.
	 * <p>
	 * @param calendar       the {@link Calendar} to consider
	 * @param publicHolidays the {@link Mapper} mapping a year to a {@link List} of public holidays
	 * <p>
	 * @return the {@link Date} of the previous business day of the specified {@link Calendar} with
	 *         the specified public holidays {@link Mapper}
	 */
	public static Date getPreviousBusinessDay(final Calendar calendar,
			final Mapper<Integer, List<? extends Date>> publicHolidays) {
		int year = getYear(calendar);
		List<? extends Date> phs = publicHolidays.call(year);
		do {
			// Decrement the calendar
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			// Update the public holidays if required
			if (year != getYear(calendar)) {
				year = getYear(calendar);
				phs = publicHolidays.call(year);
			}
		} while (!isBusinessDay(calendar, phs));
		return calendar.getTime();
	}

	/**
	 * Returns the {@link Date} of the next business day.
	 * <p>
	 * @return the {@link Date} of the next business day
	 */
	public static Date getNextBusinessDay() {
		return getNextBusinessDay(createCalendar(), PUBLIC_HOLIDAYS);
	}

	/**
	 * Returns the {@link Date} of the next business day of the specified {@link Date}.
	 * <p>
	 * @param date the {@link Date} to consider
	 * <p>
	 * @return the {@link Date} of the next business day of the specified {@link Date}
	 */
	public static Date getNextBusinessDay(final Date date) {
		return getNextBusinessDay(createCalendar(date), PUBLIC_HOLIDAYS);
	}

	/**
	 * Returns the {@link Date} of the next business day of the specified {@link Calendar}.
	 * <p>
	 * @param calendar the {@link Calendar} to consider
	 * <p>
	 * @return the {@link Date} of the next business day of the specified {@link Calendar}
	 */
	public static Date getNextBusinessDay(final Calendar calendar) {
		return getNextBusinessDay(calendar, PUBLIC_HOLIDAYS);
	}

	/**
	 * Returns the {@link Date} of the next business day of the specified {@link Date} with the
	 * specified public holidays {@link Mapper}.
	 * <p>
	 * @param date           the {@link Date} to consider
	 * @param publicHolidays the {@link Mapper} mapping a year to a {@link List} of public holidays
	 * <p>
	 * @return the {@link Date} of the next business day of the specified {@link Date} with the
	 *         specified public holidays {@link Mapper}
	 */
	public static Date getNextBusinessDay(final Date date,
			final Mapper<Integer, List<? extends Date>> publicHolidays) {
		return getNextBusinessDay(createCalendar(date), publicHolidays);
	}

	/**
	 * Returns the {@link Date} of the next business day of the specified {@link Calendar} with the
	 * specified public holidays {@link Mapper}.
	 * <p>
	 * @param calendar       the {@link Calendar} to consider
	 * @param publicHolidays the {@link Mapper} mapping a year to a {@link List} of public holidays
	 * <p>
	 * @return the {@link Date} of the next business day of the specified {@link Calendar} with the
	 *         specified public holidays {@link Mapper}
	 */
	public static Date getNextBusinessDay(final Calendar calendar,
			final Mapper<Integer, List<? extends Date>> publicHolidays) {
		int year = getYear(calendar);
		List<? extends Date> phs = publicHolidays.call(year);
		do {
			// Increment the calendar
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			// Update the public holidays if required
			if (year != getYear(calendar)) {
				year = getYear(calendar);
				phs = publicHolidays.call(year);
			}
		} while (!isBusinessDay(calendar, phs));
		return calendar.getTime();
	}

	//////////////////////////////////////////////

	public static int getMonth() {
		return getMonth(createCalendar());
	}

	public static int getMonth(final Date date) {
		return getMonth(createCalendar(date));
	}

	public static int getMonth(final Calendar calendar) {
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * Returns the {@link Date} of the current month start.
	 * <p>
	 * @return the {@link Date} of the current month start
	 */
	public static Date getMonthStart() {
		return getMonthStart(createCalendar());
	}

	/**
	 * Returns the {@link Date} of the month start of the specified {@link Date}.
	 * <p>
	 * @param date the {@link Date} to consider
	 * <p>
	 * @return the {@link Date} of the month start of the specified {@link Date}
	 */
	public static Date getMonthStart(final Date date) {
		return getMonthStart(createCalendar(date));
	}

	/**
	 * Returns the {@link Date} of the month start of the specified {@link Calendar}.
	 * <p>
	 * @param calendar the {@link Calendar} to consider
	 * <p>
	 * @return the {@link Date} of the month start of the specified {@link Calendar}
	 */
	public static Date getMonthStart(final Calendar calendar) {
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	/**
	 * Returns the {@link Date} of the current month end.
	 * <p>
	 * @return the {@link Date} of the current month end
	 */
	public static Date getMonthEnd() {
		return getMonthEnd(createCalendar());
	}

	/**
	 * Returns the {@link Date} of the month end of the specified {@link Date}.
	 * <p>
	 * @param date the {@link Date} to consider
	 * <p>
	 * @return the {@link Date} of the month end of the specified {@link Date}
	 */
	public static Date getMonthEnd(final Date date) {
		return getMonthEnd(createCalendar(date));
	}

	/**
	 * Returns the {@link Date} of the month end of the specified {@link Calendar}.
	 * <p>
	 * @param calendar the {@link Calendar} to consider
	 * <p>
	 * @return the {@link Date} of the month end of the specified {@link Calendar}
	 */
	public static Date getMonthEnd(final Calendar calendar) {
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

	//////////////////////////////////////////////

	public static int getYear() {
		return getYear(createCalendar());
	}

	public static int getYear(final Date date) {
		return getYear(createCalendar(date));
	}

	public static int getYear(final Calendar calendar) {
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * Returns the {@link Date} of the current year start.
	 * <p>
	 * @return the {@link Date} of the current year start
	 */
	public static Date getYearStart() {
		return getYearStart(createCalendar());
	}

	/**
	 * Returns the {@link Date} of the year start of the specified {@link Date}.
	 * <p>
	 * @param date the {@link Date} to consider
	 * <p>
	 * @return the {@link Date} of the year start of the specified {@link Date}
	 */
	public static Date getYearStart(final Date date) {
		return getYearStart(createCalendar(date));
	}

	/**
	 * Returns the {@link Date} of the year start of the specified {@link Calendar}.
	 * <p>
	 * @param calendar the {@link Calendar} to consider
	 * <p>
	 * @return the {@link Date} of the year start of the specified {@link Calendar}
	 */
	public static Date getYearStart(final Calendar calendar) {
		calendar.set(Calendar.DAY_OF_YEAR, 1);
		return calendar.getTime();
	}

	/**
	 * Returns the {@link Date} of the current year end.
	 * <p>
	 * @return the {@link Date} of the current year end
	 */
	public static Date getYearEnd() {
		return getYearEnd(createCalendar());
	}

	/**
	 * Returns the {@link Date} of the year end of the specified {@link Date}.
	 * <p>
	 * @param date the {@link Date} to consider
	 * <p>
	 * @return the {@link Date} of the year end of the specified {@link Date}
	 */
	public static Date getYearEnd(final Date date) {
		return getYearEnd(createCalendar(date));
	}

	/**
	 * Returns the {@link Date} of the year end of the specified {@link Calendar}.
	 * <p>
	 * @param calendar the {@link Calendar} to consider
	 * <p>
	 * @return the {@link Date} of the year end of the specified {@link Calendar}
	 */
	public static Date getYearEnd(final Calendar calendar) {
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		return calendar.getTime();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link Date} of Good Friday for the specified year.
	 * <p>
	 * @param year the year to consider
	 * <p>
	 * @return the {@link Date} of Good Friday for the specified year
	 */
	public static Date getGoodFriday(final int year) {
		final Calendar calendar = createCalendar(getEasterDay(year));
		calendar.add(Calendar.DATE, -2);
		return calendar.getTime();
	}

	/**
	 * Returns the {@link Date} of Easter in the specified year (using Butcher's algorithm).
	 * <p>
	 * @param year the year to consider
	 * <p>
	 * @return the {@link Date} of Easter in the specified year
	 */
	public static Date getEasterDay(final int year) {
		// Cycle de Méton
		final int n = year % 19;
		// Centaine et rang de l'année
		final int c = year / 100, u = year % 100;
		// Siècle bissextil
		final int s = c / 4, t = c % 4;
		// Proemptose
		final int p = (c + 8) / 25;
		// Métemptose
		final int q = (c - p + 1) / 3;
		// Epacte
		final int e = (19 * n + c - s - q + 15) % 30;
		// Année bissextile
		final int b = u / 4, d = u % 4;
		// Lettre dominicale
		final int L = (32 + 2 * t + 2 * b - e - d) % 7;
		// Correction
		final int h = (n + 11 * e + 22 * L) / 451;
		// Mois et quantième du Samedi saint
		final int x = e + L - 7 * h + 114;
		final int m = x / 31, j = x % 31;
		return createDate(year, m, j + 1);
	}

	/**
	 * Returns the {@link Date} of Easter Monday in the specified year.
	 * <p>
	 * @param year the year to consider
	 * <p>
	 * @return the {@link Date} of Easter Monday in the specified year
	 */
	public static Date getEasterMonday(final int year) {
		final Calendar calendar = createCalendar(getEasterDay(year));
		calendar.add(Calendar.DATE, 1);
		return calendar.getTime();
	}

	/**
	 * Returns the {@link Date} of Ascension Day in the specified year.
	 * <p>
	 * @param year the year to consider
	 * <p>
	 * @return the {@link Date} of Ascension Day in the specified year
	 */
	public static Date getAscensionDay(final int year) {
		final Calendar calendar = createCalendar(getEasterDay(year));
		calendar.add(Calendar.DATE, 39);
		return calendar.getTime();
	}

	/**
	 * Returns the {@link Date} of Pentecost Day in the specified year.
	 * <p>
	 * @param year the year to consider
	 * <p>
	 * @return the {@link Date} of Pentecost Day in the specified year
	 */
	public static Date getPentecostDay(final int year) {
		final Calendar calendar = createCalendar(getEasterDay(year));
		calendar.add(Calendar.DATE, 50);
		return calendar.getTime();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the Swiss public holidays in an {@link ExtendedList}.
	 * <p>
	 * @param year the year to consider
	 * <p>
	 * @return the Swiss public holidays in an {@link ExtendedList}
	 */
	public static ExtendedList<Date> getSwissPublicHolidays(final int year) {
		final ExtendedList<Date> publicHolidays = new ExtendedList<Date>(9);
		// Add New Year's Day
		publicHolidays.add(createDate(year, 1, 1));
		// Add Good Friday
		publicHolidays.add(getGoodFriday(year));
		// Add Easter Monday
		publicHolidays.add(getEasterMonday(year));
		// Add Labor Day
		publicHolidays.add(createDate(year, 5, 1));
		// Add Ascension Day
		publicHolidays.add(getAscensionDay(year));
		// Add Whit Monday (Pentecost)
		publicHolidays.add(getPentecostDay(year));
		// Add Swiss National Day
		publicHolidays.add(createDate(year, 8, 1));
		// Add Christmas Day
		publicHolidays.add(createDate(year, 12, 25));
		// Add St. Stephen's Day
		publicHolidays.add(createDate(year, 12, 26));
		return publicHolidays;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the date of the specified {@link Date}.
	 * <p>
	 * @param date  the {@link Date} to set
	 * @param year  a number of years
	 * @param month a number of months
	 * @param day   a number of days
	 */
	public static void setDate(final Date date, final int year, final int month, final int day) {
		setDate(createCalendar(date), year, month, day);
	}

	/**
	 * Sets the date of the specified {@link Calendar}.
	 * <p>
	 * @param calendar the {@link Calendar} to set
	 * @param year     a number of years
	 * @param month    a number of months
	 * @param day      a number of days
	 */
	public static void setDate(final Calendar calendar, final int year, final int month,
			final int day) {
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
	}

	//////////////////////////////////////////////

	/**
	 * Sets the time of the specified {@link Date}.
	 * <p>
	 * @param date   the {@link Date} to set
	 * @param hour   a number of hours
	 * @param minute a number of minutes
	 * @param second a number of seconds
	 */
	public static void setTime(final Date date, final int hour, final int minute,
			final int second) {
		setTime(createCalendar(), hour, minute, second, 0);
	}

	/**
	 * Sets the time of the specified {@link Date}.
	 * <p>
	 * @param date        the {@link Date} to set
	 * @param hour        a number of hours
	 * @param minute      a number of minutes
	 * @param second      a number of seconds
	 * @param millisecond a number of milliseconds
	 */
	public static void setTime(final Date date, final int hour, final int minute, final int second,
			final int millisecond) {
		setTime(createCalendar(date), hour, minute, second, millisecond);
	}

	/**
	 * Sets the time of the specified {@link Calendar}.
	 * <p>
	 * @param calendar the {@link Calendar} to set
	 * @param hour     a number of hours
	 * @param minute   a number of minutes
	 * @param second   a number of seconds
	 */
	public static void setTime(final Calendar calendar, final int hour, final int minute,
			final int second) {
		setTime(calendar, hour, minute, second, 0);
	}

	/**
	 * Sets the time of the specified {@link Calendar}.
	 * <p>
	 * @param calendar    the {@link Calendar} to set
	 * @param hour        a number of hours
	 * @param minute      a number of minutes
	 * @param second      a number of seconds
	 * @param millisecond a number of milliseconds
	 */
	public static void setTime(final Calendar calendar, final int hour, final int minute,
			final int second, final int millisecond) {
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, millisecond);
	}

	//////////////////////////////////////////////

	/**
	 * Sets the date-time of the specified {@link Date}.
	 * <p>
	 * @param date   the {@link Date} to set
	 * @param year   a number of years
	 * @param month  a number of months
	 * @param day    a number of days
	 * @param hour   a number of hours
	 * @param minute a number of minutes
	 * @param second a number of seconds
	 */
	public static void setDateTime(final Date date, final int year, final int month, final int day,
			final int hour, final int minute, final int second) {
		setDateTime(createCalendar(), year, month, day, hour, minute, second, 0);
	}

	/**
	 * Sets the date-time of the specified {@link Date}.
	 * <p>
	 * @param date        the {@link Date} to set
	 * @param year        a number of years
	 * @param month       a number of months
	 * @param day         a number of days
	 * @param hour        a number of hours
	 * @param minute      a number of minutes
	 * @param second      a number of seconds
	 * @param millisecond a number of milliseconds
	 */
	public static void setDateTime(final Date date, final int year, final int month, final int day,
			final int hour, final int minute, final int second, final int millisecond) {
		setDateTime(createCalendar(date), year, month, day, hour, minute, second, millisecond);
	}

	/**
	 * Sets the date-time of the specified {@link Calendar}.
	 * <p>
	 * @param calendar the {@link Calendar} to set
	 * @param year     a number of years
	 * @param month    a number of months
	 * @param day      a number of days
	 * @param hour     a number of hours
	 * @param minute   a number of minutes
	 * @param second   a number of seconds
	 */
	public static void setDateTime(final Calendar calendar, final int year, final int month,
			final int day, final int hour, final int minute, final int second) {
		setDateTime(calendar, year, month, day, hour, minute, second, 0);
	}

	/**
	 * Sets the date-time of the specified {@link Calendar}.
	 * <p>
	 * @param calendar    the {@link Calendar} to set
	 * @param year        a number of years
	 * @param month       a number of months
	 * @param day         a number of days
	 * @param hour        a number of hours
	 * @param minute      a number of minutes
	 * @param second      a number of seconds
	 * @param millisecond a number of milliseconds
	 */
	public static void setDateTime(final Calendar calendar, final int year, final int month,
			final int day, final int hour, final int minute, final int second,
			final int millisecond) {
		setDate(calendar, year, month, day);
		setTime(calendar, hour, minute, second, millisecond);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified {@link Date} for order. Returns a negative integer, {@code 0} or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@link Date} to compare for order (may be {@code null})
	 * @param b the other {@link Date} to compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	@SuppressWarnings("deprecation")
	public static int compare(final Date a, final Date b) {
		// Check the arguments
		if (a == b) {
			return 0;
		}
		if (a == null) {
			return -1;
		}
		if (b == null) {
			return 1;
		}

		// Compare the dates for order
		int comparison = Integers.compare(a.getYear(), b.getYear());
		if (comparison != 0) {
			return comparison;
		}
		comparison = Integers.compare(a.getMonth(), b.getMonth());
		if (comparison != 0) {
			return comparison;
		}
		return Integers.compare(a.getDate(), b.getDate());
	}

	/**
	 * Compares the specified {@link Calendar} for order. Returns a negative integer, {@code 0} or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@link Calendar} to compare for order (may be {@code null})
	 * @param b the other {@link Calendar} to compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final Calendar a, final Calendar b) {
		// Check the arguments
		if (a == b) {
			return 0;
		}
		if (a == null) {
			return -1;
		}
		if (b == null) {
			return 1;
		}

		// Compare the calendars for order
		int comparison = Integers.compare(getYear(a), getYear(b));
		if (comparison != 0) {
			return comparison;
		}
		comparison = Integers.compare(getMonth(a), getMonth(b));
		if (comparison != 0) {
			return comparison;
		}
		return Integers.compare(getDay(a), getDay(b));
	}

	/**
	 * Compares the specified {@link Date} with time for order. Returns a negative integer,
	 * {@code 0} or a positive integer as {@code a} is less than, equal to or greater than {@code b}
	 * (with {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@link Date} with time to compare for order (may be {@code null})
	 * @param b the other {@link Date} with time to compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compareWithTime(final Date a, final Date b) {
		// Check the arguments
		if (a == b) {
			return 0;
		}
		if (a == null) {
			return -1;
		}
		if (b == null) {
			return 1;
		}

		// Compare the dates with time for order
		return Longs.compare(a.getTime(), b.getTime());
	}

	/**
	 * Compares the specified {@link Calendar} with time for order. Returns a negative integer,
	 * {@code 0} or a positive integer as {@code a} is less than, equal to or greater than {@code b}
	 * (with {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@link Calendar} with time to compare for order (may be {@code null})
	 * @param b the other {@link Calendar} with time to compare against for order (may be
	 *          {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compareWithTime(final Calendar a, final Calendar b) {
		// Check the arguments
		if (a == b) {
			return 0;
		}
		if (a == null) {
			return -1;
		}
		if (b == null) {
			return 1;
		}

		// Compare the calendars with time for order
		return Longs.compare(a.getTimeInMillis(), b.getTimeInMillis());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Date} converted from the specified {@link Date} with time.
	 * <p>
	 * @param date the {@link Date} with time to convert
	 * <p>
	 * @return a {@link Date} converted from the specified {@link Date} with time
	 */
	public static Date toDate(final Date date) {
		return getDayStart(date);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FORMATTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the formatted {@link String} of the specified {@link Date}.
	 * <p>
	 * @param date the {@link Date} to format
	 * <p>
	 * @return the formatted {@link String} of the specified {@link Date}
	 */
	public static String format(final Date date) {
		return DATE_FORMAT.format(date);
	}

	/**
	 * Returns the formatted {@link String} of the specified {@link Date} with the specified pattern
	 * {@link String}.
	 * <p>
	 * @param date    the {@link Date} to format
	 * @param pattern the pattern {@link String} describing the date-time format
	 * <p>
	 * @return the formatted {@link String} of the specified {@link Date} with the specified pattern
	 *         {@link String}
	 */
	public static String formatWith(final Date date, final String pattern) {
		return new SynchronizedDateFormat(pattern).format(date);
	}

	/**
	 * Returns the formatted {@link String} of the specified {@link Date} with time.
	 * <p>
	 * @param date the {@link Date} with time to format
	 * <p>
	 * @return the formatted {@link String} of the specified {@link Date} with time
	 */
	public static String formatWithTime(final Date date) {
		return DATE_TIME_FORMAT.format(date);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Calendar createCalendar() {
		return Calendar.getInstance(Formats.TIME_ZONE, Formats.LOCALE);
	}

	public static Calendar createCalendar(final TimeZone timeZone) {
		return Calendar.getInstance(timeZone, Formats.LOCALE);
	}

	public static Calendar createCalendar(final TimeZone timeZone, final Locale locale) {
		return Calendar.getInstance(timeZone, locale);
	}

	public static Calendar createCalendar(final Date date) {
		final Calendar calendar = createCalendar();
		calendar.setTime(date);
		return calendar;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link Date} that represents the specified date.
	 * <p>
	 * @param year  the year of the {@link Date} to create
	 * @param month the month of the {@link Date} to create
	 * @param day   the day of the {@link Date} to create
	 * <p>
	 * @return a {@link Date} that represents the specified date
	 */
	public static Date createDate(final int year, final int month, final int day) {
		return createDateTime(year, month, day, 0, 0, 0, 0);
	}

	/**
	 * Creates a {@link Date} with time that represents the specified date.
	 * <p>
	 * @param year   the year of the {@link Date} to create
	 * @param month  the month of the {@link Date} to create
	 * @param day    the day of the {@link Date} to create
	 * @param hour   the hour of the {@link Date} to create
	 * @param minute the minute of the {@link Date} to create
	 * @param second the second of the {@link Date} to create
	 * <p>
	 * @return a {@link Date} with time that represents the specified date
	 */
	public static Date createDateTime(final int year, final int month, final int day,
			final int hour, final int minute, final int second) {
		return createDateTime(year, month, day, hour, minute, second, 0);
	}

	/**
	 * Creates a {@link Date} with time that represents the specified date.
	 * <p>
	 * @param year        the year of the {@link Date} to create
	 * @param month       the month of the {@link Date} to create
	 * @param day         the day of the {@link Date} to create
	 * @param hour        the hour of the {@link Date} to create
	 * @param minute      the minute of the {@link Date} to create
	 * @param second      the second of the {@link Date} to create
	 * @param millisecond the millisecond of the {@link Date} to create
	 * <p>
	 * @return a {@link Date} with time that represents the specified date
	 */
	public static Date createDateTime(final int year, final int month, final int day,
			final int hour, final int minute, final int second, final int millisecond) {
		final Calendar calendar = createCalendar();
		setDateTime(calendar, year, month, day, hour, minute, second, millisecond);
		return calendar.getTime();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Date[] createDaySequence(final Date from, final Date to) {
		final Date[] array = new Date[countDays(from, to)];
		final Calendar start = createCalendar(from);
		for (int i = 0; i < array.length; ++i) {
			array[i] = start.getTime();
			start.add(Calendar.DATE, 1);
		}
		return array;
	}

	public static Date[] createBusinessDaySequence(final Date from, final Date to) {
		return createBusinessDaySequence(from, to, PUBLIC_HOLIDAYS);
	}

	public static Date[] createBusinessDaySequence(final Date from, final Date to,
			final Mapper<Integer, List<? extends Date>> publicHolidays) {
		final Date[] array = new Date[countBusinessDays(from, to, publicHolidays)];
		final Calendar start = createCalendar(from);
		int i = 0;
		if (isBusinessDay(start, publicHolidays.call(getYear(start)))) {
			array[i++] = start.getTime();
		}
		while (i < array.length) {
			array[i++] = getNextBusinessDay(start, publicHolidays);
		}
		return array;
	}

	//////////////////////////////////////////////

	public static Date[] createMonthSequence(final Date from, final Date to) {
		final Date[] array = new Date[countMonths(from, to)];
		final Calendar start = createCalendar(from);
		for (int i = 0; i < array.length; ++i) {
			array[i] = start.getTime();
			start.add(Calendar.MONTH, 1);
		}
		return array;
	}

	public static Date[] createMonthStartSequence(final Date from, final Date to) {
		final Date[] array = new Date[countMonths(from, to)];
		final Calendar start = createCalendar(from);
		for (int i = 0; i < array.length; ++i) {
			array[i] = getMonthStart(start);
			start.add(Calendar.MONTH, 1);
		}
		return array;
	}

	public static Date[] createBusinessMonthStartSequence(final Date from, final Date to) {
		return createBusinessMonthStartSequence(from, to, PUBLIC_HOLIDAYS);
	}

	public static Date[] createBusinessMonthStartSequence(final Date from, final Date to,
			final Mapper<Integer, List<? extends Date>> publicHolidays) {
		final Date[] array = new Date[countMonths(from, to)];
		final Calendar start = createCalendar(from);
		for (int i = 0; i < array.length; ++i) {
			array[i] = getBusinessDay(getMonthStart(start));
			start.add(Calendar.MONTH, 1);
		}
		return array;
	}

	public static Date[] createMonthEndSequence(final Date from, final Date to) {
		final Date[] array = new Date[countMonths(from, to)];
		final Calendar start = createCalendar(from);
		for (int i = 0; i < array.length; ++i) {
			array[i] = getMonthEnd(start);
			start.add(Calendar.MONTH, 1);
		}
		return array;
	}

	public static Date[] createBusinessMonthEndSequence(final Date from, final Date to) {
		return createBusinessMonthEndSequence(from, to, PUBLIC_HOLIDAYS);
	}

	public static Date[] createBusinessMonthEndSequence(final Date from, final Date to,
			final Mapper<Integer, List<? extends Date>> publicHolidays) {
		final Date[] array = new Date[countMonths(from, to)];
		final Calendar start = createCalendar(from);
		for (int i = 0; i < array.length; ++i) {
			array[i] = getBusinessDay(getMonthEnd(start));
			start.add(Calendar.MONTH, 1);
		}
		return array;
	}

	//////////////////////////////////////////////

	public static Date[] createYearSequence(final Date from, final Date to) {
		final Date[] array = new Date[countYears(from, to)];
		final Calendar start = createCalendar(from);
		for (int i = 0; i < array.length; ++i) {
			array[i] = start.getTime();
			start.add(Calendar.YEAR, 1);
		}
		return array;
	}

	public static Date[] createYearStartSequence(final Date from, final Date to) {
		final Date[] array = new Date[countYears(from, to)];
		final Calendar start = createCalendar(from);
		for (int i = 0; i < array.length; ++i) {
			array[i] = getYearStart(start);
			start.add(Calendar.YEAR, 1);
		}
		return array;
	}

	public static Date[] createBusinessYearStartSequence(final Date from, final Date to) {
		return createBusinessYearStartSequence(from, to, PUBLIC_HOLIDAYS);
	}

	public static Date[] createBusinessYearStartSequence(final Date from, final Date to,
			final Mapper<Integer, List<? extends Date>> publicHolidays) {
		final Date[] array = new Date[countYears(from, to)];
		final Calendar start = createCalendar(from);
		for (int i = 0; i < array.length; ++i) {
			array[i] = getBusinessDay(getYearStart(start));
			start.add(Calendar.YEAR, 1);
		}
		return array;
	}

	public static Date[] createYearEndSequence(final Date from, final Date to) {
		final Date[] array = new Date[countYears(from, to)];
		final Calendar start = createCalendar(from);
		for (int i = 0; i < array.length; ++i) {
			array[i] = getYearEnd(start);
			start.add(Calendar.YEAR, 1);
		}
		return array;
	}

	public static Date[] createBusinessYearEndSequence(final Date from, final Date to) {
		return createBusinessYearEndSequence(from, to, PUBLIC_HOLIDAYS);
	}

	public static Date[] createBusinessYearEndSequence(final Date from, final Date to,
			final Mapper<Integer, List<? extends Date>> publicHolidays) {
		final Date[] array = new Date[countYears(from, to)];
		final Calendar start = createCalendar(from);
		for (int i = 0; i < array.length; ++i) {
			array[i] = getBusinessDay(getYearEnd(start));
			start.add(Calendar.YEAR, 1);
		}
		return array;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PARSERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parses the {@link Date} encoded in the specified {@link String}.
	 * <p>
	 * @param text the {@link String} to parse
	 * <p>
	 * @return the {@link Date} encoded in the specified {@link String}
	 * <p>
	 * @throws ParseException if there is a problem with parsing
	 */
	public static Date parse(final String text)
			throws ParseException {
		return DATE_FORMAT.parse(text);
	}

	/**
	 * Parses the {@link Date} encoded in the specified {@link String}, or returns
	 * {@code defaultDate} if there is a problem with parsing.
	 * <p>
	 * @param text        the {@link String} to parse (may be {@code null})
	 * @param defaultDate the default {@link Date} (may be {@code null})
	 * <p>
	 * @return the {@link Date} encoded in the specified {@link String}, or returns
	 *         {@code defaultDate} if there is a problem with parsing
	 * <p>
	 * @throws ParseException if there is a problem with parsing
	 */
	public static Date parse(final String text, final Date defaultDate)
			throws ParseException {
		try {
			if (Strings.isNonEmpty(text)) {
				return DATE_FORMAT.parse(text);
			}
		} catch (final ParseException ignored) {
		}
		return defaultDate;
	}

	//////////////////////////////////////////////

	/**
	 * Parses the {@link Date} with time encoded in the specified {@link String}.
	 * <p>
	 * @param text the {@link String} to parse
	 * <p>
	 * @return the {@link Date} with time encoded in the specified {@link String}
	 * <p>
	 * @throws ParseException if there is a problem with parsing
	 */
	public static Date parseWithTime(final String text)
			throws ParseException {
		return DATE_TIME_FORMAT.parse(text);
	}

	/**
	 * Parses the {@link Date} with time encoded in the specified {@link String}, or returns
	 * {@code defaultDate} if there is a problem with parsing.
	 * <p>
	 * @param text            the {@link String} to parse (may be {@code null})
	 * @param defaultDateTime the default {@link Date} with time (may be {@code null})
	 * <p>
	 * @return the {@link Date} with time encoded in the specified {@link String}, or returns
	 *         {@code defaultDate} if there is a problem with parsing
	 * <p>
	 * @throws ParseException if there is a problem with parsing
	 */
	public static Date parseWithTime(final String text, final Date defaultDateTime)
			throws ParseException {
		try {
			if (Strings.isNonEmpty(text)) {
				return DATE_TIME_FORMAT.parse(text);
			}
		} catch (final ParseException ignored) {
		}
		return defaultDateTime;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of days for the specified period of time.
	 * <p>
	 * @param from the start of the period of time (inclusive)
	 * @param to   the end of the period of time (inclusive)
	 * <p>
	 * @return the number of days for the specified period of time
	 */
	public static int countDays(final Date from, final Date to) {
		return Integers.convert((to.getTime() - from.getTime()) / MILLISECONDS_PER_DAY);
	}

	/**
	 * Returns the number of business days for the specified period of time.
	 * <p>
	 * @param from the start of the period of time (inclusive)
	 * @param to   the end of the period of time (inclusive)
	 * <p>
	 * @return the number of business days for the specified period of time
	 */
	public static int countBusinessDays(final Date from, final Date to) {
		return countBusinessDays(from, to, PUBLIC_HOLIDAYS);
	}

	/**
	 * Returns the number of business days with the specified public holidays {@link Mapper} for the
	 * specified period of time.
	 * <p>
	 * @param from           the start of the period of time (inclusive)
	 * @param to             the end of the period of time (inclusive)
	 * @param publicHolidays the {@link Mapper} mapping a year to a {@link List} of public holidays
	 * <p>
	 * @return the number of business days with the specified public holidays {@link Mapper} for the
	 *         specified period of time
	 */
	public static int countBusinessDays(final Date from, final Date to,
			final Mapper<Integer, List<? extends Date>> publicHolidays) {
		// Create the calendars for the start and end dates
		final Calendar start = createCalendar(from);
		final Calendar end = createCalendar(to);
		// Count the business days
		int businessDayCount = 0, year = getYear(start);
		List<? extends Date> phs = publicHolidays.call(year);
		while (compare(start, end) <= 0) {
			// • Update the number of business days
			if (isBusinessDay(start, phs)) {
				++businessDayCount;
			}
			// • Increment the calendar
			start.add(Calendar.DAY_OF_MONTH, 1);
			// • Update the public holidays if required
			if (year != getYear(start)) {
				year = getYear(start);
				phs = publicHolidays.call(year);
			}
		}
		return businessDayCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of months for the specified period of time.
	 * <p>
	 * @param from the start of the period of time (inclusive)
	 * @param to   the end of the period of time (inclusive)
	 * <p>
	 * @return the number of months for the specified period of time
	 */
	public static int countMonths(final Date from, final Date to) {
		// Create the calendars for the start and end dates
		final Calendar start = createCalendar(from);
		final Calendar end = createCalendar(to);
		// Count the months
		int monthCount = 0;
		while (compare(start, end) <= 0) {
			++monthCount;
			start.add(Calendar.MONTH, 1);
		}
		return monthCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of years for the specified period of time.
	 * <p>
	 * @param from the start of the period of time (inclusive)
	 * @param to   the end of the period of time (inclusive)
	 * <p>
	 * @return the number of years for the specified period of time
	 */
	public static int countYears(final Date from, final Date to) {
		// Create the calendars for the start and end dates
		final Calendar start = createCalendar(from);
		final Calendar end = createCalendar(to);
		// Count the years
		int yearCount = 0;
		while (compare(start, end) <= 0) {
			++yearCount;
			start.add(Calendar.YEAR, 1);
		}
		return yearCount;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of {@link Date}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Date},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Date;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link Date}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link Date},
	 *         {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return Date.class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Date} is a week day.
	 * <p>
	 * @param date the {@link Date} to test
	 * <p>
	 * @return {@code true} if the specified {@link Date} is a week day, {@code false} otherwise
	 */
	public static boolean isWeekDay(final Date date) {
		return isWeekDay(createCalendar(date));
	}

	/**
	 * Tests whether the specified {@link Calendar} is a week day.
	 * <p>
	 * @param calendar the {@link Calendar} to test
	 * <p>
	 * @return {@code true} if the specified {@link Calendar} is a week day, {@code false} otherwise
	 */
	public static boolean isWeekDay(final Calendar calendar) {
		return !(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
				calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
	}

	/**
	 * Tests whether the specified {@link Date} is a public holiday.
	 * <p>
	 * @param date           the {@link Date} to test
	 * @param publicHolidays the {@link List} of public holidays
	 * <p>
	 * @return {@code true} if the specified {@link Date} is a public holiday, {@code false}
	 *         otherwise
	 */
	public static boolean isPublicHoliday(final Date date,
			final List<? extends Date> publicHolidays) {
		return publicHolidays.contains(toDate(date));
	}

	/**
	 * Tests whether the specified {@link Calendar} is a public holiday.
	 * <p>
	 * @param calendar       the {@link Calendar} to test
	 * @param publicHolidays the {@link List} of public holidays
	 * <p>
	 * @return {@code true} if the specified {@link Calendar} is a public holiday, {@code false}
	 *         otherwise
	 */
	public static boolean isPublicHoliday(final Calendar calendar,
			final List<? extends Date> publicHolidays) {
		return publicHolidays.contains(toDate(calendar.getTime()));
	}

	/**
	 * Tests whether the specified {@link Date} is a business day with the specified {@link List} of
	 * public holidays.
	 * <p>
	 * @param date           the {@link Date} to test
	 * @param publicHolidays the {@link List} of public holidays
	 * <p>
	 * @return {@code true} if the specified {@link Date} is a business day with the specified
	 *         {@link List} of public holidays, {@code false} otherwise
	 */
	public static boolean isBusinessDay(final Date date,
			final List<? extends Date> publicHolidays) {
		return isWeekDay(date) && !isPublicHoliday(date, publicHolidays);
	}

	/**
	 * Tests whether the specified {@link Calendar} is a business day with the specified
	 * {@link List} of public holidays.
	 * <p>
	 * @param calendar       the {@link Calendar} to test
	 * @param publicHolidays the {@link List} of public holidays
	 * <p>
	 * @return {@code true} if the specified {@link Calendar} is a business day with the specified
	 *         {@link List} of public holidays, {@code false} otherwise
	 */
	public static boolean isBusinessDay(final Calendar calendar,
			final List<? extends Date> publicHolidays) {
		return isWeekDay(calendar) && !isPublicHoliday(calendar, publicHolidays);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Date} has time.
	 * <p>
	 * @param date the {@link Date} to test
	 * <p>
	 * @return {@code true} if the specified {@link Date} has time, {@code false} otherwise
	 */
	public static boolean hasTime(final Date date) {
		return hasTime(createCalendar(date));
	}

	/**
	 * Tests whether the specified {@link Calendar} has time.
	 * <p>
	 * @param calendar the {@link Calendar} to test
	 * <p>
	 * @return {@code true} if the specified {@link Calendar} has time, {@code false} otherwise
	 */
	public static boolean hasTime(final Calendar calendar) {
		return calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) +
				calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) > 0;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the {@link Date} to compare for equality (may be {@code null})
	 * @param b the other {@link Date} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	@SuppressWarnings("deprecation")
	public static boolean equals(final Date a, final Date b) {
		return a == b || a != null && b != null &&
				a.getYear() == b.getYear() &&
				a.getMonth() == b.getMonth() &&
				a.getDate() == b.getDate();
	}

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the {@link Calendar} to compare for equality (may be {@code null})
	 * @param b the other {@link Calendar} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final Calendar a, final Calendar b) {
		return a == b || a != null && b != null &&
				getYear(a) == getYear(b) &&
				getMonth(a) == getMonth(b) &&
				getDay(a) == getDay(b);
	}

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the {@link Date} with time to compare for equality (may be {@code null})
	 * @param b the other {@link Date} with time to compare against for equality (may be
	 *          {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equalsWithTime(final Date a, final Date b) {
		return Objects.equals(a, b);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Date}, or {@code "null"} if
	 * it is {@code null}.
	 * <p>
	 * @param date a {@link Date} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Date}, or {@code "null"} if
	 *         it is {@code null}
	 */
	public static String toString(final Date date) {
		if (date == null) {
			return NULL;
		}
		return hasTime(date) ? formatWithTime(date) : format(date);
	}
}
