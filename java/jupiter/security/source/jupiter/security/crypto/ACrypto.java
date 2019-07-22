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

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import jupiter.common.exception.IllegalTypeException;
import jupiter.common.util.Strings;

public class ACrypto
		extends Crypto {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The number of bits of the {@link PublicKey}.
	 */
	protected int publicKeySize = 0; // [bit]

	/**
	 * The {@link PrivateKey}.
	 */
	protected PrivateKey privateKey = null;
	/**
	 * The {@link PublicKey}.
	 */
	protected PublicKey publicKey = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public ACrypto() {
		this(CipherMethod.RSA);
	}

	public ACrypto(final CipherMethod method) {
		this(method, CipherMode.ECB);
	}

	public ACrypto(final CipherMethod method, final CipherMode mode) {
		this(method, mode, CipherPadding.OAEPWithSHA256AndMGF1Padding);
	}

	public ACrypto(final CipherMethod method, final CipherMode mode, final CipherPadding padding) {
		super(method, mode, padding);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int getDefaultKeySize() {
		return checkKeySize(4096) ? 4096 : 2024;
	}

	@Override
	public int getKeySize() {
		return publicKeySize;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public byte[] getPrivateKey() {
		return privateKey != null ? privateKey.getEncoded() : null;
	}

	public PrivateKey getPrivateKey(final byte[] key) {
		try {
			return KeyFactory.getInstance(method.value)
					.generatePrivate(new PKCS8EncodedKeySpec(key));
		} catch (final InvalidKeySpecException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		}
	}

	//////////////////////////////////////////////

	public byte[] getPublicKey() {
		return publicKey != null ? publicKey.getEncoded() : null;
	}

	public PublicKey getPublicKey(final byte[] key) {
		try {
			return KeyFactory.getInstance(method.value).generatePublic(new X509EncodedKeySpec(key));
		} catch (final InvalidKeySpecException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void setDefaultKeySize() {
		publicKeySize = getDefaultKeySize();
	}

	@Override
	public void setKeySize(final int publicKeySize) {
		this.publicKeySize = publicKeySize;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public void setPrivateKey(final byte[] key) {
		privateKey = getPrivateKey(key);
	}

	//////////////////////////////////////////////

	public void setPublicKey(final byte[] key) {
		publicKey = getPublicKey(key);
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
			final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(method.value);
			keyPairGenerator.initialize(publicKeySize);
			final KeyPair keyPair = keyPairGenerator.generateKeyPair();
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public byte[] combine() {
		return publicKey.getEncoded();
	}

	@Override
	public void uncombine(final byte[] combination) {
		publicKeySize = combination.length * Byte.SIZE; // [bit]
		publicKey = getPublicKey(combination);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Cipher getEncryptCipher() {
		try {
			final Cipher cipher = Cipher.getInstance(toString());
			if (publicKeySize == 0) {
				generateKey();
			}
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
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
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher;
		} catch (final InvalidKeyException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		} catch (final NoSuchPaddingException ex) {
			throw new IllegalTypeException(padding, ex);
		}
	}
}
