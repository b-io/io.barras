/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.integration.r;

import static jupiter.common.io.IO.IO;

import java.io.IOException;

import jupiter.common.io.Systems;
import jupiter.common.thread.Worker;
import jupiter.common.util.Arrays;

public class RServer
		extends Worker<String[], Integer> {

	public RServer() {
	}

	@Override
	public Integer call(final String[] command) {
		try {
			return Systems.execute(Arrays.<String>merge(new String[] {R.PATH}, command, R.ARGS));
		} catch (final InterruptedException ex) {
			IO.error(ex);
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return IO.EXIT_FAILURE;
	}

	@Override
	public Worker<String[], Integer> clone() {
		return new RServer();
	}
}
