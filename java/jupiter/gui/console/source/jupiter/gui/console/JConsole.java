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
package jupiter.gui.console;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.ESCAPE;
import static jupiter.common.util.Formats.DEFAULT_CHARSET;
import static jupiter.common.util.Formats.NEW_LINE;
import static jupiter.common.util.Strings.EMPTY;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import jupiter.common.io.InputOutput.SeverityLevel;
import jupiter.common.io.console.ConsoleHandler;
import jupiter.common.io.console.ConsoleHandler.Color;
import jupiter.common.io.console.IConsole;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.list.Index;
import jupiter.common.struct.map.hash.ExtendedHashMap;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link JConsole} is the extended JFC/Swing based console for the BeanShell desktop.
 * <p>
 * @author Florian Barras and Patrick Niemeyer (http://www.pat.net)
 */
public class JConsole
		extends JScrollPane
		implements ActionListener, IConsole, KeyListener, MouseListener, PropertyChangeListener,
		Runnable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final Font FONT = new Font("Monospaced", Font.PLAIN, 14);
	protected static final String COPY = "Copy";
	protected static final String CUT = "Cut";
	protected static final String PASTE = "Paste";

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final StyleContext STYLE_CONTEXT = new StyleContext();
	protected static final Style DEFAULT_STYLE = STYLE_CONTEXT.getStyle(StyleContext.DEFAULT_STYLE);

	/**
	 * The {@link Style} associated to their {@link Color}.
	 */
	protected static final ExtendedHashMap<Color, Style> STYLES = new ExtendedHashMap<Color, Style>();

	static {
		final SeverityLevel[] severityLevels = SeverityLevel.class.getEnumConstants();
		for (final SeverityLevel severityLevel : severityLevels) {
			final Color color = ConsoleHandler.getColor(severityLevel);
			final Style style = STYLE_CONTEXT.addStyle(color.toString(), DEFAULT_STYLE);
			final java.awt.Color c = color.toAWT();
			if (c != null) {
				StyleConstants.setForeground(style, c);
			}
			STYLES.put(color, style);
		}
	}

	protected static final ExtendedList<String> COLORS = Strings.toList(
			Color.class.getEnumConstants());

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile Color COLOR = Color.GREEN;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final ExtendedLinkedList<String> history = new ExtendedLinkedList<String>();
	protected final ExtendedLinkedList<String> inputLines = new ExtendedLinkedList<String>();
	protected volatile OutputStream outPipe;
	protected volatile InputStream inPipe;
	protected volatile InputStream in;
	protected volatile PrintStream out;
	protected volatile int commandStart = 0;
	protected volatile String currentLine;
	protected volatile int historicalLineIndex = 0;
	protected volatile JPopupMenu menu;
	protected volatile JTextPane textPane;
	protected volatile boolean isKeyUp = true;

	protected volatile String text = EMPTY;
	protected volatile Color textColor = COLOR;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link JConsole} by default.
	 */
	public JConsole() {
		this(null, null);
	}

	/**
	 * Constructs a {@link JConsole} with the specified {@link InputStream} and
	 * {@link OutputStream}.
	 * <p>
	 * @param inputStream  the {@link InputStream} to read from
	 * @param outputStream the {@link OutputStream} to write to
	 */
	public JConsole(final InputStream inputStream, final OutputStream outputStream) {
		super();
		init(inputStream, outputStream);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEARERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clears the {@link JTextPane}.
	 */
	public void clear() {
		textPane.setText(EMPTY);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void print(final Icon icon) {
		if (icon != null) {
			append(icon);
		}
	}

	public void print(final Object content) {
		append(content);
	}

	public void print(final Object content, final Font font) {
		print(content, font, null);
	}

	public void print(final Object content, final java.awt.Color color) {
		print(content, null, color);
	}

	public void print(final Object content, final Font font, final java.awt.Color color) {
		final AttributeSet old = getStyle();
		setStyle(font, color);
		append(content);
		setStyle(old, true);
	}

	public void print(final Object content, final String fontFamilyName, final int size,
			final java.awt.Color color) {
		print(content, fontFamilyName, size, color, false, false, false);
	}

	public void print(final Object content, final String fontFamilyName, final int size,
			final java.awt.Color color, final boolean bold, final boolean italic,
			final boolean underline) {
		final AttributeSet old = getStyle();
		setStyle(fontFamilyName, size, color, bold, italic, underline);
		append(content);
		setStyle(old, true);
	}

	//////////////////////////////////////////////

	/**
	 * Terminates the line.
	 */
	public void println() {
		print(NEW_LINE);
	}

	public void println(final Icon icon) {
		print(icon);
		println();
	}

	public void println(final Object content) {
		append(Objects.toString(content).concat(NEW_LINE));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public void error(final Object content) {
		print(content, java.awt.Color.RED);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void init(final InputStream inputStream, final OutputStream outputStream) {
		// Initialize the special text pane catching cut/paste, keys and programmatic behavior
		textPane = new JTextPane(new DefaultStyledDocument()) {
			/**
			 * The generated serial version ID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void cut() {
				if (textPane.getCaretPosition() < commandStart) {
					super.copy();
				} else {
					super.cut();
				}
			}

			@Override
			public void paste() {
				forceCaretMoveToEnd();
				super.paste();
			}
		};
		textPane.setText(EMPTY);
		textPane.setFont(FONT);
		textPane.setMargin(new Insets(7, 5, 7, 5));
		textPane.addKeyListener(this);
		setViewportView(textPane);
		// Create the popup menu
		menu = new JPopupMenu("Menu");
		menu.add(new JMenuItem(CUT)).addActionListener(this);
		menu.add(new JMenuItem(COPY)).addActionListener(this);
		menu.add(new JMenuItem(PASTE)).addActionListener(this);
		textPane.addMouseListener(this);
		// Make sure the popup menu follows the Look & Feel
		UIManager.addPropertyChangeListener(this);
		// Set the input
		outPipe = outputStream;
		if (outPipe == null) {
			outPipe = new PipedOutputStream();
			try {
				in = new PipedInputStream((PipedOutputStream) outPipe);
			} catch (final IOException ex) {
				IO.error(ex);
			}
		}
		// Set the output
		inPipe = inputStream;
		if (inPipe == null) {
			final PipedOutputStream pout = new PipedOutputStream();
			try {
				out = new PrintStream(pout, true, DEFAULT_CHARSET.name());
				inPipe = new BlockingPipedInputStream(pout);
			} catch (final IOException ex) {
				IO.error(ex);
			}
		}
		// Start the inpipe watcher
		new Thread(this).start();
		requestFocus();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS / WRITERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link InputStream}.
	 * <p>
	 * @return the {@link InputStream}
	 */
	public InputStream getIn() {
		return in;
	}

	/**
	 * Returns the input line.
	 * <p>
	 * @return the input line
	 */
	public String getInputLine() {
		String inputLine;
		synchronized (inputLines) {
			while (inputLines.isEmpty()) {
				try {
					inputLines.wait();
				} catch (final InterruptedException ignored) {
				}
			}
			inputLine = inputLines.removeFirst();
		}
		return inputLine;
	}

	public InputStream getInputStream() {
		return inPipe;
	}

	public String getLastLine() {
		return history.get(historicalLineIndex);
	}

	public List<String> getLines() {
		return history;
	}

	public Reader getReader() {
		return new InputStreamReader(in, DEFAULT_CHARSET);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link PrintStream}.
	 * <p>
	 * @return the {@link PrintStream}
	 */
	public PrintStream getOut() {
		return out;
	}

	/**
	 * Returns the error {@link PrintStream}.
	 * <p>
	 * @return the error {@link PrintStream}
	 */
	public PrintStream getErr() {
		return out;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CARET
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void forceCaretMoveToEnd() {
		if (textPane.getCaretPosition() < commandStart) {
			// Move the caret
			textPane.setCaretPosition(getTextLength());
			textPane.repaint();
		}
	}

	protected void forceCaretMoveToStart() {
		if (textPane.getCaretPosition() < commandStart) {
			// Move the caret
			textPane.setCaretPosition(getTextLength());
			textPane.repaint();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CURSOR
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the cursor image to the specified predefined default cursor.
	 */
	public void setCursor() {
		setCursor(Cursor.DEFAULT_CURSOR);
	}

	/**
	 * Sets the cursor image to the specified predefined cursor.
	 * <p>
	 * @param type a type of a predefined cursor
	 */
	public void setCursor(final int type) {
		setCursor(Cursor.getPredefinedCursor(type));
	}

	/**
	 * Sets the cursor image to the specified predefined crosshair cursor.
	 */
	public void setCursorToCrosshair() {
		setCursor(Cursor.CROSSHAIR_CURSOR);
	}

	/**
	 * Sets the cursor image to the specified predefined text cursor.
	 */
	public void setCursorToText() {
		setCursor(Cursor.TEXT_CURSOR);
	}

	/**
	 * Sets the cursor image to the specified predefined wait cursor.
	 */
	public void setCursorToWait() {
		setCursor(Cursor.WAIT_CURSOR);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// HISTORY
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void historyUp() {
		if (history.isNonEmpty()) {
			// Save the current line
			if (historicalLineIndex == 0) {
				currentLine = getCommand();
			}
			if (historicalLineIndex < history.size()) {
				++historicalLineIndex;
				showHistoryLine();
			}
		}
	}

	protected void historyDown() {
		if (historicalLineIndex > 0) {
			--historicalLineIndex;
			showHistoryLine();
		}
	}

	protected void showHistoryLine() {
		String showline;
		if (historicalLineIndex == 0) {
			showline = currentLine;
		} else {
			showline = history.get(history.size() - historicalLineIndex);
		}
		replaceRange(showline, commandStart, getTextLength());
		textPane.setCaretPosition(getTextLength());
		textPane.repaint();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// LINE
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void acceptLine(final String line) {
		// Check the output stream
		if (outPipe == null) {
			IO.error("No console output stream");
			return;
		}

		// Write the line (handle Unicode characters)
		try {
			outPipe.write(Strings.toUnicode(line).getBytes(DEFAULT_CHARSET.name()));
			outPipe.flush();
			textPane.repaint();
		} catch (final IOException ex) {
			throw new IllegalStateException("Cannot write in the console", ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// STYLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected AttributeSet getStyle() {
		return textPane.getCharacterAttributes();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected AttributeSet setStyle(final Font font, final java.awt.Color color) {
		if (font != null) {
			return setStyle(font.getFamily(), font.getSize(), color, font.isBold(), font.isItalic(),
					StyleConstants.isUnderline(getStyle()));
		}
		return setStyle(null, -1, color);
	}

	protected AttributeSet setStyle(final String fontFamilyName, final int size,
			final java.awt.Color color) {
		final MutableAttributeSet attributes = new SimpleAttributeSet();
		if (color != null) {
			StyleConstants.setForeground(attributes, color);
		}
		if (fontFamilyName != null) {
			StyleConstants.setFontFamily(attributes, fontFamilyName);
		}
		if (size >= 0) {
			StyleConstants.setFontSize(attributes, size);
		}
		setStyle(attributes);
		return getStyle();
	}

	protected AttributeSet setStyle(final String fontFamilyName, final int size,
			final java.awt.Color color, final boolean bold, final boolean italic,
			final boolean underline) {
		final MutableAttributeSet attr = new SimpleAttributeSet();
		if (color != null) {
			StyleConstants.setForeground(attr, color);
		}
		if (fontFamilyName != null) {
			StyleConstants.setFontFamily(attr, fontFamilyName);
		}
		if (size >= 0) {
			StyleConstants.setFontSize(attr, size);
		}
		StyleConstants.setBold(attr, bold);
		StyleConstants.setItalic(attr, italic);
		StyleConstants.setUnderline(attr, underline);
		setStyle(attr);
		return getStyle();
	}

	protected void setStyle(final AttributeSet attributes) {
		setStyle(attributes, false);
	}

	protected void setStyle(final AttributeSet attributes, final boolean replace) {
		textPane.setCharacterAttributes(attributes, replace);
	}

	//////////////////////////////////////////////

	@Override
	public void setFont(final Font font) {
		super.setFont(font);
		if (textPane != null) {
			textPane.setFont(font);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// TEXT
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected int getTextLength() {
		return textPane.getDocument().getLength();
	}

	protected String replaceRange(final Object content, final int fromSelection,
			final int toSelection) {
		final String selection = Objects.toString(content);
		textPane.select(fromSelection, toSelection);
		textPane.replaceSelection(selection);
		textPane.repaint();
		return selection;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACTION LISTENER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Handles cut, copy and paste.
	 * <p>
	 * @param event an {@link ActionEvent}
	 */
	public void actionPerformed(final ActionEvent event) {
		final String cmd = event.getActionCommand();
		if (cmd.equals(CUT)) {
			textPane.cut();
		} else if (cmd.equals(COPY)) {
			textPane.copy();
		} else if (cmd.equals(PASTE)) {
			textPane.paste();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// KEY LISTENER
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void requestFocus() {
		super.requestFocus();
		textPane.requestFocus();
	}

	public void keyPressed(final KeyEvent event) {
		type(event);
		isKeyUp = false;
	}

	public void keyTyped(final KeyEvent event) {
		type(event);
	}

	public void keyReleased(final KeyEvent event) {
		isKeyUp = true;
		type(event);
	}

	@SuppressWarnings("deprecation")
	protected synchronized void type(final KeyEvent event) {
		switch (event.getKeyCode()) {
			case KeyEvent.VK_ENTER:
				if (event.getID() == KeyEvent.KEY_PRESSED) {
					if (isKeyUp) {
						enter();
					}
				}
				event.consume();
				textPane.repaint();
				break;
			case KeyEvent.VK_UP:
				if (event.getID() == KeyEvent.KEY_PRESSED) {
					historyUp();
				}
				event.consume();
				break;
			case KeyEvent.VK_DOWN:
				if (event.getID() == KeyEvent.KEY_PRESSED) {
					historyDown();
				}
				event.consume();
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_BACK_SPACE:
			case KeyEvent.VK_DELETE:
				if (textPane.getCaretPosition() <= commandStart) {
					// This does not work for backspace; see the default case for a workaround
					event.consume();
				}
				break;
			case KeyEvent.VK_RIGHT:
				forceCaretMoveToStart();
				break;
			case KeyEvent.VK_HOME:
				textPane.setCaretPosition(commandStart);
				event.consume();
				break;
			case KeyEvent.VK_U: // clear the line
				if ((event.getModifiers() & InputEvent.CTRL_MASK) > 0) {
					replaceRange(EMPTY, commandStart, getTextLength());
					historicalLineIndex = 0;
					event.consume();
				}
				break;
			case KeyEvent.VK_ALT:
			case KeyEvent.VK_CAPS_LOCK:
			case KeyEvent.VK_CONTROL:
			case KeyEvent.VK_META:
			case KeyEvent.VK_SHIFT:
			case KeyEvent.VK_SCROLL_LOCK:
			case KeyEvent.VK_PAUSE:
			case KeyEvent.VK_INSERT:
			case KeyEvent.VK_F1:
			case KeyEvent.VK_F2:
			case KeyEvent.VK_F3:
			case KeyEvent.VK_F4:
			case KeyEvent.VK_F5:
			case KeyEvent.VK_F6:
			case KeyEvent.VK_F7:
			case KeyEvent.VK_F8:
			case KeyEvent.VK_F9:
			case KeyEvent.VK_F10:
			case KeyEvent.VK_F11:
			case KeyEvent.VK_F12:
			case KeyEvent.VK_ESCAPE:
				break;
			// Control-C
			case KeyEvent.VK_C:
				if (textPane.getSelectedText() == null) {
					if ((event.getModifiers() & InputEvent.CTRL_MASK) > 0 &&
							event.getID() == KeyEvent.KEY_PRESSED) {
						append("^C");
					}
					event.consume();
				}
				break;
			default:
				if ((event.getModifiers() &
						(InputEvent.CTRL_MASK | InputEvent.ALT_MASK | InputEvent.META_MASK)) == 0) {
					// Plain character
					forceCaretMoveToEnd();
				}
				// The getKeyCode function always returns VK_UNDEFINED for keyTyped events,
				// so backspace is not fully consumed
				if (event.paramString().contains("Backspace")) {
					if (textPane.getCaretPosition() <= commandStart) {
						event.consume();
						break;
					}
				}
				break;
		}
	}

	protected void enter() {
		String command = getCommand();
		// Special hack for empty return
		if (command.length() == 0) {
			command = ";".concat(NEW_LINE);
		} else {
			history.add(command);
			synchronized (inputLines) {
				inputLines.add(command);
				inputLines.notifyAll();
			}
			command += NEW_LINE;
		}
		append(NEW_LINE);
		historicalLineIndex = 0;
		acceptLine(command);
	}

	protected String getCommand() {
		String command = EMPTY;
		try {
			command = textPane.getText(commandStart, getTextLength() - commandStart);
		} catch (final BadLocationException ex) {
			IO.error(ex);
		}
		return command;
	}

	public synchronized void append(final Object content) {
		if (content instanceof Icon) {
			textPane.insertIcon((Icon) content);
		} else {
			// Initialize
			final StyledDocument document = textPane.getStyledDocument();
			final String styledText = text.concat(Objects.toString(content));
			final List<Index<String>> delimiters = Strings.getStringIndices(styledText, COLORS);
			final Iterator<Index<String>> delimiterIterator = delimiters.iterator();
			final List<String> textParts = Strings.splitString(styledText, COLORS);
			final Iterator<String> textIterator = textParts.iterator();

			// Append the text
			text = EMPTY;
			if (delimiters.isEmpty()) {
				textColor = COLOR;
			}
			while (textIterator.hasNext()) {
				String textPart = textIterator.next();
				// Store the text if required (if the text contains a part of the next delimiter)
				if (!textIterator.hasNext() && textPart.indexOf(ESCAPE) >= 0) {
					text = textPart;
					textPart = EMPTY;
				}
				// Insert the text part
				insertString(document, getTextLength(), textPart, textColor);
				// Update the text color
				if (delimiterIterator.hasNext()) {
					textColor = Color.parse(delimiterIterator.next().getToken());
				}
			}
		}
		commandStart = getTextLength();
		textPane.setCaretPosition(commandStart);
		textPane.repaint();
	}

	protected void insertString(final StyledDocument document, final int offset, final String text,
			final ConsoleHandler.Color textColor) {
		if (Strings.isNonEmpty(text)) {
			try {
				document.insertString(offset,
						textColor != null ? textColor.getText(text) : text,
						textColor != null ? STYLES.get(textColor) : DEFAULT_STYLE);
			} catch (final BadLocationException ignored) {
			}
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MOUSE LISTENER
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void mouseClicked(final MouseEvent event) {
	}

	public void mousePressed(final MouseEvent event) {
		if (event.isPopupTrigger()) {
			menu.show((Component) event.getSource(), event.getX(), event.getY());
		}
	}

	public void mouseReleased(final MouseEvent event) {
		if (event.isPopupTrigger()) {
			menu.show((Component) event.getSource(), event.getX(), event.getY());
			textPane.repaint();
		}
	}

	public void mouseEntered(final MouseEvent event) {
	}

	public void mouseExited(final MouseEvent event) {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROPERTY CHANGE LISTENER
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equals("lookAndFeel")) {
			SwingUtilities.updateComponentTreeUI(menu);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// RUNNABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void run() {
		try {
			inPipeWatcher();
		} catch (final IOException ex) {
			IO.error(ex);
		}
	}

	protected void inPipeWatcher()
			throws IOException {
		// Arbitrary blocking factor
		final byte[] ba = new byte[256];
		int read;
		while ((read = inPipe.read(ba)) >= 0) {
			print(new String(ba, 0, read, DEFAULT_CHARSET.name()));
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Objects.getName(this);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The overridden method {@link BlockingPipedInputStream#read} does not throw an
	 * {@link IOException} (except if the pipe is closed); it simply waits for new writers and data.
	 * This is used by {@code this} internal read thread to allow writers in different (and in
	 * particular ephemeral) threads to write to the pipe.
	 */
	protected static class BlockingPipedInputStream
			extends PipedInputStream {

		protected volatile boolean isClosed;

		protected BlockingPipedInputStream(final PipedOutputStream pipedOutputStream)
				throws IOException {
			super(pipedOutputStream);
			isClosed = false;
		}

		@Override
		public synchronized int read()
				throws IOException {
			if (isClosed) {
				throw new IOException("No console input stream");
			}

			// While there is no data
			while (!isClosed && super.in < 0) {
				// Notify all the writers
				notifyAll();
				try {
					wait(750);
				} catch (final InterruptedException ex) {
					throw new InterruptedIOException(Objects.toString(ex));
				}
			}

			// This is what the superclass does
			final int nextByte = buffer[super.out++] & 0xFF;
			if (super.out >= buffer.length) {
				super.out = 0;
			}
			if (super.in == super.out) {
				super.in = -1;
			}
			return nextByte;
		}

		@Override
		public synchronized void close()
				throws IOException {
			try {
				isClosed = true;
				notifyAll();
			} finally {
				super.close();
			}
		}
	}
}
