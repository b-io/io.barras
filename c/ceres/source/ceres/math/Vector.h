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
#ifdef __cplusplus
extern "C"
{
#endif
#ifndef _VECTOR_H
#define _VECTOR_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include "ceres/Common.h"


	/***********************************************************************************************
	 * CONSTANTS
	 **********************************************************************************************/

	/**
	 * Defines the name of the Vectors.
	 */
#define _VECTOR_NAME				_S("Vector")

	/**
	 * Defines the type of the Vectors.
	 */
#if _32_BITS
#define _VECTOR_TYPE				-268035336L /* 32 bits */
#else
#define _VECTOR_TYPE				7998392945010445330LL /* 64 bits */
#endif


	/***********************************************************************************************
	 * MACROS
	 **********************************************************************************************/

	/**
	 * Checks if the specified Vector is correct.
	 * <p>
	 * @param V the Vector to be checked
	 */
#define _VECTOR_CHECK(V)						((V) && (V)->dimension > 0 || Vector_check(V))

	/**
	 * Checks if the specified Vectors are correct.
	 * <p>
	 * @param V1 the first Vector to be checked
	 * @param V2 the second Vector to be checked
	 */
#define _VECTOR_CHECKS(V1, V2)					((V1) && (V2) && (V1)->dimension == (V2)->dimension && (V1)->dimension > 0 || Vector_checks(V1, V2))


	/***********************************************************************************************
	 * STRUCTURES
	 **********************************************************************************************/

	/**
	 * Defines the Vector.
	 */
	typedef struct Vector
	{
		/*******************************************************************************************
		 * BASIC
		 ******************************************************************************************/

		Core core;

		void (*release)(void* structure);
		void* (*clone)(const void* structure);
		boolean(*equals)(const void* structure, const type type, const void* value);
		integer(*hash)(const void* structure);
		boolean(*toString)(const void* source, string target);

		/*******************************************************************************************
		 * COMPARABLE
		 ******************************************************************************************/

		integer(*compareTo)(const void* structure, const type type, const void* value);

		/*******************************************************************************************
		 * VECTOR
		 ******************************************************************************************/

		natural dimension;
		real* values;

		void (*clear)(struct Vector* vector);
		void (*set)(struct Vector* vector, const real* values, const natural size);
		void (*setFrom)(struct Vector* vector, const natural from, const real* values, const natural size);
		void (*setVector)(struct Vector* first, const struct Vector* second);
		void (*setZero)(struct Vector* vector);

		real(*norm)(const struct Vector* vector);
		struct Vector* (*normalize)(struct Vector* vector);
		struct Vector* (*scale)(struct Vector* vector, const real number);

		struct Vector* (*plus)(struct Vector* first, const struct Vector* second);
		struct Vector* (*minus)(struct Vector* first, const struct Vector* second);
		struct Vector* (*times)(struct Vector* first, const struct Vector* second);

		real(*dot)(const struct Vector* first, const struct Vector* second);
		struct Vector* (*cross)(const struct Vector* first, const struct Vector* second, struct Vector* result);

		struct Vector* (*rotate)(const struct Vector* vector, const struct Vector* axis, const real angle, struct Vector* result);

		real(*angle)(const struct Vector* first, const struct Vector* second);
	} Vector;


	/***********************************************************************************************
	 * CHECK
	 **********************************************************************************************/

	/**
	 * Returns {@code _TRUE} if the specified Vector is not {@code NULL} and its
	 * dimension is greater than zero, {@code _FALSE} otherwise.
	 * <p>
	 * @param vector  the Vector to be checked
	 * <p>
	 * @return {@code _TRUE} if the specified Vector is not {@code NULL} and its
	 *         dimension is greater than zero, {@code _FALSE} otherwise
	 */
	boolean Vector_check(const Vector* vector);

	/**
	 * Returns {@code _TRUE} if the specified Vectors are not {@code NULL} and
	 * their dimensions are equal (and greater than zero), {@code _FALSE}
	 * otherwise.
	 * <p>
	 * @param first  the first Vector to be checked
	 * @param second the second Vector to be checked
	 * <p>
	 * @return {@code _TRUE} if the specified Vectors are not {@code NULL} and
	 *         their dimensions are equal (and greater than zero),
	 *         {@code _FALSE} otherwise
	 */
	boolean Vector_checks(const Vector* first, const Vector* second);


	/***********************************************************************************************
	 * CONSTRUCT
	 **********************************************************************************************/

	/**
	 * Constructs a Vector dynamically.
	 * <p>
	 * @param dimension the dimension of the Vector to be constructed
	 * <p>
	 * @return the dynamically constructed Vector
	 */
	Vector* Vector_new(const natural dimension);

	/**********************************************************************************************/

	/**
	 * Constructs a Vector from {@code startingPoint} to {@code endingPoint}
	 * dynamically.
	 * <p>
	 * @param startingPoint the starting point of the Vector to be constructed
	 * @param endingPoint   the ending point of the Vector to be constructed
	 * <p>
	 * @return the dynamically constructed Vector
	 */
	Vector* Vector_from_to_new(const Vector* startingPoint, const Vector* endingPoint);


	/***********************************************************************************************
	 * RESET
	 **********************************************************************************************/

	/**
	 * Resets the specified Vector.
	 * <p>
	 * @param vector    the Vector to be reset
	 * @param dimension the dimension to set
	 */
	void Vector_reset(void* vector, const natural dimension);


	/***********************************************************************************************
	 * VECTOR
	 **********************************************************************************************/

	/**
	 * Resets the specified Vector to the origin.
	 * <p>
	 * @param vector the Vector to be cleared
	 */
	void Vector_clear(Vector* vector);

	/**
	 * Sets the specified Vector with the specified values.
	 * <p>
	 * @param vector the Vector to be set
	 * @param values the values to set
	 * @param size   the number of values to set
	 */
	void Vector_set(Vector* vector, const real* values, const natural size);

	/**
	 * Sets the specified Vector with the specified values from the specified
	 * index.
	 * <p>
	 * @param vector the Vector to be set
	 * @param from   the index from which to start the assignment
	 * @param values the values to set
	 * @param size   the number of values to set
	 */
	void Vector_set_from(Vector* vector, const natural from, const real* values, const natural size);

	/**
	 * Sets the values of {@code first} to the values of {@code second}.
	 * <p>
	 * @param first  the Vector to be set
	 * @param second the Vector to set
	 */
	void Vector_set_Vector(Vector* first, const Vector* second);

	/**
	 * Sets the values of the specified Vector to zero.
	 * <p>
	 * @param vector the Vector to be set
	 */
	void Vector_set_zero(Vector* vector);

	/**********************************************************************************************/

	/**
	 * Returns the norm of the specified Vector.
	 * <p>
	 * @param vector the Vector
	 * <p>
	 * @return the norm of the specified Vector
	 */
	real Vector_norm(const Vector* vector);

	/**
	 * Normalize the specified Vector.
	 * <p>
	 * @param vector the Vector to be normalized
	 * <p>
	 * @return the resulting Vector if there is no error, {@code NULL} otherwise
	 */
	Vector* Vector_normalize(Vector* vector);

	/**
	 * Scale the specified Vector.
	 * <p>
	 * @param vector      the Vector to be scaled
	 * @param scaleFactor the scale factor
	 * <p>
	 * @return the resulting Vector if there is no error, {@code NULL} otherwise
	 */
	Vector* Vector_scale(Vector* vector, const real scaleFactor);

	/**********************************************************************************************/

	/**
	 * Add {@code second} to {@code first}.
	 * <p>
	 * @param first  the first Vector
	 * @param second the second Vector
	 * <p>
	 * @return the resulting Vector if there is no error, {@code NULL} otherwise
	 */
	Vector* Vector_plus(Vector* first, const Vector* second);

	/**
	 * Subtract {@code second} to {@code first}.
	 * <p>
	 * @param first  the first Vector
	 * @param second the second Vector
	 * <p>
	 * @return the resulting Vector if there is no error, {@code NULL} otherwise
	 */
	Vector* Vector_minus(Vector* first, const Vector* second);

	/**
	 * Multiply {@code first} by {@code second}.
	 * <p>
	 * @param first  the first Vector
	 * @param second the second Vector
	 * <p>
	 * @return the resulting Vector if there is no error, {@code NULL} otherwise
	 */
	Vector* Vector_times(Vector* first, const Vector* second);

	/**********************************************************************************************/

	/**
	 * Returns the dot value between {@code first} and {@code second}.
	 * <p>
	 * @param first  the first Vector
	 * @param second the second Vector
	 * <p>
	 * @return the dot value between {@code first} and {@code second}
	 */
	real Vector_dot(const Vector* first, const Vector* second);

	/**
	 * Computes the cross value between {@code first} and {@code second} and
	 * stores the result in {@code result}.
	 * <p>
	 * @param first  the first Vector
	 * @param second the second Vector
	 * @param result       the output Vector
	 * <p>
	 * @return the resulting Vector if there is no error, {@code NULL} otherwise
	 */
	Vector* Vector_cross(const Vector* first, const Vector* second, Vector* result);

	/**********************************************************************************************/

	/**
	 * Rotates {@code vector} around {@code axis} of {@code angle} radians and
	 * stores the result in {@code result}.
	 * <p>
	 * @param vector the Vector to be rotated
	 * @param axis   the axis of the rotation
	 * @param angle  the angle of the rotation
	 * @param result the output Vector
	 * <p>
	 * @return the resulting Vector if there is no error, {@code NULL} otherwise
	 */
	Vector* Vector_rotate(const Vector* vector, const Vector* axis, const real angle, Vector* result);

	/**********************************************************************************************/

	/**
	 * Returns the angle between {@code first} and {@code second}.
	 * <p>
	 * @param first  the first Vector
	 * @param second the second Vector
	 * <p>
	 * @return the angle between {@code first} and {@code second}
	 */
	real Vector_angle(const Vector* first, const Vector* second);

	/**
	 * Returns the oriented angle between the first face defined by
	 * {@code firstPoint} and {@code firstNormal} and the second face defined by
	 * {@code secondPoint} and {@code secondNormal}.
	 * <p>
	 * @param firstPoint   a point of the first face
	 * @param firstNormal  the normal of the first face
	 * @param secondPoint  a point of the second face
	 * @param secondNormal the normal of the second face
	 * <p>
	 * @return the oriented angle between the first face defined by
	 *         {@code firstPoint} and {@code firstNormal} and the second face
	 *         defined by {@code secondPoint} and {@code secondNormal}
	 */
	real Vector_oriented_angle(const Vector* firstPoint, Vector* firstNormal, const Vector* secondPoint, Vector* secondNormal);


	/***********************************************************************************************
	 * COMPARABLE
	 **********************************************************************************************/

	/**
	 * Constructs a Comparable.
	 * <p>
	 * @return the statically constructed Comparable
	 */
	Comparable Vector_create_Comparable(void);

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
	integer Vector_compare_to(const void* structure, const type type, const void* value);


	/***********************************************************************************************
	 * BASIC
	 **********************************************************************************************/

	/**
	 * Releases the specified structure and frees it if requested.
	 * <p>
	 * @param structure the structure to be released
	 */
	void Vector_release(void* structure);

	/**********************************************************************************************/

	/**
	 * Constructs a dynamic copy of the specified structure.
	 * <p>
	 * @param structure the structure to be cloned
	 * <p>
	 * @return a dynamic copy of the specified structure
	 */
	void* Vector_clone(const void* structure);

	/**********************************************************************************************/

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
	boolean Vector_equals(const void* structure, const type type, const void* value);

	/**********************************************************************************************/

	/**
	 * Returns the hash code of the specified structure.
	 * <p>
	 * @param structure the structure to be hashed
	 * <p>
	 * @return the hash code of the specified structure
	 */
	integer Vector_hash(const void* structure);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean Vector_to_string(const void* source, string target);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean Vector_append_to_string(const void* source, string target);


#endif /* _VECTOR_H */
#ifdef __cplusplus
}
#endif
