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

import static jupiter.common.io.IO.IO;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import jupiter.common.test.Test;

public class SCryptoTest
		extends Test {

	public SCryptoTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of {@link SCrypto#encrypt}.
	 */
	public void testEncrypt() {
		IO.test("• encrypt");

		try {
			final String text = "Hello world!";
			final SCrypto sCrypto = new SCrypto();
			final byte[] encryptedData = sCrypto.encrypt(text.getBytes());
			final String encryptedText = new String(encryptedData);
			IO.test(encryptedText);
			assertNotSame(text, encryptedText);
		} catch (final BadPaddingException ex) {
			IO.error(ex);
		} catch (final IllegalBlockSizeException ex) {
			IO.error(ex);
		}
	}

	/**
	 * Test of {@link SCrypto#decrypt}.
	 */
	public void testDecrypt() {
		IO.test("• decrypt");

		try {
			final String text = "Hello world!";
			final SCrypto sCrypto = new SCrypto();
			final byte[] encryptedData = sCrypto.encrypt(text.getBytes());
			final byte[] decryptedData = sCrypto.decrypt(encryptedData);
			final String decryptedText = new String(decryptedData);
			IO.test(decryptedText);
			assertEquals(text, new String(decryptedData));
		} catch (final BadPaddingException ex) {
			IO.error(ex);
		} catch (final IllegalBlockSizeException ex) {
			IO.error(ex);
		}
	}
}
