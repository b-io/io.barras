/*
 * The MIT License
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

#ifdef __cplusplus
extern "C"
{
#endif
#ifndef _COMMON_TYPES_H
#define _COMMON_TYPES_H


	/***************************************************************************
	 * INCLUDES
	 **************************************************************************/

#include "ceres/CommonConstants.h"
#include "ceres/CommonMacros.h"

#include <time.h>


	/***************************************************************************
	 * PRIMITIVE TYPES
	 **************************************************************************/

	/**
	 * Defines the addresses.
	 */
	typedef char* address;

	/**
	 * Defines the booleans.
	 */
	typedef unsigned char boolean;

	/**
	 * Defines the characters ([_CHARACTER_MIN, _CHARACTER_MAX]).
	 */
#ifdef _WIDE_STRING
	typedef wchar_t character;
#else
	typedef char character;
#endif

	/**
	 * Defines the digits (symbols) as unsigned characters ([_DIGIT_MIN, _DIGIT_MAX]).
	 */
	typedef unsigned char digit;

	/**
	 * Defines the integers ([_INTEGER_MIN, _INTEGER_MAX]).
	 */
#if __STDC__
	typedef signed long int integer;
#else
	typedef signed long long int integer;
#endif

	/**
	 * Defines the natural numbers ([_NATURAL_MIN, _NATURAL_MAX]).
	 */
	typedef size_t natural;

	/**
	 * Defines the real numbers ([_REAL_MIN, _REAL_MAX]).
	 */
	typedef long double real;

	/**
	 * Defines the status.
	 */
	typedef int status;

	/**
	 * Defines the types.
	 */
	typedef integer type;


	/***************************************************************************
	 * ARRAY TYPES
	 **************************************************************************/

	/**
	 * Defines a string as an array of characters of size {@code _STRING_SIZE}.
	 */
	typedef character string[_STRING_SIZE];


	/***************************************************************************
	 * BASIC
	 **************************************************************************/

	typedef void (*function_release)(void*);
	typedef void* (*function_clone)(const void*);
	typedef boolean(*function_equals)(const void*, const type, const void*);
	typedef integer(*function_hash)(const void*);
	typedef boolean(*function_to_string)(const void*, string);
	typedef boolean(*function_append_to_string)(const void*, string);


	/***************************************************************************
	 * COMPARABLE
	 **************************************************************************/

	typedef integer(*function_compare_to)(const void*, const type, const void*);


	/***************************************************************************
	 * I/O
	 **************************************************************************/

	/**
	 * Defines the I/O types.
	 */
	typedef enum IOType
	{
		_IN,
		_OUT
	} IOType;

	/**
	 * Defines the Severity Levels.
	 */
	typedef enum SeverityLevel
	{
		_RESULT,
		_INFO,
		_TEST,
		_WARNING,
		_ERROR,
		_FAILURE
	} SeverityLevel;


	/***************************************************************************
	 * TIME
	 **************************************************************************/

	/**
	 * Defines the clock ticks.
	 */
	typedef clock_t clocks;

	/**
	 * Defines the timestamps.
	 */
	typedef time_t timestamp;

	/**
	 * Defines the Times.
	 */
	typedef struct tm Time;


	/***************************************************************************
	 * SIZES
	 **************************************************************************/

	/**
	 * Defines the size of the booleans.
	 */
	extern const natural BOOLEAN_SIZE;

	/**
	 * Defines the size of the characters.
	 */
	extern const natural CHARACTER_SIZE;

	/**
	 * Defines the size of the digits.
	 */
	extern const natural DIGIT_SIZE;

	/**
	 * Defines the size of the integers.
	 */
	extern const natural INTEGER_SIZE;

	/**
	 * Defines the size of the natural numbers.
	 */
	extern const natural NATURAL_SIZE;

	/**
	 * Defines the size of the real numbers.
	 */
	extern const natural REAL_SIZE;

	/**
	 * Defines the size of the strings.
	 */
	extern const natural STRING_SIZE;


#endif /* _COMMON_TYPES_H */
#ifdef __cplusplus
}
#endif
