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
#ifndef _COMMON_MATH_H
#define _COMMON_MATH_H


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

#include <math.h>


	/***********************************************************************************************
	 * CONSTANTS
	 **********************************************************************************************/

#define _PI							3.1415926535897932384626433832795028841971

	extern const real DEG_TO_RAD;
	extern const real RAD_TO_DEG;


	/***********************************************************************************************
	 * ABSOLUTE VALUE
	 **********************************************************************************************/

	/**
	 * Returns the absolute value of the specified integer.
	 * <p>
	 * @param number an integer
	 * <p>
	 * @return the absolute value of the specified integer
	 */
	integer integer_abs(const integer number);

	/**
	 * Returns the absolute value of the specified real number.
	 * <p>
	 * @param number a real number
	 * <p>
	 * @return the absolute value of the specified real number
	 */
	real real_abs(const real number);


	/***********************************************************************************************
	 * CONVERSIONS
	 **********************************************************************************************/

	/**
	 * Converts the specified number to a digit considering its boundaries.
	 * <p>
	 * @param number the number to be converted
	 * <p>
	 * @return the resulting number
	 */
	digit natural_to_digit(const natural number);

	/**********************************************************************************************/

	/**
	 * Converts the specified number to a digit considering its boundaries.
	 * <p>
	 * @param number the number to be converted
	 * <p>
	 * @return the resulting number
	 */
	digit integer_to_digit(const integer number);

	/**
	 * Converts the specified number to a natural number considering its
	 * boundaries.
	 * <p>
	 * @param number the number to be converted
	 * <p>
	 * @return the resulting number
	 */
	natural integer_to_natural(const integer number);

	/**********************************************************************************************/

	/**
	 * Converts the specified number to a digit considering its boundaries.
	 * <p>
	 * @param number the number to be converted
	 * <p>
	 * @return the resulting number
	 */
	digit real_to_digit(const real number);

	/**
	 * Converts the specified number to a natural number considering its
	 * boundaries.
	 * <p>
	 * @param number the number to be converted
	 * <p>
	 * @return the resulting number
	 */
	natural real_to_natural(const real number);

	/**
	 * Converts the specified number to an integer considering its boundaries.
	 * <p>
	 * @param number the number to be converted
	 * <p>
	 * @return the resulting number
	 */
	integer real_to_integer(const real number);


	/***********************************************************************************************
	 * OPERATIONS
	 **********************************************************************************************/

	/**
	 * Returns the largest natural number that is less than or equal to the
	 * specified real number.
	 * <p>
	 * @param number the real number to be converted
	 * <p>
	 * @return the largest natural number that is less than or equal to the
	 *         specified real number
	 */
	natural floor_to_natural(const real number);

	/**
	 * Returns the largest integer that is less than or equal to the specified
	 * real number.
	 * <p>
	 * @param number the real number to be converted
	 * <p>
	 * @return the largest integer that is less than or equal to the specified
	 *         real number
	 */
	integer floor_to_integer(const real number);

	/**********************************************************************************************/

	/**
	 * Returns the smallest natural number that is greater than or equal to the
	 * specified real number.
	 * <p>
	 * @param number the real number to be converted
	 * <p>
	 * @return the smallest natural number that is greater than or equal to the
	 *         specified real number
	 */
	natural ceil_to_natural(const real number);

	/**
	 * Returns the smallest integer that is greater than or equal to the
	 * specified real number.
	 * <p>
	 * @param number the real number to be converted
	 * <p>
	 * @return the smallest integer that is greater than or equal to the
	 *         specified real number
	 */
	integer ceil_to_integer(const real number);

	/**********************************************************************************************/

	/**
	 * Returns the closest natural number to the specified real number.
	 * <p>
	 * @param number the real number to be converted
	 * <p>
	 * @return the closest natural number to the specified real number
	 */
	natural round_to_natural(const real number);

	/**
	 * Returns the closest integer to the specified real number.
	 * <p>
	 * @param number the real number to be converted
	 * <p>
	 * @return the closest integer to the specified real number
	 */
	integer round_to_integer(const real number);


	/***********************************************************************************************
	 * RANDOM
	 **********************************************************************************************/

	/**
	 * Returns a random real number in [0, 1[.
	 */
	real real_rand_one(void);

	/**
	 * Returns a random natural number in [from, to[.
	 */
	natural natural_rand(const natural from, const natural to);

	/**
	 * Returns a random integer in [from, to[.
	 */
	integer integer_rand(const integer from, const integer to);

	/**
	 * Returns a random real number in [from, to[.
	 */
	real real_rand(const real from, const real to);

	/**********************************************************************************************/

	/**
	 * Returns a random real number in [0, 1].
	 */
	real real_rand_one_inclusive(void);

	/**
	 * Returns a random natural number in [from, to].
	 */
	natural natural_rand_inclusive(const natural from, const natural to);

	/**
	 * Returns a random integer in [from, to].
	 */
	integer integer_rand_inclusive(const integer from, const integer to);

	/**
	 * Returns a random real number in [from, to].
	 */
	real real_rand_inclusive(const real from, const real to);

	/**********************************************************************************************/

	/**
	 * Returns a random natural number.
	 */
	natural natural_random(void);

	/**
	 * Returns a random integer.
	 */
	integer integer_random(void);


	/***********************************************************************************************
	 * POWER
	 **********************************************************************************************/

	/**
	 * Returns the square of the specified real number.
	 * <p>
	 * @param number a real number
	 * <p>
	 * @return the square of the specified real number
	 */
	real square(const real number);


	/***********************************************************************************************
	 * ROOT
	 **********************************************************************************************/

	/**
	 * Returns the root with the specified degree of the specified real number.
	 * <p>
	 * @param degree   the degree of the root
	 * @param radicand a nonnegative real number
	 * <p>
	 * @return the root with the specified degree of the specified real number
	 */
	real root(const natural degree, const real radicand);

	/**
	 * Returns the square root of the specified real number.
	 * <p>
	 * @param radicand a nonnegative real number
	 * <p>
	 * @return the square root of the specified real number
	 */
	real square_root(const real radicand);


#endif /* _COMMON_MATH_H */
#ifdef __cplusplus
}
#endif
