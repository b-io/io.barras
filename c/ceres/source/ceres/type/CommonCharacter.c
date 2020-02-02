/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2020 Florian Barras <https://barras.io> (florian@barras.io)
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

#include "ceres/type/CommonCharacter.h"


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

void* char_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			character* node = (character*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable char_create_Comparable(void)
{
	return Comparable_create(NULL, NULL, char_equals, char_hash, char_to_string, char_compare_to);
}

integer char_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_CHARACTER_TYPE, structure, type, value)
		&& type == _CHARACTER_TYPE)
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the characters */
			const character* c1 = (character*) structure;
			const character* c2 = (character*) value;

			return _COMPARE_TO(*c1, *c2);
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

boolean char_equals(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_CHARACTER_TYPE, structure, type, value)
		&& type == _CHARACTER_TYPE)
	{
		if (structure == value)
		{
			return _TRUE;
		}
		else
		{
			return *((character*) structure) == *((character*) value);
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

integer char_hash(const void* structure)
{
	if (structure != NULL)
	{
		/* Get the character */
		const character* c = (character*) structure;

		return bits_hash(2, _CHARACTER_TYPE, (integer) (*c));
	}
	return integer_random();
}

/**************************************************************************************************/

boolean char_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the character */
		const character* c = (character*) source;

#ifdef _WIDE_STRING
		_SPRINTF(target, _STRING_SIZE, _S("%lc"), *c);
#else
		_SPRINTF(target, _STRING_SIZE, _S("%c"), *c);
#endif
		return _TRUE;
	}
	return _FALSE;
}

boolean char_append_to_string(const void* source, string target)
{
	string buffer;

	char_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
