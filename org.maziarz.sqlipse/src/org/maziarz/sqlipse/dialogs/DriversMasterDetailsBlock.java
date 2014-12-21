package org.maziarz.sqlipse.dialogs;

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.maziarz.sqlipse.Configuration;
import org.maziarz.sqlipse.JdbcDriver;

final class DriversMasterDetailsBlock extends MasterDetailsBlock {
	
	private TreeViewer tv;
	private Composite parent;
	private IManagedForm mform;

	public DriversMasterDetailsBlock(Composite parent) {
		this.parent = parent;
	}

	@Override
	public void createContent(IManagedForm managedForm) {
		this.mform = managedForm;
		createContent(mform, parent);
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit tk = managedForm.getToolkit();

		Section section = tk.createSection(parent, Section.TITLE_BAR);
		section.setText("Jdbc Drivers");
		section.setDescription("The list of connection defined for sql execution");
		section.marginHeight = section.marginWidth = 4;

		Composite c = tk.createComposite(section, SWT.WRAP);
		c.setLayout(new GridLayout(2, false));
		tk.paintBordersFor(c);

		Tree tree = tk.createTree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).hint(200, SWT.DEFAULT).applyTo(tree);
		
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
		
		addDriverButton(tk, c);
		removeDriverButton(tk, c);
	
	}

	private void addDriverButton(FormToolkit tk, Composite c) {
		Button b = tk.createButton(c, "Add driver", SWT.PUSH);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(b);

		b.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				Configuration configuration = (Configuration) mform.getInput();
				JdbcDriver driver = new JdbcDriver("newDriver", "", "");
				configuration.addDriver(driver);
				
				tv.setInput(configuration.getDrivers().toArray());
				tv.setSelection(new StructuredSelection(driver));
			};
		});
	}
	
	private void removeDriverButton(FormToolkit tk, Composite c) {
		Button b = tk.createButton(c, "Remove driver", SWT.PUSH);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(b);

		b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				Configuration configuration = (Configuration) mform.getInput();
				if (!tv.getSelection().isEmpty()) {
					IStructuredSelection selection = (IStructuredSelection) tv.getSelection();
					Object o = selection.getFirstElement();
					tv.remove(o);
					configuration.remove((JdbcDriver)o);
				}
			}
		});
	}
	
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(JdbcDriver.class, new DriverDetailsPage());
		setInput((Configuration) mform.getInput()); 
	}

	protected void setInput(Configuration config) {
		if (PlatformUI.isWorkbenchRunning()) {
			List<JdbcDriver> drivers = config.getDrivers();
			tv.setInput(drivers.toArray());
			if (drivers.size() > 0) {
				tv.setSelection(new StructuredSelection(drivers.get(0)), true);
			}
		}
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}

	protected Control getControl() {
		return sashForm;
	}
	
	
}