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
package jupiter.transfer.web;

import static jupiter.common.io.InputOutput.IO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import jupiter.common.util.Strings;

public class Web {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Web}.
	 */
	protected Web() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// DOWNLOADERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Downloads the file pointed by the specified {@link URL} to the specified target {@link File}.
	 * <p>
	 * @param source the {@link URL} of the source file
	 * @param target the target {@link File}
	 * <p>
	 * @throws IOException              if there is a problem with downloading the file
	 * @throws KeyManagementException   if there is a problem with the key manager
	 * @throws NoSuchAlgorithmException if there is no such supported protocol
	 * <p>
	 * @since 1.7
	 */
	public static void download(final URL source, final File target)
			throws IOException, KeyManagementException, NoSuchAlgorithmException {
		download(source, target, null);
	}

	/**
	 * Downloads the file pointed by the specified {@link URL} to the specified target {@link File}.
	 * <p>
	 * @param source the {@link URL} of the source file
	 * @param target the target {@link File}
	 * @param cookie the cookie {@link String} to use
	 * <p>
	 * @throws IOException              if there is a problem with downloading the file
	 * @throws KeyManagementException   if there is a problem with the key manager
	 * @throws NoSuchAlgorithmException if there is no such supported protocol
	 * <p>
	 * @since 1.7
	 */
	public static void download(final URL source, final File target, final String cookie)
			throws IOException, KeyManagementException, NoSuchAlgorithmException {
		IO.info("Download the file ", Strings.quote(source), " to ", Strings.quote(target));

		// Set the connection context
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		if (IO.getSeverityLevel().isDebug()) {
			System.setProperty("javax.net.debug", "ssl:handshake:verbose");
		}
		final SSLContext context = SSLContext.getInstance("TLSv1.2");
		context.init(null, null, null);
		SSLContext.setDefault(context);

		// Download the file pointed by the URL
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		final URLConnection connection = source.openConnection();
		if (Strings.isNonEmpty(cookie)) {
			connection.setRequestProperty("Cookie", cookie);
		}
		final ReadableByteChannel channel = Channels.newChannel(connection.getInputStream());
		new FileOutputStream(target).getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
	}
}
