package org.maziarz.sqlipse;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration")
public class Configuration {

	@XmlAttribute
	private String version = "1.0";
	
	@XmlElementWrapper(name = "drivers")
	@XmlElement(name = "driver")
	private List<JdbcDriver> drivers = new ArrayList<JdbcDriver>();

	@XmlElementWrapper(name = "connections")
	@XmlElement(name = "connection")
	private List<JdbcConnection> connections = new ArrayList<JdbcConnection>();

	public List<JdbcDriver> getDrivers() {
		return drivers;
	}
	
	public List<JdbcConnection> getConnections() {
		return connections;
	}
	
	public void addDriver(JdbcDriver driver) {
		drivers.add(driver);
	}
	
	public void addConnection(JdbcConnection connection) {
		connections.add(connection);
	}
	
	public JdbcConnection getConnectionByName(String connectionName) {
		for (JdbcConnection connection : connections) {
			if (connection.getName().equals(connectionName)) {
				return connection;
			}
		}
		return null;
	}
	
	public JdbcDriver getDriverByName(String driverName) {
		for (JdbcDriver driver: drivers) {
			if (driver.getName().equals(driverName)){
				return driver;
			}
		}
		return null;
	}
	
	public static Configuration read(InputStream is) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Configuration.class);
		Configuration result = (Configuration) context.createUnmarshaller().unmarshal(is);
		return result;
	}

	public static void write(Configuration o, OutputStream os) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Configuration.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(o, os);
	}



}
