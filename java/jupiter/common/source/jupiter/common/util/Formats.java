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
package jupiter.common.util;

import static jupiter.common.util.Strings.EMPTY;

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Locale;

import jupiter.common.test.NumberArguments;

public class Formats {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The current version.
	 */
	public static final String VERSION = "1.6.0";

	//////////////////////////////////////////////

	/**
	 * The newline.
	 */
	public static final String NEW_LINE = "\n";

	/**
	 * The UTF-8 {@link Charset}.
	 */
	public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default {@link Format}.
	 */
	public static volatile Format DEFAULT_FORMAT = new Format() {
		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public StringBuffer format(final Object object, final StringBuffer toAppendTo,
				final FieldPosition position) {
			toAppendTo.append(object);
			return toAppendTo;
		}

		@Override
		public Object parseObject(final String source, final ParsePosition position) {
			return source;
		}
	};

	//////////////////////////////////////////////

	/**
	 * The default {@link Charset}.
	 */
	public static volatile Charset DEFAULT_CHARSET = UTF8_CHARSET;

	/**
	 * The default {@link Locale}.
	 */
	public static volatile Locale DEFAULT_LOCALE = Locale.getDefault();

	/**
	 * The default length of a line (used by IO).
	 */
	public static volatile int DEFAULT_LINE_LENGTH = 72;

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
	public static final String DEFAULT_PATTERN = "0.######";
	/**
	 * The default pattern {@link String} describing the default scientific {@link DecimalFormat}.
	 */
	public static final String DEFAULT_SCIENTIFIC_PATTERN = DEFAULT_PATTERN + "E0";
	/**
	 * The default minimum number of fraction digits.
	 */
	public static final int DEFAULT_MIN_FRACTION_DIGITS = 0;
	/**
	 * The default maximum number of fraction digits.
	 */
	public static final int DEFAULT_MAX_FRACTION_DIGITS = DEFAULT_PATTERN.length() - 2;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The number length used for representing numbers as {@link String}.
	 */
	public static volatile int NUMBER_LENGTH = DEFAULT_PATTERN.length();
	/**
	 * The minimum number length.
	 */
	public static volatile int MIN_NUMBER_LENGTH = 1;
	/**
	 * The minimum number of integer digits.
	 */
	public static volatile int MIN_INTEGER_DIGITS = 1;
	/**
	 * The maximum number of integer digits before using the scientific {@link DecimalFormat}.
	 */
	public static volatile int MAX_INTEGER_DIGITS = 6;
	/**
	 * The minimum number of fraction digits.
	 */
	public static volatile int MIN_FRACTION_DIGITS = DEFAULT_MIN_FRACTION_DIGITS;
	/**
	 * The maximum number of fraction digits.
	 */
	public static volatile int MAX_FRACTION_DIGITS = DEFAULT_MAX_FRACTION_DIGITS;

	/**
	 * The {@link DecimalFormat} of {@code double} values.
	 */
	public static final DecimalFormat DOUBLE_DECIMAL_FORMAT = getDoubleDecimalFormat();
	/**
	 * The {@link DecimalFormat}.
	 */
	public static volatile DecimalFormat DECIMAL_FORMAT = getDecimalFormat(DEFAULT_PATTERN);
	/**
	 * The scientific {@link DecimalFormat}.
	 */
	public static volatile DecimalFormat SCIENTIFIC_DECIMAL_FORMAT = getDecimalFormat(
			DEFAULT_SCIENTIFIC_PATTERN);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Formats}.
	 */
	protected Formats() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FORMATTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String formatNumber(final Object number) {
		// Check the arguments
		NumberArguments.requireNumber(number);

		// Format the number
		final String formattedNumber;
		final String numberString = DOUBLE_DECIMAL_FORMAT.format(number);
		int integerDigitCount = numberString.length(), fractionDigitCount = numberString.length();
		if (numberString.contains("-")) {
			--integerDigitCount;
			--fractionDigitCount;
		}
		final int decimalPointIndex = numberString.indexOf('.');
		if (decimalPointIndex >= 0) {
			integerDigitCount -= numberString.length() - decimalPointIndex;
			fractionDigitCount -= decimalPointIndex + 1;
		} else {
			fractionDigitCount = 0;
		}
		if (integerDigitCount > MAX_INTEGER_DIGITS || fractionDigitCount > MAX_FRACTION_DIGITS) {
			formattedNumber = SCIENTIFIC_DECIMAL_FORMAT.format(number).replace("E0", EMPTY);
		} else {
			formattedNumber = DECIMAL_FORMAT.format(number);
		}
		return formattedNumber;
	}

	//////////////////////////////////////////////

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
		return getDecimalFormat(pattern, DEFAULT_LOCALE);
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
		final DecimalFormat format = Strings.isNotEmpty(pattern) ? new DecimalFormat(pattern) :
				new DecimalFormat();
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(locale));
		format.setGroupingUsed(false); // whether to use grouping separators
		format.setMinimumIntegerDigits(MIN_INTEGER_DIGITS);
		format.setMinimumFractionDigits(MIN_FRACTION_DIGITS);
		format.setMaximumFractionDigits(MAX_FRACTION_DIGITS);
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
	protected static DecimalFormat getDoubleDecimalFormat() {
		return getDoubleDecimalFormat(DEFAULT_LOCALE);
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
	protected static DecimalFormat getDoubleDecimalFormat(final Locale locale) {
		final DecimalFormat format = new DecimalFormat();
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(locale));
		format.setGroupingUsed(false); // whether to use grouping separators
		format.setMaximumIntegerDigits(309);
		format.setMaximumFractionDigits(340);
		return format;
	}
}
