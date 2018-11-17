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

#include "ceres/type/CommonString.h"


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

character* chars_new(const natural size)
{
	character* string = _ARRAY_NEW(size, CHARACTER_SIZE);

	if (string != NULL)
	{
		/* End the string */
		string[size - 1] = _STRING_END;
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_CHARACTERS_NAME);
	}
	return string;
}

character* string_new(void)
{
	return chars_new(_STRING_SIZE);
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void chars_reset(character* string, const natural size)
{
	_IF(_CHARS_CHECK(string, size))
	{
		/* End the string */
		*string = _STRING_END;
	}
}

void string_reset(string string)
{
	chars_reset(string, _STRING_SIZE);
}

/**************************************************************************************************/

void chars_reset_all(character* string, const natural size)
{
	_IF(_CHARS_CHECK(string, size))
	{
		memset(string, _STRING_END, size);
	}
}

void string_reset_all(string string)
{
	chars_reset_all(string, _STRING_SIZE);
}


/***************************************************************************************************
 * COMMON
 **************************************************************************************************/

natural chars_length(const character* string, const natural size)
{
	_IF(_CHARS_CHECK(string, size))
	{
		/* Declare the iteration variable(s) */
		natural i;
		const character* s;
		/* Get the maximum length */
		const natural max = size - 1;

		for (i = 0, s = string; i < max && *s; ++i, ++s);
		return i;
	}
	return 0;
}

natural string_length(const string string)
{
	return chars_length(string, _STRING_SIZE);
}

/**************************************************************************************************/

boolean chars_is_empty(const character* string, const natural size)
{
	if (chars_length(string, size) == 0)
	{
		return _TRUE;
	}
	return _FALSE;
}

boolean string_is_empty(const string string)
{
	return chars_is_empty(string, _STRING_SIZE);
}

/**************************************************************************************************/

void chars_fill(character* string, const natural size, const character c)
{
	_IF(_CHARS_CHECK(string, size))
	{
		/* Declare the iteration variable(s) */
		natural i;
		character* s;
		/* Get the maximum length */
		const natural max = size - 1;

		for (i = 0, s = string; i < max; ++i, ++s)
		{
			*s = c;
		}
		/* End the string */
		*s = _STRING_END;
	}
}

void string_fill(string string, const character c)
{
	chars_fill(string, _STRING_SIZE, c);
}

/**************************************************************************************************/

void chars_clear(character* string, const natural size)
{
	chars_fill(string, size, _STRING_END);
}

void string_clear(string string)
{
	chars_clear(string, _STRING_SIZE);
}


/***************************************************************************************************
 * COPY
 **************************************************************************************************/

boolean chars_copy(const character* source, const natural sourceSize, const natural length, character* target, const natural targetSize)
{
	_IF(_SOURCE_TARGET_ARRAYS_CHECK(source, sourceSize, target, targetSize))
	{
		/* Declare the iteration variable(s) */
		natural i;
		const character* s;
		character* t;
		/* Get the maximum length */
		natural max;

		if (length > sourceSize - 1)
		{
			_PRINT_WARNING(_S("The specified length is greater than the maximum source length"));
			max = sourceSize - 1;
		}
		else
		{
			max = length;
		}
		if (max > targetSize - 1)
		{
			max = targetSize - 1;
		}
		for (i = 0, s = source, t = target; i < max && *s; ++i, ++s, ++t)
		{
			*t = *s;
		}
		/* End the string */
		*t = _STRING_END;
		if (i + 1 == targetSize)
		{
			/* Check for truncation */
			if (length != i && *s)
			{
				_PRINT_WARNING_TRUNCATION(_CHARACTERS_NAME);
			}
			return _FALSE;
		}
		return _TRUE;
	}
	return _FALSE;
}

boolean chars_copy_to_string(const character* source, const natural sourceSize, const natural length, character* target)
{
	return chars_copy(source, sourceSize, length, target, _STRING_SIZE);
}

/**************************************************************************************************/

boolean string_copy(const character* source, const natural length, character* target)
{
	return chars_copy(source, _STRING_SIZE, length, target, _STRING_SIZE);
}

boolean string_copy_to_chars(const character* source, const natural length, character* target, const natural targetSize)
{
	return chars_copy(source, _STRING_SIZE, length, target, targetSize);
}

/**************************************************************************************************/

void chars_from(const character* source, const natural sourceSize, const natural startIndex, character* target, const natural targetSize)
{
	chars_sub(source, sourceSize, startIndex, sourceSize - 1, target, targetSize);
}

void string_from(const string source, const natural startIndex, string target)
{
	chars_from(source, _STRING_SIZE, startIndex, target, _STRING_SIZE);
}

/**************************************************************************************************/

void chars_sub(const character* source, const natural sourceSize, const natural startIndex, const natural endIndex, character* target, const natural targetSize)
{
	_IF(_SOURCE_TARGET_ARRAYS_CHECK(source, sourceSize, target, targetSize))
	{
		if (startIndex < sourceSize - 1)
		{
			if (startIndex < endIndex)
			{
				chars_copy(&source[startIndex], sourceSize - startIndex, endIndex - startIndex, target, targetSize);
			}
			else
			{
				_PRINT_WARNING(_S("The specified starting index is greater than the specified ending index"));
				chars_reset(target, targetSize);
			}
		}
		else
		{
			_PRINT_WARNING(_S("The specified starting index is greater than the maximum source index"));
			chars_reset(target, targetSize);
		}
	}
}

void string_sub(const string source, const natural startIndex, const natural endIndex, string target)
{
	chars_sub(source, _STRING_SIZE, startIndex, endIndex, target, _STRING_SIZE);
}


/***************************************************************************************************
 * FIND
 **************************************************************************************************/

boolean chars_contain(const character* source, const natural size, const character c)
{
	_IF(_CHARS_CHECK(source, size))
	{
		/* Declare the iteration variable(s) */
		natural i;
		const character* s;

		for (i = 0, s = source; i < size && *s; ++i, ++s)
		{
			if (*s == c)
			{
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

boolean string_contain(const string source, const character c)
{
	return chars_contain(source, _STRING_SIZE, c);
}

/**************************************************************************************************/

character* chars_find(character* source, const natural size, const string set)
{
	_IF(_CHARS_CHECK(source, size)
		&& _CHECK(set, _S("set of characters")))
	{
		if (string_length(set) > 0)
		{
			/* Declare the iteration variable(s) */
			natural i;
			character* s;
			const character* c;

			for (i = 0, s = source; i < size && *s; ++i, ++s)
			{
				for (c = set; *c; ++c)
				{
					if (*s == *c)
					{
						return s;
					}
				}
			}
		}
		else
		{
			_PRINT_WARNING_EMPTY(_S("set of characters"));
		}
	}
	return NULL;
}

character* string_find(string source, const string set)
{
	return chars_find(source, _STRING_SIZE, set);
}

/**************************************************************************************************/

natural chars_find_index(character* source, const natural size, const string set)
{
	return (natural) (chars_find(source, size, set) - source);
}

natural string_find_index(string source, const string set)
{
	return chars_find_index(source, _STRING_SIZE, set);
}

/**************************************************************************************************/

character* chars_find_last(character* source, const natural size, const string set)
{
	_IF(_CHARS_CHECK(source, size)
		&& _CHECK(set, _S("set of characters")))
	{
		if (string_length(set) > 0)
		{
			character* s;
			const character* c;

			for (s = source + chars_length(source, size); s >= source; --s)
			{
				for (c = set; *c; ++c)
				{
					if (*s == *c)
					{
						return s;
					}
				}
			}
		}
		else
		{
			_PRINT_WARNING_EMPTY(_S("set of characters"));
		}
	}
	return NULL;
}

character* string_find_last(string source, const string set)
{
	return chars_find_last(source, _STRING_SIZE, set);
}

/**************************************************************************************************/

natural chars_find_last_index(character* source, const natural size, const string set)
{
	return (natural) (chars_find_last(source, size, set) - source);
}

natural string_find_last_index(string source, const string set)
{
	return chars_find_last_index(source, _STRING_SIZE, set);
}

/**************************************************************************************************/

Array* chars_find_all(character* source, const natural size, const string set)
{
	/* Declare the variable(s) */
	Array* a = Array_new(_CHARACTER_TYPE, CHARACTER_SIZE, 0);
	/* Declare the iteration variable(s) */
	character* s = source;
	natural remainingSize = size;
	character* token;

	while (remainingSize > 1 && (token = chars_find(s, remainingSize, set)))
	{
		a->addValue(a, token);
		s = token + 1;
		remainingSize = size - (natural) (s - source);

	}
	return a;
}

Array* string_find_all(string source, const string set)
{
	return chars_find_all(source, _STRING_SIZE, set);
}

/**************************************************************************************************/

character* chars_find_chars(character* source, const natural sourceSize, const character* text, const natural textSize)
{
	_IF(_CHARS_CHECK(source, sourceSize) && _CHARS_CHECK(text, textSize))
	{
		const natural length = chars_length(text, textSize);

		if (length > 0)
		{
			/* Declare the iteration variable(s) */
			natural i, j, k;
			character* s;
			character* c;
			const character* t;

			for (i = 0, s = source; i < sourceSize && *s; ++i, ++s)
			{
				if (*s == *text)
				{
					for (j = i, c = s, k = 0, t = text;
						j < sourceSize && *c && k < length && *c == *t;
						++j, ++c, ++k, ++t);
					if (k == length)
					{
						return s;
					}
				}
			}
		}
		else
		{
			_PRINT_WARNING_EMPTY(_S("text"));
		}
	}
	return NULL;
}

character* string_find_string(string source, const string text)
{
	return chars_find_chars(source, _STRING_SIZE, text, _STRING_SIZE);
}

/**************************************************************************************************/

boolean chars_replace(character* source, const natural sourceSize, const character* oldText, const natural oldTextSize, const character* newText, const natural newTextSize)
{
	_IF(_CHARS_CHECK(source, sourceSize)
		&& _CHARS_CHECK(oldText, oldTextSize)
		&& _CHARS_CHECK(newText, newTextSize))
	{
		/* Get the length of the old text */
		const natural oldLength = chars_length(oldText, oldTextSize);

		if (oldLength > 0)
		{
			/* Get the maximum source length */
			const natural sourceLengthMax = sourceSize - 1;
			/* Get the length of the new text */
			const natural newLength = chars_length(newText, newTextSize);
			/* Get the minimum length */
			const natural length = _MIN(oldLength, newLength);
			/* Declare the iteration variable(s) */
			natural i = 0;
			character* s = source;
			natural j;
			const character* t;

			if (s = chars_find_chars(s, sourceSize, oldText, oldTextSize))
			{
				/* Copy the new characters into the source */
				for (j = 0, t = newText; j < length; ++s, ++j, ++t)
				{
					*s = *t;
				}
				if (oldLength < newLength)
				{
					/* Get the index */
					const natural index = (natural) (s - source);
					/* Get the size of the buffer */
					const natural bufferSize = sourceSize - index;
					/* Construct a buffer with the size */
					character* buffer = chars_new(bufferSize);

					/* Copy the new characters into the source */
					chars_to_chars(s, bufferSize, buffer, bufferSize);
					for (i = index;
						i < sourceLengthMax && j < newLength;
						++i, ++s, ++j, ++t)
					{
						*s = *t;
					}
					/* Append the remaining characters to the source */
					for (j = 0, t = buffer;
						i < sourceLengthMax;
						++i, ++s, ++j, ++t)
					{
						*s = *t;
					}
					/* End the string */
					*s = _STRING_END;
					/* Free the buffer */
					_FREE(buffer);
				}
				else if (oldLength > newLength)
				{
					/* Get the index */
					const natural index = (natural) (s - source);
					/* Get the absolute difference */
					const natural difference = _MAX(oldLength, newLength) - length;
					/* Get the size of the buffer */
					const natural bufferSize = sourceSize - index - difference;
					/* Construct a buffer with the size */
					character* buffer = chars_new(bufferSize);
					/* Get the maximum length */
					const natural max = bufferSize - 1;

					/* Append the remaining characters to the source */
					chars_to_chars(s + difference, bufferSize, buffer, bufferSize);
					for (j = 0, t = buffer;
						j < max;
						++s, ++j, ++t)
					{
						*s = *t;
					}
					/* End the string */
					*s = _STRING_END;
					/* Free the buffer */
					_FREE(buffer);
				}
			}
			/* Check if the source is full */
			if (i == sourceLengthMax)
			{
				return _FALSE;
			}
			return _TRUE;
		}
		else
		{
			_PRINT_WARNING_EMPTY("old text");
		}
	}
	return _FALSE;
}

boolean string_replace(string source, const string oldText, const string newText)
{
	return chars_replace(source, _STRING_SIZE, oldText, _STRING_SIZE, newText, _STRING_SIZE);
}

/**************************************************************************************************/

boolean chars_replace_all(character* source, const natural sourceSize, const character* oldText, const natural oldTextSize, const character* newText, const natural newTextSize)
{
	_IF(_CHARS_CHECK(source, sourceSize)
		&& _CHARS_CHECK(oldText, oldTextSize)
		&& _CHARS_CHECK(newText, newTextSize))
	{
		/* Get the length of the old text */
		const natural oldLength = chars_length(oldText, oldTextSize);

		if (oldLength > 0)
		{
			/* Clone the source */
			character* clone = chars_new(sourceSize);
			/* Get the maximum source length */
			const natural sourceLengthMax = sourceSize - 1;
			/* Get the length of the new text */
			const natural newLength = chars_length(newText, newTextSize);
			/* Get the minimum length */
			const natural length = _MIN(oldLength, newLength);
			/* Get the absolute difference */
			const natural difference = _MAX(oldLength, newLength) - length;
			/* Declare the iteration variable(s) */
			natural i = 0;
			character* s = source;
			character* c;
			natural j;
			const character* t;
			natural k = 1;
			natural remainingSize = sourceSize;
			natural index;
			natural cloneIndex;
			natural max;

			chars_to_chars(source, sourceSize, clone, sourceSize);
			while (remainingSize > 1 && (s = chars_find_chars(s, remainingSize, oldText, oldTextSize)))
			{
				/* Copy the new characters into the source */
				for (j = 0, t = newText; j < length; ++s, ++j, ++t)
				{
					*s = *t;
				}
				/* Get the index */
				index = (natural) (s - source);
				if (oldLength < newLength)
				{
					/* Copy the new characters into the source */
					for (i = index;
						i < sourceLengthMax && j < newLength;
						++i, ++s, ++j, ++t)
					{
						*s = *t;
					}
					/* Update the index */
					index = i;
					/* Append the remaining characters to the source */
					for (c = s, t = &clone[index - k * difference];
						i < sourceLengthMax;
						++i, ++c, ++t)
					{
						*c = *t;
					}
					/* End the string */
					*c = _STRING_END;
				}
				else if (oldLength > newLength)
				{
					/* Get the index of the current element in the clone */
					cloneIndex = index + k * difference;
					/* Get the maximum length */
					max = sourceLengthMax - cloneIndex;
					/* Append the remaining characters to the source */
					for (c = s, j = 0, t = &clone[cloneIndex];
						j < max;
						++c, ++j, ++t)
					{
						*c = *t;
					}
					/* End the string */
					*c = _STRING_END;
				}
				/* Update the remaining size */
				remainingSize = sourceSize - index;
				++k;
			}
			_FREE(clone);
			/* Check if the source is full */
			if (i == sourceLengthMax)
			{
				return _FALSE;
			}
			return _TRUE;
		}
		else
		{
			_PRINT_WARNING_EMPTY("old text");
		}
	}
	return _FALSE;
}

boolean string_replace_all(string source, const string oldText, const string newText)
{
	return chars_replace_all(source, _STRING_SIZE, oldText, _STRING_SIZE, newText, _STRING_SIZE);
}


/***************************************************************************************************
 * SPLIT
 **************************************************************************************************/

Array* chars_split(character* source, const natural size, const string delimiters)
{
	/* Declare the variable(s) */
	Array* a = Array_new(_STRUCTURE_TYPE, STRUCTURE_SIZE, 0);

	_IF(_CHARS_CHECK(source, size))
	{
		/* Declare the iteration variable(s) */
		natural remainingSize = size;
		character* previous = source;
		character* next;
		natural length;
		Structure s = Structure_create(_CHARACTERS_TYPE, NULL);

		while (remainingSize > 1 && (next = chars_find(previous, remainingSize, delimiters)))
		{
			/* Get the length of the buffer */
			length = (natural) (next - previous);
			/* Get the size of the buffer */
			s.size = length + 1;
			if (length > 0)
			{
				/* Construct a buffer with the size */
				s.value = chars_new(s.size);
				/* Copy the characters into the buffer */
				chars_copy(previous, s.size, length, s.value, s.size);
				/* Add the Structure to the Array */
				a->addValue(a, &s);
			}
			previous = next + 1;
			remainingSize -= s.size;
		}
		if (remainingSize > 1 && *previous)
		{
			/* Get the size of the buffer */
			s.size = remainingSize;
			/* Construct a buffer with the remaining size */
			s.value = chars_new(s.size);
			/* Copy the remaining characters into the buffer */
			chars_copy(previous, s.size, remainingSize - 1, s.value, s.size);
			/* Add the Structure to the Array */
			a->addValue(a, &s);
		}
	}
	return a;
}

Array* string_split(string source, const string delimiters)
{
	return chars_split(source, _STRING_SIZE, delimiters);
}


/***************************************************************************************************
 * CONCATENATE
 **************************************************************************************************/

boolean chars_cat(const character* source, const natural sourceSize, const natural length, character* target, const natural targetSize)
{
	_IF(_SOURCE_TARGET_ARRAYS_CHECK(source, sourceSize, target, targetSize))
	{
		const natural targetLength = chars_length(target, targetSize);
		const natural remainingChars = targetSize - 1 - targetLength;

		if (remainingChars > 0)
		{
			return chars_copy(source, sourceSize, length, &target[targetLength], remainingChars + 1);
		}
		else
		{
			/* Check for truncation */
			if (length > 0 && *source)
			{
				_PRINT_WARNING_TRUNCATION(_CHARACTERS_NAME);
			}
			return _FALSE;
		}
	}
	return _FALSE;
}


/***************************************************************************************************
 * FORMAT
 **************************************************************************************************/

boolean chars_format(character* target, const natural targetSize, const character* format, ...)
{
	boolean isNotFull;
	/* Declare the additional arguments */
	va_list args;

	va_start(args, format);
	isNotFull = format_to_chars(format, &args, target, targetSize);
	va_end(args);
	return isNotFull;
}

boolean string_format(string target, const character* format, ...)
{
	boolean isNotFull;
	/* Declare the additional arguments */
	va_list args;

	va_start(args, format);
	isNotFull = format_to_chars(format, &args, target, _STRING_SIZE);
	va_end(args);
	return isNotFull;
}

character* string_format_new(const character* format, ...)
{
	/* Declare the additional arguments */
	va_list args;
	/* Construct a new string */
	character* target = string_new();

	va_start(args, format);
	format_to_chars(format, &args, target, _STRING_SIZE);
	va_end(args);
	return target;
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

void* string_Iterator_next(Iterator* iterator)
{
	_IF(_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			string* node = (string*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

integer chars_compare_to(const character* first, const natural firstSize, const character* second, const natural secondSize)
{
	_IF(_CHECKS(_CHARACTERS_TYPE, first, _CHARACTERS_TYPE, second))
	{
		if (first == second && firstSize == secondSize)
		{
			return 0;
		}
		else
		{
			/* Declare the iteration variable(s) */
			natural i;
			const character* c1;
			const character* c2;
			integer comparison;
			/* Get the minimum size */
			const natural size = _MIN(firstSize, secondSize);

			for (i = 0, c1 = first, c2 = second;
				i < size && *c1 && *c2;
				++i, ++c1, ++c2)
			{
				comparison = char_compare_to(c1, _CHARACTER_TYPE, c2);
				if (comparison != 0)
				{
					return comparison;
				}
			}
			if (!*c1 && *c2)
			{
				return -1;
			}
			else if (*c1 && !*c2)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
	}
	return _NOT_COMPARABLE;
}

Comparable string_create_Comparable(void)
{
	return Comparable_create(NULL, NULL, string_equals, string_hash, string_to_string, string_compare_to);
}

integer string_compare_to(const void* structure, const type type, const void* value)
{
	if (type == _STRING_TYPE)
	{
		return chars_compare_to(structure, _STRING_SIZE, value, _STRING_SIZE);
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

void chars_release(character* string)
{
	_FREE(string);
}

void string_release(string string)
{
	chars_release(string);
}

/**************************************************************************************************/

boolean chars_equals(const character* first, const natural firstSize, const character* second, const natural secondSize)
{
	return chars_compare_to(first, firstSize, second, secondSize) == 0;
}

boolean string_equals(const void* structure, const type type, const void* value)
{
	if (type == _STRING_TYPE)
	{
		return chars_equals(structure, _STRING_SIZE, value, _STRING_SIZE);
	}
	return _FALSE;
}

/**************************************************************************************************/

integer chars_hash(const character* structure, const natural size)
{
	integer code;

	if (structure != NULL)
	{
		/* Declare the iteration variable(s) */
		natural i;
		const character* c;
		boolean isLeft;

		code = (integer) _CHARACTERS_TYPE;
		for (i = 0, c = structure, isLeft = _TRUE;
			i < size && *c;
			++i, ++c, isLeft = !isLeft)
		{
			if (isLeft)
			{
				code = bits_rotate_left((natural) code, _THIRD_BITS_NUMBER);
			}
			else
			{
				code = bits_rotate_right((natural) code, _EIGHTH_BITS_NUMBER);
			}
			code ^= (integer) (*c);
		}
	}
	else
	{
		code = integer_random();
	}
	return code;
}

integer string_hash(const void* structure)
{
	return chars_hash(structure, _STRING_SIZE);
}

/**************************************************************************************************/

boolean chars_to_chars(const character* source, const natural sourceSize, character* target, const natural targetSize)
{
	return chars_copy(source, sourceSize, sourceSize - 1, target, targetSize);
}

boolean chars_append_to_chars(const character* source, const natural sourceSize, character* target, const natural targetSize)
{
	return chars_cat(source, sourceSize, sourceSize - 1, target, targetSize);
}

/**************************************************************************************************/

boolean chars_to_string(const character* source, const natural sourceSize, string target)
{
	return chars_to_chars(source, sourceSize, target, _STRING_SIZE);
}

boolean chars_append_to_string(const character* source, const natural sourceSize, string target)
{
	return chars_append_to_chars(source, sourceSize, target, _STRING_SIZE);
}

/**************************************************************************************************/

boolean string_to_chars(const string source, string target, const natural targetSize)
{
	return chars_to_chars(source, _STRING_SIZE, target, targetSize);
}

boolean string_append_to_chars(const string source, string target, const natural targetSize)
{
	return chars_append_to_chars(source, _STRING_SIZE, target, targetSize);
}

/**************************************************************************************************/

boolean string_to_string(const void* source, string target)
{
	return chars_to_chars(source, _STRING_SIZE, target, _STRING_SIZE);
}

boolean string_append_to_string(const void* source, string target)
{
	return chars_append_to_chars(source, _STRING_SIZE, target, _STRING_SIZE);
}
