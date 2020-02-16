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

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Characters.SPACE;
import static jupiter.common.util.Strings.EMPTY;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import jupiter.common.exception.IllegalClassException;
import jupiter.common.exception.IllegalTypeException;
import jupiter.common.math.Maths;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.map.tree.ComparableRedBlackTreeMap;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.thread.LockedWorkQueue;
import jupiter.common.thread.Result;
import jupiter.common.thread.WorkQueue;
import jupiter.common.thread.Worker;
import jupiter.common.util.Strings;
import jupiter.math.calculator.model.BinaryOperation;
import jupiter.math.calculator.model.Element;
import jupiter.math.calculator.model.Element.Type;
import jupiter.math.calculator.model.MatrixElement;
import jupiter.math.calculator.model.ScalarElement;
import jupiter.math.calculator.model.UnaryOperation;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.entity.Scalar;

public class Calculator
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
	 * The flag specifying whether to parallelize using a {@link WorkQueue}.
	 */
	protected static volatile boolean PARALLELIZE = true;
	/**
	 * The {@link WorkQueue} used for evaluating the elements.
	 */
	protected static volatile WorkQueue<Pair<Element, Map<String, Element>>, Result<Entity>> WORK_QUEUE = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The context containing the values of the variables.
	 */
	protected final Map<String, Element> context = new ComparableRedBlackTreeMap<String, Element>();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Calculator}.
	 */
	public Calculator() {
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
		// • The expression handler
		ExpressionHandler.parallelize();
		// • The work queue
		if (PARALLELIZE) {
			if (WORK_QUEUE == null) {
				WORK_QUEUE = new LockedWorkQueue<Pair<Element, Map<String, Element>>, Result<Entity>>(
						new Evaluator());
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
		// • The work queue
		if (WORK_QUEUE != null) {
			WORK_QUEUE.shutdown();
		}
		// • The expression handler
		ExpressionHandler.unparallelize();
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
	 * Parses the specified expression {@link String} (assignment or simple evaluation) and
	 * evaluates it to an {@link Entity}.
	 * <p>
	 * @param expression the expression {@link String} to parse and evaluate
	 * <p>
	 * @return the {@link Entity} evaluated from the specified expression {@link String}
	 */
	public Result<Entity> process(final String expression) {
		try {
			// Trim the expression
			String trimmedExpression = expression.trim();

			// Test whether the epression is an assignment
			final ExtendedLinkedList<String> expressions = Strings.removeEmpty(
					Strings.split(trimmedExpression, '='));
			final int expressionCount = expressions.size();
			if (expressionCount > 1) {
				// • Assignment
				// Extract the right-hand side of the expression
				trimmedExpression = expressions.getLast().trim();
			}

			// Parse and evaluate the (right-hand side) expression
			final Result<Entity> result = process(trimmedExpression, context);
			final Entity entity = result.getOutput();
			if (entity == null) {
				return result;
			}

			// Get the corresponding element
			final Element element;
			if (entity instanceof Scalar) {
				element = new ScalarElement(null, trimmedExpression, (Scalar) entity);
			} else if (entity instanceof Matrix) {
				element = new MatrixElement(null, trimmedExpression, (Matrix) entity);
			} else {
				return new Result<Entity>(new IllegalClassException(entity.getClass()));
			}

			// Test whether the epression is an assignment
			if (expressionCount > 1) {
				// • Assignment
				// Set the corresponding variables
				final Iterator<String> expressionIterator = expressions.iterator();
				while (expressionIterator.hasNext()) {
					final String e = expressionIterator.next();
					if (expressionIterator.hasNext()) {
						context.put(e.trim(), element);
					}
				}
			}

			// Return the evaluation of the (right-hand side) expression
			return result;
		} catch (final Exception ex) {
			return new Result<Entity>(ex);
		}
	}

	/**
	 * Parses the specified expression {@link String} to a tree of operations and numbers and
	 * evaluates it to an {@link Entity}.
	 * <p>
	 * @param expression the expression {@link String} to parse and evaluate
	 * @param context    the context {@link Map} containing the values of the variables
	 * <p>
	 * @return the {@link Entity} evaluated from the specified expression {@link String}
	 */
	protected Result<Entity> process(final String expression, final Map<String, Element> context) {
		final Result<Element> result = ExpressionHandler.parseExpression(expression, context);
		final Element element = result.getOutput();
		if (element == null) {
			return new Result<Entity>(result.getMessage());
		}
		return evaluateTree(element, context);
	}

	/**
	 * Evaluates the specified tree {@link Element} of operations and numbers to an {@link Entity}.
	 * <p>
	 * @param tree    the root {@link Element} of the tree to evaluate
	 * @param context the context {@link Map} containing the values of the variables
	 * <p>
	 * @return the {@link Entity} evaluated from the specified tree {@link Element} of operations
	 *         and numbers
	 */
	public static Result<Entity> evaluateTree(final Element tree,
			final Map<String, Element> context) {
		if (tree instanceof ScalarElement || tree instanceof MatrixElement) {
			final Entity output = tree.getEntity();
			IO.debug("Get entity <", output, ">");
			return new Result<Entity>(output);
		} else if (tree instanceof BinaryOperation) {
			return evaluateBinaryOperation((BinaryOperation) tree, context);
		} else if (tree instanceof UnaryOperation) {
			return evaluateUnaryOperation((UnaryOperation) tree, context);
		}
		return new Result<Entity>(new IllegalClassException(tree.getClass()));
	}

	/**
	 * Evaluates the specified {@link BinaryOperation} to an {@link Entity}.
	 * <p>
	 * @param binaryOperation the {@link BinaryOperation} to evaluate
	 * @param context         the context {@link Map} containing the values of the variables
	 * <p>
	 * @return the {@link Entity} evaluated from the specified {@link BinaryOperation}
	 */
	protected static Result<Entity> evaluateBinaryOperation(final BinaryOperation binaryOperation,
			final Map<String, Element> context) {
		// Evaluate the left and right expressions
		Result<Entity> leftResult, rightResult;
		if (PARALLELIZE && WORK_QUEUE.reserveWorkers(2)) {
			// Submit the tasks
			final long leftId = WORK_QUEUE.submit(
					new Pair<Element, Map<String, Element>>(binaryOperation.getLeft(), context));
			final long rightId = WORK_QUEUE.submit(
					new Pair<Element, Map<String, Element>>(binaryOperation.getRight(), context));

			// Collect the results
			leftResult = WORK_QUEUE.get(leftId);
			rightResult = WORK_QUEUE.get(rightId);
			WORK_QUEUE.freeWorkers(2);
		} else {
			// Collect the results
			leftResult = evaluateTree(binaryOperation.getLeft(), context);
			rightResult = evaluateTree(binaryOperation.getRight(), context);
		}
		// Get the entities from the results
		// • Left entity
		final Entity leftEntity = leftResult.getOutput();
		if (leftEntity == null) {
			return leftResult;
		}
		// • Right entity
		final Entity rightEntity = rightResult.getOutput();
		if (rightEntity == null) {
			return rightResult;
		}

		// Get the type of the binary operation
		final Type type = binaryOperation.getType();
		IO.debug(leftEntity, SPACE, type, SPACE, rightEntity);

		// Evaluate the binary operation
		final Entity output;
		switch (type) {
			case ADDITION:
				output = leftEntity.plus(rightEntity);
				break;
			case SUBTRACTION:
				output = leftEntity.minus(rightEntity);
				break;
			case MULTIPLICATION:
				output = leftEntity.times(rightEntity);
				break;
			case DIVISION:
				output = leftEntity.division(rightEntity);
				break;
			case POWER:
				output = leftEntity.arrayPower(rightEntity);
				break;
			case SOLUTION:
				output = leftEntity.solve(rightEntity);
				break;
			default:
				return new Result<Entity>(new IllegalTypeException(type));
		}
		return new Result<Entity>(output);
	}

	/**
	 * Evaluates the specified {@link UnaryOperation} to an {@link Entity}.
	 * <p>
	 * @param unaryOperation the {@link UnaryOperation} to evaluate
	 * @param context        the context {@link Map} containing the values of the variables
	 * <p>
	 * @return the {@link Entity} evaluated from the specified {@link UnaryOperation}
	 */
	protected static Result<Entity> evaluateUnaryOperation(final UnaryOperation unaryOperation,
			final Map<String, Element> context) {
		// Evaluate the nested expression
		final Result<Entity> result = evaluateTree(unaryOperation.getElement(), context);
		final Entity entity = result.getOutput();
		if (entity == null) {
			return result;
		}

		// Get the type of the unary operation
		final Type type = unaryOperation.getType();
		IO.debug(type, SPACE, entity);

		// Evaluate the unary operation
		final Entity output;
		switch (type) {
			case FACTORIAL:
				final Scalar scalar = (Scalar) entity;
				output = new Scalar(Maths.factorial(scalar.get()));
				break;
			case INVERSE:
				output = entity.inverse();
				break;
			case TRANSPOSE:
				output = entity.transpose();
				break;
			default:
				return new Result<Entity>(new IllegalTypeException(type));
		}
		return new Result<Entity>(output);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class Evaluator
			extends Worker<Pair<Element, Map<String, Element>>, Result<Entity>> {

		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs an {@link Evaluator}.
		 */
		protected Evaluator() {
			super();
		}

		@Override
		public Result<Entity> call(final Pair<Element, Map<String, Element>> input) {
			return Calculator.evaluateTree(input.getFirst(), input.getSecond());
		}

		/**
		 * Creates a copy of {@code this}.
		 * <p>
		 * @return a copy of {@code this}
		 *
		 * @see jupiter.common.model.ICloneable
		 */
		@Override
		public Evaluator clone() {
			return new Evaluator();
		}
	}
}
