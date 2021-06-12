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

import saturn.common.util.Bytes
import saturn.security.crypto.CipherMethod._
import saturn.security.crypto.CipherMode._
import saturn.security.crypto.CipherPadding._

import javax.crypto._
import javax.crypto.spec._

/**
 * Cipher handling the following symmetric transformations:
 *
 * - AES/CBC/NoPadding
 * - AES/CBC/PKCS5Padding
 * - AES/ECB/NoPadding
 * - AES/ECB/PKCS5Padding
 *
 * - DES/CBC/NoPadding
 * - DES/CBC/PKCS5Padding
 * - DES/ECB/NoPadding
 * - DES/ECB/PKCS5Padding
 *
 * - DESede/CBC/NoPadding
 * - DESede/CBC/PKCS5Padding
 * - DESede/ECB/NoPadding
 * - DESede/ECB/PKCS5Padding
 */
case class SCrypto(method: CipherMethod, mode: CipherMode, padding: CipherPadding)
        extends Crypto(method, mode, padding) {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	var secretKeySize: Int = 0 // [bit]
	var secretKey: SecretKey = null
	var iv: IvParameterSpec = null


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	def this(method: CipherMethod, mode: CipherMode) = this(method, mode, PKCS5Padding)

	def this(method: CipherMethod) = this(method, CBC)

	def this() = this(AES)


	////////////////////////////////////////////////////////////////////////////////////////////////
	// KEYS
	////////////////////////////////////////////////////////////////////////////////////////////////

	override def getKeySize(): Int = secretKeySize

	override def getEncryptCipher(): Cipher = {
		val cipher: Cipher = Cipher.getInstance(getParameters())
		if (secretKeySize == 0) generateKey()
		cipher.init(Cipher.ENCRYPT_MODE, secretKey)
		if (CBC.equals(mode)) iv = new IvParameterSpec(cipher.getIV())
		cipher
	}

	override def generateKey(): Unit = generateKey(getDefaultKeySize())

	override def getDefaultKeySize(): Int = method match {
		case AES => if (checkKeySize(256)) 256 else 128
		case DES => 56
		case DESede => 168
	}

	override def generateKey(size: Int): Unit = {
		setKeySize(size)
		val keyGenerator = KeyGenerator.getInstance(method)
		keyGenerator.init(secretKeySize)
		secretKey = keyGenerator.generateKey()
	}

	override def setKeySize(n: Int): Unit = secretKeySize = n


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENCRYPT / DECRYPT
	////////////////////////////////////////////////////////////////////////////////////////////////

	override def getDecryptCipher(): Cipher = {
		val cipher: Cipher = Cipher.getInstance(getParameters())
		mode match {
			case ECB => cipher.init(Cipher.DECRYPT_MODE, secretKey)
			case CBC => cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
		}
		cipher
	}

	override def combine(): Array[Byte] = mode match {
		// Secret key
		case ECB => secretKey.getEncoded()
		// Secret key + IV
		case CBC => secretKey.getEncoded() ++ iv.getIV()
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMBINE / UNCOMBINE KEYS
	////////////////////////////////////////////////////////////////////////////////////////////////

	override def uncombine(combination: Array[Byte]): Unit = mode match {
		// Secret key
		case ECB => {
			secretKeySize = combination.length * Bytes.SIZE // [bit]
			secretKey = getSecretKey(combination)
		}
		// Secret key + IV
		case CBC => {
			if (secretKeySize == 0) setDefaultKeySize()
			// - Get the secret key
			val secretKeyLength: Int = secretKeySize / Bytes.SIZE // [byte]
			val secretKeyBytes = new Array[Byte](secretKeyLength)
			Array.copy(combination, 0, secretKeyBytes, 0, secretKeyLength)
			secretKey = getSecretKey(secretKeyBytes)
			// - Get the initialization vector
			val ivLength = combination.length - secretKeyLength // [byte]
			val ivBytes = new Array[Byte](ivLength)
			Array.copy(combination, secretKeyLength, ivBytes, 0, ivLength)
			iv = getIV(ivBytes)
		}
	}

	override def setDefaultKeySize(): Unit = secretKeySize = getDefaultKeySize()

	////////////////////////////////////////////////////////////////////////////////////////////////

	def getSecretKey(key: Array[Byte]): SecretKey = new SecretKeySpec(key, method)

	def getIV(iv: Array[Byte]): IvParameterSpec = new IvParameterSpec(iv)

	def getSecretKey(): Array[Byte] = secretKey.getEncoded()

	def getIV(): Array[Byte] = iv.getIV()
}
