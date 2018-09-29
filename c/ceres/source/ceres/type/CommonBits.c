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

#include "ceres/type/CommonNumber.h"


/***************************************************************************************************
 * SIZES
 **************************************************************************************************/

const natural _BITS_NUMBER = CHAR_BIT * sizeof (natural);
const natural _HALF_BITS_NUMBER = CHAR_BIT / 2 * sizeof (natural);
const natural _THIRD_BITS_NUMBER = (natural) (CHAR_BIT / 3. * sizeof (natural));
const natural _QUARTER_BITS_NUMBER = 2 * sizeof (natural);
const natural _EIGHTH_BITS_NUMBER = sizeof (natural);


/***************************************************************************************************
 * ROTATIONS
 **************************************************************************************************/

natural bits_rotate(const natural bits, const integer shift)
{
	const integer n = shift % _BITS_NUMBER;

	if (n < 0)
	{
		return bits_rotate_left(bits, (natural) (-n));
	}
	else if (n > 0)
	{
		return bits_rotate_right(bits, (natural) n);
	}
	return bits;
}

natural bits_rotate_left(const natural bits, const natural shift)
{
	const natural n = shift % _BITS_NUMBER;

	return (bits << n) | (bits >> (_BITS_NUMBER - n));
}

natural bits_rotate_right(const natural bits, const natural shift)
{
	const natural n = shift % _BITS_NUMBER;

	return (bits >> n) | (bits << (_BITS_NUMBER - n));
}


/***************************************************************************************************
 * PRINTABLE
 **************************************************************************************************/

void bits_print(const natural bits)
{
	string output;

	bits_to_string(&bits, output);
	print(_STRING_FORMAT, output);
}

void bits_printn(const natural bits)
{
	bits_print(bits);
	_PRINT_LINE_FEED(stdout);
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

integer bits_hash(const natural n, ...)
{
	integer code;

	if (n > 0)
	{
		/* Declare the additional arguments */
		va_list args;
		/* Declare the iteration variable(s) */
		natural i;
		boolean isLeft;

		va_start(args, n);
		code = (integer) n;
		for (i = 0, isLeft = _TRUE; i < n; ++i, isLeft = !isLeft)
		{
			if (isLeft)
			{
				code = bits_rotate_left((natural) code, _THIRD_BITS_NUMBER);
			}
			else
			{
				code = bits_rotate_right((natural) code, _EIGHTH_BITS_NUMBER);
			}
			code ^= va_arg(args, integer);
		}
		va_end(args);
	}
	else
	{
		code = integer_random();
	}
	return code;
}

/**************************************************************************************************/

boolean bits_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the bits */
		natural bits = *((natural*) source);
		/* Declare the iteration variable(s) */
		natural i = _BITS_NUMBER - 1;

		chars_fill(target, i, _C('0'));
		while (bits && i >= 0)
		{
			if (bits & 1)
			{
				target[i] = _C('1');
			}
			else
			{
				target[i] = _C('0');
			}
			bits >>= 1;
			--i;
		}
		/* End the string */
		target[_BITS_NUMBER] = _STRING_END;
		return _TRUE;
	}
	return _FALSE;
}

boolean bits_append_to_string(const void* source, string target)
{
	string buffer;

	bits_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
