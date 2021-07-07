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
import static jupiter.common.Formats.CHARSET;
import static jupiter.common.Formats.DATE_FORMAT;
import static jupiter.common.Formats.DATE_TIME_FORMAT;
import static jupiter.common.Formats.DEFAULT_DATE_PATTERN;
import static jupiter.common.Formats.DEFAULT_DATE_TIME_PATTERN;
import static jupiter.common.Formats.DEFAULT_MAX_SCIENTIFIC_THRESHOLD;
import static jupiter.common.Formats.DEFAULT_MIN_SCIENTIFIC_THRESHOLD;
import static jupiter.common.Formats.DEFAULT_TIME_ZONE;
import static jupiter.common.Formats.LINE_LENGTH;
import static jupiter.common.Formats.LOCALE;
import static jupiter.common.Formats.MAX_SCIENTIFIC_THRESHOLD;
import static jupiter.common.Formats.MIN_SCIENTIFIC_THRESHOLD;
import static jupiter.common.Formats.NEWLINE;
import static jupiter.common.Formats.TIME_ZONE;
import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;
import static jupiter.common.util.Strings.LF;

import java.util.Locale;

import jupiter.common.test.Test;
import jupiter.common.time.SafeDateFormat;

public class FormatsTest
		extends Test {

	public FormatsTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Formats}.
	 */
	public void test() {
		IO.test(BULLET, "Formats");

		assertEquals(UTF_8, CHARSET);
		assertEquals(new Locale("en", "US"), LOCALE);
		assertEquals(DEFAULT_TIME_ZONE, TIME_ZONE);

		assertEquals(72, LINE_LENGTH);
		assertEquals(LF, NEWLINE);

		assertEquals(new SafeDateFormat(DEFAULT_DATE_PATTERN), DATE_FORMAT);
		assertEquals(new SafeDateFormat(DEFAULT_DATE_TIME_PATTERN), DATE_TIME_FORMAT);

		assertEquals(DEFAULT_MIN_SCIENTIFIC_THRESHOLD, MIN_SCIENTIFIC_THRESHOLD);
		assertEquals(DEFAULT_MAX_SCIENTIFIC_THRESHOLD, MAX_SCIENTIFIC_THRESHOLD);
	}
}
