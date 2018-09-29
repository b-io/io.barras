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

#include "ceres/type/CommonBoolean.h"


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

void* boolean_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			boolean* node = (boolean*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable boolean_create_Comparable(void)
{
	return Comparable_create(NULL, NULL, boolean_equals, boolean_hash, boolean_to_string, boolean_compare_to);
}

integer boolean_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_BOOLEAN_TYPE, structure, type, value)
		&& type == _BOOLEAN_TYPE)
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the booleans */
			const boolean* b1 = (boolean*) structure;
			const boolean* b2 = (boolean*) value;

			return _COMPARE_TO(*b1, *b2);
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

boolean boolean_equals(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_BOOLEAN_TYPE, structure, type, value)
		&& type == _BOOLEAN_TYPE)
	{
		if (structure == value)
		{
			return _TRUE;
		}
		else
		{
			return *((boolean*) structure) == *((boolean*) value);
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

integer boolean_hash(const void* structure)
{
	if (structure != NULL)
	{
		/* Get the boolean */
		boolean* b = (boolean*) structure;

		return bits_hash(2, _BOOLEAN_TYPE, (integer) (*b));
	}
	return integer_random();
}

/**************************************************************************************************/

boolean boolean_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the boolean */
		const boolean* b = (boolean*) source;

		if (*b)
		{
			string_to_string(_TRUE_STRING, target);
		}
		else
		{
			string_to_string(_FALSE_STRING, target);
		}
		return _TRUE;
	}
	return _FALSE;
}

boolean boolean_append_to_string(const void* source, string target)
{
	string buffer;

	boolean_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
