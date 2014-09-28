package org.maziarz.sqlipse.dialogs;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;

public class DriversDialog extends FormDialog {

	public DriversDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected void createFormContent(final IManagedForm mform) {
		DriversMasterDetailsBlock mdb = new DriversMasterDetailsBlock();
		mdb.createContent(mform);
		super.createFormContent(mform);
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
