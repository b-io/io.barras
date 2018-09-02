/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Formats {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The current version.
	 */
	public static final String VERSION = "1.0.0";

	/**
	 * The UTF-8 encoding.
	 */
	public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	/**
	 * The default encoding.
	 */
	public static final Charset DEFAULT_CHARSET = UTF8_CHARSET;
	public static final String DEFAULT_CHARSET_NAME = DEFAULT_CHARSET.name();

	/**
	 * The default locale.
	 */
	public static final Locale DEFAULT_LOCALE = Locale.getDefault();

	/**
	 * The default length of a line (useful for IO).
	 */
	public static final int DEFAULT_LINE_LENGTH = 72;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default date format.
	 */
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	/**
	 * The default date and time format.
	 */
	public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default decimal pattern.
	 */
	public static final String DEFAULT_PATTERN = "0.####";
	/**
	 * The default scientific pattern.
	 */
	public static final String DEFAULT_SCIENTIFIC_PATTERN = DEFAULT_PATTERN + "E0";

	/**
	 * The default number length.
	 */
	public static final int DEFAULT_NUMBER_LENGTH = DEFAULT_PATTERN.length();
	/**
	 * The default minimum number of digits for integer.
	 */
	public static final int DEFAULT_MIN_INTEGER_DIGITS = 1;
	/**
	 * The default minimum number of fraction digits.
	 */
	public static final int DEFAULT_MIN_FRACTION_DIGITS = 0;
	/**
	 * The default maximum number of fraction digits.
	 */
	public static final int DEFAULT_MAX_FRACTION_DIGITS = DEFAULT_NUMBER_LENGTH - 2;

	/**
	 * The minimum number length.
	 */
	public static volatile int MIN_NUMBER_LENGTH = 1;
	/**
	 * The minimum number of fraction digits.
	 */
	public static volatile int MIN_FRACTION_DIGITS = DEFAULT_MIN_FRACTION_DIGITS;
	/**
	 * The maximum number of fraction digits.
	 */
	public static volatile int MAX_FRACTION_DIGITS = DEFAULT_MAX_FRACTION_DIGITS;

	/**
	 * The default decimal format.
	 */
	public static volatile DecimalFormat DECIMAL_FORMAT = getDecimalFormat(DEFAULT_PATTERN);
	/**
	 * The default scientific format.
	 */
	public static volatile DecimalFormat SCIENTIFIC_DECIMAL_FORMAT = getDecimalFormat(
			DEFAULT_SCIENTIFIC_PATTERN);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Formats() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FORMATTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String format(final Number number) {
		final String formattedNumber;
		final String numberString = Strings.toString(number);
		int digitCount = numberString.length();
		if (numberString.contains("-")) {
			--digitCount;
		}
		if (numberString.contains(".")) {
			--digitCount;
		}
		if (digitCount > MAX_FRACTION_DIGITS + 2) {
			formattedNumber = SCIENTIFIC_DECIMAL_FORMAT.format(number).replace("E0", "");
		} else {
			formattedNumber = DECIMAL_FORMAT.format(number);
		}
		return formattedNumber;
	}

	public static DecimalFormat getDecimalFormat(final String pattern) {
		return getDecimalFormat(pattern, DEFAULT_LOCALE);
	}

	public static DecimalFormat getDecimalFormat(final String pattern, final Locale locale) {
		final DecimalFormat format = Strings.isNotEmpty(pattern) ? new DecimalFormat(pattern) :
				new DecimalFormat();
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(locale));
		format.setGroupingUsed(false); // whether to use grouping separators
		format.setMinimumFractionDigits(MIN_FRACTION_DIGITS);
		format.setMaximumFractionDigits(MAX_FRACTION_DIGITS);
		format.setMinimumIntegerDigits(DEFAULT_MIN_INTEGER_DIGITS);
		return format;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String getDimensions(final int m, final int n) {
		return Strings.parenthesize(m + " x " + n);
	}
}
