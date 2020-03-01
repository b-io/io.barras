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

import static jupiter.common.util.Formats.DEFAULT_DATE_PATTERN;
import static jupiter.common.util.Formats.DEFAULT_DATE_TIME_PATTERN;
import static jupiter.common.util.Strings.NULL;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class Dates {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link SafeDateFormat}.
	 */
	protected static volatile SafeDateFormat DATE_FORMATTER = new SafeDateFormat(
			DEFAULT_DATE_PATTERN);
	/**
	 * The {@link SafeDateFormat} with time.
	 */
	protected static volatile SafeDateFormat DATE_TIME_FORMATTER = new SafeDateFormat(
			DEFAULT_DATE_TIME_PATTERN);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Dates}.
	 */
	protected Dates() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the current date {@link String}.
	 * <p>
	 * @return the current date {@link String}
	 */
	public static String getDate() {
		return format(new Date());
	}

	/**
	 * Returns the current date-time {@link String}.
	 * <p>
	 * @return the current date-time {@link String}
	 */
	public static String getDateTime() {
		return formatWithTime(new Date());
	}

	/**
	 * Returns the current date-time {@link String} formatted according to {@code pattern}.
	 * <p>
	 * @param pattern the pattern {@link String} describing the date-time format
	 * <p>
	 * @return the current date-time {@link String} formatted according to {@code pattern}
	 */
	public static String getDateTime(final String pattern) {
		return new SafeDateFormat(pattern).format(new Date());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Date} of the last day of the current month.
	 * <p>
	 * @return the {@link Date} of the last day of the current month
	 */
	public static Date getMonthLastDay() {
		return getMonthLastDay(new Date());
	}

	/**
	 * Returns the {@link Date} of the last day of the specified month.
	 * <p>
	 * @param month the month to consider
	 * <p>
	 * @return the {@link Date} of the last day of the specified month
	 */
	public static Date getMonthLastDay(final Date month) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(month);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link Date} of the last day of the current year.
	 * <p>
	 * @return the {@link Date} of the last day of the current year
	 */
	public static Date getYearLastDay() {
		return getYearLastDay(new Date());
	}

	/**
	 * Returns the {@link Date} of the last day of the specified year.
	 * <p>
	 * @param year the year to consider
	 * <p>
	 * @return the {@link Date} of the last day of the specified year
	 */
	public static Date getYearLastDay(final Date year) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(year);
		calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
		return calendar.getTime();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Date} of Good Friday for the specified year.
	 * <p>
	 * @param year the year to consider
	 * <p>
	 * @return the {@link Date} of Good Friday for the specified year
	 */
	public static Date getGoodFriday(final int year) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(getEasterDay(year));
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
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(getEasterDay(year));
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
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(getEasterDay(year));
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
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(getEasterDay(year));
		calendar.add(Calendar.DATE, 50);
		return calendar.getTime();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

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
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link Date} that represents the specified date value.
	 * <p>
	 * @param year  the year of the {@link Date} to create
	 * @param month the month of the {@link Date} to create
	 * @param day   the day of the {@link Date} to create
	 * <p>
	 * @return a {@link Date} that represents the specified date value
	 */
	public static Date createDate(final int year, final int month, final int day) {
		return createDateWithTime(year, month, day, 0, 0, 0, 0L);
	}

	/**
	 * Creates a {@link Date} that represents the specified date with time value.
	 * <p>
	 * @param year      the year of the {@link Date} to create
	 * @param month     the month of the {@link Date} to create
	 * @param day       the day of the {@link Date} to create
	 * @param hourOfDay the hour of the day of the {@link Date} to create
	 * @param minute    the minute of the {@link Date} to create
	 * @param second    the second of the {@link Date} to create
	 * <p>
	 * @return a {@link Date} that represents the specified date with time value
	 */
	public static Date createDateWithTime(final int year, final int month, final int day,
			final int hourOfDay, final int minute, final int second) {
		return createDateWithTime(year, month, day, hourOfDay, minute, second, 0L);
	}

	/**
	 * Creates a {@link Date} that represents the specified date with time value.
	 * <p>
	 * @param year        the year of the {@link Date} to create
	 * @param month       the month of the {@link Date} to create
	 * @param day         the day of the {@link Date} to create
	 * @param hourOfDay   the hour of the day of the {@link Date} to create
	 * @param minute      the minute of the {@link Date} to create
	 * @param second      the second of the {@link Date} to create
	 * @param millisecond the millisecond of the {@link Date} to create
	 * <p>
	 * @return a {@link Date} that represents the specified date with time value
	 */
	public static Date createDateWithTime(final int year, final int month, final int day,
			final int hourOfDay, final int minute, final int second, final long millisecond) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millisecond);
		cal.set(year, month - 1, day, hourOfDay, minute, second);
		return cal.getTime();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the length of time (in milliseconds) since the epoch (January 1, 1970, 00:00:00 GMT).
	 * <p>
	 * @return the length of time (in milliseconds) since the epoch (January 1, 1970, 00:00:00 GMT)
	 */
	public static long createTimestamp() {
		return new Date().getTime();
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
		return DATE_FORMATTER.format(date);
	}

	/**
	 * Returns the formatted {@link String} of the specified {@link Date} with time.
	 * <p>
	 * @param date the {@link Date} to format
	 * <p>
	 * @return the formatted {@link String} of the specified {@link Date} with time
	 */
	public static String formatWithTime(final Date date) {
		return DATE_TIME_FORMATTER.format(date);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of business days for the specified year.
	 * <p>
	 * @param year           the year to consider
	 * @param publicHolidays the {@link List} of public holidays for the year
	 * <p>
	 * @return the number of business days for the specified year
	 */
	public static int countBusinessDays(final int year, final List<Date> publicHolidays) {
		return countBusinessDaysBetween(createDate(year, 1, 1), createDate(year, 12, 31),
				publicHolidays);
	}

	/**
	 * Returns the number of business days for the specified period of time.
	 * <p>
	 * @param startDate      the start of the period of time (inclusive)
	 * @param endDate        the end of the period of time (inclusive)
	 * @param publicHolidays the {@link List} of public holidays for the period of time
	 * <p>
	 * @return the number of business days for the specified period of time
	 */
	public static int countBusinessDaysBetween(final Date startDate, final Date endDate,
			List<Date> publicHolidays) {
		// Create the calendars for the start and end dates
		final Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		final Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		// Count the business days
		int workDays = 0, year = start.get(Calendar.YEAR);
		do {
			// • Update the number of business days
			if (start.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
					start.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY &&
					!publicHolidays.contains(start.getTime())) {
				++workDays;
			}
			// • Increment the current day
			start.add(Calendar.DAY_OF_MONTH, 1);
			// • Update the public holidays if required
			if (year != start.get(Calendar.YEAR)) {
				year = start.get(Calendar.YEAR);
				publicHolidays = getSwissPublicHolidays(year);
			}
		} while (start.getTimeInMillis() <= end.getTimeInMillis());
		return workDays;
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
		return DATE_FORMATTER.parse(text);
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
			if (Strings.isNotEmpty(text)) {
				return DATE_FORMATTER.parse(text);
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
		return DATE_TIME_FORMATTER.parse(text);
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
			if (Strings.isNotEmpty(text)) {
				return DATE_TIME_FORMATTER.parse(text);
			}
		} catch (final ParseException ignored) {
		}
		return defaultDateTime;
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
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return !(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
				calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
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
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) +
				calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) > 0;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code a} and {@code b} are equal to each other.
	 * <p>
	 * @param a the {@link Date} to compare for equality (may be {@code null})
	 * @param b the other {@link Date} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} and {@code b} are equal to each other, {@code false}
	 *         otherwise
	 */
	@SuppressWarnings("deprecation")
	public static boolean equals(final Date a, final Date b) {
		return a == b || a != null && b != null &&
				a.getDate() == b.getDate() &&
				a.getMonth() == b.getMonth() &&
				a.getYear() == b.getYear();
	}

	/**
	 * Tests whether {@code a} and {@code b} are equal to each other.
	 * <p>
	 * @param a the {@link Date} with time to compare for equality (may be {@code null})
	 * @param b the other {@link Date} with time to compare against for equality (may be
	 *          {@code null})
	 * <p>
	 * @return {@code true} if {@code a} and {@code b} are equal to each other, {@code false}
	 *         otherwise
	 */
	public static boolean equalsWithTime(final Date a, final Date b) {
		return Objects.equals(a, b);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Date} if it is not
	 * {@code null}, {@code "null"} otherwise.
	 * <p>
	 * @param date a {@link Date} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Date} if it is not
	 *         {@code null}, {@code "null"} otherwise
	 */
	public static String toString(final Date date) {
		if (date == null) {
			return NULL;
		}
		return hasTime(date) ? formatWithTime(date) : format(date);
	}
}
