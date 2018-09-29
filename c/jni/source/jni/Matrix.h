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

#include <immintrin.h>

#include <jni.h>


	/***********************************************************************************************
	 * FUNCTIONS
	 **********************************************************************************************/

	JNIEXPORT void JNICALL dot(JNIEnv* jEnv, jobject jObject) {
		printf("Hello World!\n");
	}

	/*
	JNIEXPORT void JNICALL dot(JNIEnv* jEnv, jobject jObject, jdoubleArray jA, jdoubleArray jB,
		jdoubleArray jC, jint jARowDimension, jint jInnerDimension, jint jBColumnDimension)
	{
		// Initialize
		jboolean isCopy;
		double* A = (double*) (*jEnv)->GetPrimitiveArrayCritical(jEnv, jA, &isCopy);
		double* B = (double*) (*jEnv)->GetPrimitiveArrayCritical(jEnv, jB, &isCopy);
		double* C = (double*) (*jEnv)->GetPrimitiveArrayCritical(jEnv, jC, &isCopy);
		const int aRowDimension = (int) jARowDimension;
		const int innerDimension = (int) jInnerDimension;
		const int bColumnDimension = (int) jBColumnDimension;

		// Process
		for (int i = 0; i < aRowDimension; ++i) {
			const int aRowOffset = i * innerDimension;
			for (int j = 0; j < bColumnDimension; ++j) {
				double sum = 0.;
				for (int k = 0; k < innerDimension; ++k) {
					sum += A[aRowOffset + k] * B[k * bColumnDimension + j];
				}
				C[i * bColumnDimension + j] = sum;
			}
		}

		// Release
		(*jEnv)->ReleasePrimitiveArrayCritical(jEnv, jA, (jbyte *) A, 0);
		(*jEnv)->ReleasePrimitiveArrayCritical(jEnv, jB, (jbyte *) B, 0);
		(*jEnv)->ReleasePrimitiveArrayCritical(jEnv, jC, (jbyte *) C, 0);
	}
	*/


#endif /* _MATRIX_H */
#ifdef __cplusplus
}
#endif
