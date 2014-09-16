package org.maziarz.sqlipse.dialogs;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

final class DriverDetailsPage implements IDetailsPage {
	private FormToolkit tk;

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
		parent.setLayout(new GridLayout(2, false));
		tk.createLabel(parent, "Jars: ");
		tk.createText(parent, "");
	}
}