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
package jupiter.transfer.mail;

import java.io.IOException;
import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mails {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Mails}.
	 */
	protected Mails() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the array of {@link BodyPart} of the specified {@link MimeMessage}.
	 * <p>
	 * @param mail a {@link MimeMessage}
	 * <p>
	 * @return the array of {@link BodyPart} of the specified {@link MimeMessage}
	 * <p>
	 * @throws IOException        if there is a problem with querying the file system
	 * @throws MessagingException if there is a problem with accessing {@code mail}
	 */
	public static BodyPart[] getBodyParts(final MimeMessage mail)
			throws IOException, MessagingException {
		final Object content = mail.getContent();
		if (content == null || !(content instanceof MimeMultipart)) {
			return new BodyPart[] {};
		}
		final MimeMultipart multipart = (MimeMultipart) content;
		final int count = multipart.getCount();
		final BodyPart[] bodyParts = new BodyPart[count];
		for (int bpi = 0; bpi < count; ++bpi) {
			bodyParts[bpi] = multipart.getBodyPart(bpi);
		}
		return bodyParts;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link MimeMessage} with the specified {@link Session}, sender, recipients, subject
	 * and content {@link Multipart}.
	 * <p>
	 * @param session    the {@link Session} of the mail to create
	 * @param sender     the sender {@link String}
	 * @param recipients the comma-separated recipients {@link String}
	 * @param subject    the subject {@link String} of the mail to create
	 * @param content    the content {@link Multipart} of the mail to create
	 * <p>
	 * @return a {@link MimeMessage} with the specified {@link Session}, sender, recipients, subject
	 *         and content {@link Multipart}
	 * <p>
	 * @throws AddressException   if there is a problem with the sender
	 * @throws MessagingException if there is a problem with creating the mail
	 */
	protected static MimeMessage create(final Session session, final String sender,
			final String recipients, final String subject, final Multipart content)
			throws AddressException, MessagingException {
		final MimeMessage mail = new MimeMessage(session);
		mail.setFrom(new InternetAddress(sender));
		mail.addRecipients(RecipientType.TO, InternetAddress.parse(recipients));
		mail.setSubject(subject);
		mail.setContent(content);
		mail.setSentDate(new Date());
		return mail;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of {@link MimeMessage}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link MimeMessage},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof MimeMessage;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link MimeMessage}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link MimeMessage},
	 *         {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return MimeMessage.class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link BodyPart} is an attachment.
	 * <p>
	 * @param bodyPart the {@link BodyPart} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link BodyPart} is an attachment, {@code false}
	 *         otherwise
	 * <p>
	 * @throws MessagingException if there is a problem with accessing {@code bodyPart}
	 */
	public static boolean isAttachment(final BodyPart bodyPart)
			throws MessagingException {
		return bodyPart != null && Part.ATTACHMENT.equals(bodyPart.getDisposition());
	}
}
