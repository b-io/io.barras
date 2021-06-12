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
import jupiter.common.util.Objects;
import jupiter.security.crypto.Crypto.CipherMethod;
import jupiter.security.crypto.Crypto.CipherMode;
import jupiter.security.crypto.Crypto.CipherPadding;

public class SCrypto
		extends Crypto {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The number of bits of the {@link SecretKey}.
	 */
	protected int secretKeySize = 0; // [bit]

	/**
	 * The {@link SecretKey}.
	 */
	protected SecretKey secretKey = null;
	/**
	 * The {@link IvParameterSpec} specifying an initialization vector (IV).
	 */
	protected IvParameterSpec iv = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SCrypto} by default.
	 */
	public SCrypto() {
		this(CipherMethod.AES);
	}

	/**
	 * Constructs a {@link SCrypto} with the specified {@link CipherMethod}.
	 * <p>
	 * @param method the {@link CipherMethod}
	 */
	public SCrypto(final CipherMethod method) {
		this(method, CipherMode.CBC);
	}

	/**
	 * Constructs a {@link SCrypto} with the specified {@link CipherMethod} and {@link CipherMode}.
	 * <p>
	 * @param method the {@link CipherMethod}
	 * @param mode   the {@link CipherMode}
	 */
	public SCrypto(final CipherMethod method, final CipherMode mode) {
		this(method, mode, CipherPadding.PKCS5Padding);
	}

	/**
	 * Constructs a {@link SCrypto} with the specified {@link CipherMethod}, {@link CipherMode} and
	 * {@link CipherPadding}.
	 * <p>
	 * @param method  the {@link CipherMethod}
	 * @param mode    the {@link CipherMode}
	 * @param padding the {@link CipherPadding}
	 */
	public SCrypto(final CipherMethod method, final CipherMode mode, final CipherPadding padding) {
		super(method, mode, padding);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the default size of the {@link SecretKey}.
	 * <p>
	 * @return the default size of the {@link SecretKey}
	 */
	@Override
	public int getDefaultKeySize() {
		switch (method) {
			case AES:
				return isValidKeySize(256) ? 256 : 128;
			case DES:
				return 56;
			case DESede:
				return 168;
		}
		throw new IllegalTypeException(method);
	}

	/**
	 * Returns the size of the {@link SecretKey}.
	 * <p>
	 * @return the size of the {@link SecretKey}
	 */
	@Override
	public int getKeySize() {
		return secretKeySize;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the primary encoded key of the {@link SecretKey}, or {@code null} if the
	 * {@link SecretKey} does not support encoding.
	 * <p>
	 * @return the primary encoded key of the {@link SecretKey}, or {@code null} if the
	 *         {@link SecretKey} does not support encoding
	 */
	public byte[] getSecretKey() {
		return secretKey.getEncoded();
	}

	/**
	 * Returns the primary encoded initialization vector of the {@link IvParameterSpec}.
	 * <p>
	 * @return the primary encoded initialization vector of the {@link IvParameterSpec}
	 */
	public byte[] getIV() {
		return iv.getIV();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the default size of the {@link SecretKey}.
	 */
	@Override
	public void setDefaultKeySize() {
		secretKeySize = getDefaultKeySize();
	}

	/**
	 * Sets the size of the {@link SecretKey}.
	 * <p>
	 * @param secretKeySize a size of the {@link SecretKey}
	 */
	@Override
	public void setKeySize(final int secretKeySize) {
		this.secretKeySize = secretKeySize;
	}

	//////////////////////////////////////////////

	/**
	 * Sets the {@link SecretKey} with the specified primary encoded key.
	 * <p>
	 * @param secretKey a primary encoded key
	 */
	public void setSecretKey(final byte[] secretKey) {
		this.secretKey = createSecretKey(secretKey);
	}

	/**
	 * Sets the {@link IvParameterSpec} with the specified primary encoded initialization vector.
	 * <p>
	 * @param iv a primary encoded initialization vector
	 */
	public void setIV(final byte[] iv) {
		this.iv = createIV(iv);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Combines the primary encoded data.
	 * <p>
	 * @return the primary encoded combination
	 */
	@Override
	public byte[] combine() {
		switch (mode) {
			// Secret key
			case ECB:
				return secretKey.getEncoded();
			// Secret key + IV
			case CBC:
				return Bytes.concat(secretKey.getEncoded(), iv.getIV());
			default:
				throw new IllegalTypeException(mode);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Uncombines the specified primary encoded combination.
	 * <p>
	 * @param combination the primary encoded combination to uncombine
	 */
	@Override
	public void uncombine(final byte[] combination) {
		switch (mode) {
			// Secret key
			case ECB:
				secretKeySize = combination.length * Byte.SIZE; // [bit]
				secretKey = createSecretKey(combination);
				break;
			// Secret key + IV
			case CBC:
				if (secretKeySize == 0) {
					setDefaultKeySize();
				}
				// • Create the secret key
				final int secretKeyLength = secretKeySize / Byte.SIZE; // [byte]
				final byte[] secretKeyBytes = new byte[secretKeyLength];
				System.arraycopy(combination, 0, secretKeyBytes, 0, secretKeyLength);
				secretKey = createSecretKey(secretKeyBytes);
				// • Create the initialization vector
				final int ivLength = combination.length - secretKeyLength; // [byte]
				final byte[] ivBytes = new byte[ivLength];
				System.arraycopy(combination, secretKeyLength, ivBytes, 0, ivLength);
				iv = createIV(ivBytes);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link SecretKey} of the specified size.
	 * <p>
	 * @param secretKeySize the size of the {@link SecretKey} to create
	 */
	@Override
	public void createKey(final int secretKeySize) {
		try {
			setKeySize(secretKeySize);
			final KeyGenerator keyGenerator = KeyGenerator.getInstance(method.value);
			keyGenerator.init(secretKeySize);
			secretKey = keyGenerator.generateKey();
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Creates a {@link SecretKey} with the specified primary encoded key.
	 * <p>
	 * @param secretKey the primary encoded key of the {@link SecretKey} to create
	 * <p>
	 * @return a {@link SecretKey} with the specified primary encoded key
	 */
	public SecretKey createSecretKey(final byte[] secretKey) {
		return new SecretKeySpec(secretKey, method.value);
	}

	/**
	 * Creates an {@link IvParameterSpec} with the specified primary encoded initialization vector.
	 * <p>
	 * @param iv the primary encoded initialization vector of the {@link IvParameterSpec} to create
	 * <p>
	 * @return an {@link IvParameterSpec} with the specified primary encoded initialization vector
	 */
	public IvParameterSpec createIV(final byte[] iv) {
		return new IvParameterSpec(iv);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an encrypting {@link Cipher}.
	 * <p>
	 * @return an encrypting {@link Cipher}
	 */
	@Override
	public Cipher createEncryptingCipher() {
		try {
			final Cipher cipher = Cipher.getInstance(toString());
			if (secretKeySize == 0) {
				createKey();
			}
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			if (CipherMode.CBC.equals(mode)) {
				iv = new IvParameterSpec(cipher.getIV());

			}
			return cipher;
		} catch (final InvalidKeyException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		} catch (final NoSuchPaddingException ex) {
			throw new IllegalTypeException(padding, ex);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Creates a decrypting {@link Cipher}.
	 * <p>
	 * @return a decrypting {@link Cipher}
	 */
	@Override
	public Cipher createDecryptingCipher() {
		try {
			final Cipher cipher = Cipher.getInstance(toString());
			switch (mode) {
				case ECB:
					cipher.init(Cipher.DECRYPT_MODE, secretKey);
					break;
				case CBC:
					cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
					break;
				default:
					throw new IllegalTypeException(mode);
			}
			return cipher;
		} catch (final InvalidAlgorithmParameterException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		} catch (final InvalidKeyException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		} catch (final NoSuchPaddingException ex) {
			throw new IllegalTypeException(padding, ex);
		}
	}
}
