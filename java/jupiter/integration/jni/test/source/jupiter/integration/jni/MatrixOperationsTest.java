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
package jupiter.integration.jni;

import static jupiter.common.io.IO.IO;
import static jupiter.integration.jni.MatrixOperations.JNI;

import jupiter.common.test.Test;
import jupiter.common.util.Doubles;

public class MatrixOperationsTest
		extends Test {

	public MatrixOperationsTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of test method, of class MatrixOperations.
	 * <p>
	 * @since 1.6
	 */
	public void testTest() {
		if (MatrixOperations.USE && false) {
			IO.test("test");

			JNI.test();
		}
	}

	/**
	 * Test of plus method, of class MatrixOperations.
	 * <p>
	 * @since 1.6
	 */
	public void testPlus() {
		if (MatrixOperations.USE && false) {
			IO.test("plus");

			for (int i = 0; i < 10; ++i) {
				IO.result(JNI.plus(0, 1));
			}
		}
	}

	/**
	 * Test of dot method, of class MatrixOperations.
	 * <p>
	 * @since 1.6
	 */
	public void testDot() {
		if (MatrixOperations.USE && false) {
			IO.test("dot");

			double[] A = new double[] {1., 2., 3., 4.};
			double[] B = new double[] {1., 2., 3., 4.};
			IO.result(Doubles.toString(new MatrixOperations().dot(A, B, 2, 2)));
		}
	}
}
