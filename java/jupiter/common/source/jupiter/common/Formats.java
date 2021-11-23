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
package jupiter.common;

import static jupiter.common.Charsets.UTF_8;
import static jupiter.common.util.Strings.LF;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.TimeZone;

import jupiter.common.math.Maths;
import jupiter.common.properties.Jupiter;
import jupiter.common.time.SynchronizedDateFormat;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class Formats {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The current version.
	 */
	public static final String VERSION = "1.6.0";

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default {@link Charset}.
	 */
	public static final Charset DEFAULT_CHARSET = UTF_8;
	/**
	 * The default {@link Locale}.
	 */
	public static final Locale DEFAULT_LOCALE = Locale.getDefault();
	/**
	 * The default {@link TimeZone}.
	 */
	public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

	//////////////////////////////////////////////

	/**
	 * The default line length.
	 */
	public static final int DEFAULT_LINE_LENGTH = 72;
	/**
	 * The default newline.
	 */
	public static final String DEFAULT_NEWLINE = LF;

	//////////////////////////////////////////////

	/**
	 * The default pattern {@link String} describing the date format.
	 */
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	/**
	 * The default pattern {@link String} describing the date-time format.
	 */
	public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	//////////////////////////////////////////////

	/**
	 * The default pattern {@link String} describing the default {@link DecimalFormat}.
	 */
	public static final String DEFAULT_PATTERN = "0.########";
	/**
	 * The default pattern {@link String} describing the default scientific {@link DecimalFormat}.
	 */
	public static final String DEFAULT_SCIENTIFIC_PATTERN = DEFAULT_PATTERN.concat("E0");

	/**
	 * The default maximum number of fraction digits.
	 */
	public static final int DEFAULT_MAX_FRACTION_DIGIT_COUNT = DEFAULT_PATTERN.length() - 2;
	/**
	 * The default maximum number of integer digits.
	 */
	public static final int DEFAULT_MAX_INTEGER_DIGIT_COUNT = DEFAULT_PATTERN.length() - 2;

	/**
	 * The default minimum scientific threshold (inclusive).
	 */
	public static final double DEFAULT_MIN_SCIENTIFIC_THRESHOLD = Maths.pow(10.,
			-DEFAULT_MAX_FRACTION_DIGIT_COUNT - 1);
	/**
	 * The default maximum scientific threshold (inclusive).
	 */
	public static final double DEFAULT_MAX_SCIENTIFIC_THRESHOLD = Maths.pow(10.,
			DEFAULT_MAX_INTEGER_DIGIT_COUNT);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The newline.
	 */
	public static volatile String NEWLINE = Jupiter.PROPERTIES.getProperty("newline",
			DEFAULT_NEWLINE);

	//////////////////////////////////////////////

	/**
	 * The {@link Charset}.
	 */
	public static volatile Charset CHARSET = Charsets.get(Jupiter.PROPERTIES.getProperty("charset",
			DEFAULT_CHARSET.name()));
	/**
	 * The {@link Locale}.
	 */
	public static volatile Locale LOCALE = Locales.get(Jupiter.PROPERTIES.getProperty("locale",
			DEFAULT_LOCALE.toString()));
	/**
	 * The {@link TimeZone}.
	 */
	public static volatile TimeZone TIME_ZONE = TimeZone.getTimeZone(Jupiter.PROPERTIES.getProperty(
			"timeZone", DEFAULT_TIME_ZONE.getID()));

	//////////////////////////////////////////////

	/**
	 * The line length.
	 */
	public static volatile int LINE_LENGTH = Jupiter.PROPERTIES.getInt("lineLength",
			DEFAULT_LINE_LENGTH);

	//////////////////////////////////////////////

	/**
	 * The {@link DateFormat}.
	 */
	public static volatile DateFormat DATE_FORMAT = new SynchronizedDateFormat(
			Jupiter.PROPERTIES.getProperty("datePattern", DEFAULT_DATE_PATTERN));
	/**
	 * The {@link DateFormat} with time.
	 */
	public static volatile DateFormat DATE_TIME_FORMAT = new SynchronizedDateFormat(
			Jupiter.PROPERTIES.getProperty("dateTimePattern", DEFAULT_DATE_TIME_PATTERN));

	//////////////////////////////////////////////

	/**
	 * The number length used for representing numbers as {@link String}.
	 */
	public static volatile int NUMBER_LENGTH = DEFAULT_PATTERN.length();
	/**
	 * The minimum number length.
	 */
	public static volatile int MIN_NUMBER_LENGTH = 1;

	/**
	 * The minimum number of fraction digits.
	 */
	public static volatile int MIN_FRACTION_DIGIT_COUNT = 0;
	/**
	 * The maximum number of fraction digits.
	 */
	public static volatile int MAX_FRACTION_DIGIT_COUNT = DEFAULT_MAX_FRACTION_DIGIT_COUNT;
	/**
	 * The minimum number of integer digits.
	 */
	public static volatile int MIN_INTEGER_DIGIT_COUNT = 1;
	/**
	 * The maximum number of integer digits.
	 */
	public static volatile int MAX_INTEGER_DIGIT_COUNT = DEFAULT_MAX_INTEGER_DIGIT_COUNT;

	/**
	 * The flag specifying whether to use grouping separators.
	 */
	public static volatile boolean USE_GROUPING_SEPARATORS = false;

	/**
	 * The {@link DecimalFormat}.
	 */
	public static volatile DecimalFormat DECIMAL_FORMAT = getDecimalFormat(DEFAULT_PATTERN);
	/**
	 * The {@link DecimalFormat} of {@code double} values.
	 */
	public static volatile DecimalFormat DOUBLE_DECIMAL_FORMAT = getDoubleDecimalFormat();
	/**
	 * The scientific {@link DecimalFormat}.
	 */
	public static volatile DecimalFormat SCIENTIFIC_DECIMAL_FORMAT = getDecimalFormat(
			DEFAULT_SCIENTIFIC_PATTERN);

	/**
	 * The minimum scientific threshold (inclusive).
	 */
	public static volatile double MIN_SCIENTIFIC_THRESHOLD = Jupiter.PROPERTIES.getDouble(
			"minScientificThreshold", DEFAULT_MIN_SCIENTIFIC_THRESHOLD);
	/**
	 * The maximum scientific threshold (inclusive).
	 */
	public static volatile double MAX_SCIENTIFIC_THRESHOLD = Jupiter.PROPERTIES.getDouble(
			"maxScientificThreshold", DEFAULT_MAX_SCIENTIFIC_THRESHOLD);

	//////////////////////////////////////////////

	/**
	 * The default {@link Format}.
	 */
	public static volatile Format FORMAT = new Format() {
		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public StringBuffer format(final Object content, final StringBuffer toAppendTo,
				final FieldPosition position) {
			toAppendTo.append(Objects.toString(content));
			return toAppendTo;
		}

		@Override
		public Object parseObject(final String content, final ParsePosition position) {
			return content;
		}
	};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Formats}.
	 */
	protected Formats() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link DecimalFormat} with the specified decimal pattern {@link String}.
	 * <p>
	 * @param pattern the pattern {@link String} describing the {@link DecimalFormat} ({@code null}
	 *                indicates that the default {@link DecimalFormat} should be used)
	 * <p>
	 * @return the {@link DecimalFormat} with the specified decimal pattern {@link String}
	 *
	 * @see DecimalFormat#DOUBLE_INTEGER_DIGITS
	 * @see DecimalFormat#DOUBLE_FRACTION_DIGITS
	 */
	public static DecimalFormat getDecimalFormat(final String pattern) {
		return getDecimalFormat(pattern, LOCALE);
	}

	/**
	 * Returns the {@link DecimalFormat} with the specified decimal pattern {@link String} and
	 * {@link Locale}.
	 * <p>
	 * @param pattern the pattern {@link String} describing the {@link DecimalFormat} ({@code null}
	 *                indicates that the default {@link DecimalFormat} should be used)
	 * @param locale  the {@link Locale} whose {@link DecimalFormatSymbols} should be used
	 * <p>
	 * @return the {@link DecimalFormat} with the specified decimal pattern {@link String} and
	 *         {@link Locale}
	 *
	 * @see DecimalFormat#DOUBLE_INTEGER_DIGITS
	 * @see DecimalFormat#DOUBLE_FRACTION_DIGITS
	 */
	public static DecimalFormat getDecimalFormat(final String pattern, final Locale locale) {
		final DecimalFormat format = Strings.isNonEmpty(pattern) ? new DecimalFormat(pattern) :
				new DecimalFormat();
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(locale));
		format.setGroupingUsed(USE_GROUPING_SEPARATORS);
		format.setMinimumIntegerDigits(MIN_INTEGER_DIGIT_COUNT);
		format.setMinimumFractionDigits(MIN_FRACTION_DIGIT_COUNT);
		format.setMaximumFractionDigits(MAX_FRACTION_DIGIT_COUNT);
		return format;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link DecimalFormat} of {@code double} values.
	 * <p>
	 * @return the {@link DecimalFormat} of {@code double} values
	 *
	 * @see DecimalFormat#DOUBLE_INTEGER_DIGITS
	 * @see DecimalFormat#DOUBLE_FRACTION_DIGITS
	 */
	public static DecimalFormat getDoubleDecimalFormat() {
		return getDoubleDecimalFormat(LOCALE);
	}

	/**
	 * Returns the {@link DecimalFormat} of {@code double} values with the specified {@link Locale}.
	 * <p>
	 * @param locale the {@link Locale} whose {@link DecimalFormatSymbols} should be used
	 * <p>
	 * @return the {@link DecimalFormat} of {@code double} values with the specified {@link Locale}
	 *
	 * @see DecimalFormat#DOUBLE_INTEGER_DIGITS
	 * @see DecimalFormat#DOUBLE_FRACTION_DIGITS
	 */
	public static DecimalFormat getDoubleDecimalFormat(final Locale locale) {
		final DecimalFormat format = new DecimalFormat();
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(locale));
		format.setGroupingUsed(USE_GROUPING_SEPARATORS);
		format.setMaximumIntegerDigits(309);
		format.setMaximumFractionDigits(340);
		return format;
	}
}
