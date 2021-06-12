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
package jupiter.common.util;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BAR;
import static jupiter.common.util.Characters.BULLET;
import static jupiter.common.util.Strings.EMPTY;

import jupiter.common.test.Test;

public class StringsTest
		extends Test {

	protected static final char[] DELIMITERS = new char[] {'.', ':', ',', ';', '-'};
	protected static final String[] STRING_DELIMITERS = new String[] {".", ":", ",", ";", "-"};
	protected static final String STRING = "-A.B:C,D;E-";

	////////////////////////////////////////////////////////////////////////////////////////////////

	public StringsTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Strings#toUnicode}.
	 */
	public void testToUnicode() {
		IO.test(BULLET, " toUnicode");

		assertEquals("\\u0030", Strings.toUnicode("0"));
		assertEquals("\\u0061", Strings.toUnicode("a"));
		assertEquals("\\u2022", Strings.toUnicode("•"));
	}

	//////////////////////////////////////////////

	/**
	 * Tests {@link Strings#toCase}.
	 */
	public void testToCase() {
		IO.test(BULLET, " toCase");

		assertEquals("hello world!", Strings.toCase("helloWorld!"));
	}

	/**
	 * Tests {@link Strings#toCamelCase}.
	 */
	public void testToCamelCase() {
		IO.test(BULLET, " toCamelCase");

		assertEquals("helloWorld!", Strings.toCamelCase("Hello world!"));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Strings#capitalizeFirst}.
	 */
	public void testCapitalizeFirst() {
		IO.test(BULLET, " capitalizeFirst");

		assertEquals("A", Strings.capitalizeFirst("a"));
		assertEquals("Hello", Strings.capitalizeFirst("hello"));
	}

	/**
	 * Tests {@link Strings#capitalizeStrictly}.
	 */
	public void testCapitalizeFully() {
		IO.test(BULLET, " capitalizeStrictly");

		assertEquals("Hello World!", Strings.capitalizeStrictly("hELLO wORLD!"));
	}

	//////////////////////////////////////////////

	/**
	 * Tests {@link Strings#replaceAll}.
	 */
	public void testReplaceAll() {
		IO.test(BULLET, " replaceAll");

		assertEquals(EMPTY, Strings.replaceAll(EMPTY, DELIMITERS, String.valueOf(BAR)));
		assertEquals("|A|B|C|D|E|", Strings.replaceAll(STRING, DELIMITERS, String.valueOf(BAR)));
	}

	//////////////////////////////////////////////

	/**
	 * Tests {@link Strings#split}.
	 */
	public void testSplit() {
		IO.test(BULLET, " split");

		assertTrue(Arrays.equals(new String[] {EMPTY},
				Strings.split(EMPTY, DELIMITERS).toArray()));
		assertTrue(Arrays.equals(new String[] {EMPTY, EMPTY},
				Strings.split("-", DELIMITERS).toArray()));
		assertTrue(Arrays.equals(new String[] {EMPTY, "A", "B", "C", "D", "E", EMPTY},
				Strings.split(STRING, DELIMITERS).toArray()));
	}

	/**
	 * Tests {@link Strings#splitString}.
	 */
	public void testSplitString() {
		IO.test(BULLET, " splitString");

		assertTrue(Arrays.equals(new String[] {EMPTY},
				Strings.splitString(EMPTY, STRING_DELIMITERS).toArray()));
		assertTrue(Arrays.equals(new String[] {EMPTY, EMPTY},
				Strings.splitString("-", STRING_DELIMITERS).toArray()));
		assertTrue(Arrays.equals(new String[] {EMPTY, "A", "B", "C", "D", "E", EMPTY},
				Strings.splitString(STRING, STRING_DELIMITERS).toArray()));
	}
}
