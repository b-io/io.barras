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

#include "ceres/iterable/SortedSet.h"


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

SortedSet* SortedSet_new(const type elementType, const natural elementSize, const natural initialSize, const Comparable comparator)
{
	SortedSet* ss = _NEW(SortedSet);

	_PRINT_DEBUG(_S("<newSortedSet>"));
	if (ss != NULL)
	{
		ss->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		ss->length = 0;
		ss->element.type = elementType;
		ss->element.size = elementSize;
		ss->size = 0;
		ss->elements = NULL;
		ss->comparator = comparator;
		SortedSet_reset(ss, elementType, elementSize, initialSize, comparator);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_SORTED_SET_NAME);
	}
	_PRINT_DEBUG(_S("</newSortedSet>"));
	return ss;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void SortedSet_reset(void* sortedSet, const type elementType, const natural elementSize, const natural initialSize, const Comparable comparator)
{
	_PRINT_DEBUG(_S("<resetSortedSet>"));
	_IF (_CHECK(sortedSet, _SORTED_SET_NAME))
	{
		/* Get the Sorted Set */
		SortedSet* ss = (SortedSet*) sortedSet;

		/* INHERITANCE */
		Set_reset(ss, elementType, elementSize, initialSize, comparator);

		/* FUNCTIONS */
		/* - Collection */
		ss->add = SortedSet_add;
		ss->addValue = SortedSet_add_value;
		ss->addStructure = SortedSet_add_Structure;
		/* - Basic */
		ss->clone = SortedSet_clone;
	}
	_PRINT_DEBUG(_S("</resetSortedSet>"));
}


/***************************************************************************************************
 * COLLECTION
 **************************************************************************************************/

boolean SortedSet_add(void* collection, const type type, void* value)
{
	_IF (_CHECK(collection, _SORTED_SET_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Sorted Set */
		SortedSet* ss = (SortedSet*) collection;

		if (ss->contains(ss, type, value))
		{
			return _TRUE;
		}
		else
		{
			/* Resize the Sorted Set if necessary */
			if (ss->size > ss->length
				|| ss->resize(ss, (ss->size + 1) << _RESIZE_FACTOR))
			{
				/* Declare the iteration variable(s) */
				natural i;
				address e;

				/* Find the element that is greater or equal to the value */
				for (i = 0, e = (address) ss->elements;
					i < ss->length && ss->comparator.compareTo(value, ss->element.type, e) > 0;
					++i, e += ss->element.size);
				/* Insert the value into the Sorted Set and get the next element to be set */
				if (array_insert(ss->length, ss->element.size, i, e, value))
				{
					++ss->length;
					ss->element.value = _ELEMENT_GET_NEXT(ss->element.value, ss->element.size);
					return _TRUE;
				}
			}
		}
	}
	return _FALSE;
}

boolean SortedSet_add_value(void* collection, void* value)
{
	_IF (_CHECK(collection, _SORTED_SET_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Sorted Set */
		SortedSet* ss = (SortedSet*) collection;

		if (ss->containsValue(ss, value))
		{
			return _TRUE;
		}
		else
		{
			/* Resize the Sorted Set if necessary */
			if (ss->size > ss->length
				|| ss->resize(ss, (ss->size + 1) << _RESIZE_FACTOR))
			{
				/* Declare the iteration variable(s) */
				natural i;
				address e;

				/* Find the element that is greater or equal to the value */
				for (i = 0, e = (address) ss->elements;
					i < ss->length && ss->comparator.compareTo(value, ss->element.type, e) > 0;
					++i, e += ss->element.size);
				/* Insert the value into the Sorted Set and get the next element to be set */
				if (array_insert(ss->length, ss->element.size, i, e, value))
				{
					++ss->length;
					ss->element.value = _ELEMENT_GET_NEXT(ss->element.value, ss->element.size);
					return _TRUE;
				}
			}
		}
	}
	return _FALSE;
}

boolean SortedSet_add_Structure(void* collection, const Structure* structure)
{
	if (_STRUCTURE_CHECK(structure))
	{
		return SortedSet_add(collection, structure->type, structure->value);
	}
	return _FALSE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

void* SortedSet_clone(const void* structure)
{
	_IF (_CHECK(structure, _SORTED_SET_NAME))
	{
		/* Get the Sorted Set */
		const SortedSet* ss = (SortedSet*) structure;
		/* Construct the clone dynamically */
		SortedSet* clone = SortedSet_new(ss->element.type, ss->element.size, ss->length, ss->comparator);

		clone->addAll(clone, ss);
		return clone;
	}
	return NULL;
}
