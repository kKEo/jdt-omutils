package org.maziarz.utils.ui.wicket.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;


public class OpenMarkupHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ISelection selection = (ISelection) HandlerUtil.getCurrentSelection(event);
		
		if (selection instanceof TextSelection) {
			IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
			if (editorPart != null){
				IEditorInput editorInput = editorPart.getEditorInput();
				if (editorInput instanceof FileEditorInput){
					IFile file = ((FileEditorInput) editorInput).getFile();
					openCorrespondingMarkupFile(file);
				}
			}
		} 
		else 
		if (selection instanceof IStructuredSelection && ((IStructuredSelection) selection).getFirstElement() instanceof ICompilationUnit){
			IResource resource = null;
			try {
				resource = ((ICompilationUnit) ((IStructuredSelection) selection).getFirstElement()).getUnderlyingResource();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			if (resource instanceof IFile) {
				openCorrespondingMarkupFile((IFile)resource);
			}
		} 
		
		return null;
	}

	private void openCorrespondingMarkupFile(IFile file) {
		IContainer fileContainer = file.getParent();
		if (fileContainer instanceof IFolder) {
			IFolder packageFolder = (IFolder)fileContainer;
			String correspondingFileName = null;
			if (file.getName().endsWith("java")) {
				correspondingFileName = file.getName().replaceFirst(".java$", ".html");
			} else {
				correspondingFileName = file.getName().replaceFirst(".html$", ".java");
			}
			IFile correspondingFile = packageFolder.getFile(correspondingFileName);
			
			if (correspondingFile.exists()){
				openFileInEditor(correspondingFile);
			} else {
				MessageBox mb = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				mb.setMessage("File "+correspondingFile.getProjectRelativePath()+" does not exists.");
				mb.open();
			}	
		}
	}
	
	private void openFileInEditor(IFile markupFile) {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();	
		try {
			IMarker marker = markupFile.createMarker(IMarker.TEXT);
			marker.setAttribute(IDE.EDITOR_ID_ATTR, getEditorId(markupFile));
			IDE.openEditor(activePage, marker);
			marker.delete();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private String getEditorId(IFile file) {
		IWorkbench workbench= PlatformUI.getWorkbench();
		IEditorDescriptor desc = workbench.getEditorRegistry().getDefaultEditor(file.getName());
		if (desc == null) {
			desc = workbench.getEditorRegistry().findEditor(IEditorRegistry.SYSTEM_INPLACE_EDITOR_ID);
			return desc.getId();
		} 
		return desc.getId();
	}
	
}
