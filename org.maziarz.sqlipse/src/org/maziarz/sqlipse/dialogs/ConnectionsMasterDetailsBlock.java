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
import org.maziarz.sqlipse.JdbcConnection;
import org.maziarz.sqlipse.SqlipsePlugin;

public class ConnectionsMasterDetailsBlock extends MasterDetailsBlock {

	private TreeViewer tv;
	private Composite parent;

	public ConnectionsMasterDetailsBlock(Composite parent) {
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
		section.setText("Jdbc Connections");
		section.setDescription("The list of available connections");
		section.marginHeight = section.marginWidth = 4;

		Composite c = tk.createComposite(section, SWT.WRAP);
		c.setLayout(new GridLayout());
		tk.paintBordersFor(c);

		Tree t = tk.createTree(c, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(t);

		section.setClient(c);

		final SectionPart spart = new SectionPart(section);
		managedForm.addPart(spart);

		tv = new TreeViewer(t);
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

				if (parentElement instanceof JdbcConnection) {
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

		addConnectionButton(tk, c);

	}

	private void addConnectionButton(FormToolkit tk, Composite c) {
		Button b = tk.createButton(c, "Add connection", SWT.PUSH);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false).applyTo(b);

		b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				Configuration configuration = SqlipsePlugin.getDefault().getConfiguration();
				JdbcConnection connection = new JdbcConnection(null, "new connection", "", "", "");
				configuration.addConnection(connection);
				tv.setInput(configuration.getConnections().toArray());
				tv.setSelection(new StructuredSelection(connection));
			}
		});

	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(JdbcConnection.class, new ConnectionDetailsPage());

		if (PlatformUI.isWorkbenchRunning()) {
			List<JdbcConnection> connections = SqlipsePlugin.getDefault().getConfiguration().getConnections();
			tv.setInput(connections.toArray());
			if (connections.size() > 0) {
				tv.setSelection(new StructuredSelection(connections.get(0)));
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
