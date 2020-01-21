/*
 * The MIT License
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation mails (the "Software"), to deal
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

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Strings.STAR;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.BodyTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import jupiter.common.exception.IllegalTypeException;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.time.Dates;
import jupiter.common.util.Integers;
import jupiter.common.util.Strings;

public class MailHandler
		implements ICloneable<MailHandler>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The system-dependent default name-separator {@link String} of the remote file system.
	 */
	public static volatile String REMOTE_SEPARATOR = "/";

	/**
	 * Sets the timeout and the connection timeout.
	 */
	public static volatile long TIMEOUT = 10000L; // [ms]


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The ingoing mail server {@link Protocol}.
	 */
	protected Protocol inProtocol;
	/**
	 * The outgoing mail server {@link Protocol}.
	 */
	protected Protocol outProtocol;
	/**
	 * The host name.
	 */
	protected String hostName;
	/**
	 * The user name.
	 */
	protected String userName;
	/**
	 * The password.
	 */
	protected String password;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link MailHandler}.
	 */
	public MailHandler() {
	}

	/**
	 * Constructs a {@link MailHandler} with the specified ingoing mail server {@link Protocol},
	 * outgoing mail server {@link Protocol}, host name, user name and password.
	 * <p>
	 * @param inProtocol  the ingoing mail server {@link Protocol}
	 * @param outProtocol the outgoing mail server {@link Protocol}
	 * @param hostName    the host name
	 * @param userName    the user name
	 * @param password    the password
	 */
	public MailHandler(final Protocol inProtocol, final Protocol outProtocol, final String hostName,
			final String userName, final String password) {
		this.inProtocol = inProtocol;
		this.outProtocol = outProtocol;
		this.hostName = hostName;
		this.userName = userName;
		this.password = password;
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link MailHandler} loaded from the specified {@link Properties} containing the
	 * ingoing mail server {@link Protocol}, outgoing mail server {@link Protocol}, host name,
	 * ingoing mail server port, outgoing mail server port, user name and password.
	 * <p>
	 * @param properties the {@link Properties} to load
	 */
	public MailHandler(final Properties properties) {
		load(properties);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the ingoing mail server {@link Protocol}.
	 * <p>
	 * @return the ingoing mail server {@link Protocol}
	 */
	public Protocol getInProtocol() {
		return inProtocol;
	}

	/**
	 * Returns the outgoing mail server {@link Protocol}.
	 * <p>
	 * @return the outgoing mail server {@link Protocol}
	 */
	public Protocol getOutProtocol() {
		return outProtocol;
	}

	/**
	 * Returns the host name.
	 * <p>
	 * @return the host name
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Returns the ingoing mail server port.
	 * <p>
	 * @return the ingoing mail server port
	 */
	public int getInPort() {
		return inProtocol.getPort();
	}

	/**
	 * Returns the outgoing mail server port.
	 * <p>
	 * @return the outgoing mail server port
	 */
	public int getOutPort() {
		return outProtocol.getPort();
	}

	/**
	 * Returns the user name.
	 * <p>
	 * @return the user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Returns the password.
	 * <p>
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the ingoing mail server {@link Protocol}.
	 * <p>
	 * @param inProtocol a {@link Protocol}
	 */
	public void setInProtocol(final Protocol inProtocol) {
		this.inProtocol = inProtocol;
	}

	/**
	 * Sets the outgoing mail server {@link Protocol}.
	 * <p>
	 * @param outProtocol a {@link Protocol}
	 */
	public void setOutProtocol(final Protocol outProtocol) {
		this.outProtocol = outProtocol;
	}

	/**
	 * Sets the host name.
	 * <p>
	 * @param hostName a {@link String}
	 */
	public void setHostName(final String hostName) {
		this.hostName = hostName;
	}

	/**
	 * Sets the ingoing mail server port.
	 * <p>
	 * @param inPort an {@code int} value
	 */
	public void setInPort(final int inPort) {
		inProtocol.setPort(inPort);
	}

	/**
	 * Sets the outgoing mail server port.
	 * <p>
	 * @param outPort an {@code int} value
	 */
	public void setOutPort(final int outPort) {
		outProtocol.setPort(outPort);
	}

	/**
	 * Sets the user name.
	 * <p>
	 * @param userName a {@link String}
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 * Sets the password.
	 * <p>
	 * @param password a {@link String}
	 */
	public void setPassword(final String password) {
		this.password = password;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link MimeMultipart} with the specified content {@link String}.
	 * <p>
	 * @param content the content {@link String} of the {@link MimeMultipart} to create
	 * <p>
	 * @return a {@link MimeMultipart} with the specified content {@link String}
	 * <p>
	 * @throws MessagingException if there is a problem with creating the mail content
	 */
	public static MimeMultipart createContent(final String content)
			throws MessagingException {
		final MimeMultipart multipart = new MimeMultipart("alternative");
		final MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(content, "text/html");
		multipart.addBodyPart(bodyPart);
		return multipart;
	}

	/**
	 * Creates a {@link SearchTerm} with the specified pattern {@link String}.
	 * <p>
	 * @param pattern the pattern {@link String} of the {@link SearchTerm} to create
	 * <p>
	 * @return a {@link SearchTerm} with the specified pattern {@link String}
	 */
	public static SearchTerm createSearchTerm(final String pattern) {
		return new OrTerm(new FromStringTerm(pattern),
				new OrTerm(new SubjectTerm(pattern), new BodyTerm(pattern)));
	}

	/**
	 * Creates a {@link Session} with the specified {@link Protocol} and {@code this} parameters.
	 * <p>
	 * @param protocol the {@link Protocol} of the {@link Session} to create
	 * <p>
	 * @return a {@link Session} with the specified {@link Protocol} and {@code this} parameters
	 */
	public Session createSession(final Protocol protocol) {
		// Create the properties
		final Properties properties = new Properties();
		properties.put("mail.store.protocol", protocol.value);
		properties.put("mail." + protocol + ".host", hostName);
		properties.put("mail." + protocol + ".port", protocol.getPort());
		properties.put("mail." + protocol + ".auth", Strings.toString(password != null));
		properties.put("mail." + protocol + ".timeout", Strings.toString(TIMEOUT));
		properties.put("mail." + protocol + ".connectiontimeout", Strings.toString(TIMEOUT));
		if (protocol.isSSL()) {
			properties.put("mail." + protocol + ".socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			properties.put("mail." + protocol + ".socketFactory.port", protocol.getPort());
			properties.put("mail." + protocol + ".ssl.checkserveridentity", "false");
			properties.put("mail." + protocol + ".ssl.trust", STAR);
		} else if (protocol.isTLS()) {
			properties.put("mail." + protocol + ".starttls.required", "true");
		}

		// Create the authenticator
		final Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};

		// Create the session
		final Session session = Session.getDefaultInstance(properties, authenticator);
		session.setDebug(IO.getSeverityLevel().isDebug());
		return session;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Downloads the {@link List} of {@link MimeMessage} from the mail server in the specified
	 * remote directory.
	 * <p>
	 * @param remoteDirPath the path to the remote directory
	 * <p>
	 * @return the {@link List} of {@link MimeMessage}
	 */
	public List<MimeMessage> download(final String remoteDirPath) {
		return download(remoteDirPath, STAR);
	}

	//////////////////////////////////////////////

	/**
	 * Downloads the {@link List} of {@link MimeMessage} from the mail server in the specified
	 * remote directory filtered by the specified mail filter {@link String}.
	 * <p>
	 * @param remoteDirPath the path to the remote directory
	 * @param mailFilter    the mail filter {@link String}
	 * <p>
	 * @return the {@link List} of {@link MimeMessage}
	 */
	public List<MimeMessage> download(final String remoteDirPath, final String mailFilter) {
		return download(remoteDirPath, createSearchTerm(mailFilter));
	}

	/**
	 * Downloads the {@link List} of {@link MimeMessage} from the mail server in the specified
	 * remote directory filtered by the specified mail filter {@link SearchTerm}.
	 * <p>
	 * @param remoteDirPath the path to the remote directory
	 * @param mailFilter    the mail filter {@link SearchTerm}
	 * <p>
	 * @return the {@link List} of {@link MimeMessage}
	 */
	public List<MimeMessage> download(final String remoteDirPath, final SearchTerm mailFilter) {
		final List<MimeMessage> messages = new ExtendedLinkedList<MimeMessage>();
		try {
			IO.debug("Connect to the mail server ",
					Strings.quote(hostName + ":" + inProtocol.getPort()),
					" with ", Strings.quote(userName));
			final Session session = createSession(inProtocol);
			Store store = null;
			try {
				store = session.getStore(inProtocol.value);
				store.connect(hostName, inProtocol.getPort(), userName, password);

				IO.debug("Download the filtered mails in ", Strings.quote(remoteDirPath));
				Folder folder = null;
				try {
					folder = store.getFolder(remoteDirPath);
					folder.open(Folder.READ_ONLY);
					for (final Message message : folder.getMessages()) {
						if (message.match(mailFilter)) {
							IO.debug("Download the mail ", Strings.quote(message.getSubject()));
							messages.add(new MimeMessage((MimeMessage) message));
						}
					}
				} finally {
					if (folder != null) {
						try {
							folder.close(false);
						} catch (final MessagingException ex) {
							IO.error(ex);
						}
					}
				}
			} finally {
				if (store != null) {
					try {
						store.close();
					} catch (final MessagingException ex) {
						IO.error(ex);
					}
				}
			}
		} catch (final MessagingException ex) {
			IO.error(ex);
		}
		return messages;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sends a mail with the specified subject {@link String} and content {@link String} to the
	 * specified comma-separated recipients {@link String} from the mail server with {@code this}
	 * parameters.
	 * <p>
	 * @param recipients the comma-separated recipients {@link String} to send to
	 * @param subject    the subject {@link String} of the mail to send
	 * @param content    the content {@link String} of the mail to send
	 * <p>
	 * @throws MessagingException if there is a problem with creating or sending the mail
	 */
	public void send(final String recipients, final String subject, final String content)
			throws MessagingException {
		send(recipients, subject, createContent(content));
	}

	/**
	 * Sends a mail with the specified subject {@link String} and content {@link Multipart} to the
	 * specified comma-separated recipients {@link String} from the mail server with {@code this}
	 * parameters.
	 * <p>
	 * @param recipients the comma-separated recipients {@link String} to send to
	 * @param subject    the subject {@link String} of the mail to send
	 * @param content    the content {@link Multipart} of the mail to send
	 * <p>
	 * @throws MessagingException if there is a problem with creating or sending the mail
	 */
	public void send(final String recipients, final String subject, final Multipart content)
			throws MessagingException {
		send(userName, recipients, subject, content);
	}

	/**
	 * Sends a mail with the specified subject {@link String} and content {@link Multipart} to the
	 * specified comma-separated recipients {@link String} from the mail server with {@code this}
	 * parameters.
	 * <p>
	 * @param sender     the sender {@link String} to send from
	 * @param recipients the comma-separated recipients {@link String} to send to
	 * @param subject    the subject {@link String} of the mail to send
	 * @param content    the content {@link Multipart} of the mail to send
	 * <p>
	 * @throws MessagingException if there is a problem with creating or sending the mail
	 */
	public void send(final String sender, final String recipients, final String subject,
			final Multipart content)
			throws MessagingException {
		IO.debug("Connect to the mail server ",
				Strings.quote(hostName + ":" + outProtocol.getPort()),
				" with ", Strings.quote(sender));
		final Session session = createSession(outProtocol);

		// Create and send the mail
		final MimeMessage mail = Mails.create(session, sender, recipients, subject, content);
		IO.debug("Send the mail ", Strings.quote(subject),
				" from ", Strings.quote(sender),
				" to ", Strings.quote(recipients),
				" at ", Dates.formatWithTime(mail.getSentDate()));
		Transport.send(mail);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Loads {@code this} from the specified {@link Properties}.
	 * <p>
	 * @param properties the {@link Properties} to load
	 */
	public void load(final Properties properties) {
		inProtocol = Protocol.get(properties.getProperty("inProtocol"));
		outProtocol = Protocol.get(properties.getProperty("outProtocol"));
		hostName = properties.getProperty("hostName");
		inProtocol.setPort(Integers.convert(properties.getProperty("inPort",
				Strings.toString(inProtocol.getPort()))));
		outProtocol.setPort(Integers.convert(properties.getProperty("outPort",
				Strings.toString(outProtocol.getPort()))));
		userName = properties.getProperty("userName");
		password = properties.getProperty("password");
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	public MailHandler clone() {
		try {
			return (MailHandler) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum Protocol {
		POP3("pop3"),
		POP3S("pop3s"),
		IMAP("imap"),
		IMAPS("imaps"),
		SMTP("smtp"),
		SMTPS("smtps"),
		SMTPTLS("smtptls");

		public final String value;
		public int port = -1;

		private Protocol(final String value) {
			this.value = value;
		}

		public boolean isSSL() {
			switch (this) {
				case POP3:
				case IMAP:
				case SMTP:
				case SMTPTLS:
					return false;
				case POP3S:
				case IMAPS:
				case SMTPS:
					return true;
				default:
					throw new IllegalTypeException(this);
			}
		}

		public boolean isTLS() {
			switch (this) {
				case POP3:
				case POP3S:
				case IMAP:
				case IMAPS:
				case SMTP:
				case SMTPS:
					return false;
				case SMTPTLS:
					return true;
				default:
					throw new IllegalTypeException(this);
			}
		}

		public int getDefaultPort() {
			switch (this) {
				case POP3:
					return 110;
				case POP3S:
					return 995;
				case IMAP:
					return 143;
				case IMAPS:
					return 993;
				case SMTP:
					return 25;
				case SMTPS:
					return 465;
				case SMTPTLS:
					return 587;
				default:
					throw new IllegalTypeException(this);
			}
		}

		public int getPort() {
			return port > 0 ? port : getDefaultPort();
		}

		public void setPort(final int port) {
			this.port = port > 0 ? port : getDefaultPort();
		}

		public static Protocol get(final String name) {
			final String value = Strings.toUpperCase(name);
			if (POP3.value.equals(value)) {
				return POP3;
			} else if (POP3S.value.equals(value)) {
				return POP3S;
			} else if (IMAP.value.equals(value)) {
				return IMAP;
			} else if (IMAPS.value.equals(value)) {
				return IMAPS;
			} else if (SMTP.value.equals(value)) {
				return SMTP;
			} else if (SMTPS.value.equals(value)) {
				return SMTPS;
			} else if (SMTPTLS.value.equals(value)) {
				return SMTPTLS;
			}
			throw new IllegalTypeException(name);
		}

		/**
		 * Returns a representative {@link String} of {@code this}.
		 * <p>
		 * @return a representative {@link String} of {@code this}
		 */
		@Override
		public String toString() {
			switch (this) {
				case POP3:
				case POP3S:
				case IMAP:
				case IMAPS:
					return value;
				case SMTP:
				case SMTPS:
				case SMTPTLS:
					return "smtp";
				default:
					throw new IllegalTypeException(this);
			}
		}
	}
}
