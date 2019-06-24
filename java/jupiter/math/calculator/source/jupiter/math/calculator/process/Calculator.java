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
import static jupiter.common.util.Strings.SPACE;

import java.util.List;
import java.util.Map;

import jupiter.common.exception.IllegalClassException;
import jupiter.common.exception.IllegalTypeException;
import jupiter.common.io.Message;
import jupiter.common.math.Maths;
import jupiter.common.struct.map.tree.RedBlackTreeMap;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.thread.LockedWorkQueue;
import jupiter.common.thread.Report;
import jupiter.common.thread.WorkQueue;
import jupiter.common.thread.Worker;
import jupiter.common.util.Strings;
import jupiter.math.calculator.model.BinaryOperation;
import jupiter.math.calculator.model.Element;
import jupiter.math.calculator.model.Element.Type;
import jupiter.math.calculator.model.MatrixElement;
import jupiter.math.calculator.model.Result;
import jupiter.math.calculator.model.ScalarElement;
import jupiter.math.calculator.model.UnaryOperation;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.entity.Scalar;

public class Calculator {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether to parallelize using a work queue.
	 */
	protected static volatile boolean PARALLELIZE = true;
	/**
	 * The work queue for evaluating the elements.
	 */
	protected static volatile WorkQueue<Pair<Element, Map<String, Element>>, Report<Entity>> WORK_QUEUE = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The context containing the values of the variables.
	 */
	protected final Map<String, Element> context = new RedBlackTreeMap<String, Element>();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Calculator() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parallelizes {@code this}.
	 */
	public static synchronized void parallelize() {
		IO.debug(EMPTY);

		// Initialize
		// - The expression handler
		ExpressionHandler.parallelize();
		// - The work queue
		if (PARALLELIZE) {
			if (WORK_QUEUE == null) {
				WORK_QUEUE = new LockedWorkQueue<Pair<Element, Map<String, Element>>, Report<Entity>>(
						new Evaluator());
			} else {
				IO.warn("The work queue ", WORK_QUEUE, " has already started");
			}
		}
	}

	/**
	 * Unparallelizes {@code this}.
	 */
	public static synchronized void unparallelize() {
		IO.debug(EMPTY);

		// Shutdown
		// - The work queue
		if (WORK_QUEUE != null) {
			WORK_QUEUE.shutdown();
		}
		// - The expression handler
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
	 * Parses and evaluates the specified expression (assignment or simple evaluation) and returns
	 * the result.
	 * <p>
	 * @param expression the {@link String} to parse and evaluate
	 * <p>
	 * @return the {@link Result} evaluated from the specified expression
	 */
	public Result process(final String expression) {
		try {
			// Trim the expression
			String trimmedExpression = expression.trim();

			// Test whether the epression is an assignment
			final List<String> expressions = Strings
					.removeEmpty(Strings.split(trimmedExpression, '='));
			final int size = expressions.size();
			if (size > 1) {
				// - Assignment
				// Extract the right-hand side of the expression
				trimmedExpression = expressions.get(size - 1).trim();
			}

			// Parse and evaluate the (right-hand side) expression
			final Report<Entity> result = process(trimmedExpression, context);
			final Entity entity = result.getOutput();
			if (entity == null) {
				return new Result(null, result.getMessage());
			}

			// Get the corresponding element
			final Element element;
			if (entity instanceof Scalar) {
				element = new ScalarElement(null, trimmedExpression, (Scalar) entity);
			} else if (entity instanceof Matrix) {
				element = new MatrixElement(null, trimmedExpression, (Matrix) entity);
			} else {
				return new Result(null, new Message(new IllegalClassException(entity.getClass())));
			}

			// Test whether the epression is an assignment
			if (size > 1) {
				// - Assignment
				// Set the corresponding variables
				for (int i = 0; i < size - 1; ++i) {
					context.put(expressions.get(i).trim(), element);
				}
			}

			// Return the evaluation of the (right-hand side) expression
			return new Result(entity, null);
		} catch (final Exception ex) {
			return new Result(null, IO.error(ex));
		}
	}

	/**
	 * Parses the specified expression to a tree of operations and numbers and evaluates it.
	 * <p>
	 * @param expression the {@link String} to parse and evaluate
	 * @param context    the context containing the values of the variables
	 * <p>
	 * @return the {@link Entity} evaluated from the specified expression
	 */
	protected Report<Entity> process(final String expression, final Map<String, Element> context) {
		final Report<Element> result = ExpressionHandler.parseExpression(expression, context);
		final Element element = result.getOutput();
		if (element == null) {
			return new Report<Entity>(result.getMessage());
		}
		return evaluateTree(element, context);
	}

	/**
	 * Evaluates the specified tree of operations and numbers.
	 * <p>
	 * @param tree    the root {@link Element} of the tree to evaluate
	 * @param context the context containing the values of the variables
	 * <p>
	 * @return the {@link Entity} evaluated from the specified tree of operations and numbers
	 */
	public static Report<Entity> evaluateTree(final Element tree,
			final Map<String, Element> context) {
		if (tree instanceof ScalarElement || tree instanceof MatrixElement) {
			final Entity entity = tree.getEntity();
			IO.debug("Get entity <", entity, ">");
			return new Report<Entity>(entity);
		} else if (tree instanceof BinaryOperation) {
			return evaluateBinaryOperation((BinaryOperation) tree, context);
		} else if (tree instanceof UnaryOperation) {
			return evaluateUnaryOperation((UnaryOperation) tree, context);
		}
		return new Report<Entity>(new IllegalClassException(tree.getClass()));
	}

	/**
	 * Evaluates the specified binary operation.
	 * <p>
	 * @param binaryOperation the {@link BinaryOperation} to evaluate
	 * @param context         the context containing the values of the variables
	 * <p>
	 * @return the {@link Entity} evaluated from the specified {@link BinaryOperation}
	 */
	protected static Report<Entity> evaluateBinaryOperation(final BinaryOperation binaryOperation,
			final Map<String, Element> context) {
		// Evaluate the left and right expressions
		Report<Entity> leftEntityResult, rightEntityResult;
		if (PARALLELIZE && WORK_QUEUE.reserveWorkers(2)) {
			// Submit the tasks
			final long leftId = WORK_QUEUE.submit(
					new Pair<Element, Map<String, Element>>(binaryOperation.getLeft(), context));
			final long rightId = WORK_QUEUE.submit(
					new Pair<Element, Map<String, Element>>(binaryOperation.getRight(), context));

			// Get the results
			leftEntityResult = WORK_QUEUE.get(leftId);
			rightEntityResult = WORK_QUEUE.get(rightId);
			WORK_QUEUE.freeWorkers(2);
		} else {
			// Get the results
			leftEntityResult = evaluateTree(binaryOperation.getLeft(), context);
			rightEntityResult = evaluateTree(binaryOperation.getRight(), context);
		}
		// Get the entities from the results
		// - Left entity
		final Entity leftEntity = leftEntityResult.getOutput();
		if (leftEntity == null) {
			return leftEntityResult;
		}
		// - Right entity
		final Entity rightEntity = rightEntityResult.getOutput();
		if (rightEntity == null) {
			return rightEntityResult;
		}

		// Get the type of the binary operation
		final Type type = binaryOperation.getType();
		IO.debug(leftEntity, SPACE, type, SPACE, rightEntity);

		// Evaluate the binary operation
		final Entity result;
		switch (type) {
			case ADDITION:
				result = leftEntity.plus(rightEntity);
				break;
			case SUBTRACTION:
				result = leftEntity.minus(rightEntity);
				break;
			case MULTIPLICATION:
				result = leftEntity.times(rightEntity);
				break;
			case DIVISION:
				result = leftEntity.division(rightEntity);
				break;
			case POWER:
				result = leftEntity.arrayPower(rightEntity);
				break;
			case SOLUTION:
				result = leftEntity.solve(rightEntity);
				break;
			default:
				return new Report<Entity>(new IllegalTypeException(type));
		}

		return new Report<Entity>(result);
	}

	/**
	 * Evaluates the specified unary operation.
	 * <p>
	 * @param unaryOperation the {@link UnaryOperation} to evaluate
	 * @param context        the context containing the values of the variables
	 * <p>
	 * @return the {@link Entity} evaluated from the specified {@link UnaryOperation}
	 */
	protected static Report<Entity> evaluateUnaryOperation(final UnaryOperation unaryOperation,
			final Map<String, Element> context) {
		// Evaluate the nested expression
		final Report<Entity> entityResult = evaluateTree(unaryOperation.getElement(), context);
		final Entity entity = entityResult.getOutput();
		if (entity == null) {
			return new Report<Entity>(entityResult.getMessage());
		}

		// Get the type of the unary operation
		final Type type = unaryOperation.getType();
		IO.debug(type, SPACE, entity);

		// Evaluate the unary operation
		final Entity result;
		switch (type) {
			case FACTORIAL:
				final Scalar scalar = (Scalar) entity;
				result = new Scalar(Maths.factorial(scalar.get()));
				break;
			case INVERSE:
				result = entity.inverse();
				break;
			case TRANSPOSE:
				result = entity.transpose();
				break;
			default:
				return new Report<Entity>(new IllegalTypeException(type));
		}

		return new Report<Entity>(result);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class Evaluator
			extends Worker<Pair<Element, Map<String, Element>>, Report<Entity>> {

		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		protected Evaluator() {
			super();
		}

		@Override
		public Report<Entity> call(final Pair<Element, Map<String, Element>> input) {
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
