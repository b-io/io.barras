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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
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
	 * Sets the timeout and the connection timeout.
	 */
	public static volatile long TIMEOUT = 5000L; // [ms]


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link Protocol}.
	 */
	protected Protocol protocol;
	/**
	 * The host name.
	 */
	protected String hostName;
	/**
	 * The port.
	 */
	protected int port;
	/**
	 * The user name.
	 */
	protected String userName;
	/**
	 * The password.
	 */
	protected String password;
	/**
	 * The path to the remote directory.
	 */
	protected String remoteDirPath;
	/**
	 * The mail filter {@link SearchTerm}.
	 */
	protected SearchTerm mailFilter;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link MailHandler}.
	 */
	public MailHandler() {
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link MailHandler} with the specified {@link Protocol}, host name, user name,
	 * password and path to the remote directory.
	 * <p>
	 * @param protocol      the {@link Protocol}
	 * @param hostName      the host name
	 * @param userName      the user name
	 * @param password      the password
	 * @param remoteDirPath the path to the remote directory
	 */
	public MailHandler(final Protocol protocol, final String hostName, final String userName,
			final String password, final String remoteDirPath) {
		this(protocol, hostName, -1, userName, password, remoteDirPath);
	}

	/**
	 * Constructs a {@link MailHandler} with the specified {@link Protocol}, host name, port, user
	 * name, password and path to the remote directory.
	 * <p>
	 * @param protocol      the {@link Protocol}
	 * @param hostName      the host name
	 * @param port          the port
	 * @param userName      the user name
	 * @param password      the password
	 * @param remoteDirPath the path to the remote directory
	 */
	public MailHandler(final Protocol protocol, final String hostName, final int port,
			final String userName, final String password, final String remoteDirPath) {
		this(protocol, hostName, port, userName, password, remoteDirPath, STAR);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link MailHandler} with the specified {@link Protocol}, host name, user name,
	 * password, path to the remote directory and mail filter {@link String}.
	 * <p>
	 * @param protocol      the {@link Protocol}
	 * @param hostName      the host name
	 * @param userName      the user name
	 * @param password      the password
	 * @param remoteDirPath the path to the remote directory
	 * @param mailFilter    the mail filter {@link String}
	 */
	public MailHandler(final Protocol protocol, final String hostName, final String userName,
			final String password, final String remoteDirPath, final String mailFilter) {
		this(protocol, hostName, -1, userName, password, remoteDirPath, mailFilter);
	}

	/**
	 * Constructs a {@link MailHandler} with the specified {@link Protocol}, host name, user name,
	 * password, path to the remote directory and mail filter {@link SearchTerm}.
	 * <p>
	 * @param protocol      the {@link Protocol}
	 * @param hostName      the host name
	 * @param userName      the user name
	 * @param password      the password
	 * @param remoteDirPath the path to the remote directory
	 * @param mailFilter    the mail filter {@link SearchTerm}
	 */
	public MailHandler(final Protocol protocol, final String hostName, final String userName,
			final String password, final String remoteDirPath, final SearchTerm mailFilter) {
		this(protocol, hostName, -1, userName, password, remoteDirPath, mailFilter);
	}

	/**
	 * Constructs a {@link MailHandler} with the specified {@link Protocol}, host name, port, user
	 * name, password, path to the remote directory and mail filter {@link String}.
	 * <p>
	 * @param protocol      the {@link Protocol}
	 * @param hostName      the host name
	 * @param port          the port
	 * @param userName      the user name
	 * @param password      the password
	 * @param remoteDirPath the path to the remote directory
	 * @param mailFilter    the mail filter {@link String}
	 */
	public MailHandler(final Protocol protocol, final String hostName, final int port,
			final String userName, final String password, final String remoteDirPath,
			final String mailFilter) {
		this(protocol, hostName, port, userName, password, remoteDirPath,
				createSearchTerm(mailFilter));
	}

	/**
	 * Constructs a {@link MailHandler} with the specified {@link Protocol}, host name, port, user
	 * name, password, path to the remote directory and mail filter {@link SearchTerm}.
	 * <p>
	 * @param protocol      the {@link Protocol}
	 * @param hostName      the host name
	 * @param port          the port
	 * @param userName      the user name
	 * @param password      the password
	 * @param remoteDirPath the path to the remote directory
	 * @param mailFilter    the mail filter {@link SearchTerm}
	 */
	public MailHandler(final Protocol protocol, final String hostName, final int port,
			final String userName, final String password, final String remoteDirPath,
			final SearchTerm mailFilter) {
		this.protocol = protocol;
		this.hostName = hostName;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.remoteDirPath = remoteDirPath;
		this.mailFilter = mailFilter;
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link MailHandler} loaded from the specified {@link Properties} containing the
	 * {@link Protocol}, host name, port, user name, password, path to the remote directory and mail
	 * filter {@link SearchTerm}.
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
	 * Returns the {@link Protocol}.
	 * <p>
	 * @return the {@link Protocol}
	 */
	public Protocol getProtocol() {
		return protocol;
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
	 * Returns the port.
	 * <p>
	 * @return the port
	 */
	public int getPort() {
		return port;
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

	/**
	 * Returns the path to the remote directory.
	 * <p>
	 * @return the path to the remote directory
	 */
	public String getRemoteDirPath() {
		return remoteDirPath;
	}

	/**
	 * Returns the mail filter {@link SearchTerm}.
	 * <p>
	 * @return the mail filter {@link SearchTerm}
	 */
	public SearchTerm getMailFilter() {
		return mailFilter;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@link Protocol}.
	 * <p>
	 * @param protocol a {@link Protocol}
	 */
	public void setProtocol(final Protocol protocol) {
		this.protocol = protocol;
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
	 * Sets the port.
	 * <p>
	 * @param port an {@code int} value
	 */
	public void setPort(final int port) {
		this.port = port;
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

	/**
	 * Sets the path to the remote directory.
	 * <p>
	 * @param remoteDirPath a {@link String}
	 */
	public void setRemoteDirPath(final String remoteDirPath) {
		this.remoteDirPath = remoteDirPath;
	}

	/**
	 * Sets the mail filter {@link SearchTerm}.
	 * <p>
	 * @param mailFilter a {@link SearchTerm}
	 */
	public void setMailFilter(final SearchTerm mailFilter) {
		this.mailFilter = mailFilter;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

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


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Downloads the {@link List} of {@link MimeMessage} from the mail server with {@code this}
	 * parameters.
	 * <p>
	 * @return the {@link List} of {@link MimeMessage}
	 */
	public List<MimeMessage> download() {
		final List<MimeMessage> messages = new LinkedList<MimeMessage>();
		try {
			IO.info("Connect to the mail server ",
					Strings.quote(hostName + (port > 0 ? ":" + port : "")),
					" with ", Strings.quote(userName));
			final Properties properties = new Properties();
			properties.put("mail.store.protocol", protocol.toString());
			properties.put("mail." + protocol + ".host", hostName);
			properties.put("mail." + protocol + ".port", port);
			properties.put("mail." + protocol + ".timeout", Strings.toString(TIMEOUT));
			properties.put("mail." + protocol + ".connectiontimeout", Strings.toString(TIMEOUT));
			final Authenticator authenticator = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, password);
				}
			};
			final Session session = Session.getDefaultInstance(properties, authenticator);
			session.setDebug(IO.getSeverityLevel().isDebug());
			Store store = null;
			try {
				store = session.getStore(protocol.toString());
				store.connect(hostName, port, userName, password);

				IO.info("Download the filtered mails in ", Strings.quote(remoteDirPath));
				Folder folder = null;
				try {
					folder = store.getFolder(remoteDirPath);
					folder.open(Folder.READ_ONLY);
					for (final Message message : folder.getMessages()) {
						if (message.match(mailFilter)) {
							IO.info("Download the mail ", Strings.quote(message.getSubject()));
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

	/**
	 * Sends a mail with the specified subject {@link String} and content {@link String} to the
	 * specified comma-separated recipients {@link String} from the mail server with {@code this}
	 * parameters.
	 * <p>
	 * @param recipients the comma-separated recipients {@link String} to send to
	 * @param subject    the subject of the mail to send
	 * @param content    the content of the mail to send
	 * <p>
	 * @throws MessagingException if there is a problem with sending
	 */
	public void send(final String recipients, final String subject, final String content)
			throws MessagingException {
		// Connect to the mail server
		final Properties properties = new Properties();
		properties.put("mail.store.protocol", "smtp");
		properties.put("mail.smtp.host", hostName);
		final Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};
		final Session session = Session.getDefaultInstance(properties, authenticator);
		session.setDebug(true);

		// Compose the mail
		final MimeMultipart multipart = new MimeMultipart("alternative");
		final MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(content, "text/html");
		multipart.addBodyPart(bodyPart);

		// Send the mail
		final MimeMessage mail = new MimeMessage(session);
		mail.setFrom(new InternetAddress(userName));
		mail.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
		mail.setSubject(subject);
		mail.setContent(multipart);
		mail.setSentDate(new Date());
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
		protocol = Protocol.get(properties.getProperty("protocol"));
		hostName = properties.getProperty("hostName");
		port = Integers.convert(properties.getProperty("port", "-1"));
		userName = properties.getProperty("userName");
		password = properties.getProperty("password");
		remoteDirPath = properties.getProperty("remoteDir");
		mailFilter = createSearchTerm(properties.getProperty("mailFilter", STAR));
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
			throw new RuntimeException(Strings.toString(ex), ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum Protocol {
		POP("pop"),
		IMAP("imap"),
		SMTP("smtp");

		public final String value;

		private Protocol(final String value) {
			this.value = value;
		}

		public static Protocol get(final String name) {
			final String value = Strings.toUpperCase(name);
			if (POP.value.equals(value)) {
				return POP;
			} else if (IMAP.value.equals(value)) {
				return IMAP;
			} else if (SMTP.value.equals(value)) {
				return SMTP;
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
			return value;
		}
	}
}
