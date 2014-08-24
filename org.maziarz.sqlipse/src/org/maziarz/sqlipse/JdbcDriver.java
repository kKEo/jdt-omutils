package org.maziarz.sqlipse;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;

public class JdbcDriver {
	
	@XmlID
	@XmlAttribute
	private String id = UUID.randomUUID().toString();
	
	@XmlElement
	private String jars;
	
	@XmlAttribute
	private String driver;
	
	@SuppressWarnings("unused")
	private JdbcDriver() {
	}
	
	public JdbcDriver(String jars, String driver) {
		this.jars = jars;
		this.driver = driver;
	}

	public String getDriver() {
		return driver;
	}
	
	public String getJars() {
		return jars;
	}
}
