/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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

#ifdef __cplusplus
extern "C"
{
#endif
#ifndef _SORTING_FUNCTIONS_H
#define _SORTING_FUNCTIONS_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include "ceres/Common.h"


	/***********************************************************************************************
	 * STRUCTURES
	 **********************************************************************************************/

	/**
	 * Defines the partition of integers for Quicksort.
	 */
	typedef struct IPartition
	{
		integer* a;
		integer* b;
	} IPartition;

	/**
	 * Defines the partition of Objects.
	 */
	typedef struct OPartition
	{
		Object* a;
		Object* b;
	} OPartition;


	/***********************************************************************************************
	 * QUICKSORT
	 **********************************************************************************************/

	/**
	 * Sorts an array in O(n * log(n)) time in average (worst case is n^2).
	 */
	void integers_quicksort(integer* array, const natural length);

	/**
	 * Processes 1 step of Quicksort.
	 */
	void integers_quicksort_step(integer* from, integer* to);

	/**
	 * Sorts the integers in order to form 2 sub-arrays:
	 * - the first array contains integers that are lower than or equal to the pivot and
	 * - the second array contains integers that are greater than or equal to the pivot.
	 */
	IPartition integers_quicksort_partition(integer* from, integer* to, const integer* pivot);

	/**********************************************************************************************/

	/**
	 * Sorts an array in O(n * log(n)) time in average (worst case is n^2).
	 */
	void integers_dual_pivot_quicksort(integer* array, const natural length);

	/**
	 * Processes 1 step of Dual-Pivot Quicksort.
	 */
	void integers_dual_pivot_quicksort_step(integer* from, integer* to);

	/**********************************************************************************************/

	/**
	 * Sorts an array in O(n * log(n)) time in average (worst case is n^2).
	 */
	void Objects_quicksort(Object* array, const natural length);

	/**
	 * Processes 1 step of Quicksort.
	 */
	void Objects_quicksort_step(Object* from, Object* to);

	/**
	 * Sorts the Objects in order to form 2 sub-arrays:
	 * - the first array contains Objects that are lower than or equal to the pivot and
	 * - the second array contains Objects that are greater than or equal to the pivot.
	 */
	OPartition Objects_quicksort_partition(Object* from, Object* to, const Object* pivot);


	/***********************************************************************************************
	 * RANDOMIZED QUICKSORT
	 **********************************************************************************************/

	/**
	 * Randomized Quicksort is similar to Quicksort except that the algorithm
	 * chooses a random pivot at each step. This gives the desirable property
	 * that the algorithm requires only O(n * log(n)) time in expectation.
	 */
	void integers_random_quicksort(integer* array, const natural length);

	/**
	 * Processes 1 step of Randomized Quicksort.
	 */
	void integers_random_quicksort_step(integer* from, integer* to);

	/**********************************************************************************************/

	/**
	 * Randomized Quicksort is similar to Quicksort except that the algorithm
	 * chooses a random pivot at each step. This gives the desirable property
	 * that the algorithm requires only O(n * log(n)) time in expectation.
	 */
	void Objects_random_quicksort(Object* array, const natural length);

	/**
	 * Processes 1 step of Randomized Quicksort.
	 */
	void Objects_random_quicksort_step(Object* from, Object* to);


#endif /* _SORTING_FUNCTIONS_H */
#ifdef __cplusplus
}
#endif
