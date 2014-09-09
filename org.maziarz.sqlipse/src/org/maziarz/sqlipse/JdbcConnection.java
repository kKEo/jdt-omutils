package org.maziarz.sqlipse;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;

public class JdbcConnection {

	@XmlAttribute
	private String name;

	@XmlAttribute
	private String connectionUrl;

	@XmlAttribute
	private String username;

	@XmlAttribute
	private String password;

	@XmlIDREF
	@XmlAttribute
	private JdbcDriver driver;

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

	@SuppressWarnings("unused")
	private JdbcConnection() {
	}

	public JdbcConnection(JdbcDriver driver, String conn, String user, String pass) {
		this.connectionUrl = conn;
		this.driver = driver;
		this.username = user;
		this.password = pass;
	}

	public String getName() {
		return name;
	}
	
	public String getConnectionUrl() {
		return connectionUrl;
	}

	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public JdbcDriver getDriver() {
		return driver;
	}

	public Connection connect() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		ClassLoader classLoader = ClassLoaderCache.getInstance().get(driver.getJars());
		connection = createConnection(classLoader);

		return connection;
	}

	private Connection createConnection(ClassLoader cl) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		Properties connectionProps = new Properties();
		connectionProps.setProperty("user", username);
		connectionProps.setProperty("password", password);

		Driver jdbcDriver = (Driver) cl.loadClass(driver.getDriver()).newInstance();
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

	@Override
	public String toString() {
		return name ;//+ "(" + connectionUrl + ")";
	}
}
