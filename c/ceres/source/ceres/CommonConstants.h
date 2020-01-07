/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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
#ifndef _COMMON_CONSTANTS_H
#define _COMMON_CONSTANTS_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include <stdlib.h>
#include <stdint.h>
#include <float.h>
#include <limits.h>
#include <math.h>


	/***********************************************************************************************
	 * GENERAL
	 **********************************************************************************************/

	/**
	 * Enables the Unicode character encoding standard.
	 */
#ifndef UNICODE
#define UNICODE
#endif
#ifndef _UNICODE
#define _UNICODE
#endif

	/**********************************************************************************************/

	/**
	 * Uses the Standard C.
	 */
#ifndef __STDC__
#define __STDC__							0
#endif

	/**********************************************************************************************/

	/**
	 * Defines the architecture to be considered.
	 */
#if defined(__amd64__) || defined(__amd64) || defined(__x86_64__) || defined(__x86_64) || defined(_M_X64) || defined(_M_AMD64) ||\
	defined(__ia64__) || defined(_IA64) || defined(__IA64__) || defined(__ia64) || defined(_M_IA64) || defined(__itanium__)
#define _32_BITS							0
#define _64_BITS							1
#else
#define _32_BITS							1
#define _64_BITS							0
#endif

	/**********************************************************************************************/

	/**
	 * Uses the wide characters.
	 */
/* #define _WIDE_STRING */

	/**********************************************************************************************/

	/**
	 * Enables the verification of the arguments.
	 */
#define _CHECK_MODE

	/**********************************************************************************************/

	/**
	 * Enables trace information.
	 */
/* #define _TRACE_MODE */

	/**
	 * Enables debug information.
	 */
/* #define _DEBUG_MODE */

	/**
	 * Enables test information.
	 */
#define _TEST_MODE

	/**
	 * Enables warning information.
	 */
#define _WARNING_MODE


	/***********************************************************************************************
	 * COMMON
	 **********************************************************************************************/

	/**
	 * Defines the string of the NULLs.
	 */
#define _NULL_STRING						_S("NULL")

	/**********************************************************************************************/

	/**
	 * Defines the format specifier of the pointers.
	 */
#define _POINTER_FORMAT						_S("%p")

	/**********************************************************************************************/

	/**
	 * Defines the name of the unknown structures.
	 */
#define _UNKNOWN_NAME						_S("unknown structure")

	/**
	 * Defines the type of the unknown structures.
	 */
#if _64_BITS
#define _UNKNOWN_TYPE						0L /* 32 bits */
#else
#define _UNKNOWN_TYPE						0LL /* 64 bits */
#endif

	/**
	 * Defines the string of the unknown structures.
	 */
#define _UNKNOWN_STRING						_S("?")

	/**********************************************************************************************/

	/**
	 * Defines the name of the Structures.
	 */
#define _STRUCTURE_NAME						_S("Structure")

	/**
	 * Defines the type of the Structures.
	 */
#if _64_BITS
#define _STRUCTURE_TYPE						268166434L /* 32 bits */
#else
#define _STRUCTURE_TYPE						4974991948358000738LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the Structures.
	 */
#define _STRUCTURE_FORMAT					_S("%S")

	/**********************************************************************************************/

	/**
	 * Defines the name of the arrays of Structures.
	 */
#define _STRUCTURES_NAME					_S("array of Structures")

	/**
	 * Defines the type of the arrays of Structures.
	 */
#if _64_BITS
#define _STRUCTURES_TYPE					-1689498303L /* 32 bits */
#else
#define _STRUCTURES_TYPE					7723893632854730298LL /* 64 bits */
#endif

	/**********************************************************************************************/

	/**
	 * Defines the name of the Basic structures.
	 */
#define _BASIC_NAME							_S("Basic structure")

	/**
	 * Defines the type of the Basic structures.
	 */
#if _64_BITS
#define _BASIC_TYPE							-270351478L /* 32 bits */
#else
#define _BASIC_TYPE							5119110478015111332LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the Basic structures.
	 */
#define _BASIC_FORMAT						_S("%B")

	/**********************************************************************************************/

#define _NOT_COMPARABLE						-1

	/**********************************************************************************************/

	/**
	 * Defines the name of the Comparable structures.
	 */
#define _COMPARABLE_NAME					_S("Comparable structure")

	/**
	 * Defines the type of the Comparable structures.
	 */
#if _64_BITS
#define _COMPARABLE_TYPE					-1167564821L /* 32 bits */
#else
#define _COMPARABLE_TYPE					-6856122037152631185LL /* 64 bits */
#endif


	/***********************************************************************************************
	 * I/O
	 **********************************************************************************************/

	/**
	 * Defines the line feeds.
	 */
#define _LINE_FEED							_C('\n')

	/**
	 * Defines the carriage returns.
	 */
#define _CARRIAGE_RETURN					_C('\r')

	/**********************************************************************************************/

	/**
	 * Defines the character introducing the format specifiers.
	 */
#define _FORMAT_SPECIFIER					_C('%')

	/**********************************************************************************************/

	/**
	 * Defines the name of the files.
	 */
#define _FILE_NAME							_S("file")

	/**********************************************************************************************/

	/**
	 * Defines the name of the I/O Messages.
	 */
#define _IO_MESSAGE_NAME					_S("I/O Message")

	/**
	 * Defines the type of the I/O Messages.
	 */
#if _64_BITS
#define _IO_MESSAGE_TYPE					385987721L /* 32 bits */
#else
#define _IO_MESSAGE_TYPE					343020474024623861LL /* 64 bits */
#endif

	/**********************************************************************************************/

	/**
	 * Defines the name of the arrays of I/O Messages.
	 */
#define _IO_MESSAGES_NAME					_S("array of I/O Messages")

	/**
	 * Defines the type of the arrays of I/O Messages.
	 */
#if _64_BITS
#define _IO_MESSAGES_TYPE					-1479357451L /* 32 bits */
#else
#define _IO_MESSAGES_TYPE					-262957866591864927LL /* 64 bits */
#endif


	/***********************************************************************************************
	 * ITERABLE
	 **********************************************************************************************/

	/**
	 * Defines the factor that shifts the required number of elements to
	 * allocate (in order to anticipate future increase).
	 */
#define _RESIZE_FACTOR						3

	/**********************************************************************************************/

	/**
	 * Defines the name of the Iterators.
	 */
#define _ITERATOR_NAME						_S("Iterator")

	/**
	 * Defines the type of the Iterators.
	 */
#if _64_BITS
#define _ITERATOR_TYPE						-238741008L /* 32 bits */
#else
#define _ITERATOR_TYPE						7998456870033067090LL /* 64 bits */
#endif

	/**
	 * Defines the default Iterator.
	 */
#define _ITERATOR_DEFAULT					Iterator_create(0, _UNKNOWN_TYPE, 0, NULL, Iterator_next)

	/**********************************************************************************************/

	/**
	 * Defines the name of the Iterable structures.
	 */
#define _ITERABLE_NAME						_S("Iterable structure")

	/**
	 * Defines the type of the Iterable structures.
	 */
#if _64_BITS
#define _ITERABLE_TYPE						-1599987989L /* 32 bits */
#else
#define _ITERABLE_TYPE						-5966621408067176849LL /* 64 bits */
#endif

	/**********************************************************************************************/

	/**
	 * Defines the name of the Collections.
	 */
#define _COLLECTION_NAME					_S("Collection")

	/**
	 * Defines the type of the Collections.
	 */
#if _64_BITS
#define _COLLECTION_TYPE					-1700966664L /* 32 bits */
#else
#define _COLLECTION_TYPE					7634734103968427758LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the Collections.
	 */
#define _COLLECTION_FORMAT					_S("%S")

	/**********************************************************************************************/

	/**
	 * Defines the name of the Lists.
	 */
#define _LIST_NAME							_S("List")

	/**
	 * Defines the type of the Lists.
	 */
#if _64_BITS
#define _LIST_TYPE							805313283L /* 32 bits */
#else
#define _LIST_TYPE							8286623314362575348LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the Lists.
	 */
#define _LIST_FORMAT						_S("%S")

	/**********************************************************************************************/

	/**
	 * Defines the name of the Arrays.
	 */
#define _ARRAY_NAME							_S("Array")

	/**
	 * Defines the type of the Arrays.
	 */
#if _64_BITS
#define _ARRAY_TYPE							7833849L /* 32 bits */
#else
#define _ARRAY_TYPE							1962937303161LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the Array.
	 */
#define _ARRAY_FORMAT						_S("%A")

	/**********************************************************************************************/

	/**
	 * Defines the name of the arrays of Arrays.
	 */
#define _ARRAYS_NAME						_S("array of Arrays")

	/**
	 * Defines the type of the arrays of Arrays.
	 */
#if _64_BITS
#define _ARRAYS_TYPE						1604959883L /* 32 bits */
#else
#define _ARRAYS_TYPE						7408582305883676807LL /* 64 bits */
#endif


	/***********************************************************************************************
	 * MATH
	 **********************************************************************************************/

#define _EPSILON							LDBL_EPSILON


	/***********************************************************************************************
	 * TIME
	 **********************************************************************************************/

	/**
	 * Defines the number of clock ticks per millisecond.
	 */
#define _TICKS_PER_MILLIS					((real) CLOCKS_PER_SEC / 1000.)

	/**********************************************************************************************/

	/**
	 * Defines the name of the Times.
	 */
#define _TIME_NAME							_S("Time")

	/**
	 * Defines the type of the Times.
	 */
#if _64_BITS
#define _TIME_TYPE							-805299341L /* 32 bits */
#else
#define _TIME_TYPE							7854277750135007973LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the times.
	 */
#if defined(__GNUC__)
#define _TIME_FORMAT						_S("%T")
#elif defined(_MSC_VER)
#define _TIME_FORMAT						_S("%X")
#else
#define _TIME_FORMAT						_S("%T")
#endif

	/**
	 * Defines the format specifier of the date times.
	 */
#if defined(__GNUC__)
#define _DATE_TIME_FORMAT					_S("%F %T")
#elif defined(_MSC_VER)
#define _DATE_TIME_FORMAT					_S("%x %X")
#else
#define _DATE_TIME_FORMAT					_S("%F %T")
#endif

	/**********************************************************************************************/

	/**
	 * Defines the name of the arrays of Times.
	 */
#define _TIMES_NAME							_S("array of Times")

	/**
	 * Defines the type of the arrays of Times.
	 */
#if _64_BITS
#define _TIMES_TYPE							-32064055L /* 32 bits */
#else
#define _TIMES_TYPE							-9005049474081278511LL /* 64 bits */
#endif


	/***********************************************************************************************
	 * TYPE
	 **********************************************************************************************/

	/**
	 * Defines the name of the booleans.
	 */
#define _BOOLEAN_NAME						_S("boolean")

	/**
	 * Defines the type of the booleans.
	 */
#if _64_BITS
#define _BOOLEAN_TYPE						438529326L /* 32 bits */
#else
#define _BOOLEAN_TYPE						15675400930500718LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the booleans.
	 */
#define _BOOLEAN_FORMAT						_S("%b")

	/**
	 * Defines the false value for the booleans.
	 */
#define _FALSE								0

	/**
	 * Defines the true value for the booleans.
	 */
#define _TRUE								1

	/**
	 * Defines the string of {@code _FALSE}.
	 */
#define _FALSE_STRING						_S("false")

	/**
	 * Defines the string of {@code _TRUE}.
	 */
#define _TRUE_STRING						_S("true")

	/**********************************************************************************************/

	/**
	 * Defines the name of the arrays of booleans.
	 */
#define _BOOLEANS_NAME						_S("array of booleans")

	/**
	 * Defines the type of the arrays of booleans.
	 */
#if _64_BITS
#define _BOOLEANS_TYPE						-582562266L /* 32 bits */
#else
#define _BOOLEANS_TYPE						2469613196631256235LL /* 64 bits */
#endif

	/**********************************************************************************************/

	/**
	 * Defines the name of the characters.
	 */
#define _CHARACTER_NAME						_S("character")

	/**
	 * Defines the type of the characters.
	 */
#if _64_BITS
#define _CHARACTER_TYPE						-27223692L /* 32 bits */
#else
#define _CHARACTER_TYPE						-8788439263639338892LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the characters.
	 */
#define _CHARACTER_FORMAT					_S("%c")

	/**
	 * Defines the minimum value of the characters.
	 */
#ifdef _WIDE_STRING
#define _CHARACTER_MIN						WCHAR_MIN
#else
#define _CHARACTER_MIN						CHAR_MIN
#endif

	/**
	 * Defines the maximum value of the characters.
	 */
#ifdef _WIDE_STRING
#define _CHARACTER_MAX						WCHAR_MAX
#else
#define _CHARACTER_MAX						CHAR_MAX
#endif

	/**********************************************************************************************/

	/**
	 * Defines the name of the arrays of characters.
	 */
#define _CHARACTERS_NAME					_S("array of characters")

	/**
	 * Defines the type of the arrays of characters.
	 */
#if _64_BITS
#define _CHARACTERS_TYPE					-1211079616L /* 32 bits */
#else
#define _CHARACTERS_TYPE					6553243037010994747LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the arrays of characters.
	 */
#define _CHARACTERS_FORMAT					_S("%s")

	/**********************************************************************************************/

	/**
	 * Defines the name of the digits.
	 */
#define _DIGIT_NAME							_S("digit")

	/**
	 * Defines the type of the digits.
	 */
#if _64_BITS
#define _DIGIT_TYPE							7273908L /* 32 bits */
#else
#define _DIGIT_TYPE							1810818195572LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the digits.
	 */
#define _DIGIT_FORMAT						_S("%d")

	/**
	 * Defines the minimum value of the digits.
	 */
#define _DIGIT_MIN							0

	/**
	 * Defines the maximum value of the digits.
	 */
#define _DIGIT_MAX							UCHAR_MAX

	/**********************************************************************************************/

	/**
	 * Defines the name of the arrays of digits.
	 */
#define _DIGITS_NAME						_S("array of digits")

	/**
	 * Defines the type of the arrays of digits.
	 */
#if _64_BITS
#define _DIGITS_TYPE						1459435659L /* 32 bits */
#else
#define _DIGITS_TYPE						7404500591823675527LL /* 64 bits */
#endif

	/**********************************************************************************************/

	/**
	 * Defines the name of the integers.
	 */
#define _INTEGER_NAME						_S("integer")

	/**
	 * Defines the type of the integers.
	 */
#if _64_BITS
#define _INTEGER_TYPE						440913330L /* 32 bits */
#else
#define _INTEGER_TYPE						15539491731988594LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the integers.
	 */
#define _INTEGER_FORMAT						_S("%i")

	/**
	 * Defines the minimum value of the integers.
	 */
#if _64_BITS
#define _INTEGER_MIN						LONG_MIN
#else
#define _INTEGER_MIN						LLONG_MIN
#endif

	/**
	 * Defines the maximum value of the integers.
	 */
#if _64_BITS
#define _INTEGER_MAX						LONG_MAX
#else
#define _INTEGER_MAX						LLONG_MAX
#endif

	/**********************************************************************************************/

	/**
	 * Defines the name of the arrays of integers.
	 */
#define _INTEGERS_NAME						_S("array of integers")

	/**
	 * Defines the type of the arrays of integers.
	 */
#if _64_BITS
#define _INTEGERS_TYPE						1803562790L /* 32 bits */
#else
#define _INTEGERS_TYPE						-7905976142588138325LL /* 64 bits */
#endif

	/**********************************************************************************************/

	/**
	 * Defines the name of the natural numbers.
	 */
#define _NATURAL_NAME						_S("natural number")

	/**
	 * Defines the type of the natural numbers.
	 */
#if _64_BITS
#define _NATURAL_TYPE						-1497620190L /* 32 bits */
#else
#define _NATURAL_TYPE						-8424211561607063880LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the natural numbers.
	 */
#define _NATURAL_FORMAT						_S("%n")

	/**
	 * Defines the minimum value of the natural numbers.
	 */
#define _NATURAL_MIN						0
	/**
	 * Defines the maximum value of the natural numbers.
	 */
#define _NATURAL_MAX						SIZE_MAX

	/**********************************************************************************************/

	/**
	 * Defines the name of the arrays of natural numbers.
	 */
#define _NATURALS_NAME						_S("array of natural numbers")

	/**
	 * Defines the type of the arrays of natural numbers.
	 */
#if _64_BITS
#define _NATURALS_TYPE						874773334L /* 32 bits */
#else
#define _NATURALS_TYPE						5965406374232622482LL /* 64 bits */
#endif

	/**********************************************************************************************/

	/**
	 * Defines the name of the real numbers.
	 */
#define _REAL_NAME							_S("real number")

	/**
	 * Defines the type of the real numbers.
	 */
#if _64_BITS
#define _REAL_TYPE							980147580L /* 32 bits */
#else
#define _REAL_TYPE							-4168343457386165616LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the real numbers.
	 */
#define _REAL_FORMAT						_S("%r")

	/**
	 * Defines the minimum value of the real numbers.
	 */
#define _REAL_MIN							-LDBL_MAX

	/**
	 * Defines the maximum value of the real numbers.
	 */
#define _REAL_MAX							LDBL_MAX

	/**
	 * Defines the number of decimals to be displayed.
	 */
#define _DECIMALS_NUMBER					_S("4")

	/**
	 * Defines the real number from which the engineering notation is used.
	 */
#define _ENGINEERING_NOTATION_FROM			1E10

	/**********************************************************************************************/

	/**
	 * Defines the name of the arrays of real numbers.
	 */
#define _REALS_NAME							_S("array of real numbers")

	/**
	 * Defines the type of the arrays of real numbers.
	 */
#if _64_BITS
#define _REALS_TYPE							-1166258983L /* 32 bits */
#else
#define _REALS_TYPE							2925876010904038195LL /* 64 bits */
#endif

	/**********************************************************************************************/

	/**
	 * Defines the name of the strings.
	 */
#define _STRING_NAME						_S("string")

	/**
	 * Defines the type of the strings.
	 */
#if _64_BITS
#define _STRING_TYPE						-536401687L /* 32 bits */
#else
#define _STRING_TYPE						7926335351986138663LL /* 64 bits */
#endif
	/**
	 * Defines the format specifier of the strings.
	 */
#define _STRING_FORMAT						_S("%s")

	/**
	 * Defines the maximum size of the strings.
	 */
#define _STRING_SIZE						256

	/**
	 * Defines the maximum length of the strings.
	 */
#define _STRING_LENGTH_MAX					255 /* _STRING_SIZE - 1 */

	/**
	 * Defines the end of the strings.
	 */
#define _STRING_END							_C('\0')

	/**
	 * Defines the empty string.
	 */
#define _STRING_EMPTY						_S("")

	/**********************************************************************************************/

	/**
	 * Defines the name of the arrays of strings.
	 */
#define _STRINGS_NAME						_S("array of strings")

	/**
	 * Defines the type of the arrays of strings.
	 */
#if _64_BITS
#define _STRINGS_TYPE						-181233542L /* 32 bits */
#else
#define _STRINGS_TYPE						-7825345672083828333LL /* 64 bits */
#endif

	/**********************************************************************************************/

	/**
	 * Defines the name of the Numbers.
	 */
#define _NUMBER_NAME						_S("Number")

	/**
	 * Defines the type of the Numbers.
	 */
#if _64_BITS
#define _NUMBER_TYPE						1342637888L /* 32 bits */
#else
#define _NUMBER_TYPE						7277817005702663634LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the Numbers.
	 */
#define _NUMBER_FORMAT						_S("%N")

	/**
	 * Defines the maximum length of the Numbers (arrays of digits).
	 */
#if _64_BITS
#define _NUMBER_LENGTH_MAX					32
#else
#define _NUMBER_LENGTH_MAX					64
#endif

	/**
	 * Defines the maximum base of the Numbers.
	 */
#define _NUMBER_BASE_MAX					36

	/**********************************************************************************************/

	/**
	 * Defines the name of the arrays of Numbers.
	 */
#define _NUMBERS_NAME						_S("array of Numbers")

	/**
	 * Defines the type of the arrays of Numbers.
	 */
#if _64_BITS
#define _NUMBERS_TYPE						-1513657449L /* 32 bits */
#else
#define _NUMBERS_TYPE						-8762079552042635021LL /* 64 bits */
#endif

	/**********************************************************************************************/

	/**
	 * Defines the name of the Objects.
	 */
#define _OBJECT_NAME						_S("Object")

	/**
	 * Defines the type of the Objects.
	 */
#if _64_BITS
#define _OBJECT_TYPE						805730202L /* 32 bits */
#else
#define _OBJECT_TYPE						7133701816351501620LL /* 64 bits */
#endif

	/**
	 * Defines the format specifier of the Objects.
	 */
#define _OBJECT_FORMAT						_S("%O")

	/**********************************************************************************************/

	/**
	 * Defines the name of the arrays of Objects.
	 */
#define _OBJECTS_NAME						_S("array of Objects")

	/**
	 * Defines the type of the arrays of Objects.
	 */
#if _64_BITS
#define _OBJECTS_TYPE						-977072885L /* 32 bits */
#else
#define _OBJECTS_TYPE						-9194424566182301677LL /* 64 bits */
#endif


#endif /* _COMMON_CONSTANTS_H */
#ifdef __cplusplus
}
#endif
