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

#include "ceres/CommonFunctions.h"


/***************************************************************************************************
 * FORMAT
 **************************************************************************************************/

boolean format_specifier_to_string(const character* source, va_list* args, string target)
{
	_IF(_SOURCE_TARGET_CHECK(source, target))
	{
		_IF(_CHECK(args, _S("list of variable arguments")))
		{
			switch (*source)
			{
				case _C('A'):
				{
					const Array* value = va_arg(*args, Array*);

					value->toString(value, target);
					return _TRUE;
				}
				case _C('b'):
				{
#if _32_BITS
					const boolean value = (boolean) va_arg(*args, integer);
#else
					const boolean value = va_arg(*args, boolean);
#endif

					boolean_to_string(&value, target);
					return _TRUE;
				}
				case _C('B'):
				{
					const Basic* value = va_arg(*args, Basic*);

					value->toString(value, target);
					return _TRUE;
				}
				case _C('c'):
				{
#if _32_BITS
					const character value = (character) va_arg(*args, integer);
#else
					const character value = va_arg(*args, character);
#endif

					char_to_string(&value, target);
					return _TRUE;
				}
				case _C('d'):
				{
#if _32_BITS
					const digit value = (digit) va_arg(*args, integer);
#else
					const digit value = va_arg(*args, digit);
#endif

					digit_to_string(&value, target);
					return _TRUE;
				}
				case _C('i'):
				{
					const integer value = va_arg(*args, integer);

					integer_to_string(&value, target);
					return _TRUE;
				}
				case _C('n'):
				{
#if _32_BITS
					const natural value = (natural) va_arg(*args, integer);
#else
					const natural value = va_arg(*args, natural);
#endif

					natural_to_string(&value, target);
					return _TRUE;
				}
				case _C('N'):
				{
					const Number* value = va_arg(*args, Number*);

					value->toString(value, target);
					return _TRUE;
				}
				case _C('O'):
				{
					const Object* value = va_arg(*args, Object*);

					value->toString(value, target);
					return _TRUE;
				}
				case _C('p'):
				case _C('P'):
				{
					const void* value = va_arg(*args, void*);

					pointer_to_string(value, target);
					return _TRUE;
				}
				case _C('r'):
				{
					const real value = va_arg(*args, real);

					real_to_string(&value, target);
					return _TRUE;
				}
				case _C('s'):
				{
					const character* value = va_arg(*args, character*);

					string_to_string(value, target);
					return _TRUE;
				}
				case _C('S'):
				{
					const Structure* value = va_arg(*args, Structure*);

					to_string(value->value, value->type, target);
					return _TRUE;
				}
				case _C('t'):
				{
#if _32_BITS
					const type value = (type) va_arg(*args, integer);
#else
					const type value = va_arg(*args, type);
#endif

					type_to_string(&value, target);
					return _TRUE;
				}
				case _C('T'):
				case _C('X'):
				{
					const Time* value = va_arg(*args, Time*);

					Time_to_string(value, target);
					return _TRUE;
				}
				case _FORMAT_SPECIFIER:
				{
					char_to_string(source, target);
					return _TRUE;
				}
				default:
				{
					_PRINT_ERROR_FORMAT(*source);
					string_reset(target);
				}
			}
		}
	}
	return _FALSE;
}

boolean format_to_chars(const character* source, va_list* args, character* target, const natural targetSize)
{
	_IF(_SOURCE_TARGET_ARRAY_CHECK(source, target, targetSize))
	{
		/* Declare the iteration variable(s) */
		natural i;
		const character* s;
		character* t;
		string buffer;
		natural length;
		/* Get the maximum length */
		const natural max = targetSize - 1;

		for (i = 0, s = source, t = target; i < max && *s; ++s)
		{
			if (*s == _FORMAT_SPECIFIER)
			{
				++s;
				if (*s)
				{
					if (format_specifier_to_string(s, args, buffer))
					{
						length = string_length(buffer);
						if (chars_to_chars(buffer, _STRING_SIZE, t, targetSize - i))
						{
							i += length;
							t += length;
						}
						else
						{
							/* Check for truncation */
							if (length == max - i && *(++s))
							{
								_PRINT_WARNING_TRUNCATION(_CHARACTERS_NAME);
							}
							return _FALSE;
						}
					}
					else
					{
						/* End the string */
						*t = _STRING_END;
						/* Check for truncation */
						if (*(++s))
						{
							_PRINT_WARNING_TRUNCATION(_CHARACTERS_NAME);
						}
						return _FALSE;
					}
				}
			}
			else
			{
				*t = *s;
				++i;
				++t;
			}
		}
		/* End the string */
		*t = _STRING_END;
		/* Check for truncation */
		_CHECK_TRUNCATION(i, max, s, _CHARACTERS_NAME);
	}
	return _FALSE;
}

boolean format_to_file(const character* source, va_list* args, FILE* target, const natural targetSize)
{
	_IF(_SOURCE_TARGET_ARRAY_CHECK(source, target, targetSize))
	{
		/* Declare the iteration variable(s) */
		natural i;
		const character* s;
		string buffer;
		natural length;
		/* Get the maximum length */
		const natural max = targetSize - 1;

		for (i = 0, s = source; i < max && *s; ++s)
		{
			if (*s == _FORMAT_SPECIFIER)
			{
				++s;
				if (*s)
				{
					if (format_specifier_to_string(s, args, buffer))
					{
						length = string_length(buffer);
						/* Check for truncation */
						if (length > max - i)
						{
							_PRINT_WARNING_TRUNCATION(_FILE_NAME);
							/* End the string */
							buffer[max - i] = _STRING_END;
							string_to_stream(buffer, target);
							return _FALSE;
						}
						else
						{
							string_to_stream(buffer, target);
							i += length;
						}
					}
					else
					{
						/* Check for truncation */
						if (*(++s))
						{
							_PRINT_WARNING_TRUNCATION(_FILE_NAME);
						}
						return _FALSE;
					}
				}
			}
			else
			{
				char_to_stream(*s, target);
				++i;
			}
		}
		/* Check for truncation */
		_CHECK_TRUNCATION(i, max, s, _FILE_NAME);
	}
	return _FALSE;
}


/***************************************************************************************************
 * POINTER
 **************************************************************************************************/

boolean pointer_to_string(const void* source, string target)
{
	_IF(_CHECK(target, _STRING_NAME))
	{
		if (source == NULL)
		{
			return string_to_string(_NULL_STRING, target);
		}
		else
		{
			_SPRINTF(target, _STRING_SIZE, _S("%p"), source);
			return _TRUE;
		}
	}
	return _FALSE;
}


/***************************************************************************************************
 * TYPE
 **************************************************************************************************/

natural type_get_size(const type type)
{
	switch (type)
	{
		/* Primitive types */
		case _BOOLEAN_TYPE:
			return BOOLEAN_SIZE;
		case _CHARACTER_TYPE:
			return CHARACTER_SIZE;
		case _DIGIT_TYPE:
			return DIGIT_SIZE;
		case _INTEGER_TYPE:
			return INTEGER_SIZE;
		case _NATURAL_TYPE:
			return NATURAL_SIZE;
		case _REAL_TYPE:
			return REAL_SIZE;
		case _TIME_TYPE:
			return TIME_SIZE;
		/* Structure type */
		case _STRUCTURE_TYPE:
			return STRUCTURE_SIZE;
		/* Basic types */
		case _NUMBER_TYPE:
			return NUMBER_SIZE;
		case _OBJECT_TYPE:
			return OBJECT_SIZE;
		case _ITERATOR_TYPE:
			return ITERATOR_SIZE;
		case _ARRAY_TYPE:
			return ARRAY_SIZE;
		/* Array types */
		case _STRING_TYPE:
			return STRING_SIZE;
	}
	return 0;
}

/**************************************************************************************************/

boolean type_is_dynamic(const type type)
{
	switch (type)
	{
		/* Array types (except string) */
		case _BOOLEANS_TYPE:
		case _CHARACTERS_TYPE:
		case _DIGITS_TYPE:
		case _INTEGERS_TYPE:
		case _NATURALS_TYPE:
		case _REALS_TYPE:
		case _TIMES_TYPE:
		case _STRUCTURES_TYPE:
		case _NUMBERS_TYPE:
		case _OBJECTS_TYPE:
		case _ARRAYS_TYPE:
			return _TRUE;
	}
	return _FALSE;
}

boolean type_is_array(const type type)
{
	switch (type)
	{
		/* Array types */
		case _BOOLEANS_TYPE:
		case _CHARACTERS_TYPE:
		case _DIGITS_TYPE:
		case _INTEGERS_TYPE:
		case _NATURALS_TYPE:
		case _REALS_TYPE:
		case _STRING_TYPE:
		case _TIMES_TYPE:
		case _STRUCTURES_TYPE:
		case _NUMBERS_TYPE:
		case _OBJECTS_TYPE:
		case _ARRAYS_TYPE:
			return _TRUE;
	}
	return _FALSE;
}

boolean type_is_numeric(const type type)
{
	switch (type)
	{
		/* Numeric types */
		case _DIGIT_TYPE:
		case _INTEGER_TYPE:
		case _NATURAL_TYPE:
		case _REAL_TYPE:
		case _NUMBER_TYPE:
			return _TRUE;
	}
	return _FALSE;
}

boolean type_is_Basic(const type type)
{
	switch (type)
	{
		/* Basic types */
		case _BASIC_TYPE:
		case _COMPARABLE_TYPE:
		case _NUMBER_TYPE:
		case _OBJECT_TYPE:
		case _ITERATOR_TYPE:
		case _ITERABLE_TYPE:
		case _COLLECTION_TYPE:
		case _LIST_TYPE:
		case _ARRAY_TYPE:
			return _TRUE;
	}
	return _FALSE;
}

boolean type_is_Comparable(const type type)
{
	switch (type)
	{
		/* Comparable types */
		case _COMPARABLE_TYPE:
		case _NUMBER_TYPE:
		case _OBJECT_TYPE:
		case _ITERATOR_TYPE:
		case _ITERABLE_TYPE:
		case _COLLECTION_TYPE:
		case _LIST_TYPE:
		case _ARRAY_TYPE:
			return _TRUE;
	}
	return _FALSE;
}

boolean type_is_Iterable(const type type)
{
	switch (type)
	{
		/* Iterable types */
		case _ITERABLE_TYPE:
		case _COLLECTION_TYPE:
		case _LIST_TYPE:
		case _ARRAY_TYPE:
			return _TRUE;
	}
	return _FALSE;
}

/**************************************************************************************************/

boolean type_to_string(const void* source, string target)
{
	_IF(_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the type */
		const type* t = (type*) source;

		switch (*t)
		{
			/* Primitive types */
			case _BOOLEAN_TYPE:
				return string_to_string(_BOOLEAN_NAME, target);
			case _CHARACTER_TYPE:
				return string_to_string(_CHARACTER_NAME, target);
			case _DIGIT_TYPE:
				return string_to_string(_DIGIT_NAME, target);
			case _INTEGER_TYPE:
				return string_to_string(_INTEGER_NAME, target);
			case _NATURAL_TYPE:
				return string_to_string(_NATURAL_NAME, target);
			case _REAL_TYPE:
				return string_to_string(_REAL_NAME, target);
			case _TIME_TYPE:
				return string_to_string(_TIME_NAME, target);
			/* Structure type */
			case _STRUCTURE_TYPE:
				return string_to_string(_STRUCTURE_NAME, target);
			/* Basic types */
			case _BASIC_TYPE:
				return string_to_string(_BASIC_NAME, target);
			case _COMPARABLE_TYPE:
				return string_to_string(_COMPARABLE_NAME, target);
			case _NUMBER_TYPE:
				return string_to_string(_NUMBER_NAME, target);
			case _OBJECT_TYPE:
				return string_to_string(_OBJECT_NAME, target);
			case _ITERATOR_TYPE:
				return string_to_string(_ITERATOR_NAME, target);
			case _ITERABLE_TYPE:
				return string_to_string(_ITERABLE_NAME, target);
			case _COLLECTION_TYPE:
				return string_to_string(_COLLECTION_NAME, target);
			case _LIST_TYPE:
				return string_to_string(_LIST_NAME, target);
			case _ARRAY_TYPE:
				return string_to_string(_ARRAY_NAME, target);
			/* Array types */
			case _BOOLEANS_TYPE:
				return string_to_string(_BOOLEANS_NAME, target);
			case _CHARACTERS_TYPE:
				return string_to_string(_CHARACTERS_NAME, target);
			case _DIGITS_TYPE:
				return string_to_string(_DIGITS_NAME, target);
			case _INTEGERS_TYPE:
				return string_to_string(_INTEGERS_NAME, target);
			case _NATURALS_TYPE:
				return string_to_string(_NATURALS_NAME, target);
			case _REALS_TYPE:
				return string_to_string(_REALS_NAME, target);
			case _STRING_TYPE:
				return string_to_string(_STRING_NAME, target);
			case _TIMES_TYPE:
				return string_to_string(_TIMES_NAME, target);
			case _STRUCTURES_TYPE:
				return string_to_string(_STRUCTURES_NAME, target);
			case _NUMBERS_TYPE:
				return string_to_string(_NUMBERS_NAME, target);
			case _OBJECTS_TYPE:
				return string_to_string(_OBJECTS_NAME, target);
			case _ARRAYS_TYPE:
				return string_to_string(_ARRAYS_NAME, target);
			/* Unknown type */
			default:
				_UNKNOWN_TO_STRING(target);
		}
	}
	return _FALSE;
}

boolean type_append_to_string(const void* source, string target)
{
	string buffer;

	type_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}


/***************************************************************************************************
 * STRUCTURE
 **************************************************************************************************/

Structure Structure_create(const type type, void* value)
{
	Structure s;

	s.core = Core_create(type_is_dynamic(type), _FALSE, type_is_Basic(type), type_is_Comparable(type));
	s.type = type;
	s.size = type_get_size(type);
	s.value = value;
	return s;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable Comparable_create(function_release functionRelease, function_clone functionClone, function_equals functionEquals, function_hash functionHash, function_to_string functionToString, function_compare_to functionCompareTo)
{
	Comparable c;

	c.core = Core_create(_FALSE, _FALSE, _TRUE, _TRUE);
	c.release = functionRelease;
	c.clone = functionClone;
	c.equals = functionEquals;
	c.hash = functionHash;
	c.toString = functionToString;
	c.compareTo = functionCompareTo;
	return c;
}

integer compare_to(const type firstType, const void* firstValue, const type secondType, const void* secondValue)
{
	_IF(_CHECKS(firstType, firstValue, secondType, secondValue))
	{
		if (firstValue == secondValue)
		{
			return 0;
		}
		else
		{
			switch (firstType)
			{
				/* Primitive types */
				case _BOOLEAN_TYPE:
					return boolean_compare_to(firstValue, secondType, secondValue);
				case _CHARACTER_TYPE:
					return char_compare_to(firstValue, secondType, secondValue);
				case _DIGIT_TYPE:
					return digit_compare_to(firstValue, secondType, secondValue);
				case _INTEGER_TYPE:
					return integer_compare_to(firstValue, secondType, secondValue);
				case _NATURAL_TYPE:
					return natural_compare_to(firstValue, secondType, secondValue);
				case _REAL_TYPE:
					return real_compare_to(firstValue, secondType, secondValue);
				case _TIME_TYPE:
					return Time_compare_to(firstValue, secondType, secondValue);
				/* Structure type */
				case _STRUCTURE_TYPE:
					return Structure_compare_to(firstValue, secondType, secondValue);
				/* Comparable types */
				case _COMPARABLE_TYPE:
				case _NUMBER_TYPE:
				case _OBJECT_TYPE:
				case _ITERABLE_TYPE:
				case _COLLECTION_TYPE:
				case _LIST_TYPE:
				case _ARRAY_TYPE:
					return Comparable_compare_to(firstValue, secondType, secondValue);
				/* Array types */
				case _STRING_TYPE:
					return string_compare_to(firstValue, secondType, secondValue);
				/* Unknown type */
				default:
					_PRINT_WARNING_NO_FUNCTION(_S("compare_to"), firstType);
			}
		}
	}
	return _NOT_COMPARABLE;
}

integer Structure_compare_to(const void* structure, const type type, const void* value)
{
	_IF(_CHECKS(_STRUCTURE_TYPE, structure, type, value))
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the Structure */
			const Structure* s = (Structure*) structure;

			return compare_to(s->type, s->value, type, value);
		}
	}
	return _NOT_COMPARABLE;
}

integer Comparable_compare_to(const void* structure, const type type, const void* value)
{
	_IF(_CHECKS(_COMPARABLE_TYPE, structure, type, value))
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the Comparable structure */
			const Comparable* c = (Comparable*) structure;

			if (c->compareTo != NULL)
			{
				return c->compareTo(c, type, value);
			}
		}
	}
	return _NOT_COMPARABLE;
}

function_compare_to get_function_compare_to(const type type)
{
	switch (type)
	{
		/* Primitive types */
		case _BOOLEAN_TYPE:
			return (function_compare_to) boolean_compare_to;
		case _CHARACTER_TYPE:
			return (function_compare_to) char_compare_to;
		case _DIGIT_TYPE:
			return (function_compare_to) digit_compare_to;
		case _INTEGER_TYPE:
			return (function_compare_to) integer_compare_to;
		case _NATURAL_TYPE:
			return (function_compare_to) natural_compare_to;
		case _REAL_TYPE:
			return (function_compare_to) real_compare_to;
		case _TIME_TYPE:
			return (function_compare_to) Time_compare_to;
		/* Structure type */
		case _STRUCTURE_TYPE:
			return (function_compare_to) Structure_compare_to;
		/* Comparable types */
		case _COMPARABLE_TYPE:
		case _NUMBER_TYPE:
		case _OBJECT_TYPE:
		case _ITERABLE_TYPE:
		case _COLLECTION_TYPE:
		case _LIST_TYPE:
		case _ARRAY_TYPE:
			return (function_compare_to) Comparable_compare_to;
		/* Array types */
		case _STRING_TYPE:
			return (function_compare_to) string_compare_to;
		/* Unknown type */
		default:
			_PRINT_WARNING_NO_FUNCTION(_S("compare_to"), type);
	}
	return NULL;
}

integer Structures_compare_to(const Structure* first, const Structure* second)
{
	if (_STRUCTURE_CHECKS(first, second))
	{
		if (first->value == second->value)
		{
			return _COMPARE_TO(first->size, second->size);
		}
		else
		{
			switch (first->type)
			{
				/* Primitive types */
				case _BOOLEAN_TYPE:
					return boolean_compare_to(first->value, second->type, second->value);
				case _CHARACTER_TYPE:
					return char_compare_to(first->value, second->type, second->value);
				case _DIGIT_TYPE:
					return digit_compare_to(first->value, second->type, second->value);
				case _INTEGER_TYPE:
					return integer_compare_to(first->value, second->type, second->value);
				case _NATURAL_TYPE:
					return natural_compare_to(first->value, second->type, second->value);
				case _REAL_TYPE:
					return real_compare_to(first->value, second->type, second->value);
				case _TIME_TYPE:
					return Time_compare_to(first->value, second->type, second->value);
				/* Structure type */
				case _STRUCTURE_TYPE:
					return Structure_compare_to(first->value, second->type, second->value);
				/* Comparable types */
				case _COMPARABLE_TYPE:
				case _NUMBER_TYPE:
				case _OBJECT_TYPE:
				case _ITERABLE_TYPE:
				case _COLLECTION_TYPE:
				case _LIST_TYPE:
				case _ARRAY_TYPE:
					return Comparable_compare_to(first->value, second->type, second->value);
				/* Array types */
				case _STRING_TYPE:
					return string_compare_to(first->value, second->type, second->value);
				/* Structure types */
				default:
				{
					if (first->type == second->type)
					{
						/* Get the minimum size */
						const natural size = _MIN(first->size, second->size);
						/* Get the comparison */
						const integer comparison = _ELEMENT_COMPARE_TO(first->value, second->value, size);

						if (comparison == 0)
						{
							return _COMPARE_TO(first->size, second->size);
						}
						else
						{
							return comparison;
						}
					}
					else
					{
						_PRINT_WARNING_COMPARISON(first->type, second->type);
					}
				}
			}
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

Basic Basic_create(function_release functionRelease, function_clone functionClone, function_equals functionEquals, function_hash functionHash, function_to_string functionToString)
{
	Basic b;

	b.core = Core_create(_FALSE, _FALSE, _TRUE, _FALSE);
	b.release = functionRelease;
	b.clone = functionClone;
	b.equals = functionEquals;
	b.hash = functionHash;
	b.toString = functionToString;
	return b;
}

/**************************************************************************************************/

void release(Structure* structure)
{
	if (type_is_Basic(structure->type))
	{
		_STRUCTURE_RELEASE(structure->value);
	}
	else if (structure->core.isDynamic)
	{
		_FREE(structure->value);
	}
}

/**************************************************************************************************/

boolean equals(const type firstType, const void* firstValue, const type secondType, const void* secondValue)
{
	_IF(_CHECKS(firstType, firstValue, secondType, secondValue))
	{
		if (firstValue == secondValue)
		{
			return _TRUE;
		}
		else
		{
			switch (firstType)
			{
				/* Primitive types */
				case _BOOLEAN_TYPE:
					return boolean_equals(firstValue, secondType, secondValue);
				case _CHARACTER_TYPE:
					return char_equals(firstValue, secondType, secondValue);
				case _DIGIT_TYPE:
					return digit_equals(firstValue, secondType, secondValue);
				case _INTEGER_TYPE:
					return integer_equals(firstValue, secondType, secondValue);
				case _NATURAL_TYPE:
					return natural_equals(firstValue, secondType, secondValue);
				case _REAL_TYPE:
					return real_equals(firstValue, secondType, secondValue);
				case _TIME_TYPE:
					return Time_equals(firstValue, secondType, secondValue);
				/* Structure type */
				case _STRUCTURE_TYPE:
					return Structure_equals(firstValue, secondType, secondValue);
				/* Basic types */
				case _BASIC_TYPE:
				case _COMPARABLE_TYPE:
				case _NUMBER_TYPE:
				case _OBJECT_TYPE:
				case _ITERATOR_TYPE:
				case _ITERABLE_TYPE:
				case _COLLECTION_TYPE:
				case _LIST_TYPE:
				case _ARRAY_TYPE:
					return Basic_equals(firstValue, secondType, secondValue);
				/* Array types */
				case _STRING_TYPE:
					return string_equals(firstValue, secondType, secondValue);
				/* Unknown type */
				default:
					_PRINT_WARNING_NO_FUNCTION(_S("equals"), firstType);
			}
		}
	}
	return _FALSE;
}

boolean Structure_equals(const void* structure, const type type, const void* value)
{
	_IF(_CHECKS(_STRUCTURE_TYPE, structure, type, value))
	{
		if (structure == value)
		{
			return _TRUE;
		}
		else
		{
			/* Get the Structure */
			const Structure* s = (Structure*) structure;

			return equals(s->type, s->value, type, value);
		}
	}
	return _FALSE;
}

boolean Basic_equals(const void* structure, const type type, const void* value)
{
	_IF(_CHECKS(_BASIC_TYPE, structure, type, value))
	{
		if (structure == value)
		{
			return _TRUE;
		}
		else
		{
			/* Get the Equalable structure */
			const Equalable* e = (Equalable*) structure;

			if (e->equals != NULL)
			{
				return e->equals(e, type, value);
			}
		}
	}
	return _FALSE;
}

function_equals get_function_equals(const type type)
{
	switch (type)
	{
		/* Primitive types */
		case _BOOLEAN_TYPE:
			return (function_equals) boolean_equals;
		case _CHARACTER_TYPE:
			return (function_equals) char_equals;
		case _DIGIT_TYPE:
			return (function_equals) digit_equals;
		case _INTEGER_TYPE:
			return (function_equals) integer_equals;
		case _NATURAL_TYPE:
			return (function_equals) natural_equals;
		case _REAL_TYPE:
			return (function_equals) real_equals;
		case _TIME_TYPE:
			return (function_equals) Time_equals;
		/* Structure type */
		case _STRUCTURE_TYPE:
			return (function_equals) Structure_equals;
		/* Basic types */
		case _BASIC_TYPE:
		case _COMPARABLE_TYPE:
		case _NUMBER_TYPE:
		case _OBJECT_TYPE:
		case _ITERATOR_TYPE:
		case _ITERABLE_TYPE:
		case _COLLECTION_TYPE:
		case _LIST_TYPE:
		case _ARRAY_TYPE:
			return (function_equals) Basic_equals;
		/* Array types */
		case _STRING_TYPE:
			return (function_equals) string_equals;
		/* Unknown type */
		default:
			_PRINT_WARNING_NO_FUNCTION(_S("equals"), type);
	}
	return NULL;
}

boolean Structures_equals(const Structure* first, const Structure* second)
{
	if (_STRUCTURE_CHECKS(first, second))
	{
		if (first->value == second->value)
		{
			return first->size == second->size;
		}
		else
		{
			switch (first->type)
			{
				/* Primitive types */
				case _BOOLEAN_TYPE:
					return boolean_equals(first->value, second->type, second->value);
				case _CHARACTER_TYPE:
					return char_equals(first->value, second->type, second->value);
				case _DIGIT_TYPE:
					return digit_equals(first->value, second->type, second->value);
				case _INTEGER_TYPE:
					return integer_equals(first->value, second->type, second->value);
				case _NATURAL_TYPE:
					return natural_equals(first->value, second->type, second->value);
				case _REAL_TYPE:
					return real_equals(first->value, second->type, second->value);
				case _TIME_TYPE:
					return Time_equals(first->value, second->type, second->value);
				/* Structure type */
				case _STRUCTURE_TYPE:
					return Structure_equals(first->value, second->type, second->value);
				/* Basic types */
				case _BASIC_TYPE:
				case _COMPARABLE_TYPE:
				case _NUMBER_TYPE:
				case _OBJECT_TYPE:
				case _ITERATOR_TYPE:
				case _ITERABLE_TYPE:
				case _COLLECTION_TYPE:
				case _LIST_TYPE:
				case _ARRAY_TYPE:
					return Basic_equals(first->value, second->type, second->value);
				/* Array types */
				case _STRING_TYPE:
					return string_equals(first->value, second->type, second->value);
				/* Structure types */
				default:
				{
					if (first->type == second->type)
					{
						if (first->size == second->size)
						{
							return _ELEMENT_EQUALS(first->value, second->value, first->size);
						}
					}
					else
					{
						_PRINT_WARNING_EQUALITY(first->type, second->type);
					}
				}
			}
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

integer hash(const Structure* structure)
{
	if (_STRUCTURE_CHECK(structure))
	{
		switch (structure->type)
		{
			/* Primitive types */
			case _BOOLEAN_TYPE:
				return boolean_hash(structure->value);
			case _CHARACTER_TYPE:
				return char_hash(structure->value);
			case _DIGIT_TYPE:
				return digit_hash(structure->value);
			case _INTEGER_TYPE:
				return integer_hash(structure->value);
			case _NATURAL_TYPE:
				return natural_hash(structure->value);
			case _REAL_TYPE:
				return real_hash(structure->value);
			case _TIME_TYPE:
				return Time_hash(structure->value);
			/* Structure type */
			case _STRUCTURE_TYPE:
				return Structure_hash(structure->value);
			/* Basic types */
			case _BASIC_TYPE:
			case _COMPARABLE_TYPE:
			case _NUMBER_TYPE:
			case _OBJECT_TYPE:
			case _ITERATOR_TYPE:
			case _ITERABLE_TYPE:
			case _COLLECTION_TYPE:
			case _LIST_TYPE:
			case _ARRAY_TYPE:
				return Basic_hash(structure->value);
			/* Array types */
			case _STRING_TYPE:
				return string_hash(structure->value);
			default:
				return Structure_hash(structure->value);
		}
	}
	return integer_random();
}

integer Structure_hash(const void* structure)
{
	_IF(_CHECK(structure, _STRUCTURE_NAME))
	{
		/* Get the Structure */
		const Structure* s = (Structure*) structure;

		if (s->value != NULL)
		{
			return bits_hash(3,
				s->type,
				(integer) s->size,
				*((integer*) s->value));
		}
	}
	return integer_random();
}

integer Basic_hash(const void* structure)
{
	_IF(_CHECK(structure, _BASIC_NAME))
	{
		/* Get the Hashable structure */
		const Hashable* h = (Hashable*) structure;

		if (h->hash != NULL)
		{
			return h->hash(h);
		}
	}
	return integer_random();
}

function_hash get_function_hash(const type type)
{
	switch (type)
	{
		/* Primitive types */
		case _BOOLEAN_TYPE:
			return boolean_hash;
		case _CHARACTER_TYPE:
			return char_hash;
		case _DIGIT_TYPE:
			return digit_hash;
		case _INTEGER_TYPE:
			return integer_hash;
		case _NATURAL_TYPE:
			return natural_hash;
		case _REAL_TYPE:
			return real_hash;
		case _TIME_TYPE:
			return Time_hash;
		/* Structure type */
		case _STRUCTURE_TYPE:
			return Structure_hash;
		/* Basic types */
		case _BASIC_TYPE:
		case _COMPARABLE_TYPE:
		case _NUMBER_TYPE:
		case _OBJECT_TYPE:
		case _ITERATOR_TYPE:
		case _ITERABLE_TYPE:
		case _COLLECTION_TYPE:
		case _LIST_TYPE:
		case _ARRAY_TYPE:
			return Basic_hash;
		/* Array types */
		case _STRING_TYPE:
			return string_hash;
		/* Unknown type */
		default:
			_PRINT_WARNING_NO_FUNCTION(_S("hash"), type);
	}
	return NULL;
}

/**************************************************************************************************/

boolean to_string(const void* source, const type type, string target)
{
	_IF(_CHECK(target, _STRING_NAME))
	{
		if (source == NULL)
		{
			return string_to_string(_NULL_STRING, target);
		}
		else
		{
			switch (type)
			{
				/* Primitive types */
				case _BOOLEAN_TYPE:
					return boolean_to_string(source, target);
				case _CHARACTER_TYPE:
					return char_to_string(source, target);
				case _DIGIT_TYPE:
					return digit_to_string(source, target);
				case _INTEGER_TYPE:
					return integer_to_string(source, target);
				case _NATURAL_TYPE:
					return natural_to_string(source, target);
				case _REAL_TYPE:
					return real_to_string(source, target);
				case _TIME_TYPE:
					return Time_to_string(source, target);
				/* Structure type */
				case _STRUCTURE_TYPE:
					return Structure_to_string(source, target);
				/* Basic types */
				case _BASIC_TYPE:
				case _COMPARABLE_TYPE:
				case _NUMBER_TYPE:
				case _OBJECT_TYPE:
				case _ITERATOR_TYPE:
				case _ITERABLE_TYPE:
				case _COLLECTION_TYPE:
				case _LIST_TYPE:
				case _ARRAY_TYPE:
					return Basic_to_string(source, target);
				/* Array types */
				case _STRING_TYPE:
					return string_to_string(source, target);
				default:
					_UNKNOWN_TO_STRING(target);
			}
		}
	}
	return _FALSE;
}

boolean Structure_to_string(const void* source, string target)
{
	_IF(_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the Structure */
		const Structure* s = (Structure*) source;

		switch (s->type)
		{
			case _CHARACTERS_TYPE:
				return chars_to_string(s->value, s->size, target);
			default:
				return to_string(s->value, s->type, target);
		}
	}
	return _FALSE;
}

boolean Basic_to_string(const void* source, string target)
{
	_IF(_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the Stringable structure */
		const Stringable* s = (Stringable*) source;

		if (s->toString != NULL)
		{
			return s->toString(s, target);
		}
	}
	return _FALSE;
}

function_to_string get_function_to_string(const type type)
{
	switch (type)
	{
		/* Primitive types */
		case _BOOLEAN_TYPE:
			return boolean_to_string;
		case _CHARACTER_TYPE:
			return char_to_string;
		case _DIGIT_TYPE:
			return digit_to_string;
		case _INTEGER_TYPE:
			return integer_to_string;
		case _NATURAL_TYPE:
			return natural_to_string;
		case _REAL_TYPE:
			return real_to_string;
		case _TIME_TYPE:
			return Time_to_string;
		/* Structure type */
		case _STRUCTURE_TYPE:
			return Structure_to_string;
		/* Basic types */
		case _BASIC_TYPE:
		case _COMPARABLE_TYPE:
		case _NUMBER_TYPE:
		case _OBJECT_TYPE:
		case _ITERATOR_TYPE:
		case _ITERABLE_TYPE:
		case _COLLECTION_TYPE:
		case _LIST_TYPE:
		case _ARRAY_TYPE:
			return Basic_to_string;
		/* Array types */
		case _STRING_TYPE:
			return string_to_string;
		/* Unknown type */
		default:
			_PRINT_WARNING_NO_FUNCTION(_S("to_string"), type);
	}
	return NULL;
}

boolean append_to_string(const void* source, const type type, string target)
{
	string buffer;

	to_string(source, type, buffer);
	return string_append_to_string(buffer, target);
}

boolean Structure_append_to_string(const void* source, string target)
{
	string buffer;

	Structure_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}

boolean Basic_append_to_string(const void* source, string target)
{
	string buffer;

	Basic_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}

function_append_to_string get_function_append_to_string(const type type)
{
	switch (type)
	{
		/* Primitive types */
		case _BOOLEAN_TYPE:
			return boolean_append_to_string;
		case _CHARACTER_TYPE:
			return char_append_to_string;
		case _DIGIT_TYPE:
			return digit_append_to_string;
		case _INTEGER_TYPE:
			return integer_append_to_string;
		case _NATURAL_TYPE:
			return natural_append_to_string;
		case _REAL_TYPE:
			return real_append_to_string;
		case _TIME_TYPE:
			return Time_append_to_string;
		/* Structure type */
		case _STRUCTURE_TYPE:
			return Structure_append_to_string;
		/* Basic types */
		case _BASIC_TYPE:
		case _COMPARABLE_TYPE:
		case _NUMBER_TYPE:
		case _OBJECT_TYPE:
		case _ITERATOR_TYPE:
		case _ITERABLE_TYPE:
		case _COLLECTION_TYPE:
		case _LIST_TYPE:
		case _ARRAY_TYPE:
			return Basic_append_to_string;
		/* Array types */
		case _STRING_TYPE:
			return string_append_to_string;
		/* Unknown type */
		default:
			_PRINT_WARNING_NO_FUNCTION(_S("append_to_string"), type);
	}
	return NULL;
}


/***************************************************************************************************
 * CORE
 **************************************************************************************************/

Core Core_create(const boolean isDynamic, const boolean isElement, const boolean isBasic, const boolean isComparable)
{
	Core c;

	c.isDynamic = isDynamic;
	c.isElement = isElement;
	c.isBasic = isBasic;
	c.isComparable = isComparable;
	c.status = 0;
	return c;
}
