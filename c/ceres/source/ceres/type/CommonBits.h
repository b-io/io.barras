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

#ifdef __cplusplus
extern "C"
{
#endif
#ifndef _COMMON_BITS_H
#define _COMMON_BITS_H


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

#include "ceres/math/CommonMath.h"


	/***********************************************************************************************
	 * SIZES
	 **********************************************************************************************/

	extern const natural _BITS_NUMBER;
	extern const natural _HALF_BITS_NUMBER;
	extern const natural _THIRD_BITS_NUMBER;
	extern const natural _QUARTER_BITS_NUMBER;
	extern const natural _EIGHTH_BITS_NUMBER;


	/***********************************************************************************************
	 * ROTATIONS
	 **********************************************************************************************/

	natural bits_rotate(const natural bits, const integer shift);
	natural bits_rotate_left(const natural bits, const natural shift);
	natural bits_rotate_right(const natural bits, const natural shift);


	/***********************************************************************************************
	 * PRINTABLE
	 **********************************************************************************************/

	void bits_print(const natural bits);
	void bits_printn(const natural bits);


	/***********************************************************************************************
	 * BASIC
	 **********************************************************************************************/

	/**
	 * Returns the hash code of the specified integers.
	 * <p>
	 * @param n   the number of the integers to be hashed
	 * @param ... the integers to be hashed
	 * <p>
	 * @return the hash code of the specified integers
	 */
	integer bits_hash(const natural n, ...);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean bits_to_string(const void* source, string target);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean bits_append_to_string(const void* source, string target);


#endif /* _COMMON_BITS_H */
#ifdef __cplusplus
}
#endif
