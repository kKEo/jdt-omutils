package org.maziarz.sqlipse.views;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;
import org.maziarz.sqlipse.JdbcConnection;
import org.maziarz.sqlipse.SqlipsePlugin;
import org.maziarz.sqlipse.handlers.ConnectionSupplier;

public class SqlScratchpad extends Composite implements SqlSourceProvider {

	private static final String PAGE_CONNECT_PANEL = "ConnectPanel";
	private static final String PAGE_SCRATCHPAD = "Scratchpad";

	private ConnectPanel connectPanel;

	private SourceViewer text;

	private Label scratchpadLabel;

	private TextViewer logs;

	public SqlScratchpad(Composite parent, int style) {
		super(parent, style);

		this.setLayout(new FillLayout());

		FormToolkit ft = new FormToolkit(this.getDisplay());
		ScrolledPageBook pageBook = ft.createPageBook(this, SWT.NONE);

		connectPanel = new ConnectPanel(pageBook.getContainer());
		connectPanel.onConnect(new Runnable() {
			@Override
			public void run() {
				pageBook.showPage(PAGE_SCRATCHPAD);
				loadDocumentContent();
				scratchpadLabel.setText("Connected to: " + connectPanel.getConnectionName());
			}
		});

		pageBook.registerPage(PAGE_CONNECT_PANEL, connectPanel);
		pageBook.showPage(PAGE_CONNECT_PANEL);

		Composite composite = ft.createComposite(pageBook.getContainer());
		composite.setLayout(new GridLayout(2, false));

		pageBook.registerPage(PAGE_SCRATCHPAD, composite);

		scratchpadLabel = new Label(composite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(scratchpadLabel);

		Button b = new Button(composite, SWT.PUSH | SWT.FLAT);
		b.setText("close");
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				pageBook.showPage(PAGE_CONNECT_PANEL);
				
			}
		});
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(b);

		SashForm c = new SashForm(composite, SWT.BORDER | SWT.VERTICAL);
		c.setLayout(new FillLayout());
		GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(c);

		CompositeRuler cr = new CompositeRuler();
		LineNumberRulerColumn lineCol = new LineNumberRulerColumn();
		lineCol.setForeground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY));
		cr.addDecorator(0, lineCol);

		int sourceViewerStyle = SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION;
		text = new SourceViewer(c, cr, sourceViewerStyle);

		SqlSourceViewerConfiguration configuration = new SqlSourceViewerConfiguration();
		text.configure(configuration);

		text.setEditable(true);

		text.setDocument(new Document());

		text.addTextListener(new ITextListener() {
			@Override
			public void textChanged(TextEvent event) {
				final DocumentEvent documentEvent = event.getDocumentEvent();
				if (documentEvent != null) {
					long l = documentEvent.getModificationStamp();
					if (l % 10 == 0) {
						storeDocumentContent();
					}
				}
			}
		});

		text.getTextWidget().addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.stateMask == 0) {
					return;
				} else if (e.character == 'z' && e.stateMask == SWT.MOD1) {
					text.getUndoManager().undo();
				} else if (e.stateMask == SWT.MOD1 && e.character == 'y') {
					text.getUndoManager().redo();
				} else if ((e.stateMask == (SWT.MOD1 | SWT.MOD2)) && e.character == 'z') {
					text.getUndoManager().redo();
				}

			}
		});

		logs = new TextViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		logs.setEditable(true);
		logs.setDocument(new Document());
	}

	private void storeDocumentContent() {
		File file = SqlipsePlugin.getDefault().getStateLocation().append(connectPanel.getConnectionName() + ".xml").toFile();
		Path path = FileSystems.getDefault().getPath(file.getParent(), file.getName());
		try {
			final byte[] bytes = text.getDocument().get().getBytes();
			Files.write(path, bytes, StandardOpenOption.CREATE);
			try {
				String message = "Written bytes: "+bytes+"\n";
				logs.getDocument().replace(0, message.length(), message);
			} catch (BadLocationException e) {
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadDocumentContent() {
		File file = SqlipsePlugin.getDefault().getStateLocation().append(connectPanel.getConnectionName() + ".xml").toFile();
		Path path = FileSystems.getDefault().getPath(file.getParent(), file.getName());
		try {
			byte[] bytes = Files.readAllBytes(path);
			text.getDocument().set(new String(bytes));
			String message = "Loaded bytes: " + bytes+"\n";
			try {
				logs.getDocument().replace(0, message.length(), message);
			} catch (BadLocationException e) {
			}
		} catch (IOException e) {
		}
	}

	public ITextSelection getSelection() {
		return (ITextSelection) text.getSelection();
	}

	public ISelectionProvider getSelectionProvider() {
		return text;
	}

	@Override
	public String getScript() {

		if (text.getSelection() instanceof TextSelection && ((TextSelection) text.getSelection()).getLength() > 0) {
			return ((TextSelection) text.getSelection()).getText();
		}

		StyledText textWidget = text.getTextWidget();
		int caretOffset = textWidget.getCaretOffset();
		int caretOffsetLine = textWidget.getLineAtOffset(caretOffset);

		String line = textWidget.getLine(caretOffsetLine);

		if (line.trim().isEmpty()) {
			return "";
		}

		int startLineIdx = caretOffsetLine + 1;
		while (startLineIdx - 1 >= 0 && !textWidget.getLine(startLineIdx - 1).trim().isEmpty()) {
			// System.out.println("" + (startLineIdx - 1) + ": " + textWidget.getLine(startLineIdx - 1));
			startLineIdx--;
		}

		StringBuilder sb = new StringBuilder();

		int endLineIdx = startLineIdx;
		int lineCount = textWidget.getLineCount();
		while (endLineIdx < lineCount && (line = textWidget.getLine(endLineIdx).trim()).isEmpty() == false) {
			sb.append(line).append(System.getProperty("line.separator"));
			endLineIdx++;
		}

		return sb.toString().trim();
	}

	public Object getAdapter(Class<?> adapter) {

		if (ConnectionSupplier.class.equals(adapter)) {
			return new ConnectionSupplier() {
				@Override
				public JdbcConnection getConnection() {
					return connectPanel.getConnection();
				}
			};
		}

		return null;
	}

}
