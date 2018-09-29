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

#include "ceres/iterable/CommonIterator.h"


/***************************************************************************************************
 * CONSTANTS
 **************************************************************************************************/

const size ITERATOR_SIZE = sizeof (Iterator);


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

Iterator Iterator_create(const natural length, const type elementType, const natural elementSize, void* node, void* (*next)(struct Iterator* iterator))
{
	Iterator it;

	it.core = Core_create(_FALSE, _FALSE, _TRUE, _FALSE);
	Iterator_reset(&it, length, elementType, elementSize, node, next);
	return it;
}

Iterator* Iterator_new(const natural length, const type elementType, const natural elementSize, void* node, void* (*next)(struct Iterator* iterator))
{
	Iterator* it = _NEW(Iterator);

	_PRINT_DEBUG(_S("<newIterator>"));
	if (it != NULL)
	{
		it->core = Core_create(_TRUE, _FALSE, _TRUE, _FALSE);
		Iterator_reset(it, length, elementType, elementSize, node, next);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_ITERATOR_NAME);
	}
	_PRINT_DEBUG(_S("</newIterator>"));
	return it;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void Iterator_reset(void* iterator, const natural length, const type elementType, const natural elementSize, void* node, void* (*next)(struct Iterator* iterator))
{
	_PRINT_DEBUG(_S("<resetIterator>"));
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		/* Get the Iterator */
		Iterator* it = (Iterator*) iterator;

		/* FUNCTIONS */
		/* - Iterator */
		if (next != NULL)
		{
			it->next = next;
		}
		else
		{
			it->next = Iterator_next;
		}
		/* - Basic */
		it->release = Iterator_release;
		it->clone = Iterator_clone;
		it->equals = Iterator_equals;
		it->hash = Iterator_hash;
		it->toString = Iterator_to_string;

		/* ATTRIBUTES */
		/* - Iterator */
		it->length = length;
		it->element.core = Core_create(_FALSE, _TRUE, type_is_Basic(elementType), type_is_Comparable(elementType));
		it->element.type = elementType;
		it->element.size = elementSize;
		it->element.value = NULL;
		it->index = 0;
		it->node = node;
	}
	_PRINT_DEBUG(_S("</resetIterator>"));
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

void* Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			iterator->element.value = iterator->node;
			++iterator->index;
			iterator->node = _ELEMENT_GET_NEXT(iterator->node, iterator->element.size);
			return iterator->element.value;
		}
	}
	return NULL;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

void Iterator_release(void* structure)
{
	_PRINT_DEBUG(_S("<releaseIterator>"));
	if (structure != NULL)
	{
		/* Get the Iterator */
		Iterator* it = (Iterator*) structure;

		/* Free the Iterator */
		if (it->core.isDynamic)
		{
			_FREE(it);
		}
	}
	else
	{
		_PRINT_WARNING_NULL(_ITERATOR_NAME);
	}
	_PRINT_DEBUG(_S("</releaseIterator>"));
}

/**************************************************************************************************/

void* Iterator_clone(const void* structure)
{
	_IF (_CHECK(structure, _ITERATOR_NAME))
	{
		/* Get the Iterator */
		const Iterator* it = (Iterator*) structure;
		/* Construct the clone dynamically */
		Iterator* clone = Iterator_new(it->length, it->element.type, it->element.size, it->node, it->next);

		clone->element.value = it->element.value;
		clone->index = it->index;
		return clone;
	}
	return NULL;
}

/**************************************************************************************************/

boolean Iterator_equals(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_ITERATOR_TYPE, structure, type, value)
		&& type == _ITERATOR_TYPE)
	{
		if (structure == value)
		{
			return _TRUE;
		}
		else
		{
			/* Get the Iterators */
			const Iterator* it1 = (Iterator*) structure;
			const Iterator* it2 = (Iterator*) value;

			if (it1->length == it2->length
				&& Structures_equals(&it1->element, &it2->element)
				&& it1->index == it2->index
				&& it1->node == it2->node)
			{
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

integer Iterator_hash(const void* structure)
{
	if (structure != NULL)
	{
		/* Get the Iterator */
		const Iterator* it = (Iterator*) structure;
		/* Create a Structure for the node */
		Structure n = it->element;

		n.value = it->node;
		return bits_hash(6,
			_ITERATOR_TYPE,
			(integer) it->length,
			hash(&it->element),
			(integer) it->index,
			hash(&n));
	}
	return integer_random();
}

/**************************************************************************************************/

boolean Iterator_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the Iterator */
		const Iterator* it = (Iterator*) source;

		string_format(target, _S("%s[length=%n | index=%n | node=%p | element=%p | next=%p]"), _ITERATOR_NAME, it->length, it->index, it->node, it->element, it->next);
		return _TRUE;
	}
	return _FALSE;
}

boolean Iterator_append_to_string(const void* source, string target)
{
	string buffer;

	Iterator_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
