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
 * Cipher handling the following asymmetric transformations:
 *
 * - method/ECB/PKCS1Padding
 * - method/ECB/OAEPWithSHA-1AndMGF1Padding
 * - method/ECB/OAEPWithSHA-256AndMGF1Padding
 *
 * Note: without extension the possible public key sizes are 1024 and 2048 bits.
 */
case class ACrypto(method: CipherMethod, mode: CipherMode, padding: CipherPadding)
	extends Crypto(method, mode, padding) {

	////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////

	var publicKeySize: Int = 0 // [bit]
	var privateKey: PrivateKey = null
	var publicKey: PublicKey = null


	////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////

	def this(method: CipherMethod, mode: CipherMode) = this(method, mode, OAEPWithSHA256AndMGF1Padding)
	def this(method: CipherMethod) = this(method, ECB)
	def this() = this(RSA)


	////////////////////////////////////////////////////////////////////////////
	// KEYS
	////////////////////////////////////////////////////////////////////////////

	override def getDefaultKeySize(): Int = if (checkKeySize(4096)) 4096 else 2024
	override def getKeySize(): Int = publicKeySize

	override def setDefaultKeySize(): Unit = publicKeySize = getDefaultKeySize()
	override def setKeySize(n: Int): Unit = publicKeySize = n

	override def generateKey(): Unit = generateKey(getDefaultKeySize())
	override def generateKey(size: Int): Unit = {
		setKeySize(size)
		val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(method)
		keyPairGenerator.initialize(publicKeySize)
		val keyPair: KeyPair = keyPairGenerator.generateKeyPair()
		privateKey = keyPair.getPrivate()
		publicKey = keyPair.getPublic()
	}


	////////////////////////////////////////////////////////////////////////////
	// ENCRYPT / DECRYPT
	////////////////////////////////////////////////////////////////////////////

	override def getEncryptCipher(): Cipher = {
		val cipher: Cipher = Cipher.getInstance(getParameters())
		if (publicKeySize == 0) generateKey()
		cipher.init(Cipher.ENCRYPT_MODE, publicKey)
		cipher
	}

	override def getDecryptCipher(): Cipher = {
		val cipher: Cipher = Cipher.getInstance(getParameters())
		cipher.init(Cipher.DECRYPT_MODE, privateKey)
		cipher
	}


	////////////////////////////////////////////////////////////////////////////
	// COMBINE / UNCOMBINE KEYS
	////////////////////////////////////////////////////////////////////////////

	override def combine(): Array[Byte] = publicKey.getEncoded()
	override def uncombine(combination: Array[Byte]): Unit = {
		publicKeySize = combination.length * Bytes.SIZE // [bit]
		publicKey = getPublicKey(combination)
	}


	////////////////////////////////////////////////////////////////////////////
	// SPECIFIC METHODS
	////////////////////////////////////////////////////////////////////////////

	def getPrivateKey(): Array[Byte] = if (privateKey != null) privateKey.getEncoded() else null
	def getPublicKey(): Array[Byte] = if (publicKey != null) publicKey.getEncoded() else null

	def getPrivateKey(key: Array[Byte]): PrivateKey = KeyFactory.getInstance(method).generatePrivate(new PKCS8EncodedKeySpec(key))
	def getPublicKey(key: Array[Byte]): PublicKey = KeyFactory.getInstance(method).generatePublic(new X509EncodedKeySpec(key))

	def setPrivateKey(key: Array[Byte]) = privateKey = getPrivateKey(key)
	def setPublicKey(key: Array[Byte]) = publicKey = getPublicKey(key)
}
