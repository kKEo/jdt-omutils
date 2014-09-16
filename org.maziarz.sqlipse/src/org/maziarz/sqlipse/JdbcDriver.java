package org.maziarz.sqlipse;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;

public class JdbcDriver {
	
	@XmlID
	@XmlAttribute
	private String name;
	
	@XmlElement
	private String jars;
	
	@XmlAttribute
	private String driver;
	
	@SuppressWarnings("unused")
	private JdbcDriver() {
	}
	
	public JdbcDriver(String name, String jars, String driver) {
		this.name = name;
		this.jars = jars;
		this.driver = driver;
	}

	public String getName() {
		return name;
	}
	
	public String getDriverClass() {
		return driver;
	}
	
	public String getJars() {
		return jars;
	}
}
