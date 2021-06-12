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
/***************************************************************************************************
 * INCLUDES
 **************************************************************************************************/

#include "ceres/math/CommonMath.h"


/***************************************************************************************************
 * CONSTANTS
 **************************************************************************************************/

const real RAD_TO_DEG = 180. / (real) _PI;
const real DEG_TO_RAD = (real) _PI / 180.;


/***************************************************************************************************
 * ABSOLUTE VALUE
 **************************************************************************************************/

integer integer_abs(const integer number)
{
	return (number < 0) ? -number : number;
}

real real_abs(const real number)
{
	return (number < 0.) ? -number : number;
}


/***************************************************************************************************
 * CONVERSIONS
 **************************************************************************************************/

digit natural_to_digit(const natural number)
{
	_CONVERT(natural, number, digit, _DIGIT_MIN, _DIGIT_MAX);
}

/**************************************************************************************************/


digit integer_to_digit(const integer number)
{
	_CONVERT(integer, number, digit, _DIGIT_MIN, _DIGIT_MAX);
}

natural integer_to_natural(const integer number)
{
	_CONVERT(integer, number, natural, _NATURAL_MIN, _NATURAL_MAX);
}

/**************************************************************************************************/

digit real_to_digit(const real number)
{
	_CONVERT(real, number, digit, _DIGIT_MIN, _DIGIT_MAX);
}

natural real_to_natural(const real number)
{
	_CONVERT(real, number, natural, _NATURAL_MIN, _NATURAL_MAX);
}

integer real_to_integer(const real number)
{
	_CONVERT(real, number, integer, _INTEGER_MIN, _INTEGER_MAX);
}


/***************************************************************************************************
 * OPERATIONSS
 **************************************************************************************************/

natural floor_to_natural(const real number)
{
	return real_to_natural(floor(number));
}

integer floor_to_integer(const real number)
{
	return real_to_integer(floor(number));
}

/**************************************************************************************************/

natural ceil_to_natural(const real number)
{
	return real_to_natural(ceil(number));
}

integer ceil_to_integer(const real number)
{
	return real_to_integer(ceil(number));
}

/**************************************************************************************************/

natural round_to_natural(const real number)
{
	return real_to_natural(number + 0.5);
}

integer round_to_integer(const real number)
{
	return real_to_integer(number + 0.5);
}


/***************************************************************************************************
 * RANDOM
 **************************************************************************************************/

real real_rand_one(void)
{
	return (real) rand() / ((real) RAND_MAX + 1.);
}

natural natural_rand(const natural from, const natural to)
{
	return real_to_natural((real) from + real_rand_one() * ((real) to - (real) from));
}

integer integer_rand(const integer from, const integer to)
{
	return real_to_integer((real) from + real_rand_one() * ((real) to - (real) from));
}

real real_rand(const real from, const real to)
{
	return from + (real_rand_one() * (to - from));
}

/**************************************************************************************************/

real real_rand_one_inclusive(void)
{
	return (real) rand() / (real) RAND_MAX;
}

natural natural_rand_inclusive(const natural from, const natural to)
{
	return real_to_natural((real) from + real_rand_one_inclusive() * ((real) to - (real) from));
}

integer integer_rand_inclusive(const integer from, const integer to)
{
	return real_to_integer((real) from + real_rand_one_inclusive() * ((real) to - (real) from));
}

real real_rand_inclusive(const real from, const real to)
{
	return from + (real_rand_one_inclusive() * (to - from));
}

/**************************************************************************************************/

natural natural_random(void)
{
	return natural_rand_inclusive(_NATURAL_MIN, _NATURAL_MAX);
}

integer integer_random(void)
{
	return integer_rand_inclusive(_INTEGER_MIN, _INTEGER_MAX);
}


/***************************************************************************************************
 * POWER
 **************************************************************************************************/

real square(const real number)
{
	return pow(number, 2.);
}


/***************************************************************************************************
 * ROOT
 **************************************************************************************************/

real root(const natural degree, const real radicand)
{
	if (radicand >= 0.)
	{
		return pow(radicand, 1. / (real) degree);
	}
	else
	{
		_PRINT_ERROR_NEGATIVE(_S("specified radicand"));
	}
	return 0.;
}

real square_root(const real radicand)
{
	if (radicand >= 0.)
	{
		return pow(radicand, 0.5);
	}
	else
	{
		_PRINT_ERROR_NEGATIVE(_S("specified radicand"));
	}
	return 0.;
}
