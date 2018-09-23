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

public class Ftps {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Ftps() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int download(final Properties info) {
		final Protocol protocol = Protocol.get(info.getProperty("protocol"));
		final String hostname = info.getProperty("hostname");
		final Integer port = Integer
				.valueOf(info.getProperty("port", protocol.equals("sftp") ? "22" : "21"));
		final String username = info.getProperty("username");
		final String password = info.getProperty("password");
		final String remoteDir = info.getProperty("remoteDir");
		final String localDir = info.getProperty("localDir");
		String filter = info.getProperty("filter", "*");
		final String[] filenames = info.getProperty("filenames").split(Arrays.DEFAULT_DELIMITER);

		if (filenames.length > 0) {
			if (Strings.isNotEmpty(filenames[0])) {
				// Download the files
				final int downloadedFileCount;
				switch (protocol) {
					case FTP:
						filter = filter.replace("*", ".*");
						downloadedFileCount = downloadFtp(hostname, port, username, password,
								remoteDir, localDir, filter, filenames);
						break;
					case FTPS:
						filter = filter.replace("*", ".*");
						downloadedFileCount = downloadFtps(hostname, port, username, password,
								remoteDir, localDir, filter, filenames);
						break;
					case SFTP:
						downloadedFileCount = downloadSftp(hostname, port, username, password,
								remoteDir, localDir, filter, filenames);
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
			IO.error("Empty filename");
			return 0;
		}
		IO.info("No filename");
		return 0;
	}

	/**
	 * Download the specified files from the specified FTP with the specified parameters and returns
	 * the number of downloaded files.
	 * <p>
	 * @param hostname  the host name
	 * @param port      the port
	 * @param username  the username
	 * @param password  the password
	 * @param remoteDir the remote directory where to get the files
	 * @param localDir  the local directory where to store the files
	 * @param filter    a file filter (can be a regular expression)
	 * @param filenames the array of files to download (can be regular expressions)
	 * <p>
	 * @return the number of downloaded files
	 * <p>
	 * @since 1.6
	 */
	protected static int downloadFtp(final String hostname, final int port, final String username,
			final String password, final String remoteDir, final String localDir,
			final String filter, final String[] filenames) {
		int downloadedFileCount = 0;
		final FTPClient ftp = new FTPClient();
		ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		try {
			IO.info("Connect to ", hostname, ":", port);
			ftp.connect(hostname, port);
			final int replyCode = ftp.getReplyCode();
			if (FTPReply.isPositiveCompletion(replyCode)) {
				ftp.enterLocalPassiveMode();

				IO.info("Login with ", username, " and ", password);
				if (ftp.login(username, password)) {
					ftp.pwd();
					ftp.setFileTransferMode(FTPClient.PASSIVE_REMOTE_DATA_CONNECTION_MODE);
					ftp.setFileType(FTP.BINARY_FILE_TYPE);

					IO.info("Download the files ", filter, " in ", remoteDir);
					final FTPFile[] files = ftp.listFiles(remoteDir);
					for (final FTPFile file : files) {
						final String filename = file.getName();
						if (file.isFile() && filename.matches(filter) &&
								Strings.matches(filename, filenames)) {
							final String remotePath = remoteDir + "/" + filename;
							final String localPath = localDir + "/" + filename;

							IO.info("Download the file ", remotePath, " to ", localPath);
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
					IO.error("Fail to login to ", Strings.quote(hostname), " with ",
							Strings.quote(username));
				}
			} else {
				IO.error("Fail to connect to ", Strings.quote(hostname), "; reply code: ",
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
	 * @param hostname  the host name
	 * @param port      the port
	 * @param username  the username
	 * @param password  the password
	 * @param remoteDir the remote directory where to get the files
	 * @param localDir  the local directory where to store the files
	 * @param filter    a file filter (can be a regular expression)
	 * @param filenames the array of files to download (can be regular expressions)
	 * <p>
	 * @return the number of downloaded files
	 * <p>
	 * @since 1.6
	 */
	protected static int downloadFtps(final String hostname, final int port, final String username,
			final String password, final String remoteDir, final String localDir,
			final String filter, final String[] filenames) {
		int downloadedFileCount = 0;
		final FTPSClient ftps = new FTPSClient(); // SSL/TLS
		ftps.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		try {
			IO.info("Connect to ", hostname, ":", port);
			ftps.connect(hostname, port);
			final int replyCode = ftps.getReplyCode();
			if (FTPReply.isPositiveCompletion(replyCode)) {
				ftps.enterLocalPassiveMode();

				IO.info("Login with ", username, " and ", password);
				if (ftps.login(username, password)) {
					ftps.execPBSZ(0);
					ftps.execPROT("P");
					ftps.pwd();
					ftps.setFileTransferMode(FTPClient.PASSIVE_REMOTE_DATA_CONNECTION_MODE);
					ftps.setFileType(FTP.BINARY_FILE_TYPE);

					IO.info("Download the files ", filter, " in ", remoteDir);
					final FTPFile[] files = ftps.listFiles(remoteDir);
					for (final FTPFile file : files) {
						final String filename = file.getName();
						if (file.isFile() && filename.matches(filter) &&
								Strings.matches(filename, filenames)) {
							final String remotePath = remoteDir + "/" + filename;
							final String localPath = localDir + "/" + filename;

							IO.info("Download the file ", remotePath, " to ", localPath);
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
					IO.error("Fail to login to ", Strings.quote(hostname), " with ",
							Strings.quote(username));
				}
			} else {
				IO.error("Fail to connect to ", Strings.quote(hostname), "; reply code: ",
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
	 * @param hostname  the host name
	 * @param port      the port
	 * @param username  the username
	 * @param password  the password
	 * @param remoteDir the remote directory where to get the files
	 * @param localDir  the local directory where to store the files
	 * @param filter    a file filter (can be a regular expression)
	 * @param filenames the array of files to download (can be regular expressions)
	 * <p>
	 * @return the number of downloaded files
	 * <p>
	 * @since 1.6
	 */
	protected static int downloadSftp(final String hostname, final int port, final String username,
			final String password, final String remoteDir, final String localDir,
			final String filter, final String[] filenames) {
		int downloadedFileCount = 0;
		final JSch jsch = new JSch();
		Session session;
		try {
			// Connect and login to host
			IO.info("Connect to ", hostname, ":", port, " with ", username);
			session = jsch.getSession(username, hostname, 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect();

			// Retrieve the files
			IO.info("Download the files ", filter, " in ", remoteDir);
			final Channel channel = session.openChannel("sftp");
			channel.connect();
			final ChannelSftp sftp = (ChannelSftp) channel;
			sftp.cd(remoteDir);
			final Vector<ChannelSftp.LsEntry> entries = sftp.ls(filter);
			for (final ChannelSftp.LsEntry entry : entries) {
				final String filename = entry.getFilename();
				if (Strings.matches(filename, filenames)) {
					final String localPath = localDir + File.separator + filename;

					IO.info("Download the file ", filename, " to ", localPath);
					sftp.get(filename, localPath);
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
