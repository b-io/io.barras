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
package jupiter.learning.supervised;

import static jupiter.common.io.IO.IO;

import java.io.IOException;

import jupiter.common.math.Maths;
import jupiter.common.test.Arguments;
import jupiter.math.analysis.function.Functions;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Scalar;
import jupiter.math.linear.entity.Vector;

/**
 * Binary classifier using the logistic model to estimate the probability of a binary response based
 * on one or more predictor (or independent) variables (features).
 */
public class LogisticRegression
		extends BinaryClassifier {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The weight vector W.
	 */
	protected Vector W; // (1 x n)

	/**
	 * The bias b.
	 */
	protected Scalar b;

	/**
	 * The hidden vector A.
	 */
	protected Entity A;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a logistic regression model.
	 * <p>
	 * @param featureCount the number of features
	 */
	public LogisticRegression(final int featureCount) {
		super(featureCount);
	}

	/**
	 * Constructs a logistic regression model from the specified files containing the feature
	 * vectors and the classes.
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size (n x m)
	 * @param classesPath        the path to the file containing the classes of size m
	 * <p>
	 * @throws IOException if there is a problem with reading the specified files
	 */
	public LogisticRegression(final String featureVectorsPath, final String classesPath)
			throws IOException {
		super(featureVectorsPath, classesPath);
	}

	/**
	 * Constructs a logistic regression model from the specified files containing the feature
	 * vectors and the classes.
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size (n x m)
	 *                           (or (m x n) if {@code transpose})
	 * @param classesPath        the path to the file containing the classes of size m
	 * @param transpose          the flag specifying whether to transpose the feature vectors and
	 *                           the classes
	 * <p>
	 * @throws IOException if there is a problem with reading the specified files
	 */
	public LogisticRegression(final String featureVectorsPath, final String classesPath,
			final boolean transpose)
			throws IOException {
		super(featureVectorsPath, classesPath, transpose);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public synchronized Vector getWeights() {
		return W;
	}

	public synchronized Scalar getBias() {
		return b;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public synchronized void setWeights(final Vector weights) {
		// Check the arguments
		Arguments.require(weights.getColumnDimension(), featureCount);

		// Set the weights
		W = weights;
	}

	public synchronized void setBias(final Scalar bias) {
		// Check the arguments
		Arguments.requireNonNull(bias);

		// Set the bias
		b = bias;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MODELER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Trains the model with the specified parameters and returns the number of iterations.
	 * <p>
	 * @param learningRate      the learning rate
	 * @param tolerance         the tolerance level
	 * @param maxIterationCount the maximum number of iterations
	 * <p>
	 * @return the number of iterations
	 */
	@Override
	public synchronized int train(final double learningRate, final double tolerance,
			final int maxIterationCount) {
		if (trainingExampleCount == 0) {
			IO.error("No training examples found");
			return 0;
		}

		// Initialize
		// - The weight vector
		if (W == null) {
			W = new Vector(featureCount, true); // (1 x n)
		}
		// - The bias
		if (b == null) {
			b = new Scalar(0.);
		}
		// - The frequency of the convergence test
		final int convergenceTestFrequency = Math.max(MIN_CONVERGENCE_TEST_FREQUENCY,
				Maths.roundToInt(1. / learningRate));
		// - The cost
		double j = Double.POSITIVE_INFINITY;

		// Train
		for (int i = 0; i < maxIterationCount; ++i) {
			// Compute A = sigmoid(Z) = sigmoid(W X + b)
			A = estimate(X); // (1 x m)

			// Test the convergence
			if (i % convergenceTestFrequency == 0) {
				// - Compute the cost
				final double cost = computeCost();
				final double delta = Maths.delta(j, cost);
				j = cost;

				// - Test whether the tolerance level is reached
				if (delta <= tolerance || j <= tolerance) {
					return i;
				}
			}

			// Compute the derivatives
			final Entity dZT = A.minus(Y).transpose(); // (m x 1)
			final Entity dW = X.times(dZT).divide(trainingExampleCount).transpose(); // (1 x n)
			final Scalar db = dZT.mean().toScalar();

			// Update the weights and the bias
			W.subtract(dW.multiply(learningRate)).toVector(); // (1 x n)
			b.subtract(db.multiply(learningRate)).toScalar();
		}

		return maxIterationCount;
	}

	/**
	 * Computes the cost.
	 * <p>
	 * @return the cost
	 */
	@Override
	public synchronized double computeCost() {
		return computeCost(A);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSIFIER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the estimated probability of the binary response for all feature vector in {@code X}.
	 * Computes the sigmoid of {@code Z = W X + b}.
	 * <p>
	 * @param X the feature vectors of size (n x m)
	 * <p>
	 * @return the estimated probability of the binary response for all feature vector in {@code X}
	 */
	@Override
	public synchronized Entity estimate(final Entity X) {
		return W.times(X).add(b).apply(Functions.SIGMOID); // (1 x m)
	}
}
