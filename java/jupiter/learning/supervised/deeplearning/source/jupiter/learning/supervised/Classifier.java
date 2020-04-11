/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2020 Florian Barras <https://barras.io> (florian@barras.io)
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
import static jupiter.math.analysis.function.univariate.UnivariateFunctions.ROUND;

import java.io.IOException;
import java.io.Serializable;

import jupiter.common.math.Maths;
import jupiter.common.model.ICloneable;
import jupiter.common.test.Arguments;
import jupiter.common.util.Doubles;
import jupiter.common.util.Integers;
import jupiter.common.util.Objects;
import jupiter.learning.supervised.function.ActivationFunctions;
import jupiter.learning.supervised.function.OutputActivationFunction;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.entity.Scalar;

public abstract class Classifier
		implements ICloneable<Classifier>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default learning rate {@code α}.
	 */
	public static final double DEFAULT_LEARNING_RATE = 0.1;

	/**
	 * The default exponential decay rate for the first-moment estimates {@code β1} (Adam
	 * algorithm).
	 */
	public static final double DEFAULT_FIRST_MOMENT_EXPONENTIAL_DECAY_RATE = 0.9;

	/**
	 * The default exponential decay rate for the second-moment estimates {@code β2} (Adam
	 * algorithm).
	 */
	public static final double DEFAULT_SECOND_MOMENT_EXPONENTIAL_DECAY_RATE = 0.999;

	/**
	 * The default tolerance level (or termination criterion) {@code ε}.
	 */
	public static final double DEFAULT_TOLERANCE = 1E-8;

	/**
	 * The default maximum number of iterations.
	 */
	public static final int DEFAULT_MAX_ITERATIONS = Integers.convert(1E6);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The minimum convergence test frequency.
	 */
	public static volatile int MIN_CONVERGENCE_TEST_FREQUENCY = 10;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The number of features {@code n}.
	 */
	protected int featureCount;
	/**
	 * The number of classes {@code k}.
	 */
	protected int classCount;
	/**
	 * The number of training examples {@code m}.
	 */
	protected int trainingExampleCount;

	/**
	 * The {@link Matrix} {@code X} containing the feature vectors.
	 */
	protected Matrix X; // (n x m)
	/**
	 * The {@link Matrix} {@code Y} and {@code YT} (its transpose) containing the classes.
	 */
	protected Matrix Y, YT; // (k x m), (m x k)

	/**
	 * The {@link OutputActivationFunction} {@code h} for the output layer.
	 */
	protected OutputActivationFunction outputActivationFunction;
	/**
	 * The cost.
	 */
	protected double cost = Double.POSITIVE_INFINITY;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Classifier} with the specified number of features {@code n}.
	 * <p>
	 * @param featureCount the number of features {@code n}
	 */
	protected Classifier(final int featureCount) {
		this(featureCount, 1);
	}

	/**
	 * Constructs a {@link Classifier} with the specified number of features {@code n} and number of
	 * classes {@code k}.
	 * <p>
	 * @param featureCount the number of features {@code n}
	 * @param classCount   the number of classes {@code k}
	 */
	protected Classifier(final int featureCount, final int classCount) {
		this.featureCount = featureCount;
		this.classCount = classCount;
		trainingExampleCount = 0;
		if (classCount == 1) {
			outputActivationFunction = ActivationFunctions.SIGMOID;
		} else {
			outputActivationFunction = ActivationFunctions.SOFTMAX;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Classifier} loaded from the files denoted by the specified paths
	 * containing the training examples (feature vectors and classes).
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size
	 *                           {@code n x m} to load
	 * @param classesPath        the path to the file containing the classes of size {@code m} to
	 *                           load
	 * <p>
	 * @throws IOException if there is a problem with reading the files denoted by
	 *                     {@code featureVectorsPath} or {@code classesPath}
	 */
	protected Classifier(final String featureVectorsPath, final String classesPath)
			throws IOException {
		load(featureVectorsPath, classesPath);
	}

	/**
	 * Constructs a {@link Classifier} loaded from the files denoted by the specified paths
	 * containing the training examples (feature vectors and classes) and flag specifying whether to
	 * transpose the feature vectors and classes.
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size
	 *                           {@code n x m} (or {@code m x n} if {@code transpose}) to load
	 * @param classesPath        the path to the file containing the classes of size {@code m} to
	 *                           load
	 * @param transpose          the flag specifying whether to transpose the feature vectors and
	 *                           classes
	 * <p>
	 * @throws IOException if there is a problem with reading the files denoted by
	 *                     {@code featureVectorsPath} or {@code classesPath}
	 */
	protected Classifier(final String featureVectorsPath, final String classesPath,
			final boolean transpose)
			throws IOException {
		load(featureVectorsPath, classesPath, transpose);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The number of feature vectors {@code n}.
	 * <p>
	 * @return the number of feature vectors {@code n}
	 */
	public synchronized int getFeatureCount() {
		return featureCount;
	}

	/**
	 * The number of classes {@code k}.
	 * <p>
	 * @return the number of classes {@code k}
	 */
	public synchronized int getClassCount() {
		return classCount;
	}

	/**
	 * The number of training examples {@code m}.
	 * <p>
	 * @return the number of training examples {@code m}
	 */
	public synchronized int getTrainingExampleCount() {
		return trainingExampleCount;
	}

	//////////////////////////////////////////////

	/**
	 * The {@link Matrix} {@code X} containing the feature vectors.
	 * <p>
	 * @return the {@link Matrix} {@code X} containing the feature vectors
	 */
	public synchronized Matrix getFeatureVectors() {
		return X;
	}

	/**
	 * The {@link Matrix} {@code Y} containing the classes.
	 * <p>
	 * @return the {@link Matrix} {@code Y} containing the classes
	 */
	public synchronized Matrix getClasses() {
		return Y;
	}

	/**
	 * The transposed {@link Matrix} {@code YT} containing the classes.
	 * <p>
	 * @return the transposed {@link Matrix} {@code YT} containing the classes
	 */
	public synchronized Matrix getTransposedClasses() {
		return YT;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public synchronized void setFeatureVectors(final Matrix featureVectors) {
		// Set the feature vectors
		X = featureVectors;
		featureCount = X.getRowDimension();
		trainingExampleCount = featureVectors.getColumnDimension();
	}

	public synchronized void setClasses(final Matrix classes) {
		// Check the arguments
		Arguments.require(classes.getColumnDimension(), trainingExampleCount);

		// Set the classes
		Y = classes;
		YT = classes.transpose();
		classCount = Y.getRowDimension();

		// Set the activation function for the output layer
		if (classCount == 1) {
			outputActivationFunction = ActivationFunctions.SIGMOID;
		} else {
			outputActivationFunction = ActivationFunctions.SOFTMAX;
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Loads the training examples (feature vectors and classes) from the files denoted by the
	 * specified paths.
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size
	 *                           {@code n x m} to load
	 * @param classesPath        the path to the file containing the classes of size {@code m} to
	 *                           load
	 * <p>
	 * @throws IOException if there is a problem with reading the files denoted by
	 *                     {@code featureVectorsPath} or {@code classesPath}
	 */
	public void load(final String featureVectorsPath, final String classesPath)
			throws IOException {
		load(featureVectorsPath, classesPath, false);
	}

	/**
	 * Loads the training examples (feature vectors and classes) from the files denoted by the
	 * specified paths and flag specifying whether to transpose the feature vectors and classes.
	 * <p>
	 * @param featureVectorsPath the path to the file containing the feature vectors of size
	 *                           {@code n x m} (or {@code m x n} if {@code transpose})
	 * @param classesPath        the path to the file containing the classes of size {@code m}
	 * @param transpose          the flag specifying whether to transpose the feature vectors and
	 *                           classes
	 * <p>
	 * @throws IOException if there is a problem with reading the files denoted by
	 *                     {@code featureVectorsPath} or {@code classesPath}
	 */
	public void load(final String featureVectorsPath, final String classesPath,
			final boolean transpose)
			throws IOException {
		// Load the feature vectors
		setFeatureVectors(Matrix.load(featureVectorsPath, transpose));

		// Load the classes
		setClasses(Matrix.load(classesPath, transpose));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MODEL
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Trains the model.
	 * <p>
	 * @return the number of iterations
	 */
	public synchronized int train() {
		return train(DEFAULT_LEARNING_RATE, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS);
	}

	/**
	 * Trains the model with the specified hyper-parameters.
	 * <p>
	 * @param learningRate      the learning rate {@code α}
	 * @param tolerance         the tolerance level
	 * @param maxIterationCount the maximum number of iterations
	 * <p>
	 * @return the number of iterations
	 */
	public synchronized int train(final double learningRate, final double tolerance,
			final int maxIterationCount) {
		return train(learningRate, Double.NaN, Double.NaN, tolerance, maxIterationCount);
	}

	/**
	 * Trains the model with the specified hyper-parameters.
	 * <p>
	 * @param learningRate                     the learning rate {@code α}
	 * @param firstMomentExponentialDecayRate  the first-moment exponential decay rate {@code β1}
	 * @param secondMomentExponentialDecayRate the second-moment exponential decay rate {@code β2}
	 * @param tolerance                        the tolerance level {@code ε}
	 * @param maxIterationCount                the maximum number of iterations
	 * <p>
	 * @return the number of iterations
	 */
	public abstract int train(final double learningRate,
			final double firstMomentExponentialDecayRate,
			final double secondMomentExponentialDecayRate,
			final double tolerance,
			final int maxIterationCount);

	//////////////////////////////////////////////

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
		return outputActivationFunction.computeCost(this, A);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the tolerance level {@code ε} is reached.
	 * <p>
	 * @param tolerance the tolerance level
	 * <p>
	 * @return {@code true} if the tolerance level {@code ε} is reached, {@code false} otherwise
	 */
	public synchronized boolean testConvergence(final double tolerance) {
		// Compute the current cost
		final double currentCost = computeCost();
		IO.debug("Cost: ", currentCost);
		// Compute the cost difference
		final double distance = Maths.distance(cost, currentCost);
		IO.debug("Delta: ", distance);
		// Test the convergence
		if (distance > cost) {
			IO.warn("The cost is increasing by ", distance,
					" (", Doubles.formatPercent(distance / cost), ")");
		}
		cost = currentCost;
		if (distance <= tolerance) {
			IO.warn("No more improvement");
		}
		return distance <= tolerance || cost <= tolerance;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSIFIER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the estimated probability of the binary (logistic) or multinary (softmax) response
	 * for all feature vector in {@code X}.
	 * <p>
	 * @param X the feature vectors of size {@code n x m}
	 * <p>
	 * @return the estimated probability of the binary (logistic) or multinary (softmax) response
	 *         for all feature vector in {@code X}
	 */
	public abstract Entity estimate(final Entity X);

	/**
	 * Classifies the specified example.
	 * <p>
	 * @param example the example to classify
	 * <p>
	 * @return the estimated class
	 */
	public synchronized Entity classify(final Entity example) {
		return estimate(example).apply(ROUND); // (k x m)
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Computes the accuracy.
	 * <p>
	 * @return {@code (sum(diag(A Y')) + sum(diag((1. - A) (1. - Y')))) / (k * m)}
	 */
	public synchronized double computeAccuracy() {
		// Classify X
		final Entity A = classify(X); // (k x m)
		// Compute (sum(diag(A Y')) + sum(diag((1 - A) (1 - Y')))) / (k * m)
		final double truePositive = A.diagonalTimes(YT).sum(); // sum(diag(A Y'))
		final double trueNegative = Scalar.ONE.minus(A).diagonalTimes(Scalar.ONE.minus(YT)).sum(); // sum(diag((1 - A) (1 - Y')))
		return (truePositive + trueNegative) / (classCount * trainingExampleCount);
	}

	/**
	 * Computes the precision.
	 * <p>
	 * @return {@code sum(diag(A Y')) / (sum(diag(A Y')) + sum(diag(A (1. - Y'))))}
	 */
	public synchronized double computePrecision() {
		// Classify X
		final Entity A = classify(X); // (k x m)
		// Compute sum(diag(A Y')) / (sum(diag(A Y')) + sum(diag(A (1 - Y'))))
		final double truePositive = A.diagonalTimes(YT).sum(); // sum(diag(A Y'))
		final double falsePositive = A.diagonalTimes(Scalar.ONE.minus(YT)).sum(); // sum(diag(A (1 - Y')))
		return Maths.safeDivision(truePositive, truePositive + falsePositive);
	}

	/**
	 * Computes the recall.
	 * <p>
	 * @return {@code sum(diag(A Y')) / (sum(diag(A Y')) + sum(diag((1. - A) Y')))}
	 */
	public synchronized double computeRecall() {
		// Classify X
		final Entity A = classify(X); // (k x m)
		// Compute sum(diag(A Y')) / (sum(diag(A Y')) + sum(diag((1 - A) Y')))
		final double truePositive = A.diagonalTimes(YT).sum(); // sum(diag(A Y'))
		final double falseNegative = Scalar.ONE.minus(A).diagonalTimes(YT).sum(); // sum(diag((1 - A) Y'))
		return Maths.safeDivision(truePositive, truePositive + falseNegative);
	}

	/**
	 * Computes the F1 score.
	 * <p>
	 * @return {@code 2. / ((1. / precision) + (1. / recall))}
	 */
	public synchronized double computeF1Score() {
		// Compute 2. / ((1. / precision) + (1. / recall))
		final double precision = computePrecision(); // sum(diag(A Y')) / (sum(diag(A Y')) + sum(diag(A (1 - Y'))))
		final double recall = computeRecall(); // sum(diag(A Y')) / (sum(diag(A Y')) + sum(diag((1 - A) Y')))
		return Maths.safeDivision(2., Maths.safeInverse(precision) + Maths.safeInverse(recall));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public Classifier clone() {
		try {
			final Classifier clone = (Classifier) super.clone();
			clone.X = Objects.clone(X);
			clone.Y = Objects.clone(Y);
			clone.YT = Objects.clone(YT);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}
}
