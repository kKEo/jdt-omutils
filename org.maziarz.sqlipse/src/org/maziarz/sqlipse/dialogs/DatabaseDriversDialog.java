package org.maziarz.sqlipse.dialogs;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.maziarz.sqlipse.JdbcDriver;

public class DatabaseDriversDialog extends FormDialog {

	public DatabaseDriversDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected void createFormContent(final IManagedForm mform) {
		
		MasterDetailsBlock mdb = new MasterDetailsBlock() {
			
			@Override
			protected void registerPages(DetailsPart detailsPart) {
				
				detailsPart.registerPage(JdbcDriver.class, new IDetailsPage() {
					
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
				});
				
				
			}
			
			@Override
			protected void createToolBarActions(IManagedForm managedForm) {
				
			}
			
			@Override
			protected void createMasterPart(IManagedForm managedForm, Composite parent) {
				
				FormToolkit tk = managedForm.getToolkit();
				
				Section section  = tk.createSection(parent, Section.DESCRIPTION);
				section.setText("Jdbc Connections");
				section.setDescription("The list contains connection defined for sql execution");
				section.marginHeight = section.marginWidth = 10;
				
				tk.createCompositeSeparator(section);
				
				Composite c = tk.createComposite(section, SWT.WRAP);
				c.setLayout(new FillLayout());
				tk.paintBordersFor(c);
				
				Tree t = tk.createTree(c, SWT.BORDER);
				
				section.setClient(c);
				final SectionPart spart = new SectionPart(section);
				managedForm.addPart(spart);
				
				TreeViewer tv = new TreeViewer(t);
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
						
						throw new IllegalArgumentException("Class not supported: "+parentElement.getClass());
						
					}
				});
				
				tv.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						managedForm.fireSelectionChanged(spart, event.getSelection());
					}
					
				});
				
				tv.setInput(new JdbcDriver[] {new JdbcDriver("driver","jar1", "org.acme.Driver")});
			}
		};
		
		mdb.createContent(mform);
		
		super.createFormContent(mform);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setSize(800, 600);
	}
	
	@Override
	protected Control createHelpControl(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Hello world!!");
		return label;
	}
	
	public static void main(String[] args) {
		
		class App extends ApplicationWindow {
			
			public App() {
				super(null);
			}

			private void run() {
				open();
			}
			
			@Override
			protected Control createContents(Composite parent) {
				Control createContents = super.createContents(parent);
				
				DatabaseDriversDialog d = new DatabaseDriversDialog(getShell());
				
				
				
				d.setBlockOnOpen(true);
				d.open();
				
				return createContents;
			}
			
		};
		
		new App().run();
		
	}
	
}
