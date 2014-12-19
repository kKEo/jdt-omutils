package org.maziarz.sqlipse.views;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.maziarz.sqlipse.Configuration;
import org.maziarz.sqlipse.JdbcConnection;
import org.maziarz.sqlipse.SqlipsePlugin;
import org.maziarz.sqlipse.Utils;

public class ConnectPanel extends Composite {

	private ComboViewer connections;
	private Runnable onConnect;
	
	public ConnectPanel(Composite parent) {
		super(parent, SWT.NONE);
		
		this.setLayout(new GridLayout(2, false));
		
		connections = new ComboViewer(this);
		connections.setContentProvider(ArrayContentProvider.getInstance());
		connections.setInput(SqlipsePlugin.getDefault().readConfig().getConnections());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(connections.getControl());
		
		Button b = new Button(this, SWT.NONE);
		b.setText("(manage...)");
		b.setToolTipText("Configure connections");
		b.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				Utils.executeCommand("sqlipse.commands.configureConnections");
				
				Object o = ((IStructuredSelection)connections.getSelection()).getFirstElement();
				
				String connectionName = "";
				if (o instanceof JdbcConnection) {
					connectionName= ((JdbcConnection) o).getName();
				}
				
				Configuration config = SqlipsePlugin.getDefault().readConfig();
				connections.setInput(config.getConnections());
				
				JdbcConnection connectionByName = config.getConnectionByName(connectionName);
				if (connectionByName != null) {
					connections.setSelection(new StructuredSelection(connectionByName));
				}
			}
		});
		
		
		Button bRun = new Button(this, SWT.PUSH);
		bRun.setText("Connect");
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(bRun);
		
		bRun.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onConnect.run();
			}
		});
		
	}

	public void onConnect(Runnable runnable) {
		this.onConnect = runnable;
	}

	public String getConnectionName() {
		ISelection selection = connections.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof JdbcConnection) {
				return ((JdbcConnection)firstElement).getName();
			}
		}
		return "<error>";
	}

	public JdbcConnection getConnection() {
		ISelection selection = connections.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof JdbcConnection) {
				return ((JdbcConnection)firstElement);
			}
		}
		return null;
	}

}
