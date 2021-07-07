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
package jupiter.transfer.db;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.test.Test;
import jupiter.common.util.Strings;
import jupiter.transfer.db.SQL.DB;

public class SQLTest
		extends Test {

	public SQLTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public void testCreateSelectQuery() {
		IO.test(BULLET, "createSelectQuery");

		// • Select query
		SQL.DB = DB.StandardSQL;
		String result = SQL.createSelectQuery("test", Strings.EMPTY_ARRAY, new String[] {"a"});
		IO.test(result);
		assertEquals("SELECT * FROM \"test\" WHERE \"a\"=?", result);
		SQL.DB = DB.MSSQL;
		result = SQL.createSelectQuery("test", Strings.EMPTY_ARRAY, new String[] {"a"});
		IO.test(result);
		assertEquals("SELECT * FROM [test] WHERE [a]=?", result);
	}
}
