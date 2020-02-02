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

#include "ceres/iterable/IArray.h"


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

Array* IArray_new(const natural initialSize)
{
	Array* a = _NEW(Array);

	_PRINT_DEBUG(_S("<newIArray>"));
	if (a != NULL)
	{
		a->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		a->length = 0;
		a->element.type = _INTEGER_TYPE;
		a->element.size = INTEGER_SIZE;
		a->size = 0;
		a->elements = NULL;
		IArray_reset(a, initialSize);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_ARRAY_NAME);
	}
	_PRINT_DEBUG(_S("</newIArray>"));
	return a;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void IArray_reset(void* array, const natural initialSize)
{
	_PRINT_DEBUG(_S("<resetIArray>"));
	_IF (_CHECK(array, _ARRAY_NAME))
	{
		/* Get the Array */
		Array* a = (Array*) array;

		/* INHERITANCE */
		List_reset(a, a->length, a->element.type, a->element.size, a->size);

		/* FUNCTIONS */
		/* - List */
		a->get = IArray_get;
		/* - Collection */
		a->add = IArray_add;
		a->addValue = IArray_add_value;
		a->addStructure = IArray_add_Structure;
		a->addAll = IArray_add_all;
		a->clear = IArray_clear;
		a->contains = IArray_contains;
		a->containsValue = IArray_contains_value;
		a->containsStructure = IArray_contains_Structure;
		a->containsAll = IArray_contains_all;
		a->count = IArray_count;
		a->countValue = IArray_count_value;
		a->countStructure = IArray_count_Structure;
		a->countAll = IArray_count_all;
		a->remove = IArray_remove;
		a->removeValue = IArray_remove_value;
		a->removeStructure = IArray_remove_Structure;
		a->removeAll = IArray_remove_all;
		a->resize = IArray_resize;
		/* - Iterable */
		a->iterator = IArray_iterator;
		/* - Comparable */
		a->compareTo = IArray_compare_to;
		/* - Basic */
		a->release = IArray_release;
		a->clone = IArray_clone;
		a->equals = IArray_equals;
		a->hash = IArray_hash;
		a->toString = IArray_to_string;

		/* ATTRIBUTES */
		/* Release and free the elements */
		a->clear(a);
		_FREE(a->elements);
		a->size = 0;
		/* Allocate memory for the elements */
		a->elements = _ARRAY_NEW(initialSize, a->element.size);
		a->element.value = a->elements;
		if (a->elements != NULL)
		{
			a->size = initialSize;
		}
		else
		{
			_PRINT_ERROR_ARRAY_ALLOCATION(a->element.type);
		}
	}
	_PRINT_DEBUG(_S("</resetIArray>"));
}


/***************************************************************************************************
 * LIST
 **************************************************************************************************/

Structure IArray_get(const void* list, const natural index)
{
	_IF (_CHECK(list, _ARRAY_NAME))
	{
		/* Get the Array */
		const Array* a = (Array*) list;

		if (index < a->length)
		{
			/* Get the Structure of the elements of the Array */
			Structure s = a->element;

			s.value = &((integer*) a->elements)[index];
			return s;
		}
		else
		{
			_PRINT_ERROR_INDEX(index, a->length);
		}
	}
	return _STRUCTURE_DEFAULT();
}


/***************************************************************************************************
 * COLLECTION
 **************************************************************************************************/

boolean IArray_add(void* collection, const type type, void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		Array* a = (Array*) collection;
		/* Declare the variable(s) */
		integer* v;

		/* Get the value */
		if (v = (integer*) _VALUE_GET(a->element.type, type, value))
		{
			/* Resize the Array if necessary */
			if (a->size > a->length
				|| a->resize(a, (a->size + 1) << _RESIZE_FACTOR))
			{
				/* Add the value to the Array and get the next element to be set */
				*((integer*) a->element.value) = *v;
				++a->length;
				a->element.value = (integer*) a->element.value + 1;
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

boolean IArray_add_value(void* collection, void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		Array* a = (Array*) collection;

		/* Resize the Array if necessary */
		if (a->size > a->length
			|| a->resize(a, (a->size + 1) << _RESIZE_FACTOR))
		{
			/* Add the value to the Array and get the next element to be set */
			*((integer*) a->element.value) = *((integer*) value);
			++a->length;
			a->element.value = (integer*) a->element.value + 1;
			return _TRUE;
		}
	}
	return _FALSE;
}

boolean IArray_add_Structure(void* collection, const Structure* structure)
{
	if (_STRUCTURE_CHECK(structure))
	{
		return IArray_add(collection, structure->type, structure->value);
	}
	return _FALSE;
}

boolean IArray_add_all(void* collection, const void* values)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(values, _COLLECTION_NAME _S(" of values")))
	{
		/* Get the Array and the Collection of values */
		Array* a = (Array*) collection;
		const Collection* vs = (Collection*) values;

		/* If the values are of the same type as the elements of the Array */
		if (vs->element.type == a->element.type)
		{
			/* Resize the Array if necessary */
			if (a->size >= a->length + vs->length
				|| a->resize(a, a->length + vs->length))
			{
				/* Declare the iteration variable(s) */
				Iterator it = vs->iterator(vs);
				const integer* v;

				/* Add the values to the Array */
				while (v = (integer*) it.next(&it))
				{
					*((integer*) a->element.value) = *v;
					++a->length;
					a->element.value = (integer*) a->element.value + 1;
				}
				return _TRUE;
			}
		}
			/* If the values are Structures */
		else if (vs->element.type == _STRUCTURE_TYPE)
		{
			_ARRAY_ADD_STRUCTURES(a, vs);
		}
			/* If the values are Objects */
		else if (vs->element.type == _OBJECT_TYPE)
		{
			_ARRAY_ADD_OBJECTS(a, vs);
		}
			/* Otherwise */
		else
		{
			_PRINT_ERROR_TYPE(vs->element.type, a->element.type);
		}
	}
	return _FALSE;
}

boolean IArray_add_Array(void* collection, const Array* values)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(values, _COLLECTION_NAME _S(" of values")))
	{
		/* Get the Array and the Array of values */
		Array* a = (Array*) collection;
		const Array* vs = (Array*) values;

		/* If the values are of the same type as the elements of the Array */
		if (vs->element.type == a->element.type)
		{
			/* Resize the Array if necessary */
			if (a->size >= a->length + vs->length
				|| a->resize(a, a->length + vs->length))
			{
				/* Declare the iteration variable(s) */
				Iterator it = vs->iterator(vs);
				const integer* v;

				/* Add the values to the Array */
				while (v = (integer*) it.next(&it))
				{
					*((integer*) a->element.value) = *v;
					++a->length;
					a->element.value = (integer*) a->element.value + 1;
				}
				return _TRUE;
			}
		}
			/* Otherwise */
		else
		{
			_PRINT_ERROR_TYPE(vs->element.type, a->element.type);
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

void IArray_clear(void* collection)
{
	_PRINT_DEBUG(_S("<clearIArray>"));
	_IF (_CHECK(collection, _ARRAY_NAME))
	{
		/* Get the Array */
		Array* a = (Array*) collection;

		a->length = 0;
		a->element.value = a->elements;
	}
	_PRINT_DEBUG(_S("</clearIArray>"));
}

/**************************************************************************************************/

boolean IArray_contains(const void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		const Array* a = (Array*) collection;
		/* Declare the variable(s) */
		const integer* v;
		/* Declare the iteration variable(s) */
		natural i;
		integer* e;

		/* Get the value */
		if (v = (integer*) _VALUE_GET(a->element.type, type, value))
		{
			/* Check the value for containment in the Array */
			for (i = 0, e = (integer*) a->elements; i < a->length; ++i, ++e)
			{
				if (*v == *e)
				{
					return _TRUE;
				}
			}
		}
	}
	return _FALSE;
}

boolean IArray_contains_value(const void* collection, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		const Array* a = (Array*) collection;
		/* Get the value */
		const integer v = *((integer*) value);
		/* Declare the iteration variable(s) */
		natural i;
		integer* e;

		/* Check the value for containment in the Array */
		for (i = 0, e = (integer*) a->elements; i < a->length; ++i, ++e)
		{
			if (v == *e)
			{
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

boolean IArray_contains_Structure(const void* collection, const Structure* structure)
{
	if (_STRUCTURE_CHECK(structure))
	{
		return IArray_contains(collection, structure->type, structure->value);
	}
	return _FALSE;
}

boolean IArray_contains_all(const void* collection, const void* values)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(values, _COLLECTION_NAME _S(" of values")))
	{
		/* Get the Array */
		const Array* a = (Array*) collection;
		/* Clone the values to be checked for containment */
		Collection* vs = (Collection*) ((Collection*) values)->clone(values);

		/* If the values are of the same type as the elements of the Array */
		if (vs->element.type == a->element.type)
		{
			/* Declare the iteration variable(s) */
			const integer* e;

			_ARRAY_CONTAINS_ALL(a, vs, e);
		}
			/* Otherwise */
		else
		{
			/* Declare the iteration variable(s) */
			natural i;
			address e;

			for (i = 0, e = (address) a->elements; i < a->length; ++i, e += a->element.size)
			{
				if (vs->remove(vs, a->element.type, e)
					&& vs->isEmpty(vs))
				{
					_RELEASE(vs);
					return _TRUE;
				}
			}
		}
		_RELEASE(vs);
	}
	return _FALSE;
}

/**************************************************************************************************/

natural IArray_count(const void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		const Array* a = (Array*) collection;
		/* Declare the variable(s) */
		const integer* v;
		/* Declare the iteration variable(s) */
		integer* element = a->elements;

		/* Get the value */
		if (v = (integer*) _VALUE_GET(a->element.type, type, value))
		{
			/* Return the number of occurrences of the value in the Array */
			_ARRAY_COUNT(a->length, *v, element);
		}
	}
	return 0;
}

natural IArray_count_value(const void* collection, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		const Array* a = (Array*) collection;
		/* Get the value */
		const integer v = *((integer*) value);
		/* Declare the iteration variable(s) */
		integer* element = a->elements;

		/* Return the number of occurrences of the value in the Array */
		_ARRAY_COUNT(a->length, v, element);
	}
	return 0;
}

natural IArray_count_Structure(const void* collection, const Structure* structure)
{
	if (_STRUCTURE_CHECK(structure))
	{
		return IArray_count(collection, structure->type, structure->value);
	}
	return 0;
}

natural IArray_count_all(const void* collection, const void* values)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(values, _COLLECTION_NAME _S(" of values")))
	{
		/* Get the Array and the Collection of values */
		const Array* a = (Array*) collection;
		const Collection* vs = (Collection*) values;

		/* If the values are of the same type as the elements of the Array */
		if (vs->element.type == a->element.type)
		{
			/* Declare the iteration variable(s) */
			const integer* e;

			_ARRAY_COUNT_ALL(a, vs, e);
		}
			/* Otherwise */
		else
		{
			/* Declare the variable(s) */
			natural counter = 0;
			/* Declare the iteration variable(s) */
			natural i;
			address e;

			for (i = 0, e = (address) a->elements;
				i < a->length;
				++i, e += a->element.size)
			{
				if (vs->contains(vs, a->element.type, e))
				{
					++counter;
				}
			}
			return counter;
		}
	}
	return 0;
}

/**************************************************************************************************/

boolean IArray_remove(void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		Array* a = (Array*) collection;
		/* Declare the variable(s) */
		const integer* v;
		/* Declare the iteration variable(s) */
		natural i;
		integer* e;

		/* Get the value */
		if (v = (integer*) _VALUE_GET(a->element.type, type, value))
		{
			/* Remove the value from the Array */
			for (i = 0, e = (integer*) a->elements; i < a->length; ++i, ++e)
			{
				if (*v == *e)
				{
					_ARRAY_REMOVE(a->length, a->element.size, i, e);
					--a->length;
					a->element.value = (integer*) a->element.value - 1;
					return _TRUE;
				}
			}
		}
	}
	return _FALSE;
}

boolean IArray_remove_value(void* collection, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		Array* a = (Array*) collection;
		/* Get the value */
		const integer v = *((integer*) value);
		/* Declare the iteration variable(s) */
		natural i;
		integer* e;

		/* Remove the value from the Array */
		for (i = 0, e = (integer*) a->elements; i < a->length; ++i, ++e)
		{
			if (v == *e)
			{
				_ARRAY_REMOVE(a->length, a->element.size, i, e);
				--a->length;
				a->element.value = (integer*) a->element.value - 1;
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

boolean IArray_remove_Structure(void* collection, const Structure* structure)
{
	if (_STRUCTURE_CHECK(structure))
	{
		return IArray_remove(collection, structure->type, structure->value);
	}
	return _FALSE;
}

boolean IArray_remove_all(void* collection, const void* values)
{
	/* Declare the variable(s) */
	boolean isModified = _FALSE;

	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(values, _COLLECTION_NAME _S(" of values")))
	{
		/* Get the Array and the Collection of values */
		Array* a = (Array*) collection;
		const Collection* vs = (Collection*) values;
		/* Declare the iteration variable(s) */
		natural i = 0;
		Iterator it = a->iterator(a);
		integer* e = (integer*) it.next(&it);

		while (i < a->length)
		{
			if (vs->containsStructure(vs, &it.element))
			{
				isModified = _TRUE;
				_ARRAY_REMOVE(a->length, a->element.size, i, e);
				--a->length;
				a->element.value = (integer*) a->element.value - 1;
			}
			else
			{
				++i;
				e = (integer*) it.next(&it);
			}
		}
	}
	return isModified;
}

/**************************************************************************************************/

boolean IArray_resize(void* collection, const natural size)
{
	_PRINT_DEBUG(_S("<resizeIArray>"));
	_IF (_CHECK(collection, _ARRAY_NAME))
	{
		/* Get the Array */
		Array* a = (Array*) collection;
		/* Declare the elements of the Array */
		integer* es;

		/* Release the surplus elements of the Array */
		if (a->length > size)
		{
			a->length = size;
			a->element.value = &((integer*) a->elements)[size];
		}
		/* Resize */
		es = (integer*) _ARRAY_RESIZE(a->elements, a->element.size, size);
		if (es != NULL)
		{
			a->size = size;
			a->elements = es;
			a->element.value = &es[a->length];
			_PRINT_DEBUG(_S("</resizeIArray>"));
			return _TRUE;
		}
		else if (size == 0)
		{
			a->size = size;
			a->elements = es;
			a->element.value = es;
			_PRINT_DEBUG(_S("</resizeIArray>"));
			return _TRUE;
		}
		else
		{
			_PRINT_ERROR_ARRAY_REALLOCATION(a->element.type);
		}
	}
	_PRINT_DEBUG(_S("</resizeIArray>"));
	return _FALSE;
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

Iterator IArray_iterator(const void* iterable)
{
	_IF (_CHECK(iterable, _ARRAY_NAME))
	{
		/* Get the Array */
		const Array* a = (Array*) iterable;

		return Iterator_create(a->length, a->element.type, a->element.size, a->elements, integer_Iterator_next);
	}
	return _ITERATOR_DEFAULT;
}

/**************************************************************************************************/

void* IArray_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			Array* node = (Array*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable IArray_create_Comparable(void)
{
	return Comparable_create(IArray_release, IArray_clone, IArray_equals, IArray_hash, IArray_to_string, IArray_compare_to);
}

integer IArray_compare_to(const void* structure, const type type, const void* value)
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
			/* Get the Array and the Iterable structure */
			const Array* a = (Array*) structure;
			const Iterable* vs = (Iterable*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Iterator it1 = a->iterator(a);
			const void* e;
			Iterator it2 = vs->iterator(vs);
			integer comparison;
			/* Get the minimum length */
			const natural length = _MIN(it1.length, it2.length);

			for (i = 0, e = it1.next(&it1), it2.next(&it2);
				i < length;
				++i, e = it1.next(&it1), it2.next(&it2))
			{
				comparison = integer_compare_to(e, it2.element.type, it2.element.value);
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

void IArray_release(void* structure)
{
	_PRINT_DEBUG(_S("<releaseIArray>"));
	if (structure != NULL)
	{
		/* Get the Array */
		Array* a = (Array*) structure;

		/* Release and free the elements */
		a->clear(a);
		_FREE(a->elements);
		a->size = 0;
		/* Free the Array */
		if (a->core.isDynamic)
		{
			_FREE(a);
		}
	}
	else
	{
		_PRINT_WARNING_NULL(_ARRAY_NAME);
	}
	_PRINT_DEBUG(_S("</releaseIArray>"));
}

/**************************************************************************************************/

void* IArray_clone(const void* structure)
{
	_IF (_CHECK(structure, _ARRAY_NAME))
	{
		/* Get the Array */
		const Array* a = (Array*) structure;
		/* Construct the clone dynamically */
		Array* clone = IArray_new(a->length);

		clone->addAll(clone, a);
		return clone;
	}
	return NULL;
}

/**************************************************************************************************/

boolean IArray_equals(const void* structure, const type type, const void* value)
{
	return IArray_compare_to(structure, type, value) == 0;
}

/**************************************************************************************************/

integer IArray_hash(const void* structure)
{
	integer code;

	if (structure != NULL)
	{
		/* Get the Array */
		const Array* a = (Array*) structure;
		/* Declare the iteration variable(s) */
		Iterator it = a->iterator(a);
		const void* e;
		boolean isLeft = _TRUE;

		code = (integer) _ARRAY_TYPE;
		while (e = it.next(&it))
		{
			if (isLeft)
			{
				code = bits_rotate_left((natural) code, _THIRD_BITS_NUMBER);
			}
			else
			{
				code = bits_rotate_right((natural) code, _EIGHTH_BITS_NUMBER);
			}
			code ^= integer_hash(e);
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

boolean IArray_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		boolean isNotFull;
		/* Get the Array */
		const Array* a = (Array*) source;
		/* Declare the iteration variable(s) */
		Iterator it = a->iterator(a);
		const void* e;

		isNotFull = string_to_string(_S("("), target);
		if (isNotFull)
		{
			if (e = it.next(&it))
			{
				isNotFull = integer_append_to_string(e, target);
				while (isNotFull && (e = it.next(&it)))
				{
					isNotFull = string_append_to_string(_S(", "), target)
						&& integer_append_to_string(e, target);
				}
			}
			isNotFull = isNotFull && string_append_to_string(_S(")"), target);
		}
		return isNotFull;
	}
	return _FALSE;
}

boolean IArray_append_to_string(const void* source, string target)
{
	string buffer;

	IArray_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
