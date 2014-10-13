package org.maziarz.sqlipse.dialogs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.maziarz.sqlipse.JdbcDriver;
import org.maziarz.sqlipse.utils.Strings;

final class DriverDetailsPage implements IDetailsPage {
	private FormToolkit tk;
	private Text tName;
	private JarListViewer tJars;
	private Text tDriverClass;
	
	private IManagedForm mform;
	
	private JdbcDriver jdbcConnection;
	
	private boolean isDirty = false;

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {

		if (selection instanceof IStructuredSelection) {
			Object s = ((IStructuredSelection) selection).getFirstElement();

			if (s instanceof JdbcDriver) {
				jdbcConnection = (JdbcDriver)s;
				tName.setText(Strings.emptyIfNull(jdbcConnection.getName()));
				tJars.setInput(jdbcConnection.getJars());
				tDriverClass.setText(Strings.emptyIfNull(jdbcConnection.getDriverClass()));
				
				tName.setFocus();
			}
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
		return isDirty;
	}

	@Override
	public void initialize(IManagedForm form) {
		this.tk = form.getToolkit();
		this.mform = form;
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
		section.setText("Driver Settings");
		section.marginHeight = 3;
		section.marginWidth = 10;

		Composite container = tk.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.marginTop = 10;
		container.setLayout(layout);

		tName = addTextControl(container, "Name: ");
		tJars = new JarListViewer(tk, container);
		tDriverClass = addTextControl(container, "Driver Class: ");

		section.setClient(container);

		tName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				jdbcConnection.setName(tName.getText());
				isDirty = true;
				for (IFormPart part : mform.getParts()) {
					if (part instanceof SectionPart) {
						((SectionPart)part).markStale();
					}
				}
				mform.refresh();
			}
		});
		
		addApplyDeleteComposite(container);
		
	}

	private void addApplyDeleteComposite(Composite container) {

		Composite c = tk.createComposite(container);
		c.setLayout(new FormLayout());
		
		Button apply = tk.createButton(c, "Apply", SWT.PUSH);
		Button delete = tk.createButton(c, "Delete", SWT.PUSH);
		
		FormData layoutData = new FormData();
		layoutData.right = new FormAttachment(100, -30);
		apply.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.right = new FormAttachment(apply, -10);
		delete.setLayoutData(layoutData);
		
		GridDataFactory.fillDefaults().grab(true, false).applyTo(c);
		
		apply.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				isDirty = false;
			}
		});
		
		delete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				
			}
		});
		
		
	}

	private Text addTextControl(Composite parent, String label) {
		Composite c = tk.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 10;
		layout.marginRight = 10;
		c.setLayout(layout);
		tk.createLabel(c, label);
		Text text = tk.createText(c, "", SWT.NONE);

		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(c);

		return text;
	}

}