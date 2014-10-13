package org.maziarz.sqlipse.dialogs;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;

public class ConnectionsDialog extends FormDialog {

	Shell shell;

	public ConnectionsDialog(Shell shell) {
		super(shell);
		this.shell = shell;
	}

	@Override
	protected void createFormContent(IManagedForm mform) {
		ConnectionsMasterDetailsBlock cmdb = new ConnectionsMasterDetailsBlock();
		cmdb.createContent(mform);
		mform.getForm().setText("Manage Jdbc Connections");
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(800, 500);

		int mainShellHeight = shell.getSize().y;
		int mainShellWidth = shell.getSize().x;

		int mainShellXOffset = shell.getLocation().x;
		int mainShellYOffset = shell.getLocation().y;

		newShell.setLocation(mainShellXOffset + mainShellWidth / 2 - newShell.getSize().x / 2, mainShellYOffset + mainShellHeight
				/ 2 - newShell.getSize().y / 2);
	}

}
