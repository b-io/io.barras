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
package jupiter.lang.python;

import static jupiter.common.io.InputOutput.IO;

import java.io.IOException;
import java.io.StringWriter;

import jupiter.common.io.IOHandler;
import jupiter.common.io.InputOutput;
import jupiter.common.io.Systems;
import jupiter.common.model.ICloneable;
import jupiter.common.thread.WorkQueue;
import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

import org.python.util.PythonInterpreter;

public class Python {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default {@link PythonInterpreter}.
	 */
	public static final PythonInterpreter DEFAULT_INTERPRETER = new PythonInterpreter();

	/**
	 * The default {@link StringWriter}.
	 */
	public static final StringWriter DEFAULT_INTERPRETER_OUTPUT = new StringWriter();

	static {
		DEFAULT_INTERPRETER.setOut(DEFAULT_INTERPRETER_OUTPUT);
	}

	//////////////////////////////////////////////

	/**
	 * The default {@link PythonPrinter}.
	 */
	public static final PythonPrinter DEFAULT_PRINTER = new PythonPrinter();

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile String PYTHON_SCRIPT_PATH = "python";
	public static volatile String[] ARGS = Strings.EMPTY_ARRAY;

	//////////////////////////////////////////////

	public static volatile String[] LIGHT_PACKAGES = new String[] {
		// • API
		// - System
		"fire", // generate command line interfaces (CLIs) from absolutely any Python object

		// • Data Science
		"numpy", // provide powerful capabilities to create arrays of structured datatype
		"scikit-learn", // provide simple and efficient tools for predictive data analysis
		"scikit-lego", // provide extensions to Scikit-Learn
		"scipy", // provide special functions, integration, ordinary differential equation (ODE) solvers, gradient optimization, parallel programming tools, an expression-to-C++ compiler for fast execution and others
		"statsmodels", // provide a complement to SciPy for statistical computations including descriptive statistics and estimation and inference for statistical models

		// • DB
		"mysql-connector-python", // connect to MySQL
		"psycopg2", // connect to PostgreSQL
		"pymongo", // connect to MongoDB
		"pymssql", // connect to Microsoft SQL Server
		"sqlalchemy", // provide a full suite of well known enterprise-level persistence patterns, designed for efficient and high-performing database access, adapted into a simple and Pythonic domain language

		// • File
		"flashtext", // search and replace words in a document
		"pathlib", // provide classes representing filesystem paths with semantics appropriate for different operating systems

		// • GUI
		"matplotlib", // create static, animated and interactive visualizations
		"plotly", // build front end for ML and data science models
		"seaborn", // provide a high-level interface for drawing attractive and informative statistical graphics (based on Matplotlib)

		// • Media
		"opencv-python", // provide computer vision, ML and image processing capabilities
		"xhtml2pdf", // convert XHTML to PDF

		// • Time Series
		"pandas", // provide a rich set of methods, containers and data types

		// • Util
		"arrow", // parse date-time data, extract/update components of a date-time and manipulate date-time and time-span objects
		"javaproperties", // provide support for reading and writing Java properties files (both the simple line-oriented format and XML)
		"nutil", // provide utility functions
		"psutil", // provide information on running processes and system utilization
		"python-dateutil", // provide extensions to the standard datetime module
		"validators", // provide data validation for humans

		// • Web
		"requests" // send HTTP requests
	};

	public static volatile String[] PACKAGES = Arrays.concat(LIGHT_PACKAGES, new String[] {
		// • API
		"pycountry", // provides the ISO standards for languages, countries, currencies and scripts

		// • Data Science
		"keras", // build and deploy ML models easily (built on top of TensorFlow with GPU capability)
		"nltk", // provide a suite of text processing libraries for classification, tokenization, stemming, tagging, parsing and semantic reasoning
		"tensorflow", // build and deploy ML models (with GPU capability)
		"theano", // define, optimize and efficiently evaluate mathematical expressions involving multi-dimensional arrays (built on top of NumPy with GPU capability)
		"torch", // provide tensor computation (like NumPy but with GPU capability) and automatic differentiation for building and training neural networks

		// • GUI
		"colormap", // provide simple utilities to convert colors between RGB, HEX, HLS, HUV and build colormaps for Matplotlib
		"dash", // build ML and data science web apps
		"tkinter", // provide a wrapper for the cross-platform wxWidgets GUI toolkit (which is written in C)
		"wxpython", // provide a wrapper for the cross-platform Tk GUI toolkit (which is written in C++)

		// • Media
		"fpdf", // generate PDF documents
		"imutils", // provide basic image processing functions such as translation, rotation, resizing and skeletonization
		"kaleido", // generate static images for web-based visualization libraries
		"openpyxl", // read/write Excel 2010 xlsx/xlsm/xltx/xltm files
		"pillow", // provide image processing capabilities
		"reportlab", // generate PDF documents and graphics

		// • Util
		"easydev" // provide miscellaneous functions that are repeatidly used during the development of Python packages
	});


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Python}.
	 */
	protected Python() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Installs the {@code PACKAGES}.
	 * <p>
	 * @param args ignored
	 */
	public static void main(final String[] args) {
		installPackages(PACKAGES);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int installPackage(final String name) {
		try {
			return Systems.execute(DEFAULT_PRINTER, "pip", "install", "-U", name);
		} catch (final InterruptedException ex) {
			IO.error(ex);
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return InputOutput.EXIT_FAILURE;
	}

	public static int[] installPackages(final String... names) {
		final int[] status = new int[names.length];
		for (int ni = 0; ni < names.length; ++ni) {
			status[ni] = installPackage(names[ni]);
		}
		return status;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Executes the specified Python script and returns the exit value.
	 * <p>
	 * @param script the Python script to execute
	 * <p>
	 * @return the exit value of the specified Python script
	 */
	public static int executeScript(final String... script) {
		return executeScript(DEFAULT_PRINTER, script);
	}

	/**
	 * Executes the specified Python script, prints the output with the specified printer
	 * {@link IOHandler} and returns the exit value.
	 * <p>
	 * @param printer the printer {@link IOHandler}
	 * @param script  the Python script to execute
	 * <p>
	 * @return the exit value of the specified Python script
	 */
	public static int executeScript(final IOHandler printer, final String... script) {
		try {
			return Systems.execute(printer,
					Arrays.concat(new String[] {PYTHON_SCRIPT_PATH}, script, ARGS));
		} catch (final InterruptedException ex) {
			IO.error(ex);
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return InputOutput.EXIT_FAILURE;
	}

	//////////////////////////////////////////////

	/**
	 * Executes the specified Python command, prints the output and returns it.
	 * <p>
	 * @param command the Python command to execute
	 * <p>
	 * @return the output {@link String} of the specified Python command, or {@code null} if there
	 *         is a problem with the execution
	 */
	public static String execute(final String command) {
		return execute(DEFAULT_PRINTER, command);
	}

	/**
	 * Executes the specified Python command, prints the output with the specified printer
	 * {@link IOHandler} and returns it.
	 * <p>
	 * @param printer the printer {@link IOHandler}
	 * @param command the Python command to execute
	 * <p>
	 * @return the output {@link String} of the specified Python command, or {@code null} if there
	 *         is a problem with the execution
	 */
	public static String execute(final IOHandler printer, final String command) {
		// Check the system
		Systems.requireOS();

		// Execute the Python command, print the output with the printer and return it
		try {
			final StringWriter writer = new StringWriter();
			DEFAULT_INTERPRETER.setOut(writer);
			DEFAULT_INTERPRETER.exec(command);
			final String output = writer.toString();
			printer.println(output, false);
			return output;
		} catch (final Exception ex) {
			IO.error(ex);
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class PythonPrinter
			extends IOHandler {

		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The message prefix {@link String}.
		 */
		protected static final String PREFIX = "Python> ";

		/**
		 * The printer {@link IOHandler}.
		 */
		protected final IOHandler printer;

		/**
		 * The {@link WorkQueue} used for monitoring.
		 */
		protected WorkQueue<?, ?> workQueueToMonitor;

		/**
		 * Constructs a {@link PythonPrinter}.
		 */
		protected PythonPrinter() {
			this(IO.getPrinter());
		}

		/**
		 * Constructs a {@link PythonPrinter} with the specified printer {@link IOHandler}.
		 * <p>
		 * @param printer the printer {@link IOHandler}
		 */
		protected PythonPrinter(final IOHandler printer) {
			super();
			this.printer = printer;
		}

		public void setWorkQueueToMonitor(final WorkQueue<?, ?> workQueueToMonitor) {
			this.workQueueToMonitor = workQueueToMonitor;
		}

		@Override
		public boolean print(final Object content, final boolean isError) {
			final String text = Objects.toString(content);
			final boolean status = printer.print(PREFIX.concat(text), isError);
			if (isError) {
				monitor(text);
			}
			return status;
		}

		@Override
		public boolean println(final Object content, final boolean isError) {
			final String text = Objects.toString(content);
			final boolean status = printer.println(PREFIX.concat(text), isError);
			if (isError) {
				monitor(text);
			}
			return status;
		}

		protected void monitor(final String text) {
			final String lowerCaseText = text.toLowerCase();
			if (workQueueToMonitor != null && !lowerCaseText.contains("warning") &&
					(lowerCaseText.contains("error") || lowerCaseText.contains("invalid"))) {
				IO.warn("Restart the work queue");
				workQueueToMonitor.restart(true);
			}
		}

		/**
		 * Closes {@code this}.
		 */
		@Override
		public void close() {
			printer.close();
		}

		/**
		 * Clones {@code this}.
		 * <p>
		 * @return a clone of {@code this}
		 *
		 * @see ICloneable
		 */
		@Override
		public PythonPrinter clone() {
			return new PythonPrinter(printer);
		}
	}
}
