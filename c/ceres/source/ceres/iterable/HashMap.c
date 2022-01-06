/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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

#include "ceres/iterable/HashMap.h"


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

HashMap* HashMap_new(const type elementType, const natural elementSize, const natural initialSize)
{
	HashMap* hm = _NEW(HashMap);

	_PRINT_DEBUG(_S("<newHashMap>"));
	if (hm != NULL)
	{
		hm->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		hm->length = 0;
		hm->element.type = elementType;
		hm->element.size = elementSize;
		hm->size = 0;
		hm->table = NULL;
		HashMap_reset(hm, elementType, elementSize, initialSize);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_HASH_MAP_NAME);
	}
	_PRINT_DEBUG(_S("</newHashMap>"));
	return hm;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void HashMap_reset(void* set, const type elementType, const natural elementSize, const natural initialSize)
{
	_PRINT_DEBUG(_S("<resetHashMap>"));
	_IF (_CHECK(set, _HASH_MAP_NAME))
	{
		/* Get the Hash Map */
		HashMap* hm = (HashMap*) set;

		/* INHERITANCE */
		Collection_reset(hm, hm->length, hm->element.type, hm->element.size, hm->size);

		/* FUNCTIONS */
		/* - Collection */
		hm->add = HashMap_add;
		hm->clear = HashMap_clear;
		hm->contains = HashMap_contains;
		hm->count = HashMap_count;
		hm->remove = HashMap_remove;
		hm->removeAll = HashMap_remove_all;
		hm->resize = HashMap_resize;
		/* - Iterable */
		hm->iterator = HashMap_iterator;
		/* - Comparable */
		hm->compareTo = HashMap_compare_to;
		/* - Basic */
		hm->release = HashMap_release;
		hm->clone = HashMap_clone;
		hm->equals = HashMap_equals;
		hm->hash = HashMap_hash;
		hm->toString = HashMap_to_string;

		/* ATTRIBUTES */
		/* Release and free the table */
		hm->clear(hm);
		_FREE(hm->table);
		hm->size = 0;
		/* Allocate memory for the table */
		hm->table = array_new(initialSize, sizeof (Array*));
		hm->element.type = elementType;
		hm->element.size = elementSize;
		if (hm->table != NULL)
		{
			hm->size = initialSize;
		}
		else
		{
			_PRINT_ERROR_ARRAY_ALLOCATION(elementType);
		}
	}
	_PRINT_DEBUG(_S("</resetHashMap>"));
}


/***************************************************************************************************
 * COLLECTION
 **************************************************************************************************/

boolean HashMap_add(void* collection, const type type, void* value)
{
	_IF (_CHECK(collection, _HASH_MAP_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Hash Map */
		HashMap* hm = (HashMap*) collection;

		/* Resize the Hash Map if necessary */
		if (hm->size > hm->length
			|| hm->resize(hm, (hm->size + 1) << _RESIZE_FACTOR))
		{
			const natural index = hm->comparator.hash(value) & (hm->size - 1);
			Array* a = ((Array**) hm->table)[index];

			if (a == NULL)
			{
				a = Array_new(hm->element.type, hm->element.size, 1);
				++hm->length;
			}
			a->add(a, type, value);
			return _TRUE;
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

void HashMap_clear(void* collection)
{
	_PRINT_DEBUG(_S("<clearHashMap>"));
	_IF (_CHECK(collection, _HASH_MAP_NAME))
	{
		/* Get the Hash Map */
		HashMap* hm = (HashMap*) collection;
		/* Declare the iteration variable(s) */
		natural i;
		Array** t;

		for (i = 0, t = (Array**) hm->table; i < hm->size; ++i, ++t)
		{
			_RELEASE(*t);
			*t = NULL;
		}
		hm->length = 0;
	}
	_PRINT_DEBUG(_S("</clearHashMap>"));
}

/**************************************************************************************************/

boolean HashMap_contains(const void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _HASH_MAP_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Hash Map */
		const HashMap* hm = (HashMap*) collection;

		/* TODO */
		return _TRUE;
	}
	return _FALSE;
}

/**************************************************************************************************/

natural HashMap_count(const void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _HASH_MAP_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Hash Map */
		const HashMap* hm = (HashMap*) collection;
		/* Declare the variable(s) */
		natural counter = 0;
		/* Declare the iteration variable(s) */
		Array* a;

		/* TODO */
		return counter;
	}
	return 0;
}

/**************************************************************************************************/

boolean HashMap_remove(void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _HASH_MAP_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Hash Map */
		HashMap* hm = (HashMap*) collection;
		/* Find the element with the value in the Hash Map */
		Array* a;

		/* TODO */
		return _TRUE;
	}
	return _FALSE;
}

boolean HashMap_remove_all(void* collection, const void* values)
{
	/* Declare the variable(s) */
	boolean isModified = _FALSE;

	_IF (_CHECK(collection, _HASH_MAP_NAME)
		&& _CHECK(values, _COLLECTION_NAME _S(" of values")))
	{
		/* Get the Hash Map and the Collection of values */
		HashMap* hm = (HashMap*) collection;
		const Collection* vs = (Collection*) values;
		/* Declare the iteration variable(s) */
		natural i = 0;
		Iterator it = hm->iterator(hm);
		Array* e = (Array*) it.next(&it);

		/* TODO */
	}
	return isModified;
}

boolean HashMap_resize(void* collection, const natural size)
{
	_PRINT_DEBUG(_S("<resizeHashMap>"));
	_IF (_CHECK(collection, _HASH_MAP_NAME))
	{
		/* Get the Hash Map */
		HashMap* hm = (HashMap*) collection;
		/* Declare the table of the Hash Map */
		Array** t;

		/* Release the surplus Arrays of the table */
		if (hm->length > size)
		{
			/* Declare the iteration variable(s) */
			natural i;
			Array** t;

			for (i = 0, t = (Array**) hm->table; i < hm->size; ++i, ++t)
			{
				if (i >= size)
				{
					_RELEASE(*t);
				}
				*t = NULL;
			}
			hm->length = size;
			hm->element.value = hm->table[size]; /* TO CHECK */
		}
		/* TODO */
	}
	_PRINT_DEBUG(_S("</resizeHashMap>"));
	return _FALSE;
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

Iterator HashMap_iterator(const void* iterable)
{
	_IF (_CHECK(iterable, _HASH_MAP_NAME))
	{
		/* Get the Hash Map */
		const HashMap* hm = (HashMap*) iterable;

		/* TODO */
	}
	return _ITERATOR_DEFAULT;
}

/**************************************************************************************************/

void* HashMap_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			HashMap* node = (HashMap*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable HashMap_create_Comparable(void)
{
	return Comparable_create(HashMap_release, HashMap_clone, HashMap_equals, HashMap_hash, HashMap_to_string, HashMap_compare_to);
}

integer HashMap_compare_to(const void* structure, const type type, const void* value)
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
			/* TODO */
			return _TRUE;
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

void HashMap_release(void* structure)
{
	_PRINT_DEBUG(_S("<releaseHashMap>"));
	if (structure != NULL)
	{
		/* Get the Hash Map */
		HashMap* hm = (HashMap*) structure;

		/* Release and free the table */
		hm->clear(hm);
		_FREE(hm->table);
		hm->size = 0;
		/* Free the Hash Map */
		if (hm->core.isDynamic)
		{
			_FREE(hm);
		}
	}
	else
	{
		_PRINT_WARNING_NULL(_HASH_MAP_NAME);
	}
	_PRINT_DEBUG(_S("</releaseHashMap>"));
}

/**************************************************************************************************/

void* HashMap_clone(const void* structure)
{
	_IF (_CHECK(structure, _HASH_MAP_NAME))
	{
		/* Get the Hash Map */
		const HashMap* hm = (HashMap*) structure;
		/* Construct the clone dynamically */
		HashMap* clone = HashMap_new(hm->element.type, hm->element.size, hm->length);

		clone->addAll(clone, hm);
		return clone;
	}
	return NULL;
}

/**************************************************************************************************/

boolean HashMap_equals(const void* structure, const type type, const void* value)
{
	return HashMap_compare_to(structure, type, value) == 0;
}

/**************************************************************************************************/

integer HashMap_hash(const void* structure)
{
	integer code;

	if (structure != NULL)
	{
		/* Get the Hash Map */
		const HashMap* hm = (HashMap*) structure;
		/* Declare the iteration variable(s) */
		Iterator it = hm->iterator(hm);
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

boolean HashMap_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		boolean isNotFull;
		/* Get the Hash Map */
		const HashMap* hm = (HashMap*) source;
		/* Declare the iteration variable(s) */
		Iterator it = hm->iterator(hm);
		const void* e;

		isNotFull = string_to_string(_S("{"), target);
		if (isNotFull)
		{
			if (e = it.next(&it))
			{
				isNotFull = append_to_string(e, hm->element.type, target);
				while (isNotFull && (e = it.next(&it)))
				{
					isNotFull = string_append_to_string(_S(", "), target)
						&& append_to_string(e, hm->element.type, target);
				}
			}
			isNotFull = isNotFull && string_append_to_string(_S("}"), target);
		}
		return isNotFull;
	}
	return _FALSE;
}

boolean HashMap_append_to_string(const void* source, string target)
{
	string buffer;

	HashMap_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
