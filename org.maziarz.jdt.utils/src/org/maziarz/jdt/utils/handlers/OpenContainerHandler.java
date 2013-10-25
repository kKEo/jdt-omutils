package org.maziarz.jdt.utils.handlers;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.ParameterValuesException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.internal.runtime.Log;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.maziarz.jdt.utils.OmUtils;

public class OpenContainerHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		String commandToExecute = null;
		try {
			IParameter[] manager = event.getCommand().getParameters();
			Map params = manager[0].getValues().getParameterValues();
			Object o = params.get(SWT.getPlatform());
			if (o instanceof String) {
				commandToExecute = (String) o;
			}
		} catch (NotDefinedException e) {
			e.printStackTrace();
		} catch (ParameterValuesException e) {
			e.printStackTrace();
		}

		if (commandToExecute == null) {
			return null;
		}

		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);

		Object selectionItem = selection.getFirstElement();

		if (selectionItem == null) {
			return null;
		}
		OmUtils.getDefault().logInfo("Selected: " + selectionItem.getClass());

		if (selectionItem instanceof IFile) {
			IFile file = (IFile) selectionItem;
			IPath basedir = file.getLocation().removeLastSegments(1);
			Program.launch(commandToExecute, basedir.toOSString());
		} else if (selectionItem instanceof IContainer) {
			IContainer container = (IContainer) selectionItem;
			Program.launch(commandToExecute, container.getLocation().toOSString());
		} else if (selectionItem instanceof IProjectNature) {
			IProjectNature project = (IProjectNature) selectionItem;
			Program.launch(commandToExecute, project.getProject().getLocation().toOSString());
		} else if (selectionItem instanceof IAdaptable) {
			Object file = ((IAdaptable) selectionItem).getAdapter(IResource.class);
			IPath path;
			if (file instanceof IFile) {
				path = ((IFile) file).getLocation().removeLastSegments(1);
			} else if (file instanceof IFolder) {
				path = ((IFolder) file).getLocation();
			} else {
				path = ((IResource) file).getLocation().removeLastSegments(1);
			}
			Program.launch(commandToExecute, path.toOSString());
		} else {
			String message = "Selection: " + selectionItem.getClass();
			IEditorPart ed = HandlerUtil.getActiveEditor(event);
			if (ed != null) {
				IStatusLineManager slm = ed.getEditorSite().getActionBars().getStatusLineManager();
				slm.setErrorMessage(message);
			}
		}

		return null;
	}

}
