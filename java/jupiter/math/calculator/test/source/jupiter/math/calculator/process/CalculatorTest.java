/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;
import static jupiter.math.analysis.function.univariate.UnivariateFunctions.LOG;

import java.util.Map;

import jupiter.common.struct.map.hash.ExtendedHashMap;
import jupiter.common.test.Test;
import jupiter.common.test.Tests;
import jupiter.common.thread.Result;
import jupiter.common.time.Chronometer;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;
import jupiter.math.calculator.model.Element;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.entity.Scalar;

public class CalculatorTest
		extends Test {

	public CalculatorTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Calculator#process}.
	 */
	public void testProcess() {
		IO.test(BULLET, "process");

		// Initialize
		final int testCount = 20;
		final int matrixSize = 200;
		final Chronometer chrono = new Chronometer();
		final double[] elementTimes = new double[2 * testCount];
		final double[] entityTimes = new double[2 * testCount];

		// Process
		Calculator.parallelize();
		try {
			for (int ti = 0; ti < testCount; ++ti) {
				// Initialize
				final Map<String, Element> context = new ExtendedHashMap<String, Element>();
				final Matrix matrix = Matrix.random(matrixSize);
				final String matrixString = Objects.toString(matrix);
				final String e1 = Strings.join("(", matrixString, "* inv(", matrixString, "))+",
						"(", matrixString, "* inv(", matrixString, "))");
				final String e2 = Strings.join("(", matrixString, "/", matrixString, ")+",
						"(", matrixString, "/", matrixString, ")");
				final String e3 = "log(2. + 2.) + 3.";
				final String e4 = "[1., 2.; 3., 4.] % 2.";
				final String e5 = "min(2., 4.) + max(2., 8.)";

				// Test the parsing and evaluation of the element and entity #1 (inverse)
				chrono.start();
				final Result<Element> tree1 = ExpressionHandler.parseExpression(e1, context);
				final Element element1 = tree1.getOutput();
				chrono.stop();
				IO.debug("Element1:", tree1);
				IO.debug("Element1:", chrono.getMilliseconds(), "[ms]");
				elementTimes[2 * ti] = chrono.getMilliseconds();
				chrono.start();
				final Result<Entity> entityResult1 = Calculator.evaluateTree(element1, context);
				final Entity entity1 = entityResult1.getOutput();
				chrono.stop();
				IO.debug("Entity1:", entityResult1);
				IO.debug("Entity1:", chrono.getMilliseconds(), "[ms]");
				entityTimes[2 * ti] = chrono.getMilliseconds();

				// Test the parsing and evaluation of the element and entity #2 (inverse)
				chrono.start();
				final Result<Element> tree2 = ExpressionHandler.parseExpression(e2, context);
				final Element element2 = tree2.getOutput();
				chrono.stop();
				IO.debug("Element2:", element2);
				IO.debug("Element2:", chrono.getMilliseconds(), "[ms]");
				elementTimes[2 * ti + 1] = chrono.getMilliseconds();
				chrono.start();
				final Result<Entity> entityResult2 = Calculator.evaluateTree(element2, context);
				final Entity entity2 = entityResult2.getOutput();
				chrono.stop();
				IO.debug("Entity2:", entity2);
				IO.debug("Entity2:", chrono.getMilliseconds(), "[ms]");
				entityTimes[2 * ti + 1] = chrono.getMilliseconds();

				// Verify the results
				assertEquals(entity1, entity2);

				// Test the parsing and evaluation of the element and entity #3 (log)
				final Result<Element> tree3 = ExpressionHandler.parseExpression(e3, context);
				final Element element3 = tree3.getOutput();
				IO.debug("Element3:", element3);
				final Result<Entity> entityResult3 = Calculator.evaluateTree(element3, context);
				final Entity entity3 = entityResult3.getOutput();
				IO.debug("Entity3:", entity3);

				// Verify the results
				assertEquals(new Scalar(4.).apply(LOG).add(3.), entity3);

				// Test the parsing and evaluation of the element and entity #4 (modulo)
				final Result<Element> tree4 = ExpressionHandler.parseExpression(e4, context);
				final Element element4 = tree4.getOutput();
				IO.debug("Element4:", element4);
				final Result<Entity> entityResult4 = Calculator.evaluateTree(element4, context);
				final Entity entity4 = entityResult4.getOutput();
				IO.debug("Entity4:", entity4);

				// Verify the results
				assertEquals(new Matrix(new double[][] {{1., 0.}, {1., 0.}}), entity4);

				// Test the parsing and evaluation of the element and entity #5 (modulo)
				final Result<Element> tree5 = ExpressionHandler.parseExpression(e5, context);
				final Element element5 = tree5.getOutput();
				IO.debug("Element5:", element5);
				final Result<Entity> entityResult5 = Calculator.evaluateTree(element5, context);
				final Entity entity5 = entityResult5.getOutput();
				IO.debug("Entity5:", entity5);

				// Verify the results
				assertEquals(new Scalar(10.), entity5);
			}
			Tests.printTimes(elementTimes);
			Tests.printTimes(entityTimes);
		} finally {
			Calculator.unparallelize();
		}
	}
}
