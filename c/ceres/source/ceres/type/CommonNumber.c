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
/***************************************************************************************************
 * INCLUDES
 **************************************************************************************************/

#include "ceres/type/CommonNumber.h"


/***************************************************************************************************
 * CONSTANTS
 **************************************************************************************************/

const size NUMBER_SIZE = sizeof (Number);


/***************************************************************************************************
 * CHECK
 **************************************************************************************************/

boolean Number_check(Number* number)
{
	if (_CHECK(number, _NUMBER_NAME))
	{
		if (_BASE_CHECK(number->base))
		{
			return _TRUE;
		}
		else
		{
			number->length = 0;
		}
	}
	return _FALSE;
}

boolean base_check(const natural base)
{
	/* Check if the base is greater than or equal to 2 */
	if (base >= 2)
	{
		/* Check if the base is smaller than or equal to the limit */
		if (base <= _NUMBER_BASE_MAX)
		{
			return _TRUE;
		}
		else
		{
			_PRINT_ERROR_GREATER_THAN(_S("specified base"), _NUMBER_BASE_MAX);
		}
	}
	else
	{
		_PRINT_ERROR_LESS_THAN(_S("specified base"), 2);
	}
	return _FALSE;
}


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

Number Number_create(const natural n, const natural base)
{
	Number number;

	number.core = Core_create(_FALSE, _FALSE, _TRUE, _TRUE);
	Number_reset(&number, n, base);
	return number;
}

Number* Number_new(const natural n, const natural base)
{
	Number* number = _NEW(Number);

	_PRINT_DEBUG(_S("<newNumber>"));
	if (number != NULL)
	{
		number->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		Number_reset(number, n, base);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_NUMBER_NAME);
	}
	_PRINT_DEBUG(_S("</newNumber>"));
	return number;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void Number_reset(void* structure, const natural n, const natural base)
{
	_PRINT_DEBUG(_S("<resetNumber>"));
	_IF (_CHECK(structure, _NUMBER_NAME))
	{
		/* Get the Number */
		Number* number = (Number*) structure;

		/* FUNCTIONS */
		/* - Number */
		number->changeBase = Number_change_base;
		number->toDecimal = Number_to_decimal;
		number->toNatural = Number_to_natural;
		number->toZero = Number_to_zero;
		/* - Comparable */
		number->compareTo = Number_compare_to;
		/* - Basic */
		number->release = Number_release;
		number->clone = Number_clone;
		number->equals = Number_equals;
		number->hash = Number_hash;
		number->toString = Number_to_string;

		/* ATTRIBUTES */
		/* - Number */
		if (!natural_to_Number(n, base, number))
		{
			number->toZero(number);
		}
	}
	_PRINT_DEBUG(_S("</resetNumber>"));
}


/***************************************************************************************************
 * NUMBER
 **************************************************************************************************/

void Number_change_base(void* number, const natural toBase)
{
	_IF (_CHECK(number, _NUMBER_NAME))
	{
		/* Get the Number */
		Number* n = (Number*) number;

		_IF (_BASE_CHECK(toBase) && n->base != toBase)
		{
			const natural decimal = n->toDecimal(n);

			n->base = toBase;
			decimal_to_Number(decimal, n);
		}
	}
}

natural Number_to_decimal(const void* number)
{
	_IF (_CHECK(number, _NUMBER_NAME))
	{
		natural output = 0;
		/* Get the Number */
		Number* n = (Number*) number;
		/* Declare the iteration variable(s) */
		natural i;

		for (i = 0; i < n->length; ++i)
		{
			output += n->digits[i] * real_to_natural(pow((real) n->base, (real) (n->length - 1 - i)));
		}
		return output;
	}
	return 0;
}

natural Number_to_natural(const void* number)
{
	_IF (_CHECK(number, _NUMBER_NAME))
	{
		natural output = 0;
		/* Get the Number */
		Number* n = (Number*) number;
		/* Declare the iteration variable(s) */
		natural i;

		for (i = 0; i < n->length; ++i)
		{
			output += n->digits[i] * real_to_natural(pow(10., (real) (n->length - 1 - i)));
		}
		return output;
	}
	return 0;
}

void Number_to_zero(void* number)
{
	_IF (_CHECK(number, _NUMBER_NAME))
	{
		/* Get the Number */
		Number* n = (Number*) number;
		/* Declare the iteration variable(s) */
		natural i;

		for (i = 0; i < _NUMBER_LENGTH_MAX; ++i)
		{
			n->digits[i] = 0;
		}
		n->length = 1;
		n->base = 10;
	}
}


/***************************************************************************************************
 * CONVERSIONS
 **************************************************************************************************/

boolean decimal_to_Number(const natural decimal, Number* number)
{
	_IF (_NUMBER_CHECK(number))
	{
		/* If the decimal number is greater than 0 */
		if (decimal > 0)
		{
			/* Compute the number of digits required to represent the decimal number in the base of the Number */
			const natural length = floor_to_natural(log((real) decimal) / log((real) number->base)) + 1;

			/* Check if the length is smaller than the size */
			if (length <= _NUMBER_LENGTH_MAX)
			{
				/* Declare the iteration variable(s) */
				natural quotient;
				natural i;
				natural remainder;

				/* Set the length */
				number->length = length;
				/* Set the digits (change the base of the decimal number to the base of the Number) */
				quotient = decimal;
				i = 0;
				do
				{
					remainder = quotient % number->base;
					number->digits[length - 1 - i] = (digit) remainder;
					quotient = (quotient - remainder) / number->base;
					++i;
				}
				while (quotient > 0);
				return _TRUE;
			}
			else
			{
				/* The length is greater than _NUMBER_LENGTH_MAX */
				_PRINT_ERROR_GREATER_THAN(_S("length"), _NUMBER_LENGTH_MAX);
				/* Set the length */
				number->length = 0;
			}
		}
			/* Else the digit is 0 */
		else if (decimal == 0)
		{
			/* Set the length */
			number->length = 1;
			/* Set the digits */
			number->digits[0] = 0;
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

boolean natural_to_Number(const natural n, const natural base, Number* number)
{
	_IF (_CHECK(number, _NUMBER_NAME))
	{
		_IF (_BASE_CHECK(base))
		{
			string buffer;
			/* Declare the iteration variable(s) */
			natural i;
			/* Get the maximum length */
			natural max;

			natural_to_string(&n, buffer);
			max = string_length(buffer);
			if (max > _STRING_LENGTH_MAX)
			{
				max = _STRING_LENGTH_MAX;
			}
			number->length = max;
			/* Set the base */
			number->base = base;
			/* Set the digits */
			i = 0;
			while (i < max)
			{
				number->digits[i] = num_char_to_digit(buffer[i]);
				++i;
			}
			return _TRUE;
		}
	}
	return _FALSE;
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

void* Number_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			Number* node = (Number*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable Number_create_Comparable(void)
{
	return Comparable_create(Number_release, Number_clone, Number_equals, Number_hash, Number_to_string, Number_compare_to);
}

integer Number_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_NUMBER_TYPE, structure, type, value))
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the decimal number */
			const natural n1 = ((Number*) structure)->toDecimal(structure);

			switch (type)
			{
				case _DIGIT_TYPE:
				{
					const digit* n2 = (digit*) value;

					return _COMPARE_TO(n1, *n2);
				}
				case _INTEGER_TYPE:
				{
					const integer* n2 = (integer*) value;

					return _COMPARE_TO(n1, *n2);
				}
				case _NATURAL_TYPE:
				{
					const natural* n2 = (natural*) value;

					return _COMPARE_TO(n1, *n2);
				}
				case _REAL_TYPE:
				{
					const real* n2 = (real*) value;

					return _REAL_COMPARE_TO(n1, *n2);
				}
				case _NUMBER_TYPE:
				{
					const natural n2 = ((Number*) value)->toDecimal(value);

					return _COMPARE_TO(n1, n2);
				}
			}
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

void Number_release(void* structure)
{
	_PRINT_DEBUG(_S("<releaseNumber>"));
	if (structure != NULL)
	{
		/* Get the Number */
		Number* n = (Number*) structure;

		/* Free the Number */
		if (n->core.isDynamic)
		{
			_FREE(n);
		}
	}
	else
	{
		_PRINT_WARNING_NULL(_NUMBER_NAME);
	}
	_PRINT_DEBUG(_S("<releaseNumber>"));
}

/**************************************************************************************************/

void* Number_clone(const void* structure)
{
	_IF (_CHECK(structure, _NUMBER_NAME))
	{
		/* Get the Number */
		const Number* n = (Number*) structure;

		/* Construct the clone dynamically */
		return Number_new(n->toNatural(n), n->base);
	}
	return NULL;
}

/**************************************************************************************************/

boolean Number_equals(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_NUMBER_TYPE, structure, type, value))
	{
		if (structure == value)
		{
			return _TRUE;
		}
		else
		{
			/* Get the decimal number */
			const natural n = ((Number*) structure)->toDecimal(structure);

			switch (type)
			{
				case _DIGIT_TYPE:
					return n == *((digit*) value);
				case _INTEGER_TYPE:
					return n == *((integer*) value);
				case _NATURAL_TYPE:
					return n == *((natural*) value);
				case _REAL_TYPE:
				{
					const real* r = (real*) value;

					return _REAL_EQUALS(n, *r);
				}
				case _NUMBER_TYPE:
					return n == ((Number*) value)->toDecimal(value);
			}
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

integer Number_hash(const void* structure)
{
	if (structure != NULL)
	{
		/* Get the Number */
		const Number* number = (Number*) structure;
		/* Get the natural number */
		const natural n = number->toNatural(number);

		return bits_hash(2, _NUMBER_TYPE, natural_hash(&n));
	}
	return integer_random();
}

/**************************************************************************************************/

boolean Number_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the Number */
		const Number* n = (Number*) source;

		if (n->length > 0)
		{
			/* If the base is equal to 26 (hexavigesimal),
			 * use the Latin alphabet to represent the Number
			 */
			if (n->base == 26)
			{
				/* Declare the iteration variable(s) */
				natural i;
				/* Get the maximum length */
				natural max;

				if (n->length > _STRING_LENGTH_MAX)
				{
					max = _STRING_LENGTH_MAX;
				}
				else
				{
					max = n->length;
				}
				for (i = 0; i < max; ++i)
				{
					target[i] = digit_to_alpha_char(n->digits[i]);
				}
				/* End the string */
				target[i] = _STRING_END;
			}
				/* Else use the Arabic numerals (0-9) and the Latin letters (A-Z) */
			else
			{
				return digits_to_string(n->digits, n->length, target);
			}
		}
		else
		{
			string_reset(target);
		}
		return _TRUE;
	}
	return _FALSE;
}

boolean Number_append_to_string(const void* source, string target)
{
	string buffer;

	Number_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
