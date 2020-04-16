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
package jupiter.common.io;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.io.file.Files.TEMP_FILE_EXTENSION;
import static jupiter.common.util.Characters.BULLET;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import jupiter.common.io.file.FileHandler;
import jupiter.common.test.Test;

public class FileHandlerTest
		extends Test {

	public FileHandlerTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link FileHandler#replaceAll}.
	 */
	public void testReplaceAll() {
		IO.test(BULLET, " replaceAll");

		final FileHandler fileHandler = new FileHandler("replaceAll.".concat(TEMP_FILE_EXTENSION));
		try {
			fileHandler.empty();
			fileHandler.writeLine("TEST1");
			fileHandler.writeLine("TEST22");
			fileHandler.writeLine("TEST3");
			fileHandler.replaceAll("TEST22", "TEST2");
			final BufferedReader reader = fileHandler.createReader();
			int i = 1;
			String line;
			while ((line = reader.readLine()) != null) {
				assertEquals("TEST" + i, line);
				++i;
			}
		} catch (final FileNotFoundException ex) {
			IO.error(ex);
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			fileHandler.clear();
			fileHandler.delete();
		}
	}
}
