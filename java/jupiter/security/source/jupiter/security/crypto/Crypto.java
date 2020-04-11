/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2020 Florian Barras <https://barras.io> (florian@barras.io)
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

import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import jupiter.common.exception.IllegalTypeException;

public abstract class Crypto {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link CipherMethod}.
	 */
	protected final CipherMethod method;
	/**
	 * The {@link CipherMethod}.
	 */
	protected final CipherMode mode;
	/**
	 * The {@link CipherMethod}.
	 */
	protected final CipherPadding padding;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Crypto} with the specified {@link CipherMethod}, {@link CipherMode} and
	 * {@link CipherPadding}.
	 * <p>
	 * @param method  the {@link CipherMethod}
	 * @param mode    the {@link CipherMode}
	 * @param padding the {@link CipherPadding}
	 */
	public Crypto(final CipherMethod method, final CipherMode mode, final CipherPadding padding) {
		this.method = method;
		this.mode = mode;
		this.padding = padding;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link CipherMethod}.
	 * <p>
	 * @return the {@link CipherMethod}
	 */
	public CipherMethod getMethod() {
		return method;
	}

	/**
	 * Returns the {@link CipherMode}.
	 * <p>
	 * @return the {@link CipherMode}
	 */
	public CipherMode getMode() {
		return mode;
	}

	/**
	 * Returns the {@link CipherPadding}.
	 * <p>
	 * @return the {@link CipherPadding}
	 */
	public CipherPadding getPadding() {
		return padding;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the default key size.
	 * <p>
	 * @return the default key size
	 */
	public abstract int getDefaultKeySize();

	/**
	 * Returns the key size.
	 * <p>
	 * @return the key size
	 */
	public abstract int getKeySize();

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the default key size.
	 */
	public abstract void setDefaultKeySize();

	/**
	 * Sets the key size.
	 * <p>
	 * @param keySize a key size
	 */
	public abstract void setKeySize(final int keySize);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Combines the primary encoded data.
	 * <p>
	 * @return the primary encoded combination
	 */
	public abstract byte[] combine();

	//////////////////////////////////////////////

	/**
	 * Uncombines the specified primary encoded combination.
	 * <p>
	 * @param combination the primary encoded combination to uncombine
	 */
	public abstract void uncombine(final byte[] combination);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a key by default.
	 */
	public void createKey() {
		createKey(getDefaultKeySize());
	}

	/**
	 * Creates a key of the specified size.
	 * <p>
	 * @param keySize the size of the key to create
	 */
	public abstract void createKey(final int keySize);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an encrypting {@link Cipher}.
	 * <p>
	 * @return an encrypting {@link Cipher}
	 */
	public abstract Cipher createEncryptingCipher();

	//////////////////////////////////////////////

	/**
	 * Creates a decrypting {@link Cipher}.
	 * <p>
	 * @return a decrypting {@link Cipher}
	 */
	public abstract Cipher createDecryptingCipher();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Encrypts the specified {@code byte} array.
	 * <p>
	 * @param bytes the {@code byte} array to encrypt
	 * <p>
	 * @return the encrypted {@code byte} array
	 * <p>
	 * @throws BadPaddingException       if there is a problem with the padding
	 * @throws IllegalBlockSizeException if there is a problem with the block size
	 */
	public byte[] encrypt(final byte[] bytes)
			throws BadPaddingException, IllegalBlockSizeException {
		return createEncryptingCipher().doFinal(bytes);
	}

	/**
	 * Decrypts the specified {@code byte} array.
	 * <p>
	 * @param bytes the {@code byte} array to decrypt
	 * <p>
	 * @return the decrypted {@code byte} array
	 * <p>
	 * @throws BadPaddingException       if there is a problem with the padding
	 * @throws IllegalBlockSizeException if there is a problem with the block size
	 */
	public byte[] decrypt(final byte[] bytes)
			throws BadPaddingException, IllegalBlockSizeException {
		return createDecryptingCipher().doFinal(bytes);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the specified key size is valid, {@code false} otherwise.
	 * <p>
	 * @param keySize the key size to test
	 * <p>
	 * @return {@code true} if the specified key size is valid, {@code false} otherwise
	 */
	public boolean isValidKeySize(final int keySize) {
		try {
			return keySize <= Cipher.getMaxAllowedKeyLength(method.value);
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return method + "/" + mode + "/" + padding;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum CipherMethod {
		AES("AES"),
		DES("DES"),
		DESede("DESede"),
		RSA("RSA");

		public final String value;

		private CipherMethod(final String value) {
			this.value = value;
		}

		/**
		 * Returns a representative {@link String} of {@code this}.
		 * <p>
		 * @return a representative {@link String} of {@code this}
		 */
		@Override
		public String toString() {
			return value;
		}
	}

	public enum CipherMode {
		CBC("CBC"),
		ECB("ECB");

		public final String value;

		private CipherMode(final String value) {
			this.value = value;
		}

		/**
		 * Returns a representative {@link String} of {@code this}.
		 * <p>
		 * @return a representative {@link String} of {@code this}
		 */
		@Override
		public String toString() {
			return value;
		}
	}

	public enum CipherPadding {
		NoPadding("NoPadding"),
		PKCS1Padding("PKCS1Padding"),
		PKCS5Padding("PKCS5Padding"),
		OAEPWithSHA1AndMGF1Padding("OAEPWithSHA-1AndMGF1Padding"),
		OAEPWithSHA256AndMGF1Padding("OAEPWithSHA-256AndMGF1Padding");

		public final String value;

		private CipherPadding(final String value) {
			this.value = value;
		}

		/**
		 * Returns a representative {@link String} of {@code this}.
		 * <p>
		 * @return a representative {@link String} of {@code this}
		 */
		@Override
		public String toString() {
			return value;
		}
	}
}
