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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.util.Formats;

public class Dates {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(
			Formats.DEFAULT_DATE_FORMAT);
	protected static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat(
			Formats.DEFAULT_DATE_TIME_FORMAT);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Dates() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link Date} that represents the specified time value.
	 * <p>
	 * @param day   the day of the date to create
	 * @param month the month of the date to create
	 * @param year  the year of the date to create
	 * <p>
	 * @return a {@link Date} that represents the specified time value
	 */
	public static Date createDate(final int day, final int month, final int year) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(year, month - 1, day, 0, 0, 0);
		return cal.getTime();
	}

	/**
	 * Returns the current number of milliseconds since January 1, 1970, 00:00:00 GMT.
	 * <p>
	 * @return the current number of milliseconds since January 1, 1970, 00:00:00 GMT
	 */
	public static long getTime() {
		return new Date().getTime();
	}

	/**
	 * Returns the current date and time {@link String}.
	 * <p>
	 * @return the current date and time {@link String}
	 */
	public static String getDateTime() {
		return Dates.getDateTime(Formats.DEFAULT_DATE_TIME_FORMAT);
	}

	/**
	 * Returns the current date and time {@link String} formatted according to {@code format}.
	 * <p>
	 * @param format the format {@link String} of the date and time
	 * <p>
	 * @return the current date and time {@link String} formatted according to {@code format}
	 */
	public static String getDateTime(final String format) {
		final DateFormat dateFormat = new SimpleDateFormat(format);
		final Date date = new Date();
		return dateFormat.format(date);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified {@link Date}.
	 * <p>
	 * @param date the {@link Date} to format
	 * <p>
	 * @return a {@link String} representation of the specified {@link Date}
	 */
	public static String format(final Date date) {
		return DATE_FORMATTER.format(date);
	}

	/**
	 * Returns a {@link String} representation of the specified {@link Date} with time.
	 * <p>
	 * @param date the {@link Date} to format
	 * <p>
	 * @return a {@link String} representation of the specified {@link Date} with time
	 */
	public static String formatWithTime(final Date date) {
		return DATE_TIME_FORMATTER.format(date);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// BUSINESS DAYS
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
			// - Update the number of business days
			if (start.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
					start.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY &&
					!publicHolidays.contains(start.getTime())) {
				++workDays;
			}
			// - Increment the current day
			start.add(Calendar.DAY_OF_MONTH, 1);
			// - Update the public holidays if required
			if (year != start.get(Calendar.YEAR)) {
				year = start.get(Calendar.YEAR);
				publicHolidays = getSwissPublicHolidays(year);
			}
		} while (start.getTimeInMillis() <= end.getTimeInMillis());
		return workDays;
	}

	/**
	 * Tests whether {@code date} is a week day.
	 * <p>
	 * @param date the date to consider
	 * <p>
	 * @return {@code true} if {@code date} is a week day, {@code false} otherwise
	 */
	public static Boolean isWeekDay(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return !(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
				calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
	}

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

	/**
	 * Returns the date of Good Friday for the specified year.
	 * <p>
	 * @param year the year to consider
	 * <p>
	 * @return the date of Good Friday for the specified year
	 */
	public static Date getGoodFriday(final int year) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(getEasterDate(year));
		calendar.add(Calendar.DATE, -2);
		return calendar.getTime();
	}

	/**
	 * Returns the date of Easter in the specified year (using Butcher's algorithm).
	 * <p>
	 * @param year the year to consider
	 * <p>
	 * @return the date of Easter in the specified year
	 */
	public static Date getEasterDate(final int year) {
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
	 * Returns the date of Easter Monday in the specified year.
	 * <p>
	 * @param year the year to consider
	 * <p>
	 * @return the date of Easter Monday in the specified year
	 */
	public static Date getEasterMonday(final int year) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(getEasterDate(year));
		calendar.add(Calendar.DATE, 1);
		return calendar.getTime();
	}

	/**
	 * Returns the date of Ascension Day in the specified year.
	 * <p>
	 * @param year the year to consider
	 * <p>
	 * @return the date of Ascension Day in the specified year
	 */
	public static Date getAscensionDay(final int year) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(getEasterDate(year));
		calendar.add(Calendar.DATE, 39);
		return calendar.getTime();
	}

	/**
	 * Returns the date of Pentecost Day in the specified year.
	 * <p>
	 * @param year the year to consider
	 * <p>
	 * @return the date of Pentecost Day in the specified year
	 */
	public static Date getPentecostDay(final int year) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(getEasterDate(year));
		calendar.add(Calendar.DATE, 50);
		return calendar.getTime();
	}
}
