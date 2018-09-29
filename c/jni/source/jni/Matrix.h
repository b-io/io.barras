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
#ifndef _MATRIX_H
#define _MATRIX_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>

/*
#include <immintrin.h>
*/

#include <jni.h>


	/***********************************************************************************************
	 * OPERATORS
	 **********************************************************************************************/

	JNIEXPORT void JNICALL dot(JNIEnv* context, jobject this);

	/*
	JNIEXPORT void JNICALL dot(JNIEnv* jEnv, jobject jObject, jdoubleArray jA, jdoubleArray jB,
		jdoubleArray jC, jint jARowDimension, jint jInnerDimension, jint jBColumnDimension);
	*/


#endif /* _MATRIX_H */
#ifdef __cplusplus
}
#endif
