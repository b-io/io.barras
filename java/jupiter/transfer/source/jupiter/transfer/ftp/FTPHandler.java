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
package jupiter.transfer.ftp;

import static jupiter.common.io.IO.IO;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import jupiter.common.exception.IllegalTypeException;
import jupiter.common.io.Resources;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Arrays;
import jupiter.common.util.Integers;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class FTPHandler
		implements ICloneable<FTPHandler>, Serializable {

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
	public static final String REMOTE_SEPARATOR = "/";


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
	 * The path to the local directory.
	 */
	protected String localDirPath;
	/**
	 * The file filter {@link String}.
	 */
	protected String fileFilter;
	/**
	 * The array of file names.
	 */
	protected String[] fileNames;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link FTPHandler}.
	 */
	public FTPHandler() {
	}

	/**
	 * Constructs a {@link FTPHandler} with the specified {@link Protocol}, host name, port, user
	 * name, password, path to the remote directory, path to the local directory, file filter
	 * {@link String} and array of file names.
	 * <p>
	 * @param protocol      the {@link Protocol}
	 * @param hostName      the host name
	 * @param port          the port
	 * @param userName      the user name
	 * @param password      the password
	 * @param remoteDirPath the path to the remote directory
	 * @param localDirPath  the path to the local directory
	 * @param fileFilter    the file filter {@link String}
	 * @param fileNames     the array of file names
	 */
	public FTPHandler(final Protocol protocol, final String hostName, final int port,
			final String userName, final String password, final String remoteDirPath,
			final String localDirPath, final String fileFilter, final String[] fileNames) {
		this.protocol = protocol;
		this.hostName = hostName;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.remoteDirPath = remoteDirPath;
		this.localDirPath = localDirPath;
		this.fileFilter = fileFilter;
		this.fileNames = fileNames;
	}

	/**
	 * Constructs a {@link FTPHandler} loaded from the specified {@link Properties} containing the
	 * {@link Protocol}, host name, port, user name, password, path to the remote directory, path to
	 * the local directory, file filter {@link String} and array of file names.
	 * <p>
	 * @param properties the {@link Properties} to load
	 */
	public FTPHandler(final Properties properties) {
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
	 * Returns the path to the local directory.
	 * <p>
	 * @return the path to the local directory
	 */
	public String getLocalDirPath() {
		return localDirPath;
	}

	/**
	 * Returns the file filter {@link String}.
	 * <p>
	 * @return the file filter {@link String}
	 */
	public String getFileFilter() {
		return fileFilter;
	}

	/**
	 * Returns the array of file names.
	 * <p>
	 * @return the array of file names
	 */
	public String[] getFileNames() {
		return fileNames;
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
	 * Sets the path to the local directory.
	 * <p>
	 * @param localDirPath a {@link String}
	 */
	public void setLocalDirPath(final String localDirPath) {
		this.localDirPath = localDirPath;
	}

	/**
	 * Sets the file filter {@link String}.
	 * <p>
	 * @param fileFilter a {@link String}
	 */
	public void setFileFilter(final String fileFilter) {
		this.fileFilter = fileFilter;
	}

	/**
	 * Sets the array of file names.
	 * <p>
	 * @param fileNames an array of {@link String}
	 */
	public void setFileNames(final String[] fileNames) {
		this.fileNames = fileNames;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Downloads the files from the FTP, FTPS or SFTP with {@code this} parameters and returns the
	 * number of downloaded files.
	 * <p>
	 * @return the number of downloaded files
	 */
	public int download() {
		if (fileNames.length > 0) {
			if (Strings.isNotEmpty(fileNames[0])) {
				// Download the files
				final int downloadedFileCount;
				switch (protocol) {
					case FTP:
						fileFilter = fileFilter.replace("*", ".*");
						downloadedFileCount = downloadFTP();
						break;
					case FTPS:
						fileFilter = fileFilter.replace("*", ".*");
						downloadedFileCount = downloadFTPS();
						break;
					case SFTP:
						downloadedFileCount = downloadSFTP();
						break;
					default:
						throw new IllegalTypeException(protocol);
				}
				if (downloadedFileCount > 0) {
					IO.info(downloadedFileCount, " files downloaded");
				} else {
					IO.warn("No files downloaded");
				}
				return downloadedFileCount;
			}
			IO.error("Empty file name");
			return 0;
		}
		IO.info("No file name");
		return 0;
	}

	//////////////////////////////////////////////

	/**
	 * Downloads the files from the FTP with {@code this} parameters and returns the number of
	 * downloaded files.
	 * <p>
	 * @return the number of downloaded files
	 */
	protected int downloadFTP() {
		int downloadedFileCount = 0;
		final FTPClient ftp = new FTPClient();
		ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		try {
			IO.info("Connect to ", Strings.quote(hostName + ":" + port));
			ftp.connect(hostName, port);
			final int replyCode = ftp.getReplyCode();
			if (FTPReply.isPositiveCompletion(replyCode)) {
				ftp.enterLocalPassiveMode();

				IO.info("Login with ", Strings.quote(userName), " and ", Strings.quote(password));
				if (ftp.login(userName, password)) {
					ftp.pwd();
					ftp.setFileTransferMode(FTPClient.PASSIVE_REMOTE_DATA_CONNECTION_MODE);
					ftp.setFileType(FTP.BINARY_FILE_TYPE);

					IO.info("Download the files ", Strings.quote(fileFilter), " in ",
							Strings.quote(remoteDirPath));
					final FTPFile[] files = ftp.listFiles(remoteDirPath);
					for (final FTPFile file : files) {
						final String fileName = file.getName();
						if (file.isFile() && fileName.matches(fileFilter) &&
								Strings.matches(fileName, fileNames)) {
							final String remotePath = remoteDirPath + REMOTE_SEPARATOR + fileName;
							final String localPath = localDirPath + File.separator + fileName;

							IO.info("Download the file ", Strings.quote(remotePath), " to ",
									Strings.quote(localPath));
							OutputStream output = null;
							try {
								output = new BufferedOutputStream(new FileOutputStream(localPath));
								if (ftp.retrieveFile(remotePath, output)) {
									++downloadedFileCount;
								} else {
									IO.error("Unable to download the file ",
											Strings.quote(remotePath), " to ",
											Strings.quote(localPath));
								}
							} finally {
								Resources.close(output);
							}
						}
					}
				} else {
					IO.error("Fail to login to ", Strings.quote(hostName), " with ",
							Strings.quote(userName));
				}
			} else {
				IO.error("Fail to connect to ", Strings.quote(hostName), "; reply code: ",
						replyCode);
			}
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			try {
				if (ftp.isConnected()) {
					ftp.logout();
					ftp.disconnect();
				}
			} catch (final IOException ignored) {
			}
		}
		return downloadedFileCount;
	}

	/**
	 * Downloads the files from the FTPS with {@code this} parameters and returns the number of
	 * downloaded files.
	 * <p>
	 * @return the number of downloaded files
	 */
	protected int downloadFTPS() {
		int downloadedFileCount = 0;
		final FTPSClient ftps = new FTPSClient(); // SSL/TLS
		ftps.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		try {
			IO.info("Connect to ", Strings.quote(hostName + ":" + port));
			ftps.connect(hostName, port);
			final int replyCode = ftps.getReplyCode();
			if (FTPReply.isPositiveCompletion(replyCode)) {
				ftps.enterLocalPassiveMode();

				IO.info("Login with ", Strings.quote(userName), " and ", Strings.quote(password));
				if (ftps.login(userName, password)) {
					ftps.execPBSZ(0);
					ftps.execPROT("P");
					ftps.pwd();
					ftps.setFileTransferMode(FTPClient.PASSIVE_REMOTE_DATA_CONNECTION_MODE);
					ftps.setFileType(FTP.BINARY_FILE_TYPE);

					IO.info("Download the files ", Strings.quote(fileFilter), " in ",
							Strings.quote(remoteDirPath));
					final FTPFile[] files = ftps.listFiles(remoteDirPath);
					for (final FTPFile file : files) {
						final String fileName = file.getName();
						if (file.isFile() && fileName.matches(fileFilter) &&
								Strings.matches(fileName, fileNames)) {
							final String remotePath = remoteDirPath + REMOTE_SEPARATOR + fileName;
							final String localPath = localDirPath + File.separator + fileName;

							IO.info("Download the file ", Strings.quote(remotePath), " to ",
									Strings.quote(localPath));
							OutputStream output = null;
							try {
								output = new BufferedOutputStream(new FileOutputStream(localPath));
								if (ftps.retrieveFile(remotePath, output)) {
									++downloadedFileCount;
								} else {
									IO.error("Unable to download the file ",
											Strings.quote(remotePath), " to ",
											Strings.quote(localPath));
								}
							} finally {
								Resources.close(output);
							}
						}
					}
				} else {
					IO.error("Fail to login to ", Strings.quote(hostName), " with ",
							Strings.quote(userName));
				}
			} else {
				IO.error("Fail to connect to ", Strings.quote(hostName), "; reply code: ",
						replyCode);
			}
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			try {
				if (ftps.isConnected()) {
					ftps.logout();
					ftps.disconnect();
				}
			} catch (final IOException ignored) {
			}
		}
		return downloadedFileCount;
	}

	/**
	 * Downloads the files from the SFTP with {@code this} parameters and returns the number of
	 * downloaded files.
	 * <p>
	 * @return the number of downloaded files
	 */
	protected int downloadSFTP() {
		int downloadedFileCount = 0;
		final JSch jsch = new JSch();
		Session session;
		try {
			// Connect and login to host
			IO.info("Connect to ", Strings.quote(hostName + ":" + port), " with ",
					Strings.quote(userName));
			session = jsch.getSession(userName, hostName, 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect();

			// Retrieve the files
			IO.info("Download the files ", Strings.quote(fileFilter), " in ",
					Strings.quote(remoteDirPath));
			final Channel channel = session.openChannel("sftp");
			channel.connect();
			final ChannelSftp sftp = (ChannelSftp) channel;
			if (!Strings.isNullOrEmpty(remoteDirPath)) {
				sftp.cd(remoteDirPath);
			}
			final Vector<ChannelSftp.LsEntry> entries = sftp.ls(fileFilter);
			for (final ChannelSftp.LsEntry entry : entries) {
				final String fileName = entry.getFilename();
				if (Strings.matches(fileName, fileNames)) {
					final String localPath = localDirPath + File.separator + fileName;

					IO.info("Download the file ", Strings.quote(fileName), " to ",
							Strings.quote(localPath));
					sftp.get(fileName, localPath);
					++downloadedFileCount;
				}
			}
			sftp.exit();
			session.disconnect();
		} catch (final JSchException ex) {
			IO.error(ex);
		} catch (final SftpException ex) {
			IO.error(ex);
		}
		return downloadedFileCount;
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
		switch (protocol) {
			case FTP:
			case FTPS:
				port = Integers.convert(properties.getProperty("port", "21"));
				break;
			case SFTP:
				port = Integers.convert(properties.getProperty("port", "22"));
				break;
			default:
				throw new IllegalTypeException(protocol);
		}
		userName = properties.getProperty("userName");
		password = properties.getProperty("password");
		remoteDirPath = properties.getProperty("remoteDir");
		localDirPath = properties.getProperty("localDir");
		fileFilter = properties.getProperty("fileFilter", "*");
		fileNames = properties.getProperty("fileNames").split(Arrays.DEFAULT_DELIMITER);
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
	public FTPHandler clone() {
		try {
			final FTPHandler clone = (FTPHandler) super.clone();
			clone.fileNames = Objects.clone(fileNames);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new RuntimeException(Strings.toString(ex), ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum Protocol {
		FTP("FTP"),
		FTPS("FTPS"),
		SFTP("SFTP");

		public final String value;

		private Protocol(final String value) {
			this.value = value;
		}

		public static Protocol get(final String name) {
			final String value = Strings.toUpperCase(name);
			if (FTP.value.equals(value)) {
				return FTP;
			} else if (FTPS.value.equals(value)) {
				return FTPS;
			} else if (SFTP.value.equals(value)) {
				return SFTP;
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
