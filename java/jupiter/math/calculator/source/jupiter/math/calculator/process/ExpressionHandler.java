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
import static jupiter.common.util.Strings.EMPTY;
import static jupiter.common.util.Characters.LEFT_BRACKET;
import static jupiter.common.util.Characters.RIGHT_BRACKET;
import static jupiter.common.util.Characters.LEFT_PARENTHESIS;
import static jupiter.common.util.Characters.RIGHT_PARENTHESIS;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import jupiter.common.exception.ParseException;
import jupiter.common.math.Interval;
import jupiter.common.math.IntervalList;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.tuple.Triple;
import jupiter.common.thread.LockedWorkQueue;
import jupiter.common.thread.Result;
import jupiter.common.thread.WorkQueue;
import jupiter.common.thread.Worker;
import jupiter.common.util.Arrays;
import jupiter.common.util.Characters;
import jupiter.common.util.Strings;
import jupiter.math.calculator.model.BinaryOperation;
import jupiter.math.calculator.model.Element;
import jupiter.math.calculator.model.Element.Type;
import jupiter.math.calculator.model.MatrixElement;
import jupiter.math.calculator.model.ScalarElement;
import jupiter.math.calculator.model.UnaryOperation;
import jupiter.math.linear.entity.Matrix;

public class ExpressionHandler
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The {@link List} of binary operators.
	 */
	@SuppressWarnings("unchecked")
	protected static final List<List<Character>> BINARY_FUNCTIONS = Arrays.<List<Character>>asList(
			Arrays.<Character>asList('+', '-'), Arrays.<Character>asList('*', '/'),
			Arrays.<Character>asList('^'), Arrays.<Character>asList('~'));
	/**
	 * The {@link List} of unary operators.
	 */
	@SuppressWarnings("unchecked")
	protected static final List<List<Character>> UNARY_FUNCTIONS = Arrays.<List<Character>>asList(
			Arrays.<Character>asList('!', '\''), Arrays.<Character>asList('@'));

	/**
	 * The flag specifying whether to parallelize using a {@link WorkQueue}.
	 */
	protected static volatile boolean PARALLELIZE = true;
	/**
	 * The {@link WorkQueue} used for parsing the expressions.
	 */
	protected static volatile WorkQueue<Triple<Element, String, Map<String, Element>>, Result<Element>> WORK_QUEUE = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected ExpressionHandler() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parallelizes {@code this}.
	 */
	public static synchronized void parallelize() {
		IO.debug(EMPTY);

		// Initialize
		if (PARALLELIZE) {
			if (WORK_QUEUE == null) {
				WORK_QUEUE = new LockedWorkQueue<Triple<Element, String, Map<String, Element>>, Result<Element>>(
						new Parser());
			} else {
				IO.debug("The work queue ", WORK_QUEUE, " has already started");
			}
		}
	}

	/**
	 * Unparallelizes {@code this}.
	 */
	public static synchronized void unparallelize() {
		IO.debug(EMPTY);

		// Shutdown
		if (WORK_QUEUE != null) {
			WORK_QUEUE.shutdown();
		}
	}

	/**
	 * Reparallelizes {@code this}.
	 */
	public static synchronized void reparallelize() {
		IO.debug(EMPTY);

		unparallelize();
		parallelize();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the root {@link Element} of the tree whose nodes and leaves correspond respectively
	 * to the operations and numbers of the specified expression {@link String}.
	 * <p>
	 * @param expression the expression {@link String} to parse
	 * @param context    the context {@link Map} containing the values of the variables
	 * <p>
	 * @return the root {@link Element} of the tree whose nodes and leaves correspond respectively
	 *         to the operations and numbers of the specified expression {@link String}
	 */
	public static Result<Element> parseExpression(final String expression,
			final Map<String, Element> context) {
		return parseExpression(null, expression, context);
	}

	/**
	 * Returns a node or a leaf {@link Element} with the specified parent {@link Element}
	 * corresponding respectively to an operation or a number parsed from the specified expression
	 * {@link String}.
	 * <p>
	 * @param parent     the parent {@link Element} of the expression {@link String} to parse
	 * @param expression the expression {@link String} to parse
	 * @param context    the context {@link Map} containing the values of the variables
	 * <p>
	 * @return a node or leaf {@link Element} with the specified parent {@link Element}
	 *         corresponding respectively to an operation or a number parsed from the specified
	 *         expression {@link String}
	 */
	public static Result<Element> parseExpression(final Element parent, final String expression,
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

	protected static Result<Element> parseBinaryOperation(final Element parent,
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
		final Result<Element> leftNodeResult, rightNodeResult;
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
		// • Left node
		leftNode = leftNodeResult.getOutput();
		if (leftNode == null) {
			return leftNodeResult;
		}
		// • Right node
		rightNode = rightNodeResult.getOutput();
		if (rightNode == null) {
			return rightNodeResult;
		}

		// Return the binary operation
		IO.debug("Create new ", type, " Node: <", leftNode.getExpression(), "> ", type, " <",
				rightNode.getExpression(), ">");
		return new Result<Element>(
				new BinaryOperation(parent, expression, type, leftNode, rightNode));
	}

	protected static Result<Element> parseUnaryOperation(final Element parent,
			final String expression, final IntervalList<Integer> delimitingIntervals,
			final Map<String, Element> context) {
		// Parse an unary operation, a nested expression or a single element
		final int unaryOperatorIndex = getLastUnaryOperatorIndex(expression, delimitingIntervals);

		// • Unary operation
		if (unaryOperatorIndex >= 0) {
			// Parse the unary operator
			final char unaryOperator = expression.charAt(unaryOperatorIndex);
			final Element.Type type = getType(unaryOperator);
			IO.debug("Type: ", type);
			// Parse the nested expression
			final String nestedExpression = Strings.removeEmpty(Strings.split(expression, unaryOperator))
					.get(0);
			IO.debug("Nested expression: ", nestedExpression);
			final Result<Element> nodeResult = parseExpression(parent, nestedExpression, context);
			final Element node = nodeResult.getOutput();
			if (node == null) {
				return nodeResult;
			}
			// Return the unary operation
			IO.debug("Create new ", type, " Node: <", node.getExpression(), ">");
			return new Result<Element>(new UnaryOperation(parent, expression, type, node));
		}

		// • Nested expression
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

		// • Single element
		// Parse the single element
		IO.debug("Create new Leaf: <", expression, ">");
		final Element output;
		if (context.containsKey(expression)) {
			// Variable
			output = context.get(expression);
		} else if (Matrix.is(expression)) {
			// Matrix
			output = new MatrixElement(parent, expression);
		} else if (Strings.isNumeric(expression)) {
			// Scalar
			output = new ScalarElement(parent, expression);
		} else {
			return new Result<Element>(
					new ParseException("Unparsable element: <" + expression + ">"));
		}
		// Return the single element
		output.setParent(parent);
		output.setExpression(expression);
		return new Result<Element>(output);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the binary operator in the specified expression {@link String}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * <p>
	 * @return the index of the binary operator in the specified expression {@link String}, or
	 *         {@code -1} if there is no such occurrence
	 */
	protected static int getBinaryOperatorIndex(final String expression,
			final IntervalList<Integer> delimitingIntervals) {
		final ExtendedList<Integer> indexes = getBinaryOperatorIndexes(expression,
				delimitingIntervals);
		IO.debug("Indexes: ", indexes);
		if (!indexes.isEmpty()) {
			return indexes.getMiddle();
		}
		return -1;
	}

	/**
	 * Returns the indexes of all the binary operators in the specified expression {@link String}
	 * that are not in the specified delimiting intervals.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * <p>
	 * @return the indexes of all the binary operators in the specified expression {@link String}
	 *         that are not in the specified delimiting intervals
	 */
	protected static ExtendedList<Integer> getBinaryOperatorIndexes(final String expression,
			final IntervalList<Integer> delimitingIntervals) {
		return getOperatorIndexes(expression, delimitingIntervals, expression.length() - 1,
				BINARY_FUNCTIONS);
	}

	/**
	 * Returns the index of the last unary operator in the specified expression {@link String} that
	 * is not in the specified delimiting intervals.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * <p>
	 * @return the index of the last unary operator in the specified expression {@link String} that
	 *         is not in the specified delimiting intervals
	 */
	protected static Integer getLastUnaryOperatorIndex(final String expression,
			final IntervalList<Integer> delimitingIntervals) {
		return getLastOperatorIndexFromList(expression, delimitingIntervals,
				expression.length() - 1, UNARY_FUNCTIONS);
	}

	/**
	 * Returns the indexes of all the operators in the specified expression {@link String} that are
	 * not in the specified delimiting intervals.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * @param fromIndex           the index to start seeking backward from (inclusive)
	 * @param allOperators        the {@link List} of all the operators to find
	 * <p>
	 * @return the indexes of all the operators in the specified expression {@link String} that are
	 *         not in the specified delimiting intervals
	 */
	protected static ExtendedList<Integer> getOperatorIndexes(final String expression,
			final IntervalList<Integer> delimitingIntervals, final int fromIndex,
			final List<List<Character>> allOperators) {
		// Initialize
		final ExtendedList<Integer> indexes = new ExtendedList<Integer>();
		final int allOperatorCount = allOperators.size();
		int binaryOperatorsIndex = 0;

		// Get the operator indexes
		do {
			final List<Character> operators = allOperators.get(binaryOperatorsIndex);
			int index = fromIndex + 1;
			while ((index = getLastOperatorIndex(expression, delimitingIntervals, --index,
					operators)) >= 0) {
				indexes.add(index);
			}
			++binaryOperatorsIndex;
		} while (binaryOperatorsIndex < allOperatorCount && indexes.isEmpty());
		return indexes;
	}

	/**
	 * Returns the index of the last operator in the specified expression {@link String} that is not
	 * in the specified delimiting intervals.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * @param fromIndex           the index to start seeking backward from (inclusive)
	 * @param operators           the {@link List} of operators to find
	 * <p>
	 * @return the index of the last operator in the specified expression {@link String} that is not
	 *         in the specified delimiting intervals
	 */
	@SuppressWarnings("empty-statement")
	protected static int getLastOperatorIndex(final String expression,
			final IntervalList<Integer> delimitingIntervals, final int fromIndex,
			final List<Character> operators) {
		int index = fromIndex + 1;
		while ((index = Strings.findLast(expression, operators, --index)) >= 0 &&
				delimitingIntervals.isValid() && delimitingIntervals.isInside(index));
		return index;
	}

	/**
	 * Returns the index of the last operator in the specified expression {@link String} that is not
	 * in the specified delimiting intervals.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * @param fromIndex           the index to start seeking backward from (inclusive)
	 * @param allOperators        the {@link List} of all the operators to find
	 * <p>
	 * @return the index of the last operator in the specified expression {@link String} that is not
	 *         in the specified delimiting intervals
	 */
	protected static int getLastOperatorIndexFromList(final String expression,
			final IntervalList<Integer> delimitingIntervals, final int fromIndex,
			final List<List<Character>> allOperators) {
		// Initialize
		final int allOperatorCount = allOperators.size();

		// Get the last operator index
		int index, binaryOperatorsIndex = 0;
		do {
			index = getLastOperatorIndex(expression, delimitingIntervals, fromIndex,
					allOperators.get(binaryOperatorsIndex));
			++binaryOperatorsIndex;
		} while (index < 0 && binaryOperatorsIndex < allOperatorCount);
		return index;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the delimiting intervals in the specified expression {@link String}.
	 * <p>
	 * @param expression the expression {@link String} to parse
	 * <p>
	 * @return the delimiting intervals in the specified expression {@link String}
	 */
	protected static IntervalList<Integer> getDelimitingIntervals(final String expression) {
		// Initialize
		final List<Interval<Integer>> delimitingIntervals = new ExtendedLinkedList<Interval<Integer>>();

		// Get the delimiting intervals
		int parenthesisCount = 0, lowerBound, upperBound = -1;
		for (int index = expression.length() - 1; index >= 0; --index) {
			final Element.Type type = getType(expression.charAt(index));
			if (type == Element.Type.RIGHT_PARENTHESIS || type == Element.Type.RIGHT_BRACKET) {
				if (parenthesisCount == 0) {
					upperBound = index;
				}
				++parenthesisCount;
			} else if (type == Element.Type.LEFT_PARENTHESIS || type == Element.Type.LEFT_BRACKET) {
				--parenthesisCount;
				if (parenthesisCount == 0) {
					lowerBound = index;
					delimitingIntervals.add(new Interval<Integer>(lowerBound, upperBound));
				}
			}
		}
		return new IntervalList<Integer>(delimitingIntervals);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Type} of the specified {@code char} token.
	 * <p>
	 * @param token a {@code char} token
	 * <p>
	 * @return the {@link Type} of the specified {@code char} token
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

			case LEFT_PARENTHESIS:
				return Element.Type.LEFT_PARENTHESIS;
			case RIGHT_PARENTHESIS:
				return Element.Type.RIGHT_PARENTHESIS;
			case LEFT_BRACKET:
				return Element.Type.LEFT_BRACKET;
			case RIGHT_BRACKET:
				return Element.Type.RIGHT_BRACKET;
		}
		return Element.Type.ENTITY;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class Parser
			extends Worker<Triple<Element, String, Map<String, Element>>, Result<Element>> {

		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs a {@link Parser}.
		 */
		protected Parser() {
			super();
		}

		@Override
		public Result<Element> call(final Triple<Element, String, Map<String, Element>> input) {
			return parseExpression(input.getFirst(), input.getSecond(), input.getThird());
		}

		/**
		 * Creates a copy of {@code this}.
		 * <p>
		 * @return a copy of {@code this}
		 *
		 * @see jupiter.common.model.ICloneable
		 */
		@Override
		public Parser clone() {
			return new Parser();
		}
	}
}
