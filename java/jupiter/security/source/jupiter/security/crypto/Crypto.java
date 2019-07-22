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
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public CipherMethod getMethod() {
		return method;
	}

	public CipherMode getMode() {
		return mode;
	}

	public CipherPadding getPadding() {
		return padding;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract int getDefaultKeySize();

	public abstract int getKeySize();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract void setDefaultKeySize();

	public abstract void setKeySize(final int keySize);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract void generateKey();

	public abstract void generateKey(final int size);

	////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract byte[] combine();

	public abstract void uncombine(final byte[] combination);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract Cipher getEncryptCipher();

	public byte[] encrypt(final byte[] bytes)
			throws BadPaddingException, IllegalBlockSizeException {
		return getEncryptCipher().doFinal(bytes);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract Cipher getDecryptCipher();

	public byte[] decrypt(final byte[] bytes)
			throws BadPaddingException, IllegalBlockSizeException {
		return getDecryptCipher().doFinal(bytes);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean checkKeySize(final int size) {
		try {
			return size <= Cipher.getMaxAllowedKeyLength(method.value);
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
