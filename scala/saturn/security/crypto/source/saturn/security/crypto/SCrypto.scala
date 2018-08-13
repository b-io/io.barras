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
package saturn.security.crypto

import java.security._
import java.security.interfaces._
import java.security.spec._

import javax.crypto._
import javax.crypto.spec._

import saturn.common.util.Bytes

import CipherMethod._
import CipherMode._
import CipherPadding._

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

	////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////

	var secretKeySize: Int = 0 // bits
	var secretKey: SecretKey = null
	var iv: IvParameterSpec = null


	////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////

	def this(method: CipherMethod, mode: CipherMode) = this(method, mode, PKCS5Padding)
	def this(method: CipherMethod) = this(method, CBC)
	def this() = this(AES)


	////////////////////////////////////////////////////////////////////////////
	// KEYS
	////////////////////////////////////////////////////////////////////////////

	override def getDefaultKeySize(): Int = method match {
		case AES => if (checkKeySize(256)) 256 else 128
		case DES => 56
		case DESede => 168
	}
	override def getKeySize(): Int = secretKeySize

	override def setDefaultKeySize(): Unit = secretKeySize = getDefaultKeySize()
	override def setKeySize(n: Int): Unit = secretKeySize = n

	override def generateKey(): Unit = generateKey(getDefaultKeySize())
	override def generateKey(size: Int): Unit = {
		setKeySize(size)
		val keyGenerator = KeyGenerator.getInstance(method)
		keyGenerator.init(secretKeySize)
		secretKey = keyGenerator.generateKey()
	}


	////////////////////////////////////////////////////////////////////////////
	// ENCRYPT / DECRYPT
	////////////////////////////////////////////////////////////////////////////

	override def getEncryptCipher(): Cipher = {
		val cipher: Cipher = Cipher.getInstance(getParameters())
		if (secretKeySize == 0) generateKey()
		cipher.init(Cipher.ENCRYPT_MODE, secretKey)
		if (CBC.equals(mode)) iv = new IvParameterSpec(cipher.getIV())
		cipher
	}

	override def getDecryptCipher(): Cipher = {
		val cipher: Cipher = Cipher.getInstance(getParameters())
		mode match {
			case ECB => cipher.init(Cipher.DECRYPT_MODE, secretKey)
			case CBC => cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
		}
		cipher
	}


	////////////////////////////////////////////////////////////////////////////
	// COMBINE / UNCOMBINE KEYS
	////////////////////////////////////////////////////////////////////////////

	override def combine(): Array[Byte] = mode match {
		// Secret key
		case ECB => secretKey.getEncoded()
		// Secret key + IV
		case CBC => secretKey.getEncoded() ++ iv.getIV()
	}
	override def uncombine(combination: Array[Byte]): Unit = mode match {
		// Secret key
		case ECB => {
			secretKeySize = combination.length * Bytes.SIZE // bits
			secretKey = getSecretKey(combination)
		}
		// Secret key + IV
		case CBC => {
			if (secretKeySize == 0) setDefaultKeySize()
			// - Get the secret key
			val secretKeyLength: Int = secretKeySize / Bytes.SIZE // bytes
			val secretKeyBytes = new Array[Byte](secretKeyLength)
			Array.copy(combination, 0, secretKeyBytes, 0, secretKeyLength)
			secretKey = getSecretKey(secretKeyBytes)
			// - Get the initialization vector
			val ivLength = combination.length - secretKeyLength // bytes
			val ivBytes = new Array[Byte](ivLength)
			Array.copy(combination, secretKeyLength, ivBytes, 0, ivLength)
			iv = getIV(ivBytes)
		}
	}


	////////////////////////////////////////////////////////////////////////////
	// SPECIFIC METHODS
	////////////////////////////////////////////////////////////////////////////

	def getSecretKey(): Array[Byte] = secretKey.getEncoded()
	def getIV(): Array[Byte] = iv.getIV()

	def getSecretKey(key: Array[Byte]): SecretKey = new SecretKeySpec(key, method)
	def getIV(iv: Array[Byte]): IvParameterSpec = new IvParameterSpec(iv)
}
