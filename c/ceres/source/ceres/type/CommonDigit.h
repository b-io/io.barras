/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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
#ifndef _COMMON_DIGIT_H
#define _COMMON_DIGIT_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

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


	/***********************************************************************************************
	 * NUMERIC CHAR
	 **********************************************************************************************/

	/**
	 * Converts a numeric character ('0'-'9') to a digit (0-9).
	 */
	digit num_char_to_digit(const character numChar);

	/**
	 * Converts a digit (0-9) to a numeric character ('0'-'9').
	 */
	character digit_to_num_char(const digit d);


	/***********************************************************************************************
	 * ALPHABETIC CHAR
	 **********************************************************************************************/

	/**
	 * Converts an alphabetic character ('A'-'Z') to a digit (0-25).
	 */
	digit alpha_char_to_digit(const character alphaChar);

	/**
	 * Converts a digit (0-25) to an alphabetic character ('A'-'Z').
	 */
	character digit_to_alpha_char(const digit digit);


	/***********************************************************************************************
	 * GENERIC CHAR
	 **********************************************************************************************/

	/**
	 * Returns {@code _TRUE} if the specified character is numeric ('0'-'9'),
	 * {@code _FALSE} otherwise.
	 * <p>
	 * @return {@code _TRUE} if the specified character is numeric ('0'-'9'),
	 *         {@code _FALSE} otherwise
	 */
	boolean char_is_num(const character c);

	/**
	 * Converts a character ('0'-'9' or 'A'-'Z') to a digit (0-35).
	 */
	digit char_to_digit(const character c);

	/**
	 * Converts a digit (0-35) to a character ('0'-'9' or 'A'-'Z').
	 */
	character digit_to_char(const digit digit);


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
	void* digit_Iterator_next(Iterator* iterator);


	/***********************************************************************************************
	 * COMPARABLE
	 **********************************************************************************************/

	/**
	 * Constructs a Comparable.
	 * <p>
	 * @return the statically constructed Comparable
	 */
	Comparable digit_create_Comparable(void);

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
	integer digit_compare_to(const void* structure, const type type, const void* value);


	/***********************************************************************************************
	 * BASIC
	 **********************************************************************************************/

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
	boolean digit_equals(const void* structure, const type type, const void* value);

	/**********************************************************************************************/

	/**
	 * Returns the hash code of the specified structure.
	 * <p>
	 * @param structure the structure to be hashed
	 * <p>
	 * @return the hash code of the specified structure
	 */
	integer digit_hash(const void* structure);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 */
	boolean digit_to_string(const void* source, string target);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 */
	boolean digit_append_to_string(const void* source, string target);


#endif /* _COMMON_DIGIT_H */
#ifdef __cplusplus
}
#endif
