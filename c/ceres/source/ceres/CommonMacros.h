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
#ifndef _COMMON_MACROS_H
#define _COMMON_MACROS_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include "ceres/CommonConstants.h"


	/***********************************************************************************************
	 * CHECK
	 **********************************************************************************************/

	/**
	 * The {@code if} control statement of the checks.
	 * <p>
	 * @param CONDITION a boolean condition
	 */
#ifdef _CHECK_MODE
#define _IF(CONDITION)						if (CONDITION)
#else
#define _IF(CONDITION)
#endif

	/**
	 * Checks if the specified argument is correct.
	 * <p>
	 * @param ARGUMENT the argument to be checked
	 * @param NAME     the name of the argument to be checked
	 */
#define _CHECK(ARGUMENT, NAME)				((ARGUMENT) || check(ARGUMENT, NAME))

	/**
	 * Checks if the specified arguments are correct.
	 * <p>
	 * @param T1 the type of the first argument to be checked
	 * @param V1 the value of the first argument to be checked
	 * @param T2 the type of the second argument to be checked
	 * @param V2 the value of the second argument to be checked
	 */
#define _CHECKS(T1, V1, T2, V2)				(((V1) && (V2)) || checks(T1, V1, T2, V2))

	/**
	 * Checks if the specified type is correct.
	 * <p>
	 * @param T1 the specified type
	 * @param T2 the expected type
	 */
#define _TYPE_CHECK(T1, T2)					(T1 == T2 || type_check(T1, T2))

	/**
	 * Checks if the specified Structure is correct.
	 * <p>
	 * @param S the Structure to be checked
	 */
#define _STRUCTURE_CHECK(S)					(((S) && (S)->value) || Structure_check(S))

	/**
	 * Checks if the specified Structures are correct.
	 * <p>
	 * @param S1 the first Structure to be checked
	 * @param S2 the second Structure to be checked
	 */
#define _STRUCTURE_CHECKS(S1, S2)			(((S1) && (S2) && (S1)->value && (S2)->value) || Structure_checks(S1, S2))

	/**
	 * Checks if the specified array is correct (and nonempty).
	 * <p>
	 * @param ARRAY the array to be checked
	 * @param SIZE  the size of the array to be checked
	 * @param NAME  the name of the array to be checked
	 */
#define _ARRAY_CHECK(ARRAY, SIZE, NAME)		(_CHECK(ARRAY, NAME) && SIZE > 0 || array_check(ARRAY, SIZE, NAME))

	/**
	 * Checks if the specified source and target are correct.
	 * <p>
	 * @param S the source to be checked
	 * @param T the target to be checked
	 */
#define _SOURCE_TARGET_CHECK(S, T)			(_CHECK(S, _S("source")) && _CHECK(T, _S("target")))

	/**
	 * Checks if the specified source and target array are correct.
	 * <p>
	 * @param S  the source to be checked
	 * @param T  the target array to be checked
	 * @param TS the size of the target array to be checked
	 */
#define _SOURCE_TARGET_ARRAY_CHECK(S, T, TS)	(_CHECK(S, _S("source")) && _ARRAY_CHECK(T, TS, _S("target array")))

	/**
	 * Checks if the specified source and target arrays are correct.
	 * <p>
	 * @param S  the source array to be checked
	 * @param SS the size of the source array to be checked
	 * @param T  the target array to be checked
	 * @param TS the size of the target array to be checked
	 */
#define _SOURCE_TARGET_ARRAYS_CHECK(S, SS, T, TS)	(_ARRAY_CHECK(S, SS, _S("source array")) && _ARRAY_CHECK(T, TS, _S("target array")))

	/**
	 * Returns {@code _TRUE} if the specified array of characters size is
	 * correct, {@code _FALSE} otherwise.
	 * <p>
	 * @param string the array of characters to be checked
	 * @param size   the size of the array of characters to be checked
	 * <p>
	 * @return {@code _TRUE} if the specified array of characters size is
	 *         correct, {@code _FALSE} otherwise
	 */
#define _CHARS_CHECK(STRING, SIZE)			_ARRAY_CHECK(STRING, SIZE, _CHARACTERS_NAME)


	/***********************************************************************************************
	 * DYNAMIC MEMORY ALLOCATION
	 **********************************************************************************************/

#ifdef _TEST_MODE
#define _PRINT_NEW							printn(_S("<new />"))
#define _PRINT_RESIZE						printn(_S("<resize />"))
#define _PRINT_FREE							printn(_S("<free />"))
#else
#define _PRINT_NEW
#define _PRINT_RESIZE
#define _PRINT_FREE
#endif

	/**
	 * Allocates memory space for a structure with the specified type.
	 */
#define _NEW(TYPE)							(TYPE*) calloc(1, sizeof(TYPE)); _PRINT_NEW
	/**
	 * Increases or decreases the memory space of the specified structure.
	 */
#define _RESIZE(STRUCTURE, TYPE)			(TYPE*) realloc(STRUCTURE, sizeof(TYPE)); _PRINT_RESIZE

	/**
	 * Allocates memory space for an array of N elements with the specified type and initializes them to zero.
	 */
#define _ARRAY_NEW(N, ELEMENT_SIZE)			calloc(N, ELEMENT_SIZE); _PRINT_NEW
	/**
	 * Increases or decreases the memory space of the specified array.
	 */
#define _ARRAY_RESIZE(ARRAY, ELEMENT_SIZE, N)	realloc(ARRAY, N * ELEMENT_SIZE); _PRINT_RESIZE

	/**
	 * Frees the allocated memory space of the specified structure.
	 */
#define _FREE(STRUCTURE)					if (STRUCTURE != NULL) { free(STRUCTURE); STRUCTURE = NULL; _PRINT_FREE; }

	/**
	 * Releases the specified structure that implements the abstract layer Releasable.
	 */
#define _RELEASE(RELEASABLE)				if (RELEASABLE != NULL && (RELEASABLE)->release != NULL) { (RELEASABLE)->release(RELEASABLE); }
	/**
	 * Casts the specified structure to Releasable and releases it.
	 */
#define _STRUCTURE_RELEASE(STRUCTURE)		if (STRUCTURE != NULL) { Releasable* _d = (Releasable*) (STRUCTURE); _RELEASE(_d); }

	/**
	 * Prints the error for memory allocation failure.
	 */
#define _PRINT_ERROR_MEMORY_ALLOCATION(NAME)	_PRINT_ERROR(_S("Memory allocation for ") NAME _S(" failed"))
	/**
	 * Prints the error for array allocation failure.
	 */
#define _PRINT_ERROR_ARRAY_ALLOCATION(T)	{ string _message; string_format(_message, _S("Memory allocation for the array of type '%t' failed"), (T)); _PRINT_ERROR(_message); }
	/**
	 * Prints the error for array reallocation failure.
	 */
#define _PRINT_ERROR_ARRAY_REALLOCATION(T)	{ string _message; string_format(_message, _S("Memory reallocation for the array of type '%t' failed"), (T)); _PRINT_ERROR(_message); }


	/***********************************************************************************************
	 * COMMON
	 **********************************************************************************************/

	/**
	 * Defines the wide or normal characters.
	 */
#ifdef _WIDE_STRING
#define _WIDEN(x)							L ## x
#define _C(x)								_WIDEN(x)
#define _S(x)								_WIDEN(x)
#else
#define _C(x)								x
#define _S(x)								x
#endif

	/**
	 * Copies the unknown type string into the specified target.
	 * <p>
	 * @param TARGET the output string
	 * <p>
	 * @return {@code _TRUE} if the specified target is not full,
	 *         {@code _FALSE} otherwise
	 */
#define _UNKNOWN_TO_STRING(TARGET)			{ const string _s = _UNKNOWN_STRING; return string_to_string(&_s, TARGET); }

	/**
	 * Creates the default Structure.
	 */
#define _STRUCTURE_DEFAULT()				Structure_create(_UNKNOWN_TYPE, NULL)

	/**
	 * Get the encapsulated value.
	 * <p>
	 * @param EXPECTED_TYPE the expected type
	 * @param TYPE          the type of the value
	 * @param VALUE         the value
	 */
#define _VALUE_GET(EXPECTED_TYPE, TYPE, VALUE)	((TYPE == EXPECTED_TYPE) ? VALUE : (TYPE == _STRUCTURE_TYPE) ? ((Structure*) VALUE)->value : (TYPE == _OBJECT_TYPE) ? ((Object*) VALUE)->structure.value : NULL)

	/**
	 * Defines the comparison for order between the specified values.
	 * <p>
	 * @param V1 the value to be compared for order
	 * @param V2 the value with which to compare
	 * <p>
	 * @return a negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second
	 */
#define _COMPARE_TO(V1, V2)					((V1 < V2) ? -1 : (V1 > V2) ? 1 : 0)

	/**
	 * Prints the warning/error/failure for {@code NULL}.
	 */
#define _PRINT_WARNING_NULL(NAME)			{ string _message; string_format(_message, _S("The specified %s is %s"), (NAME), (_NULL_STRING)); _PRINT_WARNING(_message); }
#define _PRINT_ERROR_NULL(NAME)				{ string _message; string_format(_message, _S("The specified %s is %s"), (NAME), (_NULL_STRING)); _PRINT_ERROR(_message); }

#define _PRINT_WARNING_ELEMENT_NULL(E, S)	{ string _message; string_format(_message, _S("The %s of the specified %s is %s"), (E), (S), (_NULL_STRING)); _PRINT_WARNING(_message); }
#define _PRINT_ERROR_ELEMENT_NULL(E, S)		{ string _message; string_format(_message, _S("The %s of the specified %s is %s"), (E), (S), (_NULL_STRING)); _PRINT_ERROR(_message); }

	/**
	 * Prints the error for instantiation failure.
	 */
#define _PRINT_ERROR_INSTANTIATION()		{ string _message; string_format(_message, _S("The size of the element to be instantiated is equal to zero")); _PRINT_ERROR(_message); }

	/**
	 * Prints the error for type mismatch.
	 */
#define _PRINT_ERROR_TYPE(T1, T2)			{ string _message; string_format(_message, _S("The specified type '%t' is not the expected type '%t'"), (T1), (T2)); _PRINT_ERROR(_message); }
#define _PRINT_ERROR_NOT_NUMERIC_TYPE(T)	{ string _message; string_format(_message, _S("The specified type '%t' is not a numeric type"), (T)); _PRINT_ERROR(_message); }
#define _PRINT_ERROR_NOT_BASIC_TYPE(T)		{ string _message; string_format(_message, _S("The specified type '%t' is not a basic type"), (T)); _PRINT_ERROR(_message); }
#define _PRINT_ERROR_NOT_ELEMENTARY_TYPE(T)	{ string _message; string_format(_message, _S("The specified type '%t' is not an elementary type"),(T)); _PRINT_ERROR(_message); }

	/**
	 * Prints the warning for type incompatibility.
	 */
#define _PRINT_WARNING_EQUALITY(T1, T2)		{ string _message; string_format(_message, _S("The types '%t' and '%t' are incompatible for equality comparison"), (T1), (T2)); _PRINT_WARNING(_message); }
#define _PRINT_WARNING_COMPARISON(T1, T2)	{ string _message; string_format(_message, _S("The types '%t' and '%t' are incompatible for comparison"), (T1), (T2)); _PRINT_WARNING(_message); }

	/**
	 * Prints the warning for no available function.
	 */
#define _PRINT_WARNING_NO_FUNCTION(NAME, T)	{ string _message; string_format(_message, _S("There is no function %s for the specified type '%t'"), (NAME), (T)); _PRINT_WARNING(_message); }


	/***********************************************************************************************
	 * I/O
	 **********************************************************************************************/

	/**
	 * Writes formatted data to string.
	 */
#ifdef _WIDE_STRING
#define _SPRINTF(STRING, SIZE, FORMAT, VALUE)	swprintf(STRING, SIZE, FORMAT, VALUE)
#else
#if defined(__GNUC__)
#define _SPRINTF(STRING, SIZE, FORMAT, VALUE)	snprintf(STRING, SIZE, FORMAT, VALUE)
#elif defined(_MSC_VER)
#define _SPRINTF(STRING, SIZE, FORMAT, VALUE)	sprintf_s(STRING, SIZE, FORMAT, VALUE)
#else
#define _SPRINTF(STRING, SIZE, FORMAT, VALUE)	sprintf(STRING, FORMAT, VALUE)
#endif
#endif

	/**
	 * Prints a line feed to the specified file.
	 */
#define _PRINT_LINE_FEED(FILE)				char_to_stream(_LINE_FEED, FILE)
	/**
	 * Prints a carriage return to the specified file.
	 */
#define _PRINT_CARRIAGE_RETURN(FILE)		char_to_stream(_CARRIAGE_RETURN, FILE)

	/**
	 * Prints the specified trace/debug/test/info/result message to stdout.
	 */
#ifdef _TRACE_MODE
#define _PRINT_TRACE(CONTENT)				print_trace(_S(__FILE__), _STRING_EMPTY, __LINE__, CONTENT)
#else
#define _PRINT_TRACE(CONTENT)
#endif
#ifdef _DEBUG_MODE
#define _PRINT_DEBUG(CONTENT)				print_debug(_S(__FILE__), _STRING_EMPTY, CONTENT)
#else
#define _PRINT_DEBUG(CONTENT)
#endif
#ifdef _TEST_MODE
#define _PRINT_TEST(CONTENT)				print_test(_S(__FILE__), CONTENT)
#else
#define _PRINT_TEST(CONTENT)
#endif
#define _PRINT_INFO(CONTENT)				print_info(CONTENT)
#define _PRINT_RESULT(CONTENT)				print_result(CONTENT)

	/**
	 * Prints the specified warning/error/failure message to stderr.
	 */
#ifdef _WARN_MODE
#define _PRINT_WARNING(CONTENT)				print_warning(_S(__FILE__), CONTENT)
#else
#define _PRINT_WARNING(CONTENT)
#endif
#define _PRINT_ERROR(CONTENT)				print_error(_S(__FILE__), _STRING_EMPTY, CONTENT)
#define _PRINT_FAILURE(CONTENT)				print_failure(_S(__FILE__), _STRING_EMPTY, __LINE__, CONTENT)

	/**
	 * Prints the error for unknown format specifier.
	 */
#define _PRINT_ERROR_FORMAT(SPECIFIER)		{ string _message; string_format(_message, _S("Unknown format specifier '%c'"), (SPECIFIER)); _PRINT_ERROR(_message); }


	/***********************************************************************************************
	 * ITERABLE
	 **********************************************************************************************/

	/**
	 * Returns the next element of the Iterator.
	 */
#define _ITERATOR_NEXT(ITERATOR, NODE)		{ (ITERATOR)->element.value = NODE; ++(ITERATOR)->index; (ITERATOR)->node = NODE + 1; return (ITERATOR)->element.value; }

	/**
	 * Compares the specified elements for order.
	 */
#define _ELEMENT_COMPARE_TO(E1, E2, ELEMENT_SIZE)	((integer) memcmp(E1, E2, ELEMENT_SIZE))
	/**
	 * Compares the specified elements for equality.
	 */
#define _ELEMENT_EQUALS(E1, E2, ELEMENT_SIZE)	(memcmp(E1, E2, ELEMENT_SIZE) == 0)
	/**
	 * Gets the middle element.
	 */
#define _ELEMENT_GET_MIDDLE(E1, E2)			(E1 + (E2 - E1) / 2)
	/**
	 * Gets the next element.
	 */
#define _ELEMENT_GET_NEXT(E, ELEMENT_SIZE)	((address) E + ELEMENT_SIZE)
	/**
	 * Gets the previous element.
	 */
#define _ELEMENT_GET_PREVIOUS(E, ELEMENT_SIZE)	((address) E - ELEMENT_SIZE)
	/**
	 * Sets the specified element to the specified value.
	 */
#define _ELEMENT_SET(E, ELEMENT_SIZE, VALUE)	memmove(E, VALUE, ELEMENT_SIZE)

	/**
	 * Adds the specified value to the array and gets the next element to be set.
	 */
#define _ARRAY_ADD(E, ELEMENT_SIZE, VALUE)	_ELEMENT_GET_NEXT(_ELEMENT_SET(E, ELEMENT_SIZE, VALUE), ELEMENT_SIZE)
	/**
	 * Adds the specified Structures of the Array and returns {@code _TRUE} if the Array changed as a result of this call, {@code _FALSE} otherwise.
	 */
#define _ARRAY_ADD_STRUCTURES(ARRAY, VALUES)	{ boolean _isModified = _TRUE; Iterator _it = (VALUES)->iterator(VALUES); Structure* _e; while (_it.next(&_it)) { _e = (Structure*) _it.element.value; if (_e->type == (ARRAY)->element.type) { if ((ARRAY)->addStructure(ARRAY, _e)) { _isModified = _TRUE; } } } return _isModified; }
	/**
	 * Adds the specified Objects of the Array and returns {@code _TRUE} if the Array changed as a result of this call, {@code _FALSE} otherwise.
	 */
#define _ARRAY_ADD_OBJECTS(ARRAY, VALUES)	{ boolean _isModified = _TRUE; Iterator _it = (VALUES)->iterator(VALUES); Object* _e; while (_it.next(&_it)) { _e = (Object*) _it.element.value; if (_e->structure.type == (ARRAY)->element.type) { if ((ARRAY)->addStructure(ARRAY, &_e->structure)) { _isModified = _TRUE; } } } return _isModified; }
	/**
	 * Copies the specified source array into the specified target array.
	 */
#define _ARRAY_COPY(SOURCE, LENGTH, ELEMENT_SIZE, TARGET)	memmove(TARGET, SOURCE, LENGTH * ELEMENT_SIZE)
	/**
	 * Returns the number of elements of the specified array that are equal to the specified value.
	 */
#define _ARRAY_COUNT(SIZE, VALUE, E)		{ natural _counter = 0; natural _i; for (_i = 0; _i < SIZE; ++_i, ++E) { if (VALUE == *E) { ++_counter; } } return _counter; }
	/**
	 * Gets the element at the specified index.
	 */
#define _ARRAY_GET(ARRAY, ELEMENT_SIZE, INDEX)		((address) ARRAY + INDEX * ELEMENT_SIZE)
	/**
	 * Returns the first element of the specified array that is equal to the specified value and its index.
	 */
#define _ARRAY_FIND(SIZE, VALUE, E)			{ natural _i; for (_i = 0; _i < SIZE; ++_i, ++E) { if (VALUE == *E) { return Element_create(_i, E); } } }
	/**
	 * Returns the first element of the specified array that is equal to the specified value, {@code NULL} otherwise.
	 */
#define _ARRAY_FIND_ELEMENT(SIZE, VALUE, E)	{ natural _i; for (_i = 0; _i < SIZE; ++_i, ++E) { if (VALUE == *E) { return E; } } }
	/**
	 * Returns the first element of the specified array that is equal to the specified value, {@code NULL} otherwise.
	 */
#define _ARRAY_FIND_INDEX(SIZE, VALUE, E)	{ natural _i; for (_i = 0; _i < SIZE && VALUE != *E; ++_i, ++E); return _i; }
	/**
	 * Inserts the specified element into the array.
	 */
#define _ARRAY_INSERT(LENGTH, ELEMENT_SIZE, INDEX, E)	memmove(_ELEMENT_GET_NEXT(E, ELEMENT_SIZE), E, (LENGTH - INDEX) * ELEMENT_SIZE)
	/**
	 * Removes the specified element from the array.
	 */
#define _ARRAY_REMOVE(LENGTH, ELEMENT_SIZE, INDEX, E)	memmove(E, _ELEMENT_GET_NEXT(E, ELEMENT_SIZE), (LENGTH - INDEX) * ELEMENT_SIZE)
	/**
	 * Sets the element at the specified index to the specified value.
	 */
#define _ARRAY_SET(ARRAY, ELEMENT_SIZE, INDEX, VALUE)	memmove(_ARRAY_GET(array, elementSize, index), value, elementSize)
	/**
	 * Copies the specified array into the specified string.
	 */
#define _ARRAY_TO_STRING(ARRAY, SIZE, ELEMENT_TYPE, APPEND_TO_STRING, STRING) { boolean _isNotFull = string_to_string(_S("("), STRING); natural _i = 0; if (_i < SIZE) { const ELEMENT_TYPE* _e = (ELEMENT_TYPE*) ARRAY; _isNotFull = APPEND_TO_STRING(_e, STRING); ++_i; ++_e; while (_isNotFull && _i < SIZE) { _isNotFull = string_append_to_string(_S(", "), STRING) && APPEND_TO_STRING(_e, STRING); ++_i; ++_e; } } return _isNotFull && string_append_to_string(_S(")"), STRING); }

	/**
	 * Returns {@code _TRUE} if the specified Array contains all of the specified values, {@code _FALSE} otherwise.
	 */
#define _ARRAY_CONTAINS_ALL(ARRAY, VALUES, E)	{ natural _i; for (_i = 0, E = (ARRAY)->elements; _i < (ARRAY)->length; ++_i, ++E) { if ((VALUES)->remove(VALUES, (ARRAY)->element.type, E) && (VALUES)->isEmpty(VALUES)) { _RELEASE(VALUES); return _TRUE; } } }
	/**
	 * Returns the number of the specified values in the specified Array.
	 */
#define _ARRAY_COUNT_ALL(ARRAY, VALUES, E)	{ natural _counter = 0; natural _i; for (_i = 0, E = (ARRAY)->elements; _i < (ARRAY)->length; ++_i, ++E) { if ((VALUES)->contains(VALUES, (ARRAY)->element.type, E)) { ++_counter; } } return _counter; }

	/**
	 * Prints the warning for empty array.
	 */
#define _PRINT_WARNING_EMPTY(NAME)			{ string _message; string_format(_message, _S("The specified %s is empty"), (NAME)); _PRINT_WARNING(_message); }
	/**
	 * Prints the error for wrong array index.
	 */
#define _PRINT_ERROR_INDEX(INDEX, LENGTH)	{ string _message; string_format(_message, _S("The specified index is out of bounds (index: %n, length: %n)"), (INDEX), (LENGTH)); _PRINT_ERROR(_message); }
	/**
	 * Prints the error for wrong array size.
	 */
#define _PRINT_ERROR_SIZE(NAME)				{ string _message; string_format(_message, _S("The size of the specified %s is equal to zero"), (NAME)); _PRINT_ERROR(_message); }

	/**
	 * Checks for full array.
	 */
#define _CHECK_FULL(INDEX, MAX)				{ if (INDEX == MAX) { return _FALSE; } return _TRUE; }
	/**
	 * Checks for truncation.
	 */
#define _CHECK_TRUNCATION(INDEX, MAX, E, NAME)	{ if (INDEX == MAX) { if (*E) { _PRINT_WARNING_TRUNCATION(NAME); } return _FALSE; } return _TRUE; }
	/**
	 * Prints the warning for truncation.
	 */
#define _PRINT_WARNING_TRUNCATION(NAME)		{ string _message; string_format(_message, _S("The target %s is truncated"), (NAME)); _PRINT_WARNING(_message); }


	/***********************************************************************************************
	 * MATH
	 **********************************************************************************************/

	/**
	 * Gets the minimum number.
	 */
#define _MIN(a, b)							((a < b) ? a : b)
	/**
	 * Gets the maximum number.
	 */
#define _MAX(a, b)							((a > b) ? a : b)

	/**
	 * Converts the specified number.
	 */
#define _CONVERT(TYPE, VALUE, TO_TYPE, MIN, MAX)	if (VALUE > (TYPE) MAX) { return MAX; } else if (VALUE < (TYPE) MIN) { return MIN; } else { return (TO_TYPE) number; }

	/**
	 * Prints the error for negative number.
	 */
#define _PRINT_ERROR_NEGATIVE(NAME)			{ string _message; string_format(_message, _S("The %s is negative"), (NAME)); _PRINT_ERROR(_message); }
	/**
	 * Prints the error for less than an integer.
	 */
#define _PRINT_ERROR_LESS_THAN(NAME, LOWER_BOUND)	{ string _message; string_format(_message, _S("The %s is less than %i"), (NAME), (LOWER_BOUND)); _PRINT_ERROR(_message); }
	/**
	 * Prints the error for greater than an integer.
	 */
#define _PRINT_ERROR_GREATER_THAN(NAME, UPPER_BOUND)	{ string _message; string_format(_message, _S("The %s is greater than %i"), (NAME), (UPPER_BOUND)); _PRINT_ERROR(_message); }


	/***********************************************************************************************
	 * TIME
	 **********************************************************************************************/

	/**
	 * Sets the Coordinated Universal Time.
	 */
#if defined(__GNUC__)
#define _GM_TIME(TIME, TIMESTAMP)			*TIME = *gmtime(TIMESTAMP)
#elif defined(_MSC_VER)
#define _GM_TIME(TIME, TIMESTAMP)			gmtime_s(TIME, TIMESTAMP)
#else
#define _GM_TIME(TIME, TIMESTAMP)			*TIME = *gmtime(TIMESTAMP)
#endif

	/**
	 * Sets the local Time.
	 */
#if defined(__GNUC__)
#define _LOCAL_TIME(TIME, TIMESTAMP)		*TIME = *localtime(TIMESTAMP)
#elif defined(_MSC_VER)
#define _LOCAL_TIME(TIME, TIMESTAMP)		localtime_s(TIME, TIMESTAMP)
#else
#define _LOCAL_TIME(TIME, TIMESTAMP)		*TIME = *localtime(TIMESTAMP)
#endif


	/***********************************************************************************************
	 * TYPE
	 **********************************************************************************************/

	/**
	 * Defines the comparison for equality between the real numbers.
	 */
#define _REAL_EQUALS(V1, V2)				((V1 >= V2 - _EPSILON && V1 <= V2 + _EPSILON) ? _TRUE : _FALSE)
	/**
	 * Defines the comparison for order between the real numbers.
	 */
#define _REAL_COMPARE_TO(V1, V2)			((V1 < V2 - _EPSILON) ? -1 : (V1 > V2 + _EPSILON) ? 1 : 0)

	/**
	 * Swaps the specified integers.
	 */
#define _INTEGER_SWAP(A, B)					{ const integer _i = *(A); *(A) = *(B); *(B) = _i; }
	/**
	 * Swaps the specified Objects.
	 */
#define _OBJECT_SWAP(A, B)					{ const Object _o = *(A); *(A) = *(B); *(B) = _o; }


#endif /* _COMMON_MACROS_H */
#ifdef __cplusplus
}
#endif
