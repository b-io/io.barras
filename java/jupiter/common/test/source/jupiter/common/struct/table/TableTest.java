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
package jupiter.common.struct.table;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import java.io.IOException;

import jupiter.common.test.Test;

public class TableTest
		extends Test {

	public TableTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Table#shift}.
	 */
	public void testShift() {
		IO.test(BULLET, "shift");

		final Table<Object> table = new Table<Object>();
		final Table<Object> shiftedTable = table.clone();
		shiftedTable.shift(-1, -2);
		assertEquals(table, shiftedTable);
		shiftedTable.resize(1, 2);
		shiftedTable.shift(-1, -2);
		assertEquals(table, shiftedTable);
	}

	/**
	 * Tests {@link Table#transpose}.
	 */
	public void testTranspose() {
		IO.test(BULLET, "transpose");

		final DoubleTable table = new DoubleTable(1, 2);
		table.fill(5.);
		final DoubleTable clone = table.clone();
		assertEquals(table, clone);
		clone.transpose();
		assertNotSame(table, clone);
		clone.transpose();
		assertEquals(table, clone);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Table#load}.
	 */
	public void testLoad() {
		IO.test(BULLET, "load");

		try {
			new StringTable("test/resources/example.csv", true);
		} catch (final IOException ex) {
			IO.error(ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Table#equals}.
	 */
	public void testEquals() {
		IO.test(BULLET, "equals");

		final DoubleTable table = new DoubleTable(1, 2);
		table.fill(5.);
		assertEquals(table, table.clone());
	}

	/**
	 * Tests {@link Table#hashCode}.
	 */
	public void testHashCode() {
		IO.test(BULLET, "hashCode");

		final DoubleTable table = new DoubleTable(1, 2);
		table.fill(5.);
		assertEquals(table.hashCode(), table.clone().hashCode());
	}
}
