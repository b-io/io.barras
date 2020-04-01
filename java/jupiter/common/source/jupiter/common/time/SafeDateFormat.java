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

import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.DateFormat.Field;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import jupiter.common.model.ICloneable;

public class SafeDateFormat
		extends SimpleDateFormat {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SafeDateFormat} by default.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>This constructor may not support all {@link Locale}. For full coverage, use the factory
	 * methods of {@link DateFormat}.</dd>
	 * </dl>
	 */
	public SafeDateFormat() {
		super();
	}

	/**
	 * Constructs a {@link SafeDateFormat} with the specified pattern {@link String} and the default
	 * {@link DateFormatSymbols} for the default {@link Locale}.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>This constructor may not support all {@link Locale}. For full coverage, use the factory
	 * methods of {@link DateFormat}.</dd>
	 * </dl>
	 * <p>
	 * This is equivalent to calling the constructor {@link #SafeDateFormat(String, Locale)
	 * SafeDateFormat(pattern, Locale#getDefault())}.
	 *
	 * @see Locale#getDefault()
	 * <p>
	 * @param pattern the pattern {@link String} describing the date-time format
	 * <p>
	 * @throws IllegalArgumentException if {@code pattern} is invalid
	 */
	public SafeDateFormat(final String pattern) {
		super(pattern);
	}

	/**
	 * Constructs a {@link SafeDateFormat} with the specified pattern {@link String} and the default
	 * {@link DateFormatSymbols} for the specified {@link Locale}.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>This constructor may not support all {@link Locale}. For full coverage, use the factory
	 * methods of {@link DateFormat}.</dd>
	 * </dl>
	 * <p>
	 * @param pattern the pattern {@link String} describing the date-time format
	 * @param locale  the {@link Locale} whose {@link DateFormatSymbols} should be used
	 * <p>
	 * @throws IllegalArgumentException if {@code pattern} is invalid
	 */
	public SafeDateFormat(final String pattern, final Locale locale) {
		super(pattern, locale);
	}

	/**
	 * Constructs a {@link SafeDateFormat} with the specified pattern {@link String} and
	 * {@link DateFormatSymbols}.
	 * <p>
	 * @param pattern       the pattern {@link String} describing the date-time format
	 * @param formatSymbols the {@link DateFormatSymbols} used for formatting
	 * <p>
	 * @throws IllegalArgumentException if {@code pattern} is invalid
	 */
	public SafeDateFormat(final String pattern, final DateFormatSymbols formatSymbols) {
		super(pattern, formatSymbols);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the beginning date of the 100-year period 2-digit years are interpreted as being
	 * within.
	 * <p>
	 * @return the start of the 100-year period into which two digit years are parsed
	 * <p>
	 * @since 1.2
	 *
	 * @see #set2DigitYearStart(Date)
	 */
	@Override
	public synchronized Date get2DigitYearStart() {
		return super.get2DigitYearStart();
	}

	/**
	 * Returns a copy of the {@link DateFormatSymbols} of {@code this}.
	 * <p>
	 * @return the {@link DateFormatSymbols} of {@code this}
	 *
	 * @see #setDateFormatSymbols(DateFormatSymbols)
	 */
	@Override
	public synchronized DateFormatSymbols getDateFormatSymbols() {
		return super.getDateFormatSymbols();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
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
	 * @see #get2DigitYearStart()
	 */
	@Override
	public synchronized void set2DigitYearStart(final Date startDate) {
		super.set2DigitYearStart(startDate);
	}

	/**
	 * Sets the {@link DateFormatSymbols} of {@code this}.
	 * <p>
	 * @param formatSymbols a {@link DateFormatSymbols}
	 *
	 * @see #getDateFormatSymbols()
	 */
	@Override
	public synchronized void setDateFormatSymbols(final DateFormatSymbols formatSymbols) {
		super.setDateFormatSymbols(formatSymbols);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FORMATTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Formats the specified {@link Date} into a date-time {@link String} and appends the result to
	 * the specified {@link StringBuffer}.
	 * <p>
	 * @param date       the date-time value to format into a date-time {@link String}
	 * @param toAppendTo where the new date-time {@link String} is to be appended
	 * @param position   the formatting position. On input: an alignment field, if desired; on
	 *                   output: the offsets of the alignment field
	 * <p>
	 * @return the formatted date-time {@link StringBuffer}
	 */
	@Override
	public synchronized StringBuffer format(final Date date, final StringBuffer toAppendTo,
			final FieldPosition position) {
		return super.format(date, toAppendTo, position);
	}

	/**
	 * Formats an {@link Object} producing an {@link AttributedCharacterIterator}. You can use the
	 * returned {@link AttributedCharacterIterator} to build the resulting {@link String}, as well
	 * as to determine information about the resulting {@link String}.
	 * <p>
	 * Each attribute key of the {@link AttributedCharacterIterator} will be of type {@link Field},
	 * with the corresponding attribute value being the same as the attribute key.
	 * <p>
	 * @param content the content {@link Object} to format
	 * <p>
	 * @return an {@link AttributedCharacterIterator} describing the formatted value
	 * <p>
	 * @throws IllegalArgumentException if {@code this} cannot format {@code object} or if the
	 *                                  format pattern {@link String} is invalid
	 * <p>
	 * @since 1.4
	 */
	@Override
	public synchronized AttributedCharacterIterator formatToCharacterIterator(
			final Object content) {
		return super.formatToCharacterIterator(content);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

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

	//////////////////////////////////////////////

	/**
	 * Applies the specified pattern {@link String} to {@code this}.
	 * <p>
	 * @param pattern the pattern {@link String} describing the date-time format
	 * <p>
	 * @throws IllegalArgumentException if {@code pattern} is invalid
	 */
	@Override
	public synchronized void applyPattern(final String pattern) {
		super.applyPattern(pattern);
	}

	/**
	 * Applies the specified localized pattern {@link String} to {@code this}.
	 * <p>
	 * @param pattern the pattern {@link String} describing the localized date-time format
	 * <p>
	 * @throws IllegalArgumentException if {@code pattern} is invalid
	 */
	@Override
	public synchronized void applyLocalizedPattern(final String pattern) {
		super.applyLocalizedPattern(pattern);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PARSERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parses the {@link Date} encoded in the specified {@link String}.
	 * <p>
	 * The method attempts to parse {@code text} starting at the index specified by
	 * {@code position}. If parsing succeeds, then the {@code position} index is updated to the
	 * index after the last {@code char} token used (parsing does not necessarily use all characters
	 * up to the end of the {@link String}) and the parsed date is returned. The updated
	 * {@code position} can be used to indicate the starting point for the next call to this method.
	 * If an error occurs, then the {@code position} index is not changed, the error index of
	 * {@code position} is set to the index of the {@code char} token where the error occurred and
	 * {@code null} is returned.
	 * <p>
	 * This parsing operation uses the {@link DateFormat#calendar calendar} to produce a
	 * {@link Date}. All the date-time fields of {@code calendar} are
	 * {@linkplain Calendar#clear() cleared} before parsing and the {@code calendar} default values
	 * of the date-time fields are used for any missing date-time information. For example, the year
	 * value of the parsed {@link Date} is 1970 with {@link GregorianCalendar} if no year value is
	 * specified from the parsing operation. The {@link TimeZone} value may be overwritten,
	 * depending on the specified pattern and the time zone value in {@code text}. Any
	 * {@link TimeZone} value that has previously been set by a call to the method
	 * {@link #setTimeZone(TimeZone)} may need to be restored for further operations.
	 * <p>
	 * @param text     the {@link String} to partially parse
	 * @param position a {@link ParsePosition} object with index and error index information as
	 *                 described above
	 * <p>
	 * @return a {@link Date} parsed from the {@link String}, or {@code null} in case of error
	 */
	@Override
	public synchronized Date parse(final String text, final ParsePosition position) {
		return super.parse(text, position);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>This also clones the {@link DateFormatSymbols}.</dd>
	 * </dl>
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public SafeDateFormat clone() {
		return (SafeDateFormat) super.clone();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		return super.equals(other);
	}

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see #equals(Object)
	 * @see System#identityHashCode(Object)
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
