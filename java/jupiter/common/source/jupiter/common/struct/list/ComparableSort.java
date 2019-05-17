/*
 * The MIT License
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
package jupiter.common.struct.list;

/**
 * This is a duplicate of {@code ComparableTimSort} from Oracle Java 8.
 */
public class ComparableSort {
	/**
	 * This is the minimum sized sequence that will be merged. Shorter sequences will be lengthened
	 * by calling {@link #binarySort}. If the entire array is less than this length, no merges will
	 * be performed.
	 * <p>
	 * This constant should be a power of two. It was 64 in Tim Peter's C implementation, but 32 was
	 * empirically determined to work better in this implementation. In the unlikely event that you
	 * set this constant to be a number that's not a power of two, you'll need to change the
	 * {@link #minRunLength} computation.
	 * <p>
	 * If you decrease this constant, you must change the {@code stackLen} computation in the
	 * TimSort constructor, or you risk an {@link ArrayIndexOutOfBoundsException}. See listsort.txt
	 * for a discussion of the minimum stack length required as a function of the length of the
	 * array being sorted and the minimum merge sequence length.
	 */
	protected static final int MIN_MERGE = 32;

	/**
	 * The array being sorted.
	 */
	protected final Object[] a;

	/**
	 * When we get into galloping mode, we stay there until both runs win less often than
	 * {@code MIN_GALLOP} consecutive times.
	 */
	protected static final int MIN_GALLOP = 7;

	/**
	 * This controls when we get *into* galloping mode. It is initialized to {@code MIN_GALLOP}. The
	 * {@link #mergeLo} and {@link #mergeHi} methods nudge it higher for random data and lower for
	 * highly structured data.
	 */
	protected int minGallop = MIN_GALLOP;

	/**
	 * Maximum initial size of {@code tmp} array, which is used for merging. The array can grow to
	 * accommodate demand.
	 * <p>
	 * Unlike Tim's original C version, we do not allocate this much storage when sorting smaller
	 * arrays. This change was required for performance.
	 */
	protected static final int INITIAL_TMP_STORAGE_LENGTH = 256;

	/**
	 * Temporary storage for merges. A workspace array may optionally be provided in constructor and
	 * if so will be used as long as it is big enough.
	 */
	protected Object[] tmp;
	protected int tmpBase; // base of tmp array slice
	protected int tmpLen; // length of tmp array slice

	/**
	 * A stack of pending runs yet to be merged. Run {@code i} starts at address {@code base[i]} and
	 * extends for {@code len[i]} elements. It's always true (so long as the indices are in bounds)
	 * that:
	 * <p>
	 * {@code runBase[i] + runLen[i] == runBase[i + 1]}
	 * <p>
	 * so we could cut the storage for this, but it's a minor amount and keeping all the info
	 * explicit simplifies the code.
	 */
	protected int stackSize = 0; // number of pending runs on stack
	protected final int[] runBase;
	protected final int[] runLen;

	/**
	 * Creates a TimSort instance to maintain the state of an ongoing sort.
	 * <p>
	 * @param a        the array to be sorted
	 * @param work     a workspace array (slice)
	 * @param workBase the origin of the usable space in the work array
	 * @param workLen  the usable size of the work array
	 */
	protected ComparableSort(Object[] a, Object[] work, int workBase, int workLen) {
		this.a = a;

		// Allocate temporary storage (which may be increased later if necessary)
		int len = a.length;
		int tlen = (len < 2 * INITIAL_TMP_STORAGE_LENGTH) ?
				len >>> 1 : INITIAL_TMP_STORAGE_LENGTH;
		if (work == null || workLen < tlen || workBase + tlen > work.length) {
			tmp = new Object[tlen];
			tmpBase = 0;
			tmpLen = tlen;
		} else {
			tmp = work;
			tmpBase = workBase;
			tmpLen = workLen;
		}

		/*
		 * Allocate runs-to-be-merged stack (which cannot be expanded). The stack length
		 * requirements are described in listsort.txt. The C version always uses the same stack
		 * length (85), but this was measured to be too expensive when sorting "mid-sized" arrays
		 * (e.g., 100 elements) in Java. Therefore, we use smaller (but sufficiently large) stack
		 * lengths for smaller arrays. The "magic numbers" in the computation below must be changed
		 * if {@code MIN_MERGE} is decreased. See the {@code MIN_MERGE} declaration above for more
		 * information. The maximum value of 49 allows for an array up to length {@code
		 * Integer.MAX_VALUE - 4}, if array is filled by the worst case stack size increasing
		 * scenario. More explanations are given in section 4 of:
		 * http://envisage-project.eu/wp-content/uploads/2015/02/sorting.pdf
		 */
		int stackLen = (len < 120 ? 5 :
				len < 1542 ? 10 :
						len < 119151 ? 24 : 49);
		runBase = new int[stackLen];
		runLen = new int[stackLen];
	}

	/*
	 * The next method (package protected and static) constitutes the entire API of this class.
	 */

	/**
	 * Sorts the given range, using the given workspace array slice for temporary storage when
	 * possible. This method is designed to be invoked from public methods (in class
	 * {@link jupiter.common.util.Arrays}) after performing any necessary array bounds checks and
	 * expanding parameters into the required forms.
	 * <p>
	 * @param a        the array to be sorted
	 * @param lo       the index of the first element, inclusive, to be sorted
	 * @param hi       the index of the last element, exclusive, to be sorted
	 * @param work     a workspace array (slice)
	 * @param workBase the origin of the usable space in the work array
	 * @param workLen  the usable size of the work array
	 */
	public static void sort(Object[] a, int lo, int hi, Object[] work, int workBase, int workLen) {
		assert a != null && lo >= 0 && lo <= hi && hi <= a.length;

		int nRemaining = hi - lo;
		if (nRemaining < 2) {
			return; // arrays of size 0 and 1 are always sorted
		}
		// If array is small, do a "mini-TimSort" with no merges
		if (nRemaining < MIN_MERGE) {
			int initRunLen = countRunAndMakeAscending(a, lo, hi);
			binarySort(a, lo, hi, lo + initRunLen);
			return;
		}

		/**
		 * March over the array once, left to right, finding natural runs, extending short natural
		 * runs to minRun elements and merging runs to maintain stack invariant.
		 */
		ComparableSort ts = new ComparableSort(a, work, workBase, workLen);
		int minRun = minRunLength(nRemaining);
		do {
			// Identify next run
			int runLen = countRunAndMakeAscending(a, lo, hi);

			// If run is short, extend to min(minRun, nRemaining)
			if (runLen < minRun) {
				int force = nRemaining <= minRun ? nRemaining : minRun;
				binarySort(a, lo, lo + force, lo + runLen);
				runLen = force;
			}

			// Push run onto pending-run stack and maybe merge
			ts.pushRun(lo, runLen);
			ts.mergeCollapse();

			// Advance to find next run
			lo += runLen;
			nRemaining -= runLen;
		} while (nRemaining != 0);

		// Merge all remaining runs to complete sort
		assert lo == hi;
		ts.mergeForceCollapse();
		assert ts.stackSize == 1;
	}

	/**
	 * Sorts the specified portion of the specified array using a binary insertion sort. This is the
	 * best method for sorting small numbers of elements. It requires O(n log n) compares, but
	 * O(n^2) data movement (worst case).
	 * <p>
	 * If the initial part of the specified range is already sorted, this method can take advantage
	 * of it: the method assumes that the elements from index {@code lo}, inclusive, to
	 * {@code start}, exclusive are already sorted.
	 * <p>
	 * @param a     the array in which a range is to be sorted
	 * @param lo    the index of the first element in the range to be sorted
	 * @param hi    the index after the last element in the range to be sorted
	 * @param start the index of the first element in the range that is not already known to be
	 *              sorted ({@code lo <= start <= hi})
	 */
	@SuppressWarnings({"fallthrough", "rawtypes", "unchecked"})
	protected static void binarySort(Object[] a, int lo, int hi, int start) {
		assert lo <= start && start <= hi;
		if (start == lo) {
			start++;
		}
		for (; start < hi; start++) {
			Comparable pivot = (Comparable) a[start];

			// Set left (and right) to the index where a[start] (pivot) belongs
			int left = lo;
			int right = start;
			assert left <= right;
			// Invariants:
			//   pivot >= all in [lo, left) and
			//   pivot < all in [right, start).
			while (left < right) {
				int mid = (left + right) >>> 1;
				if (pivot.compareTo(a[mid]) < 0) {
					right = mid;
				} else {
					left = mid + 1;
				}
			}
			assert left == right;

			// The invariants still hold:
			//   pivot >= all in [lo, left) and
			//   pivot < all in [left, start),
			// so pivot belongs at left. Note that if there are elements equal to pivot, left points
			// to the first slot after them -- that's why this sort is stable. Slide elements over
			// to make room for pivot.
			int n = start - left; // the number of elements to move
			// Switch is just an optimization for arraycopy in default case
			switch (n) {
				case 2: a[left + 2] = a[left + 1];
				case 1: a[left + 1] = a[left];
					break;
				default: System.arraycopy(a, left, a, left + 1, n);
			}
			a[left] = pivot;
		}
	}

	/**
	 * Returns the length of the run beginning at the specified position in the specified array and
	 * reverses the run if it is descending (ensuring that the run will always be ascending when the
	 * method returns).
	 * <p>
	 * A run is the longest ascending sequence with {@code a[lo] <= a[lo + 1] <= a[lo + 2] <= ...}
	 * or the longest descending sequence with {@code a[lo] > a[lo + 1] > a[lo + 2] > ...}.
	 * <p>
	 * For its intended use in a stable merge sort, the strictness of the definition of "descending"
	 * is needed so that the call can safely reverse a descending sequence without violating
	 * stability.
	 * <p>
	 * @param a  the array in which a run is to be counted and possibly reversed
	 * @param lo the index of the first element in the run
	 * @param hi the index after the last element that may be contained in the run. It is required
	 *           that {@code lo < hi}.
	 * <p>
	 * @return the length of the run beginning at the specified position in the specified array
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected static int countRunAndMakeAscending(Object[] a, int lo, int hi) {
		assert lo < hi;
		int runHi = lo + 1;
		if (runHi == hi) {
			return 1;
		}

		// Find end of run and reverse range if descending
		if (((Comparable) a[runHi++]).compareTo(a[lo]) < 0) {
			// Descending
			while (runHi < hi && ((Comparable) a[runHi]).compareTo(a[runHi - 1]) < 0) {
				runHi++;
			}
			reverseRange(a, lo, runHi);
		} else {
			// Ascending
			while (runHi < hi && ((Comparable) a[runHi]).compareTo(a[runHi - 1]) >= 0) {
				runHi++;
			}
		}

		return runHi - lo;
	}

	/**
	 * Reverse the specified range of the specified array.
	 * <p>
	 * @param a  the array in which a range is to be reversed
	 * @param lo the index of the first element in the range to be reversed
	 * @param hi the index after the last element in the range to be reversed
	 */
	protected static void reverseRange(Object[] a, int lo, int hi) {
		hi--;
		while (lo < hi) {
			Object t = a[lo];
			a[lo++] = a[hi];
			a[hi--] = t;
		}
	}

	/**
	 * Returns the minimum acceptable run length for an array of the specified length. Natural runs
	 * shorter than this will be extended with {@link #binarySort}.
	 * <p>
	 * Roughly speaking, the computation is:
	 * <p>
	 * If {@code n < MIN_MERGE}, return {@code n} (it's too small to bother with fancy stuff); else
	 * if {@code n} is an exact power of 2, return {@code MIN_MERGE / 2}; else return an int
	 * {@code k}, {@code MIN_MERGE / 2 <= k <= MIN_MERGE}, such that {@code n / k} is close to, but
	 * strictly less than, an exact power of 2.
	 * <p>
	 * For the rationale, see listsort.txt.
	 * <p>
	 * @param n the length of the array to be sorted
	 * <p>
	 * @return the length of the minimum run to be merged
	 */
	protected static int minRunLength(int n) {
		assert n >= 0;
		int r = 0; // becomes 1 if any 1 bits are shifted off
		while (n >= MIN_MERGE) {
			r |= (n & 1);
			n >>= 1;
		}
		return n + r;
	}

	/**
	 * Pushes the specified run onto the pending-run stack.
	 * <p>
	 * @param runBase the index of the first element in the run
	 * @param runLen  the number of elements in the run
	 */
	protected void pushRun(int runBase, int runLen) {
		this.runBase[stackSize] = runBase;
		this.runLen[stackSize] = runLen;
		stackSize++;
	}

	/**
	 * Examines the stack of runs waiting to be merged and merges adjacent runs until the stack
	 * invariants are reestablished:
	 * <p>
	 * 1. {@code runLen[i - 3] > runLen[i - 2] + runLen[i - 1]}
	 * <p>
	 * 2. {@code runLen[i - 2] > runLen[i - 1]}
	 * <p>
	 * This method is called each time a new run is pushed onto the stack, so the invariants are
	 * guaranteed to hold for {@code i < stackSize} upon entry to the method.
	 */
	protected void mergeCollapse() {
		while (stackSize > 1) {
			int n = stackSize - 2;
			if (n > 0 && runLen[n - 1] <= runLen[n] + runLen[n + 1]) {
				if (runLen[n - 1] < runLen[n + 1]) {
					n--;
				}
				mergeAt(n);
			} else if (runLen[n] <= runLen[n + 1]) {
				mergeAt(n);
			} else {
				break; // invariant is established
			}
		}
	}

	/**
	 * Merges all runs on the stack until only one remains. This method is called once, to complete
	 * the sort.
	 */
	protected void mergeForceCollapse() {
		while (stackSize > 1) {
			int n = stackSize - 2;
			if (n > 0 && runLen[n - 1] < runLen[n + 1]) {
				n--;
			}
			mergeAt(n);
		}
	}

	/**
	 * Merges the two runs at stack indices {@code i} and {@code i + 1}. Run {@code i} must be the
	 * penultimate or antepenultimate run on the stack. In other words, {@code i} must be equal to
	 * {@code stackSize - 2} or {@code stackSize - 3}.
	 * <p>
	 * @param i the stack index of the first of the two runs to merge
	 */
	@SuppressWarnings("unchecked")
	protected void mergeAt(int i) {
		assert stackSize >= 2;
		assert i >= 0;
		assert i == stackSize - 2 || i == stackSize - 3;

		int base1 = runBase[i];
		int len1 = runLen[i];
		int base2 = runBase[i + 1];
		int len2 = runLen[i + 1];
		assert len1 > 0 && len2 > 0;
		assert base1 + len1 == base2;

		/*
		 * Record the length of the combined runs; if i is the 3rd-last run now, also slide over the
		 * last run (which isn't involved in this merge). The current run ({@code i + 1}) goes away
		 * in any case.
		 */
		runLen[i] = len1 + len2;
		if (i == stackSize - 3) {
			runBase[i + 1] = runBase[i + 2];
			runLen[i + 1] = runLen[i + 2];
		}
		stackSize--;

		/*
		 * Find where the first element of run2 goes in run1. Prior elements in run1 can be ignored
		 * (because they're already in place).
		 */
		int k = gallopRight((Comparable<Object>) a[base2], a, base1, len1, 0);
		assert k >= 0;
		base1 += k;
		len1 -= k;
		if (len1 == 0) {
			return;
		}

		/*
		 * Find where the last element of run1 goes in run2. Subsequent elements in run2 can be
		 * ignored (because they're already in place).
		 */
		len2 = gallopLeft((Comparable<Object>) a[base1 + len1 - 1], a,
				base2, len2, len2 - 1);
		assert len2 >= 0;
		if (len2 == 0) {
			return;
		}

		/*
		 * Merge remaining runs, using {@code tmp} array with {@code min(len1, len2)} elements.
		 */
		if (len1 <= len2) {
			mergeLo(base1, len1, base2, len2);
		} else {
			mergeHi(base1, len1, base2, len2);
		}
	}

	/**
	 * Locates the position at which to insert the specified key into the specified sorted range; if
	 * the range contains an element equal to {@code key}, returns the index of the leftmost equal
	 * element.
	 * <p>
	 * @param key  the key whose insertion point to search for
	 * @param a    the array in which to search
	 * @param base the index of the first element in the range
	 * @param len  the length of the range (must be greater than 0)
	 * @param hint the index at which to begin the search, {@code 0 <= hint < n} (the closer hint is
	 *             to the result, the faster this method will run)
	 * <p>
	 * @return the int k, {@code 0 <= k <= n} such that {@code a[b + k - 1] < key <= a[b + k]},
	 *         pretending that {@code a[b - 1]} is minus infinity and {@code a[b + n]} is infinity;
	 *         in other words, {@code key} belongs at index {@code b + k}; or in other words, the
	 *         first {@code k} elements of {@code a} should precede {@code key}, and the last
	 *         {@code n - k} should follow it
	 */
	protected static int gallopLeft(Comparable<Object> key, Object[] a,
			int base, int len, int hint) {
		assert len > 0 && hint >= 0 && hint < len;

		int lastOfs = 0;
		int ofs = 1;
		if (key.compareTo(a[base + hint]) > 0) {
			/*
			 * Gallop right until {@code a[base + hint + lastOfs] < key <= a[base + hint + ofs]}.
			 */
			int maxOfs = len - hint;
			while (ofs < maxOfs && key.compareTo(a[base + hint + ofs]) > 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) // int overflow
				{
					ofs = maxOfs;
				}
			}
			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			// Make offsets relative to base
			lastOfs += hint;
			ofs += hint;
		} else {
			// key <= a[base + hint]
			/*
			 * Gallop left until {@code a[base + hint - ofs] < key <= a[base + hint - lastOfs]}.
			 */
			final int maxOfs = hint + 1;
			while (ofs < maxOfs && key.compareTo(a[base + hint - ofs]) <= 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) // int overflow
				{
					ofs = maxOfs;
				}
			}
			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			// Make offsets relative to base
			int tmp = lastOfs;
			lastOfs = hint - ofs;
			ofs = hint - tmp;
		}
		assert -1 <= lastOfs && lastOfs < ofs && ofs <= len;

		/*
		 * Now {@code a[base + lastOfs] < key <= a[base + ofs]}, so key belongs somewhere to the
		 * right of {@code lastOfs} but no farther right than {@code ofs}. Do a binary search, with
		 * invariant {@code a[base + lastOfs - 1] < key <= a[base + ofs]}.
		 */
		lastOfs++;
		while (lastOfs < ofs) {
			int m = lastOfs + ((ofs - lastOfs) >>> 1);

			if (key.compareTo(a[base + m]) > 0) {
				lastOfs = m + 1; // a[base + m] < key
			} else {
				ofs = m; // key <= a[base + m]
			}
		}
		assert lastOfs == ofs; // so a[base + ofs - 1] < key <= a[base + ofs]
		return ofs;
	}

	/**
	 * Like {@link #gallopLeft}, except that if the range contains an element equal to {@code key},
	 * {@link #gallopRight} returns the index after the rightmost equal element.
	 * <p>
	 * @param key  the key whose insertion point to search for
	 * @param a    the array in which to search
	 * @param base the index of the first element in the range
	 * @param len  the length of the range (must be greater than 0)
	 * @param hint the index at which to begin the search, {@code 0 <= hint < n} (the closer hint is
	 *             to the result, the faster this method will run)
	 * <p>
	 * @return the int {@code k}, {@code 0 <= k <= n} such that
	 *         {@code a[b + k - 1] <= key < a[b + k]}
	 */
	protected static int gallopRight(Comparable<Object> key, Object[] a,
			int base, int len, int hint) {
		assert len > 0 && hint >= 0 && hint < len;

		int ofs = 1;
		int lastOfs = 0;
		if (key.compareTo(a[base + hint]) < 0) {
			/*
			 * Gallop left until {@code a[b + hint - ofs] <= key < a[b + hint - lastOfs]}.
			 */
			int maxOfs = hint + 1;
			while (ofs < maxOfs && key.compareTo(a[base + hint - ofs]) < 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) // int overflow
				{
					ofs = maxOfs;
				}
			}
			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			// Make offsets relative to b
			int tmp = lastOfs;
			lastOfs = hint - ofs;
			ofs = hint - tmp;
		} else {
			// a[b + hint] <= key
			/*
			 * Gallop right until {@code a[b + hint + lastOfs] <= key < a[b + hint + ofs]}.
			 */
			int maxOfs = len - hint;
			while (ofs < maxOfs && key.compareTo(a[base + hint + ofs]) >= 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) // int overflow
				{
					ofs = maxOfs;
				}
			}
			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			// Make offsets relative to b
			lastOfs += hint;
			ofs += hint;
		}
		assert -1 <= lastOfs && lastOfs < ofs && ofs <= len;

		/*
		 * Now {@code a[b + lastOfs] <= key < a[b + ofs]}, so key belongs somewhere to the right of
		 * {@code lastOfs} but no farther right than {@code ofs}. Do a binary search, with invariant
		 * {@code a[b + lastOfs - 1] <= key < a[b + ofs]}.
		 */
		lastOfs++;
		while (lastOfs < ofs) {
			int m = lastOfs + ((ofs - lastOfs) >>> 1);

			if (key.compareTo(a[base + m]) < 0) {
				ofs = m; // key < a[b + m]
			} else {
				lastOfs = m + 1; // a[b + m] <= key
			}
		}
		assert lastOfs == ofs; // so a[b + ofs - 1] <= key < a[b + ofs]
		return ofs;
	}

	/**
	 * Merges two adjacent runs in place, in a stable fashion. The first element of the first run
	 * must be greater than the first element of the second run ({@code a[base1] > a[base2]}), and
	 * the last element of the first run ({@code a[base1 + len1 - 1]}) must be greater than all
	 * elements of the second run.
	 * <p>
	 * For performance, this method should be called only when {@code len1 <= len2}; its twin,
	 * {@link #mergeHi} should be called if {@code len1 >= len2}. (Either method may be called if
	 * {@code len1 == len2}.)
	 * <p>
	 * @param base1 index of first element in first run to be merged
	 * @param len1  length of first run to be merged (must be greater than 0)
	 * @param base2 index of first element in second run to be merged (must be {@code aBase + aLen})
	 * @param len2  length of second run to be merged (must be greater than 0)
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void mergeLo(int base1, int len1, int base2, int len2) {
		assert len1 > 0 && len2 > 0 && base1 + len1 == base2;

		// Copy first run into temporary array
		Object[] a = this.a; // for performance
		Object[] tmp = ensureCapacity(len1);

		int cursor1 = tmpBase; // indexes into tmp array
		int cursor2 = base2; // indexes int a
		int dest = base1; // indexes int a
		System.arraycopy(a, base1, tmp, cursor1, len1);

		// Move first element of second run and deal with degenerate cases
		a[dest++] = a[cursor2++];
		if (--len2 == 0) {
			System.arraycopy(tmp, cursor1, a, dest, len1);
			return;
		}
		if (len1 == 1) {
			System.arraycopy(a, cursor2, a, dest, len2);
			a[dest + len2] = tmp[cursor1]; // last elt of run 1 to end of merge
			return;
		}

		// Use local variable for performance
		int minGallop = this.minGallop;
outer:  while (true) {
			int count1 = 0; // number of times in a row that first run won
			int count2 = 0; // number of times in a row that second run won

			/*
			 * Do the straightforward thing until (if ever) one run starts winning consistently.
			 */
			do {
				assert len1 > 1 && len2 > 0;
				if (((Comparable) a[cursor2]).compareTo(tmp[cursor1]) < 0) {
					a[dest++] = a[cursor2++];
					count2++;
					count1 = 0;
					if (--len2 == 0) {
						break outer;
					}
				} else {
					a[dest++] = tmp[cursor1++];
					count1++;
					count2 = 0;
					if (--len1 == 1) {
						break outer;
					}
				}
			} while ((count1 | count2) < minGallop);

			/*
			 * One run is winning so consistently that galloping may be a huge win. So try that, and
			 * continue galloping until (if ever) neither run appears to be winning consistently
			 * anymore.
			 */
			do {
				assert len1 > 1 && len2 > 0;
				count1 = gallopRight((Comparable) a[cursor2], tmp, cursor1, len1, 0);
				if (count1 != 0) {
					System.arraycopy(tmp, cursor1, a, dest, count1);
					dest += count1;
					cursor1 += count1;
					len1 -= count1;
					if (len1 <= 1) // len1 == 1 || len1 == 0
					{
						break outer;
					}
				}
				a[dest++] = a[cursor2++];
				if (--len2 == 0) {
					break outer;
				}

				count2 = gallopLeft((Comparable) tmp[cursor1], a, cursor2, len2, 0);
				if (count2 != 0) {
					System.arraycopy(a, cursor2, a, dest, count2);
					dest += count2;
					cursor2 += count2;
					len2 -= count2;
					if (len2 == 0) {
						break outer;
					}
				}
				a[dest++] = tmp[cursor1++];
				if (--len1 == 1) {
					break outer;
				}
				minGallop--;
			} while (count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);
			if (minGallop < 0) {
				minGallop = 0;
			}
			minGallop += 2; // penalize for leaving gallop mode
		} // end of "outer" loop
		this.minGallop = minGallop < 1 ? 1 : minGallop; // write back to field

		switch (len1) {
			case 0:
				throw new IllegalArgumentException(
						"Comparison method violates its general contract!");
			case 1:
				assert len2 > 0;
				System.arraycopy(a, cursor2, a, dest, len2);
				a[dest + len2] = tmp[cursor1]; //  Last elt of run 1 to end of merge
				break;
			default:
				assert len2 == 0;
				assert len1 > 1;
				System.arraycopy(tmp, cursor1, a, dest, len1);
		}
	}

	/**
	 * Like {@link #mergeLo}, except that this method should be called only if {@code len1 >= len2};
	 * {@link #mergeLo} should be called if {@code len1 <= len2}. (Either method may be called if
	 * {@code len1 == len2}.)
	 * <p>
	 * @param base1 index of first element in first run to be merged
	 * @param len1  length of first run to be merged (must be greater than 0)
	 * @param base2 index of first element in second run to be merged (must be {@code aBase + aLen})
	 * @param len2  length of second run to be merged (must be greater than 0)
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void mergeHi(int base1, int len1, int base2, int len2) {
		assert len1 > 0 && len2 > 0 && base1 + len1 == base2;

		// Copy second run into temporary array
		Object[] a = this.a; // for performance
		Object[] tmp = ensureCapacity(len2);
		int tmpBase = this.tmpBase;
		System.arraycopy(a, base2, tmp, tmpBase, len2);

		int cursor1 = base1 + len1 - 1; // indexes into a
		int cursor2 = tmpBase + len2 - 1; // indexes into tmp array
		int dest = base2 + len2 - 1; // indexes into a

		// Move last element of first run and deal with degenerate cases
		a[dest--] = a[cursor1--];
		if (--len1 == 0) {
			System.arraycopy(tmp, tmpBase, a, dest - (len2 - 1), len2);
			return;
		}
		if (len2 == 1) {
			dest -= len1;
			cursor1 -= len1;
			System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
			a[dest] = tmp[cursor2];
			return;
		}

		// Use local variable for performance
		int minGallop = this.minGallop;
outer:  while (true) {
			int count1 = 0; // number of times in a row that first run won
			int count2 = 0; // number of times in a row that second run won

			/*
			 * Do the straightforward thing until (if ever) one run appears to win consistently.
			 */
			do {
				assert len1 > 0 && len2 > 1;
				if (((Comparable) tmp[cursor2]).compareTo(a[cursor1]) < 0) {
					a[dest--] = a[cursor1--];
					count1++;
					count2 = 0;
					if (--len1 == 0) {
						break outer;
					}
				} else {
					a[dest--] = tmp[cursor2--];
					count2++;
					count1 = 0;
					if (--len2 == 1) {
						break outer;
					}
				}
			} while ((count1 | count2) < minGallop);

			/*
			 * One run is winning so consistently that galloping may be a huge win. So try that, and
			 * continue galloping until (if ever) neither run appears to be winning consistently
			 * anymore.
			 */
			do {
				assert len1 > 0 && len2 > 1;
				count1 = len1 - gallopRight((Comparable) tmp[cursor2], a, base1, len1, len1 - 1);
				if (count1 != 0) {
					dest -= count1;
					cursor1 -= count1;
					len1 -= count1;
					System.arraycopy(a, cursor1 + 1, a, dest + 1, count1);
					if (len1 == 0) {
						break outer;
					}
				}
				a[dest--] = tmp[cursor2--];
				if (--len2 == 1) {
					break outer;
				}

				count2 = len2 - gallopLeft((Comparable) a[cursor1], tmp, tmpBase, len2, len2 - 1);
				if (count2 != 0) {
					dest -= count2;
					cursor2 -= count2;
					len2 -= count2;
					System.arraycopy(tmp, cursor2 + 1, a, dest + 1, count2);
					if (len2 <= 1) {
						break outer; // len2 == 1 || len2 == 0
					}
				}
				a[dest--] = a[cursor1--];
				if (--len1 == 0) {
					break outer;
				}
				minGallop--;
			} while (count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);
			if (minGallop < 0) {
				minGallop = 0;
			}
			minGallop += 2; // penalize for leaving gallop mode
		} // end of "outer" loop
		this.minGallop = minGallop < 1 ? 1 : minGallop; // write back to field

		switch (len2) {
			case 0:
				throw new IllegalArgumentException(
						"Comparison method violates its general contract!");
			case 1:
				assert len1 > 0;
				dest -= len1;
				cursor1 -= len1;
				System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
				a[dest] = tmp[cursor2]; // move first elt of run2 to front of merge
				break;
			default:
				assert len1 == 0;
				assert len2 > 0;
				System.arraycopy(tmp, tmpBase, a, dest - (len2 - 1), len2);
				break;
		}
	}

	/**
	 * Ensures that the external array {@code tmp} has at least the specified number of elements,
	 * increasing its size if necessary. The size increases exponentially to ensure amortized linear
	 * time complexity.
	 * <p>
	 * @param minCapacity the minimum required capacity of the {@code tmp} array
	 * <p>
	 * @return {@code tmp}, whether or not it grew
	 */
	protected Object[] ensureCapacity(int minCapacity) {
		if (tmpLen < minCapacity) {
			// Compute smallest power of 2 > minCapacity
			int newSize = minCapacity;
			newSize |= newSize >> 1;
			newSize |= newSize >> 2;
			newSize |= newSize >> 4;
			newSize |= newSize >> 8;
			newSize |= newSize >> 16;
			newSize++;

			if (newSize < 0) // not bloody likely!
			{
				newSize = minCapacity;
			} else {
				newSize = Math.min(newSize, a.length >>> 1);
			}

			@SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
			Object[] newArray = new Object[newSize];
			tmp = newArray;
			tmpLen = newSize;
			tmpBase = 0;
		}
		return tmp;
	}
}
