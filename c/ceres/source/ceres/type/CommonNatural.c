/*
 * The MIT License (MIT)
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

#include "ceres/type/CommonNatural.h"


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

void* natural_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			natural* node = (natural*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable natural_create_Comparable(void)
{
	return Comparable_create(NULL, NULL, natural_equals, natural_hash, natural_to_string, natural_compare_to);
}

integer natural_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_NATURAL_TYPE, structure, type, value))
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the natural number */
			const natural* n1 = (natural*) structure;

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

integer natural_hash(const void* structure)
{
	if (structure != NULL)
	{
		/* Get the natural number */
		const natural* n = (natural*) structure;

		return bits_hash(2, _NATURAL_TYPE, (integer) (*n));
	}
	return integer_random();
}

/**************************************************************************************************/

boolean natural_equals(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_NATURAL_TYPE, structure, type, value))
	{
		if (structure == value)
		{
			return _TRUE;
		}
		else
		{
			/* Get the natural number */
			const natural* n = (natural*) structure;

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

boolean natural_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the natural number */
		const natural* n = (natural*) source;

#if defined(__GNUC__)
		_SPRINTF(target, _STRING_SIZE, _S("%zu"), *n);
#elif defined(_MSC_VER)
		_SPRINTF(target, _STRING_SIZE, _S("%Iu"), *n);
#else
		_SPRINTF(target, _STRING_SIZE, _S("%llu"), *n);
#endif
		return _TRUE;
	}
	return _FALSE;
}

boolean natural_append_to_string(const void* source, string target)
{
	string buffer;

	natural_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
