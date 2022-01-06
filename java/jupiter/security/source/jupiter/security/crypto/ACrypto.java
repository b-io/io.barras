/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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
import jupiter.common.util.Objects;
import jupiter.security.crypto.Crypto.CipherMethod;
import jupiter.security.crypto.Crypto.CipherMode;
import jupiter.security.crypto.Crypto.CipherPadding;

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

	/**
	 * Constructs an {@link ACrypto} by default.
	 */
	public ACrypto() {
		this(CipherMethod.RSA);
	}

	/**
	 * Constructs an {@link ACrypto} with the specified {@link CipherMethod}.
	 * <p>
	 * @param method the {@link CipherMethod}
	 */
	public ACrypto(final CipherMethod method) {
		this(method, CipherMode.ECB);
	}

	/**
	 * Constructs an {@link ACrypto} with the specified {@link CipherMethod} and {@link CipherMode}.
	 * <p>
	 * @param method the {@link CipherMethod}
	 * @param mode   the {@link CipherMode}
	 */
	public ACrypto(final CipherMethod method, final CipherMode mode) {
		this(method, mode, CipherPadding.OAEPWithSHA256AndMGF1Padding);
	}

	/**
	 * Constructs an {@link ACrypto} with the specified {@link CipherMethod}, {@link CipherMode} and
	 * {@link CipherPadding}.
	 * <p>
	 * @param method  the {@link CipherMethod}
	 * @param mode    the {@link CipherMode}
	 * @param padding the {@link CipherPadding}
	 */
	public ACrypto(final CipherMethod method, final CipherMode mode, final CipherPadding padding) {
		super(method, mode, padding);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the default size of the {@link PublicKey}.
	 * <p>
	 * @return the default size of the {@link PublicKey}
	 */
	@Override
	public int getDefaultKeySize() {
		return isValidKeySize(4096) ? 4096 : 2024;
	}

	/**
	 * Returns the size of the {@link PublicKey}.
	 * <p>
	 * @return the size of the {@link PublicKey}
	 */
	@Override
	public int getKeySize() {
		return publicKeySize;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the primary encoded key of the {@link PrivateKey}, or {@code null} if the
	 * {@link PrivateKey} does not support encoding.
	 * <p>
	 * @return the primary encoded key of the {@link PrivateKey}, or {@code null} if the
	 *         {@link PrivateKey} does not support encoding
	 */
	public byte[] getPrivateKey() {
		return privateKey != null ? privateKey.getEncoded() : null;
	}

	/**
	 * Returns the primary encoded key of the {@link PublicKey}, or {@code null} if the
	 * {@link PublicKey} does not support encoding.
	 * <p>
	 * @return the primary encoded key of the {@link PublicKey}, or {@code null} if the
	 *         {@link PublicKey} does not support encoding
	 */
	public byte[] getPublicKey() {
		return publicKey != null ? publicKey.getEncoded() : null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the default size of the {@link PublicKey}.
	 */
	@Override
	public void setDefaultKeySize() {
		publicKeySize = getDefaultKeySize();
	}

	/**
	 * Sets the size of the {@link PublicKey}.
	 * <p>
	 * @param publicKeySize a size of the {@link PublicKey}
	 */
	@Override
	public void setKeySize(final int publicKeySize) {
		this.publicKeySize = publicKeySize;
	}

	//////////////////////////////////////////////

	/**
	 * Sets the {@link PrivateKey} with the specified primary encoded key.
	 * <p>
	 * @param privateKey a primary encoded key
	 */
	public void setPrivateKey(final byte[] privateKey) {
		this.privateKey = createPrivateKey(privateKey);
	}

	/**
	 * Sets the {@link PublicKey} with the specified primary encoded key.
	 * <p>
	 * @param publicKey a primary encoded key
	 */
	public void setPublicKey(final byte[] publicKey) {
		this.publicKey = createPublicKey(publicKey);
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
		return publicKey.getEncoded();
	}

	//////////////////////////////////////////////

	/**
	 * Uncombines the specified primary encoded combination.
	 * <p>
	 * @param combination the primary encoded combination to uncombine
	 */
	@Override
	public void uncombine(final byte[] combination) {
		publicKeySize = combination.length * Byte.SIZE; // [bit]
		publicKey = createPublicKey(combination);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link PrivateKey} and a {@link PublicKey} of the specified size.
	 * <p>
	 * @param publicKeySize the size of the {@link PublicKey} to create
	 */
	@Override
	public void createKey(final int publicKeySize) {
		try {
			setKeySize(publicKeySize);
			final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(method.value);
			keyPairGenerator.initialize(publicKeySize);
			final KeyPair keyPair = keyPairGenerator.generateKeyPair();
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Creates a {@link PrivateKey} with the specified primary encoded key.
	 * <p>
	 * @param privateKey the primary encoded key of the {@link PrivateKey} to create
	 * <p>
	 * @return a {@link PrivateKey} with the specified primary encoded key
	 */
	public PrivateKey createPrivateKey(final byte[] privateKey) {
		try {
			return KeyFactory.getInstance(method.value)
					.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
		} catch (final InvalidKeySpecException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		}
	}

	/**
	 * Creates a {@link PublicKey} with the specified primary encoded key.
	 * <p>
	 * @param publicKey the primary encoded key of the {@link PublicKey} to create
	 * <p>
	 * @return a {@link PublicKey} with the specified primary encoded key
	 */
	public PublicKey createPublicKey(final byte[] publicKey) {
		try {
			return KeyFactory.getInstance(method.value)
					.generatePublic(new X509EncodedKeySpec(publicKey));
		} catch (final InvalidKeySpecException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		}
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
			if (publicKeySize == 0) {
				createKey();
			}
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
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
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher;
		} catch (final InvalidKeyException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalTypeException(method, ex);
		} catch (final NoSuchPaddingException ex) {
			throw new IllegalTypeException(padding, ex);
		}
	}
}
