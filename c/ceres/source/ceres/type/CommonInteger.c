/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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

#include "ceres/type/CommonInteger.h"


/***************************************************************************************************
 * GENERATIONS
 **************************************************************************************************/

integer* sequence_new(const natural size)
{
	return sequence_from_step_new(size, 0, 1);
}

integer* sequence_from_new(const natural size, const integer start)
{
	return sequence_from_step_new(size, start, 1);
}

integer* sequence_from_step_new(const natural size, const integer start, const integer step)
{
	/* Declare the array of values */
	integer* vs;
	/* Declare the iteration variable(s) */
	natural i;
	integer v;

	vs = _ARRAY_NEW(size, INTEGER_SIZE);
	for (i = 0, v = start; i < size; ++i, v += step)
	{
		vs[i] = v;
	}
	return vs;
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

void* integer_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			integer* node = (integer*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable integer_create_Comparable(void)
{
	return Comparable_create(NULL, NULL, integer_equals, integer_hash, integer_to_string, integer_compare_to);
}

integer integer_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_INTEGER_TYPE, structure, type, value))
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the integer */
			const integer* n1 = (integer*) structure;

			switch (type)
			{
				case _DIGIT_TYPE:
				{
					const digit* n2 = (digit*) value;

					return _COMPARE_TO(*n1, *n2);
				}
				case _INTEGER_TYPE:
				{
					const integer* n2 = (integer*) value;

					return _COMPARE_TO(*n1, *n2);
				}
				case _NATURAL_TYPE:
				{
					const natural* n2 = (natural*) value;

					return _COMPARE_TO(*n1, *n2);
				}
				case _REAL_TYPE:
				{
					const real* n2 = (real*) value;

					return _REAL_COMPARE_TO(*n1, *n2);
				}
				case _NUMBER_TYPE:
				{
					const natural n2 = ((Number*) value)->toDecimal(value);

					return _COMPARE_TO(*n1, n2);
				}
			}
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

boolean integer_equals(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_INTEGER_TYPE, structure, type, value))
	{
		if (structure == value)
		{
			return _TRUE;
		}
		else
		{
			/* Get the integer */
			const integer* n = (integer*) structure;

			switch (type)
			{
				case _DIGIT_TYPE:
					return *n == *((digit*) value);
				case _INTEGER_TYPE:
					return *n == *((integer*) value);
				case _NATURAL_TYPE:
					return *n == *((natural*) value);
				case _REAL_TYPE:
				{
					const real* r = (real*) value;

					return _REAL_EQUALS(*n, *r);
				}
				case _NUMBER_TYPE:
					return *n == ((Number*) value)->toDecimal(value);
			}
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

integer integer_hash(const void* structure)
{
	if (structure != NULL)
	{
		/* Get the integer */
		const integer* i = (integer*) structure;

		return bits_hash(2, _INTEGER_TYPE, *i);
	}
	return integer_random();
}

/**************************************************************************************************/

boolean integer_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the integer */
		const integer* i = (integer*) source;

#if _32_BITS
		_SPRINTF(target, _STRING_SIZE, _S("%ld"), *i);
#else
		_SPRINTF(target, _STRING_SIZE, _S("%lld"), *i);
#endif
		return _TRUE;
	}
	return _FALSE;
}

boolean integer_append_to_string(const void* source, string target)
{
	string buffer;

	integer_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
