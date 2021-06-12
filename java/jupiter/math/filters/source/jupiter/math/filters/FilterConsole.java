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
package jupiter.math.filters;

import static jupiter.common.io.InputOutput.IO;

import jupiter.common.io.InputOutput;
import jupiter.gui.console.GraphicalConsole;
import jupiter.math.linear.entity.Scalar;

public class FilterConsole
		extends GraphicalConsole {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts the {@link FilterConsole}.
	 * <p>
	 * @param args the array of command line arguments
	 */
	public static void main(final String[] args) {
		int status = InputOutput.EXIT_SUCCESS;
		final FilterConsole console = new FilterConsole();
		try {
			// Get the measurements to update and predict the state (using the Kalman filter)
			console.run();
		} catch (final Exception ignored) {
			status = InputOutput.EXIT_FAILURE;
		} finally {
			console.exit(status);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link KalmanFilter}.
	 */
	protected final KalmanFilter filter = new KalmanFilter();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link FilterConsole}.
	 */
	protected FilterConsole() {
		filter.x = new Scalar(1.); // the initial guess
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Executes the specified input expression {@link String}.
	 * <p>
	 * @param inputExpression the input expression {@link String} to execute
	 */
	@Override
	protected void execute(final String inputExpression) {
		// Update the state (a posteriori)
		filter.update(new Scalar(Double.parseDouble(inputExpression)));
		// Predict the state (a priori)
		filter.predict();
		IO.result(filter.x);
	}
}
