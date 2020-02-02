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

#include "ceres/CommonArrays.h"


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

void* array_new(const natural size, const natural elementSize)
{
	if (elementSize > 0)
	{
		_PRINT_NEW;
		return _ARRAY_NEW(size, elementSize);
	}
	else
	{
		_PRINT_ERROR_INSTANTIATION();
	}
	return NULL;
}


/***************************************************************************************************
 * ELEMENT
 **************************************************************************************************/

Element Element_create(const natural index, void* pointer)
{
	Element element;

	element.index = index;
	element.pointer = pointer;
	return element;
}


/***************************************************************************************************
 * ARRAY
 **************************************************************************************************/

void* array_add(const type elementType, const natural elementSize, void* element, const void* value)
{
	switch (elementType)
	{
		case _BOOLEAN_TYPE:
		{
			*((boolean*) element) = *((boolean*) value);
			return (boolean*) element + 1;
		}
		case _CHARACTER_TYPE:
		{
			*((character*) element) = *((character*) value);
			return (character*) element + 1;
		}
		case _DIGIT_TYPE:
		{
			*((digit*) element) = *((digit*) value);
			return (digit*) element + 1;
		}
		case _INTEGER_TYPE:
		{
			*((integer*) element) = *((integer*) value);
			return (integer*) element + 1;
		}
		case _NATURAL_TYPE:
		{
			*((natural*) element) = *((natural*) value);
			return (natural*) element + 1;
		}
		case _REAL_TYPE:
		{
			*((real*) element) = *((real*) value);
			return (real*) element + 1;
		}
		case _TIME_TYPE:
		{
			*((Time*) element) = *((Time*) value);
			return (Time*) element + 1;
		}
		case _NUMBER_TYPE:
		{
			*((Number*) element) = *((Number*) value);
			return (Number*) element + 1;
		}
		case _OBJECT_TYPE:
		{
			*((Object*) element) = *((Object*) value);
			return (Object*) element + 1;
		}
		case _ARRAY_TYPE:
		{
			*((Array*) element) = *((Array*) value);
			return (Array*) element + 1;
		}
		default:
		{
			return _ARRAY_ADD(element, elementSize, value);
		}
	}
}

boolean array_copy(const void* source, const natural sourceSize, const natural elementSize, void* target, const natural targetSize)
{
	/* Get the minimum size */
	const natural size = _MIN(sourceSize, targetSize);

	_ARRAY_COPY(source, size, elementSize, target);
	return sourceSize <= targetSize;
}

/**************************************************************************************************/

Element array_find(const void* array, const natural length, const type elementType, const natural elementSize, const void* value)
{
	switch (elementType)
	{
		case _BOOLEAN_TYPE:
		{
			/* Get the value */
			const boolean v = *((boolean*) value);
			/* Declare the iteration variable(s) */
			boolean* e = (boolean*) array;

			_ARRAY_FIND(length, v, e);
			break;
		}
		case _CHARACTER_TYPE:
		{
			/* Get the value */
			const character v = *((character*) value);
			/* Declare the iteration variable(s) */
			character* e = (character*) array;

			_ARRAY_FIND(length, v, e);
			break;
		}
		case _DIGIT_TYPE:
		{
			/* Get the value */
			const digit v = *((digit*) value);
			/* Declare the iteration variable(s) */
			digit* e = (digit*) array;

			_ARRAY_FIND(length, v, e);
			break;
		}
		case _INTEGER_TYPE:
		{
			/* Get the value */
			const integer v = *((integer*) value);
			/* Declare the iteration variable(s) */
			integer* e = (integer*) array;

			_ARRAY_FIND(length, v, e);
			break;
		}
		case _NATURAL_TYPE:
		{
			/* Get the value */
			const natural v = *((natural*) value);
			/* Declare the iteration variable(s) */
			natural* e = (natural*) array;

			_ARRAY_FIND(length, v, e);
			break;
		}
		case _REAL_TYPE:
		{
			/* Get the value */
			const real v = *((real*) value);
			/* Declare the iteration variable(s) */
			real* e = (real*) array;

			_ARRAY_FIND(length, v, e);
			break;
		}
		case _TIME_TYPE:
		{
			/* Get the value */
			const Time* v = (Time*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Time* e;

			for (i = 0, e = (Time*) array; i < length; ++i, ++e)
			{
				if (Time_equals(v, _TIME_TYPE, e))
				{
					return Element_create(i, e);
				}
			}
			break;
		}
		case _NUMBER_TYPE:
		{
			/* Get the value */
			const Number* v = (Number*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Number* e;

			for (i = 0, e = (Number*) array; i < length; ++i, ++e)
			{
				if (v->equals(v, _NUMBER_TYPE, e))
				{
					return Element_create(i, e);
				}
			}
			break;
		}
		case _OBJECT_TYPE:
		{
			/* Get the value */
			const Object* v = (Object*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Object* e;

			for (i = 0, e = (Object*) array; i < length; ++i, ++e)
			{
				if (v->equals(v, e->structure.type, e->structure.value))
				{
					return Element_create(i, e);
				}
			}
			break;
		}
		case _ARRAY_TYPE:
		{
			/* Get the value */
			const Array* v = (Array*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Array* e;

			for (i = 0, e = (Array*) array; i < length; ++i, ++e)
			{
				if (v->equals(v, _ARRAY_TYPE, e))
				{
					return Element_create(i, e);
				}
			}
			break;
		}
		default:
		{
			/* Declare the iteration variable(s) */
			natural i;
			address e;

			for (i = 0, e = (address) array; i < length; ++i, e += elementSize)
			{
				if (_ELEMENT_EQUALS(value, e, elementSize))
				{
					return Element_create(i, e);
				}
			}
		}
	}
	return Element_create(length, NULL);
}

void* array_find_element(const void* array, const natural length, const type elementType, const natural elementSize, const void* value)
{
	switch (elementType)
	{
		case _BOOLEAN_TYPE:
		{
			/* Get the value */
			const boolean v = *((boolean*) value);
			/* Declare the iteration variable(s) */
			boolean* e = (boolean*) array;

			_ARRAY_FIND_ELEMENT(length, v, e);
			break;
		}
		case _CHARACTER_TYPE:
		{
			/* Get the value */
			const character v = *((character*) value);
			/* Declare the iteration variable(s) */
			character* e = (character*) array;

			_ARRAY_FIND_ELEMENT(length, v, e);
			break;
		}
		case _DIGIT_TYPE:
		{
			/* Get the value */
			const digit v = *((digit*) value);
			/* Declare the iteration variable(s) */
			digit* e = (digit*) array;

			_ARRAY_FIND_ELEMENT(length, v, e);
			break;
		}
		case _INTEGER_TYPE:
		{
			/* Get the value */
			const integer v = *((integer*) value);
			/* Declare the iteration variable(s) */
			integer* e = (integer*) array;

			_ARRAY_FIND_ELEMENT(length, v, e);
			break;
		}
		case _NATURAL_TYPE:
		{
			/* Get the value */
			const natural v = *((natural*) value);
			/* Declare the iteration variable(s) */
			natural* e = (natural*) array;

			_ARRAY_FIND_ELEMENT(length, v, e);
			break;
		}
		case _REAL_TYPE:
		{
			/* Get the value */
			const real v = *((real*) value);
			/* Declare the iteration variable(s) */
			real* e = (real*) array;

			_ARRAY_FIND_ELEMENT(length, v, e);
			break;
		}
		case _TIME_TYPE:
		{
			/* Get the value */
			const Time* v = (Time*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Time* e;

			for (i = 0, e = (Time*) array; i < length; ++i, ++e)
			{
				if (Time_equals(v, _TIME_TYPE, e))
				{
					return e;
				}
			}
			break;
		}
		case _NUMBER_TYPE:
		{
			/* Get the value */
			const Number* v = (Number*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Number* e;

			for (i = 0, e = (Number*) array; i < length; ++i, ++e)
			{
				if (v->equals(v, _NUMBER_TYPE, e))
				{
					return e;
				}
			}
			break;
		}
		case _OBJECT_TYPE:
		{
			/* Get the value */
			const Object* v = (Object*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Object* e;

			for (i = 0, e = (Object*) array; i < length; ++i, ++e)
			{
				if (v->equals(v, e->structure.type, e->structure.value))
				{
					return e;
				}
			}
			break;
		}
		case _ARRAY_TYPE:
		{
			/* Get the value */
			const Array* v = (Array*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Array* e;

			for (i = 0, e = (Array*) array; i < length; ++i, ++e)
			{
				if (v->equals(v, _ARRAY_TYPE, e))
				{
					return e;
				}
			}
			break;
		}
		default:
		{
			/* Declare the iteration variable(s) */
			natural i;
			address e;

			for (i = 0, e = (address) array; i < length; ++i, e += elementSize)
			{
				if (_ELEMENT_EQUALS(value, e, elementSize))
				{
					return e;
				}
			}
		}
	}
	return NULL;
}

natural array_find_index(const void* array, const natural length, const type elementType, const natural elementSize, const void* value)
{
	switch (elementType)
	{
		case _BOOLEAN_TYPE:
		{
			/* Get the value */
			const boolean v = *((boolean*) value);
			/* Declare the iteration variable(s) */
			boolean* e = (boolean*) array;

			_ARRAY_FIND_INDEX(length, v, e);
			break;
		}
		case _CHARACTER_TYPE:
		{
			/* Get the value */
			const character v = *((character*) value);
			/* Declare the iteration variable(s) */
			character* e = (character*) array;

			_ARRAY_FIND_INDEX(length, v, e);
			break;
		}
		case _DIGIT_TYPE:
		{
			/* Get the value */
			const digit v = *((digit*) value);
			/* Declare the iteration variable(s) */
			digit* e = (digit*) array;

			_ARRAY_FIND_INDEX(length, v, e);
			break;
		}
		case _INTEGER_TYPE:
		{
			/* Get the value */
			const integer v = *((integer*) value);
			/* Declare the iteration variable(s) */
			integer* e = (integer*) array;

			_ARRAY_FIND_INDEX(length, v, e);
			break;
		}
		case _NATURAL_TYPE:
		{
			/* Get the value */
			const natural v = *((natural*) value);
			/* Declare the iteration variable(s) */
			natural* e = (natural*) array;

			_ARRAY_FIND_INDEX(length, v, e);
			break;
		}
		case _REAL_TYPE:
		{
			/* Get the value */
			const real v = *((real*) value);
			/* Declare the iteration variable(s) */
			real* e = (real*) array;

			_ARRAY_FIND_INDEX(length, v, e);
			break;
		}
		case _TIME_TYPE:
		{
			/* Get the value */
			const Time* v = (Time*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Time* e;

			for (i = 0, e = (Time*) array; i < length; ++i, ++e)
			{
				if (Time_equals(v, _TIME_TYPE, e))
				{
					return i;
				}
			}
			break;
		}
		case _NUMBER_TYPE:
		{
			/* Get the value */
			const Number* v = (Number*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Number* e;

			for (i = 0, e = (Number*) array; i < length; ++i, ++e)
			{
				if (v->equals(v, _NUMBER_TYPE, e))
				{
					return i;
				}
			}
			break;
		}
		case _OBJECT_TYPE:
		{
			/* Get the value */
			const Object* v = (Object*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Object* e;

			for (i = 0, e = (Object*) array; i < length; ++i, ++e)
			{
				if (v->equals(v, e->structure.type, e->structure.value))
				{
					return i;
				}
			}
			break;
		}
		case _ARRAY_TYPE:
		{
			/* Get the value */
			const Array* v = (Array*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Array* e;

			for (i = 0, e = (Array*) array; i < length; ++i, ++e)
			{
				if (v->equals(v, _ARRAY_TYPE, e))
				{
					return i;
				}
			}
			break;
		}
		default:
		{
			/* Declare the iteration variable(s) */
			natural i;
			address e;

			for (i = 0, e = (address) array; i < length; ++i, e += elementSize)
			{
				if (_ELEMENT_EQUALS(value, e, elementSize))
				{
					return i;
				}
			}
		}
	}
	return length;
}

/**************************************************************************************************/

Element array_find_with_comparator(const void* array, const natural length, const type elementType, const natural elementSize, const void* value, const Comparable* comparator)
{
	/* Declare the iteration variable(s) */
	natural i;
	address e;

	for (i = 0, e = (address) array; i < length; ++i, e += elementSize)
	{
		if (comparator->equals(value, elementType, e))
		{
			return Element_create(i, e);
		}
	}
	return Element_create(length, NULL);
}

void* array_find_element_with_comparator(const void* array, const natural length, const type elementType, const natural elementSize, const void* value, const Comparable* comparator)
{
	/* Declare the iteration variable(s) */
	natural i;
	address e;

	for (i = 0, e = (address) array; i < length; ++i, e += elementSize)
	{
		if (comparator->equals(value, elementType, e))
		{
			return e;
		}
	}
	return NULL;
}

natural array_find_index_with_comparator(const void* array, const natural length, const type elementType, const natural elementSize, const void* value, const Comparable* comparator)
{
	/* Declare the iteration variable(s) */
	natural i;
	address e;

	for (i = 0, e = (address) array; i < length; ++i, e += elementSize)
	{
		if (comparator->equals(value, elementType, e))
		{
			return i;
		}
	}
	return length;
}

/**************************************************************************************************/

void* array_get(const void* array, const type elementType, const natural elementSize, const natural index)
{
	switch (elementType)
	{
		case _BOOLEAN_TYPE:
		{
			return &((boolean*) array)[index];
		}
		case _CHARACTER_TYPE:
		{
			return &((character*) array)[index];
		}
		case _DIGIT_TYPE:
		{
			return &((digit*) array)[index];
		}
		case _INTEGER_TYPE:
		{
			return &((integer*) array)[index];
		}
		case _NATURAL_TYPE:
		{
			return &((natural*) array)[index];
		}
		case _REAL_TYPE:
		{
			return &((real*) array)[index];
		}
		case _TIME_TYPE:
		{
			return &((Time*) array)[index];
		}
		case _NUMBER_TYPE:
		{
			return &((Number*) array)[index];
		}
		case _OBJECT_TYPE:
		{
			return &((Object*) array)[index];
		}
		case _ARRAY_TYPE:
		{
			return &((Array*) array)[index];
		}
		default:
		{
			return _ARRAY_GET(array, elementSize, index);
		}
	}
}

boolean array_insert(const natural length, const natural elementSize, const natural index, void* element, const void* value)
{
	if (index < length)
	{
		_ARRAY_INSERT(length, elementSize, index, element);
	}
	_ELEMENT_SET(element, elementSize, value);
	return _TRUE;
}

void* array_remove(const natural length, const type elementType, const natural elementSize, const natural index, void* element)
{
	switch (elementType)
	{
		case _NUMBER_TYPE:
		case _OBJECT_TYPE:
		case _ARRAY_TYPE:
		{
			_STRUCTURE_RELEASE(element);
		}
	}
	return _ARRAY_REMOVE(length, elementSize, index, element);
}

boolean array_set(void* array, const type elementType, const natural elementSize, const natural index, const void* value)
{
	switch (elementType)
	{
		case _BOOLEAN_TYPE:
		{
			((boolean*) array)[index] = *((boolean*) value);
			break;
		}
		case _CHARACTER_TYPE:
		{
			((character*) array)[index] = *((character*) value);
			break;
		}
		case _DIGIT_TYPE:
		{
			((digit*) array)[index] = *((digit*) value);
			break;
		}
		case _INTEGER_TYPE:
		{
			((integer*) array)[index] = *((integer*) value);
			break;
		}
		case _NATURAL_TYPE:
		{
			((natural*) array)[index] = *((natural*) value);
			break;
		}
		case _REAL_TYPE:
		{
			((real*) array)[index] = *((real*) value);
			break;
		}
		case _TIME_TYPE:
		{
			((Time*) array)[index] = *((Time*) value);
			break;
		}
		case _NUMBER_TYPE:
		{
			((Number*) array)[index] = *((Number*) value);
			break;
		}
		case _OBJECT_TYPE:
		{
			((Object*) array)[index] = *((Object*) value);
			break;
		}
		case _ARRAY_TYPE:
		{
			((Array*) array)[index] = *((Array*) value);
			break;
		}
		default:
		{
			_ARRAY_SET(array, elementSize, index, value);
		}
	}
	return _TRUE;
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

Iterator array_iterator(void* array, const natural length, const type elementType, const natural elementSize)
{
	switch (elementType)
	{
		case _BOOLEAN_TYPE:
		case _BOOLEANS_TYPE:
			return Iterator_create(length, elementType, elementSize, array, boolean_Iterator_next);
		case _CHARACTER_TYPE:
		case _CHARACTERS_TYPE:
			return Iterator_create(length, elementType, elementSize, array, char_Iterator_next);
		case _DIGIT_TYPE:
		case _DIGITS_TYPE:
			return Iterator_create(length, elementType, elementSize, array, digit_Iterator_next);
		case _INTEGER_TYPE:
		case _INTEGERS_TYPE:
			return Iterator_create(length, elementType, elementSize, array, integer_Iterator_next);
		case _NATURAL_TYPE:
		case _NATURALS_TYPE:
			return Iterator_create(length, elementType, elementSize, array, natural_Iterator_next);
		case _REAL_TYPE:
		case _REALS_TYPE:
			return Iterator_create(length, elementType, elementSize, array, real_Iterator_next);
		case _TIME_TYPE:
		case _TIMES_TYPE:
			return Iterator_create(length, elementType, elementSize, array, Time_Iterator_next);
		case _NUMBER_TYPE:
		case _NUMBERS_TYPE:
			return Iterator_create(length, elementType, elementSize, array, Number_Iterator_next);
		case _OBJECT_TYPE:
		case _OBJECTS_TYPE:
			return Iterator_create(length, elementType, elementSize, array, Object_Iterator_next);
		case _ARRAY_TYPE:
		case _ARRAYS_TYPE:
			return Iterator_create(length, elementType, elementSize, array, Array_Iterator_next);
		case _STRING_TYPE:
			return Iterator_create(length, elementType, elementSize, array, string_Iterator_next);
		default:
			return Iterator_create(length, elementType, elementSize, array, Iterator_next);
	}
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

boolean array_to_string(const Structure* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		switch (source->type)
		{
			case _BOOLEANS_TYPE:
				_ARRAY_TO_STRING(source->value, source->size, boolean, boolean_append_to_string, target);
			case _CHARACTERS_TYPE:
			case _STRING_TYPE:
				return chars_to_string(source->value, source->size, target);
			case _DIGITS_TYPE:
				_ARRAY_TO_STRING(source->value, source->size, digit, digit_append_to_string, target);
			case _INTEGERS_TYPE:
				_ARRAY_TO_STRING(source->value, source->size, integer, integer_append_to_string, target);
			case _NATURALS_TYPE:
				_ARRAY_TO_STRING(source->value, source->size, natural, natural_append_to_string, target);
			case _REALS_TYPE:
				_ARRAY_TO_STRING(source->value, source->size, real, real_append_to_string, target);
			case _TIMES_TYPE:
				_ARRAY_TO_STRING(source->value, source->size, Time, Time_append_to_string, target);
			case _NUMBERS_TYPE:
				_ARRAY_TO_STRING(source->value, source->size, Number, Number_append_to_string, target);
			case _OBJECTS_TYPE:
				_ARRAY_TO_STRING(source->value, source->size, Object, Object_append_to_string, target);
			case _ARRAYS_TYPE:
				_ARRAY_TO_STRING(source->value, source->size, Array, Array_append_to_string, target);
			default:
				_PRINT_ERROR_NOT_ELEMENTARY_TYPE(source->type);
		}
	}
	return _FALSE;
}

boolean append_array_to_string(const Structure* source, string target)
{
	string buffer;

	array_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
