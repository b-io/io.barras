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
#ifndef _COMMON_VERIFIER_H
#define _COMMON_VERIFIER_H


	/***************************************************************************
	 * INCLUDES
	 **************************************************************************/

#include "ceres/CommonConstants.h"
#include "ceres/CommonMacros.h"
#include "ceres/CommonTypes.h"
#include "ceres/CommonStructures.h"
#include "ceres/CommonFunctions.h"
#include "ceres/CommonArrays.h"

#include "ceres/io/CommonIO.h"


	/***************************************************************************
	 * ARGUMENT
	 **************************************************************************/

	/**
	 * Returns {@code _TRUE} if the specified argument is correct,
	 * {@code _FALSE} otherwise.
	 * <p>
	 * @param argument the argument to be checked
	 * @param name     the name of the argument to be checked
	 * <p>
	 * @return {@code _TRUE} if the specified argument is correct,
	 *         {@code _FALSE} otherwise
	 */
	boolean check(const void* argument, const string name);

	/**
	 * Returns {@code _TRUE} if the specified arguments are correct,
	 * {@code _FALSE} otherwise.
	 * <p>
	 * @param firstType   the type of the first argument to be checked
	 * @param firstValue  the value of the first argument to be checked
	 * @param secondType  the type of the second argument to be checked
	 * @param secondValue the value of the second argument to be checked
	 * <p>
	 * @return {@code _TRUE} if the specified arguments are correct,
	 * {@code _FALSE} otherwise
	 */
	boolean checks(const type firstType, const void* firstValue, const type secondType, const void* secondValue);


	/***************************************************************************
	 * TYPE
	 **************************************************************************/

	/**
	 * Returns {@code _TRUE} if the specified types are equal, {@code _FALSE}
	 * otherwise.
	 * <p>
	 * @param givenType    the type to be checked
	 * @param expectedType the type to be expected
	 * <p>
	 * @return {@code _TRUE} if the specified types are equal, {@code _FALSE}
	 *         otherwise
	 */
	boolean type_check(const type givenType, const type expectedType);


	/***************************************************************************
	 * STRUCTURE
	 **************************************************************************/

	/**
	 * Returns {@code _TRUE} if the specified Structure is correct,
	 * {@code _FALSE} otherwise.
	 * <p>
	 * @param structure the Structure to be checked
	 * <p>
	 * @return {@code _TRUE} if the specified Structure is correct,
	 *         {@code _FALSE} otherwise
	 */
	boolean Structure_check(const Structure* structure);

	/**
	 * Returns {@code _TRUE} if the specified Structures are correct,
	 * {@code _FALSE} otherwise.
	 * <p>
	 * @param first  the first Structure to be checked
	 * @param second the second Structure to be checked
	 * <p>
	 * @return {@code _TRUE} if the specified Structures are correct,
	 *         {@code _FALSE} otherwise
	 */
	boolean Structure_checks(const Structure* first, const Structure* second);


	/***************************************************************************
	 * ARRAY
	 **************************************************************************/

	/**
	 * Returns {@code _TRUE} if the specified array is correct and nonempty,
	 * {@code _FALSE} otherwise.
	 * <p>
	 * @param array the array to be checked
	 * @param size  the size of the array to be checked
	 * @param name  the name of the array to be checked
	 * <p>
	 * @return {@code _TRUE} if the specified array is correct and nonempty,
	 *         {@code _FALSE} otherwise
	 */
	boolean array_check(const void* array, const natural size, const string name);


#endif /* _COMMON_VERIFIER_H */
#ifdef __cplusplus
}
#endif
