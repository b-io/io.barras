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

import jupiter.common.util.Arrays;
import jupiter.common.util.Strings;
import jupiter.gui.console.GraphicalConsole;

public class RConsole {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void main(final String[] args) {
		int status = IO.EXIT_SUCCESS;
		final GraphicalConsole console = new GraphicalConsole();
		R.start();
		try {
			R.installPackages(
					Arrays.toArray("bsts", "dplyr", "forecast", "Jmisc", "highcharter", "imputeTS",
							"lars", "lubridate", "mgcv", "quantmod", "Rblpapi", "RODBC", "shiny"));
			interactions();
		} catch (final Exception ignored) {
			status = IO.EXIT_FAILURE;
		} finally {
			R.stop();
			console.exit(status);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Interacts with the user.
	 */
	protected static void interactions() {
		boolean isRunning = true;

		do {
			// Process the input expression
			final String inputExpression = IO.input().trim();
			if (Strings.toLowerCase(inputExpression).contains("exit")) {
				IO.info("Good bye!");
				isRunning = false;
			} else {
				R.execute(inputExpression);
			}
		} while (isRunning);
	}
}
