/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.common.struct.list.row;

import static junit.framework.Assert.assertEquals;
import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.test.Test;
import jupiter.common.util.Arrays;
import jupiter.common.util.Strings;

public class RowListTest
		extends Test {

	public RowListTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link RowList#getHeader}.
	 */
	public void testGetHeader() {
		IO.test(BULLET, "getHeader");

		final RowList emptyRowList = new RowList(0);
		assertEquals(Strings.EMPTY_ARRAY, emptyRowList.getHeader());

		final String[] header = new String[] {"A", "B", "C"};
		final Object[] values = new Integer[] {1, 2, 3};
		assertEquals(header, new RowList(new Row(header, values)).getHeader());
		assertEquals(header, new RowList(header, new Row(header, values)).getHeader());
	}

	//////////////////////////////////////////////

	/**
	 * Tests {@link RowList#getRow}.
	 */
	public void testGetRow() {
		IO.test(BULLET, "getRow");

		final Object index = "Test";
		final String[] header = new String[] {"A", "B", "C"};
		final Object[] values = new Integer[] {1, 2, 3};
		final Row row = new Row(index, header, values);
		final RowList rowList = new RowList(row);
		assertEquals(1, rowList.getRowCount());
		assertEquals(3, rowList.getColumnCount());
		assertTrue(Arrays.equals(row.elements, rowList.getRow(index)));
	}

	/**
	 * Tests {@link RowList#getColumn}.
	 */
	public void testGetColumn() {
		IO.test(BULLET, "getColumn");

		final String[] header = new String[] {"A", "B", "C"};
		final Object[] values = new Integer[] {1, 2, 3};
		final Row row = new Row(header, values);
		final RowList rowList = new RowList(row);
		assertEquals(1, rowList.getRowCount());
		assertEquals(3, rowList.getColumnCount());
		assertTrue(Arrays.equals(new Integer[] {1}, rowList.getColumn("A")));
	}

	//////////////////////////////////////////////

	/**
	 * Tests {@link RowList#clone}.
	 */
	public void testClone() {
		IO.test(BULLET, "clone");

		final Object index = "Test";
		final String[] header = new String[] {"A", "B", "C"};
		final Object[] values = new Integer[] {1, 2, 3};
		final Row row = new Row(index, header, values);
		final RowList rowList = new RowList(row);
		rowList.add(row);
		assertEquals(rowList, rowList.clone());
	}
}
