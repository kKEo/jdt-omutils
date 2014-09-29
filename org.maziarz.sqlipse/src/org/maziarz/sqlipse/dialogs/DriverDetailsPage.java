package org.maziarz.sqlipse.dialogs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;

final class DriverDetailsPage implements IDetailsPage {
	private FormToolkit tk;
	private Object tName;
	private Object tJars;
	private Object tDriverClass;

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {

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
		tJars = addJarControl(container);
		tDriverClass = addTextControl(container, "Driver Class: ");

		section.setClient(container);

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

	private Text addJarControl(final Composite parent) {

		Composite c = tk.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 10;
		layout.marginRight = 10;
		c.setLayout(layout);
		tk.createLabel(c, "Jars");

		Hyperlink browse = tk.createHyperlink(c, "browse", SWT.NONE);
		final Text text = tk.createText(c, "", SWT.READ_ONLY);

		browse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FileDialog fileDialog = new FileDialog(parent.getShell());
				if (fileDialog.open() != null) {
					StringBuilder sb = new StringBuilder();
					for (String selected : fileDialog.getFileNames()) {
						sb.append(fileDialog.getFilterPath() + "/" + selected).append("\n");
					}
					text.setText(sb.toString());
				}
			}
		});

		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(text);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(c);

		return text;
	}
}