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
#ifndef _COMMON_NUMBER_H
#define _COMMON_NUMBER_H


	/***************************************************************************
	 * INCLUDES
	 **************************************************************************/

#include "ceres/CommonConstants.h"
#include "ceres/CommonMacros.h"
#include "ceres/CommonTypes.h"
#include "ceres/CommonStructures.h"
#include "ceres/CommonFunctions.h"
#include "ceres/CommonArrays.h"
#include "ceres/CommonVerifier.h"

#include "ceres/io/CommonIO.h"

#include "ceres/type/CommonType.h"

#include "ceres/iterable/CommonIterator.h"


	/***************************************************************************
	 * SIZES
	 **************************************************************************/

	/**
	 * Defines the size of the Numbers.
	 */
	extern const natural NUMBER_SIZE;


	/***************************************************************************
	 * MACROS
	 **************************************************************************/

	/**
	 * Checks if the specified Number is correct.
	 * <p>
	 * @param N the Number to be checked
	 */
#define _NUMBER_CHECK(N)						((N) && (N)->base >= 2 && (N)->base <= _NUMBER_BASE_MAX || Number_check(N))

	/**
	 * Checks if the specified base is correct.
	 * <p>
	 * @param B the base to be checked
	 */
#define _BASE_CHECK(B)							(B >= 2 && B <= _NUMBER_BASE_MAX || base_check(B))


	/***************************************************************************
	 * CHECK
	 **************************************************************************/

	/**
	 * Returns {@code _TRUE} if the specified Number is correct, {@code _FALSE}
	 * otherwise.
	 * <p>
	 * @param number the Number to be checked
	 * <p>
	 * @return {@code _TRUE} if the specified Number is correct, {@code _FALSE}
	 *         otherwise
	 */
	boolean Number_check(Number* number);

	/**
	 * Returns {@code _TRUE} if the specified base is correct, {@code _FALSE}
	 * otherwise.
	 * <p>
	 * @param base the base to be checked
	 * <p>
	 * @return {@code _TRUE} if the specified base is correct, {@code _FALSE}
	 *         otherwise
	 */
	boolean base_check(const natural base);


	/***************************************************************************
	 * CONSTRUCT
	 **************************************************************************/

	/**
	 * Constructs a Number statically.
	 * <p>
	 * @param n    the natural number to set
	 * @param base the base to set
	 * <p>
	 * @return the statically constructed Number
	 */
	Number Number_create(const natural n, const natural base);

	/**
	 * Constructs a Number dynamically.
	 * <p>
	 * @param n    the natural number to set
	 * @param base the base to set
	 * <p>
	 * @return the dynamically constructed Number
	 */
	Number* Number_new(const natural n, const natural base);


	/***************************************************************************
	 * RESET
	 **************************************************************************/

	/**
	 * Resets the specified Number.
	 * <p>
	 * @param structure the Number to be reset
	 * @param n         the natural number to set
	 * @param base      the base to set
	 */
	void Number_reset(void* structure, const natural n, const natural base);


	/***************************************************************************
	 * NUMBER
	 **************************************************************************/

	/**
	 * Changes the base of the specified Number to the specified base.
	 * <p>
	 * @param number the Number to be changed
	 * @param toBase the base to set
	 */
	void Number_change_base(void* number, const natural toBase);

	/**
	 * Converts the specified Number to a decimal number.
	 * <p>
	 * @param number the Number to be converted
	 * <p>
	 * @return the resulting decimal number
	 */
	natural Number_to_decimal(const void* number);

	/**
	 * Converts the specified Number to a natural number.
	 * <p>
	 * @param number the Number to be converted
	 * <p>
	 * @return the resulting natural number
	 */
	natural Number_to_natural(const void* number);


	/**
	 * Sets the specified Number to zero.
	 * <p>
	 * @param number the Number to be set
	 */
	void Number_to_zero(void* number);


	/***************************************************************************
	 * CONVERSIONS
	 **************************************************************************/

	/**
	 * Converts the specified decimal number to the specified Number.
	 * <p>
	 * @param decimal the decimal number to be converted
	 * @param number  the output Number
	 * <p>
	 * @return {@code _TRUE} if there is no error, {@code _FALSE} otherwise
	 */
	boolean decimal_to_Number(const natural decimal, Number* number);

	/**************************************************************************/

	/**
	 * Converts the specified natural number with the specified base to the
	 * specified Number.
	 * <p>
	 * @param n      the natural number to be converted
	 * @param base   the base of the natural number to be converted
	 * @param number the output Number
	 * <p>
	 * @return {@code _TRUE} if there is no error, {@code _FALSE} otherwise
	 */
	boolean natural_to_Number(const natural n, const natural base, Number* number);


	/***************************************************************************
	 * ITERABLE
	 **************************************************************************/

	/**
	 * Returns the next element in the iteration.
	 * <p>
	 * @param iterator the Iterator of the iteration
	 * <p>
	 * @return the next element in the iteration
	 */
	void* Number_Iterator_next(Iterator* iterator);


	/***************************************************************************
	 * COMPARABLE
	 **************************************************************************/

	/**
	 * Constructs a Comparable.
	 * <p>
	 * @return the statically constructed Comparable
	 */
	Comparable Number_create_Comparable(void);

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
	integer Number_compare_to(const void* structure, const type type, const void* value);


	/***************************************************************************
	 * BASIC
	 **************************************************************************/

	/**
	 * Releases the specified structure and frees it if requested.
	 * <p>
	 * @param structure the structure to be released
	 */
	void Number_release(void* structure);

	/**************************************************************************/

	/**
	 * Constructs a dynamic copy of the specified structure.
	 * <p>
	 * @param structure the structure to be cloned
	 * <p>
	 * @return a dynamic copy of the specified structure
	 */
	void* Number_clone(const void* structure);

	/**************************************************************************/

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
	boolean Number_equals(const void* structure, const type type, const void* value);

	/**************************************************************************/

	/**
	 * Returns the hash code of the specified structure.
	 * <p>
	 * @param structure the structure to be hashed
	 * <p>
	 * @return the hash code of the specified structure
	 */
	integer Number_hash(const void* structure);

	/**************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean Number_to_string(const void* source, string target);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean Number_append_to_string(const void* source, string target);


#endif /* _COMMON_NUMBER_H */
#ifdef __cplusplus
}
#endif
