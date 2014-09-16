package org.maziarz.sqlipse;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.commands.ICommandService;

public class Utils {

	public static void executeCommand(IWorkbenchPartSite site, String commandId) {
		ICommandService commandService = (ICommandService) site.getService(ICommandService.class);
		Command c = commandService.getCommand(commandId);
		try {
			c.executeWithChecks(new ExecutionEvent());
		} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e1) {
			throw new RuntimeException(e1);
		}
	}

	
	
}
