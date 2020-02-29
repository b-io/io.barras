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

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Characters.BULLET;

import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.entity.Vector;

public class OptimizationAdamTest
		extends jupiter.common.test.Test {

	public OptimizationAdamTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of optimize method, of class OptimizationAdam.
	 */
	public void testOptimize() {
		IO.test(BULLET, " optimize");

		// Initialize
		final int layerCount = 2; // L
		// • W
		final Matrix[] weights = new Matrix[layerCount];
		weights[0] = new Matrix(new double[][] {
			new double[] {1.63178673, -0.61919778, -0.53561312},
			new double[] {-1.08040999, 0.85796626, -2.29409733}});
		weights[1] = new Matrix(new double[][] {
			new double[] {0.32648046, -0.25681174, 1.46954931},
			new double[] {-2.05269934, -0.31497584, -0.37661299},
			new double[] {-2.05269934, -0.31497584, -0.37661299}});
		// • b
		final Vector[] bias = new Vector[layerCount];
		bias[0] = new Vector(new double[][] {
			new double[] {1.74394258},
			new double[] {-0.76782544}});
		bias[1] = new Vector(new double[][] {
			new double[] {-0.8795164},
			new double[] {0.03047424},
			new double[] {0.57756686}});
		// • dW
		final Matrix[] dW = new Matrix[layerCount];
		dW[0] = new Matrix(new double[][] {
			new double[] {-1.10061918, 1.14472371, 0.90159072},
			new double[] {0.50249434, 0.90085595, -0.68372786}});
		dW[1] = new Matrix(new double[][] {
			new double[] {-0.26788808, 0.53035547, -0.69166075},
			new double[] {-0.39675353, -0.6871727, -0.84520564},
			new double[] {-0.67124613, -0.0126646, -1.11731035}});
		// • db
		final Vector[] db = new Vector[layerCount];
		db[0] = new Vector(new double[][] {
			new double[] {-0.12289023},
			new double[] {-0.93576943}});
		db[1] = new Vector(new double[][] {
			new double[] {0.2344157},
			new double[] {1.65980218},
			new double[] {0.74204416}});
		// • Adam optimization
		final OptimizationAdam dwOptimizer = new OptimizationAdam(layerCount, weights);
		final OptimizationAdam dbOptimizer = new OptimizationAdam(layerCount, bias);
		dwOptimizer.setT(2);
		dbOptimizer.setT(2);

		// Optimize
		final Entity dW1 = dwOptimizer.optimize(0, dW[0]);
		final Entity db1 = dbOptimizer.optimize(0, db[0]);
		final Entity dW2 = dwOptimizer.optimize(1, dW[1]);
		final Entity db2 = dbOptimizer.optimize(1, db[1]);
		IO.test("- dW1 = ", dW1);
		IO.test("- db1 = ", db1);
		IO.test("- dW2 = ", dW2);
		IO.test("- db2 = ", db2);
	}
}
