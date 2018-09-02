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
package jupiter.gui.console;

import static jupiter.common.io.IO.IO;

import java.awt.Color;
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
import java.util.LinkedList;
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
import javax.swing.text.StyleConstants;

import jupiter.common.io.console.IConsole;
import jupiter.common.util.Formats;
import jupiter.common.util.Strings;

/**
 * A JFC/Swing based console for the BeanShell desktop.
 * <p>
 * @author Patrick Niemeyer, http://www.pat.net
 */
public class JConsole
		extends JScrollPane
		implements IConsole, Runnable, KeyListener, MouseListener, ActionListener,
		PropertyChangeListener {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -4344016972311184815L;

	protected static final Font FONT = new Font("Monospaced", Font.PLAIN, 14);
	protected static final String COPY = "Copy";
	protected static final String CUT = "Cut";
	protected static final String PASTE = "Paste";
	protected static final String ZEROS = "000";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final List<String> history = new LinkedList<String>();
	protected final LinkedList<String> inputLines = new LinkedList<String>();
	protected volatile OutputStream outPipe;
	protected volatile InputStream inPipe;
	protected volatile InputStream in;
	protected volatile PrintStream out;
	protected volatile int commandStart = 0;
	protected volatile String currentLine;
	protected volatile int historicalLineIndex = 0;
	protected volatile JPopupMenu menu;
	protected volatile JTextPane text;
	protected volatile boolean isKeyUp = true;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public JConsole() {
		this(null, null);
	}

	public JConsole(final InputStream cin, final OutputStream cout) {
		super();
		init(cin, cout);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// JCONSOLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void init(final InputStream cin, final OutputStream cout) {
		// Special TextPane which catches for cut and paste, both L&F keys and programmatic behavior
		text = new JTextPane(new DefaultStyledDocument()) {
			/**
			 * The generated serial version ID.
			 */
			private static final long serialVersionUID = -1922769428193312459L;

			@Override
			public void cut() {
				if (text.getCaretPosition() < commandStart) {
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
		text.setText(Strings.EMPTY);
		text.setFont(FONT);
		text.setMargin(new Insets(7, 5, 7, 5));
		text.addKeyListener(this);
		setViewportView(text);
		// Create the popup menu
		menu = new JPopupMenu("Menu");
		menu.add(new JMenuItem(CUT)).addActionListener(this);
		menu.add(new JMenuItem(COPY)).addActionListener(this);
		menu.add(new JMenuItem(PASTE)).addActionListener(this);
		text.addMouseListener(this);
		// Make sure the popup menu follows the Look & Feel
		UIManager.addPropertyChangeListener(this);
		outPipe = cout;
		if (outPipe == null) {
			outPipe = new PipedOutputStream();
			try {
				in = new PipedInputStream((PipedOutputStream) outPipe);
			} catch (final IOException ex) {
				IO.error(ex);
			}
		}
		inPipe = cin;
		if (inPipe == null) {
			final PipedOutputStream pout = new PipedOutputStream();
			try {
				out = new PrintStream(pout, true, Formats.DEFAULT_CHARSET_NAME);
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
	// ICONSOLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	public String input() {
		String input;
		synchronized (inputLines) {
			while (inputLines.isEmpty()) {
				try {
					inputLines.wait();
				} catch (final InterruptedException ignored) {
				}
			}
			input = inputLines.removeFirst();
		}
		return input;
	}

	public PrintStream getOut() {
		return out;
	}

	public PrintStream getErr() {
		return out;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// INPUT
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Reader getReader() {
		return new InputStreamReader(in, Formats.DEFAULT_CHARSET);
	}

	public InputStream getInputStream() {
		return inPipe;
	}

	public List<String> getLines() {
		return history;
	}

	public String getLastLine() {
		return history.get(historicalLineIndex);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OUTPUT
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void print(final Object content) {
		append(content);
	}

	/**
	 * Prints "\\n" (i.e. newline).
	 */
	public void println() {
		print("\n");
		text.repaint();
	}

	public void println(final Object content) {
		append(Strings.toString(content) + "\n");
	}

	public void error(final Object content) {
		print(content, Color.RED);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public void println(final Icon icon) {
		print(icon);
		println();
		text.repaint();
	}

	public void print(final Icon icon) {
		if (icon == null) {
			return;
		}
		append(icon);
	}

	public void print(final Object s, final Font font) {
		print(s, font, null);
	}

	public void print(final Object s, final Color color) {
		print(s, null, color);
	}

	public void print(final Object content, final Font font, final Color color) {
		final AttributeSet old = getStyle();
		setStyle(font, color);
		append(Strings.toString(content));
		setStyle(old, true);
	}

	public void print(final Object s, final String fontFamilyName, final int size,
			final Color color) {
		print(s, fontFamilyName, size, color, false, false, false);
	}

	public void print(final Object content, final String fontFamilyName, final int size,
			final Color color, final boolean bold, final boolean italic, final boolean underline) {
		final AttributeSet old = getStyle();
		setStyle(fontFamilyName, size, color, bold, italic, underline);
		append(content);
		setStyle(old, true);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CARET
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void forceCaretMoveToEnd() {
		if (text.getCaretPosition() < commandStart) {
			// Move the caret
			text.setCaretPosition(getTextLength());
		}
		text.repaint();
	}

	protected void forceCaretMoveToStart() {
		if (text.getCaretPosition() < commandStart) {
			// Move the caret
			text.setCaretPosition(getTextLength());
		}
		text.repaint();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CURSOR
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void setWaitFeedback(final boolean enable) {
		if (enable) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// HISTORY
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void historyUp() {
		if (history.isEmpty()) {
			return;
		}
		// Save the current line
		if (historicalLineIndex == 0) {
			currentLine = getCmd();
		}
		if (historicalLineIndex < history.size()) {
			++historicalLineIndex;
			showHistoryLine();
		}
	}

	protected void historyDown() {
		if (historicalLineIndex == 0) {
			return;
		}
		historicalLineIndex--;
		showHistoryLine();
	}

	protected void showHistoryLine() {
		String showline;
		if (historicalLineIndex == 0) {
			showline = currentLine;
		} else {
			showline = history.get(history.size() - historicalLineIndex);
		}
		replaceRange(showline, commandStart, getTextLength());
		text.setCaretPosition(getTextLength());
		text.repaint();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// LINE
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void acceptLine(String line) {
		// Handle Unicode characters
		final StringBuilder builder = Strings.createBuilder();
		final int lineLength = line.length();
		for (int i = 0; i < lineLength; ++i) {
			String val = Integer.toString(line.charAt(i), 16);
			val = ZEROS.substring(0, 4 - val.length()) + val;
			builder.append("\\u").append(val);
		}
		line = builder.toString();
		if (outPipe == null) {
			IO.error("No console output stream");
		} else {
			try {
				outPipe.write(line.getBytes(Formats.DEFAULT_CHARSET.name()));
				outPipe.flush();
			} catch (final IOException ex) {
				outPipe = null;
				throw new RuntimeException(
						"Unable to write in the console" + IO.appendException(ex));
			}
		}
		// text.repaint();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// STYLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected AttributeSet getStyle() {
		return text.getCharacterAttributes();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected AttributeSet setStyle(final Font font, final Color color) {
		if (font != null) {
			return setStyle(font.getFamily(), font.getSize(), color, font.isBold(), font.isItalic(),
					StyleConstants.isUnderline(getStyle()));
		}
		return setStyle(null, -1, color);
	}

	protected AttributeSet setStyle(final String fontFamilyName, final int size,
			final Color color) {
		final MutableAttributeSet attr = new SimpleAttributeSet();
		if (color != null) {
			StyleConstants.setForeground(attr, color);
		}
		if (fontFamilyName != null) {
			StyleConstants.setFontFamily(attr, fontFamilyName);
		}
		if (size != -1) {
			StyleConstants.setFontSize(attr, size);
		}
		setStyle(attr);
		return getStyle();
	}

	protected AttributeSet setStyle(final String fontFamilyName, final int size, final Color color,
			final boolean bold, final boolean italic, final boolean underline) {
		final MutableAttributeSet attr = new SimpleAttributeSet();
		if (color != null) {
			StyleConstants.setForeground(attr, color);
		}
		if (fontFamilyName != null) {
			StyleConstants.setFontFamily(attr, fontFamilyName);
		}
		if (size != -1) {
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

	protected void setStyle(final AttributeSet attributes, final boolean overwrite) {
		text.setCharacterAttributes(attributes, overwrite);
	}

	@Override
	public void setFont(final Font font) {
		super.setFont(font);
		if (text != null) {
			text.setFont(font);
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
		while ((read = inPipe.read(ba)) != -1) {
			print(new String(ba, 0, read, Formats.DEFAULT_CHARSET.name()));
			// text.repaint();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// TEXT
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected int getTextLength() {
		return text.getDocument().getLength();
	}

	protected String replaceRange(final Object content, final int from, final int to) {
		final String selection = Strings.toString(content);
		text.select(from, to);
		text.replaceSelection(selection);
		// text.repaint();
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
			text.cut();
		} else if (cmd.equals(COPY)) {
			text.copy();
		} else if (cmd.equals(PASTE)) {
			text.paste();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// KEY LISTENER
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void requestFocus() {
		super.requestFocus();
		text.requestFocus();
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

	protected synchronized void type(final KeyEvent event) {
		switch (event.getKeyCode()) {
			case KeyEvent.VK_ENTER:
				if (event.getID() == KeyEvent.KEY_PRESSED) {
					if (isKeyUp) {
						enter();
					}
				}
				event.consume();
				text.repaint();
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
				if (text.getCaretPosition() <= commandStart) {
					// This does not work for backspace; see the default case for a workaround
					event.consume();
				}
				break;
			case KeyEvent.VK_RIGHT:
				forceCaretMoveToStart();
				break;
			case KeyEvent.VK_HOME:
				text.setCaretPosition(commandStart);
				event.consume();
				break;
			case KeyEvent.VK_U: // clear the line
				if ((event.getModifiers() & InputEvent.CTRL_MASK) > 0) {
					replaceRange(Strings.EMPTY, commandStart, getTextLength());
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
				if (text.getSelectedText() == null) {
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
					if (text.getCaretPosition() <= commandStart) {
						event.consume();
						break;
					}
				}
				break;
		}
	}

	protected void enter() {
		String s = getCmd();
		// Special hack for empty return
		if (s.length() == 0) {
			s = ";\n";
		} else {
			history.add(s);
			synchronized (inputLines) {
				inputLines.add(s);
				inputLines.notifyAll();
			}
			s += "\n";
		}
		append("\n");
		historicalLineIndex = 0;
		acceptLine(s);
	}

	protected String getCmd() {
		String s = Strings.EMPTY;
		try {
			s = text.getText(commandStart, getTextLength() - commandStart);
		} catch (final BadLocationException ex) {
			IO.error(ex);
		}
		return s;
	}

	public synchronized void append(final Object content) {
		if (content instanceof String) {
			final int slen = getTextLength();
			text.select(slen, slen);
			text.replaceSelection((String) content);
		} else if (content instanceof Icon) {
			text.insertIcon((Icon) content);
		}
		commandStart = getTextLength();
		text.setCaretPosition(commandStart);
		text.repaint();
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
		}
		text.repaint();
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
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The overridden method {@code read} does not throw an {@link IOException}; it simply waits for
	 * new writers and data. This is used by the JConsole internal read thread to allow writers in
	 * different (and in particular ephemeral) threads to write to the pipe.
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
				// Notify any writers to wake up
				notifyAll();
				try {
					wait(750);
				} catch (final InterruptedException ex) {
					throw new InterruptedIOException(ex.getMessage());
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
			super.close();
			isClosed = true;
			notifyAll();
		}
	}
}
