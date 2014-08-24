package org.maziarz.sqlipse.handlers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.maziarz.sqlipse.JdbcConnection;

public class RunQueryHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);

		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		Object adapter = activePart.getAdapter(ConnectionSupplier.class);
		JdbcConnection jc = ((ConnectionSupplier) adapter).getConnection();

		ResultSetProcessor rsp = (ResultSetProcessor) activePart.getAdapter(ResultSetProcessor.class);

		if (currentSelection instanceof TextSelection) {
			TextSelection currentSelection2 = (TextSelection) currentSelection;

			if (currentSelection2.isEmpty()) {

				System.out.println("Startline: " + currentSelection2.getStartLine());

			}

			String query = currentSelection2.getText();
			System.out.println("Run Query: " + query);
			Connection c = null;
			try {
				c = jc.connect();

				DatabaseMetaData metaData = c.getMetaData();
				System.out.println("Driver name: " + metaData.getDriverName());

				if (query.trim().toUpperCase().startsWith("SELECT") //
						|| query.trim().toUpperCase().startsWith("SHOW")) {
					ResultSet rs = null;
					if ("SQLiteJDBC".equals(metaData.getDriverName())) {
						rs = c.prepareStatement(query).executeQuery();
					} else {
						rs = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery();
					}
					rsp.process(rs);
				} else {
					boolean res = c.prepareStatement(query).execute();
					System.out.println("Result: " + res);
				}

			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				throw new RuntimeException("" + e, e);
			} finally {
				try {
					c.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		System.out.println(currentSelection);

		return null;
	}

}
