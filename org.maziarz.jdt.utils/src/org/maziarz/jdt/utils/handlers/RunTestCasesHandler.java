package org.maziarz.jdt.utils.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class RunTestCasesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		// IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Object selectedObject = selection.getFirstElement();

		if (selectedObject instanceof IFile) {
			IFile file = (IFile) selectedObject;
			IProject project = file.getProject();
			IJavaProject javaProject = JavaCore.create(project);

			IVMInstall vmInstall;
			try {
				vmInstall = JavaRuntime.getVMInstall(javaProject);

				if (vmInstall == null)
					vmInstall = JavaRuntime.getDefaultVMInstall();
				if (vmInstall != null) {
					IVMRunner vmRunner = vmInstall.getVMRunner(ILaunchManager.RUN_MODE);
					if (vmRunner != null) {
						String[] classPath = null;
						try {
							classPath = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);
						} catch (CoreException e) {
						}
						if (classPath != null) {
							VMRunnerConfiguration vmConfig = new VMRunnerConfiguration("Test", classPath);
							vmConfig.setWorkingDirectory("/home/krma/Workspaces/CDIMC/runtime-UiCdimc/TestProject");
							ILaunch launch = new Launch(null, ILaunchManager.RUN_MODE, null);
							vmRunner.run(vmConfig, launch, null);
						}
					}
				}

			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

}
