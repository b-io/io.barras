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

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jupiter.common.io.Resources;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HTTPHandler
		implements ICloneable<HTTPHandler>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The entity header used to indicate the media type of the resource.
	 */
	public static final String HEADER_CONTENT_TYPE = "Content-Type";

	public static final String HEADER_CONTENT_TYPE_APPLICATION_JAVASCRIPT = "application/javascript";
	public static final String HEADER_CONTENT_TYPE_APPLICATION_JSON = "application/json";
	public static final String HEADER_CONTENT_TYPE_APPLICATION_MSWORD = "application/msword";
	public static final String HEADER_CONTENT_TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";
	public static final String HEADER_CONTENT_TYPE_APPLICATION_PDF = "application/pdf";
	public static final String HEADER_CONTENT_TYPE_APPLICATION_XHTML = "application/xhtml+xml";
	public static final String HEADER_CONTENT_TYPE_APPLICATION_XML = "application/xml";
	public static final String HEADER_CONTENT_TYPE_APPLICATION_ZIP = "application/zip";

	public static final String HEADER_CONTENT_TYPE_AUDIO_FLAC = "audio/flac";
	public static final String HEADER_CONTENT_TYPE_AUDIO_MIDI = "audio/midi";
	public static final String HEADER_CONTENT_TYPE_AUDIO_MPEG = "audio/mpeg";
	public static final String HEADER_CONTENT_TYPE_AUDIO_OGG = "audio/ogg";
	public static final String HEADER_CONTENT_TYPE_AUDIO_WAV = "audio/wav";

	public static final String HEADER_CONTENT_TYPE_EXAMPLE = "example";

	public static final String HEADER_CONTENT_TYPE_FONT = "font";

	public static final String HEADER_CONTENT_TYPE_IMAGE_BMP = "image/bmp";
	public static final String HEADER_CONTENT_TYPE_IMAGE_GIF = "image/gif";
	public static final String HEADER_CONTENT_TYPE_IMAGE_JPEG = "image/jpeg";
	public static final String HEADER_CONTENT_TYPE_IMAGE_PNG = "image/png";
	public static final String HEADER_CONTENT_TYPE_IMAGE_TIFF = "image/tiff";
	public static final String HEADER_CONTENT_TYPE_IMAGE_SVG = "image/svg+xml";

	public static final String HEADER_CONTENT_TYPE_MESSAGE = "message";

	public static final String HEADER_CONTENT_TYPE_MODEL = "model";

	public static final String HEADER_CONTENT_TYPE_MULTIPART_MIXED = "multipart/mixed";
	public static final String HEADER_CONTENT_TYPE_MULTIPART_ALTERNATIVE = "multipart/alternative";
	public static final String HEADER_CONTENT_TYPE_MULTIPART_RELATED = "multipart/related";

	public static final String HEADER_CONTENT_TYPE_TEXT_CSS = "text/css";
	public static final String HEADER_CONTENT_TYPE_TEXT_CSV = "text/csv";
	public static final String HEADER_CONTENT_TYPE_TEXT_HTML = "text/html";
	public static final String HEADER_CONTENT_TYPE_TEXT_PLAIN = "text/plain";
	public static final String HEADER_CONTENT_TYPE_TEXT_XML = "text/xml";

	public static final String HEADER_CONTENT_TYPE_VIDEO_MPEG = "video/mpeg";
	public static final String HEADER_CONTENT_TYPE_VIDEO_MP4 = "video/mp4";
	public static final String HEADER_CONTENT_TYPE_VIDEO_QUICKTIME = "video/quicktime";
	public static final String HEADER_CONTENT_TYPE_VIDEO_WEB = "video/web";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link CloseableHttpClient}.
	 */
	protected final CloseableHttpClient client;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link HTTPHandler}.
	 */
	public HTTPHandler() {
		client = HttpClients.createDefault();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link CloseableHttpClient}.
	 * <p>
	 * @return the {@link CloseableHttpClient}
	 */
	public CloseableHttpClient getClient() {
		return client;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONTROLLERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shutdowns {@code this}.
	 */
	public void shutdown() {
		Resources.closeAuto(client);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sends a HTTP GET request to the specified URL {@link String} with the specified {@link Map}
	 * containing the headers.
	 * <p>
	 * @param url     the URL {@link String} to send to
	 * @param headers the {@link Map} containing the headers (may be {@code null})
	 * <p>
	 * @return the {@link HTTPResult}
	 * <p>
	 * @throws ClientProtocolException if there is a problem with the HTTP protocol
	 * @throws IOException             if there is a problem with executing the HTTP GET request
	 * <p>
	 * @since 1.7
	 */
	public HTTPResult get(final String url, final Map<String, String> headers)
			throws ClientProtocolException, IOException {
		// Create the HTTP GET request with the URL
		final HttpGet request = new HttpGet(url);

		// Add the headers to the HTTP GET request
		if (headers != null) {
			final Set<Entry<String, String>> hs = headers.entrySet();
			for (final Entry<String, String> h : hs) {
				request.addHeader(h.getKey(), h.getValue());
			}
		}

		// Execute the HTTP GET request
		CloseableHttpResponse response = null;
		try {
			response = client.execute(request);
			// Return the HTTP result
			return new HTTPResult(response);
		} finally {
			Resources.closeAuto(response);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sends a HTTP POST request to the specified URL {@link String} with the specified {@link Map}
	 * containing the headers and {@link HttpEntity}.
	 * <p>
	 * @param url     the URL {@link String} to send to
	 * @param headers the {@link Map} containing the headers (may be {@code null})
	 * @param entity  the {@link HttpEntity}
	 * <p>
	 * @return the {@link HTTPResult}
	 * <p>
	 * @throws ClientProtocolException if there is a problem with the HTTP protocol
	 * @throws IOException             if there is a problem with executing the HTTP POST request
	 * <p>
	 * @since 1.7
	 */
	public HTTPResult post(final String url, final Map<String, String> headers,
			final HttpEntity entity)
			throws ClientProtocolException, IOException {
		// Create the HTTP POST request with the URL
		final HttpPost request = new HttpPost(url);

		// Add the headers to the HTTP POST request
		if (headers != null) {
			final Set<Entry<String, String>> hs = headers.entrySet();
			for (final Map.Entry<String, String> h : hs) {
				request.addHeader(h.getKey(), h.getValue());
			}
		}

		// Add the parameters to the HTTP POST request
		request.setEntity(entity);

		// Execute the HTTP POST request
		CloseableHttpResponse response = null;
		try {
			response = client.execute(request);
			// Return the HTTP result
			return new HTTPResult(response);
		} finally {
			Resources.closeAuto(response);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Sends a HTTP POST request to the specified URL {@link String} with the specified {@link Map}
	 * containing the headers and JSON {@link String}.
	 * <p>
	 * @param url     the URL {@link String} to send to
	 * @param headers the {@link Map} containing the headers (may be {@code null})
	 * @param json    the JSON {@link String}
	 * <p>
	 * @return the {@link HTTPResult}
	 * <p>
	 * @throws ClientProtocolException if there is a problem with the HTTP protocol
	 * @throws IOException             if there is a problem with executing the HTTP POST request
	 * <p>
	 * @since 1.7
	 */
	public HTTPResult postJSON(final String url, final Map<String, String> headers,
			final String json)
			throws ClientProtocolException, IOException {
		final Map<String, String> hs = headers != null ? headers : new HashMap<String, String>();
		hs.put(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_APPLICATION_JSON);
		return post(url, hs, new StringEntity(json));
	}

	//////////////////////////////////////////////

	/**
	 * Sends a HTTP POST request to the specified URL {@link String} with the specified {@link Map}
	 * containing the headers and {@link List} of parameter {@link NameValuePair}.
	 * <p>
	 * @param url        the URL {@link String} to send to
	 * @param headers    the {@link Map} containing the headers (may be {@code null})
	 * @param parameters the {@link List} of parameter {@link NameValuePair}
	 * <p>
	 * @return the {@link HTTPResult}
	 * <p>
	 * @throws ClientProtocolException if there is a problem with the HTTP protocol
	 * @throws IOException             if there is a problem with executing the HTTP POST request
	 * <p>
	 * @since 1.7
	 */
	public HTTPResult postParameters(final String url, final Map<String, String> headers,
			final List<NameValuePair> parameters)
			throws ClientProtocolException, IOException {
		return post(url, headers, new UrlEncodedFormEntity(parameters));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public HTTPHandler clone() {
		try {
			return (HTTPHandler) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}
}
