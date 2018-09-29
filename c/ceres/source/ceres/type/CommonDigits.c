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

#include "ceres/type/CommonDigits.h"


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

integer digits_hash(const void* structure, const natural length)
{
	if (structure != NULL)
	{
		/* Get the string */
		string s;

		digits_to_string(structure, length, s);
		return bits_hash(2, _DIGITS_TYPE, string_hash(s));
	}
	return integer_random();
}

/**************************************************************************************************/

boolean digits_to_string(const void* source, const natural length, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		/* Declare the iteration variable(s) */
		natural i;
		const digit* d;
		character* c;
		/* Get the maximum length */
		natural max;

		/* Check for truncation */
		if (length > _STRING_LENGTH_MAX)
		{
			_PRINT_WARNING_TRUNCATION(_STRING_NAME);
			max = _STRING_LENGTH_MAX;
		}
		else
		{
			max = length;
		}
		for (i = 0, d = (digit*) source, c = target;
			i < max;
			++i, ++d, ++c)
		{
			*c = digit_to_char(*d);
		}
		/* End the string */
		*c = _STRING_END;
		/* Check if the target is full */
		if (i == _STRING_LENGTH_MAX)
		{
			return _FALSE;
		}
		return _TRUE;
	}
	return _FALSE;
}

boolean digits_append_to_string(const void* source, const natural length, string target)
{
	string buffer;

	digits_to_string(source, length, buffer);
	return string_append_to_string(buffer, target);
}
