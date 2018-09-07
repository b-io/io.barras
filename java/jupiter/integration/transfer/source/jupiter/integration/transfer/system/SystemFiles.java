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
package jupiter.integration.transfer.system;

import static jupiter.common.io.IO.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import jupiter.common.io.Resources;
import jupiter.common.io.file.Files;
import jupiter.common.util.Arrays;
import jupiter.common.util.Strings;

public class SystemFiles {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected SystemFiles() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static IOFileFilter createFilter(final String pattern) {
		return new IOFileFilter() {
			public boolean accept(final File file) {
				return file.getName().matches(pattern);
			}

			public boolean accept(final File file, final String string) {
				return accept(file);
			}
		};
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void clean(final File dir)
			throws IOException {
		FileUtils.cleanDirectory(dir);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Collection<File> find(final String dirname, final String fileFilter) {
		return find(new File(dirname), fileFilter, Strings.EMPTY);
	}

	public static Collection<File> find(final File dir, final String fileFilter) {
		return find(dir, fileFilter, Strings.EMPTY);
	}

	public static Collection<File> find(final String dirname, final String fileFilter,
			final String dirFilter) {
		return find(new File(dirname), fileFilter, dirFilter);
	}

	public static Collection<File> find(final File dir, final String fileFilter,
			final String dirFilter) {
		return FileUtils.listFiles(dir, createFilter(fileFilter), createFilter(dirFilter));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Collection<File> find(final String dirname, final String[] fileFilters) {
		return find(new File(dirname), fileFilters, Strings.EMPTY);
	}

	public static Collection<File> find(final File dir, final String[] fileFilters) {
		return find(dir, fileFilters, Strings.EMPTY);
	}

	public static Collection<File> find(final String dirname, final String[] fileFilters,
			final String dirFilter) {
		return find(new File(dirname), fileFilters, dirFilter);
	}

	public static Collection<File> find(final File dir, final String[] fileFilters,
			final String dirFilter) {
		final Collection<File> files = new LinkedList<File>();
		for (final String fileFilter : fileFilters) {
			files.addAll(find(dir, fileFilter, dirFilter));
		}
		return files;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Collection<File> filterByExtensions(final Collection<File> files,
			final Set<String> extensions) {
		final Collection<File> filteredFiles = new LinkedList<File>();
		for (final File file : files) {
			if (extensions.contains(Files.getExtension(file.getName()).toLowerCase())) {
				filteredFiles.add(file);
			}
		}
		return filteredFiles;
	}

	public static Collection<File> filterByKeyword(final Collection<File> files,
			final String keyword) {
		final Collection<File> filteredFiles = new LinkedList<File>();
		for (final File file : files) {
			if (file.getName().contains(keyword)) {
				filteredFiles.add(file);
			}
		}
		return filteredFiles;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int unzip(final Properties info) {
		final String localDir = info.getProperty("localDir");
		final String filter = info.getProperty("filter", "*").replace("*", ".*");
		final String[] filenames = info.getProperty("filenames").split(Arrays.DEFAULT_DELIMITER);
		if (filenames.length > 0) {
			if (Strings.isNotEmpty(filenames[0])) {
				// Unzip the files
				int unzippedFileCount = 0;
				for (final String filename : filenames) {
					final Collection<File> files = SystemFiles.find(localDir, filename, "zip");
					for (final File file : files) {
						if (file.isFile() && filename.matches(filter)) {
							unzippedFileCount += unzip(file.getAbsolutePath(), localDir);
						}
					}
				}
				if (unzippedFileCount > 0) {
					IO.info(unzippedFileCount, " files unzipped");
				} else {
					IO.warn("No files unzipped");
				}
				return unzippedFileCount;
			}
			IO.error("Empty filename");
			return 0;
		}
		IO.info("No filename");
		return 0;
	}

	/**
	 * Unzip the specified file and returns the number of unzipped files.
	 *
	 * @param pathname  the pathname of the file to unzip
	 * @param targetDir the output directory
	 *
	 * @return the number of unzipped files
	 */
	protected static int unzip(final String pathname, final String targetDir) {
		int n = 0;
		final byte[] buffer = new byte[1024];
		try {
			// Create the target directory is not exists
			final File folder = new File(targetDir);
			if (!folder.exists()) {
				folder.mkdir();
			}
			// Create output directory is not exists
			ZipInputStream zis = null;
			try {
				// Get the zip file content
				zis = new ZipInputStream(new FileInputStream(pathname));
				// Get the zipped file list entry
				ZipEntry ze;
				while ((ze = zis.getNextEntry()) != null) {
					FileOutputStream fos = null;
					try {
						// Create the target directory
						final File targetFile = new File(targetDir + File.separator + ze.getName());
						new File(targetFile.getParent()).mkdirs();
						// Unzip the file
						IO.info("Unzip ", targetFile.getAbsoluteFile());
						fos = new FileOutputStream(targetFile);
						int length;
						while ((length = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, length);
						}
						++n;
					} catch (final IOException ex) {
						IO.error(ex);
					} finally {
						Resources.close(fos);
					}
				}
			} finally {
				if (zis != null) {
					zis.closeEntry();
				}
			}
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return n;
	}
}
