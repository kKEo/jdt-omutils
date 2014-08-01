package org.maziarz.sqlipse;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JdbcConnection {

	private String connectionUrl;
	private String driverClassName;
	private String username;
	private String password;
	private String jarList;

	private Connection connection;

	private static class ClassLoaderCache {

		Map<String, ClassLoader> cache = new HashMap<String, ClassLoader>();

		private static ClassLoaderCache instance;

		private static ClassLoaderCache getInstance() {
			if (instance == null) {
				synchronized (JdbcConnection.class) {
					if (instance == null) {
						instance = new ClassLoaderCache();
					}
				}
			}
			return instance;
		}

		private ClassLoader createClassLoader(String jarList) {
			String[] jarPaths = jarList.split(";");

			if (jarPaths.length == 0) {
				throw new RuntimeException("No driver jars found: " + jarList);
			}

			List<URL> jars = new ArrayList<URL>(jarPaths.length);
			for (String jar : jarPaths) {
				try {
					jars.add(new File(jar).toURI().toURL());
				} catch (MalformedURLException e) {
					throw new RuntimeException("Cannot create class loader: " + e.getMessage(), e);
				}
			}

			return URLClassLoader.newInstance(jars.toArray(new URL[0]));
		}

		public ClassLoader get(String jarList) {
			if (!cache.containsKey(jarList)) {
				cache.put(jarList, createClassLoader(jarList));
			}

			return cache.get(jarList);
		}

	}

	public JdbcConnection(String jarList, String driver, String conn, String user, String pass) {
		this.connectionUrl = conn;
		this.driverClassName = driver;
		this.username = user;
		this.password = pass;
		this.jarList = jarList;
	}

	public Connection connect() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		ClassLoader classLoader = ClassLoaderCache.getInstance().get(jarList);
		connection = createConnection(classLoader);

		return connection;
	}

	private Connection createConnection(ClassLoader cl) throws SQLException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {

		Properties connectionProps = new Properties();
		connectionProps.setProperty("user", username);
		connectionProps.setProperty("password", password);

		Driver jdbcDriver = (Driver) cl.loadClass(driverClassName).newInstance();
		return jdbcDriver.connect(connectionUrl, connectionProps);

	}

	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
			}
		}
	}

	public static JdbcConnection getH2Test() {
		return new JdbcConnection("/home/krma/Downloads/Java/JDBCDrivers/h2-1.3.153.jar", "org.h2.Driver", "jdbc:h2:~/test2",
				"aa", "");
	}

	public static void main(String[] args) {

		{

			JdbcConnection jdbcConnection = getH2Test();
			Connection c = null;
			try {
				c = jdbcConnection.connect();
				ResultSet rs = c.prepareStatement("select * from INFORMATION_SCHEMA.TABLES").executeQuery();
				rs.first();
				System.out.println(rs.getString(1));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		{
			JdbcConnection jdbcConnection = getH2Test();
			Connection c = null;
			try {
				c = jdbcConnection.connect();
				ResultSet rs = c.prepareStatement("select count(1) from user_tables", ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE).executeQuery();
				rs.first();

				System.out.println(rs.getString(1));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

	}

}
