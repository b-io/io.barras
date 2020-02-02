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
#ifdef __cplusplus
extern "C"
{
#endif
#ifndef _COMMON_INTEGER_H
#define _COMMON_INTEGER_H


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
	 * GENERATIONS
	 **********************************************************************************************/

	/**
	 * Returns an {@code integer} array of size {@code size} containing the sequence
	 * of integers starting with zero and spaced by one.
	 * <p>
	 * @param size the size of the array to be generated
	 * <p>
	 * @return an {@code integer} array of size {@code size} containing the sequence
	 *         of integers starting with zero and spaced by one
	 */
	integer* sequence_new(const natural size);

	/**
	 * Returns an {@code integer} array of size {@code size} containing the sequence
	 * of integers starting with {@code start} and spaced by one.
	 * <p>
	 * @param size  the size of the array to be generated
	 * @param start the first value of the array to be generated
	 * <p>
	 * @return an {@code integer} array of size {@code size} containing the sequence
	 *         of integers starting with {@code start} and spaced by one
	 */
	integer* sequence_from_new(const natural size, const integer start);

	/**
	 * Returns an {@code integer} array of size {@code size} containing the sequence
	 * of integers starting with {@code start} and spaced by {@code step}.
	 * <p>
	 * @param size  the size of the array to be generated
	 * @param start the first value of the array to be generated
	 * @param step  the interval between the values of the array to be generated
	 * <p>
	 * @return an {@code integer} array of size {@code size} containing the sequence
	 *         of integers starting with {@code start} and spaced by
	 *         {@code step}
	 */
	integer* sequence_from_step_new(const natural size, const integer start, const integer step);


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
	void* integer_Iterator_next(Iterator* iterator);


	/***********************************************************************************************
	 * COMPARABLE
	 **********************************************************************************************/

	/**
	 * Constructs a Comparable.
	 * <p>
	 * @return the statically constructed Comparable
	 */
	Comparable integer_create_Comparable(void);

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
	integer integer_compare_to(const void* structure, const type type, const void* value);


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
	boolean integer_equals(const void* structure, const type type, const void* value);

	/**********************************************************************************************/

	/**
	 * Returns the hash code of the specified structure.
	 * <p>
	 * @param structure the structure to be hashed
	 * <p>
	 * @return the hash code of the specified structure
	 */
	integer integer_hash(const void* structure);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean integer_to_string(const void* source, string target);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean integer_append_to_string(const void* source, string target);


#endif /* _COMMON_INTEGER_H */
#ifdef __cplusplus
}
#endif
