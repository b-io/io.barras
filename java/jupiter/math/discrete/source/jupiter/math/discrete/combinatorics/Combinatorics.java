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
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.util.Arrays;
import jupiter.common.util.Booleans;
import jupiter.common.util.Bytes;
import jupiter.common.util.Characters;
import jupiter.common.util.Doubles;
import jupiter.common.util.Floats;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Shorts;

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
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the factorial representation of the specified {@code int} value in an
	 * {@link ExtendedLinkedList}.
	 * <p>
	 * @param value an {@code int} value
	 * <p>
	 * @return the factorial representation of the specified {@code int} value in an
	 *         {@link ExtendedLinkedList}
	 */
	public static ExtendedLinkedList<Integer> getFactorialRepresentation(final int value) {
		final ExtendedLinkedList<Integer> result = new ExtendedLinkedList<Integer>();
		int remainder, quotient = value, radix = 1;
		do {
			remainder = quotient % radix;
			quotient = quotient / radix;
			result.push(remainder);
			++radix;
		} while (quotient != 0);
		return result;
	}

	public static int getPermutationIndex(final int index, final int k, final int n) {
		return getKPermutationIndex(index, n, n);
	}

	public static int getKPermutationIndex(final int index, final int k, final int n) {
		return 0;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the distinct ordered subsets (i.e. permutations) of a {@code n}-element set.
	 * <p>
	 * @param n the number of elements in the set
	 * <p>
	 * @return all the distinct ordered subsets (i.e. permutations) of a {@code n}-element set
	 */
	public static int[][] createAllPermutations(final int n) {
		// Generate all the permutations
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	//////////////////////////////////////////////

	/**
	 * Returns the distinct ordered {@code n}-element subsets (i.e. {@code n}-permutations) of a
	 * {@code n}-element set in lexicographical order.
	 * <p>
	 * @param n the number of elements in the set and in the ordered subsets
	 * <p>
	 * @return the distinct ordered {@code n}-element subsets (i.e. {@code n}-permutations) of a
	 *         {@code n}-element set in lexicographical order
	 */
	public static int[][] createPermutations(final int n) {
		// Initialize
		final int permutationCount = P(n, n);
		final int[][] permutations = new int[permutationCount][n];
		final int[] permutation = Integers.createSequence(n);

		// Generate the n-permutations in lexicographical order
		permutations[0] = permutation.clone();
		for (int p = 1; p < permutationCount; ++p) {
			int fromIndex = n - 1;
			while (permutation[fromIndex - 1] >= permutation[fromIndex]) {
				--fromIndex;
			}
			int toIndex = n - 1;
			final int pivot = permutation[fromIndex - 1];
			while (toIndex > fromIndex && permutation[toIndex] <= pivot) {
				--toIndex;
			}
			Integers.swap(permutation, fromIndex - 1, toIndex);
			Integers.reverse(permutation, fromIndex);
			permutations[p] = permutation.clone();
		}
		return permutations;
	}

	public static boolean[][] createPermutations(final boolean[] array) {
		return Booleans.filterAll(array, createPermutations(array.length));
	}

	public static char[][] createPermutations(final char[] array) {
		return Characters.filterAll(array, createPermutations(array.length));
	}

	public static byte[][] createPermutations(final byte[] array) {
		return Bytes.filterAll(array, createPermutations(array.length));
	}

	public static short[][] createPermutations(final short[] array) {
		return Shorts.filterAll(array, createPermutations(array.length));
	}

	public static int[][] createPermutations(final int[] array) {
		return Integers.filterAll(array, createPermutations(array.length));
	}

	public static long[][] createPermutations(final long[] array) {
		return Longs.filterAll(array, createPermutations(array.length));
	}

	public static float[][] createPermutations(final float[] array) {
		return Floats.filterAll(array, createPermutations(array.length));
	}

	public static double[][] createPermutations(final double[] array) {
		return Doubles.filterAll(array, createPermutations(array.length));
	}

	public static <T> T[][] createPermutations(final T[] array) {
		return Arrays.<T>filterAll(array, createPermutations(array.length));
	}

	//////////////////////////////////////////////

	/**
	 * Returns the distinct ordered {@code k}-element subsets (i.e. {@code k}-permutations) of a
	 * {@code n}-element set in lexicographical order.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the ordered subsets
	 * <p>
	 * @return the distinct ordered {@code k}-element subsets (i.e. {@code k}-permutations) of a
	 *         {@code n}-element set in lexicographical order
	 */
	public static int[][] createKPermutations(final int n, final int k) {
		return createKPermutations(n, k, true);
	}

	/**
	 * Returns the distinct ordered {@code k}-element subsets (i.e. {@code k}-permutations) of a
	 * {@code n}-element set (in lexicographical order if {@code sort}).
	 * <p>
	 * @param n    the number of elements in the set
	 * @param k    the number of elements in the ordered subsets
	 * @param sort the flag specifying whether to sort in lexicographical order
	 * <p>
	 * @return the distinct ordered {@code k}-element subsets (i.e. {@code k}-permutations) of a
	 *         {@code n}-element set (in lexicographical order if {@code sort})
	 */
	public static int[][] createKPermutations(final int n, final int k, final boolean sort) {
		// Initialize
		final int permutationCount = P(n, k);
		final int[][] permutations = new int[permutationCount][k];
		final int[][] combinations = createKCombinations(n, k);

		// Generate the k-permutations (in lexicographical order if sort)
		if (sort) {

		} else {
			int p = 0;
			for (final int[] combination : combinations) {
				final int[][] subpermutations = createPermutations(combination);
				for (final int[] subpermutation : subpermutations) {
					permutations[p++] = subpermutation;
				}
			}
		}
		return permutations;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the distinct subsets (i.e. combinations) of a {@code n}-element set.
	 * <p>
	 * @param n the number of elements in the set
	 * <p>
	 * @return all the distinct subsets (i.e. combinations) of a {@code n}-element set
	 */
	public static int[][] createAllCombinations(final int n) {
		// Initialize
		final int combinationCount = 1 << n;
		final int[][] combinations = new int[combinationCount][];

		// Generate all the combinations
		for (int c = 0; c < combinationCount; ++c) {
			combinations[c] = new int[Integer.bitCount(c)];
			int index = 0;
			for (int i = 0; i < n; ++i) {
				// Use the binary representation of c as a mask to select the elements
				if ((c & (1 << i)) != 0) {
					combinations[c][index++] = i;
				}
			}
		}
		return combinations;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the distinct {@code k}-element subsets (i.e. {@code k}-combinations) of a
	 * {@code n}-element set in lexicographical order.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the subsets
	 * <p>
	 * @return the distinct {@code k}-element subsets (i.e. {@code k}-combinations) of a
	 *         {@code n}-element set in lexicographical order
	 */
	public static int[][] createKCombinations(final int n, final int k) {
		// Initialize
		final int combinationCount = C(n, k);
		final int[][] combinations = new int[combinationCount][k];
		final int[] combination = Integers.createSequence(k);

		// Generate the k-combinations in lexicographical order
		combinations[0] = combination.clone();
		for (int c = 1; c < combinationCount; ++c) {
			int fromIndex = k - 1;
			while (fromIndex > 0 && combination[fromIndex] == n - k + fromIndex) {
				--fromIndex;
			}
			++combination[fromIndex];
			for (int i = fromIndex + 1; i < k; ++i) {
				combination[i] = combination[i - 1] + 1;
			}
			combinations[c] = combination.clone();
		}
		return combinations;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMBINATORIAL FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number {@code P(n, k)} of distinct ordered {@code k}-element subsets (i.e.
	 * {@code k}-permutations) of a {@code n}-element set.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the ordered subsets
	 * <p>
	 * @return the number {@code P(n, k)} of distinct ordered {@code k}-element subsets (i.e.
	 *         {@code k}-permutations) of a {@code n}-element set
	 */
	public static int P(final int n, final int k) {
		if (k == 0 || n == 0) {
			return 1;
		}
		return Maths.productSeries(n - k + 1, n);
	}

	/**
	 * Returns the number {@code P(n, k)} of distinct ordered {@code k}-element subsets (i.e.
	 * {@code k}-permutations) of a {@code n}-element set.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the ordered subsets
	 * <p>
	 * @return the number {@code P(n, k)} of distinct ordered {@code k}-element subsets (i.e.
	 *         {@code k}-permutations) of a {@code n}-element set
	 */
	public static long P(final long n, final long k) {
		if (k == 0L || n == 0L) {
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
	public static int PR(final int... ms) {
		final int n = Maths.sum(ms);
		if (n == 0) {
			return 1;
		}
		int result = Maths.productSeries(ms[0] + 1, n);
		for (final int m : ms) {
			result /= Maths.factorial(m);
		}
		return result;
	}

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
	public static int C(final int n, final int k) {
		if (k == 0 || n == 0 || k >= n) {
			return 1;
		}
		return P(n, k) / Maths.factorial(k);
	}

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
	public static int CR(final int n, final int k) {
		return C(n + k - 1, k);
	}

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
	public static int CR(final int n, final int k, final int m) {
		return CR(n - m, k);
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
	public static int CR(final int k, final int ms[]) {
		final int n = Maths.sum(ms);
		throw new UnsupportedOperationException("Not yet implemented!");
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
		throw new UnsupportedOperationException("Not yet implemented!");
	}
}
