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
import static jupiter.common.util.Strings.SPACE;

import java.util.HashMap;

import jupiter.common.math.Maths;
import jupiter.common.struct.table.StringTable;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.test.DoubleArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Characters;
import jupiter.common.util.Doubles;
import jupiter.common.util.Integers;
import jupiter.math.analysis.function.Functions;

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
	protected static final double DEFAULT_CACHE_SIZE = 100;

	/**
	 * The default tolerance level (or termination criterion) ε.
	 */
	protected static final double DEFAULT_TOLERANCE = 1E-6;


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
	 * The problem.
	 */
	protected final svm_problem problem;
	/**
	 * The parameters.
	 */
	protected final svm_parameter parameters;

	/**
	 * The model.
	 */
	protected svm_model model;

	/**
	 * The probability estimates (per class).
	 */
	protected final HashMap<Integer, Double> probabilityEstimates;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a support vector machine.
	 * <p>
	 * @param featureCount the number of features
	 */
	public SVM(final int featureCount) {
		this.featureCount = featureCount;
		trainingExampleCount = 0;
		problem = new svm_problem();
		parameters = new svm_parameter();
		setDefaultParameters();
		probabilityEstimates = new HashMap<Integer, Double>(Arrays.DEFAULT_CAPACITY);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Get the feature vectors.
	 * <p>
	 * @return the feature vectors
	 */
	public svm_node[][] getFeatureVectors() {
		return problem.x;
	}

	/**
	 * Get the classes.
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
	public HashMap<Integer, Double> getProbabilityEstimates() {
		return probabilityEstimates;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the default parameters.
	 */
	public void setDefaultParameters() {
		parameters.cache_size = DEFAULT_CACHE_SIZE;
		parameters.eps = DEFAULT_TOLERANCE;
		parameters.probability = 1; // do probability estimates
		parameters.shrinking = 1; // use the shrinking heuristics
		setTypeToCSVC();
	}

	/**
	 * Sets the cache size.
	 * <p>
	 * @param cacheSize a {@code double} value
	 */
	public void setCacheSize(final double cacheSize) {
		parameters.cache_size = cacheSize;
	}

	/**
	 * Sets the kernel type to linear.
	 */
	public void setKernelTypeToLinear() {
		parameters.kernel_type = svm_parameter.LINEAR;
	}

	/**
	 * Sets the kernel type to polynomial with the specified parameters.
	 * <p>
	 * @param degree      an {@code int} value
	 * @param coefficient a {@code double} value
	 * @param constant    a {@code double} value
	 */
	public void setKernelTypeToPolynomial(final int degree, final double coefficient,
			final double constant) {
		// Set the parameters
		parameters.kernel_type = svm_parameter.POLY;
		parameters.degree = IntegerArguments.requirePositive(degree);
		parameters.gamma = DoubleArguments.requireNonZero(coefficient);
		parameters.coef0 = DoubleArguments.requireNonNegative(constant);
	}

	/**
	 * Sets the kernel type to Gaussian with the specified parameter.
	 * <p>
	 * @param variance a {@code double} value
	 */
	public void setKernelTypeToGaussian(final double variance) {
		setKernelTypeToRBF(0.5 / DoubleArguments.requirePositive(variance));
	}

	/**
	 * Sets the kernel type to radial basis function (RBF) with the specified parameter.
	 * <p>
	 * @param gamma a {@code double} value
	 */
	public void setKernelTypeToRBF(final double gamma) {
		parameters.kernel_type = svm_parameter.RBF;
		parameters.gamma = DoubleArguments.requirePositive(gamma);
	}

	/**
	 * Sets the tolerance level (or termination criterion).
	 * <p>
	 * @param tolerance a {@code double} value
	 */
	public void setTolerance(final double tolerance) {
		parameters.eps = tolerance;
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
		parameters.svm_type = svm_parameter.C_SVC;
		parameters.C = c;
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
		parameters.svm_type = svm_parameter.NU_SVC;
		parameters.nu = nu;
	}

	/**
	 * Sets the type to one-class. This type selects a hyper-sphere to maximize density.
	 * <p>
	 * @param nu a {@code double} value
	 */
	public void setTypeToOneClass(final double nu) {
		parameters.svm_type = svm_parameter.ONE_CLASS;
		parameters.nu = nu;
	}

	/**
	 * Sets the type to epsilon SVR. This type supports vector regression robust to small (epsilon)
	 * errors.
	 * <p>
	 * @param c       a {@code double} value
	 * @param epsilon a {@code double} value
	 */
	public void setTypeToEpsilonSVR(final double c, final double epsilon) {
		parameters.svm_type = svm_parameter.EPSILON_SVR;
		parameters.C = c;
		parameters.p = epsilon;
	}

	/**
	 * Sets the weights (only for C-SVC).
	 * <p>
	 * @param weights an array of {@code double} values
	 */
	public void setWeights(final double[] weights) {
		// Check the arguments
		DoubleArguments.requireMaxLength(weights, trainingExampleCount);

		// Set the parameters
		parameters.weight = weights;
		parameters.nr_weight = weights.length;
		parameters.weight_label = Functions.ROUND.applyToIntPrimitiveArray(problem.y);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Loads the training examples from the specified arrays.
	 * <p>
	 * @param featureVectors the 2D array of feature vectors of size (m x n)
	 * @param classes        the array of classes of size m
	 */
	public void loadTrainingExamples(final double[][] featureVectors, final int[] classes) {
		// Check the arguments
		ArrayArguments.requireSameLength(Arguments.requireNonNull(featureVectors).length,
				Arguments.requireNonNull(classes).length);

		// Load the training examples
		if (featureVectors.length > 0) {
			trainingExampleCount = featureVectors.length;
			updateProblem();
			for (int i = 0; i < trainingExampleCount; ++i) {
				final int n = featureVectors[i].length;
				if (n < featureCount) {
					throw new IllegalArgumentException("The feature vector size is wrong at row " +
							i + SPACE + Arguments.atLeastExpectedButFound(n, featureCount));
				}
				updateClassification(i, classes[i]);
				updateFeatureVector(i, featureVectors[i]);
			}
		} else {
			IO.warn("No training examples found");
		}
	}

	/**
	 * Loads the training examples from the specified table.
	 * <p>
	 * @param trainingExamples the {@link StringTable} containing the training examples of size (m x
	 *                         n + 1)
	 * @param classesIndex     the index of the column containing the classes in the table
	 */
	public void loadTrainingExamples(final StringTable trainingExamples, final int classesIndex) {
		if (trainingExamples != null) {
			final int m = trainingExamples.getRowCount();
			if (m > 0) {
				final int n = trainingExamples.getColumnCount();
				if (n <= featureCount) {
					throw new IllegalArgumentException("There are not enough columns " +
							Arguments.atLeastExpectedButFound(n, featureCount + 1));
				}
				if (classesIndex < n - 1) {
					throw new IllegalArgumentException("The classes index is out of bound " +
							Arguments.atLeastExpectedButFound(classesIndex, n - 1));
				}
				trainingExampleCount = m;
				updateProblem();
				for (int i = 0; i < trainingExampleCount; ++i) {
					updateClassification(i,
							Integers.convert(trainingExamples.get(i, classesIndex)));
					for (int j = 0; j < featureCount; ++j) {
						updateValue(i, j, Doubles.convert(trainingExamples.get(i, j)));
					}
				}
			} else {
				IO.warn("No training examples found");
			}
		}
	}

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
			final double[] featureVector) {
		for (int featureIndex = 0; featureIndex < featureCount; ++featureIndex) {
			updateValue(trainingExampleIndex, featureIndex, featureVector[featureIndex]);
		}
	}

	/**
	 * Updates the value of the feature vector of the training example at the specified indexes.
	 * <p>
	 * @param trainingExampleIndex the index of the training example
	 * @param featureIndex         the index of the feature
	 * @param value                the {@code double} value at the indexes
	 */
	protected void updateValue(final int trainingExampleIndex, final int featureIndex,
			final double value) {
		if (featureIndex == 0) {
			IO.debug(Characters.LEFT_PARENTHESIS, SPACE);
		}
		final svm_node node = new svm_node();
		node.index = featureIndex;
		node.value = value;
		problem.x[trainingExampleIndex][featureIndex] = node;
		IO.debug(node.value, SPACE);
		if (featureIndex == featureCount - 1) {
			IO.debug(Characters.RIGHT_PARENTHESIS);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MODELER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Trains the model with the problem and parameters and returns it.
	 * <p>
	 * @return the trained model
	 */
	public svm_model train() {
		if (trainingExampleCount == 0) {
			IO.error("No training examples found");
			return null;
		}
		model = svm.svm_train(problem, parameters);
		return model;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSIFIER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Classifies the specified example and returns the estimated class.
	 * <p>
	 * @param example the example to classify
	 * <p>
	 * @return the estimated class
	 */
	public Integer classify(final double[] example) {
		if (model == null) {
			IO.error("No model found");
			return null;
		}

		final svm_node[] nodes = new svm_node[featureCount];
		for (int i = 0; i < featureCount; ++i) {
			final svm_node node = new svm_node();
			node.index = i;
			node.value = example[i];
			nodes[i] = node;
		}
		final int totalClasses = model.nr_class;
		final int[] labels = new int[totalClasses];
		svm.svm_get_labels(model, labels);
		final double[] probabilityEstimatesPerClass = new double[totalClasses];
		final int prediction = Maths.roundToInt(
				svm.svm_predict_probability(model, nodes, probabilityEstimatesPerClass));
		for (int i = 0; i < totalClasses; ++i) {
			probabilityEstimates.put(labels[i], probabilityEstimatesPerClass[i]);
		}
		return prediction;
	}
}
