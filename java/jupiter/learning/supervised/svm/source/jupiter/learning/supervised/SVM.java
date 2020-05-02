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
import static jupiter.common.util.Characters.LEFT_PARENTHESIS;
import static jupiter.common.util.Characters.RIGHT_PARENTHESIS;
import static jupiter.common.util.Characters.SPACE;
import static jupiter.math.analysis.function.univariate.UnivariateFunctions.ROUND;

import jupiter.common.math.Maths;
import jupiter.common.struct.map.hash.ExtendedHashMap;
import jupiter.common.struct.table.StringTable;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.test.DoubleArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Doubles;
import jupiter.common.util.Integers;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVM {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default cache size.
	 */
	public static final double DEFAULT_CACHE_SIZE = 128;

	/**
	 * The default tolerance level (or termination criterion) {@code ε}.
	 */
	public static final double DEFAULT_TOLERANCE = 1E-8;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The number of features {@code n}.
	 */
	protected final int featureCount;
	/**
	 * The number of training examples {@code m}.
	 */
	protected int trainingExampleCount;

	/**
	 * The problem.
	 */
	protected final svm_problem problem;
	/**
	 * The hyper-parameters.
	 */
	protected final svm_parameter hyperParameters;

	/**
	 * The trained model.
	 */
	protected svm_model model;

	/**
	 * The probability estimates associated to their classes.
	 */
	protected final ExtendedHashMap<Integer, Double> probabilityEstimates;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a support vector machine {@link SVM} with the specified number of features
	 * {@code n}.
	 * <p>
	 * @param featureCount the number of features {@code n}
	 */
	public SVM(final int featureCount) {
		this.featureCount = featureCount;
		trainingExampleCount = 0;
		problem = new svm_problem();
		hyperParameters = new svm_parameter();
		setDefaultParameters();
		probabilityEstimates = new ExtendedHashMap<Integer, Double>();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the feature vectors.
	 * <p>
	 * @return the feature vectors
	 */
	public svm_node[][] getFeatureVectors() {
		return problem.x;
	}

	/**
	 * Returns the classes.
	 * <p>
	 * @return the classes
	 */
	public double[] getClasses() {
		return problem.y;
	}

	/**
	 * Returns the probability estimates.
	 * <p>
	 * @return the probability estimates
	 */
	public ExtendedHashMap<Integer, Double> getProbabilityEstimates() {
		return probabilityEstimates;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the hyper-parameters by default.
	 */
	public void setDefaultParameters() {
		hyperParameters.cache_size = DEFAULT_CACHE_SIZE;
		hyperParameters.eps = DEFAULT_TOLERANCE;
		hyperParameters.probability = 1; // do probability estimates
		hyperParameters.shrinking = 1; // use the shrinking heuristics
		setTypeToCSVC();
	}

	/**
	 * Sets the cache size.
	 * <p>
	 * @param cacheSize a {@code double} value
	 */
	public void setCacheSize(final double cacheSize) {
		hyperParameters.cache_size = cacheSize;
	}

	/**
	 * Sets the kernel type to linear.
	 */
	public void setKernelTypeToLinear() {
		hyperParameters.kernel_type = svm_parameter.LINEAR;
	}

	/**
	 * Sets the kernel type to polynomial with the specified hyper-parameters.
	 * <p>
	 * @param degree      an {@code int} value
	 * @param coefficient a {@code double} value
	 * @param constant    a {@code double} value
	 */
	public void setKernelTypeToPolynomial(final int degree, final double coefficient,
			final double constant) {
		// Set the hyper-parameters
		hyperParameters.kernel_type = svm_parameter.POLY;
		hyperParameters.degree = IntegerArguments.requirePositive(degree);
		hyperParameters.gamma = DoubleArguments.requireNonZero(coefficient);
		hyperParameters.coef0 = DoubleArguments.requireNonNegative(constant);
	}

	/**
	 * Sets the kernel type to Gaussian with the specified hyper-parameter.
	 * <p>
	 * @param variance a {@code double} value
	 */
	public void setKernelTypeToGaussian(final double variance) {
		setKernelTypeToRBF(0.5 / DoubleArguments.requirePositive(variance));
	}

	/**
	 * Sets the kernel type to radial basis function (RBF) with the specified hyper-parameter.
	 * <p>
	 * @param gamma a {@code double} value
	 */
	public void setKernelTypeToRBF(final double gamma) {
		hyperParameters.kernel_type = svm_parameter.RBF;
		hyperParameters.gamma = DoubleArguments.requirePositive(gamma);
	}

	/**
	 * Sets the tolerance level (or termination criterion) {@code ε}.
	 * <p>
	 * @param tolerance a {@code double} value
	 */
	public void setTolerance(final double tolerance) {
		hyperParameters.eps = tolerance;
	}

	/**
	 * Sets the type to C-SVC. This type regularizes the support vector classification.
	 */
	public void setTypeToCSVC() {
		setTypeToCSVC(1.);
	}

	/**
	 * Sets the type to C-SVC. This type regularizes the support vector classification.
	 * <p>
	 * @param c a {@code double} value
	 */
	public void setTypeToCSVC(final double c) {
		hyperParameters.svm_type = svm_parameter.C_SVC;
		hyperParameters.C = c;
	}


	/**
	 * Sets the type to nu-SVC. This type automatically regularizes the support vector
	 * classification.
	 */
	public void setTypeToNuSVC() {
		setTypeToNuSVC(0.5);
	}

	/**
	 * Sets the type to nu-SVC. This type automatically regularizes the support vector
	 * classification.
	 * <p>
	 * @param nu a {@code double} value
	 */
	public void setTypeToNuSVC(final double nu) {
		hyperParameters.svm_type = svm_parameter.NU_SVC;
		hyperParameters.nu = nu;
	}

	/**
	 * Sets the type to one-class. This type selects a hyper-sphere to maximize density.
	 * <p>
	 * @param nu a {@code double} value
	 */
	public void setTypeToOneClass(final double nu) {
		hyperParameters.svm_type = svm_parameter.ONE_CLASS;
		hyperParameters.nu = nu;
	}

	/**
	 * Sets the type to epsilon SVR. This type supports vector regression robust to small (epsilon)
	 * errors.
	 * <p>
	 * @param c       a {@code double} value
	 * @param epsilon a {@code double} value
	 */
	public void setTypeToEpsilonSVR(final double c, final double epsilon) {
		hyperParameters.svm_type = svm_parameter.EPSILON_SVR;
		hyperParameters.C = c;
		hyperParameters.p = epsilon;
	}

	/**
	 * Sets the weights (only for C-SVC).
	 * <p>
	 * @param weights a {@code double} array
	 */
	public void setWeights(final double... weights) {
		// Check the arguments
		DoubleArguments.requireMaxLength(weights, trainingExampleCount);

		// Set the hyper-parameters
		hyperParameters.weight = weights;
		hyperParameters.nr_weight = weights.length;
		hyperParameters.weight_label = ROUND.applyToPrimitiveIntArray(problem.y);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Loads the training examples (feature vectors and classes) from the specified 2D
	 * {@code double} array of feature vectors and {@code double} array of classes.
	 * <p>
	 * @param featureVectors the 2D {@code double} array of feature vectors of size {@code m x n} to
	 *                       load
	 * @param classes        the {@code double} array of classes of size {@code m} to load
	 */
	public void load(final double[][] featureVectors, final int... classes) {
		// Check the arguments
		ArrayArguments.requireSameLength(
				Arguments.requireNonNull(featureVectors, "feature vectors").length,
				Arguments.requireNonNull(classes, "classes").length);

		// Load the training examples
		if (featureVectors.length > 0) {
			trainingExampleCount = featureVectors.length;
			updateProblem();
			for (int tei = 0; tei < trainingExampleCount; ++tei) {
				final int n = featureVectors[tei].length;
				if (n < featureCount) {
					throw new IllegalArgumentException("The feature vector size is wrong at row " +
							tei + SPACE + Arguments.atLeastExpectedButFound(n, featureCount));
				}
				updateClassification(tei, classes[tei]);
				updateFeatureVector(tei, featureVectors[tei]);
			}
		} else {
			IO.warn("No training examples found");
		}
	}

	/**
	 * Loads the training examples (feature vectors and classes) from the specified
	 * {@link StringTable}.
	 * <p>
	 * @param trainingExamples the {@link StringTable} containing the training examples (feature
	 *                         vectors and classes) of size {@code m x n + 1} to load
	 * @param classesIndex     the index of the column containing the classes of size {@code m} to
	 *                         load
	 */
	public void load(final StringTable trainingExamples, final int classesIndex) {
		// Check the arguments
		Arguments.requireNonNull(trainingExamples, "training examples");

		// Load the training examples
		final int m = trainingExamples.getRowCount();
		if (m > 0) {
			final int n = trainingExamples.getColumnCount();
			if (n <= featureCount) {
				throw new IllegalArgumentException("There are not enough columns " +
						Arguments.atLeastExpectedButFound(n, featureCount + 1));
			}
			if (classesIndex < n - 1) {
				throw new IllegalArgumentException("The classes index is out of bounds " +
						Arguments.atLeastExpectedButFound(classesIndex, n - 1));
			}
			trainingExampleCount = m;
			updateProblem();
			for (int tei = 0; tei < trainingExampleCount; ++tei) {
				updateClassification(tei,
						Integers.convert(trainingExamples.get(tei, classesIndex)));
				for (int fi = 0; fi < featureCount; ++fi) {
					updateValue(tei, fi, Doubles.convert(trainingExamples.get(tei, fi)));
				}
			}
		} else {
			IO.warn("No training examples found");
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// UPDATERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Updates the problem.
	 */
	protected void updateProblem() {
		problem.l = trainingExampleCount;
		problem.x = new svm_node[trainingExampleCount][featureCount];
		problem.y = new double[trainingExampleCount];
	}

	/**
	 * Updates the class of the training example at the specified index.
	 * <p>
	 * @param trainingExampleIndex the index of the training example
	 * @param classification       the class of the training example
	 */
	protected void updateClassification(final int trainingExampleIndex, final int classification) {
		IO.debug(classification, ": ");
		problem.y[trainingExampleIndex] = classification;
	}

	/**
	 * Updates the feature vector of the training example at the specified index.
	 * <p>
	 * @param trainingExampleIndex the index of the training example
	 * @param featureVector        the feature vector of the training example
	 */
	protected void updateFeatureVector(final int trainingExampleIndex,
			final double... featureVector) {
		for (int fi = 0; fi < featureCount; ++fi) {
			updateValue(trainingExampleIndex, fi, featureVector[fi]);
		}
	}

	/**
	 * Updates the value of the feature vector of the training example at the specified indices.
	 * <p>
	 * @param trainingExampleIndex the index of the training example
	 * @param featureIndex         the index of the feature
	 * @param value                the {@code double} value at the indices
	 */
	protected void updateValue(final int trainingExampleIndex, final int featureIndex,
			final double value) {
		if (featureIndex == 0) {
			IO.debug(LEFT_PARENTHESIS, SPACE);
		}
		final svm_node node = new svm_node();
		node.index = featureIndex;
		node.value = value;
		problem.x[trainingExampleIndex][featureIndex] = node;
		IO.debug(node.value, SPACE);
		if (featureIndex == featureCount - 1) {
			IO.debug(RIGHT_PARENTHESIS);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MODEL
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Trains the model with the problem and hyper-parameters.
	 * <p>
	 * @return the trained model
	 */
	public svm_model train() {
		// Check the arguments
		if (trainingExampleCount == 0) {
			IO.error("No training examples found");
			return null;
		}

		// Train the model with the problem and hyper-parameters
		model = svm.svm_train(problem, hyperParameters);
		return model;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSIFIER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Classifies the specified example.
	 * <p>
	 * @param example the example to classify
	 * <p>
	 * @return the estimated class
	 */
	public Integer classify(final double... example) {
		// Check the arguments
		if (model == null) {
			IO.error("No model found");
			return null;
		}

		// Classify the example
		final svm_node[] nodes = new svm_node[featureCount];
		for (int fi = 0; fi < featureCount; ++fi) {
			final svm_node node = new svm_node();
			node.index = fi;
			node.value = example[fi];
			nodes[fi] = node;
		}
		final int classCount = model.nr_class;
		final int[] labels = new int[classCount];
		svm.svm_get_labels(model, labels);
		final double[] probabilityEstimatesPerClass = new double[classCount];
		final int estimatedClass = Maths.roundToInt(
				svm.svm_predict_probability(model, nodes, probabilityEstimatesPerClass));
		for (int ci = 0; ci < classCount; ++ci) {
			probabilityEstimates.put(labels[ci], probabilityEstimatesPerClass[ci]);
		}
		// Return the estimated class
		return estimatedClass;
	}
}
