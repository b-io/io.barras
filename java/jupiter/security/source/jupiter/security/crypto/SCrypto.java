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
package jupiter.security.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import jupiter.common.exception.IllegalTypeException;
import jupiter.common.util.Bytes;
import jupiter.common.util.Strings;

public class SCrypto
		extends Crypto {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The number of bits of the {@link SecretKey}.
	 */
	public int secretKeySize = 0; // [bit]

	/**
	 * The {@link SecretKey}.
	 */
	public SecretKey secretKey = null;
	/**
	 * The parameters of the initialization vector (IV).
	 */
	public IvParameterSpec iv = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public SCrypto() {
		this(CipherMethod.AES);
	}

	public SCrypto(final CipherMethod method) {
		this(method, CipherMode.CBC);
	}

	public SCrypto(final CipherMethod method, final CipherMode mode) {
		this(method, mode, CipherPadding.PKCS5Padding);
	}

	public SCrypto(final CipherMethod method, final CipherMode mode, final CipherPadding padding) {
		super(method, mode, padding);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int getDefaultKeySize() {
		switch (method) {
			case AES:
				return checkKeySize(256) ? 256 : 128;
			case DES:
				return 56;
			case DESede:
				return 168;
		}
		throw new IllegalTypeException(method);
	}

	@Override
	public int getKeySize() {
		return secretKeySize;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////


	public byte[] getSecretKey() {
		return secretKey.getEncoded();
	}

	public SecretKey getSecretKey(final byte[] key) {
		return new SecretKeySpec(key, method.value);
	}

	//////////////////////////////////////////////

	public byte[] getIV() {
		return iv.getIV();
	}

	public IvParameterSpec getIV(byte[] iv) {
		return new IvParameterSpec(iv);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void setDefaultKeySize() {
		secretKeySize = getDefaultKeySize();
	}

	@Override
	public void setKeySize(final int secretKeySize) {
		this.secretKeySize = secretKeySize;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void generateKey() {
		generateKey(getDefaultKeySize());
	}

	@Override
	public void generateKey(final int size) {
		try {
			setKeySize(size);
			final KeyGenerator keyGenerator = KeyGenerator.getInstance(method.value);
			keyGenerator.init(secretKeySize);
			secretKey = keyGenerator.generateKey();
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public byte[] combine() {
		switch (mode) {
			// Secret key
			case ECB: return secretKey.getEncoded();
			// Secret key + IV
			case CBC: return Bytes.merge(secretKey.getEncoded(), iv.getIV());
			default:
				throw new IllegalTypeException(mode);
		}
	}

	@Override
	public void uncombine(final byte[] combination) {
		switch (mode) {
			// Secret key
			case ECB:
				secretKeySize = combination.length * Byte.SIZE; // [bit]
				secretKey = getSecretKey(combination);
				break;
			// Secret key + IV
			case CBC:
				if (secretKeySize == 0) {
					setDefaultKeySize();
				}
				// - Get the secret key
				final int secretKeyLength = secretKeySize / Byte.SIZE; // [byte]
				final byte[] secretKeyBytes = new byte[secretKeyLength];
				System.arraycopy(combination, 0, secretKeyBytes, 0, secretKeyLength);
				secretKey = getSecretKey(secretKeyBytes);
				// - Get the initialization vector (IV)
				final int ivLength = combination.length - secretKeyLength; // [byte]
				final byte[] ivBytes = new byte[ivLength];
				System.arraycopy(combination, secretKeyLength, ivBytes, 0, ivLength);
				iv = getIV(ivBytes);
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Cipher getEncryptCipher() {
		try {
			final Cipher cipher = Cipher.getInstance(toString());
			if (secretKeySize == 0) {
				generateKey();
			}
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			if (CipherMode.CBC.equals(mode)) {
				iv = new IvParameterSpec(cipher.getIV());

			}
			return cipher;
		} catch (final InvalidKeyException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		} catch (final NoSuchPaddingException ex) {
			throw new IllegalTypeException(padding, ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Cipher getDecryptCipher() {
		try {
			final Cipher cipher = Cipher.getInstance(toString());
			switch (mode) {
				case ECB: cipher.init(Cipher.DECRYPT_MODE, secretKey);
					break;
				case CBC: cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
					break;
				default:
					throw new IllegalTypeException(mode);
			}
			return cipher;
		} catch (final InvalidAlgorithmParameterException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		} catch (final InvalidKeyException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		} catch (final NoSuchPaddingException ex) {
			throw new IllegalTypeException(padding, ex);
		}
	}
}
