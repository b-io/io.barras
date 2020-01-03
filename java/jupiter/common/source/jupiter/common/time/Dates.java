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
package jupiter.common.time;

import static jupiter.common.util.Formats.DEFAULT_DATE_PATTERN;
import static jupiter.common.util.Formats.DEFAULT_DATE_TIME_PATTERN;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jupiter.common.struct.list.ExtendedList;

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
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link Date} that represents the specified time value.
	 * <p>
	 * @param day   the day of the {@link Date} to create
	 * @param month the month of the {@link Date} to create
	 * @param year  the year of the {@link Date} to create
	 * <p>
	 * @return a {@link Date} that represents the specified time value
	 */
	public static Date createDate(final int day, final int month, final int year) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0L);
		cal.set(year, month - 1, day, 0, 0, 0);
		return cal.getTime();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the current number of milliseconds since January 1, 1970, 00:00:00 GMT.
	 * <p>
	 * @return the current number of milliseconds since January 1, 1970, 00:00:00 GMT
	 */
	public static long getTime() {
		return new Date().getTime();
	}

	//////////////////////////////////////////////

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
		return createDate(j + 1, m, year);
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
	// FUNCTIONS
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

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Date}.
	 * <p>
	 * @param date the {@link Date} to format
	 * <p>
	 * @return a representative {@link String} of the specified {@link Date}
	 */
	public static String format(final Date date) {
		return DATE_FORMATTER.format(date);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Date} with time.
	 * <p>
	 * @param date the {@link Date} to format
	 * <p>
	 * @return a representative {@link String} of the specified {@link Date} with time
	 */
	public static String formatWithTime(final Date date) {
		return DATE_TIME_FORMATTER.format(date);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// BUSINESS DAYS
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
		publicHolidays.add(createDate(1, 1, year));
		// Add Good Friday
		publicHolidays.add(getGoodFriday(year));
		// Add Easter Monday
		publicHolidays.add(getEasterMonday(year));
		// Add Labor Day
		publicHolidays.add(createDate(1, 5, year));
		// Add Ascension Day
		publicHolidays.add(getAscensionDay(year));
		// Add Whit Monday (Pentecost)
		publicHolidays.add(getPentecostDay(year));
		// Add Swiss National Day
		publicHolidays.add(createDate(1, 8, year));
		// Add Christmas Day
		publicHolidays.add(createDate(25, 12, year));
		// Add St. Stephen's Day
		publicHolidays.add(createDate(26, 12, year));
		return publicHolidays;
	}

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
		return countBusinessDaysBetween(createDate(1, 1, year), createDate(31, 12, year),
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
		// Create calendars
		final Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		final Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		// Count the business days
		int workDays = 0;
		int year = start.get(Calendar.YEAR);
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
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link Date}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link Date},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return Date.class.isAssignableFrom(c);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Date} is a week day.
	 * <p>
	 * @param day the {@link Date} to consider
	 * <p>
	 * @return {@code true} if the specified {@link Date} is a week day, {@code false} otherwise
	 */
	public static Boolean isWeekDay(final Date day) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(day);
		return !(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
				calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
	}
}
