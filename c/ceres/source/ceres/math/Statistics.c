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
/***************************************************************************************************
 * INCLUDES
 **************************************************************************************************/

#include "ceres/math/Statistics.h"


/***************************************************************************************************
 * COMPUTE
 **************************************************************************************************/

real digits_compute(const digit* values, const natural size, const real constant, void (*compute)(real*, const real, const real))
{
	if (_ARRAY_CHECK(values, size, _DIGITS_NAME))
	{
		/* Declare the iteration variable(s) */
		const digit* v = values;

		_ARRAY_COMPUTE(size, compute, constant, v);
	}
	return 0.;
}

real naturals_compute(const natural* values, const natural size, const real constant, void (*compute)(real*, const real, const real))
{
	if (_ARRAY_CHECK(values, size, _NATURALS_NAME))
	{
		/* Declare the iteration variable(s) */
		const natural* v = values;

		_ARRAY_COMPUTE(size, compute, constant, v);
	}
	return 0.;
}

real integers_compute(const integer* values, const natural size, const real constant, void (*compute)(real*, const real, const real))
{
	if (_ARRAY_CHECK(values, size, _INTEGERS_NAME))
	{
		/* Declare the iteration variable(s) */
		const integer* v = values;

		_ARRAY_COMPUTE(size, compute, constant, v);
	}
	return 0.;
}

real reals_compute(const real* values, const natural size, const real constant, void (*compute)(real*, const real, const real))
{
	if (_ARRAY_CHECK(values, size, _REALS_NAME))
	{
		/* Declare the iteration variable(s) */
		const real* v = values;

		_ARRAY_COMPUTE(size, compute, constant, v);
	}
	return 0.;
}

real Numbers_compute(const Number* values, const natural size, const real constant, void (*compute)(real*, const real, const real))
{
	if (_ARRAY_CHECK(values, size, _NUMBERS_NAME))
	{
		real result = 0.;
		/* Declare the iteration variable(s) */
		const Number* v = values;
		natural i;

		for (i = 0; i < size; ++i, ++v)
		{
			compute(&result, constant, v->toDecimal(v));
		}
		return result;
	}
	return 0.;
}

real Array_compute(const Array* array, const real constant, void (*compute)(real*, const real, const real))
{
	_IF (_CHECK(array, _ARRAY_NAME) && array->size > 0)
	{
		switch (array->element.type)
		{
			case _DIGIT_TYPE:
				return digits_compute(array->elements, array->length, constant, compute);
			case _NATURAL_TYPE:
				return naturals_compute(array->elements, array->length, constant, compute);
			case _INTEGER_TYPE:
				return integers_compute(array->elements, array->length, constant, compute);
			case _REAL_TYPE:
				return reals_compute(array->elements, array->length, constant, compute);
			case _NUMBER_TYPE:
				return Numbers_compute(array->elements, array->length, constant, compute);
			default:
				_PRINT_ERROR_NOT_NUMERIC_TYPE(array->element.type);
		}
	}
	return 0.;
}


/***************************************************************************************************
 * CALCULATIONS
 **************************************************************************************************/

void element_sum(real* result, const real first, const real second)
{
	*result += first + second;
}

void element_sum_of_squared_differences(real* result, const real first, const real second)
{
	*result += square(first - second);
}


/***************************************************************************************************
 * MEAN
 **************************************************************************************************/

real Array_mean(const Array* array)
{
	_IF (_CHECK(array, _ARRAY_NAME) && array->size > 0)
	{
		return Array_compute(array, 0., element_sum) / (real) array->length;
	}
	return 0.;
}


/***************************************************************************************************
 * VARIANCE & STANDARD DEVIATION
 **************************************************************************************************/

real Array_variance(const Array* array, const real mean)
{
	_IF (_CHECK(array, _ARRAY_NAME) && array->size > 0)
	{
		return Array_compute(array, mean, element_sum_of_squared_differences) / (real) array->length;
	}
	return 0.;
}

real Array_standard_deviation(const Array* array, const real mean)
{
	return square_root(Array_variance(array, mean));
}

/**************************************************************************************************/

real Array_sample_variance(const Array* array, const real mean)
{
	_IF (_CHECK(array, _ARRAY_NAME) && array->size > 0)
	{
		if (array->length > 1)
		{
			return Array_compute(array, mean, element_sum_of_squared_differences) / ((real) array->length - 1.);
		}
		else
		{
			_PRINT_ERROR_LESS_THAN(_S("length of the specified Array"), 2);
		}
	}
	return 0.;
}

real Array_sample_standard_deviation(const Array* array, const real mean)
{
	return square_root(Array_sample_variance(array, mean));
}
