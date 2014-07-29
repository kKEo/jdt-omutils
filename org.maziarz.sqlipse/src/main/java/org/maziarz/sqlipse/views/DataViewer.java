package org.maziarz.sqlipse.views;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.maziarz.sqlipse.JdbcConnection;
import org.maziarz.sqlipse.views.DataViewer.ViewContentProvider.Row;

public class DataViewer extends ViewPart {

	public static final String ID = "dateexplorer.views.DataViewer";

	private Action runSqlAction;
	private Action action2;

	private SqlScratchpad scratchpad;
	private CTabFolder folder;

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
				List<Row> rows = new ArrayList<DataViewer.ViewContentProvider.Row>();

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

	public DataViewer() {
	}

	public void createPartControl(Composite parent) {

		SashForm sash = new SashForm(parent, SWT.NONE);

		scratchpad = new SqlScratchpad(sash, SWT.BORDER);

		folder = new CTabFolder(sash, SWT.BORDER);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(sash, "dataexplorer.viewer");

		makeActions();
		contributeToActionBars();

		scratchpad.setAction(runSqlAction);

		sash.setWeights(new int[] { 1, 3 });
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
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				System.out.println(obj);
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
		manager.add(runSqlAction);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(runSqlAction);
		manager.add(action2);
	}

	private void makeActions() {

		runSqlAction = new Action() {
			public void run() {
				String query = scratchpad.getQuery();
				System.out.println("Query: " + query);
				if (query.isEmpty()) {
					return;
				}
				Connection c = null;
				try {
					c = JdbcConnection.getH2Test().connect();

					if (query.trim().toUpperCase().startsWith("SELECT")) {
						ResultSet rs = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
								.executeQuery();
						addTabItem(folder, rs);
					} else {
						int i = c.prepareStatement(query).executeUpdate();
						System.out.println("Result: " + i);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						c.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		};

		runSqlAction.setText("Connection");
		runSqlAction.setToolTipText("Action 1 tooltip");
		runSqlAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));

		action2 = new Action() {
			public void run() {

			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));

	}

	public void setFocus() {
		scratchpad.setFocus();
	}
}