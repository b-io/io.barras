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
import java.io.Serializable;

import jupiter.common.math.Maths;
import jupiter.common.model.ICloneable;
import jupiter.common.test.Arguments;
import jupiter.common.util.Doubles;
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
	public static volatile double DEFAULT_LEARNING_RATE = 0.1;

	/**
	 * The default exponential decay rate for the first-moment estimates β1 (Adam algorithm).
	 */
	public static volatile double DEFAULT_FIRST_MOMENT_EXPONENTIAL_DECAY_RATE = Double.NaN; // 0.9

	/**
	 * The default exponential decay rate for the second-moment estimates β2 (Adam algorithm).
	 */
	public static volatile double DEFAULT_SECOND_MOMENT_EXPONENTIAL_DECAY_RATE = Double.NaN; // 0.999

	/**
	 * The default tolerance level (or termination criterion) ε.
	 */
	public static volatile double DEFAULT_TOLERANCE = 1E-8;

	/**
	 * The default maximum number of iterations.
	 */
	public static volatile int DEFAULT_MAX_ITERATIONS = Integers.convert(1E6);

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
	protected int featureCount;
	/**
	 * The number of training examples m.
	 */
	protected int trainingExampleCount;

	/**
	 * The {@link Matrix} X containing the feature vectors.
	 */
	protected Matrix X; // (n x m)
	/**
	 * The {@link Vector} Y containing the classes.
	 */
	protected Vector Y, YT; // (1 x m), (m x 1)

	/**
	 * The cost.
	 */
	protected double cost = Double.POSITIVE_INFINITY;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link BinaryClassifier}.
	 * <p>
	 * @param featureCount the number of features
	 */
	protected BinaryClassifier(final int featureCount) {
		this.featureCount = featureCount;
		trainingExampleCount = 0;
	}

	/**
	 * Constructs a {@link BinaryClassifier} loaded from the specified files containing the training
	 * examples (feature vectors and classes).
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size (n x m)
	 *                           to load
	 * @param classesPath        the path to the file containing the classes of size m to load
	 * <p>
	 * @throws IOException if there is a problem with reading the specified files
	 */
	protected BinaryClassifier(final String featureVectorsPath, final String classesPath)
			throws IOException {
		load(featureVectorsPath, classesPath);
	}

	/**
	 * Constructs a {@link BinaryClassifier} loaded from the specified files containing the training
	 * examples (feature vectors and classes) and flag specifying whether to transpose the feature
	 * vectors and classes.
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size (n x m)
	 *                           (or (m x n) if {@code transpose}) to load
	 * @param classesPath        the path to the file containing the classes of size m to load
	 * @param transpose          the flag specifying whether to transpose the feature vectors and
	 *                           classes
	 * <p>
	 * @throws IOException if there is a problem with reading the specified files
	 */
	protected BinaryClassifier(final String featureVectorsPath, final String classesPath,
			final boolean transpose)
			throws IOException {
		load(featureVectorsPath, classesPath, transpose);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link Matrix} X containing the feature vectors.
	 * <p>
	 * @return the {@link Matrix} X containing the feature vectors
	 */
	public synchronized Matrix getFeatureVectors() {
		return X;
	}

	/**
	 * The {@link Vector} Y containing the classes.
	 * <p>
	 * @return the {@link Vector} Y containing the classes
	 */
	public synchronized Vector getClasses() {
		return Y;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
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
	 * Trains the model with the specified hyper-parameters and returns the number of iterations.
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

	/**
	 * Tests whether the tolerance level is reached.
	 * <p>
	 * @param tolerance the tolerance level
	 * <p>
	 * @return {@code true} if the tolerance level is reached, {@code false} otherwise
	 */
	public synchronized boolean testConvergence(final double tolerance) {
		// Compute the current cost
		final double currentCost = computeCost();
		IO.debug("Cost: ", currentCost);
		// Compute the cost difference
		final double delta = Maths.delta(cost, currentCost);
		IO.debug("Delta: ", delta);
		// Test the convergence
		if (delta > cost) {
			IO.warn("The cost is increasing by ", delta,
					" (", Doubles.toPercentage(delta / cost), ")");
		}
		cost = currentCost;
		if (delta <= tolerance) {
			IO.warn("No more improvement");
		}
		return delta <= tolerance || cost <= tolerance;
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

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Computes the accuracy.
	 * <p>
	 * @return {@code (A Y' + (1 - A) (1 - Y')) / m}
	 */
	public synchronized double computeAccuracy() {
		// Classify X
		final Entity A = classify(X); // (1 x m)
		// Compute (A Y' + (1 - A) (1 - Y')) / m
		final double truePositive = A.times(YT).toScalar().get(); // A Y'
		final double trueNegative = Scalar.ONE.minus(A).times(Scalar.ONE.minus(YT)).toScalar().get(); // (1 - A) (1 - Y')
		return (truePositive + trueNegative) / trainingExampleCount;
	}

	/**
	 * Computes the precision.
	 * <p>
	 * @return {@code A Y' / (A Y' + A (1 - Y'))}
	 */
	public synchronized double computePrecision() {
		// Classify X
		final Entity A = classify(X); // (1 x m)
		// Compute A Y' / (A Y' + A (1 - Y'))
		final double truePositive = A.times(YT).toScalar().get(); // A Y'
		final double falsePositive = A.times(Scalar.ONE.minus(YT)).toScalar().get(); // A (1 - Y')
		return truePositive / (truePositive + falsePositive);
	}

	/**
	 * Computes the recall.
	 * <p>
	 * @return {@code A Y' / (A Y' + (1 - A) Y')}
	 */
	public synchronized double computeRecall() {
		// Classify X
		final Entity A = classify(X); // (1 x m)
		// Compute A Y' / (A Y' + (1 - A) Y')
		final double truePositive = A.times(YT).toScalar().get(); // A Y'
		final double falseNegative = Scalar.ONE.minus(A).times(YT).toScalar().get(); // (1 - A) Y'
		return truePositive / (truePositive + falseNegative);
	}

	/**
	 * Computes the F1 score.
	 * <p>
	 * @return {@code 2. / ((1. / precision) + (1. / recall))}
	 */
	public synchronized double computeF1Score() {
		// Compute 2. / ((1. / precision) + (1. / recall))
		final double precision = computePrecision(); // A Y' / (A Y' + A (1 - Y'))
		final double recall = computeRecall(); // A Y' / (A Y' + (1 - A) Y')
		return 2. / (1. / precision + 1. / recall);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Loads the training examples (feature vectors and classes) from the specified files.
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size (n x m)
	 *                           to load
	 * @param classesPath        the path to the file containing the classes of size m to load
	 * <p>
	 * @throws IOException if there is a problem with reading the specified files
	 */
	public void load(final String featureVectorsPath, final String classesPath)
			throws IOException {
		X = Matrix.create(featureVectorsPath);
		featureCount = X.getRowDimension();
		trainingExampleCount = X.getColumnDimension();
		Y = Matrix.create(classesPath).toVector();
		Arguments.requireEquals(Y.getColumnDimension(), trainingExampleCount);
		YT = Y.transpose();
	}

	/**
	 * Loads the training examples (feature vectors and classes) from the specified files and flag
	 * specifying whether to transpose the feature vectors and classes.
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size (n x m)
	 *                           (or (m x n) if {@code transpose})
	 * @param classesPath        the path to the file containing the classes of size m
	 * @param transpose          the flag specifying whether to transpose the feature vectors and
	 *                           classes
	 * <p>
	 * @throws IOException if there is a problem with reading the specified files
	 */
	public void load(final String featureVectorsPath, final String classesPath,
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
