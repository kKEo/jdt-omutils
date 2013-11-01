package org.maziarz.utils.ui.wicket.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

public class CreateMarkupHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		IResource resource = null;

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection stucturedSelection = (IStructuredSelection) selection;
			Object selectedObject = stucturedSelection.getFirstElement();
			if (selectedObject instanceof ICompilationUnit) {
			
			ICompilationUnit compilationUnit = (ICompilationUnit) selectedObject;
			resource = compilationUnit.getResource();
			}
		} else 
		if (selection instanceof TextSelection) {
			IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
			if (editorPart != null){
				IEditorInput editorInput = editorPart.getEditorInput();
				if (editorInput instanceof FileEditorInput){
					resource = ((FileEditorInput) editorInput).getFile();
				}
			}
		} 

		if (resource instanceof IFile) {
			IContainer container = resource.getParent();
			if (container instanceof IFolder) {

				String fileName = resource.getName();
				int lastDot = fileName.lastIndexOf('.');
				String className = fileName.substring(0, lastDot);

				IFile markupFile = ((IFolder) container).getFile(className + ".html");
				if (!markupFile.exists()) {
					byte[] bytes = "<html xmlns:wicket=\"\"><wicket:panel></wicket:panel></html>".getBytes();
					InputStream content = new ByteArrayInputStream(bytes);
					try {
						markupFile.create(content, IResource.NONE, null);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}

			}
		}

		return null;
	}

}
