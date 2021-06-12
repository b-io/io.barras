/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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
#ifndef _COMMON_ITERABLE_H
#define _COMMON_ITERABLE_H


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
	 * RESET
	 **********************************************************************************************/

	/**
	 * Resets the specified Iterable structure.
	 * <p>
	 * @param iterable    the Iterable structure to be reset
	 * @param length      the length to set
	 * @param elementType the element type to set
	 * @param elementSize the element size to set
	 */
	void Iterable_reset(void* iterable, const natural length, const type elementType, const natural elementSize);


	/***********************************************************************************************
	 * ITERABLE
	 **********************************************************************************************/

	/**
	 * Returns {@code _TRUE} if the specified Iterable structure is empty,
	 * {@code _FALSE} otherwise.
	 * <p>
	 * @param iterable the Iterable structure
	 * <p>
	 * @return {@code _TRUE} if the specified Iterable structure is empty,
	 *         {@code _FALSE} otherwise
	 */
	boolean Iterable_isEmpty(const void* iterable);


#endif /* _COMMON_ITERABLE_H */
#ifdef __cplusplus
}
#endif
