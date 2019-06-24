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

import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SafeDateFormat
		extends SimpleDateFormat {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -5020293094126916960L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SafeDateFormat} using the default pattern and date format symbols for the
	 * default {@link java.util.Locale.Category#FORMAT FORMAT} locale.
	 * <b>Note:</b> This constructor may not support all locales. For full coverage, use the factory
	 * methods in the {@link DateFormat} class.
	 */
	public SafeDateFormat() {
		super();
	}

	/**
	 * Constructs a {@link SafeDateFormat} using the specified pattern and the default date format
	 * symbols for the default {@link java.util.Locale.Category#FORMAT FORMAT} locale.
	 * <b>Note:</b> This constructor may not support all locales. For full coverage, use the factory
	 * methods in the {@link DateFormat} class.
	 * <p>
	 * This is equivalent to calling {@link #SafeDateFormat(String, Locale)
	 * SafeDateFormat(pattern, Locale.getDefault(java.util.Locale.Category.FORMAT))}.
	 *
	 * @see java.util.Locale#getDefault
	 * @see java.util.Locale.Category#FORMAT
	 * <p>
	 * @param pattern the pattern describing the date-time format
	 * <p>
	 * @exception NullPointerException     if the specified pattern is {@code null}
	 * @exception IllegalArgumentException if the specified pattern is invalid
	 */
	public SafeDateFormat(final String pattern) {
		super(pattern);
	}

	/**
	 * Constructs a {@link SafeDateFormat} using the specified pattern and the default date format
	 * symbols for the specified locale.
	 * <b>Note:</b> This constructor may not support all locales. For full coverage, use the factory
	 * methods in the {@link DateFormat} class.
	 * <p>
	 * @param pattern the pattern describing the date-time format
	 * @param locale  the locale whose date format symbols should be used
	 * <p>
	 * @exception NullPointerException     if the specified pattern or locale is {@code null}
	 * @exception IllegalArgumentException if the specified pattern is invalid
	 */
	public SafeDateFormat(final String pattern, final Locale locale) {
		super(pattern, locale);
	}

	/**
	 * Constructs a {@link SafeDateFormat} using the specified pattern and date format symbols.
	 * <p>
	 * @param pattern       the pattern describing the date-time format
	 * @param formatSymbols the date format symbols to be used for formatting
	 * <p>
	 * @exception NullPointerException     if the specified pattern or formatSymbols is {@code null}
	 * @exception IllegalArgumentException if the specified pattern is invalid
	 */
	public SafeDateFormat(final String pattern, final DateFormatSymbols formatSymbols) {
		super(pattern, formatSymbols);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the 100-year period 2-digit years will be interpreted as being in to begin on the date
	 * the user specifies.
	 * <p>
	 * @param startDate during parsing, two digit years will be placed in the range
	 *                  {@code startDate} to {@code startDate + 100 years}
	 * <p>
	 * @since 1.2
	 *
	 * @see #get2DigitYearStart
	 */
	@Override
	public synchronized void set2DigitYearStart(final Date startDate) {
		super.set2DigitYearStart(startDate);
	}

	/**
	 * Returns the beginning date of the 100-year period 2-digit years are interpreted as being
	 * within.
	 * <p>
	 * @return the start of the 100-year period into which two digit years are parsed
	 * <p>
	 * @since 1.2
	 *
	 * @see #set2DigitYearStart
	 */
	@Override
	public synchronized Date get2DigitYearStart() {
		return super.get2DigitYearStart();
	}

	/**
	 * Formats the specified {@link Date} into a date-time {@link String} and appends the result to
	 * the specified {@link StringBuffer}.
	 * <p>
	 * @param date       the date-time value to format into a date-time {@link String}
	 * @param toAppendTo where the new date-time text is to be appended
	 * @param pos        the formatting position. On input: an alignment field, if desired; on
	 *                   output: the offsets of the alignment field
	 * <p>
	 * @return the formatted date-time string
	 * <p>
	 * @exception NullPointerException if the specified {@code date} is {@code null}
	 */
	@Override
	public synchronized StringBuffer format(final Date date, final StringBuffer toAppendTo,
			final FieldPosition pos) {
		return super.format(date, toAppendTo, pos);
	}

	/**
	 * Formats an {@link Object} producing an {@link AttributedCharacterIterator}. You can use the
	 * returned {@link AttributedCharacterIterator} to build the resulting {@link String}, as well
	 * as to determine information about the resulting {@link String}.
	 * <p>
	 * Each attribute key of the {@link AttributedCharacterIterator} will be of type
	 * {@link java.text.DateFormat.Field}, with the corresponding attribute value being the same as
	 * the attribute key.
	 * <p>
	 * @param object the {@link Object} to format
	 * <p>
	 * @return an {@link AttributedCharacterIterator} describing the formatted value
	 * <p>
	 * @exception NullPointerException     if {@code object} is {@code null}
	 * @exception IllegalArgumentException if {@code this} cannot format {@code object}, or if the
	 *                                     format pattern {@link String} is invalid
	 * <p>
	 * @since 1.4
	 */
	@Override
	public synchronized AttributedCharacterIterator formatToCharacterIterator(final Object object) {
		return super.formatToCharacterIterator(object);
	}

	/**
	 * Parses the specified {@link String} to produce a {@link Date}.
	 * <p>
	 * The method attempts to parse text starting at the index specified by {@code pos}. If parsing
	 * succeeds, then the index of {@code pos} is updated to the index after the last character used
	 * (parsing does not necessarily use all characters up to the end of the {@link String}), and
	 * the parsed date is returned. The updated {@code pos} can be used to indicate the starting
	 * point for the next call to this method. If an error occurs, then the index of {@code pos} is
	 * not changed, the error index of {@code pos} is set to the index of the character where the
	 * error occurred, and {@code null} is returned.
	 * <p>
	 * This parsing operation uses the {@link DateFormat#calendar calendar} to produce a
	 * {@link Date}. All of {@code calendar}'s date-time fields are
	 * {@linkplain Calendar#clear() cleared} before parsing, and the {@code calendar}'s default
	 * values of the date-time fields are used for any missing date-time information. For example,
	 * the year value of the parsed {@link Date} is 1970 with {@link GregorianCalendar} if no year
	 * value is specified from the parsing operation. The {@link java.util.TimeZone} value may be
	 * overwritten, depending on the specified pattern and the time zone value in {@code text}. Any
	 * {@link java.util.TimeZone} value that has previously been set by a call to
	 * {@link #setTimeZone(java.util.TimeZone) setTimeZone} may need to be restored for further
	 * operations.
	 * <p>
	 * @param text a {@link String}, part of which should be parsed
	 * @param pos  a {@link ParsePosition} object with index and error index information as
	 *             described above
	 * <p>
	 * @return a {@link Date} parsed from the {@link String}, or {@code null} in case of error
	 * <p>
	 * @exception NullPointerException if {@code text} or {@code pos} is {@code null}
	 */
	@Override
	public synchronized Date parse(final String text, final ParsePosition pos) {
		return super.parse(text, pos);
	}

	/**
	 * Returns a pattern {@link String} describing {@code this}.
	 * <p>
	 * @return a pattern {@link String} describing {@code this}
	 */
	@Override
	public synchronized String toPattern() {
		return super.toPattern();
	}

	/**
	 * Returns a localized pattern {@link String} describing {@code this}.
	 * <p>
	 * @return a localized pattern {@link String} describing {@code this}
	 */
	@Override
	public synchronized String toLocalizedPattern() {
		return super.toLocalizedPattern();
	}

	/**
	 * Applies the specified pattern {@link String} to {@code this}.
	 * <p>
	 * @param pattern the new date-time pattern for {@code this}
	 * <p>
	 * @exception NullPointerException     if the specified pattern is {@code null}
	 * @exception IllegalArgumentException if the specified pattern is invalid
	 */
	@Override
	public synchronized void applyPattern(final String pattern) {
		super.applyPattern(pattern);
	}

	/**
	 * Applies the specified localized pattern {@link String} to {@code this}.
	 * <p>
	 * @param pattern a {@link String} to map to the new date-time format pattern for this format
	 * <p>
	 * @exception NullPointerException     if the specified pattern is {@code null}
	 * @exception IllegalArgumentException if the specified pattern is invalid
	 */
	@Override
	public synchronized void applyLocalizedPattern(final String pattern) {
		super.applyLocalizedPattern(pattern);
	}

	/**
	 * Gets a copy of the date-time format symbols of {@code this}.
	 * <p>
	 * @return the date-time format symbols of {@code this}
	 *
	 * @see #setDateFormatSymbols
	 */
	@Override
	public synchronized DateFormatSymbols getDateFormatSymbols() {
		return super.getDateFormatSymbols();
	}

	/**
	 * Sets the date-time format symbols of {@code this}.
	 * <p>
	 * @param newFormatSymbols the new date-time format symbols
	 * <p>
	 * @exception NullPointerException if the specified newFormatSymbols is {@code null}
	 *
	 * @see #getDateFormatSymbols
	 */
	@Override
	public synchronized void setDateFormatSymbols(final DateFormatSymbols newFormatSymbols) {
		super.setDateFormatSymbols(newFormatSymbols);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}. This also clones the date format symbols.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see Cloneable
	 */
	@Override
	public SafeDateFormat clone() {
		return (SafeDateFormat) super.clone();
	}

	/**
	 * Compares the specified {@link Object} with {@code this} for equality.
	 * <p>
	 * @return {@code true} if the specified {@link Object} is equal to {@code this}
	 */
	@Override
	public boolean equals(final Object obj) {
		return super.equals(obj);
	}

	/**
	 * Returns the hash code for {@code this}.
	 * <p>
	 * @return the hash code for {@code this}
	 *
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
