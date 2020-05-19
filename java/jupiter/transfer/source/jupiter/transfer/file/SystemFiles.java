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
package jupiter.transfer.file;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Strings.EMPTY;
import static jupiter.common.util.Strings.STAR;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import jupiter.common.io.file.Files;
import jupiter.common.properties.Properties;
import jupiter.common.struct.list.ExtendedLinkedList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public class SystemFiles {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link SystemFiles}.
	 */
	protected SystemFiles() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static IOFileFilter createNameFilter(final String nameFilter) {
		return new IOFileFilter() {
			public boolean accept(final File file) {
				return file.getName().matches(nameFilter);
			}

			public boolean accept(final File file, final String text) {
				return accept(file);
			}
		};
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void clean(final File dir)
			throws IOException {
		FileUtils.cleanDirectory(dir);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Collection<File> find(final File dir, final String fileFilter) {
		return find(dir, fileFilter, EMPTY);
	}

	public static Collection<File> find(final File dir, final String fileFilter,
			final String dirFilter) {
		return FileUtils.listFiles(dir, createNameFilter(fileFilter), createNameFilter(dirFilter));
	}

	//////////////////////////////////////////////

	public static ExtendedLinkedList<File> find(final File dir, final String... fileFilters) {
		return find(dir, fileFilters, EMPTY);
	}

	public static ExtendedLinkedList<File> find(final File dir, final String[] fileFilters,
			final String dirFilter) {
		final ExtendedLinkedList<File> files = new ExtendedLinkedList<File>();
		for (final String fileFilter : fileFilters) {
			files.addAll(find(dir, fileFilter, dirFilter));
		}
		return files;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static ExtendedLinkedList<File> filterByExtensions(
			final Collection<? extends File> files, final Set<String> extensions) {
		final ExtendedLinkedList<File> filteredFiles = new ExtendedLinkedList<File>();
		for (final File file : files) {
			if (extensions.contains(Files.getExtension(file.getName()).toLowerCase())) {
				filteredFiles.add(file);
			}
		}
		return filteredFiles;
	}

	public static ExtendedLinkedList<File> filterByKeyword(final Collection<? extends File> files,
			final String keyword) {
		final ExtendedLinkedList<File> filteredFiles = new ExtendedLinkedList<File>();
		for (final File file : files) {
			if (file.getName().contains(keyword)) {
				filteredFiles.add(file);
			}
		}
		return filteredFiles;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int unzip(final Properties properties) {
		final File localDir = new File(properties.getProperty("localDir"));
		final String filter = properties.getProperty("filter", STAR).replace(STAR, ".*");
		final String[] fileNames = properties.getPropertyArray("fileNames");

		// Check the file names
		if (fileNames.length == 0) {
			IO.warn("No file names");
			return 0;
		}
		if (fileNames[0].isEmpty()) {
			IO.warn("Empty file name");
			return 0;
		}

		// Unzip the files
		int unzippedFileCount = 0;
		for (final String fileName : fileNames) {
			final Collection<File> files = find(localDir, fileName, "zip");
			for (final File file : files) {
				if (file.isFile() && fileName.matches(filter)) {
					unzippedFileCount += Files.unzipDir(file, localDir);
				}
			}
		}
		if (unzippedFileCount > 0) {
			IO.debug(unzippedFileCount, " files unzipped");
		} else {
			IO.warn("No files unzipped");
		}
		return unzippedFileCount;
	}
}
