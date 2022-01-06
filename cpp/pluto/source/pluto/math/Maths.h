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
#ifndef _MATHS_H
#define _MATHS_H


////////////////////////////////////////////////////////////////////////////////////////////////////
// INCLUDES
////////////////////////////////////////////////////////////////////////////////////////////////////

#include <cmath>

#include "pluto/util/Arguments.h"


////////////////////////////////////////////////////////////////////////////////////////////////////
// USING
////////////////////////////////////////////////////////////////////////////////////////////////////

using namespace std;


////////////////////////////////////////////////////////////////////////////////////////////////////
// CLASS
////////////////////////////////////////////////////////////////////////////////////////////////////

class Maths
{
	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	static constexpr double TOLERANCE = 1E-16;
	static constexpr double DEFAULT_CONFIDENCE = 0.975; //< 97.5%
	static constexpr double DEFAULT_Z = 1.9599639845400536; //< 97.5%
	static constexpr double SQUARE_ROOT_OF_2 = sqrt(2.);
	static constexpr double E = 2850325. / 1048576. + 8.254840070411028747E-8; //< Euler's number
	static constexpr double SQUARE_ROOT_OF_E = sqrt(E);
	static constexpr double PI = 105414357. / 33554432. + 1.984187159361080883E-9;
	static constexpr double SQUARE_ROOT_OF_PI = sqrt(PI);
	static constexpr double SQUARE_ROOT_OF_2_PI = sqrt(2. * PI);
	static constexpr double DEGREE_TO_RADIAN = PI / 180.;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

protected:

	Maths();
	virtual ~Maths();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// BASIC FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	static double getDecimalPart(const double number)
	{
		return number - floor(number);
	}

	static int floorToInt(const double number)
	{
		return (int) floor(number);
	}

	static long floorToLong(const double number)
	{
		return (long) floor(number);
	}

	static int ceilToInt(const double number)
	{
		return (int) ceil(number);
	}

	static long ceilToLong(const double number)
	{
		return (long) ceil(number);
	}

	static int roundToInt(const double number)
	{
		return (int) round(number);
	}

	static long roundToLong(const double number)
	{
		return (long) round(number);
	}

	static double gamma(const double z)
	{
		const double tmp1 = sqrt(2. * PI / z);
		double tmp2 = z + 1. / (12. * z - 1. / (10. * z));
		tmp2 = pow(tmp2 / E, z);
		return tmp1 * tmp2;
	}

	static long factorial(const double n)
	{
		return roundToLong(n * gamma(n));
	}

	static long getGCD(const long a, const long b)
	{
		long i = a, j = b, t;
		while (j != 0)
		{
			t = j;
			j = i % t;
			i = t;
		}
		return i;
	}

	static long getLCM(const long a, const long b)
	{
		return a * b / getGCD(a, b);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ALGORITHMIC
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	static long countPoints(const double min, const double max, const double increment)
	{
		return (min <= max) ? 1L + floorToLong((max - min) / increment) : 0L;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GEOMETRY
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	static double haversin(const double angle)
	{
		return (1. - cos(angle)) / 2.;
	}
};


#else
class Maths;
#endif // _MATHS_H
