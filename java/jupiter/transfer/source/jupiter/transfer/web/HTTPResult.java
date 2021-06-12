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
import java.util.Locale;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

public class HTTPResult
		implements ICloneable<HTTPResult>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link HttpEntity}.
	 */
	protected final HttpEntity entity;
	/**
	 * The array of {@link Header}.
	 */
	protected final Header[] headers;
	/**
	 * The {@link Locale}.
	 */
	protected final Locale locale;
	/**
	 * The {@link ProtocolVersion}.
	 */
	protected final ProtocolVersion protocolVersion;
	/**
	 * The {@link StatusLine}.
	 */
	protected final StatusLine statusLine;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link HTTPResult} with the specified {@link HttpEntity}, array of
	 * {@link Header}, {@link Locale}, {@link ProtocolVersion} and {@link StatusLine}.
	 * <p>
	 * @param entity          the {@link HttpEntity}
	 * @param headers         the array of {@link Header}
	 * @param locale          the {@link Locale}
	 * @param protocolVersion the {@link ProtocolVersion}
	 * @param statusLine      the {@link StatusLine}
	 */
	public HTTPResult(final HttpEntity entity, final Header[] headers, final Locale locale,
			final ProtocolVersion protocolVersion, final StatusLine statusLine) {
		this.entity = entity;
		this.headers = headers;
		this.locale = locale;
		this.protocolVersion = protocolVersion;
		this.statusLine = statusLine;
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link HTTPResult} with the specified {@link CloseableHttpResponse}.
	 * <p>
	 * @param response the {@link CloseableHttpResponse}
	 */
	public HTTPResult(final CloseableHttpResponse response) {
		this(response.getEntity(), response.getAllHeaders(), response.getLocale(),
				response.getProtocolVersion(), response.getStatusLine());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link HttpEntity}.
	 * <p>
	 * @return the {@link HttpEntity}
	 */
	public HttpEntity getEntity() {
		return entity;
	}

	/**
	 * Returns the array of {@link Header}.
	 * <p>
	 * @return the array of {@link Header}
	 */
	public Header[] getHeaders() {
		return headers;
	}

	/**
	 * Returns the {@link Locale}.
	 * <p>
	 * @return the {@link Locale}
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Returns the {@link ProtocolVersion}.
	 * <p>
	 * @return the {@link ProtocolVersion}
	 */
	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}

	/**
	 * Returns the {@link StatusLine}.
	 * <p>
	 * @return the {@link StatusLine}
	 */
	public StatusLine getStatusLine() {
		return statusLine;
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
	public HTTPResult clone() {
		try {
			return (HTTPResult) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		try {
			return EntityUtils.toString(entity);
		} catch (final IOException ignored) {
		}
		return Strings.EMPTY;
	}
}
