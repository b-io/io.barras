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
package jupiter.learning.supervised;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import java.io.IOException;
import java.util.Random;

import jupiter.common.test.Test;
import jupiter.common.test.Tests;
import jupiter.common.time.Chronometer;
import jupiter.common.util.Doubles;
import jupiter.common.util.Strings;
import jupiter.learning.supervised.function.ActivationFunction;
import jupiter.learning.supervised.function.ActivationFunctions;
import jupiter.learning.supervised.function.RegularizationFunction;
import jupiter.learning.supervised.function.RegularizationFunctions;
import jupiter.learning.supervised.function.RegularizationL2;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.entity.Vector;

public class NeuralNetworkTest
		extends Test {

	public NeuralNetworkTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link NeuralNetwork#train}.
	 */
	public void testTrain() {
		IO.test(BULLET, "train");

		// Initialize
		final int testCount = 2;
		final double[] times = new double[testCount];
		final int featureCount = 2;
		final int hiddenLayerCount = featureCount - 1;
		final int layerCount = hiddenLayerCount + 1; // L
		final int hiddenLayerSize = featureCount * featureCount;
		final NeuralNetwork model = new NeuralNetwork(featureCount);
		model.setActivationFunction(ActivationFunctions.TANH);
		model.setRegularizationFunction(RegularizationFunctions.NONE);
		// • X
		final Matrix featureVectors = new Matrix(new double[][] {
			new double[] {1.62434536, -0.61175641, -0.52817175},
			new double[] {-1.07296862, 0.86540763, -2.30153870}});
		// • Y
		final Vector classes = new Vector(1, 0, 1).transpose();
		// • W
		final Matrix[] weights = new Matrix[layerCount];
		weights[0] = new Matrix(new double[][] {
			new double[] {-0.00416758, -0.00056267},
			new double[] {-0.02136196, 0.01640271},
			new double[] {-0.01793436, -0.00841747},
			new double[] {0.00502881, -0.01245288}}); // (nh x n)
		weights[1] = new Matrix(new double[][] {
			new double[] {-0.01057952, -0.00909008, 0.00551454, 0.02292208}}); // (1 x nh)
		final Chronometer chrono = new Chronometer();

		for (int ti = 0; ti < testCount; ++ti) {
			model.setFeatureVectors(featureVectors); // (n x m)
			model.setClasses(classes); // (1 x m)
			model.setWeights(weights);
			model.setBias(null);

			// Train the model
			chrono.start();
			final int iterationCount = model.train(1.2, 1E-8, 10000, hiddenLayerCount,
					hiddenLayerSize);
			times[ti] = chrono.stop();

			// Report the statistics
			final double accuracy = model.computeAccuracy();
			final double f1Score = model.computeF1Score();
			final double cost = model.computeCost();
			IO.test(Doubles.formatPercent(accuracy), "accuracy,",
					Doubles.formatPercent(f1Score), "F1 score and",
					cost, "cost in",
					iterationCount, "iterations in",
					chrono.getMilliseconds(), "[ms]");

			// Verify the model
			assertEquals(1., accuracy, Classifier.DEFAULT_TOLERANCE);
			assertEquals(1., f1Score, Classifier.DEFAULT_TOLERANCE);
			assertEquals(0., cost, Classifier.DEFAULT_TOLERANCE);
		}
		Tests.printTimes(times);
	}

	/**
	 * Tests {@link NeuralNetwork#train} importing feature vectors and classes from files.
	 * <p>
	 * @throws IOException if there is a problem with reading the files
	 */
	public void testTrainFromFiles()
			throws IOException {
		IO.test(BULLET, "train from files");

		// Initialize
		Doubles.RANDOM = new Random(1L);
		final int testCount = 2;
		final double[] times = new double[testCount];

		IO.test("A) Test the activation function TANH");
		for (int ti = 0; ti < testCount; ++ti) {
			times[ti] = testExample("A", 1000, 0.1, 1, 4, ActivationFunctions.TANH,
					RegularizationFunctions.NONE, 0.75, 0.75, 0.5, 0.25);
		}
		Tests.printTimes(times);

		IO.test("B) Test the activation function RELU");
		testExample("B", 200, 0.075, 1, 0, ActivationFunctions.RELU, new RegularizationL2(0.9),
				0.75, 0.75, 0.5, 0.25);

		IO.test("C) Test the L2 regularization");
		for (int ti = 0; ti < testCount; ++ti) {
			times[ti] = testExample("C", 100, 0.1, 2, 0, ActivationFunctions.RELU,
					new RegularizationL2(0.9), 0.75, 0.75, 0.25, 0.25);
		}
		Tests.printTimes(times);

		IO.test("D) Test the Adam optimization");
		for (int ti = 0; ti < testCount; ++ti) {
			times[ti] = testExample("D", 1000, 0.9, 0.9, 0.999, 1, 4, ActivationFunctions.RELU,
					new RegularizationL2(0.9), 0.75, 0.75, 0.25, 0.25);
		}
		Tests.printTimes(times);

		IO.test("E) Test the last activation function SOFTMAX");
		for (int ti = 0; ti < testCount; ++ti) {
			times[ti] = testExample("E", 1000, 0.9, 1, 4, ActivationFunctions.RELU,
					new RegularizationL2(0.9), 0.75, 0.75, 0.25, 0.25);
		}
		Tests.printTimes(times);
	}

	protected static double testExample(final String example, final int maxIterationCount,
			final double learningRate,
			final int hiddenLayerCount, final int hiddenLayerSize,
			final ActivationFunction activationFunction,
			final RegularizationFunction regularizationFunction,
			final double expectedAccuracy, final double expectedF1Score, final double expectedCost,
			final double tolerance)
			throws IOException {
		return testExample(example, maxIterationCount,
				learningRate,
				Double.NaN,
				Double.NaN,
				hiddenLayerCount, hiddenLayerSize,
				activationFunction,
				regularizationFunction,
				expectedAccuracy, expectedF1Score, expectedCost,
				tolerance);
	}

	protected static double testExample(final String example, final int maxIterationCount,
			final double learningRate,
			final double firstMomentExponentialDecayRate,
			final double secondMomentExponentialDecayRate,
			final int hiddenLayerCount, final int hiddenLayerSize,
			final ActivationFunction activationFunction,
			final RegularizationFunction regularizationFunction,
			final double expectedAccuracy, final double expectedF1Score, final double expectedCost,
			final double tolerance)
			throws IOException {
		// Initialize
		final NeuralNetwork model = new NeuralNetwork("test/resources/" + example + "/X.csv",
				"test/resources/" + example + "/Y.csv");
		model.setActivationFunction(activationFunction);
		model.setRegularizationFunction(regularizationFunction);
		try {
			final int layerCount = hiddenLayerCount + 1; // L
			final Matrix[] weights = new Matrix[layerCount];
			for (int li = 0; li < layerCount; ++li) {
				weights[li] = Matrix.load(
						Strings.join("test/resources/", example, "/W", li + 1, ".csv"));
				IO.test("W", li + 1, "loaded");
			}
			model.setWeights(weights);
		} catch (final IOException ignored) {
			IO.warn("No weight files; the initial weights will be randomly generated");
		}
		final Chronometer chrono = new Chronometer();

		// Train the model
		chrono.start();
		final int iterationCount = model.train(learningRate, firstMomentExponentialDecayRate,
				secondMomentExponentialDecayRate, Classifier.DEFAULT_TOLERANCE, maxIterationCount,
				hiddenLayerCount, hiddenLayerSize);
		chrono.stop();

		// Report the statistics
		final double accuracy = model.computeAccuracy();
		final double f1Score = model.computeF1Score();
		final double cost = model.computeCost();
		IO.test(Doubles.formatPercent(accuracy), "accuracy,",
				Doubles.formatPercent(f1Score), "F1 score and",
				cost, "cost in",
				iterationCount, "iterations in",
				chrono.getMilliseconds(), "[ms]");

		// Verify the model
		assertEquals(expectedAccuracy, accuracy, tolerance);
		assertEquals(expectedF1Score, f1Score, tolerance);
		assertEquals(expectedCost, cost, tolerance);
		return chrono.getMilliseconds();
	}
}
