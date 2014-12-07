package org.maziarz.sqlipse.views;

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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.text.undo.DocumentUndoManagerRegistry;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public class SqlScratchpad extends Composite implements SqlSourceProvider {

	private SourceViewer text;

	public SqlScratchpad(Composite parent, int style) {
		super(parent, style);

		this.setLayout(new FillLayout());

		CompositeRuler cr = new CompositeRuler();
		LineNumberRulerColumn lineCol = new LineNumberRulerColumn();
		lineCol.setForeground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY));
		cr.addDecorator(0, lineCol);

		int sourceViewerStyle = SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION;
		text = new SourceViewer(this, cr, sourceViewerStyle);
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
//			System.out.println("" + (startLineIdx - 1) + ": " + textWidget.getLine(startLineIdx - 1));
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

}
