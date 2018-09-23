/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.learning.supervised;

import static jupiter.common.io.IO.IO;

import java.io.IOException;
import java.util.Random;

import jupiter.common.test.Test;
import jupiter.common.test.Tests;
import jupiter.common.time.Chronometer;
import jupiter.common.util.Doubles;
import jupiter.learning.supervised.function.ActivationFunction;
import jupiter.learning.supervised.function.ActivationFunctions;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.entity.Vector;

public class NeuralNetworkTest
		extends Test {

	public NeuralNetworkTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of classify method, of class NeuralNetwork.
	 */
	public void testClassify() {
		IO.test("classify");

		// Initialize
		final NeuralNetwork model = new NeuralNetwork(2);
		final int layerCount = 2;
		// - X
		model.setFeatureVectors(new Matrix(new double[][] {
			new double[] {
				1.62434536, -0.61175641, -0.52817175
			}, new double[] {
				-1.07296862, 0.86540763, -2.30153870
			}
		})); // (n x m)
		// - Y
		model.setClasses(new Vector(1, 0, 1).transpose()); // (1 x m)
		// - W
		final Matrix[] weights = new Matrix[layerCount];
		weights[0] = new Matrix(new double[][] {
			new double[] {
				-0.00416758, -0.00056267
			}, new double[] {
				-0.02136196, 0.01640271
			}, new double[] {
				-0.01793436, -0.00841747
			}, new double[] {
				0.00502881, -0.01245288
			}
		}); // (nh x n)
		weights[1] = new Matrix(new double[][] {
			new double[] {
				-0.01057952, -0.00909008, 0.00551454, 0.02292208
			}
		}); // (1 x nh)
		model.setWeights(weights);

		// Train
		final int iterationCount = model.train(1.2, 1E-8, 10000, layerCount - 1, 4);

		// Test
		// - The accuracy
		final double accuracy = model.computeAccuracy();
		assertEquals(1., accuracy, BinaryClassifier.DEFAULT_TOLERANCE);
		// - The cost
		final double cost = model.computeCost();
		assertEquals(2.11368793E-5, cost, BinaryClassifier.DEFAULT_TOLERANCE);

		// Report the statistics
		IO.test(Doubles.toPercentage(accuracy), " accuracy in ", iterationCount, " iterations");
	}

	/**
	 * Test of classify method, of class NeuralNetwork.
	 */
	public void testClassify_File() {
		IO.test("classify_File");

		// Initialize
		Doubles.RANDOM = new Random(1L);
		final int testCount = 2;
		final double[] times = new double[testCount];

		// Test
		Matrix.start();
		try {
			// - Test the example A
			for (int t = 0; t < testCount; ++t) {
				times[t] = testExample("A", 1000, 0.1, 1, 4, ActivationFunctions.TANH, 0.9, 0.285,
						0.5);
			}
			Tests.printTimes(times);

			// - Test the example B
			for (int t = 0; t < testCount; ++t) {
				times[t] = testExample("B", 100, 0.0075, 1, 7, ActivationFunctions.RELU, 0.7, 0.65,
						0.05);
			}
			Tests.printTimes(times);
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			Matrix.stop();
		}
	}

	protected static double testExample(final String example, final int maxIterations,
			final double learningRate, final int hiddenLayerCount, final int hiddenLayerSize,
			final ActivationFunction activationFunction, final double expectedAccuracy,
			final double expectedCost, final double tolerance)
			throws IOException {
		// Initialize
		final NeuralNetwork model = new NeuralNetwork("test/resources/" + example + "/X.csv",
				"test/resources/" + example + "/Y.csv");
		model.setActivationFunction(activationFunction);
		try {
			final Matrix W1 = Matrix.load("test/resources/" + example + "/W1.csv");
			final Matrix W2 = Matrix.load("test/resources/" + example + "/W2.csv");
			final Matrix[] weights = new Matrix[] {
				W1, W2
			};
			model.setWeights(weights);
		} catch (final Exception ignored) {
		}
		final Chronometer chrono = new Chronometer();

		// Train
		chrono.start();
		final int iterationCount = model.train(learningRate, BinaryClassifier.DEFAULT_TOLERANCE,
				maxIterations, hiddenLayerCount, hiddenLayerSize);
		chrono.stop();

		// Test
		// - The accuracy
		final double accuracy = model.computeAccuracy();
		assertEquals(expectedAccuracy, accuracy, tolerance);
		// - The cost
		final double cost = model.computeCost();
		assertEquals(expectedCost, cost, tolerance);

		// Report the statistics
		IO.test(Doubles.toPercentage(accuracy), " accuracy in ", iterationCount, " iterations",
				" in ", chrono.getMilliseconds(), " [ms]");
		return chrono.getMilliseconds();
	}
}
