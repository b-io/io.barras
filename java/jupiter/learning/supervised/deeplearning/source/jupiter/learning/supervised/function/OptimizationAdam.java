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
package jupiter.learning.supervised.function;

import static jupiter.math.analysis.function.Functions.ROOT;

import jupiter.common.model.ICloneable;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.entity.Vector;

/**
 * {@link OptimizationAdam} is the {@link OptimizationFunction} using a momentum exponentially
 * weighted average and RMSprop.
 */
public class OptimizationAdam
		extends OptimizationFunction {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The exponential decay hyper-parameter {@code β1} for the first moment estimates (i.e.
	 * momentum).
	 */
	protected double beta1;
	/**
	 * The exponential decay hyper-parameter {@code β2} for the second moment estimates (i.e.
	 * RMSprop).
	 */
	protected double beta2;
	/**
	 * The hyper-parameter {@code t} for the number of steps (used for the bias correction).
	 */
	protected int t;

	/**
	 * The array of moving averages of the first gradient {@link Entity} {@code V} (for momentum).
	 */
	protected final Matrix[] V;
	/**
	 * The array of moving averages of the squared gradient {@link Entity} {@code S} (for RMSprop).
	 */
	protected final Matrix[] S;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link OptimizationAdam} with the specified number of layers {@code L} and
	 * initializes the arrays of momentum {@link Entity} {@code V} and RMSprop
	 * {@link Entity} {@code S} with the specified array of model {@link Vector}.
	 * <p>
	 * @param layerCount the number of layers {@code L}
	 * @param models     the array of model {@link Vector} for the arrays of momentum {@link Entity}
	 *                   {@code V} and RMSprop {@link Entity} {@code S}
	 */
	public OptimizationAdam(final int layerCount, final Vector[] models) {
		super();
		setDefaultParameters();
		V = new Vector[layerCount];
		S = new Vector[layerCount];
		for (int l = 0; l < layerCount; ++l) {
			V[l] = new Vector(models[l].getDimension(), models[l].isTransposed());
			S[l] = new Vector(models[l].getDimension(), models[l].isTransposed());
		}
	}

	/**
	 * Constructs an {@link OptimizationAdam} with the specified number of layers {@code L} and
	 * initializes the arrays of momentum {@link Entity} {@code V} and RMSprop
	 * {@link Entity} {@code S} with the specified array of model {@link Matrix}.
	 * <p>
	 * @param layerCount the number of layers {@code L}
	 * @param models     the array of model {@link Matrix} for the arrays of momentum {@link Entity}
	 *                   {@code V} and RMSprop {@link Entity} {@code S}
	 */
	public OptimizationAdam(final int layerCount, final Matrix[] models) {
		super();
		setDefaultParameters();
		V = new Matrix[layerCount];
		S = new Matrix[layerCount];
		for (int l = 0; l < layerCount; ++l) {
			V[l] = new Matrix(models[l].getDimensions());
			S[l] = new Matrix(models[l].getDimensions());
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the hyper-parameter {@code β1} for the momentum exponentially weighted average.
	 * <p>
	 * @return the hyper-parameter {@code β1} for the momentum exponentially weighted average
	 */
	public double getBeta1() {
		return beta1;
	}

	/**
	 * Returns the hyper-parameter {@code β2} for the RMSprop.
	 * <p>
	 * @return the hyper-parameter {@code β2} for the RMSprop
	 */
	public double getBeta2() {
		return beta2;
	}

	/**
	 * Returns the hyper-parameter {@code t} for the number of steps (used for the bias correction).
	 * <p>
	 * @return the hyper-parameter {@code t} for the number of steps (used for the bias correction)
	 */
	public int getT() {
		return t;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the hyper-parameters {@code β1}, {@code β2} and {@code t} by default.
	 */
	public void setDefaultParameters() {
		setParameters(0.9, 0.999, 1);
	}

	/**
	 * Sets the hyper-parameters {@code β1}, {@code β2} and {@code t}.
	 * <p>
	 * @param beta1 the hyper-parameter {@code β1} for the momentum exponentially weighted average
	 * @param beta2 the hyper-parameter {@code β2} for the RMSprop
	 * @param t     the hyper-parameter {@code t} for the number of steps
	 */
	public void setParameters(final double beta1, final double beta2, final int t) {
		this.beta1 = beta1;
		this.beta2 = beta2;
		this.t = t;
	}

	//////////////////////////////////////////////

	/**
	 * Sets the hyper-parameter {@code β1}.
	 * <p>
	 * @param beta1 the hyper-parameter {@code β1} for the momentum exponentially weighted average
	 */
	public void setBeta1(final double beta1) {
		this.beta1 = beta1;
	}

	/**
	 * Sets the hyper-parameter {@code β2}.
	 * <p>
	 * @param beta2 the hyper-parameter {@code β2} for the RMSprop
	 */
	public void setBeta2(final double beta2) {
		this.beta2 = beta2;
	}

	/**
	 * Sets the hyper-parameter {@code t}.
	 * <p>
	 * @param t the hyper-parameter {@code t} for the number of steps
	 */
	public void setT(final int t) {
		this.t = t;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Optimizes the specified descent gradient {@link Matrix} at the specified layer.
	 * <p>
	 * @param layer    the layer of the descent gradient {@link Matrix} to optimize
	 * @param gradient the descent gradient {@link Matrix} to optimize
	 * <p>
	 * @return the optimized descent gradient {@link Matrix}
	 */
	@Override
	public Entity optimize(final int layer, final Matrix gradient) {
		// Compute the momentum exponentially weighted average
		V[layer].multiply(beta1).add(gradient.times(1. - beta1));
		// Compute the RMSprop
		S[layer].multiply(beta2).add(gradient.arrayTimes(gradient).multiply(1. - beta2));
		// Update the weights and bias
		return V[layer].division(1. - Math.pow(beta1, t)) // bias correction
				.arrayDivide(S[layer].division(1. - Math.pow(beta2, t)) // bias correction
						.apply(ROOT)
						.toMatrix());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public OptimizationAdam clone() {
		return (OptimizationAdam) super.clone();
	}
}
