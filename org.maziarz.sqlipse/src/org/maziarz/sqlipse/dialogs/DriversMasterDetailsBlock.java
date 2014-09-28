package org.maziarz.sqlipse.dialogs;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.maziarz.sqlipse.JdbcDriver;
import org.maziarz.sqlipse.SqlipsePlugin;

final class DriversMasterDetailsBlock extends MasterDetailsBlock {
	
	private TreeViewer tv;

	
	@Override
	public void createContent(IManagedForm managedForm) {
		super.createContent(managedForm);
		sashForm.setWeights(new int[] { 1, 2 });
	}
	


	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {

		FormToolkit tk = managedForm.getToolkit();

		Section section = tk.createSection(parent, Section.DESCRIPTION);
		section.setText("Jdbc Connections");
		section.setDescription("The list contains connection defined for sql execution");
		section.marginHeight = section.marginWidth = 10;

		tk.createCompositeSeparator(section);

		Composite c = tk.createComposite(section, SWT.WRAP);
		c.setLayout(new FillLayout());
		tk.paintBordersFor(c);

		Tree tree = tk.createTree(c, SWT.BORDER);

		section.setClient(c);
		final SectionPart spart = new SectionPart(section);
		managedForm.addPart(spart);

		tv = new TreeViewer(tree);
		tv.setLabelProvider(new LabelProvider());
		tv.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {

			}

			@Override
			public boolean hasChildren(Object element) {
				return getChildren(element).length > 0;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return getChildren(inputElement);
			}

			@Override
			public Object[] getChildren(Object parentElement) {

				if (parentElement instanceof Object[]) {
					return (Object[]) parentElement;
				}

				if (parentElement instanceof JdbcDriver) {
					return new Object[0];
				}

				throw new IllegalArgumentException("Class not supported: " + parentElement.getClass());

			}
		});

		tv.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
			}
		});

	}
	
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(JdbcDriver.class, new DriverDetailsPage());
		
		if (PlatformUI.isWorkbenchRunning()) {
			List<JdbcDriver> connections = SqlipsePlugin.getDefault().getConfiguration().getDrivers();
			tv.setInput(connections.toArray());
			if (connections.size() > 0) {
				tv.setSelection(new StructuredSelection(connections.get(0)));
			}
		} 
		
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}
	
	
}