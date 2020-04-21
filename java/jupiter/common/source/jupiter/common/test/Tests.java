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
package jupiter.common.test;

import static jupiter.common.io.InputOutput.IO;

import java.util.Collection;

import jupiter.common.io.Messages;
import jupiter.common.math.DoubleInterval;
import jupiter.common.math.Maths;
import jupiter.common.math.Statistics;
import jupiter.common.util.Doubles;
import jupiter.common.util.Numbers;

public class Tests {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Tests}.
	 */
	protected Tests() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void printTimes(final double... times) {
		printValues("time [ms]", times);
	}

	public static void printTimes(final Number[] times) {
		printValues("time [ms]", times);
	}

	public static void printTimes(final Collection<? extends Number> times) {
		printValues("time [ms]", times);
	}

	//////////////////////////////////////////////

	public static void printValues(final String label, final double... values) {
		final double mean = Statistics.mean(values);
		final double stddev = Statistics.sampleStandardDeviationWith(values, mean);
		final DoubleInterval confidenceInterval = Statistics.confidenceInterval(values.length, mean,
				stddev);
		printAverageValue(label, mean, confidenceInterval);
		printMinMaxInterval(Maths.min(values), Maths.max(values));
	}

	public static void printValues(final String label, final Number[] values) {
		final double mean = Statistics.mean(values);
		final double stddev = Statistics.sampleStandardDeviationWith(values, mean);
		final DoubleInterval confidenceInterval = Statistics.confidenceInterval(values.length, mean,
				stddev);
		printAverageValue(label, mean, confidenceInterval);
		printMinMaxInterval(Maths.min(values), Maths.max(values));
	}

	public static void printValues(final String label, final Collection<? extends Number> values) {
		final double mean = Statistics.mean(values);
		final double stddev = Statistics.sampleStandardDeviationWith(values, mean);
		final DoubleInterval confidenceInterval = Statistics.confidenceInterval(values.size(),
				mean, stddev);
		printAverageValue(label, mean, confidenceInterval);
		printMinMaxInterval(Maths.min(values), Maths.max(values));
	}

	protected static void printAverageValue(final String label, final double mean,
			final DoubleInterval confidenceInterval) {
		IO.test("Average ", label, ": ", Doubles.format(mean),
				" ± ", Doubles.format(confidenceInterval.getUpperBoundValue() - mean),
				" (", Doubles.formatPercent(Maths.DEFAULT_CONFIDENCE), ")");
	}

	protected static void printMinMaxInterval(final double min, final double max) {
		IO.test("Min/max interval: ", new DoubleInterval(min, max));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of {@link Test}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Test},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Test;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link Test}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link Test},
	 *         {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return Test.class.isAssignableFrom(c);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void check(final boolean isPassed) {
		if (!isPassed) {
			final StackTraceElement stackTraceElement = new Throwable().fillInStackTrace()
					.getStackTrace()[1];
			final String simpleClassName = Messages.getSimpleClassName(stackTraceElement);
			IO.error("The test in class ", simpleClassName, " at line ",
					stackTraceElement.getLineNumber(), " has failed");
		}
	}

	public static void equals(final Number a, final Number b) {
		if (!Numbers.equals(a, b)) {
			IO.error("The equality test has failed: ", a, " is not equal to ", b);
		}
	}
}
