package org.maziarz.jdt.utils.handlers;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class ExternalFileHyperlink implements IHyperlink {

	private File file;
	private int lineNumber;

	public ExternalFileHyperlink(File file, int lineNumber) {
		super();
		this.file = file;
		this.lineNumber = lineNumber;
	}

	@Override
	public void linkEntered() {
	}

	@Override
	public void linkExited() {
	}

	@Override
	public void linkActivated() {
		IEditorInput input;
		IFileStore fileStore;

		try {
			fileStore = EFS.getStore(file.toURI());
			input = new FileStoreEditorInput(fileStore);

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		try {
			String defaultTextEditorId = getEditorId();

			IEditorPart editorPart = activePage.openEditor(input, defaultTextEditorId, true);

			if (lineNumber > 0 && editorPart instanceof ITextEditor) {

				ITextEditor textEditor = (ITextEditor) editorPart;

				IDocumentProvider provider = textEditor.getDocumentProvider();

				try {
					provider.connect(input);

				} catch (CoreException e) {
					e.printStackTrace();
					return;
				}

				IDocument document = provider.getDocument(input);

				try {
					IRegion lineRegion = document.getLineInformation(lineNumber);
					textEditor.selectAndReveal(lineRegion.getOffset(), lineRegion.getLength());
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				provider.disconnect(input);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getEditorId() {

		IWorkbench workbench = PlatformUI.getWorkbench();
		IEditorDescriptor desc = workbench.getEditorRegistry().getDefaultEditor(file.getName());
		if (desc == null) {
			desc = workbench.getEditorRegistry().findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
			return desc.getId();
		}

		return "org.eclipse.ui.DefaultTextEditor";
	}

}
