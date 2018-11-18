/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.integration.transfer.ftp;

import static jupiter.common.io.IO.IO;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import jupiter.common.util.Arrays;
import jupiter.common.util.Strings;

public class FTPHandler {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String REMOTE_SEPARATOR = "/";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Protocol protocol;
	protected String hostName;
	protected int port;
	protected String userName;
	protected String password;
	protected String remoteDir;
	protected String localDir;
	protected String filter;
	protected String[] fileNames;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public FTPHandler() {
	}

	public FTPHandler(final Protocol protocol, final String hostName, final int port,
			final String userName, final String password, final String remoteDir,
			final String localDir, final String filter, final String[] fileNames) {
		this.protocol = protocol;
		this.hostName = hostName;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.remoteDir = remoteDir;
		this.localDir = localDir;
		this.filter = filter;
		this.fileNames = fileNames;
	}

	public FTPHandler(final Properties properties) {
		loadProperties(properties);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void loadProperties(final Properties properties) {
		protocol = Protocol.get(properties.getProperty("protocol"));
		hostName = properties.getProperty("hostName");
		switch (protocol) {
			case SFTP:
				port = Integer.valueOf(properties.getProperty("port", "22"));
				break;
			default:
				port = Integer.valueOf(properties.getProperty("port", "21"));
		}
		userName = properties.getProperty("userName");
		password = properties.getProperty("password");
		remoteDir = properties.getProperty("remoteDir");
		localDir = properties.getProperty("localDir");
		filter = properties.getProperty("filter", "*");
		fileNames = properties.getProperty("fileNames").split(Arrays.DEFAULT_DELIMITER);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public int download(final Properties properties) {
		loadProperties(properties);
		return download();
	}

	public int download() {
		if (fileNames.length > 0) {
			if (Strings.isNotEmpty(fileNames[0])) {
				// Download the files
				final int downloadedFileCount;
				switch (protocol) {
					case FTP:
						filter = filter.replace("*", ".*");
						downloadedFileCount = downloadFTP();
						break;
					case FTPS:
						filter = filter.replace("*", ".*");
						downloadedFileCount = downloadFTPS();
						break;
					case SFTP:
						downloadedFileCount = downloadSFTP();
						break;
					default:
						downloadedFileCount = 0;
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

	/**
	 * Download the specified files from the specified FTP with the specified parameters and returns
	 * the number of downloaded files.
	 * <p>
	 * @since 1.6
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

					IO.info("Download the files ", Strings.quote(filter), " in ",
							Strings.quote(remoteDir));
					final FTPFile[] files = ftp.listFiles(remoteDir);
					for (final FTPFile file : files) {
						final String fileName = file.getName();
						if (file.isFile() && fileName.matches(filter) &&
								Strings.matches(fileName, fileNames)) {
							final String remotePath = remoteDir + REMOTE_SEPARATOR + fileName;
							final String localPath = localDir + File.separator + fileName;

							IO.info("Download the file ", Strings.quote(remotePath), " to ",
									Strings.quote(localPath));
							OutputStream output = null;
							try {
								output = new BufferedOutputStream(new FileOutputStream(localPath));
								final boolean isSuccess = ftp.retrieveFile(remotePath, output);
								if (isSuccess) {
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
	 * Download the specified files from the specified FTPS with the specified parameters and
	 * returns the number of downloaded files.
	 * <p>
	 * @return the number of downloaded files
	 * <p>
	 * @since 1.6
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

					IO.info("Download the files ", Strings.quote(filter), " in ",
							Strings.quote(remoteDir));
					final FTPFile[] files = ftps.listFiles(remoteDir);
					for (final FTPFile file : files) {
						final String fileName = file.getName();
						if (file.isFile() && fileName.matches(filter) &&
								Strings.matches(fileName, fileNames)) {
							final String remotePath = remoteDir + REMOTE_SEPARATOR + fileName;
							final String localPath = localDir + File.separator + fileName;

							IO.info("Download the file ", Strings.quote(remotePath), " to ",
									Strings.quote(localPath));
							OutputStream output = null;
							try {
								output = new BufferedOutputStream(new FileOutputStream(localPath));
								final boolean isSuccess = ftps.retrieveFile(remotePath, output);
								if (isSuccess) {
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
	 * Download the specified files from the specified SFTP with the specified parameters and
	 * returns the number of downloaded files.
	 * <p>
	 * @return the number of downloaded files
	 * <p>
	 * @since 1.6
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
			IO.info("Download the files ", Strings.quote(filter), " in ", Strings.quote(remoteDir));
			final Channel channel = session.openChannel("sftp");
			channel.connect();
			final ChannelSftp sftp = (ChannelSftp) channel;
			sftp.cd(remoteDir);
			final Vector<ChannelSftp.LsEntry> entries = sftp.ls(filter);
			for (final ChannelSftp.LsEntry entry : entries) {
				final String fileName = entry.getFilename();
				if (Strings.matches(fileName, fileNames)) {
					final String localPath = localDir + File.separator + fileName;

					IO.info("Download the file ", Strings.quote(fileName), " to ",
							Strings.quote(localPath));
					sftp.get(fileName, localPath);
					++downloadedFileCount;
				}
			}
			sftp.exit();
			session.disconnect();
			return downloadedFileCount;
		} catch (final JSchException ex) {
			IO.error(ex);
		} catch (final SftpException ex) {
			IO.error(ex);
		}
		return downloadedFileCount;
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

		@Override
		public String toString() {
			return value;
		}
	}
}
