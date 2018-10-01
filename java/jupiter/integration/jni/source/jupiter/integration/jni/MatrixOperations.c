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

JNIEXPORT jstring JNICALL Java_jupiter_integration_jni_MatrixOperations_hello(JNIEnv* env, jobject obj)
{
	jstring value;
	char buffer[255];

	sprintf(buffer, "%s", "Hello World from MatrixOperations!");
	value = (*env)->NewStringUTF(env, buffer);
	return value;
}

JNIEXPORT jint JNICALL Java_jupiter_integration_jni_MatrixOperations_plus(JNIEnv* env, jobject obj, jint a, jint b)
{
	return a + b;
}
