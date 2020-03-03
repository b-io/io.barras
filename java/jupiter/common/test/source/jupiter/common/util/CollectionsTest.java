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

import java.util.Collection;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.test.Test;

public class CollectionsTest
		extends Test {

	protected static final Collection<String> EMPTY_COLLECTION = new ExtendedList<String>();
	protected static final Collection<Number> NUMBER_COLLECTION = new ExtendedList<Number>(null,
			new Integer(1), new Double(1.));
	protected static final Collection<String> STRING_COLLECTION = new ExtendedList<String>(null,
			"a", "b", "c", "d", "e", "f");

	////////////////////////////////////////////////////////////////////////////////////////////////

	public CollectionsTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of getElementClass method, of class Arrays.
	 */
	public void testGetElementClass() {
		IO.test(BULLET, " getElementClass");

		assertEquals(null, Collections.getElementClass(EMPTY_COLLECTION));
		assertEquals(Number.class, Collections.getElementClass(NUMBER_COLLECTION));
		assertEquals(String.class, Collections.getElementClass(STRING_COLLECTION));
	}
}