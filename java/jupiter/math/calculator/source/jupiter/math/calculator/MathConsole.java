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
package jupiter.math.calculator;

import static jupiter.common.io.IO.IO;

import jupiter.common.util.Strings;
import jupiter.math.calculator.process.Calculator;

public class MathConsole {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts the {@link MathConsole}.
	 * <p>
	 * @param args the array of command line arguments
	 */
	public static void main(final String[] args) {
		IO.clear();
		Calculator.parallelize();
		try {
			interactions();
		} finally {
			Calculator.unparallelize();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Interacts with the user.
	 */
	protected static void interactions() {
		final Calculator calc = new Calculator();
		boolean isRunning = true;
		do {
			// Process the input expression
			final String inputExpression = IO.input().trim();
			if (Strings.toLowerCase(inputExpression).contains("exit")) {
				IO.info("Good bye!");
				isRunning = false;
			} else {
				IO.result(calc.process(inputExpression));
			}
		} while (isRunning);
	}
}