package org.maziarz.sqlipse.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ConnectionsDialog extends FormDialog {

	Shell shell;

	public ConnectionsDialog(Shell shell) {
		super(shell);
		this.shell = shell;
	}

	@Override
	protected void createFormContent(IManagedForm mform) {
		
		mform.getForm().setText("Jdbc Connections");
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		mform.getForm().getBody().setLayout(layout);
		
		final CTabFolder cTabFolder = new CTabFolder(mform.getForm().getBody(), SWT.FLAT | SWT.LEFT) ;
		FormToolkit tk = mform.getToolkit();
		tk.adapt(cTabFolder, true, true);
		
		cTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Color selectedColor = tk.getColors().getColor(IFormColors.SEPARATOR);
		cTabFolder.setSelectionBackground(new Color[] {selectedColor, tk.getColors().getBackground()}, new int[] {50});
		tk.paintBordersFor(cTabFolder);
		
		createTabs(mform, cTabFolder);
		
		cTabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSelection(cTabFolder);
			}
		});
		
		cTabFolder.setSelection(0);
		updateSelection(cTabFolder);
		
	}
	
	private void updateSelection(CTabFolder cTabFolder){
		CTabItem item = cTabFolder.getSelection();
	}

	private void createTabs(IManagedForm mform, CTabFolder cTabFolder) {
		
		createConnectionsTab(mform, cTabFolder, "Connections");
		createDriversTab(mform, cTabFolder, "Drivers");
		
	}

	private void createConnectionsTab(IManagedForm mform, CTabFolder cTabFolder, String title) {
		
		CTabItem item = new CTabItem(cTabFolder, SWT.NONE);
		item.setText(title);
		
		ConnectionsMasterDetailsBlock mdb = new ConnectionsMasterDetailsBlock(cTabFolder);
		mdb.createContent(mform);
		
		item.setControl(mdb.getControl());
	}
	
	private void createDriversTab(IManagedForm mform, CTabFolder cTabFolder, String title) {
		
		CTabItem item = new CTabItem(cTabFolder, SWT.NONE);
		item.setText(title);
		
		DriversMasterDetailsBlock block = new DriversMasterDetailsBlock(cTabFolder);
		block.createContent(mform);
		
		item.setControl(block.getControl());
		
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
