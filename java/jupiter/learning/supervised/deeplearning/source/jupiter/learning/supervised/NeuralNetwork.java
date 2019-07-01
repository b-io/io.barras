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
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;
import jupiter.learning.supervised.function.ActivationFunction;
import jupiter.learning.supervised.function.ActivationFunctions;
import jupiter.learning.supervised.function.RegularizationFunction;
import jupiter.learning.supervised.function.RegularizationFunctions;
import jupiter.math.analysis.function.Functions;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.entity.Vector;

/**
 * {@link NeuralNetwork} is the {@link BinaryClassifier} using a neural network to estimate the
 * probability of a binary response based on one or more predictor (or independent) variables
 * (features).
 */
public class NeuralNetwork
		extends BinaryClassifier {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The array of {@link Matrix} W containing the weights.
	 */
	protected Matrix[] W; // n -> nh... -> 1: (nh x n) -> (nh x nh)... -> (1 x nh)
	/**
	 * The array of {@link Vector} b containing the bias.
	 */
	protected Vector[] b; // n -> nh... -> 1: (nh x 1) -> (nh x 1)... -> (1 x 1)
	/**
	 * The array of {@link Matrix} A containing the feature vectors, hidden vectors and Y estimates
	 * (A[l + 1] = g(Z[l + 1]) = g(W[l] A[l] + b[l])).
	 */
	protected Entity[] A; // n -> nh... -> 1: (n x m) -> (nh x m)... -> (1 x m)

	/**
	 * The {@link ActivationFunction} g.
	 */
	protected ActivationFunction activationFunction;
	/**
	 * The {@link RegularizationFunction} r.
	 */
	protected RegularizationFunction regularizationFunction;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link NeuralNetwork}.
	 * <p>
	 * @param featureCount the number of features
	 */
	public NeuralNetwork(final int featureCount) {
		super(featureCount);
		setDefaultFunctions();
	}

	/**
	 * Constructs a {@link NeuralNetwork} with the specified files containing the feature vectors
	 * and classes.
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size (n x m)
	 * @param classesPath        the path to the file containing the classes of size m
	 * <p>
	 * @throws IOException if there is a problem with reading the specified files
	 */
	public NeuralNetwork(final String featureVectorsPath, final String classesPath)
			throws IOException {
		super(featureVectorsPath, classesPath);
		setDefaultFunctions();
	}

	/**
	 * Constructs a {@link NeuralNetwork} with the specified files containing the feature vectors
	 * and classes.
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size (n x m)
	 *                           (or (m x n) if {@code transpose})
	 * @param classesPath        the path to the file containing the classes of size m
	 * @param transpose          the flag specifying whether to transpose the feature vectors and
	 *                           classes
	 * <p>
	 * @throws IOException if there is a problem with reading the specified files
	 */
	public NeuralNetwork(final String featureVectorsPath, final String classesPath,
			final boolean transpose)
			throws IOException {
		super(featureVectorsPath, classesPath, transpose);
		setDefaultFunctions();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
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

	public synchronized RegularizationFunction getRegularizationFunction() {
		return regularizationFunction;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public synchronized void setWeights(final Matrix[] weights) {
		// Check the arguments
		if (weights != null) {
			Arguments.require(weights[0].getColumnDimension(), featureCount);
		}

		// Set the weights
		try {
			W = Objects.clone(weights);
		} catch (final CloneNotSupportedException ex) {
			throw new RuntimeException(Strings.toString(ex), ex);
		}
	}

	public synchronized void setBias(final Vector[] bias) {
		// Check the arguments
		if (bias != null) {
			Arguments.require(bias[0].getColumnDimension(), 1);
		}

		// Set the bias
		try {
			b = Objects.clone(bias);
		} catch (final CloneNotSupportedException ex) {
			throw new RuntimeException(Strings.toString(ex), ex);
		}
	}

	public synchronized void setActivationFunction(final ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}

	public synchronized void setRegularizationFunction(
			final RegularizationFunction regularizationFunction) {
		this.regularizationFunction = regularizationFunction;
	}

	protected void setDefaultFunctions() {
		activationFunction = ActivationFunctions.TANH;
		regularizationFunction = RegularizationFunctions.L2;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MODELER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Trains the model with the specified hyper-parameters and returns the number of iterations.
	 * <p>
	 * @param learningRate                     the learning rate
	 * @param firstMomentExponentialDecayRate  the first-moment exponential decay rate
	 * @param secondMomentExponentialDecayRate the second-moment exponential decay rate
	 * @param tolerance                        the tolerance level
	 * @param maxIterationCount                the maximum number of iterations
	 * <p>
	 * @return the number of iterations
	 */
	@Override
	public synchronized int train(final double learningRate,
			final double firstMomentExponentialDecayRate,
			final double secondMomentExponentialDecayRate,
			final double tolerance,
			final int maxIterationCount) {
		final int hiddenLayerCount;
		final int hiddenLayerSize;
		if (W == null) {
			hiddenLayerCount = featureCount;
			hiddenLayerSize = featureCount * featureCount;
		} else {
			hiddenLayerCount = W.length;
			hiddenLayerSize = 0;
		}
		return train(learningRate, firstMomentExponentialDecayRate,
				secondMomentExponentialDecayRate, tolerance, maxIterationCount, hiddenLayerCount,
				hiddenLayerSize);
	}

	/**
	 * Trains the model with the specified hyper-parameters and returns the number of iterations.
	 * <p>
	 * @param learningRate      the learning rate
	 * @param tolerance         the tolerance level
	 * @param maxIterationCount the maximum number of iterations
	 * @param hiddenLayerCount  the number of hidden layers
	 * @param hiddenLayerSize   the size of the hidden layers
	 * <p>
	 * @return the number of iterations
	 */
	public synchronized int train(final double learningRate,
			final double tolerance,
			final int maxIterationCount,
			final int hiddenLayerCount,
			final int hiddenLayerSize) {
		return train(learningRate, DEFAULT_FIRST_MOMENT_EXPONENTIAL_DECAY_RATE,
				DEFAULT_SECOND_MOMENT_EXPONENTIAL_DECAY_RATE, tolerance, maxIterationCount,
				hiddenLayerCount, hiddenLayerSize);
	}

	/**
	 * Trains the model with the specified hyper-parameters and returns the number of iterations.
	 * <p>
	 * @param learningRate                     the learning rate
	 * @param firstMomentExponentialDecayRate  the first-moment exponential decay rate
	 * @param secondMomentExponentialDecayRate the second-moment exponential decay rate
	 * @param tolerance                        the tolerance level
	 * @param maxIterationCount                the maximum number of iterations
	 * @param hiddenLayerCount                 the number of hidden layers
	 * @param hiddenLayerSize                  the size of the hidden layers
	 * <p>
	 * @return the number of iterations
	 */
	public synchronized int train(final double learningRate,
			final double firstMomentExponentialDecayRate,
			final double secondMomentExponentialDecayRate,
			final double tolerance,
			final int maxIterationCount,
			final int hiddenLayerCount,
			final int hiddenLayerSize) {
		if (trainingExampleCount == 0) {
			IO.error("No training examples found");
			return 0;
		}

		// Parallelize
		Matrix.parallelize();

		// Initialize
		final int layerCount = hiddenLayerCount + 1; // L
		// - The weight matrices
		if (W == null) {
			W = new Matrix[layerCount];
			W[0] = Matrix.random(hiddenLayerSize, featureCount)
					.subtract(0.5)
					.multiply(Math.sqrt(2. / featureCount)); // (nh x n)
			final double scalingFactor = Math.sqrt(2. / hiddenLayerSize);
			for (int l = 1; l < layerCount - 1; ++l) {
				W[l] = Matrix.random(hiddenLayerSize, hiddenLayerSize)
						.subtract(0.5)
						.multiply(scalingFactor); // (nh x nh)
			}
			W[layerCount - 1] = Matrix.random(1, hiddenLayerSize)
					.subtract(0.5)
					.multiply(scalingFactor); // (1 x nh)
		}
		// - The bias vectors
		if (b == null) {
			b = new Vector[layerCount];
			for (int l = 0; l < layerCount - 1; ++l) {
				b[l] = new Vector(W[l].getRowDimension()); // (nh x 1)
			}
			b[layerCount - 1] = new Vector(1); // (1 x 1)
		}
		// - The feature and hidden vectors
		A = new Matrix[layerCount + 1];
		A[0] = X; // (n x m)
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
		for (int i = 0; i < maxIterationCount; ++i) {
			// Perform the forward propagation step (n -> nh... -> 1)
			for (int l = 0; l < layerCount - 1; ++l) {
				// - Compute A[l + 1] = g(Z[l + 1]) = g(W[l] A[l] + b[l])
				A[l + 1] = computeForward(l).apply(activationFunction); // (nh x m)
			}
			// - Compute A[L + 1] = sigmoid(Z[L + 1]) = sigmoid(W[L] A[L] + b[L])
			A[layerCount] = computeForward(layerCount - 1).apply(Functions.SIGMOID); // (1 x m)

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
					IO.debug("Stop training after ", i, " iterations and with ", j, " cost");
					return i;
				}
			}

			// Perform the backward propagation step (n <- nh... <- 1)
			for (int l = layerCount - 1; l >= 0; --l) {
				// - Compute the derivative with respect to Z
				if (l == layerCount - 1) {
					dZ = A[l + 1].minus(Y); // (1 x m)
				} else {
					dZ = dA.arrayMultiply(activationFunction.derive(A[l + 1]).toMatrix()); // (nh x m)
				}
				dA = W[l].transpose().times(dZ).toMatrix(); // (n x m) <- (nh x m)... <- (nh x m)
				final Entity dZT = dZ.transpose(); // (m x nh) <- (m x nh)... <- (m x 1)

				// - Compute the derivatives with respect to W and b
				final Matrix dW = A[l].times(dZT)
						.transpose()
						.divide(trainingExampleCount)
						.add(regularizationFunction.derive(trainingExampleCount, W[l]))
						.toMatrix(); // (nh x n) <- (nh x nh)... <- (1 x nh)
				final Vector db = dZT.mean().toVector();

				// - Update the weights and bias
				W[l].subtract(dW.multiply(learningRate)); // (nh x n) <- (nh x nh)... <- (1 x nh)
				b[l].subtract(db.multiply(learningRate)); // (nh x 1) <- (nh x 1)... <- (1 x 1)
			}
		}
		IO.debug("Stop training after ", maxIterationCount, " iterations and with ", j, " cost");
		return maxIterationCount;
	}

	/**
	 * Returns the result of {@code Z[l + 1] = W[l] A[l] + b[l]} for the specified layer.
	 * <p>
	 * @param layer the layer to compute
	 * <p>
	 * @return the result of {@code Z[l + 1] = W[l] A[l] + b[l]} for the specified layer
	 */
	protected Entity computeForward(final int layer) {
		return computeForward(layer, A[layer]);
	}

	/**
	 * Returns the result of {@code Z[l + 1] = W[l] A + b[l]} for the specified layer and
	 * {@link Entity}.
	 * <p>
	 * @param layer the layer to compute
	 * @param A     an {@link Entity}
	 * <p>
	 * @return the result of {@code Z[l + 1] = W[l] A + b[l]} for the specified layer and
	 *         {@link Entity}
	 */
	protected Entity computeForward(final int layer, final Entity A) {
		return W[layer].forward(A, b[layer]); // n -> nh... -> 1: (nh x n) (n x m) + (nh x 1) ->
		// (nh x nh) (nh x m) + (nh x 1)... -> (1 x nh) (nh x m) + (1 x 1)
	}

	/**
	 * Computes the cost.
	 * <p>
	 * @return the cost
	 */
	@Override
	public synchronized double computeCost() {
		return computeCost(A[A.length - 1]) +
				regularizationFunction.computeCost(trainingExampleCount, W);
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
		final int layerCount = W.length; // or b.length
		Entity estimate = X; // (n x m)
		for (int l = 0; l < layerCount - 1; ++l) {
			estimate = computeForward(l, estimate).apply(activationFunction); // (nh x m)
		}
		return computeForward(layerCount - 1, estimate).apply(Functions.SIGMOID); // (1 x m)
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	public NeuralNetwork clone() {
		try {
			final NeuralNetwork clone = (NeuralNetwork) super.clone();
			clone.W = Objects.clone(W);
			clone.b = Objects.clone(b);
			clone.A = Objects.clone(A);
			clone.activationFunction = Objects.clone(activationFunction);
			clone.regularizationFunction = Objects.clone(regularizationFunction);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new RuntimeException(Strings.toString(ex), ex);
		}
	}
}
