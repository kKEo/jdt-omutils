package org.maziarz.sqlipse.dialogs;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.maziarz.sqlipse.Configuration;

public class DriversDialog extends FormDialog {

	private Shell shell;
	private IManagedForm mform;
	private Configuration config;

	public DriversDialog(Shell shell) {
		super(shell);
		this.shell = shell;
	}

	public DriversDialog(Shell shell, Configuration config) {
		this(shell);
		this.config = config;
	}
	
	@Override
	protected void createFormContent(final IManagedForm mform) {
		this.mform = mform;
		this.mform.setInput(config);
		DriversMasterDetailsBlock mdb = new DriversMasterDetailsBlock(mform.getForm().getBody());
		mdb.createContent(mform);
		super.createFormContent(mform);
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
