/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io>
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
package jupiter.common.thread;

import static jupiter.common.io.IO.IO;

import java.io.IOException;

import junit.framework.TestCase;
import jupiter.common.struct.table.StringTable;
import jupiter.common.struct.table.Table;

public class TableTest
		extends TestCase {

	public TableTest(final String testName) {
		super(testName);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of load method, of class Table.
	 */
	public void testLoad() {
		IO.test("load");

		try {
			new StringTable("test/resources/testFX.csv", true);
		} catch (final IOException ex) {
			IO.error(ex);
		}
	}

	/**
	 * Test of transpose method, of class Table.
	 */
	public void testTranspose() {
		IO.test("transpose");

		final Table<Double> table1 = new Table<Double>(Double.class, 1, 2);
		table1.fill(5.);
		final Table<Double> table2 = table1.clone();
		assertEquals(table1, table2);
		table2.transpose();
		assertNotSame(table1, table2);
		table2.transpose();
		assertEquals(table1, table2);
	}

	/**
	 * Test of equals method, of class Table.
	 */
	public void testEquals() {
		IO.test("equals");

		final Table<Double> table1 = new Table<Double>(Double.class, 1, 2);
		table1.fill(5.);
		final Table<Double> table2 = table1.clone();
		assertEquals(table1, table2);
	}

	/**
	 * Test of hashCode method, of class Table.
	 */
	public void testHashCode() {
		IO.test("hashCode");

		final Table<Double> table1 = new Table<Double>(Double.class, 1, 2);
		table1.fill(5.);
		final Table<Double> table2 = table1.clone();
		assertEquals(table1.hashCode(), table2.hashCode());
	}
}
