package org.maziarz.sqlipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;
import org.maziarz.sqlipse.dialogs.ConnectionsDialog;

public class ConfigureConnectionsHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ConnectionsDialog dialog = new ConnectionsDialog(HandlerUtil.getActiveShell(event));
		
		dialog.create();
		
		dialog.setBlockOnOpen(true);
		
		dialog.open();
		
		return null;
	}

}
