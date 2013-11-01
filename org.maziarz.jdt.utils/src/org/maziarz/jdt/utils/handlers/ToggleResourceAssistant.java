package org.maziarz.jdt.utils.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.maziarz.jdt.utils.IncrementalProjectBuilder;
import org.maziarz.jdt.utils.OmUtils;

public class ToggleResourceAssistant extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		IProject project = null;

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object firstElement = ss.getFirstElement();
			if (firstElement instanceof IProject) {
				project = (IProject) firstElement;
			} else if (firstElement instanceof IAdaptable) {
				project = (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
			}
			if (project != null) {
				toggleBuilder(project);
			}

		}

		return null;
	}

	private void toggleBuilder(IProject project) {

		try {
			IProjectDescription desc = project.getDescription();
			ICommand[] commands = desc.getBuildSpec();
			for (int i = 0; i < commands.length; ++i)
				if (commands[i].getBuilderName().equals(IncrementalProjectBuilder.BUILDER_ID))
					return;
			// add builder to project
			ICommand command = desc.newCommand();
			command.setBuilderName(IncrementalProjectBuilder.BUILDER_ID);
			ICommand[] nc = new ICommand[commands.length + 1];
			// Add it before other builders.
			System.arraycopy(commands, 0, nc, 1, commands.length);
			nc[0] = command;
			desc.setBuildSpec(nc);
			project.setDescription(desc, null);

		} catch (CoreException e) {
			OmUtils.getDefault().logInfo("Error: " + e.getMessage());
		}

	}

}
