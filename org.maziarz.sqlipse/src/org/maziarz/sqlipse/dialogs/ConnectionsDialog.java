package org.maziarz.sqlipse.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.maziarz.sqlipse.Configuration;
import org.maziarz.sqlipse.SqlipsePlugin;

public class ConnectionsDialog extends FormDialog {

	private Shell shell;
	private IManagedForm mform;
	private Configuration config;
	private ConnectionsMasterDetailsBlock connectionsMdb;
	private DriversMasterDetailsBlock driversMdb;

	public ConnectionsDialog(Shell shell, Configuration c) {
		super(shell);
		this.shell = shell;
		this.config = c;
	}

	@Override
	protected void createFormContent(IManagedForm mform) {
		
		this.mform = mform;
		this.mform.setInput(config);
		
		mform.getForm().setText("Jdbc Connections");
		
		mform.getForm().getBody().setLayout(new FillLayout());
		
		final CTabFolder cTabFolder = new CTabFolder(mform.getForm().getBody(), SWT.FLAT | SWT.LEFT) ;
		FormToolkit tk = mform.getToolkit();
		tk.adapt(cTabFolder, true, true);
		
		Color selectedColor = tk.getColors().getColor(IFormColors.H_HOVER_LIGHT);
		cTabFolder.setSelectionBackground(new Color[] {tk.getColors().getBackground(), selectedColor}, new int[] {50});
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
		if (cTabFolder.getSelectionIndex() == 0) {
			connectionsMdb.setInput(config);
		} else {
			driversMdb.setInput(config);
		}
	}

	private void createTabs(IManagedForm mform, CTabFolder cTabFolder) {
		createConnectionsTab(mform, cTabFolder, "Connections");
		createDriversTab(mform, cTabFolder, "Drivers");
	}

	private void createConnectionsTab(IManagedForm mform, CTabFolder cTabFolder, String title) {
		
		CTabItem item = new CTabItem(cTabFolder, SWT.NONE);
		item.setText(title);
		
		connectionsMdb = new ConnectionsMasterDetailsBlock(cTabFolder);
		connectionsMdb.createContent(mform);

		item.setControl(connectionsMdb.getControl());
	}
	
	private void createDriversTab(IManagedForm mform, CTabFolder cTabFolder, String title) {
		
		CTabItem item = new CTabItem(cTabFolder, SWT.NONE);
		item.setText(title);
		
		driversMdb = new DriversMasterDetailsBlock(cTabFolder);
		driversMdb.createContent(mform);
		
		item.setControl(driversMdb.getControl());
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
	
	@Override
	public int open() {
		return super.open();
	}

	@Override
	protected void okPressed() {
		Object o = mform.getInput();
		if (o instanceof Configuration) {
			SqlipsePlugin.getDefault().writeConfig((Configuration)o);
		}
		super.okPressed();
	}
	
}
