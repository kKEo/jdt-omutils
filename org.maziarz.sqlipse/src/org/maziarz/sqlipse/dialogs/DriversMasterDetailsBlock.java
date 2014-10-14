package org.maziarz.sqlipse.dialogs;

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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
import org.maziarz.sqlipse.SqlipsePlugin;

final class DriversMasterDetailsBlock extends MasterDetailsBlock {
	
	private TreeViewer tv;
	private Composite parent;

	public DriversMasterDetailsBlock(Composite parent) {
		this.parent = parent;
	}

	@Override
	public void createContent(IManagedForm managedForm) {
		createContent(managedForm, parent);
		sashForm.setWeights(new int[] { 1, 2 });
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit tk = managedForm.getToolkit();

		Section section = tk.createSection(parent, Section.TITLE_BAR);
		section.setText("Jdbc Drivers");
		section.setDescription("The list of connection defined for sql execution");
		section.marginHeight = section.marginWidth = 4;
		

		Composite c = tk.createComposite(section, SWT.WRAP);
		c.setLayout(new GridLayout());
		tk.paintBordersFor(c);

		Tree tree = tk.createTree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).hint(200, SWT.DEFAULT).applyTo(tree);
		
		section.setClient(c);
		
		final SectionPart spart = new SectionPart(section) {
			
			@Override
			public void refresh() {
				tv.refresh();
			}
			
		};
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
		
		
		
		Button b = tk.createButton(c, "Add driver", SWT.PUSH);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false).applyTo(b);

		b.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				Configuration configuration = SqlipsePlugin.getDefault().getConfiguration();
				JdbcDriver driver = new JdbcDriver("", "", "");
				configuration.addDriver(driver);
				
				tv.setInput(configuration.getDrivers().toArray());
				tv.setSelection(new StructuredSelection(driver));
			};
		});
		
	}
	
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(JdbcDriver.class, new DriverDetailsPage());
		
		if (PlatformUI.isWorkbenchRunning()) {
			List<JdbcDriver> drivers = SqlipsePlugin.getDefault().getConfiguration().getDrivers();
			tv.setInput(drivers.toArray());
			if (drivers.size() > 0) {
				tv.setSelection(new StructuredSelection(drivers.get(0)));
			}
		} 
		
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}

	public Control getControl() {
		return sashForm;
	}
	
	
}