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


/***************************************************************************************************
 * INCLUDES
 **************************************************************************************************/

#include "ceres/math/Vector.h"


/***************************************************************************************************
 * CHECK
 **************************************************************************************************/

boolean Vector_check(const Vector* vector)
{
	if (_CHECK(vector, _VECTOR_NAME))
	{
		if (vector->dimension > 0)
		{
			if (vector->values != NULL)
			{
				return _TRUE;
			}
			else
			{
				_PRINT_ERROR_ELEMENT_NULL(_S("array of values"), _VECTOR_NAME);
			}
		}
		else
		{
			_PRINT_ERROR(_S("The dimension of the specified Vector is equal to zero"));
		}
	}
	return _FALSE;
}

boolean Vector_checks(const Vector* first, const Vector* second)
{
	if (_VECTOR_CHECK(first) && _VECTOR_CHECK(second))
	{
		if (first->dimension == second->dimension)
		{
			return _TRUE;
		}
		else
		{
			_PRINT_ERROR(_S("The dimensions of the specified Vectors do not match"));
		}
	}
	return _FALSE;
}


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

Vector* Vector_new(const natural dimension)
{
	Vector* v = _NEW(Vector);

	_PRINT_TEST(_S("<newVector"));
	if (v != NULL)
	{
		v->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		v->values = NULL;
		Vector_reset(v, dimension);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_VECTOR_NAME);
	}
	_PRINT_TEST(_S("</newVector"));
	return v;
}

/**************************************************************************************************/

Vector* Vector_from_to_new(const Vector* startingPoint, const Vector* endingPoint)
{
	_IF (_VECTOR_CHECKS(startingPoint, endingPoint))
	{
		Vector* v = Vector_new(startingPoint->dimension);

		if (v != NULL)
		{
			_IF (_VECTOR_CHECKS(startingPoint, endingPoint))
			{
				v->setZero(v);
				v->plus(v, endingPoint)->minus(v, startingPoint);
			}
		}
		return v;
	}
	return NULL;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void Vector_reset(void* vector, const natural dimension)
{
	_PRINT_TEST(_S("<resetVector>"));
	_IF (_CHECK(vector, _VECTOR_NAME))
	{
		/* Get the Vector*/
		Vector* v = (Vector*) vector;

		/* FUNCTIONS */
		/* - Vector*/
		v->clear = Vector_clear;
		v->set = Vector_set;
		v->setFrom = Vector_set_from;
		v->setVector = Vector_set_Vector;
		v->setZero = Vector_set_zero;
		v->norm = Vector_norm;
		v->normalize = Vector_normalize;
		v->scale = Vector_scale;
		v->plus = Vector_plus;
		v->minus = Vector_minus;
		v->times = Vector_times;
		v->dot = Vector_dot;
		v->cross = Vector_cross;
		v->rotate = Vector_rotate;
		v->angle = Vector_angle;
		/* - Comparable */
		v->compareTo = Vector_compare_to;
		/* - Basic */
		v->release = Vector_release;
		v->toString = Vector_to_string;

		/* ATTRIBUTES */
		/* Free the values */
		_FREE(v->values);
		v->dimension = 0;
		/* Allocate memory for the values */
		v->values = _ARRAY_NEW(dimension, REAL_SIZE);
		if (v->values != NULL)
		{
			v->dimension = dimension;
		}
		else
		{
			_PRINT_ERROR_ARRAY_ALLOCATION(_REAL_TYPE);
		}
	}
	_PRINT_TEST(_S("</resetVector>"));
}


/***************************************************************************************************
 * VECTOR
 **************************************************************************************************/

void Vector_clear(Vector* vector)
{
	_IF (_VECTOR_CHECK(vector))
	{
		/* Declare the iteration variable(s) */
		natural i;

		for (i = 0; i < vector->dimension; ++i)
		{
			vector->values[i] = 0.;
		}
	}
}

void Vector_set(Vector* vector, const real* values, const natural size)
{
	_IF (_VECTOR_CHECK(vector))
	{
		if (_ARRAY_CHECK(values, size, _REALS_NAME))
		{
			/* Declare the iteration variable(s) */
			natural i;
			/* Get the minimum length */
			const natural length = _MIN(vector->dimension, size);

			for (i = 0; i < length; ++i)
			{
				vector->values[i] = values[i];
			}
		}
	}
}

void Vector_set_from(Vector* vector, const natural from, const real* values, const natural size)
{
	_IF (_VECTOR_CHECK(vector))
	{
		if (_ARRAY_CHECK(values, size, _REALS_NAME))
		{
			/* Declare the iteration variable(s) */
			natural i, j;

			for (i = from, j = 0; i < vector->dimension && j < size; ++i, ++j)
			{
				vector->values[i] = values[j];
			}
		}
	}
}

void Vector_set_Vector(Vector* first, const Vector* second)
{
	_IF (_VECTOR_CHECKS(first, second))
	{
		/* Declare the iteration variable(s) */
		natural i;
		/* Get the minimum length */
		const natural length = _MIN(first->dimension, second->dimension);

		for (i = 0; i < length; ++i)
		{
			first->values[i] = second->values[i];
		}
	}
}

void Vector_set_zero(Vector* vector)
{
	_IF (_VECTOR_CHECK(vector))
	{
		/* Declare the iteration variable(s) */
		natural i;

		for (i = 0; i < vector->dimension; ++i)
		{
			vector->values[i] = 0.;
		}
	}
}

/**************************************************************************************************/

real Vector_norm(const Vector* vector)
{
	_IF (_VECTOR_CHECK(vector))
	{
		real norm = 0.;
		/* Declare the iteration variable(s) */
		natural i;

		for (i = 0; i < vector->dimension; ++i)
		{
			norm += square(vector->values[i]);
		}
		return sqrt(norm);
	}
	return 0.;
}

Vector* Vector_normalize(Vector* vector)
{
	_IF (_VECTOR_CHECK(vector))
	{
		const real norm = vector->norm(vector);

		if (norm != 0.)
		{
			vector->scale(vector, 1. / norm);
		}
		return vector;
	}
	return NULL;
}

Vector* Vector_scale(Vector* vector, const real scaleFactor)
{
	_IF (_VECTOR_CHECK(vector))
	{
		/* Declare the iteration variable(s) */
		natural i;

		for (i = 0; i < vector->dimension; ++i)
		{
			vector->values[i] *= scaleFactor;
		}
		return vector;
	}
	return NULL;
}

/**************************************************************************************************/

Vector* Vector_plus(Vector* first, const Vector* second)
{
	_IF (_VECTOR_CHECKS(first, second))
	{
		/* Declare the iteration variable(s) */
		natural i;

		for (i = 0; i < first->dimension; ++i)
		{
			first->values[i] += second->values[i];
		}
		return first;
	}
	return NULL;
}

Vector* Vector_minus(Vector* first, const Vector* second)
{
	_IF (_VECTOR_CHECKS(first, second))
	{
		/* Declare the iteration variable(s) */
		natural i;

		for (i = 0; i < first->dimension; ++i)
		{
			first->values[i] -= second->values[i];
		}
		return first;
	}
	return NULL;
}

Vector* Vector_times(Vector* first, const Vector* second)
{
	_IF (_VECTOR_CHECKS(first, second))
	{
		/* Declare the iteration variable(s) */
		natural i;

		for (i = 0; i < first->dimension; ++i)
		{
			first->values[i] *= second->values[i];
		}
		return first;
	}
	return NULL;
}

/**************************************************************************************************/

real Vector_dot(const Vector* first, const Vector* second)
{
	_IF (_VECTOR_CHECKS(first, second))
	{
		real result = 0.;
		/* Declare the iteration variable(s) */
		natural i;

		for (i = 0; i < first->dimension; ++i)
		{
			result += first->values[i] * second->values[i];
		}
		return result;
	}
	return 0.;
}

Vector* Vector_cross(const Vector* first, const Vector* second, Vector* result)
{
	_IF (_VECTOR_CHECK(first) && _VECTOR_CHECK(second) && _VECTOR_CHECK(result))
	{
		if (first->dimension == 3 && second->dimension == 3 && result->dimension == 3)
		{
			result->values[0] = first->values[1] * second->values[2] - first->values[2] * second->values[1];
			result->values[1] = first->values[2] * second->values[0] - first->values[0] * second->values[2];
			result->values[2] = first->values[0] * second->values[1] - first->values[1] * second->values[0];
			return result;
		}
		else
		{
			_PRINT_ERROR(_S("The specified Vectors are not tridimensional"));
		}
	}
	return NULL;
}

/**************************************************************************************************/

Vector* Vector_rotate(const Vector* vector, const Vector* axis, const real angle, Vector* result)
{
	_IF (_VECTOR_CHECK(vector) && _VECTOR_CHECK(axis) && _VECTOR_CHECK(result))
	{
		if (vector->dimension == 3 && axis->dimension == 3 && result->dimension == 3)
		{
			/* Store the cosinus of the angle */
			const real c = cos(angle);
			/* Construct a temporary Vector */
			Vector* t = Vector_new(3);

			/* Set the result to zero */
			result->setZero(result);
			/* Add cos(angle)*first to the result */
			t->setVector(t, vector);
			t->scale(t, c);
			result->plus(result, t);
			/* Add (1-cos(angle))*(axis.first)*axis to the result */
			t->setVector(t, axis);
			t->scale(t, (1 - c) * axis->dot(axis, vector));
			result->plus(result, t);
			/* Add sin(angle)*(axis x first) to the result */
			axis->cross(axis, vector, t)->scale(t, sin(angle));
			result->plus(result, t);
			/* Release the temporary Vector */
			_RELEASE(t);
			return result;
		}
		else
		{
			_PRINT_ERROR(_S("The specified Vectors are not tridimensional"));
		}
	}
	return NULL;
}

/**************************************************************************************************/

real Vector_angle(const Vector* first, const Vector* second)
{
	_IF (_VECTOR_CHECKS(first, second))
	{
		real angle;
		/* Construct the Vectors */
		Vector* v1 = Vector_new(first->dimension);
		Vector* v2 = Vector_new(second->dimension);
		/* Construct the normal */
		Vector* n = Vector_new(first->dimension);

		/* Copy the Vectors */
		v1->setVector(v1, first);
		v1->normalize(v1);
		v2->setVector(v2, second);
		v2->normalize(v2);
		/* Compute the normal */
		v1->cross(v1, v2, n);
		/* Compute the angle */
		angle = atan2(n->norm(n), v1->dot(v1, v2));
		/* Release the Vectors and the normal */
		_RELEASE(v1);
		_RELEASE(v2);
		_RELEASE(n);
		return angle;
	}
	return 0.;
}

real Vector_oriented_angle(const Vector* firstPoint, Vector* firstNormal, const Vector* secondPoint, Vector* secondNormal)
{
	_IF (_VECTOR_CHECKS(firstPoint, secondPoint)
		&& _VECTOR_CHECKS(firstPoint, firstNormal)
		&& _VECTOR_CHECKS(secondPoint, secondNormal))
	{
		real angle;
		/* Construct the Vectors */
		Vector* v12 = Vector_from_to_new(firstPoint, secondPoint);
		Vector* v21 = Vector_from_to_new(secondPoint, firstPoint);
		/* Get the normals */
		Vector* n1 = firstNormal;
		Vector* n2 = secondNormal;

		/* Normalize the Vectors and the normals */
		v12->normalize(v12);
		v21->normalize(v21);
		n1->normalize(n1);
		n2->normalize(n2);
		/* Compute the oriented angle */
		angle = v12->angle(v12, n1) + v21->angle(v21, n2);
		/* Release the Vectors */
		_RELEASE(v12);
		_RELEASE(v21);
		return angle;
	}
	return 0.;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable Vector_create_Comparable(void)
{
	return Comparable_create(Vector_release, Vector_clone, Vector_equals, Vector_hash, Vector_to_string, Vector_compare_to);
}

integer Vector_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_VECTOR_TYPE, structure, type, value)
		&& type == _VECTOR_TYPE)
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the Vectors */
			const Vector* v1 = (Vector*) structure;
			const Vector* v2 = (Vector*) value;
			/* Declare the iteration variable(s) */
			natural i;
			const real* e1;
			const real* e2;
			/* Get the minimum length */
			const natural length = _MIN(v1->dimension, v2->dimension);

			for (i = 0, e1 = v1->values, e2 = v2->values;
				i < length;
				++i, ++e1, ++e2)
			{
				if (*e1 < *e2)
				{
					return -1;
				}
				else if (*e1 > *e2)
				{
					return 1;
				}
			}
			return _COMPARE_TO(v1->dimension, v2->dimension);
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

void Vector_release(void* structure)
{
	_PRINT_TEST(_S("<releaseVector>"));
	if (structure != NULL)
	{
		/* Get the Vector*/
		Vector* v = (Vector*) structure;

		/* Free the values */
		_FREE(v->values);
		v->dimension = 0;
		/* Free the Vector*/
		if (v->core.isDynamic)
		{
			_FREE(v);
		}
	}
	else
	{
		_PRINT_WARNING_NULL(_VECTOR_NAME);
	}
	_PRINT_TEST(_S("</releaseVector>"));
}

/**************************************************************************************************/

void* Vector_clone(const void* structure)
{
	_IF (_CHECK(structure, _VECTOR_NAME))
	{
		/* Get the Vector */
		const Vector* v = (Vector*) structure;
		/* Construct the clone dynamically */
		Vector* clone = Vector_new(v->dimension);

		clone->setVector(clone, v);
		return clone;
	}
	return NULL;
}

/**************************************************************************************************/

boolean Vector_equals(const void* structure, const type type, const void* value)
{
	return Vector_compare_to(structure, type, value) == 0;
}

/**************************************************************************************************/

integer Vector_hash(const void* structure)
{
	integer code;

	if (structure != NULL)
	{
		/* Get the Vector */
		const Vector* v = (Vector*) structure;
		/* Declare the iteration variable(s) */
		natural i;
		const real* e;
		boolean isLeft = _TRUE;

		code = (integer) _VECTOR_TYPE;
		for (i = 0, e = v->values;
			i < v->dimension;
			++i, ++e)
		{
			if (isLeft)
			{
				code = bits_rotate_left((natural) code, _THIRD_BITS_NUMBER);
			}
			else
			{
				code = bits_rotate_right((natural) code, _EIGHTH_BITS_NUMBER);
			}
			code ^= real_hash(e);
			isLeft = !isLeft;
		}
	}
	else
	{
		code = integer_random();
	}
	return code;
}

/**************************************************************************************************/

boolean Vector_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		boolean isNotFull;
		const Vector* v = (Vector*) source;

		isNotFull = string_to_string(_S("["), target);
		if (isNotFull)
		{
			if (v->dimension > 0)
			{
				/* Declare the iteration variable(s) */
				natural i;

				isNotFull = real_append_to_string(&v->values[0], target);
				for (i = 1; isNotFull && i < v->dimension; ++i)
				{
					isNotFull = string_append_to_string(_S(", "), target)
						&& real_append_to_string(&v->values[i], target);
				}
			}
			isNotFull = isNotFull && string_append_to_string(_S("]"), target);
		}
		return isNotFull;
	}
	return _FALSE;
}

boolean Vector_append_to_string(const void* source, string target)
{
	string buffer;

	Vector_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
