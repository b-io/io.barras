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

#include "ceres/iterable/Set.h"


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

Set* Set_new(const type elementType, const natural elementSize, const natural initialSize, const Comparable comparator)
{
	Set* s = _NEW(Set);

	_PRINT_DEBUG(_S("<newSet>"));
	if (s != NULL)
	{
		s->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		s->length = 0;
		s->element.type = elementType;
		s->element.size = elementSize;
		s->size = 0;
		s->elements = NULL;
		s->comparator = comparator;
		Set_reset(s, elementType, elementSize, initialSize, comparator);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_SET_NAME);
	}
	_PRINT_DEBUG(_S("</newSet>"));
	return s;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void Set_reset(void* set, const type elementType, const natural elementSize, const natural initialSize, const Comparable comparator)
{
	_PRINT_DEBUG(_S("<resetSet>"));
	_IF (_CHECK(set, _SET_NAME))
	{
		/* Get the Set */
		Set* s = (Set*) set;

		/* INHERITANCE */
		Array_reset(s, s->element.type, s->element.size, s->length);

		/* FUNCTIONS */
		/* - Collection */
		s->add = Set_add;
		s->addValue = Set_add_value;
		s->addStructure = Set_add_Structure;
		s->addAll = Collection_add_all_and_resize;
		/* - Iterable */
		s->iterator = Array_iterator;
		/* - Comparable */
		s->compareTo = Set_compare_to;
		/* - Basic */
		s->release = Set_release;
		s->clone = Set_clone;
		s->equals = Set_equals;
		s->hash = Set_hash;
		s->toString = Set_to_string;

		/* ATTRIBUTES */
		/* Release and free the elements */
		s->clear(s);
		_FREE(s->elements);
		s->size = 0;
		/* Allocate memory for the elements */
		s->elements = array_new(initialSize, elementSize);
		s->element.type = elementType;
		s->element.size = elementSize;
		s->element.value = s->elements;
		if (s->elements != NULL)
		{
			s->size = initialSize;
		}
		else
		{
			_PRINT_ERROR_ARRAY_ALLOCATION(elementType);
		}
		s->comparator = comparator;
	}
	_PRINT_DEBUG(_S("</resetSet>"));
}


/***************************************************************************************************
 * COLLECTION
 **************************************************************************************************/

boolean Set_add(void* collection, const type type, void* value)
{
	_IF (_CHECK(collection, _SET_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Set */
		Set* s = (Set*) collection;

		if (s->contains(s, type, value))
		{
			return _TRUE;
		}
		else
		{
			/* Resize the Set if necessary */
			if (s->size > s->length
				|| s->resize(s, (s->size + 1) << _RESIZE_FACTOR))
			{
				/* Add the value to the Set and get the next element to be set */
				if (s->element.value = array_add(s->element.type, s->element.size, s->element.value, value))
				{
					++s->length;
					return _TRUE;
				}
			}
		}
	}
	return _FALSE;
}

boolean Set_add_value(void* collection, void* value)
{
	_IF (_CHECK(collection, _SET_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Set */
		Set* s = (Set*) collection;

		if (s->containsValue(s, value))
		{
			return _TRUE;
		}
		else
		{
			/* Resize the Set if necessary */
			if (s->size > s->length
				|| s->resize(s, (s->size + 1) << _RESIZE_FACTOR))
			{
				/* Add the value to the Set and get the next element to be set */
				if (s->element.value = array_add(s->element.type, s->element.size, s->element.value, value))
				{
					++s->length;
					return _TRUE;
				}
			}
		}
	}
	return _FALSE;
}

boolean Set_add_Structure(void* collection, const Structure* structure)
{
	if (_STRUCTURE_CHECK(structure))
	{
		return Set_add(collection, structure->type, structure->value);
	}
	return _FALSE;
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

void* Set_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			Set* node = (Set*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable Set_create_Comparable(void)
{
	return Comparable_create(Set_release, Set_clone, Set_equals, Set_hash, Set_to_string, Set_compare_to);
}

integer Set_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_ARRAY_TYPE, structure, type, value)
		&& type_is_Iterable(type))
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the Set and the Iterable structure */
			const Set* s = (Set*) structure;
			const Iterable* vs = (Iterable*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Iterator it1 = s->iterator(s);
			Iterator it2 = vs->iterator(vs);
			integer comparison;
			/* Get the minimum length */
			const natural length = _MIN(it1.length, it2.length);

			for (i = 0, it1.next(&it1), it2.next(&it2);
				i < length;
				++i, it1.next(&it1), it2.next(&it2))
			{
				comparison = s->comparator.compareTo(it1.element.value, it2.element.type, it2.element.value);
				if (comparison != 0)
				{
					return comparison;
				}
			}
			return _COMPARE_TO(it1.length, it2.length);
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

void Set_release(void* structure)
{
	_PRINT_DEBUG(_S("<releaseSet>"));
	if (structure != NULL)
	{
		/* Get the Set */
		Set* s = (Set*) structure;

		/* Release and free the elements */
		s->clear(s);
		_FREE(s->elements);
		s->size = 0;
		/* Free the Set */
		if (s->core.isDynamic)
		{
			_FREE(s);
		}
	}
	else
	{
		_PRINT_WARNING_NULL(_SET_NAME);
	}
	_PRINT_DEBUG(_S("</releaseSet>"));
}

/**************************************************************************************************/

void* Set_clone(const void* structure)
{
	_IF (_CHECK(structure, _SET_NAME))
	{
		/* Get the Set */
		const Set* s = (Set*) structure;
		/* Construct the clone dynamically */
		Set* clone = Set_new(s->element.type, s->element.size, s->length, s->comparator);

		clone->addAll(clone, s);
		return clone;
	}
	return NULL;
}

/**************************************************************************************************/

boolean Set_equals(const void* structure, const type type, const void* value)
{
	return Set_compare_to(structure, type, value) == 0;
}

/**************************************************************************************************/

integer Set_hash(const void* structure)
{
	integer code;

	if (structure != NULL)
	{
		/* Get the Set */
		const Set* s = (Set*) structure;
		/* Declare the iteration variable(s) */
		Iterator it = s->iterator(s);
		boolean isLeft = _TRUE;

		code = (integer) _ARRAY_TYPE;
		while (it.next(&it))
		{
			if (isLeft)
			{
				code = bits_rotate_left((natural) code, _THIRD_BITS_NUMBER);
			}
			else
			{
				code = bits_rotate_right((natural) code, _EIGHTH_BITS_NUMBER);
			}
			code ^= hash(&it.element);
			isLeft = !isLeft;
		}
	}
	else
	{
		code = integer_random();
	}
	return code;
}

/**************************************************************************************************/

boolean Set_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		boolean isNotFull;
		/* Get the Set */
		const Set* s = (Set*) source;
		/* Declare the iteration variable(s) */
		Iterator it = s->iterator(s);
		const void* e;

		isNotFull = string_to_string(_S("{"), target);
		if (isNotFull)
		{
			if (e = it.next(&it))
			{
				isNotFull = append_to_string(e, s->element.type, target);
				while (isNotFull && (e = it.next(&it)))
				{
					isNotFull = string_append_to_string(_S(", "), target)
						&& append_to_string(e, s->element.type, target);
				}
			}
			isNotFull = isNotFull && string_append_to_string(_S("}"), target);
		}
		return isNotFull;
	}
	return _FALSE;
}

boolean Set_append_to_string(const void* source, string target)
{
	string buffer;

	Set_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
