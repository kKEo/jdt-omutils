package org.maziarz.sqlipse.dialogs;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;

public class ConnectionsDialog extends FormDialog {

	public ConnectionsDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected void createFormContent(IManagedForm mform) {
		ConnectionsMasterDetailBlock cmdb = new ConnectionsMasterDetailBlock();
		cmdb.createContent(mform);
		mform.getForm().setText("Manage Jdbc Connections");
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(700, 500);

		int height = newShell.getDisplay().getBounds().height;
		int width = newShell.getDisplay().getBounds().width;

		newShell.setLocation(width / 2 - newShell.getSize().x / 2, height / 2 - newShell.getSize().y / 2);
	}

	// @Override
	// protected Control createHelpControl(Composite parent) {
	// Label label = new Label(parent, SWT.NONE);
	// label.setText("Hello world!!");
	// return label;
	// }

	public static void main(String[] args) {

		class App extends ApplicationWindow {

			public App() {
				super(null);
			}

			private void run() {
				open();
			}

			@Override
			protected Control createContents(Composite parent) {
				Control createContents = super.createContents(parent);
				ConnectionsDialog d = new ConnectionsDialog(getShell());
				d.setBlockOnOpen(true);
				d.open();

				return createContents;
			}

		}
		;

		new App().run();

	}

}
