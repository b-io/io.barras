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
package jupiter.math.calculator.process;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.LEFT_BRACKET;
import static jupiter.common.util.Characters.LEFT_PARENTHESIS;
import static jupiter.common.util.Characters.RIGHT_BRACKET;
import static jupiter.common.util.Characters.RIGHT_PARENTHESIS;
import static jupiter.common.util.Strings.EMPTY;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import jupiter.common.exception.ParseException;
import jupiter.common.math.Interval;
import jupiter.common.math.IntervalList;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.list.Index;
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
import jupiter.math.linear.entity.Entity;
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

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link ExtendedList} of unary operators.
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	protected static final ExtendedList<ExtendedList<Character>> UNARY_OPERATORS = new ExtendedList<ExtendedList<Character>>(
			Arrays.<Character>asList('!', '\''));
	/**
	 * The {@link ExtendedList} of binary operators.
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	protected static final ExtendedList<ExtendedList<Character>> BINARY_OPERATORS = new ExtendedList<ExtendedList<Character>>(
			Arrays.<Character>asList('+', '-'),
			Arrays.<Character>asList('*', '/', '%'),
			Arrays.<Character>asList('^'),
			Arrays.<Character>asList('~'));

	/**
	 * The {@link ExtendedList} of argument delimiters.
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	protected static final ExtendedList<ExtendedList<Character>> ARGUMENT_DELIMITERS = new ExtendedList<ExtendedList<Character>>(
			Arrays.<Character>asList(Arrays.DELIMITER));
	/**
	 * The {@link ExtendedList} of univariate functions.
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	protected static final ExtendedList<String> UNIVARIATE_FUNCTIONS = new ExtendedList<String>(
			Element.Type.MAGIC.toString().toLowerCase(),
			Element.Type.RANDOM.toString().toLowerCase(),

			Element.Type.ABS.toString().toLowerCase(),
			Element.Type.EXP.toString().toLowerCase(),
			Element.Type.INV.toString().toLowerCase(),
			Element.Type.LOG.toString().toLowerCase(),
			Element.Type.ROOT.toString().toLowerCase(),

			Element.Type.FLOOR.toString().toLowerCase(),
			Element.Type.CEIL.toString().toLowerCase(),
			Element.Type.ROUND.toString().toLowerCase(),

			Element.Type.COSH.toString().toLowerCase(),
			Element.Type.COS.toString().toLowerCase(),
			Element.Type.SINH.toString().toLowerCase(),
			Element.Type.SIN.toString().toLowerCase(),
			Element.Type.TANH.toString().toLowerCase(),
			Element.Type.TAN.toString().toLowerCase(),
			Element.Type.HAV.toString().toLowerCase());
	/**
	 * The {@link ExtendedList} of bivariate functions.
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	protected static final ExtendedList<String> BIVARIATE_FUNCTIONS = new ExtendedList<String>(
			Element.Type.MIN.toString().toLowerCase(),
			Element.Type.MAX.toString().toLowerCase());

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The threshold on the length of the expressions to parallelize.
	 */
	protected static volatile int THRESHOLD = 100;
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
	// CONTROLLERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parallelizes {@code this}.
	 */
	public static synchronized void parallelize() {
		IO.trace(EMPTY);

		// Initialize
		if (WORK_QUEUE == null) {
			WORK_QUEUE = new LockedWorkQueue<Triple<Element, String, Map<String, Element>>, Result<Element>>(
					new Parser());
		} else {
			IO.trace("The work queue ", WORK_QUEUE, " has already started");
		}
	}

	/**
	 * Unparallelizes {@code this}.
	 */
	public static synchronized void unparallelize() {
		IO.trace(EMPTY);

		// Shutdown
		if (WORK_QUEUE != null) {
			WORK_QUEUE.shutdown();
			WORK_QUEUE = null;
		}
	}

	/**
	 * Reparallelizes {@code this}.
	 */
	public static synchronized void reparallelize() {
		IO.trace(EMPTY);

		unparallelize();
		parallelize();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PARSERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the root {@link Element} of the tree whose nodes and leaves correspond respectively
	 * to the operations and {@link Entity} of the specified expression {@link String} with the
	 * specified context {@link Map}.
	 * <p>
	 * @param expression the expression {@link String} to parse
	 * @param context    the context {@link Map} containing the values of the variables
	 * <p>
	 * @return the root {@link Element} of the tree whose nodes and leaves correspond respectively
	 *         to the operations and {@link Entity} of the specified expression {@link String} with
	 *         the specified context {@link Map}
	 */
	public static Result<Element> parseExpression(final String expression,
			final Map<String, Element> context) {
		return parseExpression(null, expression, context);
	}

	/**
	 * Returns a node or leaf {@link Element} with the specified parent {@link Element}
	 * corresponding respectively to an operation or an {@link Entity} parsed from the specified
	 * expression {@link String} with the specified context {@link Map}.
	 * <p>
	 * @param parent     the parent {@link Element} of the expression {@link String} to parse
	 * @param expression the expression {@link String} to parse
	 * @param context    the context {@link Map} containing the values of the variables
	 * <p>
	 * @return a node or leaf {@link Element} with the specified parent {@link Element}
	 *         corresponding respectively to an operation or an {@link Entity} parsed from the
	 *         specified expression {@link String} with the specified context {@link Map}
	 */
	public static Result<Element> parseExpression(final Element parent, final String expression,
			final Map<String, Element> context) {
		// Trim the expression
		final String trimmedExpression = expression.trim();
		IO.debug(trimmedExpression);

		// Find the delimiting intervals
		final IntervalList<Integer> delimitingIntervals = getDelimitingIntervals(trimmedExpression);

		// Parse the expression
		// • Binary operator
		Result<Element> result = parseBinaryOperator(parent, trimmedExpression, delimitingIntervals,
				context);
		if (result != null) {
			return result;
		}
		// • Unary operator
		result = parseUnaryOperator(parent, trimmedExpression, delimitingIntervals, context);
		if (result != null) {
			return result;
		}
		// • Univariate function
		result = parseUnivariateFunction(parent, trimmedExpression, delimitingIntervals, context);
		if (result != null) {
			return result;
		}
		// • Bivariate function
		result = parseBivariateFunction(parent, trimmedExpression, delimitingIntervals, context);
		if (result != null) {
			return result;
		}
		// • Nested expression
		result = parseNestedExpression(parent, trimmedExpression, delimitingIntervals, context);
		if (result != null) {
			return result;
		}
		// • Entity
		return parseEntity(parent, trimmedExpression, delimitingIntervals, context);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a node {@link Element} with the specified parent {@link Element} corresponding to an
	 * unary operator parsed from the specified expression {@link String} with the specified context
	 * {@link Map}, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param parent              the parent {@link Element} of the expression {@link String} to
	 *                            parse
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * @param context             the context {@link Map} containing the values of the variables
	 * <p>
	 * @return a node {@link Element} with the specified parent {@link Element} corresponding to an
	 *         unary operator parsed from the specified expression {@link String} with the specified
	 *         context {@link Map}, or {@code null} if there is no such occurrence
	 */
	protected static Result<Element> parseUnaryOperator(final Element parent,
			final String expression, final IntervalList<Integer> delimitingIntervals,
			final Map<String, Element> context) {
		// Get the index of the last unary operator
		final int unaryOperatorIndex = getLastUnaryOperatorIndex(expression, delimitingIntervals);
		if (unaryOperatorIndex < 0) {
			return null;
		}
		// Get the type of the last unary operator
		final char unaryOperator = expression.charAt(unaryOperatorIndex);
		final Element.Type type = getType(unaryOperator);
		IO.debug("Type: ", type);
		// Parse the unary operation
		final String nestedExpression = Strings.removeEmpty(
				Strings.split(expression, unaryOperator)).get(0);
		IO.debug("Nested expression: ", nestedExpression);
		return parseUnaryOperation(type, parent, nestedExpression, context);
	}

	/**
	 * Returns a node {@link Element} with the specified parent {@link Element} corresponding to a
	 * univariate function parsed from the specified expression {@link String} with the specified
	 * context {@link Map}, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param parent              the parent {@link Element} of the expression {@link String} to
	 *                            parse
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * @param context             the context {@link Map} containing the values of the variables
	 * <p>
	 * @return a node {@link Element} with the specified parent {@link Element} corresponding to a
	 *         univariate function parsed from the specified expression {@link String} with the
	 *         specified context {@link Map}, or {@code null} if there is no such occurrence
	 */
	protected static Result<Element> parseUnivariateFunction(final Element parent,
			final String expression, final IntervalList<Integer> delimitingIntervals,
			final Map<String, Element> context) {
		// Get the index of the last univariate function
		final Index<String> univariateFunctionIndex = getLastUnivariateFunctionIndex(expression,
				delimitingIntervals);
		if (univariateFunctionIndex == null) {
			return null;
		}
		// Get the type of the last univariate function
		final int index = univariateFunctionIndex.getIndex();
		final String function = univariateFunctionIndex.getToken();
		final Element.Type type = getType(function);
		IO.debug("Type: ", type);
		// Parse the unary operation
		final String nestedExpression = expression.substring(index + function.length()).trim();
		IO.debug("Nested expression: ", nestedExpression);
		return parseUnaryOperation(type, parent, nestedExpression, context);
	}

	protected static Result<Element> parseUnaryOperation(final Element.Type type,
			final Element parent, final String expression, final Map<String, Element> context) {
		// Parse the nested expression
		final Result<Element> nodeResult = parseExpression(parent, expression, context);
		final Element node = nodeResult.getOutput();
		if (node == null) {
			return nodeResult;
		}

		// Return the unary operation
		IO.debug("Create new ", type, " Node: <", node.getExpression(), ">");
		return new Result<Element>(new UnaryOperation(parent, expression, type, node));
	}

	//////////////////////////////////////////////

	/**
	 * Returns a node {@link Element} with the specified parent {@link Element} corresponding to a
	 * binary operator parsed from the specified expression {@link String} with the specified
	 * context {@link Map}, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param parent              the parent {@link Element} of the expression {@link String} to
	 *                            parse
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * @param context             the context {@link Map} containing the values of the variables
	 * <p>
	 * @return a node {@link Element} with the specified parent {@link Element} corresponding to a
	 *         binary operator parsed from the specified expression {@link String} with the
	 *         specified context {@link Map}, or {@code null} if there is no such occurrence
	 */
	protected static Result<Element> parseBinaryOperator(final Element parent,
			final String expression, final IntervalList<Integer> delimitingIntervals,
			final Map<String, Element> context) {
		// Get the index of the middle binary operator
		final int binaryOperatorIndex = getMiddleBinaryOperatorIndex(expression,
				delimitingIntervals);
		if (binaryOperatorIndex < 0) {
			return null;
		}
		// Get the type of the middle binary operator
		final Element.Type type = getType(expression.charAt(binaryOperatorIndex));
		IO.debug("Type: ", type);
		// Parse the binary operation
		return parseBinaryOperation(type, parent, expression, binaryOperatorIndex, context);
	}

	/**
	 * Returns a node {@link Element} with the specified parent {@link Element} corresponding to a
	 * bivariate function parsed from the specified expression {@link String} with the specified
	 * context {@link Map}, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param parent              the parent {@link Element} of the expression {@link String} to
	 *                            parse
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * @param context             the context {@link Map} containing the values of the variables
	 * <p>
	 * @return a node {@link Element} with the specified parent {@link Element} corresponding to a
	 *         bivariate function parsed from the specified expression {@link String} with the
	 *         specified context {@link Map}, or {@code null} if there is no such occurrence
	 */
	protected static Result<Element> parseBivariateFunction(final Element parent,
			final String expression, final IntervalList<Integer> delimitingIntervals,
			final Map<String, Element> context) {
		// Get the index of the last bivariate function
		final Index<String> bivariateFunctionIndex = getLastBivariateFunctionIndex(expression,
				delimitingIntervals);
		if (bivariateFunctionIndex == null) {
			return null;
		}
		// Get the type of the last bivariate function
		final String function = bivariateFunctionIndex.getToken();
		final Element.Type type = getType(function);
		IO.debug("Type: ", type);
		// Parse the binary operation
		final String nestedExpression = getNestedExpression(expression,
				delimitingIntervals.getLast()).trim();
		IO.debug("Nested expression: ", nestedExpression);
		final int argumentDelimiterIndex = getArgumentDelimiterIndex(nestedExpression,
				getDelimitingIntervals(nestedExpression));
		if (argumentDelimiterIndex < 0) {
			return null;
		}
		return parseBinaryOperation(type, parent, nestedExpression, argumentDelimiterIndex,
				context);
	}

	protected static Result<Element> parseBinaryOperation(final Element.Type type,
			final Element parent, final String expression, final int binaryOperatorIndex,
			final Map<String, Element> context) {
		// Extract the left and right expressions
		final String leftExpression = expression.substring(0, binaryOperatorIndex);
		final String rightExpression = expression.substring(binaryOperatorIndex + 1);

		// Parse the left and right expressions
		final Element leftNode, rightNode;
		final Result<Element> leftNodeResult, rightNodeResult;
		if ((leftExpression.length() > THRESHOLD || rightExpression.length() > THRESHOLD) &&
				WORK_QUEUE != null && WORK_QUEUE.reserveWorkers(2)) {
			// Submit the tasks
			final long leftId = WORK_QUEUE.submit(new Triple<Element, String, Map<String, Element>>(
					parent, leftExpression, context));
			final long rightId = WORK_QUEUE
					.submit(new Triple<Element, String, Map<String, Element>>(parent,
							rightExpression, context));

			// Collect the results
			leftNodeResult = WORK_QUEUE.get(leftId);
			rightNodeResult = WORK_QUEUE.get(rightId);
			WORK_QUEUE.freeWorkers(2);
		} else {
			// Collect the results
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

	//////////////////////////////////////////////

	/**
	 * Returns a node {@link Element} with the specified parent {@link Element} corresponding to a
	 * nested expression parsed from the specified expression {@link String} with the specified
	 * context {@link Map}, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param parent              the parent {@link Element} of the expression {@link String} to
	 *                            parse
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * @param context             the context {@link Map} containing the values of the variables
	 * <p>
	 * @return a node {@link Element} with the specified parent {@link Element} corresponding to a
	 *         nested expression parsed from the specified expression {@link String} with the
	 *         specified context {@link Map}, or {@code null} if there is no such occurrence
	 */
	protected static Result<Element> parseNestedExpression(final Element parent,
			final String expression, final IntervalList<Integer> delimitingIntervals,
			final Map<String, Element> context) {
		// Parse the nested expression
		if (delimitingIntervals.isValid() && delimitingIntervals.size() == 1) {
			final String nestedExpression = getNestedExpression(expression,
					delimitingIntervals.get(0));
			if (nestedExpression != null) {
				IO.debug("Nested expression: ", nestedExpression);
				return parseExpression(parent, nestedExpression, context);
			}
		}
		return null;
	}

	protected static String getNestedExpression(final String expression,
			final Interval<Integer> interval) {
		if (Characters.isParenthesis(expression.charAt(interval.getLowerBound().getValue())) &&
				Characters.isParenthesis(expression.charAt(interval.getUpperBound().getValue()))) {
			return expression.substring(interval.getLowerBound().getValue() + 1,
					interval.getUpperBound().getValue());
		}
		return null;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a leaf {@link Element} with the specified parent {@link Element} corresponding to an
	 * {@link Entity} parsed from the specified expression {@link String} with the specified context
	 * {@link Map}.
	 * <p>
	 * @param parent              the parent {@link Element} of the expression {@link String} to
	 *                            parse
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * @param context             the context {@link Map} containing the values of the variables
	 * <p>
	 * @return a leaf {@link Element} with the specified parent {@link Element} corresponding to an
	 *         {@link Entity} parsed from the specified expression {@link String} with the specified
	 *         context {@link Map}
	 */
	protected static Result<Element> parseEntity(final Element parent, final String expression,
			final IntervalList<Integer> delimitingIntervals, final Map<String, Element> context) {
		IO.debug("Create new Leaf: <", expression, ">");
		final Element entity;
		if (context.containsKey(expression)) {
			// Parse the variable
			entity = context.get(expression);
		} else if (Matrix.isParsableFrom(expression)) {
			// Parse the matrix
			entity = new MatrixElement(parent, expression);
		} else {
			// Parse the scalar
			try {
				entity = new ScalarElement(parent, expression);
			} catch (final NumberFormatException ex) {
				return new Result<Element>(
						new ParseException("Unparsable element: <" + expression + ">", ex));
			}
		}
		entity.setParent(parent);
		entity.setExpression(expression);
		return new Result<Element>(entity);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the last unary operator in the specified expression {@link String} that
	 * is not in the specified delimiting intervals, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * <p>
	 * @return the index of the last unary operator in the specified expression {@link String} that
	 *         is not in the specified delimiting intervals, or {@code -1} if there is no such
	 *         occurrence
	 */
	protected static int getLastUnaryOperatorIndex(final String expression,
			final IntervalList<Integer> delimitingIntervals) {
		return getLastOperatorIndexFromList(expression, delimitingIntervals,
				expression.length() - 1, UNARY_OPERATORS);
	}

	/**
	 * Returns the index of the middle binary operator in the specified expression {@link String},
	 * or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * <p>
	 * @return the index of the middle binary operator in the specified expression {@link String},
	 *         or {@code -1} if there is no such occurrence
	 */
	protected static int getMiddleBinaryOperatorIndex(final String expression,
			final IntervalList<Integer> delimitingIntervals) {
		final ExtendedLinkedList<Integer> indices = getBinaryOperatorIndices(expression,
				delimitingIntervals);
		IO.debug("Indices: ", indices);
		if (indices.isNonEmpty()) {
			return indices.getMiddle();
		}
		return -1;
	}

	/**
	 * Returns the indices of all the binary operators in the specified expression {@link String}
	 * that are not in the specified delimiting intervals.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * <p>
	 * @return the indices of all the binary operators in the specified expression {@link String}
	 *         that are not in the specified delimiting intervals
	 */
	protected static ExtendedLinkedList<Integer> getBinaryOperatorIndices(final String expression,
			final IntervalList<Integer> delimitingIntervals) {
		return getOperatorIndices(expression, delimitingIntervals, expression.length() - 1,
				BINARY_OPERATORS);
	}

	/**
	 * Returns the index of the argument delimiter in the specified expression {@link String}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * <p>
	 * @return the index of the argument delimiter in the specified expression {@link String}, or
	 *         {@code -1} if there is no such occurrence
	 */
	protected static int getArgumentDelimiterIndex(final String expression,
			final IntervalList<Integer> delimitingIntervals) {
		return getLastOperatorIndexFromList(expression, delimitingIntervals,
				expression.length() - 1, ARGUMENT_DELIMITERS);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the indices of all the operators in the specified expression {@link String} that are
	 * not in the specified delimiting intervals.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * @param fromIndex           the index to start seeking backward from (inclusive)
	 * @param allOperators        the {@link List} of all the operators to find
	 * <p>
	 * @return the indices of all the operators in the specified expression {@link String} that are
	 *         not in the specified delimiting intervals
	 */
	protected static ExtendedLinkedList<Integer> getOperatorIndices(final String expression,
			final IntervalList<Integer> delimitingIntervals, final int fromIndex,
			final List<? extends List<Character>> allOperators) {
		// Initialize
		final ExtendedLinkedList<Integer> indices = new ExtendedLinkedList<Integer>();
		final int allOperatorCount = allOperators.size();
		int binaryOperatorsIndex = 0;

		// Return the operator indices
		do {
			final List<Character> operators = allOperators.get(binaryOperatorsIndex);
			int index = fromIndex;
			while ((index = getLastOperatorIndex(expression, delimitingIntervals, index,
					operators)) >= 0) {
				indices.add(index);
				--index;
			}
			++binaryOperatorsIndex;
		} while (binaryOperatorsIndex < allOperatorCount && indices.isEmpty());
		return indices;
	}

	/**
	 * Returns the index of the last operator in the specified expression {@link String} that is not
	 * in the specified delimiting intervals, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * @param fromIndex           the index to start seeking backward from (inclusive)
	 * @param operators           the {@link List} of operators to find
	 * <p>
	 * @return the index of the last operator in the specified expression {@link String} that is not
	 *         in the specified delimiting intervals, or {@code -1} if there is no such occurrence
	 */
	protected static int getLastOperatorIndex(final String expression,
			final IntervalList<Integer> delimitingIntervals, final int fromIndex,
			final List<Character> operators) {
		int index = fromIndex;
		while ((index = Strings.findLast(expression, operators, index)) >= 0 &&
				delimitingIntervals.isValid() && delimitingIntervals.isInside(index)) {
			--index;
		}
		return index;
	}

	/**
	 * Returns the index of the last operator in the specified expression {@link String} that is not
	 * in the specified delimiting intervals, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * @param fromIndex           the index to start seeking backward from (inclusive)
	 * @param allOperators        the {@link List} of all the operators to find
	 * <p>
	 * @return the index of the last operator in the specified expression {@link String} that is not
	 *         in the specified delimiting intervals, or {@code -1} if there is no such occurrence
	 */
	protected static int getLastOperatorIndexFromList(final String expression,
			final IntervalList<Integer> delimitingIntervals, final int fromIndex,
			final List<? extends List<Character>> allOperators) {
		// Initialize
		final int allOperatorCount = allOperators.size();

		// Return the last operator index
		int index, binaryOperatorsIndex = 0;
		do {
			index = getLastOperatorIndex(expression, delimitingIntervals, fromIndex,
					allOperators.get(binaryOperatorsIndex));
			++binaryOperatorsIndex;
		} while (index < 0 && binaryOperatorsIndex < allOperatorCount);
		return index;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link Index} of the last univariate function in the specified expression
	 * {@link String} that is not in the specified delimiting intervals, or {@code null} if there is
	 * no such occurrence.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * <p>
	 * @return the {@link Index} of the last univariate function in the specified expression
	 *         {@link String} that is not in the specified delimiting intervals, or {@code null} if
	 *         there is no such occurrence
	 */
	protected static Index<String> getLastUnivariateFunctionIndex(final String expression,
			final IntervalList<Integer> delimitingIntervals) {
		Index<String> index = null;
		int i = expression.length() - 1;
		while ((index = Strings.findLastString(expression, UNIVARIATE_FUNCTIONS, i)) != null &&
				delimitingIntervals.isValid() && delimitingIntervals.isInside(index.getIndex())) {
			i = index.getIndex() - 1;
		}
		return index;
	}

	/**
	 * Returns the {@link Index} of the last bivariate function in the specified expression
	 * {@link String} that is not in the specified delimiting intervals, or {@code null} if there is
	 * no such occurrence.
	 * <p>
	 * @param expression          the expression {@link String} to parse
	 * @param delimitingIntervals the delimiting intervals in the expression {@link String} to parse
	 * <p>
	 * @return the {@link Index} of the last bivariate function in the specified expression
	 *         {@link String} that is not in the specified delimiting intervals, or {@code null} if
	 *         there is no such occurrence
	 */
	protected static Index<String> getLastBivariateFunctionIndex(final String expression,
			final IntervalList<Integer> delimitingIntervals) {
		Index<String> index = null;
		int i = expression.length() - 1;
		while ((index = Strings.findLastString(expression, BIVARIATE_FUNCTIONS, i)) != null &&
				delimitingIntervals.isValid() && delimitingIntervals.isInside(index.getIndex())) {
			i = index.getIndex() - 1;
		}
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
		final IntervalList<Integer> delimitingIntervals = new IntervalList<Integer>();

		// Return the delimiting intervals
		int parenthesisCount = 0, fromIndex, toIndex = -1;
		for (int i = expression.length() - 1; i >= 0; --i) {
			final Element.Type type = getType(expression.charAt(i));
			if (type == Element.Type.RIGHT_PARENTHESIS || type == Element.Type.RIGHT_BRACKET) {
				if (parenthesisCount == 0) {
					toIndex = i;
				}
				++parenthesisCount;
			} else if (type == Element.Type.LEFT_PARENTHESIS || type == Element.Type.LEFT_BRACKET) {
				--parenthesisCount;
				if (parenthesisCount == 0) {
					fromIndex = i;
					delimitingIntervals.add(new Interval<Integer>(fromIndex, toIndex));
				}
			}
		}
		return delimitingIntervals;
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
			// • Binary operators
			case '+':
				return Element.Type.ADDITION;
			case '-':
				return Element.Type.SUBTRACTION;
			case '*':
				return Element.Type.MULTIPLICATION;
			case '/':
				return Element.Type.DIVISION;
			case '%':
				return Element.Type.MODULO;
			case '^':
				return Element.Type.POWER;
			case '~':
				return Element.Type.SOLUTION;

			// • Unary operators
			case '!':
				return Element.Type.FACTORIAL;
			case '\'':
				return Element.Type.TRANSPOSE;

			// • Nested expressions
			case LEFT_PARENTHESIS:
				return Element.Type.LEFT_PARENTHESIS;
			case RIGHT_PARENTHESIS:
				return Element.Type.RIGHT_PARENTHESIS;
			case LEFT_BRACKET:
				return Element.Type.LEFT_BRACKET;
			case RIGHT_BRACKET:
				return Element.Type.RIGHT_BRACKET;
		}
		// • Entities
		return Element.Type.ENTITY;
	}

	/**
	 * Returns the {@link Type} of the specified token {@link String}.
	 * <p>
	 * @param token a token {@link String}
	 * <p>
	 * @return the {@link Type} of the specified token {@link String}
	 */
	protected static Element.Type getType(final String token) {
		// • Univariate and bivariate functions
		return Element.Type.valueOf(token.toUpperCase());
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
		 * Clones {@code this}.
		 * <p>
		 * @return a clone of {@code this}
		 *
		 * @see ICloneable
		 */
		@Override
		public Parser clone() {
			return new Parser();
		}
	}
}
