package org.maziarz.sqlipse.handlers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.maziarz.sqlipse.JdbcConnection;
import org.maziarz.sqlipse.views.SqlSourceProvider;

public class RunQueryHandler extends AbstractHandler implements IHandler {

	private Map<JdbcConnection,Connection> connections = new HashMap<>();
	
	private Connection getConneciton(JdbcConnection c){
		
		if (connections.get(c) == null) {
			try {
				connections.put(c, c.connect());
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
		
		return connections.get(c);
	}	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);

		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		ConnectionSupplier connectionSupplier = (ConnectionSupplier)activePart.getAdapter(ConnectionSupplier.class);
		JdbcConnection jc = connectionSupplier.getConnection();

		SqlSourceProvider sourceProvider = (SqlSourceProvider) activePart.getAdapter(SqlSourceProvider.class);
		
		ResultSetProcessor rsp = (ResultSetProcessor) activePart.getAdapter(ResultSetProcessor.class);

		if (currentSelection instanceof TextSelection) {
			
			String query = sourceProvider.getScript();
			System.out.println("Run Query: \"" + query +"\"");
			Connection conn = null;
			try {
				conn = getConneciton(jc);

				DatabaseMetaData metaData = conn.getMetaData();
				System.out.println("Driver name: " + metaData.getDriverName());

				if (query.trim().toUpperCase().startsWith("SELECT") //
						|| query.trim().toUpperCase().startsWith("SHOW")) {
					ResultSet rs = null;
					if ("SQLiteJDBC".equals(metaData.getDriverName())) {
						rs = conn.prepareStatement(query).executeQuery();
					} else {
						rs = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery();
					}
					rsp.process(rs);
				} else {
					boolean res = conn.prepareStatement(query).execute();
					System.out.println("Result: " + res);
				}

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		return null;
	}

}
