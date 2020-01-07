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
#ifndef _FORMATTED_TIME_H
#define _FORMATTED_TIME_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include "ceres/Common.h"


	/***********************************************************************************************
	 * CONSTANTS
	 **********************************************************************************************/

	/**
	 * Defines the name of the Formatted Times.
	 */
#define _FORMATTED_TIME_NAME		_S("Formatted Time")

	/**
	 * Defines the type of the Formatted Times.
	 */
#if _32_BITS
#define _FORMATTED_TIME_TYPE		731609567L /* 32 bits */
#else
#define _FORMATTED_TIME_TYPE		-8194824287521670209LL /* 64 bits */
#endif


	/***********************************************************************************************
	 * SIZES
	 **********************************************************************************************/

	/**
	 * Defines the size of the Formatted Times.
	 */
	extern const size FORMATTED_TIME_SIZE;


	/***********************************************************************************************
	 * STRUCTURES
	 **********************************************************************************************/

	/**
	 * Defines the Formatted Time.
	 */
	typedef struct FormattedTime
	{
		/*******************************************************************************************
		 * BASIC
		 ******************************************************************************************/

		Core core;

		void (*release)(void* structure);
		void* (*clone)(const void* structure);
		boolean(*equals)(const void* structure, const type type, const void* value);
		integer(*hash)(const void* structure);
		boolean(*toString)(const void* source, string target);

		/*******************************************************************************************
		 * COMPARABLE
		 ******************************************************************************************/

		integer(*compareTo)(const void* structure, const type type, const void* value);

		/*******************************************************************************************
		 * FORMATTED TIME
		 ******************************************************************************************/

		timestamp time;
		Time utc;
		Time local;
		string format;
	} FormattedTime;


	/***********************************************************************************************
	 * CONSTRUCT
	 **********************************************************************************************/

	/**
	 * Constructs a Formatted Time statically.
	 * <p>
	 * @param stamp  the time stamp to set (optional)
	 * @param format the format to set (optional)
	 * <p>
	 * @return the statically constructed Formatted Time
	 */
	FormattedTime FormattedTime_create(const timestamp* stamp, const string format);

	/**
	 * Constructs a Formatted Time dynamically.
	 * <p>
	 * @param stamp  the time stamp to set (optional)
	 * @param format the format to set (optional)
	 * <p>
	 * @return the dynamically constructed Formatted Time
	 */
	FormattedTime* FormattedTime_new(const timestamp* stamp, const string format);


	/***********************************************************************************************
	 * RESET
	 **********************************************************************************************/

	/**
	 * Resets the specified Formatted Time.
	 * <p>
	 * @param formattedTime the Formatted Time to be reset
	 * @param stamp         the time stamp to set (optional)
	 * @param format        the format to set (optional)
	 */
	void FormattedTime_reset(void* formattedTime, const timestamp* stamp, const string format);


	/***********************************************************************************************
	 * ITERABLE
	 **********************************************************************************************/

	/**
	 * Returns the next element in the iteration.
	 * <p>
	 * @param iterator the Iterator of the iteration
	 * <p>
	 * @return the next element in the iteration
	 */
	void* FormattedTime_Iterator_next(Iterator* iterator);


	/***********************************************************************************************
	 * COMPARABLE
	 **********************************************************************************************/

	/**
	 * Constructs a Comparable.
	 * <p>
	 * @return the statically constructed Comparable
	 */
	Comparable FormattedTime_create_Comparable(void);

	/**
	 * Compares the specified structures for order. Returns a negative integer,
	 * zero, or a positive integer as the first argument is less than, equal to,
	 * or greater than the second.
	 * <p>
	 * @param structure the structure to be compared for order
	 * @param type      the type of the structure with which to compare
	 * @param value     the value of the structure with which to compare
	 * <p>
	 * @return a negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second
	 */
	integer FormattedTime_compare_to(const void* structure, const type type, const void* value);


	/***********************************************************************************************
	 * BASIC
	 **********************************************************************************************/

	/**
	 * Releases the specified structure and frees it if requested.
	 * <p>
	 * @param structure the structure to be released
	 */
	void FormattedTime_release(void* structure);

	/**********************************************************************************************/

	/**
	 * Constructs a dynamic copy of the specified structure.
	 * <p>
	 * @param structure the structure to be cloned
	 * <p>
	 * @return a dynamic copy of the specified structure
	 */
	void* FormattedTime_clone(const void* structure);

	/**********************************************************************************************/

	/**
	 * Returns {@code _TRUE} if the specified structures are equal,
	 * {@code _FALSE} otherwise.
	 * <p>
	 * @param structure the structure to be compared for equality
	 * @param type      the type of the structure with which to compare
	 * @param value     the value of the structure with which to compare
	 * <p>
	 * @return {@code _TRUE} if the specified structures are equal,
	 *         {@code _FALSE} otherwise
	 */
	boolean FormattedTime_equals(const void* structure, const type type, const void* value);

	/**********************************************************************************************/

	/**
	 * Returns the hash code of the specified structure.
	 * <p>
	 * @param structure the structure to be hashed
	 * <p>
	 * @return the hash code of the specified structure
	 */
	integer FormattedTime_hash(const void* structure);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean FormattedTime_to_string(const void* source, string target);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean FormattedTime_append_to_string(const void* source, string target);


#endif /* _FORMATTED_TIME_H */
#ifdef __cplusplus
}
#endif
