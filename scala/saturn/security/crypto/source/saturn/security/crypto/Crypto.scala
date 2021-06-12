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
package saturn.security.crypto

import javax.crypto._

object CipherMethod extends Enumeration {
	type CipherMethod = String
	val AES = "AES"
	val DES = "DES"
	val DESede = "DESede"
	val RSA = "RSA"
}

object CipherMode extends Enumeration {
	type CipherMode = String
	val CBC = "CBC"
	val ECB = "ECB"
}

object CipherPadding extends Enumeration {
	type CipherPadding = String
	val NoPadding = "NoPadding"
	val PKCS1Padding = "PKCS1Padding"
	val PKCS5Padding = "PKCS5Padding"
	val OAEPWithSHA1AndMGF1Padding = "OAEPWithSHA-1AndMGF1Padding"
	val OAEPWithSHA256AndMGF1Padding = "OAEPWithSHA-256AndMGF1Padding"
}

import saturn.security.crypto.CipherMethod._
import saturn.security.crypto.CipherMode._
import saturn.security.crypto.CipherPadding._

/**
 * Cipher handling the following transformations:
 *
 * - AES/CBC/NoPadding (128)
 * - AES/CBC/PKCS5Padding (128)
 * - AES/ECB/NoPadding (128)
 * - AES/ECB/PKCS5Padding (128)
 * - DES/CBC/NoPadding (56)
 * - DES/CBC/PKCS5Padding (56)
 * - DES/ECB/NoPadding (56)
 * - DES/ECB/PKCS5Padding (56)
 * - DESede/CBC/NoPadding (168)
 * - DESede/CBC/PKCS5Padding (168)
 * - DESede/ECB/NoPadding (168)
 * - DESede/ECB/PKCS5Padding (168)
 * - RSA/ECB/PKCS1Padding (1024, 2048)
 *
 * Non supported transformations:
 *
 * - RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
 * - RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)
 */
abstract class Crypto(method: CipherMethod, mode: CipherMode, padding: CipherPadding) {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// PARAMETERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	def getParameters(): String = method + "/" + mode + "/" + padding

	def getMethod(): CipherMethod = method

	def getMode(): CipherMode = mode

	def getPadding(): CipherPadding = padding


	////////////////////////////////////////////////////////////////////////////////////////////////
	// KEYS
	////////////////////////////////////////////////////////////////////////////////////////////////

	def checkKeySize(size: Int): Boolean = (size <= Cipher.getMaxAllowedKeyLength(method))

	def getDefaultKeySize(): Int

	def getKeySize(): Int

	def setDefaultKeySize(): Unit

	def setKeySize(n: Int): Unit

	def generateKey(): Unit

	def generateKey(size: Int): Unit


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENCRYPT / DECRYPT
	////////////////////////////////////////////////////////////////////////////////////////////////

	def getEncryptCipher(): Cipher

	def getDecryptCipher(): Cipher

	def encrypt(text: Array[Byte]): Array[Byte] = getEncryptCipher().doFinal(text)

	def decrypt(text: Array[Byte]): Array[Byte] = getDecryptCipher().doFinal(text)


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMBINE / UNCOMBINE KEYS
	////////////////////////////////////////////////////////////////////////////////////////////////

	def combine(): Array[Byte]

	def uncombine(combination: Array[Byte]): Unit
}
