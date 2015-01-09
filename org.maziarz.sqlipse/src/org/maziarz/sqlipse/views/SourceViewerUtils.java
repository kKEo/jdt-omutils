package org.maziarz.sqlipse.views;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;

public class SourceViewerUtils {

	public static class SourceStatement {

		private static final SourceStatement EMPTY = new SourceStatement("", 0, 0, 0, 0, 0);

		private String text;
		private int startLine;
		private int startIdx;

		private int endLine;
		private int endIdx;

		private int carretOffset;

		private SourceStatement(String text, int startLine, int startIdx, int endLine, int endIdx, int carretOffset) {
			this.text = text;
			this.startLine = startLine;
			this.startIdx = startIdx;
			this.endLine = endLine;
			this.endIdx = endIdx;
			this.carretOffset = carretOffset;
		}

		public String getText() {
			return text;
		}

		public int getStartLine() {
			return startLine;
		}

		public int getStartIdx() {
			return startIdx;
		}

		public int getEndLine() {
			return endLine;
		}

		public int getEndIdx() {
			return endIdx;
		}

		public int getCarretOffset() {
			return carretOffset;
		}

	}

	public static SourceStatement getScript(SourceViewer sourceViewer) {

		StyledText textWidget = sourceViewer.getTextWidget();
		int caretOffset = textWidget.getCaretOffset();
		int caretOffsetLine = textWidget.getLineAtOffset(caretOffset);

		String line = textWidget.getLine(caretOffsetLine);

		if (line.trim().isEmpty()) {
			return SourceStatement.EMPTY;
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

		int startLineOffet = sourceViewer.getTextWidget().getOffsetAtLine(startLineIdx);
		int endLineOffset = sourceViewer.getTextWidget().getOffsetAtLine(endLineIdx);
		return new SourceStatement(sb.toString(), // text
				startLineIdx, //
				startLineOffet, //
				endLineIdx, //
				endLineOffset, //
				caretOffset - startLineOffet); //
	}

	public static String getPrefix(ITextViewer viewer, int offset) {
		IDocument doc = viewer.getDocument();
		if (doc == null || offset > doc.getLength())
			return "";

		int length = 0;
		try {
			while (--offset >= 0 && Character.isJavaIdentifierPart(doc.getChar(offset)))
				length++;
			return doc.get(offset + 1, length);
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}

	}
}
