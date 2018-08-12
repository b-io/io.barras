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
#ifndef _STATISTICS_H
#define _STATISTICS_H


	/***************************************************************************
	 * INCLUDES
	 **************************************************************************/

#include "ceres/Common.h"


	/***************************************************************************
	 * MACROS
	 **************************************************************************/

#define _ARRAY_COMPUTE(SIZE, COMPUTE, CONSTANT, E)		{ real _result = 0.; natural _i; for (_i = 0; _i < SIZE; ++_i, ++E) { COMPUTE(&_result, CONSTANT, (real) *E); } return _result; }


	/***************************************************************************
	 * COMPUTE
	 **************************************************************************/

	/**
	 * Iterates over the specified values, performs calculations on them using
	 * the specified function and returns the result.
	 * <p>
	 * @param values   the array of digits
	 * @param size     the size of the array of digits
	 * @param constant the constant to be used
	 * @param compute  the calculation to be performed for each value
	 * <p>
	 * @return the result
	 */
	real digits_compute(const digit* values, const natural size, const real constant, void (*compute)(real*, const real, const real));

	/**
	 * Iterates over the specified values, performs calculations on them using
	 * the specified function and returns the result.
	 * <p>
	 * @param values   the array of natural numbers
	 * @param size     the size of the array of natural numbers
	 * @param constant the constant to be used
	 * @param compute  the calculation to be performed for each value
	 * <p>
	 * @return the result
	 */
	real naturals_compute(const natural* values, const natural size, const real constant, void (*compute)(real*, const real, const real));

	/**
	 * Iterates over the specified values, performs calculations on them using
	 * the specified function and returns the result.
	 * <p>
	 * @param values   the array of integers
	 * @param size     the size of the array of integers
	 * @param constant the constant to be used
	 * @param compute  the calculation to be performed for each value
	 * <p>
	 * @return the result
	 */
	real integers_compute(const integer* values, const natural size, const real constant, void (*compute)(real*, const real, const real));

	/**
	 * Iterates over the specified values, performs calculations on them using
	 * the specified function and returns the result.
	 * <p>
	 * @param values   the array of real numbers
	 * @param size     the size of the array of real numbers
	 * @param constant the constant to be used
	 * @param compute  the calculation to be performed for each value
	 * <p>
	 * @return the result
	 */
	real reals_compute(const real* values, const natural size, const real constant, void (*compute)(real*, const real, const real));

	/**
	 * Iterates over the specified values, performs calculations on them using
	 * the specified function and returns the result.
	 * <p>
	 * @param values   the array of Numbers
	 * @param size     the size of the array of Numbers
	 * @param constant the constant to be used
	 * @param compute  the calculation to be performed for each value
	 * <p>
	 * @return the result
	 */
	real Numbers_compute(const Number* values, const natural size, const real constant, void (*compute)(real*, const real, const real));

	/**
	 * Iterates over the specified Array of values, performs calculations on
	 * them using the specified function and returns the result.
	 * <p>
	 * @param array    the Array of values
	 * @param constant the constant to be used
	 * @param compute  the calculation to be performed for each value
	 * <p>
	 * @return the result
	 */
	real Array_compute(const Array* array, const real constant, void (*compute)(real*, const real, const real));


	/***************************************************************************
	 * CALCULATIONS
	 **************************************************************************/

	/**
	 * Stores the sum of the specified values in the specified variable.
	 * <p>
	 * @param result the variable in which the result will be stored
	 * @param first  the first value to be considered in the sum
	 * @param second the second value to be considered in the sum
	 */
	void element_sum(real* result, const real first, const real second);

	/**
	 * Stores the sum of the squared differences of the specified values in the
	 * specified variable.
	 * <p>
	 * @param result the variable in which the result will be stored
	 * @param first  the first value to be considered in the sum
	 * @param second the second value to be considered in the sum
	 */
	void element_sum_of_squared_differences(real* result, const real first, const real second);


	/***************************************************************************
	 * MEAN
	 **************************************************************************/

	/**
	 * Returns the mean of the values of the specified Array.
	 * <p>
	 * @param array the Array of values
	 * <p>
	 * @return the mean of the values of the specified Array
	 */
	real Array_mean(const Array* array);


	/***************************************************************************
	 * VARIANCE & STANDARD DEVIATION
	 **************************************************************************/

	/**
	 * Returns the variance of the values of the specified Array with the
	 * specified mean.
	 * <p>
	 * @param array the Array of values
	 * @param mean  the mean of the values
	 * <p>
	 * @return the variance of the values of the specified Array with the
	 *         specified mean
	 */
	real Array_variance(const Array* array, const real mean);

	/**
	 * Returns the standard deviation of the values of the specified Array with
	 * the specified mean.
	 * <p>
	 * @param array the Array of values
	 * @param mean  the mean of the values
	 * <p>
	 * @return the standard deviation of the values of the specified Array with
	 *         the specified mean
	 */
	real Array_standard_deviation(const Array* array, const real mean);

	/******************************************************************************/

	/**
	 * Returns the sample variance of the values of the specified Array with the
	 * specified sample mean.
	 * <p>
	 * @param array the Array of values
	 * @param mean  the sample mean of the values
	 * <p>
	 * @return the sample variance of the values of the specified Array with the
	 *         specified sample mean
	 */
	real Array_sample_variance(const Array* array, const real mean);

	/**
	 * Returns the sample standard deviation of the values of the specified
	 * Array with the specified sample mean.
	 * <p>
	 * @param array the Array of values
	 * @param mean  the sample mean of the values
	 * <p>
	 * @return the sample standard deviation of the values of the specified
	 *         Array with the specified sample mean
	 */
	real Array_sample_standard_deviation(const Array* array, const real mean);


#endif /* _STATISTICS_H */
#ifdef __cplusplus
}
#endif
