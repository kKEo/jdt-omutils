package org.maziarz.sqlipse.dialogs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.maziarz.sqlipse.JdbcConnection;
import org.maziarz.sqlipse.Utils;

final class ConnectionDetailsPage implements IDetailsPage {
	private FormToolkit tk;

	private Text tName;
	private Text tConnectionUrl;
	private Text tUsername;
	private Text tPassword;

	private Combo cDriverName;
	private Text tJars;
	private Combo cDriverClass;

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {

		if (selection instanceof IStructuredSelection) {
			Object s = ((IStructuredSelection) selection).getFirstElement();
			if (s instanceof JdbcConnection) {
				JdbcConnection jdbcConnection = (JdbcConnection) s;
				tName.setText(jdbcConnection.getName());
				tConnectionUrl.setText(jdbcConnection.getConnectionUrl());
				tUsername.setText(jdbcConnection.getUsername());
				tPassword.setText(jdbcConnection.getPassword());

				if (jdbcConnection.getDriver() != null) {
					cDriverName.setText(jdbcConnection.getDriver().getName());
					tJars.setText(jdbcConnection.getDriver().getJars());
					cDriverClass.setText(jdbcConnection.getDriver().getDriverClass());
				} else {
					cDriverName.setText("");
					tJars.setText("");
					cDriverClass.setText("");
				}
			}
		}

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

		tk.createLabel(container, "ConnectionUrl: ");
		tConnectionUrl = tk.createText(container, "");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(tConnectionUrl);

		tk.createLabel(container, "Username: ");
		tUsername = tk.createText(container, "");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(tUsername);

		tk.createLabel(container, "Password: ");
		tPassword = tk.createText(container, "");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(tPassword);

		section.setClient(container);

		Section s = tk.createSection(container, Section.TWISTIE | Section.TITLE_BAR);
		s.setText("Driver Details");
		s.setDescription("Required Jars on classpath");
		s.marginHeight = 3;
		s.marginWidth = 0;
		GridDataFactory.fillDefaults().span(2, 1).applyTo(s);

		Composite c = tk.createComposite(s, SWT.WRAP);
		c.setLayout(new GridLayout(3, false));

		tk.createLabel(c, "Driver name: ");

		cDriverName = new Combo(c, SWT.NONE);
		cDriverName.setBackground(tk.getColors().getBackground());
		cDriverName.setForeground(tk.getColors().getForeground());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(cDriverName);

		Hyperlink addNew = tk.createHyperlink(c, "Add new", SWT.NONE);
		addNew.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				Utils.executeCommand(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite(), "sqlipse.commands.configureDrivers");
			}
		});

		tk.createLabel(c, "Jars: ");
		tJars = tk.createText(c, "", SWT.MULTI);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tJars);
		tk.createHyperlink(c, "Browse", SWT.NONE);

		tk.createLabel(c, "Driver class: ");
		cDriverClass = new Combo(c, SWT.NONE);
		cDriverClass.setBackground(tk.getColors().getBackground());
		cDriverClass.setForeground(tk.getColors().getForeground());

		GridDataFactory.fillDefaults().grab(true, false).applyTo(cDriverClass);

		s.setExpanded(true);

		s.setClient(c);
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void setFocus() {
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
		return false;
	}

	@Override
	public void initialize(IManagedForm form) {
		this.tk = form.getToolkit();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void commit(boolean onSave) {
	}

}