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

#include "ceres/iterable/ArrayList.h"


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

ArrayList* ArrayList_new(const natural initialSize)
{
	ArrayList* al = _NEW(ArrayList);

	_PRINT_DEBUG(_S("<newArrayList>"));
	if (al != NULL)
	{
		al->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		al->length = 0;
		al->size = 0;
		al->elements = NULL;
		al->next = NULL;
		ArrayList_reset(al, initialSize);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_ARRAY_LIST_NAME);
	}
	_PRINT_DEBUG(_S("</newArrayList>"));
	return al;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void ArrayList_reset(void* arrayList, const natural initialSize)
{
	_PRINT_DEBUG(_S("<resetArrayList>"));
	_IF (_CHECK(arrayList, _ARRAY_LIST_NAME))
	{
		/* Get the Array List */
		ArrayList* al = (ArrayList*) arrayList;

		/* INHERITANCE */
		List_reset(al, al->length, _OBJECT_TYPE, OBJECT_SIZE, al->size);

		/* FUNCTIONS */
		/* - List */
		al->get = ArrayList_get;
		al->removeAt = ArrayList_remove_at;
		/* - Collection */
		al->add = ArrayList_add;
		al->addStructure = ArrayList_add_Structure;
		al->clear = ArrayList_clear;
		al->contains = ArrayList_contains;
		al->containsStructure = ArrayList_contains_Structure;
		al->count = ArrayList_count;
		al->countStructure = ArrayList_count_Structure;
		al->remove = ArrayList_remove;
		al->removeStructure = ArrayList_remove_Structure;
		al->removeAll = ArrayList_remove_all;
		al->resize = ArrayList_resize;
		/* - Iterable */
		al->iterator = ArrayList_iterator;
		/* - Comparable */
		al->compareTo = ArrayList_compare_to;
		/* - Basic */
		al->release = ArrayList_release;
		al->clone = ArrayList_clone;
		al->equals = ArrayList_equals;

		/* ATTRIBUTES */
		/* Release and free the elements */
		al->clear(al);
		_FREE(al->elements);
		al->size = 0;
		/* Allocate memory for the elements */
		al->elements = _ARRAY_NEW(initialSize, al->element.size);
		al->next = al->elements;
		if (al->elements != NULL)
		{
			al->size = initialSize;
		}
		else
		{
			_PRINT_ERROR_ARRAY_ALLOCATION(_OBJECT_TYPE);
		}
	}
	_PRINT_DEBUG(_S("</resetArrayList>"));
}


/***************************************************************************************************
 * LIST
 **************************************************************************************************/

Structure ArrayList_get(const void* list, const natural index)
{
	_IF (_CHECK(list, _ARRAY_LIST_NAME))
	{
		/* Get the Array List */
		const ArrayList* al = (ArrayList*) list;

		if (index < al->length)
		{
			return al->elements[index].structure;
		}
		else
		{
			_PRINT_ERROR_INDEX(index, al->length);
		}
	}
	return _STRUCTURE_DEFAULT();
}

boolean ArrayList_remove_at(void* list, const natural index)
{
	_IF (_CHECK(list, _ARRAY_LIST_NAME))
	{
		/* Get the Array List */
		ArrayList* al = (ArrayList*) list;

		if (index < al->length)
		{
			Object* e = &al->elements[index];

			_RELEASE(e);
			_ARRAY_REMOVE(al->length, OBJECT_SIZE, index, e);
			--al->length;
			--al->next;
			return _TRUE;
		}
		else
		{
			_PRINT_ERROR_INDEX(index, al->length);
		}
	}
	return _FALSE;
}


/***************************************************************************************************
 * COLLECTION
 **************************************************************************************************/

boolean ArrayList_add(void* collection, const type type, void* value)
{
	_IF (_CHECK(collection, _ARRAY_LIST_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array List */
		ArrayList* al = (ArrayList*) collection;

		/* Resize the Array List if necessary */
		if (al->size > al->length
			|| al->resize(al, (al->size + 1) << _RESIZE_FACTOR))
		{
			/* Add the value to the Array List */
			if (type == _OBJECT_TYPE)
			{
				*al->next = *((Object*) value);
			}
			else if (type == _STRUCTURE_TYPE)
			{
				/* Get the Structure */
				const Structure s = *((Structure*) value);

				*al->next = Object_create(&s);
			}
			else
			{
				/* Create the Structure */
				const Structure s = Structure_create(type, value);

				*al->next = Object_create(&s);
			}
			++al->length;
			++al->next;
			return _TRUE;
		}
	}
	return _FALSE;
}

boolean ArrayList_add_Structure(void* collection, const Structure* structure)
{
	_IF (_CHECK(collection, _ARRAY_LIST_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Array List */
		ArrayList* al = (ArrayList*) collection;

		/* Resize the Array List if necessary */
		if (al->size > al->length
			|| al->resize(al, (al->size + 1) << _RESIZE_FACTOR))
		{
			/* Add the value to the Array List */
			*al->next = (structure->type == _OBJECT_TYPE) ? *((Object*) structure->value) : Object_create(structure);
			++al->length;
			++al->next;
			return _TRUE;
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

void ArrayList_clear(void* collection)
{
	_PRINT_DEBUG(_S("<clearArrayList>"));
	_IF (_CHECK(collection, _ARRAY_LIST_NAME))
	{
		/* Get the Array List */
		ArrayList* al = (ArrayList*) collection;
		/* Declare the iteration variable(s) */
		natural i;
		Object* e;

		/* Release the elements of the Array List */
		for (i = 0, e = al->elements; i < al->length; ++i, ++e)
		{
			_RELEASE(e);
		}
		al->length = 0;
		al->next = al->elements;
	}
	_PRINT_DEBUG(_S("</clearArrayList>"));
}

/**************************************************************************************************/

boolean ArrayList_contains(const void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_LIST_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array List */
		const ArrayList* al = (ArrayList*) collection;
		/* Declare the iteration variable(s) */
		natural i;
		const Object* e;

		for (i = 0, e = al->elements; i < al->length; ++i, ++e)
		{
			if (e->equals(e, type, value))
			{
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

boolean ArrayList_contains_Structure(const void* collection, const Structure* structure)
{
	_IF (_CHECK(collection, _ARRAY_LIST_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Array List */
		const ArrayList* al = (ArrayList*) collection;
		/* Declare the iteration variable(s) */
		natural i;
		const Object* e;

		for (i = 0, e = al->elements; i < al->length; ++i, ++e)
		{
			if (Structures_equals(structure, &e->structure))
			{
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

natural ArrayList_count(const void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_LIST_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array List */
		const ArrayList* al = (ArrayList*) collection;
		/* Declare the variable(s) */
		natural counter = 0;
		/* Declare the iteration variable(s) */
		natural i;
		const Object* e;

		for (i = 0, e = al->elements; i < al->length; ++i, ++e)
		{
			if (e->equals(e, type, value))
			{
				++counter;
			}
		}
		return counter;
	}
	return 0;
}

natural ArrayList_count_Structure(const void* collection, const Structure* structure)
{
	_IF (_CHECK(collection, _ARRAY_LIST_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Array List */
		const ArrayList* al = (ArrayList*) collection;
		/* Declare the variable(s) */
		natural counter = 0;
		/* Declare the iteration variable(s) */
		natural i;
		const Object* e;

		for (i = 0, e = al->elements; i < al->length; ++i, ++e)
		{
			if (Structures_equals(structure, &e->structure))
			{
				++counter;
			}
		}
		return counter;
	}
	return 0;
}

/**************************************************************************************************/

boolean ArrayList_remove(void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_LIST_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array List */
		ArrayList* al = (ArrayList*) collection;
		/* Declare the iteration variable(s) */
		natural i;
		Object* e;

		for (i = 0, e = al->elements; i < al->length; ++i, ++e)
		{
			if (e->equals(e, type, value))
			{
				_RELEASE(e);
				_ARRAY_REMOVE(al->length, OBJECT_SIZE, i, e);
				--al->length;
				--al->next;
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

boolean ArrayList_remove_Structure(void* collection, const Structure* structure)
{
	_IF (_CHECK(collection, _ARRAY_LIST_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Array List */
		ArrayList* al = (ArrayList*) collection;
		/* Declare the iteration variable(s) */
		natural i;
		Object* e;

		for (i = 0, e = al->elements; i < al->length; ++i, ++e)
		{
			if (Structures_equals(structure, &e->structure))
			{
				_RELEASE(e);
				_ARRAY_REMOVE(al->length, OBJECT_SIZE, i, e);
				--al->length;
				--al->next;
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

boolean ArrayList_remove_all(void* collection, const void* values)
{
	/* Declare the variable(s) */
	boolean isModified = _FALSE;

	_IF (_CHECK(collection, _ARRAY_LIST_NAME)
		&& _CHECK(values, _COLLECTION_NAME _S(" of values")))
	{
		/* Get the Array List and the Collection of values */
		ArrayList* al = (ArrayList*) collection;
		const Collection* vs = (Collection*) values;
		/* Declare the iteration variable(s) */
		natural i = 0;
		Object* e = al->elements;

		while (i < al->length)
		{
			if (vs->containsStructure(vs, &e->structure))
			{
				isModified = _TRUE;
				_RELEASE(e);
				_ARRAY_REMOVE(al->length, OBJECT_SIZE, i, e);
				--al->length;
				--al->next;
			}
			else
			{
				++i;
				++e;
			}
		}
	}
	return isModified;
}

/**************************************************************************************************/

boolean ArrayList_resize(void* collection, const natural size)
{
	_PRINT_DEBUG(_S("<resizeArrayList>"));
	_IF (_CHECK(collection, _ARRAY_LIST_NAME))
	{
		/* Get the Array List */
		ArrayList* al = (ArrayList*) collection;
		/* Declare the elements of the Array List */
		Object* es;

		/* Release the surplus elements of the Array List */
		if (al->length > size)
		{
			/* Declare the iteration variable(s) */
			natural i;
			Object* e;

			for (i = size, e = &al->elements[size]; i < al->length; ++i, ++e)
			{
				_RELEASE(e);
			}
			al->length = size;
			al->next = &al->elements[size];
		}
		/* Resize */
		es = _ARRAY_RESIZE(al->elements, al->element.size, size);
		if (es != NULL)
		{
			al->size = size;
			al->elements = es;
			al->next = &es[al->length];
			_PRINT_DEBUG(_S("</resizeArrayList>"));
			return _TRUE;
		}
		else if (size == 0)
		{
			al->size = size;
			al->elements = es;
			al->next = es;
			_PRINT_DEBUG(_S("</resizeArrayList>"));
			return _TRUE;
		}
		else
		{
			_PRINT_ERROR_ARRAY_REALLOCATION(al->element.type);
		}
	}
	_PRINT_DEBUG(_S("</resizeArrayList>"));
	return _FALSE;
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

Iterator ArrayList_iterator(const void* iterable)
{
	_IF (_CHECK(iterable, _ARRAY_LIST_NAME))
	{
		/* Get the Array List */
		const ArrayList* al = (ArrayList*) iterable;

		return Iterator_create(al->length, al->element.type, al->element.size, al->elements, Object_Iterator_next);
	}
	return _ITERATOR_DEFAULT;
}

/**************************************************************************************************/

void* ArrayList_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			ArrayList* node = (ArrayList*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable ArrayList_create_Comparable(void)
{
	return Comparable_create(ArrayList_release, ArrayList_clone, ArrayList_equals, Collection_hash, Collection_to_string, ArrayList_compare_to);
}

integer ArrayList_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_ARRAY_LIST_TYPE, structure, type, value)
		&& type_is_Iterable(type))
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the Array List and the Iterable structure */
			const ArrayList* al = (ArrayList*) structure;
			const Iterable* vs = (Iterable*) value;
			/* Declare the iteration variable(s) */
			natural i;
			const Object* e;
			Iterator it = vs->iterator(vs);
			integer comparison;
			/* Get the minimum length */
			const natural length = _MIN(al->length, it.length);

			for (i = 0, e = al->elements, it.next(&it);
				i < length;
				++i, ++e, it.next(&it))
			{
				comparison = e->compareTo(e, it.element.type, it.element.value);
				if (comparison != 0)
				{
					return comparison;
				}
			}
			return _COMPARE_TO(al->length, it.length);
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

void ArrayList_release(void* structure)
{
	_PRINT_DEBUG(_S("<releaseArrayList>"));
	if (structure != NULL)
	{
		/* Get the Array List */
		ArrayList* al = (ArrayList*) structure;

		/* Release and free the elements */
		al->clear(al);
		_FREE(al->elements);
		al->size = 0;
		/* Free the Array List */
		if (al->core.isDynamic)
		{
			_FREE(al);
		}
	}
	else
	{
		_PRINT_WARNING_NULL(_ARRAY_LIST_NAME);
	}
	_PRINT_DEBUG(_S("</releaseArrayList>"));
}

/**************************************************************************************************/

void* ArrayList_clone(const void* structure)
{
	_IF (_CHECK(structure, _ARRAY_LIST_NAME))
	{
		/* Get the Array List */
		const ArrayList* al = (ArrayList*) structure;
		/* Construct the clone dynamically */
		ArrayList* clone = ArrayList_new(al->length);

		clone->addAll(clone, al);
		return clone;
	}
	return NULL;
}

/**************************************************************************************************/

boolean ArrayList_equals(const void* structure, const type type, const void* value)
{
	return ArrayList_compare_to(structure, type, value) == 0;
}
