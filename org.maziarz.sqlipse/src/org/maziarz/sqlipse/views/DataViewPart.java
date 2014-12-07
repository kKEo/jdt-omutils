package org.maziarz.sqlipse.views;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;
import org.maziarz.sqlipse.Configuration;
import org.maziarz.sqlipse.JdbcConnection;
import org.maziarz.sqlipse.SqlipsePlugin;
import org.maziarz.sqlipse.Utils;
import org.maziarz.sqlipse.handlers.ConnectionSupplier;
import org.maziarz.sqlipse.handlers.ResultSetProcessor;
import org.maziarz.sqlipse.views.DataViewPart.ViewContentProvider.Row;

public class DataViewPart extends ViewPart {

	public static final String ID = "sqlipse.views.DataViewer";

	private SqlScratchpad scratchpad;
	private CTabFolder folder;

	private ComboViewer connections;
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		
		if (ConnectionSupplier.class.equals(adapter)) {
			final ISelection selection = connections.getSelection();
			if (selection instanceof StructuredSelection) {
				if (((StructuredSelection) selection).getFirstElement() instanceof JdbcConnection) {
					return new ConnectionSupplier() {
						@Override
						public JdbcConnection getConnection() {
							return (JdbcConnection)((StructuredSelection) selection).getFirstElement();
						}
					};
				}
			}
		}
		
		if (ResultSetProcessor.class.equals(adapter)) {
			return new ResultSetProcessor() {
				
				@Override
				public void process(ResultSet rs) {
					try {
						addTabItem(folder, rs);
					} catch (SQLException e) {
						new RuntimeException(""+e, e);
					}
				}
			};
		}
		
		if (SqlSourceProvider.class.equals(adapter)) {
			
			return scratchpad;
			
		}
		
		return super.getAdapter(adapter);
	}
	

	class ViewContentProvider implements IStructuredContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		class Row {

			private Object[] values;

			private Row(Object[] values) {
				this.values = values;
			}

			public Object[] getValues() {
				return values;
			}

		}

		public Object[] getElements(Object parent) {

			if (parent instanceof ResultSet) {
				ResultSet rs = (ResultSet) parent;
				List<Row> rows = new ArrayList<DataViewPart.ViewContentProvider.Row>();

				try {
					ResultSetMetaData meta = rs.getMetaData();
					int cols = meta.getColumnCount();

					while (rs.next()) {
						Object[] result = new Object[cols];
						for (int i = 0; i < cols; i++) {
							result[i] = rs.getObject(i + 1);
						}
						rows.add(new Row(result));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				return rows.toArray();
			}

			return new String[] {};
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {

			if (obj instanceof Row) {
				return getText(((Row) obj).getValues()[index]);
			}

			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return null;// PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	class NameSorter extends ViewerSorter {
	}

	public DataViewPart() {
	}

	public void createPartControl(Composite parent) {
		
		SashForm sash = new SashForm(parent, SWT.NONE);
		
		Composite c = new Composite(sash, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		c.setLayout(layout);
		
		Button b = new Button(c, SWT.NONE);
		b.setText("(+)");
		b.setToolTipText("Configure connections");
		b.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				Utils.executeCommand(DataViewPart.this.getSite(), "sqlipse.commands.configureConnections");
				
				Object o = ((IStructuredSelection)connections.getSelection()).getFirstElement();
				
				String connectionName = "";
				if (o instanceof JdbcConnection) {
					connectionName= ((JdbcConnection) o).getName();
				}
				
				Configuration config = SqlipsePlugin.getDefault().readConfig();
				connections.setInput(config.getConnections());
				
				JdbcConnection connectionByName = config.getConnectionByName(connectionName);
				if (connectionByName != null) {
					connections.setSelection(new StructuredSelection(connectionByName));
				}
			}
		});
		
		connections = new ComboViewer(c);
		connections.setContentProvider(ArrayContentProvider.getInstance());
		connections.setInput(SqlipsePlugin.getDefault().readConfig().getConnections());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(connections.getControl());
		
		Button bRun = new Button(c, SWT.PUSH);
		bRun.setText("Run");
		bRun.setToolTipText("Run selected query");

		scratchpad = new SqlScratchpad(c, SWT.BORDER);
		GridDataFactory.fillDefaults().span(3, 1).grab(true, true).applyTo(scratchpad);
		
		folder = new CTabFolder(sash, SWT.BORDER);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(sash, "sqlipse.viewer");
		contributeToActionBars();

		sash.setWeights(new int[] { 1, 3 });
		
		((IContextService)getSite().getService(IContextService.class)).activateContext("sqlipse.context");
		getSite().setSelectionProvider(scratchpad.getSelectionProvider());
	}
	
	private TableViewer addTabItem(CTabFolder folder, ResultSet rs) throws SQLException {
		CTabItem item = new CTabItem(folder, SWT.NONE);
		item.setText("Results");
		item.setShowClose(true);

		final TableViewer viewer = new TableViewer(folder, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

		ResultSetMetaData md = rs.getMetaData();
		for (int i = 1; i <= md.getColumnCount(); i++) {
			TableViewerColumn col = new TableViewerColumn(viewer, SWT.CENTER);
			col.getColumn().setText(md.getColumnName(i));
		}

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(rs);

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
			}
		});

		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		item.setControl(viewer.getTable());

		folder.setSelection(folder.getItemCount() - 1);

		for (int i = 0, n = viewer.getTable().getColumnCount(); i < n; i++) {
			viewer.getTable().getColumn(i).pack();
		}

		return viewer;
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
		//bars.getStatusLineManager().add(action1);
	}

	private void fillLocalPullDown(IMenuManager manager) {
//		manager.add(new Separator());
//		manager.add(new Action("Hello"){});
	}

	private void fillLocalToolBar(IToolBarManager manager) {
	}

	public void setFocus() {
		scratchpad.setFocus();
	}
	
}