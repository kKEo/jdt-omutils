package org.maziarz.sqlipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;
import org.maziarz.sqlipse.dialogs.DriversDialog;

public class ConfigureDriversHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		System.out.println("OPening drivers dialog");
		
		DriversDialog dialog = new DriversDialog(HandlerUtil.getActiveShell(event));
		dialog.create();
		dialog.setBlockOnOpen(true);
		dialog.open();
		
		return null;
	}

}
