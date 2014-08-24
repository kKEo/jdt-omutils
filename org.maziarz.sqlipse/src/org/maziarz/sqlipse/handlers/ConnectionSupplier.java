package org.maziarz.sqlipse.handlers;

import org.maziarz.sqlipse.JdbcConnection;

public interface ConnectionSupplier {
	JdbcConnection getConnection();
}
