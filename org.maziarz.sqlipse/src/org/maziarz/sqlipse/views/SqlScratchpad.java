package org.maziarz.sqlipse.views;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.text.undo.DocumentUndoManagerRegistry;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;
import org.maziarz.sqlipse.JdbcConnection;
import org.maziarz.sqlipse.handlers.ConnectionSupplier;

public class SqlScratchpad extends Composite implements SqlSourceProvider {

	private static final String PAGE_START = "Start";
	private static final String PAGE_CONNECT_PANEL = "ConnectPanel";
	private static final String PAGE_SCRATCHPAD = "Scratchpad";
	
	private ConnectPanel connectPanel;

	private SourceViewer text;

	private Label scratchpadLabel;

	public SqlScratchpad(Composite parent, int style) {
		super(parent, style);

		this.setLayout(new FillLayout());

		FormToolkit ft = new FormToolkit(this.getDisplay());
		ScrolledPageBook pageBook = ft.createPageBook(this, SWT.NONE);

		Button connectButton = ft.createButton(pageBook.getContainer(), "Connect..", SWT.PUSH | SWT.FLAT);
		connectButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				pageBook.showPage(PAGE_CONNECT_PANEL);
			}
		});
		pageBook.registerPage(PAGE_START, connectButton);
		pageBook.showPage(PAGE_START);

		connectPanel = new ConnectPanel(pageBook.getContainer());
		connectPanel.onConnect(new Runnable() {
			@Override
			public void run() {
				pageBook.showPage(PAGE_SCRATCHPAD);
				scratchpadLabel.setText("Connected to: " + connectPanel.getConnectionName());
			}
		});

		pageBook.registerPage(PAGE_CONNECT_PANEL, connectPanel);

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
				pageBook.showPage("Start");
			}
		});
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(b);

		Composite c = new Composite (composite, SWT.BORDER);
		c.setLayout(new FillLayout());
		
		CompositeRuler cr = new CompositeRuler();
		LineNumberRulerColumn lineCol = new LineNumberRulerColumn();
		lineCol.setForeground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY));
		cr.addDecorator(0, lineCol);

		int sourceViewerStyle = SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION;
		text = new SourceViewer(c, cr, sourceViewerStyle);

		GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(c);

		TextSourceViewerConfiguration configuration = new TextSourceViewerConfiguration();

		text.configure(configuration);

		text.setEditable(true);
		text.setDocument(new Document());
		text.addTextListener(new ITextListener() {
			@Override
			public void textChanged(TextEvent event) {
				long l = event.getDocumentEvent().getModificationStamp();
				if (l % 10 == 0) {
					DocumentUndoManagerRegistry.getDocumentUndoManager(text.getDocument()).commit();
				}
			}
		});

		text.getTextWidget().addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.stateMask == 0) {
					return;
				}

				if (e.character == 'z' && e.stateMask == SWT.MOD1) {
					text.getUndoManager().undo();
				} else if (e.stateMask == SWT.MOD1 && e.character == 'y') {
					text.getUndoManager().redo();
				} else if ((e.stateMask == (SWT.MOD1 | SWT.MOD2)) && e.character == 'z') {
					text.getUndoManager().redo();
				}

			}
		});

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
