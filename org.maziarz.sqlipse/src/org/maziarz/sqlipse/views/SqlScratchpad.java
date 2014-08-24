package org.maziarz.sqlipse.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.text.undo.DocumentUndoManagerRegistry;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public class SqlScratchpad extends Composite {

	private SourceViewer text;
	private Action run;

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
				if (e.character == '\r' && e.stateMask == SWT.MOD1) {
					if (run != null) {
						run.run();
					}
				}
				if (e.character == 'z' && e.stateMask == SWT.MOD1) {
					text.getUndoManager().undo();
				}
				if (e.character == 'y' && e.stateMask == SWT.MOD1) {
					text.getUndoManager().redo();
				}

				e.doit = false;
			}
		});
		
	}

	public void setAction(Action run) {
		this.run = run;
	}

	public ITextSelection getSelection() {
		return (ITextSelection) text.getSelection();
	}

	public String getQuery() {
		int offset = getSelection().getOffset();

		if (getSelection().getLength() > 0) {
			return getSelection().getText();
		}

		int lineOffset;
		try {
			lineOffset = text.getDocument().getLineInformationOfOffset(offset).getOffset();
			int lineLength = text.getDocument().getLineInformationOfOffset(offset).getLength();
			return text.getDocument().get(lineOffset, lineLength);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return "";
	}

	public ISelectionProvider getSelectionProvider() {
		return text;
	}

}
