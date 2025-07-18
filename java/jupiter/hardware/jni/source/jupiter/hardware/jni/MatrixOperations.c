/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2025 Florian Barras <https://barras.io> (florian@barras.io)
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

#include "jupiter_hardware_jni_MatrixOperations.h"


/***************************************************************************************************
 * OPERATORS
 **************************************************************************************************/

JNIEXPORT void JNICALL Java_jupiter_hardware_jni_MatrixOperations_test(JNIEnv* env, jobject obj)
{
	printf("[INFO] Java_jupiter_hardware_jni_MatrixOperations_test\n");
}

JNIEXPORT jdoubleArray JNICALL Java_jupiter_hardware_jni_MatrixOperations_multiply(JNIEnv* env,
    jobject obj, jdoubleArray A, jdoubleArray B, jint aColumnDimension, jint bColumnDimension)
{
    /* Validate the inputs */
    if (A == NULL || B == NULL) {
        jclass exceptionClass = (*env)->FindClass(env, "java/lang/NullPointerException");
        if (exceptionClass != NULL) {
            (*env)->ThrowNew(env, exceptionClass, "Input matrices cannot be null");
        }
        return NULL;
    }

    /* Initialize the multiplication buffers */
    jsize aLength = (*env)->GetArrayLength(env, A);
    jsize bLength = (*env)->GetArrayLength(env, B);
    jsize aRowDimension = aLength / aColumnDimension;
    jsize bRowDimension = bLength / bColumnDimension;
    if (aColumnDimension != bRowDimension) {
        jclass exceptionClass = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
        if (exceptionClass != NULL) {
            (*env)->ThrowNew(env, exceptionClass, "Matrix dimensions do not match for multiplication");
        }
        return NULL;
    }
    jsize resultDimension = aRowDimension * bColumnDimension;
    jboolean isCopyA, isCopyB;
    jdouble* aBuffer = (*env)->GetDoubleArrayElements(env, A, &isCopyA);
    jdouble* bBuffer = (*env)->GetDoubleArrayElements(env, B, &isCopyB);

    /* Allocate the result array */
    jdoubleArray result = (*env)->NewDoubleArray(env, resultDimension);
    if (result == NULL) {
        (*env)->ReleaseDoubleArrayElements(env, A, aBuffer, JNI_ABORT);
        (*env)->ReleaseDoubleArrayElements(env, B, bBuffer, JNI_ABORT);

        jclass exceptionClass = (*env)->FindClass(env, "java/lang/OutOfMemoryError");
        if (exceptionClass != NULL) {
            (*env)->ThrowNew(env, exceptionClass, "Failed to allocate result array");
        }
        return NULL;
    }
    jdouble* resultBuffer = (*env)->GetDoubleArrayElements(env, result, NULL);

    /* Set the block size for tiling */
    const int BLOCK_SIZE = 64;

    /* Perform the blocked matrix multiplication */
    for (int iBlock = 0; iBlock < aRowDimension; iBlock += BLOCK_SIZE) {
        for (int jBlock = 0; jBlock < bColumnDimension; jBlock += BLOCK_SIZE) {
            for (int kBlock = 0; kBlock < aColumnDimension; kBlock += BLOCK_SIZE) {
                /* Process each block */
                for (int i = iBlock; i < iBlock + BLOCK_SIZE && i < aRowDimension; ++i) {
                    for (int j = jBlock; j < jBlock + BLOCK_SIZE && j < bColumnDimension; ++j) {
                        jdouble sum = 0.0;
                        for (int k = kBlock; k < kBlock + BLOCK_SIZE && k < aColumnDimension; ++k) {
                            sum += aBuffer[i * aColumnDimension + k] * bBuffer[k * bColumnDimension + j];
                        }
                        resultBuffer[i * bColumnDimension + j] += sum;
                    }
                }
            }
        }
    }

    /* Release the memory */
    (*env)->ReleaseDoubleArrayElements(env, A, aBuffer, JNI_ABORT);
    (*env)->ReleaseDoubleArrayElements(env, B, bBuffer, JNI_ABORT);
    (*env)->ReleaseDoubleArrayElements(env, result, resultBuffer, 0);

    return result;
}
