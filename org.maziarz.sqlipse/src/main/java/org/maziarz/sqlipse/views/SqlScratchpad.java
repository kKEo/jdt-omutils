package org.maziarz.sqlipse.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

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

		text = new SourceViewer(this, cr, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		text.setEditable(true);
		text.setDocument(new Document());

		text.getTextWidget().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == '\r' && e.stateMask == SWT.CTRL) {
					if (run != null) {
						run.run();
					}
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

}
