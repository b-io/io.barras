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


/*******************************************************************************
 * INCLUDES
 ******************************************************************************/

#include "ceres/type/CommonReal.h"


/*******************************************************************************
 * REAL
 ******************************************************************************/

real to_real(const Structure* structure)
{
	switch (structure->type)
	{
		case _BOOLEAN_TYPE:
			return (real) *((boolean*) structure->value);
		case _DIGIT_TYPE:
			return (real) *((digit*) structure->value);
		case _INTEGER_TYPE:
			return (real) *((integer*) structure->value);
		case _NATURAL_TYPE:
			return (real) *((natural*) structure->value);
		case _REAL_TYPE:
			return *((real*) structure->value);
		case _NUMBER_TYPE:
			return (real) Number_to_decimal(structure->value);
		default:
			_PRINT_ERROR_NOT_NUMERIC_TYPE(structure->type);
	}
	return 0.;
}


/*******************************************************************************
 * ITERABLE
 ******************************************************************************/

void* real_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			real* node = (real*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/*******************************************************************************
 * COMPARABLE
 ******************************************************************************/

Comparable real_create_Comparable(void)
{
	return Comparable_create(NULL, NULL, real_equals, real_hash, real_to_string, real_compare_to);
}

integer real_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_REAL_TYPE, structure, type, value))
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the real number */
			const real* n1 = (real*) structure;

			switch (type)
			{
				case _DIGIT_TYPE:
				{
					const digit* n2 = (digit*) value;

					return _REAL_COMPARE_TO(*n1, *n2);
				}
				case _INTEGER_TYPE:
				{
					const integer* n2 = (integer*) value;

					return _REAL_COMPARE_TO(*n1, *n2);
				}
				case _NATURAL_TYPE:
				{
					const natural* n2 = (natural*) value;

					return _REAL_COMPARE_TO(*n1, *n2);
				}
				case _REAL_TYPE:
				{
					const real* n2 = (real*) value;

					return _REAL_COMPARE_TO(*n1, *n2);
				}
				case _NUMBER_TYPE:
				{
					const natural n2 = ((Number*) value)->toDecimal(value);

					return _REAL_COMPARE_TO(*n1, n2);
				}
			}
		}
	}
	return _NOT_COMPARABLE;
}


/*******************************************************************************
 * BASIC
 ******************************************************************************/

boolean real_equals(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_REAL_TYPE, structure, type, value))
	{
		if (structure == value)
		{
			return _TRUE;
		}
		else
		{
			/* Get the real number */
			const real* n1 = (real*) structure;

			switch (type)
			{
				case _DIGIT_TYPE:
				{
					const digit* n2 = (digit*) value;

					return _REAL_EQUALS(*n1, *n2);
				}
				case _INTEGER_TYPE:
				{
					const integer* n2 = (integer*) value;

					return _REAL_EQUALS(*n1, *n2);
				}
				case _NATURAL_TYPE:
				{
					const natural* n2 = (natural*) value;

					return _REAL_EQUALS(*n1, *n2);
				}
				case _REAL_TYPE:
				{
					const real* n2 = (real*) value;

					return _REAL_EQUALS(*n1, *n2);
				}
				case _NUMBER_TYPE:
				{
					const natural n2 = ((Number*) value)->toDecimal(value);

					return _REAL_EQUALS(*n1, n2);
				}
			}
		}
	}
	return _FALSE;
}

/******************************************************************************/

integer real_hash(const void* structure)
{
	if (structure != NULL)
	{
		/* Get the real number */
		const real* r = (real*) structure;

		return bits_hash(2, _REAL_TYPE, (integer) (*r));
	}
	return integer_random();
}

/******************************************************************************/

boolean real_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the real number */
		const real* r = (real*) source;
		/* Get the absolute value of the real number */
		const real av = real_abs(*r);

		if (av < _ENGINEERING_NOTATION_FROM)
		{
			_SPRINTF(target, _STRING_SIZE, _S("%.") _DECIMALS_NUMBER _S("Lf"), *r);
		}
		else
		{
			_SPRINTF(target, _STRING_SIZE, _S("%.") _DECIMALS_NUMBER _S("Le"), *r);
		}
		return _TRUE;
	}
	return _FALSE;
}

boolean real_append_to_string(const void* source, string target)
{
	string buffer;

	real_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
