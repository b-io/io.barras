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
package jupiter.math.statistics;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.math.Maths;
import jupiter.common.test.Test;

public class StatisticsTest
		extends Test {

	public StatisticsTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests getConditionalProbability method, of class Statistics.
	 */
	public void testGetConditionalProbability() {
		IO.test(BULLET, " getConditionalProbability");

		// Compute the conditional probability of cheating given 3 consecutive victories (12.5%)
		final double peh = 0.5; // probability of winning if cheating
		final double penh = 0.125; // probability of winning if not cheating
		final double ph = 0.25; // probability of cheating
		final double phe = Statistics.getConditionalProbability(peh, ph, new double[] {peh, penh},
				new double[] {ph, 1. - ph}); // probability of cheating if winning
		assertEquals(0.5714285714285714, phe, Maths.TOLERANCE);
	}
}
