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
package jupiter.math.calculator.process;

import static jupiter.common.io.IO.IO;

import java.util.HashMap;
import java.util.Map;

import jupiter.common.test.Test;
import jupiter.common.test.Tests;
import jupiter.common.thread.Result;
import jupiter.common.time.Chronometer;
import jupiter.common.util.Strings;
import jupiter.math.calculator.model.Element;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Matrix;

public class CalculatorTest
		extends Test {

	public CalculatorTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of process method, of class Calculator.
	 */
	public void testProcess() {
		IO.test("process");

		// Initialize
		final int testCount = 20;
		final int matrixSize = 200;
		final Chronometer chrono = new Chronometer();
		final double[] elementTimes = new double[2 * testCount];
		final double[] entityTimes = new double[2 * testCount];

		// Process
		Calculator.parallelize();
		try {
			for (int t = 0; t < testCount; ++t) {
				// Initialize
				final Map<String, Element> context = new HashMap<String, Element>();
				final Matrix matrix = Matrix.random(matrixSize);
				final String matrixString = Strings.toString(matrix);
				final String e1 = "(" + matrixString + " * " + "@(" + matrixString + ")) + " + "(" +
						matrixString + " * " + "@(" + matrixString + "))";
				final String e2 = "(" + matrixString + " / " + matrixString + ") + " + "(" +
						matrixString + " / " + matrixString + ")";

				// Test the parsing and evaluation of the element and entity #1
				chrono.start();
				final Result<Element> tree1 = ExpressionHandler.parseExpression(e1, context);
				final Element element1 = tree1.getOutput();
				chrono.stop();
				IO.debug("Element1: ", tree1);
				IO.debug("Element1: ", chrono.getMilliseconds(), " [ms]");
				elementTimes[2 * t] = chrono.getMilliseconds();
				chrono.start();
				final Result<Entity> entityResult1 = Calculator.evaluateTree(element1, context);
				final Entity entity1 = entityResult1.getOutput();
				chrono.stop();
				IO.debug("Entity1: ", entityResult1);
				IO.debug("Entity1: ", chrono.getMilliseconds(), " [ms]");
				entityTimes[2 * t] = chrono.getMilliseconds();

				// Test the parsing and evaluation of the element and entity #2
				chrono.start();
				final Result<Element> tree2 = ExpressionHandler.parseExpression(e2, context);
				final Element element2 = tree2.getOutput();
				chrono.stop();
				IO.debug("Element2: ", element2);
				IO.debug("Element2: ", chrono.getMilliseconds(), " [ms]");
				elementTimes[2 * t + 1] = chrono.getMilliseconds();
				chrono.start();
				final Result<Entity> entityResult2 = Calculator.evaluateTree(element2, context);
				final Entity entity2 = entityResult2.getOutput();
				chrono.stop();
				IO.debug("Entity2: ", entity2);
				IO.debug("Entity2: ", chrono.getMilliseconds(), " [ms]");
				entityTimes[2 * t + 1] = chrono.getMilliseconds();

				// Test the results
				assertEquals(entity1, entity2);
			}
			Tests.printTimes(elementTimes);
			Tests.printTimes(entityTimes);
		} finally {
			Calculator.unparallelize();
		}
	}
}
