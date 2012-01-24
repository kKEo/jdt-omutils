package org.maziarz.jdt.utils.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class CopyLocalPathHandler extends AbstractHandler implements IHandler {

	private static Clipboard myClipboard;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);

		StringBuffer files = new StringBuffer();

		String delimiter = (selection.size() > 1) ? "\n" : "";

		for (Object selectionItem : selection.toList()) {

			if (selectionItem instanceof IFile) {
				IFile file = (IFile) selectionItem;
				files.append(file.getLocation() + delimiter);
			} else if (selectionItem instanceof IProjectNature) {
				IProject project = ((IProjectNature) selectionItem).getProject();
				files.append(project.getLocation() + delimiter);
			}
		}

		if (files.length() > 0) {
			myClipboard = new Clipboard(window.getShell().getDisplay());
			TextTransfer textTransfer = TextTransfer.getInstance();
			myClipboard.setContents(new Object[] { files.toString() }, new Transfer[] { textTransfer });
		}

		return null;
	}

}
