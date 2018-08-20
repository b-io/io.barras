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

import junit.framework.TestCase;
import jupiter.common.io.IO.SeverityLevel;
import jupiter.common.test.Tests;
import jupiter.common.time.Chronometer;
import jupiter.common.util.Doubles;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.entity.Vector;

public class NeuralNetworkTest
		extends TestCase {

	public NeuralNetworkTest() {
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of classify method, of class NeuralNetwork.
	 */
	public void testClassify() {
		IO.test("classify");

		// Initialize
		final NeuralNetwork model = new NeuralNetwork(2);
		final int nLayers = 2;
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
		final Matrix[] weights = new Matrix[nLayers];
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
		final int nIterations = model.train(1.2, 1E-8, 10000, nLayers - 1, 4);

		// Test
		// - The accuracy
		final double accuracy = model.computeAccuracy();
		IO.test(Doubles.toPercentage(accuracy), " accuracy in ", nIterations, " iterations");
		assertEquals(1., accuracy, BinaryClassifier.DEFAULT_TOLERANCE);
		// - The cost
		final double cost = model.computeCost();
		assertEquals(2.11368793E-5, cost, BinaryClassifier.DEFAULT_TOLERANCE);
	}

	/**
	 * Test of classify method, of class NeuralNetwork.
	 */
	public void testClassify_File() {
		IO.test("classify_File");

		// Set up
		Doubles.RANDOM = new Random(1L);
		IO.setSeverityLevel(SeverityLevel.TEST);
		final int nTests = 2;
		final double[] times = new double[nTests];

		// Test
		try {
			// - Test the example A
			for (int i = 0; i < nTests; ++i) {
				times[i] = testExample("A", 1, 4, 0.9, 0.285);
			}
			Tests.printTimes(times);

			// - Test the example B
			for (int i = 0; i < nTests; ++i) {
				times[i] = testExample("B", 1, 4, 0.9, 0.285);
			}
			Tests.printTimes(times);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}

	protected static long testExample(final String example, final int nHiddenLayers,
			final int hiddenLayerSize, final double expectedAccuracy, final double expectedCost)
			throws IOException {
		// Initialize
		final Chronometer chrono = new Chronometer();

		// Construct
		final NeuralNetwork model = new NeuralNetwork("test/resources/" + example + "/X.csv",
				"test/resources/" + example + "/Y.csv");

		// Train
		chrono.start();
		final int nIterations = model.train(BinaryClassifier.DEFAULT_LEARNING_RATE,
				BinaryClassifier.DEFAULT_TOLERANCE, 10000, nHiddenLayers, hiddenLayerSize);
		final long time = chrono.stop();

		// Test
		// - The accuracy
		final double accuracy = model.computeAccuracy();
		IO.test(Doubles.toPercentage(accuracy), " accuracy in ", nIterations, " iterations");
		assertEquals(expectedAccuracy, accuracy, 0.05);
		// - The cost
		final double cost = model.computeCost();
		assertEquals(expectedCost, cost, 0.05);

		return time;
	}
}
