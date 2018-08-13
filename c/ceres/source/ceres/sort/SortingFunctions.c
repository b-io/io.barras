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


/***************************************************************************************************
 * INCLUDES
 **************************************************************************************************/

#include "ceres/sort/SortingFunctions.h"


/***************************************************************************************************
 * QUICKSORT
 **************************************************************************************************/

void integers_quicksort(integer* array, const natural length)
{
	integers_quicksort_step(array, &array[length - 1]);
}

void integers_quicksort_step(integer* from, integer* to)
{
	if (from < to)
	{
		/* Split the array into 2 sub-arrays:
		 * - one contains the integers that are lower than the pivot and
		 * - the other one contains the integers that are greater than the pivot
		 */
		IPartition partition = integers_quicksort_partition(from, to, _ELEMENT_GET_MIDDLE(from, to));

		/* Recursive calls to sort the sub-arrays */
		integers_quicksort_step(from, partition.b);
		integers_quicksort_step(partition.a, to);
	}
}

IPartition integers_quicksort_partition(integer* from, integer* to, const integer* pivot)
{
	/* Declare the partition */
	IPartition partition;

	/* Initialize the partition */
	partition.a = from;
	partition.b = to;

	/* Start the partition */
	do
	{
		/* Find the next integer A from the beginning that is greater than or equal to the pivot */
		while (partition.a <= partition.b && *partition.a < *pivot) ++partition.a;

		/* Find the next integer B from the end that is lower than or equal to the pivot */
		while (partition.b >= partition.a && *partition.b > *pivot) --partition.b;

		/* Swap the integers A and B if A is listed before B */
		if (partition.a < partition.b)
		{
			_INTEGER_SWAP(partition.a, partition.b);
			++partition.a;
			--partition.b;
		}
			/* Break the loop if A and B are the same*/
		else if (partition.a == partition.b)
		{
			++partition.a;
			--partition.b;
			break;
		}
			/* Break the loop otherwise */
		else
		{
			break;
		}
	}
	while (_TRUE);
	return partition;
}

/**************************************************************************************************/

void integers_dual_pivot_quicksort(integer* array, const natural length)
{
	integers_dual_pivot_quicksort_step(array, &array[length - 1]);
}

void integers_dual_pivot_quicksort_step(integer* left, integer* right)
{
	if (left < right)
	{
		/* Declare the values of the pivots */
		integer p, q;
		/* Get the pointers */
		integer* l = left + 1;
		integer* k = l;
		integer* g = right - 1;

		if (*left > *right)
		{
			_INTEGER_SWAP(left, right);
		}
		p = *left;
		q = *right;
		while (k <= g)
		{
			if (*k < p)
			{
				_INTEGER_SWAP(k, l);
				++l;
			}
			else if (*k >= q)
			{
				while (*g > q && k < g)
				{
					--g;
				}
				_INTEGER_SWAP(k, g);
				--g;
				if (*k < p)
				{
					_INTEGER_SWAP(k, l);
					++l;
				}
			}
			++k;
		}
		--l;
		++g;
		_INTEGER_SWAP(left, l);
		_INTEGER_SWAP(right, g);
		integers_dual_pivot_quicksort_step(left, l - 1);
		integers_dual_pivot_quicksort_step(l + 1, g - 1);
		integers_dual_pivot_quicksort_step(g + 1, right);
	}
}

/**************************************************************************************************/

void Objects_quicksort(Object* array, const natural length)
{
	Objects_quicksort_step(array, &array[length - 1]);
}

void Objects_quicksort_step(Object* from, Object* to)
{
	if (from < to)
	{
		/* Split the array into 2 sub-arrays:
		 * - one contains the Objects that are lower than the pivot and
		 * - the other one contains the Objects that are greater than the pivot
		 */
		OPartition partition = Objects_quicksort_partition(from, to, _ELEMENT_GET_MIDDLE(from, to));

		/* Recursive calls to sort the sub-arrays */
		Objects_quicksort_step(from, partition.b);
		Objects_quicksort_step(partition.a, to);
	}
}

OPartition Objects_quicksort_partition(Object* from, Object* to, const Object* pivot)
{
	/* Declare the partition */
	OPartition partition;

	/* Initialize the partition */
	partition.a = from;
	partition.b = to;

	/* Start the partition */
	do
	{
		/* Find the next Object A from the beginning that is greater than or equal to the pivot */
		while (partition.a <= partition.b && Structures_compare_to(&partition.a->structure, &pivot->structure) < 0) ++partition.a;

		/* Find the next Object B from the end that is lower than or equal to the pivot */
		while (partition.b >= partition.a && Structures_compare_to(&partition.b->structure, &pivot->structure) > 0) --partition.b;

		/* Swap the Objects A and B if A is listed before B */
		if (partition.a < partition.b)
		{
			_OBJECT_SWAP(partition.a, partition.b);
			++partition.a;
			--partition.b;
		}
			/* Break the loop if A and B are the same*/
		else if (partition.a == partition.b)
		{
			++partition.a;
			--partition.b;
			break;
		}
			/* Break the loop otherwise */
		else
		{
			break;
		}
	}
	while (_TRUE);
	return partition;
}


/***************************************************************************************************
 * RANDOMIZED QUICKSORT
 **************************************************************************************************/

void integers_random_quicksort(integer* array, const natural length)
{
	integers_random_quicksort_step(array, &array[length - 1]);
}

void integers_random_quicksort_step(integer* from, integer* to)
{
	if (from < to)
	{
		/* Choose the pivot randomly */
		const integer* pivot = (integer*) natural_rand_inclusive((natural) from, (natural) to);

		/* Split the array into 2 sub-arrays:
		 * - one contains the integers that are lower than the pivot and
		 * - the other one contains the integers that are greater than the pivot
		 */
		IPartition partition = integers_quicksort_partition(from, to, pivot);

		/* Recursive calls to sort the sub-arrays */
		integers_random_quicksort_step(from, partition.b);
		integers_random_quicksort_step(partition.a, to);
	}
}

/**************************************************************************************************/

void Objects_random_quicksort(Object* array, const natural length)
{
	Objects_random_quicksort_step(array, &array[length - 1]);
}

void Objects_random_quicksort_step(Object* from, Object* to)
{
	if (from < to)
	{
		/* Choose the pivot randomly */
		const Object* pivot = (Object*) natural_rand_inclusive((natural) from, (natural) to);

		/* Split the array into 2 sub-arrays:
		 * - one contains the Objects that are lower than the pivot and
		 * - the other one contains the Objects that are greater than the pivot
		 */
		OPartition partition = Objects_quicksort_partition(from, to, pivot);

		/* Recursive calls to sort the sub-arrays */
		Objects_random_quicksort_step(from, partition.b);
		Objects_random_quicksort_step(partition.a, to);
	}
}
