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

import java.io.IOException;
import java.io.Serializable;

import jupiter.common.model.ICloneable;
import jupiter.common.test.Arguments;
import jupiter.common.util.Integers;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;
import jupiter.math.analysis.function.Functions;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.entity.Scalar;
import jupiter.math.linear.entity.Vector;

public abstract class BinaryClassifier
		implements ICloneable<BinaryClassifier>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The default learning rate α.
	 */
	protected static final double DEFAULT_LEARNING_RATE = 0.1;

	/**
	 * The default exponential decay rate for the first-moment estimates β1 (Adam algorithm).
	 */
	protected static final double DEFAULT_FIRST_MOMENT_EXPONENTIAL_DECAY_RATE = 0.9;

	/**
	 * The default exponential decay rate for the second-moment estimates β2 (Adam algorithm).
	 */
	protected static final double DEFAULT_SECOND_MOMENT_EXPONENTIAL_DECAY_RATE = 0.999;

	/**
	 * The default tolerance level (or termination criterion) ε.
	 */
	protected static final double DEFAULT_TOLERANCE = 1E-6;

	/**
	 * The default maximum number of iterations.
	 */
	protected static final int DEFAULT_MAX_ITERATIONS = Integers.convert(1E6);

	/**
	 * The minimum convergence test frequency.
	 */
	public static volatile int MIN_CONVERGENCE_TEST_FREQUENCY = 10;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The number of features n.
	 */
	protected final int featureCount;

	/**
	 * The number of training examples m.
	 */
	protected int trainingExampleCount;

	/**
	 * The matrix of feature vectors X.
	 */
	protected Matrix X; // (n x m)

	/**
	 * The vector of classes Y.
	 */
	protected Vector Y, YT; // (1 x m), (m x 1)


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a binary classifier.
	 * <p>
	 * @param featureCount the number of features
	 */
	protected BinaryClassifier(final int featureCount) {
		this.featureCount = featureCount;
		trainingExampleCount = 0;
	}

	/**
	 * Constructs a binary classifier with the specified files containing the feature vectors and
	 * the classes.
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size (n x m)
	 * @param classesPath        the path to the file containing the classes of size m
	 * <p>
	 * @throws IOException if there is a problem with reading the specified files
	 */
	protected BinaryClassifier(final String featureVectorsPath, final String classesPath)
			throws IOException {
		X = Matrix.create(featureVectorsPath);
		featureCount = X.getRowDimension();
		trainingExampleCount = X.getColumnDimension();
		Y = Matrix.create(classesPath).toVector();
		Arguments.requireEquals(Y.getColumnDimension(), trainingExampleCount);
		YT = Y.transpose();
	}

	/**
	 * Constructs a binary classifier with the specified files containing the feature vectors and
	 * the classes.
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size (n x m)
	 *                           (or (m x n) if {@code transpose})
	 * @param classesPath        the path to the file containing the classes of size m
	 * @param transpose          the flag specifying whether to transpose the feature vectors and
	 *                           the classes
	 * <p>
	 * @throws IOException if there is a problem with reading the specified files
	 */
	protected BinaryClassifier(final String featureVectorsPath, final String classesPath,
			final boolean transpose)
			throws IOException {
		X = Matrix.create(featureVectorsPath, transpose);
		featureCount = X.getRowDimension();
		trainingExampleCount = X.getColumnDimension();
		Y = Matrix.create(classesPath, transpose).toVector();
		Arguments.requireEquals(Y.getColumnDimension(), trainingExampleCount);
		YT = Y.transpose();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public synchronized Matrix getFeatureVectors() {
		return X;
	}

	public synchronized Vector getClasses() {
		return Y;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public synchronized void setFeatureVectors(final Matrix featureVectors) {
		// Check the arguments
		Arguments.require(featureVectors.getRowDimension(), featureCount);

		// Set the feature vectors
		X = featureVectors;
		trainingExampleCount = featureVectors.getColumnDimension();
	}

	public synchronized void setClasses(final Vector classes) {
		// Check the arguments
		Arguments.require(classes.getColumnDimension(), trainingExampleCount);

		// Set the classes
		Y = classes;
		YT = classes.transpose();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MODELER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Trains the model and returns the number of iterations.
	 * <p>
	 * @return the number of iterations
	 */
	public synchronized int train() {
		return train(DEFAULT_LEARNING_RATE, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS);
	}

	/**
	 * Trains the model with the specified parameters and returns the number of iterations.
	 * <p>
	 * @param learningRate      the learning rate
	 * @param tolerance         the tolerance level
	 * @param maxIterationCount the maximum number of iterations
	 * <p>
	 * @return the number of iterations
	 */
	public synchronized int train(final double learningRate, final double tolerance,
			final int maxIterationCount) {
		return train(learningRate, DEFAULT_FIRST_MOMENT_EXPONENTIAL_DECAY_RATE,
				DEFAULT_SECOND_MOMENT_EXPONENTIAL_DECAY_RATE, tolerance, maxIterationCount);
	}

	/**
	 * Trains the model with the specified parameters and returns the number of iterations.
	 * <p>
	 * @param learningRate                     the learning rate
	 * @param firstMomentExponentialDecayRate  the first-moment exponential decay rate
	 * @param secondMomentExponentialDecayRate the second-moment exponential decay rate
	 * @param tolerance                        the tolerance level
	 * @param maxIterationCount                the maximum number of iterations
	 * <p>
	 * @return the number of iterations
	 */
	public abstract int train(final double learningRate,
			final double firstMomentExponentialDecayRate,
			final double secondMomentExponentialDecayRate,
			final double tolerance,
			final int maxIterationCount);

	/**
	 * Computes the cost.
	 * <p>
	 * @return the cost
	 */
	public abstract double computeCost();

	/**
	 * Computes the cost of {@code A}.
	 * <p>
	 * @param A an {@link Entity}
	 * <p>
	 * @return the cost of {@code A}
	 */
	public synchronized double computeCost(final Entity A) {
		// Compute -(log(A) Y' + log(1 - A) (1 - Y')) / m
		return -A.apply(Functions.LOG)
				.times(YT)
				.add(Scalar.ONE.minus(A).apply(Functions.LOG).times(Scalar.ONE.minus(YT)))
				.toScalar()
				.get() / trainingExampleCount;
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
	public abstract Entity estimate(final Entity X);

	/**
	 * Classifies the specified example and returns the estimated class.
	 * <p>
	 * @param example the example to classify
	 * <p>
	 * @return the estimated class
	 */
	public synchronized Entity classify(final Entity example) {
		return estimate(example).apply(Functions.ROUND); // (1 x m)
	}

	/**
	 * Computes the accuracy.
	 * <p>
	 * @return the accuracy
	 */
	public synchronized double computeAccuracy() {
		// Classify X
		final Entity A = classify(X); // (1 x m)
		// Compute (A Y' + (1 - A) (1 - Y')) / m
		return A.times(YT).add(Scalar.ONE.minus(A).times(Scalar.ONE.minus(YT))).toScalar().get() /
				trainingExampleCount;
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
	public BinaryClassifier clone() {
		try {
			final BinaryClassifier clone = (BinaryClassifier) super.clone();
			clone.X = Objects.clone(X);
			clone.Y = Objects.clone(Y);
			clone.YT = Objects.clone(YT);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new RuntimeException(Strings.toString(ex), ex);
		}
	}
}
