package org.maziarz.sqlipse.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.maziarz.sqlipse.Configuration;
import org.maziarz.sqlipse.JdbcConnection;
import org.maziarz.sqlipse.JdbcDriver;

final class ConnectionDetailsPage implements IDetailsPage {
	
	private FormToolkit tk;

	private Text tName;
	private Combo cDriverName;
	private Text tConnectionUrl;
	private Text tUsername;
	private Text tPassword;
	
	private IManagedForm mform;

	private JdbcConnection model;
	private JdbcConnection localModel;

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {

		if (selection instanceof IStructuredSelection) {
			Object s = ((IStructuredSelection) selection).getFirstElement();
			
			if (s instanceof JdbcConnection) {
				model = (JdbcConnection) s;
				
				localModel = JdbcConnection.cloneMe(model);
				
				initializeForm();
				setFocus();
			}
		}

	}

	private void initializeForm() {
		tName.setText(localModel.getName());
		tConnectionUrl.setText(localModel.getConnectionUrl());
		tUsername.setText(localModel.getUsername());
		tPassword.setText(localModel.getPassword());

		if (localModel.getDriver() != null) {
			cDriverName.setText(localModel.getDriver().getName());
		} else {
			cDriverName.setText("");
		}
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void setFocus() {
		tName.setFocus();
	}

	@Override
	public void refresh() {
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public boolean isDirty() {
		return !model.equals(localModel);
	}

	@Override
	public void initialize(IManagedForm mform) {
		this.tk = mform.getToolkit();
		this.mform = mform;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void commit(boolean onSave) {
	}
	
	@Override
	public void createContents(Composite parent) {

		parent.setLayout(new FillLayout());

		Section section = tk.createSection(parent, Section.TITLE_BAR);
		section.setText("Connection Settings");
		section.marginHeight = 3;
		section.marginWidth = 10;
		
		tk.createHyperlink(section, "test", SWT.NONE);

		Composite container = tk.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout(2, false);
		// layout.marginRight = 10;
		layout.marginTop = 10;
		container.setLayout(layout);

		tk.createLabel(container, "Name: ");
		tName = tk.createText(container, "");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(tName);
		tName.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				localModel.setName(tName.getText());
			}
		});

		tk.createLabel(container, "Driver name: ");
		Composite driverComposite = tk.createComposite(container);
		driverComposite.setLayout(new GridLayout(2, false));

		cDriverName = new Combo(driverComposite, SWT.NONE);
		cDriverName.setBackground(tk.getColors().getBackground());
		cDriverName.setForeground(tk.getColors().getForeground());
		
		cDriverName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				List<String> driverNames = new ArrayList<String>();
				for ( JdbcDriver d : ((Configuration) mform.getInput()).getDrivers() ){
					driverNames.add(d.getName());
				}
				cDriverName.setItems(driverNames.toArray(new String[0]));
			}
		});
		
		cDriverName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				JdbcDriver driver = ((Configuration) mform.getInput()).getDriverByName(cDriverName.getText());
				localModel.setDriver(driver);
			}
		});
		
		GridDataFactory.fillDefaults().grab(true, false).applyTo(cDriverName);

		final Hyperlink addNew = tk.createHyperlink(driverComposite, "Add new", SWT.NONE);
		addNew.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				DriversDialog dialog = new DriversDialog(addNew.getShell(), (Configuration) mform.getInput());
				dialog.create();
				dialog.setBlockOnOpen(true);
				dialog.open();
			}
		});
		
		
		tk.createLabel(container, "ConnectionUrl: ");
		tConnectionUrl = tk.createText(container, "");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(tConnectionUrl);
		tConnectionUrl.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				localModel.setConnectionUrl(tConnectionUrl.getText());
			}
		});

		tk.createLabel(container, "Username: ");
		tUsername = tk.createText(container, "");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(tUsername);
		tUsername.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				localModel.setUsername(tUsername.getText());
			}
		});

		tk.createLabel(container, "Password: ");
		tPassword = tk.createText(container, "");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(tPassword);
		tPassword.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				localModel.setPassword(tPassword.getText());
			}
		});

		section.setClient(container);

		Section s = tk.createSection(container, Section.TWISTIE | Section.TITLE_BAR);
		s.setText("Options");
		s.marginHeight = 3;
		s.marginWidth = 0;
		GridDataFactory.fillDefaults().span(2, 1).applyTo(s);

		Composite c = tk.createComposite(s, SWT.WRAP);
		c.setLayout(new GridLayout(3, false));

		s.setExpanded(false);

		s.setClient(c);
		
		addApplyDeleteComposite(container);
	}
	
	private void addApplyDeleteComposite(final Composite container) {

		Composite c = tk.createComposite(container);
		c.setLayout(new FormLayout());
		
		Button apply = tk.createButton(c, "Apply", SWT.PUSH);
		Button reset = tk.createButton(c, "Reset", SWT.PUSH);
		
		FormData layoutData = new FormData();
		layoutData.right = new FormAttachment(100, -30);
		apply.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.right = new FormAttachment(apply, -10);
		reset.setLayoutData(layoutData);
		
		GridDataFactory.fillDefaults().grab(true, false).applyTo(c);
		
		apply.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				apply(model, localModel);
				initializeForm();
			}
		});
		
		reset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				apply(localModel, model);
				initializeForm();
			}
		});
		
	}

	private void apply(JdbcConnection model, JdbcConnection local){
		model.setName(local.getName());
		model.setConnectionUrl(local.getConnectionUrl());
		model.setDriver(local.getDriver());
		model.setUsername(local.getUsername());
		model.setPassword(local.getPassword());
	}
	
}