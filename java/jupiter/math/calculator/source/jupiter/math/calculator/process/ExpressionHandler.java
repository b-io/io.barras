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
package jupiter.math.calculator.process;

import static jupiter.common.io.IO.IO;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jupiter.common.exception.ParseException;
import jupiter.common.math.Interval;
import jupiter.common.math.IntervalList;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.tuple.Triple;
import jupiter.common.thread.LockedWorkQueue;
import jupiter.common.thread.Report;
import jupiter.common.thread.WorkQueue;
import jupiter.common.thread.Worker;
import jupiter.common.util.Arrays;
import jupiter.common.util.Characters;
import jupiter.common.util.Strings;
import jupiter.math.calculator.model.BinaryOperation;
import jupiter.math.calculator.model.Element;
import jupiter.math.calculator.model.MatrixElement;
import jupiter.math.calculator.model.ScalarElement;
import jupiter.math.calculator.model.UnaryOperation;
import jupiter.math.linear.entity.Matrix;

public class ExpressionHandler {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link List} of binary operators.
	 */
	protected static final List<List<Character>> BINARY_OPERATORS = Arrays.toList(
			Arrays.toList('+', '-'), Arrays.toList('*', '/'), Arrays.<Character>toList('^'),
			Arrays.toList('~'));
	/**
	 * The {@link List} of unary operators.
	 */
	protected static final List<List<Character>> UNARY_OPERATORS = Arrays
			.toList(Arrays.toList('!', '\''), Arrays.toList('@'));

	/**
	 * The flag specifying whether to parallelize using a work queue.
	 */
	protected static volatile boolean PARALLELIZE = true;
	/**
	 * The work queue for parsing the expressions.
	 */
	protected static volatile WorkQueue<Triple<Element, String, Map<String, Element>>, Report<Element>> WORK_QUEUE = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected ExpressionHandler() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts {@code this}.
	 */
	public static synchronized void start() {
		IO.debug("");

		// Initialize
		if (PARALLELIZE) {
			if (WORK_QUEUE == null) {
				WORK_QUEUE = new LockedWorkQueue<Triple<Element, String, Map<String, Element>>, Report<Element>>(
						new Parser());
			} else {
				IO.warn("The work queue ", WORK_QUEUE, " has already started");
			}
		}
	}

	/**
	 * Stops {@code this}.
	 */
	public static synchronized void stop() {
		IO.debug("");

		// Shutdown
		if (WORK_QUEUE != null) {
			WORK_QUEUE.shutdown();
		}
	}

	/**
	 * Restarts {@code this}.
	 */
	public static synchronized void restart() {
		IO.debug("");

		stop();
		start();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a tree whose nodes and leaves correspond respectively to the operations and numbers
	 * of the specified expression.
	 * <p>
	 * @param expression the expression to parse
	 * @param context    the context containing the values of the variables
	 * <p>
	 * @return the root {@link Element} of the tree of operations and numbers corresponding to the
	 *         specified expression
	 */
	public static Report<Element> parseExpression(final String expression,
			final Map<String, Element> context) {
		return parseExpression(null, expression, context);
	}

	/**
	 * Returns a node or a leaf with the specified parent node corresponding respectively to an
	 * operation or a number parsed from the specified expression.
	 * <p>
	 * @param parent     the parent node {@link Element} of the expression
	 * @param expression the expression to parse
	 * @param context    the context containing the values of the variables
	 * <p>
	 * @return a node or leaf {@link Element} corresponding respectively to an operation or a number
	 *         parsed from the specified expression
	 */
	public static Report<Element> parseExpression(final Element parent, final String expression,
			final Map<String, Element> context) {
		// Trim the expression
		final String trimmedExpression = expression.trim();
		IO.debug(trimmedExpression);

		// Find the delimiting intervals
		final IntervalList<Integer> delimitingIntervals = getDelimitingIntervals(trimmedExpression);

		// Get the index of the binary operator (if it exists)
		final int binaryOperatorIndex = getBinaryOperatorIndex(trimmedExpression,
				delimitingIntervals);

		// Parse the operation
		if (binaryOperatorIndex >= 0) {
			return parseBinaryOperation(parent, trimmedExpression, binaryOperatorIndex, context);
		}
		return parseUnaryOperation(parent, trimmedExpression, delimitingIntervals, context);
	}

	protected static Report<Element> parseBinaryOperation(final Element parent,
			final String expression, final int binaryOperatorIndex,
			final Map<String, Element> context) {
		// Get the binary operator
		final Element.Type type = getType(expression.charAt(binaryOperatorIndex));
		IO.debug("Type: ", type);

		// Extract the left and right expressions
		final String leftExpression = expression.substring(0, binaryOperatorIndex);
		final String rightExpression = expression.substring(binaryOperatorIndex + 1);

		// Parse the left and right expressions
		final Element leftNode, rightNode;
		final Report<Element> leftNodeResult, rightNodeResult;
		if (PARALLELIZE && (leftExpression.length() > 100 || rightExpression.length() > 100) &&
				WORK_QUEUE.reserveWorkers(2)) {
			// Submit the tasks
			final long leftId = WORK_QUEUE.submit(new Triple<Element, String, Map<String, Element>>(
					parent, leftExpression, context));
			final long rightId = WORK_QUEUE
					.submit(new Triple<Element, String, Map<String, Element>>(parent,
							rightExpression, context));

			// Get the results
			leftNodeResult = WORK_QUEUE.get(leftId);
			rightNodeResult = WORK_QUEUE.get(rightId);
			WORK_QUEUE.freeWorkers(2);
		} else {
			// Get the results
			leftNodeResult = parseExpression(parent, leftExpression, context);
			rightNodeResult = parseExpression(parent, rightExpression, context);
		}
		// Get the elements from the results
		// - Left node
		leftNode = leftNodeResult.getOutput();
		if (leftNode == null) {
			return leftNodeResult;
		}
		// - Right node
		rightNode = rightNodeResult.getOutput();
		if (rightNode == null) {
			return rightNodeResult;
		}

		// Return the binary operation
		IO.debug("Create new ", type, " Node: <", leftNode.getExpression(), "> ", type, " <",
				rightNode.getExpression(), ">");
		return new Report<Element>(
				new BinaryOperation(parent, expression, type, leftNode, rightNode));
	}

	protected static Report<Element> parseUnaryOperation(final Element parent,
			final String expression, final IntervalList<Integer> delimitingIntervals,
			final Map<String, Element> context) {
		// Parse an unary operation, a nested expression or a single element
		final int unaryOperatorIndex = getLastUnaryOperatorIndex(expression, delimitingIntervals);

		// - Unary operation
		if (unaryOperatorIndex >= 0) {
			// Parse the unary operator
			final char unaryOperator = expression.charAt(unaryOperatorIndex);
			final Element.Type type = getType(unaryOperator);
			IO.debug("Type: ", type);
			// Parse the nested expression
			final String nestedExpression = Strings.split(expression, unaryOperator).get(0);
			IO.debug("Nested expression: ", nestedExpression);
			final Report<Element> nodeResult = parseExpression(parent, nestedExpression, context);
			final Element node = nodeResult.getOutput();
			if (node == null) {
				return nodeResult;
			}
			// Return the unary operation
			IO.debug("Create new ", type, " Node: <", node.getExpression(), ">");
			return new Report<Element>(new UnaryOperation(parent, expression, type, node));
		}

		// - Nested expression
		if (delimitingIntervals.isValid()) {
			// Parse the nested expression
			final List<Interval<Integer>> intervals = delimitingIntervals.getIntervals();
			if (intervals.size() == 1) {
				final Interval<Integer> interval = intervals.get(0);
				if (Characters.isParenthesis(expression.charAt(interval.getLowerBound())) &&
						Characters.isParenthesis(expression.charAt(interval.getUpperBound()))) {
					// Return the nested expression
					final String nestedExpression = expression
							.substring(interval.getLowerBound() + 1, interval.getUpperBound());
					IO.debug("Nested expression: ", nestedExpression);
					return parseExpression(parent, nestedExpression, context);
				}
			}
		}

		// - Single element
		// Parse the single element
		IO.debug("Create new Leaf: <", expression, ">");
		final Element leaf;
		if (context.containsKey(expression)) {
			// Variable
			leaf = context.get(expression);
		} else if (Matrix.isMatrix(expression)) {
			// Matrix
			leaf = new MatrixElement(parent, expression);
		} else if (Strings.isNumeric(expression)) {
			// Scalar
			leaf = new ScalarElement(parent, expression);
		} else {
			return new Report<Element>(
					new ParseException("Unparsable element: <" + expression + ">"));
		}
		// Return the single element
		leaf.setParent(parent);
		leaf.setExpression(expression);
		return new Report<Element>(leaf);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the binary operator in the specified expression, or {@code -1} if there
	 * is no such occurrence.
	 * <p>
	 * @param expression          the expression to parse
	 * @param delimitingIntervals the delimiting intervals in the specified expression
	 * <p>
	 * @return the index of the binary operator in the specified expression, or {@code -1} if there
	 *         is no such occurrence
	 */
	protected static int getBinaryOperatorIndex(final String expression,
			final IntervalList<Integer> delimitingIntervals) {
		final ExtendedList<Integer> indexes = getBinaryOperatorIndexes(expression,
				delimitingIntervals);
		IO.debug("Indexes: ", indexes);
		if (indexes.size() > 0) {
			return indexes.getMiddle();
		}
		return -1;
	}

	/**
	 * Returns the indexes of all the binary operators in the specified expression that are not in
	 * the specified delimiting intervals.
	 * <p>
	 * @param expression          a {@link String}
	 * @param delimitingIntervals the delimiting intervals in the specified expression
	 * <p>
	 * @return the indexes of all the binary operators in the specified expression that are not in
	 *         the specified delimiting intervals
	 */
	protected static ExtendedList<Integer> getBinaryOperatorIndexes(final String expression,
			final IntervalList<Integer> delimitingIntervals) {
		return getOperatorIndexes(expression, delimitingIntervals, expression.length(),
				BINARY_OPERATORS);
	}

	/**
	 * Returns the index of the last unary operator in the specified expression that is not in the
	 * specified delimiting intervals.
	 * <p>
	 * @param expression          a {@link String}
	 * @param delimitingIntervals the delimiting intervals in the specified expression
	 * <p>
	 * @return the index of the last unary operator in the specified expression that is not in the
	 *         specified delimiting intervals
	 */
	protected static Integer getLastUnaryOperatorIndex(final String expression,
			final IntervalList<Integer> delimitingIntervals) {
		return getLastOperatorIndexFromList(expression, delimitingIntervals, expression.length(),
				UNARY_OPERATORS);
	}

	/**
	 * Returns the indexes of all the operators in the specified expression that are not in the
	 * specified delimiting intervals.
	 * <p>
	 * @param expression          a {@link String}
	 * @param delimitingIntervals the delimiting intervals in the specified expression
	 * @param to                  the index to finish seeking at (exclusive)
	 * @param allOperators        the {@link List} of all the operators to find
	 * <p>
	 * @return the indexes of all the operators in the specified expression that are not in the
	 *         specified delimiting intervals
	 */
	protected static ExtendedList<Integer> getOperatorIndexes(final String expression,
			final IntervalList<Integer> delimitingIntervals, final int to,
			final List<List<Character>> allOperators) {
		final ExtendedList<Integer> indexes = new ExtendedList<Integer>();
		final int size = allOperators.size();
		int binaryOperatorsIndex = 0;

		do {
			final List<Character> operators = allOperators.get(binaryOperatorsIndex);
			int index = getLastOperatorIndex(expression, delimitingIntervals, to, operators);
			while (index >= 0) {
				indexes.add(index);
				index = getLastOperatorIndex(expression, delimitingIntervals, index, operators);
			}
			++binaryOperatorsIndex;
		} while (binaryOperatorsIndex < size && indexes.isEmpty());

		return indexes;
	}

	/**
	 * Returns the index of the last operator in the specified expression that is not in the
	 * specified delimiting intervals.
	 * <p>
	 * @param expression          a {@link String}
	 * @param delimitingIntervals the delimiting intervals in the specified expression
	 * @param to                  the index to finish seeking at (exclusive)
	 * @param operators           the {@link List} of operators to find
	 * <p>
	 * @return the index of the last operator in the specified expression that is not in the
	 *         specified delimiting intervals
	 */
	protected static int getLastOperatorIndex(final String expression,
			final IntervalList<Integer> delimitingIntervals, final int to,
			final List<Character> operators) {
		int index = to;
		do {
			index = Strings.findLastCharacter(expression, operators, index);
		} while (index >= 0 && delimitingIntervals.isValid() &&
				delimitingIntervals.isInside(index));
		return index;
	}

	/**
	 * Returns the index of the last operator in the specified expression that is not in the
	 * specified delimiting intervals.
	 * <p>
	 * @param expression          a {@link String}
	 * @param delimitingIntervals the delimiting intervals in the specified expression
	 * @param to                  the index to finish seeking at (exclusive)
	 * @param allOperators        the {@link List} of all the operators to find
	 * <p>
	 * @return the index of the last operator in the specified expression that is not in the
	 *         specified delimiting intervals
	 */
	protected static int getLastOperatorIndexFromList(final String expression,
			final IntervalList<Integer> delimitingIntervals, final int to,
			final List<List<Character>> allOperators) {
		final int size = allOperators.size();
		int index;
		int binaryOperatorsIndex = 0;

		do {
			final List<Character> operators = allOperators.get(binaryOperatorsIndex);
			index = getLastOperatorIndex(expression, delimitingIntervals, to, operators);
			++binaryOperatorsIndex;
		} while (index < 0 && binaryOperatorsIndex < size);

		return index;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the delimiting intervals in the specified expression.
	 * <p>
	 * @param expression a {@link String}
	 * <p>
	 * @return the delimiting intervals in the specified expression
	 */
	protected static IntervalList<Integer> getDelimitingIntervals(final String expression) {
		final List<Interval<Integer>> delimitingIntervals = new LinkedList<Interval<Integer>>();
		int counter = 0;
		int lowerBound, upperBound = -1;

		for (int index = expression.length() - 1; index >= 0; --index) {
			final Element.Type type = getType(expression.charAt(index));
			if (type == Element.Type.RIGHT_PARENTHESIS || type == Element.Type.RIGHT_BRACKET) {
				if (counter == 0) {
					upperBound = index;
				}
				++counter;
			} else if (type == Element.Type.LEFT_PARENTHESIS || type == Element.Type.LEFT_BRACKET) {
				--counter;
				if (counter == 0) {
					lowerBound = index;
					delimitingIntervals.add(new Interval<Integer>(lowerBound, upperBound));
				}
			}
		}

		return new IntervalList<Integer>(delimitingIntervals);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Element.Type} of the specified token.
	 * <p>
	 * @param token a {@code char} value
	 * <p>
	 * @return the {@link Element.Type} of the specified token
	 */
	protected static Element.Type getType(final char token) {
		switch (token) {
			case '+':
				return Element.Type.ADDITION;
			case '-':
				return Element.Type.SUBTRACTION;
			case '*':
				return Element.Type.MULTIPLICATION;
			case '/':
				return Element.Type.DIVISION;
			case '^':
				return Element.Type.POWER;
			case '~':
				return Element.Type.SOLUTION;

			case '!':
				return Element.Type.FACTORIAL;
			case '@':
				return Element.Type.INVERSE;
			case '\'':
				return Element.Type.TRANSPOSE;

			case Characters.LEFT_PARENTHESIS:
				return Element.Type.LEFT_PARENTHESIS;
			case Characters.RIGHT_PARENTHESIS:
				return Element.Type.RIGHT_PARENTHESIS;
			case Characters.LEFT_BRACKET:
				return Element.Type.LEFT_BRACKET;
			case Characters.RIGHT_BRACKET:
				return Element.Type.RIGHT_BRACKET;
		}
		return Element.Type.ENTITY;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class Parser
			extends Worker<Triple<Element, String, Map<String, Element>>, Report<Element>> {

		protected Parser() {
			super();
		}

		@Override
		public Report<Element> call(final Triple<Element, String, Map<String, Element>> input) {
			return parseExpression(input.getFirst(), input.getSecond(), input.getThird());
		}

		@Override
		public Parser clone() {
			return new Parser();
		}
	}
}
