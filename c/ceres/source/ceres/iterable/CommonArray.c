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

#include "ceres/iterable/CommonArray.h"


/***************************************************************************************************
 * CONSTANTS
 **************************************************************************************************/

const size ARRAY_SIZE = sizeof (Array);


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

Array* Array_new(const type elementType, const natural elementSize, const natural initialSize)
{
	Array* a = _NEW(Array);

	_PRINT_DEBUG(_S("<newArray>"));
	if (a != NULL)
	{
		a->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		a->length = 0;
		a->element.type = elementType;
		a->element.size = elementSize;
		a->size = 0;
		a->elements = NULL;
		Array_reset(a, elementType, elementSize, initialSize);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_ARRAY_NAME);
	}
	_PRINT_DEBUG(_S("</newArray>"));
	return a;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void Array_reset(void* array, const type elementType, const natural elementSize, const natural initialSize)
{
	_PRINT_DEBUG(_S("<resetArray>"));
	_IF (_CHECK(array, _ARRAY_NAME))
	{
		/* Get the Array */
		Array* a = (Array*) array;

		/* INHERITANCE */
		List_reset(a, a->length, a->element.type, a->element.size, a->size);

		/* FUNCTIONS */
		/* - List */
		a->get = Array_get;
		/* - Collection */
		a->add = Array_add;
		a->addValue = Array_add_value;
		a->addAll = Array_add_all;
		a->clear = Array_clear;
		a->contains = Array_contains;
		a->containsValue = Array_contains_value;
		a->containsAll = Array_contains_all;
		a->count = Array_count;
		a->countValue = Array_count_value;
		a->countAll = Array_count_all;
		a->remove = Array_remove;
		a->removeValue = Array_remove_value;
		a->removeAll = Array_remove_all;
		a->resize = Array_resize;
		/* - Iterable */
		a->iterator = Array_iterator;
		/* - Comparable */
		a->compareTo = Array_compare_to;
		/* - Basic */
		a->release = Array_release;
		a->clone = Array_clone;
		a->equals = Array_equals;
		a->hash = Array_hash;
		a->toString = Array_to_string;

		/* ATTRIBUTES */
		/* Release and free the elements */
		a->clear(a);
		_FREE(a->elements);
		a->size = 0;
		/* Allocate memory for the elements */
		a->elements = array_new(initialSize, elementSize);
		a->element.type = elementType;
		a->element.size = elementSize;
		a->element.value = a->elements;
		if (a->elements != NULL)
		{
			a->size = initialSize;
		}
		else
		{
			_PRINT_ERROR_ARRAY_ALLOCATION(elementType);
		}
	}
	_PRINT_DEBUG(_S("</resetArray>"));
}


/***************************************************************************************************
 * LIST
 **************************************************************************************************/

Structure Array_get(const void* list, const natural index)
{
	_IF (_CHECK(list, _ARRAY_NAME))
	{
		/* Get the Array */
		const Array* a = (Array*) list;

		if (index < a->length)
		{
			/* Get the Structure of the elements of the Array */
			Structure s = a->element;

			s.value = array_get(a->elements, a->element.type, a->element.size, index);
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

boolean Array_add(void* collection, const type type, void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		Array* a = (Array*) collection;
		/* Declare the variable(s) */
		void* v;

		/* Get the value */
		if (v = _VALUE_GET(a->element.type, type, value))
		{
			/* Resize the Array if necessary */
			if (a->size > a->length
				|| a->resize(a, (a->size + 1) << _RESIZE_FACTOR))
			{
				/* Add the value to the Array and get the next element to be set */
				if (a->element.value = array_add(a->element.type, a->element.size, a->element.value, v))
				{
					++a->length;
					return _TRUE;
				}
			}
		}
	}
	return _FALSE;
}

boolean Array_add_value(void* collection, void* value)
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
			if (a->element.value = array_add(a->element.type, a->element.size, a->element.value, value))
			{
				++a->length;
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

boolean Array_add_all(void* collection, const void* values)
{
	/* Declare the variable(s) */
	boolean isModified = _FALSE;

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

				/* Add the values to the Array */
				while (it.next(&it))
				{
					/* Add the value to the Array and get the next element to be set */
					if (a->element.value = array_add(a->element.type, a->element.size, a->element.value, it.element.value))
					{
						isModified = _TRUE;
						++a->length;
					}
				}
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
	return isModified;
}

boolean Array_add_Array(void* collection, const void* values)
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
				_ARRAY_COPY(vs->elements, vs->length, vs->element.size, a->element.value);
				a->length += vs->length;
				a->element.value = _ELEMENT_GET_NEXT(a->element.value, vs->length * vs->element.size);
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

void Array_clear(void* collection)
{
	_PRINT_DEBUG(_S("<clearArray>"));
	_IF (_CHECK(collection, _ARRAY_NAME))
	{
		/* Get the Array */
		Array* a = (Array*) collection;

		if (type_is_Basic(a->element.type))
		{
			/* Declare the iteration variable(s) */
			Iterator it = a->iterator(a);
			void* e;

			/* Release the elements of the Array */
			while (e = it.next(&it))
			{
				_STRUCTURE_RELEASE(e);
			}
		}
		a->length = 0;
		a->element.value = a->elements;
	}
	_PRINT_DEBUG(_S("</clearArray>"));
}

/**************************************************************************************************/

boolean Array_contains(const void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		const Array* a = (Array*) collection;
		/* Declare the variable(s) */
		const void* v;

		/* Get the value */
		if (v = _VALUE_GET(a->element.type, type, value))
		{
			/* Check the value for containment in the Array */
			return array_find_element(a->elements, a->length, a->element.type, a->element.size, value) != NULL;
		}
	}
	return _FALSE;
}

boolean Array_contains_value(const void* collection, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		const Array* a = (Array*) collection;

		/* Check the value for containment in the Array */
		return array_find_element(a->elements, a->length, a->element.type, a->element.size, value) != NULL;
	}
	return _FALSE;
}

boolean Array_contains_all(const void* collection, const void* values)
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
			switch (a->element.type)
			{
				case _BOOLEAN_TYPE:
				{
					/* Declare the iteration variable(s) */
					const boolean* e;

					_ARRAY_CONTAINS_ALL(a, vs, e);
					break;
				}
				case _CHARACTER_TYPE:
				{
					/* Declare the iteration variable(s) */
					const character* e;

					_ARRAY_CONTAINS_ALL(a, vs, e);
					break;
				}
				case _DIGIT_TYPE:
				{
					/* Declare the iteration variable(s) */
					const digit* e;

					_ARRAY_CONTAINS_ALL(a, vs, e);
					break;
				}
				case _INTEGER_TYPE:
				{
					/* Declare the iteration variable(s) */
					const integer* e;

					_ARRAY_CONTAINS_ALL(a, vs, e);
					break;
				}
				case _NATURAL_TYPE:
				{
					/* Declare the iteration variable(s) */
					const natural* e;

					_ARRAY_CONTAINS_ALL(a, vs, e);
					break;
				}
				case _REAL_TYPE:
				{
					/* Declare the iteration variable(s) */
					const real* e;

					_ARRAY_CONTAINS_ALL(a, vs, e);
					break;
				}
				case _TIME_TYPE:
				{
					/* Declare the iteration variable(s) */
					const Time* e;

					_ARRAY_CONTAINS_ALL(a, vs, e);
					break;
				}
				case _NUMBER_TYPE:
				{
					/* Declare the iteration variable(s) */
					const Number* e;

					_ARRAY_CONTAINS_ALL(a, vs, e);
					break;
				}
				case _OBJECT_TYPE:
				{
					/* Declare the iteration variable(s) */
					const Object* e;

					_ARRAY_CONTAINS_ALL(a, vs, e);
					break;
				}
				case _ARRAY_TYPE:
				{
					/* Declare the iteration variable(s) */
					const Array* e;

					_ARRAY_CONTAINS_ALL(a, vs, e);
					break;
				}
				default:
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
			}
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

natural Array_count(const void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		const Array* a = (Array*) collection;
		/* Declare the variable(s) */
		natural counter = 0;
		const void* v;
		/* Declare the iteration variable(s) */
		void* e = a->elements;

		/* Get the value */
		if (v = _VALUE_GET(a->element.type, type, value))
		{
			/* Return the number of occurrences of the value in the Array */
			while (e = array_find_element(e, a->length, a->element.type, a->element.size, v))
			{
				++counter;
				e = _ELEMENT_GET_NEXT(e, a->element.size);
			}
		}
		return counter;
	}
	return 0;
}

natural Array_count_value(const void* collection, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		const Array* a = (Array*) collection;
		/* Declare the variable(s) */
		natural counter = 0;
		/* Declare the iteration variable(s) */
		void* e = a->elements;

		/* Return the number of occurrences of the value in the Array */
		while (e = array_find_element(e, a->length, a->element.type, a->element.size, value))
		{
			++counter;
			e = _ELEMENT_GET_NEXT(e, a->element.size);
		}
		return counter;
	}
	return 0;
}

natural Array_count_all(const void* collection, const void* values)
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
			switch (a->element.type)
			{
				case _BOOLEAN_TYPE:
				{
					/* Declare the iteration variable(s) */
					const boolean* e;

					_ARRAY_COUNT_ALL(a, vs, e);
				}
				case _CHARACTER_TYPE:
				{
					/* Declare the iteration variable(s) */
					const character* e;

					_ARRAY_COUNT_ALL(a, vs, e);
				}
				case _DIGIT_TYPE:
				{
					/* Declare the iteration variable(s) */
					const digit* e;

					_ARRAY_COUNT_ALL(a, vs, e);
				}
				case _INTEGER_TYPE:
				{
					/* Declare the iteration variable(s) */
					const integer* e;

					_ARRAY_COUNT_ALL(a, vs, e);
				}
				case _NATURAL_TYPE:
				{
					/* Declare the iteration variable(s) */
					const natural* e;

					_ARRAY_COUNT_ALL(a, vs, e);
				}
				case _REAL_TYPE:
				{
					/* Declare the iteration variable(s) */
					const real* e;

					_ARRAY_COUNT_ALL(a, vs, e);
				}
				case _TIME_TYPE:
				{
					/* Declare the iteration variable(s) */
					const Time* e;

					_ARRAY_COUNT_ALL(a, vs, e);
				}
				case _NUMBER_TYPE:
				{
					/* Declare the iteration variable(s) */
					const Number* e;

					_ARRAY_COUNT_ALL(a, vs, e);
				}
				case _OBJECT_TYPE:
				{
					/* Declare the iteration variable(s) */
					const Object* e;

					_ARRAY_COUNT_ALL(a, vs, e);
				}
				case _ARRAY_TYPE:
				{
					/* Declare the iteration variable(s) */
					const Array* e;

					_ARRAY_COUNT_ALL(a, vs, e);
				}
				default:
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

boolean Array_remove(void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		Array* a = (Array*) collection;
		/* Declare the variable(s) */
		const void* v;
		Element e;

		/* Get the value */
		if (v = _VALUE_GET(a->element.type, type, value))
		{
			/* Find the element with the value in the Array */
			e = array_find(a->elements, a->length, a->element.type, a->element.size, v);
			/* Remove the element from the Array */
			if (e.pointer != NULL)
			{
				array_remove(a->length, a->element.type, a->element.size, e.index, e.pointer);
				--a->length;
				a->element.value = _ELEMENT_GET_PREVIOUS(a->element.value, a->element.size);
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

boolean Array_remove_value(void* collection, const void* value)
{
	_IF (_CHECK(collection, _ARRAY_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Array */
		Array* a = (Array*) collection;
		/* Find the element with the value in the Array */
		Element e = array_find(a->elements, a->length, a->element.type, a->element.size, value);

		/* Remove the element from the Array */
		if (e.pointer != NULL)
		{
			array_remove(a->length, a->element.type, a->element.size, e.index, e.pointer);
			--a->length;
			a->element.value = _ELEMENT_GET_PREVIOUS(a->element.value, a->element.size);
			return _TRUE;
		}
	}
	return _FALSE;
}

boolean Array_remove_all(void* collection, const void* values)
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
		void* e = it.next(&it);

		while (i < a->length)
		{
			if (vs->containsStructure(vs, &it.element))
			{
				isModified = _TRUE;
				array_remove(a->length, a->element.type, a->element.size, i, e);
				--a->length;
				a->element.value = _ELEMENT_GET_PREVIOUS(a->element.value, a->element.size);
			}
			else
			{
				++i;
				e = it.next(&it);
			}
		}
	}
	return isModified;
}

/**************************************************************************************************/

boolean Array_resize(void* collection, const natural size)
{
	_PRINT_DEBUG(_S("<resizeArray>"));
	_IF (_CHECK(collection, _ARRAY_NAME))
	{
		/* Get the Array */
		Array* a = (Array*) collection;
		/* Declare the elements of the Array */
		void* es;

		/* Release the surplus elements of the Array */
		if (a->length > size)
		{
			if (type_is_Basic(a->element.type))
			{
				/* Declare the iteration variable(s) */
				Iterator it = a->iterator(a);
				void* e;
				natural i = 0;

				while (e = it.next(&it))
				{
					if (i >= size)
					{
						_STRUCTURE_RELEASE(e);
					}
					++i;
				}
			}
			a->length = size;
			a->element.value = array_get(a->elements, a->element.type, a->element.size, size);
		}
		/* Resize */
		es = _ARRAY_RESIZE(a->elements, a->element.size, size);
		if (es != NULL)
		{
			a->size = size;
			a->elements = es;
			a->element.value = array_get(es, a->element.type, a->element.size, a->length);
			_PRINT_DEBUG(_S("</resizeArray>"));
			return _TRUE;
		}
		else if (size == 0)
		{
			a->size = size;
			a->elements = es;
			a->element.value = es;
			_PRINT_DEBUG(_S("</resizeArray>"));
			return _TRUE;
		}
		else
		{
			_PRINT_ERROR_ARRAY_REALLOCATION(a->element.type);
		}
	}
	_PRINT_DEBUG(_S("</resizeArray>"));
	return _FALSE;
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

Iterator Array_iterator(const void* iterable)
{
	_IF (_CHECK(iterable, _ARRAY_NAME))
	{
		/* Get the Array */
		const Array* a = (Array*) iterable;

		return array_iterator(a->elements, a->length, a->element.type, a->element.size);
	}
	return _ITERATOR_DEFAULT;
}

/**************************************************************************************************/

void* Array_Iterator_next(Iterator* iterator)
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

Comparable Array_create_Comparable(void)
{
	return Comparable_create(Array_release, Array_clone, Array_equals, Array_hash, Array_to_string, Array_compare_to);
}

integer Array_compare_to(const void* structure, const type type, const void* value)
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
			Iterator it2 = vs->iterator(vs);
			integer comparison;
			/* Get the minimum length */
			const natural length = _MIN(it1.length, it2.length);

			for (i = 0, it1.next(&it1), it2.next(&it2);
				i < length;
				++i, it1.next(&it1), it2.next(&it2))
			{
				comparison = Structures_compare_to(&it1.element, &it2.element);
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

void Array_release(void* structure)
{
	_PRINT_DEBUG(_S("<releaseArray>"));
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
	_PRINT_DEBUG(_S("</releaseArray>"));
}

/**************************************************************************************************/

void* Array_clone(const void* structure)
{
	_IF (_CHECK(structure, _ARRAY_NAME))
	{
		/* Get the Array */
		const Array* a = (Array*) structure;
		/* Construct the clone dynamically */
		Array* clone = Array_new(a->element.type, a->element.size, a->length);

		Array_add_Array(clone, a);
		return clone;
	}
	return NULL;
}

/**************************************************************************************************/

boolean Array_equals(const void* structure, const type type, const void* value)
{
	return Array_compare_to(structure, type, value) == 0;
}

/**************************************************************************************************/

integer Array_hash(const void* structure)
{
	integer code;

	if (structure != NULL)
	{
		/* Get the Array */
		const Array* a = (Array*) structure;
		/* Declare the iteration variable(s) */
		Iterator it = a->iterator(a);
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

boolean Array_to_string(const void* source, string target)
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
				isNotFull = append_to_string(e, a->element.type, target);
				while (isNotFull && (e = it.next(&it)))
				{
					isNotFull = string_append_to_string(_S(", "), target)
						&& append_to_string(e, a->element.type, target);
				}
			}
			isNotFull = isNotFull && string_append_to_string(_S(")"), target);
		}
		return isNotFull;
	}
	return _FALSE;
}

boolean Array_append_to_string(const void* source, string target)
{
	string buffer;

	Array_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
