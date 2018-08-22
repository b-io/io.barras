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

import jupiter.common.math.Maths;
import jupiter.common.test.Arguments;
import jupiter.learning.supervised.function.ActivationFunction;
import jupiter.learning.supervised.function.ActivationFunctions;
import jupiter.math.analysis.function.Functions;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.entity.Scalar;
import jupiter.math.linear.entity.Vector;

/**
 * Binary classifier using a neural network to estimate the probability of a binary response based
 * on one or more predictor (or independent) variables (features).
 */
public class NeuralNetwork
		extends BinaryClassifier {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The array of weight matrices W.
	 */
	protected Matrix[] W; // n -> nh... -> 1: (nh x n) -> (nh x nh)... -> (1 x nh)

	/**
	 * The array of bias vectors b.
	 */
	protected Vector[] b; // n -> nh... -> 1: (nh x 1) -> (nh x 1)... -> (1 x 1)

	/**
	 * The array of feature and hidden vectors A (A[l + 1] = g(Z[l + 1]) = g(W[l] A[l] + b[l])).
	 */
	protected Entity[] A; // n -> nh... -> 1: (n x m) -> (nh x m)... -> (1 x m)

	/**
	 * The activation function g.
	 */
	protected ActivationFunction activationFunction;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a neural network.
	 * <p>
	 * @param nFeatures the number of features
	 */
	public NeuralNetwork(final int nFeatures) {
		super(nFeatures);
	}

	/**
	 * Constructs a neural network from the specified files containing the feature vectors and the
	 * classes.
	 * <p>
	 * @param featureVectorsPathname the pathname of the file containing the feature vectors of size
	 *                               (n x m)
	 * @param classesPathname        the pathname of the file containing the classes of size m
	 * <p>
	 * @throws IOException if there is a problem with reading the files
	 */
	public NeuralNetwork(final String featureVectorsPathname, final String classesPathname)
			throws IOException {
		super(featureVectorsPathname, classesPathname);
	}

	/**
	 * Constructs a neural network from the specified files containing the feature vectors and the
	 * classes.
	 * <p>
	 * @param featureVectorsPathname the pathname of the file containing the feature vectors of size
	 *                               (n x m) (or (m x n) if {@code transpose})
	 * @param classesPathname        the pathname of the file containing the classes of size m
	 * @param transpose              the option specifying whether to transpose the feature vectors
	 *                               and the classes
	 * <p>
	 * @throws IOException if there is a problem with reading the files
	 */
	public NeuralNetwork(final String featureVectorsPathname, final String classesPathname,
			final boolean transpose)
			throws IOException {
		super(featureVectorsPathname, classesPathname, transpose);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS && SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public synchronized Matrix[] getWeights() {
		return W;
	}

	public synchronized Vector[] getBias() {
		return b;
	}

	public synchronized ActivationFunction getActivationFunction() {
		return activationFunction;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public synchronized void setWeights(final Matrix[] weights) {
		// Check the arguments
		Arguments.require(weights[0].getColumnDimension(), nFeatures);

		// Set the weights
		W = weights;
	}

	public synchronized void setBias(final Vector[] bias) {
		// Check the arguments
		Arguments.require(bias[0].getColumnDimension(), 1);

		// Set the bias
		b = bias;
	}

	public synchronized void setActivationFunction(final ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MODELER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Trains the model with the specified parameters and returns the number of iterations.
	 * <p>
	 * @param learningRate  the learning rate
	 * @param tolerance     the tolerance level
	 * @param maxIterations the maximum number of iterations
	 * <p>
	 * @return the number of iterations
	 */
	@Override
	public synchronized int train(final double learningRate, final double tolerance,
			final int maxIterations) {
		final int nHiddenLayers = nFeatures;
		final int hiddenLayerSize = nFeatures * nFeatures;
		return train(learningRate, tolerance, maxIterations, nHiddenLayers, hiddenLayerSize);
	}

	/**
	 * Trains the model with the specified parameters and returns the number of iterations.
	 * <p>
	 * @param learningRate    the learning rate
	 * @param tolerance       the tolerance level
	 * @param maxIterations   the maximum number of iterations
	 * @param nHiddenLayers   the number of hidden layers
	 * @param hiddenLayerSize the size of the hidden layers nh
	 * <p>
	 * @return the number of iterations
	 */
	public synchronized int train(final double learningRate, final double tolerance,
			final int maxIterations, final int nHiddenLayers, final int hiddenLayerSize) {
		if (mTrainingExamples == 0) {
			IO.error("No training examples found");
			return 0;
		}

		// Initialize
		final Scalar alpha = new Scalar(learningRate);
		final int nLayers = nHiddenLayers + 1; // L
		// - The weight matrices
		if (W == null) {
			W = new Matrix[nLayers];
			W[0] = Matrix.random(hiddenLayerSize, nFeatures).subtract(0.5).rightDivide(50.); // (nh x n)
			for (int i = 1; i < nLayers - 1; ++i) {
				W[i] = Matrix.random(hiddenLayerSize, hiddenLayerSize).subtract(0.5)
						.rightDivide(50.); // (nh x nh)
			}
			W[nLayers - 1] = Matrix.random(1, hiddenLayerSize).subtract(0.5).rightDivide(50.); // (1 x nh)
		}
		// - The bias vectors
		if (b == null) {
			b = new Vector[nLayers];
			for (int l = 0; l < nLayers - 1; ++l) {
				b[l] = new Vector(hiddenLayerSize); // (nh x 1)
			}
			b[nLayers - 1] = new Vector(1); // (1 x 1)
		}
		// - The feature and hidden vectors
		A = new Matrix[nLayers + 1];
		A[0] = X; // (n x m)
		// - The activation function
		if (activationFunction == null) {
			activationFunction = ActivationFunctions.TANH;
		}
		// - The frequency of the convergence test
		final int convergenceTestFrequency = Math.max(MIN_CONVERGENCE_TEST_FREQUENCY,
				Maths.roundToInt(1. / learningRate));
		// - The cost
		double j = Double.POSITIVE_INFINITY;
		// - The derivative with respect to Z
		Entity dZ = null;
		// - The derivative with respect to A
		Matrix dA = null;

		// Train
		for (int i = 0; i < maxIterations; ++i) {
			// Perform the forward propagation step (n -> nh... -> 1)
			for (int l = 0; l < nLayers - 1; ++l) {
				// - Compute A[l + 1] = g(Z[l + 1]) = g(W[l] A[l] + b[l])
				A[l + 1] = computeForward(l).apply(activationFunction); // (nh x m)
			}
			// - Compute A[L + 1] = sigmoid(Z[L + 1]) = sigmoid(W[L] A[L] + b[L])
			A[nLayers] = computeForward(nLayers - 1).apply(Functions.SIGMOID); // (1 x m)

			// Test the convergence
			if (i % convergenceTestFrequency == 0) {
				// - Compute the cost
				final double cost = computeCost();
				IO.debug(i, ") Cost: ", cost);
				final double delta = Maths.delta(j, cost);
				IO.debug(i, ") Delta: ", delta);
				j = cost;

				// - Test whether the tolerance level is reached
				if (delta <= tolerance || j <= tolerance) {
					return i;
				}
			}

			// Perform the backward propagation step (n <- nh... <- 1)
			for (int l = nLayers - 1; l >= 0; --l) {
				// - Compute the derivative with respect to Z
				if (l == nLayers - 1) {
					dZ = A[l + 1].minus(Y); // (1 x m)
				} else {
					dZ = dA.arrayTimes(activationFunction.derive(A[l + 1]).toMatrix()); // (nh x m)
				}
				final Entity dZT = dZ.transpose(); // (m x nh) <- (m x nh)... <- (m x 1)
				dA = W[l].transpose().times(dZ).toMatrix(); // (n x m) <- (nh x m)... <- (nh x m)

				// - Compute the derivatives with respect to W and b
				final Entity dW = A[l].times(dZT).division(new Scalar(mTrainingExamples))
						.transpose(); // (nh x n) <- (nh x nh)... <- (1 x nh)
				final Entity db = dZT.mean();

				// - Update the weights and the bias
				W[l] = W[l].minus(alpha.times(dW)); // (nh x n) <- (nh x nh)... <- (1 x nh)
				b[l] = b[l].minus(alpha.times(db)).toVector(); // (nh x 1) <- (nh x 1)... <- (1 x 1)
			}
		}

		return maxIterations;
	}

	/**
	 * Returns the result of {@code Z[l + 1] = W[l] A[l] + b[l]} for the specified layer.
	 * <p>
	 * @param l the layer to compute
	 * <p>
	 * @return the result of {@code Z[l + 1] = W[l] A[l] + b[l]} for the specified layer
	 */
	protected Entity computeForward(final int l) {
		return computeForward(l, A[l]);
	}

	/**
	 * Returns the result of {@code Z[l + 1] = W[l] A + b[l]} for the specified layer and
	 * {@link Entity}.
	 * <p>
	 * @param l the layer to compute
	 * @param A an {@link Entity}
	 * <p>
	 * @return the result of {@code Z[l + 1] = W[l] A + b[l]} for the specified layer and
	 *         {@link Entity}
	 */
	protected Entity computeForward(final int l, final Entity A) {
		return W[l].times(A).plus(b[l]); // n -> nh... -> 1: (nh x n) (n x m) + (nh x 1) ->
		// (nh x nh) (nh x m) + (nh x 1)... -> (1 x nh) (nh x m) + (1 x 1)
	}

	/**
	 * Computes the cost.
	 * <p>
	 * @return the cost
	 */
	@Override
	public synchronized double computeCost() {
		return computeCost(A[A.length - 1]);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSIFIER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the estimated probability of the binary response for all feature vector in {@code X}.
	 * <p>
	 * @param X the feature vectors of size (n x m)
	 * <p>
	 * @return the estimated probability of the binary response for all feature vector in {@code X}
	 */
	@Override
	public synchronized Entity estimate(final Entity X) {
		// Check the arguments
		Arguments.requireEquals(W.length, b.length);

		// Estimate the binary response
		final int nLayers = W.length; // or b.length
		Entity estimate = X; // (n x m)
		for (int l = 0; l < nLayers - 1; ++l) {
			estimate = computeForward(l, estimate).apply(activationFunction); // (nh x m)
		}
		return computeForward(nLayers - 1, estimate).apply(Functions.SIGMOID); // (1 x m)
	}
}
