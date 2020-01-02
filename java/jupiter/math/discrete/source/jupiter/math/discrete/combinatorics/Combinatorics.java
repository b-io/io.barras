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
package jupiter.math.discrete.combinatorics;

import jupiter.common.math.Maths;

/**
 * {@link Combinatorics} is a collection of combinatorial functions.
 */
public class Combinatorics {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Combinatorics}.
	 */
	protected Combinatorics() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMBINATORIAL FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number {@code P(n, k)} of distinct ordered {@code k}-element subsets (i.e.
	 * {@code k}-permutations) of a {@code n}-element set.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the subsets
	 * <p>
	 * @return the number {@code P(n, k)} of distinct ordered {@code k}-element subsets (i.e.
	 *         {@code k}-permutations) of a {@code n}-element set
	 */
	public static long P(final long n, final long k) {
		if (k == 0L || n == 0L || k >= n) {
			return 1L;
		}
		return Maths.productSeries(n - k + 1L, n);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of permutations {@code PR(ms)} of a {@code n}-element tuple with the
	 * specified element multiplicities {@code ms} where {@code n = sum(ms)}.
	 * <p>
	 * @param ms the multiplicity of each distinct element in the tuple
	 * <p>
	 * @return the number of permutations {@code PR(ms)} of a {@code n}-element tuple with the
	 *         specified element multiplicities {@code ms} where {@code n = sum(ms)}
	 */
	public static long PR(final long... ms) {
		final long n = Maths.sum(ms);
		if (n == 0L) {
			return 1L;
		}
		long result = Maths.productSeries(ms[0] + 1L, n);
		for (final long m : ms) {
			result /= Maths.factorial(m);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number {@code C(n, k)} of distinct {@code k}-element subsets (i.e.
	 * {@code k}-combinations) of a {@code n}-element set.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the subsets
	 * <p>
	 * @return the number {@code C(n, k)} of distinct {@code k}-element subsets (i.e.
	 *         {@code k}-combinations) of a {@code n}-element set
	 */
	public static long C(final long n, final long k) {
		if (k == 0L || n == 0L || k >= n) {
			return 1L;
		}
		return P(n, k) / Maths.factorial(k);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number {@code CR(n, k)} of distinct {@code k}-element multisubsets (i.e.
	 * {@code k}-multicombinations) of a {@code n}-element multiset.
	 * <p>
	 * @param n the number of distinct elements in the multiset
	 * @param k the number of elements in the multisubsets
	 * <p>
	 * @return the number {@code CR(n, k)} of distinct {@code k}-element multisubsets (i.e.
	 *         {@code k}-multicombinations) of a {@code n}-element multiset
	 */
	public static long CR(final long n, final long k) {
		return C(n + k - 1, k);
	}

	/**
	 * Returns the number {@code CR(n, k, m)} of distinct {@code k}-element multisubsets (i.e.
	 * {@code k}-multicombinations) having a distinct element with multiplicity at least {@code m}
	 * of a {@code n}-element multiset.
	 * <p>
	 * @param n the number of distinct elements in the multiset
	 * @param k the number of elements in the multisubsets
	 * @param m the minimum multiplicity of a distinct element in the multisubsets
	 * <p>
	 * @return the number {@code CR(n, k, m)} of distinct {@code k}-element multisubsets (i.e.
	 *         {@code k}-multicombinations) having a distinct element with multiplicity at least
	 *         {@code m} of a {@code n}-element multiset
	 */
	public static long CR(final long n, final long k, final long m) {
		return CR(n - m, k);
	}

	/**
	 * Returns the number {@code CR(k, ms)} of distinct {@code k}-element multisubsets (i.e.
	 * {@code k}-multicombinations) of a finite {@code n}-element multiset with the specified
	 * element multiplicities {@code ms} where {@code n = sum(ms)}.
	 * <p>
	 * @param k  the number of elements in the multisubsets
	 * @param ms the multiplicity of each distinct element in the multiset
	 * <p>
	 * @return the number {@code CR(k, ms)} of distinct {@code k}-element multisubsets (i.e.
	 *         {@code k}-multicombinations) of a finite {@code n}-element multiset with the
	 *         specified element multiplicities {@code ms} where {@code n = sum(ms)}
	 */
	public static long CR(final long k, final long ms[]) {
		final long n = Maths.sum(ms);
		return 1L;
	}
}
