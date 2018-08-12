/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io>
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

import java.math.BigInteger;
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

	// The option specifying whether to use threads
	protected static final boolean USE_THREADS = true;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	// The pool of threads
	protected static LockedWorkQueue<Pair<Element, Map<String, Element>>, Report<Entity>> THREAD_POOL = null;
	// The context containing the values of the variables
	protected volatile Map<String, Element> context;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Calculator() {
		context = new RedBlackTreeMap<String, Element>();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CALCULATOR
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Initializes the working threads.
	 */
	public static void start() {
		IO.debug("");
		stop();
		if (USE_THREADS) {
			THREAD_POOL = new LockedWorkQueue<Pair<Element, Map<String, Element>>, Report<Entity>>(
					new Evaluator());
		}
		ExpressionHandler.start();
	}

	/**
	 * Stops the thread pool.
	 */
	public static void stop() {
		IO.debug("");
		ExpressionHandler.stop();
		if (USE_THREADS) {
			if (THREAD_POOL != null) {
				THREAD_POOL.shutdown();
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Processes the specified expression (assignment or simple evaluation).
	 * <p>
	 * @param expression the {@link String} to parse
	 * <p>
	 * @return the evaluation of {@code expression}
	 */
	public Result process(final String expression) {
		try {
			// Trim the expression
			String trimmedExpression = expression.trim();

			// Test whether the epression is an assignment
			final List<String> expressions = Strings.split(trimmedExpression, '=');
			final int size = expressions.size();
			if (size > 1) {
				// - Assignment
				// Extract the right-hand side of the expression
				trimmedExpression = expressions.get(size - 1).trim();
			}

			// Evaluate the (right-hand side) expression
			final Report<Entity> result = evaluate(trimmedExpression, context);
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
	 * @param expression the {@link String} to parse
	 * @param context    the context containing the values of the variables
	 * <p>
	 * @return the evaluation of {@code expression}
	 */
	protected Report<Entity> evaluate(final String expression, final Map<String, Element> context) {
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
	 * @param tree    the {@link Element} to evaluate
	 * @param context the context containing the values of the variables
	 * <p>
	 * @return the evaluation of {@code tree}
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
	 * @return the evaluation of {@code binaryOperation}
	 */
	protected static Report<Entity> evaluateBinaryOperation(final BinaryOperation binaryOperation,
			final Map<String, Element> context) {
		// Evaluate the left and right expressions
		Report<Entity> leftEntityResult, rightEntityResult;
		if (USE_THREADS && THREAD_POOL.reserveWorkers(2)) {
			// Submit the tasks
			final long leftId = THREAD_POOL.submit(
					new Pair<Element, Map<String, Element>>(binaryOperation.getLeft(), context));
			final long rightId = THREAD_POOL.submit(
					new Pair<Element, Map<String, Element>>(binaryOperation.getRight(), context));

			// Get the results
			leftEntityResult = THREAD_POOL.get(leftId);
			rightEntityResult = THREAD_POOL.get(rightId);
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
		IO.debug(leftEntity, " ", type, " ", rightEntity);

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
				result = leftEntity.power(rightEntity);
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
	 * @return the evaluation of {@code unaryOperation}
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
		IO.debug(type, " ", entity);

		// Evaluate the unary operation
		final Entity result;
		switch (type) {
			case FACTORIAL:
				final Scalar scalar = (Scalar) entity;
				final BigInteger bigInt = Maths.factorial(scalar.intValue());
				result = new Scalar(bigInt.doubleValue());
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
	// WORKER
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class Evaluator
			extends Worker<Pair<Element, Map<String, Element>>, Report<Entity>> {

		@Override
		public Report<Entity> call(final Pair<Element, Map<String, Element>> input) {
			return Calculator.evaluateTree(input.getFirst(), input.getSecond());
		}

		@Override
		public Evaluator clone() {
			return new Evaluator();
		}
	}
}
