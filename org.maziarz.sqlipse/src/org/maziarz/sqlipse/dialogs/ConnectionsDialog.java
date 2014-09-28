package org.maziarz.sqlipse.dialogs;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;

public class ConnectionsDialog extends FormDialog {

	public ConnectionsDialog(Shell shell) {
		super(shell);
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

		int height = newShell.getDisplay().getBounds().height;
		int width = newShell.getDisplay().getBounds().width;

		newShell.setLocation(width / 2 - newShell.getSize().x / 2, height / 2 - newShell.getSize().y / 2);
	}

}
