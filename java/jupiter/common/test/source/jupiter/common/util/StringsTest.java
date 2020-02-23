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

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Characters.BULLET;
import static jupiter.common.util.Characters.SPLITTER;
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
	 * Test of toUnicode method, of class Strings.
	 */
	public void testToUnicode() {
		IO.test(BULLET, " toUnicode");

		assertTrue("\\u0030".equals(Strings.toUnicode("0")));
		assertTrue("\\u0061".equals(Strings.toUnicode("a")));
		assertTrue("\\u2022".equals(Strings.toUnicode("•")));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of replaceAll method, of class Strings.
	 */
	public void testReplaceAll() {
		IO.test(BULLET, " replaceAll");

		assertEquals(EMPTY, Strings.replaceAll(EMPTY, DELIMITERS, String.valueOf(SPLITTER)));
		assertEquals("|A|B|C|D|E|",
				Strings.replaceAll(STRING, DELIMITERS, String.valueOf(SPLITTER)));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of split method, of class Strings.
	 */
	public void testSplit() {
		IO.test(BULLET, " split");

		assertEquals(Objects.hashCode(new String[] {EMPTY}),
				Objects.hashCode(Strings.split(EMPTY, DELIMITERS).toArray()));
		assertEquals(Objects.hashCode(Objects.hashCode(new String[] {EMPTY, EMPTY})),
				Objects.hashCode(Strings.split("-", DELIMITERS).toArray()));
		assertEquals(Objects.hashCode(new String[] {EMPTY, "A", "B", "C", "D", "E", EMPTY}),
				Objects.hashCode(Strings.split(STRING, DELIMITERS).toArray()));
	}

	/**
	 * Test of splitString method, of class Strings.
	 */
	public void testSplitString() {
		IO.test(BULLET, " splitString");

		assertEquals(Objects.hashCode(new String[] {EMPTY}),
				Objects.hashCode(Strings.splitString(EMPTY, STRING_DELIMITERS).toArray()));
		assertEquals(Objects.hashCode(new String[] {EMPTY, EMPTY}),
				Objects.hashCode(Strings.splitString("-", STRING_DELIMITERS).toArray()));
		assertEquals(Objects.hashCode(new String[] {EMPTY, "A", "B", "C", "D", "E", EMPTY}),
				Objects.hashCode(Strings.splitString(STRING, STRING_DELIMITERS).toArray()));
	}
}
