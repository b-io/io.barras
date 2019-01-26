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

#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>

#include "jupiter_integration_jni_MatrixOperations.h"


/***************************************************************************************************
 * OPERATORS
 **************************************************************************************************/

JNIEXPORT jdoubleArray JNICALL Java_jupiter_integration_jni_MatrixOperations_dot(JNIEnv* env, jobject obj,
	jdoubleArray A, jdoubleArray B, jint aColumnDimension, jint bColumnDimension)
{
	/* Initialize */
	jsize aLength = (*env)->GetArrayLength(env, A);
	jsize aRowDimension = aLength / aColumnDimension;
	jsize resultDimension = aRowDimension * bColumnDimension;
	jboolean aCopy, bCopy, resultCopy;
	jdouble* aBuffer = (*env)->GetDoubleArrayElements(env, A, &aCopy);
	jdouble* bBuffer = (*env)->GetDoubleArrayElements(env, B, &bCopy);
	jdoubleArray result = (*env)->NewDoubleArray(env, resultDimension);
	jdouble* resultBuffer = (*env)->GetDoubleArrayElements(env, result, &resultCopy);

	/* Process */
	for (int e = 0; e < resultDimension; ++e)
	{
		jdouble sum = 0.;
		jint rowOffset = e / bColumnDimension;
		jint columnOffset = e % bColumnDimension;
		for (int i = 0; i < aColumnDimension; ++i)
		{
			sum += aBuffer[rowOffset * aColumnDimension + i] *
				bBuffer[i * bColumnDimension + columnOffset];
		}
		resultBuffer[e] = sum;
	}

	/* Release the memory */
	if (aCopy == JNI_TRUE)
	{
		(*env)->ReleaseDoubleArrayElements(env, A, aBuffer, JNI_ABORT);
	}
	if (bCopy == JNI_TRUE)
	{
		(*env)->ReleaseDoubleArrayElements(env, B, bBuffer, JNI_ABORT);
	}
	if (resultCopy == JNI_TRUE)
	{
		(*env)->ReleaseDoubleArrayElements(env, result, resultBuffer, 0);
	}
	return result;
}
