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
 * <p>
 * Definitions:
 * - A set is a collection of elements that is unordered and has no duplicate elements.
 * - A tuple is a collection of elements that is ordered and may have duplicate elements.
 * - A multiset is a collection of elements that is unordered and may have duplicate elements.
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
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of {@code k}-permutations {@code P(n, k)} of an {@code n}-element set,
	 * i.e. the number of distinct ordered {@code k}-element subsets of an {@code n}-element set.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the subset
	 * <p>
	 * @return the number of {@code k}-permutations {@code P(n, k)} of an {@code n}-element set,
	 *         i.e. the number of distinct ordered {@code k}-element subsets of an {@code n}-element
	 *         set
	 */
	public static long P(final long n, final long k) {
		if (k == 0L || n == 0L || k >= n) {
			return 1L;
		}
		return Maths.productSeries(n - k + 1L, n);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of permutations {@code P(n, m...)} of an {@code n}-element tuple with the
	 * specified element multiplicities {@code m}.
	 * <p>
	 * @param m the multiplicity of each distinct element in the tuple
	 * <p>
	 * @return the number of permutations {@code P(n, m...)} of an {@code n}-element tuple with the
	 *         specified element multiplicities {@code m}
	 */
	public static long PR(final long... m) {
		final long n = Maths.sum(m);
		if (n == 0L) {
			return 1L;
		}
		long result = Maths.productSeries(m[0] + 1L, n);
		for (int i = 1; i < m.length; ++i) {
			result /= Maths.factorial(m[i]);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of {@code k}-combinations {@code C(n, k)} of an {@code n}-element set,
	 * i.e. the number of distinct {@code k}-element subsets of an {@code n}-element set.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the subset
	 * <p>
	 * @return the number of {@code k}-combinations {@code C(n, k)} of an {@code n}-element set,
	 *         i.e. the number of distinct {@code k}-element subsets of an {@code n}-element set
	 */
	public static long C(final long n, final long k) {
		if (k == 0L || n == 0L || k >= n) {
			return 1L;
		}
		return P(n, k) / Maths.factorial(k);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of {@code k}-multicombinations {@code C(n, k...)} of an {@code n}-element
	 * multiset, i.e. the number of distinct {@code k}-element multisubsets of an {@code n}-element
	 * multiset.
	 * <p>
	 * @param n the number of distinct elements in the multiset
	 * @param k the number of elements in the multisubset
	 * <p>
	 * @return the number of {@code k}-multicombinations {@code C(n, k...)} of an {@code n}-element
	 *         multiset, i.e. the number of distinct {@code k}-element multisubsets of an
	 *         {@code n}-element multiset
	 */
	public static long CR(final long n, final long k) {
		return C(n + k - 1, k);
	}

	/**
	 * Returns the number of {@code k}-multicombinations {@code C(n, k...)} of an {@code n}-element
	 * multiset having a distinct element with multiplicity at least {@code m}, i.e. the number of
	 * distinct {@code k}-element multisubsets of an {@code n}-element multiset having a distinct
	 * element with multiplicity at least {@code m}.
	 * <p>
	 * @param n the number of distinct elements in the multiset
	 * @param k the number of elements in the multisubset
	 * @param m the minimum multiplicity of a distinct element in the multisubset
	 * <p>
	 * @return the number of {@code k}-multicombinations {@code C(n, k...)} of an {@code n}-element
	 *         multiset having a distinct element with multiplicity at least {@code m}, i.e. the
	 *         number of distinct {@code k}-element multisubsets of an {@code n}-element multiset
	 *         having a distinct element with multiplicity at least {@code m}
	 */
	public static long CR(final long n, final long k, final long m) {
		return CR(n - m, k);
	}
}
